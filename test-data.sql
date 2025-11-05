-- =============================================
-- TEST DATA FOR ASSESSMENT SYSTEM
-- Complete regeneration with snake_case convention
-- =============================================

-- Clear existing data and reset sequences
-- TRUNCATE TABLE isbelongto, assessment, criteria, employee, supervisor, users RESTART IDENTITY CASCADE;

-- =============================================
-- 1. INSERT USERS
-- =============================================
-- Password for all users: password123 (BCrypt hash with cost 12)
INSERT INTO users (email, name, password) VALUES
('supervisor1@gmail.com', 'Alice Johnson', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5gyg5gsC1Uq1i'),
('supervisor2@gmail.com', 'Bob Wilson', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5gyg5gsC1Uq1i'),
('employee1@gmail.com', 'John Smith', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5gyg5gsC1Uq1i'),
('employee2@gmail.com', 'Sarah Brown', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5gyg5gsC1Uq1i'),
('employee3@gmail.com', 'Mike Taylor', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5gyg5gsC1Uq1i');

-- =============================================
-- 2. INSERT SUPERVISORS (Inheritance table)
-- =============================================
INSERT INTO supervisor (id)
SELECT id FROM users WHERE email IN ('supervisor1@gmail.com', 'supervisor2@gmail.com');

-- =============================================
-- 3. INSERT EMPLOYEES (Inheritance table)
-- =============================================
INSERT INTO employee (id)
SELECT id FROM users WHERE email IN ('employee1@gmail.com', 'employee2@gmail.com', 'employee3@gmail.com');

-- =============================================
-- 4. INSERT CRITERIA
-- =============================================
INSERT INTO criteria (name, description, weight, category) VALUES
('Problem Solving', 'Ability to identify and solve complex problems', 20, 'HARDSKILL'),
('Communication', 'Effective verbal and written communication', 15, 'SOFTSKILL'),
('Teamwork', 'Collaboration and team contribution', 15, 'SOFTSKILL'),
('Code Quality', 'Writing clean, maintainable code', 20, 'HARDSKILL'),
('Time Management', 'Meeting deadlines and managing priorities', 10, 'SOFTSKILL'),
('Leadership', 'Leading projects and mentoring others', 10, 'SOFTSKILL'),
('Innovation', 'Creative thinking and new ideas', 10, 'HARDSKILL');

-- =============================================
-- 5. INSERT ASSESSMENTS
-- =============================================
-- Assessment 1: Supervisor1 -> Employee1 (Published with scores)
INSERT INTO assessment (total_score, status, supervisor_id, employee_id, created_at) VALUES
(85, 'Published',
 (SELECT id FROM users WHERE email = 'supervisor1@gmail.com'),
 (SELECT id FROM users WHERE email = 'employee1@gmail.com'),
 CURRENT_TIMESTAMP);

-- Assessment 2: Supervisor1 -> Employee2 (In Progress, no scores yet)
INSERT INTO assessment (total_score, status, supervisor_id, employee_id, created_at) VALUES
(0, 'InProgress',
 (SELECT id FROM users WHERE email = 'supervisor1@gmail.com'),
 (SELECT id FROM users WHERE email = 'employee2@gmail.com'),
 CURRENT_TIMESTAMP);

-- Assessment 3: Supervisor2 -> Employee3 (Published with scores)
INSERT INTO assessment (total_score, status, supervisor_id, employee_id, created_at) VALUES
(92, 'Published',
 (SELECT id FROM users WHERE email = 'supervisor2@gmail.com'),
 (SELECT id FROM users WHERE email = 'employee3@gmail.com'),
 CURRENT_TIMESTAMP);

-- =============================================
-- 6. INSERT ISBELONGTO (Scores for published assessments)
-- =============================================
-- Scores for Assessment 1 (Employee1 by Supervisor1) - Total: 85
INSERT INTO isbelongto (score, comment, assessment_id, criteria_id) VALUES
(18, 'Excellent problem-solving skills demonstrated',
 (SELECT a.id FROM assessment a JOIN users u ON a.employee_id = u.id WHERE u.email = 'employee1@gmail.com' AND a.status = 'Published'),
 (SELECT id FROM criteria WHERE name = 'Problem Solving')),
(13, 'Good communication with team members',
 (SELECT a.id FROM assessment a JOIN users u ON a.employee_id = u.id WHERE u.email = 'employee1@gmail.com' AND a.status = 'Published'),
 (SELECT id FROM criteria WHERE name = 'Communication')),
(14, 'Strong team player and collaborator',
 (SELECT a.id FROM assessment a JOIN users u ON a.employee_id = u.id WHERE u.email = 'employee1@gmail.com' AND a.status = 'Published'),
 (SELECT id FROM criteria WHERE name = 'Teamwork')),
(18, 'Consistently writes clean and maintainable code',
 (SELECT a.id FROM assessment a JOIN users u ON a.employee_id = u.id WHERE u.email = 'employee1@gmail.com' AND a.status = 'Published'),
 (SELECT id FROM criteria WHERE name = 'Code Quality')),
(8, 'Needs improvement on time management',
 (SELECT a.id FROM assessment a JOIN users u ON a.employee_id = u.id WHERE u.email = 'employee1@gmail.com' AND a.status = 'Published'),
 (SELECT id FROM criteria WHERE name = 'Time Management')),
(7, 'Shows good leadership potential',
 (SELECT a.id FROM assessment a JOIN users u ON a.employee_id = u.id WHERE u.email = 'employee1@gmail.com' AND a.status = 'Published'),
 (SELECT id FROM criteria WHERE name = 'Leadership')),
(7, 'Innovative approach to solving problems',
 (SELECT a.id FROM assessment a JOIN users u ON a.employee_id = u.id WHERE u.email = 'employee1@gmail.com' AND a.status = 'Published'),
 (SELECT id FROM criteria WHERE name = 'Innovation'));

-- Scores for Assessment 3 (Employee3 by Supervisor2) - Total: 92
INSERT INTO isbelongto (score, comment, assessment_id, criteria_id) VALUES
(20, 'Outstanding problem-solving abilities',
 (SELECT a.id FROM assessment a JOIN users u ON a.employee_id = u.id WHERE u.email = 'employee3@gmail.com' AND a.status = 'Published'),
 (SELECT id FROM criteria WHERE name = 'Problem Solving')),
(14, 'Clear and effective communicator',
 (SELECT a.id FROM assessment a JOIN users u ON a.employee_id = u.id WHERE u.email = 'employee3@gmail.com' AND a.status = 'Published'),
 (SELECT id FROM criteria WHERE name = 'Communication')),
(15, 'Excellent team collaboration skills',
 (SELECT a.id FROM assessment a JOIN users u ON a.employee_id = u.id WHERE u.email = 'employee3@gmail.com' AND a.status = 'Published'),
 (SELECT id FROM criteria WHERE name = 'Teamwork')),
(19, 'Maintains very high code quality standards',
 (SELECT a.id FROM assessment a JOIN users u ON a.employee_id = u.id WHERE u.email = 'employee3@gmail.com' AND a.status = 'Published'),
 (SELECT id FROM criteria WHERE name = 'Code Quality')),
(10, 'Perfect time management and prioritization',
 (SELECT a.id FROM assessment a JOIN users u ON a.employee_id = u.id WHERE u.email = 'employee3@gmail.com' AND a.status = 'Published'),
 (SELECT id FROM criteria WHERE name = 'Time Management')),
(8, 'Natural leader, mentors junior developers',
 (SELECT a.id FROM assessment a JOIN users u ON a.employee_id = u.id WHERE u.email = 'employee3@gmail.com' AND a.status = 'Published'),
 (SELECT id FROM criteria WHERE name = 'Leadership')),
(6, 'Brings innovative solutions to complex challenges',
 (SELECT a.id FROM assessment a JOIN users u ON a.employee_id = u.id WHERE u.email = 'employee3@gmail.com' AND a.status = 'Published'),
 (SELECT id FROM criteria WHERE name = 'Innovation'));

-- =============================================
-- TEST DATA SUMMARY
-- =============================================
-- Users:
--   - 2 Supervisors: supervisor1@gmail.com, supervisor2@gmail.com
--   - 3 Employees: employee1@gmail.com, employee2@gmail.com, employee3@gmail.com
--   - All passwords: password123
--
-- Assessments:
--   - Assessment 1: Supervisor1 -> Employee1 (Published, score: 85)
--   - Assessment 2: Supervisor1 -> Employee2 (InProgress, no scores)
--   - Assessment 3: Supervisor2 -> Employee3 (Published, score: 92)
--
-- Criteria: 7 total (weights sum to 100)
--   TECHNICAL: Problem Solving (20), Code Quality (20), Innovation (10)
--   SOFT_SKILL: Communication (15), Teamwork (15), Leadership (10), Time Management (10)
