package com.pedro.paymentapi.payment;

import com.pedro.paymentapi.customer.CustomerService;
import com.pedro.paymentapi.error.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PaymentServiceTest {

    private CustomerService customerService;
    private PaymentRepository paymentRepository;
    private PaymentService service;
    @BeforeEach
    void setUp() {
        paymentRepository = mock(PaymentRepository.class);
        customerService = mock(CustomerService.class);
        service = new PaymentService(paymentRepository, customerService);
    }
    @Test
    void givenPayment_whenCreated_thenConfirmed () {
        //given
        long paymentId = 130L;
        Payment p = new Payment();
        p.setId(paymentId);
        p.setStatus(PaymentStatus.CREATED);
        p.setCreatedAt(Instant.now());

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(p));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        Payment result = service.confirm(paymentId);

        assertThat(result.getStatus(), is(PaymentStatus.CONFIRMED));

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus(), is(PaymentStatus.CONFIRMED));

    }

    @Test
    void givenPayment_whenCreated_thenCancelledAndSaved(){
        long paymentId = 1L;
        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.CREATED);
        payment.setCreatedAt(Instant.now());

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).then(inv -> inv.getArgument(0));

        Payment result = service.cancel(paymentId);

        assertThat(result.getStatus(), is(PaymentStatus.CANCELLED));
    }

    @Test
    void givenConfirmedPayment_whenCancelPayment_thenThrowsBadRequest_andDoesNotSave() {
        long paymentId = 2L;
        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.CONFIRMED);
        payment.setCreatedAt(Instant.now());

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> service.cancel(paymentId));
        assertTrue(ex.getMessage().contains("Cannot cancel payment"));

        //assertThrows(BadRequestException.class, () -> service.cancel(paymentId));

        verify(paymentRepository, never()).save(any(Payment.class));

    }

}
