-- =============================================
-- TEST DATA FOR ASSESSMENT SYSTEM
-- =============================================

-- Clear existing data (TRUNCATE is faster and auto-resets sequences)
TRUNCATE TABLE isbelongto, assessment, criteria, employee, supervisor, users RESTART IDENTITY CASCADE;

-- =============================================
-- 1. INSERT USERS (Supervisors)
-- =============================================
INSERT INTO users (id, email, name, password) VALUES
(1, 'supervisor1@gmail.com', 'Alice Johnson', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5gyg5gsC1Uq1i'), -- password: password123
(2, 'supervisor2@gmail.com', 'Bob Wilson', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5gyg5gsC1Uq1i'),
(3, 'supervisor3@gmail.com', 'Carol Davis', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5gyg5gsC1Uq1i');

-- =============================================
-- 2. INSERT SUPERVISORS (extends users)
-- =============================================
INSERT INTO supervisor (id) VALUES
(1),
(2),
(3);

-- =============================================
-- 3. INSERT USERS (Employees)
-- =============================================
INSERT INTO users (id, email, name, password) VALUES
(4, 'employee1@gmail.com', 'John Smith', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5gyg5gsC1Uq1i'), -- password: password123
(5, 'employee2@gmail.com', 'Sarah Brown', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5gyg5gsC1Uq1i'),
(6, 'employee3@gmail.com', 'Mike Taylor', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5gyg5gsC1Uq1i'),
(7, 'employee4@gmail.com', 'Emma Wilson', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5gyg5gsC1Uq1i');

-- =============================================
-- 4. INSERT EMPLOYEES (extends users)
-- =============================================
INSERT INTO employee (id) VALUES
(4),
(5),
(6),
(7);

-- =============================================
-- 5. INSERT CRITERIA
-- =============================================
INSERT INTO criteria (id, name, description, weight, category) VALUES
(1, 'Problem Solving', 'Ability to identify issues and propose effective solutions.', 3, 'HARDSKILL'),
(2, 'Technical Skills', 'Proficiency in required technical tools and languages.', 4, 'HARDSKILL'),
(3, 'Communication', 'Clear and effective communication with team members.', 2, 'SOFTSKILL'),
(4, 'Teamwork', 'Collaboration and cooperation with colleagues.', 2, 'SOFTSKILL'),
(5, 'Leadership', 'Ability to guide and motivate team members.', 3, 'SOFTSKILL'),
(6, 'Time Management', 'Efficient use of time and meeting deadlines.', 2, 'SOFTSKILL'),
(7, 'Innovation', 'Creative thinking and proposing new ideas.', 3, 'HARDSKILL'),
(8, 'Adaptability', 'Flexibility in handling changing requirements.', 2, 'SOFTSKILL');

-- =============================================
-- 6. INSERT ASSESSMENTS
-- =============================================
-- Assessment 1: Supervisor 1 -> Employee 1 (Published)
INSERT INTO assessment (id, supervisor_id, employee_id, status, total_score, created_at) VALUES
(1, 1, 4, 'Published', 88.5, '2025-11-01 10:30:00');

-- Assessment 2: Supervisor 1 -> Employee 2 (InProgress)
INSERT INTO assessment (id, supervisor_id, employee_id, status, total_score, created_at) VALUES
(2, 1, 5, 'InProgress', 75.0, '2025-11-05 14:20:00');

-- Assessment 3: Supervisor 2 -> Employee 3 (Published)
INSERT INTO assessment (id, supervisor_id, employee_id, status, total_score, created_at) VALUES
(3, 2, 6, 'Published', 92.3, '2025-10-28 09:15:00');

-- Assessment 4: Supervisor 2 -> Employee 4 (InProgress)
INSERT INTO assessment (id, supervisor_id, employee_id, status, total_score, created_at) VALUES
(4, 2, 7, 'InProgress', 80.0, '2025-11-03 16:45:00');

-- Assessment 5: Supervisor 3 -> Employee 1 (Published)
INSERT INTO assessment (id, supervisor_id, employee_id, status, total_score, created_at) VALUES
(5, 3, 4, 'Published', 85.0, '2025-10-15 11:00:00');

-- =============================================
-- 7. INSERT CRITERIA SCORES (isbelongto)
-- =============================================

-- Assessment 1 scores
INSERT INTO isbelongto (id, assessment_id, criteria_id, score, comment) VALUES
(1, 1, 1, 90, 'Excellent problem-solving skills demonstrated in project delivery.'),
(2, 1, 2, 85, 'Strong technical proficiency, minor improvements needed in documentation.'),
(3, 1, 3, 88, 'Clear and effective communicator with stakeholders.'),
(4, 1, 4, 92, 'Outstanding teamwork and collaboration.'),
(5, 1, 5, 87, 'Good leadership potential, needs more experience.');

-- Assessment 2 scores
INSERT INTO isbelongto (id, assessment_id, criteria_id, score, comment) VALUES
(6, 2, 1, 75, 'Good problem-solving but needs to consider edge cases.'),
(7, 2, 2, 80, 'Competent technical skills, still learning advanced topics.'),
(8, 2, 3, 70, 'Communication could be more proactive.'),
(9, 2, 4, 75, 'Works well in team but occasionally needs guidance.');

-- Assessment 3 scores
INSERT INTO isbelongto (id, assessment_id, criteria_id, score, comment) VALUES
(10, 3, 1, 95, 'Exceptional analytical and problem-solving abilities.'),
(11, 3, 2, 90, 'Expert-level technical skills across multiple domains.'),
(12, 3, 3, 92, 'Excellent communicator, both written and verbal.'),
(13, 3, 4, 93, 'Natural team player, always willing to help others.'),
(14, 3, 6, 91, 'Excellent time management and prioritization.');

-- Assessment 4 scores
INSERT INTO isbelongto (id, assessment_id, criteria_id, score, comment) VALUES
(15, 4, 1, 82, 'Solid problem-solving approach, improving consistently.'),
(16, 4, 2, 78, 'Good technical foundation, expanding skillset.'),
(17, 4, 3, 80, 'Communicates effectively with team members.'),
(18, 4, 7, 85, 'Shows creativity in proposing new solutions.');

-- Assessment 5 scores
INSERT INTO isbelongto (id, assessment_id, criteria_id, score, comment) VALUES
(19, 5, 1, 88, 'Strong problem solver with practical approach.'),
(20, 5, 2, 82, 'Good technical skills, continues to learn new technologies.'),
(21, 5, 4, 85, 'Excellent team collaborator.'),
(22, 5, 5, 84, 'Shows leadership qualities in guiding junior members.'),
(23, 5, 8, 87, 'Highly adaptable to changing project requirements.');

-- =============================================
-- Note: TRUNCATE with RESTART IDENTITY already reset sequences
-- No need for manual sequence updates
-- =============================================

-- =============================================
-- VERIFICATION QUERIES
-- =============================================
-- Run these to verify data was inserted correctly

-- Check users count
-- SELECT 'Users' as table_name, COUNT(*) as count FROM users
-- UNION ALL
-- SELECT 'Supervisors', COUNT(*) FROM supervisor
-- UNION ALL
-- SELECT 'Employees', COUNT(*) FROM employee
-- UNION ALL
-- SELECT 'Criteria', COUNT(*) FROM criteria
-- UNION ALL
-- SELECT 'Assessments', COUNT(*) FROM assessment
-- UNION ALL
-- SELECT 'Scores', COUNT(*) FROM isbelongto;

-- Check assessments with details
-- SELECT 
--     a.id,
--     s.name as supervisor_name,
--     e.name as employee_name,
--     a.status,
--     a.totalscore,
--     a.created_at
-- FROM assessment a
-- JOIN users s ON a.supervisorid = s.id
-- JOIN users e ON a.employee_id = e.id
-- ORDER BY a.created_at DESC;
