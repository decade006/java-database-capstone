package com.project.back_end.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;

@Entity
public class Schedule {

        public static final int MONDAY = 1;
        public static final int TUESDAY = 2;
        public static final int WEDNESDAY = 3;
        public static final int THURSDAY = 4;
        public static final int FRIDAY = 5;
        public static final int SATURDAY = 6;
        public static final int SUNDAY = 7;

        @Id
        @GeneratedValue
        private Long id;

        private Integer dateOfWeek = MONDAY;
        private LocalDateTime startTime;
        private LocalDateTime endTime;

        @ManyToOne
        private Doctor doctor;

        public Schedule() {
        }

        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }


        public Doctor getDoctor() {
                return doctor;
        }

        public void setDoctor(Doctor doctor) {
                this.doctor = doctor;
        }
}
