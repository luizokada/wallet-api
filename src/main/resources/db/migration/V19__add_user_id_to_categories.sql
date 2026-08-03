ALTER TABLE categories ADD COLUMN user_id VARCHAR(255) NULL REFERENCES users(id);
CREATE INDEX idx_categories_user_type ON categories (user_id, type);
