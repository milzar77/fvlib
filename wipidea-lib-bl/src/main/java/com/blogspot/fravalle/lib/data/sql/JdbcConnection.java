/*
 * JdbcConnection.java - jappframework (jappframework.jar)
 * Copyright (C) 2005
 * Source file created on 5-dic-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [JdbcConnection]
 * 2DO:
 *
 */

package com.blogspot.fravalle.lib.data.sql;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.blogspot.fravalle.lib.bl.business.exceptions.FrameworkDatabaseException;
import com.blogspot.fravalle.lib.bl.business.exceptions.FrameworkFatalException;
import com.blogspot.fravalle.lib.monitor.ErrorCodes;
import com.blogspot.fravalle.lib.monitor.Monitor;
import com.blogspot.fravalle.util.Constants;
import com.blogspot.fravalle.util.UIRes;

/**
 * @author antares
 */
public class JdbcConnection extends DatabaseManager {
	
	static private JdbcConnection singleton = new JdbcConnection(Constants._fileDatabaseForDirectConnection);

	static private Properties customProps;
	
	protected JdbcConnection(Properties configProps) {
		super();
		customProps = configProps;
		Properties authConn = new Properties();
		
		if (configProps.getProperty("user") != null && configProps.getProperty("password") != null) {
			authConn.setProperty("user", (String)configProps.getProperty("user"));
			authConn.setProperty("password", (String)configProps.getProperty("password"));
		}
		
		String jdbcDriver = (String)configProps.getProperty("jdbcDriver");
		String jdbcUrl = (String)configProps.getProperty("jdbcUrl");
		
		try {
			if (jdbcDriver != null)
				Class.forName( jdbcDriver );
			setConnection( DriverManager.getConnection( jdbcUrl, authConn) );
			Monitor.log( UIRes.getLabel("event.db.connection.create") );
		} catch (ClassNotFoundException e) {
			Monitor.debug( new Long(ErrorCodes.DATA_DB_NOTFOUND_DRIVER).longValue(), e );
		} catch (SQLException e) {
			Monitor.debug( new Long(ErrorCodes.DATA_DB_CONNECTION_ERROR).longValue(), e );
		} catch (FrameworkDatabaseException e) {
			Monitor.debug( new Long(ErrorCodes.DATA_DB_CONNECTION_ERROR).longValue(), e );
		}
	}
	
	protected JdbcConnection(String configUrl) {
		super(configUrl);
		Properties authConn = new Properties();
		
		if (configuration.getProperty("user") != null && configuration.getProperty("password") != null) {
			authConn.setProperty("user", (String)configuration.getProperty("user"));
			authConn.setProperty("password", (String)configuration.getProperty("password"));
		}
		
		String jdbcDriver = (String)configuration.getProperty("jdbcDriver");
		String jdbcUrl = (String)configuration.getProperty("jdbcUrl");
		
		try {
			if (jdbcDriver != null)
				Class.forName( jdbcDriver );
			setConnection( DriverManager.getConnection( jdbcUrl, authConn) );
			Monitor.log( UIRes.getLabel("event.db.connection.create") );
		} catch (ClassNotFoundException e) {
			Monitor.debug( new Long(ErrorCodes.DATA_DB_NOTFOUND_DRIVER).longValue(), e );
		} catch (SQLException e) {
			Monitor.debug( new Long(ErrorCodes.DATA_DB_CONNECTION_ERROR).longValue(), e );
		} catch (FrameworkDatabaseException e) {
			Monitor.debug( new Long(ErrorCodes.DATA_DB_CONNECTION_ERROR).longValue(), e );
		}
	}
	
	static public synchronized JdbcConnection getInstance(String configUrl) {
		singleton = new JdbcConnection(configUrl);
		return singleton;
	}

	static public synchronized JdbcConnection getInstance(Properties props) {
		if (singleton == null)
			singleton = new JdbcConnection(props);
		else {
			if (!props.equals(customProps))
				singleton = new JdbcConnection(props);
		}
		return singleton;
	}

	
	static public synchronized JdbcConnection getInstance() {
		return singleton;
	}
	/* (non-Javadoc)
	 * @see com.blogspot.fravalle.lib.data.sql.BaseManager#get(com.blogspot.fravalle.lib.data.sql.SearchConditions)
	 */
	protected Object get(SearchConditions search) throws FrameworkFatalException, FrameworkDatabaseException {
		Object rtObject = null;
		
		if (search.isXmlBridge()) {
			rtObject = getStructuredData(search);
			Monitor.log( Constants._logHistoryTag + "Ricezione struttura dati in formato xml bridge" );
		} else {
			rtObject = getVectorData(search);
			Monitor.log( Constants._logHistoryTag + "Ricezione struttura dati in formato vettoriale" );
		}
		
		return rtObject;
	}
	
}
