package com.pedro.paymentapi.payment;

import com.pedro.paymentapi.customer.Customer;
import com.pedro.paymentapi.customer.CustomerService;
import com.pedro.paymentapi.error.NotFoundException;
import com.pedro.paymentapi.payment.dto.CreatePaymentRequest;
import com.pedro.paymentapi.error.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CustomerService customerService;

    public PaymentService(PaymentRepository paymentRepository, CustomerService customerService) {
        this.paymentRepository = paymentRepository;
        this.customerService = customerService;
    }


    @Transactional(readOnly = true)
    public Payment getById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Payment not found: " + id));
    }

    @Transactional
    public Payment createForCustomer(Long customerId,
                                     BigDecimal amount,
                                     String currency,
                                     String description) {

        // ✅ Reutiliza lógica + 404 del CustomerService
        Customer customer = customerService.getById(customerId);

        Payment p = new Payment();
        p.setAmount(amount);
        p.setCurrency(currency != null ? currency.toUpperCase() : null);
        p.setDescription(StringUtils.hasText(description) ? description : null);
        p.setStatus(PaymentStatus.CREATED);
        p.setCreatedAt(Instant.now());
        p.setCustomer(customer);

        return paymentRepository.save(p);
    }

    @Transactional(readOnly = true)
    public List<Payment> listByCustomer(Long customerId) {
        // ✅ Si el customer no existe, devolvemos 404 (no [])
        customerService.getById(customerId);
        return paymentRepository.findByCustomerId(customerId);
    }

    private Payment findPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Payment not found: " + paymentId));
    }

    @Transactional
    public Payment confirm (Long paymentId) {
        Payment payment = findPaymentById(paymentId);
        if (payment.getStatus() != PaymentStatus.CREATED) {
            throw new BadRequestException("Cannot confirm payment in status: " + payment.getStatus());
        }
        payment.setStatus(PaymentStatus.CONFIRMED);

        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment cancel (Long paymentId) {
        Payment payment = findPaymentById(paymentId);
        if (payment.getStatus() != PaymentStatus.CREATED) {

                throw new BadRequestException("Cannot cancel payment in status: " + payment.getStatus());

        }
        payment.setStatus(PaymentStatus.CANCELLED);

        return paymentRepository.save(payment);

    }
}
