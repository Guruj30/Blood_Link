package com.bloodlink.repository;

import com.bloodlink.model.BloodGroup;
import com.bloodlink.model.BloodInventoryItem;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for aggregated blood inventory storage.
 */
public interface BloodInventoryRepository {

    List<BloodInventoryItem> findAll();

    Optional<BloodInventoryItem> findByBloodGroup(BloodGroup group);

    Optional<BloodInventoryItem> findById(String id);

    BloodInventoryItem save(BloodInventoryItem item);
}
