package com.bloodlink.repository;

import com.bloodlink.model.AvailabilityStatus;
import com.bloodlink.model.BloodGroup;
import com.bloodlink.model.Donor;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Donor persistence.
 * Decouples domain logic from persistence mechanisms (In-Memory, JDBC, JPA, MySQL).
 */
public interface DonorRepository {

    List<Donor> findAll();

    Optional<Donor> findById(String id);

    Donor save(Donor donor);

    boolean deleteById(String id);

    List<Donor> findByBloodGroup(BloodGroup bloodGroup);

    List<Donor> findByCity(String city);

    List<Donor> findByAvailability(AvailabilityStatus availability);

    String generateNextId();
}
