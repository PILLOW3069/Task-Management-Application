package com.hr.TaskTracker.controller;

import com.hr.TaskTracker.model.User;
import com.hr.TaskTracker.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService service) {
        this.userService = service;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User user = userService.getUserById(id); // will throw if not found or not authorized
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted");
    }

    // UserController.java
    @GetMapping("/search")
    public ResponseEntity<List<UserSearchResponse>> searchUsers(@RequestParam("query") String query) {
        List<User> users = userService.searchUsers(query);
        List<UserSearchResponse> results = users.stream()
                .map(u -> new UserSearchResponse(u.getUser_id(), u.getUsername()))
                .toList();
        return ResponseEntity.ok(results);
    }

    public record UserSearchResponse(Long user_id, String username) {}

}
