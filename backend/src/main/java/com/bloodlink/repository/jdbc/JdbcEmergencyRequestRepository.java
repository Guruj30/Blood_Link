package com.bloodlink.repository.jdbc;

import com.bloodlink.model.BloodGroup;
import com.bloodlink.model.EmergencyRequest;
import com.bloodlink.model.RequestStatus;
import com.bloodlink.model.UrgencyLevel;
import com.bloodlink.repository.EmergencyRequestRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * JDBC implementation of EmergencyRequestRepository.
 * Integrates emergency requests and clinical priority workflow with MySQL 'emergency_requests' table.
 * Owned by Member 4 (Integration & Database Connection).
 */
@Repository
@Primary
@ConditionalOnProperty(name = "bloodlink.persistence.mode", havingValue = "JDBC_MYSQL", matchIfMissing = true)
public class JdbcEmergencyRequestRepository implements EmergencyRequestRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcEmergencyRequestRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<EmergencyRequest> rowMapper = (rs, rowNum) -> {
        String id = rs.getString("id");
        String bgStr = rs.getString("blood_group");
        BloodGroup bloodGroup = BloodGroup.fromCode(bgStr);
        int units = rs.getInt("units");
        String hospital = rs.getString("hospital");
        String location = rs.getString("location");
        String urgencyStr = rs.getString("urgency");
        UrgencyLevel urgency = UrgencyLevel.fromValue(urgencyStr);
        String statusStr = rs.getString("status");
        RequestStatus status = RequestStatus.fromValue(statusStr);
        Timestamp createdAtTs = rs.getTimestamp("created_at");
        LocalDateTime createdAt = (createdAtTs != null) ? createdAtTs.toLocalDateTime() : LocalDateTime.now();
        String notes = rs.getString("notes");
        String requester = rs.getString("requester");

        return new EmergencyRequest(id, bloodGroup, units, hospital, location, urgency, status, createdAt, notes, requester);
    };

    @Override
    public List<EmergencyRequest> findAll() {
        String sql = "SELECT id, blood_group, units, hospital, location, urgency, status, created_at, notes, requester FROM emergency_requests ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public Optional<EmergencyRequest> findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return Optional.empty();
        }
        String sql = "SELECT id, blood_group, units, hospital, location, urgency, status, created_at, notes, requester FROM emergency_requests WHERE id = ?";
        try {
            EmergencyRequest req = jdbcTemplate.queryForObject(sql, rowMapper, id.trim());
            return Optional.ofNullable(req);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public EmergencyRequest save(EmergencyRequest request) {
        Objects.requireNonNull(request, "EmergencyRequest cannot be null");
        if (request.getId() == null || request.getId().trim().isEmpty()) {
            request.setId(generateNextId());
        }

        String sql = "INSERT INTO emergency_requests (id, blood_group, units, hospital, location, urgency, status, created_at, notes, requester) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE " +
                     "blood_group = VALUES(blood_group), units = VALUES(units), " +
                     "hospital = VALUES(hospital), location = VALUES(location), " +
                     "urgency = VALUES(urgency), status = VALUES(status), " +
                     "notes = VALUES(notes), requester = VALUES(requester)";

        LocalDateTime created = request.getCreatedAt() != null ? request.getCreatedAt() : LocalDateTime.now();
        Timestamp createdTs = Timestamp.valueOf(created);
        String urgencyStr = request.getUrgency() != null ? request.getUrgency().getValue().toUpperCase() : "NORMAL";
        String statusStr = request.getStatus() != null ? request.getStatus().getValue().toUpperCase() : "PENDING";
        String bloodGroupStr = request.getBloodGroup() != null ? request.getBloodGroup().getCode() : "O+";

        jdbcTemplate.update(sql,
                request.getId(),
                bloodGroupStr,
                request.getUnits(),
                request.getHospital(),
                request.getLocation(),
                urgencyStr,
                statusStr,
                createdTs,
                request.getNotes(),
                request.getRequester()
        );

        return request;
    }

    @Override
    public boolean deleteById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        String sql = "DELETE FROM emergency_requests WHERE id = ?";
        int rows = jdbcTemplate.update(sql, id.trim());
        return rows > 0;
    }

    @Override
    public List<EmergencyRequest> findByStatus(RequestStatus status) {
        if (status == null) return Collections.emptyList();
        String sql = "SELECT id, blood_group, units, hospital, location, urgency, status, created_at, notes, requester FROM emergency_requests WHERE UPPER(status) = UPPER(?) ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, rowMapper, status.getValue());
    }

    @Override
    public List<EmergencyRequest> findByUrgency(UrgencyLevel urgency) {
        if (urgency == null) return Collections.emptyList();
        String sql = "SELECT id, blood_group, units, hospital, location, urgency, status, created_at, notes, requester FROM emergency_requests WHERE UPPER(urgency) = UPPER(?) ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, rowMapper, urgency.getValue());
    }

    @Override
    public List<EmergencyRequest> findByBloodGroup(BloodGroup bloodGroup) {
        if (bloodGroup == null) return Collections.emptyList();
        String sql = "SELECT id, blood_group, units, hospital, location, urgency, status, created_at, notes, requester FROM emergency_requests WHERE blood_group = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, rowMapper, bloodGroup.getCode());
    }

    @Override
    public String generateNextId() {
        try {
            String sql = "SELECT id FROM emergency_requests WHERE id LIKE 'REQ-%' ORDER BY LENGTH(id) DESC, id DESC LIMIT 1";
            List<String> results = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("id"));
            if (results.isEmpty()) {
                return "REQ-2025";
            }
            String maxId = results.get(0);
            String numericPart = maxId.replaceAll("\\D+", "");
            int nextVal = numericPart.isEmpty() ? 2025 : Integer.parseInt(numericPart) + 1;
            return "REQ-" + nextVal;
        } catch (Exception e) {
            return "REQ-" + (2000 + (int)(Math.random() * 8000));
        }
    }
}
