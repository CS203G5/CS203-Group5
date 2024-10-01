-- DROP DATABASE IF EXISTS cs203_db;
-- CREATE SCHEMA cs203_db;

USE cs203_db;

DROP PROCEDURE IF EXISTS getDuelsByTournament;
DROP PROCEDURE IF EXISTS getByPlayer;
DROP PROCEDURE IF EXISTS deleteDuel;

DELIMITER $$


CREATE DEFINER=`root`@`localhost` PROCEDURE `getDuelsByTournament`(IN p_tournament_id BIGINT)
BEGIN
	SELECT * FROM Duel WHERE tournament_id = p_tournament_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getByPlayer`(IN p_pid BIGINT)
BEGIN
    SELECT * FROM Duel WHERE pid1 = p_pid OR pid2 = p_pid;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `deleteDuel`(IN p_duel_id BIGINT)
BEGIN
    DELETE FROM Duel WHERE duel_id = p_duel_id;
END$$


DELIMITER ;

