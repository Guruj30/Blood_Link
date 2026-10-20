package com.bloodlink.repository.jdbc;

import com.bloodlink.model.BloodGroup;
import com.bloodlink.model.BloodInventoryItem;
import com.bloodlink.repository.BloodInventoryRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * JDBC implementation of BloodInventoryRepository.
 * Integrates aggregate inventory metrics with MySQL 'blood_inventory' table.
 * Owned by Member 4 (Integration & Database Connection).
 */
@Repository
@Primary
@ConditionalOnProperty(name = "bloodlink.persistence.mode", havingValue = "JDBC_MYSQL", matchIfMissing = true)
public class JdbcBloodInventoryRepository implements BloodInventoryRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcBloodInventoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<BloodInventoryItem> rowMapper = (rs, rowNum) -> {
        String id = rs.getString("id");
        String bgStr = rs.getString("blood_group");
        BloodGroup bloodGroup = BloodGroup.fromCode(bgStr);
        int units = rs.getInt("units");
        int lowThreshold = rs.getInt("low_threshold");
        Timestamp lastUpdatedTs = rs.getTimestamp("last_updated");
        LocalDateTime lastUpdated = (lastUpdatedTs != null) ? lastUpdatedTs.toLocalDateTime() : LocalDateTime.now();

        return new BloodInventoryItem(id, bloodGroup, units, lowThreshold, lastUpdated);
    };

    @Override
    public List<BloodInventoryItem> findAll() {
        String sql = "SELECT id, blood_group, units, low_threshold, last_updated FROM blood_inventory ORDER BY id ASC";
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public Optional<BloodInventoryItem> findByBloodGroup(BloodGroup group) {
        if (group == null) {
            return Optional.empty();
        }
        String sql = "SELECT id, blood_group, units, low_threshold, last_updated FROM blood_inventory WHERE blood_group = ?";
        try {
            BloodInventoryItem item = jdbcTemplate.queryForObject(sql, rowMapper, group.getCode());
            return Optional.ofNullable(item);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<BloodInventoryItem> findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return Optional.empty();
        }
        String sql = "SELECT id, blood_group, units, low_threshold, last_updated FROM blood_inventory WHERE id = ?";
        try {
            BloodInventoryItem item = jdbcTemplate.queryForObject(sql, rowMapper, id.trim());
            return Optional.ofNullable(item);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public BloodInventoryItem save(BloodInventoryItem item) {
        Objects.requireNonNull(item, "BloodInventoryItem cannot be null");
        if (item.getId() == null || item.getId().trim().isEmpty()) {
            item.setId("INV-" + item.getBloodGroup().getCode().replace("+", "POS").replace("-", "NEG"));
        }

        String sql = "INSERT INTO blood_inventory (id, blood_group, units, low_threshold, last_updated) " +
                     "VALUES (?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE " +
                     "blood_group = VALUES(blood_group), units = VALUES(units), " +
                     "low_threshold = VALUES(low_threshold), last_updated = VALUES(last_updated)";

        LocalDateTime updated = item.getLastUpdated() != null ? item.getLastUpdated() : LocalDateTime.now();
        Timestamp updatedTs = Timestamp.valueOf(updated);

        jdbcTemplate.update(sql,
                item.getId(),
                item.getBloodGroup().getCode(),
                item.getUnits(),
                item.getLowThreshold(),
                updatedTs
        );

        return item;
    }
}
