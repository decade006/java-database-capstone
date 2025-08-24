package com.project.back_end.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "messages")
public class Message {
        @Id
        private String id;

        private Long patientId;
        private Long doctorId;
        private String message;
        private LocalDateTime timestamp;

        public Message() {
        }

        public Message(Long patientId, Long doctorId, String message) {
                this.patientId = patientId;
                this.doctorId = doctorId;
                this.message = message;
                this.timestamp = LocalDateTime.now();
        }

        public String getId() {
                return id;
        }

        public void setId(String id) {
                this.id = id;
        }

        public Long getPatientId() {
                return patientId;
        }

        public void setPatientId(Long patientId) {
                this.patientId = patientId;
        }

        public Long getDoctorId() {
                return doctorId;
        }

        public void setDoctorId(Long doctorId) {
                this.doctorId = doctorId;
        }

        public String getMessage() {
                return message;
        }

        public void setMessage(String message) {
                this.message = message;
        }

        public LocalDateTime getTimestamp() {
                return timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
                this.timestamp = timestamp;
        }
}
