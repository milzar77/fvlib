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

package com.blogspot.fravalle.util.xml;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * @author antares
 */
public class XmlReader {
	Document	document;
	int			scan	= 1;

	/**
	 *  
	 */
	public XmlReader(String docPath) throws SaxApplicationException {
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
			// throw
			new SaxApplicationException(e);
		}
	}

	private void printNodeList(NodeList nl) {
		int c = scan++ ;
		for (int i = 0; i < nl.getLength(); i++ ) {
			Node n = nl.item(i);
			// System.out.println( getTreeDepth(i*c) );
			out(n, new String[]{"type", "title"});
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
		}/*
		  * else { out(" <" + n.getNodeName() + ">");
		  * compareNodeType(n.getNodeType()); }
		  */
	}

	private void out(Node n, String[] filters) {
		for (int i = 0; i < filters.length; i++ ) {
			String filter = filters[i];
			out(n, filter);
		}
	}

	private void out(String output) {
		System.out.println(output);
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
			System.out.println("Status:" + c.isClosed());
			c.close();
			System.out.println("Status:" + c.isClosed());
		}
		catch ( Exception e ) {
			System.out.println("ERROR: failed to load HSQLDB JDBC driver.");
			e.printStackTrace();
			return;
		}
	}
}