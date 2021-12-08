-- Table: netNodes

-- DROP TABLE netNodes;

CREATE TABLE netNodes
(
  id integer NOT NULL,
  ndIp varchar(150) NOT NULL,
  ndName varchar(250) NOT NULL,
  ndDescription varchar(250) NOT NULL,
  servletname varchar(50) NOT NULL,
  servletmap varchar(50) NOT NULL,
  ndCuver varchar(20) NOT NULL,
  ndLastver varchar(20) NOT NULL,
  ndVerdate timestamp DEFAULT 'NOW',
  ndScope varchar(150) NOT NULL,
  ndUsers varchar(250) NOT NULL,
  ndRealusr varchar(50) NOT NULL,
  hwDetailid integer NOT NULL,
  swDetailid integer NOT NULL,
  ndActive bool DEFAULT false,
  custom varchar(50),
  CONSTRAINT pk_netNodes PRIMARY KEY (id, ndIp)
) 
WITHOUT OIDS;
ALTER TABLE netNodes OWNER TO madsql;
COMMENT ON TABLE netNodes IS 'Elenco nodi di rete lan';



-- Table: hwCmps

-- DROP TABLE hwCmps;

CREATE TABLE hwCmps
(
  id integer NOT NULL,
  cmpName varchar(250) NOT NULL,
  cmpDescription varchar(250) NOT NULL,
  servletname varchar(50) NOT NULL,
  servletmap varchar(50) NOT NULL,
  cmpBrand varchar(150) NOT NULL,
  cmpFunc varchar(150) NOT NULL,
  cmpSell timestamp DEFAULT 'NOW',
  cmpWebsite varchar(250) NOT NULL,
  cmpNotes text,
  cmpPlatform varchar(150) NOT NULL,
  cmpDefntport smallint,
  ndActive bool DEFAULT false,
  custom varchar(50),
  CONSTRAINT pk_hwCmps PRIMARY KEY (id)
) 
WITHOUT OIDS;
ALTER TABLE hwCmps OWNER TO madsql;
COMMENT ON TABLE hwCmps IS 'Tabella di riepilogo componenti hardware';


-- Table: swCmps

-- DROP TABLE swCmps;

CREATE TABLE swCmps
(
  id integer NOT NULL,
  swFile varchar(250) NOT NULL,
  swName varchar(250) NOT NULL,
  swDescription varchar(250) NOT NULL,
  servletname varchar(50) NOT NULL,
  servletmap varchar(50) NOT NULL,
  swDownload timestamp DEFAULT 'NOW',
  swWebsite varchar(250) NOT NULL,
  swNotes text,
  swPlatform varchar(150) NOT NULL,
  swVersion varchar(250) DEFAULT '0.0',
  custom varchar(50),
  CONSTRAINT pk_swCmps PRIMARY KEY (id, swFile)
) 
WITHOUT OIDS;
ALTER TABLE swCmps OWNER TO madsql;
COMMENT ON TABLE swCmps IS 'Tabella di riepilogo componenti software';



-- Table: ndHistory

-- DROP TABLE ndHistory;

CREATE TABLE ndHistory
(
  id integer NOT NULL,
  sysName varchar(50) NOT NULL,
  sysVersion varchar(50) DEFAULT '0.0',
  ndVersion varchar(250) NOT NULL,
  lastSave timestamp DEFAULT 'NOW',
  hwDid integer NOT NULL,
  swDid integer NOT NULL,
  historyNotes text,
  custom varchar(50),
  CONSTRAINT pk_ndHistory PRIMARY KEY (id, sysVersion)
) 
WITHOUT OIDS;
ALTER TABLE ndHistory OWNER TO madsql;
COMMENT ON TABLE ndHistory IS 'Tabella di riepilogo storico delle configurazioni';


-- Table: netMap

-- DROP TABLE netMap;

CREATE TABLE netMap
(
  id integer NOT NULL,
  nmName varchar(250) NOT NULL,
  ndId integer NOT NULL,
  custom varchar(50),
  CONSTRAINT pk_netMap PRIMARY KEY (id, ndId)
) 
WITHOUT OIDS;
ALTER TABLE netMap OWNER TO madsql;
COMMENT ON TABLE netMap IS 'Tabella di riepilogo storico delle configurazioni';