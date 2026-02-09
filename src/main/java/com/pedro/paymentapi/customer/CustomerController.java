package com.pedro.paymentapi.customer;

import com.pedro.paymentapi.customer.dto.CreateCustomerRequest;
import com.pedro.paymentapi.payment.Payment;
import com.pedro.paymentapi.payment.dto.CreatePaymentRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/customers")
public class CustomerController {
    private CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Customer create(@Valid @RequestBody CreateCustomerRequest request) {
        return service.create(request);
    }


    @GetMapping("/{id}")
    public Customer get(@PathVariable Long id) {
        return service.getById(id);
    }
}
