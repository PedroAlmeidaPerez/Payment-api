package com.pedro.paymentapi.customer;

import com.pedro.paymentapi.customer.dto.CustomerResponse;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


class CustomerMapperTest {

    private CustomerMapper customerMapper = new CustomerMapper();
    @Test
    void toResponse_mapsFiles() {
        Customer c = new Customer();
        c.setId(1L);
        c.setEmail("pedro@gmail.com");
        c.setFullName("Pedro Almeida Perez");
        c.setCreatedAt(Instant.parse("2026-02-21T10:00:00Z"));

        CustomerResponse res = customerMapper.toResponse(c);

        assertThat(res.getId(), is(1L));
        assertThat(res.getEmail(), is("pedro@gmail.com"));
        assertThat(res.getFullName(), is("Pedro Almeida Perez"));
        assertThat(res.getCreatedAt(), is(Instant.parse("2026-02-21T10:00:00Z")));
    }
}