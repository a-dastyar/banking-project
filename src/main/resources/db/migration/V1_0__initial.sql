CREATE TABLE
  `users` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `created_at` datetime(6) DEFAULT NULL,
    `updated_at` datetime(6) DEFAULT NULL,
    `email` varchar(255) DEFAULT NULL,
    `password` varchar(255) DEFAULT NULL,
    `username` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `user_email_idx` (`email`),
    UNIQUE KEY `user_username_idx` (`username`)
  ) ENGINE = InnoDB AUTO_INCREMENT = 2 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE
  `user_roles` (
    `username` varchar(255) NOT NULL,
    `name` enum('MEMBER', 'MANAGER', 'ADMIN') NOT NULL,
    PRIMARY KEY (`username`, `name`),
    KEY `user_role_username_fk` (`username`),
    CONSTRAINT `user_role_user_username_fk` FOREIGN KEY (`username`) REFERENCES `users` (`username`)
  ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE
  `account_number_sequence` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `created_at` datetime(6) DEFAULT NULL,
    `updated_at` datetime(6) DEFAULT NULL,
    `sequence` bigint DEFAULT NULL,
    `year` int DEFAULT NULL,
    PRIMARY KEY (`id`)
  ) ENGINE = InnoDB AUTO_INCREMENT = 2 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE
  `bank_accounts` (
    `type` varchar(50) NOT NULL,
    `id` bigint NOT NULL AUTO_INCREMENT,
    `created_at` datetime(6) DEFAULT NULL,
    `updated_at` datetime(6) DEFAULT NULL,
    `account_number` varchar(255) NOT NULL,
    `balance` double DEFAULT NULL,
    `account_holder_id` bigint NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `bnk_acc_num_idx` (`account_number`),
    KEY `bank_user_id_fk` (`account_holder_id`),
    CONSTRAINT `bank_user_id_fk` FOREIGN KEY (`account_holder_id`) REFERENCES `users` (`id`)
  ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE
  `checking_accounts` (
    `debt` double DEFAULT NULL,
    `overdraft_limit` double DEFAULT NULL,
    `id` bigint NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `checking_bank_id_fk` FOREIGN KEY (`id`) REFERENCES `bank_accounts` (`id`)
  ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE
  `saving_accounts` (
    `interest_period` enum('MONTHLY', 'YEARLY') NOT NULL,
    `interest_rate` double DEFAULT NULL,
    `minimum_balance` double DEFAULT NULL,
    `id` bigint NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `saving_bank_id_fk` FOREIGN KEY (`id`) REFERENCES `bank_accounts` (`id`)
  ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE
  `transactions` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `created_at` datetime(6) DEFAULT NULL,
    `updated_at` datetime(6) DEFAULT NULL,
    `amount` double DEFAULT NULL,
    `date` datetime(6) DEFAULT NULL,
    `type` enum(
      'WITHDRAW',
      'DEPOSIT',
      'INTEREST',
      'TRANSACTION_FEE'
    ) DEFAULT NULL,
    `bank_account_id` bigint NOT NULL,
    PRIMARY KEY (`id`),
    KEY `trx_bnk_acc_id_idx` (`bank_account_id`),
    CONSTRAINT `trx_bank_id_fk` FOREIGN KEY (`bank_account_id`) REFERENCES `bank_accounts` (`id`)
  ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
