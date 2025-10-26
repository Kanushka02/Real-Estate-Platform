-- Create Database
CREATE DATABASE realestate_db;

-- Connect to the database
\c realestate_db;

-- Sri Lanka Districts (for reference/seeding)
-- Colombo, Gampaha, Kalutara, Kandy, Matale, Nuwara Eliya, Galle, Matara, Hambantota,
-- Jaffna, Kilinochchi, Mannar, Vavuniya, Mullaitivu, Batticaloa, Ampara, Trincomalee,
-- Kurunegala, Puttalam, Anuradhapura, Polonnaruwa, Badulla, Monaragala, Ratnapura, Kegalle

-- Note: Tables will be auto-created by JPA/Hibernate
-- This file is for reference and manual database setup if needed

-- Example insert for admin user (password should be hashed with BCrypt)
-- Default password: admin123 (hash: $2a$10$XE3BbQfgLr1aRmkQJ2YH4.Q1LvEy7I5sZQNmF/1nqQv7F9FQN9rQq)
-- INSERT INTO users (username, email, password, full_name, active, created_at, updated_at)
-- VALUES ('admin', 'admin@realestate.lk', '$2a$10$XE3BbQfgLr1aRmkQJ2YH4.Q1LvEy7I5sZQNmF/1nqQv7F9FQN9rQq', 'Admin User', true, NOW(), NOW());

-- Example: Link admin user to ROLE_ADMIN
-- INSERT INTO user_roles (user_id, role_id)
-- SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN';

