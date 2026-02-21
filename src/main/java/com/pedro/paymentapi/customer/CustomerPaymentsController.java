package com.pedro.paymentapi.customer;
import com.pedro.paymentapi.payment.Payment;
import com.pedro.paymentapi.payment.PaymentMapper;
import com.pedro.paymentapi.payment.PaymentService;
import com.pedro.paymentapi.payment.PaymentStatus;
import com.pedro.paymentapi.payment.dto.CreatePaymentRequest;
import com.pedro.paymentapi.payment.dto.PaymentResponse;
import com.pedro.paymentapi.payment.dto.PaymentSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    public Page<PaymentResponse> list(
            @PathVariable Long customerId,
            @RequestParam(required = false) PaymentStatus status,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
    ) {
        return paymentService.listByCustomer(customerId, status, pageable)
                .map(paymentMapper::toResponse);
    }

    @RequestMapping("/summary")
    public PaymentSummaryResponse summary(@PathVariable Long customerId) {
        return paymentService.getSummaryByCustomer(customerId);
    }
}

