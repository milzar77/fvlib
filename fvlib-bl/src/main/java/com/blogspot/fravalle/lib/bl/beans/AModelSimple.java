/*
 * Model.java - FrameWork (FrameWork.jar)
 * Copyright (C) 2005
 * Source file created on 3-dic-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [Model]
 * TODO:
 *
 */

package com.blogspot.fravalle.lib.bl.beans;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.blogspot.fravalle.lib.monitor.Monitor;

/**
 * 
 * Gestione e lettura di qualsiasi tipo di oggetto
 * 
 * @author antares
 */
abstract public class AModelSimple extends AModel {

	public AModelSimple(){
		super();
	}
	
	/**
	 * 
	 * @param fieldValue New value to set for param field
	 * @param fieldName
	 */
	protected void updateField(Object fieldValue, String fieldName) {
		Method m;
		try {
			m = (Method)getClass().getMethod("set" + fieldName, new Class[] { Object.class });
			m.invoke( this, new Object[] {fieldValue});
		} catch (NoSuchMethodException ex) {
			Monitor.debug(Monitor.DEBUG_ERROR, "Metodo del bean inesistente: " + fieldName +" -- "+ ex.getMessage() );
		} catch ( IllegalArgumentException ex ) {
			Monitor.debug(Monitor.DEBUG_ERROR, "Argomento illegale del metodo: " + fieldName +" -- "+ ex.getMessage() );
		} catch ( IllegalAccessException ex ) {
			Monitor.debug(Monitor.DEBUG_ERROR, "Accesso illegale al metodo: " + fieldName +" -- "+ ex.getMessage() );
		} catch ( InvocationTargetException ex ) {
			Monitor.debug(Monitor.DEBUG_ERROR, "Errore di invocazione metodo: " + fieldName +" -- "+ ex.getMessage() );
		}

	}
	
	/**
	 * 
	 * @param fieldName Param field name to get
	 */
	public Object getFieldValue(String fieldName) {
	
		Object fieldValue = new Object();
		
		try {
			Method m = (Method)getClass().getMethod("get" + fieldName, new Class[] {});
			fieldValue = (Object)m.invoke( this, new Object[] {});
		} catch (NoSuchMethodException ex) {
			Monitor.debug(Monitor.DEBUG_ERROR, "Metodo del bean inesistente: " + fieldName +" -- "+ ex.getMessage() );
		} catch ( IllegalArgumentException ex ) {
			Monitor.debug(Monitor.DEBUG_ERROR, "Argomento illegale del metodo: " + fieldName +" -- "+ ex.getMessage() );
		} catch ( IllegalAccessException ex ) {
			Monitor.debug(Monitor.DEBUG_ERROR, "Accesso illegale al metodo: " + fieldName +" -- "+ ex.getMessage() );
		} catch ( InvocationTargetException ex ) {
			Monitor.debug(Monitor.DEBUG_ERROR, "Errore di invocazione metodo: " + fieldName +" -- "+ ex.getMessage() );
		}

		return 	fieldValue;

	}
	

	
	/**
	 * @param paramMap Param field names map for filling this bean
	 */
	public void beanFiller(java.util.Hashtable paramMap) {
	    for (java.util.Enumeration e = paramMap.keys(); e.hasMoreElements();) {
	        String ky = (String)e.nextElement();

			String vl = ""+paramMap.get(ky);

			updateField(vl, ky);
			
	    }
	}
		
}
