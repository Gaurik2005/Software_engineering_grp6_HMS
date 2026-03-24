package com.gray.hospital.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name="payslips")
@Data
public class Payslip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long payslipId;

    @ManyToOne
    @JoinColumn(name="doctor_id")
    private Doctor doctor;

    private Integer month;

    private Integer year;

    private BigDecimal amount;
}