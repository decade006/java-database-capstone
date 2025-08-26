package com.project.back_end.services;

/*
  INSTRUCTIONS (kept as requested):
  1. Mark as @Service; inject PatientRepository, AppointmentRepository, TokenService via constructor.
  2. createPatient: persist patient and return status code (1 success, 0 error).
  3. getPatientAppointment/filterByCondition/filterByDoctor/filterByDoctorAndCondition: read-only queries returning AppointmentDTOs.
  4. getPatientDetails: extract email from token and fetch patient.
  5. getPatientAppointmentsInternal: helper that chooses the proper filter combination.
  6. Use DTOs to shape response and avoid exposing sensitive data.
*/

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public PatientService(PatientRepository patientRepository,
                          AppointmentRepository appointmentRepository,
                          TokenService tokenService) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional(readOnly = true)
    public List<AppointmentDTO> getPatientAppointment(Long patientId) {
        var list = appointmentRepository.findByPatientId(patientId);
        return toDTOs(list);
    }

    @Transactional(readOnly = true)
    public List<AppointmentDTO> filterByCondition(Long patientId, String condition) {
        int status = "past".equalsIgnoreCase(condition) ? 1 : 0;
        var list = appointmentRepository.findByPatientIdAndStatusOrderByAppointmentTimeAsc(patientId, status);
        return toDTOs(list);
    }

    @Transactional(readOnly = true)
    public List<AppointmentDTO> filterByDoctor(Long patientId, String doctorName) {
        var list = appointmentRepository.findByDoctorNameContainingIgnoreCaseAndPatientId(doctorName, patientId);
        return toDTOs(list);
    }

    @Transactional(readOnly = true)
    public List<AppointmentDTO> filterByDoctorAndCondition(Long patientId, String doctorName, String condition) {
        int status = "past".equalsIgnoreCase(condition) ? 1 : 0;
        var list = appointmentRepository.findByDoctorNameContainingIgnoreCaseAndPatientIdAndStatus(doctorName, patientId, status);
        return toDTOs(list);
    }

    public Patient getPatientDetails(String token) {
        String email = tokenService.extractEmail(token);
        return patientRepository.findByEmail(email);
    }

    // Helper used by shared Service.filterPatient
    @Transactional(readOnly = true)
    public List<AppointmentDTO> getPatientAppointmentsInternal(Long patientId, String condition, String doctorName) {
        boolean hasCond = condition != null && !condition.isBlank();
        boolean hasDoc = doctorName != null && !doctorName.isBlank();
        if (hasCond && hasDoc) return filterByDoctorAndCondition(patientId, doctorName, condition);
        if (hasCond) return filterByCondition(patientId, condition);
        if (hasDoc) return filterByDoctor(patientId, doctorName);
        return getPatientAppointment(patientId);
    }

    private List<AppointmentDTO> toDTOs(List<Appointment> list) {
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
}
