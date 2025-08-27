package com.project.back_end.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "patient_feedback")
public class PatientFeedBack {

        public static final int ONE_STAR = 1;
        public static final int TWO_STAR = 2;
        public static final int THREE_STAR = 3;
        public static final int FOUR_STAR = 4;
        public static final int FIVE_STAR = 5;
        @Id
        private String id;

        private Long appointmentId;
        private Integer rating;
        private String comment;
        private LocalDateTime createdAt;

        public PatientFeedBack() {
        }

        public String getId() {
                return id;
        }

        public void setId(String id) {
                this.id = id;
        }

        public Long getAppointmentId() {
                return appointmentId;
        }

        public void setAppointmentId(Long appointmentId) {
                this.appointmentId = appointmentId;
        }

        public Integer getRating() {
                return rating;
        }

        public void setRating(Integer rating) {
                this.rating = rating;
        }

        public String getComment() {
                return comment;
        }

        public void setComment(String comment) {
                this.comment = comment;
        }

        public LocalDateTime getCreatedAt() {
                return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
                this.createdAt = createdAt;
        }
}
