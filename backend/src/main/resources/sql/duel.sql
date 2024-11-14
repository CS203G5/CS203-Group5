-- DROP DATABASE IF EXISTS cs203_db;
-- CREATE SCHEMA cs203_db;

USE cs203_db;

DROP TABLE IF EXISTS duel;
CREATE TABLE duel (
    duel_id BIGINT AUTO_INCREMENT PRIMARY KEY,  
    round_name VARCHAR(255),                    
    winner BIGINT,                              
    
    -- Foreign keys for player1 and player2 (Profile IDs)
    pid1 BIGINT,
    pid2 BIGINT,
    
    -- Foreign key for the associated tournament
    tournament_id BIGINT NOT NULL,
    
    -- Embedded DuelResult fields
    player1Time BIGINT,                      
    player2Time BIGINT,                      

    -- Foreign key constraints
    CONSTRAINT fk_player1 FOREIGN KEY (pid1) REFERENCES profile(profile_id),
    CONSTRAINT fk_player2 FOREIGN KEY (pid2) REFERENCES profile(profile_id),
    CONSTRAINT fk_tournament FOREIGN KEY (tournament_id) REFERENCES tournament(tournament_id)
);


DROP PROCEDURE IF EXISTS getDuelsByTournament;
DROP PROCEDURE IF EXISTS getDuelsByRoundName;
DROP PROCEDURE IF EXISTS getDuelsByPlayer;
DROP PROCEDURE IF EXISTS createDuel;
DROP PROCEDURE IF EXISTS deleteDuel;

DELIMITER $$


CREATE DEFINER=root@localhost PROCEDURE getDuelsByTournament(IN p_tournament_id BIGINT)
BEGIN
 SELECT * FROM Duel WHERE tournament_id = p_tournament_id;
END$$

CREATE DEFINER=root@localhost PROCEDURE getDuelsByRoundName(IN p_round_name VARCHAR(255))
BEGIN
 SELECT * FROM Duel WHERE round_name = p_round_name;
END$$

CREATE DEFINER=root@localhost PROCEDURE getDuelsByPlayer(IN p_pid BIGINT)
BEGIN
    SELECT * FROM Duel WHERE pid1 = p_pid OR pid2 = p_pid;
END$$

CREATE DEFINER=root@localhost PROCEDURE createDuel(
    IN p_tid BIGINT,
    IN p_round_name VARCHAR(255), 
    IN p_pid1 BIGINT, 
    IN p_pid2 BIGINT, 
    IN p_winner BIGINT,
    OUT p_duel_id BIGINT 
)
BEGIN
    IF p_pid1 = p_pid2 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Players must be different';
    ELSE
        IF EXISTS (
            SELECT 1 
            FROM Duel 
            WHERE tournament_id = p_tid 
                AND round_name = p_round_name 
                AND pid1 = p_pid1 
                AND pid2 = p_pid2
        ) THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'A duel with the same players, round, and tournament already exists';
        ELSE
            INSERT INTO Duel (tournament_id, round_name, pid1, pid2, winner) 
            VALUES (p_tid, p_round_name, p_pid1, p_pid2, p_winner);
            SELECT 'Duel created successfully' AS message;
        END IF;
    END IF;
END$$


CREATE DEFINER=root@localhost PROCEDURE deleteDuel(IN p_duel_id BIGINT)
BEGIN
    DELETE FROM Duel WHERE duel_id = p_duel_id;
END$$


DELIMITER ;