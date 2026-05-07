package com.taskmanager.task_api.service.impl;

import com.taskmanager.task_api.dto.TaskResponse;
import com.taskmanager.task_api.entity.Task;
import com.taskmanager.task_api.service.TaskService;
import com.taskmanager.task_api.util.TaskMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Profile("dev")
public class StaticDataImpl implements TaskService {

    private final Map<Long, Task> tasks;
    private Long currentId;

    public StaticDataImpl() {
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
    public List<TaskResponse> getAllTasks() {
        return new ArrayList<>(this.tasks.values()
                .stream()
                .map(TaskMapper::mapToTaskResponse)
                .toList());
    }

    @Override
    public TaskResponse getTaskById(Long id) {
        if (this.tasks.containsKey(id)) {
            Task task = getTask(id);
            return TaskMapper.mapToTaskResponse(this.tasks.get(id));
        }
        return null;
    }

    @Override
    public TaskResponse createTask(Task task) {
        this.tasks.put(this.currentId + 1L, task);
        currentId += 1L;
        return TaskMapper.mapToTaskResponse(task);
    }

    @Override
    public void deleteById(Long id) {
        this.tasks.remove(id);
    }

    @Override
    public TaskResponse patchTask(Task patchedTask, Long id) {
        Task taskEntity = this.getTask(id);
        TaskMapper.copyNonNullData(taskEntity, patchedTask);
        this.tasks.put(id, taskEntity);
        return TaskMapper.mapToTaskResponse(taskEntity);
    }

    @Override
    public TaskResponse updateTask(Task taskObject, Long id) {
        Task taskEntity = this.getTask(id);
        TaskMapper.copyAllData(taskEntity, taskObject);
        this.tasks.put(id, taskEntity);
        return TaskMapper.mapToTaskResponse(taskEntity);
    }

    private Task getTask(Long id) {
        return this.tasks.get(id);
    }
}
