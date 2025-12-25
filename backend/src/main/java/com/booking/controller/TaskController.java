package com.booking.controller;

import com.booking.dto.TaskRequest;
import com.booking.dto.TaskResponse;
import com.booking.entity.Task;
import com.booking.service.TaskService;
import com.booking.util.CsvExporter;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TaskController {
    
    private final TaskService taskService;
    private final CsvExporter csvExporter;
    
    @PostMapping
    public ResponseEntity<?> createTask(@Valid @RequestBody TaskRequest request, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(401).body(new ErrorResponse("Not authenticated"));
            }
            
            TaskResponse response = taskService.createTask(request, userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long assignedUserId,
            @RequestParam(required = false) Long createdBy) {
        
        if (status != null) {
            Task.Status taskStatus = Task.Status.valueOf(status.toUpperCase());
            return ResponseEntity.ok(taskService.getTasksByStatus(taskStatus));
        } else if (assignedUserId != null) {
            return ResponseEntity.ok(taskService.getTasksByAssignedUser(assignedUserId));
        } else if (createdBy != null) {
            return ResponseEntity.ok(taskService.getTasksByCreator(createdBy));
        }
        
        return ResponseEntity.ok(taskService.getAllTasks());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {
        try {
            TaskResponse response = taskService.getTaskById(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveTask(@PathVariable Long id, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(401).body(new ErrorResponse("Not authenticated"));
            }
            
            TaskResponse response = taskService.approveTask(id, userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectTask(@PathVariable Long id, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(401).body(new ErrorResponse("Not authenticated"));
            }
            
            TaskResponse response = taskService.rejectTask(id, userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
    
    @GetMapping("/calendar")
    public ResponseEntity<List<TaskResponse>> getTasksForCalendar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        
        return ResponseEntity.ok(taskService.getTasksByDateRange(start, end));
    }
    
    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportTasksToCsv() {
        List<TaskResponse> tasks = taskService.getAllTasks();
        byte[] csvData = csvExporter.exportTasksToCsv(tasks);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "tasks.csv");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(csvData);
    }
    
    record ErrorResponse(String error) {}
}
