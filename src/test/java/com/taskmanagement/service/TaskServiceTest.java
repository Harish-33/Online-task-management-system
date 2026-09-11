package com.taskmanagement.service;

import com.taskmanagement.dto.request.TaskCreateRequest;
import com.taskmanagement.dto.response.TaskResponse;
import com.taskmanagement.dto.response.TaskStatsResponse;
import com.taskmanagement.entity.*;
import com.taskmanagement.exception.InvalidOperationException;
import com.taskmanagement.exception.ResourceNotFoundException;
import com.taskmanagement.mapper.TaskMapper;
import com.taskmanagement.repository.CategoryRepository;
import com.taskmanagement.repository.TaskRepository;
import com.taskmanagement.repository.UserRepository;
import com.taskmanagement.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskServiceImpl taskService;

    private User sampleUser;
    private Category sampleCategory;
    private Task sampleTask;
    private TaskResponse sampleTaskResponse;

    @BeforeEach
    void setUp() {
        sampleUser = new User(1L, "john_doe", "john@example.com", "John Doe");
        sampleCategory = new Category(1L, "Backend", "Backend tasks", "#3B82F6");
        sampleTask = new Task(1L, "Setup Database", "Configure H2", TaskStatus.TODO, TaskPriority.HIGH,
                LocalDate.now().plusDays(2), sampleUser, sampleCategory);
        sampleTask.setCreatedAt(LocalDateTime.now());
        sampleTask.setUpdatedAt(LocalDateTime.now());

        sampleTaskResponse = new TaskResponse(
                1L, "Setup Database", "Configure H2", TaskStatus.TODO, TaskPriority.HIGH,
                LocalDate.now().plusDays(2), false, null, null,
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Should create task successfully when valid request is provided")
    void testCreateTask_Success() {
        TaskCreateRequest request = new TaskCreateRequest("Setup Database", "Configure H2",
                TaskPriority.HIGH, LocalDate.now().plusDays(2), 1L, 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(sampleCategory));
        when(taskMapper.toEntity(request, sampleUser, sampleCategory)).thenReturn(sampleTask);
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);
        when(taskMapper.toResponse(sampleTask)).thenReturn(sampleTaskResponse);

        TaskResponse result = taskService.createTask(request);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Setup Database");
        verify(taskRepository, times(1)).save(sampleTask);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when assignee does not exist")
    void testCreateTask_AssigneeNotFound() {
        TaskCreateRequest request = new TaskCreateRequest("Setup Database", "Configure H2",
                TaskPriority.HIGH, LocalDate.now().plusDays(2), 99L, null);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.createTask(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(taskRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should retrieve task by ID when task exists")
    void testGetTaskById_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskMapper.toResponse(sampleTask)).thenReturn(sampleTaskResponse);

        TaskResponse result = taskService.getTaskById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when task ID does not exist")
    void testGetTaskById_NotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Task not found");
    }

    @Test
    @DisplayName("Should update task status successfully")
    void testUpdateTaskStatus_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);
        when(taskMapper.toResponse(any(Task.class))).thenReturn(sampleTaskResponse);

        TaskResponse result = taskService.updateTaskStatus(1L, TaskStatus.IN_PROGRESS);

        assertThat(result).isNotNull();
        verify(taskRepository).save(sampleTask);
        assertThat(sampleTask.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("Should throw InvalidOperationException when changing status of CANCELLED task")
    void testUpdateTaskStatus_FromCancelled_ThrowsException() {
        sampleTask.setStatus(TaskStatus.CANCELLED);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));

        assertThatThrownBy(() -> taskService.updateTaskStatus(1L, TaskStatus.IN_PROGRESS))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("Cannot transition directly from CANCELLED state");

        verify(taskRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete task successfully when task exists")
    void testDeleteTask_Success() {
        when(taskRepository.existsById(1L)).thenReturn(true);
        doNothing().when(taskRepository).deleteById(1L);

        taskService.deleteTask(1L);

        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should calculate task statistics correctly")
    void testGetTaskStats() {
        when(taskRepository.count()).thenReturn(10L);
        when(taskRepository.countByStatus(TaskStatus.TODO)).thenReturn(4L);
        when(taskRepository.countByStatus(TaskStatus.IN_PROGRESS)).thenReturn(3L);
        when(taskRepository.countByStatus(TaskStatus.COMPLETED)).thenReturn(2L);
        when(taskRepository.countByStatus(TaskStatus.CANCELLED)).thenReturn(1L);
        when(taskRepository.countByPriority(TaskPriority.LOW)).thenReturn(2L);
        when(taskRepository.countByPriority(TaskPriority.MEDIUM)).thenReturn(5L);
        when(taskRepository.countByPriority(TaskPriority.HIGH)).thenReturn(2L);
        when(taskRepository.countByPriority(TaskPriority.URGENT)).thenReturn(1L);
        when(taskRepository.countByDueDateBeforeAndStatusNot(any(LocalDate.class), eq(TaskStatus.COMPLETED)))
                .thenReturn(2L);

        TaskStatsResponse stats = taskService.getTaskStats();

        assertThat(stats.getTotalTasks()).isEqualTo(10L);
        assertThat(stats.getTodoCount()).isEqualTo(4L);
        assertThat(stats.getInProgressCount()).isEqualTo(3L);
        assertThat(stats.getCompletedCount()).isEqualTo(2L);
        assertThat(stats.getOverdueCount()).isEqualTo(2L);
    }
}
