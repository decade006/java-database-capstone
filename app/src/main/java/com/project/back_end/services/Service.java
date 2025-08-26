package com.project.back_end.services;

/*
  INSTRUCTIONS (kept as requested):
  1. Mark as @Service and use constructor injection for TokenService, Repositories, and other Services.
  2. validateToken: delegate to TokenService for role-based validation.
  3. validateAdmin: verify credentials; on success generate token; return appropriate HTTP codes.
  4. filterDoctor: support flexible filters (name, time, speciality) delegating to DoctorService for time logic.
  5. validateAppointment: ensure doctor exists and chosen time is available.
  6. validatePatient: ensure uniqueness by email/phone.
  7. validatePatientLogin: validate credentials and return token.
  8. filterPatient: extract patient from token and return filtered appointments via PatientService.
*/

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@org.springframework.stereotype.Service
public class Service {

        private final TokenService tokenService;
        private final AdminRepository adminRepository;
        private final DoctorRepository doctorRepository;
        private final PatientRepository patientRepository;
        private final AppointmentRepository appointmentRepository;
        private final DoctorService doctorService;
        private final PatientService patientService;

        public Service(TokenService tokenService,
                       AdminRepository adminRepository,
                       DoctorRepository doctorRepository,
                       PatientRepository patientRepository,
                       AppointmentRepository appointmentRepository,
                       DoctorService doctorService,
                       PatientService patientService) {
                this.tokenService = tokenService;
                this.adminRepository = adminRepository;
                this.doctorRepository = doctorRepository;
                this.patientRepository = patientRepository;
                this.appointmentRepository = appointmentRepository;
                this.doctorService = doctorService;
                this.patientService = patientService;
        }

        public boolean validateToken(String token, String role) {
                return tokenService.validateToken(token, role);
        }

        public ResponseEntity<Map<String, Object>> validateAdmin(Admin admin) {
                Map<String, Object> res = new HashMap<>();
                try {
                        if (admin == null || admin.getUsername() == null || admin.getPassword() == null) {
                                res.put("message", "Username and password are required");
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
                        }
                        Admin saved = adminRepository.findByUsername(admin.getUsername());
                        if (saved == null) {
                                res.put("message", "Invalid credentials");
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
                        }
                        if (!Objects.equals(saved.getPassword(), admin.getPassword())) {
                                res.put("message", "Invalid credentials");
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
                        }
                        String token = tokenService.generateToken(saved.getUsername());
                        res.put("token", token);
                        res.put("message", "Login successful");
                        return ResponseEntity.ok(res);
                } catch (Exception e) {
                        res.put("message", "Internal error");
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
                }
        }

        // name/time/speciality filter - delegates time filtering to DoctorService
        public Map<String, Object> filterDoctor(String name, String time, String speciality) {
                Map<String, Object> res = new HashMap<>();
                // Normalize inputs: treat literal "null"/"undefined" and blanks as empty
                name = normalizeFilterValue(name);
                time = normalizeFilterValue(time);
                speciality = normalizeFilterValue(speciality);

                List<Doctor> doctors;
                boolean hasName = name != null && !name.isBlank();
                boolean hasSpec = speciality != null && !speciality.isBlank();
                boolean hasTime = time != null && !time.isBlank();

                if (!hasName && !hasSpec && !hasTime) {
                        doctors = doctorRepository.findAll();
                } else if (hasName && hasSpec && hasTime) {
                        doctors = doctorService.filterDoctorsByNameSpecilityandTime(name, speciality, time);
                } else if (hasName && hasSpec) {
                        doctors = doctorService.filterDoctorByNameAndSpecility(name, speciality);
                } else if (hasName && hasTime) {
                        doctors = doctorService.filterDoctorByNameAndTime(name, time);
                } else if (hasSpec && hasTime) {
                        doctors = doctorService.filterDoctorByTimeAndSpecility(speciality, time);
                } else if (hasName) {
                        doctors = doctorService.findDoctorByName(name);
                } else if (hasSpec) {
                        doctors = doctorService.filterDoctorBySpecility(speciality);
                } else { // only time
                        doctors = doctorService.filterDoctorsByTime(time);
                }
                res.put("doctors", doctors);
                return res;
        }

        private String normalizeFilterValue(String v) {
                if (v == null) return null;
                String trimmed = v.trim();
                if (trimmed.isEmpty()) return null;
                if ("null".equalsIgnoreCase(trimmed) || "undefined".equalsIgnoreCase(trimmed)) return null;
                return trimmed;
        }

        // Returns: 1 valid, 0 invalid time, -1 doctor not found
        public int validateAppointment(Long doctorId, LocalDate date, LocalTime time) {
                var doctorOpt = doctorRepository.findById(doctorId);
                if (doctorOpt.isEmpty()) return -1;
                var available = doctorService.getDoctorAvailabilitySlots(doctorId, date);
                boolean ok = available.stream().anyMatch(t -> t.equals(time));
                return ok ? 1 : 0;
        }

        public boolean validatePatient(Patient patient) {
                if (patient == null) return false;
                return patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone()) == null;
        }

        public ResponseEntity<Map<String, Object>> validatePatientLogin(Login login) {
                Map<String, Object> res = new HashMap<>();
                try {
                        if (login == null || login.getEmail() == null || login.getPassword() == null) {
                                res.put("message", "Email and password are required");
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
                        }
                        var patient = patientRepository.findByEmail(login.getEmail());
                        if (patient == null || !Objects.equals(patient.getPassword(), login.getPassword())) {
                                res.put("message", "Invalid credentials");
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
                        }
                        String token = tokenService.generateToken(patient.getEmail());
                        res.put("token", token);
                        res.put("message", "Login successful");
                        return ResponseEntity.ok(res);
                } catch (Exception e) {
                        res.put("message", "Internal error");
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
                }
        }

        public Map<String, Object> filterPatient(String condition, String doctorName, String token) {
                Map<String, Object> res = new HashMap<>();
                if (!validateToken(token, "patient")) {
                        res.put("message", "Invalid token");
                        res.put("appointments", List.of());
                        return res;
                }
                String email = tokenService.extractEmail(token);
                var patient = patientRepository.findByEmail(email);
                if (patient == null) {
                        res.put("appointments", List.of());
                        return res;
                }
                var list = patientService.getPatientAppointmentsInternal(patient.getId(), condition, doctorName);
                res.put("appointments", list);
                return res;
        }
}
