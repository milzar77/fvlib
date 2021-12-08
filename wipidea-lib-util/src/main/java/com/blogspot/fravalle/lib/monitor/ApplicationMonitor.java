/*
 * ApplicationMonitor.java - jappframework (jappframework.jar)
 * Copyright (C) 2005
 * Source file created on 31-dic-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [ApplicationMonitor]
 * 2DO:
 *
 */

package com.blogspot.fravalle.lib.monitor;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * [][ApplicationMonitor]
 * 
 * <p><b><u>2DO</u>:</b>
 * 
 * 
 * @author Francesco Valle - (antares)
 */
public final class ApplicationMonitor extends Logger {

	private static ApplicationMonitor instance;
	
	
	private ApplicationMonitor() {
		super( ApplicationMonitor.class.getClass().getName() , null);
		LogManager manager = LogManager.getLogManager();
		manager.addLogger(this);
		global("New logger created: <" + getName() + ">, hascode: " + hashCode());
	}
	
	private ApplicationMonitor(Object reference) {
		super( reference.getClass().getName() , null);
		LogManager manager = LogManager.getLogManager();
		manager.addLogger(this);
		global("New logger created: <" + getName() + "> from [" + reference.getClass().getName() + "] hascode: " + hashCode() );
	}

	public static ApplicationMonitor getInstance() {
		if (instance == null)
			instance = new ApplicationMonitor();
		// instance.info( "hascode: " + instance.hashCode());
		return instance;
	}
	public static ApplicationMonitor getInstance(Object reference) {
		if (instance == null)
			instance = new ApplicationMonitor(reference);
		// instance.info( "hascode: " + instance.hashCode());
		return instance;
	}
	
	
	public void global(String message) {
		global.info(message);
	}
	
	public void history(Object ref, String message) {
		instance.logp(Level.INFO, "History log for: <" + ref.getClass().getName() + ">", null, message);
	}	
	
	public void history(String message) {
		instance.log(Level.INFO, message);
	}
	
	/**
	 * Metodo di scrittura debug basato su codice errore gestito dalla classe ErrorCodes tramite il file
	 * di proprietà /res/errors.properties, livello di notifica alto
	 * @param errorCode
	 * codice di errore
	 * @see ErrorCodes
	 */
	public void history(long errorCode) {
		instance.log(Level.SEVERE, ErrorCodes.error(errorCode));
	}
	
	/**
	 * Metodo di scrittura log basato su codice errore gestito dalla classe ErrorCodes tramite il file
	 * di proprietà /res/errors.properties
	 * @param errorCode
	 * codice di errore
	 * @param objects
	 * oggetti da tracciare
	 * @see ErrorCodes
	 */
	public void history(long errorCode, Object[] objects) {
		String message = ErrorCodes.error(errorCode);
		for (int i =0; i < objects.length; i++) {
			message += "\r\n";
			if (objects[i]!=null)
				message += objects[i].getClass().getName() + " : " + String.valueOf(objects[i]);
			else
				message += "\t" + String.valueOf(objects[i]);
		}
		history(message);
	}
	
	public void trace(Object ref, String message) {
		logp(Level.SEVERE, ref.getClass().getName(), null, message);
	}	

	/* Metodi trasferiti da Monitor */
	
	/**
	 * Metodo di tracciamento eccezione
	 * @param e
	 * eccezione tracciata
	 */
	public void debug(Exception e) {
		e.printStackTrace();
	}
	
	/**
	 * Metodo di scrittura debug basato su codice errore gestito dalla classe ErrorCodes tramite il file
	 * di proprietà /res/errors.properties, livello di notifica alto
	 * @param errorCode
	 * codice di errore
	 * @param e
	 * eccezione tracciata
	 * @see ErrorCodes
	 */
	public void debug(long errorCode, Exception e) {
		instance.log(Level.SEVERE, ErrorCodes.error(errorCode), e);
	}
	
}