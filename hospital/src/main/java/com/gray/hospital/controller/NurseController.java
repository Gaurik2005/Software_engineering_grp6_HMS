package com.gray.hospital.controller;

import com.gray.hospital.entity.Nurse;
import com.gray.hospital.service.NurseService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/nurses")
public class NurseController {

    private final NurseService nurseService;

    public NurseController(NurseService nurseService){
        this.nurseService = nurseService;
    }

    @PostMapping("/assign")
    public Nurse assignDoctor(
            @RequestParam Long nurseId,
            @RequestParam Long doctorId){

        return nurseService.assignDoctor(nurseId,doctorId);
    }
}