package com.pedro.paymentapi.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    private static final String API = com.pedro.paymentapi.ApiPaths.V;

    @Test
    void createCustomer_returns201_andPersists() throws Exception {
        String body = "{\"email\":\"paco@hotmail.com\",\"fullName\":\"Pedro A P\"}";

        mockMvc.perform(post(API + "/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.email", is("paco@hotmail.com")))
                .andExpect(jsonPath("$.fullName", is("Pedro A P")))
                .andExpect(jsonPath("$.createdAt", notNullValue()));

    }

    @Test
    void getCustomer_notFound_returns404() throws Exception {
        mockMvc.perform(get(API + "/customers/{id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

}
