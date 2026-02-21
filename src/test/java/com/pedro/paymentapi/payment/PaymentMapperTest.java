package com.pedro.paymentapi.payment;

import com.pedro.paymentapi.payment.dto.PaymentResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class PaymentMapperTest {

    private PaymentMapper paymentMapper = new PaymentMapper();

    @Test
    void toResponse() {
        Payment p = new Payment();
        p.setId(1L);
        p.setAmount(BigDecimal.valueOf(11.0));
        p.setCurrency("EUR");
        p.setDescription("TEST");
        p.setStatus(PaymentStatus.CONFIRMED);
        p.setCreatedAt(Instant.parse("2026-02-21T10:00:00Z"));
        //p.setCustomerId(p.getCustomer() != null ? p.getCustomer().getId() : null);

        PaymentResponse resp = paymentMapper.toResponse(p);

        assertThat(resp.getId(), is(1L));
        assertThat(resp.getAmount(), is(BigDecimal.valueOf(11.0)));
        assertThat(resp.getCurrency(), is("EUR"));
        assertThat(resp.getDescription(), is("TEST"));
        assertThat(resp.getStatus(), is("CONFIRMED"));
        assertThat(resp.getCreatedAt(), is(Instant.parse( "2026-02-21T10:00:00Z")));

    }
}