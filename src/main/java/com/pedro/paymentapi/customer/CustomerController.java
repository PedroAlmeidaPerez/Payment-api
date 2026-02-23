package com.pedro.paymentapi.customer;

import com.pedro.paymentapi.ApiPaths;
import com.pedro.paymentapi.customer.dto.CreateCustomerRequest;
import com.pedro.paymentapi.customer.dto.CustomerResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(ApiPaths.V + "/customers")
public class CustomerController {
    private final CustomerService service;
    private final CustomerMapper customerMapper;

    public CustomerController(CustomerService service, CustomerMapper customerMapper) {
        this.service = service;
        this.customerMapper = customerMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse create(@Valid @RequestBody CreateCustomerRequest request) {
        return customerMapper.toResponse(service.create(request));
    }


    @GetMapping("/{id}")
    public CustomerResponse get(@PathVariable Long id) {
        return customerMapper.toResponse(service.getById(id));
    }
}
