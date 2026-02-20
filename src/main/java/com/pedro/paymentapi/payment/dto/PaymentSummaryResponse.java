package com.pedro.paymentapi.payment.dto;

import com.pedro.paymentapi.payment.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public class PaymentSummaryResponse {
    private Long id;

    private String customerFullName;
    private int totalCount;
    private BigDecimal totalAmount;
    private Map<PaymentStatus, Long> byStatus;

    public PaymentSummaryResponse(Long id, String customerFullName, int totalCount, BigDecimal totalAmount, Map<PaymentStatus, Long> byStatus) {
        this.id = id;
        this.customerFullName = customerFullName;
        this.totalCount = totalCount;
        this.totalAmount = totalAmount;
        this.byStatus = byStatus;
    }

    public Long getId() {
        return id;
    }

    public String getCustomerFullName() {
        return customerFullName;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public Map<PaymentStatus, Long> getByStatus() {
        return byStatus;
    }
}
