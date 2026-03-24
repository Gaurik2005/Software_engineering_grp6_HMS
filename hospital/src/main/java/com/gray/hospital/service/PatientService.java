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

        if(patientRepository.findByEmail(patient.getEmail()).isPresent()){
            throw new RuntimeException("Email already registered");
        }

        return patientRepository.save(patient);
    }

}