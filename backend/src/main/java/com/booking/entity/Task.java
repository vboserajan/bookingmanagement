package com.booking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(length = 1000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;
    
    @Column(nullable = false)
    private Long assignedUserId;
    
    @Column(nullable = false)
    private Long createdBy;
    
    @Column(nullable = false)
    private LocalDateTime createdDate;
    
    @Column(nullable = false)
    private LocalDateTime scheduledDate;
    
    private Long approvedBy;
    
    private LocalDateTime approvalDate;
    
    @PrePersist
    protected void onCreate() {
        if (createdDate == null) {
            createdDate = LocalDateTime.now();
        }
        if (status == null) {
            status = Status.PENDING;
        }
    }
    
    public enum Status {
        PENDING,
        APPROVED,
        REJECTED
    }
    
    public enum Priority {
        LOW,
        MEDIUM,
        HIGH
    }
}
