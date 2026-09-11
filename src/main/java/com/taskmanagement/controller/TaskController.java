package com.taskmanagement.controller;

import com.taskmanagement.dto.request.TaskCreateRequest;
import com.taskmanagement.dto.request.TaskStatusUpdateRequest;
import com.taskmanagement.dto.request.TaskUpdateRequest;
import com.taskmanagement.dto.response.ApiResponse;
import com.taskmanagement.dto.response.TaskResponse;
import com.taskmanagement.dto.response.TaskStatsResponse;
import com.taskmanagement.entity.TaskPriority;
import com.taskmanagement.entity.TaskStatus;
import com.taskmanagement.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/tasks")
@Tag(name = "Task Management", description = "Endpoints for creating, retrieving, updating, deleting, and tracking tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @Operation(summary = "Create a new task", description = "Creates a new task with validation and assigns to user/category if provided")
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(@Valid @RequestBody TaskCreateRequest request) {
        TaskResponse response = taskService.createTask(request);
        return new ResponseEntity<>(ApiResponse.success("Task created successfully", response), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID", description = "Retrieves complete details of a task by its unique identifier")
    public ResponseEntity<ApiResponse<TaskResponse>> getTaskById(@PathVariable Long id) {
        TaskResponse response = taskService.getTaskById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "List and filter tasks", description = "Fetches a paginated list of tasks filtered by status, priority, category, assignee, date range, or keyword search")
    public ResponseEntity<ApiResponse<Page<TaskResponse>>> getAllTasks(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDateTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String sort) {

        String[] sortParts = sort.split(",");
        String sortProperty = sortParts[0];
        Sort.Direction direction = (sortParts.length > 1 && sortParts[1].equalsIgnoreCase("asc"))
                ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortProperty));
        Page<TaskResponse> tasks = taskService.getAllTasks(status, priority, categoryId, assigneeId, search, dueDateFrom, dueDateTo, pageable);
        return ResponseEntity.ok(ApiResponse.success("Tasks fetched successfully", tasks));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing task", description = "Updates all fields of an existing task")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskUpdateRequest request) {
        TaskResponse response = taskService.updateTask(id, request);
        return ResponseEntity.ok(ApiResponse.success("Task updated successfully", response));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update task status", description = "Updates the lifecycle status of a task (TODO, IN_PROGRESS, COMPLETED, CANCELLED)")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTaskStatus(
            @PathVariable Long id,
            @Valid @RequestBody TaskStatusUpdateRequest request) {
        TaskResponse response = taskService.updateTaskStatus(id, request.getStatus());
        return ResponseEntity.ok(ApiResponse.success("Task status updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task", description = "Deletes a task by its ID")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok(ApiResponse.success("Task deleted successfully", null));
    }

    @GetMapping("/stats")
    @Operation(summary = "Get task tracking statistics", description = "Calculates counts by status, priority, and overdue tasks")
    public ResponseEntity<ApiResponse<TaskStatsResponse>> getTaskStats() {
        TaskStatsResponse stats = taskService.getTaskStats();
        return ResponseEntity.ok(ApiResponse.success("Task statistics retrieved", stats));
    }
}
