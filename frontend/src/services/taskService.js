import api from './api';

export const taskService = {
    createTask: async (taskData) => {
        const response = await api.post('/tasks', taskData);
        return response.data;
    },

    getAllTasks: async (filters = {}) => {
        const params = new URLSearchParams();
        if (filters.status) params.append('status', filters.status);
        if (filters.assignedUserId) params.append('assignedUserId', filters.assignedUserId);
        if (filters.createdBy) params.append('createdBy', filters.createdBy);

        const response = await api.get(`/tasks?${params.toString()}`);
        return response.data;
    },

    getTaskById: async (id) => {
        const response = await api.get(`/tasks/${id}`);
        return response.data;
    },

    approveTask: async (id) => {
        const response = await api.put(`/tasks/${id}/approve`);
        return response.data;
    },

    rejectTask: async (id) => {
        const response = await api.put(`/tasks/${id}/reject`);
        return response.data;
    },

    getTasksForCalendar: async (start, end) => {
        const response = await api.get('/tasks/calendar', {
            params: { start, end },
        });
        return response.data;
    },

    exportTasksCSV: async () => {
        const response = await api.get('/tasks/export/csv', {
            responseType: 'blob',
        });

        // Create download link
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', 'tasks.csv');
        document.body.appendChild(link);
        link.click();
        link.remove();
    },
};
