package com.gray.hospital.repository;

import com.gray.hospital.entity.Nurse;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NurseRepository extends JpaRepository<Nurse, Long> {

    Optional<Nurse> findByEmail(String email);

    Optional<Nurse> findByDoctorDoctorId(Long doctorId);
}
