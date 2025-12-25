import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { taskService } from '../services/taskService';
import TaskCard from './TaskCard';
import './TaskList.css';

const TaskList = () => {
    const [tasks, setTasks] = useState([]);
    const [filteredTasks, setFilteredTasks] = useState([]);
    const [loading, setLoading] = useState(true);
    const [statusFilter, setStatusFilter] = useState('ALL');
    const [sortBy, setSortBy] = useState('date');
    const navigate = useNavigate();

    useEffect(() => {
        loadTasks();
    }, []);

    useEffect(() => {
        applyFiltersAndSort();
    }, [tasks, statusFilter, sortBy]);

    const loadTasks = async () => {
        try {
            const data = await taskService.getAllTasks();
            setTasks(data);
        } catch (error) {
            console.error('Error loading tasks:', error);
        } finally {
            setLoading(false);
        }
    };

    const applyFiltersAndSort = () => {
        let filtered = [...tasks];

        // Apply status filter
        if (statusFilter !== 'ALL') {
            filtered = filtered.filter(task => task.status === statusFilter);
        }

        // Apply sorting
        if (sortBy === 'date') {
            filtered.sort((a, b) => new Date(b.createdDate) - new Date(a.createdDate));
        } else if (sortBy === 'priority') {
            const priorityOrder = { HIGH: 3, MEDIUM: 2, LOW: 1 };
            filtered.sort((a, b) => priorityOrder[b.priority] - priorityOrder[a.priority]);
        } else if (sortBy === 'scheduled') {
            filtered.sort((a, b) => new Date(a.scheduledDate) - new Date(b.scheduledDate));
        }

        setFilteredTasks(filtered);
    };

    const handleExportCSV = async () => {
        try {
            await taskService.exportTasksCSV();
        } catch (error) {
            console.error('Error exporting CSV:', error);
            alert('Failed to export CSV');
        }
    };

    if (loading) {
        return <div className="loading">Loading tasks...</div>;
    }

    return (
        <div className="task-list-page">
            <div className="task-list-header">
                <h1>All Tasks</h1>
                <div className="header-actions">
                    <button className="btn-export" onClick={handleExportCSV}>
                        📥 Export CSV
                    </button>
                    <button className="btn-create" onClick={() => navigate('/tasks/new')}>
                        ➕ Create Task
                    </button>
                </div>
            </div>

            <div className="filters-bar">
                <div className="filter-group">
                    <label>Status:</label>
                    <select value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
                        <option value="ALL">All</option>
                        <option value="PENDING">Pending</option>
                        <option value="APPROVED">Approved</option>
                        <option value="REJECTED">Rejected</option>
                    </select>
                </div>

                <div className="filter-group">
                    <label>Sort by:</label>
                    <select value={sortBy} onChange={(e) => setSortBy(e.target.value)}>
                        <option value="date">Created Date</option>
                        <option value="priority">Priority</option>
                        <option value="scheduled">Scheduled Date</option>
                    </select>
                </div>

                <div className="task-count">
                    {filteredTasks.length} task{filteredTasks.length !== 1 ? 's' : ''}
                </div>
            </div>

            <div className="tasks-grid">
                {filteredTasks.length === 0 ? (
                    <div className="no-tasks">
                        <p>No tasks found</p>
                    </div>
                ) : (
                    filteredTasks.map(task => (
                        <TaskCard
                            key={task.id}
                            task={task}
                            onApprove={taskService.approveTask}
                            onReject={taskService.rejectTask}
                            onRefresh={loadTasks}
                        />
                    ))
                )}
            </div>
        </div>
    );
};

export default TaskList;
