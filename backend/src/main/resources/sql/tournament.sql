-- DROP DATABASE IF EXISTS cs203_db;
-- CREATE SCHEMA cs203_db;

USE cs203_db;

DROP PROCEDURE IF EXISTS getTournamentByOrganizer;
DROP PROCEDURE IF EXISTS fuzzySearchTournament;
DROP PROCEDURE IF EXISTS getTournamentByDate;
DROP PROCEDURE IF EXISTS getTournamentByMatchingAlgo;
DROP PROCEDURE IF EXISTS getTournamentBySorted;
DROP PROCEDURE IF EXISTS deleteTournament;

DELIMITER $$

-- TOURNAMENT --
CREATE DEFINER=`root`@`localhost` PROCEDURE `getTournamentByOrganizer`(IN p_organizer_id BIGINT)
BEGIN
	SELECT * FROM Tournament WHERE organizer_id = p_organizer_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `fuzzySearchTournament`(IN p_search_term VARCHAR(255))
BEGIN
    SELECT *
    FROM Tournament
    WHERE name LIKE CONCAT('%', p_search_term, '%')
        OR location LIKE CONCAT('%', p_search_term, '%')
        OR description LIKE CONCAT('%', p_search_term, '%');
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getTournamentByDate`(IN p_startDate VARCHAR(255), p_endDate VARCHAR(255))
BEGIN
	SELECT *
    FROM Tournament 
    WHERE date BETWEEN p_startDate and p_endDate;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getTournamentBySorted`(IN p_sortBy VARCHAR(255), IN p_order VARCHAR(4))
BEGIN
    SET @sql = CONCAT('SELECT * FROM Tournament ORDER BY ', p_sortBy, ' ', p_order);
    
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
END$$


CREATE DEFINER=`root`@`localhost` PROCEDURE `getTournamentByMatchingAlgo`(IN p_isRandom boolean)
BEGIN
	SELECT * FROM Tournament WHERE is_random = p_isRandom;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `deleteTournament`(IN p_tournament_id BIGINT)
BEGIN
    DELETE FROM Duel WHERE tournament_id = p_tournament_id;
END$$
