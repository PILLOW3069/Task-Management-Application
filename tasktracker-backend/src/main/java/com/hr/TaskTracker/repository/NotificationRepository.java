package com.hr.TaskTracker.repository;

import com.hr.TaskTracker.model.Notification;
import com.hr.TaskTracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.hr.TaskTracker.model.Task;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserAndIsReadFalse(User user);
    List<Notification> findByTask( Task task);
}
