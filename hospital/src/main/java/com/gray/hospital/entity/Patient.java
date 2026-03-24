package com.gray.hospital.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="patients")
@Data
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long patientId;

    private String name;

    private Integer age;

    private String address;

    private String email;

    private String phone;

    private String password;
}