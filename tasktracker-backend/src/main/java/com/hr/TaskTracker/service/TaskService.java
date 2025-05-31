package com.hr.TaskTracker.service;

import com.hr.TaskTracker.model.Task;
import com.hr.TaskTracker.model.User;
import com.hr.TaskTracker.repository.TaskRepository;
import com.hr.TaskTracker.repository.UserRepository;
import com.hr.TaskTracker.security.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hr.TaskTracker.repository.NotificationRepository;
import com.hr.TaskTracker.service.NotificationService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private NotificationService notificationService;

    private User getUserFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Long userId = jwtUtil.extractUserId(token);
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }


    public List<Task> getUserTasks(HttpServletRequest request) {
        User user = getUserFromRequest(request);
        return taskRepository.findAllByUserOrCollaborator(user);
    }

    public Task createTask(Task task, HttpServletRequest request) {
        User user = getUserFromRequest(request);
        task.setUser(user);
        if (task.getCollaborators() != null) {
            List<User> validCollaborators = task.getCollaborators().stream()
                    .map(collaborator -> userRepository.findById(collaborator.getUser_id())
                            .orElseThrow(() -> new EntityNotFoundException("User not found: " + collaborator.getUser_id())))
                    .collect(Collectors.toList());
            task.setCollaborators(validCollaborators);
        }
        Task savedTask = taskRepository.save(task);

        notificationService.scheduleNotification(savedTask); // NEW: Schedule notification
        return savedTask;

    }

    public Task updateTask(Long taskId, Task updatedTask, HttpServletRequest request) {
        User user = getUserFromRequest(request);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getUser().getUser_id().equals(user.getUser_id()) && !task.getCollaborators().contains(user)) {
            throw new RuntimeException("Unauthorized to update this task");
        }

        task.setTitle(updatedTask.getTitle());
        task.setDescription(updatedTask.getDescription());
        task.setStatus(updatedTask.getStatus());
        task.setDueDateTime(updatedTask.getDueDateTime());
        task.setStartDateTime(updatedTask.getStartDateTime());
        task.setPriority(updatedTask.getPriority());

        Task savedTask = taskRepository.save(task);
        notificationService.scheduleNotification(savedTask);
        return savedTask;

    }

    public void deleteTask(Long taskId, HttpServletRequest request) {
        User user = getUserFromRequest(request);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getUser().getUser_id().equals(user.getUser_id())) {
            throw new RuntimeException("Unauthorized to delete this task");
        }// delete dependent records first
        notificationService.deleteNotification(task);
        taskRepository.delete(task);
    }
}

