package com.gray.hospital.service;

import com.gray.hospital.entity.*;
import com.gray.hospital.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final RosterRepository rosterRepository;

    public AppointmentService(
        AppointmentRepository appointmentRepository,
        PatientRepository patientRepository,
        DoctorRepository doctorRepository,
        RosterRepository rosterRepository){

    this.appointmentRepository = appointmentRepository;
    this.patientRepository = patientRepository;
    this.doctorRepository = doctorRepository;
    this.rosterRepository = rosterRepository;
}

    public Appointment bookAppointment(
        Long patientId,
        Long doctorId,
        LocalDateTime start,
        LocalDateTime end){

        LocalDate date = start.toLocalDate();

        Roster roster = rosterRepository
                .findByDoctorDoctorIdAndDate(doctorId,date)
                .orElseThrow(() -> new RuntimeException("Doctor not scheduled that day"));

        if(start.toLocalTime().isBefore(roster.getStartTime())
            || end.toLocalTime().isAfter(roster.getEndTime())){

            throw new RuntimeException("Outside doctor shift hours");
        }

        if(!appointmentRepository
                .findByDoctorDoctorIdAndSlotStart(doctorId,start)
                .isEmpty()){

            throw new RuntimeException("Slot already booked");
        }

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow();

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow();

        Appointment appointment = new Appointment();

        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setSlotStart(start);
        appointment.setSlotEnd(end);
        appointment.setStatus("BOOKED");

        return appointmentRepository.save(appointment);
    }

    public List<Doctor> getAvailableDoctors(){

        return doctorRepository.findByDoctorType("REGULAR");
    }
    
}