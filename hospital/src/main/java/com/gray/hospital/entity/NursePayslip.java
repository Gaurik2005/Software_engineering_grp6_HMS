package com.gray.hospital.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "nurse_payslips")
@Data
public class NursePayslip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long nursePayslipId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nurse_id")
    private Nurse nurse;

    private Integer month;

    private Integer year;

    private BigDecimal amount;

    private LocalDateTime generatedAt;
}
