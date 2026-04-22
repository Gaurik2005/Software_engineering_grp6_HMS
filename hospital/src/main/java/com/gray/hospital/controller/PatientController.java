package com.gray.hospital.controller;

import com.gray.hospital.entity.Patient;
import com.gray.hospital.service.PatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService){
        this.patientService = patientService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Patient patient){
        try {
            return ResponseEntity.ok(patientService.registerPatient(patient));
        } catch (RuntimeException exception) {
            return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage()));
        }
    }

}
