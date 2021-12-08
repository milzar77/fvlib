/*
 * Constants.java - antares-web (antares-web.jar)
 * Copyright (C) 2005
 * Source file created on 19-apr-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [Constants]
 * 2DO:
 *
 */

package com.blogspot.fravalle.j2ee.web;


/**
 * @author antares
 */
public interface Constants {
	
	/* xml operations, dataModel indexes */
	public static final int XML_DOC_CODE = 0, XML_VIEW_CODE = 1, XML_CACHE_CODE = 2;
	public static final String XML_DATA_EXT = ".xml", XML_VIEW_EXT = ".xsl", XML_MODEL_EXT = ".dtd";
	public static final String XML_DB_ROW = "ROW";
	public static final boolean CACHE_ENABLED = true;
	public static final boolean CACHE_DISABLED = false;
	
	public static final String XSL_BASE_PATH = "../res/xsl/";
	
	
	/* objects non inheritable via implematation, use direct access to interface Constants */
	// http error codes
	public static final int HTTP_ERROR_CODE = 0, HTTP_ERROR_TITLE = 1, HTTP_ERROR_DESC = 1; 
	public static final Object[]	HTTP_ERROR_506	= {new Integer(506),"Opzione non esistente.","Non scrivere come un troglodita nell'url"};
	public static final Object[]	HTTP_ERROR_507	= {new Integer(507),"Opzione non configurata.","Contattare il trilobite di sistema"};
	
	/* objects inheritable via implementation, access via abstract class ActionPages */
	// bean errors atribute mapping
	public static final String	BEAN_ERROR_LABEL		= "error";
	public static final String	BEAN_ERROR_DESC_SOLUTION= "description";
	public static final String	BEAN_ERROR_OBJ			= "objectError";
	public static final String	BEAN_ERROR_SERVLET_SRC	= "objectSource";
	public static final String	BEAN_ERROR_SERVLET_MAP	= "objectMapping";
	
	
	// bean atribute mapping, rinominare tutti i BEAN_* in PAGE_BEAN_*
	final public static String BEAN_PAGE_NAME = "pageData";
	// final public static String BEAN_CONTENT_DB_NAME = "connection";
	
	// xml transformation content mapping
	final public static String PAGE_CONTENT_NAME = "pageContent";
	final public static String PAGE_CONTENT_EMPTY = "L'oggetto richiesto non e' disponibile.";

	// 2do mapping, aggiungere in metodo apposito di ActionPages, adattare come per estensioni ignorate
	final public static String INIT = "init";
	final public static String INFO = "info";
	final public static String ROOT = "root";
	final public static String TEST = "test";
	final public static String TEST_ERROR = "testError";
	
	
	// extension mapping
	final public static String EXT_LEGGI = ".leggi";
	final public static String EXT_SCRIVI = ".scrivi";
	
	// ignore list of extension retrieved from mapping, css and other resources linked in relative mode
	// es: /root/res/siteStyle.css
	final public static String[] EXT_IGNORE_LIST = {".js",".gif",".jpg",".css",".ico"};
	
}