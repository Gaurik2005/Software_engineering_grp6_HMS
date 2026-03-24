package com.gray.hospital.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name="regular_doctors")
@Data
public class RegularDoctor {

    @Id
    private Long doctorId;

    private BigDecimal basicPay;

}