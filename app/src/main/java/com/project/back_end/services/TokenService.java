package com.project.back_end.services;

import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class TokenService {

        private final AdminRepository adminRepository;
        private final DoctorRepository doctorRepository;
        private final PatientRepository patientRepository;

        private final String jwtSecret;

        public TokenService(AdminRepository adminRepository,
                            DoctorRepository doctorRepository,
                            PatientRepository patientRepository,
                            @Value("${jwt.secret}") String jwtSecret) {
                this.adminRepository = adminRepository;
                this.doctorRepository = doctorRepository;
                this.patientRepository = patientRepository;
                this.jwtSecret = jwtSecret;
        }

        private SecretKey getSigningKey() {
                // Ensure key size is adequate for HMAC-SHA algorithms
                return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        }

        public String generateToken(String email) {
                Instant now = Instant.now();
                return Jwts.builder()
                        .subject(email)
                        .issuedAt(Date.from(now))
                        .expiration(Date.from(now.plus(7, ChronoUnit.DAYS)))
                        .signWith(getSigningKey())
                        .compact();
        }

        public String extractEmail(String token) {
                var claims = Jwts.parser()
                        .verifyWith(getSigningKey())
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();
                return claims.getSubject();
        }

        public boolean validateToken(String token, String role) {
                if (token == null) return false;
                try {
                        String subject = extractEmail(token);
                        if (subject == null || subject.isBlank()) return false;

                        if ("admin".equalsIgnoreCase(role)) {
                                // Admins are identified by username
                                return adminRepository.findByUsername(subject) != null;
                        } else if ("doctor".equalsIgnoreCase(role)) {
                                return doctorRepository.findByEmail(subject) != null;
                        } else if ("patient".equalsIgnoreCase(role)) {
                                return patientRepository.findByEmail(subject) != null;
                        } else {
                                // Unknown role
                                return false;
                        }
                } catch (Exception e) {
                        // Covers signature issues, token expiration, malformed token, etc.
                        return false;
                }
        }
}
