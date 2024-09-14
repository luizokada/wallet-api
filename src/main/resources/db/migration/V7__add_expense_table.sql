CREATE TABLE IF NOT EXISTS Expenses (
    id varChar(255) PRIMARY KEY,
    amount BIGINT NOT NULL,
    description TEXT,
    category_id varChar(255),
    wallet_id varChar(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES expense_categories(id),
    FOREIGN KEY (wallet_id) REFERENCES wallets(id)
);