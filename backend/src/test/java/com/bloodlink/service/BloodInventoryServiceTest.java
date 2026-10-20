package com.bloodlink.service;

import com.bloodlink.dto.BloodInventoryDto;
import com.bloodlink.dto.StockAdjustmentRequestDto;
import com.bloodlink.exception.ExpiredBloodUnitException;
import com.bloodlink.exception.InsufficientStockException;
import com.bloodlink.model.BloodGroup;
import com.bloodlink.model.BloodInventoryItem;
import com.bloodlink.model.BloodUnit;
import com.bloodlink.repository.inmemory.InMemoryBloodInventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Blood Inventory Service Tests")
class BloodInventoryServiceTest {

    private BloodInventoryService inventoryService;
    private InMemoryBloodInventoryRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryBloodInventoryRepository();
        inventoryService = new BloodInventoryService(repository);
    }

    @Test
    @DisplayName("Lists all 8 standard blood groups in inventory")
    void testGetAllInventory() {
        List<BloodInventoryDto> list = inventoryService.getAllInventory();
        assertEquals(8, list.size());
    }

    @Test
    @DisplayName("Adjust stock: Set mode updates units and threshold")
    void testAdjustStockSet() {
        StockAdjustmentRequestDto dto = new StockAdjustmentRequestDto(25, 12, "set");
        BloodInventoryDto result = inventoryService.adjustInventory("A+", dto);

        assertEquals(25, result.getUnits());
        assertEquals(12, result.getLowThreshold());
    }

    @Test
    @DisplayName("Adjust stock: Add mode increments units")
    void testAdjustStockAdd() {
        int initialUnits = inventoryService.getInventoryByGroup("A+").getUnits();
        StockAdjustmentRequestDto dto = new StockAdjustmentRequestDto(10, null, "add");
        BloodInventoryDto result = inventoryService.adjustInventory("A+", dto);

        assertEquals(initialUnits + 10, result.getUnits());
    }

    @Test
    @DisplayName("Adjust stock: Subtract mode decrements units")
    void testAdjustStockSubtract() {
        inventoryService.adjustInventory("A+", new StockAdjustmentRequestDto(20, null, "set"));
        StockAdjustmentRequestDto dto = new StockAdjustmentRequestDto(5, null, "subtract");
        BloodInventoryDto result = inventoryService.adjustInventory("A+", dto);

        assertEquals(15, result.getUnits());
    }

    @Test
    @DisplayName("Adjust stock: Insufficient stock throws InsufficientStockException")
    void testInsufficientStockThrowsException() {
        inventoryService.adjustInventory("O-", new StockAdjustmentRequestDto(3, null, "set"));
        StockAdjustmentRequestDto dto = new StockAdjustmentRequestDto(10, null, "subtract");

        InsufficientStockException ex = assertThrows(InsufficientStockException.class,
                () -> inventoryService.adjustInventory("O-", dto));

        assertEquals("O-", ex.getBloodGroup());
        assertEquals(10, ex.getRequested());
        assertEquals(3, ex.getAvailable());
    }

    @Test
    @DisplayName("Deduct stock directly: throws InsufficientStockException when stock is too low")
    void testDeductStockInsufficient() {
        inventoryService.adjustInventory("B-", new StockAdjustmentRequestDto(2, null, "set"));

        assertThrows(InsufficientStockException.class,
                () -> inventoryService.deductStock(BloodGroup.B_NEGATIVE, 5));
    }

    @Test
    @DisplayName("Expired blood unit is strictly rejected")
    void testExpiredBloodUnitValidation() {
        // Shelf life 35 days, collected 40 days ago -> expired 5 days ago
        LocalDate collectionDate = LocalDate.now().minusDays(40);
        BloodUnit expiredUnit = new BloodUnit("UNIT-999", BloodGroup.O_NEGATIVE, collectionDate, 35);

        assertTrue(expiredUnit.isExpired());
        assertFalse(expiredUnit.isUsable());

        ExpiredBloodUnitException ex = assertThrows(ExpiredBloodUnitException.class,
                () -> inventoryService.validateUnitNotExpired(expiredUnit));

        assertTrue(ex.getMessage().contains("expired on"));
    }

    @Test
    @DisplayName("Fresh, unexpired blood unit is valid and usable")
    void testValidBloodUnit() {
        // Shelf life 35 days, collected 5 days ago -> 30 days remaining
        LocalDate collectionDate = LocalDate.now().minusDays(5);
        BloodUnit freshUnit = new BloodUnit("UNIT-100", BloodGroup.A_POSITIVE, collectionDate, 35);

        assertFalse(freshUnit.isExpired());
        assertTrue(freshUnit.isUsable());
        assertDoesNotThrow(() -> inventoryService.validateUnitNotExpired(freshUnit));
    }
}
