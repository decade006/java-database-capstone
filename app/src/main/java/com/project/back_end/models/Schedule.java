package com.project.back_end.models;

import jakarta.persistence.Embeddable;

import java.time.LocalTime;

@Embeddable
public class Schedule {

        private LocalTime startTime;
        private LocalTime endTime;

        public Schedule() {
        }

        public LocalTime getStartTime() {
                return startTime;
        }

        public void setStartTime(LocalTime startTime) {
                this.startTime = startTime;
        }

        public LocalTime getEndTime() {
                return endTime;
        }

        public void setEndTime(LocalTime endTime) {
                this.endTime = endTime;
        }
}
