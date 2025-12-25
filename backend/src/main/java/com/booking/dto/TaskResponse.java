package com.booking.dto;

import com.booking.entity.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private Task.Status status;
    private Task.Priority priority;
    private Long assignedUserId;
    private String assignedUserName;
    private Long createdBy;
    private String createdByName;
    private LocalDateTime createdDate;
    private LocalDateTime scheduledDate;
    private Long approvedBy;
    private String approvedByName;
    private LocalDateTime approvalDate;
}
