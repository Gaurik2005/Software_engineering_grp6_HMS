package com.gray.hospital.service;

import com.gray.hospital.entity.Doctor;
import com.gray.hospital.entity.RegularDoctor;
import com.gray.hospital.repository.DoctorRepository;
import com.gray.hospital.repository.RegularDoctorRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final RegularDoctorRepository regularDoctorRepository;

    public DoctorService(DoctorRepository doctorRepository,
                         RegularDoctorRepository regularDoctorRepository) {
        this.doctorRepository = doctorRepository;
        this.regularDoctorRepository = regularDoctorRepository;
    }

    public Doctor addDoctor(Doctor doctor, BigDecimal basicPay){
        Doctor savedDoctor = doctorRepository.save(doctor);

        if ("REGULAR".equalsIgnoreCase(savedDoctor.getDoctorType())) {
            if (basicPay == null) {
                throw new RuntimeException("Regular doctors require a basic pay");
            }

            RegularDoctor regularDoctor = new RegularDoctor();
            regularDoctor.setDoctorId(savedDoctor.getDoctorId());
            regularDoctor.setBasicPay(basicPay);
            regularDoctorRepository.save(regularDoctor);
        }

        return savedDoctor;
    }

    public List<Doctor> getDoctors(){
        return doctorRepository.findAll();
    }

    public Doctor updateBasicPay(Long doctorId, BigDecimal newPay){
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        if (!"REGULAR".equalsIgnoreCase(doctor.getDoctorType())) {
            throw new RuntimeException("Only regular doctors have basic pay");
        }

        RegularDoctor regularDoctor = regularDoctorRepository.findById(doctorId)
                .orElseGet(() -> {
                    RegularDoctor created = new RegularDoctor();
                    created.setDoctorId(doctorId);
                    return created;
                });

        regularDoctor.setBasicPay(newPay);
        regularDoctorRepository.save(regularDoctor);

        return doctor;
    }

    public void deleteDoctor(Long doctorId){
        if (!doctorRepository.existsById(doctorId)) {
            throw new RuntimeException("Doctor not found");
        }

        doctorRepository.deleteById(doctorId);
        regularDoctorRepository.findById(doctorId)
                .ifPresent(regularDoctorRepository::delete);
    }
}
