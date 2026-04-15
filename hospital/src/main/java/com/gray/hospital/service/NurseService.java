package com.gray.hospital.service;

import com.gray.hospital.entity.Doctor;
import com.gray.hospital.entity.Nurse;
import com.gray.hospital.entity.NurseDuty;
import com.gray.hospital.repository.DoctorRepository;
import com.gray.hospital.repository.NurseDutyRepository;
import com.gray.hospital.repository.NurseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class NurseService {

    private final NurseRepository nurseRepository;
    private final DoctorRepository doctorRepository;
    private final NurseDutyRepository nurseDutyRepository;

    public NurseService(
            NurseRepository nurseRepository,
            DoctorRepository doctorRepository,
            NurseDutyRepository nurseDutyRepository){

        this.nurseRepository = nurseRepository;
        this.doctorRepository = doctorRepository;
        this.nurseDutyRepository = nurseDutyRepository;
    }

    public Nurse assignDoctor(Long nurseId, Long doctorId){

        Nurse nurse = nurseRepository.findById(nurseId)
                .orElseThrow();

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow();

        nurse.setDoctor(doctor);

        return nurseRepository.save(nurse);
    }

    public NurseDuty assignDuty(Long nurseId,
                                Long assignedByNurseId,
                                LocalDate dutyDate,
                                LocalTime shiftStart,
                                LocalTime shiftEnd,
                                String wardOrRoom,
                                String notes){

        Nurse nurse = nurseRepository.findById(nurseId)
                .orElseThrow(() -> new RuntimeException("Nurse not found"));

        NurseDuty nurseDuty = new NurseDuty();
        nurseDuty.setNurse(nurse);
        nurseDuty.setAssignedByNurseId(assignedByNurseId);
        nurseDuty.setDutyDate(dutyDate);
        nurseDuty.setShiftStart(shiftStart);
        nurseDuty.setShiftEnd(shiftEnd);
        nurseDuty.setWardOrRoom(wardOrRoom);
        nurseDuty.setNotes(notes);

        return nurseDutyRepository.save(nurseDuty);
    }

    public List<NurseDuty> getDutyForWeek(LocalDate startDate){
        return nurseDutyRepository.findByDutyDateBetweenOrderByDutyDateAscShiftStartAsc(
                startDate,
                startDate.plusDays(6)
        );
    }

    public List<NurseDuty> getDutyForNurse(Long nurseId, LocalDate startDate){
        return nurseDutyRepository.findByNurseNurseIdAndDutyDateBetweenOrderByDutyDateAscShiftStartAsc(
                nurseId,
                startDate,
                startDate.plusDays(6)
        );
    }
}
