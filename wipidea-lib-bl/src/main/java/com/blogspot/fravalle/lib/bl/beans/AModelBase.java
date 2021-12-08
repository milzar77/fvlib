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
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.blogspot.fravalle.lib.monitor.Monitor;
import com.blogspot.fravalle.util.Constants;
import com.blogspot.fravalle.util.SettingRes;

/**
 * 
 * Gestione di bean java privi di getter e setter
 * 
 * @author antares
 */
abstract public class AModelBase extends AModel  {

	public AModelBase(){
		super();
		pcsBean = new PropertyChangeSupport(this);
		pcsBean.addPropertyChangeListener(new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent e) {
							updateField( (Object)e.getNewValue(), e.getPropertyName());
							/* Richiamo il metodo che imposta le chiavi dell'elemento definita nella implementazione finale  */
							getModelKey();
					}
		});
	}
	/*
    protected boolean isModelKey(String test, String testValue) {    	
    	for (int i = 0; i < getModelKey().getKeys().length; i++) {
    		if ( getModelKey().getKeys()[i].equals(test) )
    			getModelKey().setKeyValues(new String[]{test, testValue});
    			return true;
    	}
    	return false;
   	}
	*/
	/**
	 * 
	 * @param fieldValue New value to set for param field
	 * @param fieldName
	 */
	protected void updateField(Object fieldValue, String fieldName) {
		Field f;
		try {
			f = (Field)getClass().getField(fieldName);
			Class c = f.getType();
			if (c == String.class) {
				if (fieldValue!=null)
					f.set(this, fieldValue.toString());
				else
					f.set(this, null);
			} else if (c == int.class) {
				String cleared = null;
				if (fieldValue!=null)
					cleared = clear( fieldValue.toString(), int.class );
				else
					cleared = "0";
				f.set(this, new Integer(cleared));
				/* in presenza della virgola genera eccezione, controllare formati virgola accettati in input o valuecleaner */
			} else if (c == long.class) {
				if (fieldValue==null)
					fieldValue="0";
				f.set(this, new Long(fieldValue.toString()));
			} else if (c == double.class) {
				if (fieldValue==null)
					fieldValue="0";
				f.set(this, new Double(fieldValue.toString()));
			} else if (c == float.class) {
				if (fieldValue==null)
					fieldValue="0";
				f.set(this, new Float(fieldValue.toString()));
			} else if (c == short.class) {
				if (fieldValue==null)
					fieldValue="0";
				f.set(this, new Short(fieldValue.toString()));
			} else if (c == byte.class) {
				f.set(this, new Byte(fieldValue.toString()));
			} else if (c == boolean.class) {
				if (fieldValue==null)
					fieldValue="false";
				String strConvert = fieldValue.toString();
				f.set(this, new Boolean(strConvert));
			} else if (c == Boolean.class) {
				if (fieldValue==null)
					fieldValue="false";
				String strConvert = fieldValue.toString();
				if (strConvert.equals("f")) 
					strConvert = "false";
				else if (strConvert.equals("t"))
					strConvert = "true";
				else if (strConvert.equals("true"))
					strConvert = "true";
				else if (strConvert.equals("false"))
					strConvert = "false";
				f.set(this, new Boolean(strConvert));
			} else if (c == java.sql.Timestamp.class) {
				/* TODO: L'oggetto Timestamp crea problemi di serializzazione durante la procedura di xml encoding */
				String rawData = fieldValue.toString();
				
	   	  		SimpleDateFormat sdf = new SimpleDateFormat(SettingRes.get("db.format.date.timestamp"));
	   	  		Calendar calendar = Calendar.getInstance();
	   	  		if (rawData.equals(""))
	   	  			calendar.setTime( calendar.getTime() );
	   	  		else
	   	  			calendar.setTime( sdf.parse(rawData) );
	   	   		// Timestamp timestamp = new Timestamp( calendar.getTimeInMillis() );
	   	   		f.set(this, new Timestamp( calendar.getTimeInMillis() ));
	   	   		Monitor.log("Date parsed: " + rawData);
			} else {
				f.set(this, fieldValue);
			}
				
		} catch ( IllegalArgumentException ex ) {
			Monitor.debug(Monitor.DEBUG_ERROR, "Argomento illegale del metodo: " + fieldName +" -- "+ ex.getMessage() );
		} catch ( IllegalAccessException ex ) {
			Monitor.debug(Monitor.DEBUG_ERROR, "Accesso illegale alla proprietà: " + fieldName +" -- "+ ex.getMessage() );
		} catch (SecurityException ex) {
			Monitor.debug(Monitor.DEBUG_ERROR, "Autorizzazione non valida: " + fieldName +" -- "+ ex.getMessage() );
		} catch (NoSuchFieldException ex) {
			Monitor.debug(Monitor.DEBUG_ERROR, "Nessuna proprietà rispondente: " + fieldName +" -- "+ ex.getMessage() );
		} catch (ParseException ex) {
			Monitor.debug(Monitor.DEBUG_ERROR, "Formato della data non valido: " + fieldName +" -- "+ ex.getMessage() );
		}

	}
	
	/**
	 * @param fieldName Param field name to get
	 */
	public Object getFieldValue(String fieldName) {
		Object fieldValue = new Object();
		try {
			Field f = (Field)getClass().getField(fieldName);
			fieldValue = (Object)f.get( this );
			if (fieldValue==null) {
				Class c = f.getType();
				if (c == Boolean.class) {
					f.set(this, new Boolean(false));
					fieldValue = new Boolean(false);
				} else if (c == java.sql.Timestamp.class) {
					/* TODO: L'oggetto Timestamp crea problemi di serializzazione durante la procedura di xml encoding */
					f.set(this, new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis()));
					fieldValue = new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis());
				} else if (c == java.util.Date.class) {
					f.set(this, new java.util.Date(Calendar.getInstance().getTimeInMillis()));
					fieldValue = new java.util.Date(Calendar.getInstance().getTimeInMillis());
				}
				
			}
		}catch ( IllegalArgumentException ex ) {
			Monitor.debug(Monitor.DEBUG_ERROR, "Argomento illegale del metodo: " + fieldName +" -- "+ ex.getMessage() );
		} catch ( IllegalAccessException ex ) {
			Monitor.debug(Monitor.DEBUG_ERROR, "Accesso illegale al metodo: " + fieldName +" -- "+ ex.getMessage() );
		} catch (SecurityException ex) {
			Monitor.debug(Monitor.DEBUG_ERROR, "Autorizzazione non valida: " + fieldName +" -- "+ ex.getMessage() );
		} catch (NoSuchFieldException ex) {
			Monitor.debug(Monitor.DEBUG_ERROR, "Nessuna proprietà rispondente: " + fieldName +" -- "+ ex.getMessage() );
			/* patch per ottenere valore generato a runtime */
			if (fieldName.equals(Constants.CARD_RUNTIME_KEY))
				fieldValue = get_runtimeKey();
		}
		return 	fieldValue;
	}
	
	public Primitive getPrimitive(String fieldName) {
		Object o = getFieldValue(fieldName);
		return new Primitive(o);
	}
	public long getPrimitiveInt(String fieldName) {
		Object o = getFieldValue(fieldName);;
		if (o instanceof Integer)
			return ((Integer)o).intValue();
		else
			return ((Long)o).longValue();
	}
	public double getPrimitiveFloat(String fieldName) {
		Object o = getFieldValue(fieldName);
		if (o instanceof Float)
			return ((Float)o).floatValue();
		else
			return ((Double)o).doubleValue();
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
	
	final protected class Primitive extends APrimitive {

		private Double reference;
		
		/**
		 * Perdita di precisione
		 */
		public Primitive(Object o) {
			super();
			reference = new Double(o.toString());
			// 2DO Auto-generated constructor stub
		}
		
		/* (non-Javadoc)
		 * @see com.blogspot.fravalle.lib.bl.beans.ABaseModel.IPrimitive#_i()
		 */
		final public int aint() {
			// 2DO Auto-generated method stub
			/* per gioco creare un convertitore che spezzi i numeri superiori al tipo incapsulabile */
			return reference.intValue();
		}

		/* (non-Javadoc)
		 * @see com.blogspot.fravalle.lib.bl.beans.ABaseModel.IPrimitive#_l()
		 */
		final public long along() {
			// 2DO Auto-generated method stub
			return reference.longValue();
		}

		/* (non-Javadoc)
		 * @see com.blogspot.fravalle.lib.bl.beans.ABaseModel.IPrimitive#_s()
		 */
		final public short ashort() {
			// 2DO Auto-generated method stub
			return reference.shortValue();
		}

		/* (non-Javadoc)
		 * @see com.blogspot.fravalle.lib.bl.beans.ABaseModel.IPrimitive#_f()
		 */
		final public float afloat() {
			// 2DO Auto-generated method stub
			return reference.floatValue();
		}

		/* (non-Javadoc)
		 * @see com.blogspot.fravalle.lib.bl.beans.ABaseModel.IPrimitive#_d()
		 */
		final public double adouble() {
			// 2DO Auto-generated method stub
			return reference.doubleValue();
		}
		
	}
	abstract public class APrimitive implements IPrimitive {
		abstract public int aint();
		final public int i(){return aint();}
		
		abstract public long along();
		final public long l(){return along();}
		
		abstract public short ashort();
		final public short s(){return ashort();}
		
		abstract public float afloat();
		final public float f(){return afloat();}
		
		abstract public double adouble();
		final public double d(){return adouble();}
	}
	public interface IPrimitive {
		/**
		 * @return un intero
		 */
		public int i();
		/**
		 * @return un intero lungo
		 */
		public long l();
		/**
		 * @return
		 */
		public short s();
		/**
		 * @return un virgola mobile
		 */
		public float f();
		/**
		 * @return
		 */
		public double d();
	}

}
