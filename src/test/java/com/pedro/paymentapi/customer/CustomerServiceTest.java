package com.pedro.paymentapi.customer;

import com.pedro.paymentapi.customer.dto.CreateCustomerRequest;
import com.pedro.paymentapi.error.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CustomerServiceTest {
    private CustomerRepository customerRepository;
    private CustomerService service;
    @BeforeEach
    void setUp() {
        customerRepository = mock(CustomerRepository.class);
        service = new CustomerService(customerRepository);
    }

    @Test
    void givenCustomer_whenCreate_thenSave() {
        CreateCustomerRequest customerRequest = new CreateCustomerRequest();
        customerRequest.setFullName("Pedro");
        customerRequest.setEmail("pedro@gmail.com");

        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));

        Customer customer = service.create(customerRequest);

        assertThat(customer.getFullName(), is("Pedro"));

    }

    @Test
    void givenExistingCustomerIdIdWhenGetIdThenReturnsCustomer() {
        Customer customer = new Customer();
        Long customerId = 1L;
        customer.setId(customerId);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        Customer result = service.getById(customerId);

        assertThat(result, notNullValue());
        assertThat(result.getId(), is(customerId));
        verify(customerRepository).findById(customerId);
        verifyNoMoreInteractions(customerRepository);
        assertThat(result, is(customer));
    }

    @Test
    void givenMissingCustomerId_whenGetById_thenThrowsNotFoundException() {
        Long customerId = 1L;

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> service.getById(customerId)
        );

        assertTrue(ex.getMessage().contains("Customer not found"));

        verify(customerRepository, times(1)).findById(customerId);
        verifyNoMoreInteractions(customerRepository);
    }
}