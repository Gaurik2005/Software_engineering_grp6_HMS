package com.gray.hospital.service;

import com.gray.hospital.entity.*;
import com.gray.hospital.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CabinService {

    private final CabinRepository cabinRepository;
    private final CabinBookingRepository bookingRepository;
    private final PatientRepository patientRepository;
    private final PaymentRepository paymentRepository;

    public CabinService(
            CabinRepository cabinRepository,
            CabinBookingRepository bookingRepository,
            PatientRepository patientRepository,
            PaymentRepository paymentRepository){

        this.cabinRepository = cabinRepository;
        this.bookingRepository = bookingRepository;
        this.patientRepository = patientRepository;
        this.paymentRepository = paymentRepository;
    }

    public CabinBooking bookCabin(
            Long patientId,
            Long cabinId,
            LocalDate start,
            LocalDate end){
        validateDateRange(start, end);

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

        CabinBooking savedBooking = bookingRepository.save(booking);

        Payment payment = new Payment();
        payment.setPatientId(patientId);
        payment.setReferenceType("CABIN");
        payment.setReferenceId(savedBooking.getBookingId());
        payment.setAmount(calculateBookingAmount(cabin, start, end));
        payment.setStatus("PAID");
        payment.setDescription("Cabin booking payment");
        payment.setPaidAt(LocalDateTime.now());
        paymentRepository.save(payment);

        return savedBooking;
    }

    public List<Cabin> getAvailableCabins(LocalDate start, LocalDate end){
        validateDateRange(start, end);

        return cabinRepository.findAll().stream()
                .filter(cabin -> bookingRepository
                        .findByCabinCabinIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                                cabin.getCabinId(),
                                end,
                                start
                        ).isEmpty())
                .collect(Collectors.toList());
    }

    public BigDecimal calculateAmount(Long cabinId, LocalDate start, LocalDate end){
        Cabin cabin = cabinRepository.findById(cabinId)
                .orElseThrow();

        validateDateRange(start, end);
        return calculateBookingAmount(cabin, start, end);
    }

    private BigDecimal calculateBookingAmount(Cabin cabin, LocalDate start, LocalDate end){
        long bookedDays = ChronoUnit.DAYS.between(start, end) + 1;
        BigDecimal dailyRate = cabin.getDailyRate() != null
                ? cabin.getDailyRate()
                : BigDecimal.valueOf(3000);

        return dailyRate.multiply(BigDecimal.valueOf(bookedDays));
    }

    private void validateDateRange(LocalDate start, LocalDate end){
        if (end.isBefore(start)) {
            throw new RuntimeException("End date cannot be before start date");
        }
    }
}
