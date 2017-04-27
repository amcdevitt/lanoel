CREATE TABLE IF NOT EXISTS TournamentParticipant
(
TournamentParticipantKey BIGINT PRIMARY KEY AUTO_INCREMENT,
TournamentKey VARCHAR(255),
ParticipantName VARCHAR(255),
UserName VARCHAR(255)
);

--IF NOT EXISTS( SELECT NULL
--            FROM INFORMATION_SCHEMA.COLUMNS
--           WHERE table_name = 'TournamentLanoel'
--             AND table_schema = 'db_name'
--             AND column_name = 'columnname')  THEN

--  ALTER TABLE `TableName` ADD `ColumnName` int(1) NOT NULL default '0';

--END IF;

CREATE TABLE IF NOT EXISTS Tournament
(
TournamentKey BIGINT PRIMARY KEY AUTO_INCREMENT,
TournamentName VARCHAR(100),
Type VARCHAR(255),
Created DATETIME DEFAULT CURRENT_TIMESTAMP
);

