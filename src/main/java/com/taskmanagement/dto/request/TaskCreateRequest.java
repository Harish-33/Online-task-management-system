package com.taskmanagement.dto.request;

import com.taskmanagement.entity.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class TaskCreateRequest {

    @NotBlank(message = "Task title is required and cannot be blank")
    @Size(min = 3, max = 150, message = "Task title must be between 3 and 150 characters")
    private String title;

    private String description;

    private TaskPriority priority = TaskPriority.MEDIUM;

    private LocalDate dueDate;

    private Long assigneeId;

    private Long categoryId;

    public TaskCreateRequest() {
    }

    public TaskCreateRequest(String title, String description, TaskPriority priority,
                             LocalDate dueDate, Long assigneeId, Long categoryId) {
        this.title = title;
        this.description = description;
        this.priority = priority != null ? priority : TaskPriority.MEDIUM;
        this.dueDate = dueDate;
        this.assigneeId = assigneeId;
        this.categoryId = categoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
