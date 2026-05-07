package com.taskmanager.task_api.service;

import com.taskmanager.task_api.dto.TaskResponse;
import com.taskmanager.task_api.entity.Task;

import java.util.List;

public interface TaskService {
    List<TaskResponse> getAllTasks();

    TaskResponse getTaskById(Long id);

    TaskResponse createTask(Task task);

    void deleteById(Long id);

    TaskResponse patchTask(Task patchedTask, Long id);

    TaskResponse updateTask(Task taskObject, Long id);
}
