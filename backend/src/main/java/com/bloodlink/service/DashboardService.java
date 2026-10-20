package com.bloodlink.service;

import com.bloodlink.dto.*;
import com.bloodlink.exception.InvalidBloodGroupException;
import com.bloodlink.model.AvailabilityStatus;
import com.bloodlink.model.BloodGroup;
import com.bloodlink.model.EmergencyRequest;
import com.bloodlink.model.RequestStatus;
import com.bloodlink.repository.DonorRepository;
import com.bloodlink.repository.EmergencyRequestRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service providing aggregated clinical KPIs and reports for Overview and Reports views.
 */
@Service
public class DashboardService {

    private final DonorRepository donorRepository;
    private final EmergencyRequestRepository requestRepository;
    private final BloodInventoryService inventoryService;
    private final RedCellCompatibilityEngine redCellEngine;
    private final PlasmaCompatibilityEngine plasmaEngine;
    private final EmergencyRequestService requestService;

    public DashboardService(DonorRepository donorRepository,
                            EmergencyRequestRepository requestRepository,
                            BloodInventoryService inventoryService,
                            RedCellCompatibilityEngine redCellEngine,
                            PlasmaCompatibilityEngine plasmaEngine,
                            EmergencyRequestService requestService) {
        this.donorRepository = donorRepository;
        this.requestRepository = requestRepository;
        this.inventoryService = inventoryService;
        this.redCellEngine = redCellEngine;
        this.plasmaEngine = plasmaEngine;
        this.requestService = requestService;
    }

    public DashboardSummaryDto getDashboardSummary() {
        var allDonors = donorRepository.findAll();
        var allRequests = requestRepository.findAll();
        var allInventory = inventoryService.getAllInventory();

        int totalDonors = allDonors.size();
        int availableDonors = (int) allDonors.stream()
                .filter(d -> d.getAvailability() == AvailabilityStatus.AVAILABLE)
                .count();

        int totalUnits = allInventory.stream()
                .mapToInt(BloodInventoryDto::getUnits)
                .sum();

        int activeRequests = (int) allRequests.stream()
                .filter(r -> r.getStatus() == RequestStatus.PENDING ||
                             r.getStatus() == RequestStatus.REVIEWING ||
                             r.getStatus() == RequestStatus.IN_PROGRESS)
                .count();

        int fulfilledRequests = (int) allRequests.stream()
                .filter(r -> r.getStatus() == RequestStatus.FULFILLED)
                .count();

        List<BloodInventoryDto> lowStockAlerts = allInventory.stream()
                .filter(i -> i.getUnits() <= i.getLowThreshold())
                .collect(Collectors.toList());

        int lowStockCount = (int) lowStockAlerts.stream().filter(i -> i.getUnits() > 0).count();
        int criticalStockCount = (int) lowStockAlerts.stream().filter(i -> i.getUnits() == 0).count();

        List<EmergencyRequestResponseDto> recentRequests = allRequests.stream()
                .filter(r -> r.getStatus() == RequestStatus.PENDING ||
                             r.getStatus() == RequestStatus.REVIEWING ||
                             r.getStatus() == RequestStatus.IN_PROGRESS)
                .sorted()
                .limit(5)
                .map(requestService::toDto)
                .collect(Collectors.toList());

        return new DashboardSummaryDto(
                totalDonors,
                availableDonors,
                totalUnits,
                activeRequests,
                fulfilledRequests,
                lowStockCount,
                criticalStockCount,
                recentRequests,
                lowStockAlerts
        );
    }

    public ReportsSummaryDto getReportsSummary() {
        var allInventory = inventoryService.getAllInventory();
        var allRequests = requestRepository.findAll();
        var allDonors = donorRepository.findAll();

        Map<String, Long> statusBreakdown = allRequests.stream()
                .collect(Collectors.groupingBy(r -> r.getStatus().getValue(), Collectors.counting()));

        Map<String, Long> urgencyBreakdown = allRequests.stream()
                .collect(Collectors.groupingBy(r -> r.getUrgency().getValue(), Collectors.counting()));

        Map<String, Long> donorAvailBreakdown = allDonors.stream()
                .collect(Collectors.groupingBy(d -> d.getAvailability().getValue(), Collectors.counting()));

        Map<String, Long> donorBgBreakdown = allDonors.stream()
                .collect(Collectors.groupingBy(d -> d.getBloodGroup().getCode(), Collectors.counting()));

        return new ReportsSummaryDto(
                allInventory,
                statusBreakdown,
                urgencyBreakdown,
                donorAvailBreakdown,
                donorBgBreakdown
        );
    }

    public CompatibilityCheckResponseDto checkCompatibility(String donorBgStr, String recipientBgStr, String componentType) {
        BloodGroup donorBg;
        BloodGroup recipientBg;
        try {
            donorBg = BloodGroup.fromCode(donorBgStr);
        } catch (IllegalArgumentException e) {
            throw new InvalidBloodGroupException(donorBgStr);
        }
        try {
            recipientBg = BloodGroup.fromCode(recipientBgStr);
        } catch (IllegalArgumentException e) {
            throw new InvalidBloodGroupException(recipientBgStr);
        }

        CompatibilityRuleEngine engine = "PLASMA".equalsIgnoreCase(componentType) ? plasmaEngine : redCellEngine;
        boolean compatible = engine.isCompatible(donorBg, recipientBg);
        String explanation = engine.explainCompatibility(donorBg, recipientBg);
        List<String> allCompatible = engine.getCompatibleDonorsForRecipient(recipientBg).stream()
                .map(BloodGroup::getCode)
                .collect(Collectors.toList());

        return new CompatibilityCheckResponseDto(
                recipientBg.getCode(),
                donorBg.getCode(),
                compatible,
                engine.getComponentType(),
                allCompatible,
                explanation,
                engine.getMedicalDisclaimer()
        );
    }
}
