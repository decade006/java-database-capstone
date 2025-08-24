package com.project.back_end.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class Payment {

        public static final int PAID = 1;
        public static final int WAIT = 2;

        @Id
        @GeneratedValue
        private Long id;

        public void setId(Long id) {
                this.id = id;
        }

        public Long getId() {
                return id;
        }

        private Integer amount;
        private Integer status = WAIT;

        @OneToOne
        private Appointment appointment;

}
