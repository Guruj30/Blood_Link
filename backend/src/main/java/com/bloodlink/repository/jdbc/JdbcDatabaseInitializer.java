package com.bloodlink.repository.jdbc;

import com.bloodlink.model.BloodGroup;
import com.bloodlink.model.BloodInventoryItem;
import com.bloodlink.repository.BloodInventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Initializes database defaults if tables are empty.
 * Ensures all 8 ABO/Rh blood groups are present in 'blood_inventory' to prevent 404s in frontend.
 * Owned by Member 4 (Integration & Database Connection).
 */
@Component
@ConditionalOnProperty(name = "bloodlink.persistence.mode", havingValue = "JDBC_MYSQL")
public class JdbcDatabaseInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(JdbcDatabaseInitializer.class);

    private final BloodInventoryRepository inventoryRepository;

    public JdbcDatabaseInitializer(BloodInventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public void run(String... args) {
        try {
            log.info("Checking blood_inventory table initialization...");
            int index = 1;
            for (BloodGroup group : BloodGroup.values()) {
                if (inventoryRepository.findByBloodGroup(group).isEmpty()) {
                    String id = "INV-" + String.format("%03d", index);
                    // Standard clinical starting baseline
                    int defaultUnits = (group == BloodGroup.O_POSITIVE || group == BloodGroup.A_POSITIVE) ? 20 : 10;
                    inventoryRepository.save(new BloodInventoryItem(id, group, defaultUnits, 5, LocalDateTime.now()));
                    log.info("Initialized baseline inventory item for blood group {}: {} units", group.getCode(), defaultUnits);
                }
                index++;
            }
        } catch (Exception e) {
            log.warn("Database initialization check skipped or deferred: {}", e.getMessage());
        }
    }
}
