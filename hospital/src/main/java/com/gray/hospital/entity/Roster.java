package com.gray.hospital.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name="roster")
@Data
public class Roster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rosterId;

    @ManyToOne
    @JoinColumn(name="doctor_id")
    private Doctor doctor;

    private Long roomId;

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;
}