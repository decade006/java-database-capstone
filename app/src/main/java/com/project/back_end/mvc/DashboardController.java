package com.project.back_end.mvc;

import com.project.back_end.services.TokenService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DashboardController {

    private final TokenService tokenService;

    public DashboardController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    // Clean URL (recommended). If a token query param is present, validate it.
    @GetMapping("/adminDashboard")
    public String adminDashboard(@RequestParam(value = "token", required = false) String token) {
        if (token != null && !token.isBlank()) {
            if (!tokenService.validateToken(token, "admin")) {
                return "redirect:/";
            }
        }
        return "admin/adminDashboard";
    }

    // Backward compatible route where token is part of the URL
    @GetMapping("/adminDashboard/{token}")
    public String adminDashboardWithToken(@PathVariable("token") String token) {
        return tokenService.validateToken(token, "admin") ? "admin/adminDashboard" : "redirect:/";
    }

    @GetMapping("/doctorDashboard")
    public String doctorDashboard(@RequestParam(value = "token", required = false) String token) {
        if (token != null && !token.isBlank()) {
            if (!tokenService.validateToken(token, "doctor")) {
                return "redirect:/";
            }
        }
        return "doctor/doctorDashboard";
    }

    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboardWithToken(@PathVariable("token") String token) {
        return tokenService.validateToken(token, "doctor") ? "doctor/doctorDashboard" : "redirect:/";
    }
}
