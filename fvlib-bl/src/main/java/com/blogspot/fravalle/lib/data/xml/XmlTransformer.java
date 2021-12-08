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

package com.blogspot.fravalle.lib.data.xml;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.blogspot.fravalle.lib.monitor.Monitor;
import com.blogspot.fravalle.util.Constants;
import com.blogspot.fravalle.util.SettingRes;



/**
 * @author admin
 */
public class XmlTransformer implements XmlConstants {
	
	/**
	 * Default class logger set to "xslt"
	 */
	final static protected Logger logger = Logger.getLogger("xslt");
	
	// singleton object
	final public String XML_CACHE_RUNTIME_REF = "-";
	
	static private XmlTransformer	instance		= null;
	static private String[]			instanceSeed	= new String[2];
	private Document				document;
	private String					modelDocPath	= "data.xml";
	private StringBuffer			modelDocSrc		= null;
	private boolean					isModelDocCache	= false;
	private String					modelViewPath	= "data.xsl";
	
	private String[][] xslParams;

	
	/**
	 *  
	 */
	private XmlTransformer() {
		super();
		instanceSeed[0] = "" + new Date().getTime();
		instanceSeed[1] = "" + getClass().hashCode();
	}

	static public synchronized XmlTransformer getInstance() {
		if (instance == null) {
			init();
			instance = new XmlTransformer();
		} else {
			init();
		}
		return instance;
	}
	
	private void createXmlDocument() throws SAXParseException, SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
			ErrorHandler eh = new ErrorHandler() {
				public void error(SAXParseException parException)
						throws SAXException {
					logger.severe(parException.getMessage());
				}

				public void fatalError(SAXParseException parException)
						throws SAXException {
					logger.severe(parException.getMessage());
				}

				public void warning(SAXParseException parException)
						throws SAXException {
					logger.warning(parException.getMessage());
				}
			};
			if (builder.isValidating())
				builder.setErrorHandler(eh);
				//builder.setErrorHandler(null);
			//File f = new File( this.modelDocPath );
			File f = new File( clearIllegalChars(this.modelDocPath) );
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
					if (this.modelDocSrc.toString().equals( PAGE_CONTENT_EMPTY )) {
						Monitor.log( PAGE_CONTENT_EMPTY );
					}
					sourceXml = new StringBuffer( this.modelDocSrc.toString() );
					sourceXml.insert(0, XmlUtils.getDocumentProlog());
				}
				this.document = builder.parse(new ByteArrayInputStream(sourceXml.toString().getBytes()));
			}
	}

	final public Document getXmlDom() throws RuntimeException {
		if (instance.document==null)
			throw new RuntimeException("You must pre-load the xml source path");
		return instance.document;
	}
	
	public String transform(XmlSource source) {
		Object[] data = new Object[2];
		data[0]=source.getXml();
		data[1]=source.getXsl();
		this.modelDocPath="data.xml";
		this.modelDocSrc=null;
		return transform(data);
	}
	
	public String transform(Object[] sXmlData) {
		if (sXmlData[XML_DOC_CODE] instanceof String) 
			this.modelDocPath = sXmlData[XML_DOC_CODE].toString();
		else if (sXmlData[XML_DOC_CODE] instanceof StringBuffer)
			this.modelDocSrc = new StringBuffer( sXmlData[XML_DOC_CODE].toString() );
		/*else if (sXmlData[XML_DOC_CODE] instanceof Document)
			this.modelDocSrc = "";*/
		// array index out of bound
		if (sXmlData.length == (XML_CACHE_CODE+1)) {
			if (sXmlData[XML_CACHE_CODE] instanceof Boolean) {
				this.isModelDocCache = ((Boolean)sXmlData[XML_CACHE_CODE]).booleanValue();
			}
		} else {
			this.isModelDocCache = true;
		}

		if (sXmlData.length == (XSL_PARAMS_CODE+1)) {
			if (sXmlData[XSL_PARAMS_CODE] instanceof String[][]) {
				xslParams = (String[][])sXmlData[XSL_PARAMS_CODE];
			}
		} else {
			this.xslParams = null;
		}

		// XmlUtils.isCacheResult(true)

		this.modelViewPath = sXmlData[XML_VIEW_CODE].toString();

		String s = null;
		try {
			s =  this.transform();
		} catch (SAXParseException e) {
			Monitor.debug(e);
		} catch (SAXException e) {
			Monitor.debug(e);
		} catch (IOException e) {
			Monitor.debug(e);
		} catch (ParserConfigurationException e) {
			Monitor.debug(e);
		}
		return s;
	}

	final protected String transform() throws SAXParseException, SAXException, IOException, ParserConfigurationException {
		
		this.createXmlDocument();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		boolean isError = false;
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			//TODO: controllare validità degli altri metodi in cui è impiegato il metodo clearIllegalChars
			//File f = new File( this.modelViewPath );
			File f = new File( clearIllegalChars(this.modelViewPath) );
			if (!f.exists()) {
				String newXslPath = this.modelViewPath.substring(0, this.modelViewPath.lastIndexOf("/") + 1)
						+ "default.xsl";
				f = new File(newXslPath);
			}
			Transformer t = tf.newTransformer(new StreamSource( f ));
			
			if (this.xslParams != null) {
				for (int i = 0; i < xslParams.length; i++) {
					t.setParameter(xslParams[i][0],xslParams[i][1]);
				}
			}
			
			/* Il recupero di documenti xsl che importano crea eccezioni IO durante l'esecuzione da JAR */
			/* Eccezione generata dal caricamento della libreria gnujaxp.jar che ridefinisce l'intero package javax.xml.transform:
			 * "URI was not reported to parser for entity [document]"
			 * ; Line#: 1; Column#: 5
			 * javax.xml.transform.TransformerException: required character (found "g") (expected "<")
			 * 
			 * Nei file coinvolti non esistono errori di forma. La codifica ISO o UTF
			 * non influenza l'eccezione
			 */
			// Transformer t = tf.newTransformer(new StreamSource( getClass().getResourceAsStream(this.modelViewPath) ));
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
			/*
			if (isModelDocCache) {
				// Object[] items = {DbLoader.getInsertSeed(), "001", String.valueOf(document.getDocumentElement()), String.valueOf(baos), String.valueOf(this.modelViewPath), String.valueOf(instanceSeed[1]), String.valueOf(document.hashCode())};
				// Object[] items = {DbLoader.getInsertSeed(), "001", String.valueOf("<![CDATA["+this.modelDocSrc.toString()+"]]>"), String.valueOf(baos), String.valueOf(this.modelViewPath), String.valueOf(instanceSeed[1]), String.valueOf(document.hashCode())};
				Object[] items = {DbInserter.getInsertSeed(), "001", String.valueOf( this.modelDocSrc.toString() ), String.valueOf(baos), String.valueOf(this.modelViewPath), String.valueOf(instanceSeed[1]), String.valueOf(document.hashCode())};
				
				
				// DISABILITATA IN ATTESA DELLA GESTIONE CACHE
				if (false == true)
					DbInserter.insertItem(
						ADb.getQuerySchema(ADb.DB_FLOW_CONTENT, getClass().getName()+":TRANSFORM_INSERT_ITEM"),
						items);
			}
			*/
		}
		return baos.toString();
	}

	final public File volatileCache(StringBuffer documentSource, String beanRef) {
		File tempCache = null;
		String str = SettingRes.get("mdi.working.xslt.temp.dir") + SettingRes.get("mdi.working.xslt.temp.file.prefix");
		String ext = SettingRes.get("mdi.working.xslt.temp.file.extension");
		if (beanRef.startsWith(XML_CACHE_RUNTIME_REF)) {
			str += beanRef.substring( XML_CACHE_RUNTIME_REF.length() ) + "_";
			ext = SettingRes.get("mdi.working.xml.cache.extension");
		} else
			str += beanRef + "_";
		try {
			String testTmpDir = null;
			testTmpDir = System.getProperty("java.io.tmpdir");
			testTmpDir += "//" + SettingRes.get("mdi.working.xslt.temp.dir");
			File test = new File(testTmpDir);
			if (!test.exists())
				test.mkdirs();
				
			tempCache = File.createTempFile(str, ext);
			FileWriter fw = new FileWriter(tempCache);
			Monitor.log("Encoding: " + fw.getEncoding());
			fw.write(documentSource.toString());
			fw.flush();
			fw.close();
			
			tempCache.deleteOnExit();
			
		} catch (IOException e) {
			Monitor.debug(e);
		} finally {
			Runtime.getRuntime().gc();
			Runtime.getRuntime().freeMemory();
		}
		return tempCache;
	}
	
	final public void cacheBean(Object[] dataModel, String beanRef) {
		try {
			File xmlSource = this.volatileCache(new StringBuffer(dataModel[0].toString()), XML_CACHE_RUNTIME_REF+beanRef);
			
			String xslResult = this.transform(dataModel);
			File tempCache = this.volatileCache(new StringBuffer(xslResult), beanRef);
			
			File xmlCache = new File(
					tempCache.getParent() + File.separator
					+ beanRef
					+ SettingRes.get("mdi.working.xml.bean.extension")
					);
			
			Monitor.log("[CACHE][COMPARE]: " + xmlCache.compareTo(tempCache));
			
			if (xmlCache.exists()) {
					Monitor.log("[CACHE][OLD]: Local cache exists! It may differ from real data");
			} else {
				boolean isOk = false;
				
				isOk = xmlCache.createNewFile();
				if (isOk)
					Monitor.log("[CACHE][NEW]: Cache created!");
				else
					Monitor.log("[CACHE][PROBLEM]: Cache not created! check permissions");
				
				isOk = tempCache.renameTo(xmlCache);
				if (isOk)
					Monitor.log("[CACHE][TRANSFER]: Cache transferred!");
				else {
					tempCache.delete();
					isOk = tempCache.renameTo(xmlCache);
					if (isOk)
						Monitor.log("[CACHE][PROBLEM]: Cache cleaned and trasferred!");
					else
						Monitor.log("[CACHE][PROBLEM]: Cache not trasferred! check permissions");
					
				}
			}
		} catch (IOException e) {
			Monitor.debug(e);
		} finally {
			Runtime.getRuntime().gc();
		}
	}
	
	
	private String clearIllegalChars(String s) {
		if (s.indexOf("\n")!=-1)
			return s.replaceAll("\n","");
		return s;
	}
	
	public static void main(String[] args) {
		try {
			Monitor.log( Constants._logDefaultDef +getInstance().transform(new String[]{"src//dataw.xml", "src//view.xsl"}));
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	static final protected void init() {
		/*logger.warning("INIT: Overriding JDK 1.6.0 XSLT builder/factory settings based on CRIMSON");
		try {
			Class.forName(CFG_DocumentBuilderFactory_crimson);
			System.setProperty("javax.xml.parsers.DocumentBuilderFactory", CFG_DocumentBuilderFactory_crimson);
			System.setProperty("javax.xml.parsers.SAXParserFactory", CFG_SAXParserFactory_crimson);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			logger.severe("INIT: " + e.getMessage());
		}*/
	}
	
	protected void restore() {
		logger.warning("RESTORE: Restoring JDK 1.6.0 XSLT builder/factory settings based on JAXP");
		System.setProperty("javax.xml.parsers.DocumentBuilderFactory", CFG_DocumentBuilderFactory_jaxp);
		System.setProperty("javax.xml.parsers.SAXParserFactory", CFG_SAXParserFactory_jaxp);
	}
	
	public void destroy() {
		instance.restore();
	}
	
	@Override
	protected void finalize() throws Throwable {
		instance.destroy();
		super.finalize();
	}
	
}