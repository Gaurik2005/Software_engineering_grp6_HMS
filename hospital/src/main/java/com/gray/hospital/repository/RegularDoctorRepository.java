package com.gray.hospital.repository;

import com.gray.hospital.entity.RegularDoctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegularDoctorRepository
        extends JpaRepository<RegularDoctor,Long> {
}