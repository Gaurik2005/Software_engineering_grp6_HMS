package com.gray.hospital.controller.dto;

public record WeeklyRosterRow(
        String weekStart,
        String weekEnd,
        String date,
        Long doctorId,
        String doctorName,
        String doctorType,
        Long roomId,
        String startTime,
        String endTime,
        Long nurseId,
        String nurseName
) {
}
