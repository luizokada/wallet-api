CREATE TABLE IF NOT EXISTS Wallets (
    id varChar(255) PRIMARY KEY,
    user_id varChar(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(id)
    );