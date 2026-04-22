package com.gray.hospital.service;

import com.gray.hospital.entity.Patient;
import com.gray.hospital.repository.PatientRepository;
import org.springframework.stereotype.Service;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository){
        this.patientRepository = patientRepository;
    }

    public Patient registerPatient(Patient patient){
        if(patient.getName() == null || patient.getName().isBlank()){
            throw new RuntimeException("Name is required");
        }

        if(patient.getEmail() == null || patient.getEmail().isBlank()){
            throw new RuntimeException("Email is required");
        }

        if(patient.getPassword() == null || patient.getPassword().isBlank()){
            throw new RuntimeException("Password is required");
        }

        patient.setEmail(patient.getEmail().trim());

        if(patientRepository.findByEmail(patient.getEmail()).isPresent()){
            throw new RuntimeException("Email already registered");
        }

        return patientRepository.save(patient);
    }

}
