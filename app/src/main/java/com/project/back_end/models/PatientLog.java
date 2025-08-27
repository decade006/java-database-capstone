package com.project.back_end.models;

import jakarta.persistence.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "patient_logs")
public class PatientLog {

        public static final String CHECK_IN = "CHECK IN";
        public static final String CHECK_OUT = "CHECK OUT";
        public static final String CANCEL = "CANCEL";
        public static final String PAYMENT = "PAYMENT";

        @Id
        private String id;
        private String action;
        private Long patientId;
        private Long doctorId;
        private Long appointmentId;
        private LocalDateTime timestamp;
}
