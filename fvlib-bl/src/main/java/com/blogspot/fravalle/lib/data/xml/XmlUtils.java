/*
 * XmlUtils.java - antares-web (antares-web.jar)
 * Copyright (C) 2005
 * Source file created on 24-apr-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [XmlUtils]
 * 2DO:
 *
 */

package com.blogspot.fravalle.lib.data.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;


/**
 * @author antares
 */
public abstract class XmlUtils implements XmlConstants {
	final public static String getDocumentProlog() {
		return "<?xml version=\"1.0\" encoding=\"ISO_8859-1\" standalone=\"yes\"?>";
	}

	final public static String tagErrorList(String parMessage) {
		return "<errors>" + parMessage + "</errors>";
	}

	final public static String tagError(String parLabel, String parMessage) {
		return "<error>"+ tag("LABEL", parLabel) +"<CONTENT>" + parMessage + "</CONTENT></error>";
	}
	final public static String tagErrorCData(String parLabelName, String parLabelValue, String parMessage) {
		return "<error>"+ tag(parLabelName, parLabelValue) +"<![CDATA[" + parMessage + "]]></error>";
	}
	final public static String tagErrorCData(String parMessage) {
		return "<error><![CDATA[" + parMessage + "]]></error>";
	}
	
	final public static String tagError(String parXml) {
		return "<error>" + parXml + "</error>";
	}
	
	final public static String tagHyperdb(String parName, String parValue) {
		return "<" + parName + ">" + parValue + "</" + parName + ">";
	}

	final public static String tagHyperdbCData(String parName, String parValue) {
		return "<" + parName + "><![CDATA[" + parValue + "]]></" + parName + ">";
	}

	final public static String tagException(String parName, String parValue, String sException) {
		return "<" + parName + "><saxexception><![CDATA["+sException+"]]></saxexception><!--<![CDATA[" + parValue + "]]>--></" + parName + ">";
		// return "<" + parName + "><saxexception><![CDATA["+sException+"]]></saxexception>" + parValue + "</" + parName + ">";
	}
	
	/**
	 * Metodo di generazione CDATA
	 * 
	 * @param parValue
	 * Contenuto CDATA
	 * @return
	 * Elemento CDATA
	 */
	final public static String tagCData(String parValue) {
		return "<![CDATA[" + parValue + "]]>";
	}
	
	/**
	 * Metodo di generazione tag il cui contenuto � di tipo CDATA
	 * 
	 * @param parName
	 * Nome assegnato al tag
	 * @param parValue
	 * Contenuto del tag di tipo CDATA
	 * @return
	 * Stringa il cui contenuto rappresenta il tag formattato
	 * @deprecated
	 */
	final public static String tagCData(String parName, String parValue) {
		return "<" + parName + "><![CDATA[" + parValue + "]]></" + parName + ">";
	}
	/**
	 * Metodo di generazione tag il cui contenuto � di tipo plain text
	 * 
	 * @param parName
	 * Nome assegnato al tag
	 * @param parValue
	 * Contenuto del tag di tipo plain text
	 * @return
	 * Stringa il cui contenuto rappresenta il tag formattato
	 */
	final public static String tag(String parName, String parValue) {
		return "<" + parName + ">" + parValue + "</" + parName + ">";
	}
	
	/**
	 * Metodo di generazione tag il cui contenuto � di tipo CDATA
	 * 
	 * @param parName
	 * Nome assegnato al tag
	 * @param parValue
	 * Contenuto del tag di tipo CDATA
	 * @return
	 * Stringa il cui contenuto rappresenta il tag formattato
	 */
	final public static String tagColumnData(String parName, String parValue) {
		return "<" + parName + "><![CDATA[" + parValue + "]]></" + parName + ">";
	}
	/**
	 * Metodo di generazione tag il cui contenuto � di tipo CDATA, il tag � inoltre corredato di commento tecnico 
	 * 
	 * @param parName
	 * Nome assegnato al tag
	 * @param parValue
	 * Contenuto del tag di tipo CDATA
	 * @param comment
	 * Contenuto del messaggio di commento posizionato come primo sotto-nodo del tag
	 * @return
	 * Stringa il cui contenuto rappresenta il tag formattato corredato di commento
	 */
	final public static String tagColumnData(String parName, String parValue, String comment) {
		return "<" + parName + "><!-- " + comment + " --><![CDATA[" + parValue + "]]></" + parName + ">";
	}
	/**
	 * Metodo di generazione tag il cui contenuto � di tipo CDATA, corredato di attributi
	 * 
	 * @param parName
	 * Nome assegnato al tag
	 * @param parValue
	 * Contenuto del tag di tipo CDATA
	 * @param att
	 * Array bidimensionale di stringhe contenenti gli attributi, att[POSIZIONE][ATT_NAME] e att[POSIZIONE][ATT_VALUE]
	 * @return
	 * Stringa il cui contenuto rappresenta il tag formattato corredato di attributi
	 */
	final public static String tagColumnDataAtt(String parName, String parValue, boolean isLabelAlias, String[][] att) {
		final int ATT_NAME = 0, ATT_VALUE = 1;
		StringBuffer attPipe = new StringBuffer(512);
		for (int i = 0; i < att.length; i++) {
			if (att[i].length>0) {
				attPipe.append(" ");
				attPipe.append(att[i][ATT_NAME]);
				attPipe.append("=\"");
				attPipe.append(att[i][ATT_VALUE]);
				attPipe.append("\"");
			}
		}
		String sLabel = "";
		if (isLabelAlias) {
			sLabel = "<label><![CDATA["+"]]>"+parName+"</label>";
			parName = "col";
		}
		String nodeContent = "";
		if (!parValue.startsWith("<"))
			nodeContent = "<![CDATA[" + parValue + "]]>";
		else
			nodeContent = parValue;
		return "<" + parName + attPipe.toString() + ">"+nodeContent+sLabel+"</" + parName + ">";
	}
	/**
	 * Metodo di generazione tag il cui contenuto � di tipo plain text, corredato di attributi
	 * 
	 * @param parName
	 * Nome assegnato al tag
	 * @param parValue
	 * Contenuto del tag di tipo plain text
	 * @param att
	 * Array bidimensionale di stringhe contenenti gli attributi, att[POSIZIONE][ATT_NAME] e att[POSIZIONE][ATT_VALUE]
	 * @return
	 * Stringa il cui contenuto rappresenta il tag formattato corredato di attributi
	 */
	final public static String tagAtt(String parName, String parValue, String[][] att) {
		final int ATT_NAME = 0, ATT_VALUE = 1;
		StringBuffer attPipe = new StringBuffer(512);
		for (int i = 0; i < att.length; i++) {
			attPipe.append(" ");
			attPipe.append(att[i][ATT_NAME]);
			attPipe.append("=\"");
			attPipe.append(att[i][ATT_VALUE]);
			attPipe.append("\"");
		}
		return "<" + parName + attPipe.toString() + ">" + parValue + "</" + parName + ">";
	}
	/**
	 * Metodo di generazione tag il cui contenuto � di tipo plain text, corredato di attributo singolo
	 * 
	 * @param parName
	 * Nome assegnato al tag
	 * @param parValue
	 * Contenuto del tag di tipo plain text
	 * @param attName
	 * Nome dell'attributo
	 * @param attValue
	 * Valore dell'attributo
	 * @return
	 * Stringa il cui contenuto rappresenta il tag formattato corredato di attributo singolo
	 */
	final public static String tagAtt(String parName, String parValue, String attName, String attValue) {
		return "<" + parName + " " + attName + "=\"" + attValue + "\">" + parValue + "</" + parName + ">";
	}
	

	final public static String tagPage(String parName, String parValue) {
		String content = "<CONTENT><" + parName + ">" + String.valueOf(parValue) + "</" + parName + "></CONTENT>";
		StringBuffer sb = new StringBuffer();
		sb.append(XmlUtils.tag( "DOCUMENT", content ));
		return String.valueOf(sb);
	}
	/* final public static String tagPage(String parName, String parValue, HttpServletRequest req) {
		String content = "<CONTENT><" + parName + ">" + String.valueOf(parValue) + "</" + parName + "></CONTENT>";
		StringBuffer sb = new StringBuffer();
		sb.append(XmlUtils.tag( "DOCUMENT", tagPageData(req)+content ));
		return String.valueOf(sb);
	} */
	
	final public static String tagErrorListPage(String parMessage) {
		String content = "<CONTENT><errors>" + String.valueOf(parMessage) + "</errors></CONTENT>";
		StringBuffer sb = new StringBuffer();
		sb.append(XmlUtils.tag( "DOCUMENT", content ));
		return String.valueOf(sb);
	}
	/* final public static String tagErrorListPage(String parMessage, HttpServletRequest req) {
		String content = "<CONTENT><errors>" + String.valueOf(parMessage) + "</errors></CONTENT>";
		StringBuffer sb = new StringBuffer();
		sb.append(XmlUtils.tag( "DOCUMENT", tagPageData(req)+content ));
		return String.valueOf(sb);
	} */
	
	/* final private static String tagPageData(HttpServletRequest req) {
		StringBuffer sb = new StringBuffer();
		sb.append(XmlUtils.tag( "FOOTER", String.valueOf("Default Footer") ));
		sb.append(XmlUtils.tag( "DOCTITLE", String.valueOf( req.getAttribute("docTitle") ) ));
		sb.append(XmlUtils.tagCData( "URL_RELOAD", String.valueOf( req.getRequestURI() ) + "?" + String.valueOf( req.getQueryString() ) ));
		sb.append(XmlUtils.tagCData( "URL_BACK", String.valueOf( req.getHeader("referer") ) ));
		sb.append(XmlUtils.tag( "URL_CONTEXT", String.valueOf( req.getContextPath() ) ));
		sb.append(XmlUtils.tag( "URL_HOME", String.valueOf( req.getContextPath() + "/web/index.jsp" ) ));
		sb.append(XmlUtils.tagCData( "URL_QUERY", String.valueOf( req.getQueryString() ) ));
		sb.append(XmlUtils.tag( "HOST_CONTAINER", String.valueOf( req.getScheme() + "://" + req.getServerName() +":"+ req.getServerPort() ) ));
		sb.append(XmlUtils.tag( "HOST_BASE", String.valueOf( req.getScheme() + "://" + req.getServerName() ) ));
		
		if (String.valueOf( req.getQueryString() ).indexOf(UtilConstants.PAR_KEY) != -1 ) {
			String reqKey = req.getParameter(UtilConstants.PAR_KEY);
			String xmlId = reqKey.substring( 0, reqKey.indexOf(UtilConstants.PAR_KEY_SEPARATOR) );
			String xmlKey = reqKey.substring( reqKey.indexOf(UtilConstants.PAR_KEY_SEPARATOR)+1 );
			sb.append(XmlUtils.tag( "XML_ID", xmlId ));
			sb.append(XmlUtils.tag( "XML_KEY", xmlKey ));
		}
		
		StringBuffer queryParameters = new StringBuffer("<dummy>dummy</dummy>"); 
		for (Enumeration e = req.getParameterNames(); e.hasMoreElements();) {
			String key = String.valueOf(e.nextElement());
			String value = req.getParameter(key);
			queryParameters.append( XmlUtils.tagCData( key,value ) );
		}
		sb.append(XmlUtils.tag( "REQUEST-PARAMETERS", String.valueOf(queryParameters) ));
		
		StringBuffer queryHeaders = new StringBuffer("<dummy>dummy</dummy>"); 
		for (Enumeration e = req.getHeaderNames(); e.hasMoreElements();) {
			String key = String.valueOf(e.nextElement());
			String value = req.getHeader(key);
			if (checkValidTagName(key))
				queryHeaders.append( XmlUtils.tagCData( key,value ) );
			else
				queryHeaders.append( XmlUtils.tagCData( "invalidTagName","{Invalid tag name:["+key+"]}"+value ) );
		}
		sb.append(XmlUtils.tag( "REQUEST-HEADERS", String.valueOf(queryHeaders) ));
		
		StringBuffer queryAttributes = new StringBuffer("<dummy>dummy</dummy>"); 
		for (Enumeration e = req.getAttributeNames(); e.hasMoreElements();) {
			String key = String.valueOf(e.nextElement());
			Object obj = req.getAttribute(key);
				queryAttributes.append( XmlUtils.tagCData( key, String.valueOf(obj) ) );
			// incapsulare bean con xml encode, solo per jdk 1.4
		}
		sb.append(XmlUtils.tag( "REQUEST-ATTRIBUTES", String.valueOf(queryAttributes) ));
		
		return "<PAGE>" + String.valueOf(sb) + "</PAGE>";
	} */
	
	private final static boolean checkValidTagName(String tagName) {
		if (tagName.startsWith(" ") || tagName.startsWith("-") || (tagName.indexOf("--")!=-1) )
			return false;
		else
			return true;
	}
	
	public static Boolean cacheResult(boolean b) {
		return new Boolean(b);
	}
	
	public static Boolean isCacheResult(boolean b) {
		return new Boolean(b);
	}

	public static String validateXmlDocumentMessage(StringBuffer sb) {
		String s = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(true);
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setErrorHandler(null);
			Document doc = builder.parse(new ByteArrayInputStream(sb.toString().getBytes()));
			s = null;
		}
		catch ( ParserConfigurationException e ) {
			e.printStackTrace();
			s = e.getMessage();
		}
		catch ( SAXException e ) {
			e.printStackTrace();
			s = e.getMessage();
		}
		catch ( IOException e ) {
			e.printStackTrace();
			s = e.getMessage();
		}
		return s;
	}
	
	public static boolean validateXmlDocument(StringBuffer sb) {
		boolean b = false;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(true);
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setErrorHandler(null);
			Document doc = builder.parse(new ByteArrayInputStream(sb.toString().getBytes()));
			b = true;
		}
		catch ( ParserConfigurationException e ) {
			e.printStackTrace();
			b = false;
		}
		catch ( SAXException e ) {
			e.printStackTrace();
			b = false;
		}
		catch ( IOException e ) {
			e.printStackTrace();
			b = false;
		}
		return b;
	}
	
	/**
	 * @param parErrorDesc
	 * @return
	 */
	public static final String replaceEntity(String sContent, String sReplace, String sReplaced) {
		// rimpiazzare con routine basata su metodi di base
		String s = "";
		StringTokenizer st = new StringTokenizer(sContent, sReplace);
		if (st.countTokens()>0) {
			s = "";
			int i=0;
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				if (i>0) s += sReplaced + token;
				else s += token;
				i++;
			}
		}
		return s;
	}
	
	/**
	 * @param parErrorDesc
	 * @return
	 */
	public static final String replace(String parErrorDesc) {
		// rimpiazzare con routine basata su metodi di base
		String s = parErrorDesc;
		StringTokenizer st = new StringTokenizer(parErrorDesc, "<");
		if (st.countTokens()!=0) {
			s = "";
			int i=0;
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				if (i>0) s += "&lt;" + token;
				else s += token;
				i++;
			}
		}
		return s;
	}
	
}