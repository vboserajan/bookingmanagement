# Workflow Logic Documentation

## Overview

The Booking Management System implements a simple yet effective approval workflow for task management with role-based access control.

## User Roles and Permissions

### 1. USER Role
**Permissions:**
- Create new tasks
- View all tasks
- View task details
- Export tasks to CSV
- View calendar
- Access dashboard

**Restrictions:**
- Cannot approve or reject tasks
- Cannot modify other users' tasks

### 2. MANAGER Role
**Permissions:**
- All USER permissions
- Approve pending tasks
- Reject pending tasks
- View approval history

**Restrictions:**
- Cannot modify system configuration
- Cannot create/delete users

### 3. ADMIN Role
**Permissions:**
- All MANAGER permissions
- Full system access
- User management capabilities (if implemented)

## Task Lifecycle

### State Diagram

```
┌─────────────┐
│   Created   │
│  (PENDING)  │
└──────┬──────┘
       │
       ├──────────────┬──────────────┐
       │              │              │
       ▼              ▼              ▼
┌──────────┐   ┌──────────┐   ┌──────────┐
│ Approved │   │ Rejected │   │ Pending  │
│ (FINAL)  │   │ (FINAL)  │   │(Waiting) │
└──────────┘   └──────────┘   └──────────┘
```

### Status Transitions

#### 1. Task Creation
- **Trigger**: User submits task creation form
- **Initial Status**: PENDING
- **Fields Set**:
  - Title, Description, Priority
  - Assigned User ID
  - Scheduled Date/Time
  - Created By (current user)
  - Created Date (timestamp)
- **Notification**: Task created log entry

#### 2. Task Approval
- **Trigger**: Manager/Admin clicks "Approve" button
- **Validation**:
  - User must have MANAGER or ADMIN role
  - Task must be in PENDING status
- **Status Change**: PENDING → APPROVED
- **Fields Updated**:
  - Status = APPROVED
  - Approved By = Manager/Admin user ID
  - Approval Date = Current timestamp
- **Notification**: Console log with approval details
  ```
  ✅ NOTIFICATION: Task 'Review Q4 Budget' (ID: 1) has been APPROVED by Manager User (MANAGER)
  ```

#### 3. Task Rejection
- **Trigger**: Manager/Admin clicks "Reject" button
- **Validation**:
  - User must have MANAGER or ADMIN role
  - Task must be in PENDING status
- **Status Change**: PENDING → REJECTED
- **Fields Updated**:
  - Status = REJECTED
  - Approved By = Manager/Admin user ID (field name is reused)
  - Approval Date = Current timestamp
- **Notification**: Console log with rejection details
  ```
  ❌ NOTIFICATION: Task 'Organize Office Party' (ID: 5) has been REJECTED by Manager User (MANAGER)
  ```

## Approval Workflow Process

### Step-by-Step Flow

1. **Task Submission**
   ```
   User → Create Task Form → Submit
   ↓
   Backend validates input
   ↓
   Task saved with PENDING status
   ↓
   User sees task in "Pending Tasks"
   ```

2. **Manager Review**
   ```
   Manager logs in
   ↓
   Views pending tasks (filtered or in dashboard)
   ↓
   Reviews task details
   ↓
   Decides: Approve or Reject
   ```

3. **Approval Action**
   ```
   Manager clicks "Approve"
   ↓
   Backend validates manager role
   ↓
   Updates task status to APPROVED
   ↓
   Logs notification to console
   ↓
   Frontend updates UI
   ↓
   Task moves to "Approved Tasks" section
   ```

4. **Rejection Action**
   ```
   Manager clicks "Reject"
   ↓
   Backend validates manager role
   ↓
   Updates task status to REJECTED
   ↓
   Logs notification to console
   ↓
   Frontend updates UI
   ↓
   Task moves to "Rejected Tasks" section
   ```

## Security and Access Control

### Authentication
- **Method**: Session-based authentication
- **Login Flow**:
  1. User submits credentials
  2. Backend validates username/password
  3. Session created with user ID and role
  4. Frontend stores user data in localStorage
  5. Subsequent requests include session cookie

### Authorization

#### Endpoint Protection
```java
// Public endpoints
/api/auth/** - Anyone can access

// Protected endpoints
/api/tasks/** - Authenticated users only
/api/users/** - Authenticated users only
```

#### Role-Based Method Security
```java
// Approval methods check role
public TaskResponse approveTask(Long taskId, Long approverId) {
    User approver = userRepository.findById(approverId);
    
    if (approver.getRole() != MANAGER && approver.getRole() != ADMIN) {
        throw new RuntimeException("Only managers and admins can approve tasks");
    }
    
    // Proceed with approval
}
```

#### Frontend Role Checks
```javascript
// Conditional rendering based on role
{isManager() && task.status === 'PENDING' && (
  <div className="task-actions">
    <button onClick={handleApprove}>Approve</button>
    <button onClick={handleReject}>Reject</button>
  </div>
)}
```

## Notification System

### Current Implementation
- **Type**: Console logging
- **Format**: Structured log messages with emojis
- **Information Included**:
  - Action (Approved/Rejected)
  - Task title and ID
  - Approver name and role
  - Timestamp (automatic via logging)

### Example Logs
```
2024-12-24 13:45:23 INFO  TaskService - ✅ NOTIFICATION: Task 'Submit Monthly Report' (ID: 4) has been APPROVED by Manager User (MANAGER)

2024-12-24 14:20:15 INFO  TaskService - ❌ NOTIFICATION: Task 'Organize Office Party' (ID: 5) has been REJECTED by Admin User (ADMIN)
```

### Future Enhancements
- Email notifications to task creator
- Email notifications to assigned user
- In-app notification center
- Slack/Teams integration
- SMS notifications for high-priority tasks

## Data Flow Diagram

### Task Creation Flow
```
┌─────────┐      ┌──────────┐      ┌──────────┐      ┌──────────┐
│ User UI │─────▶│ Frontend │─────▶│ Backend  │─────▶│ Database │
└─────────┘      └──────────┘      └──────────┘      └──────────┘
    │                  │                  │                  │
    │ Fill Form        │                  │                  │
    │                  │ POST /api/tasks  │                  │
    │                  │                  │ Save Task        │
    │                  │                  │                  │
    │                  │ ◀────────────────┤ Task Saved       │
    │ ◀────────────────┤ Task Response    │                  │
    │ Show Success     │                  │                  │
```

### Approval Flow
```
┌─────────┐      ┌──────────┐      ┌──────────┐      ┌──────────┐
│Manager  │─────▶│ Frontend │─────▶│ Backend  │─────▶│ Database │
│   UI    │      │          │      │          │      │          │
└─────────┘      └──────────┘      └──────────┘      └──────────┘
    │                  │                  │                  │
    │ Click Approve    │                  │                  │
    │                  │ PUT /tasks/1/    │                  │
    │                  │     approve      │                  │
    │                  │                  │ Validate Role    │
    │                  │                  │ Update Status    │
    │                  │                  │ Log Notification │
    │                  │                  │                  │
    │                  │ ◀────────────────┤ Updated Task     │
    │ ◀────────────────┤ Task Response    │                  │
    │ Update UI        │                  │                  │
```

## Business Rules

### Task Creation Rules
1. Title is mandatory
2. Priority must be LOW, MEDIUM, or HIGH
3. Assigned user must exist in the system
4. Scheduled date must be in the future (recommended, not enforced)
5. Creator is automatically set to current user

### Approval Rules
1. Only MANAGER or ADMIN can approve/reject
2. Only PENDING tasks can be approved/rejected
3. Once approved/rejected, status is final (no reversal)
4. Approver cannot be the same as creator (not enforced, but recommended)

### Access Rules
1. All authenticated users can view all tasks
2. All authenticated users can create tasks
3. Only managers/admins see approval buttons
4. Session timeout after inactivity (default: 30 minutes)

## Error Handling

### Validation Errors
- **Missing required fields**: HTTP 400 with error message
- **Invalid user ID**: HTTP 400 "User not found"
- **Invalid date format**: HTTP 400 with validation error

### Authorization Errors
- **Not authenticated**: HTTP 401 "Not authenticated"
- **Insufficient permissions**: HTTP 400 "Only managers and admins can approve tasks"

### Business Logic Errors
- **Task not found**: HTTP 400 "Task not found"
- **Invalid status transition**: Prevented by UI (buttons hidden)

## Performance Considerations

### Database Queries
- Indexed fields: `id`, `status`, `assignedUserId`, `createdBy`
- Efficient queries with Spring Data JPA
- Lazy loading for related entities

### Caching Strategy
- Session data cached in memory
- User data cached in frontend localStorage
- Task list refreshed on demand

### Scalability
- Stateless backend (except sessions)
- Horizontal scaling possible
- Database connection pooling
- Frontend CDN deployment ready

## Monitoring and Auditing

### Current Logging
- Task creation logged
- Approval/rejection logged with full details
- Authentication events logged
- Error conditions logged

### Audit Trail
- Created By field tracks task creator
- Approved By field tracks approver
- Created Date and Approval Date provide timeline
- All changes logged to console

### Future Enhancements
- Database audit table
- Change history tracking
- User activity dashboard
- Compliance reporting
