/*
 * AResource.java - jappframework (jappframework.jar)
 * Copyright (C) 2005
 * Source file created on 10-dic-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [AResource]
 * 2DO:
 *
 */

package com.blogspot.fravalle.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import com.blogspot.fravalle.lib.monitor.IErrorCodes;
import com.blogspot.fravalle.lib.monitor.Monitor;

/**
 * @author antares
 */
public abstract class AResource implements IErrorCodes {
	
	protected final static String STR_BLANK="";
	protected final static String STR_NULL=null;
	public static final String ALL_PROPERTIES = "TUTTE LE PROPS";
	
	protected final static String FLOW_LOGGING_DEFAULT=Constants._logDefaultTag;
	protected final static String FLOW_LOGGING_HISTORY=Constants._logHistoryTag;
	protected final static String FLOW_LOGGING_WARN=Constants._logWarnTag;
	protected final static String FLOW_LOGGING_EVENT=Constants._logEventTag;
	protected final static String PREFIX_LOG_INTERNAL_APPEND=Constants._logInternalTag;
	
	protected final static String PREFIX_LOG_DEFAULT_APPEND=Constants._logDefaultTag;
	protected final static String PREFIX_LOG_HISTORY_APPEND=Constants._logHistoryTag;
	protected final static String PREFIX_LOG_WARN_APPEND=Constants._logWarnTag;
	protected final static String PREFIX_LOG_EVENT_APPEND=Constants._logEventTag;
	
	/**
	 * Metodo utilizzato per accedere alle proprietà localizzate dalla
	 * classe UIRes
	 * @param refKey
	 * @param key
	 * @return
	 */
	public static String findKeyReference (String refKey, String key) {
		String value = null;
		String oldValue = refKey;
		if (refKey.startsWith("$")) {
			refKey = refKey.substring(1);
			
			if (refKey.startsWith("{") && refKey.endsWith("}")) {
				refKey = refKey.substring(1,refKey.length()-1);
			
				if (refKey.startsWith("$")) {
					/* Implementare raccolta variabli in array */
					String newValue = UIRes.getLabel( refKey.substring(1) );
					if (newValue==null || newValue.equals(String.valueOf(STR_NULL))) {
						Monitor.log(IErrorCodes.PROPS_KEY_REFVAR_NOTFOUND, new Object[]{refKey, key});
						newValue = "VARIABLE NOT FOUND!";
					}
					value = newValue;
				} else {/* altri casi annidati in parentesi graffe */
					String newValue = UIRes.getLabel( refKey );
					if (newValue==null || newValue.equals(String.valueOf(STR_NULL))) {
						Monitor.log(IErrorCodes.PROPS_KEY_REFVAR_NOTFOUND, new Object[]{refKey, key});
						newValue = "VARIABLE NOT FOUND!";
					}
					value = newValue;
				}
			} else {
					/* Implementare raccolta variabli in array */
					String newValue = SettingRes.get( refKey );
					if (newValue==null || newValue.equals(String.valueOf(STR_NULL))) {
						Monitor.log(IErrorCodes.PROPS_KEY_REFVAR_NOTFOUND, new Object[]{refKey, key});
						newValue = "VARIABLE NOT FOUND!";
					}
					value = newValue;
			}			
		} else {
			value = oldValue;
		}
		return value;
	}
	
	public static File getObjectResource(String uri) {
		String sFile = Runtime.getRuntime().getClass().getResource(uri).getFile();
		return new File(sFile);
	}
	public static FileInputStream getObjectResourceAsStream(String uri) {
		return (FileInputStream)Runtime.getRuntime().getClass().getResourceAsStream(uri);
	}	
	
	
	protected static final String ALL_PROPERTY = "TUTTE LE PROPS";

	static final public Properties getAllProps() {
		return getAllProps("res.application",null);
	}
	static final public Properties getAllProps(String bundleName, Map addingProps) {
		ResourceBundle optionalResources = null;
		try {
			optionalResources = ResourceBundle.getBundle(bundleName, Locale.ITALY);
		} catch (Exception e) {
			Monitor.debug(e);
		}
		Properties newProps = getProps(optionalResources);
		if (addingProps!=null)
			newProps.putAll(addingProps);
		return newProps;
	}
	
	protected final static Properties getProps(ResourceBundle resources) {
		Properties props = new Properties();
		try {
			Enumeration e = resources.getKeys();
			while (e.hasMoreElements()) {
				String key = (String)e.nextElement();
				props.setProperty( key , resources.getString(key) );
			}
		} catch (MissingResourceException mre) {
			Monitor.debug(mre.getMessage());
		}
		return props;
	}
	

	
	
}
