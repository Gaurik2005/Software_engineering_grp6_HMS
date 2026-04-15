package com.gray.hospital.controller;

import com.gray.hospital.entity.NursePayslip;
import com.gray.hospital.entity.Payslip;
import com.gray.hospital.service.PayrollService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payroll")
public class PayrollController {

    private final PayrollService payrollService;

    public PayrollController(PayrollService payrollService){
        this.payrollService = payrollService;
    }

    @PostMapping("/doctor")
    public Payslip generateDoctorPayslip(
            @RequestParam Long doctorId,
            @RequestParam int month,
            @RequestParam int year){

        return payrollService.generateDoctorPayslip(
                doctorId,month,year);
    }

    @PostMapping("/nurse")
    public NursePayslip generateNursePayslip(
            @RequestParam Long nurseId,
            @RequestParam int month,
            @RequestParam int year){
        return payrollService.generateNursePayslip(nurseId, month, year);
    }
}
