/*
 * DataConnection.java - FrameWork (FrameWork.jar)
 * Copyright (C) 2005
 * Source file created on 3-dic-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [Test]
 * TODO:
 *
 */

package com.blogspot.fravalle.lib.data.sql;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.blogspot.fravalle.lib.monitor.ErrorCodes;
import com.blogspot.fravalle.lib.monitor.Monitor;
import com.blogspot.fravalle.util.Constants;
import com.blogspot.fravalle.util.UIRes;

final public class DataConnection extends Properties {
 	
    private String resourcePropertyUrl;
	private String restrictId = "";
    
	public String getDriver() {
	    String s = "";
		try {
			// s = getProperty(restrictId + "driver");
			s = getProperty("drivers");
			if (s == null)
				s = getProperty(restrictId + "driver");
		} catch (Exception e) {
			Monitor.debug(ErrorCodes.DATA_DB_NOTFOUND_DRIVER, e);
		}
	    return s;
	}
	public String getUrl() {
		String s = "";
		try {
			s = getProperty(restrictId + "url");
		} catch (Exception e) {
			Monitor.debug(ErrorCodes.DATA_DB_NOTFOUND_URL, e);
		}
		return s;
	}
	public String getUser() throws Exception {
		String s = "";
		s = getProperty(restrictId + "user");
		return s;
	}
	public String getPassword() throws Exception {
		String s = "";
		s = getProperty(restrictId + "password");
		return s;
	}

	public Properties getCredentials(String rId) {
		this.restrictId = rId+".";
		return this.getCredentials();
	}
	
	public Properties getCredentials() {
		Properties p = new Properties();
		try {
			p.setProperty("user",getUser());
			p.setProperty("password",getPassword());
		} catch (Exception e) {
			Monitor.debug(ErrorCodes.DATA_DB_NOTFOUND_LOGIN, e);
		}
		return p;
	}

	public Properties getDefaultParams() {
		Properties p = new Properties();
		try {
			p.setProperty("jdbcDriver",getDriver());
			p.setProperty("jdbcUrl",getUrl());
		} catch (Exception e) {
			Monitor.debug(ErrorCodes.DATA_DB_NOTFOUND_DRIVER, e);
		}
		return p;
	}
	
	public Properties getDefaultParams(String rId) {
		this.restrictId = rId+".";
		return this.getDefaultParams();
	}
	
	public Properties getParams() {
		Properties p = new Properties();
		try {
			p = this.getCredentials();
			p.putAll(this.getDefaultParams());
			if (this.restrictId!=null)
				p.setProperty("dbPoolName", this.restrictId);
			Monitor.log( UIRes.getLabel("history.db.connection") + String.valueOf(p) );
		} catch (Exception e) {
			Monitor.debug(ErrorCodes.DATA_DB_NOTFOUND_DRIVER, e);
		}
		return p;
	}
	
	public Properties getParams(String rId) {
		this.restrictId = rId+".";
		return this.getParams();
	}
	
	private void setPoolname(String poolname) {
		setProperty("dbPoolName",poolname);
	}

    public DataConnection(String poolname, boolean b) {
	    super();
		this.resourcePropertyUrl = Constants._fileDatabasePath;
		java.io.InputStream is = getClass().getResourceAsStream( resourcePropertyUrl );
		try {
		   load(is);
		} catch (Exception e) {
			Monitor.debug(ErrorCodes.DATA_DB_NOTFOUND_FILECONF, e);
		} finally {
			if (b)
				setPoolname(poolname);
			else
				this.restrictId = poolname+".";
		}
	}
    
	public DataConnection() {
	    super();
		this.resourcePropertyUrl = Constants._fileDatabasePath;
		java.io.InputStream is = getClass().getResourceAsStream( resourcePropertyUrl );
		try {
		   load(is);
		} catch (Exception e) {
			Monitor.debug(ErrorCodes.DATA_DB_NOTFOUND_FILECONF, e);
		}
	}

    public DataConnection(String configurationUrl) {
		super();
		Object[] messages = new Object[]{};
		this.resourcePropertyUrl = configurationUrl;
		InputStream is = getClass().getResourceAsStream( configurationUrl );
		try {
		   if (is != null) {
		   		load(is);
		   } else {
		   		messages = new Object[]{"Filename = [" + configurationUrl + "]"};
		   		Monitor.log(ErrorCodes.DATA_DB_NOTFOUND_FILECONF,messages);
		   		if (configurationUrl.equals(Constants._fileDatabaseForDirectConnection))
		   			configurationUrl = Constants._fileDatabasePath;
		   		else if (configurationUrl.equals(Constants._fileDatabasePath))
		   			configurationUrl = Constants._fileDatabaseForDirectConnection;
		   		
		   		is = getClass().getResourceAsStream( configurationUrl );
		   		messages = new Object[]{"Trying to load another default database configuration file:",configurationUrl};
		   		Monitor.log(ErrorCodes.CODE_WARNING_PATCH,messages);
		   		if (is != null)
		   			load(is);
		   		else
		   			load(new FileInputStream(configurationUrl));
		   }
		} catch (IOException e) {
			Monitor.debug(ErrorCodes.DATA_DB_NOTFOUND_FILECONF, e);
		}
    }
    
	
	public DataConnection(java.io.File configurationFile) {
		super();
		this.resourcePropertyUrl = configurationFile.getAbsolutePath();
		try {
		   java.io.InputStream is = new java.io.FileInputStream(configurationFile);
		   load(is);
		} catch (Exception e) {
			Monitor.debug(ErrorCodes.DATA_DB_NOTFOUND_FILECONF, e);
		}
	}
	
	public DataConnection(java.io.File configurationFile, String rId) {
		super();
		this.restrictId = rId+"."; 
		this.resourcePropertyUrl = configurationFile.getAbsolutePath();
		try {
		   java.io.InputStream is = new java.io.FileInputStream(configurationFile);
		   load(is);
		} catch (Exception e) {
			Monitor.debug(ErrorCodes.DATA_DB_NOTFOUND_FILECONF, e);
		}
	}
	
}
