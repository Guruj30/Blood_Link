package com.bloodlink.repository;

import com.bloodlink.model.BloodGroup;
import com.bloodlink.model.EmergencyRequest;
import com.bloodlink.model.RequestStatus;
import com.bloodlink.model.UrgencyLevel;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for clinical emergency request persistence.
 */
public interface EmergencyRequestRepository {

    List<EmergencyRequest> findAll();

    Optional<EmergencyRequest> findById(String id);

    EmergencyRequest save(EmergencyRequest request);

    boolean deleteById(String id);

    List<EmergencyRequest> findByStatus(RequestStatus status);

    List<EmergencyRequest> findByUrgency(UrgencyLevel urgency);

    List<EmergencyRequest> findByBloodGroup(BloodGroup bloodGroup);

    String generateNextId();
}
