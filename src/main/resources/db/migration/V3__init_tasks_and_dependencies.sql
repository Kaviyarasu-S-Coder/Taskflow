-- Migration: V3__init_tasks_and_dependencies.sql
-- Description: Create tasks and task_dependencies tables

CREATE TABLE tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_code VARCHAR(30) NOT NULL UNIQUE,
    project_id BIGINT NOT NULL,
    task_number BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description MEDIUMTEXT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'BACKLOG',
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    task_type VARCHAR(20) NOT NULL DEFAULT 'TASK',
    story_points INT NULL,
    estimated_hours DECIMAL(6,2) NULL,
    logged_hours DECIMAL(6,2) NOT NULL DEFAULT 0.00,
    due_date DATETIME(3) NULL,
    reporter_id BIGINT NOT NULL,
    assignee_id BIGINT NULL,
    parent_task_id BIGINT NULL,
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    created_by BIGINT NULL,
    updated_by BIGINT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at DATETIME(3) NULL,
    CONSTRAINT uq_tasks_proj_number UNIQUE (project_id, task_number),
    CONSTRAINT fk_tasks_project FOREIGN KEY (project_id) REFERENCES projects(id),
    CONSTRAINT fk_tasks_reporter FOREIGN KEY (reporter_id) REFERENCES users(id),
    CONSTRAINT fk_tasks_assignee FOREIGN KEY (assignee_id) REFERENCES users(id),
    CONSTRAINT fk_tasks_parent FOREIGN KEY (parent_task_id) REFERENCES tasks(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE task_dependencies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    predecessor_id BIGINT NOT NULL,
    successor_id BIGINT NOT NULL,
    dependency_type VARCHAR(20) NOT NULL DEFAULT 'BLOCKS',
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    created_by BIGINT NULL,
    updated_by BIGINT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at DATETIME(3) NULL,
    CONSTRAINT uq_task_dep UNIQUE (predecessor_id, successor_id),
    CONSTRAINT fk_td_predecessor FOREIGN KEY (predecessor_id) REFERENCES tasks(id) ON DELETE CASCADE,
    CONSTRAINT fk_td_successor FOREIGN KEY (successor_id) REFERENCES tasks(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_tasks_proj_number ON tasks(project_id, task_number);
CREATE INDEX idx_tasks_assignee_status ON tasks(assignee_id, status, deleted);
CREATE INDEX idx_tasks_parent ON tasks(parent_task_id);
