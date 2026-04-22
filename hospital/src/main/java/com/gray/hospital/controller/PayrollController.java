package com.gray.hospital.controller;

import com.gray.hospital.controller.dto.PayslipEmailResponse;
import com.gray.hospital.controller.dto.PayslipGenerationResponse;
import com.gray.hospital.service.PayslipDeliveryService;
import com.gray.hospital.service.PayslipDocumentService;
import com.gray.hospital.service.PayrollService;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/payroll")
public class PayrollController {

    private final PayrollService payrollService;
    private final PayslipDocumentService payslipDocumentService;
    private final PayslipDeliveryService payslipDeliveryService;

    public PayrollController(PayrollService payrollService,
                             PayslipDocumentService payslipDocumentService,
                             PayslipDeliveryService payslipDeliveryService){
        this.payrollService = payrollService;
        this.payslipDocumentService = payslipDocumentService;
        this.payslipDeliveryService = payslipDeliveryService;
    }

    @PostMapping("/doctor")
    public PayslipGenerationResponse generateDoctorPayslip(
            @RequestParam Long doctorId,
            @RequestParam int month,
            @RequestParam int year){

        return payrollService.generateDoctorPayslip(
                doctorId,month,year);
    }

    @PostMapping("/nurse")
    public PayslipGenerationResponse generateNursePayslip(
            @RequestParam Long nurseId,
            @RequestParam int month,
            @RequestParam int year){
        return payrollService.generateNursePayslip(nurseId, month, year);
    }

    @GetMapping("/doctor/document")
    public ResponseEntity<Resource> getDoctorPayslipDocument(
            @RequestParam Long doctorId,
            @RequestParam int month,
            @RequestParam int year) {
        return serveDocument(payslipDocumentService.resolveDoctorPayslipPath(doctorId, year, month), false);
    }

    @GetMapping("/nurse/document")
    public ResponseEntity<Resource> getNursePayslipDocument(
            @RequestParam Long nurseId,
            @RequestParam int month,
            @RequestParam int year) {
        return serveDocument(payslipDocumentService.resolveNursePayslipPath(nurseId, year, month), false);
    }

    @GetMapping("/doctor/download")
    public ResponseEntity<Resource> downloadDoctorPayslip(
            @RequestParam Long doctorId,
            @RequestParam int month,
            @RequestParam int year) {
        return serveDocument(payslipDocumentService.resolveDoctorPayslipPath(doctorId, year, month), true);
    }

    @GetMapping("/nurse/download")
    public ResponseEntity<Resource> downloadNursePayslip(
            @RequestParam Long nurseId,
            @RequestParam int month,
            @RequestParam int year) {
        return serveDocument(payslipDocumentService.resolveNursePayslipPath(nurseId, year, month), true);
    }

    @PostMapping("/doctor/email")
    public PayslipEmailResponse emailDoctorPayslip(
            @RequestParam Long doctorId,
            @RequestParam int month,
            @RequestParam int year) {
        return payslipDeliveryService.emailDoctorPayslip(doctorId, month, year);
    }

    @PostMapping("/nurse/email")
    public PayslipEmailResponse emailNursePayslip(
            @RequestParam Long nurseId,
            @RequestParam int month,
            @RequestParam int year) {
        return payslipDeliveryService.emailNursePayslip(nurseId, month, year);
    }

    private ResponseEntity<Resource> serveDocument(java.nio.file.Path filePath, boolean attachment) {
        FileSystemResource resource = new FileSystemResource(filePath);
        if (!resource.exists()) {
            throw new RuntimeException("Payslip document not found");
        }

        String dispositionType = attachment ? "attachment" : "inline";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, dispositionType + "; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.TEXT_HTML)
                .body(resource);
    }
}
