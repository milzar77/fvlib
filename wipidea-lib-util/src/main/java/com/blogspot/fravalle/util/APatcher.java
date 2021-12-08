/*
 * Patcher.java - jappframework (jappframework.jar)
 * Copyright (C) 2006
 * Source file created on 7-feb-2006
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [Patcher]
 * 2DO:
 *
 */

package com.blogspot.fravalle.util;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.logging.Logger;

/**
 * @author francesco
 */
abstract public class APatcher {
	
	static final private String notFound = "Key ["; 
	
	static private ResourceBundle rb = ResourceBundle.getBundle("res.patches.local", Locale.getDefault());
	
	// abstract void patchInfo(long cod);
	static private Logger localLogger = Logger.getLogger("util-log");
	
	static protected String patch(long id, String type) {
		String s = null;
		int max = 1000;
		int maxCodeLength = 4;
		String keyCode = "";
		if (id >= max) {
			return "DEBUG: Out of params limit";
		} else {
			String testVal = String.valueOf(id);
			if (testVal.length() == 1)
				keyCode = "000" + testVal;
			else if (testVal.length() == 2)
				keyCode = "00" + testVal;
			else if (testVal.length() == 3)
				keyCode = "0" + testVal;
			else if (testVal.length() == 4)
				keyCode = "" + testVal;
		}
		
		try {
			s = rb.getString( "patch." + keyCode + type );
		} catch (MissingResourceException mre) {
			s = notFound + id + "] with code " + keyCode + " not found. Add it to your /res/patches/local.properties";
			localLogger.severe( s );
		}
		return s;
	}
	
	protected static String getLabel(long id) {
		return patch(id, ".label");
	}
	public static String getDescription(long id) {
		return patch(id, ".desc");
	}
	
	protected static String get(long id) {
		return patch(id, "");
	}
	
	public static boolean isTarget(long id) {
		String target = patch(id, ".target");
		boolean isPatched = false;
		StringTokenizer st = new StringTokenizer(target,";");
		int directiveCounter = st.countTokens();
		while (st.hasMoreTokens()) {
			String directive = (String)st.nextToken();
			String name = directive.substring(0,directive.indexOf(":"));
			String values = directive.substring(directive.indexOf(":")+1);
			if ( isPatched(name, values) )
				directiveCounter--;
		}
		if (directiveCounter == 0) {
			localLogger.warning( getDescription(id) );
			return true;
		} else
			return false;
	}
	
	protected static boolean isPatched(String directive, String patterns) {
		boolean isPatched = false;
		StringTokenizer st1 = new StringTokenizer(patterns,"[");
		while (st1.hasMoreTokens()) {
			String testPattern = (String)st1.nextToken();
			testPattern = testPattern.substring(0,testPattern.indexOf("]"));
			isPatched = System.getProperty(directive).toLowerCase().indexOf( testPattern.toLowerCase() ) != -1;
			if (isPatched) {
				localLogger.fine("Condition ["+directive+"] patch found: ["+System.getProperty(directive)+"]");
				break;
			}
		}
		return isPatched;
	}
	
}
