package com.gray.hospital.controller.dto;

public record DoctorAppointmentRow(
        Long appointmentId,
        Long patientId,
        String patientName,
        String slotStart,
        String slotEnd,
        String status,
        boolean hasMedicalRecord
) {
}
