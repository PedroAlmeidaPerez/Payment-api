package com.pedro.paymentapi.customer;

import com.pedro.paymentapi.customer.dto.CreateCustomerRequest;
import com.pedro.paymentapi.error.NotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class CustomerService {

    private final CustomerRepository repository;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    public Customer create(CreateCustomerRequest req) {
        Customer customer = new Customer();
        customer.setEmail(req.getEmail());
        customer.setFullName(req.getFullName());
        customer.setCreatedAt(Instant.now());

        return repository.save(customer);
    }

    public Customer getById (Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found: " + id));
    }
}
