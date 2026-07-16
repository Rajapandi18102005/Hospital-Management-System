// Global Configuration and Helpers
const API_BASE_URL = '/api';

// Notification System
function showToast(message, type = 'success') {
    const existingToast = document.getElementById('toast-notification');
    if (existingToast) existingToast.remove();

    const toast = document.createElement('div');
    toast.id = 'toast-notification';
    toast.className = `notification-banner show ${type}`;

    let icon = '🔔';
    if (type === 'success') icon = '✅';
    if (type === 'error')   icon = '❌';
    if (type === 'warning') icon = '⚠️';

    toast.innerHTML = `<span>${icon}</span> <span style="font-weight: 500;">${message}</span>`;
    document.body.appendChild(toast);

    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 400);
    }, 4000);
}

// Session Management
const Auth = {
    saveUser(user) {
        localStorage.setItem('hms_user', JSON.stringify(user));
    },

    getUser() {
        const raw = localStorage.getItem('hms_user');
        return raw ? JSON.parse(raw) : null;
    },

    // Store raw password (base64) for HTTP Basic auth headers
    saveCredentials(username, password) {
        // btoa encodes "username:password" to Base64
        localStorage.setItem('hms_creds', btoa(`${username}:${password}`));
    },

    getCredentials() {
        return localStorage.getItem('hms_creds');
    },

    logout() {
        localStorage.removeItem('hms_user');
        localStorage.removeItem('hms_creds');
        showToast('Logged out successfully. Redirecting...', 'success');
        setTimeout(() => { window.location.href = 'login.html'; }, 1000);
    },

    checkAuth(allowedRoles = []) {
        const user = this.getUser();
        if (!user) {
            window.location.href = 'login.html';
            return null;
        }
        if (allowedRoles.length > 0 && !allowedRoles.includes(user.role.toUpperCase())) {
            showToast('Access denied! Redirecting to dashboard...', 'error');
            setTimeout(() => { this.redirectToDashboard(user.role); }, 1500);
            return null;
        }
        return user;
    },

    redirectToDashboard(role) {
        if (!role) return;
        const r = role.toUpperCase();
        if (r === 'PATIENT')      window.location.href = 'patient.html';
        else if (r === 'DOCTOR')  window.location.href = 'doctor.html';
        else if (r === 'ADMIN')   window.location.href = 'admin.html';
        else                      window.location.href = 'index.html';
    }
};

// ── Fetch API wrapper ─────────────────────────────────────────────────────────
// Automatically attaches HTTP Basic auth header for protected endpoints.
async function apiRequest(endpoint, options = {}) {
    const url = `${API_BASE_URL}${endpoint}`;

    // Build headers
    const headers = { 'Content-Type': 'application/json', ...options.headers };

    // Attach HTTP Basic credentials for all requests except register/login
    const isAuthEndpoint = endpoint === '/users/register' || endpoint === '/users/login';
    const creds = Auth.getCredentials();
    if (!isAuthEndpoint && creds) {
        headers['Authorization'] = `Basic ${creds}`;
    }

    options.headers = headers;

    try {
        const response = await fetch(url, options);
        const data = await response.json();

        if (!response.ok) {
            // 401 = credentials expired/invalid → force re-login
            if (response.status === 401) {
                Auth.logout();
                throw new Error('Session expired. Please login again.');
            }
            throw new Error(data.message || 'Something went wrong');
        }
        return data;
    } catch (error) {
        console.error(`API Error on ${endpoint}:`, error);
        throw error;
    }
}
