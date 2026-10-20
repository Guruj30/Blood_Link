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
@DisplayName("Emergency Request Controller HTTP Tests")
class EmergencyRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/requests returns clinical emergency requests")
    void testGetAllRequests() throws Exception {
        mockMvc.perform(get("/api/requests")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(5))))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].bloodGroup").exists())
                .andExpect(jsonPath("$[0].urgency").exists());
    }

    @Test
    @DisplayName("POST /api/requests with valid payload creates request and returns 201 CREATED")
    void testCreateEmergencyRequestValid() throws Exception {
        String json = """
                {
                    "bloodGroup": "O-",
                    "units": 2,
                    "hospital": "Stanley Medical College",
                    "location": "Chennai",
                    "urgency": "critical",
                    "notes": "Emergency labor transfusion",
                    "requester": "Dr. Devi"
                }
                """;

        mockMvc.perform(post("/api/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.bloodGroup").value("O-"))
                .andExpect(jsonPath("$.units").value(2))
                .andExpect(jsonPath("$.status").value("pending"))
                .andExpect(jsonPath("$.compatibleBloodGroups", hasItem("O-")));
    }

    @Test
    @DisplayName("POST /api/requests with 0 units returns 400 Bad Request")
    void testCreateEmergencyRequestZeroUnits() throws Exception {
        String json = """
                {
                    "bloodGroup": "O-",
                    "units": 0,
                    "hospital": "Test Hospital",
                    "location": "Chennai"
                }
                """;

        mockMvc.perform(post("/api/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.validationErrors.units").exists());
    }

    @Test
    @DisplayName("PATCH /api/requests/{id}/status updates request status")
    void testUpdateStatus() throws Exception {
        // Create request first
        String createJson = """
                {
                    "bloodGroup": "B+",
                    "units": 1,
                    "hospital": "City Care Hospital",
                    "location": "Salem",
                    "urgency": "normal"
                }
                """;

        String responseContent = mockMvc.perform(post("/api/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // Extract ID
        String id = responseContent.split("\"id\":\"")[1].split("\"")[0];

        String patchJson = """
                {
                    "status": "in_progress",
                    "notes": "Dispatched donor outreach team"
                }
                """;

        mockMvc.perform(patch("/api/requests/" + id + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.status").value("in_progress"))
                .andExpect(jsonPath("$.notes").value(containsString("Dispatched donor outreach team")));
    }
}
