package com.taskmanagement.config;

import com.taskmanagement.entity.*;
import com.taskmanagement.repository.CategoryRepository;
import com.taskmanagement.repository.TaskRepository;
import com.taskmanagement.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TaskRepository taskRepository;

    public DataInitializer(UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            logger.info("Sample data already exists. Skipping data initialization.");
            return;
        }

        logger.info("Initializing sample users, categories, and tasks...");

        // 1. Create Users
        User john = userRepository.save(new User(null, "john_doe", "john.doe@example.com", "John Doe"));
        User alice = userRepository.save(new User(null, "alice_smith", "alice.smith@example.com", "Alice Smith"));
        User bob = userRepository.save(new User(null, "bob_dev", "bob.dev@example.com", "Bob Developer"));

        // 2. Create Categories
        Category backend = categoryRepository.save(new Category(null, "Backend", "API and server-side development", "#3B82F6"));
        Category frontend = categoryRepository.save(new Category(null, "Frontend", "UI/UX and client components", "#10B981"));
        Category devops = categoryRepository.save(new Category(null, "DevOps", "CI/CD and infrastructure configuration", "#F59E0B"));

        // 3. Create Tasks
        Task task1 = new Task(
                null,
                "Implement JWT Authentication",
                "Add Spring Security and JWT token filter for authentication and authorization.",
                TaskStatus.IN_PROGRESS,
                TaskPriority.HIGH,
                LocalDate.now().plusDays(3),
                john,
                backend
        );

        Task task2 = new Task(
                null,
                "Design Responsive Dashboard",
                "Create React dashboard with task statistics cards and filterable grid.",
                TaskStatus.TODO,
                TaskPriority.MEDIUM,
                LocalDate.now().plusDays(7),
                alice,
                frontend
        );

        Task task3 = new Task(
                null,
                "Database Migration Script",
                "Write Liquibase or Flyway scripts to migrate schemas into staging MySQL cluster.",
                TaskStatus.COMPLETED,
                TaskPriority.LOW,
                LocalDate.now().minusDays(2),
                bob,
                backend
        );

        Task task4 = new Task(
                null,
                "Configure GitHub Actions CI Workflow",
                "Automate Maven builds, unit testing, and Docker image publishing on push.",
                TaskStatus.TODO,
                TaskPriority.URGENT,
                LocalDate.now().minusDays(1), // Overdue task demonstration
                john,
                devops
        );

        taskRepository.saveAll(List.of(task1, task2, task3, task4));

        logger.info("Sample data initialized successfully! Total tasks loaded: {}", taskRepository.count());
    }
}
