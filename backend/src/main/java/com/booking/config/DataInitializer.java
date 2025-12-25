package com.booking.config;

import com.booking.entity.Task;
import com.booking.entity.User;
import com.booking.repository.TaskRepository;
import com.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            initializeUsers();
            initializeTasks();
        }
    }
    
    private void initializeUsers() {
        // Create Admin user
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("password123"));
        admin.setName("Admin User");
        admin.setEmail("admin@booking.com");
        admin.setRole(User.Role.ADMIN);
        userRepository.save(admin);
        
        // Create Manager user
        User manager = new User();
        manager.setUsername("manager");
        manager.setPassword(passwordEncoder.encode("password123"));
        manager.setName("Manager User");
        manager.setEmail("manager@booking.com");
        manager.setRole(User.Role.MANAGER);
        userRepository.save(manager);
        
        // Create Regular user
        User user = new User();
        user.setUsername("user");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setName("Regular User");
        user.setEmail("user@booking.com");
        user.setRole(User.Role.USER);
        userRepository.save(user);
        
        log.info("✅ Initialized 3 users: admin, manager, user (password: password123)");
    }
    
    private void initializeTasks() {
        User user = userRepository.findByUsername("user").orElseThrow();
        User manager = userRepository.findByUsername("manager").orElseThrow();
        
        // Create sample pending tasks
        Task task1 = new Task();
        task1.setTitle("Review Q4 Budget Report");
        task1.setDescription("Review and analyze the Q4 budget report for accuracy and completeness");
        task1.setStatus(Task.Status.PENDING);
        task1.setPriority(Task.Priority.HIGH);
        task1.setAssignedUserId(manager.getId());
        task1.setCreatedBy(user.getId());
        task1.setCreatedDate(LocalDateTime.now().minusDays(2));
        task1.setScheduledDate(LocalDateTime.now().plusDays(3));
        taskRepository.save(task1);
        
        Task task2 = new Task();
        task2.setTitle("Prepare Team Meeting Agenda");
        task2.setDescription("Create agenda for the upcoming team meeting scheduled for next week");
        task2.setStatus(Task.Status.PENDING);
        task2.setPriority(Task.Priority.MEDIUM);
        task2.setAssignedUserId(user.getId());
        task2.setCreatedBy(manager.getId());
        task2.setCreatedDate(LocalDateTime.now().minusDays(1));
        task2.setScheduledDate(LocalDateTime.now().plusDays(5));
        taskRepository.save(task2);
        
        Task task3 = new Task();
        task3.setTitle("Update Client Database");
        task3.setDescription("Update client contact information in the CRM system");
        task3.setStatus(Task.Status.PENDING);
        task3.setPriority(Task.Priority.LOW);
        task3.setAssignedUserId(user.getId());
        task3.setCreatedBy(user.getId());
        task3.setCreatedDate(LocalDateTime.now().minusHours(6));
        task3.setScheduledDate(LocalDateTime.now().plusDays(7));
        taskRepository.save(task3);
        
        // Create an approved task
        Task task4 = new Task();
        task4.setTitle("Submit Monthly Report");
        task4.setDescription("Compile and submit the monthly performance report");
        task4.setStatus(Task.Status.APPROVED);
        task4.setPriority(Task.Priority.HIGH);
        task4.setAssignedUserId(user.getId());
        task4.setCreatedBy(user.getId());
        task4.setCreatedDate(LocalDateTime.now().minusDays(5));
        task4.setScheduledDate(LocalDateTime.now().plusDays(1));
        task4.setApprovedBy(manager.getId());
        task4.setApprovalDate(LocalDateTime.now().minusDays(4));
        taskRepository.save(task4);
        
        // Create a rejected task
        Task task5 = new Task();
        task5.setTitle("Organize Office Party");
        task5.setDescription("Plan and organize the annual office party");
        task5.setStatus(Task.Status.REJECTED);
        task5.setPriority(Task.Priority.LOW);
        task5.setAssignedUserId(user.getId());
        task5.setCreatedBy(user.getId());
        task5.setCreatedDate(LocalDateTime.now().minusDays(10));
        task5.setScheduledDate(LocalDateTime.now().plusDays(30));
        task5.setApprovedBy(manager.getId());
        task5.setApprovalDate(LocalDateTime.now().minusDays(9));
        taskRepository.save(task5);
        
        log.info("✅ Initialized 5 sample tasks (3 pending, 1 approved, 1 rejected)");
    }
}
