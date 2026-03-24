package com.gray.hospital.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="doctors")
@Data
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long doctorId;

    private String name;

    private String email;

    private String phone;

    private String qualification;

    private String expertise;

    private String doctorType;
}