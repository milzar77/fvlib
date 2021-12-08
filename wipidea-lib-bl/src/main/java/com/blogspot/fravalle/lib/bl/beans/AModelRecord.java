/*
 * QueryModel.java - FrameWork (FrameWork.jar)
 * Copyright (C) 2005
 * Source file created on 4-dic-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [QueryModel]
 * TODO:
 *
 */

package com.blogspot.fravalle.lib.bl.beans;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import com.blogspot.fravalle.lib.monitor.Monitor;
import com.blogspot.fravalle.util.Constants;
import com.blogspot.fravalle.util.SettingRes;

/**
 * Questa classe astratta definisce i metodi di tutti gli oggetti collegati ad un record set.
 * 
 * <p><b><u>TODO</u>:</b> Implementare recupero delle chiavi primarie, al momento inserito nella classe Model
 * @author Francesco Valle - (antares)
 */
abstract public class AModelRecord extends AModelBase implements IModelRecord {
	
    private String sqlTable = null;

    
	public AModelRecord(final Hashtable ht) {
		super();
		beanFiller(ht);
	}
	
	public AModelRecord(final String table) {
		super();
		// TODO: rimuovere valorizzazione diretta della variabile
		this.sqlTable = table;
	}
    
    
	/* (non-Javadoc)
	 * @see com.blogspot.fravalle.lib.bl.beans.IRecord#setSqltable(java.lang.String)
	 */
	public void setSqltable(final String par) {
		this.sqlTable = par;
	}
	
	/* (non-Javadoc)
	 * @see com.blogspot.fravalle.lib.bl.beans.IRecord#getSqltable()
	 */
	public String getSqltable() {
		return this.sqlTable;
	}

	public boolean hasChanges() {
		return key != null;
	}
	
	/* protected String getWhereClause(Vector vt) {
		String _whereClause = " WHERE ";
		for (int i = 0; i < getModelKey().getKeys().length; i++) {
			_whereClause += getModelKey().getKeys()[i]+ "=? AND ";
			vt.add( getFieldValue(getModelKey().getKeys()[i]) );
			// eliminato il riferimento con underscore
		}
		return _whereClause + " 1=1";
	}
	*/
	public Vector updateSqlBean() {
		String query = "UPDATE "+sqlTable+" SET ", declaredCols = "";
		String colSep = ", ";
		Vector vtColValues = new Vector();
		Vector vtGlobal = new Vector();
		Field[] fs = getClass().getDeclaredFields();
		for (int i = 0; i < fs.length; i++) {
			String colName = fs[i].getName();
			if (colName.startsWith("_"))
				colName = fs[i].getName().substring(1);

			declaredCols += colName + "=?" + colSep;
			try {
				// TODO: rivedere questa magapatch
				if (fs[i].getName().toLowerCase().indexOf("date") != -1 ) {
					Object o = fs[i].get(this);
					SimpleDateFormat sdf = new SimpleDateFormat(SettingRes.get("db.format.date.timestamp"));
		   	  		Calendar c = Calendar.getInstance();
		   	  		c.setTimeInMillis( ((Long)o).longValue() );
					vtColValues.add( sdf.format( c.getTime() ) );
				} else {
					vtColValues.add( fs[i].get(this) );
				}
			} catch (IllegalArgumentException e) {
				vtColValues.add( null );
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				vtColValues.add( null );
				e.printStackTrace();
			}
		}
		query += declaredCols.substring(0, declaredCols.lastIndexOf(colSep));
		/* Aggiungo in automatico i nuovi valori da mappare */
		// query += getWhereClause(vtColValues);

		if (key != null)
			query += getModelKey().getQueryKeys(vtColValues);
		else
			query = "vai in errore!!!";
		
		Monitor.debug(vtColValues.toString());
		
		
		
		vtGlobal.add(query);
		vtGlobal.add(vtColValues);
		return vtGlobal;
	}
	
	public String beanPrepareSQL(Vector vtBeanMap) {
		updateSqlBean();
		String queryModel = "";
		String insertQuery = " VALUES(";
		
		boolean isUpdate = true;
		if (this.getModelKeyId() > 0) isUpdate = true;
		else isUpdate = false;
		
		if (isUpdate)
			queryModel = "UPDATE "+sqlTable+" SET ";
		else
			queryModel = "INSERT INTO "+sqlTable+"(";
		
		String primaryKey = "";
		
		
			for (int i = 0; i < vtBeanMap.size(); i++) {
				String fldMap = vtBeanMap.elementAt( i ).toString();
					if (isUpdate) {
						queryModel += fldMap.substring(1) + "=?, ";
					} else { 
						queryModel += fldMap.substring(1) + ", ";
						insertQuery += "?, ";
					}
			}
		
		
		if (isUpdate) { 
			queryModel = queryModel.substring(0, queryModel.lastIndexOf(", "));
			// TODO : utilizzare elemento in posizione 0
			// per identificare la chiave primaria del record
			
			queryModel += " WHERE " + primaryKey + "=" + this.getModelKeyId() + ";"; 
		} else {
			queryModel = queryModel.substring(0, queryModel.lastIndexOf(", "));
			insertQuery = insertQuery.substring(0, insertQuery.lastIndexOf(", "));
			queryModel += ")" + insertQuery + ")" + ";";
		}
		
		Monitor.log( Constants._logDefaultDef +queryModel);
		
		return queryModel;
		
		
	}

}
