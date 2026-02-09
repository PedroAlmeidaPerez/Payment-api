package com.pedro.paymentapi.customer;

import com.pedro.paymentapi.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
