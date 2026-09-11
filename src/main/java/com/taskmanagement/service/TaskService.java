package com.taskmanagement.service;

import com.taskmanagement.dto.request.TaskCreateRequest;
import com.taskmanagement.dto.request.TaskUpdateRequest;
import com.taskmanagement.dto.response.TaskResponse;
import com.taskmanagement.dto.response.TaskStatsResponse;
import com.taskmanagement.entity.TaskPriority;
import com.taskmanagement.entity.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface TaskService {
    TaskResponse createTask(TaskCreateRequest request);
    TaskResponse getTaskById(Long id);
    Page<TaskResponse> getAllTasks(TaskStatus status,
                                   TaskPriority priority,
                                   Long categoryId,
                                   Long assigneeId,
                                   String search,
                                   LocalDate dueDateFrom,
                                   LocalDate dueDateTo,
                                   Pageable pageable);
    TaskResponse updateTask(Long id, TaskUpdateRequest request);
    TaskResponse updateTaskStatus(Long id, TaskStatus newStatus);
    void deleteTask(Long id);
    TaskStatsResponse getTaskStats();
}
