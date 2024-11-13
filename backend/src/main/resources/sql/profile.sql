USE cs203_db;

-- Dropping existing procedures if they exist
DROP PROCEDURE IF EXISTS get_profile_by_id;
DROP PROCEDURE IF EXISTS get_profiles_by_username;
DROP PROCEDURE IF EXISTS get_profiles_by_email;
DROP PROCEDURE IF EXISTS delete_profile;
DROP PROCEDURE IF EXISTS create_profile_with_defaults;

DELIMITER $$

-- Procedure to get a profile by its ID
CREATE DEFINER=`root`@`localhost` PROCEDURE `get_profile_by_id`(IN p_profile_id BIGINT)
BEGIN
	SELECT * FROM profile WHERE profile_id = p_profile_id;
END$$

-- Procedure to search profiles by username
CREATE DEFINER=`root`@`localhost` PROCEDURE `get_profiles_by_username`(IN p_username VARCHAR(255))
BEGIN
	SELECT * FROM profile WHERE username LIKE CONCAT('%', p_username, '%');
END$$

-- Procedure to search profiles by email
CREATE DEFINER=`root`@`localhost` PROCEDURE `get_profiles_by_email`(IN p_email VARCHAR(255))
BEGIN
	SELECT * FROM profile WHERE email = p_email;
END$$

-- Procedure to delete a profile by its ID
CREATE DEFINER=`root`@`localhost` PROCEDURE `delete_profile`(IN p_profile_id BIGINT)
BEGIN
    DELETE FROM profile WHERE profile_id = p_profile_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `create_profile_with_defaults`(IN p_username VARCHAR(255))
BEGIN
    INSERT INTO profile (bio, email, privacy_settings, rating, role, username)
    VALUES (NULL, 'newuser@example.com', 'public', 0, 'USER', p_username);
END$$

DELIMITER ;
