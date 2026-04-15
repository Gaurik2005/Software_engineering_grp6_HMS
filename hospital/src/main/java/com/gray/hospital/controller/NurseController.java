package com.gray.hospital.controller;

import com.gray.hospital.entity.Doctor;
import com.gray.hospital.entity.Nurse;
import com.gray.hospital.entity.NurseDuty;
import com.gray.hospital.repository.DoctorRepository;
import com.gray.hospital.repository.NurseRepository;
import com.gray.hospital.service.NurseService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/nurses")
public class NurseController {

    private final NurseRepository nurseRepository;
    private final DoctorRepository doctorRepository;
    private final NurseService nurseService;

    public NurseController(NurseRepository nurseRepository,
                           DoctorRepository doctorRepository,
                           NurseService nurseService){
        this.nurseRepository = nurseRepository;
        this.doctorRepository = doctorRepository;
        this.nurseService = nurseService;
    }

    // ✅ ADD NURSE
    @PostMapping("/add")
    public Nurse add(@RequestBody Nurse nurse){
        return nurseRepository.save(nurse);
    }

    // ✅ DELETE NURSE
    @DeleteMapping("/delete")
    public void delete(@RequestParam Long nurseId){
        nurseRepository.deleteById(nurseId);
    }

    // ✅ ASSIGN NURSE TO DOCTOR (MAIN FEATURE)
    @PutMapping("/assign")
    public Nurse assignNurse(
            @RequestParam Long nurseId,
            @RequestParam Long doctorId,
            HttpServletRequest request){

        Object role = request.getSession().getAttribute("role");

        if(role == null || !role.equals("HEAD_NURSE")){
            throw new RuntimeException("Only Head Nurse can assign");
        }

        Nurse nurse = nurseRepository.findById(nurseId)
                .orElseThrow(() -> new RuntimeException("Nurse not found"));

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        nurse.setDoctor(doctor);

        return nurseRepository.save(nurse);
    }

    // ✅ VIEW ALL NURSES
    @GetMapping("/all")
    public List<Nurse> all(){
        return nurseRepository.findAll();
    }

    @PutMapping("/make-head")
    public Nurse makeHead(@RequestParam Long nurseId){

        Nurse nurse = nurseRepository.findById(nurseId)
                .orElseThrow(() -> new RuntimeException("Nurse not found"));

        nurse.setRole("HEAD");

        return nurseRepository.save(nurse);
    }

    @PutMapping("/update-pay")
    public Nurse updateNursePay(
            @RequestParam Long nurseId,
            @RequestParam BigDecimal salary){

        Nurse nurse = nurseRepository.findById(nurseId)
                .orElseThrow(() -> new RuntimeException("Nurse not found"));

        nurse.setSalary(salary);

        return nurseRepository.save(nurse);
    }

    @PostMapping("/duties/assign")
    public NurseDuty assignDuty(
            @RequestParam Long nurseId,
            @RequestParam String dutyDate,
            @RequestParam String shiftStart,
            @RequestParam String shiftEnd,
            @RequestParam(required = false) String wardOrRoom,
            @RequestParam(required = false) String notes,
            HttpServletRequest request){

        Object role = request.getSession().getAttribute("role");
        Object userId = request.getSession().getAttribute("userId");

        if (role == null || !role.equals("HEAD_NURSE")) {
            throw new RuntimeException("Only Head Nurse can assign weekly nurse duty");
        }

        return nurseService.assignDuty(
                nurseId,
                ((Number) userId).longValue(),
                LocalDate.parse(dutyDate),
                LocalTime.parse(shiftStart),
                LocalTime.parse(shiftEnd),
                wardOrRoom,
                notes
        );
    }

    @GetMapping("/duties/week")
    public List<NurseDuty> getWeeklyDuties(@RequestParam String startDate){
        return nurseService.getDutyForWeek(LocalDate.parse(startDate));
    }

    @GetMapping("/{nurseId}/duties/week")
    public List<NurseDuty> getNurseWeeklyDuties(
            @PathVariable Long nurseId,
            @RequestParam String startDate){
        return nurseService.getDutyForNurse(nurseId, LocalDate.parse(startDate));
    }

}
