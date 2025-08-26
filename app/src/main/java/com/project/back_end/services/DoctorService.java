package com.project.back_end.services;

/*
  INSTRUCTIONS (kept as requested):
  1. Mark as @Service; inject DoctorRepository, AppointmentRepository, TokenService via constructor.
  2. Use @Transactional(readOnly = true) on read queries.
  3. getDoctorAvailabilitySlots: compute available hourly slots for a date excluding booked ones.
  4. saveDoctor/updateDoctor/deleteDoctor: handle existence checks and return codes (-1 not found/exist conflict, 0 error, 1 success).
  5. validateDoctor: simple credential validation.
  6. Filtering helpers: support combinations of name/time/speciality; isolate AM/PM logic.
*/

import com.project.back_end.models.Doctor;
import com.project.back_end.models.Schedule;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public DoctorService(DoctorRepository doctorRepository,
                         AppointmentRepository appointmentRepository,
                         TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    @Transactional(readOnly = true)
    public List<LocalTime> getDoctorAvailabilitySlots(Long doctorId, LocalDate date) {
        var doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) return List.of();
        Doctor doctor = doctorOpt.get();

        // Collect booked start times for that date
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);
        var booked = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end)
                .stream().map(a -> a.getAppointmentTime().toLocalTime()).collect(Collectors.toSet());

        Set<LocalTime> available = new HashSet<>();
        if (doctor.getAvailableTimes() != null) {
            for (Schedule s : doctor.getAvailableTimes()) {
                if (s == null || s.getStartTime() == null || s.getEndTime() == null) continue;
                LocalTime t = s.getStartTime();
                while (t.isBefore(s.getEndTime())) {
                    if (!booked.contains(t)) available.add(t);
                    t = t.plusHours(1);
                }
            }
        }
        return new ArrayList<>(available).stream().sorted().collect(Collectors.toList());
    }

    public int saveDoctor(Doctor doctor) {
        try {
            if (doctorRepository.findByEmail(doctor.getEmail()) != null) return -1;
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public int updateDoctor(Doctor doctor) {
        try {
            if (doctor.getId() == null || !doctorRepository.existsById(doctor.getId())) return -1;
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional(readOnly = true)
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    public int deleteDoctor(Long id) {
        try {
            if (!doctorRepository.existsById(id)) return -1;
            appointmentRepository.deleteAllByDoctorId(id);
            doctorRepository.deleteById(id);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public Doctor validateDoctor(String email, String password) {
        Doctor d = doctorRepository.findByEmail(email);
        if (d == null) return null;
        if (!d.getPassword().equals(password)) return null;
        return d;
    }

    @Transactional(readOnly = true)
    public List<Doctor> findDoctorByName(String name) {
        return doctorRepository.findByNameLike(name);
    }

    // Filtering helpers
    private boolean matchesTime(Doctor d, String time) {
        if (d.getAvailableTimes() == null) return false;
        boolean am = "AM".equalsIgnoreCase(time);
        boolean pm = "PM".equalsIgnoreCase(time);
        for (Schedule s : d.getAvailableTimes()) {
            if (s == null || s.getStartTime() == null || s.getEndTime() == null) continue;
            LocalTime st = s.getStartTime();
            LocalTime et = s.getEndTime();
            if (am && st.isBefore(LocalTime.NOON)) return true;
            if (pm && et.isAfter(LocalTime.NOON)) return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorsByNameSpecilityandTime(String name, String speciality, String time) {
        return doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, speciality)
                .stream().filter(d -> matchesTime(d, time)).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorByTime(List<Doctor> doctors, String time) {
        return doctors.stream().filter(d -> matchesTime(d, time)).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorByNameAndTime(String name, String time) {
        return filterDoctorByTime(findDoctorByName(name), time);
    }

    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorByNameAndSpecility(String name, String speciality) {
        return doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, speciality);
    }

    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorByTimeAndSpecility(String speciality, String time) {
        return filterDoctorByTime(doctorRepository.findBySpecialtyIgnoreCase(speciality), time);
    }

    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorBySpecility(String speciality) {
        return doctorRepository.findBySpecialtyIgnoreCase(speciality);
    }

    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorsByTime(String time) {
        return filterDoctorByTime(doctorRepository.findAll(), time);
    }
}
