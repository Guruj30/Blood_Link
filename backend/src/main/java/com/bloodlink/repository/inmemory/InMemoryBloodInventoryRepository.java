package com.bloodlink.repository.inmemory;

import com.bloodlink.model.BloodGroup;
import com.bloodlink.model.BloodInventoryItem;
import com.bloodlink.repository.BloodInventoryRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe in-memory repository for blood inventory.
 * Initialized with verified seed inventory for all 8 standard blood groups.
 * Serves as an operational mock for testing or offline execution.
 */
@Repository
@ConditionalOnProperty(name = "bloodlink.persistence.mode", havingValue = "IN_MEMORY", matchIfMissing = false)
public class InMemoryBloodInventoryRepository implements BloodInventoryRepository {

    private final Map<BloodGroup, BloodInventoryItem> storage = new ConcurrentHashMap<>();

    public InMemoryBloodInventoryRepository() {
        seedInitialInventory();
    }

    private void seedInitialInventory() {
        addSeed("bg-1", BloodGroup.A_POSITIVE, 48, 15, LocalDateTime.of(2026, 10, 18, 8, 30));
        addSeed("bg-2", BloodGroup.A_NEGATIVE, 12, 10, LocalDateTime.of(2026, 10, 17, 14, 20));
        addSeed("bg-3", BloodGroup.B_POSITIVE, 35, 15, LocalDateTime.of(2026, 10, 18, 11, 0));
        addSeed("bg-4", BloodGroup.B_NEGATIVE, 7, 10, LocalDateTime.of(2026, 10, 17, 9, 45));
        addSeed("bg-5", BloodGroup.AB_POSITIVE, 22, 10, LocalDateTime.of(2026, 10, 18, 13, 15));
        addSeed("bg-6", BloodGroup.AB_NEGATIVE, 4, 8, LocalDateTime.of(2026, 10, 16, 16, 30));
        addSeed("bg-7", BloodGroup.O_POSITIVE, 61, 20, LocalDateTime.of(2026, 10, 18, 7, 0));
        addSeed("bg-8", BloodGroup.O_NEGATIVE, 9, 15, LocalDateTime.of(2026, 10, 17, 12, 0));
    }

    private void addSeed(String id, BloodGroup bg, int units, int threshold, LocalDateTime updated) {
        BloodInventoryItem item = new BloodInventoryItem(id, bg, units, threshold, updated);
        storage.put(bg, item);
    }

    @Override
    public List<BloodInventoryItem> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<BloodInventoryItem> findByBloodGroup(BloodGroup group) {
        if (group == null) return Optional.empty();
        return Optional.ofNullable(storage.get(group));
    }

    @Override
    public Optional<BloodInventoryItem> findById(String id) {
        if (id == null) return Optional.empty();
        return storage.values().stream()
                .filter(i -> i.getId().equalsIgnoreCase(id.trim()))
                .findFirst();
    }

    @Override
    public BloodInventoryItem save(BloodInventoryItem item) {
        Objects.requireNonNull(item, "BloodInventoryItem to save cannot be null");
        storage.put(item.getBloodGroup(), item);
        return item;
    }
}
