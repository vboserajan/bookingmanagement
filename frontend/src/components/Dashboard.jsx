import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { taskService } from '../services/taskService';
import { useAuth } from '../context/AuthContext';
import './Dashboard.css';

const Dashboard = () => {
    const [stats, setStats] = useState({
        pending: 0,
        approved: 0,
        rejected: 0,
    });
    const [recentTasks, setRecentTasks] = useState([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();
    const { user } = useAuth();

    useEffect(() => {
        loadDashboardData();
    }, []);

    const loadDashboardData = async () => {
        try {
            const allTasks = await taskService.getAllTasks();

            // Calculate stats
            const pending = allTasks.filter(t => t.status === 'PENDING').length;
            const approved = allTasks.filter(t => t.status === 'APPROVED').length;
            const rejected = allTasks.filter(t => t.status === 'REJECTED').length;

            setStats({ pending, approved, rejected });
            setRecentTasks(allTasks.slice(0, 5));
        } catch (error) {
            console.error('Error loading dashboard:', error);
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return <div className="loading">Loading dashboard...</div>;
    }

    return (
        <div className="dashboard">
            <div className="dashboard-header">
                <h1>Welcome back, {user?.name}! 👋</h1>
                <p>Here's what's happening with your tasks</p>
            </div>

            <div className="stats-grid">
                <div className="stat-card pending">
                    <div className="stat-icon">⏳</div>
                    <div className="stat-content">
                        <h3>{stats.pending}</h3>
                        <p>Pending Tasks</p>
                    </div>
                </div>

                <div className="stat-card approved">
                    <div className="stat-icon">✅</div>
                    <div className="stat-content">
                        <h3>{stats.approved}</h3>
                        <p>Approved Tasks</p>
                    </div>
                </div>

                <div className="stat-card rejected">
                    <div className="stat-icon">❌</div>
                    <div className="stat-content">
                        <h3>{stats.rejected}</h3>
                        <p>Rejected Tasks</p>
                    </div>
                </div>
            </div>

            <div className="quick-actions">
                <h2>Quick Actions</h2>
                <div className="action-buttons">
                    <button
                        className="action-btn create"
                        onClick={() => navigate('/tasks/new')}
                    >
                        <span className="action-icon">➕</span>
                        Create New Task
                    </button>
                    <button
                        className="action-btn view"
                        onClick={() => navigate('/tasks')}
                    >
                        <span className="action-icon">📋</span>
                        View All Tasks
                    </button>
                    <button
                        className="action-btn calendar"
                        onClick={() => navigate('/calendar')}
                    >
                        <span className="action-icon">📅</span>
                        Calendar View
                    </button>
                </div>
            </div>

            <div className="recent-tasks">
                <h2>Recent Tasks</h2>
                {recentTasks.length === 0 ? (
                    <p className="no-tasks">No tasks yet. Create your first task!</p>
                ) : (
                    <div className="task-list-simple">
                        {recentTasks.map(task => (
                            <div key={task.id} className="task-item-simple">
                                <div className="task-info">
                                    <h4>{task.title}</h4>
                                    <p>{task.description}</p>
                                </div>
                                <span className={`status-badge ${task.status.toLowerCase()}`}>
                                    {task.status}
                                </span>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
};

export default Dashboard;
