package com.taskmanagement.service.impl;

import com.taskmanagement.dto.request.TaskCreateRequest;
import com.taskmanagement.dto.request.TaskUpdateRequest;
import com.taskmanagement.dto.response.TaskResponse;
import com.taskmanagement.dto.response.TaskStatsResponse;
import com.taskmanagement.entity.*;
import com.taskmanagement.exception.InvalidOperationException;
import com.taskmanagement.exception.ResourceNotFoundException;
import com.taskmanagement.mapper.TaskMapper;
import com.taskmanagement.repository.CategoryRepository;
import com.taskmanagement.repository.TaskRepository;
import com.taskmanagement.repository.TaskSpecification;
import com.taskmanagement.repository.UserRepository;
import com.taskmanagement.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TaskMapper taskMapper;

    public TaskServiceImpl(TaskRepository taskRepository,
                           UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.taskMapper = taskMapper;
    }

    @Override
    public TaskResponse createTask(TaskCreateRequest request) {
        User assignee = null;
        if (request.getAssigneeId() != null) {
            assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getAssigneeId()));
        }

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
        }

        Task task = taskMapper.toEntity(request, assignee, category);
        Task savedTask = taskRepository.save(task);
        return taskMapper.toResponse(savedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
        return taskMapper.toResponse(task);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponse> getAllTasks(TaskStatus status,
                                          TaskPriority priority,
                                          Long categoryId,
                                          Long assigneeId,
                                          String search,
                                          LocalDate dueDateFrom,
                                          LocalDate dueDateTo,
                                          Pageable pageable) {
        Specification<Task> spec = TaskSpecification.filterTasks(
                status, priority, categoryId, assigneeId, search, dueDateFrom, dueDateTo
        );

        return taskRepository.findAll(spec, pageable).map(taskMapper::toResponse);
    }

    @Override
    public TaskResponse updateTask(Long id, TaskUpdateRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));

        task.setTitle(request.getTitle().trim());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());

        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }

        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }

        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getAssigneeId()));
            task.setAssignee(assignee);
        } else {
            task.setAssignee(null);
        }

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
            task.setCategory(category);
        } else {
            task.setCategory(null);
        }

        Task updatedTask = taskRepository.save(task);
        return taskMapper.toResponse(updatedTask);
    }

    @Override
    public TaskResponse updateTaskStatus(Long id, TaskStatus newStatus) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));

        if (task.getStatus() == TaskStatus.CANCELLED && newStatus != TaskStatus.CANCELLED) {
            throw new InvalidOperationException("Cannot transition directly from CANCELLED state. Please use full task update to reopen.");
        }

        task.setStatus(newStatus);
        Task updatedTask = taskRepository.save(task);
        return taskMapper.toResponse(updatedTask);
    }

    @Override
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task", "id", id);
        }
        taskRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskStatsResponse getTaskStats() {
        long totalTasks = taskRepository.count();
        long todoCount = taskRepository.countByStatus(TaskStatus.TODO);
        long inProgressCount = taskRepository.countByStatus(TaskStatus.IN_PROGRESS);
        long completedCount = taskRepository.countByStatus(TaskStatus.COMPLETED);
        long cancelledCount = taskRepository.countByStatus(TaskStatus.CANCELLED);

        long lowPriorityCount = taskRepository.countByPriority(TaskPriority.LOW);
        long mediumPriorityCount = taskRepository.countByPriority(TaskPriority.MEDIUM);
        long highPriorityCount = taskRepository.countByPriority(TaskPriority.HIGH);
        long urgentPriorityCount = taskRepository.countByPriority(TaskPriority.URGENT);

        long overdueCount = taskRepository.countByDueDateBeforeAndStatusNot(LocalDate.now(), TaskStatus.COMPLETED);

        return new TaskStatsResponse(
                totalTasks,
                todoCount,
                inProgressCount,
                completedCount,
                cancelledCount,
                lowPriorityCount,
                mediumPriorityCount,
                highPriorityCount,
                urgentPriorityCount,
                overdueCount
        );
    }
}
