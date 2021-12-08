/*
 * FilteredProperties.java - jappframework (jappframework.jar)
 * Copyright (C) 2005
 * Source file created on 10-dic-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [FilteredProperties]
 * 2DO:
 *
 */

package com.blogspot.fravalle.util;

import java.util.Properties;

/**
 * @author antares
 */
public class FilteredProperties extends Properties {

	private String filter;
	private String descritpion;
	private Object propertySource;
	private String fileName;
	
	/**
	 * 
	 */
	public FilteredProperties() {
		super();
		// 2DO Auto-generated constructor stub
	}

	/**
	 * @param parArg0
	 */
	public FilteredProperties(Properties parArg0) {
		super(parArg0);
		// 2DO Auto-generated constructor stub
	}

	public final String getDescritpion() {
		return descritpion;
	}
	public final void setDescritpion(String parDescritpion) {
		descritpion = parDescritpion;
	}
	public final String getFilter() {
		return filter;
	}
	public final void setFilter(String parFilter) {
		filter = parFilter;
	}
	public final Object getPropertySource() {
		return propertySource;
	}
	public final void setPropertySource(Object parPropertySource) {
		propertySource = parPropertySource;
	}
	public final String getFileName() {
		return fileName;
	}
	public final void setFileName(String parFileName) {
		fileName = parFileName;
	}
}
