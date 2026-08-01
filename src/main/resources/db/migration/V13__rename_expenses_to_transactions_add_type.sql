ALTER TABLE expenses RENAME TO transactions;
ALTER TABLE transactions ADD COLUMN type varchar(10) NOT NULL DEFAULT 'EXPENSE';
ALTER TABLE transactions ADD CONSTRAINT transactions_type_check CHECK (type IN ('EXPENSE', 'INCOME'));
