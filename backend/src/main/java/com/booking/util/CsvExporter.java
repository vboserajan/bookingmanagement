package com.booking.util;

import com.booking.dto.TaskResponse;
import org.springframework.stereotype.Component;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class CsvExporter {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    public byte[] exportTasksToCsv(List<TaskResponse> tasks) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(outputStream);
        
        // Write CSV header
        writer.println("ID,Title,Description,Status,Priority,Assigned To,Created By,Created Date,Scheduled Date,Approved By,Approval Date");
        
        // Write task data
        for (TaskResponse task : tasks) {
            writer.printf("%d,\"%s\",\"%s\",%s,%s,\"%s\",\"%s\",%s,%s,\"%s\",%s%n",
                    task.getId(),
                    escapeCsv(task.getTitle()),
                    escapeCsv(task.getDescription()),
                    task.getStatus(),
                    task.getPriority(),
                    escapeCsv(task.getAssignedUserName()),
                    escapeCsv(task.getCreatedByName()),
                    task.getCreatedDate().format(DATE_FORMATTER),
                    task.getScheduledDate().format(DATE_FORMATTER),
                    task.getApprovedByName() != null ? escapeCsv(task.getApprovedByName()) : "",
                    task.getApprovalDate() != null ? task.getApprovalDate().format(DATE_FORMATTER) : ""
            );
        }
        
        writer.flush();
        writer.close();
        
        return outputStream.toByteArray();
    }
    
    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\"", "\"\"");
    }
}
