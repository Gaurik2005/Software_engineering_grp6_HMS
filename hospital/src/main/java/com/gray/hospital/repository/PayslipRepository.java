package com.gray.hospital.repository;

import com.gray.hospital.entity.Payslip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PayslipRepository extends JpaRepository<Payslip,Long> {

    Optional<Payslip> findByDoctorDoctorIdAndMonthAndYear(Long doctorId, Integer month, Integer year);
}
