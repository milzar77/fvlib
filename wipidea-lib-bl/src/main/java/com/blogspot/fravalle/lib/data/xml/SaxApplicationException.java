/*
 * SaxApplicationException.java - jweevlib (jweevlib.jar)
 * Copyright (C) 2005
 * Source file created on 20-mar-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [SaxApplicationException]
 * 2DO:
 *
 */

package com.blogspot.fravalle.lib.data.xml;


import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.blogspot.fravalle.lib.monitor.Monitor;
import com.blogspot.fravalle.util.Constants;


/**
 * @author admin
 */
public class SaxApplicationException extends Exception {
	/**
	 *  
	 */
	public SaxApplicationException() {
		super();
		// 2DO Auto-generated constructor stub
	}

	/**
	 * @param parArg0
	 */
	public SaxApplicationException(String parArg0) {
		super(parArg0);
		// 2DO Auto-generated constructor stub
	}

	/**
	 * @param parArg0
	 */
	public SaxApplicationException(Throwable t) {
		super(t);
		errorInterceptor(t, t.getClass().getName());
	}

	protected void errorInterceptor(Object o, String exceptionLabel) {
		if (o instanceof SAXParseException) {
			SAXParseException spe = (SAXParseException)o;
			Monitor.log( Constants._logDefaultDef +"\n** Parsing error"
					+ ", line " + spe.getLineNumber() + ", uri "
					+ spe.getSystemId());
		} else if (o instanceof SAXException) {
			SAXException sxe = (SAXException)o;
		} else if (o instanceof ParserConfigurationException) {
			ParserConfigurationException pce = (ParserConfigurationException)o;
		} else if (o instanceof IOException) {
			IOException ioe = (IOException)o;
		}
		Exception e = (Exception)o;
		Monitor.debug(e);
		errorReport(e, exceptionLabel);
		return;
	}
	
	protected void errorReport(Exception e, String exceptionLabel) {
		Monitor.log( Constants._logDefaultDef +"[Report Eccezione] " + exceptionLabel );
		Monitor.log( Constants._logDefaultDef +"...connecting to local error database...");
		Monitor.log( Constants._logDefaultDef +"[Messaggio]");
		Monitor.log( Constants._logDefaultDef +e.getMessage());
		Monitor.log( Constants._logDefaultDef +"[Sorgente]");
		Monitor.log( Constants._logDefaultDef +e.getCause().getMessage());
		// connessione a db locale HSQLDB
		}
}