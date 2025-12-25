package com.booking.service;

import com.booking.entity.Task;
import com.booking.entity.User;
import com.booking.repository.TaskRepository;
import com.booking.repository.UserRepository;
import com.booking.dto.TaskRequest;
import com.booking.dto.TaskResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    private User testUser;
    private User managerUser;
    private Task testTask;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setName("Test User");
        testUser.setRole(User.Role.USER);

        managerUser = new User();
        managerUser.setId(2L);
        managerUser.setUsername("manager");
        managerUser.setName("Manager User");
        managerUser.setRole(User.Role.MANAGER);

        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setPriority(Task.Priority.HIGH);
        testTask.setStatus(Task.Status.PENDING);
        testTask.setAssignedUserId(1L);
        testTask.setCreatedBy(1L);
        testTask.setScheduledDate(LocalDateTime.now().plusDays(1));
    }

    @Test
    void testCreateTask() {
        TaskRequest request = new TaskRequest();
        request.setTitle("New Task");
        request.setDescription("New Description");
        request.setPriority(Task.Priority.MEDIUM);
        request.setAssignedUserId(1L);
        request.setScheduledDate(LocalDateTime.now().plusDays(2));

        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        TaskResponse response = taskService.createTask(request, 1L);

        assertNotNull(response);
        assertEquals("Test Task", response.getTitle());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testApproveTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(userRepository.findById(2L)).thenReturn(Optional.of(managerUser));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        TaskResponse response = taskService.approveTask(1L, 2L);

        assertNotNull(response);
        assertEquals(Task.Status.APPROVED, testTask.getStatus());
        assertEquals(2L, testTask.getApprovedBy());
        assertNotNull(testTask.getApprovalDate());
        verify(taskRepository, times(1)).save(testTask);
    }

    @Test
    void testRejectTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(userRepository.findById(2L)).thenReturn(Optional.of(managerUser));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        TaskResponse response = taskService.rejectTask(1L, 2L);

        assertNotNull(response);
        assertEquals(Task.Status.REJECTED, testTask.getStatus());
        assertEquals(2L, testTask.getApprovedBy());
        assertNotNull(testTask.getApprovalDate());
        verify(taskRepository, times(1)).save(testTask);
    }

    @Test
    void testApprovalByNonManager_ShouldFail() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            taskService.approveTask(1L, 1L);
        });

        assertEquals("Only managers and admins can approve tasks", exception.getMessage());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testGetTasksByStatus() {
        when(taskRepository.findByStatusOrderByPriorityDesc(Task.Status.PENDING))
                .thenReturn(java.util.List.of(testTask));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        var tasks = taskService.getTasksByStatus(Task.Status.PENDING);

        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        verify(taskRepository, times(1)).findByStatusOrderByPriorityDesc(Task.Status.PENDING);
    }
}
