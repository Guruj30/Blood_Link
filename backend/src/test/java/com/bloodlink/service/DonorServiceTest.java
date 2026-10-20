package com.bloodlink.service;

import com.bloodlink.dto.DonorRequestDto;
import com.bloodlink.dto.DonorResponseDto;
import com.bloodlink.exception.InvalidBloodGroupException;
import com.bloodlink.exception.ResourceNotFoundException;
import com.bloodlink.model.AvailabilityStatus;
import com.bloodlink.model.BloodGroup;
import com.bloodlink.model.Donor;
import com.bloodlink.repository.inmemory.InMemoryDonorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Donor Service Tests")
class DonorServiceTest {

    private DonorService donorService;
    private InMemoryDonorRepository donorRepository;

    @BeforeEach
    void setUp() {
        donorRepository = new InMemoryDonorRepository();
        DonorEligibilityService eligibilityService = new DonorEligibilityService(90);
        RedCellCompatibilityEngine redCellEngine = new RedCellCompatibilityEngine();
        donorService = new DonorService(donorRepository, eligibilityService, redCellEngine);
    }

    @Test
    @DisplayName("Registers new donor with calculated eligibleAfter date and generated ID")
    void testRegisterDonor() {
        LocalDate lastDonation = LocalDate.now().minusDays(30);
        DonorRequestDto dto = new DonorRequestDto("Vijay Varma", "O+", "Chennai",
                "+91-98765-43210", "available", lastDonation, "Test donor");

        DonorResponseDto registered = donorService.registerDonor(dto);

        assertNotNull(registered.getId());
        assertTrue(registered.getId().startsWith("D-"));
        assertEquals("Vijay Varma", registered.getName());
        assertEquals("O+", registered.getBloodGroup());
        assertEquals(lastDonation.plusDays(90), registered.getEligibleAfter());
        assertFalse(registered.isEligibleToDonate(), "Donated 30 days ago, must not be eligible yet");
    }

    @Test
    @DisplayName("Invalid blood group during registration throws InvalidBloodGroupException")
    void testRegisterDonorInvalidBloodGroup() {
        DonorRequestDto dto = new DonorRequestDto("Test Person", "X+", "Chennai",
                "+91-98765-43210", "available", null, "Invalid test");

        assertThrows(InvalidBloodGroupException.class, () -> donorService.registerDonor(dto));
    }

    @Test
    @DisplayName("Updates existing donor details correctly")
    void testUpdateDonor() {
        DonorRequestDto updateDto = new DonorRequestDto("Arjun Menon Updated", "O+", "Bangalore",
                "+91-98400-99999", "on_leave", null, "Updated notes");

        DonorResponseDto updated = donorService.updateDonor("D-10041", updateDto);

        assertEquals("Arjun Menon Updated", updated.getName());
        assertEquals("Bangalore", updated.getCity());
        assertEquals("on_leave", updated.getAvailability());
    }

    @Test
    @DisplayName("Updating non-existent donor throws ResourceNotFoundException")
    void testUpdateNonExistentDonor() {
        DonorRequestDto dto = new DonorRequestDto("Nobody", "O+", "City", "", "available", null, "");
        assertThrows(ResourceNotFoundException.class, () -> donorService.updateDonor("D-99999", dto));
    }

    @Test
    @DisplayName("Deletes donor cleanly")
    void testDeleteDonor() {
        assertNotNull(donorService.getDonorById("D-10041"));
        donorService.deleteDonor("D-10041");
        assertThrows(ResourceNotFoundException.class, () -> donorService.getDonorById("D-10041"));
    }

    @Test
    @DisplayName("Finds compatible donors for O- recipient (must only find O- donors)")
    void testFindCompatibleDonorsForONegative() {
        List<DonorResponseDto> compatible = donorService.findCompatibleDonors("O-", null, false);
        assertFalse(compatible.isEmpty());
        for (DonorResponseDto d : compatible) {
            assertEquals("O-", d.getBloodGroup(), "Only O- can donate to O- recipient");
        }
    }

    @Test
    @DisplayName("Behavior when no compatible donor is found returns an empty list cleanly without throwing")
    void testNoCompatibleDonorFoundReturnsEmptyList() {
        // Clear all O- donors from repository
        for (Donor d : donorRepository.findAll()) {
            if (d.getBloodGroup() == BloodGroup.O_NEGATIVE) {
                donorRepository.deleteById(d.getId());
            }
        }

        List<DonorResponseDto> compatible = donorService.findCompatibleDonors("O-", null, true);
        assertNotNull(compatible);
        assertTrue(compatible.isEmpty(), "Must return an empty list cleanly when no matching donors exist");
    }
}
