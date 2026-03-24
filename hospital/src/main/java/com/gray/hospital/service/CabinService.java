package com.gray.hospital.service;

import com.gray.hospital.entity.*;
import com.gray.hospital.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class CabinService {

    private final CabinRepository cabinRepository;
    private final CabinBookingRepository bookingRepository;
    private final PatientRepository patientRepository;

    public CabinService(
            CabinRepository cabinRepository,
            CabinBookingRepository bookingRepository,
            PatientRepository patientRepository){

        this.cabinRepository = cabinRepository;
        this.bookingRepository = bookingRepository;
        this.patientRepository = patientRepository;
    }

    public CabinBooking bookCabin(
            Long patientId,
            Long cabinId,
            LocalDate start,
            LocalDate end){

        if(!bookingRepository
                .findByCabinCabinIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        cabinId,end,start)
                .isEmpty()){

            throw new RuntimeException("Cabin already booked for those dates");
        }

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow();

        Cabin cabin = cabinRepository.findById(cabinId)
                .orElseThrow();

        CabinBooking booking = new CabinBooking();

        booking.setPatient(patient);
        booking.setCabin(cabin);
        booking.setStartDate(start);
        booking.setEndDate(end);

        return bookingRepository.save(booking);
    }
}