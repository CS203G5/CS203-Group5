USE cs203_db;

DROP PROCEDURE IF EXISTS getParticipantsByUserId;
DROP PROCEDURE IF EXISTS getParticipantsByTournamentId;

DELIMITER $$

CREATE DEFINER=`root`@`localhost` PROCEDURE getParticipantsByUserId(IN userId BIGINT)
BEGIN
    SELECT * FROM participant WHERE user_id = userId;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE getParticipantsByTournamentId(IN tournamentId BIGINT)
BEGIN
    SELECT * FROM participant WHERE tournament_id = tournamentId;
END$$

DELIMITER ;