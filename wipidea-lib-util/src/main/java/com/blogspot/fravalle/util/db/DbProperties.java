/*
 * SQLUtils.java - Weev Utility Library package (weev-lib.jar)
 * Copyright (C) 2 novembre 2002 Francesco Valle <info@weev.it>
 * http://www.weev.it
 *
 */

package com.blogspot.fravalle.util.db;

import java.util.logging.Logger;

final public class DbProperties extends java.util.Properties {

    static private final Logger logger = Logger.getAnonymousLogger();
    
    private String resourcePropertyUrl;
	private String restrictId = "";
    
	public String getDriver() {
	    String s = "";
		try {
		    s = getProperty(restrictId + "driver");
		} catch (Exception e) {
                    logger.fine( "Impossibile caricare il <driver> di connessione database dal file di configurazione '" + this.resourcePropertyUrl + "'.\r\nERROR CAUSE:\t" + e.getMessage());
		}
	    return s;
	}
	public String getUrl() {
		String s = "";
		try {
			s = getProperty(restrictId + "url");
		} catch (Exception e) {
                    logger.fine( "Impossibile caricare l'<url> di connessione database dal file di configurazione '" + this.resourcePropertyUrl + "'.\r\nERROR CAUSE:\t" + e.getMessage());
		}
		return s;
	}
	public String getUser() {
		String s = "";
		try {
			s = getProperty(restrictId + "user");
		} catch (Exception e) {
                    logger.fine( "Impossibile caricare la credenziale <utente> database dal file di configurazione '" + this.resourcePropertyUrl + "'.\r\nERROR CAUSE:\t" + e.getMessage());
		}
		return s;
	}
	public String getPassword() {
		String s = "";
		try {
			s = getProperty(restrictId + "password");
		} catch (Exception e) {
                    logger.fine( "Impossibile caricare la credenziale <password> database dal file di configurazione '" + this.resourcePropertyUrl + "'.\r\nERROR CAUSE:\t" + e.getMessage());
		}
		return s;
	}

	public java.util.Properties getCredentials() {
		java.util.Properties p = new java.util.Properties();
		try {
			p.setProperty("user",getUser());
			p.setProperty("password",getPassword());
		} catch (Exception e) {
                    logger.fine( "Impossibile caricare le credenziali utente database dal file di configurazione '" + this.resourcePropertyUrl + "'.\r\nERROR CAUSE:\t" + e.getMessage());
		}
		return p;
	}
	
	public java.util.Properties getDefaultParams() {
		java.util.Properties p = new java.util.Properties();
		try {
			p.setProperty("jdbcDriver",getDriver());
			p.setProperty("jdbcUrl",getUrl());
		} catch (Exception e) {
                    logger.fine( "Impossibile caricare i parametri di connessione database dal file di configurazione'" + this.resourcePropertyUrl + "'.\r\nERROR CAUSE:\t" + e.getMessage());
		}
		return p;
	}
	
	public DbProperties() {
	    super();
		this.resourcePropertyUrl = "/dbPooling.properties";
		java.io.InputStream is = getClass().getResourceAsStream( resourcePropertyUrl );
		try {
		   load(is);
		} catch (Exception e) {
                    logger.fine( "Impossibile caricare il file di configurazione predefinito '" + this.resourcePropertyUrl + "'");
		   // e.printStackTrace();
		}
	}

	public DbProperties(java.io.File configurationFile) {
		super();
		this.resourcePropertyUrl = configurationFile.getAbsolutePath();
		try {
		   java.io.InputStream is = new java.io.FileInputStream(configurationFile);
		   load(is);
		} catch (Exception e) {
                    logger.fine( "Impossibile caricare il file di configurazione '" + this.resourcePropertyUrl + "'");
		   // e.printStackTrace();
		}
	}
	
	public DbProperties(java.io.File configurationFile, String restrictId) {
		super();
		this.restrictId = restrictId; 
		this.resourcePropertyUrl = configurationFile.getAbsolutePath();
		try {
		   java.io.InputStream is = new java.io.FileInputStream(configurationFile);
		   load(is);
		} catch (Exception e) {
                    logger.fine( "Impossibile caricare il file di configurazione '" + this.resourcePropertyUrl + "'");
		   // e.printStackTrace();
		}
	}
	
    public DbProperties(String configurationUrl) {
		super();
		this.resourcePropertyUrl = configurationUrl;
		java.io.InputStream is = getClass().getResourceAsStream( configurationUrl );
		try {
		   load(is);
		} catch (Exception e) {
                    logger.fine( "Impossibile caricare il file di configurazione '" + this.resourcePropertyUrl + "'");
            // e.printStackTrace();
		}
    }

}
