/*
 * XmlImport.java - antares-web (antares-web.jar)
 * Copyright (C) 2005
 * Source file created on 27-set-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [XmlImport]
 * 2DO:
 *
 */

package com.blogspot.fravalle.lib.data.xml;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.blogspot.fravalle.lib.monitor.Monitor;
import com.blogspot.fravalle.util.Constants;

/**
 * @author antares
 */
public class XmlImport {

	private static StringBuffer logEvents = new StringBuffer(1024);
	private static StringBuffer logRecords = new StringBuffer(1024);
	
	private XmlImport() throws Exception {
		this("c://temp//doc.xml", "c://temp//docout.xml");
	}
	
	private XmlImport(String sFileIn, String sFileOut) throws SaxApplicationException {
		super();
		// String query = "SELECT titolo, autore, idart, corpoart FROM articoli GROUP BY corpoart;";
		// 1- recupero dati da db su bridge jdbc
		// 2- scrivo log dati su file
		// 3- eseguo export su bridge jdbc
		// 4- leggo xml riportato in ogni record e ne scrivo contenuto su nuovo xml
		//    scrivendo il testo in codifica ISO-8859-1
		// 5- chiudo connessione
		
		// lettura xml e riscrittura
		XmlExport xmlExport = new XmlExport(sFileIn);
		int status = 0;
		String messageSatus = "";

		status = xmlExport.exportXmlNodes(sFileOut);
		if (status == 0)
			messageSatus = "-- [?] - Operation aborted on process init.\r\n";
		else if (status > 0)
			messageSatus = "-- [x] - Operation on file {" + sFileIn + "} concluded.\r\n";
		else
			messageSatus = "-- [!] - Operation on file {" + sFileIn + "} aborted.\r\n";
		logEvents.append(messageSatus);
	}
	
	public static String getLogEvents() {
		String message = "";
		if (logEvents != null)
			message = "<pre>" + logEvents.toString() + "</pre>";
		else
			message = "<pre>Empty log</pre>";
		
		logEvents = new StringBuffer(1024);
		return message;
	}
	
	public static String getLogRecords() {
		String records = "";
		if (logRecords != null)
			records = "<pre>" + logRecords.toString() + "</pre>";
		else
			records = "<pre>Empty log</pre>";
		
		logRecords = new StringBuffer(1024);
		return records;
	}
	
	public static String getFileOut(String sName) {
		if (sName.indexOf("/") != -1) {
			sName = sName.substring(sName.lastIndexOf("/")+1,sName.length());
		}
		return sName;
	}
	
	private static String replaceEntity(String s) {
		String out = "";
		if ( System.getProperties().getProperty("java.version").indexOf("1.4") != -1 ) {
			out = s;
			String cutter = "[������'��]";
			if (s.matches( ".*"+cutter+".*" )) {
				out = s.replaceAll("[�]","&amp;agrave;");
				out = out.replaceAll("[�]","&amp;igrave;");
				out = out.replaceAll("[�]","&amp;ugrave;");
				out = out.replaceAll("[�]","&amp;ograve;");
				out = out.replaceAll("[�]","&amp;egrave;");
				out = out.replaceAll("[�]","&amp;eacute;");
				out = out.replaceAll("[']","&amp;#39;");
				out = out.replaceAll("[�]","&amp;laquo;");
				out = out.replaceAll("[�]","&amp;raquo;");
			}
		} else {
			// sostituzione apostrofo
			out = s;
		}
		return out;
	}
	
	public static final void main(String args[]) {
		// System.out.println(replaceEntity("La gravit� � una forza pi� forte dell'assenza, del perch� ne parler� �Cosimo de Cosmis�"));
		
		try {
			/*
			String jdbcDriver 	= "org.postgresql.Driver";
			String jdbcUrl 		= "jdbc:postgresql://db.orion.lan:5432/xmlworld";
			// "madsql", "onemad"
			
			Class.forName(jdbcDriver);
			Connection c = DriverManager.getConnection(jdbcUrl, "madsql", "onemad");
			
			Statement stm = c.createStatement();
			ResultSet rs = stm.executeQuery("select * from xmlmodels");
			while(rs.next()) {
				System.out.print( rs.getString(1) );
				System.out.print( "|" + rs.getString(2) );
				System.out.print( "|" + rs.getString(3) );
				System.out.print( "\r\n" );
			}
			
			System.out.println( c.isClosed() );
			
			c.close();
			*/
			/*
			dbExport();
			String result = getLogRecords();
			File fOut = new File( "c://temp//out//records.txt" );	
			FileOutputStream fos = new FileOutputStream(fOut);
			fos.write(result.toString().getBytes());
			fos.flush();
			fos.close();
			*/
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public static void dbExport() throws Exception {
		String sFileIn = "", sFileOut = "";
		logRecords = new StringBuffer(1024);
		Class.forName("org.gjt.mm.mysql.Driver");
		Connection c = DriverManager.getConnection("jdbc:mysql://db.orion.lan:3306/archivio",
				"madsql", "onemad");
		try {
			
			Statement stm = c.createStatement();
			ResultSet rs = stm.executeQuery("select testata, titolo, autore, tema, bookmark, idart, posart, dataart, corpoart, istech, hits from articoli -- limit 5");
			
			while(rs.next()) {
				try {
					String recTestata, recTitolo, recAutore, recTema, recBookmark, recIdart, recPosart, recDataart, recCorpoart, recIstech, recHits;
					recTestata = replaceEntity( rs.getString(1) );
					recTitolo = replaceEntity( rs.getString(2) );
					recAutore = replaceEntity( rs.getString(3) );
					recTema = replaceEntity( rs.getString(4) );
					recBookmark = rs.getString(5);
					recIdart = rs.getString(6);
					recPosart = rs.getString(7);
					recDataart = rs.getString(8);
					recCorpoart = rs.getString(9);
					recIstech = rs.getString(10);
					recHits = rs.getString(11);
					
					// 2DO: conversione caratteri proibiti in colonne record da esportare in log, lettere accentate, accenti non ascii, virgolette
					
					String queryInsert = "INSERT INTO XMLMODELS VALUES('"+recIdart+"','"+recPosart+"','"+recTitolo+"','"+recTestata+"','read','read','"+getFileOut(recCorpoart)+"','"+recAutore+"','"+recTema+"','press',1,'"+recDataart+"','custom')";
					logRecords.append(queryInsert+"\r\n");

				} catch (Exception e) {
					Monitor.debug(e);
				} finally {
					Runtime.getRuntime().gc();
				}
			}
			
			rs.close();
			stm.close();
			
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			c.close();
			Runtime.getRuntime().gc();
		}
	}
	
	public static void xmlExportConversion(String exportPath) throws Exception {
		String sFileIn = "", sFileOut = "";
		Class.forName("org.gjt.mm.mysql.Driver");
		Connection c = DriverManager.getConnection("jdbc:mysql://db.orion.lan:3306/archivio",
				"madsql", "onemad");
		try {
			
			Statement stm = c.createStatement();
			ResultSet rs = stm.executeQuery("select corpoart, idart, posart from articoli group by corpoart");
			
			while(rs.next()) {
				sFileIn = rs.getString(1);
				String detailExportId = "[Id Art]:" + rs.getString(2) + " [Pos Art]:" + rs.getString(3);
				sFileOut = exportPath + getFileOut(sFileIn);
				// isolamento delle eccezioni parser SAX per proseguire nell'elaborazione dei documenti
				try {
					Object o = new XmlImport( sFileIn, sFileOut );
				} catch (Exception e) {
					Monitor.log(Constants._logDefaultDef+"{XML-PATH}: --> " + sFileIn);
					Monitor.log(Constants._logDefaultDef+"{ERROR-ON}: --> " + detailExportId);
					Monitor.log(Constants._logDefaultDef+"{MESSAGE-DETAIL}: --> " + e.getMessage());
				} finally {
					Runtime.getRuntime().gc();
				}
			}
			
			rs.close();
			stm.close();
			
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			c.close();
			Runtime.getRuntime().gc();
		}
	}
	
	private void importXml() {
		int statusError = 0;
		
		String textContainer = "";
		
		try {	
			
			String freadstr = "";
			freadstr = freadstr.substring( 0, freadstr.lastIndexOf(".")+1 );
			
			freadstr += "xml";
			
			String tagName = "ARTICLE";
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			Document doc = db.parse(new File(freadstr));
			
			int articlePos = (new Integer( 0 )).intValue();
			
			statusError = 0;
			
			NodeList allNodes = doc.getElementsByTagName(tagName);
			
			statusError = 1;
			
			Node articleNd = allNodes.item( (articlePos-1) );
			
			statusError = 2;
			
			NodeList articleNodes = articleNd.getChildNodes();
			
			statusError = 3;
			
			for (int e=0; e < articleNodes.getLength(); e++) {
				// if (articleNodes.item(e).getNodeName().indexOf("#") == -1) {
				if (articleNodes.item(e).getNodeName().equals("CONTENT")) {
					Monitor.log( Constants._logDefaultDef +nodeValue( articleNodes.item(e) ) );
					textContainer += nodeValue( articleNodes.item(e) );
				}
			}
		
		} catch (Exception ex) {
			Monitor.debug("<P><B>error: </B><I>"+ ex.getMessage() + "</I> <B>" + "[" + statusError + ", " + 0 + "]" + "</B></P>");
		}
	}
	
	private static String nodeValue(Node nd) {
		String value = "";
		NodeList ndl = nd.getChildNodes();
		if (ndl.getLength() == 1) {
			if (ndl.item(0).getNodeName().indexOf("#") != -1) 
				// value = "<B> NODO COMPLESSO - " + ndl.item(0).getParentNode().getNodeName() + "</B><BR>";
				value += ndl.item(0).getNodeValue();
			else
				// value = "<B> NODO COMPLESSO - " + ndl.item(0).getParentNode().getNodeName() + "</B><BR>";
				value += ndl.item(0).toString();
		} else if (ndl.getLength() > 1) {
			// value += "<B>NODO COMPLESSO - " + ndl.item(0).getParentNode().getNodeName() + "</B><BR>";
			for (int e=0; e < ndl.getLength(); e++)
				value += ndl.item(e).toString();
		}
		return value;
	}
	
}
