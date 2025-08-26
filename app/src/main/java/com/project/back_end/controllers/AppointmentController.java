package com.project.back_end.controllers;

/*
  INSTRUCTIONS (kept as requested):
  1. Set Up the Controller Class:
     - @RestController with @RequestMapping("/appointments") to group appointment APIs.

  2. Autowire Dependencies:
     - AppointmentService for appointment business logic.
     - Shared Service for token validation and appointment checks.
     - TokenService, DoctorRepository, PatientRepository as needed.

  3. getAppointments (GET /{date}/{patientName}/{token}):
     - Validate doctor token; fetch doctor by token; return appointments for that date filtered by patient name.

  4. bookAppointment (POST /{token}):
     - Validate patient token; validate requested slot; save appointment; return appropriate status/message.

  5. updateAppointment (PUT /{token}):
     - Validate patient token; delegate update logic to AppointmentService; return appropriately mapped status.

  6. cancelAppointment (optional future):
     - Would validate ownership and remove appointment.
*/

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;
import com.project.back_end.services.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final Service sharedService;
    private final TokenService tokenService;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public AppointmentController(AppointmentService appointmentService,
                                 Service sharedService,
                                 TokenService tokenService,
                                 DoctorRepository doctorRepository,
                                 PatientRepository patientRepository) {
        this.appointmentService = appointmentService;
        this.sharedService = sharedService;
        this.tokenService = tokenService;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<Map<String, Object>> getAppointments(@PathVariable String date,
                                                               @PathVariable String patientName,
                                                               @PathVariable String token) {
        Map<String, Object> res = new HashMap<>();
        if (!sharedService.validateToken(token, "doctor")) {
            res.put("message", "Invalid token");
            res.put("appointments", List.of());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
        }
        String email = tokenService.extractEmail(token);
        var doctor = doctorRepository.findByEmail(email);
        var list = appointmentService.getAppointments(doctor.getId(), LocalDate.parse(date), patientName);
        res.put("appointments", list);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, Object>> bookAppointment(@RequestBody Appointment appointment,
                                                               @PathVariable String token) {
        Map<String, Object> res = new HashMap<>();
        if (!sharedService.validateToken(token, "patient")) {
            res.put("message", "Invalid token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
        }
        LocalDate date = appointment.getAppointmentTime().toLocalDate();
        LocalTime time = appointment.getAppointmentTime().toLocalTime();
        int ok = sharedService.validateAppointment(appointment.getDoctor().getId(), date, time);
        if (ok == -1) {
            res.put("message", "Invalid doctor ID");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }
        if (ok == 0) {
            res.put("message", "Selected slot is not available");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(res);
        }
        int result = appointmentService.bookAppointment(appointment);
        if (result == 1) {
            res.put("message", "Appointment booked successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        } else {
            res.put("message", "Failed to book appointment");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @PutMapping("/{token}")
    public ResponseEntity<Map<String, Object>> updateAppointment(@RequestBody Appointment appointment,
                                                                 @PathVariable String token) {
        Map<String, Object> res = new HashMap<>();
        if (!sharedService.validateToken(token, "patient")) {
            res.put("message", "Invalid token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
        }
        var result = appointmentService.updateAppointment(appointment);
        HttpStatus status = HttpStatus.OK;
        if ("Appointment not found".equals(result.get("message"))) status = HttpStatus.NOT_FOUND;
        else if ("Unauthorized update request".equals(result.get("message"))) status = HttpStatus.UNAUTHORIZED;
        else if ("Doctor not found".equals(result.get("message"))) status = HttpStatus.BAD_REQUEST;
        else if ("Selected slot not available".equals(result.get("message"))) status = HttpStatus.CONFLICT;
        return ResponseEntity.status(status).body(result);
    }
}
