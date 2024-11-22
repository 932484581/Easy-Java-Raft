SHOW DATABASES;

USE `easy-java-raft`;

CREATE TABLE IF NOT EXISTS `log0` (
    `log_index` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志的序号',
    `log_term` BIGINT NOT NULL COMMENT '日志的任期号',
    `command` VARCHAR(50) COMMENT '输入的指令'
);

CREATE TABLE IF NOT EXISTS kv_store (
    `key` VARCHAR(255) NOT NULL,
    `value` VARCHAR(255),
    PRIMARY KEY (`key`)
);