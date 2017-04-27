CREATE TABLE IF NOT EXISTS TournamentSwiss
(
TournamentSwissKey BIGINT PRIMARY KEY AUTO_INCREMENT,
TournamentKey BIGINT NOT NULL,
NumberOfRounds INT NOT NULL,
PointsPerGameWon INT NOT NULL,
PointsPerRoundWon INT NOT NULL,
PointsForDraw INT NOT NULL,
PlayersCanPlaySamePlayerMoreThanOnce BIT
);

CREATE TABLE IF NOT EXISTS TournamentSwiss_Round
(
TournamentSwissRoundKey BIGINT PRIMARY KEY AUTO_INCREMENT,
RoundNumber INT NOT NULL,
PlayerKey BIGINT NOT NULL,
GamesWon INT NOT NULL,
Draws INT NOT NULL,
PlayerDrop BIT NOT NULL
);