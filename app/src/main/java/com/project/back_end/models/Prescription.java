package com.project.back_end.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "prescriptions")
public class Prescription {

        @Id
        private String id;

        @NotNull
        @Size(min = 3, max = 100)
        private String patientName;

        @NotNull
        private Long appointmentId;


        private List<
                @NotNull
                @Size(min = 3, max = 100)
                        String
                > medications;

        @NotNull
        private String dosage;

        @Size(max = 200)
        private String doctorNotes;

        public Prescription() {
        }

        public Prescription(String patientName, List<String> medications, String dosage, String doctorNotes, Long appointmentId) {
                this.patientName = patientName;
                this.medications = medications;
                this.dosage = dosage;
                this.doctorNotes = doctorNotes;
                this.appointmentId = appointmentId;
        }

        public String getId() {
                return id;
        }

        public void setId(String id) {
                this.id = id;
        }

        public String getPatientName() {
                return patientName;
        }

        public void setPatientName(String patientName) {
                this.patientName = patientName;
        }

        public Long getAppointmentId() {
                return appointmentId;
        }

        public void setAppointmentId(Long appointmentId) {
                this.appointmentId = appointmentId;
        }

        public List<String> getMedications() {
                return medications;
        }

        public void setMedications(List<String> medications) {
                this.medications = medications;
        }

        public String getDosage() {
                return dosage;
        }

        public void setDosage(String dosage) {
                this.dosage = dosage;
        }

        public String getDoctorNotes() {
                return doctorNotes;
        }

        public void setDoctorNotes(String doctorNotes) {
                this.doctorNotes = doctorNotes;
        }
}
