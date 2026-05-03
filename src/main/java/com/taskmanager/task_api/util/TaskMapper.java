package com.taskmanager.task_api.util;

import com.taskmanager.task_api.entity.Task;

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
}
