package com.gray.hospital.controller.dto;

public record PatientMedicalHistoryRow(
        Long medicalRecordId,
        Long appointmentId,
        Long doctorId,
        String doctorName,
        String doctorExpertise,
        String slotStart,
        String slotEnd,
        String status,
        String diagnosis,
        String prescription,
        String notes,
        String updatedAt
) {
}
