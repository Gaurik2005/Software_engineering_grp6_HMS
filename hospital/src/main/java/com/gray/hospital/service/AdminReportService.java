package com.gray.hospital.service;

import com.gray.hospital.entity.NursePayslip;
import com.gray.hospital.entity.Payslip;
import com.gray.hospital.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class AdminReportService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final CabinBookingRepository cabinBookingRepository;
    private final PayslipRepository payslipRepository;
    private final PaymentRepository paymentRepository;
    private final NursePayslipRepository nursePayslipRepository;

    public AdminReportService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            CabinBookingRepository cabinBookingRepository,
            PayslipRepository payslipRepository,
            PaymentRepository paymentRepository,
            NursePayslipRepository nursePayslipRepository){

        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.cabinBookingRepository = cabinBookingRepository;
        this.payslipRepository = payslipRepository;
        this.paymentRepository = paymentRepository;
        this.nursePayslipRepository = nursePayslipRepository;
    }

    public Map<String,Object> generateReport(){
        Map<String, Object> report = new HashMap<>();
        report.put("registeredPatients", patientRepository.count());
        report.put("totalAppointments", appointmentRepository.count());
        report.put("totalCabinBookings", cabinBookingRepository.count());
        report.put("totalIncome", paymentRepository.sumPaidAmount());
        report.put("totalSalaryOutgo", getAllDoctorPayslipTotal().add(getAllNursePayslipTotal()));
        report.put("totalPatientsVisited", buildVisitedPatientSet(null, null).size());
        return report;
    }

    public Map<String, Object> generateReport(LocalDate startDate, LocalDate endDate){
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay().minusNanos(1);

        Map<String, Object> report = new HashMap<>();
        report.put("startDate", startDate);
        report.put("endDate", endDate);
        report.put("totalAppointments", appointmentRepository.countBySlotStartBetween(startDateTime, endDateTime));
        report.put("totalCabinBookings",
                cabinBookingRepository.countByStartDateLessThanEqualAndEndDateGreaterThanEqual(endDate, startDate));
        report.put("totalIncome", paymentRepository.sumPaidAmountBetween(startDateTime, endDateTime));
        report.put("totalSalaryOutgo", getDoctorPayslipTotalBetween(startDate, endDate)
                .add(getNursePayslipTotalBetween(startDate, endDate)));
        report.put("totalPatientsVisited", buildVisitedPatientSet(startDate, endDate).size());
        return report;
    }

    private Set<Long> buildVisitedPatientSet(LocalDate startDate, LocalDate endDate){
        Set<Long> patientIds = new HashSet<>();

        if (startDate == null || endDate == null) {
            appointmentRepository.findAll().forEach(appointment ->
                    patientIds.add(appointment.getPatient().getPatientId()));
            cabinBookingRepository.findAll().forEach(booking ->
                    patientIds.add(booking.getPatient().getPatientId()));
            return patientIds;
        }

        patientIds.addAll(appointmentRepository.findDistinctPatientIdsBySlotStartBetween(
                startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay().minusNanos(1)
        ));
        patientIds.addAll(cabinBookingRepository.findDistinctPatientIdsByDateOverlap(startDate, endDate));

        return patientIds;
    }

    private BigDecimal getAllDoctorPayslipTotal(){
        return payslipRepository.findAll().stream()
                .map(Payslip::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getAllNursePayslipTotal(){
        return nursePayslipRepository.findAll().stream()
                .map(NursePayslip::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getDoctorPayslipTotalBetween(LocalDate startDate, LocalDate endDate){
        YearMonth startMonth = YearMonth.from(startDate);
        YearMonth endMonth = YearMonth.from(endDate);

        return payslipRepository.findAll().stream()
                .filter(payslip -> isWithinMonthRange(payslip.getYear(), payslip.getMonth(), startMonth, endMonth))
                .map(Payslip::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getNursePayslipTotalBetween(LocalDate startDate, LocalDate endDate){
        YearMonth startMonth = YearMonth.from(startDate);
        YearMonth endMonth = YearMonth.from(endDate);

        return nursePayslipRepository.findAll().stream()
                .filter(payslip -> isWithinMonthRange(payslip.getYear(), payslip.getMonth(), startMonth, endMonth))
                .map(NursePayslip::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private boolean isWithinMonthRange(Integer year, Integer month, YearMonth startMonth, YearMonth endMonth){
        YearMonth candidate = YearMonth.of(year, month);
        return !candidate.isBefore(startMonth) && !candidate.isAfter(endMonth);
    }
}
