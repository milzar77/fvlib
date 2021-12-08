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
import java.util.Vector;
import java.util.logging.Logger;



public abstract class UIRes extends AResource {
	
	static final protected Logger logger = Logger.getLogger(UIRes.class.getName());
	
	public static Properties langres;
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

	public static int getInt(String key) {
		int value = 0;
		return value;
	}
	
	public static String getLabel(String key) {
		
		String value = new String("NOT FOUND");
		
		if (langres == null) {
			//prepareLangRes("it");
			prepareLangRes("");
		}
		
		try {
			value = logFlow(key, value);
			value += langres.getProperty(key);
		} catch (MissingResourceException mre) {
			logger.info(
			        key + " key not found!"
			        +"\r\n"+mre.getMessage());
		}
		
		value = findKeyReference(value, key);
		
		if (value!=null && !value.equals("null") && !value.equals(""))
			return value;
		else
			return "<html><code><strike>[" + key + "] not found</strike></code></html>";

	}

	public static FilteredProperties getPropertiesGroup(String keyFilter) {
		FilteredProperties props = new FilteredProperties();
		if (langres == null) {
			prepareLangRes("");
		}
		try {
			Enumeration e = langres.keys();
			while (e.hasMoreElements()) {
				String key = (String)e.nextElement();
				if (key.startsWith(keyFilter) || keyFilter.equals(ALL_PROPERTIES))
					props.setProperty( key, langres.getProperty(key) );
			}
		} catch (MissingResourceException mre) {
			logger.severe(mre.getMessage());
		}
		
		/* Proprietà del filtro, crea problemi con metodi che non distinguono le chiavi */
		String messageDescription = "Gruppo di proprietà filtrato da proprietà principali";
		props.setDescritpion(messageDescription);
		props.setFilter(keyFilter);
		props.setPropertySource(langres);
		return props;
	}
	
	public static Vector getLabelGroup(String keyFilter) {
		Vector vt = new Vector();
		if (langres == null) {
			prepareLangRes("");
		}
		try {
			Enumeration e = langres.keys();
			while (e.hasMoreElements()) {
				String key = (String)e.nextElement();
				if (key.startsWith(keyFilter) || keyFilter.equals(ALL_PROPERTIES))
					vt.addElement( langres.getProperty(key) );
			}
		} catch (MissingResourceException mre) {
			logger.severe(mre.getMessage());
		}
		return vt;
	}
	
	
	public static void setLang(String lang) {
		prepareLangRes(lang);
	}
	
	private static void prepareLangRes(String lang) {
		
		Locale locale=null;
		if (lang.equals("it")) {
		    locale=Locale.ITALIAN;
		} else if (lang.equals("us")) {
			locale=Locale.US;
		} else if (lang.equals("uk")) {
			locale=Locale.ENGLISH;
		} else if (lang.equals("fr")) {
			locale=Locale.FRENCH;
		} else if (lang.equals("veneto")) {
			locale=Locale.CHINESE;
		} else {
			locale = new Locale(Locale.ENGLISH.getLanguage(), Locale.UK.getCountry());
			//locale=Locale.ITALIAN;
		}
		
		langres = new Properties();
		
		if (locale!=null)
			logger.fine("Locale: " + locale.getLanguage() +" , "+ locale.getCountry());
		else
			logger.fine("Locale: " + locale);
		
		langres.setProperty("internal.ui.notfound", "Attenzione, file di localizzazione res/ui/messages.properties non trovato! Risorse testuali caricate da file predefinito");
		langres.setProperty("internal.ui.override", "Attenzione, il file di localizzazione res/ui/messages.properties consente l'override delle impostazioni predefinite dal file res/ui/defaultMessages.properties");
		
		ResourceBundle languageResources;
		if (locale!=null)
			languageResources = ResourceBundle.getBundle("res.ui.defaultMessages", locale);
		else
			languageResources = ResourceBundle.getBundle("res.ui.defaultMessages");
		
		ResourceBundle optionalResources = null;
		try {
			if (locale!=null)
				optionalResources = ResourceBundle.getBundle("res.ui.messages", locale);
			else
				optionalResources = ResourceBundle.getBundle("res.ui.messages");
		} catch (Exception e) {
			logger.severe(e.getMessage());
			logger.warning( langres.getProperty("internal.ui.notfound") );
		}

		langres.putAll( getProps(languageResources) );
		if (optionalResources != null) {
			langres.putAll( getProps(optionalResources) );
			logger.warning( langres.getProperty("internal.ui.override") );
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
	

}


