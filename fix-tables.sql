-- =============================================
-- FIX TABLE STRUCTURE FOR AUTO INCREMENT
-- Run this before test-data.sql
-- =============================================

-- Drop all tables and recreate with proper auto-increment
DROP TABLE IF EXISTS isbelongto CASCADE;
DROP TABLE IF EXISTS assessment CASCADE;
DROP TABLE IF EXISTS criteria CASCADE;
DROP TABLE IF EXISTS employee CASCADE;
DROP TABLE IF EXISTS supervisor CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Create users table with SERIAL (auto-increment)
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(255),
    password VARCHAR(255)
);

-- Create supervisor table (inheritance)
CREATE TABLE supervisor (
    id BIGINT PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE
);

-- Create employee table (inheritance)
CREATE TABLE employee (
    id BIGINT PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE
);

-- Create criteria table with SERIAL
CREATE TABLE criteria (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    weight INTEGER NOT NULL,
    category VARCHAR(50) NOT NULL
);

-- Create assessment table with SERIAL
CREATE TABLE assessment (
    id SERIAL PRIMARY KEY,
    total_score BIGINT NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL,
    supervisor_id BIGINT NOT NULL REFERENCES supervisor(id),
    employee_id BIGINT NOT NULL REFERENCES employee(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create isbelongto table with SERIAL
CREATE TABLE isbelongto (
    id SERIAL PRIMARY KEY,
    score BIGINT,
    comment TEXT,
    assessment_id BIGINT NOT NULL REFERENCES assessment(id) ON DELETE CASCADE,
    criteria_id BIGINT NOT NULL REFERENCES criteria(id) ON DELETE CASCADE
);
