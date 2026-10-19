package com.bloodlink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application entry point for the BloodLink Healthcare Platform.
 * Provides REST services for donor matching, blood inventory management,
 * and emergency request coordination.
 */
@SpringBootApplication
public class BloodLinkApplication {

    public static void main(String[] args) {
        SpringApplication.run(BloodLinkApplication.class, args);
    }
}
