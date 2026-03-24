package com.gray.hospital.service;

import com.gray.hospital.entity.Doctor;
import com.gray.hospital.entity.Nurse;
import com.gray.hospital.repository.DoctorRepository;
import com.gray.hospital.repository.NurseRepository;
import org.springframework.stereotype.Service;

@Service
public class NurseService {

    private final NurseRepository nurseRepository;
    private final DoctorRepository doctorRepository;

    public NurseService(
            NurseRepository nurseRepository,
            DoctorRepository doctorRepository){

        this.nurseRepository = nurseRepository;
        this.doctorRepository = doctorRepository;
    }

    public Nurse assignDoctor(Long nurseId, Long doctorId){

        Nurse nurse = nurseRepository.findById(nurseId)
                .orElseThrow();

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow();

        nurse.setDoctor(doctor);

        return nurseRepository.save(nurse);
    }
}