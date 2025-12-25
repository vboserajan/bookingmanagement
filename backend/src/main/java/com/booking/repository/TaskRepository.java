package com.booking.repository;

import com.booking.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    List<Task> findByStatus(Task.Status status);
    
    List<Task> findByAssignedUserId(Long userId);
    
    List<Task> findByCreatedBy(Long userId);
    
    List<Task> findByStatusOrderByPriorityDesc(Task.Status status);
    
    List<Task> findByScheduledDateBetween(LocalDateTime start, LocalDateTime end);
    
    List<Task> findAllByOrderByCreatedDateDesc();
}
