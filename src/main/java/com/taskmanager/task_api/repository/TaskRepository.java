package com.taskmanager.task_api.repository;

import com.taskmanager.task_api.entity.AppUser;
import com.taskmanager.task_api.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssignedUser(AppUser appUser);
}
