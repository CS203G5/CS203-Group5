CREATE DATABASE  IF NOT EXISTS `cs203_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `cs203_db`;
-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: cs203_db
-- ------------------------------------------------------
-- Server version	8.2.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `duel`
--

DROP TABLE IF EXISTS `duel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `duel` (
  `duel_id` bigint NOT NULL AUTO_INCREMENT,
  `round_name` varchar(255) DEFAULT NULL,
  `winner` bigint DEFAULT NULL,
  `pid1` bigint DEFAULT NULL,
  `pid2` bigint DEFAULT NULL,
  `tournament_id` bigint NOT NULL,
  `player1Time` bigint DEFAULT NULL,
  `player2Time` bigint DEFAULT NULL,
  PRIMARY KEY (`duel_id`),
  KEY `FKikcxke09iwp67iovelmfu3uyh` (`pid1`),
  KEY `FKhvyiwsaxos0qvsaaym8uw9dt4` (`pid2`),
  KEY `FKornwk1uj7x5446f13v2ja0gfo` (`tournament_id`)
) ENGINE=MyISAM AUTO_INCREMENT=64 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `duel`
--

LOCK TABLES `duel` WRITE;
/*!40000 ALTER TABLE `duel` DISABLE KEYS */;
INSERT INTO `duel` VALUES (17,'1',1,3,2,1,47000,48000),(16,'1',1,1,6,1,44000,46000),(15,'1',2,5,4,1,150,120),(24,'2',2,3,4,1,43000,42000),(25,'2',1,1,NULL,1,NULL,NULL),(26,'3',2,4,1,1,43000,42000),(34,'1',2,3,2,52,43000,42000),(33,'1',2,4,5,52,40000,39000),(35,'1',1,1,NULL,52,NULL,NULL),(36,'2',2,2,1,52,40000,39000),(37,'2',1,5,NULL,52,NULL,NULL),(38,'3',2,5,1,52,40000,39000),(48,'2',2,2,4,202,42000,41000),(47,'1',1,4,3,202,38000,39000),(46,'1',2,1,2,202,40000,39000),(49,'1',1,4,1,252,30000,31000),(50,'1',2,2,3,252,33000,31000),(51,'2',2,4,3,252,51000,41000),(61,'1',0,5,3,352,NULL,NULL),(60,'1',0,2,4,352,NULL,NULL),(62,'1',2,3,1,302,51000,30000),(63,'1',2,2,NULL,302,NULL,NULL);
/*!40000 ALTER TABLE `duel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `participant`
--

DROP TABLE IF EXISTS `participant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `participant` (
  `lose` int NOT NULL,
  `win` int NOT NULL,
  `tournament_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`tournament_id`,`user_id`),
  KEY `FKad1bwn6lqhubfi0acu7vatmqs` (`user_id`),
  CONSTRAINT `FKad1bwn6lqhubfi0acu7vatmqs` FOREIGN KEY (`user_id`) REFERENCES `profile` (`profile_id`),
  CONSTRAINT `FKsemo5l7yljfxrmsqlil3md408` FOREIGN KEY (`tournament_id`) REFERENCES `tournament` (`tournament_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `participant`
--

LOCK TABLES `participant` WRITE;
/*!40000 ALTER TABLE `participant` DISABLE KEYS */;
INSERT INTO `participant` VALUES (0,0,1,1),(0,0,1,2),(0,0,1,3),(0,0,1,4),(0,0,1,5),(0,0,1,6),(0,0,52,1),(0,0,52,2),(0,0,52,3),(0,0,52,4),(0,0,52,5),(0,0,102,1),(0,0,202,1),(0,0,202,2),(0,0,202,3),(0,0,202,4),(0,0,252,1),(0,0,252,2),(0,0,252,3),(0,0,252,4),(0,0,302,1),(0,0,302,2),(0,0,302,3),(0,0,352,2),(0,0,352,3),(0,0,352,4),(0,0,352,5),(0,0,353,17);
/*!40000 ALTER TABLE `participant` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `profile`
--

DROP TABLE IF EXISTS `profile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `profile` (
  `rating` double NOT NULL DEFAULT '0',
  `profile_id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `bio` varchar(255) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `privacy_settings` varchar(255) NOT NULL,
  `role` varchar(255) NOT NULL,
  PRIMARY KEY (`profile_id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `profile`
--

LOCK TABLES `profile` WRITE;
/*!40000 ALTER TABLE `profile` DISABLE KEYS */;
INSERT INTO `profile` VALUES (5.798767327866244,1,'fai','update 2','rufaidahadn@gmail.com','Public','ADMIN'),(-9.384685528584567,2,'lala',NULL,'test@gmail.com','Public','PLAYER'),(0.49560585635585525,3,'lili',NULL,'test@gmail.com','Public','PLAYER'),(-4.37610694931922,4,'mimi',NULL,'test@gmail.com','Public','PLAYER'),(0.2825289764148837,5,'lulu',NULL,'test@gmail.com','Public','PLAYER'),(0,6,'mumu',NULL,'test@gmail.com','Public','PLAYER'),(0,17,'Dio',NULL,'verdio.wong.2023@scis.smu.edu.sg','Public','PLAYER');
/*!40000 ALTER TABLE `profile` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tournament`
--

DROP TABLE IF EXISTS `tournament`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tournament` (
  `date` date DEFAULT NULL,
  `is_random` bit(1) NOT NULL,
  `time` time(6) DEFAULT NULL,
  `modified_at` datetime(6) DEFAULT NULL,
  `organizer_id` bigint DEFAULT NULL,
  `tournament_id` bigint NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `location` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`tournament_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tournament`
--

LOCK TABLES `tournament` WRITE;
/*!40000 ALTER TABLE `tournament` DISABLE KEYS */;
INSERT INTO `tournament` VALUES ('2024-12-06',_binary '','02:07:00.000000','2024-10-20 02:10:35.539804',1,1,'DescriptionDescriptionDescriptionDescriptionDescriptionDescriptionDescriptionDescriptionDescriptionDescriptionDescriptionDescriptionDescriptionDescriptionDescriptionDescription','Location edit','Tournament 1'),('2024-10-24',_binary '','18:53:31.000000','2024-10-20 18:53:31.044642',1,52,'DescriptionDescriptionDescription','location new','new tournament'),('2024-10-24',_binary '','22:59:03.000000','2024-10-20 22:59:04.298550',1,102,'DescriptionDescriptionDescription','testtttttttttttttttttt','testtttt'),('2024-11-22',_binary '\0','23:18:08.000000','2024-10-20 23:18:08.518913',1,202,' NEW TEST NEW TEST NEW TEST NEW TEST',' NEW TEST NEW TEST','tournament NEW TEST'),('2024-11-23',_binary '\0','01:53:47.000000','2024-11-04 01:53:47.679495',1,252,'Tournament with NOT random','LocationLocationLocation','REALLY TEST THIS'),('2024-11-23',_binary '\0','07:44:41.000000','2024-11-14 07:44:41.868173',1,302,'Proper Description','Proper Location','Proper Tournament Name'),('2024-11-29',_binary '','09:43:31.000000','2024-11-14 09:43:31.948603',1,352,'lalala','lalala','lalala'),('2024-11-22',_binary '','10:15:00.000000','2024-11-14 10:16:22.226430',1,353,'Tournament YAYTournament YAYTournament YAY','Tournament YAY edit','Tournament YAY');
/*!40000 ALTER TABLE `tournament` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tournament_seq`
--

DROP TABLE IF EXISTS `tournament_seq`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tournament_seq` (
  `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tournament_seq`
--

LOCK TABLES `tournament_seq` WRITE;
/*!40000 ALTER TABLE `tournament_seq` DISABLE KEYS */;
INSERT INTO `tournament_seq` VALUES (451);
/*!40000 ALTER TABLE `tournament_seq` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'cs203_db'
--

--
-- Dumping routines for database 'cs203_db'
--
/*!50003 DROP PROCEDURE IF EXISTS `createDuel` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `createDuel`(
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
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `deleteDuel` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `deleteDuel`(IN p_duel_id BIGINT)
BEGIN
    DELETE FROM Duel WHERE duel_id = p_duel_id;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `deleteProfile` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `deleteProfile`(IN p_profile_id BIGINT)
BEGIN
    DELETE FROM profile WHERE profileId = p_profile_id;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `deleteTournament` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `deleteTournament`(IN p_tournament_id BIGINT)
BEGIN
    DELETE FROM Duel WHERE tournament_id = p_tournament_id;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `delete_profile` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `delete_profile`(IN p_profile_id BIGINT)
BEGIN
    DELETE FROM profile WHERE profile_id = p_profile_id;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `fuzzySearchTournament` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `fuzzySearchTournament`(IN p_search_term VARCHAR(255))
BEGIN
    SELECT *
    FROM Tournament
    WHERE name LIKE CONCAT('%', p_search_term, '%')
        OR location LIKE CONCAT('%', p_search_term, '%')
        OR description LIKE CONCAT('%', p_search_term, '%');
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getByPlayer` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `getByPlayer`(IN p_pid BIGINT)
BEGIN
    SELECT * FROM Duel WHERE pid1 = p_pid OR pid2 = p_pid;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getDuelsByPlayer` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `getDuelsByPlayer`(IN p_pid BIGINT)
BEGIN
    SELECT * FROM Duel WHERE pid1 = p_pid OR pid2 = p_pid;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getDuelsByRoundName` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `getDuelsByRoundName`(IN p_round_name VARCHAR(255))
BEGIN
	SELECT * FROM Duel WHERE round_name = p_round_name;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getDuelsByTournament` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `getDuelsByTournament`(IN p_tournament_id BIGINT)
BEGIN
	SELECT * FROM Duel WHERE tournament_id = p_tournament_id;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getParticipantsByTournamentId` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `getParticipantsByTournamentId`(IN tournamentId BIGINT)
BEGIN
    SELECT * FROM Participant WHERE tournament_id = tournamentId;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getParticipantsByUserId` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `getParticipantsByUserId`(IN userId BIGINT)
BEGIN
    SELECT * FROM Participant WHERE user_id = userId;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getProfileById` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `getProfileById`(IN p_profile_id BIGINT)
BEGIN
	SELECT * FROM profile WHERE profileId = p_profile_id;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getProfilesByEmail` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `getProfilesByEmail`(IN p_email VARCHAR(255))
BEGIN
	SELECT * FROM profile WHERE email = p_email;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getProfilesByUsername` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `getProfilesByUsername`(IN p_username VARCHAR(255))
BEGIN
	SELECT * FROM profile WHERE username LIKE CONCAT('%', p_username, '%');
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getTournamentByDate` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `getTournamentByDate`(IN p_start_date VARCHAR(255), p_end_date VARCHAR(255))
BEGIN
	SELECT *
    FROM Tournament 
    WHERE date BETWEEN p_start_date and p_end_date;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getTournamentByMatchingAlgo` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `getTournamentByMatchingAlgo`(IN p_is_random boolean)
BEGIN
	SELECT * FROM Tournament WHERE is_random = p_is_random;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getTournamentByOrganizer` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `getTournamentByOrganizer`(IN p_organizer_id BIGINT)
BEGIN
	SELECT * FROM Tournament WHERE organizer_id = p_organizer_id;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getTournamentBySorted` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `getTournamentBySorted`(IN p_sortBy VARCHAR(255), IN p_order VARCHAR(4))
BEGIN
    SET @sql = CONCAT('SELECT * FROM Tournament ORDER BY ', p_sort_by, ' ', p_order);
    
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `get_profiles_by_email` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `get_profiles_by_email`(IN p_email VARCHAR(255))
BEGIN
	SELECT * FROM profile WHERE email = p_email;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `get_profiles_by_username` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `get_profiles_by_username`(IN p_username VARCHAR(255))
BEGIN
	SELECT * FROM profile WHERE username LIKE CONCAT('%', p_username, '%');
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `get_profile_by_id` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `get_profile_by_id`(IN p_profile_id BIGINT)
BEGIN
	SELECT * FROM profile WHERE profile_id = p_profile_id;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-11-14 10:56:33
