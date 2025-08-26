package com.project.back_end.controllers;

/*
  INSTRUCTIONS (kept as requested):
  1. Set Up the Controller Class:
     - Annotate the class with @RestController to define it as a REST API controller for patient-related operations.
     - Use @RequestMapping("/patient") to prefix all endpoints with /patient, grouping patient functionalities under a common route.

  2. Autowire Dependencies:
     - Inject PatientService to handle patient-specific logic such as creation, retrieval, and appointments.
     - Inject the shared Service class for tasks like token validation and login authentication.

  3. Define the getPatient Method:
     - Handles HTTP GET requests to retrieve patient details using a token.
     - Validates the token for the "patient" role using the shared service.
     - If valid, returns patient information; otherwise, returns an error message with 401 status.

  4. Define the createPatient Method:
     - Handles HTTP POST requests for patient registration.
     - Accepts a Patient object in the request body.
     - First checks if the patient already exists using the shared service, then saves.

  5. Define the login Method:
     - Handles HTTP POST requests for patient login using a Login DTO.
     - Delegates to validatePatientLogin in the shared service.

  6. Define the getPatientAppointment Method:
     - Handles HTTP GET requests to fetch appointment details for a specific patient.
     - Requires the patient ID, token, and user role as path variables.

  7. Define the filterPatientAppointment Method:
     - Handles HTTP GET requests to filter a patient's appointments based on condition and doctor name.
*/

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;
    private final Service sharedService;

    public PatientController(PatientService patientService, Service sharedService) {
        this.patientService = patientService;
        this.sharedService = sharedService;
    }

    @GetMapping("/{token}")
    public ResponseEntity<Map<String, Object>> getPatient(@PathVariable String token) {
        Map<String, Object> res = new HashMap<>();
        if (!sharedService.validateToken(token, "patient")) {
            res.put("message", "Invalid token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
        }
        var patient = patientService.getPatientDetails(token);
        res.put("patient", patient);
        return ResponseEntity.ok(res);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createPatient(@RequestBody Patient patient) {
        Map<String, Object> res = new HashMap<>();
        if (!sharedService.validatePatient(patient)) {
            res.put("message", "Patient already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(res);
        }
        int saved = patientService.createPatient(patient);
        if (saved == 1) {
            res.put("message", "Patient created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        } else {
            res.put("message", "Failed to create patient");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Login login) {
        return sharedService.validatePatientLogin(login);
    }

    @GetMapping("/{id}/{user}/{token}")
    public ResponseEntity<Map<String, Object>> getPatientAppointment(@PathVariable Long id,
                                                                     @PathVariable String user,
                                                                     @PathVariable String token) {
        Map<String, Object> res = new HashMap<>();
        if (!sharedService.validateToken(token, user)) {
            res.put("message", "Invalid token");
            res.put("appointments", java.util.List.of());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
        }
        res.put("appointments", patientService.getPatientAppointment(id));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/filter/{condition}/{name}/{token}")
    public ResponseEntity<Map<String, Object>> filterPatientAppointment(@PathVariable String condition,
                                                                        @PathVariable String name,
                                                                        @PathVariable String token) {
        Map<String, Object> resp = new HashMap<>(sharedService.filterPatient(condition, name, token));
        HttpStatus status = resp.containsKey("message") && "Invalid token".equals(resp.get("message"))
                ? HttpStatus.UNAUTHORIZED : HttpStatus.OK;
        return ResponseEntity.status(status).body(resp);
    }
}


