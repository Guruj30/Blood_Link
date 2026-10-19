package com.bloodlink.repository.inmemory;

import com.bloodlink.model.BloodGroup;
import com.bloodlink.model.EmergencyRequest;
import com.bloodlink.model.RequestStatus;
import com.bloodlink.model.UrgencyLevel;
import com.bloodlink.repository.EmergencyRequestRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Thread-safe in-memory repository for clinical emergency requests.
 * Initialized with realistic fictional demo requests matching frontend seedData.js.
 */
@Repository
public class InMemoryEmergencyRequestRepository implements EmergencyRequestRepository {

    private final Map<String, EmergencyRequest> storage = new ConcurrentHashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger(2025);

    public InMemoryEmergencyRequestRepository() {
        seedInitialRequests();
    }

    private void seedInitialRequests() {
        addSeed("REQ-2024", BloodGroup.O_NEGATIVE, 4, "Demo City Medical Centre", "Chennai",
                UrgencyLevel.CRITICAL, RequestStatus.IN_PROGRESS,
                LocalDateTime.of(2026, 10, 18, 9, 15), "Demo: Trauma surgery simulation", "Dr. Demo Admin");
        addSeed("REQ-2023", BloodGroup.AB_POSITIVE, 2, "Sample General Hospital", "Coimbatore",
                UrgencyLevel.HIGH, RequestStatus.PENDING,
                LocalDateTime.of(2026, 10, 18, 7, 42), "Demo: Elective procedure simulation", "Dr. Demo Admin");
        addSeed("REQ-2022", BloodGroup.B_POSITIVE, 3, "Demo Apollo Demo Hospital", "Madurai",
                UrgencyLevel.NORMAL, RequestStatus.FULFILLED,
                LocalDateTime.of(2026, 10, 17, 15, 30), "Demo: Orthopedic simulation", "Dr. Demo Admin");
        addSeed("REQ-2021", BloodGroup.A_POSITIVE, 2, "Demo MGMGH", "Chennai",
                UrgencyLevel.HIGH, RequestStatus.REVIEWING,
                LocalDateTime.of(2026, 10, 17, 11, 20), "Demo: Cardiac simulation", "Dr. Demo Admin");
        addSeed("REQ-2020", BloodGroup.O_POSITIVE, 5, "Demo GH Trichy", "Trichy",
                UrgencyLevel.CRITICAL, RequestStatus.FULFILLED,
                LocalDateTime.of(2026, 10, 16, 20, 5), "Demo: Emergency simulation", "Dr. Demo Admin");
        addSeed("REQ-2019", BloodGroup.AB_NEGATIVE, 1, "Demo Fortis Demo", "Vellore",
                UrgencyLevel.NORMAL, RequestStatus.CANCELLED,
                LocalDateTime.of(2026, 10, 16, 10, 0), "Demo: Cancelled simulation", "Dr. Demo Admin");
        addSeed("REQ-2018", BloodGroup.B_NEGATIVE, 2, "Demo CMC Demo", "Vellore",
                UrgencyLevel.HIGH, RequestStatus.IN_PROGRESS,
                LocalDateTime.of(2026, 10, 15, 14, 0), "Demo: In-progress simulation", "Dr. Demo Admin");
    }

    private void addSeed(String id, BloodGroup bg, int units, String hospital, String loc,
                         UrgencyLevel urgency, RequestStatus status, LocalDateTime created,
                         String notes, String requester) {
        EmergencyRequest req = new EmergencyRequest(id, bg, units, hospital, loc, urgency, status, created, notes, requester);
        storage.put(id, req);
    }

    @Override
    public List<EmergencyRequest> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<EmergencyRequest> findById(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public EmergencyRequest save(EmergencyRequest request) {
        if (request.getId() == null || request.getId().trim().isEmpty()) {
            request.setId(generateNextId());
        }
        storage.put(request.getId(), request);
        return request;
    }

    @Override
    public boolean deleteById(String id) {
        if (id == null) return false;
        return storage.remove(id) != null;
    }

    @Override
    public List<EmergencyRequest> findByStatus(RequestStatus status) {
        return storage.values().stream()
                .filter(r -> r.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmergencyRequest> findByUrgency(UrgencyLevel urgency) {
        return storage.values().stream()
                .filter(r -> r.getUrgency() == urgency)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmergencyRequest> findByBloodGroup(BloodGroup bloodGroup) {
        return storage.values().stream()
                .filter(r -> r.getBloodGroup() == bloodGroup)
                .collect(Collectors.toList());
    }

    @Override
    public String generateNextId() {
        return "REQ-" + idCounter.getAndIncrement();
    }
}
