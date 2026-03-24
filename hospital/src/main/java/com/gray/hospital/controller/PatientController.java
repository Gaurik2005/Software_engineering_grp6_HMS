package com.gray.hospital.controller;

import com.gray.hospital.entity.Patient;
import com.gray.hospital.service.PatientService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService){
        this.patientService = patientService;
    }

    @PostMapping("/register")
    public Patient register(@RequestBody Patient patient){
        return patientService.registerPatient(patient);
    }

}