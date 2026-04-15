package com.gray.hospital.service;

import com.gray.hospital.entity.*;
import com.gray.hospital.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PayrollService {

    private final AppointmentRepository appointmentRepository;
    private final RegularDoctorRepository regularDoctorRepository;
    private final DoctorRepository doctorRepository;
    private final PayslipRepository payslipRepository;
    private final NurseRepository nurseRepository;
    private final NursePayslipRepository nursePayslipRepository;

    public PayrollService(
            AppointmentRepository appointmentRepository,
            RegularDoctorRepository regularDoctorRepository,
            DoctorRepository doctorRepository,
            PayslipRepository payslipRepository,
            NurseRepository nurseRepository,
            NursePayslipRepository nursePayslipRepository){

        this.appointmentRepository = appointmentRepository;
        this.regularDoctorRepository = regularDoctorRepository;
        this.doctorRepository = doctorRepository;
        this.payslipRepository = payslipRepository;
        this.nurseRepository = nurseRepository;
        this.nursePayslipRepository = nursePayslipRepository;
    }

    public Payslip generateDoctorPayslip(
            Long doctorId,
            int month,
            int year){
        payslipRepository.findByDoctorDoctorIdAndMonthAndYear(doctorId, month, year)
                .ifPresent(existing -> {
                    throw new RuntimeException("Doctor payslip already generated for this month");
                });

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

        Payslip payslip = new Payslip();

        payslip.setDoctor(doctor);
        payslip.setMonth(month);
        payslip.setYear(year);
        payslip.setAmount(salary);

        return payslipRepository.save(payslip);
    }

    public NursePayslip generateNursePayslip(Long nurseId, int month, int year){
        nursePayslipRepository.findByNurseNurseIdAndMonthAndYear(nurseId, month, year)
                .ifPresent(existing -> {
                    throw new RuntimeException("Nurse payslip already generated for this month");
                });

        Nurse nurse = nurseRepository.findById(nurseId)
                .orElseThrow(() -> new RuntimeException("Nurse not found"));

        if (nurse.getSalary() == null) {
            throw new RuntimeException("Nurse salary is not set");
        }

        NursePayslip nursePayslip = new NursePayslip();
        nursePayslip.setNurse(nurse);
        nursePayslip.setMonth(month);
        nursePayslip.setYear(year);
        nursePayslip.setAmount(nurse.getSalary());
        nursePayslip.setGeneratedAt(LocalDateTime.now());

        return nursePayslipRepository.save(nursePayslip);
    }
}
