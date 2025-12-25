package com.booking.dto;

import com.booking.entity.Task;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    @NotNull(message = "Priority is required")
    private Task.Priority priority;
    
    @NotNull(message = "Assigned user is required")
    private Long assignedUserId;
    
    @NotNull(message = "Scheduled date is required")
    private LocalDateTime scheduledDate;
}
