/*
 * UIRes.java - jdbbrowser (jdbbrowser.jar)
 * Copyright (C) 2003
 * Source file created on 26-ott-2003
 * Author: Francesco Valle <info@weev.it>
 * http://www.weev.it
 *
 */

package com.blogspot.fravalle.util;

import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;



public abstract class SettingRes extends AResource {
	
	static Locale locale;
	public static Properties opzioni;
	public static ResourceBundle queryres;
	
	public static String jdbcDriver;
	public static String jdbcUrl;
	public static String jdbcUsr;
	public static String jdbcPwd;
	public static String jdbcPool;

	
	private static String netConnection;

	
	
	public static void setNetConnection(String status) {
		netConnection = status;
	}

	public static String getNetConnection() {
		if (netConnection == null)
			return "offline";
		else
			return netConnection;
	}
	
	public static String getQuery(String key) {
		if (queryres == null) {
			prepareQueryRes();
		}
		return queryres.getString(key);
	}
	
	private final static String PREFIX_LOG_INTERNAL="internal.";
	private final static String PREFIX_LOG_DEFAULT="log.";
	private final static String PREFIX_LOG_HISTORY="history.";
	private final static String PREFIX_LOG_WARN="warning.";
	private final static String PREFIX_LOG_EVENT="event.";

	private static String clear(String value, String defaultValue) {
		value = defaultValue;
		return value;
	}
	private static String logFlowDefault(String s) {
		return PREFIX_LOG_DEFAULT_APPEND;
	}
	private static String logFlowHistory(String s) {
		return PREFIX_LOG_HISTORY_APPEND;
	}
	private static String logFlowWarn(String s) {
		return PREFIX_LOG_WARN_APPEND;
	}
	private static String logFlowEvent(String s) {
		return PREFIX_LOG_EVENT_APPEND;
	}
	private static String logFlowInternal(String s) {
		return PREFIX_LOG_INTERNAL_APPEND;
	}
	private static String logFlow(String key, String value) {
		value = clear(value, STR_BLANK);
		if (key.startsWith(PREFIX_LOG_DEFAULT))
			return logFlowDefault(value);
		else if (key.startsWith(PREFIX_LOG_HISTORY))
			return logFlowHistory(value);
		else if (key.startsWith(PREFIX_LOG_WARN))
			return logFlowWarn(value);
		else if (key.startsWith(PREFIX_LOG_EVENT))
			return logFlowEvent(value);
		else if (key.startsWith(PREFIX_LOG_INTERNAL))
			return logFlowInternal(value);
		return value;
	}
	
	final public static void add(Properties keys) {
		if (opzioni!=null)
			opzioni.putAll(keys);
		else
			System.out.println("Impossibile aggiungere le opzioni all'istanza di configurazione principale");
	}
	
	final public static String get(String key) {
		
		String value = new String("NOT FOUND");

		if (opzioni == null) {
			prepareLangRes("");
		}
		
		try {
			value = opzioni.getProperty(key);
			//TODO: controllare validità
			/*
			value = logFlow(key, value);
			value += opzioni.getProperty(key);
			*/
		} catch (MissingResourceException mre) {
			Logger.getAnonymousLogger().severe(
			        key + " key not found!"
			        +"\r\n"+mre.getMessage());
		}
		
		/* TODO: Verificare precedente implementazione senza IF e blocco logLabel fino a return */
		if (value!=null)
			value = findKeyReference(value, key);
		
		String logLabel = logFlow(key, value);
		if (!"".equals(logLabel) && value != null)
			value = logLabel + value;
		if (value==null)
			return KEY_NOTFOUND;
		return value;
	}


	public static Vector getLabelGroup(String keyFilter) {
		Vector vt = new Vector();
		if (opzioni == null) {
			prepareLangRes("");
		}
		try {
			Enumeration e = opzioni.keys();
			while (e.hasMoreElements()) {
				String key = (String)e.nextElement();
				if (key.startsWith(keyFilter) || keyFilter.equals(ALL_PROPERTY))
					vt.addElement( opzioni.getProperty(key) );
			}
		} catch (MissingResourceException mre) {
			Logger.getAnonymousLogger().severe(mre.getMessage());
		}
		return vt;
	}
	
	
	public static void setLang(String lang) {
		prepareLangRes(lang);
	}
	
	private static void prepareLangRes(String lang) {
		
		locale=null;
		if (lang.equals("it")) {
		    locale=Locale.ITALIAN;
		} else if (lang.equals("us")) {
			locale=Locale.US;
		} else if (lang.equals("uk")) {
			locale=Locale.ENGLISH;
		} else if (lang.equals("fr")) {
			locale=Locale.FRENCH;
		} else {
			locale=Locale.ITALIAN;
		}
		
		opzioni = new Properties();
		
		opzioni.setProperty("internal.ui.notfound", "Attenzione, file di localizzazione res/application.properties non trovato! Risorse testuali caricate da file predefinito");
		opzioni.setProperty("internal.ui.override", "Attenzione, il file di localizzazione res/application.properties consente l'override delle impostazioni predefinite dal file res/settings.properties");
		
		ResourceBundle languageResources = ResourceBundle.getBundle("res.settings", locale);
		ResourceBundle optionalResources = null;
		try {
			optionalResources = ResourceBundle.getBundle("res.application", locale);
		} catch (Exception e) {
			Logger.getAnonymousLogger().severe(e.getMessage());
			Logger.getAnonymousLogger().severe( get("internal.ui.notfound") );
		}

		opzioni.putAll( getProps(languageResources) );
		if (optionalResources != null) {
			opzioni.putAll( getProps(optionalResources) );
			Logger.getAnonymousLogger().warning( get("internal.ui.override") );
		}		
	}
	
	private static void prepareQueryRes() {
		
		queryres = ResourceBundle.getBundle("res.gui.guiquery");
		
		
		
		jdbcDriver 	= queryres.getString( getNetConnection() + ".driver" );
		jdbcUrl 	= queryres.getString( getNetConnection() + ".url" );
		
		jdbcUsr 	= queryres.getString( getNetConnection() + ".usr" );
		jdbcPwd 	= queryres.getString( getNetConnection() + ".pwd" );
		jdbcPool 	= queryres.getString( getNetConnection() + ".pool" );
		
	}
	
	
	static private boolean isParseTest = false;
	static String excludedKeys = "";
	
	static boolean isExcluded(String key) {
		StringTokenizer st = new StringTokenizer(key,".");
		boolean status = false;
		while (st.hasMoreTokens()){
			String token = st.nextToken();
			status = excludedKeys.indexOf(token) != -1;
			if (status)
				break;
		}
		return status;
	}
	
	private static boolean testKey(String key, String keyFilter) {
		if (isParseTest)
			return !isExcluded(key)
				&& (key.startsWith(keyFilter)
						|| key.indexOf(keyFilter) != -1
						|| keyFilter.equals(ALL_PROPERTIES)
				);
		else
			return !isExcluded(key)
				&& (key.startsWith(keyFilter)
						|| keyFilter.equals(ALL_PROPERTIES)
				);
	}
	
	public static FilteredProperties getPropertiesGroup(String keyFilter, boolean isParse, String parExcluded) {
		isParseTest = isParse;
		excludedKeys = parExcluded; 
		return getPropertiesGroup(keyFilter);
	}
	
	public static FilteredProperties getPropertiesGroup(String keyFilter, boolean isParse) {
		isParseTest = isParse;
		return getPropertiesGroup(keyFilter);
	}
	
	public static FilteredProperties getPropertiesGroup(String keyFilter) {
		FilteredProperties props = new FilteredProperties();
		if (opzioni == null) {
			prepareLangRes("");
		}
		try {
			Enumeration e = opzioni.keys();
			while (e.hasMoreElements()) {
				String key = (String)e.nextElement();
				if (testKey(key, keyFilter))
					props.setProperty( key, (String)get(key) );
			}
		} catch (MissingResourceException mre) {
			Logger.getAnonymousLogger().severe(mre.getMessage());
		}
		
		/* Proprietà del filtro, crea problemi con metodi che non distinguono le chiavi */
		String messageDescription = "Gruppo di proprietà filtrato da proprietà principali";
		props.setDescritpion(messageDescription);
		props.setFilter(keyFilter);
		props.setPropertySource(opzioni);
		return props;
	}
	
	static final public Level getLogLevel() {
		if ( "OFF".equals(get("logging.level")) )
			return Level.OFF;
		if ( "ALL".equals(get("logging.level")) ) 
			return Level.ALL;
		if ( "CONFIG".equals(get("logging.level")) ) 
			return Level.CONFIG;
		if ( "FINE".equals(get("logging.level")) ) 
			return Level.FINE;
		if ( "FINER".equals(get("logging.level")) ) 
			return Level.FINER;
		if ( "FINEST".equals(get("logging.level")) ) 
			return Level.FINEST;
		if ( "INFO".equals(get("logging.level")) ) 
			return Level.INFO;
		if ( "SEVERE".equals(get("logging.level")) ) 
			return Level.SEVERE;
		if ( "WARNING".equals(get("logging.level")) ) 
			return Level.WARNING;
		
		return Level.OFF;
	}
	
}


