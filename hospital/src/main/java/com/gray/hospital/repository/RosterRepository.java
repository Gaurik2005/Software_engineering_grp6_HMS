package com.gray.hospital.repository;

import com.gray.hospital.entity.Roster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RosterRepository extends JpaRepository<Roster, Long> {

    List<Roster> findByDate(LocalDate date);

    Optional<Roster> findByDoctorDoctorIdAndDate(Long doctorId, LocalDate date);

}