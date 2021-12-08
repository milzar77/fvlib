-- DROP TABLE configuration;
-- Tabella da destinare a database secondario stats
CREATE TABLE configuration 
(
	id smallint NOT NULL , 
	area smallint NOT NULL , 
	pageId smallint DEFAULT -1, 
	pageTree smallint, 
	pageLevel smallint, 
	pageSequence smallint, 
	pageComponent smallint DEFAULT -1, 
	paramName varchar (250) NOT NULL ,
	paramDescription text,
	queryModel text,
	querySchema varchar (50), -- DEFAULT 'select',
	queryType varchar (50), -- DEFAULT 'prepared',
	queryParameters smallint,
	queryParameterNames varchar(250),
	queryClassName varchar(250),
	dbPool varchar (50), -- DEFAULT 'pgsql',
	resourceName varchar (200),
	resourcePath varchar (200),
	uri varchar (150), 
	querystring varchar (150), 
	xmlData text,
	xmlOldvalues text,
	error_code varchar (50),
	log_date timestamp DEFAULT 'NOW', 
	custom varchar (50),
	custom1 varchar (50),
	CONSTRAINT pk_configuration PRIMARY KEY (area, id, pageId, pageComponent)
)
WITHOUT OIDS;
ALTER TABLE configuration OWNER TO madsql;
COMMENT ON TABLE configuration IS 'Elenco impostazioni globali';


-- DROP TABLE logs;
-- Tabella da destinare a database secondario stats
CREATE TABLE logs 
(
	id varchar (50) NOT NULL , 
	page varchar (150) NOT NULL , 
	forward varchar (50) NOT NULL , 
	uri varchar (150) NOT NULL , 
	querystring varchar (150) NOT NULL , 
	clen varchar (50) NOT NULL , 
	rhost varchar (50) NOT NULL , 
	sessionid varchar (50) NOT NULL , 
	headers text NOT NULL ,
	error_code varchar (50),
	log_date timestamp DEFAULT 'NOW', 
	custom varchar (50),
	custom1 varchar (50),
	CONSTRAINT pk_logs PRIMARY KEY (id)
)
WITHOUT OIDS;
ALTER TABLE logs OWNER TO madsql;
COMMENT ON TABLE logs IS 'Elenco eventi globali';
-- COMMENT ON COLUMN logs.page IS 'page name';

CREATE TABLE errors 
(
	ID varchar (50) NOT NULL , 
	errorLabel varchar (150) NOT NULL , 
	errorDescription varchar (250) NOT NULL , 
	servletName varchar (50) NOT NULL , 
	servletMap varchar (50) NOT NULL , 
	LOG_DATE timestamp DEFAULT 'NOW', 
	CUSTOM varchar (50),
	CUSTOM1 varchar (50),
	CUSTOM2 varchar (50),
	CUSTOM3 varchar (50),
	CUSTOM4 varchar (50),
	CUSTOM5 varchar (50),
	CONSTRAINT pk_errors PRIMARY KEY (ID)
)
WITHOUT OIDS;
ALTER TABLE errors OWNER TO madsql;
COMMENT ON TABLE errors IS 'Elenco errori globali';


-- DROP TABLE xmlmodels;

CREATE TABLE xmlmodels 
(
	ID varchar (50) NOT NULL , 
	xmlKey varchar (50) NOT NULL ,
	xmlLabel varchar (250) NOT NULL , 
	xmlDescription varchar (250) NOT NULL , 
	servletName varchar (50) NOT NULL , 
	servletMap varchar (50) NOT NULL , 
	xmlContent text NOT NULL , 
	xmlAuthor varchar (100) NOT NULL , 
	xmlFlow varchar (100) NOT NULL , 
	xmlMedia varchar (50) NOT NULL , 
	xmlResource boolean NOT NULL , 
	xmlDate timestamp DEFAULT 'NOW', 
	CUSTOM varchar (50),
	CONSTRAINT pk_xmlmodels PRIMARY KEY (ID, xmlKey)
)
WITHOUT OIDS;
ALTER TABLE xmlmodels OWNER TO madsql;
COMMENT ON TABLE xmlmodels IS 'Elenco documenti xml globale';



-- DROP TABLE XMLRESULTS IF EXISTS;

CREATE TABLE xmlresults 
(
	ID varchar (50) NOT NULL , 
	xmlKey varchar (50) NOT NULL ,
	xmlData text NOT NULL , 
	xslOutput text NOT NULL , 
	xslView varchar (150) NOT NULL , 
	singletonInstance varchar (50) NOT NULL , 
	documentInstance varchar (50) NOT NULL , 
	CUSTOM varchar (50),
	CONSTRAINT pk_xmlresults PRIMARY KEY (ID, xmlKey)
)
WITHOUT OIDS;
ALTER TABLE xmlresults OWNER TO madsql;
COMMENT ON TABLE xmlresults IS 'Elenco documenti xml trasformati';

-- [DATI DI IMPORT]

