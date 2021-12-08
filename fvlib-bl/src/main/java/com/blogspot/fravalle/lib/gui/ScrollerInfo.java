/*
 * ScrollerInfo.java - jappframework (jappframework.jar)
 * Copyright (C) 2005
 * Source file created on 11-dic-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [ScrollerInfo]
 * 2DO:
 *
 */

package com.blogspot.fravalle.lib.gui;

import java.io.File;

import com.blogspot.fravalle.lib.bl.business.ExternalConnection;
import com.blogspot.fravalle.lib.bl.business.exceptions.FrameworkDatabaseException;
import com.blogspot.fravalle.lib.bl.business.exceptions.FrameworkFatalException;
import com.blogspot.fravalle.lib.data.sql.SearchConditions;
import com.blogspot.fravalle.lib.data.xml.XmlTransformer;
import com.blogspot.fravalle.lib.monitor.Monitor;
import com.blogspot.fravalle.util.SettingRes;

/**
 * @author antares
 */
public class ScrollerInfo extends ExternalConnection {

	protected SearchConditions searchConditions;
	
	/**
	 * Default constructor for testing purpose
	 */
	public ScrollerInfo() {
		super();
	}
	
	/**
	 * Constructor used to manage local data stored into serialized xml bean
	 * @param strBeanReference
	 * The package domain and class name reference to load
	 */
	public ScrollerInfo(String strBeanReference) {
		super();
		this.setBeanClassReference(strBeanReference);
		this.setLabelMasked(true);
		this.setScanner(false);
	}
	
	/**
	 * Constructor used to manage data from RDBMS cached into serialized xml bean
	 * @param strBeanReference
	 * @param strSqlReference
	 */
	public ScrollerInfo(String strBeanReference, SearchConditions search) {
		super();
		this.searchConditions = search;
		this.setBeanClassReference(strBeanReference);
		this.setSqlReference(search.getQuery());
		this.setLabelMasked(true);
		this.setScanner(false);
		if ( !this.existsXmlCache() ) {
			try {
				this.precacheData(search);
			} catch (FrameworkDatabaseException e) {
				Monitor.log("La consultazione dei dati in offline è disponibile solo in presenza della cache");
				Monitor.debug(e);
			} catch (FrameworkFatalException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void precacheData(SearchConditions search) throws FrameworkFatalException, FrameworkDatabaseException {
//		JdbcConnectionPool connection = new JdbcConnectionPool(Constants._fileDatabasePath,SettingRes.get("mdi.applet.WinModelTypes.db.pool"));
//		Object[] dataModel = new Object[]{ connection.getData(search) , search.getXslTransformer()};
		StringBuffer sb = (StringBuffer)this.dataChannel(search);
		String modelName = "";
		try {
			Class c = Class.forName(getBeanClassReference());
			modelName = c.getSuperclass().getName();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		String[][] xslParams = new String[][]{
				new String[] {"beanName",getBeanClassReference()},
				new String[] {"beanModelName",modelName}
		};

		Object[] dataModel = new Object[]{ sb , search.getXslTransformer(), new Boolean(true),xslParams};
		
		XmlTransformer.getInstance().cacheBean(dataModel, getBeanClassReference());
	}
	
	public void recacheData() throws FrameworkFatalException, FrameworkDatabaseException {
		if ( this.existsXmlCache() ) {
			this.xmlCache().delete();
		}
		StringBuffer sb = (StringBuffer)this.dataChannel(searchConditions);
		String modelName = "";
		try {
			Class c = Class.forName(getBeanClassReference());
			modelName = c.getSuperclass().getName();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		String[][] xslParams = new String[][]{
				new String[] {"beanName",getBeanClassReference()},
				new String[] {"beanModelName",modelName}
		};

		Object[] dataModel = new Object[]{ sb , searchConditions.getXslTransformer(), new Boolean(true),xslParams};		
		XmlTransformer.getInstance().cacheBean(dataModel, getBeanClassReference());
	}
	
	private String beanClassReference; 
	private boolean isLabelMasked;
	
	private boolean isScanner;
	private boolean isAdministrativePanel;
	
	private String source;
	private String xmlReference;
	private String sqlReference;
	
	private boolean isXmlReference;
	private boolean isSqlReference;
	
/*	public ScrollerSource(String source, boolean isXML) {
		this.source = source;
		this.isXML = isXML;
		if (!isXML) this.isSQL = true; 
	}
*/
	public final String getBeanClassReference() {
		return beanClassReference;
	}
	public final void setBeanClassReference(String parBeanClassReference) {
		beanClassReference = parBeanClassReference;
		setXmlReference(parBeanClassReference);
	}
	public final boolean isAdministrativePanel() {
		return isAdministrativePanel;
	}
	public final void setAdministrativePanel(boolean parIsAdministrativePanel) {
		isAdministrativePanel = parIsAdministrativePanel;
	}
	public final boolean isLabelMasked() {
		return isLabelMasked;
	}
	public final void setLabelMasked(boolean parIsLabelMasked) {
		isLabelMasked = parIsLabelMasked;
	}
	public final boolean isScanner() {
		return isScanner;
	}
	public final void setScanner(boolean parIsScanner) {
		isScanner = parIsScanner;
	}
	
	public final boolean isSqlReference() {
		return getSqlReference() != null;
	}

	/**
	 * Controllo necessario per identificare i seguenti casi:
	 * 1) Bean caricato da cache xml
	 * 2) Dati trasferiti e gestiti esclusivamente su base dati xml
	 * @return boolean
	 * Controllo formato di riferimento oggetto dati
	 */
	public final boolean isXmlReference() {
		return ( getSource() == getBeanClassReference() );
	}
	
	public final String getSource() {
		return source;
	}
	public final void setSource(String parSource) {
		source = parSource;
	}
	public final String getSqlReference() {
		return sqlReference;
	}
	
	public final void setSqlReference(String parSqlReference) {
		setSource(parSqlReference);
		sqlReference = parSqlReference;
	}

	public final String getXmlReference() {
		return xmlReference;
	}

	public final void setXmlReference(String parXmlReference) {
		setSource(parXmlReference);
		xmlReference = System.getProperty("java.io.tmpdir").replaceAll("\\\\","//") + "working//" + parXmlReference + SettingRes.get("mdi.working.xml.bean.extension");
		// SettingRes.get("mdi.working.xml.dir") + parXmlReference + SettingRes.get("mdi.working.xml.bean.extension");
	}
	final public boolean existsXmlCache() {
		File file = new File( getXmlReference() );
		return file.exists();
	}
	final public File xmlCache() {;
		return new File( getXmlReference() );
	}
}
