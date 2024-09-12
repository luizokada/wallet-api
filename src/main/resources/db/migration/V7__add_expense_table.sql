CREATE TABLE IF NOT EXISTS Expense (
    id varChar(255) PRIMARY KEY,
    amount BIGINT NOT NULL,
    description TEXT NOT NULL,
    category_id varChar(255),
    wallet_id varChar(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES expense_categories(id),
    FOREIGN KEY (wallet_id) REFERENCES wallets(id)
);