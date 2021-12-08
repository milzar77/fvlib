/*
 * Constants.java - jappframework (jappframework.jar)
 * Copyright (C) 2005
 * Source file created on 4-dic-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [Constants]
 * 2DO:
 *
 */

package com.blogspot.fravalle.lib.data.xml;

/**
 * @author antares
 */
public interface XmlConstants {
	
	final public static String PAGE_CONTENT_EMPTY = "L'oggetto richiesto non e' disponibile.";
	final public static int XML_DOC_CODE = 0, XML_VIEW_CODE = 1, XML_CACHE_CODE = 2, XSL_PARAMS_CODE = 3;
	
	final public String emptyDocument = "<xml />";
	
	
	final public String CFG_DocumentBuilderFactory_jaxp = "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl";
	final public String CFG_DocumentBuilderFactory_crimson = "org.apache.crimson.jaxp.DocumentBuilderFactoryImpl";
	final public String CFG_DocumentBuilderFactory_xerces = "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl";

	final public String CFG_SAXParserFactory_jaxp = "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl";
	final public String CFG_SAXParserFactory_crimson = "org.apache.crimson.jaxp.SAXParserFactoryImpl";
	final public String CFG_SAXParserFactory_xerces = "org.apache.xerces.jaxp.SAXParserFactoryImpl";

	
	final public String CFG_TransformerFactory_trax = "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl";
	final public String CFG_TransformerFactory_xalan = "org.apache.xalan.processor.TransformerFactoryImpl";
	
	
}
