package com.taskmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanagement.dto.request.TaskCreateRequest;
import com.taskmanagement.dto.request.TaskStatusUpdateRequest;
import com.taskmanagement.dto.response.TaskResponse;
import com.taskmanagement.dto.response.TaskStatsResponse;
import com.taskmanagement.entity.TaskPriority;
import com.taskmanagement.entity.TaskStatus;
import com.taskmanagement.exception.ResourceNotFoundException;
import com.taskmanagement.service.TaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @Test
    @DisplayName("POST /api/v1/tasks - Should return 201 Created when request is valid")
    void testCreateTask_Valid() throws Exception {
        TaskCreateRequest request = new TaskCreateRequest(
                "Write Integration Tests", "Add MockMvc tests", TaskPriority.HIGH,
                LocalDate.now().plusDays(3), 1L, 1L
        );

        TaskResponse response = new TaskResponse(
                1L, "Write Integration Tests", "Add MockMvc tests",
                TaskStatus.TODO, TaskPriority.HIGH, LocalDate.now().plusDays(3),
                false, null, null, LocalDateTime.now(), LocalDateTime.now()
        );

        when(taskService.createTask(any(TaskCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("Write Integration Tests"));
    }

    @Test
    @DisplayName("POST /api/v1/tasks - Should return 400 Bad Request when title is blank")
    void testCreateTask_BlankTitle_ValidationError() throws Exception {
        TaskCreateRequest request = new TaskCreateRequest(
                "", "Description", TaskPriority.MEDIUM, null, null, null
        );

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.fieldErrors.title").exists());
    }

    @Test
    @DisplayName("GET /api/v1/tasks/{id} - Should return 200 OK when task exists")
    void testGetTaskById_Exists() throws Exception {
        TaskResponse response = new TaskResponse(
                1L, "Write Docs", "Swagger", TaskStatus.TODO, TaskPriority.LOW,
                LocalDate.now().plusDays(1), false, null, null,
                LocalDateTime.now(), LocalDateTime.now()
        );

        when(taskService.getTaskById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("Write Docs"));
    }

    @Test
    @DisplayName("GET /api/v1/tasks/{id} - Should return 404 Not Found when task does not exist")
    void testGetTaskById_NotFound() throws Exception {
        when(taskService.getTaskById(99L)).thenThrow(new ResourceNotFoundException("Task", "id", 99L));

        mockMvc.perform(get("/api/v1/tasks/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Task not found with id: '99'"));
    }

    @Test
    @DisplayName("PATCH /api/v1/tasks/{id}/status - Should return 200 OK on status update")
    void testUpdateTaskStatus() throws Exception {
        TaskStatusUpdateRequest request = new TaskStatusUpdateRequest(TaskStatus.COMPLETED);
        TaskResponse response = new TaskResponse(
                1L, "Write Docs", "Swagger", TaskStatus.COMPLETED, TaskPriority.LOW,
                LocalDate.now().plusDays(1), false, null, null,
                LocalDateTime.now(), LocalDateTime.now()
        );

        when(taskService.updateTaskStatus(eq(1L), eq(TaskStatus.COMPLETED))).thenReturn(response);

        mockMvc.perform(patch("/api/v1/tasks/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));
    }

    @Test
    @DisplayName("GET /api/v1/tasks - Should return paginated tasks list")
    void testGetAllTasks() throws Exception {
        TaskResponse response = new TaskResponse(
                1L, "Write Docs", "Swagger", TaskStatus.TODO, TaskPriority.LOW,
                LocalDate.now().plusDays(1), false, null, null,
                LocalDateTime.now(), LocalDateTime.now()
        );

        when(taskService.getAllTasks(any(), any(), any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(response)));

        mockMvc.perform(get("/api/v1/tasks?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].title").value("Write Docs"));
    }

    @Test
    @DisplayName("GET /api/v1/tasks/stats - Should return task metrics")
    void testGetTaskStats() throws Exception {
        TaskStatsResponse stats = new TaskStatsResponse(10, 4, 3, 2, 1, 2, 5, 2, 1, 0);

        when(taskService.getTaskStats()).thenReturn(stats);

        mockMvc.perform(get("/api/v1/tasks/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalTasks").value(10))
                .andExpect(jsonPath("$.data.completedCount").value(2));
    }
}
