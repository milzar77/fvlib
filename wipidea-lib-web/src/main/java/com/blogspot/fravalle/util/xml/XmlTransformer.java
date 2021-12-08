/*
 * XmlTransformer.java - jweevlib (jweevlib.jar)
 * Copyright (C) 2005
 * Source file created on 20-mar-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [XmlTransformer]
 * 2DO:
 *
 */

package com.blogspot.fravalle.util.xml;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.blogspot.fravalle.j2ee.web.ActionPages;
import com.blogspot.fravalle.j2ee.web.Constants;
import com.blogspot.fravalle.j2ee.web.business.WebObjectException;
import com.blogspot.fravalle.util.db.ADb;
import com.blogspot.fravalle.util.db.DbInserter;


/**
 * @author admin
 */
public class XmlTransformer implements Constants {
	// singleton object
	static private XmlTransformer	instance		= null;
	static private String[]			instanceSeed	= new String[2];
	private Document				document;
	private String					modelDocPath	= "data.xml";
	private StringBuffer			modelDocSrc		= null;
	private boolean					isModelDocCache	= false;
	private String					modelViewPath	= "data.xsl";

	/**
	 *  
	 */
	private XmlTransformer() {
		super();
		instanceSeed[0] = "" + new Date().getTime();
		instanceSeed[1] = "" + getClass().hashCode();
	}

	// utile per xml statici che non devono essere modificati fino al termine
	// dell'applicazione
	/*
	private XmlTransformer(Object[] dataModel) {
		super();
		instanceSeed[0] = "" + new Date().getTime();
		instanceSeed[1] = "" + getClass().hashCode();
		if (dataModel[XML_DOC_CODE] instanceof String) 
			this.modelDocPath = dataModel[XML_DOC_CODE].toString();
		else if (dataModel[XML_DOC_CODE] instanceof StringBuffer)
			this.modelDocSrc = new StringBuffer( dataModel[XML_DOC_CODE].toString() );
		
		this.modelViewPath = dataModel[XML_VIEW_CODE].toString();
	}
	*/

	// provare senza modificatore synchro
	static public synchronized XmlTransformer getInstance() {
		if (instance == null) instance = new XmlTransformer();
		return instance;
	}
	
	private void createXmlDocument() throws SAXParseException, SAXException, IOException, WebObjectException, Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);
		factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			if (builder.isValidating()) builder.setErrorHandler(null);
			File f = new File( this.modelDocPath );
			if (f.exists()) {
				this.document = builder.parse(f);
			} else {
				StringBuffer sourceXml = new StringBuffer();
				if (this.modelDocSrc == null) {
					// default document error
					String errorDesc = XmlUtils.tagError( "<desc>Percorso XML inesistente.</desc>" );
					String errorId = XmlUtils.tagError( "<id>1</id>" );
					
					sourceXml.append( XmlUtils.getDocumentProlog() );
					sourceXml.append( XmlUtils.tagErrorList( errorDesc + errorId ) );
				} else {
					if (this.modelDocSrc.toString().equals( ActionPages.PAGE_CONTENT_EMPTY )) {
						throw new WebObjectException(ActionPages.PAGE_CONTENT_EMPTY);
					}
					sourceXml = new StringBuffer( this.modelDocSrc.toString() );
					sourceXml.insert(0, XmlUtils.getDocumentProlog());
				}
				this.document = builder.parse(new ByteArrayInputStream(sourceXml.toString().getBytes()));
			}
	}

	public String transform(Object[] sXmlData) throws Exception {
		if (sXmlData[XML_DOC_CODE] instanceof String) 
			this.modelDocPath = sXmlData[0].toString();
		else if (sXmlData[XML_DOC_CODE] instanceof StringBuffer)
			this.modelDocSrc = new StringBuffer( sXmlData[0].toString() );
		
		// array index out of bound
		if (sXmlData.length == (XML_CACHE_CODE+1)) {
			if (sXmlData[XML_CACHE_CODE] instanceof Boolean) {
				this.isModelDocCache = ((Boolean)sXmlData[XML_CACHE_CODE]).booleanValue();
			}
		} else {
			this.isModelDocCache = true;
		}

		// XmlUtils.isCacheResult(true)

		this.modelViewPath = sXmlData[XML_VIEW_CODE].toString();
		
		return this.transform();
	}

	final protected String transform() throws Exception {
		
		this.createXmlDocument();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		boolean isError = false;
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			if (this.getClass().getResource("/")!=null)
				System.out.println("============DEBUGGING-CURRENT_PATH:" + this.getClass().getResource("/").getPath() );
			System.out.println("============DEBUGGING-FILE:" + new File(this.modelViewPath).getAbsolutePath() );
				
			File f = new File(this.modelViewPath);
			if (!f.exists()) {
				String newXslPath = this.modelViewPath.substring(0, this.modelViewPath.lastIndexOf("/") + 1)
						+ "default.xsl";
				f = new File(newXslPath);
			}
			Transformer t = tf.newTransformer(new StreamSource(f));
			ByteArrayInputStream bais = new ByteArrayInputStream(document.getDocumentElement().toString().getBytes());
			StreamSource streamSrc = new StreamSource(bais);
			t.transform(streamSrc, new StreamResult(baos));
			bais.close();
			streamSrc = null;
		}catch ( Exception e ) {
			e.printStackTrace();
			isError = true;
		}
		finally {
			try {
				baos.close();
			}
			catch ( IOException ioe ) {
				ioe.printStackTrace();
			}
			if (isModelDocCache) {
				// Object[] items = {DbLoader.getInsertSeed(), "001", String.valueOf(document.getDocumentElement()), String.valueOf(baos), String.valueOf(this.modelViewPath), String.valueOf(instanceSeed[1]), String.valueOf(document.hashCode())};
				// Object[] items = {DbLoader.getInsertSeed(), "001", String.valueOf("<![CDATA["+this.modelDocSrc.toString()+"]]>"), String.valueOf(baos), String.valueOf(this.modelViewPath), String.valueOf(instanceSeed[1]), String.valueOf(document.hashCode())};
				Object[] items = {DbInserter.getInsertSeed(), "001", String.valueOf( this.modelDocSrc.toString() ), String.valueOf(baos), String.valueOf(this.modelViewPath), String.valueOf(instanceSeed[1]), String.valueOf(document.hashCode())};
				
				
				/* DISABILITATA IN ATTESA DELLA GESTIONE CACHE */
				// if (!isError)
				if (false == true)
					DbInserter.insertItem(
						ADb.getQuerySchema(ADb.DB_FLOW_CONTENT, getClass().getName()+":TRANSFORM_INSERT_ITEM"),
						items);
			}
		}
		return baos.toString();
	}
	
	public static void main(String[] args) {
		try {
			System.out.println(getInstance().transform(new String[]{"src//dataw.xml", "src//view.xsl"}));
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
		

		
	}
}