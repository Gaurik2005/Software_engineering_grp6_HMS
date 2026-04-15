package com.gray.hospital.repository;

import com.gray.hospital.entity.NurseDuty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface NurseDutyRepository extends JpaRepository<NurseDuty, Long> {

    List<NurseDuty> findByDutyDateBetweenOrderByDutyDateAscShiftStartAsc(LocalDate startDate, LocalDate endDate);

    List<NurseDuty> findByNurseNurseIdAndDutyDateBetweenOrderByDutyDateAscShiftStartAsc(
            Long nurseId,
            LocalDate startDate,
            LocalDate endDate
    );
}
