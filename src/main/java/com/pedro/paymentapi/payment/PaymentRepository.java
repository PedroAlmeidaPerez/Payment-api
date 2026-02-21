package com.pedro.paymentapi.payment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Page<Payment> findByCustomerId(Long customerId, Pageable pageable);
    List<Payment> findByCustomerId(Long customerId);
    Page<Payment> findByCustomerIdAndStatus(Long customerId, PaymentStatus status, Pageable pageable);
}
