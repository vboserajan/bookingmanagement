# Booking Management System

## Project Overview

Successfully built a **full-stack booking management system** with Spring Boot backend, React frontend, and Docker containerization. The application implements a complete approval workflow with role-based access control.

## Project Demo Video DownLoad Link (Using Download Raw File button)

https://github.com/vboserajan/bookingmanagement/blob/main/BookingManagementDemo.mov 

## ✅ Features

### Core Requirements

#### 1. User Authentication ✓
- **Login/Logout**: Session-based authentication with Spring Security
- **Demo Users**: Pre-configured users with different roles
  - Admin: `admin` / `password123`
  - Manager: `manager` / `password123`
  - User: `user` / `password123`
- **Password Security**: BCrypt encryption for all passwords
- **Session Management**: HTTP sessions with automatic timeout

#### 2. Dashboard ✓
- **Statistics Cards**: Real-time counts of pending, approved, and rejected tasks
- **Quick Actions**: One-click navigation to create tasks, view all tasks, or calendar
- **Recent Tasks**: Overview of the 5 most recent tasks
- **Responsive Design**: Grid layout adapts to screen size

**Features:**
- Color-coded statistics (Orange=Pending, Green=Approved, Red=Rejected)
- Animated hover effects on cards
- Personalized welcome message

#### 3. Task Creation Form ✓
- **Required Fields**:
  - Title (text input with validation)
  - Priority (dropdown: Low/Medium/High)
  - Assigned User (dropdown populated from database)
  - Scheduled Date/Time (datetime picker)
- **Optional Fields**:
  - Description (textarea)
- **Validation**: Client-side and server-side validation
- **User Experience**: Clear error messages, loading states

#### 4. Task List View ✓
- **Grid Layout**: Responsive card-based display
- **Filtering**: Dropdown to filter by status (All/Pending/Approved/Rejected)
- **Sorting**: Multiple sort options
  - Created Date (newest first)
  - Priority (High → Low)
  - Scheduled Date (earliest first)
- **Task Count**: Dynamic display of filtered results
- **Export**: CSV export button

#### 5. Approval Workflow ✓
- **Status Transitions**: Pending → Approved/Rejected (final states)
- **Role Validation**: Only MANAGER and ADMIN roles can approve/reject
- **Approval Actions**: 
  - Approve button (green) on pending tasks
  - Reject button (red) on pending tasks
  - Buttons only visible to managers/admins
- **Notifications**: Console log notifications with full details

### Bonus Features

#### 1. Role-Based Access Control ✓
- **Three Roles**: ADMIN, MANAGER, USER
- **Permission Hierarchy**:
  - USER: Create and view tasks
  - MANAGER: All USER permissions + approve/reject
  - ADMIN: All MANAGER permissions + full access
- **UI Adaptation**: Approve/reject buttons only shown to managers
- **Backend Validation**: Role checked on every approval/rejection

#### 2. Calendar View ✓
- **FullCalendar Integration**: Professional calendar component
- **Color Coding**: Tasks colored by status
  - Orange: Pending
  - Green: Approved
  - Red: Rejected
- **Interactive**: Click task to view details in modal
- **Views**: Month and week views available
- **Navigation**: Previous/next month, today button

#### 3. CSV Export ✓
- **Export Button**: One-click export from task list
- **Complete Data**: All task fields included
- **Proper Formatting**: CSV escaping for special characters
- **Download**: Automatic file download as `tasks.csv`

## Technical Implementation

### Backend (Spring Boot)

**Technology Stack:**
- Java 21
- Spring Boot 3.2.1
- Spring Data JPA
- Spring Security
- H2 Database (dev) / MySQL (prod)
- Maven

**REST API Endpoints:**

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/auth/login` | User login | No |
| POST | `/api/auth/logout` | User logout | Yes |
| GET | `/api/auth/current-user` | Get current user | Yes |
| POST | `/api/tasks` | Create task | Yes |
| GET | `/api/tasks` | List tasks (with filters) | Yes |
| GET | `/api/tasks/{id}` | Get task by ID | Yes |
| PUT | `/api/tasks/{id}/approve` | Approve task | Yes (Manager/Admin) |
| PUT | `/api/tasks/{id}/reject` | Reject task | Yes (Manager/Admin) |
| GET | `/api/tasks/calendar` | Get tasks for calendar | Yes |
| GET | `/api/tasks/export/csv` | Export tasks as CSV | Yes |
| GET | `/api/users` | List all users | Yes |

### Frontend (React)

**Technology Stack:**
- React 18
- Vite (build tool)
- React Router (navigation)
- Axios (HTTP client)
- FullCalendar (calendar view)
- date-fns (date utilities)

## Deployment & Containerization

### Docker Containerization

**Multi-Stage Dockerfile:**
- **Stage 1 (Frontend)**: Node.js 18 Alpine, builds React app
- **Stage 2 (Backend)**: Maven 3.9 with JDK 21, builds Spring Boot JAR and embeds React build
- **Stage 3 (Runtime)**: eclipse-temurin:21-jre-alpine, runs the application

**Benefits:**
- Single container for full application
- Optimized image size
- Production-ready
- Easy deployment

### Docker Compose (Future Use)
*Note: Docker Compose configuration is provided for future scalability (e.g., adding MySQL) but is not currently used for the single-container deployment.*

**Services:**
- **MySQL**: Database with persistent volume
- **App**: Application container with health checks

## Setup Instructions

### Local Development

**Backend:**
```bash
cd backend
JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home mvn spring-boot:run
```
Access: http://localhost:8080

**Frontend:**
```bash
cd frontend
npm install
npm run dev
```
Access: http://localhost:5173

### Docker (Single Container)

```bash
docker build -t booking-management:latest .
docker run -p 8080:8080 booking-management:latest
```
Access: http://localhost:8080

## Testing

### Unit Tests
- **TaskServiceTest**: Covers creation, approval, rejection, and status filtering.
- **UserServiceTest**: Covers user creation, duplicate checks, and password encryption.

### Manual Verification Steps
1. **Login**: Use demo credentials (`user`/`password123`).
2. **Dashboard**: Verify stats and charts.
3. **Task Creation**: Create a task and check the list.
4. **Approval**: Login as `manager`, approve a task, verify status change.
5. **Calendar**: Check task visibility on calendar dates.
6. **Export**: Download CSV and verify contents.

## Project Stats
- **Total Files**: 50+
- **Total Lines of Code**: ~4,500
- **Documentation**: Comprehensive README and Architecture guides
