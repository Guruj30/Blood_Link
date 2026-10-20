package com.bloodlink.repository.inmemory;

import com.bloodlink.model.BloodGroup;
import com.bloodlink.model.EmergencyRequest;
import com.bloodlink.model.RequestStatus;
import com.bloodlink.model.UrgencyLevel;
import com.bloodlink.repository.EmergencyRequestRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Thread-safe in-memory repository for clinical emergency requests.
 * Initialized with realistic fictional demo requests matching frontend seedData.js.
 * Serves as an operational mock for testing or offline execution.
 */
@Repository
@ConditionalOnProperty(name = "bloodlink.persistence.mode", havingValue = "IN_MEMORY", matchIfMissing = false)
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
                LocalDateTime.of(2026, 10, 17, 12, 10), "Demo: Pediatric emergency simulation", "Dr. Demo Admin");
        addSeed("REQ-2020", BloodGroup.O_POSITIVE, 5, "Demo KG Hospital", "Coimbatore",
                UrgencyLevel.CRITICAL, RequestStatus.FULFILLED,
                LocalDateTime.of(2026, 10, 16, 20, 0), "Demo: Cardiac emergency simulation", "Dr. Demo Admin");
        addSeed("REQ-2019", BloodGroup.A_NEGATIVE, 1, "Demo Velammal Hospital", "Madurai",
                UrgencyLevel.LOW, RequestStatus.PENDING,
                LocalDateTime.of(2026, 10, 16, 14, 25), "Demo: Planned observation simulation", "Dr. Demo Admin");
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
        Objects.requireNonNull(request, "EmergencyRequest to save cannot be null");
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
        if (status == null) return Collections.emptyList();
        return storage.values().stream()
                .filter(r -> r.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmergencyRequest> findByUrgency(UrgencyLevel urgency) {
        if (urgency == null) return Collections.emptyList();
        return storage.values().stream()
                .filter(r -> r.getUrgency() == urgency)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmergencyRequest> findByBloodGroup(BloodGroup bloodGroup) {
        if (bloodGroup == null) return Collections.emptyList();
        return storage.values().stream()
                .filter(r -> r.getBloodGroup() == bloodGroup)
                .collect(Collectors.toList());
    }

    @Override
    public String generateNextId() {
        return "REQ-" + idCounter.incrementAndGet();
    }
}
