package com.bloodlink.controller;

import com.bloodlink.dto.DonorRequestDto;
import com.bloodlink.dto.DonorResponseDto;
import com.bloodlink.service.DonorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for blood donor registry management.
 * Consumed by Donors.jsx, Manage.jsx, and AddDonorModal.jsx.
 */
@RestController
@RequestMapping("/api/donors")
public class DonorController {

    private final DonorService donorService;

    public DonorController(DonorService donorService) {
        this.donorService = donorService;
    }

    @GetMapping
    public ResponseEntity<List<DonorResponseDto>> getDonors(
            @RequestParam(required = false) String bloodGroup,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String availability,
            @RequestParam(required = false) String search) {
        List<DonorResponseDto> donors = donorService.getAllDonors(bloodGroup, city, availability, search);
        return ResponseEntity.ok(donors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DonorResponseDto> getDonorById(@PathVariable String id) {
        return ResponseEntity.ok(donorService.getDonorById(id));
    }

    @PostMapping
    public ResponseEntity<DonorResponseDto> registerDonor(@Valid @RequestBody DonorRequestDto dto) {
        DonorResponseDto created = donorService.registerDonor(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DonorResponseDto> updateDonor(@PathVariable String id,
                                                        @Valid @RequestBody DonorRequestDto dto) {
        DonorResponseDto updated = donorService.updateDonor(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDonor(@PathVariable String id) {
        donorService.deleteDonor(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/compatible")
    public ResponseEntity<List<DonorResponseDto>> getCompatibleDonors(
            @RequestParam String recipientBloodGroup,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "true") boolean availableOnly) {
        List<DonorResponseDto> donors = donorService.findCompatibleDonors(recipientBloodGroup, city, availableOnly);
        return ResponseEntity.ok(donors);
    }
}
