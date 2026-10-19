package com.bloodlink.controller;

import com.bloodlink.dto.CompatibilityCheckResponseDto;
import com.bloodlink.dto.DashboardSummaryDto;
import com.bloodlink.dto.ReportsSummaryDto;
import com.bloodlink.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for analytical KPIs, dashboard statistics, and compatibility tests.
 * Consumed by Overview.jsx, Reports.jsx, and diagnostic panels.
 */
@RestController
@RequestMapping("/api")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard/summary")
    public ResponseEntity<DashboardSummaryDto> getDashboardSummary() {
        return ResponseEntity.ok(dashboardService.getDashboardSummary());
    }

    @GetMapping("/reports/summary")
    public ResponseEntity<ReportsSummaryDto> getReportsSummary() {
        return ResponseEntity.ok(dashboardService.getReportsSummary());
    }

    @GetMapping("/compatibility/check")
    public ResponseEntity<CompatibilityCheckResponseDto> checkCompatibility(
            @RequestParam String donorBloodGroup,
            @RequestParam String recipientBloodGroup,
            @RequestParam(defaultValue = "RED_CELL") String componentType) {
        CompatibilityCheckResponseDto result = dashboardService.checkCompatibility(
                donorBloodGroup, recipientBloodGroup, componentType
        );
        return ResponseEntity.ok(result);
    }
}
