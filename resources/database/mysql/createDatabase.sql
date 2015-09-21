CREATE TABLE IF NOT EXISTS Person
(
PersonKey BIGINT PRIMARY KEY AUTO_INCREMENT,
PersonName varchar(255),
Title varchar(500),
Information varchar(2000)
);

CREATE TABLE IF NOT EXISTS Game
(
GameKey BIGINT PRIMARY KEY AUTO_INCREMENT,
GameName varchar(500),
Location varchar(2000),
Rules varchar(5000)
);

CREATE TABLE IF NOT EXISTS Vote
(
VoteKey BIGINT PRIMARY KEY AUTO_INCREMENT,
PersonKey BIGINT,
GameKey BIGINT,
VoteNumber INT
);