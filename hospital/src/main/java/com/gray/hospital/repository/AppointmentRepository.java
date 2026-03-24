package com.gray.hospital.repository;

import com.gray.hospital.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByDoctorDoctorIdAndSlotStart(Long doctorId, LocalDateTime slotStart);
    long countByDoctorDoctorIdAndSlotStartBetween(
        Long doctorId,
        LocalDateTime start,
        LocalDateTime end
);
}