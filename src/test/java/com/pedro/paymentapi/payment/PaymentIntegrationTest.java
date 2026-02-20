package com.pedro.paymentapi.payment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerPaymentsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private long createCustomerAndReturnId() throws Exception {
        String email = "pedro_test_" + System.nanoTime() + "@hotmail.com";
        String customerBody = "{\"email\":\"" + email + "\",\"fullName\":\"Pedro Test\"}";

        String response = mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return Long.parseLong(response.replaceAll(".*\"id\"\\s*:\\s*(\\d+).*", "$1"));
    }

    @Test
    void createPaymentForCustomer_returns201() throws Exception {
        long customerId = createCustomerAndReturnId();
        String body = "{\"amount\":9999.50,\"currency\":\"EUR\",\"description\":\"hola\"}";

        mockMvc.perform(post("/customers/{customerId}/payments", customerId)
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
    void payment_canBeConfirmed_fromCreated()throws Exception {
        long customerId = createCustomerAndReturnId();
        String body = "{\"amount\":10.50,\"currency\":\"EUR\",\"description\":\"description2\"}";
        String paymentResponse = mockMvc.perform(post("/customers/{customerId}/payments", customerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long paymentId = Long.parseLong(
                paymentResponse.replaceAll(".*\"id\"\\s*:\\s*(\\d+).*", "$1")
        );

        mockMvc.perform(post("/payments/{id}/confirm", paymentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CONFIRMED")));



    }

    @Test
    void payment_canBeCanceled_fromCreated()throws Exception {
        long customerId = createCustomerAndReturnId();
        String body = "{\"amount\":10.50,\"currency\":\"EUR\",\"description\":\"description2\"}";
        String paymentResponse = mockMvc.perform(post("/customers/{customerId}/payments", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long paymentId = Long.parseLong(
                paymentResponse.replaceAll(".*\"id\"\\s*:\\s*(\\d+).*", "$1")
        );

        mockMvc.perform(post("/payments/{id}/cancel", paymentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CANCELLED")));
    }

    @Test
    void payment_canNotBeCanceled_fromConfirmed() throws Exception {
        long customerId = createCustomerAndReturnId();

        String body = "{\"amount\":12.50,\"currency\":\"EUR\",\"description\":\"description3\"}";

        String paymentResponse = mockMvc.perform(post("/customers/{customerId}/payments", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long paymentId = Long.parseLong(paymentResponse.replaceAll(".*\"id\"\\s*:\\s*(\\d+).*", "$1"));

        // Confirmar
        mockMvc.perform(post("/payments/{id}/confirm", paymentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CONFIRMED")));

        // Intentar cancelar (debe fallar)
        mockMvc.perform(post("/payments/{id}/cancel", paymentId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", notNullValue()));
    }

    @Test
    void payment_canNotBeConfirmed_fromCancelled() throws Exception {
        long customerId = createCustomerAndReturnId();

        String body = "{\"amount\":15.50,\"currency\":\"EUR\",\"description\":\"description4\"}";

        String paymentResponse = mockMvc.perform(post("/customers/{customerId}/payments", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long paymentId = Long.parseLong(paymentResponse.replaceAll(".*\"id\"\\s*:\\s*(\\d+).*", "$1"));

        // Confirmar
        mockMvc.perform(post("/payments/{id}/cancel", paymentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CANCELLED")));

        // Intentar cancelar (debe fallar)
        mockMvc.perform(post("/payments/{id}/confirm", paymentId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", notNullValue()));
    }


    @Test
    void createPayment_invalidBody_returns400() throws Exception {
        long customerId = createCustomerAndReturnId();

        // amount too small + currency wrong length
        String body = "{\"amount\":0,\"currency\":\"E\"}";

        mockMvc.perform(post("/customers/{customerId}/payments", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", notNullValue()))
                .andExpect(jsonPath("$.fields", notNullValue()));
    }

    @Test
    void listPayments_afterCreate_containsCreatedPayment() throws Exception {
        long customerId = createCustomerAndReturnId();

        String body = "{\"amount\":10.00,\"currency\":\"EUR\",\"description\":\"test\"}";

        mockMvc.perform(post("/customers/{customerId}/payments", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/customers/{customerId}/payments", customerId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", isA(java.util.List.class)))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].amount", is(10.00)))
                .andExpect(jsonPath("$[0].currency", is("EUR")))
                .andExpect(jsonPath("$[0].description", is("test")))
                .andExpect(jsonPath("$[0].status", is("CREATED")));
    }

    @Test
    void createPayment_missingCustomer_returns404() throws Exception {
        String body = "{\"amount\":10.00,\"currency\":\"EUR\",\"description\":\"test\"}";

        mockMvc.perform(post("/customers/{customerId}/payments", 999999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    void summary_withMultiplePayments_returnsAggregatedData() throws  Exception {
        long customerId = createCustomerAndReturnId();

        String body = "{\"amount\":10.00,\"currency\":\"EUR\",\"description\":\"test\"}";
        String body2 = "{\"amount\":11.00,\"currency\":\"EUR\",\"description\":\"test1\"}";
        String body3 = "{\"amount\":12.00,\"currency\":\"EUR\",\"description\":\"test2\"}";

        mockMvc.perform(post("/customers/{customerId}/payments", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/customers/{customerId}/payments", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body2))
                .andExpect(status().isCreated());


        String paymentResponse = mockMvc.perform(post("/customers/{customerId}/payments", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body3))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long paymentId = Long.parseLong(
                paymentResponse.replaceAll(".*\"id\"\\s*:\\s*(\\d+).*", "$1")
        );

        mockMvc.perform(post("/payments/{id}/cancel", paymentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CANCELLED")));

        mockMvc.perform(get("/customers/{customerId}/payments/summary", customerId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isMap())
                //.andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.totalAmount").value(33.00));
    }
}
