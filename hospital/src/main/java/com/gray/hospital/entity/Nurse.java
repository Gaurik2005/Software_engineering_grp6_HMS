package com.gray.hospital.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "nurses")
@Data
public class Nurse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="nurse_id")
    private Long nurseId;

    private String name;

    private String email;

    private String phone;

    private BigDecimal salary;

    private String role;

    @ManyToOne
    @JoinColumn(name="doctor_id")
    private Doctor doctor;
}