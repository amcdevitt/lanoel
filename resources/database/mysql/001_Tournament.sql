CREATE TABLE IF NOT EXISTS TournamentParticipant
(
TournamentParticipantKey BIGINT PRIMARY KEY AUTO_INCREMENT,
TournamentKey BIGINT,
TournamentParticipantData JSON
);

CREATE TABLE IF NOT EXISTS Tournament
(
TournamentKey BIGINT PRIMARY KEY AUTO_INCREMENT,
TournamentData JSON,
Created DATETIME DEFAULT CURRENT_TIMESTAMP
);

