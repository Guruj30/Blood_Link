package com.bloodlink.repository.jdbc;

import com.bloodlink.model.AvailabilityStatus;
import com.bloodlink.model.BloodGroup;
import com.bloodlink.model.Donor;
import com.bloodlink.repository.DonorRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * JDBC implementation of DonorRepository.
 * Integrates Java OOP Donor entities with MySQL 'donors' table using prepared statements.
 * Owned by Member 4 (Integration & Database Connection).
 */
@Repository
@Primary
@ConditionalOnProperty(name = "bloodlink.persistence.mode", havingValue = "JDBC_MYSQL", matchIfMissing = true)
public class JdbcDonorRepository implements DonorRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcDonorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Donor> rowMapper = (rs, rowNum) -> {
        String id = rs.getString("id");
        String name = rs.getString("name");
        String bgStr = rs.getString("blood_group");
        BloodGroup bloodGroup = BloodGroup.fromCode(bgStr);
        String city = rs.getString("city");
        String phone = rs.getString("phone");
        String availStr = rs.getString("availability");
        AvailabilityStatus availability = AvailabilityStatus.fromValue(availStr);

        Date lastDonationDate = rs.getDate("last_donation");
        LocalDate lastDonation = (lastDonationDate != null) ? lastDonationDate.toLocalDate() : null;

        Date eligibleAfterDate = rs.getDate("eligible_after");
        LocalDate eligibleAfter = (eligibleAfterDate != null) ? eligibleAfterDate.toLocalDate() : null;

        Date registeredDate = rs.getDate("registered");
        LocalDate registered = (registeredDate != null) ? registeredDate.toLocalDate() : LocalDate.now();

        String notes = rs.getString("notes");

        return new Donor(id, name, bloodGroup, city, phone, availability, lastDonation, eligibleAfter, registered, notes);
    };

    @Override
    public List<Donor> findAll() {
        String sql = "SELECT id, name, blood_group, city, phone, availability, last_donation, eligible_after, registered, notes FROM donors ORDER BY id ASC";
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public Optional<Donor> findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return Optional.empty();
        }
        String sql = "SELECT id, name, blood_group, city, phone, availability, last_donation, eligible_after, registered, notes FROM donors WHERE id = ?";
        try {
            Donor donor = jdbcTemplate.queryForObject(sql, rowMapper, id.trim());
            return Optional.ofNullable(donor);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Donor save(Donor donor) {
        Objects.requireNonNull(donor, "Donor cannot be null");
        if (donor.getId() == null || donor.getId().trim().isEmpty()) {
            donor.setId(generateNextId());
        }

        String sql = "INSERT INTO donors (id, name, blood_group, city, phone, availability, last_donation, eligible_after, registered, notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE " +
                     "name = VALUES(name), blood_group = VALUES(blood_group), city = VALUES(city), " +
                     "phone = VALUES(phone), availability = VALUES(availability), " +
                     "last_donation = VALUES(last_donation), eligible_after = VALUES(eligible_after), " +
                     "registered = VALUES(registered), notes = VALUES(notes)";

        Date lastDonationSql = donor.getLastDonation() != null ? Date.valueOf(donor.getLastDonation()) : null;
        Date eligibleAfterSql = donor.getEligibleAfter() != null ? Date.valueOf(donor.getEligibleAfter()) : null;
        Date registeredSql = donor.getRegistered() != null ? Date.valueOf(donor.getRegistered()) : Date.valueOf(LocalDate.now());
        String availabilityStr = donor.getAvailability() != null ? donor.getAvailability().getValue().toUpperCase() : "AVAILABLE";
        String bloodGroupStr = donor.getBloodGroup() != null ? donor.getBloodGroup().getCode() : "O+";

        jdbcTemplate.update(sql,
                donor.getId(),
                donor.getName(),
                bloodGroupStr,
                donor.getCity(),
                donor.getPhone(),
                availabilityStr,
                lastDonationSql,
                eligibleAfterSql,
                registeredSql,
                donor.getNotes()
        );

        return donor;
    }

    @Override
    public boolean deleteById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        String sql = "DELETE FROM donors WHERE id = ?";
        int rows = jdbcTemplate.update(sql, id.trim());
        return rows > 0;
    }

    @Override
    public List<Donor> findByBloodGroup(BloodGroup bloodGroup) {
        if (bloodGroup == null) return Collections.emptyList();
        String sql = "SELECT id, name, blood_group, city, phone, availability, last_donation, eligible_after, registered, notes FROM donors WHERE blood_group = ? ORDER BY id ASC";
        return jdbcTemplate.query(sql, rowMapper, bloodGroup.getCode());
    }

    @Override
    public List<Donor> findByCity(String city) {
        if (city == null || city.trim().isEmpty()) return Collections.emptyList();
        String sql = "SELECT id, name, blood_group, city, phone, availability, last_donation, eligible_after, registered, notes FROM donors WHERE LOWER(city) = LOWER(?) ORDER BY id ASC";
        return jdbcTemplate.query(sql, rowMapper, city.trim());
    }

    @Override
    public List<Donor> findByAvailability(AvailabilityStatus availability) {
        if (availability == null) return Collections.emptyList();
        String sql = "SELECT id, name, blood_group, city, phone, availability, last_donation, eligible_after, registered, notes FROM donors WHERE UPPER(availability) = UPPER(?) ORDER BY id ASC";
        return jdbcTemplate.query(sql, rowMapper, availability.getValue());
    }

    @Override
    public String generateNextId() {
        try {
            String sql = "SELECT id FROM donors WHERE id LIKE 'D-%' ORDER BY LENGTH(id) DESC, id DESC LIMIT 1";
            List<String> results = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("id"));
            if (results.isEmpty()) {
                return "D-10001";
            }
            String maxId = results.get(0);
            String numericPart = maxId.replaceAll("\\D+", "");
            int nextVal = numericPart.isEmpty() ? 10001 : Integer.parseInt(numericPart) + 1;
            return "D-" + nextVal;
        } catch (Exception e) {
            return "D-" + (10000 + (int)(Math.random() * 90000));
        }
    }
}
