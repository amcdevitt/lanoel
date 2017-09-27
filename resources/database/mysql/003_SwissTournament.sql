CREATE TABLE IF NOT EXISTS TournamentSwiss_Round
(
TournamentKey BIGINT NOT NULL,
RoundData JSON
);

CREATE TABLE IF NOT EXISTS TournamentSwiss_CurrentRoundPairing
(
TournamentKey BIGINT NOT NULL,
PlayerOneKey BIGINT NOT NULL,
PlayerTwoKey BIGINT
);