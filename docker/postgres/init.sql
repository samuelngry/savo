-- Initial database setup
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create basic tables for testing
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert test user
INSERT INTO users (email, password_hash, first_name, last_name) 
VALUES ('test@savo.com', '$2a$10$dummy_hash', 'Test', 'User');