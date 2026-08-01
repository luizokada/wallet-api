CREATE TABLE IF NOT EXISTS password_reset_tokens (
    id varchar(255) PRIMARY KEY,
    user_id varchar(255) NOT NULL,
    token varchar(255) NOT NULL UNIQUE,
    expires_at timestamp NOT NULL,
    used_at timestamp,
    created_at timestamp NOT NULL DEFAULT current_timestamp,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
