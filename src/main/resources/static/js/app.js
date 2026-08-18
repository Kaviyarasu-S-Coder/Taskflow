/**
 * TaskFlow Enterprise Frontend Controller
 */

// Application State
const state = {
  token: localStorage.getItem('taskflow_token') || null,
  refreshToken: localStorage.getItem('taskflow_refresh_token') || null,
  user: JSON.parse(localStorage.getItem('taskflow_user') || 'null'),
  workspaces: [],
  projects: [],
  currentWorkspaceId: null,
  currentProjectId: null,
  tasks: [],
  activeTaskId: null
};

// API Helper with JWT Bearer Token Auto-Injection
async function apiCall(endpoint, options = {}) {
  const headers = {
    'Content-Type': 'application/json',
    ...(options.headers || {})
  };

  if (state.token) {
    headers['Authorization'] = `Bearer ${state.token}`;
  }

  try {
    const response = await fetch(endpoint, {
      ...options,
      headers
    });

    if (response.status === 401 && state.refreshToken && endpoint !== '/api/v1/auth/refresh') {
      // Attempt token refresh
      const refreshed = await attemptTokenRefresh();
      if (refreshed) {
        return apiCall(endpoint, options);
      }
    }

    if (!response.ok) {
      const err = await response.json().catch(() => ({ detail: 'Request failed with status ' + response.status }));
      throw new Error(err.detail || err.title || 'API Error');
    }

    if (response.status === 204) return null;
    return await response.json();
  } catch (error) {
    console.error('API Error:', error);
    throw error;
  }
}

async function attemptTokenRefresh() {
  try {
    const res = await fetch('/api/v1/auth/refresh', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ refreshToken: state.refreshToken })
    });
    if (res.ok) {
      const data = await res.json();
      state.token = data.accessToken;
      state.refreshToken = data.refreshToken;
      localStorage.setItem('taskflow_token', data.accessToken);
      localStorage.setItem('taskflow_refresh_token', data.refreshToken);
      return true;
    }
  } catch (e) {
    console.error('Refresh token failed:', e);
  }
  logout();
  return false;
}

// Toast Notifications
function showToast(message, type = 'success') {
  const container = document.getElementById('toast-container');
  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;
  toast.textContent = message;
  container.appendChild(toast);
  setTimeout(() => {
    toast.style.opacity = '0';
    setTimeout(() => toast.remove(), 200);
  }, 3500);
}

// Authentication Handlers
function updateAuthUI() {
  const userNameEl = document.getElementById('user-name');
  const userRoleEl = document.getElementById('user-role');
  const userAvatarEl = document.getElementById('user-avatar');
  const authBtn = document.getElementById('auth-btn');

  if (state.token && state.user) {
    userNameEl.textContent = `${state.user.firstName || ''} ${state.user.lastName || ''}`.trim() || state.user.email;
    userRoleEl.textContent = state.user.roles ? state.user.roles.join(', ') : 'Member';
    userAvatarEl.textContent = (state.user.firstName ? state.user.firstName[0] : 'U').toUpperCase();
    authBtn.textContent = 'Logout';
    authBtn.onclick = logout;
  } else {
    userNameEl.textContent = 'Guest User';
    userRoleEl.textContent = 'Click below to Sign In';
    userAvatarEl.textContent = 'TF';
    authBtn.textContent = 'Login / Register';
    authBtn.onclick = () => openModal('auth-modal');
  }
}

function logout() {
  state.token = null;
  state.refreshToken = null;
  state.user = null;
  localStorage.removeItem('taskflow_token');
  localStorage.removeItem('taskflow_refresh_token');
  localStorage.removeItem('taskflow_user');
  updateAuthUI();
  showToast('Logged out successfully');
  document.getElementById('col-BACKLOG').innerHTML = '';
  document.getElementById('col-IN_PROGRESS').innerHTML = '';
  document.getElementById('col-IN_REVIEW').innerHTML = '';
  document.getElementById('col-DONE').innerHTML = '';
  openModal('auth-modal');
}

// Modals Management
function openModal(id) {
  const el = document.getElementById(id);
  if (el) el.classList.add('open');
}

function closeModal(id) {
  const el = document.getElementById(id);
  if (el) el.classList.remove('open');
}

// Initialize Application Data
async function initApp() {
  updateAuthUI();

  if (!state.token) {
    openModal('auth-modal');
    return;
  }

  try {
    await loadWorkspaces();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

// Workspaces & Projects
async function loadWorkspaces() {
  const select = document.getElementById('workspace-select');
  const projWksSelect = document.getElementById('proj-workspace-select');
  select.innerHTML = '<option value="">Loading...</option>';

  try {
    const orgId = state.user?.organizationId || 1;
    const workspaces = await apiCall(`/api/v1/workspaces/org/${orgId}`).catch(() => []);
    state.workspaces = workspaces || [];

    select.innerHTML = '';
    projWksSelect.innerHTML = '';

    if (state.workspaces.length === 0) {
      // Auto-create default workspace if none exists
      const defaultWks = await apiCall('/api/v1/workspaces', {
        method: 'POST',
        body: JSON.stringify({
          organizationId: orgId,
          name: 'Primary Workspace',
          description: 'Main production workspace'
        })
      });
      state.workspaces = [defaultWks];
    }

    state.workspaces.forEach(w => {
      const opt = document.createElement('option');
      opt.value = w.id;
      opt.textContent = w.name;
      select.appendChild(opt);

      const opt2 = opt.cloneNode(true);
      projWksSelect.appendChild(opt2);
    });

    state.currentWorkspaceId = state.workspaces[0].id;
    select.value = state.currentWorkspaceId;
    await loadProjects(state.currentWorkspaceId);
  } catch (err) {
    console.error('Failed to load workspaces:', err);
    select.innerHTML = '<option value="">No Workspaces</option>';
  }
}

async function loadProjects(workspaceId) {
  const select = document.getElementById('project-select');
  select.innerHTML = '<option value="">Loading...</option>';

  try {
    const projects = await apiCall(`/api/v1/projects/workspace/${workspaceId}`).catch(() => []);
    state.projects = projects || [];
    select.innerHTML = '';

    if (state.projects.length === 0) {
      // Auto-create initial project
      const initialProject = await apiCall('/api/v1/projects', {
        method: 'POST',
        body: JSON.stringify({
          workspaceId: workspaceId,
          keyPrefix: 'CORE',
          name: 'Core Platform',
          description: 'Core microservices and backend API engine',
          leadId: state.user?.id || 1
        })
      });
      state.projects = [initialProject];
    }

    state.projects.forEach(p => {
      const opt = document.createElement('option');
      opt.value = p.id;
      opt.textContent = `${p.name} (${p.keyPrefix})`;
      select.appendChild(opt);
    });

    state.currentProjectId = state.projects[0].id;
    select.value = state.currentProjectId;
    await loadTasks(state.currentProjectId);
  } catch (err) {
    console.error('Failed to load projects:', err);
    select.innerHTML = '<option value="">No Projects</option>';
  }
}

// Tasks & Kanban Rendering
async function loadTasks(projectId) {
  try {
    const tasks = await apiCall(`/api/v1/tasks/project/${projectId}`).catch(() => []);
    state.tasks = tasks || [];
    renderKanban();
  } catch (err) {
    showToast('Failed to load project tasks: ' + err.message, 'error');
  }
}

function renderKanban() {
  const columns = {
    BACKLOG: document.getElementById('col-BACKLOG'),
    IN_PROGRESS: document.getElementById('col-IN_PROGRESS'),
    IN_REVIEW: document.getElementById('col-IN_REVIEW'),
    DONE: document.getElementById('col-DONE')
  };

  const counts = { BACKLOG: 0, IN_PROGRESS: 0, IN_REVIEW: 0, DONE: 0 };
  Object.values(columns).forEach(col => col.innerHTML = '');

  state.tasks.forEach(task => {
    const status = task.status || 'BACKLOG';
    if (counts[status] !== undefined) counts[status]++;

    const card = createTaskCard(task);
    if (columns[status]) {
      columns[status].appendChild(card);
    } else {
      columns.BACKLOG.appendChild(card);
    }
  });

  // Update Counters & Stats
  document.getElementById('count-BACKLOG').textContent = counts.BACKLOG;
  document.getElementById('count-IN_PROGRESS').textContent = counts.IN_PROGRESS;
  document.getElementById('count-IN_REVIEW').textContent = counts.IN_REVIEW;
  document.getElementById('count-DONE').textContent = counts.DONE;

  document.getElementById('stat-total').textContent = state.tasks.length;
  document.getElementById('stat-inprogress').textContent = counts.IN_PROGRESS;
  document.getElementById('stat-review').textContent = counts.IN_REVIEW;
  document.getElementById('stat-done').textContent = counts.DONE;
}

function createTaskCard(task) {
  const card = document.createElement('div');
  card.className = 'task-card';
  card.onclick = () => openTaskDetails(task.id);

  const priorityClass = `badge-${(task.priority || 'MEDIUM').toLowerCase()}`;

  card.innerHTML = `
    <div class="task-card-header">
      <span class="task-key">${task.formattedTaskKey || 'TSK-' + task.taskNumber}</span>
      <span class="badge ${priorityClass}">${task.priority || 'MEDIUM'}</span>
    </div>
    <div class="task-title">${escapeHtml(task.title)}</div>
    ${task.description ? `<div class="task-desc-preview">${escapeHtml(task.description)}</div>` : ''}
    <div class="task-footer">
      <div class="task-meta">
        <span>⏱️ ${task.loggedHours || 0}/${task.estimatedHours || 0}h</span>
      </div>
      <div class="task-meta">
        <span>🎯 ${task.storyPoints || 0} pts</span>
      </div>
    </div>
  `;

  return card;
}

// Task Details Modal
async function openTaskDetails(taskId) {
  state.activeTaskId = taskId;
  try {
    const task = await apiCall(`/api/v1/tasks/${taskId}`);
    document.getElementById('detail-task-key').textContent = task.formattedTaskKey || `TASK-${task.taskNumber}`;
    document.getElementById('detail-task-title').textContent = task.title;
    document.getElementById('detail-task-desc').textContent = task.description || 'No description provided.';
    document.getElementById('detail-task-status').value = task.status || 'BACKLOG';
    document.getElementById('detail-logged-hours').value = task.loggedHours || 0;
    
    const priorityEl = document.getElementById('detail-task-priority');
    priorityEl.textContent = task.priority || 'MEDIUM';
    priorityEl.className = `badge badge-${(task.priority || 'MEDIUM').toLowerCase()}`;

    // Render Dependencies
    const depList = document.getElementById('detail-dependencies-list');
    if (task.dependencies && task.dependencies.length > 0) {
      depList.innerHTML = task.dependencies.map(d => 
        `<div style="padding: 4px 8px; background: rgba(255,255,255,0.05); border-radius: 4px; margin-bottom: 4px;">
          🔗 Depends on Task ID: <b>#${d.successorId}</b> (${d.dependencyType})
        </div>`
      ).join('');
    } else {
      depList.textContent = 'No dependencies linked to this task.';
    }

    // Load Comments
    await loadComments(taskId);

    openModal('task-detail-modal');
  } catch (err) {
    showToast('Failed to load task details: ' + err.message, 'error');
  }
}

async function loadComments(taskId) {
  const container = document.getElementById('detail-comments-list');
  container.innerHTML = '<div style="font-size:0.75rem; color:var(--text-muted);">Loading comments...</div>';
  try {
    const comments = await apiCall(`/api/v1/comments/task/${taskId}`).catch(() => []);
    if (!comments || comments.length === 0) {
      container.innerHTML = '<div style="font-size:0.8rem; color:var(--text-muted);">No comments yet. Start the conversation!</div>';
      return;
    }
    container.innerHTML = comments.map(c => `
      <div class="comment-item">
        <div class="comment-meta">${c.commentCode || 'Comment'} • ${new Date(c.createdAt || Date.now()).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})}</div>
        <div>${escapeHtml(c.content)}</div>
      </div>
    `).join('');
  } catch (err) {
    container.innerHTML = '<div style="font-size:0.8rem; color:var(--priority-critical);">Could not load comments.</div>';
  }
}

// Event Listeners Registration
document.addEventListener('DOMContentLoaded', () => {
  initApp();

  // Auth Tab Toggles
  const tabLogin = document.getElementById('tab-login');
  const tabRegister = document.getElementById('tab-register');
  const loginForm = document.getElementById('login-form');
  const registerForm = document.getElementById('register-form');
  const authModalTitle = document.getElementById('auth-modal-title');

  tabLogin.onclick = () => {
    tabLogin.className = 'btn btn-primary';
    tabRegister.className = 'btn btn-secondary';
    loginForm.style.display = 'flex';
    registerForm.style.display = 'none';
    authModalTitle.textContent = 'Sign In to TaskFlow';
  };

  tabRegister.onclick = () => {
    tabRegister.className = 'btn btn-primary';
    tabLogin.className = 'btn btn-secondary';
    registerForm.style.display = 'flex';
    loginForm.style.display = 'none';
    authModalTitle.textContent = 'Register New Account';
  };

  // Login Submit
  loginForm.onsubmit = async (e) => {
    e.preventDefault();
    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;

    try {
      const data = await apiCall('/api/v1/auth/login', {
        method: 'POST',
        body: JSON.stringify({ email, password })
      });
      const userObj = {
        id: data.userId,
        email: data.email,
        firstName: data.firstName,
        lastName: data.lastName,
        roles: data.roles || [],
        organizationId: 1
      };
      state.token = data.accessToken;
      state.refreshToken = data.refreshToken;
      state.user = userObj;
      localStorage.setItem('taskflow_token', data.accessToken);
      localStorage.setItem('taskflow_refresh_token', data.refreshToken);
      localStorage.setItem('taskflow_user', JSON.stringify(userObj));
      
      closeModal('auth-modal');
      showToast(`Welcome back, ${userObj.firstName}!`);
      updateAuthUI();
      loadWorkspaces();
    } catch (err) {
      showToast(err.message, 'error');
    }
  };

  // Register Submit
  registerForm.onsubmit = async (e) => {
    e.preventDefault();
    const firstName = document.getElementById('reg-firstname').value;
    const lastName = document.getElementById('reg-lastname').value;
    const organizationName = document.getElementById('reg-org').value;
    const email = document.getElementById('reg-email').value;
    const password = document.getElementById('reg-password').value;

    try {
      const data = await apiCall('/api/v1/auth/register', {
        method: 'POST',
        body: JSON.stringify({ firstName, lastName, organizationName, email, password })
      });
      const userObj = {
        id: data.userId,
        email: data.email,
        firstName: data.firstName,
        lastName: data.lastName,
        roles: data.roles || [],
        organizationId: 1
      };
      state.token = data.accessToken;
      state.refreshToken = data.refreshToken;
      state.user = userObj;
      localStorage.setItem('taskflow_token', data.accessToken);
      localStorage.setItem('taskflow_refresh_token', data.refreshToken);
      localStorage.setItem('taskflow_user', JSON.stringify(userObj));

      closeModal('auth-modal');
      showToast('Account registered successfully!');
      updateAuthUI();
      loadWorkspaces();
    } catch (err) {
      showToast(err.message, 'error');
    }
  };

  // Close Modal Buttons
  document.getElementById('close-auth-modal').onclick = () => closeModal('auth-modal');
  document.getElementById('close-proj-modal').onclick = () => closeModal('project-modal');
  document.getElementById('cancel-proj-btn').onclick = () => closeModal('project-modal');
  document.getElementById('close-task-modal').onclick = () => closeModal('task-modal');
  document.getElementById('cancel-task-btn').onclick = () => closeModal('task-modal');
  document.getElementById('close-detail-modal').onclick = () => closeModal('task-detail-modal');

  // New Project Button & Form
  document.getElementById('btn-new-project').onclick = () => openModal('project-modal');
  document.getElementById('project-form').onsubmit = async (e) => {
    e.preventDefault();
    const name = document.getElementById('proj-name').value;
    const keyPrefix = document.getElementById('proj-prefix').value.toUpperCase();
    const workspaceId = document.getElementById('proj-workspace-select').value;
    const description = document.getElementById('proj-desc').value;

    try {
      const proj = await apiCall('/api/v1/projects', {
        method: 'POST',
        body: JSON.stringify({
          workspaceId: Number(workspaceId),
          keyPrefix,
          name,
          description,
          leadId: state.user?.id || 1
        })
      });
      closeModal('project-modal');
      showToast(`Project ${proj.name} created!`);
      loadProjects(workspaceId);
    } catch (err) {
      showToast(err.message, 'error');
    }
  };

  // New Task Button & Form
  document.getElementById('btn-create-task').onclick = () => {
    if (!state.currentProjectId) {
      showToast('Please select or create a project first', 'error');
      return;
    }
    openModal('task-modal');
  };

  document.getElementById('task-form').onsubmit = async (e) => {
    e.preventDefault();
    const title = document.getElementById('task-title').value;
    const description = document.getElementById('task-desc').value;
    const priority = document.getElementById('task-priority').value;
    const taskType = document.getElementById('task-type').value;
    const storyPoints = Number(document.getElementById('task-points').value) || 0;
    const estimatedHours = Number(document.getElementById('task-est-hours').value) || 0;

    try {
      const task = await apiCall('/api/v1/tasks', {
        method: 'POST',
        body: JSON.stringify({
          projectId: state.currentProjectId,
          title,
          description,
          priority,
          taskType,
          storyPoints,
          estimatedHours,
          reporterId: state.user?.id || 1,
          assigneeId: state.user?.id || 1
        })
      });
      closeModal('task-modal');
      showToast(`Task ${task.formattedTaskKey || 'created'}!`);
      loadTasks(state.currentProjectId);
    } catch (err) {
      showToast(err.message, 'error');
    }
  };

  // Status & Hours Update
  document.getElementById('btn-save-status').onclick = async () => {
    if (!state.activeTaskId) return;
    const status = document.getElementById('detail-task-status').value;
    const loggedHours = Number(document.getElementById('detail-logged-hours').value) || 0;

    try {
      await apiCall(`/api/v1/tasks/${state.activeTaskId}`, {
        method: 'PUT',
        body: JSON.stringify({ status, loggedHours })
      });
      showToast('Task updated successfully');
      loadTasks(state.currentProjectId);
    } catch (err) {
      showToast(err.message, 'error');
    }
  };

  // Add Comment
  document.getElementById('comment-form').onsubmit = async (e) => {
    e.preventDefault();
    const input = document.getElementById('comment-input');
    const content = input.value.trim();
    if (!content || !state.activeTaskId) return;

    try {
      await apiCall('/api/v1/comments', {
        method: 'POST',
        body: JSON.stringify({
          taskId: state.activeTaskId,
          authorId: state.user?.id || 1,
          content
        })
      });
      input.value = '';
      showToast('Comment added');
      loadComments(state.activeTaskId);
    } catch (err) {
      showToast(err.message, 'error');
    }
  };

  // Add Dependency
  document.getElementById('add-dep-form').onsubmit = async (e) => {
    e.preventDefault();
    const predecessorId = Number(document.getElementById('dep-predecessor-id').value);
    if (!predecessorId || !state.activeTaskId) return;

    try {
      await apiCall(`/api/v1/tasks/${state.activeTaskId}/dependencies`, {
        method: 'POST',
        body: JSON.stringify({
          predecessorId: predecessorId,
          dependencyType: 'BLOCKS'
        })
      });
      document.getElementById('dep-predecessor-id').value = '';
      showToast('Dependency linked!');
      openTaskDetails(state.activeTaskId);
    } catch (err) {
      showToast(err.message, 'error');
    }
  };

  // Workspace & Project Select Change
  document.getElementById('workspace-select').onchange = (e) => {
    state.currentWorkspaceId = Number(e.target.value);
    loadProjects(state.currentWorkspaceId);
  };

  document.getElementById('project-select').onchange = (e) => {
    state.currentProjectId = Number(e.target.value);
    loadTasks(state.currentProjectId);
  };
});

function escapeHtml(str) {
  if (!str) return '';
  return str.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;");
}
