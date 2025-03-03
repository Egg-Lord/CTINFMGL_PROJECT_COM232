-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: userdatabase
-- ------------------------------------------------------
-- Server version	8.0.41

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
-- Table structure for table `claimhistory`
--

DROP TABLE IF EXISTS `claimhistory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `claimhistory` (
  `accountnumber` char(12) NOT NULL,
  `claim_amount` decimal(10,2) NOT NULL,
  `claim_date` datetime NOT NULL,
  PRIMARY KEY (`accountnumber`,`claim_date`),
  CONSTRAINT `claimhistory_ibfk_1` FOREIGN KEY (`accountnumber`) REFERENCES `userinfotable` (`accountnumber`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `claimhistory`
--

LOCK TABLES `claimhistory` WRITE;
/*!40000 ALTER TABLE `claimhistory` DISABLE KEYS */;
INSERT INTO `claimhistory` VALUES ('129050604229',100.00,'2025-02-26 14:29:59'),('129050604229',500.00,'2025-02-26 14:32:22'),('129050604229',500.00,'2025-02-26 14:32:37'),('129050604229',100.00,'2025-02-26 14:35:03'),('129050604229',500.00,'2025-02-26 15:16:40'),('129050604229',1000.00,'2025-02-28 22:53:29'),('129050604229',1000.00,'2025-03-03 17:43:16');
/*!40000 ALTER TABLE `claimhistory` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `debitcard`
--

DROP TABLE IF EXISTS `debitcard`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `debitcard` (
  `accountnumber` char(12) NOT NULL,
  `cardnumber` char(16) NOT NULL,
  `expires` char(5) NOT NULL,
  `cvv` char(3) NOT NULL,
  PRIMARY KEY (`cardnumber`),
  KEY `accountnumber` (`accountnumber`),
  CONSTRAINT `debitcard_ibfk_1` FOREIGN KEY (`accountnumber`) REFERENCES `userinfotable` (`accountnumber`),
  CONSTRAINT `debitcard_chk_1` CHECK (((length(`cardnumber`) = 16) and regexp_like(`cardnumber`,_utf8mb4'^[0-9]+$'))),
  CONSTRAINT `debitcard_chk_2` CHECK (regexp_like(`expires`,_utf8mb4'^(0[1-9]|1[0-2])\\/[0-9]{2}$')),
  CONSTRAINT `debitcard_chk_3` CHECK (((length(`cvv`) = 3) and regexp_like(`cvv`,_utf8mb4'^[0-9]+$')))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `debitcard`
--

LOCK TABLES `debitcard` WRITE;
/*!40000 ALTER TABLE `debitcard` DISABLE KEYS */;
INSERT INTO `debitcard` VALUES ('854309113869','1092993819457715','03/28','778'),('129050604229','5777910343904849','02/28','331'),('324847942348','9766625980178922','02/28','658');
/*!40000 ALTER TABLE `debitcard` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `deposits`
--

DROP TABLE IF EXISTS `deposits`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `deposits` (
  `accountnumber` char(12) NOT NULL,
  `deposit_amount` decimal(10,2) NOT NULL,
  `deposit_date` datetime NOT NULL,
  KEY `accountnumber` (`accountnumber`),
  CONSTRAINT `deposits_ibfk_1` FOREIGN KEY (`accountnumber`) REFERENCES `userinfotable` (`accountnumber`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `deposits`
--

LOCK TABLES `deposits` WRITE;
/*!40000 ALTER TABLE `deposits` DISABLE KEYS */;
INSERT INTO `deposits` VALUES ('129050604229',100.00,'2025-02-26 14:42:19'),('129050604229',100.00,'2025-02-26 14:46:49'),('129050604229',100.00,'2025-02-26 14:47:25'),('129050604229',300.00,'2025-02-26 14:51:31'),('129050604229',100.00,'2025-02-26 15:03:11'),('129050604229',400.00,'2025-02-26 15:15:52'),('129050604229',1000.00,'2025-02-26 15:32:01'),('129050604229',1000.00,'2025-02-28 21:47:58'),('129050604229',1000.00,'2025-03-03 17:42:29');
/*!40000 ALTER TABLE `deposits` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gobalancedeposit`
--

DROP TABLE IF EXISTS `gobalancedeposit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gobalancedeposit` (
  `accountnumber` char(12) NOT NULL,
  `deposit_amount` decimal(10,2) NOT NULL,
  `deposit_date` datetime NOT NULL,
  PRIMARY KEY (`accountnumber`,`deposit_date`),
  CONSTRAINT `gobalancedeposit_ibfk_1` FOREIGN KEY (`accountnumber`) REFERENCES `userinfotable` (`accountnumber`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gobalancedeposit`
--

LOCK TABLES `gobalancedeposit` WRITE;
/*!40000 ALTER TABLE `gobalancedeposit` DISABLE KEYS */;
INSERT INTO `gobalancedeposit` VALUES ('129050604229',0.00,'2025-02-26 13:11:22'),('129050604229',100.00,'2025-02-26 13:12:51'),('129050604229',100.00,'2025-02-26 13:12:59'),('129050604229',100.00,'2025-02-26 13:24:19'),('129050604229',100.00,'2025-02-26 13:54:09'),('129050604229',100.00,'2025-02-26 14:08:34'),('129050604229',500.00,'2025-02-26 14:13:11'),('129050604229',100.00,'2025-02-26 14:29:57'),('129050604229',500.00,'2025-02-26 14:32:17'),('129050604229',500.00,'2025-02-26 14:32:31'),('129050604229',100.00,'2025-02-26 14:35:02'),('129050604229',500.00,'2025-02-26 15:16:33'),('129050604229',1000.00,'2025-02-28 22:53:17'),('129050604229',1000.00,'2025-03-03 17:43:03'),('324847942348',0.00,'2025-02-26 13:11:52'),('854309113869',0.00,'2025-03-01 00:06:18');
/*!40000 ALTER TABLE `gobalancedeposit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transactionhistory`
--

DROP TABLE IF EXISTS `transactionhistory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transactionhistory` (
  `transactionID` char(6) NOT NULL,
  `fromaccount` char(12) NOT NULL,
  `toaccount` char(12) NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `transaction_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`transactionID`),
  KEY `fromaccount` (`fromaccount`),
  KEY `toaccount` (`toaccount`),
  CONSTRAINT `transactionhistory_ibfk_1` FOREIGN KEY (`fromaccount`) REFERENCES `userinfotable` (`accountnumber`),
  CONSTRAINT `transactionhistory_ibfk_2` FOREIGN KEY (`toaccount`) REFERENCES `userinfotable` (`accountnumber`),
  CONSTRAINT `transactionhistory_chk_1` CHECK (regexp_like(`transactionID`,_utf8mb4'^[0-9]{6}$'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transactionhistory`
--

LOCK TABLES `transactionhistory` WRITE;
/*!40000 ALTER TABLE `transactionhistory` DISABLE KEYS */;
INSERT INTO `transactionhistory` VALUES ('017510','129050604229','324847942348',100.00,'2025-02-26 06:31:55'),('283912','129050604229','324847942348',1000.00,'2025-02-26 07:16:09'),('284240','129050604229','324847942348',1000.00,'2025-03-03 09:42:49');
/*!40000 ALTER TABLE `transactionhistory` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `userinfotable`
--

DROP TABLE IF EXISTS `userinfotable`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `userinfotable` (
  `accountnumber` char(12) NOT NULL,
  `username` varchar(20) NOT NULL,
  `accountname` varchar(20) NOT NULL,
  `userpassword` varchar(30) NOT NULL,
  `balance` decimal(10,2) NOT NULL,
  `gobalance` decimal(10,2) NOT NULL,
  `phonenumber` char(11) NOT NULL,
  `email` varchar(100) NOT NULL,
  `birthday` date NOT NULL,
  PRIMARY KEY (`accountnumber`),
  CONSTRAINT `userinfotable_chk_1` CHECK (((length(`accountnumber`) = 12) and regexp_like(`accountnumber`,_utf8mb4'^[0-9]+$'))),
  CONSTRAINT `userinfotable_chk_2` CHECK (regexp_like(`username`,_utf8mb4'^[A-Za-z0-9]+$')),
  CONSTRAINT `userinfotable_chk_3` CHECK (regexp_like(`accountname`,_utf8mb4'^[A-Za-z ]+$')),
  CONSTRAINT `userinfotable_chk_4` CHECK (regexp_like(`userpassword`,_utf8mb4'^[A-Za-z0-9]+$')),
  CONSTRAINT `userinfotable_chk_5` CHECK (((length(`phonenumber`) = 11) and regexp_like(`phonenumber`,_utf8mb4'^[0-9]+$'))),
  CONSTRAINT `userinfotable_chk_6` CHECK (regexp_like(`email`,_utf8mb4'^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `userinfotable`
--

LOCK TABLES `userinfotable` WRITE;
/*!40000 ALTER TABLE `userinfotable` DISABLE KEYS */;
INSERT INTO `userinfotable` VALUES ('129050604229','rid','Mark Rid','123',3000.00,0.00,'22222222222','mark@gmail.com','2005-03-30'),('324847942348','airi','Janina Airi','123',2100.00,0.00,'22222222222','airi@gmail.com','2005-01-12'),('854309113869','asd','asdasd','123',0.00,0.00,'12344444444','asd@gmail.com','2025-03-04');
/*!40000 ALTER TABLE `userinfotable` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-03-04  4:57:38
