ALTER TABLE payments
    ADD COLUMN customer_id BIGINT NOT NULL;

ALTER TABLE payments
    ADD CONSTRAINT fk_payments_customer
        FOREIGN KEY (customer_id) REFERENCES customer(id);
