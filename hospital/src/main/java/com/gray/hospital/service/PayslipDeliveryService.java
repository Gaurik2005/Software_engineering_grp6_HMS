package com.gray.hospital.service;

import com.gray.hospital.controller.dto.PayslipEmailResponse;
import com.gray.hospital.entity.Doctor;
import com.gray.hospital.entity.Nurse;
import com.gray.hospital.repository.DoctorRepository;
import com.gray.hospital.repository.NurseRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;

@Service
public class PayslipDeliveryService {

    private final JavaMailSender mailSender;
    private final DoctorRepository doctorRepository;
    private final NurseRepository nurseRepository;
    private final PayslipDocumentService payslipDocumentService;

    @Value("${spring.mail.host:}")
    private String mailHost;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    public PayslipDeliveryService(ObjectProvider<JavaMailSender> mailSenderProvider,
                                  DoctorRepository doctorRepository,
                                  NurseRepository nurseRepository,
                                  PayslipDocumentService payslipDocumentService) {
        this.mailSender = mailSenderProvider.getIfAvailable();
        this.doctorRepository = doctorRepository;
        this.nurseRepository = nurseRepository;
        this.payslipDocumentService = payslipDocumentService;
    }

    public PayslipEmailResponse emailDoctorPayslip(Long doctorId, int month, int year) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        if (!StringUtils.hasText(doctor.getEmail())) {
            throw new RuntimeException("Doctor email is not set");
        }

        Path documentPath = payslipDocumentService.resolveDoctorPayslipPath(doctorId, year, month);
        verifyDocumentExists(documentPath);

        return deliver(
                doctor.getEmail(),
                "DOCTOR",
                doctorId,
                month,
                year,
                "Doctor Payslip",
                "Dear " + safeName(doctor.getName()) + ",\n\nPlease find your payslip attached.",
                documentPath
        );
    }

    public PayslipEmailResponse emailNursePayslip(Long nurseId, int month, int year) {
        Nurse nurse = nurseRepository.findById(nurseId)
                .orElseThrow(() -> new RuntimeException("Nurse not found"));

        if (!StringUtils.hasText(nurse.getEmail())) {
            throw new RuntimeException("Nurse email is not set");
        }

        Path documentPath = payslipDocumentService.resolveNursePayslipPath(nurseId, year, month);
        verifyDocumentExists(documentPath);

        return deliver(
                nurse.getEmail(),
                "NURSE",
                nurseId,
                month,
                year,
                "Nurse Payslip",
                "Dear " + safeName(nurse.getName()) + ",\n\nPlease find your payslip attached.",
                documentPath
        );
    }

    private PayslipEmailResponse deliver(String recipientEmail,
                                         String payslipType,
                                         Long staffId,
                                         int month,
                                         int year,
                                         String subject,
                                         String body,
                                         Path documentPath) {
        if (!isSmtpConfigured()) {
            Path outboxPath = writeOutboxCopy(recipientEmail, payslipType, staffId, month, year, documentPath);
            return new PayslipEmailResponse(
                    recipientEmail,
                    payslipType,
                    staffId,
                    month,
                    year,
                    false,
                    true,
                    "SMTP is not configured. Payslip email saved to outbox instead.",
                    outboxPath.toString()
            );
        }

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(recipientEmail);
            helper.setSubject(subject + " - " + month + "/" + year);
            helper.setText(buildTextEmailBody(body, documentPath), true);
            if (StringUtils.hasText(mailUsername)) {
                helper.setFrom(mailUsername);
            }
            helper.addAttachment(documentPath.getFileName().toString(), documentPath.toFile());
            mailSender.send(mimeMessage);

            return new PayslipEmailResponse(
                    recipientEmail,
                    payslipType,
                    staffId,
                    month,
                    year,
                    true,
                    false,
                    "Payslip emailed successfully.",
                    documentPath.toString()
            );
        } catch (Exception exception) {
            throw new RuntimeException("Failed to send payslip email", exception);
        }
    }

    private boolean isSmtpConfigured() {
        return mailSender != null && StringUtils.hasText(mailHost);
    }

    private void verifyDocumentExists(Path documentPath) {
        if (!Files.exists(documentPath)) {
            throw new RuntimeException("Payslip document not found. Generate the payslip first.");
        }
    }

    private Path writeOutboxCopy(String recipientEmail,
                                 String payslipType,
                                 Long staffId,
                                 int month,
                                 int year,
                                 Path documentPath) {
        try {
            Path outboxDir = Path.of("target", "email-outbox");
            Files.createDirectories(outboxDir);
            String sanitizedEmail = recipientEmail.replaceAll("[^a-zA-Z0-9._-]", "_");
            String filename = payslipType.toLowerCase() + "-" + staffId + "-" + year + "-" + month + "-" + sanitizedEmail + ".txt";
            Path outboxPath = outboxDir.resolve(filename);
            Files.copy(documentPath, outboxPath, StandardCopyOption.REPLACE_EXISTING);

            Path logPath = outboxDir.resolve("email-log.txt");
            String logLine = LocalDateTime.now() + " | " + payslipType + " | " + staffId + " | " + recipientEmail + " | " + outboxPath + System.lineSeparator();
            Files.writeString(logPath, logLine, java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
            return outboxPath;
        } catch (IOException exception) {
            throw new RuntimeException("Failed to write payslip email outbox artifact", exception);
        }
    }

    private String safeName(String value) {
        return StringUtils.hasText(value) ? value : "Staff Member";
    }

    private String buildTextEmailBody(String introText, Path documentPath) {
    try {
        String html = Files.readString(documentPath);

        // VERY basic HTML → text cleanup
        String text = html
                .replaceAll("<[^>]*>", "")   // remove all tags
                .replaceAll("&nbsp;", " ")
                .replaceAll("&amp;", "&")
                .replaceAll("\\s{2,}", " ")
                .trim();

        return introText + "\n\n----- PAYSLIP -----\n\n" + text;

    } catch (IOException e) {
        throw new RuntimeException("Failed to build text email", e);
    }
}
    private String escapeHtml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}


