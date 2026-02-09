package com.pedro.paymentapi.payment;

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
class PaymentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createPayment_returns201_andPersists() throws Exception {
        String body = "{\"amount\":9999.50,\"currency\":\"EUR\",\"description\":\"hola\"}";

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.amount", is(9999.50)))
                .andExpect(jsonPath("$.currency", is("EUR")))
                .andExpect(jsonPath("$.description", is("hola")))
                .andExpect(jsonPath("$.status", is("CREATED")))
                .andExpect(jsonPath("$.createdAt", notNullValue()));
    }

    @Test
    void createPayment_invalidBody_returns400() throws Exception {
        // amount too small + currency wrong length
        String body = "{\"amount\":0,\"currency\":\"E\"}";

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createThenGet_returnsSamePayment() throws Exception {
        String body = "{\"amount\":10.00,\"currency\":\"EUR\",\"description\":\"test\"}";

        String response = mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extraer id sin librerías extra (simple)
        // response contiene: ..."id":1,...
        long id = Long.parseLong(response.replaceAll(".*\"id\"\\s*:\\s*(\\d+).*", "$1"));

        mockMvc.perform(get("/payments/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) id)))
                .andExpect(jsonPath("$.amount", is(10.00)))
                .andExpect(jsonPath("$.currency", is("EUR")))
                .andExpect(jsonPath("$.description", is("test")))
                .andExpect(jsonPath("$.status", is("CREATED")));
    }
}
