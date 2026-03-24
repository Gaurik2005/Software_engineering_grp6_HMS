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

    public PayrollService(
            AppointmentRepository appointmentRepository,
            RegularDoctorRepository regularDoctorRepository,
            DoctorRepository doctorRepository,
            PayslipRepository payslipRepository){

        this.appointmentRepository = appointmentRepository;
        this.regularDoctorRepository = regularDoctorRepository;
        this.doctorRepository = doctorRepository;
        this.payslipRepository = payslipRepository;
    }

    public Payslip generateDoctorPayslip(
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

        Payslip payslip = new Payslip();

        payslip.setDoctor(doctor);
        payslip.setMonth(month);
        payslip.setYear(year);
        payslip.setAmount(salary);

        return payslipRepository.save(payslip);
    }
}