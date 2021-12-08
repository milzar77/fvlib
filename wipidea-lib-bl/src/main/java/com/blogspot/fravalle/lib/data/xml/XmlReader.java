/*
 * XmlReader.java - jweevlib (jweevlib.jar)
 * Copyright (C) 2005
 * Source file created on 23-apr-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [XmlReader]
 * 2DO:
 *
 */

package com.blogspot.fravalle.lib.data.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.blogspot.fravalle.util.Constants;
import com.blogspot.fravalle.util.UIRes;


/**
 * @author antares
 */
public class XmlReader {
	
	static final private Logger logger = Logger.getLogger(XmlReader.class.getName());
	
	private Document	document;
	int			scan	= 1;
	boolean isFilteringActive = false;
	
	public Document getDocument() {
		return document;
	}
	
	public void updateNodeValue(String nodeName, String nodeValue) {
		/* NodeList nl = getDocument().getElementsByTagName( getDocument().getFirstChild().getNodeName() );
		for(int i=0; i<nl.getLength(); i++) {
			if(((Element)nl.item(i)).getElementsByTagName(nodeName).item(0).getFirstChild()==null)
				((Element)nl.item(i)).getElementsByTagName(nodeName).item(0).appendChild(getDocument().createTextNode(nodeValue));
			else
				((Element)nl.item(i)).getElementsByTagName(nodeName).item(0).getFirstChild().setNodeValue(nodeValue);
			
		}
		*/
		NodeList nl = getDocument().getElementsByTagName( nodeName );
		for(int i=0; i<nl.getLength(); i++) {
			if(((Element)nl.item(i)).getFirstChild()==null)
				((Element)nl.item(i)).appendChild(getDocument().createTextNode(nodeValue));
			else
				((Element)nl.item(i)).getFirstChild().setNodeValue(nodeValue);
			
		}
	}

	public void updateNodeAttribute(String nodeName, String attName, String attValue) {
		/* NodeList nl = getDocument().getElementsByTagName( getDocument().getFirstChild().getNodeName() );
		for(int i=0; i<nl.getLength(); i++) {
			((Element)nl.item(i)).getElementsByTagName(nodeName).item(0).getAttributes().getNamedItem(attName).setNodeValue(attValue);
		}
		*/
		NodeList nl = getDocument().getElementsByTagName( nodeName );
		for(int i=0; i<nl.getLength(); i++) {
			((Element)nl.item(i)).getAttributes().getNamedItem(attName).setNodeValue(attValue);
		}
	}
	
	public void testModify() {
		
		logger.finer(getDocument().getFirstChild().toString());
		//MainLogger.getLog().info(getDocument().getFirstChild().toString());
		
		NodeList nl = getDocument().getElementsByTagName("DOCUMENT");
		for(int i=0; i<nl.getLength(); i++) {
			String nodeName = "server";
			String nodeValue = "il mio colore preferito";
			if(((Element)nl.item(i)).getElementsByTagName(nodeName).item(0).getFirstChild()==null)
				((Element)nl.item(i)).getElementsByTagName(nodeName).item(0).appendChild(getDocument().createTextNode(nodeValue));
			else
				((Element)nl.item(i)).getElementsByTagName(nodeName).item(0).getFirstChild().setNodeValue(nodeValue);
			
		}
		
		NodeList nl1 = getDocument().getElementsByTagName("list");
		Node oldNode = nl1.item(0);

		Node node = getDocument().createElement("plutarco");
		Node nv = getDocument().createTextNode("plutarco");
		nv.setNodeValue("stikazzzzz");
		node.appendChild(nv);
		
		DocumentFragment d = getDocument().createDocumentFragment();
		d.appendChild(node);

		oldNode.getParentNode().replaceChild(getDocument().importNode(d, true), oldNode);

		logger.fine("==============="
				+ System.getProperty("line.separator")
				+ getDocument().getFirstChild().toString()
				);
	}
	
	/**
	 *  
	 */
	public XmlReader(Object docSource) throws SaxApplicationException {
		super();
		
		/* Aggiungere impostazione di rimozione XERCES DOM SERVICE IMPLEMENTATION */
		
		isFilteringActive = true;

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);
		factory.setNamespaceAware(true);
		try {
			// XmlDocumentBuilder crimson = new XmlDocumentBuilder();
			// document = crimson.createDocument();
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.getDOMImplementation();
			if (builder.isValidating())
				builder.setErrorHandler(null);
			if (docSource instanceof StringBuffer) {
				((StringBuffer)docSource).insert(0, XmlUtils.getDocumentProlog()+"\n");
				document = builder.parse(new ByteArrayInputStream(docSource.toString().getBytes()));
			} else {
				if (docSource.toString().startsWith("<") || docSource.toString().indexOf("<")!=-1) {
					docSource = XmlUtils.getDocumentProlog()+"\n" + docSource;
					document = builder.parse(new ByteArrayInputStream(docSource.toString().getBytes()));
				} else
					document = builder.parse(new File(docSource.toString()));
			}
			printNodeList(document.getChildNodes());

		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	/**
	 *  
	 */
	/* public XmlReader(String docPath) throws SaxApplicationException {
		super();
		// factory builder
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// factory.setValidating(true);
		// factory.setNamespaceAware(true);
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(new File(docPath));
			printNodeList(document.getChildNodes());
		}
		catch ( Exception e ) {
			new SaxApplicationException(e);
		}
	} */

	private void printNodeList(NodeList nl) {
		int c = scan++ ;
		for (int i = 0; i < nl.getLength(); i++ ) {
			Node n = nl.item(i);
			if (isFilteringActive)
				out(n, new String[]{"type", "title"});
			else
				out(n);
			
			if (n.hasChildNodes()) printNodeList(n.getChildNodes());
		}
	}

	private void compareNodeType(short key) {
		switch ( key ) {
			case Node.ELEMENT_NODE:
				out("This is an element value type");
				break;
			case Node.ATTRIBUTE_NODE:
				out("This is an attribute value type");
				break;
			case Node.TEXT_NODE:
			default:
				out("This is a text node value type");
		}
	}

	private void out(Node n, String filter) {
		if (n.hasAttributes() && n.getAttributes().getNamedItem(filter) != null) {
			out("<" + n.getParentNode().getNodeName() + ">");
			out("attrn:" + n.getAttributes().getNamedItem(filter).getNodeName());
			out("value:"
					+ n.getAttributes().getNamedItem(filter).getNodeValue());
			compareNodeType(n.getAttributes().getNamedItem(filter)
					.getNodeType());
		}
	}

	private void out(Node n, String[] filters) {
		for (int i = 0; i < filters.length; i++ ) {
			String filter = filters[i];
			out(n, filter);
		}
	}

	private void out(Node n) {
		out(" <" + n.getNodeName() + ">");
	    compareNodeType(n.getNodeType());
	}
	
	private void out(String output) {
		logger.finest(UIRes.getLabel("history.xmloutput")+output);
		//Monitor.log(UIRes.getLabel("history.xmloutput")+output);
	}

	private String getTreeDepth(int count) {
		String s = "<";
		for (int i = 1; i <= count; i++ ) {
			s += "-";
		}
		return s;
	}
	

	public static void main(String[] args) throws SaxApplicationException {
		String docPath = "";
		if (args.length == 0)
			docPath = "..//XMLTestProject//document.xml";
		else docPath = args[0];
		/* XmlReader.class.getClass().getConstructor(new Class[]{String.class}); */
		// new XmlReader(docPath);
		try {
			Class.forName("org.hsqldb.jdbcDriver"); //.newInstance();
			Connection c = DriverManager.getConnection("jdbc:hsqldb:mem:.",
					"sa", "");
			logger.info(Constants._logDefaultDef+"Status:" + c.isClosed());
			c.close();
			logger.info(Constants._logDefaultDef+"Status:" + c.isClosed());
		}
		catch ( Exception e ) {
			logger.severe("ERROR: failed to load HSQLDB JDBC driver.");
			e.printStackTrace();
			return;
		}
	}
}