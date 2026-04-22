package com.gray.hospital.controller;

import com.gray.hospital.controller.dto.DoctorAppointmentRow;
import com.gray.hospital.controller.dto.MedicalRecordResponse;
import com.gray.hospital.controller.dto.PatientMedicalHistoryRow;
import com.gray.hospital.entity.MedicalRecord;
import com.gray.hospital.repository.AppointmentRepository;
import com.gray.hospital.entity.Appointment;
import com.gray.hospital.entity.Doctor;
import com.gray.hospital.service.AppointmentService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final AppointmentRepository appointmentRepository;

    public AppointmentController(AppointmentService appointmentService,
                             AppointmentRepository appointmentRepository){
        this.appointmentService = appointmentService;
        this.appointmentRepository = appointmentRepository;
    }
    
    @GetMapping("/patient")
    public List<Appointment> getByPatient(@RequestParam Long patientId){
        return appointmentRepository.findByPatientPatientId(patientId);
    }

    @GetMapping("/doctor/{doctorId}")
    public List<DoctorAppointmentRow> getByDoctor(@PathVariable Long doctorId) {
        return appointmentService.getAppointmentsForDoctor(doctorId).stream()
                .map(appointment -> new DoctorAppointmentRow(
                        appointment.getAppointmentId(),
                        appointment.getPatient() != null ? appointment.getPatient().getPatientId() : null,
                        appointment.getPatient() != null ? appointment.getPatient().getName() : "Unknown Patient",
                        appointment.getSlotStart() != null ? appointment.getSlotStart().toString() : null,
                        appointment.getSlotEnd() != null ? appointment.getSlotEnd().toString() : null,
                        appointment.getStatus(),
                        appointmentService.medicalRecordExists(appointment.getAppointmentId())
                ))
                .toList();
    }
    @GetMapping("/doctors")
    public List<Doctor> availableDoctors(){
        return appointmentService.getAvailableDoctors();
    }

    @GetMapping("/doctors/day")
    public List<Doctor> availableDoctorsForDay(@RequestParam String date){
        return appointmentService.getAvailableDoctors(LocalDate.parse(date));
    }

    @GetMapping("/doctors/week")
    public List<Doctor> availableDoctorsForWeek(@RequestParam String startDate){
        return appointmentService.getAvailableDoctorsForWeek(LocalDate.parse(startDate));
    }

    @GetMapping("/slots")
    public List<Map<String, LocalDateTime>> availableSlots(
            @RequestParam Long doctorId,
            @RequestParam String date,
            @RequestParam(required = false, defaultValue = "30") int slotMinutes){
        return appointmentService.getAvailableSlots(doctorId, LocalDate.parse(date), slotMinutes);
    }

    @GetMapping("/fees")
    public Map<String, Integer> getBookingFees(){
        Map<String, Integer> fees = new LinkedHashMap<>();
        fees.put("appointmentBookingFee", 500);
        return fees;
    }

    @PostMapping("/book")
    public Appointment book(
            @RequestParam Long patientId,
            @RequestParam Long doctorId,
            @RequestParam String start,
            @RequestParam String end){

        return appointmentService.bookAppointment(
                patientId,
                doctorId,
                LocalDateTime.parse(start),
                LocalDateTime.parse(end)
        );
    }

    @PostMapping("/{appointmentId}/medical-record")
    public MedicalRecordResponse saveMedicalRecord(
            @PathVariable Long appointmentId,
            @RequestBody Map<String, String> payload){
        return toMedicalRecordResponse(appointmentId, appointmentService.saveMedicalRecord(
                appointmentId,
                payload.get("diagnosis"),
                payload.get("prescription"),
                payload.get("notes")
        ));
    }

    @GetMapping("/{appointmentId}/medical-record")
    public MedicalRecordResponse getMedicalRecord(@PathVariable Long appointmentId){
        return toMedicalRecordResponse(appointmentId, appointmentService.getMedicalRecord(appointmentId));
    }

    @GetMapping("/doctor/{doctorId}/medical-records")
    public List<MedicalRecordResponse> getMedicalRecordsForDoctor(@PathVariable Long doctorId) {
        return appointmentService.getMedicalRecordsForDoctor(doctorId).stream()
                .map(medicalRecord -> toMedicalRecordResponse(
                        medicalRecord.getAppointment().getAppointmentId(),
                        medicalRecord
                ))
                .toList();
    }

    @GetMapping("/patient/{patientId}/medical-history")
    public List<PatientMedicalHistoryRow> getMedicalHistoryForPatient(@PathVariable Long patientId) {
        return appointmentService.getMedicalRecordsForPatient(patientId).stream()
                .map(medicalRecord -> {
                    Appointment appointment = medicalRecord.getAppointment();
                    Doctor doctor = appointment.getDoctor();

                    return new PatientMedicalHistoryRow(
                            medicalRecord.getMedicalRecordId(),
                            appointment.getAppointmentId(),
                            doctor != null ? doctor.getDoctorId() : null,
                            doctor != null ? doctor.getName() : "Unknown Doctor",
                            doctor != null ? doctor.getExpertise() : null,
                            appointment.getSlotStart() != null ? appointment.getSlotStart().toString() : null,
                            appointment.getSlotEnd() != null ? appointment.getSlotEnd().toString() : null,
                            appointment.getStatus(),
                            medicalRecord.getDiagnosis(),
                            medicalRecord.getPrescription(),
                            medicalRecord.getNotes(),
                            medicalRecord.getUpdatedAt() != null ? medicalRecord.getUpdatedAt().toString() : null
                    );
                })
                .toList();
    }

    private MedicalRecordResponse toMedicalRecordResponse(Long appointmentId, MedicalRecord medicalRecord) {
        return new MedicalRecordResponse(
                medicalRecord.getMedicalRecordId(),
                appointmentId,
                medicalRecord.getDiagnosis(),
                medicalRecord.getPrescription(),
                medicalRecord.getNotes(),
                medicalRecord.getCreatedAt() != null ? medicalRecord.getCreatedAt().toString() : null,
                medicalRecord.getUpdatedAt() != null ? medicalRecord.getUpdatedAt().toString() : null
        );
    }
}
