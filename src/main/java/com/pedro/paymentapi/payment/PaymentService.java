package com.pedro.paymentapi.payment;

import com.pedro.paymentapi.customer.Customer;
import com.pedro.paymentapi.customer.CustomerService;
import com.pedro.paymentapi.error.NotFoundException;
import com.pedro.paymentapi.payment.dto.CreatePaymentRequest;
import com.pedro.paymentapi.error.BadRequestException;
import com.pedro.paymentapi.payment.dto.PaymentSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

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
    public Page<Payment> listByCustomer(Long customerId, PaymentStatus status, Pageable pageable) {
        // ✅ Si el customer no existe, devolvemos 404 (no [])
        customerService.getById(customerId);
        if (status == null) {
            return paymentRepository.findByCustomerId(customerId, pageable);
        }
        return paymentRepository.findByCustomerIdAndStatus(customerId, status, pageable);
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


    public PaymentSummaryResponse getSummaryByCustomer (Long customerId) {
        Customer c = customerService.getById(customerId); // 404 si no existe
        List<Payment> listPayments = paymentRepository.findByCustomerId(customerId);

        int totalCount = listPayments.size();

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Payment p: listPayments) {
            if (p.getAmount() != null) {
                totalAmount = totalAmount.add(p.getAmount());
            }
        }

        Map<PaymentStatus, Long> byStatus = new EnumMap<>(PaymentStatus.class);
        for (Payment p : listPayments) {
            if (p.getStatus() != null) {
                byStatus.put(p.getStatus(), byStatus.getOrDefault(p.getStatus(), 0L) + 1L);
            }
        }

        //String customer = customerService.getById(customerId).getFullName();

        return new PaymentSummaryResponse(customerId, c.getFullName(), totalCount, totalAmount, byStatus);
    }
}
