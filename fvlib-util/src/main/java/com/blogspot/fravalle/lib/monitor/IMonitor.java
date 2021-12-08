/*
 * IMonitor.java - jappframework (jappframework.jar)
 * Copyright (C) 2005
 * Source file created on 4-dic-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [IMonitor]
 * 2DO:
 *
 */

package com.blogspot.fravalle.lib.monitor;

/**
 * Interfaccia di definizione livelli di notifica errore
 * @author antares
 */
public interface IMonitor {

	/**
	 * Livello di notifica errori applicativi fatali per debug
	 */
	public final static int DEBUG_MORTAL_ERROR = -2;
	/**
	 * Livello di notifica soli errori per debug
	 */
	public final static int DEBUG_ERROR = -1;
	/**
	 * Livello di notifica inesistente per debug
	 */
	public final static int DEBUG_NONE = 0;
	/**
	 * Livello di notifica basso per debug
	 */
	public final static int DEBUG_LIGHT = 1;
	/**
	 * Livello di notifica medio per debug
	 */
	public final static int DEBUG_MEDIUM = 2;
	/**
	 * Livello di notifica alto per debug
	 */
	public final static int DEBUG_HEAVY = 3;
	/**
	 * Livello di notifica storico applicativo
	 */
	public final static int OUTPUT_LOG = 4;
	
}
