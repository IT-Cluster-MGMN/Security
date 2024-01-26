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


INSERT INTO `users`(username, `password`, enabled)
VALUES
    ('bob@gmail.com', '12345678', 1),
    ('sam@gmail.com', '87654321', 1);


INSERT INTO `authorities`(`username`, `authority`)
VALUES
    ('bob@gmail.com', 'ROLE_USER'),
    ('sam@gmail.com', 'ROLE_MANAGER');

