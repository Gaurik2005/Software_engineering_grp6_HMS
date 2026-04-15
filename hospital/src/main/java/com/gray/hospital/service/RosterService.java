package com.gray.hospital.service;

import com.gray.hospital.controller.dto.WeeklyRosterRow;
import com.gray.hospital.entity.Doctor;
import com.gray.hospital.entity.Nurse;
import com.gray.hospital.entity.Roster;
import com.gray.hospital.repository.DoctorRepository;
import com.gray.hospital.repository.NurseRepository;
import com.gray.hospital.repository.RosterRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RosterService {

    public static final LocalTime DEFAULT_SHIFT_START = LocalTime.of(9, 0);
    public static final LocalTime DEFAULT_SHIFT_END = LocalTime.of(17, 0);

    private final RosterRepository rosterRepository;
    private final DoctorRepository doctorRepository;
    private final NurseRepository nurseRepository;

    public RosterService(RosterRepository rosterRepository,
                         DoctorRepository doctorRepository,
                         NurseRepository nurseRepository){

        this.rosterRepository = rosterRepository;
        this.doctorRepository = doctorRepository;
        this.nurseRepository = nurseRepository;
    }

    public Roster addRoster(Long doctorId,
                            Long roomId,
                            LocalDate date,
                            String start,
                            String end){
        if (rosterRepository.existsByDoctorDoctorIdAndDate(doctorId, date)) {
            throw new RuntimeException("Doctor already has a roster for this date");
        }

        if (rosterRepository.existsByRoomIdAndDate(roomId, date)) {
            throw new RuntimeException("Room already assigned for this date");
        }

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow();

        Roster roster = new Roster();

        roster.setDoctor(doctor);
        roster.setRoomId(roomId);
        roster.setDate(date);
        roster.setStartTime(resolveStartTime(start));
        roster.setEndTime(resolveEndTime(end));

        return rosterRepository.save(roster);
    }

    public Roster addStandardRoster(Long doctorId,
                                    Long roomId,
                                    LocalDate date){
        return addRoster(
                doctorId,
                roomId,
                date,
                DEFAULT_SHIFT_START.toString(),
                DEFAULT_SHIFT_END.toString()
        );
    }

    public List<Roster> getRosterForDay(LocalDate date){

        return rosterRepository.findByDate(date);

    }
    public List<Roster> getRosterByDate(LocalDate date){
        return rosterRepository.findByDate(date);
    }

    public List<Roster> getRosterForWeek(LocalDate startDate){
        return rosterRepository.findByDateBetween(startDate, startDate.plusDays(6));
    }

    public List<WeeklyRosterRow> getWeeklyRosterView(LocalDate startDate){
        LocalDate endDate = startDate.plusDays(6);
        List<Roster> weeklyRoster = rosterRepository.findByDateBetween(startDate, endDate);
        List<WeeklyRosterRow> rows = new ArrayList<>();

        for (Roster roster : weeklyRoster) {
            Nurse nurse = nurseRepository.findByDoctorDoctorId(roster.getDoctor().getDoctorId())
                    .orElse(null);

            rows.add(new WeeklyRosterRow(
                    startDate.toString(),
                    endDate.toString(),
                    roster.getDate().toString(),
                    roster.getDoctor().getDoctorId(),
                    roster.getDoctor().getName(),
                    roster.getDoctor().getDoctorType(),
                    roster.getRoomId(),
                    roster.getStartTime().toString(),
                    roster.getEndTime().toString(),
                    nurse != null ? nurse.getNurseId() : null,
                    nurse != null ? nurse.getName() : "Not Assigned"
            ));
        }

        return rows;
    }

    public List<WeeklyRosterRow> getWeeklyRosterViewForDoctor(LocalDate startDate, Long doctorId){
        return getWeeklyRosterView(startDate).stream()
                .filter(row -> row.doctorId().equals(doctorId))
                .toList();
    }

    private LocalTime resolveStartTime(String start){
        if (start == null || start.isBlank()) {
            return DEFAULT_SHIFT_START;
        }

        LocalTime parsed = LocalTime.parse(start);
        if (!parsed.equals(DEFAULT_SHIFT_START)) {
            throw new RuntimeException("Doctor roster must start at 09:00");
        }
        return parsed;
    }

    private LocalTime resolveEndTime(String end){
        if (end == null || end.isBlank()) {
            return DEFAULT_SHIFT_END;
        }

        LocalTime parsed = LocalTime.parse(end);
        if (!parsed.equals(DEFAULT_SHIFT_END)) {
            throw new RuntimeException("Doctor roster must end at 17:00");
        }
        return parsed;
    }
}
