import React, { useState, useEffect } from 'react';
import FullCalendar from '@fullcalendar/react';
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/interaction';
import { taskService } from '../services/taskService';
import { format, startOfMonth, endOfMonth } from 'date-fns';
import './CalendarView.css';

const CalendarView = () => {
    const [events, setEvents] = useState([]);
    const [selectedTask, setSelectedTask] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadCalendarTasks();
    }, []);

    const loadCalendarTasks = async () => {
        try {
            const now = new Date();
            const start = startOfMonth(new Date(now.getFullYear(), now.getMonth() - 1, 1));
            const end = endOfMonth(new Date(now.getFullYear(), now.getMonth() + 2, 1));

            const tasks = await taskService.getTasksForCalendar(
                start.toISOString(),
                end.toISOString()
            );

            const calendarEvents = tasks.map(task => ({
                id: task.id,
                title: task.title,
                start: task.scheduledDate,
                backgroundColor: getStatusColor(task.status),
                borderColor: getStatusColor(task.status),
                extendedProps: task,
            }));

            setEvents(calendarEvents);
        } catch (error) {
            console.error('Error loading calendar tasks:', error);
        } finally {
            setLoading(false);
        }
    };

    const getStatusColor = (status) => {
        switch (status) {
            case 'PENDING': return '#ffa726';
            case 'APPROVED': return '#66bb6a';
            case 'REJECTED': return '#ef5350';
            default: return '#999';
        }
    };

    const handleEventClick = (info) => {
        setSelectedTask(info.event.extendedProps);
    };

    const closeModal = () => {
        setSelectedTask(null);
    };

    if (loading) {
        return <div className="loading">Loading calendar...</div>;
    }

    return (
        <div className="calendar-view">
            <h1>Task Calendar</h1>

            <div className="calendar-container">
                <FullCalendar
                    plugins={[dayGridPlugin, interactionPlugin]}
                    initialView="dayGridMonth"
                    events={events}
                    eventClick={handleEventClick}
                    height="auto"
                    headerToolbar={{
                        left: 'prev,next today',
                        center: 'title',
                        right: 'dayGridMonth,dayGridWeek'
                    }}
                />
            </div>

            {selectedTask && (
                <div className="modal-overlay" onClick={closeModal}>
                    <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                        <button className="modal-close" onClick={closeModal}>×</button>
                        <h2>{selectedTask.title}</h2>
                        <p className="task-description">{selectedTask.description}</p>

                        <div className="task-info-grid">
                            <div className="info-item">
                                <span className="info-label">Status:</span>
                                <span className={`status-badge ${selectedTask.status.toLowerCase()}`}>
                                    {selectedTask.status}
                                </span>
                            </div>
                            <div className="info-item">
                                <span className="info-label">Priority:</span>
                                <span className="priority-text">{selectedTask.priority}</span>
                            </div>
                            <div className="info-item">
                                <span className="info-label">Assigned to:</span>
                                <span>{selectedTask.assignedUserName}</span>
                            </div>
                            <div className="info-item">
                                <span className="info-label">Created by:</span>
                                <span>{selectedTask.createdByName}</span>
                            </div>
                            <div className="info-item">
                                <span className="info-label">Scheduled:</span>
                                <span>{format(new Date(selectedTask.scheduledDate), 'PPpp')}</span>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default CalendarView;
