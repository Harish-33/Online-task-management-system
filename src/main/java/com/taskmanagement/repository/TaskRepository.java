package com.taskmanagement.repository;

import com.taskmanagement.entity.Task;
import com.taskmanagement.entity.TaskPriority;
import com.taskmanagement.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    long countByStatus(TaskStatus status);
    long countByPriority(TaskPriority priority);
    long countByDueDateBeforeAndStatusNot(LocalDate date, TaskStatus status);
    List<Task> findByAssigneeId(Long assigneeId);
    List<Task> findByCategoryId(Long categoryId);
}
