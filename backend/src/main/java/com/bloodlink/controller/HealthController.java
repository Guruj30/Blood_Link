package com.bloodlink.controller;

import com.bloodlink.dto.HealthResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Health check REST controller.
 * Exposes system status, persistence mode notice, and operational metrics.
 */
@RestController
@RequestMapping("/api/health")
public class HealthController {

    @Value("${spring.application.name:bloodlink-backend}")
    private String appName;

    @Value("${bloodlink.persistence.mode:IN_MEMORY_MOCKED_PENDING_APPROVED_SCHEMA}")
    private String persistenceMode;

    @GetMapping
    public ResponseEntity<HealthResponseDto> getHealth() {
        Map<String, Object> details = new LinkedHashMap<>();
        details.put("javaVersion", System.getProperty("java.version"));
        details.put("osName", System.getProperty("os.name"));
        details.put("uptimeMs", System.currentTimeMillis() - ProcessHandle.current().info().startInstant().map(java.time.Instant::toEpochMilli).orElse(System.currentTimeMillis()));
        details.put("freeMemoryMb", Runtime.getRuntime().freeMemory() / (1024 * 1024));
        details.put("maxMemoryMb", Runtime.getRuntime().maxMemory() / (1024 * 1024));

        HealthResponseDto response = new HealthResponseDto(
                "UP",
                appName,
                "1.0.0",
                persistenceMode,
                details
        );
        return ResponseEntity.ok(response);
    }
}
