package com.gray.hospital.controller;

import com.gray.hospital.entity.CabinBooking;
import com.gray.hospital.service.CabinService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/cabins")
public class CabinController {

    private final CabinService cabinService;

    public CabinController(CabinService cabinService){
        this.cabinService = cabinService;
    }

    @PostMapping("/book")
    public CabinBooking book(
            @RequestParam Long patientId,
            @RequestParam Long cabinId,
            @RequestParam String start,
            @RequestParam String end){

        return cabinService.bookCabin(
                patientId,
                cabinId,
                LocalDate.parse(start),
                LocalDate.parse(end)
        );
    }
}