package com.taskmanager.task_api.service.impl;

import com.taskmanager.task_api.entity.Task;
import com.taskmanager.task_api.repository.TaskRepository;
import com.taskmanager.task_api.service.TaskService;
import com.taskmanager.task_api.util.TaskMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Profile("prod")
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public Task getTaskById(Long id) {
        return this.taskRepository.findById(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Task with id " + id + " not found"
        ));
    }

    @Override
    public Task saveTasks(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public void deleteById(Long id) {
        Long idToBeDeleted = getTaskById(id).getId();
        this.taskRepository.deleteById(idToBeDeleted);
    }

    @Override
    public Task patchTask(Task patchedTask, Long id) {
        Task taskEntity = this.getTaskById(id);
        TaskMapper.copyNonNullData(taskEntity, patchedTask);
        return taskRepository.save(taskEntity);
    }

    @Override
    public Task updateTask(Task taskObject, Long id) {
        Task taskEntity = this.getTaskById(id);
        TaskMapper.copyAllData(taskEntity, taskObject);
        return taskRepository.save(taskEntity);
    }
}
