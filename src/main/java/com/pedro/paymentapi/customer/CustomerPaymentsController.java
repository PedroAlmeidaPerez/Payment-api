package com.pedro.paymentapi.customer;
import com.pedro.paymentapi.payment.Payment;
import com.pedro.paymentapi.payment.PaymentMapper;
import com.pedro.paymentapi.payment.PaymentService;
import com.pedro.paymentapi.payment.dto.CreatePaymentRequest;
import com.pedro.paymentapi.payment.dto.PaymentResponse;
import com.pedro.paymentapi.payment.dto.PaymentSummaryResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/customers/{customerId}/payments")
public class CustomerPaymentsController {

    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;

    public CustomerPaymentsController(PaymentService paymentService, PaymentMapper paymentMapper) {
        this.paymentService = paymentService;
        this.paymentMapper = paymentMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse create(@PathVariable Long customerId, @Valid @RequestBody CreatePaymentRequest body) {
        Payment p = paymentService.createForCustomer(customerId, body.getAmount(), body.getCurrency(), body.getDescription());
        return paymentMapper.toResponse(p);
    }

    @GetMapping
    public List<PaymentResponse> list(@PathVariable Long customerId) {
        return paymentService.listByCustomer(customerId).stream()
                .map(paymentMapper::toResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    @RequestMapping("/summary")
    public PaymentSummaryResponse summary(@PathVariable Long customerId) {
        return paymentService.getSummaryByCustomer(customerId);
    }
}

