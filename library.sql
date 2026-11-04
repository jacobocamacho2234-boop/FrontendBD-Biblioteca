/*M!999999\- enable the sandbox mode */ 
-- MariaDB dump 10.20-11.8.9-MariaDB, for Win64 (AMD64)
--
-- Host: localhost    Database: smart_library
-- ------------------------------------------------------
-- Server version	11.8.9-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*M!100616 SET @OLD_NOTE_VERBOSITY=@@NOTE_VERBOSITY, NOTE_VERBOSITY=0 */;

--
-- Table structure for table `book`
--

DROP TABLE IF EXISTS `book`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `book` (
  `id_book` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(120) NOT NULL,
  `author` varchar(80) NOT NULL,
  `isbn` varchar(20) DEFAULT NULL,
  `id_category` int(11) DEFAULT NULL,
  `total_copies` int(11) NOT NULL DEFAULT 1,
  `copies_available` int(11) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id_book`),
  UNIQUE KEY `isbn` (`isbn`),
  KEY `id_category` (`id_category`),
  CONSTRAINT `book_ibfk_1` FOREIGN KEY (`id_category`) REFERENCES `category` (`id_category`),
  CONSTRAINT `CONSTRAINT_1` CHECK (`total_copies` >= 1),
  CONSTRAINT `CONSTRAINT_2` CHECK (`copies_available` >= 0)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `book`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `book` WRITE;
/*!40000 ALTER TABLE `book` DISABLE KEYS */;
INSERT INTO `book` VALUES
(1,'Java: How to Program','Paul Deitel','9780000000001',1,3,2),
(2,'Head First Java','Kathy Sierra','9780000000002',1,2,2),
(3,'Clean Code','Robert C. Martin','9780000000003',1,5,1),
(4,'Database System Concepts','Abraham Silberschatz','9780000000004',2,3,3),
(5,'Learning SQL','Alan Beaulieu','9780000000005',2,2,1),
(6,'Linear Algebra','Stanley Grossman','9780000000006',3,4,2),
(7,'Calculus','James Stewart','9780000000007',3,4,2),
(8,'One Hundred Years of Solitude','Gabriel García Márquez','9780000000008',4,3,3),
(9,'The Little Prince','Antoine de Saint-Exupéry','9780000000009',4,2,2),
(10,'A Brief History of Time','Stephen Hawking','9780000000010',5,2,1);
/*!40000 ALTER TABLE `book` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `id_category` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`id_category`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES
(4,'Databases'),
(3,'Math'),
(2,'Novel'),
(1,'Programming'),
(5,'Science');
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `fine`
--

DROP TABLE IF EXISTS `fine`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `fine` (
  `id_fine` int(11) NOT NULL AUTO_INCREMENT,
  `id_loan` int(11) NOT NULL,
  `amount` decimal(8,2) NOT NULL,
  `paid` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id_fine`),
  KEY `id_loan` (`id_loan`),
  CONSTRAINT `fine_ibfk_1` FOREIGN KEY (`id_loan`) REFERENCES `loan` (`id_loan`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fine`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `fine` WRITE;
/*!40000 ALTER TABLE `fine` DISABLE KEYS */;
/*!40000 ALTER TABLE `fine` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `loan`
--

DROP TABLE IF EXISTS `loan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `loan` (
  `id_loan` int(11) NOT NULL AUTO_INCREMENT,
  `id_users` int(11) NOT NULL,
  `id_book` int(11) NOT NULL,
  `loan_date` date NOT NULL,
  `deadline` date NOT NULL,
  `return_date` date DEFAULT NULL,
  `state` enum('active','returned') NOT NULL DEFAULT 'active',
  PRIMARY KEY (`id_loan`),
  KEY `id_users` (`id_users`),
  KEY `id_book` (`id_book`),
  CONSTRAINT `loan_ibfk_1` FOREIGN KEY (`id_users`) REFERENCES `users` (`id_users`),
  CONSTRAINT `loan_ibfk_2` FOREIGN KEY (`id_book`) REFERENCES `book` (`id_book`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `loan`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `loan` WRITE;
/*!40000 ALTER TABLE `loan` DISABLE KEYS */;
INSERT INTO `loan` VALUES
(1,1,1,'2026-10-24','2026-10-31',NULL,'active'),
(2,2,3,'2026-10-29','2026-11-05',NULL,'active'),
(3,4,5,'2026-11-02','2026-11-09',NULL,'active'),
(4,3,2,'2026-10-14','2026-10-21','2026-10-21','returned');
/*!40000 ALTER TABLE `loan` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id_users` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(80) NOT NULL,
  `email` varchar(80) DEFAULT NULL,
  `type` enum('student','teacher','admin') DEFAULT 'student',
  `active` tinyint(1) DEFAULT 1,
  PRIMARY KEY (`id_users`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES
(1,'Ana López','ana.lopez@gmail.com','student',1),
(2,'Carlos Ramírez','carlos.ramirez@gmail.com','student',1),
(3,'Martha Ruiz','martha.ruiz@gmail.com','teacher',1),
(4,'Luis Pérez','luis.perez@gmail.com','student',1),
(5,'Library admin','admin@gmail.com','admin',1);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*M!100616 SET NOTE_VERBOSITY=@OLD_NOTE_VERBOSITY */;

-- Dump completed on 2026-11-03 19:31:48
