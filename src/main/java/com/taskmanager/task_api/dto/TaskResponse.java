package com.taskmanager.task_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Getter
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private String status;
    private String assignedTo; // just the username, nothing else
}
