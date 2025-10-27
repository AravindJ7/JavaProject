-- Database setup script for Expense Split Backend
-- Run this script to create the database and user

-- Create database
CREATE DATABASE expense_split_db;

-- Create user
CREATE USER expense_user WITH PASSWORD 'password';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE expense_split_db TO expense_user;

-- Connect to the database
\c expense_split_db;

-- Grant schema privileges
GRANT ALL ON SCHEMA public TO expense_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO expense_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO expense_user;

-- Set default privileges for future tables
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO expense_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO expense_user;
