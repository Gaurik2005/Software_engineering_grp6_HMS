package com.gray.hospital.service;

import com.gray.hospital.controller.dto.PayslipGenerationResponse;
import com.gray.hospital.entity.*;
import com.gray.hospital.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.nio.file.Path;

@Service
public class PayrollService {

    private final AppointmentRepository appointmentRepository;
    private final RegularDoctorRepository regularDoctorRepository;
    private final DoctorRepository doctorRepository;
    private final PayslipRepository payslipRepository;
    private final NurseRepository nurseRepository;
    private final NursePayslipRepository nursePayslipRepository;
    private final PayslipDocumentService payslipDocumentService;

    public PayrollService(
            AppointmentRepository appointmentRepository,
            RegularDoctorRepository regularDoctorRepository,
            DoctorRepository doctorRepository,
            PayslipRepository payslipRepository,
            NurseRepository nurseRepository,
            NursePayslipRepository nursePayslipRepository,
            PayslipDocumentService payslipDocumentService){

        this.appointmentRepository = appointmentRepository;
        this.regularDoctorRepository = regularDoctorRepository;
        this.doctorRepository = doctorRepository;
        this.payslipRepository = payslipRepository;
        this.nurseRepository = nurseRepository;
        this.nursePayslipRepository = nursePayslipRepository;
        this.payslipDocumentService = payslipDocumentService;
    }

    public PayslipGenerationResponse generateDoctorPayslip(
            Long doctorId,
            int month,
            int year){
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow();

        LocalDateTime start =
                LocalDateTime.of(year,month,1,0,0);

        LocalDateTime end =
                start.plusMonths(1);

        long patientsSeen =
                appointmentRepository
                        .countByDoctorDoctorIdAndSlotStartBetween(
                                doctorId,start,end);

        BigDecimal salary;

        if("REGULAR".equals(doctor.getDoctorType())){

            BigDecimal basicPay =
                    regularDoctorRepository
                            .findById(doctorId)
                            .orElseThrow()
                            .getBasicPay();

            salary = basicPay.add(
                    BigDecimal.valueOf(patientsSeen * 100));

        }else{

            salary = BigDecimal.valueOf(patientsSeen * 200);
        }

        Payslip payslip = payslipRepository.findByDoctorDoctorIdAndMonthAndYear(doctorId, month, year)
                .orElseGet(Payslip::new);

        payslip.setDoctor(doctor);
        payslip.setMonth(month);
        payslip.setYear(year);
        payslip.setAmount(salary);

        Payslip savedPayslip = payslipRepository.save(payslip);
        BigDecimal basicPay = "REGULAR".equals(doctor.getDoctorType())
                ? regularDoctorRepository.findById(doctorId).orElseThrow().getBasicPay()
                : BigDecimal.ZERO;
        BigDecimal variablePay = salary.subtract(basicPay);
        Path documentPath = payslipDocumentService.generateDoctorPayslip(
                savedPayslip.getPayslipId(),
                doctor,
                month,
                year,
                basicPay,
                patientsSeen,
                variablePay,
                salary
        );

        return new PayslipGenerationResponse(
                savedPayslip.getPayslipId(),
                "DOCTOR",
                doctor.getDoctorId(),
                doctor.getName(),
                month,
                year,
                salary,
                documentPath.toString(),
                "/payroll/doctor/document?doctorId=" + doctorId + "&month=" + month + "&year=" + year
        );
    }

    public PayslipGenerationResponse generateNursePayslip(Long nurseId, int month, int year){
        Nurse nurse = nurseRepository.findById(nurseId)
                .orElseThrow(() -> new RuntimeException("Nurse not found"));

        if (nurse.getSalary() == null) {
            throw new RuntimeException("Nurse salary is not set");
        }

        NursePayslip nursePayslip = nursePayslipRepository.findByNurseNurseIdAndMonthAndYear(nurseId, month, year)
                .orElseGet(NursePayslip::new);
        nursePayslip.setNurse(nurse);
        nursePayslip.setMonth(month);
        nursePayslip.setYear(year);
        nursePayslip.setAmount(nurse.getSalary());
        nursePayslip.setGeneratedAt(LocalDateTime.now());

        NursePayslip savedPayslip = nursePayslipRepository.save(nursePayslip);
        Path documentPath = payslipDocumentService.generateNursePayslip(
                savedPayslip.getNursePayslipId(),
                nurse,
                month,
                year,
                nurse.getSalary()
        );

        return new PayslipGenerationResponse(
                savedPayslip.getNursePayslipId(),
                "NURSE",
                nurse.getNurseId(),
                nurse.getName(),
                month,
                year,
                nurse.getSalary(),
                documentPath.toString(),
                "/payroll/nurse/document?nurseId=" + nurseId + "&month=" + month + "&year=" + year
        );
    }
}
