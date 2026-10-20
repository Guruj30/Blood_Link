package com.bloodlink.repository.inmemory;

import com.bloodlink.model.AvailabilityStatus;
import com.bloodlink.model.BloodGroup;
import com.bloodlink.model.Donor;
import com.bloodlink.repository.DonorRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Thread-safe in-memory implementation of DonorRepository.
 * Populated with realistic fictional demo seed data matching the frontend's seedData.js.
 * Serves as an operational mock for testing or offline execution.
 */
@Repository
@ConditionalOnProperty(name = "bloodlink.persistence.mode", havingValue = "IN_MEMORY", matchIfMissing = false)
public class InMemoryDonorRepository implements DonorRepository {

    private final Map<String, Donor> storage = new ConcurrentHashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger(10056);

    public InMemoryDonorRepository() {
        seedInitialDonors();
    }

    private void seedInitialDonors() {
        addSeed("D-10041", "Arjun Menon", BloodGroup.O_POSITIVE, "Chennai", "+91-98400-00001",
                AvailabilityStatus.AVAILABLE, LocalDate.of(2026, 7, 12), LocalDate.of(2026, 10, 12),
                LocalDate.of(2024, 3, 15), "Demo record");
        addSeed("D-10042", "Priya Nair", BloodGroup.A_POSITIVE, "Coimbatore", "+91-98400-00002",
                AvailabilityStatus.AVAILABLE, LocalDate.of(2026, 6, 5), LocalDate.of(2026, 9, 5),
                LocalDate.of(2024, 5, 20), "Demo record");
        addSeed("D-10043", "Ravi Krishnan", BloodGroup.B_POSITIVE, "Madurai", "+91-98400-00003",
                AvailabilityStatus.UNAVAILABLE, LocalDate.of(2026, 9, 1), LocalDate.of(2026, 12, 1),
                LocalDate.of(2023, 11, 8), "Demo record");
        addSeed("D-10044", "Deepa Rajendran", BloodGroup.AB_POSITIVE, "Chennai", "+91-98400-00004",
                AvailabilityStatus.AVAILABLE, LocalDate.of(2026, 5, 20), LocalDate.of(2026, 8, 20),
                LocalDate.of(2025, 1, 10), "Demo record");
        addSeed("D-10045", "Karthik Selvam", BloodGroup.O_NEGATIVE, "Trichy", "+91-98400-00005",
                AvailabilityStatus.AVAILABLE, LocalDate.of(2026, 4, 14), LocalDate.of(2026, 7, 14),
                LocalDate.of(2023, 8, 22), "Demo record");
        addSeed("D-10046", "Lakshmi Iyer", BloodGroup.A_NEGATIVE, "Chennai", "+91-98400-00006",
                AvailabilityStatus.UNAVAILABLE, LocalDate.of(2026, 8, 18), LocalDate.of(2026, 11, 18),
                LocalDate.of(2024, 2, 14), "Demo record");
        addSeed("D-10047", "Suresh Babu", BloodGroup.B_NEGATIVE, "Salem", "+91-98400-00007",
                AvailabilityStatus.AVAILABLE, LocalDate.of(2026, 3, 30), LocalDate.of(2026, 6, 30),
                LocalDate.of(2023, 5, 19), "Demo record");
        addSeed("D-10048", "Meera Sundaram", BloodGroup.AB_NEGATIVE, "Tirunelveli", "+91-98400-00008",
                AvailabilityStatus.AVAILABLE, LocalDate.of(2026, 2, 11), LocalDate.of(2026, 5, 11),
                LocalDate.of(2024, 8, 30), "Demo record");
        addSeed("D-10049", "Vignesh Kumar", BloodGroup.O_POSITIVE, "Coimbatore", "+91-98400-00009",
                AvailabilityStatus.AVAILABLE, LocalDate.of(2026, 7, 28), LocalDate.of(2026, 10, 28),
                LocalDate.of(2024, 1, 12), "Demo record");
        addSeed("D-10050", "Ananya Venkatesh", BloodGroup.A_POSITIVE, "Chennai", "+91-98400-00010",
                AvailabilityStatus.UNAVAILABLE, LocalDate.of(2026, 9, 15), LocalDate.of(2026, 12, 15),
                LocalDate.of(2024, 9, 1), "Demo record");
        addSeed("D-10051", "Balaji Natarajan", BloodGroup.B_POSITIVE, "Madurai", "+91-98400-00011",
                AvailabilityStatus.AVAILABLE, LocalDate.of(2026, 6, 22), LocalDate.of(2026, 9, 22),
                LocalDate.of(2023, 7, 4), "Demo record");
        addSeed("D-10052", "Kavitha Raman", BloodGroup.O_NEGATIVE, "Vellore", "+91-98400-00012",
                AvailabilityStatus.AVAILABLE, LocalDate.of(2026, 1, 19), LocalDate.of(2026, 4, 19),
                LocalDate.of(2024, 4, 18), "Demo record");
        addSeed("D-10053", "Siddharth Rao", BloodGroup.A_NEGATIVE, "Chennai", "+91-98400-00013",
                AvailabilityStatus.AVAILABLE, LocalDate.of(2026, 5, 8), LocalDate.of(2026, 8, 8),
                LocalDate.of(2024, 6, 25), "Demo record");
        addSeed("D-10054", "Gayathri Shankaran", BloodGroup.O_POSITIVE, "Erode", "+91-98400-00014",
                AvailabilityStatus.UNAVAILABLE, LocalDate.of(2026, 8, 31), LocalDate.of(2026, 11, 30),
                LocalDate.of(2023, 10, 11), "Demo record");
        addSeed("D-10055", "Prashanth Murali", BloodGroup.AB_POSITIVE, "Chennai", "+91-98400-00015",
                AvailabilityStatus.AVAILABLE, LocalDate.of(2026, 4, 2), LocalDate.of(2026, 7, 2),
                LocalDate.of(2025, 2, 1), "Demo record");
    }

    private void addSeed(String id, String name, BloodGroup bg, String city, String phone,
                         AvailabilityStatus availability, LocalDate lastDonation,
                         LocalDate eligibleAfter, LocalDate registered, String notes) {
        Donor donor = new Donor(id, name, bg, city, phone, availability, lastDonation, eligibleAfter, registered, notes);
        storage.put(id, donor);
    }

    @Override
    public List<Donor> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<Donor> findById(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Donor save(Donor donor) {
        Objects.requireNonNull(donor, "Donor to save cannot be null");
        if (donor.getId() == null || donor.getId().trim().isEmpty()) {
            donor.setId(generateNextId());
        }
        storage.put(donor.getId(), donor);
        return donor;
    }

    @Override
    public boolean deleteById(String id) {
        if (id == null) return false;
        return storage.remove(id) != null;
    }

    @Override
    public List<Donor> findByBloodGroup(BloodGroup bloodGroup) {
        if (bloodGroup == null) return Collections.emptyList();
        return storage.values().stream()
                .filter(d -> d.getBloodGroup() == bloodGroup)
                .collect(Collectors.toList());
    }

    @Override
    public List<Donor> findByCity(String city) {
        if (city == null || city.trim().isEmpty()) return Collections.emptyList();
        return storage.values().stream()
                .filter(d -> d.getCity() != null && d.getCity().equalsIgnoreCase(city.trim()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Donor> findByAvailability(AvailabilityStatus availability) {
        if (availability == null) return Collections.emptyList();
        return storage.values().stream()
                .filter(d -> d.getAvailability() == availability)
                .collect(Collectors.toList());
    }

    @Override
    public String generateNextId() {
        return "D-" + idCounter.incrementAndGet();
    }
}
