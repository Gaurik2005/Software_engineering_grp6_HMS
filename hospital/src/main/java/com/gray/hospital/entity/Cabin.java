package com.gray.hospital.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name="cabins")
@Data
public class Cabin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cabinId;

    private String status;

    private BigDecimal dailyRate;
}