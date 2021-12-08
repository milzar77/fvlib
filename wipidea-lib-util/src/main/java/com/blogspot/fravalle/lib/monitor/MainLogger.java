/*
 * MainLogger.java - jappframework (jappframework.jar)
 * Copyright (C) 2006
 * Source file created on 8-feb-2006
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [MainLogger]
 * 2DO:
 *
 */

package com.blogspot.fravalle.lib.monitor;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author francesco
 */
abstract public class MainLogger {
	
	static boolean isNew = true;
	static public boolean isReset = false;
	
	static Level level = Level.OFF;
	
	public static final void setLevel(Level l) {
		level = l;
		isReset = true;
		System.out.println("Setting default logging level to: " + level.getLocalizedName());
	}
	
	/**
	 * 
	 */
	public static final Logger getLog() {
		if (isNew || isReset) {
			Logger.getLogger("global").setLevel( level );
			isNew = false;
			isReset = false;
		}
		return Logger.getLogger("global");
	}
	
	public static final void genericoInfo(String s) {
		getLog().info(s);
	}
	
	public static final void genericWarn(String s) {
		getLog().warning(s);
	}
	
}
