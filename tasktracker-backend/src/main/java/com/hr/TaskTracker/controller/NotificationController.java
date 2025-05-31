package com.hr.TaskTracker.controller;

import com.hr.TaskTracker.model.Notification;
import com.hr.TaskTracker.model.User;
import com.hr.TaskTracker.repository.UserRepository;
import com.hr.TaskTracker.security.JwtUtil;
import com.hr.TaskTracker.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // Helper to extract user from JWT in request
    private User getUserFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Long userId = jwtUtil.extractUserId(token);
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Get all notifications for the current user
    @GetMapping
    public List<Notification> getUserNotifications(HttpServletRequest request) {
        User user = getUserFromRequest(request);
        return notificationService.getUserNotifications(user);
    }

    // Mark a notification as read
    @PostMapping("/{id}/read")
    public void markAsRead(@PathVariable Long id, HttpServletRequest request) {
        User user = getUserFromRequest(request);
        notificationService.markAsRead(id, user);
    }
}
