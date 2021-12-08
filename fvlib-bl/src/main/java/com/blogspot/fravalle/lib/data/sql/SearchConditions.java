/*
 * SearchConditions.java - jappframework (jappframework.jar)
 * Copyright (C) 2005
 * Source file created on 5-dic-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [SearchConditions]
 * 2DO:
 *
 */

package com.blogspot.fravalle.lib.data.sql;

import java.util.Vector;

import javax.swing.JFrame;

import com.blogspot.fravalle.lib.bl.beans.IModelList;
import com.blogspot.fravalle.lib.gui.mdi.AMDIApplet;
import com.blogspot.fravalle.lib.monitor.Monitor;

/**
 * @author antares
 */
public class SearchConditions {
	
	final static public int SELECT = 0;
	final static public int UPDATE = 1;
	final static public int INSERT = 2;
	final static public int DELETE = 3;
	
	public SearchConditions() {
		super();
		this.setQueryType(SELECT);
	}
	
	public SearchConditions(int parQueryType) {
		super();
		this.setQueryType(parQueryType);
	}
	private int queryType;
	private Vector[] vtData;
	private IModelList list;
	
	private String poolName;
	private String orderKey;
	private String orderVersus;
	private String orderMultiKey;
	private String recordPageCurrent;
	private String recordPageOffset;
	private int recordPerPage;
	private String xslTransformer;
	/**
	 * <code>query</code>: istruzioni di interrogazione database da eseguire durante
	 * la generazione della griglia di visualizzazione
	 */
	private String query;

	public final String getOrderKey() {
		return orderKey;
	}
	public final void setOrderKey(String parOrderKey) {
		orderKey = parOrderKey;
	}
	public final String getOrderMultiKey() {
		return orderMultiKey;
	}
	public final void setOrderMultiKey(String parOrderMultiKey) {
		orderMultiKey = parOrderMultiKey;
	}
	public final String getOrderVersus() {
		return orderVersus;
	}
	public final void setOrderVersus(String parOrderVersus) {
		orderVersus = parOrderVersus;
	}
	public final String getQuery() {
		return query;
	}
	public final void setQuery(String parQuery) {
		query = parQuery;
	}
	public final String getRecordPageCurrent() {
		return recordPageCurrent;
	}
	public final void setRecordPageCurrent(String parRecordPageCurrent) {
		recordPageCurrent = parRecordPageCurrent;
	}
	public final String getRecordPageOffset() {
		return recordPageOffset;
	}
	public final void setRecordPageOffset(String parRecordPageOffset) {
		recordPageOffset = parRecordPageOffset;
	}
	public final int getRecordPerPage() {
		return recordPerPage;
	}
	public final void setRecordPerPage(int parRecordPerPage) {
		recordPerPage = parRecordPerPage;
	}
	public final String getXslTransformer() {
		return xslTransformer;
	}
	public final void setXslTransformer(Object o) {
		boolean eclipse = true;
		String res = Monitor.getRunTimePath(eclipse) + "/res/xml/xsl/";
		if (o instanceof String)
			res += String.valueOf(o);
		else if (o instanceof JFrame)
			res += ((JFrame)o).getClass().getName() + ".xsl";
		else if (o instanceof AMDIApplet)
			res += ((AMDIApplet)o).getClass().getName() + ".xsl";
		else
			res += "default.xsl";
		this.xslTransformer = res;
	}
	public final String getPoolName() {
		return poolName;
	}
	public final void setPoolName(String parPoolName) {
		poolName = parPoolName;
	}
	public boolean isXmlBridge(){
		if (this.getXslTransformer() != null)
			return true;
		else
			return false;
	}
	public final int getQueryType() {
		return queryType;
	}
	private final void setQueryType(int parQueryType) {
		queryType = parQueryType;
	}
	public boolean isUpdate(){
		return getQueryType() == UPDATE;
	}
	public boolean isInsert(){
		return getQueryType() == INSERT;
	}
	
	public void setUpdatableData(IModelList parList) {
		list = parList;
	}
	public IModelList getUpdatableData() {
		return list;
	}
	
	public void setUpdatable(Vector[] vtArray) {
		vtData = vtArray;
		// array bidimensionale, posizione 0 = valori colonne, 1 = nomi colonne
	}
	public Vector[] getUpdatable() {
		return vtData;
	}
}
