package com.gray.hospital.controller;

import com.gray.hospital.entity.Roster;
import com.gray.hospital.service.RosterService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/roster")
public class RosterController {

    private final RosterService rosterService;

    public RosterController(RosterService rosterService){
        this.rosterService = rosterService;
    }

    @PostMapping("/add")
    public Roster addRoster(
            @RequestParam Long doctorId,
            @RequestParam Long roomId,
            @RequestParam String date,
            @RequestParam String start,
            @RequestParam String end){

        return rosterService.addRoster(
                doctorId,
                roomId,
                LocalDate.parse(date),
                start,
                end
        );
    }

    @GetMapping
    public List<Roster> getRoster(@RequestParam String date){

        return rosterService.getRosterForDay(
                LocalDate.parse(date)
        );
    }
}