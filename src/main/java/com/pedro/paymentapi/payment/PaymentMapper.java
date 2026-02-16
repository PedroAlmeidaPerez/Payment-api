package com.pedro.paymentapi.payment;

import com.pedro.paymentapi.payment.dto.PaymentResponse;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponse toResponse(Payment p) {
        PaymentResponse r = new PaymentResponse();
        r.setId(p.getId());
        r.setAmount(p.getAmount());
        r.setCurrency(p.getCurrency());
        r.setDescription(p.getDescription());
        r.setStatus(p.getStatus() != null ? p.getStatus().name() : null);
        r.setCreatedAt(p.getCreatedAt());
        r.setCustomerId(p.getCustomer() != null ? p.getCustomer().getId() : null);
        return r;
    }
}
