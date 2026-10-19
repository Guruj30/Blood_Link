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
@DisplayName("Donor Controller HTTP Tests")
class DonorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/donors returns initial list of donors")
    void testGetDonors() throws Exception {
        mockMvc.perform(get("/api/donors")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(10))))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].bloodGroup").exists());
    }

    @Test
    @DisplayName("GET /api/donors/{id} returns donor details for valid ID")
    void testGetDonorById() throws Exception {
        mockMvc.perform(get("/api/donors/D-10041")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("D-10041"))
                .andExpect(jsonPath("$.name").value("Arjun Menon"))
                .andExpect(jsonPath("$.bloodGroup").value("O+"));
    }

    @Test
    @DisplayName("GET /api/donors/{id} returns 404 for unknown donor ID")
    void testGetDonorByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/donors/D-999999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value(containsString("Donor not found")));
    }

    @Test
    @DisplayName("POST /api/donors with valid payload returns 201 CREATED")
    void testRegisterDonorValid() throws Exception {
        String json = """
                {
                    "name": "Dr. Aarav Sharma",
                    "bloodGroup": "B+",
                    "city": "Chennai",
                    "phone": "+91-98400-11223",
                    "availability": "available",
                    "lastDonation": "2026-06-01",
                    "notes": "Testing controller registration"
                }
                """;

        mockMvc.perform(post("/api/donors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Dr. Aarav Sharma"))
                .andExpect(jsonPath("$.bloodGroup").value("B+"));
    }

    @Test
    @DisplayName("POST /api/donors with invalid payload returns 400 with validation errors")
    void testRegisterDonorInvalid() throws Exception {
        String json = """
                {
                    "name": "",
                    "bloodGroup": "INVALID_GROUP",
                    "city": ""
                }
                """;

        mockMvc.perform(post("/api/donors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.validationErrors.name").exists())
                .andExpect(jsonPath("$.validationErrors.bloodGroup").exists())
                .andExpect(jsonPath("$.validationErrors.city").exists());
    }

    @Test
    @DisplayName("GET /api/donors/compatible returns compatible donors for recipient")
    void testCompatibleDonorsEndpoint() throws Exception {
        mockMvc.perform(get("/api/donors/compatible?recipientBloodGroup=O-")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].bloodGroup", everyItem(is("O-"))));
    }
}
