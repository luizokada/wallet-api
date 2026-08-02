ALTER TABLE transactions ADD COLUMN installment_number int;
ALTER TABLE transactions ADD COLUMN installment_total int;
ALTER TABLE transactions ADD COLUMN series_id varchar(255);

CREATE INDEX IF NOT EXISTS transactions_series_id_index
    ON transactions(series_id);
