CREATE TABLE IF NOT EXISTS TournamentSwiss
(
TournamentSwissKey BIGINT PRIMARY KEY AUTO_INCREMENT,
TournamentKey BIGINT NOT NULL,
NumberOfRounds INT NOT NULL,
PointsPerGameWon INT NOT NULL,
PointsPerRoundWon INT NOT NULL,
PointsForDraw INT NOT NULL,
PlayersCanPlaySamePlayerMoreThanOnce BIT NOT NULL
);

CREATE TABLE IF NOT EXISTS TournamentSwiss_Round
(
TournamentKey BIGINT NOT NULL,
RoundNumber INT NOT NULL,
PlayerKey BIGINT NOT NULL,
GamesWon INT NOT NULL,
Draws INT NOT NULL,
PlayerDrop BIT NOT NULL,
RoundWon BIT NOT NULL
);

CREATE TABLE IF NOT EXISTS TournamentSwiss_CurrentRoundPairing
(
TournamentKey BIGINT NOT NULL,
PlayerOneKey BIGINT NOT NULL,
PlayerTwoKey BIGINT
);