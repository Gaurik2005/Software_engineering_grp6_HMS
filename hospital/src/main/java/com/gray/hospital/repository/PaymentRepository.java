package com.gray.hospital.repository;

import com.gray.hospital.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("""
            select coalesce(sum(p.amount), 0)
            from Payment p
            where p.status = 'PAID'
            """)
    BigDecimal sumPaidAmount();

    @Query("""
            select coalesce(sum(p.amount), 0)
            from Payment p
            where p.status = 'PAID' and p.paidAt between :start and :end
            """)
    BigDecimal sumPaidAmountBetween(LocalDateTime start, LocalDateTime end);
}
