CREATE USER 'securityuser'@'%' IDENTIFIED BY 'UJjdn38HNDln9';
GRANT SELECT, INSERT, UPDATE ON `Security-Service`.* TO 'securityuser'@'%';


CREATE SCHEMA IF NOT EXISTS `Security`;


USE `Security`;


CREATE TABLE IF NOT EXISTS users (
    `username`		VARCHAR(50) PRIMARY KEY,
    `password` 		VARCHAR(80) NOT NULL,
    `enabled` 		INTEGER DEFAULT 1
);


CREATE TABLE IF NOT EXISTS authorities (
    `username`		VARCHAR(50) PRIMARY KEY,
    `authority`		VARCHAR(12) NOT NULL,
    FOREIGN KEY(`username`) REFERENCES users(`username`)
);


CREATE TABLE IF NOT EXISTS user_info (
    `username`		VARCHAR(50) PRIMARY KEY,
    `name`			VARCHAR(20),
    `surname`		VARCHAR(20),
    `patronymic`	VARCHAR(20),
    `number`		VARCHAR(12),
    FOREIGN KEY(`username`) REFERENCES users(`username`)
);


CREATE TABLE IF NOT EXISTS refresh_token (
    `username`		VARCHAR(50) PRIMARY KEY,
    `token`		    TEXT,
    FOREIGN KEY(`username`) REFERENCES users(`username`)
);


INSERT INTO `users`(username, `password`, enabled)
VALUES
    ('bob@gmail.com', '12345678', 1),
    ('sam@gmail.com', '87654321', 1);


INSERT INTO `authorities`(`username`, `authority`)
VALUES
    ('bob@gmail.com', 'ROLE_USER'),
    ('sam@gmail.com', 'ROLE_MANAGER');


INSERT INTO `user_info`(`username`,`name`, surname, patronymic, `number`)
VALUES
    ('bob@gmail.com', 'Bob', 'Smith', 'Jackson', '380975246691'),
    ('sam@gmail.com', 'Sam', 'Brown', 'Johnson', '380947347396');
