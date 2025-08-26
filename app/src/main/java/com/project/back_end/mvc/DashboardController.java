package com.project.back_end.mvc;

import com.project.back_end.services.TokenService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class DashboardController {

        private final TokenService tokenService;

        public DashboardController(TokenService tokenService) {
                this.tokenService = tokenService;
        }

        // Backward compatible route where token is part of the URL
        @GetMapping("/adminDashboard/{token}")
        public String adminDashboardWithToken(@PathVariable("token") String token) {
                return tokenService.validateToken(token, "admin") ? "admin/adminDashboard" : "redirect:/";
        }

        @GetMapping("/doctorDashboard/{token}")
        public String doctorDashboardWithToken(@PathVariable("token") String token) {
                return tokenService.validateToken(token, "doctor") ? "doctor/doctorDashboard" : "redirect:/";
        }
}
