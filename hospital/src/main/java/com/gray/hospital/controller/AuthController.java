package com.gray.hospital.controller;

import com.gray.hospital.entity.Patient;
import com.gray.hospital.entity.Doctor;
import com.gray.hospital.entity.Nurse;
import com.gray.hospital.repository.PatientRepository;
import com.gray.hospital.repository.DoctorRepository;
import com.gray.hospital.repository.NurseRepository;

import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final NurseRepository  nurseRepository;

    public AuthController(PatientRepository patientRepository,
                          DoctorRepository doctorRepository,
                            NurseRepository nurseRepository){
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.nurseRepository = nurseRepository;
    }

    // ✅ LOGIN
    @PostMapping("/login")
    public Map<String,Object> login(
            @RequestParam String email,
            @RequestParam String password,
            HttpServletRequest request){

        Map<String,Object> res = new HashMap<>();

        // 🔥 ADMIN LOGIN (hardcoded)
        if(email.equals("admin") && password.equals("admin")){
            request.getSession().setAttribute("role","ADMIN");
            request.getSession().setAttribute("userId",0);

            res.put("role","ADMIN");
            res.put("userId", 0);
            return res;
        }

        //  PATIENT LOGIN
        Optional<Patient> patientOpt = patientRepository.findByEmail(email);

        if(patientOpt.isPresent()){
            Patient p = patientOpt.get();

            if(p.getPassword().equals(password)){
                request.getSession().setAttribute("role","PATIENT");
                request.getSession().setAttribute("userId",p.getPatientId());

                res.put("role","PATIENT");
                res.put("userId", p.getPatientId());
                return res;
            }
        }

        //  DOCTOR LOGIN
        Optional<Doctor> doctorOpt = doctorRepository.findByEmail(email);

        if(doctorOpt.isPresent()){
            Doctor d = doctorOpt.get();

            if(d.getPassword().equals(password)){
                request.getSession().setAttribute("role","DOCTOR");
                request.getSession().setAttribute("userId",d.getDoctorId());

                res.put("role","DOCTOR");
                res.put("userId", d.getDoctorId());
                return res;
            }
        }

        Optional<Nurse> nurseOpt = nurseRepository.findByEmail(email);

        if(nurseOpt.isPresent()){
            Nurse n = nurseOpt.get();

            if(n.getPassword().equals(password)){

                if("HEAD".equals(n.getRole())){
                    request.getSession().setAttribute("role","HEAD_NURSE");
                } else {
                    request.getSession().setAttribute("role","NURSE");
                }

                request.getSession().setAttribute("userId", n.getNurseId());

                res.put("role", request.getSession().getAttribute("role"));
                res.put("userId", n.getNurseId());
                return res;
            }
        }

        //  INVALID
        throw new RuntimeException("Invalid credentials");
    }

    //  GET CURRENT USER
    @GetMapping("/me")
    public Map<String,Object> me(HttpServletRequest request){

        Map<String,Object> res = new HashMap<>();

        Object role = request.getSession().getAttribute("role");
        Object userId = request.getSession().getAttribute("userId");

        res.put("role", role);
        res.put("userId", userId);

        return res;
    }

    //  LOGOUT
    @PostMapping("/logout")
    public void logout(HttpServletRequest request){
        request.getSession().invalidate();
    }
}
