package com.gray.hospital.repository;

import com.gray.hospital.entity.CabinBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface CabinBookingRepository extends JpaRepository<CabinBooking, Long> {

    List<CabinBooking> findByCabinCabinIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long cabinId,
            LocalDate endDate,
            LocalDate startDate
    );

    long countByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate endDate, LocalDate startDate);

    @Query("""
            select distinct cb.patient.patientId
            from CabinBooking cb
            where cb.startDate <= :endDate and cb.endDate >= :startDate
            """)
    List<Long> findDistinctPatientIdsByDateOverlap(LocalDate startDate, LocalDate endDate);

}
