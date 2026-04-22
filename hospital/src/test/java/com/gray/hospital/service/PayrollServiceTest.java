package com.gray.hospital.service;

import com.gray.hospital.controller.dto.PayslipGenerationResponse;
import com.gray.hospital.entity.Doctor;
import com.gray.hospital.entity.Nurse;
import com.gray.hospital.entity.NursePayslip;
import com.gray.hospital.entity.RegularDoctor;
import com.gray.hospital.repository.AppointmentRepository;
import com.gray.hospital.repository.DoctorRepository;
import com.gray.hospital.repository.NursePayslipRepository;
import com.gray.hospital.repository.NurseRepository;
import com.gray.hospital.repository.PayslipRepository;
import com.gray.hospital.repository.RegularDoctorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PayrollServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private RegularDoctorRepository regularDoctorRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PayslipRepository payslipRepository;

    @Mock
    private NurseRepository nurseRepository;

    @Mock
    private NursePayslipRepository nursePayslipRepository;

    @Mock
    private PayslipDocumentService payslipDocumentService;

    @InjectMocks
    private PayrollService payrollService;

    @Test
    void generateDoctorPayslipUpdatesExistingMonthlyPayslipInsteadOfFailing() {
        Doctor doctor = new Doctor();
        doctor.setDoctorId(1L);
        doctor.setDoctorType("REGULAR");

        RegularDoctor regularDoctor = new RegularDoctor();
        regularDoctor.setDoctorId(1L);
        regularDoctor.setBasicPay(new BigDecimal("60000.00"));

        com.gray.hospital.entity.Payslip existingPayslip = new com.gray.hospital.entity.Payslip();
        existingPayslip.setPayslipId(99L);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(regularDoctorRepository.findById(1L)).thenReturn(Optional.of(regularDoctor));
        when(appointmentRepository.countByDoctorDoctorIdAndSlotStartBetween(any(), any(), any())).thenReturn(5L);
        when(payslipRepository.findByDoctorDoctorIdAndMonthAndYear(1L, 4, 2026)).thenReturn(Optional.of(existingPayslip));
        when(payslipRepository.save(any(com.gray.hospital.entity.Payslip.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(payslipDocumentService.generateDoctorPayslip(anyLong(), any(), anyInt(), anyInt(), any(), anyLong(), any(), any()))
                .thenReturn(Path.of("target/payslips/doctor-1-2026-4.html"));

        PayslipGenerationResponse payslip = payrollService.generateDoctorPayslip(1L, 4, 2026);

        assertEquals(99L, payslip.payslipId());
        assertEquals(new BigDecimal("60500.00"), payslip.amount());
        assertEquals(4, payslip.month());
        assertEquals(2026, payslip.year());
        assertEquals("/payroll/doctor/document?doctorId=1&month=4&year=2026", payslip.documentUrl());
    }

    @Test
    void generateNursePayslipUpdatesExistingMonthlyPayslipInsteadOfFailing() {
        Nurse nurse = new Nurse();
        nurse.setNurseId(2L);
        nurse.setSalary(new BigDecimal("25000.00"));

        NursePayslip existingPayslip = new NursePayslip();
        existingPayslip.setNursePayslipId(77L);

        when(nurseRepository.findById(2L)).thenReturn(Optional.of(nurse));
        when(nursePayslipRepository.findByNurseNurseIdAndMonthAndYear(2L, 4, 2026))
                .thenReturn(Optional.of(existingPayslip));
        when(nursePayslipRepository.save(any(NursePayslip.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(payslipDocumentService.generateNursePayslip(anyLong(), any(), anyInt(), anyInt(), any()))
                .thenReturn(Path.of("target/payslips/nurse-2-2026-4.html"));

        PayslipGenerationResponse payslip = payrollService.generateNursePayslip(2L, 4, 2026);

        assertEquals(77L, payslip.payslipId());
        assertEquals(new BigDecimal("25000.00"), payslip.amount());
        assertEquals(4, payslip.month());
        assertEquals(2026, payslip.year());
        assertEquals("/payroll/nurse/document?nurseId=2&month=4&year=2026", payslip.documentUrl());
    }
}
