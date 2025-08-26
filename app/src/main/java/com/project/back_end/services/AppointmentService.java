package com.project.back_end.services;

/*
  INSTRUCTIONS (kept as requested):
  1. Mark as @Service; inject AppointmentRepository, Service (shared), TokenService, PatientRepository, DoctorRepository.
  2. Annotate DB-modifying methods with @Transactional.
  3. bookAppointment: persist and return 1 on success, 0 on failure.
  4. updateAppointment: validate ownership, slot availability (via shared service), then update; return message map.
  5. cancelAppointment: ensure patient owns appointment and delete.
  6. getAppointments: read-only query for a doctor's daily appointments, optional patient-name filter, map to DTOs.
  7. changeStatus: update appointment status in a transaction.
*/

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final Service sharedService;
    private final TokenService tokenService;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              Service sharedService,
                              TokenService tokenService,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.sharedService = sharedService;
        this.tokenService = tokenService;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public Map<String, Object> updateAppointment(Appointment input) {
        Map<String, Object> res = new HashMap<>();
        Optional<Appointment> opt = appointmentRepository.findById(input.getId());
        if (opt.isEmpty()) {
            res.put("message", "Appointment not found");
            return res;
        }
        Appointment existing = opt.get();
        if (!existing.getPatient().getId().equals(input.getPatient().getId())) {
            res.put("message", "Unauthorized update request");
            return res;
        }
        // basic validation: timeslot
        LocalDate date = input.getAppointmentTime().toLocalDate();
        LocalTime time = input.getAppointmentTime().toLocalTime();
        int ok = sharedService.validateAppointment(input.getDoctor().getId(), date, time);
        if (ok != 1) {
            res.put("message", ok == -1 ? "Doctor not found" : "Selected slot not available");
            return res;
        }
        existing.setAppointmentTime(input.getAppointmentTime());
        existing.setDoctor(input.getDoctor());
        existing.setStatus(input.getStatus());
        appointmentRepository.save(existing);
        res.put("message", "Appointment updated successfully");
        return res;
    }

    @Transactional
    public boolean cancelAppointment(Long appointmentId, Long patientId) {
        Optional<Appointment> opt = appointmentRepository.findById(appointmentId);
        if (opt.isEmpty()) return false;
        if (!opt.get().getPatient().getId().equals(patientId)) return false;
        appointmentRepository.deleteById(appointmentId);
        return true;
    }

    @Transactional(readOnly = true)
    public List<AppointmentDTO> getAppointments(Long doctorId, LocalDate date, String patientName) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);
        List<Appointment> list;
        if (patientName == null || patientName.isBlank() || "null".equalsIgnoreCase(patientName)) {
            list = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);
        } else {
            list = appointmentRepository.findByDoctorIdAndPatientNameContainingIgnoreCaseAndAppointmentTimeBetween(doctorId, patientName, start, end);
        }
        return list.stream().map(a -> new AppointmentDTO(
                a.getId(),
                a.getDoctor().getId(),
                a.getDoctor().getName(),
                a.getPatient().getId(),
                a.getPatient().getName(),
                a.getPatient().getEmail(),
                a.getPatient().getPhone(),
                a.getPatient().getAddress(),
                a.getAppointmentTime(),
                a.getStatus()
        )).collect(Collectors.toList());
    }

    @Transactional
    public void changeStatus(long id, int status) {
        appointmentRepository.updateStatus(status, id);
    }
}
