package com.gray.hospital.service;

import com.gray.hospital.entity.*;
import com.gray.hospital.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AppointmentService {

    public static final int DEFAULT_SLOT_MINUTES = 60;

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final RosterRepository rosterRepository;
    private final PaymentRepository paymentRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    public AppointmentService(
        AppointmentRepository appointmentRepository,
        PatientRepository patientRepository,
        DoctorRepository doctorRepository,
        RosterRepository rosterRepository,
        PaymentRepository paymentRepository,
        MedicalRecordRepository medicalRecordRepository){

    this.appointmentRepository = appointmentRepository;
    this.patientRepository = patientRepository;
    this.doctorRepository = doctorRepository;
    this.rosterRepository = rosterRepository;
    this.paymentRepository = paymentRepository;
    this.medicalRecordRepository = medicalRecordRepository;
}

    public Appointment bookAppointment(
        Long patientId,
        Long doctorId,
        LocalDateTime start,
        LocalDateTime end){
        if (!start.plusMinutes(DEFAULT_SLOT_MINUTES).equals(end)) {
            throw new RuntimeException("Appointments must be booked in 1-hour slots");
        }

        LocalDate date = start.toLocalDate();

        Roster roster = rosterRepository
                .findByDoctorDoctorIdAndDate(doctorId,date)
                .orElseThrow(() -> new RuntimeException("Doctor not scheduled that day"));

        if(start.toLocalTime().isBefore(roster.getStartTime())
            || end.toLocalTime().isAfter(roster.getEndTime())){

            throw new RuntimeException("Outside doctor shift hours");
        }

        if(!appointmentRepository
                .findByDoctorDoctorIdAndSlotStart(doctorId,start)
                .isEmpty()){

            throw new RuntimeException("Slot already booked");
        }

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow();

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow();

        Appointment appointment = new Appointment();

        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setSlotStart(start);
        appointment.setSlotEnd(end);
        appointment.setStatus("BOOKED");

        Appointment savedAppointment = appointmentRepository.save(appointment);

        Payment payment = new Payment();
        payment.setPatientId(patientId);
        payment.setReferenceType("APPOINTMENT");
        payment.setReferenceId(savedAppointment.getAppointmentId());
        payment.setAmount(java.math.BigDecimal.valueOf(500));
        payment.setStatus("PAID");
        payment.setDescription("Appointment booking fee");
        payment.setPaidAt(LocalDateTime.now());
        paymentRepository.save(payment);

        return savedAppointment;
    }

    public List<Doctor> getAvailableDoctors(){
        return doctorRepository.findAll();
    }

    public List<Doctor> getAvailableDoctors(LocalDate date){
        List<Roster> rosterEntries = rosterRepository.findByDate(date);
        Map<Long, Doctor> doctors = new LinkedHashMap<>();

        for (Roster roster : rosterEntries) {
            doctors.put(roster.getDoctor().getDoctorId(), roster.getDoctor());
        }

        return new ArrayList<>(doctors.values());
    }

    public List<Doctor> getAvailableDoctorsForWeek(LocalDate startDate){
        LocalDate endDate = startDate.plusDays(6);
        List<Roster> rosterEntries = rosterRepository.findByDateBetween(startDate, endDate);
        Map<Long, Doctor> doctors = new LinkedHashMap<>();

        for (Roster roster : rosterEntries) {
            doctors.put(roster.getDoctor().getDoctorId(), roster.getDoctor());
        }

        return new ArrayList<>(doctors.values());
    }

    public List<Map<String, LocalDateTime>> getAvailableSlots(Long doctorId, LocalDate date, int slotMinutes){
        Roster roster = rosterRepository.findByDoctorDoctorIdAndDate(doctorId, date)
                .orElseThrow(() -> new RuntimeException("Doctor not scheduled that day"));

        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();

        List<Appointment> existingAppointments =
                appointmentRepository.findByDoctorDoctorIdAndSlotStartBetweenOrderBySlotStart(
                        doctorId,
                        dayStart,
                        dayEnd
                );

        List<Map<String, LocalDateTime>> availableSlots = new ArrayList<>();
        LocalDateTime slotStart = LocalDateTime.of(date, roster.getStartTime());
        LocalDateTime shiftEnd = LocalDateTime.of(date, roster.getEndTime());

        while (!slotStart.plusMinutes(slotMinutes).isAfter(shiftEnd)) {
            LocalDateTime slotEnd = slotStart.plusMinutes(slotMinutes);

            if (isSlotFree(existingAppointments, slotStart, slotEnd)) {
                Map<String, LocalDateTime> slot = new LinkedHashMap<>();
                slot.put("start", slotStart);
                slot.put("end", slotEnd);
                availableSlots.add(slot);
            }

            slotStart = slotStart.plusMinutes(slotMinutes);
        }

        return availableSlots;
    }

    public MedicalRecord saveMedicalRecord(Long appointmentId,
                                           String diagnosis,
                                           String prescription,
                                           String notes){
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        MedicalRecord medicalRecord = medicalRecordRepository.findByAppointmentAppointmentId(appointmentId)
                .orElseGet(MedicalRecord::new);

        medicalRecord.setAppointment(appointment);
        medicalRecord.setDiagnosis(diagnosis);
        medicalRecord.setPrescription(prescription);
        medicalRecord.setNotes(notes);

        LocalDateTime now = LocalDateTime.now();
        if (medicalRecord.getCreatedAt() == null) {
            medicalRecord.setCreatedAt(now);
        }
        medicalRecord.setUpdatedAt(now);

        appointment.setStatus("COMPLETED");
        appointmentRepository.save(appointment);

        return medicalRecordRepository.save(medicalRecord);
    }

    public MedicalRecord getMedicalRecord(Long appointmentId){
        return medicalRecordRepository.findByAppointmentAppointmentId(appointmentId)
                .orElseThrow(() -> new RuntimeException("Medical record not found"));
    }

    private boolean isSlotFree(List<Appointment> appointments,
                               LocalDateTime slotStart,
                               LocalDateTime slotEnd){
        for (Appointment appointment : appointments) {
            if (slotStart.isBefore(appointment.getSlotEnd())
                    && slotEnd.isAfter(appointment.getSlotStart())) {
                return false;
            }
        }

        return true;
    }
}
