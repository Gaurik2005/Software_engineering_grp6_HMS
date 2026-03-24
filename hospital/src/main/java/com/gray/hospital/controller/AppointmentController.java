package com.gray.hospital.controller;

import com.gray.hospital.entity.Appointment;
import com.gray.hospital.entity.Doctor;
import com.gray.hospital.service.AppointmentService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService){
        this.appointmentService = appointmentService;
    }

    @GetMapping("/doctors")
    public List<Doctor> availableDoctors(){
        return appointmentService.getAvailableDoctors();
    }

    @PostMapping("/book")
    public Appointment book(
            @RequestParam Long patientId,
            @RequestParam Long doctorId,
            @RequestParam String start,
            @RequestParam String end){

        return appointmentService.bookAppointment(
                patientId,
                doctorId,
                LocalDateTime.parse(start),
                LocalDateTime.parse(end)
        );
    }
}