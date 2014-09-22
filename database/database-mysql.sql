/*
Created		22/09/2014
Modified		22/09/2014
Project		
Model		
Company		
Author		
Version		
Database		mySQL 5 
*/


drop table IF EXISTS READER_HISTORY;
drop table IF EXISTS POLICY_HISTORY;
drop table IF EXISTS AUTOMATION_POLICIES;
drop table IF EXISTS AUTOMATION_CONNECTION;
drop table IF EXISTS AUTOMATION_READER;
drop table IF EXISTS AUTOMATION_USERS;


Create table AUTOMATION_USERS (
	LOGIN Varchar(50) NOT NULL,
	NAME Char(255),
	PASSWORD Char(255),
	EMAIL Char(255),
	ENABLED Char(1),
 Primary Key (LOGIN)) ENGINE = InnoDB;

Create table AUTOMATION_READER (
	READER_NAME Char(50) NOT NULL,
	CRON_INTERVAL Char(20),
	LOGIN Varchar(50) NOT NULL,
	CONNECTION_NAME Char(50) NOT NULL,
	READER_SQL Text,
	ENABLED Char(1),
	LOGGING Char(1),
 Primary Key (READER_NAME)) ENGINE = InnoDB;

Create table AUTOMATION_CONNECTION (
	CONNECTION_NAME Char(50) NOT NULL,
	USERNAMER Char(50),
	PASSWORD Char(255),
	JDBC_URL Char(20),
	ENABLED Char(1),
 Primary Key (CONNECTION_NAME)) ENGINE = InnoDB;

Create table AUTOMATION_POLICIES (
	POLICY_NAME Char(50) NOT NULL,
	READER_NAME Char(50) NOT NULL,
	ENABLED Varchar(1),
	SCRIPT Text,
	EXECUTION_ORDER Int,
	LOGGING Char(1),
 Primary Key (POLICY_NAME)) ENGINE = InnoDB;

Create table POLICY_HISTORY (
	POLICY_NAME Char(50) NOT NULL,
	EXECUTION_DATE Timestamp NOT NULL,
	EXECUTION_TIME Int,
 Primary Key (EXECUTION_DATE)) ENGINE = InnoDB;

Create table READER_HISTORY (
	READER_NAME Char(50) NOT NULL,
	EXECUTION_DATE Timestamp NOT NULL,
	EVENT_COUNT Int,
	EXECUTION_TIME Int,
	HIST_TEXT Text,
 Primary Key (EXECUTION_DATE)) ENGINE = InnoDB;


Alter table AUTOMATION_READER add Foreign Key (LOGIN) references AUTOMATION_USERS (LOGIN) on delete  restrict on update  restrict;
Alter table AUTOMATION_POLICIES add Foreign Key (READER_NAME) references AUTOMATION_READER (READER_NAME) on delete  restrict on update  restrict;
Alter table READER_HISTORY add Foreign Key (READER_NAME) references AUTOMATION_READER (READER_NAME) on delete  restrict on update  restrict;
Alter table AUTOMATION_READER add Foreign Key (CONNECTION_NAME) references AUTOMATION_CONNECTION (CONNECTION_NAME) on delete  restrict on update  restrict;
Alter table POLICY_HISTORY add Foreign Key (POLICY_NAME) references AUTOMATION_POLICIES (POLICY_NAME) on delete  restrict on update  restrict;


/* Users permissions */


