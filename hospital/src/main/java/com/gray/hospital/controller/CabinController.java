package com.gray.hospital.controller;

import com.gray.hospital.entity.CabinBooking;
import com.gray.hospital.entity.Cabin;
import com.gray.hospital.service.CabinService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/cabins")
public class CabinController {

    private final CabinService cabinService;

    public CabinController(CabinService cabinService){
        this.cabinService = cabinService;
    }

    @GetMapping("/available")
    public List<Cabin> getAvailableCabins(
            @RequestParam String start,
            @RequestParam String end){
        return cabinService.getAvailableCabins(LocalDate.parse(start), LocalDate.parse(end));
    }

    @GetMapping("/amount")
    public BigDecimal calculateAmount(
            @RequestParam Long cabinId,
            @RequestParam String start,
            @RequestParam String end){
        return cabinService.calculateAmount(cabinId, LocalDate.parse(start), LocalDate.parse(end));
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
