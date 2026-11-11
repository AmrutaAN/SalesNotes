package com.salesnotes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salesnotes.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("devlocal")
class CatalogueTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;
    private String employeeToken;

    @BeforeEach
    void setUp() throws Exception {
        // Admin token
        LoginRequest adminReq = new LoginRequest("companyadmin", "Admin@123");
        MvcResult adminRes = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminReq)))
                .andExpect(status().isOk())
                .andReturn();
        LoginResponse adminLogin = objectMapper.readValue(adminRes.getResponse().getContentAsString(), LoginResponse.class);
        this.adminToken = "Bearer " + adminLogin.getAccessToken();

        // Employee token
        LoginRequest empReq = new LoginRequest("companyemployee", "User@123");
        MvcResult empRes = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(empReq)))
                .andExpect(status().isOk())
                .andReturn();
        LoginResponse empLogin = objectMapper.readValue(empRes.getResponse().getContentAsString(), LoginResponse.class);
        this.employeeToken = "Bearer " + empLogin.getAccessToken();
    }

    @Test
    @DisplayName("Should create catalogue item, audit price change in changelog, and calculate tiered quote")
    void shouldCreateItemAndCalculateTieredQuote() throws Exception {
        String uniqueSku = "SKU-VALVE-" + System.currentTimeMillis();

        // 1. Create Catalogue Item (Admin)
        CatalogueItemRequestDto req = new CatalogueItemRequestDto();
        req.setItemCode(uniqueSku);
        req.setItemName("Precision Industrial Ball Valve 2-inch");
        req.setCategory("Valves");
        req.setUom("Pcs");
        req.setBasePrice(new BigDecimal("2000.00"));
        req.setDefaultDiscountPct(new BigDecimal("5.00"));
        req.setTaxRatePct(new BigDecimal("18.00"));
        req.setHasTieredPricing(true);
        req.setPriceTiers(List.of(
                new ItemPriceTierDto(1, 49, new BigDecimal("5.00"), "Standard (1-49 Pcs)"),
                new ItemPriceTierDto(50, 99, new BigDecimal("10.00"), "Wholesale (50-99 Pcs)"),
                new ItemPriceTierDto(100, null, new BigDecimal("15.00"), "Bulk (100+ Pcs)")
        ));

        MvcResult createResult = mockMvc.perform(post("/api/v1/catalogue")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.itemCode").value(uniqueSku))
                .andExpect(jsonPath("$.netPrice").value(1900.00))
                .andReturn();

        CatalogueItemResponseDto created = objectMapper.readValue(createResult.getResponse().getContentAsString(), CatalogueItemResponseDto.class);
        String itemId = created.getId();

        // 2. Update price from 2000.00 to 2200.00 and verify audit in changelog
        CatalogueItemRequestDto updateReq = new CatalogueItemRequestDto();
        updateReq.setBasePrice(new BigDecimal("2200.00"));
        updateReq.setNotes("Raw steel price increase Q3");

        mockMvc.perform(patch("/api/v1/catalogue/" + itemId)
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.basePrice").value(2200.00));

        // Verify Changelog has recorded the change
        MvcResult logResult = mockMvc.perform(get("/api/v1/catalogue/" + itemId + "/changelog")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andReturn();

        ItemChangeLogResponseDto[] logs = objectMapper.readValue(logResult.getResponse().getContentAsString(), ItemChangeLogResponseDto[].class);
        assertThat(logs.length).isGreaterThanOrEqualTo(2); // item_created + field_update

        // 3. Employee requests a tiered price quote for 60 units (Wholesale tier: 10% discount)
        // Base = 2200. 10% off = 1980 net unit price. Subtotal for 60 units = 118,800.
        // Tax 18% = 21,384. Total = 140,184.
        mockMvc.perform(get("/api/v1/catalogue/" + itemId + "/quote")
                        .header("Authorization", employeeToken)
                        .param("quantity", "60"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.applicableDiscountPct").value(10.00))
                .andExpect(jsonPath("$.netUnitPrice").value(1980.00))
                .andExpect(jsonPath("$.subtotal").value(118800.00))
                .andExpect(jsonPath("$.totalAmount").value(140184.00));

        // 4. Verify RBAC: Employee cannot modify catalogue items
        mockMvc.perform(delete("/api/v1/catalogue/" + itemId)
                        .header("Authorization", employeeToken))
                .andExpect(status().isForbidden());
    }
}
