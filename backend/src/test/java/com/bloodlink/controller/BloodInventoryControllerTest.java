package com.bloodlink.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Blood Inventory Controller HTTP Tests")
class BloodInventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/inventory returns 8 standard blood group inventories")
    void testGetAllInventory() throws Exception {
        mockMvc.perform(get("/api/inventory")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(8)))
                .andExpect(jsonPath("$[*].group", hasItems("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")));
    }

    @Test
    @DisplayName("GET /api/inventory/{bloodGroup} returns single group stock")
    void testGetInventoryByGroup() throws Exception {
        mockMvc.perform(get("/api/inventory/A+")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.group").value("A+"))
                .andExpect(jsonPath("$.units").isNumber())
                .andExpect(jsonPath("$.lowThreshold").isNumber());
    }

    @Test
    @DisplayName("PUT /api/inventory/{bloodGroup}/adjust updates stock")
    void testAdjustStock() throws Exception {
        String json = """
                {
                    "units": 55,
                    "lowThreshold": 18,
                    "mode": "set"
                }
                """;

        mockMvc.perform(put("/api/inventory/A+/adjust")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.group").value("A+"))
                .andExpect(jsonPath("$.units").value(55))
                .andExpect(jsonPath("$.lowThreshold").value(18));
    }

    @Test
    @DisplayName("PUT /api/inventory/{bloodGroup}/adjust with negative units returns 400 Bad Request")
    void testAdjustStockNegativeUnits() throws Exception {
        String json = """
                {
                    "units": -5,
                    "mode": "set"
                }
                """;

        mockMvc.perform(put("/api/inventory/A+/adjust")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"));
    }
}
