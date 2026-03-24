package com.gray.hospital.repository;

import com.gray.hospital.entity.CabinBooking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CabinBookingRepository extends JpaRepository<CabinBooking, Long> {

    List<CabinBooking> findByCabinCabinIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long cabinId,
            LocalDate endDate,
            LocalDate startDate
    );

}