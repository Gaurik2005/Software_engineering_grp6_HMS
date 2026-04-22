package com.gray.hospital.controller.dto;

public record MedicalRecordResponse(
        Long medicalRecordId,
        Long appointmentId,
        String diagnosis,
        String prescription,
        String notes,
        String createdAt,
        String updatedAt
) {
}
