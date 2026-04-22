package com.gray.hospital.controller.dto;

import java.math.BigDecimal;

public record PayslipGenerationResponse(
        Long payslipId,
        String staffType,
        Long staffId,
        String staffName,
        int month,
        int year,
        BigDecimal amount,
        String documentPath,
        String documentUrl
) {
}
