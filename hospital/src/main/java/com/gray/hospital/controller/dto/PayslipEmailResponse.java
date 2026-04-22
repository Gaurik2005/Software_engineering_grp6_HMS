package com.gray.hospital.controller.dto;

public record PayslipEmailResponse(
        String recipientEmail,
        String payslipType,
        Long staffId,
        int month,
        int year,
        boolean delivered,
        boolean simulated,
        String message,
        String artifactPath
) {
}
