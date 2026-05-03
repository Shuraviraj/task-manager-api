package com.taskmanager.task_api.service;

import com.taskmanager.task_api.entity.Task;

import java.util.List;

public interface TaskService {
    List<Task> getAllTasks();

    Task getTaskById(Long id);

    Task saveTasks(Task task);

    void deleteById(Long id);

    Task patchTask(Task patchedTask, Long id);

    Task updateTask(Task taskObject, Long id);
}
