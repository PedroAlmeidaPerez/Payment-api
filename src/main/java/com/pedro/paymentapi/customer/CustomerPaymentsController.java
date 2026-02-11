package com.pedro.paymentapi.customer;
import com.pedro.paymentapi.payment.Payment;
import com.pedro.paymentapi.payment.PaymentService;
import com.pedro.paymentapi.payment.dto.CreatePaymentRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/customers/{customerId}/payments")
public class CustomerPaymentsController {

    private final PaymentService paymentService;

    public CustomerPaymentsController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Payment create(@PathVariable Long customerId, @Valid @RequestBody CreatePaymentRequest body) {
        return paymentService.createForCustomer(customerId, body.getAmount(), body.getCurrency(), body.getDescription());
    }

    @GetMapping
    public List<Payment> list(@PathVariable Long customerId) {
        return paymentService.listByCustomer(customerId);
    }
}

