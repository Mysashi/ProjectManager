package com.project.project.api;

import com.project.project.domain.dto.request.auth.LoginRequestDto;
import com.project.project.domain.dto.request.auth.RegisterRequestDto;
import com.project.project.domain.dto.response.RegisterResponseDto;
import com.project.project.domain.dto.response.UserResponseDto;
import com.project.project.domain.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    private final UserServiceImpl userService;

    @Autowired
    public AuthRestController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public RegisterResponseDto registerUser(@RequestBody RegisterRequestDto request) {
        return userService.register(request);
    }

    @PostMapping("/login")
    public UserResponseDto loginUser(@RequestBody LoginRequestDto request, HttpServletResponse response) {
        return userService.login(request, response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestBody Map<String, String> payload, HttpServletResponse response) {
        String requestToken = payload.get("refreshToken");
        return userService.logoutUser(requestToken, response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@AuthenticationPrincipal UserDetails userDetails,
                                          @RequestBody Map<String, String> payload) {
        return userService.refreshToken(payload, userDetails);
    }
}
