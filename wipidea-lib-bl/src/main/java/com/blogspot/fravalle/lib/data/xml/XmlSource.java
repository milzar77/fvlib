/*
 * XmlSource.java - libs (libs.jar)
 * Copyright (C) 2006
 * Source file created on 26-mag-2006
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [XmlSource]
 * 2DO:
 *
 */

package com.blogspot.fravalle.lib.data.xml;

/**
 * [][XmlSource]
 * 
 * <p><b><u>2DO</u>:</b>
 * 
 * 
 * @author Francesco Valle - (antares)
 */
public final class XmlSource {

	private String xmlPath;
	private String xslPath;
	private StringBuffer xmlBuffer;
	private StringBuffer xslBuffer;
	/**
	 * 
	 */
	public XmlSource() {
		super();
	}

	/**
	 * @param parXmlPath
	 * @param parXslPath
	 */
	public XmlSource(String parXmlPath, String parXslPath) {
		super();
		this.xmlPath = parXmlPath;
		this.xslPath = parXslPath;
	}
	/**
	 * @param parXslPath
	 * @param parXmlBuffer
	 */
	public XmlSource(StringBuffer parXmlBuffer, String parXslPath) {
		super();
		this.xmlBuffer = parXmlBuffer;
		this.xslPath = parXslPath;
	}
	/**
	 * @param parXmlPath
	 * @param parXslBuffer
	 */
	public XmlSource(String parXmlPath, StringBuffer parXslBuffer) {
		super();
		this.xmlPath = parXmlPath;
		this.xslBuffer = parXslBuffer;
	}
	/**
	 * @param parXmlBuffer
	 * @param parXslBuffer
	 */
	public XmlSource(StringBuffer parXmlBuffer, StringBuffer parXslBuffer) {
		super();
		this.xmlBuffer = parXmlBuffer;
		this.xslBuffer = parXslBuffer;
	}
	
	public Object getXml(){
		if (xmlPath==null)
			return xmlBuffer;
		else
			return xmlPath;
	}
	
	public Object getXsl(){
		if (xslPath==null)
			return xslBuffer;
		else
			return xslPath;
	}
	
}
