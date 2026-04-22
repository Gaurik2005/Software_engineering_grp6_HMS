package com.gray.hospital.service;

import com.gray.hospital.entity.Doctor;
import com.gray.hospital.entity.Nurse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Service
public class PayslipDocumentService {

    public Path generateDoctorPayslip(
            Long payslipId,
            Doctor doctor,
            int month,
            int year,
            BigDecimal basicPay,
            long patientsSeen,
            BigDecimal variablePay,
            BigDecimal totalAmount
    ) {
        String filename = "doctor-" + doctor.getDoctorId() + "-" + year + "-" + month + ".html";
        String title = "Doctor Payslip";
        String body = """
                <div class="meta-grid">
                    <div><span>Doctor</span><strong>%s</strong></div>
                    <div><span>Doctor ID</span><strong>%d</strong></div>
                    <div><span>Specialization</span><strong>%s</strong></div>
                    <div><span>Qualification</span><strong>%s</strong></div>
                    <div><span>Month / Year</span><strong>%d / %d</strong></div>
                    <div><span>Payslip ID</span><strong>%d</strong></div>
                </div>
                <table>
                    <tr><th>Component</th><th>Value</th></tr>
                    <tr><td>Basic Pay</td><td>Rs. %s</td></tr>
                    <tr><td>Patients Seen</td><td>%d</td></tr>
                    <tr><td>Variable Pay</td><td>Rs. %s</td></tr>
                    <tr class="total"><td>Total Amount</td><td>Rs. %s</td></tr>
                </table>
                """.formatted(
                safe(doctor.getName()),
                doctor.getDoctorId(),
                safe(doctor.getExpertise()),
                safe(doctor.getQualification()),
                month,
                year,
                payslipId,
                money(basicPay),
                patientsSeen,
                money(variablePay),
                money(totalAmount)
        );

        return writeHtml(filename, title, body);
    }

    public Path generateNursePayslip(
            Long payslipId,
            Nurse nurse,
            int month,
            int year,
            BigDecimal salary
    ) {
        String filename = "nurse-" + nurse.getNurseId() + "-" + year + "-" + month + ".html";
        String title = "Nurse Payslip";
        String body = """
                <div class="meta-grid">
                    <div><span>Nurse</span><strong>%s</strong></div>
                    <div><span>Nurse ID</span><strong>%d</strong></div>
                    <div><span>Role</span><strong>%s</strong></div>
                    <div><span>Assigned Doctor</span><strong>%s</strong></div>
                    <div><span>Month / Year</span><strong>%d / %d</strong></div>
                    <div><span>Payslip ID</span><strong>%d</strong></div>
                </div>
                <table>
                    <tr><th>Component</th><th>Value</th></tr>
                    <tr><td>Fixed Salary</td><td>Rs. %s</td></tr>
                    <tr class="total"><td>Total Amount</td><td>Rs. %s</td></tr>
                </table>
                """.formatted(
                safe(nurse.getName()),
                nurse.getNurseId(),
                safe(nurse.getRole()),
                nurse.getDoctor() != null ? safe(nurse.getDoctor().getName()) : "Not Assigned",
                month,
                year,
                payslipId,
                money(salary),
                money(salary)
        );

        return writeHtml(filename, title, body);
    }

    public Path resolveDoctorPayslipPath(Long doctorId, int year, int month) {
        return getBaseDirectory().resolve("doctor-" + doctorId + "-" + year + "-" + month + ".html");
    }

    public Path resolveNursePayslipPath(Long nurseId, int year, int month) {
        return getBaseDirectory().resolve("nurse-" + nurseId + "-" + year + "-" + month + ".html");
    }

    private Path writeHtml(String filename, String title, String body) {
        try {
            Path file = getBaseDirectory().resolve(filename);
            Files.createDirectories(file.getParent());
            Files.writeString(file, wrapHtml(title, body), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return file;
        } catch (IOException exception) {
            throw new RuntimeException("Failed to generate payslip document", exception);
        }
    }

    private Path getBaseDirectory() {
        return Path.of("target", "payslips");
    }

    private String wrapHtml(String title, String body) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>%s</title>
                <style>
                    body { font-family: Georgia, serif; margin: 0; padding: 32px; background: #f7f4ee; color: #1f2933; }
                    .sheet { max-width: 860px; margin: 0 auto; background: #fff; border: 1px solid #d7d0c4; border-radius: 20px; padding: 32px; }
                    .eyebrow { text-transform: uppercase; letter-spacing: 0.14em; font-size: 12px; color: #0f766e; margin: 0 0 10px; }
                    h1 { margin: 0 0 8px; font-size: 36px; }
                    p { margin: 0 0 18px; color: #52606d; }
                    .meta-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 14px; margin: 24px 0; }
                    .meta-grid div { border: 1px solid #e2dccf; border-radius: 14px; padding: 14px; background: #faf7f1; }
                    .meta-grid span { display: block; font-size: 12px; text-transform: uppercase; letter-spacing: 0.08em; color: #6b7280; margin-bottom: 6px; }
                    table { width: 100%%; border-collapse: collapse; margin-top: 20px; }
                    th, td { text-align: left; padding: 14px; border-bottom: 1px solid #e5e0d7; }
                    th { background: #f4efe7; font-size: 12px; text-transform: uppercase; letter-spacing: 0.08em; color: #6b7280; }
                    .total td { font-weight: 700; font-size: 18px; }
                </style>
                </head>
                <body>
                    <section class="sheet">
                        <p class="eyebrow">Gray Hospital</p>
                        <h1>%s</h1>
                        <p>Generated by the payroll system. You can print this page or save it as PDF from the browser.</p>
                        %s
                    </section>
                </body>
                </html>
                """.formatted(title, title, body);
    }

    private String money(BigDecimal value) {
        return value == null ? "0.00" : value.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}
