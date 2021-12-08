/*
 * BaseManager.java - jappframework (jappframework.jar)
 * Copyright (C) 2005
 * Source file created on 5-dic-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [BaseManager]
 * 2DO:
 *
 */

package com.blogspot.fravalle.lib.data.sql;

import java.sql.Connection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import com.blogspot.fravalle.lib.bl.business.exceptions.FrameworkDatabaseException;
import com.blogspot.fravalle.lib.bl.business.exceptions.FrameworkFatalException;
import com.blogspot.fravalle.lib.gui.AWindow;
import com.blogspot.fravalle.lib.monitor.ErrorCodes;
import com.blogspot.fravalle.lib.monitor.IErrorCodes;

/**
 * @author antares
 */
public abstract class ABaseManager implements IBaseManager {

	public int _dbServer = DB_SERVER_UNDEFINED;
	
	public int getDbServer() {
		return _dbServer;
	}
	
	private Connection conn;
	
	protected DataConnection configuration;
	
	protected static Logger getLogger() {
		return AWindow.getMainLogger();
	}
	
	protected static AWindow getMDIContainer() {
		return AWindow.getWindowContainer();
	}
	
	public String getServerType(int serverType) {
		if (serverType == DB_SERVER_MYSQL)
			return PARSE_MYSQL;
		else if (serverType == DB_SERVER_POSTGRESQL)
			return PARSE_POSTGRESQL;
		else
			return "other";
	}
	
	protected String convertiTipo(String columnType, int serverType) {
		if (serverType == DB_SERVER_MYSQL) {
			if (columnType.equals("INTEGER"))
				return "int4";
			else if (columnType.equals("VARCHAR"))
				return columnType.toLowerCase();
			else if (columnType.equals("MEDIUMINT") || columnType.equals("SMALLINT"))
				return "int2";
			else if (columnType.equals("DATE"))
				return "date";
			else if (columnType.equals("TIMESTAMP"))
				return "timestamp";
		} /* else if (serverType == DB_SERVER_POSTGRESQL) {
			mediumint(9)
		} */
		return columnType;
	}
	
	private boolean isDbServer(String what) {
		getLogger().info( ErrorCodes.error(IErrorCodes.CODE_MESSAGE_GENERIC) + new Object[]{"Connesso al database server=" + conn.getClass().getName()} );
		return conn.getClass().getName().indexOf(what) != -1;
	}
	
	public void clearConnection() {
		this.conn = null;
	}
	
	protected Connection getConnection()  /* throws FrameworkDatabaseException */ {
		/* if (conn == null)
			throw new FrameworkDatabaseException("Connessione non disponibile"); */
		if ( this.isDbServer(PARSE_MYSQL) )
			this._dbServer = DB_SERVER_MYSQL;
		else if ( this.isDbServer(PARSE_POSTGRESQL) )
			this._dbServer = DB_SERVER_POSTGRESQL;
		else
			this._dbServer = DB_SERVER_OTHER;
		return conn;
	}
	
	protected void setConnection(Connection conn) throws FrameworkDatabaseException {
		this.conn = conn;
		if (conn == null)
			throw new FrameworkDatabaseException("Connessione non disponibile");
		
		if ( this.isDbServer(PARSE_MYSQL) )
			this._dbServer = DB_SERVER_MYSQL;
		else if ( this.isDbServer(PARSE_POSTGRESQL) )
			this._dbServer = DB_SERVER_POSTGRESQL;
		else
			this._dbServer = DB_SERVER_OTHER;
	}
	
	protected boolean isConnection() {
		return this.conn != null;
	}
	
	/**
	 * Metodo di recupero generico dati implementato da ogni classe che eredita BaseManager.
	 * @param search
	 * oggetto rappresentante i riferimenti della ricerca
	 * @return
	 * qualsiasi oggetto
	 */
	protected abstract Object get(SearchConditions search) throws FrameworkFatalException, FrameworkDatabaseException;
	
	/**
	 * Metodo di recupero dati esposto per ogni classe che eredita BaseManager. Il tipo oggetto e'
	 * legato all'oggetto istanziato nel metodo astratto di get della classe che eredita BaseManager
	 * @param search
	 * oggetto rappresentante i riferimenti della ricerca
	 * @return
	 * qualsiasi oggetto
	 */
	public Object getData(SearchConditions search) throws FrameworkFatalException, FrameworkDatabaseException {
		Object o = get(search);
		/*
		Monitor.log(search.toString());
		if (o instanceof String)
			Monitor.log(String.valueOf(o));
		else if (o instanceof StringBuffer)
			Monitor.log(o.toString());
		*/
		return o;
	}
	
	// public abstract IBaseManager getInstance();
	
	public Object getAvailableServers(Object o) {
		for (Enumeration e = configuration.keys(); e.hasMoreElements();) {
			String key = String.valueOf(e.nextElement());
			if (key.indexOf("db.") != -1 && key.indexOf(".url") != -1) {
				String poolName = key.substring(0, key.indexOf(".url"));
				if (o instanceof Vector)
					((Vector)o).add(poolName);
				else
					((Hashtable)o).put(key,configuration.getProperty(key));
			}
		}
		return o;
	}

	
}
