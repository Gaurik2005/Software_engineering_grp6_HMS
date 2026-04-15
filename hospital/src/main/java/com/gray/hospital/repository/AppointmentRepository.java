package com.gray.hospital.repository;

import com.gray.hospital.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByDoctorDoctorIdAndSlotStart(Long doctorId, LocalDateTime slotStart);

    long countByDoctorDoctorIdAndSlotStartBetween(
            Long doctorId,
            LocalDateTime start,
            LocalDateTime end
    );

    List<Appointment> findByDoctorDoctorIdAndSlotStartBetweenOrderBySlotStart(
            Long doctorId,
            LocalDateTime start,
            LocalDateTime end
    );

    long countBySlotStartBetween(LocalDateTime start, LocalDateTime end);

    @Query("""
            select distinct a.patient.patientId
            from Appointment a
            where a.slotStart between :start and :end
            """)
    List<Long> findDistinctPatientIdsBySlotStartBetween(LocalDateTime start, LocalDateTime end);

    List<Appointment> findByPatientPatientId(Long patientId);
}
