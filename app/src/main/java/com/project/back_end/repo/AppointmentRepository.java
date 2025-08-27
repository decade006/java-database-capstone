package com.project.back_end.repo;

import com.project.back_end.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

        List<Appointment> findByDoctorIdAndAppointmentTimeBetween(Long doctorId, LocalDateTime start, LocalDateTime end);

        List<Appointment> findByDoctorIdAndPatientNameContainingIgnoreCaseAndAppointmentTimeBetween(Long doctorId, String patientName, LocalDateTime start, LocalDateTime end);

        @Transactional
        void deleteAllByDoctorId(Long doctorId);

        List<Appointment> findByPatientId(Long patientId);

        List<Appointment> findByPatientIdAndStatusOrderByAppointmentTimeAsc(Long patientId, int status);

        List<Appointment> findByDoctorNameContainingIgnoreCaseAndPatientId(String doctorName, Long patientId);

        List<Appointment> findByDoctorNameContainingIgnoreCaseAndPatientIdAndStatus(String doctorName, Long patientId, int status);

        @Modifying
        @Transactional
        @Query("update Appointment a set a.status = :status where a.id = :id")
        void updateStatus(@Param("status") int status, @Param("id") long id);
}
