package com.pedro.paymentapi.payment;

import com.pedro.paymentapi.payment.dto.CreatePaymentRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.pedro.paymentapi.payment.error.NotFoundException;

import java.time.Instant;

@Service
public class PaymentService {

    private final PaymentRepository repository;

    public PaymentService(PaymentRepository repository) {
        this.repository = repository;
    }

    public Payment create(CreatePaymentRequest req) {
        Payment payment = new Payment();
        payment.setAmount(req.getAmount());

        String currency = req.getCurrency();
        payment.setCurrency(currency != null ? currency.toUpperCase() : null);

        String desc = req.getDescription();
        payment.setDescription(StringUtils.hasText(desc) ? desc : null);

        payment.setStatus(PaymentStatus.CREATED);
        payment.setCreatedAt(Instant.now());

        return repository.save(payment);
    }

    public Payment getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Payment not found: " + id));
    }


    // (Opcional) Si tenías el método viejo, puedes dejarlo o borrarlo,
    // pero el controller nuevo debe llamar a create(CreatePaymentRequest).
}
