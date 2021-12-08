/*
 * XmlExport.java - antares-web (antares-web.jar)
 * Copyright (C) 2005
 * Source file created on 28-set-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [XmlExport]
 * 2DO:
 *
 */

package com.blogspot.fravalle.util.xml;

/**
 * @author antares
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * @author antares
 * 
 * Classe di conversione contenuti xml CDATA in cui sia presente formattazione HTML nel testo
 * 
 */
public class XmlExport {
	Document	document;
	int			scan	= 1;
	
	public XmlExport(String docPath) throws SaxApplicationException {
		super();
		// factory builder
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setCoalescing(true);
		// factory.setValidating(true);
		// factory.setNamespaceAware(true);
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(new File(docPath));
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
	}

	protected int exportXmlNodes(String sFileOut) {
		StringBuffer sb = new StringBuffer(1024);
		NodeList nl = document.getChildNodes();
		int c = scan++ ;
		for (int i = 0; i < nl.getLength(); i++ ) {
			Node n = nl.item(i);
			if (n.toString().startsWith("<XML>")) {
				// System.out.println(n.toString());
				sb.append(n.toString());
			}
			/* utile in caso di export selettivo	
			if (n.hasChildNodes())
				printNodeList(n.getChildNodes());
			*/
		}

		File fOut = new File( sFileOut );	
		try {
			FileOutputStream fos = new FileOutputStream(fOut);
			fos.write(sb.toString().getBytes());
			fos.flush();
			fos.close();
		}
		catch ( FileNotFoundException e ) {
			e.printStackTrace();
			return -1;
		}
		catch ( IOException e ) {
			e.printStackTrace();
			return -1;
		}
		
		return 1;
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
	
	/*
	public static void main(String[] args) throws SaxApplicationException {
		String docPath = "";
		if (args.length == 0)
			docPath = "..//XMLTestProject//document.xml";
		else docPath = args[0];
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
	*/
}