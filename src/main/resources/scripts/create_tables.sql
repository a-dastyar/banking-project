CREATE TABLE IF NOT EXISTS bank_accounts (
    id BIGINT NOT NULL auto_increment,
    account_number VARCHAR(20) NOT NUll,
    account_holder_name VARCHAR(20) NOT NUll,
    balance DOUBLE NOT NUll,
    is_subclass BOOLEAN DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE (account_number)
);
CREATE INDEX bnk_acc_num_idx ON bank_accounts (account_number);
CREATE TABLE IF NOT EXISTS saving_accounts (
    id BIGINT NOT NULL ,
    interest_rate DOUBLE NOT NUll,
    interest_period VARCHAR(20) NOT NUll,
    minimum_balance DOUBLE NOT NUll,
    PRIMARY KEY (id),
    FOREIGN KEY (id) REFERENCES bank_accounts(id)
);
CREATE TABLE IF NOT EXISTS checking_accounts (
    id BIGINT NOT NULL ,
    overdraft_limit DOUBLE NOT NUll,
    debt DOUBLE NOT NUll,
    PRIMARY KEY (id),
    FOREIGN KEY (id) REFERENCES bank_accounts(id)
);
CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT NOT NULL auto_increment,
    type VARCHAR(20) NOT NUll,
    amount DOUBLE NOT NUll,
    bank_account_id BIGINT NOT NUll,
    date TIMESTAMP NOT NUll,
    PRIMARY KEY (id),
    FOREIGN KEY (bank_account_id) REFERENCES bank_accounts(id)
);