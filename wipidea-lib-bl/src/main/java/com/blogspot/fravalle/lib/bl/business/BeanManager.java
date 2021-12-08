/*
 * BeanWriter.java - jweevlib (jweevlib.jar)
 * Copyright (C) 2003
 * Source file created on 15-nov-2003
 * Author: Francesco Valle <info@weev.it>
 * http://www.weev.it
 *
 */


package com.blogspot.fravalle.lib.bl.business;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import com.blogspot.fravalle.lib.bl.beans.IModelList;
import com.blogspot.fravalle.lib.bl.beans.IModelRecord;
import com.blogspot.fravalle.lib.bl.beans.ModelList;
import com.blogspot.fravalle.lib.data.sql.DataConnection;
import com.blogspot.fravalle.lib.data.sql.SQLSelect;
import com.blogspot.fravalle.lib.gui.ScrollerInfo;
import com.blogspot.fravalle.lib.monitor.Monitor;


/**
 * @author antares
 * 
 */
final public class BeanManager {

	private static BeanManager singleton;
	private BeanManager(){}
	public static BeanManager getInstance() {
		if (singleton == null)
			singleton = new BeanManager();
		return singleton;
	}
	
	public IModelList loadSQLBean(ScrollerInfo info) {
		IModelList vt = new ModelList();
		try {
			String sqlSelect = info.getSqlReference();
		    
			SQLSelect dbs;
			
			dbs = new SQLSelect(new Properties(), true, false);
			
			dbs.select(sqlSelect, info.getBeanClassReference(), true, 100);
			
			// TODO : AGGIUNGERE PASSAGGIO DI CONVERSIONE DIRETTAMENTE IN CLASSE PCKG weev.sql
			java.util.Vector vtResult = (java.util.Vector)dbs.getResults();
			
			for ( int i=0; i < vtResult.size(); i++) {
			    vt.addElement( (IModelRecord)vtResult.elementAt(i) );
			}
			
			// java.util.Collection coll = new Collection(  );
			
			// vt =  new BeanVector()

			
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return vt;

	}
	
	/**
	 * @deprecated
	 * @param sqlBean
	 * @param recordClassName
	 * @param dbProp
	 * @return
	 */
	public static IModelList loadSQLBean(String sqlBean, String recordClassName, DataConnection dbProp) {
		IModelList vt = new ModelList();
		// TODO : impostare parametri per jdbc
		try {
			
			String sqlSelect = sqlBean;
		    
			SQLSelect dbs;

			
			dbs = new SQLSelect(dbProp.getParams(), true, false);
			
			dbs.select(sqlSelect, recordClassName, true, 100);
			
			// TODO : AGGIUNGERE PASSAGGIO DI CONVERSIONE DIRETTAMENTE IN CLASSE PCKG weev.sql
			java.util.Vector vtResult = (java.util.Vector)dbs.getResults();
			
			for ( int i=0; i < vtResult.size(); i++) {
			    vt.addElement( (IModelRecord)vtResult.elementAt(i) );
			}
			
			// java.util.Collection coll = new Collection(  );
			
			// vt =  new BeanVector()

			
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return vt;

	}
    
	public static IModelList loadXMLBean(String xmlBean) {
		return loadBean(xmlBean);
	}
	
	public static IModelList loadBean(String xmlBean) {
		IModelList vt = new ModelList();
	    try {
			java.beans.XMLDecoder d = new java.beans.XMLDecoder(
											  new java.io.BufferedInputStream(
													  new java.io.FileInputStream( xmlBean )
											  )
									  );
			vt = (IModelList)d.readObject();
			
			d.close();

		} catch (Exception ex) {
			Monitor.debug( "Impossibile recuperare il file <" + xmlBean + ">");
			Monitor.debug( ex.getMessage() );
			Monitor.debug( ex );
		}

		return vt;

	}

	
	public static void writeBean(Object[] array, String xmlBean) {
		
		writer(array, xmlBean);

	}
	
	public static void writeBean(java.util.Vector vt, String xmlBean) {
		
		writer(vt, xmlBean);

	}
	
	public static void writeBean(IModelList vt, String xmlBean) {
		
		writer(vt, xmlBean);

	}
	
	public static void saveBean(Object obj, String xmlBean) {
		
		IModelList vt = loadBean(xmlBean);

		IModelList vtBackup = vt;
		writer(vtBackup, xmlBean);

		vt.addElement( obj );
		writer(vt, xmlBean);

	}
	
	static public Object readModel(StringBuffer xmlSource) {
		File tempFile= null;
		try {
			tempFile = File.createTempFile(BeanManager.class.getName(),".tmp");
			FileWriter fwXml = new FileWriter(tempFile);
			fwXml.write(xmlSource.toString());
			fwXml.flush();
			fwXml.close();
		} catch ( IOException e ) {
			e.printStackTrace();
		}
		if (tempFile!=null && tempFile.exists())
			return readModel(tempFile.getAbsolutePath());
		else
			return null;
	}
	
	static public Object readModel(String xmlBean) {
		Object o = null;
	    try {
			java.beans.XMLDecoder d = new java.beans.XMLDecoder(
											  new java.io.BufferedInputStream(
													  new java.io.FileInputStream( xmlBean )
											  )
									  );
			o = (Object)d.readObject();
			
			d.close();

		} catch (Exception ex) {
			Monitor.debug( ex );
		}

		return o;
	}
	static public void writeModel(Object obj, String xmlBean) {
		try {

			java.beans.XMLEncoder e = new java.beans.XMLEncoder(
											  new java.io.BufferedOutputStream(
													  new java.io.FileOutputStream( xmlBean )
											  )
									  );
			e.writeObject( obj );
			e.flush();
			e.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private static void writer(Object[] array, String xmlBean) {
		try {

			java.beans.XMLEncoder e = new java.beans.XMLEncoder(
											  new java.io.BufferedOutputStream(
													  new java.io.FileOutputStream( xmlBean )
											  )
									  );
			e.writeObject( array );
			e.flush();
			e.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private static void writer(java.util.Vector vt, String xmlBean) {
		try {

			java.beans.XMLEncoder e = new java.beans.XMLEncoder(
											  new java.io.BufferedOutputStream(
													  new java.io.FileOutputStream( xmlBean )
											  )
									  );
			e.writeObject( vt );
			e.flush();
			e.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private static void writer(IModelList vt, String xmlBean) {
		try {

			java.beans.XMLEncoder e = new java.beans.XMLEncoder(
											  new java.io.BufferedOutputStream(
													  new java.io.FileOutputStream( xmlBean )
											  )
									  );
			e.writeObject( vt );
			e.flush();
			e.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	
}


