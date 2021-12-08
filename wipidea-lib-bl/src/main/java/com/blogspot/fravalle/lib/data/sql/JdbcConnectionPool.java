/*
 * JdbcConnectionPool.java - jappframework (jappframework.jar)
 * Copyright (C) 2005
 * Source file created on 5-dic-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [JdbcConnectionPool]
 * 2DO:
 *
 */

package com.blogspot.fravalle.lib.data.sql;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.blogspot.fravalle.lib.bl.business.exceptions.FrameworkDatabaseException;
import com.blogspot.fravalle.lib.bl.business.exceptions.FrameworkFatalException;
import com.blogspot.fravalle.lib.data.db.pooling.PoolManager;
import com.blogspot.fravalle.lib.monitor.Monitor;
import com.blogspot.fravalle.util.Constants;
import com.blogspot.fravalle.util.UIRes;

/**
 * @author antares
 */
public class JdbcConnectionPool extends DatabaseManager {
	
	public PoolManager poolMgr = PoolManager.getInstance();
	// private JdbcConnectionPool singleton = new JdbcConnectionPool();

	public JdbcConnectionPool() {
		super(new DatabaseConfigurator().getConfigurator());
		poolMgr = PoolManager.getInstance();
	}
	
	public JdbcConnectionPool(String configUrl) {
		super(configUrl);
		poolMgr = PoolManager.getInstance();
	}
	
	public JdbcConnectionPool(String configUrl, String strPoolname) throws FrameworkDatabaseException {
		super(configUrl);
		poolMgr = PoolManager.getInstance();
		setConnection( poolMgr.getConnection( strPoolname ) );
		Monitor.log( UIRes.getLabel("event.db.connection.pool.create") );
	}
	
	public void setPool(String strPoolname) throws FrameworkDatabaseException {
		setConnection( poolMgr.getConnection( strPoolname ) );
		Monitor.log( UIRes.getLabel("event.db.connection.pool.create") );
	}
	
	public Object getAvailablePools(Object o) {
		for (Enumeration e = configuration.keys(); e.hasMoreElements();) {
			String key = String.valueOf(e.nextElement());
			if (key.indexOf(".url") != -1) {
				String poolName = key.substring(0, key.indexOf(".url"));
				if (o instanceof Vector)
					((Vector)o).add(poolName);
				else
					((Hashtable)o).put(key,configuration.getProperty(key));
			}
		}
		return o;
	}
	
	/* public JdbcConnectionPool getInstance() {
		return singleton;
	} */
	
	/* (non-Javadoc)
	 * @see com.blogspot.fravalle.lib.data.sql.BaseManager#get(com.blogspot.fravalle.lib.data.sql.SearchConditions)
	 */
	protected Object get(SearchConditions search) throws FrameworkFatalException, FrameworkDatabaseException {
		Object rtObject = null;
		if (!isConnection() && search.getPoolName() != null) {
			setConnection( poolMgr.getConnection( search.getPoolName() ) );
		}
		
		if (search.isXmlBridge()) {
			rtObject = getStructuredData(search);
			Monitor.log( Constants._logHistoryTag + "Ricezione struttura dati in formato xml bridge" );
		} else {
			rtObject = getVectorData(search);
			Monitor.log( Constants._logHistoryTag + "Ricezione struttura dati in formato vettoriale" );
		}
		
		poolMgr.freeConnection(search.getPoolName(),getConnection());
		return rtObject;
	};

	
	public static final void main (String[] args) {
		new JdbcConnectionPool();
	}
	
}



class DatabaseConfigurator {
	private String configurator;
	
	public DatabaseConfigurator() {}
	
	public String getConfigurator() {
		this.configurator = ClassLoader.getSystemResource(".").getPath() + "/db.properties";
		if ( !this.exists() ) {
			StringBuffer buffer = new StringBuffer(512);
			buffer.append("drivers=org.postgresql.Driver org.gjt.mm.mysql.Driver sun.jdbc.odbc.JdbcOdbcDriver org.hsqldb.jdbcDriver");
			buffer.append("\n");
			buffer.append("logfile="+System.getProperty("java.io.tmpdir")+"//global-connections.log");
			buffer.append("\n");
			buffer.append("loglevel=error");
			buffer.append("\n");
			buffer.append("url=jdbc:postgresql://db.orion.lan:5432/weblan");
			buffer.append("\n");
			buffer.append("user=guest");
			buffer.append("\n");
			buffer.append("password=guest");
			
			try {
				File tmpFile = File.createTempFile("data_connection-",".properties");
				tmpFile.deleteOnExit();
				PrintWriter out=new PrintWriter(new BufferedWriter(new FileWriter(tmpFile)));
				out.write(buffer.toString());
				out.flush();
				out.close();
				configurator = tmpFile.getAbsolutePath();
			} catch (IOException e) {
				Monitor.debug(e);
			} finally {
				Runtime.getRuntime().gc();
			}
		}
		
		return configurator;
	}
	
	private boolean exists() {
		return (new File(this.configurator)).exists();
	}
	
}