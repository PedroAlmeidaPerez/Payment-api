package com.pedro.paymentapi.payment;

import com.pedro.paymentapi.payment.dto.CreatePaymentRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public Payment get(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping("/{id}/confirm")
    @ResponseStatus(HttpStatus.OK)
    public Payment confirm(@PathVariable Long id) {
        return service.confirm(id);
    }

    @PostMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public Payment cancel(@PathVariable Long id) {
        return service.cancel(id);
    }

}
