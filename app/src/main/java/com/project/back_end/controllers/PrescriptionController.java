package com.project.back_end.controllers;

/*
  INSTRUCTIONS (kept as requested):
  1. Set Up the Controller Class:
     - @RestController with @RequestMapping("${api.path}prescription") for prescription-related endpoints.

  2. Autowire Dependencies:
     - Inject PrescriptionService for saving and fetching prescriptions.
     - Inject shared Service for token validation.
     - Inject AppointmentService to update appointment status when a prescription is issued.

  3. savePrescription (POST /{token}):
     - Validate doctor token.
     - Delegate save to PrescriptionService.
     - On success, mark the appointment as completed via AppointmentService.

  4. getPrescription (GET /{appointmentId}/{token}):
     - Validate doctor token and fetch prescription by appointment ID.
*/

import com.project.back_end.models.Prescription;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${api.path}prescription")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final Service sharedService;
    private final AppointmentService appointmentService;

    public PrescriptionController(PrescriptionService prescriptionService,
                                  Service sharedService,
                                  AppointmentService appointmentService) {
        this.prescriptionService = prescriptionService;
        this.sharedService = sharedService;
        this.appointmentService = appointmentService;
    }

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, Object>> savePrescription(@RequestBody Prescription prescription,
                                                                @PathVariable String token) {
        if (!sharedService.validateToken(token, "doctor")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(java.util.Map.of("message", "Invalid token"));
        }
        var resp = prescriptionService.savePrescription(prescription);
        if (resp.getStatusCode().is2xxSuccessful()) {
            // mark appointment completed
            appointmentService.changeStatus(prescription.getAppointmentId(), 1);
        }
        return resp;
    }

    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<?> getPrescription(@PathVariable Long appointmentId, @PathVariable String token) {
        if (!sharedService.validateToken(token, "doctor")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(java.util.Map.of("message", "Invalid token"));
        }
        return prescriptionService.getPrescription(appointmentId);
    }
}
