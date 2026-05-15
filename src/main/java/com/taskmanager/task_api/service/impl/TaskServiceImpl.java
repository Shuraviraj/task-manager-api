package com.taskmanager.task_api.service.impl;

import com.taskmanager.task_api.dto.TaskResponse;
import com.taskmanager.task_api.entity.AppUser;
import com.taskmanager.task_api.entity.Task;
import com.taskmanager.task_api.repository.TaskRepository;
import com.taskmanager.task_api.repository.UserRepository;
import com.taskmanager.task_api.service.TaskService;
import com.taskmanager.task_api.util.TaskMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Primary
public class TaskServiceImpl implements TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository) {
        // INFO is too noisy for bean creation. DEBUG is better, or just omit it entirely.
        log.debug("Initializing TaskService");
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Page<TaskResponse> getAllTasks(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TaskResponse> response;

        if (isUserAdmin()) {
            response = taskRepository.findAll(pageable)
                    .map(TaskMapper::mapToTaskResponse);
            log.info("Admin user fetching all tasks, page: {}, size: {}", page, size);
        } else {

            String username = getLoggedInUserName();
            AppUser appUser = findByUserNameUserRepo(username);

            response = taskRepository.findByAssignedUser(appUser, pageable)
                    .map(TaskMapper::mapToTaskResponse);
            log.info("Fetching tasks for user: {}, page: {}, size: {}", username, page, size);
        }
        return response;
    }

    @Override
    public TaskResponse getTaskById(Long id) {
        Task task = this.getTaskEntityById(id);
        log.info("Fetched task ID: {} for user: {}", task.getTitle(), getLoggedInUserName());
        return TaskMapper.mapToTaskResponse(task);
    }

    @Override
    public TaskResponse createTask(Task task) {
        String username = getLoggedInUserName();
        AppUser user = findByUserNameUserRepo(username);

        task.setAssignedUser(user);
        Task savedEntity = taskRepository.save(task);

        // Log the ID, not the whole entity
        log.info("Task created with ID: {} by user: {}", savedEntity.getTitle(), username);
        return TaskMapper.mapToTaskResponse(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        Task task = getTaskEntityById(id);
        String username = getLoggedInUserName();

        // Context is key: WHO deleted WHAT
        log.info("Task ID: {} deleted by user: {}", task.getTitle(), username);
        this.taskRepository.deleteById(task.getId());
    }

    @Override
    public TaskResponse patchTask(Task patchedTask, Long id) {
        Task taskEntity = this.getTaskEntityById(id);
        TaskMapper.copyNonNullData(taskEntity, patchedTask);

        log.info("Patching task ID: {} by user: {}", id, getLoggedInUserName());
        return TaskMapper.mapToTaskResponse(taskRepository.save(taskEntity));
    }

    @Override
    public TaskResponse updateTask(Task taskObject, Long id) {
        Task taskEntity = this.getTaskEntityById(id);
        TaskMapper.copyAllData(taskEntity, taskObject);

        log.info("Updating task ID: {} by user: {}", id, getLoggedInUserName());
        return TaskMapper.mapToTaskResponse(taskRepository.save(taskEntity));
    }

    private Task getTaskEntityById(Long id) {
        return this.taskRepository.findById(id).orElseThrow(() -> {
            // WARN instead of ERROR for client-side mistakes (404)
            log.warn("Task lookup failed. ID {} not found", id);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Task with id " + id + " not found");
        });
    }

    private AppUser findByUserNameUserRepo(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> {
            log.warn("User lookup failed. Username {} not found", username);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Username " + username + " not found");
        });
    }

    private String getLoggedInUserName() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private boolean isUserAdmin() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}