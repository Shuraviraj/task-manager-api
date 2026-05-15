package com.taskmanager.task_api.service.impl;

import com.taskmanager.task_api.dto.TaskResponse;
import com.taskmanager.task_api.entity.Task;
import com.taskmanager.task_api.service.TaskService;
import com.taskmanager.task_api.util.TaskMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Profile("mock")
public class StaticDataImpl implements TaskService {

    private static final Logger log = LoggerFactory.getLogger(StaticDataImpl.class);
    private final Map<Long, Task> tasks;
    private Long currentId;

    public StaticDataImpl() {
        log.debug("Initializing StaticDataImpl with dev mock data");
        this.tasks = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            Task task = Task.builder().id(i + 1L)
                    .title((int) ((Math.random() * 1000) % 100) + " Sample Title")
                    .status((int) ((Math.random() * 1000) % 100) + " Sample Status")
                    .description((int) ((Math.random() * 1000) % 100) + " Sample description")
                    .build();
            this.tasks.put(task.getId(), task);
        }
        currentId = 10L;
    }

    @Override
    public Page<TaskResponse> getAllTasks(int page, int size) {
        log.info("Fetching dev mock tasks page: {}, size: {}", page, size);
        List<TaskResponse> allTasks = new ArrayList<>(this.tasks.values()
                .stream()
                .map(TaskMapper::mapToTaskResponse)
                .toList());

        int start = page * size;
        int end = Math.min(start + size, allTasks.size());

        List<TaskResponse> pageContent = allTasks.subList(start, end);
        return new PageImpl<>(pageContent, PageRequest.of(page, size), allTasks.size());
    }

    @Override
    public TaskResponse getTaskById(Long id) {
        if (this.tasks.containsKey(id)) {
            log.info("Fetched dev mock task ID: {}", id);
            return TaskMapper.mapToTaskResponse(this.tasks.get(id));
        }
        log.warn("Dev mock task lookup failed. ID {} not found", id);
        return null; // Note: In a real app, throw ResponseStatusException here like in prod
    }

    @Override
    public TaskResponse createTask(Task task) {
        currentId += 1L;
        task.setId(currentId); // Ensure the mock task actually gets the new ID
        this.tasks.put(currentId, task);
        log.info("Created dev mock task with ID: {}", currentId);
        return TaskMapper.mapToTaskResponse(task);
    }

    @Override
    public void deleteById(Long id) {
        if (this.tasks.remove(id) != null) {
            log.info("Deleted dev mock task ID: {}", id);
        } else {
            log.warn("Attempted to delete non-existent dev mock task ID: {}", id);
        }
    }

    @Override
    public TaskResponse patchTask(Task patchedTask, Long id) {
        Task taskEntity = this.getTask(id);
        if (taskEntity == null) {
            log.warn("Cannot patch. Dev mock task ID {} not found", id);
            return null;
        }
        TaskMapper.copyNonNullData(taskEntity, patchedTask);
        this.tasks.put(id, taskEntity);
        log.info("Patched dev mock task ID: {}", id);
        return TaskMapper.mapToTaskResponse(taskEntity);
    }

    @Override
    public TaskResponse updateTask(Task taskObject, Long id) {
        Task taskEntity = this.getTask(id);
        if (taskEntity == null) {
            log.warn("Cannot update. Dev mock task ID {} not found", id);
            return null;
        }
        TaskMapper.copyAllData(taskEntity, taskObject);
        this.tasks.put(id, taskEntity);
        log.info("Updated dev mock task ID: {}", id);
        return TaskMapper.mapToTaskResponse(taskEntity);
    }

    private Task getTask(Long id) {
        return this.tasks.get(id);
    }
}