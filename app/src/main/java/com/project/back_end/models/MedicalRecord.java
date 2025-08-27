package com.project.back_end.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class MedicalRecord {

        @Id
        @GeneratedValue
        private Long id;

        @ManyToOne
        private Patient patient;

        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }


}
