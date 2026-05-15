import api from './axios';

// ── Auth ──────────────────────────────────────────
export const login = (data) => api.post('/auth/login', data);
export const register = (data) => api.post('/auth/register', data);

// ── Applications ──────────────────────────────────
export const getApplications = () => api.get('/applications');
export const getApplication = (id) => api.get(`/applications/${id}`);
export const createApplication = (data) => api.post('/applications', data);
export const updateApplication = (id, data) => api.put(`/applications/${id}`, data);
export const deleteApplication = (id) => api.delete(`/applications/${id}`);
export const updateStage = (id, stage) => api.patch(`/applications/${id}/stage`, { stage });

// ── Contacts ──────────────────────────────────────
export const getContacts = () => api.get('/contacts');
export const createContact = (data) => api.post('/contacts', data);
export const updateContact = (id, data) => api.put(`/contacts/${id}`, data);
export const deleteContact = (id) => api.delete(`/contacts/${id}`);

// ── AI ────────────────────────────────────────────
export const uploadResume = (resumeText) => api.post('/ai/resume', { resumeText });
export const getResume = () => api.get('/ai/resume');
export const parseJd = (description) => api.post('/ai/parse-jd', { description });
export const scoreApplication = (id) => api.post(`/ai/score/${id}`);

// ── Emails ────────────────────────────────────────
export const getEmails = () => api.get('/emails');
export const getEmail = (id) => api.get(`/emails/${id}`);
export const updateEmail = (id, data) => api.patch(`/emails/${id}`, data);
export const sendEmail = (id, recipientEmail) => api.post(`/emails/${id}/send`, { recipientEmail });