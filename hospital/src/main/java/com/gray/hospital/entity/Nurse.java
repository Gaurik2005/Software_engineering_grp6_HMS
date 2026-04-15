package com.gray.hospital.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
    
    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private String role;

    

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="doctor_id")
    @JsonIgnore
    private Doctor doctor;
}