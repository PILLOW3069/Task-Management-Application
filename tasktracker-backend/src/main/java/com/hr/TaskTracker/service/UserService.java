package com.hr.TaskTracker.service;

import com.hr.TaskTracker.model.User;
import com.hr.TaskTracker.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository repo) {
        this.userRepository = repo;
    }

    // Only allow admins to list all users (if needed)
    public List<User> getAllUsers() {
        // Implement admin check here if needed
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!user.getUsername().equals(currentUsername)) {
            throw new AccessDeniedException("You are not authorized to access this resource");
        }

        return user;
    }

    public User updateUser(Long id, User updatedUser) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!existingUser.getUsername().equals(currentUsername)) {
            throw new AccessDeniedException("You can only update your own profile");
        }

        // Update only allowed fields
        existingUser.setEmail(updatedUser.getEmail());
        // Consider adding password encoder here if updating password
        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!user.getUsername().equals(currentUsername)) {
            throw new AccessDeniedException("You can only delete your own account");
        }

        userRepository.delete(user);
    }

    public List<User> searchUsers(String query) {
        return userRepository.searchByUsername(query);
    }

}
