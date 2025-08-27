package com.project.back_end.controllers;

/*
  INSTRUCTIONS (kept as requested):
  1. Set Up the Controller Class:
     - Annotate class with @RestController and use @RequestMapping("${api.path}doctor").
     - Manage doctor-related functionalities: registration, login, updates, availability, filtering.

  2. Autowire Dependencies:
     - Inject DoctorService for doctor CRUD and availability.
     - Inject shared Service for token validation and filtering.
     - Inject TokenService for token generation/extraction when needed.

  3. getDoctorAvailability:
     - GET: /availability/{user}/{doctorId}/{date}/{token}
     - Validate token for the provided user role. On success, return available time slots for the date.

  4. getDoctor:
     - GET: returns list of all doctors.

  5. saveDoctor:
     - POST with admin token: validates token; handle conflicts and errors; return appropriate status.

  6. doctorLogin:
     - POST login with Login DTO; validate credentials; return token and message.

  7. updateDoctor:
     - PUT with admin token; validate existence; save or return errors accordingly.

  8. deleteDoctor:
     - DELETE with admin token; delete associated appointments and doctor; return status.

  9. filter:
     - GET filter by name/time/speciality via shared service.
*/

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;
import com.project.back_end.services.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${api.path}doctor")
public class DoctorController {

        private final DoctorService doctorService;
        private final Service sharedService;
        private final TokenService tokenService;

        public DoctorController(DoctorService doctorService, Service sharedService, TokenService tokenService) {
                this.doctorService = doctorService;
                this.sharedService = sharedService;
                this.tokenService = tokenService;
        }

        @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
        public ResponseEntity<Map<String, Object>> getDoctorAvailability(@PathVariable String user,
                                                                         @PathVariable Long doctorId,
                                                                         @PathVariable String date,
                                                                         @PathVariable String token) {
                Map<String, Object> res = new HashMap<>();
                if (!sharedService.validateToken(token, user)) {
                        res.put("message", "Invalid token");
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
                }
                var slots = doctorService.getDoctorAvailabilitySlots(doctorId, LocalDate.parse(date));
                res.put("availableTimes", slots);
                return ResponseEntity.ok(res);
        }


        @GetMapping
        public ResponseEntity<Map<String, Object>> getDoctor() {
                Map<String, Object> res = new HashMap<>();
                res.put("doctors", doctorService.getDoctors());
                return ResponseEntity.ok(res);
        }

        @PostMapping("/{token}")
        public ResponseEntity<Map<String, Object>> saveDoctor(@RequestBody Doctor doctor, @PathVariable String token) {
                Map<String, Object> res = new HashMap<>();
                if (!sharedService.validateToken(token, "admin")) {
                        res.put("message", "Unauthorized");
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
                }
                int r = doctorService.saveDoctor(doctor);
                if (r == -1) {
                        res.put("message", "Doctor already exists");
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(res);
                } else if (r == 0) {
                        res.put("message", "Failed to save doctor");
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
                }
                res.put("message", "Doctor saved successfully");
                return ResponseEntity.status(HttpStatus.CREATED).body(res);
        }

        @PostMapping("/login")
        public ResponseEntity<Map<String, Object>> doctorLogin(@RequestBody Login login) {
                Map<String, Object> res = new HashMap<>();
                var doc = doctorService.validateDoctor(login.getEmail(), login.getPassword());
                if (doc == null) {
                        res.put("message", "Invalid credentials");
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
                }
                res.put("token", tokenService.generateToken(doc.getEmail()));
                res.put("message", "Login successful");
                return ResponseEntity.ok(res);
        }

        @PutMapping("/{token}")
        public ResponseEntity<Map<String, Object>> updateDoctor(@RequestBody Doctor doctor, @PathVariable String token) {
                Map<String, Object> res = new HashMap<>();
                if (!sharedService.validateToken(token, "admin")) {
                        res.put("message", "Unauthorized");
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
                }
                int r = doctorService.updateDoctor(doctor);
                if (r == -1) {
                        res.put("message", "Doctor not found");
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
                } else if (r == 0) {
                        res.put("message", "Failed to update doctor");
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
                }
                res.put("message", "Doctor updated successfully");
                return ResponseEntity.ok(res);
        }

        @DeleteMapping("/{id}/{token}")
        public ResponseEntity<Map<String, Object>> deleteDoctor(@PathVariable Long id, @PathVariable String token) {
                Map<String, Object> res = new HashMap<>();
                if (!sharedService.validateToken(token, "admin")) {
                        res.put("message", "Unauthorized");
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
                }
                int r = doctorService.deleteDoctor(id);
                if (r == -1) {
                        res.put("message", "Doctor not found");
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
                } else if (r == 0) {
                        res.put("message", "Failed to delete doctor");
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
                }
                res.put("message", "Doctor deleted successfully");
                return ResponseEntity.ok(res);
        }

        @GetMapping("/filter/{name}/{time}/{speciality}")
        public Map<String, Object> filter(@PathVariable String name,
                                          @PathVariable String time,
                                          @PathVariable String speciality) {
                return sharedService.filterDoctor(name, time, speciality);
        }
}
