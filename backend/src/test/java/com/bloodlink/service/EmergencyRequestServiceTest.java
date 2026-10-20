package com.bloodlink.service;

import com.bloodlink.dto.EmergencyRequestCreateDto;
import com.bloodlink.dto.EmergencyRequestResponseDto;
import com.bloodlink.dto.EmergencyRequestStatusUpdateDto;
import com.bloodlink.dto.StockAdjustmentRequestDto;
import com.bloodlink.exception.InsufficientStockException;
import com.bloodlink.exception.InvalidOperationException;
import com.bloodlink.model.BloodGroup;
import com.bloodlink.model.EmergencyRequest;
import com.bloodlink.model.RequestStatus;
import com.bloodlink.model.UrgencyLevel;
import com.bloodlink.repository.inmemory.InMemoryBloodInventoryRepository;
import com.bloodlink.repository.inmemory.InMemoryEmergencyRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Emergency Request Service Tests")
class EmergencyRequestServiceTest {

    private EmergencyRequestService requestService;
    private InMemoryEmergencyRequestRepository requestRepository;
    private BloodInventoryService inventoryService;

    @BeforeEach
    void setUp() {
        requestRepository = new InMemoryEmergencyRequestRepository();
        InMemoryBloodInventoryRepository inventoryRepository = new InMemoryBloodInventoryRepository();
        inventoryService = new BloodInventoryService(inventoryRepository);
        RedCellCompatibilityEngine redCellEngine = new RedCellCompatibilityEngine();
        requestService = new EmergencyRequestService(requestRepository, inventoryService, redCellEngine);
    }

    @Test
    @DisplayName("Deterministic priority ordering orders CRITICAL ahead of HIGH and NORMAL")
    void testPriorityOrdering() {
        // Clear seed data to test deterministic ordering in isolation
        for (EmergencyRequest r : requestRepository.findAll()) {
            requestRepository.deleteById(r.getId());
        }

        LocalDateTime baseTime = LocalDateTime.of(2026, 10, 19, 10, 0);

        // Add low priority first
        requestRepository.save(new EmergencyRequest("REQ-1", BloodGroup.A_POSITIVE, 2, "Hosp A", "City",
                UrgencyLevel.LOW, RequestStatus.PENDING, baseTime.minusHours(5), "", "Doc"));

        // Add normal priority
        requestRepository.save(new EmergencyRequest("REQ-2", BloodGroup.A_POSITIVE, 2, "Hosp B", "City",
                UrgencyLevel.NORMAL, RequestStatus.PENDING, baseTime.minusHours(4), "", "Doc"));

        // Add high priority
        requestRepository.save(new EmergencyRequest("REQ-3", BloodGroup.A_POSITIVE, 2, "Hosp C", "City",
                UrgencyLevel.HIGH, RequestStatus.PENDING, baseTime.minusHours(3), "", "Doc"));

        // Add critical priority created most recently
        requestRepository.save(new EmergencyRequest("REQ-4", BloodGroup.A_POSITIVE, 2, "Hosp D", "City",
                UrgencyLevel.CRITICAL, RequestStatus.PENDING, baseTime, "", "Doc"));

        // Add another critical priority created earlier (should come first due to FIFO tie-break)
        requestRepository.save(new EmergencyRequest("REQ-5", BloodGroup.A_POSITIVE, 2, "Hosp E", "City",
                UrgencyLevel.CRITICAL, RequestStatus.PENDING, baseTime.minusHours(1), "", "Doc"));

        List<EmergencyRequestResponseDto> list = requestService.getAllRequests(null, null, null, null);

        assertEquals(5, list.size());
        assertEquals("REQ-5", list.get(0).getId(), "Earliest critical request must be first");
        assertEquals("REQ-4", list.get(1).getId(), "Second critical request must be second");
        assertEquals("REQ-3", list.get(2).getId(), "High urgency request must follow critical");
        assertEquals("REQ-2", list.get(3).getId(), "Normal urgency request must follow high");
        assertEquals("REQ-1", list.get(4).getId(), "Low urgency request must be last");
    }

    @Test
    @DisplayName("Create request generates formatted ID and default status pending")
    void testCreateRequest() {
        EmergencyRequestCreateDto dto = new EmergencyRequestCreateDto(
                "O-", 2, "Apollo Medical Center", "Chennai", "critical", "Trauma emergency", "Dr. Rao"
        );
        EmergencyRequestResponseDto created = requestService.createRequest(dto);

        assertNotNull(created.getId());
        assertTrue(created.getId().startsWith("REQ-"));
        assertEquals("O-", created.getBloodGroup());
        assertEquals(2, created.getUnits());
        assertEquals("pending", created.getStatus());
        assertEquals("critical", created.getUrgency());
    }

    @Test
    @DisplayName("Fulfilling request deducts inventory stock atomically")
    void testFulfillRequestDeductsInventory() {
        inventoryService.adjustInventory("O-", new StockAdjustmentRequestDto(15, null, "set"));

        EmergencyRequestCreateDto createDto = new EmergencyRequestCreateDto(
                "O-", 3, "General Hospital", "Chennai", "critical", "Urgent transfusion", "Dr. Rao"
        );
        EmergencyRequestResponseDto created = requestService.createRequest(createDto);

        EmergencyRequestStatusUpdateDto statusDto = new EmergencyRequestStatusUpdateDto("fulfilled", "Units issued", true);
        EmergencyRequestResponseDto updated = requestService.updateRequestStatus(created.getId(), statusDto);

        assertEquals("fulfilled", updated.getStatus());
        // Initial 15 - 3 = 12
        assertEquals(12, inventoryService.getAvailableStock(BloodGroup.O_NEGATIVE));
    }

    @Test
    @DisplayName("Fulfilling request fails when stock is insufficient")
    void testFulfillRequestFailsInsufficientStock() {
        inventoryService.adjustInventory("AB-", new StockAdjustmentRequestDto(1, null, "set"));

        EmergencyRequestCreateDto createDto = new EmergencyRequestCreateDto(
                "AB-", 5, "City Clinic", "Salem", "high", "Need blood", "Dr. Rao"
        );
        EmergencyRequestResponseDto created = requestService.createRequest(createDto);

        EmergencyRequestStatusUpdateDto statusDto = new EmergencyRequestStatusUpdateDto("fulfilled", "Try issuing", true);

        assertThrows(InsufficientStockException.class,
                () -> requestService.updateRequestStatus(created.getId(), statusDto));
    }

    @Test
    @DisplayName("Duplicate fulfillment is strictly prevented")
    void testDuplicateFulfillmentPrevented() {
        inventoryService.adjustInventory("A+", new StockAdjustmentRequestDto(20, null, "set"));

        EmergencyRequestCreateDto createDto = new EmergencyRequestCreateDto(
                "A+", 2, "City Clinic", "Salem", "normal", "Routine surgery", "Dr. Rao"
        );
        EmergencyRequestResponseDto created = requestService.createRequest(createDto);

        requestService.updateRequestStatus(created.getId(), new EmergencyRequestStatusUpdateDto("fulfilled"));

        // Attempt second fulfillment
        assertThrows(InvalidOperationException.class,
                () -> requestService.updateRequestStatus(created.getId(), new EmergencyRequestStatusUpdateDto("fulfilled")));
    }

    @Test
    @DisplayName("Cancelled request cannot be reopened")
    void testCancelledRequestCannotBeReopened() {
        EmergencyRequestCreateDto createDto = new EmergencyRequestCreateDto(
                "B+", 1, "City Clinic", "Salem", "low", "Standby", "Dr. Rao"
        );
        EmergencyRequestResponseDto created = requestService.createRequest(createDto);

        requestService.updateRequestStatus(created.getId(), new EmergencyRequestStatusUpdateDto("cancelled"));

        assertThrows(InvalidOperationException.class,
                () -> requestService.updateRequestStatus(created.getId(), new EmergencyRequestStatusUpdateDto("in_progress")));
    }
}
