package com.hr.TaskTracker.controller;

import com.hr.TaskTracker.model.User;
import com.hr.TaskTracker.repository.UserRepository;
import com.hr.TaskTracker.security.JwtUtil;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "User registered successfully!";
    }

    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> loginData) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginData.get("username"),
                        loginData.get("password")
                )
        );

        User user = userRepository.findByUsername(loginData.get("username"))
                .orElseThrow(() -> new RuntimeException("User not found"));

        return jwtUtil.generateToken(user.getUser_id());
    }
}
