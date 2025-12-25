# Booking Management System

A full-stack web application for creating, managing, and approving tasks/bookings with role-based access control and approval workflow.

## 🚀 Features

### Core Features
- **User Authentication**: Secure login/logout with session management
- **Dashboard**: Overview of pending, approved, and rejected tasks with statistics
- **Task Management**: Create, view, filter, and sort tasks
- **Approval Workflow**: Manager/Admin approval system with status transitions (Pending → Approved/Rejected)
- **Role-Based Access Control**: Three user roles (Admin, Manager, User) with different permissions
- **Notifications**: Console log notifications for task approvals/rejections

### Bonus Features
- **Calendar View**: Visual calendar display of scheduled tasks using FullCalendar
- **CSV Export**: Export all tasks to CSV format
- **Modern UI**: Beautiful, responsive interface with gradient designs and smooth animations
- **Docker Support**: Single container deployment with multi-stage build

## 🛠️ Technology Stack

### Backend
- **Java 25** with **Spring Boot 3.2.1**
- **Spring Data JPA** for database access
- **Spring Security** for authentication and authorization
- **H2 Database** (development) / **MySQL** (production)
- **Maven** for dependency management

### Frontend
- **React 18** with **Vite**
- **React Router** for navigation
- **Axios** for API calls
- **FullCalendar** for calendar view
- **date-fns** for date utilities
- Modern CSS with gradients and animations

### DevOps
- **Docker** with multi-stage builds
- **Docker Compose** for orchestration

## 📋 Prerequisites

- **Java 25** or higher
- **Node.js 18** or higher
- **Maven 3.9** or higher
- **Docker** (optional, for containerized deployment)

## 🔧 Setup Instructions

### Option 1: Local Development

#### 1. Clone the Repository
```bash
git clone <repository-url>
cd BookingManagement
```

#### 2. Start the Backend
```bash
# Build and run Spring Boot application
./mvnw spring-boot:run
```

The backend will start on **http://localhost:8080**

#### 3. Start the Frontend (in a new terminal)
```bash
cd frontend
npm install
npm run dev
```

The frontend will start on **http://localhost:5173**

#### 4. Access the Application
Open your browser and navigate to **http://localhost:5173**

### Option 2: Docker Deployment

#### Single Container (H2 Database)
```bash
# Build the Docker image
docker build -t booking-management:latest .

# Run the container
docker run -p 8080:8080 booking-management:latest
```

Access the application at **http://localhost:8080**

#### Docker Compose (with MySQL)
```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

Access the application at **http://localhost:8080**

## 🔐 Default Credentials

The application comes with pre-configured demo users:

| Username | Password | Role |
|----------|----------|------|
| `admin` | `password123` | ADMIN |
| `manager` | `password123` | MANAGER |
| `user` | `password123` | USER |

## 📡 API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/logout` - User logout
- `GET /api/auth/current-user` - Get current user info

### Tasks
- `POST /api/tasks` - Create a new task
- `GET /api/tasks` - Get all tasks (supports filtering by status, assignedUserId, createdBy)
- `GET /api/tasks/{id}` - Get task by ID
- `PUT /api/tasks/{id}/approve` - Approve task (Manager/Admin only)
- `PUT /api/tasks/{id}/reject` - Reject task (Manager/Admin only)
- `GET /api/tasks/calendar?start={start}&end={end}` - Get tasks for calendar view
- `GET /api/tasks/export/csv` - Export tasks as CSV

### Users
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID

## 🧪 Running Tests

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=TaskServiceTest

# Run with coverage
./mvnw test jacoco:report
```

## 🏗️ Project Structure

```
BookingManagement/
├── src/main/java/com/booking/
│   ├── config/          # Security and data initialization
│   ├── controller/      # REST API controllers
│   ├── dto/             # Data Transfer Objects
│   ├── entity/          # JPA entities
│   ├── repository/      # Spring Data repositories
│   ├── service/         # Business logic
│   └── util/            # Utility classes
├── src/main/resources/
│   ├── application.properties
│   └── application-mysql.properties
├── src/test/java/       # Unit tests
├── frontend/
│   ├── src/
│   │   ├── components/  # React components
│   │   ├── context/     # React context
│   │   ├── services/    # API services
│   │   └── utils/       # Utilities
│   └── package.json
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```

## 🎨 Features Walkthrough

### 1. Login
- Navigate to the login page
- Enter credentials (use demo credentials above)
- Redirected to dashboard upon successful login

### 2. Dashboard
- View statistics: pending, approved, and rejected task counts
- Quick actions: Create task, view all tasks, calendar view
- Recent tasks overview

### 3. Create Task
- Click "Create New Task" button
- Fill in task details:
  - Title (required)
  - Description
  - Priority (Low/Medium/High)
  - Assign to user
  - Scheduled date and time
- Submit to create task (status: PENDING)

### 4. Task List
- View all tasks in a grid layout
- Filter by status (All/Pending/Approved/Rejected)
- Sort by date, priority, or scheduled date
- Export tasks to CSV
- Managers see Approve/Reject buttons on pending tasks

### 5. Approval Workflow
- Login as Manager or Admin
- Navigate to task list
- Click "Approve" or "Reject" on pending tasks
- Check backend console for notification logs
- Task status updates immediately

### 6. Calendar View
- Click "Calendar" in navigation
- View tasks on their scheduled dates
- Color-coded by status (Orange=Pending, Green=Approved, Red=Rejected)
- Click on task to view details

### 7. CSV Export
- Click "Export CSV" button in task list
- Download CSV file with all task data

## 🔒 Security Features

- **Password Encryption**: BCrypt password hashing
- **Session Management**: HTTP session-based authentication
- **Role-Based Access**: Method-level security for approval actions
- **CORS Configuration**: Configured for frontend-backend communication

## 🌐 Database Configuration

### H2 (Development)
- In-memory database
- Auto-configured on startup
- H2 Console: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:bookingdb`

### MySQL (Production)
```bash
# Start with MySQL profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=mysql

# Or with Docker Compose
docker-compose up
```

## 📝 Workflow Logic

### User Roles
- **USER**: Can create tasks, view all tasks
- **MANAGER**: All USER permissions + approve/reject tasks
- **ADMIN**: All MANAGER permissions + full system access

### Task Lifecycle
1. **Creation**: User creates task → Status: PENDING
2. **Approval**: Manager/Admin approves → Status: APPROVED
3. **Rejection**: Manager/Admin rejects → Status: REJECTED

### Notifications
- Approval/rejection triggers console log notification
- Format: `✅/❌ NOTIFICATION: Task '{title}' (ID: {id}) has been APPROVED/REJECTED by {name} ({role})`

## 🐛 Troubleshooting

### Backend won't start
- Ensure Java 25+ is installed: `java -version`
- Check port 8080 is available
- Verify Maven installation: `./mvnw -version`

### Frontend won't start
- Ensure Node.js 18+ is installed: `node -version`
- Delete `node_modules` and reinstall: `rm -rf node_modules && npm install`
- Check port 5173 is available

### Docker build fails
- Ensure Docker is running
- Check available disk space
- Try: `docker system prune -a`

## 📄 License

This project is created for educational purposes.

## 👥 Contributors

Built with Spring Boot, React, and ❤️
