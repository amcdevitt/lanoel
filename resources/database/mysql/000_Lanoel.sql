CREATE TABLE IF NOT EXISTS Person
(
PersonKey BIGINT PRIMARY KEY AUTO_INCREMENT,
PersonName varchar(255),
Title varchar(500),
Information varchar(2000),
UserName varchar(200)
);

CREATE TABLE IF NOT EXISTS Game
(
GameKey BIGINT PRIMARY KEY AUTO_INCREMENT,
GameName varchar(500),
Location varchar(2000),
Rules varchar(5000),
IsFree BIT(1)
);

CREATE TABLE IF NOT EXISTS Vote
(
VoteKey BIGINT PRIMARY KEY AUTO_INCREMENT,
PersonKey BIGINT,
GameKey BIGINT,
VoteNumber INT
);

CREATE TABLE IF NOT EXISTS Suggestion
(
SuggestionKey VARCHAR(2000) PRIMARY KEY,
Description VARCHAR(8000),
Category VARCHAR(2000)
);

