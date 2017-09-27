CREATE TABLE IF NOT EXISTS TournamentLanoel_Round
(
RoundKey BIGINT PRIMARY KEY AUTO_INCREMENT,
TournamentKey BIGINT,
RoundNumber INT,
GameKey BIGINT
);

CREATE TABLE IF NOT EXISTS TournamentLanoel_RoundStanding
(
RoundKey BIGINT,
ParticipantKey BIGINT,
Place INT,
UNIQUE (RoundKey, ParticipantKey)
);

CREATE TABLE IF NOT EXISTS TournamentLanoel_PointValues
(
Place INT PRIMARY KEY,
PointValue INT
);