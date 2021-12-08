-- ::CONF
DROP TABLE configuration IF EXISTS;

CREATE TABLE configuration 
(
	id integer NOT NULL , 
	area integer NOT NULL , 
	pageId integer DEFAULT -1, 
	pageTree integer, 
	pageLevel integer, 
	pageSequence integer, 
	pageComponent integer DEFAULT -1, 
	paramName varchar (250) NOT NULL ,
	paramDescription LONGVARCHAR,
	queryModel LONGVARCHAR,
	querySchema varchar (50), -- DEFAULT 'select',
	queryType varchar (50), -- DEFAULT 'prepared',
	queryParameters integer,
	queryParameterNames varchar(250),
	queryClassName varchar(250),
	dbPool varchar (50), -- DEFAULT 'pgsql',
	resourceName varchar (200),
	resourcePath varchar (200),
	uri varchar (150), 
	querystring varchar (150), 
	xmlData LONGVARCHAR,
	xmlOldvalues LONGVARCHAR,
	error_code varchar (50),
	log_date timestamp DEFAULT 'NOW', 
	custom varchar (50),
	custom1 varchar (50),
	CONSTRAINT pk_configuration PRIMARY KEY (area, id, pageId, pageComponent)
);

-- ::LOGS

DROP TABLE LOGS IF EXISTS;

CREATE TABLE LOGS 
(
	ID varchar (50) NOT NULL , 
	PAGE varchar (50) NOT NULL , 
	FORWARD varchar (50) NOT NULL , 
	URI varchar (50) NOT NULL , 
	QUERYSTRING varchar (50) NOT NULL , 
	CLEN varchar (50) NOT NULL , 
	RHOST varchar (50) NOT NULL , 
	SESSIONID varchar (50) NOT NULL , 
	HEADERS varchar (250) NOT NULL ,
	ERROR_CODE varchar (50),
	LOG_DATE DATETIME DEFAULT 'NOW', 
	CUSTOM varchar (50),
	CUSTOM1 varchar (50),
	CONSTRAINT pk_logs PRIMARY KEY (ID)
);

-- ::ERRORS

DROP TABLE ERRORS IF EXISTS;

CREATE TABLE ERRORS 
(
	ID varchar (50) NOT NULL , 
	errorLabel varchar (150) NOT NULL , 
	errorDescription varchar (250) NOT NULL , 
	servletName varchar (50) NOT NULL , 
	servletMap varchar (50) NOT NULL , 
	LOG_DATE DATETIME DEFAULT 'NOW', 
	CUSTOM varchar (50),
	CUSTOM1 varchar (50),
	CUSTOM2 varchar (50),
	CUSTOM3 varchar (50),
	CUSTOM4 varchar (50),
	CUSTOM5 varchar (50),
	CONSTRAINT pk_errors PRIMARY KEY (ID)
);

-- ::XMLMODELS

DROP TABLE XMLMODELS IF EXISTS;

CREATE TABLE XMLMODELS 
(
	ID varchar (50) NOT NULL , 
	xmlKey varchar (50) NOT NULL ,
	xmlLabel varchar (50) NOT NULL , 
	xmlDescription varchar (50) NOT NULL , 
	servletName varchar (50) NOT NULL , 
	servletMap varchar (50) NOT NULL , 
	xmlContent LONGVARCHAR NOT NULL , 
	xmlAuthor varchar (50) NOT NULL , 
	xmlFlow varchar (50) NOT NULL , 
	xmlMedia varchar (50) NOT NULL , 
	xmlResource bit NOT NULL , 
	xmlDate DATETIME DEFAULT 'NOW', 
	CUSTOM varchar (50),
	CONSTRAINT pk_xmlmodels PRIMARY KEY (ID, xmlKey)
);

-- ::XMLRESULTS

DROP TABLE XMLRESULTS IF EXISTS;

CREATE TABLE XMLRESULTS 
(
	ID varchar (50) NOT NULL , 
	xmlKey varchar (50) NOT NULL ,
	xmlData LONGVARCHAR NOT NULL , 
	xslOutput LONGVARCHAR NOT NULL , 
	xslView varchar (150) NOT NULL , 
	singletonInstance varchar (50) NOT NULL , 
	documentInstance varchar (50) NOT NULL , 
	CUSTOM varchar (50),
	CONSTRAINT pk_xmlresults PRIMARY KEY (ID, xmlKey)
);

