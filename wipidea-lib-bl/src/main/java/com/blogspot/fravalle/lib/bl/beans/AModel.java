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
import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import com.blogspot.fravalle.lib.bl.ModelKey;
import com.blogspot.fravalle.lib.monitor.Monitor;

/**
 * @author antares
 */
public abstract class AModel implements IModel {
	
	/**
	 * 
	 * Per eseguire l'override di questo metodo richiamare nel metodo di
	 * override della classe di implementazione finale il seguente metodo:
	 * <p><code>super.updateField((Object)fieldValue,fieldName);</code>
	 * 
	 * @param fieldValue New value to set for param field
	 * @param fieldName
	 */
	abstract protected void updateField(String fieldValue, String fieldName);
	
	/**
	 * @param fieldName Param field name to get
	 */
	abstract public Object getFieldValue(String fieldName);
	
	/**
	 * Questo oggetto rappresenta le chiavi di identificazione dell'elemento,
	 * utilizzato dalla sottoclasse AModelRecord
	 */
	protected ModelKey key;
	
	/**
	 * @param fieldName Param field type to get
	 */
	public Class getFieldType(String fieldName) {
		Field f;
		Class classType = null;
		try {
			f = (Field)getClass().getField(fieldName);
			classType = f.getType();
		} catch (NoSuchFieldException ex) {
			Monitor.debug(Monitor.DEBUG_ERROR, "Nessuna proprietà rispondente: " + fieldName +" -- "+ ex.getMessage() );
		}
		return classType;
	}
	
	/**
	 * @param fieldName Param field type to get
	 */
	public Object clearType(String fieldName, Object fieldValue) {
		Class c = getFieldType(fieldName);
		if (c!=null) {
			if (c == int.class)
				return clear(String.valueOf(fieldValue), c);
			else if (c == Integer.class)
				return clear(String.valueOf(fieldValue), c);
			return fieldValue;
		} else
			return null;
		
	}
	
	public final static String clear(String s, final Class type) {
		try {
			Number n = null;
			if (Locale.getDefault().getLanguage() == Locale.ITALIAN.getLanguage() ) {
				if (s.indexOf(".")!=-1) {
					Monitor.debug("Value ["+s+"] to clear");
					s = s.replace('.',',');
				}
			}
			
			n = NumberFormat.getInstance(Locale.getDefault()).parse(s);
			
			s = String.valueOf(n.intValue());
			
		} catch (ParseException e) {
			s = "0";
			return s;
			// e.printStackTrace();
		}
		return s;
	}
	
	/**
	 * @param paramMap Param field names map for filling this bean
	 */
	abstract public void beanFiller(java.util.Hashtable paramMap);
	
	public AModel(){
		pcsBean = new PropertyChangeSupport(this);
		pcsBean.addPropertyChangeListener(new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent e) {
							updateField( (String)e.getNewValue(), e.getPropertyName());
					}
		});
	}
	
	
	protected PropertyChangeSupport pcsBean;
	
	protected long modelKeyid;
	
	private String orderByGroup = null;
	
	protected String objectHeader = "Object header not set"; 
	
	protected String[] modelHeader; 
	
	protected String separator;
	
	protected void addPropertyChangeListener(PropertyChangeListener l) {
		pcsBean.addPropertyChangeListener(l);
	}
	
	protected void removePropertyChangeListener(PropertyChangeListener l) {
		pcsBean.addPropertyChangeListener(l);
	}

	public void firePropertyChange(String str, String oldStr, double[] newStr) {
		pcsBean.firePropertyChange(str, oldStr, newStr);
	}
	public void firePropertyChange(String str, String oldStr, String[] newStr) {
		pcsBean.firePropertyChange(str, oldStr, newStr);
	}
	public void firePropertyChange(String str, String oldStr, String newStr) {
		pcsBean.firePropertyChange(str, oldStr, newStr);
	}

	public void firePropertyChange(String str, int oldInt, int newInt) {
		pcsBean.firePropertyChange(str, oldInt, newInt);
	}

	public void firePropertyChange(String str, java.util.Date oldStr, java.util.Date newStr) {
		pcsBean.firePropertyChange(str, oldStr, newStr);
	}
	
	public String getObjectHeader() {
	    return objectHeader;
	}
	public void setObjectHeader(String objectHeader) {
		this.objectHeader = objectHeader;
	}
	
	public String[] getHeaderModel() {
		return modelHeader;
	}
	
	public String getHeaderModelColumn(int idx) {
	    return modelHeader[idx];
	}
	
	public String getHeaderModelValue(int idx) {
		String[] values = objectHeader.split( separator );
		return values[idx];
	}
	
	public void setHeaderModel(String[] headers, String separator) {
		this.separator = separator;
	    this.modelHeader = headers;
		this.objectHeader = "";
        for (int i = 0; i < headers.length; i++) 
            objectHeader += separator + getFieldValue( headers[i] );
	}
	
	
	/* (non-Javadoc)
	 * @see com.blogspot.fravalle.lib.bl.beans.IModel#getModelKeyId()
	 */
	public long getModelKeyId() {
		
		return this.modelKeyid;
	}
	
	/* (non-Javadoc)
	 * @see com.blogspot.fravalle.lib.bl.beans.IModel#setModelKeyId(long)
	 */
	public void setModelKeyId(long id) {
		// this.modelKeyid = (new java.util.Date()).getTime();
		this.modelKeyid = id;
	}
	
	// public String toString() {return objectHeader;}
	public String toString() {
		String key = "key-" + this.hashCode();
		/* String[] array = this.getModelKey().getKeys();
		for (int i = 0; i < array.length; i++) {
			key += "@"+this.getFieldValue("_"+array[i]);
		} */
		return key;
	}
	
	public void setOrderByGroup(final String order) {
		this.orderByGroup = order;
	}
	
	public String getOrderByGroup() {
		return this.orderByGroup;
	}
	
	/**
	 * Metodo fittizio ritornante una chiave univoca
	 * @return
	 * Stringa fittizia rappresentante un oggetto univoco invocato dagli elementi UI
	 */
	public String get_runtimeKey(){return this.toString();}
	
	public TreeNodeBean getNode() {
		TreeNodeBean node = new TreeNodeBean(this);
		node.setTitle( "Oggetto" );
		return node;
	}
	
}
