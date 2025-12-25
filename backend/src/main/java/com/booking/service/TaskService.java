package com.booking.service;

import com.booking.dto.TaskRequest;
import com.booking.dto.TaskResponse;
import com.booking.entity.Task;
import com.booking.entity.User;
import com.booking.repository.TaskRepository;
import com.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {
    
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public TaskResponse createTask(TaskRequest request, Long createdByUserId) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setAssignedUserId(request.getAssignedUserId());
        task.setScheduledDate(request.getScheduledDate());
        task.setCreatedBy(createdByUserId);
        task.setStatus(Task.Status.PENDING);
        task.setCreatedDate(LocalDateTime.now());
        
        Task savedTask = taskRepository.save(task);
        log.info("Task created: {} by user {}", savedTask.getId(), createdByUserId);
        
        return convertToResponse(savedTask);
    }
    
    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAllByOrderByCreatedDateDesc().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<TaskResponse> getTasksByStatus(Task.Status status) {
        return taskRepository.findByStatusOrderByPriorityDesc(status).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<TaskResponse> getTasksByAssignedUser(Long userId) {
        return taskRepository.findByAssignedUserId(userId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<TaskResponse> getTasksByCreator(Long userId) {
        return taskRepository.findByCreatedBy(userId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<TaskResponse> getTasksByDateRange(LocalDateTime start, LocalDateTime end) {
        return taskRepository.findByScheduledDateBetween(start, end).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public TaskResponse approveTask(Long taskId, Long approverId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        User approver = userRepository.findById(approverId)
                .orElseThrow(() -> new RuntimeException("Approver not found"));
        
        // Validate that approver has MANAGER or ADMIN role
        if (approver.getRole() != User.Role.MANAGER && approver.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Only managers and admins can approve tasks");
        }
        
        task.setStatus(Task.Status.APPROVED);
        task.setApprovedBy(approverId);
        task.setApprovalDate(LocalDateTime.now());
        
        Task updatedTask = taskRepository.save(task);
        
        // Simulate notification
        log.info("✅ NOTIFICATION: Task '{}' (ID: {}) has been APPROVED by {} ({})", 
                task.getTitle(), task.getId(), approver.getName(), approver.getRole());
        
        return convertToResponse(updatedTask);
    }
    
    @Transactional
    public TaskResponse rejectTask(Long taskId, Long approverId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        User approver = userRepository.findById(approverId)
                .orElseThrow(() -> new RuntimeException("Approver not found"));
        
        // Validate that approver has MANAGER or ADMIN role
        if (approver.getRole() != User.Role.MANAGER && approver.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Only managers and admins can reject tasks");
        }
        
        task.setStatus(Task.Status.REJECTED);
        task.setApprovedBy(approverId);
        task.setApprovalDate(LocalDateTime.now());
        
        Task updatedTask = taskRepository.save(task);
        
        // Simulate notification
        log.info("❌ NOTIFICATION: Task '{}' (ID: {}) has been REJECTED by {} ({})", 
                task.getTitle(), task.getId(), approver.getName(), approver.getRole());
        
        return convertToResponse(updatedTask);
    }
    
    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return convertToResponse(task);
    }
    
    private TaskResponse convertToResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setPriority(task.getPriority());
        response.setAssignedUserId(task.getAssignedUserId());
        response.setCreatedBy(task.getCreatedBy());
        response.setCreatedDate(task.getCreatedDate());
        response.setScheduledDate(task.getScheduledDate());
        response.setApprovedBy(task.getApprovedBy());
        response.setApprovalDate(task.getApprovalDate());
        
        // Enrich with user names
        userRepository.findById(task.getAssignedUserId())
                .ifPresent(user -> response.setAssignedUserName(user.getName()));
        
        userRepository.findById(task.getCreatedBy())
                .ifPresent(user -> response.setCreatedByName(user.getName()));
        
        if (task.getApprovedBy() != null) {
            userRepository.findById(task.getApprovedBy())
                    .ifPresent(user -> response.setApprovedByName(user.getName()));
        }
        
        return response;
    }
}
