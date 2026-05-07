package com.taskmanager.task_api.util;

import com.taskmanager.task_api.dto.TaskResponse;
import com.taskmanager.task_api.entity.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskMapper {

    private TaskMapper() {}

    public static void copyNonNullData(Task destination, Task source) {
        if (source == null ||  destination == null) {
            return;
        }
        if (source.getTitle() != null) {
            destination.setTitle(source.getTitle());
        }
        if (source.getDescription() != null) {
            destination.setDescription(source.getDescription());
        }
        if (source.getStatus() != null) {
            destination.setStatus(source.getStatus());
        }
    }

    public static void copyAllData(Task destination, Task source) {
        if (source == null || destination == null) {return;}
        destination.setTitle(source.getTitle());
        destination.setDescription(source.getDescription());
        destination.setStatus(source.getStatus());
    }

    public static TaskResponse mapToTaskResponse(Task source) {
        return  TaskResponse.builder()
                .id(source.getId())
                .title(source.getTitle())
                .description(source.getDescription())
                .status(source.getStatus())
                .assignedTo(source.getAssignedUser().getUsername())
                .build();
    }

    public static List<TaskResponse> mapToTaskResponse(List<Task> byAssignedUser) {
        List<TaskResponse> taskResponses = new ArrayList<>();
        for (Task task : byAssignedUser) {
            taskResponses.add(mapToTaskResponse(task));
        }
        return taskResponses;
    }
}
