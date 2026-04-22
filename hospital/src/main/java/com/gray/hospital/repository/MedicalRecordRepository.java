package com.gray.hospital.repository;

import com.gray.hospital.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    Optional<MedicalRecord> findByAppointmentAppointmentId(Long appointmentId);

    List<MedicalRecord> findByAppointmentDoctorDoctorIdOrderByUpdatedAtDesc(Long doctorId);

    List<MedicalRecord> findByAppointmentPatientPatientIdOrderByUpdatedAtDesc(Long patientId);

    boolean existsByAppointmentAppointmentId(Long appointmentId);
}
