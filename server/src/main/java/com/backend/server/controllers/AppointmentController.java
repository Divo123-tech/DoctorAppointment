package com.backend.server.controllers;

import com.backend.server.DTO.AppointmentDTO;
import com.backend.server.entities.Appointment;
import com.backend.server.entities.Doctor;
import com.backend.server.entities.Patient;
import com.backend.server.entities.User;
import com.backend.server.services.AppointmentService;
import com.backend.server.services.DoctorService;
import com.backend.server.services.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final DoctorService doctorService;
    @Autowired
    private final PatientService patientService;
    /**
     * Get doctor's appointments for a specific date
     */
    @GetMapping("/doctor/{doctorId}/date/{date}")
    public ResponseEntity<List<LocalTime>> getDoctorAppointmentsByDate(
            @PathVariable int doctorId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
            ) {

        Doctor doctor = doctorService.getDoctorById(doctorId);

        List<LocalTime> appointments = appointmentService.getDoctorAppointmentTimesByDate(doctor, date);
        return ResponseEntity.ok(appointments);
    }

    /**
     * Check if a time slot is available for a doctor
     */
    @GetMapping("/availability")
    public ResponseEntity<Boolean> checkAppointmentAvailability(
            @RequestParam int doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time) {

        Doctor doctor = doctorService.getDoctorById(doctorId);

        boolean isAvailable = appointmentService.isAppointmentAvailable(doctor, date, time);

//        Map<String, Boolean> response = new HashMap<>();
//        response.put("available", isAvailable);

        return ResponseEntity.ok(isAvailable);
    }

    /**
     * Get all appointments for a patient with specific status
     */
    @GetMapping()
    public ResponseEntity<List<AppointmentDTO.GetAppointmentDTO>> getPatientAppointmentsByStatus(
            @RequestParam(defaultValue = "CONFIRMED") String status,
            Authentication auth) {
        User user = (User) auth.getPrincipal(); // this is safe
        Patient patient = patientService.getPatientById(user.getId());

        List<AppointmentDTO.GetAppointmentDTO> appointments = appointmentService.getPatientAppointmentsByStatus(patient, Appointment.Status.valueOf(status));
        return ResponseEntity.ok(appointments);
    }
    /**
     * Get all appointments for a doctor
     */
    @GetMapping("/doctor")
    public ResponseEntity<List<AppointmentDTO.GetAppointmentDTO>> getDoctorAppointments(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "CONFIRMED") String status,
            Authentication auth) {
        User user = (User) auth.getPrincipal(); // this is safe

        Doctor doctor = doctorService.getDoctorById(user.getId());

        List<AppointmentDTO.GetAppointmentDTO> appointments = appointmentService.getDoctorAppointments(doctor, Appointment.Status.valueOf(status));
        return ResponseEntity.ok(appointments);
    }


    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDTO.GetAppointmentDTO> getAppointmentById(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    @PostMapping
    public ResponseEntity<AppointmentDTO.GetAppointmentDTO> createAppointment(@RequestBody AppointmentDTO.GetAppointmentDTO appointmentDTO, Authentication auth) {
        User userRequesting = (User) auth.getPrincipal(); // this is safe
        Doctor doctor = doctorService.getDoctorById(appointmentDTO.getDoctorId());

        boolean isAvailable = appointmentService.isAppointmentAvailable(doctor, appointmentDTO.getDate(), appointmentDTO.getTime());
        if (!isAvailable) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "time not available");
        }
        return ResponseEntity.ok(appointmentService.createAppointment(appointmentDTO, userRequesting.getId()));

    }

    @PatchMapping("/{id}")
    public ResponseEntity<AppointmentDTO.GetAppointmentDTO> updateAppointment(@PathVariable Long id, @RequestBody AppointmentDTO.GetAppointmentDTO appointmentDTO, Authentication auth) {
        User userRequesting = (User) auth.getPrincipal(); // this is safe
        return ResponseEntity.ok(appointmentService.updateAppointment(id, appointmentDTO, userRequesting));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }
}
