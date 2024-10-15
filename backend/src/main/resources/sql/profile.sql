USE profile_db;

-- Dropping existing procedures if they exist
DROP PROCEDURE IF EXISTS getProfileById;
DROP PROCEDURE IF EXISTS getProfilesByUsername;
DROP PROCEDURE IF EXISTS getProfilesByEmail;
DROP PROCEDURE IF EXISTS deleteProfile;

DELIMITER $$

-- Procedure to get a profile by its ID
CREATE DEFINER=`root`@`localhost` PROCEDURE `getProfileById`(IN p_profile_id BIGINT)
BEGIN
	SELECT * FROM profile WHERE profileId = p_profile_id;
END$$

-- Procedure to search profiles by username
CREATE DEFINER=`root`@`localhost` PROCEDURE `getProfilesByUsername`(IN p_username VARCHAR(255))
BEGIN
	SELECT * FROM profile WHERE username LIKE CONCAT('%', p_username, '%');
END$$

-- Procedure to search profiles by email
CREATE DEFINER=`root`@`localhost` PROCEDURE `getProfilesByEmail`(IN p_email VARCHAR(255))
BEGIN
	SELECT * FROM profile WHERE email = p_email;
END$$

-- Procedure to delete a profile by its ID
CREATE DEFINER=`root`@`localhost` PROCEDURE `deleteProfile`(IN p_profile_id BIGINT)
BEGIN
    DELETE FROM profile WHERE profileId = p_profile_id;
END$$

DELIMITER ;
