package com.project.back_end.services;

/*
  INSTRUCTIONS (kept as requested):
  1. Mark as @Service; inject PrescriptionRepository via constructor.
  2. savePrescription: prevent duplicate prescriptions per appointment; save and return 201, otherwise 400/500.
  3. getPrescription: fetch by appointmentId; return 200 with doc or 404/500 with message.
*/

import com.project.back_end.models.Prescription;
import com.project.back_end.repo.PrescriptionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    public ResponseEntity<Map<String, Object>> savePrescription(Prescription prescription) {
        Map<String, Object> res = new HashMap<>();
        try {
            List<Prescription> existing = prescriptionRepository.findByAppointmentId(prescription.getAppointmentId());
            if (!existing.isEmpty()) {
                res.put("message", "Prescription already exists for this appointment");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
            }
            prescriptionRepository.save(prescription);
            res.put("message", "Prescription saved successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        } catch (Exception e) {
            res.put("message", "Internal error while saving prescription");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    public ResponseEntity<?> getPrescription(Long appointmentId) {
        try {
            List<Prescription> list = prescriptionRepository.findByAppointmentId(appointmentId);
            if (list.isEmpty()) {
                Map<String, Object> res = new HashMap<>();
                res.put("message", "Prescription not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
            }
            return ResponseEntity.ok(list.get(0));
        } catch (Exception e) {
            Map<String, Object> res = new HashMap<>();
            res.put("message", "Internal error while fetching prescription");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }
}
