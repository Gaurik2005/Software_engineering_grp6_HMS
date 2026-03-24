package com.gray.hospital.repository;

import com.gray.hospital.entity.Payslip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayslipRepository extends JpaRepository<Payslip,Long> {
}