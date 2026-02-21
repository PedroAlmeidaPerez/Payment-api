package com.pedro.paymentapi.customer;

import com.pedro.paymentapi.customer.dto.CustomerResponse;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {
    CustomerResponse toResponse(Customer customer) {
        CustomerResponse r = new CustomerResponse();
        r.setId(customer.getId());
        r.setEmail(customer.getEmail());
        r.setFullName(customer.getFullName());
        r.setCreatedAt(customer.getCreatedAt());
        r.setPayments(customer.getPayments());

        return r;
    }
}
