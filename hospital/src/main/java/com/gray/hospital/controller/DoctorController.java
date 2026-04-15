package com.gray.hospital.controller;

import com.gray.hospital.entity.Doctor;
import com.gray.hospital.service.DoctorService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService){
        this.doctorService = doctorService;
    }

    @GetMapping("/all")
    public List<Doctor> getAllDoctors(){
        return doctorService.getDoctors();
    }

    @PostMapping("/add")
    public Doctor addDoctor(@RequestBody Doctor doctor,
                            @RequestParam(required = false) BigDecimal basicPay){
        return doctorService.addDoctor(doctor, basicPay);
    }

    @DeleteMapping("/delete")
    public void deleteDoctor(@RequestParam Long doctorId){
        doctorService.deleteDoctor(doctorId);
    }


    @PutMapping("/update-pay")
    public Doctor updatePay(
            @RequestParam Long doctorId,
            @RequestParam BigDecimal newPay){
        return doctorService.updateBasicPay(doctorId, newPay);
    }

}
