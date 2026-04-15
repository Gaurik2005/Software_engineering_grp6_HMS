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

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "nurse_duties")
@Data
public class NurseDuty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long nurseDutyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nurse_id")
    private Nurse nurse;

    private Long assignedByNurseId;

    private LocalDate dutyDate;

    private LocalTime shiftStart;

    private LocalTime shiftEnd;

    private String wardOrRoom;

    private String notes;
}
