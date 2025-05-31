package com.hr.TaskTracker.controller;

import com.hr.TaskTracker.model.Task;
import com.hr.TaskTracker.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(HttpServletRequest request) {
        return ResponseEntity.ok(taskService.getUserTasks(request));
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task, HttpServletRequest request) {
        return ResponseEntity.ok(taskService.createTask(task, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
            @PathVariable Long id,
            @RequestBody Task task,
            HttpServletRequest request) {
        return ResponseEntity.ok(taskService.updateTask(id, task, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, HttpServletRequest request) {
        taskService.deleteTask(id, request);
        return ResponseEntity.noContent().build();
    }
}
