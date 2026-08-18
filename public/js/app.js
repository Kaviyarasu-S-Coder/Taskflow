/**
 * TaskFlow Enterprise Frontend Controller
 * Supports both Live Spring Boot REST API & Interactive Demo Mode (for Vercel).
 */

// Application State
const state = {
  token: localStorage.getItem('taskflow_token') || null,
  refreshToken: localStorage.getItem('taskflow_refresh_token') || null,
  user: JSON.parse(localStorage.getItem('taskflow_user') || 'null'),
  apiBase: localStorage.getItem('taskflow_api_url') || (window.TASKFLOW_API_URL || ''),
  demoMode: localStorage.getItem('taskflow_demo_mode') === 'true' || 
            (window.location.hostname.includes('vercel.app') && !localStorage.getItem('taskflow_api_url')),
  workspaces: [],
  projects: [],
  currentWorkspaceId: null,
  currentProjectId: null,
  tasks: [],
  activeTaskId: null
};

// ── MOCK DATA STORE (For Instant Demo on Vercel) ───────────────────────────
const MOCK_STORAGE_KEY = 'taskflow_mock_data_v1';

function getMockStore() {
  let store = JSON.parse(localStorage.getItem(MOCK_STORAGE_KEY) || 'null');
  if (!store) {
    store = {
      workspaces: [
        { id: 1, name: 'Core Engineering', description: 'Primary engineering workspace', organizationId: 1 }
      ],
      projects: [
        { id: 1, workspaceId: 1, keyPrefix: 'ENG', name: 'TaskFlow Platform', description: 'Main microservices & dashboard', leadId: 1, nextTaskSeq: 5 }
      ],
      tasks: [
        { id: 1, projectId: 1, taskNumber: 1, taskCode: 'ENG-1', formattedTaskKey: 'ENG-1', title: 'Design Microservice Architecture', description: 'Design DDD bounded contexts, ports & adapters', priority: 'HIGH', taskType: 'STORY', status: 'IN_PROGRESS', storyPoints: 8, estimatedHours: 16.0, loggedHours: 8.0, reporterId: 1, assigneeId: 1 },
        { id: 2, projectId: 1, taskNumber: 2, taskCode: 'ENG-2', formattedTaskKey: 'ENG-2', title: 'Implement JWT Auth & Refresh Tokens', description: 'HMAC-SHA256 token provider and Spring Security filters', priority: 'CRITICAL', taskType: 'FEATURE', status: 'DONE', storyPoints: 5, estimatedHours: 10.0, loggedHours: 10.0, reporterId: 1, assigneeId: 1 },
        { id: 3, projectId: 1, taskNumber: 3, taskCode: 'ENG-3', formattedTaskKey: 'ENG-3', title: 'Add Kanban Board Drag and Drop UI', description: 'Interactive dark glassmorphism dashboard with filters', priority: 'MEDIUM', taskType: 'STORY', status: 'IN_REVIEW', storyPoints: 3, estimatedHours: 6.0, loggedHours: 5.0, reporterId: 1, assigneeId: 1 },
        { id: 4, projectId: 1, taskNumber: 4, taskCode: 'ENG-4', formattedTaskKey: 'ENG-4', title: 'Setup Multi-Stage Docker & Kubernetes', description: 'Dockerfile with Eclipse Temurin JRE 21 and docker-compose', priority: 'LOW', taskType: 'TASK', status: 'BACKLOG', storyPoints: 2, estimatedHours: 4.0, loggedHours: 0.0, reporterId: 1, assigneeId: 1 }
      ],
      comments: [
        { id: 1, taskId: 1, authorId: 1, commentCode: 'CMT-101', content: 'Architecture draft approved for sprint kickoff.', createdAt: new Date().toISOString() }
      ],
      dependencies: []
    };
    saveMockStore(store);
  }
  return store;
}

function saveMockStore(store) {
  localStorage.setItem(MOCK_STORAGE_KEY, JSON.stringify(store));
}

// ── MOCK API DISPATCHER ───────────────────────────────────────────────────
function handleMockApi(endpoint, options = {}) {
  const method = (options.method || 'GET').toUpperCase();
  const body = options.body ? JSON.parse(options.body) : {};
  const store = getMockStore();

  // Auth: Login / Register
  if (endpoint === '/api/v1/auth/login' || endpoint === '/api/v1/auth/register') {
    const user = {
      userId: 1,
      id: 1,
      email: body.email || 'admin@taskflow.com',
      firstName: body.firstName || 'Kavi',
      lastName: body.lastName || 'Admin',
      roles: ['ROLE_SUPER_ADMIN', 'ROLE_DEVELOPER'],
      organizationId: 1
    };
    return Promise.resolve({
      accessToken: 'demo_jwt_token_' + Date.now(),
      refreshToken: 'demo_refresh_token_' + Date.now(),
      ...user
    });
  }

  // Workspaces
  if (endpoint.startsWith('/api/v1/workspaces/org/')) {
    return Promise.resolve(store.workspaces);
  }
  if (endpoint === '/api/v1/workspaces' && method === 'POST') {
    const newWks = {
      id: store.workspaces.length + 1,
      name: body.name,
      description: body.description,
      organizationId: body.organizationId || 1
    };
    store.workspaces.push(newWks);
    saveMockStore(store);
    return Promise.resolve(newWks);
  }

  // Projects
  if (endpoint.startsWith('/api/v1/projects/workspace/')) {
    const wksId = parseInt(endpoint.split('/').pop());
    return Promise.resolve(store.projects.filter(p => p.workspaceId === wksId));
  }
  if (endpoint === '/api/v1/projects' && method === 'POST') {
    const newProj = {
      id: store.projects.length + 1,
      workspaceId: body.workspaceId,
      keyPrefix: (body.keyPrefix || 'PROJ').toUpperCase(),
      name: body.name,
      description: body.description || '',
      leadId: body.leadId || 1,
      nextTaskSeq: 1
    };
    store.projects.push(newProj);
    saveMockStore(store);
    return Promise.resolve(newProj);
  }

  // Tasks
  if (endpoint.startsWith('/api/v1/tasks/project/')) {
    const projId = parseInt(endpoint.split('/').pop());
    return Promise.resolve(store.tasks.filter(t => t.projectId === projId));
  }
  if (endpoint === '/api/v1/tasks' && method === 'POST') {
    const proj = store.projects.find(p => p.id === body.projectId) || store.projects[0];
    const seq = proj ? proj.nextTaskSeq++ : store.tasks.length + 1;
    const prefix = proj ? proj.keyPrefix : 'TSK';
    const newTask = {
      id: Date.now(),
      projectId: body.projectId,
      taskNumber: seq,
      taskCode: `${prefix}-${seq}`,
      formattedTaskKey: `${prefix}-${seq}`,
      title: body.title,
      description: body.description || '',
      priority: body.priority || 'MEDIUM',
      taskType: body.taskType || 'TASK',
      status: 'BACKLOG',
      storyPoints: body.storyPoints || 1,
      estimatedHours: body.estimatedHours || 0,
      loggedHours: 0,
      reporterId: state.user?.id || 1,
      assigneeId: body.assigneeId || state.user?.id || 1
    };
    store.tasks.push(newTask);
    saveMockStore(store);
    return Promise.resolve(newTask);
  }
  if (endpoint.match(/\/api\/v1\/tasks\/(\d+)\/status/) && method === 'PATCH') {
    const id = parseInt(endpoint.match(/\/api\/v1\/tasks\/(\d+)\/status/)[1]);
    const task = store.tasks.find(t => t.id === id);
    if (task) task.status = body.status;
    saveMockStore(store);
    return Promise.resolve(task);
  }
  if (endpoint.match(/\/api\/v1\/tasks\/(\d+)\/log-hours/) && method === 'POST') {
    const id = parseInt(endpoint.match(/\/api\/v1\/tasks\/(\d+)\/log-hours/)[1]);
    const task = store.tasks.find(t => t.id === id);
    if (task) task.loggedHours = (task.loggedHours || 0) + (body.hours || 0);
    saveMockStore(store);
    return Promise.resolve(task);
  }

  // Comments
  if (endpoint.startsWith('/api/v1/comments/task/')) {
    const taskId = parseInt(endpoint.split('/').pop());
    return Promise.resolve(store.comments.filter(c => c.taskId === taskId));
  }
  if (endpoint === '/api/v1/comments' && method === 'POST') {
    const newComment = {
      id: Date.now(),
      taskId: body.taskId,
      authorId: body.authorId || 1,
      commentCode: 'CMT-' + Math.random().toString(36).substring(2, 7).toUpperCase(),
      content: body.content,
      createdAt: new Date().toISOString()
    };
    store.comments.push(newComment);
    saveMockStore(store);
    return Promise.resolve(newComment);
  }

  // Dependencies & Notifications Fallbacks
  if (endpoint.startsWith('/api/v1/dependencies/task/')) {
    return Promise.resolve([]);
  }
  if (endpoint.startsWith('/api/v1/notifications/')) {
    return Promise.resolve([]);
  }

  return Promise.resolve({});
}

// ── API HELPER ────────────────────────────────────────────────────────────
async function apiCall(endpoint, options = {}) {
  if (state.demoMode) {
    return handleMockApi(endpoint, options);
  }

  const headers = {
    'Content-Type': 'application/json',
    ...(options.headers || {})
  };

  if (state.token) {
    headers['Authorization'] = `Bearer ${state.token}`;
  }

  const base = state.apiBase || '';
  const url = endpoint.startsWith('http') ? endpoint : `${base}${endpoint}`;

  try {
    const response = await fetch(url, { ...options, headers });

    if (response.status === 401 && state.refreshToken && endpoint !== '/api/v1/auth/refresh') {
      const refreshed = await attemptTokenRefresh();
      if (refreshed) {
        return apiCall(endpoint, options);
      }
    }

    if (!response.ok) {
      if (response.status === 404 && !state.apiBase && window.location.hostname.includes('vercel.app')) {
        // Fallback gracefully to demo mode on Vercel
        console.warn('Backend API returned 404 on Vercel. Auto-enabling Interactive Demo Mode.');
        setDemoMode(true);
        showToast('Running in Interactive Demo Mode on Vercel!', 'info');
        return handleMockApi(endpoint, options);
      }
      const err = await response.json().catch(() => ({ detail: `Request failed with status ${response.status}` }));
      throw new Error(err.detail || err.title || `API Error (${response.status})`);
    }

    if (response.status === 204) return null;
    return await response.json();
  } catch (error) {
    if (!state.apiBase && window.location.hostname.includes('vercel.app')) {
      setDemoMode(true);
      showToast('Offline / Demo Mode active', 'info');
      return handleMockApi(endpoint, options);
    }
    console.error('API Error:', error);
    throw error;
  }
}

async function attemptTokenRefresh() {
  if (state.demoMode) return true;
  try {
    const base = state.apiBase || '';
    const res = await fetch(`${base}/api/v1/auth/refresh`, {
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

// ── TOAST NOTIFICATIONS ───────────────────────────────────────────────────
function showToast(message, type = 'success') {
  const container = document.getElementById('toast-container');
  if (!container) return;
  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;
  toast.textContent = message;
  container.appendChild(toast);
  setTimeout(() => {
    toast.style.opacity = '0';
    setTimeout(() => toast.remove(), 200);
  }, 3500);
}

// ── AUTHENTICATION ────────────────────────────────────────────────────────
function updateAuthUI() {
  const userNameEl = document.getElementById('user-name');
  const userRoleEl = document.getElementById('user-role');
  const userAvatarEl = document.getElementById('user-avatar');
  const authBtn = document.getElementById('auth-btn');

  if (state.user) {
    userNameEl.textContent = `${state.user.firstName || ''} ${state.user.lastName || ''}`.trim() || state.user.email;
    userRoleEl.textContent = (state.user.roles && state.user.roles[0]) ? state.user.roles[0].replace('ROLE_', '') : 'User';
    userAvatarEl.textContent = (state.user.firstName ? state.user.firstName[0] : 'U').toUpperCase();
    authBtn.textContent = 'Logout';
    authBtn.onclick = logout;
  } else {
    userNameEl.textContent = 'Guest User';
    userRoleEl.textContent = state.demoMode ? 'Demo Mode Active' : 'Not authenticated';
    userAvatarEl.textContent = 'TF';
    authBtn.textContent = 'Login / Register';
    authBtn.onclick = () => openModal('auth-modal');
  }

  updateApiStatusUI();
}

function updateApiStatusUI() {
  const dot = document.getElementById('api-status-dot');
  const text = document.getElementById('api-status-text');
  if (!dot || !text) return;

  if (state.demoMode) {
    dot.style.background = '#38bdf8'; // Blue for demo
    text.textContent = 'Demo Mode';
  } else if (state.apiBase) {
    dot.style.background = '#10b981'; // Green for custom remote API
    text.textContent = 'Live Remote API';
  } else {
    dot.style.background = '#10b981';
    text.textContent = 'Local Server (8080)';
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
  ['BACKLOG', 'IN_PROGRESS', 'IN_REVIEW', 'DONE'].forEach(col => {
    const el = document.getElementById(`col-${col}`);
    if (el) el.innerHTML = '';
  });
  openModal('auth-modal');
}

function setDemoMode(enable) {
  state.demoMode = enable;
  localStorage.setItem('taskflow_demo_mode', enable ? 'true' : 'false');
  updateApiStatusUI();
}

// ── MODAL MANAGEMENT ──────────────────────────────────────────────────────
function openModal(id) {
  const el = document.getElementById(id);
  if (el) el.classList.add('open');
}

function closeModal(id) {
  const el = document.getElementById(id);
  if (el) el.classList.remove('open');
}

// ── INITIALIZATION ────────────────────────────────────────────────────────
async function initApp() {
  updateAuthUI();

  // If in demo mode and no user, auto-login demo user
  if (state.demoMode && !state.user) {
    state.user = {
      id: 1,
      email: 'demo@taskflow.io',
      firstName: 'Demo',
      lastName: 'User',
      roles: ['ROLE_SUPER_ADMIN'],
      organizationId: 1
    };
    state.token = 'demo_token';
    localStorage.setItem('taskflow_user', JSON.stringify(state.user));
    updateAuthUI();
  }

  if (!state.token && !state.demoMode) {
    openModal('auth-modal');
    return;
  }

  try {
    await loadWorkspaces();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

// ── WORKSPACES & PROJECTS ─────────────────────────────────────────────────
async function loadWorkspaces() {
  const select = document.getElementById('workspace-select');
  const projWksSelect = document.getElementById('proj-workspace-select');
  if (!select) return;
  select.innerHTML = '<option value="">Loading...</option>';

  try {
    const orgId = state.user?.organizationId || 1;
    const workspaces = await apiCall(`/api/v1/workspaces/org/${orgId}`).catch(() => []);
    state.workspaces = workspaces || [];

    select.innerHTML = '';
    if (projWksSelect) projWksSelect.innerHTML = '';

    if (state.workspaces.length === 0) {
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

      if (projWksSelect) {
        const opt2 = opt.cloneNode(true);
        projWksSelect.appendChild(opt2);
      }
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
  if (!select) return;
  select.innerHTML = '<option value="">Loading...</option>';

  try {
    const projects = await apiCall(`/api/v1/projects/workspace/${workspaceId}`).catch(() => []);
    state.projects = projects || [];

    select.innerHTML = '';

    if (state.projects.length === 0) {
      const defaultProj = await apiCall('/api/v1/projects', {
        method: 'POST',
        body: JSON.stringify({
          workspaceId: workspaceId,
          keyPrefix: 'PROJ',
          name: 'TaskFlow Main',
          leadId: state.user?.id || 1
        })
      });
      state.projects = [defaultProj];
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

// ── TASKS & KANBAN ────────────────────────────────────────────────────────
async function loadTasks(projectId) {
  try {
    const tasks = await apiCall(`/api/v1/tasks/project/${projectId}`).catch(() => []);
    state.tasks = tasks || [];
    renderKanbanBoard();
  } catch (err) {
    showToast('Failed to load tasks', 'error');
  }
}

function renderKanbanBoard() {
  const columns = {
    BACKLOG: document.getElementById('col-BACKLOG'),
    IN_PROGRESS: document.getElementById('col-IN_PROGRESS'),
    IN_REVIEW: document.getElementById('col-IN_REVIEW'),
    DONE: document.getElementById('col-DONE')
  };

  Object.values(columns).forEach(col => {
    if (col) col.innerHTML = '';
  });

  const counts = { BACKLOG: 0, IN_PROGRESS: 0, IN_REVIEW: 0, DONE: 0 };

  state.tasks.forEach(task => {
    const status = task.status || 'BACKLOG';
    if (counts[status] !== undefined) counts[status]++;

    const colEl = columns[status] || columns.BACKLOG;
    if (colEl) {
      const card = createTaskCard(task);
      colEl.appendChild(card);
    }
  });

  // Update counts & stats
  Object.keys(counts).forEach(status => {
    const el = document.getElementById(`count-${status}`);
    if (el) el.textContent = counts[status];
  });

  const totalEl = document.getElementById('stat-total');
  const inprogressEl = document.getElementById('stat-inprogress');
  const reviewEl = document.getElementById('stat-review');
  const doneEl = document.getElementById('stat-done');

  if (totalEl) totalEl.textContent = state.tasks.length;
  if (inprogressEl) inprogressEl.textContent = counts.IN_PROGRESS;
  if (reviewEl) reviewEl.textContent = counts.IN_REVIEW;
  if (doneEl) doneEl.textContent = counts.DONE;
}

function createTaskCard(task) {
  const card = document.createElement('div');
  card.className = 'task-card';
  card.draggable = true;
  card.dataset.taskId = task.id;

  const priorityClass = `badge-${(task.priority || 'MEDIUM').toLowerCase()}`;

  card.innerHTML = `
    <div class="task-card-header">
      <span class="task-key">${task.formattedTaskKey || task.taskCode || `TSK-${task.id}`}</span>
      <span class="badge ${priorityClass}">${task.priority || 'MEDIUM'}</span>
    </div>
    <div class="task-card-title">${escapeHtml(task.title)}</div>
    <div class="task-card-footer">
      <div class="task-type-indicator">
        <span>${getTaskTypeIcon(task.taskType)}</span>
        <span>${task.taskType || 'TASK'}</span>
      </div>
      <div class="task-points">${task.storyPoints ? task.storyPoints + ' pts' : (task.loggedHours || 0) + 'h'}</div>
    </div>
  `;

  card.onclick = () => openTaskDetailModal(task);

  // Drag & Drop
  card.addEventListener('dragstart', (e) => {
    e.dataTransfer.setData('text/plain', task.id);
    card.style.opacity = '0.5';
  });

  card.addEventListener('dragend', () => {
    card.style.opacity = '1';
  });

  return card;
}

function getTaskTypeIcon(type) {
  switch (type) {
    case 'BUG': return '🐞';
    case 'FEATURE': return '⭐';
    case 'STORY': return '📖';
    case 'EPIC': return '⚡';
    default: return '📋';
  }
}

function escapeHtml(str) {
  return (str || '').replace(/[&<>"']/g, m => ({
    '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
  })[m]);
}

// ── TASK DETAIL MODAL ─────────────────────────────────────────────────────
async function openTaskDetailModal(task) {
  state.activeTaskId = task.id;
  document.getElementById('detail-task-key').textContent = task.formattedTaskKey || task.taskCode || `TSK-${task.id}`;
  document.getElementById('detail-task-title').textContent = task.title;
  document.getElementById('detail-task-desc').textContent = task.description || 'No description provided.';
  document.getElementById('detail-task-status').value = task.status || 'BACKLOG';
  document.getElementById('detail-logged-hours').value = task.loggedHours || 0;

  const prioEl = document.getElementById('detail-task-priority');
  prioEl.className = `badge badge-${(task.priority || 'MEDIUM').toLowerCase()}`;
  prioEl.textContent = task.priority || 'MEDIUM';

  openModal('task-detail-modal');
  await loadComments(task.id);
}

async function loadComments(taskId) {
  const listEl = document.getElementById('detail-comments-list');
  listEl.innerHTML = '<div style="color: var(--text-muted); font-size: 0.8rem;">Loading comments...</div>';

  try {
    const comments = await apiCall(`/api/v1/comments/task/${taskId}`).catch(() => []);
    listEl.innerHTML = '';
    if (comments.length === 0) {
      listEl.innerHTML = '<div style="color: var(--text-muted); font-size: 0.8rem;">No comments yet.</div>';
      return;
    }

    comments.forEach(c => {
      const item = document.createElement('div');
      item.className = 'comment-item';
      const timeStr = c.createdAt ? new Date(c.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : 'just now';
      item.innerHTML = `
        <div class="comment-author">User #${c.authorId || 1} • <span style="font-weight: normal; color: var(--text-muted);">${timeStr}</span></div>
        <div class="comment-content">${escapeHtml(c.content)}</div>
      `;
      listEl.appendChild(item);
    });
  } catch (err) {
    listEl.innerHTML = '<div style="color: var(--danger-color); font-size: 0.8rem;">Failed to load comments</div>';
  }
}

// ── SETUP DRAG AND DROP LISTENERS ─────────────────────────────────────────
function setupDragAndDrop() {
  document.querySelectorAll('.kanban-column').forEach(column => {
    column.addEventListener('dragover', (e) => {
      e.preventDefault();
      column.classList.add('drag-over');
    });

    column.addEventListener('dragleave', () => {
      column.classList.remove('drag-over');
    });

    column.addEventListener('drop', async (e) => {
      e.preventDefault();
      column.classList.remove('drag-over');
      const taskId = e.dataTransfer.getData('text/plain');
      const targetStatus = column.dataset.status;

      if (!taskId || !targetStatus) return;

      const task = state.tasks.find(t => t.id == taskId);
      if (task && task.status !== targetStatus) {
        task.status = targetStatus;
        renderKanbanBoard();

        try {
          await apiCall(`/api/v1/tasks/${taskId}/status`, {
            method: 'PATCH',
            body: JSON.stringify({ status: targetStatus })
          });
          showToast(`Task moved to ${targetStatus.replace('_', ' ')}`);
        } catch (err) {
          showToast('Failed to update status on server', 'error');
        }
      }
    });
  });
}

// ── DOM INITIALIZATION & EVENT LISTENERS ──────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
  initApp();
  setupDragAndDrop();

  // API Config Modal Elements
  const apiConfigBtn = document.getElementById('btn-api-config');
  const apiModal = document.getElementById('api-config-modal');
  const closeApiModal = document.getElementById('close-api-modal');
  const saveApiBtn = document.getElementById('btn-save-api-url');
  const switchDemoBtn = document.getElementById('btn-switch-demo');
  const apiUrlInput = document.getElementById('api-base-url-input');

  if (apiConfigBtn) {
    apiConfigBtn.onclick = () => {
      apiUrlInput.value = state.apiBase || '';
      openModal('api-config-modal');
    };
  }
  if (closeApiModal) closeApiModal.onclick = () => closeModal('api-config-modal');

  if (saveApiBtn) {
    saveApiBtn.onclick = () => {
      const url = apiUrlInput.value.trim();
      state.apiBase = url;
      localStorage.setItem('taskflow_api_url', url);
      setDemoMode(false);
      closeModal('api-config-modal');
      showToast(url ? `Connected to ${url}` : 'Using default backend');
      initApp();
    };
  }

  if (switchDemoBtn) {
    switchDemoBtn.onclick = () => {
      setDemoMode(true);
      closeModal('api-config-modal');
      showToast('Switched to Interactive Demo Mode');
      initApp();
    };
  }

  // Auth Tabs
  const tabLogin = document.getElementById('tab-login');
  const tabRegister = document.getElementById('tab-register');
  const loginForm = document.getElementById('login-form');
  const registerForm = document.getElementById('register-form');
  const authModalTitle = document.getElementById('auth-modal-title');

  if (tabLogin && tabRegister) {
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
  }

  // Login Submit
  if (loginForm) {
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
          id: data.userId || data.id || 1,
          email: data.email,
          firstName: data.firstName || 'User',
          lastName: data.lastName || '',
          roles: data.roles || ['ROLE_DEVELOPER'],
          organizationId: data.organizationId || 1
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
  }

  // Register Submit
  if (registerForm) {
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
          id: data.userId || data.id || 1,
          email: data.email,
          firstName: data.firstName || firstName,
          lastName: data.lastName || lastName,
          roles: data.roles || ['ROLE_ORG_ADMIN'],
          organizationId: data.organizationId || 1
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
  }

  // Select switchers
  const wksSelect = document.getElementById('workspace-select');
  if (wksSelect) {
    wksSelect.onchange = (e) => {
      state.currentWorkspaceId = e.target.value;
      loadProjects(state.currentWorkspaceId);
    };
  }

  const projSelect = document.getElementById('project-select');
  if (projSelect) {
    projSelect.onchange = (e) => {
      state.currentProjectId = e.target.value;
      loadTasks(state.currentProjectId);
    };
  }

  // New Project Form
  const btnNewProj = document.getElementById('btn-new-project');
  if (btnNewProj) btnNewProj.onclick = () => openModal('project-modal');

  const projForm = document.getElementById('project-form');
  if (projForm) {
    projForm.onsubmit = async (e) => {
      e.preventDefault();
      const workspaceId = document.getElementById('proj-workspace-select').value || state.currentWorkspaceId;
      const keyPrefix = document.getElementById('proj-prefix').value.trim();
      const name = document.getElementById('proj-name').value.trim();
      const description = document.getElementById('proj-desc').value.trim();

      try {
        await apiCall('/api/v1/projects', {
          method: 'POST',
          body: JSON.stringify({ workspaceId, keyPrefix, name, description, leadId: state.user?.id || 1 })
        });
        closeModal('project-modal');
        showToast('Project created successfully!');
        loadProjects(state.currentWorkspaceId);
      } catch (err) {
        showToast(err.message, 'error');
      }
    };
  }

  // Create Task Form
  const btnCreateTask = document.getElementById('btn-create-task');
  if (btnCreateTask) btnCreateTask.onclick = () => openModal('task-modal');

  const taskForm = document.getElementById('task-form');
  if (taskForm) {
    taskForm.onsubmit = async (e) => {
      e.preventDefault();
      const title = document.getElementById('task-title').value.trim();
      const description = document.getElementById('task-desc').value.trim();
      const priority = document.getElementById('task-priority').value;
      const taskType = document.getElementById('task-type').value;
      const storyPoints = parseInt(document.getElementById('task-points').value) || null;
      const estimatedHours = parseFloat(document.getElementById('task-hours').value) || null;

      try {
        await apiCall('/api/v1/tasks', {
          method: 'POST',
          body: JSON.stringify({
            projectId: state.currentProjectId,
            title,
            description,
            priority,
            taskType,
            storyPoints,
            estimatedHours,
            reporterId: state.user?.id || 1
          })
        });
        closeModal('task-modal');
        showToast('Task created successfully!');
        taskForm.reset();
        loadTasks(state.currentProjectId);
      } catch (err) {
        showToast(err.message, 'error');
      }
    };
  }

  // Save Task Status & Logged Hours in Detail Modal
  const btnSaveStatus = document.getElementById('btn-save-status');
  if (btnSaveStatus) {
    btnSaveStatus.onclick = async () => {
      const status = document.getElementById('detail-task-status').value;
      const hours = parseFloat(document.getElementById('detail-logged-hours').value) || 0;

      try {
        if (state.activeTaskId) {
          await apiCall(`/api/v1/tasks/${state.activeTaskId}/status`, {
            method: 'PATCH',
            body: JSON.stringify({ status })
          });
          if (hours > 0) {
            await apiCall(`/api/v1/tasks/${state.activeTaskId}/log-hours`, {
              method: 'POST',
              body: JSON.stringify({ hours })
            });
          }
          showToast('Task updated successfully!');
          closeModal('task-detail-modal');
          loadTasks(state.currentProjectId);
        }
      } catch (err) {
        showToast(err.message, 'error');
      }
    };
  }

  // Comment Submit
  const commentForm = document.getElementById('comment-form');
  if (commentForm) {
    commentForm.onsubmit = async (e) => {
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
        loadComments(state.activeTaskId);
        showToast('Comment added');
      } catch (err) {
        showToast(err.message, 'error');
      }
    };
  }

  // Close Modals
  const closeBtns = [
    { btn: 'close-auth-modal', modal: 'auth-modal' },
    { btn: 'close-proj-modal', modal: 'project-modal' },
    { btn: 'cancel-proj-btn', modal: 'project-modal' },
    { btn: 'close-task-modal', modal: 'task-modal' },
    { btn: 'cancel-task-btn', modal: 'task-modal' },
    { btn: 'close-detail-modal', modal: 'task-detail-modal' }
  ];

  closeBtns.forEach(({ btn, modal }) => {
    const el = document.getElementById(btn);
    if (el) el.onclick = () => closeModal(modal);
  });
});
