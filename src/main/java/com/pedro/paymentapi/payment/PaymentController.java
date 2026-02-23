package com.pedro.paymentapi.payment;

import com.pedro.paymentapi.ApiPaths;
import com.pedro.paymentapi.payment.dto.PaymentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.V + "/payments")
public class PaymentController {

    private final PaymentService service;
    private final PaymentMapper paymentMapper;

    public PaymentController(PaymentService service, PaymentMapper paymentMapper) {
        this.service = service;
        this.paymentMapper = paymentMapper;
    }

    @GetMapping("/{id}")
    public PaymentResponse get(@PathVariable Long id) {
        return paymentMapper.toResponse(service.getById(id));
    }

    @PostMapping("/{id}/confirm")
    @ResponseStatus(HttpStatus.OK)
    public PaymentResponse confirm(@PathVariable Long id) {
        return paymentMapper.toResponse(service.confirm(id));
    }

    @PostMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public PaymentResponse cancel(@PathVariable Long id) {
        return paymentMapper.toResponse(service.cancel(id));
    }

}
