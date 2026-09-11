package com.taskmanagement.mapper;

import com.taskmanagement.dto.request.TaskCreateRequest;
import com.taskmanagement.dto.response.CategoryResponse;
import com.taskmanagement.dto.response.TaskResponse;
import com.taskmanagement.dto.response.UserResponse;
import com.taskmanagement.entity.Category;
import com.taskmanagement.entity.Task;
import com.taskmanagement.entity.TaskPriority;
import com.taskmanagement.entity.TaskStatus;
import com.taskmanagement.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TaskMapper {

    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;

    public TaskMapper(UserMapper userMapper, CategoryMapper categoryMapper) {
        this.userMapper = userMapper;
        this.categoryMapper = categoryMapper;
    }

    public Task toEntity(TaskCreateRequest request, User assignee, Category category) {
        if (request == null) {
            return null;
        }
        Task task = new Task();
        task.setTitle(request.getTitle().trim());
        task.setDescription(request.getDescription());
        task.setStatus(TaskStatus.TODO);
        task.setPriority(request.getPriority() != null ? request.getPriority() : TaskPriority.MEDIUM);
        task.setDueDate(request.getDueDate());
        task.setAssignee(assignee);
        task.setCategory(category);
        return task;
    }

    public TaskResponse toResponse(Task task) {
        if (task == null) {
            return null;
        }

        boolean isOverdue = task.getDueDate() != null
                && task.getDueDate().isBefore(LocalDate.now())
                && task.getStatus() != TaskStatus.COMPLETED
                && task.getStatus() != TaskStatus.CANCELLED;

        UserResponse assigneeResponse = task.getAssignee() != null ? userMapper.toResponse(task.getAssignee()) : null;
        CategoryResponse categoryResponse = task.getCategory() != null ? categoryMapper.toResponse(task.getCategory()) : null;

        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                isOverdue,
                assigneeResponse,
                categoryResponse,
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}
