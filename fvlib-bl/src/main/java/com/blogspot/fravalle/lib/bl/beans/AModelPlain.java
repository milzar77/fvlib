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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Hashtable;

import com.blogspot.fravalle.lib.monitor.Monitor;

/**
 * Questa classe implementa i metodi principali di introspezione per la gestione di oggetti basati esclusivamente su stringa (plain)
 * 
 * <p><b><u>TODO</u>:</b>
 * @author Francesco Valle - (antares)
 */
abstract public class AModelPlain extends AModel {

	public AModelPlain(){
		super();
		pcsBean = new PropertyChangeSupport(this);
		pcsBean.addPropertyChangeListener(new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent e) {
							updateField( (String)e.getNewValue(), e.getPropertyName());
							/* Richiamo il metodo che imposta le chiavi dell'elemento definita nella implementazione finale  */
							getModelKey();
					}
		});
	}
	
	/**
	 * 
	 * @param fieldValue New value to set for param field
	 * @param fieldName
	 */
	protected void updateField(String fieldValue, String fieldName) {
		Method m;
		try {
				m = (Method)getClass().getMethod("set" + fieldName, new Class[] { String.class });
				m.invoke( this, new Object[] {new String(fieldValue)});
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
		String fieldValue = "";
		try {
			Method m = (Method)getClass().getMethod("get" + fieldName, new Class[] {});
			fieldValue = (String)m.invoke( this, new Object[] {});
			if (fieldValue != null) {
				if (fieldValue.startsWith("java.lang.Object@"))
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
	public void beanFiller(Hashtable paramMap) {
	    for (Enumeration e = paramMap.keys(); e.hasMoreElements();) {
	        String ky = (String)e.nextElement();

			String vl = ""+paramMap.get(ky);

			updateField(vl, ky);
			
	    }
	}
		
}
