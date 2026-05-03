package com.taskmanager.task_api.service.impl;

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
    public List<Task> getAllTasks() {
        return new ArrayList<>(this.tasks.values());
    }

    @Override
    public Task getTaskById(Long id) {
        if (this.tasks.containsKey(id)) {
            return this.tasks.get(id);
        }
        return null;
    }

    @Override
    public Task saveTasks(Task task) {
        this.tasks.put(this.currentId + 1L, task);
        currentId += 1L;
        return task;
    }

    @Override
    public void deleteById(Long id) {
        this.tasks.remove(id);
    }

    @Override
    public Task patchTask(Task patchedTask, Long id) {
        Task taskEntity = this.getTaskById(id);
        TaskMapper.copyNonNullData(taskEntity, patchedTask);
        this.tasks.put(id, taskEntity);
        return taskEntity;
    }

    @Override
    public Task updateTask(Task taskObject, Long id) {
        Task taskEntity = this.getTaskById(id);
        TaskMapper.copyAllData(taskEntity, taskObject);
        this.tasks.put(id, taskEntity);
        return taskEntity;
    }
}
