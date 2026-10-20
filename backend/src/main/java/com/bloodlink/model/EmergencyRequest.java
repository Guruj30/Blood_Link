package com.bloodlink.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity representing an urgent clinical blood request.
 * Implements Comparable to enforce deterministic priority ordering:
 * 1. Urgency Level (Critical [1] > High [2] > Normal [3] > Low [4])
 * 2. Submission timestamp (earlier requests first)
 * 3. Unique identifier (lexicographical tie-breaker)
 *
 * Notice: Software prioritization is an operational clinical dispatch tool.
 * It does NOT replace emergency dispatch services (112 / 911).
 */
public class EmergencyRequest implements Comparable<EmergencyRequest> {

    private String id;
    private BloodGroup bloodGroup;
    private int units;
    private String hospital;
    private String location;
    private UrgencyLevel urgency;
    private RequestStatus status;
    private LocalDateTime createdAt;
    private String notes;
    private String requester;

    public EmergencyRequest() {
        this.status = RequestStatus.PENDING;
        this.urgency = UrgencyLevel.NORMAL;
        this.createdAt = LocalDateTime.now();
    }

    public EmergencyRequest(String id, BloodGroup bloodGroup, int units, String hospital,
                            String location, UrgencyLevel urgency, RequestStatus status,
                            LocalDateTime createdAt, String notes, String requester) {
        this.id = id;
        this.bloodGroup = Objects.requireNonNull(bloodGroup, "Blood group must not be null");
        this.units = units;
        this.hospital = Objects.requireNonNull(hospital, "Hospital must not be null");
        this.location = Objects.requireNonNull(location, "Location must not be null");
        this.urgency = urgency != null ? urgency : UrgencyLevel.NORMAL;
        this.status = status != null ? status : RequestStatus.PENDING;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.notes = notes;
        this.requester = requester != null ? requester : "Clinical Staff";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BloodGroup getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(BloodGroup bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        if (units <= 0) {
            throw new IllegalArgumentException("Units required must be greater than zero");
        }
        this.units = units;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public UrgencyLevel getUrgency() {
        return urgency;
    }

    public void setUrgency(UrgencyLevel urgency) {
        this.urgency = urgency;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    /**
     * Enforces deterministic priority ordering across requests.
     */
    @Override
    public int compareTo(EmergencyRequest other) {
        if (other == null) return -1;
        // 1. Urgency priority weight ASC (1=critical, 2=high, 3=normal, 4=low)
        int urgencyCompare = Integer.compare(
                this.urgency.getPriorityWeight(),
                other.urgency.getPriorityWeight()
        );
        if (urgencyCompare != 0) {
            return urgencyCompare;
        }

        // 2. Earlier createdAt first (FIFO within same urgency)
        if (this.createdAt != null && other.createdAt != null) {
            int dateCompare = this.createdAt.compareTo(other.createdAt);
            if (dateCompare != 0) {
                return dateCompare;
            }
        }

        // 3. Stable tie-breaker on ID
        if (this.id != null && other.id != null) {
            return this.id.compareTo(other.id);
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmergencyRequest that = (EmergencyRequest) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
