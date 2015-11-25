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

CREATE TABLE IF NOT EXISTS Tournament
(
TournamentKey BIGINT PRIMARY KEY AUTO_INCREMENT,
TournamentName varchar(100)
);

CREATE TABLE IF NOT EXISTS Round
(
RoundKey BIGINT PRIMARY KEY AUTO_INCREMENT,
TournamentKey BIGINT,
RoundNumber INT,
GameKey BIGINT
);

CREATE TABLE IF NOT EXISTS RoundStanding
(
RoundKey BIGINT,
PersonKey BIGINT,
Place INT,
UNIQUE (RoundKey, PersonKey)
);

CREATE TABLE IF NOT EXISTS PointValues
(
Place INT PRIMARY KEY,
PointValue INT
);
