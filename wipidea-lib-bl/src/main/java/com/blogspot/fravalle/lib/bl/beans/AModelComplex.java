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
 * Gestione comprensiva degli oggetti primitivi, specifiche speciali per beans
 * TODO:nei bean skeleton implementare oggetti di tipo definito	
		public void int_area(int l){_area=l;}
		public int int_area(){return l;}
		public void long_area(){}
		public long long_area(long l){return l;}
 *		
 *		
 * @author antares
 */
abstract public class AModelComplex extends AModel {

	public AModelComplex(){
		super();
	}
	
	/**
	 * 
	 * @param fieldValue New value to set for param field
	 * @param fieldName
	 */
	protected void updateField(String fieldValue, String fieldName) {
		boolean isNumber = false;
		/* try {
			int intValue = Integer.parseInt(fieldValue);
			isNumber = true;
			Monitor.debug("<reflection>[DataType: int]");
		} catch (NumberFormatException e) {
			Monitor.debug("<reflection>[DataType: str]");
		} */
		Method m;
		try {
			
			if (isNumber) {
				m = (Method)getClass().getMethod("int" + fieldName, new Class[] { Object.class });
				Monitor.debug("<reflection>intSetter</reflection>");
				m.invoke( this, new Object[] {fieldValue});
				Monitor.debug("<reflection>int</reflection>");
			}else{
				m = (Method)getClass().getMethod("set" + fieldName, new Class[] { String.class });
				m.invoke( this, new Object[] {new String(fieldValue)});
			}
			
			
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
	 * @param fieldName Param field name to get
	 */
	public Object getFieldValue(String fieldName) {
	
		Object fieldValue = "";
		
		try {
			Method m = (Method)getClass().getMethod("int" + fieldName, new Class[] {});
			fieldValue = (Object)m.invoke( this, new Object[] {});
			if (fieldValue != null) {
				if (new String(fieldValue.toString()).startsWith("java.lang.Object@"))
				    fieldValue = "";
			}
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
