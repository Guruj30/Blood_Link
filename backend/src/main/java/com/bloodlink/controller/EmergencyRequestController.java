package com.bloodlink.controller;

import com.bloodlink.dto.EmergencyRequestCreateDto;
import com.bloodlink.dto.EmergencyRequestResponseDto;
import com.bloodlink.dto.EmergencyRequestStatusUpdateDto;
import com.bloodlink.service.EmergencyRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for emergency blood requests and status dispatch workflow.
 * Consumed by Requests.jsx, RequestDetailModal.jsx, and CreateRequestModal.jsx.
 */
@RestController
@RequestMapping("/api/requests")
public class EmergencyRequestController {

    private final EmergencyRequestService requestService;

    public EmergencyRequestController(EmergencyRequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping
    public ResponseEntity<List<EmergencyRequestResponseDto>> getAllRequests(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String urgency,
            @RequestParam(required = false) String bloodGroup,
            @RequestParam(required = false) String search) {
        List<EmergencyRequestResponseDto> requests = requestService.getAllRequests(status, urgency, bloodGroup, search);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmergencyRequestResponseDto> getRequestById(@PathVariable String id) {
        return ResponseEntity.ok(requestService.getRequestById(id));
    }

    @PostMapping
    public ResponseEntity<EmergencyRequestResponseDto> createRequest(@Valid @RequestBody EmergencyRequestCreateDto dto) {
        EmergencyRequestResponseDto created = requestService.createRequest(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<EmergencyRequestResponseDto> updateRequestStatus(
            @PathVariable String id,
            @Valid @RequestBody EmergencyRequestStatusUpdateDto dto) {
        EmergencyRequestResponseDto updated = requestService.updateRequestStatus(id, dto);
        return ResponseEntity.ok(updated);
    }
}
