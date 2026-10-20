package com.bloodlink.dto;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Health check response DTO.
 */
public class HealthResponseDto {

    private String status;
    private String application;
    private String version;
    private LocalDateTime timestamp;
    private String persistenceMode;
    private Map<String, Object> details;

    public HealthResponseDto() {
        this.timestamp = LocalDateTime.now();
    }

    public HealthResponseDto(String status, String application, String version, String persistenceMode, Map<String, Object> details) {
        this.status = status;
        this.application = application;
        this.version = version;
        this.timestamp = LocalDateTime.now();
        this.persistenceMode = persistenceMode;
        this.details = details;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getPersistenceMode() {
        return persistenceMode;
    }

    public void setPersistenceMode(String persistenceMode) {
        this.persistenceMode = persistenceMode;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }
}
