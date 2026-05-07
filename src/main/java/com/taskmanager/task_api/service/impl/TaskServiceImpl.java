package com.taskmanager.task_api.service.impl;

import com.taskmanager.task_api.dto.TaskResponse;
import com.taskmanager.task_api.entity.AppUser;
import com.taskmanager.task_api.entity.Task;
import com.taskmanager.task_api.repository.TaskRepository;
import com.taskmanager.task_api.repository.UserRepository;
import com.taskmanager.task_api.service.TaskService;
import com.taskmanager.task_api.util.TaskMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Profile("prod")
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    TaskServiceImpl(TaskRepository taskRepository,
                    UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<TaskResponse> getAllTasks() {
        AppUser appUser = findByUserNameUserRepo(getLoggedInUserName());
        return TaskMapper.mapToTaskResponse(taskRepository.findByAssignedUser(appUser));
    }

    @Override
    public TaskResponse getTaskById(Long id) {
        Task task = this.getTaskEntityById(id);
        return TaskMapper.mapToTaskResponse(task);
    }

    @Override
    public TaskResponse createTask(Task task) {
        // Get logged-in username from SecurityContextHolder and Find the user from DB
        AppUser user = findByUserNameUserRepo(getLoggedInUserName());

        // Assign user to task
        task.setAssignedUser(user);
        Task savedEntity = taskRepository.save(task);
        return TaskMapper.mapToTaskResponse(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        Long idToBeDeleted = getTaskById(id).getId();
        this.taskRepository.deleteById(idToBeDeleted);
    }

    @Override
    public TaskResponse patchTask(Task patchedTask, Long id) {
        Task taskEntity = this.getTaskEntityById(id);
        TaskMapper.copyNonNullData(taskEntity, patchedTask);
        return TaskMapper.mapToTaskResponse(taskRepository.save(taskEntity));
    }

    @Override
    public TaskResponse updateTask(Task taskObject, Long id) {
        Task taskEntity = this.getTaskEntityById(id);
        TaskMapper.copyAllData(taskEntity, taskObject);
        return TaskMapper.mapToTaskResponse(taskRepository.save(taskEntity));
    }

    private Task getTaskEntityById(Long id) {
        return this.taskRepository.findById(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Task with id " + id + " not found"
        ));
    }

    private AppUser findByUserNameUserRepo(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Username " + username + " not found")
                );
    }

    private String getLoggedInUserName() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }
}
