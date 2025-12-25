import React from 'react';
import { useAuth } from '../context/AuthContext';
import { format } from 'date-fns';
import './TaskCard.css';

const TaskCard = ({ task, onApprove, onReject, onRefresh }) => {
    const { isManager } = useAuth();
    const [loading, setLoading] = useState(false);

    const handleApprove = async () => {
        setLoading(true);
        try {
            await onApprove(task.id);
            onRefresh();
        } catch (error) {
            console.error('Error approving task:', error);
            alert('Failed to approve task');
        } finally {
            setLoading(false);
        }
    };

    const handleReject = async () => {
        setLoading(true);
        try {
            await onReject(task.id);
            onRefresh();
        } catch (error) {
            console.error('Error rejecting task:', error);
            alert('Failed to reject task');
        } finally {
            setLoading(false);
        }
    };

    const getPriorityColor = (priority) => {
        switch (priority) {
            case 'HIGH': return '#ef5350';
            case 'MEDIUM': return '#ffa726';
            case 'LOW': return '#66bb6a';
            default: return '#999';
        }
    };

    return (
        <div className="task-card">
            <div className="task-card-header">
                <h3>{task.title}</h3>
                <div className="task-badges">
                    <span
                        className="priority-badge"
                        style={{ backgroundColor: getPriorityColor(task.priority) }}
                    >
                        {task.priority}
                    </span>
                    <span className={`status-badge ${task.status.toLowerCase()}`}>
                        {task.status}
                    </span>
                </div>
            </div>

            <p className="task-description">{task.description}</p>

            <div className="task-details">
                <div className="detail-item">
                    <span className="detail-label">Assigned to:</span>
                    <span className="detail-value">{task.assignedUserName}</span>
                </div>
                <div className="detail-item">
                    <span className="detail-label">Created by:</span>
                    <span className="detail-value">{task.createdByName}</span>
                </div>
                <div className="detail-item">
                    <span className="detail-label">Scheduled:</span>
                    <span className="detail-value">
                        {format(new Date(task.scheduledDate), 'MMM dd, yyyy HH:mm')}
                    </span>
                </div>
                {task.approvedByName && (
                    <div className="detail-item">
                        <span className="detail-label">
                            {task.status === 'APPROVED' ? 'Approved' : 'Rejected'} by:
                        </span>
                        <span className="detail-value">{task.approvedByName}</span>
                    </div>
                )}
            </div>

            {isManager() && task.status === 'PENDING' && (
                <div className="task-actions">
                    <button
                        className="btn-approve"
                        onClick={handleApprove}
                        disabled={loading}
                    >
                        ✓ Approve
                    </button>
                    <button
                        className="btn-reject"
                        onClick={handleReject}
                        disabled={loading}
                    >
                        ✗ Reject
                    </button>
                </div>
            )}
        </div>
    );
};

// Add missing import
import { useState } from 'react';

export default TaskCard;
