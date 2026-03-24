package com.gray.hospital.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name="cabin_bookings")
@Data
public class CabinBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @ManyToOne
    @JoinColumn(name="patient_id")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name="cabin_id")
    private Cabin cabin;

    private LocalDate startDate;

    private LocalDate endDate;
}