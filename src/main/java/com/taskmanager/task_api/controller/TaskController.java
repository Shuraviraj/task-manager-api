package com.taskmanager.task_api.controller;

import com.taskmanager.task_api.entity.Task;
import com.taskmanager.task_api.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Task> getAllTasks() {
        return this.taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Task getTaskById(@PathVariable("id") Long id) {
        return this.taskService.getTaskById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task createTask(@Valid @RequestBody Task task) {
        return this.taskService.saveTasks(task);
    }

    @DeleteMapping({"/{id}"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTaskById(@PathVariable("id") Long id) {
        this.taskService.deleteById(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Task> patchTask(@Valid @RequestBody Task patchedTask, @PathVariable("id") Long id) {
        Task patchTask = this.taskService.patchTask(patchedTask, id);
        if (patchTask == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(patchTask, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Task updateTask(@Valid @RequestBody Task taskObject, @PathVariable("id") Long id) {
        return this.taskService.updateTask(taskObject, id);
    }
}
