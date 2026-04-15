package com.gray.hospital.repository;

import com.gray.hospital.entity.NursePayslip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NursePayslipRepository extends JpaRepository<NursePayslip, Long> {

    Optional<NursePayslip> findByNurseNurseIdAndMonthAndYear(Long nurseId, Integer month, Integer year);
}
