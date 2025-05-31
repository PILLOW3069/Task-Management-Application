package com.hr.TaskTracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.hr.TaskTracker.model.Task;
import com.hr.TaskTracker.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("SELECT t FROM Task t WHERE t.user = :user OR :user MEMBER OF t.collaborators")
    List<Task> findAllByUserOrCollaborator(@Param("user") User user);


}

