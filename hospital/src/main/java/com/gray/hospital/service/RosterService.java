package com.gray.hospital.service;

import com.gray.hospital.entity.Doctor;
import com.gray.hospital.entity.Roster;
import com.gray.hospital.repository.DoctorRepository;
import com.gray.hospital.repository.RosterRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RosterService {

    private final RosterRepository rosterRepository;
    private final DoctorRepository doctorRepository;

    public RosterService(RosterRepository rosterRepository,
                         DoctorRepository doctorRepository){

        this.rosterRepository = rosterRepository;
        this.doctorRepository = doctorRepository;
    }

    public Roster addRoster(Long doctorId,
                            Long roomId,
                            LocalDate date,
                            String start,
                            String end){

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow();

        Roster roster = new Roster();

        roster.setDoctor(doctor);
        roster.setRoomId(roomId);
        roster.setDate(date);
        roster.setStartTime(java.time.LocalTime.parse(start));
        roster.setEndTime(java.time.LocalTime.parse(end));

        return rosterRepository.save(roster);
    }

    public List<Roster> getRosterForDay(LocalDate date){

        return rosterRepository.findByDate(date);

    }
}

