package com.gray.hospital.controller;

import com.gray.hospital.service.AdminReportService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final AdminReportService adminReportService;

    public ReportController(AdminReportService adminReportService){
        this.adminReportService = adminReportService;
    }

    @GetMapping("/summary")
    public Map<String,Object> summary(){
        return adminReportService.generateReport();
    }

    @GetMapping("/summary/range")
    public Map<String, Object> summaryForRange(
            @RequestParam String startDate,
            @RequestParam String endDate){
        return adminReportService.generateReport(LocalDate.parse(startDate), LocalDate.parse(endDate));
    }
}
