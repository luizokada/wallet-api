ALTER TABLE expense_categories RENAME TO categories;
ALTER TABLE categories ADD COLUMN type varchar(10) NOT NULL DEFAULT 'EXPENSE';
ALTER TABLE categories ADD CONSTRAINT categories_type_check CHECK (type IN ('EXPENSE', 'INCOME'));
