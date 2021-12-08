/*
 * DbLoader.java - antares (antares.jar)
 * Copyright (C) 2005
 * Source file created on 26-mar-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [DbLoader]
 * 2DO:
 * 
 * Notes:
 * Tutte le costanti con prefisso CTX provengono dall'interfaccia UtilConstants
 * 
 */

package com.blogspot.fravalle.util.db;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Enumeration;
import javax.servlet.ServletConfig;
import javax.servlet.*;
import javax.servlet.http.*;
import org.xml.sax.SAXParseException;

import com.blogspot.fravalle.j2ee.web.ActionPages;
import com.blogspot.fravalle.lib.data.db.pooling.PoolManager;
import com.blogspot.fravalle.util.xml.XmlTransformer;
import com.blogspot.fravalle.util.xml.XmlUtils;


/**
 * @author antares
 */
public class DbLoader extends ADb {
	
	private String				ACTION			= "";
	private String				PAGE			= "";
	private final boolean		LOG_WEB			= true;
	private final boolean		LOG_CONSOLE		= false;

	private static String		INIT_BASE_XSL_ARCHIVE= "/";
	private static String		INIT_BASE_XML_ARCHIVE= "/";
	
	private String				suffixView		= null;
	// private final static String hashCodeSeed = "0";
	HttpServletRequest rq = null;

	protected void vmInfo() {
		for (Enumeration e = System.getProperties().keys(); e.hasMoreElements();) {
			String s = String.valueOf( e.nextElement() );
			System.out.println( s +"\t\t="+ System.getProperties().getProperty(s));
			// java.vm.version , java.runtime.version , java.version
		}
	}
	
	protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		super.service(req, res);
		req.setAttribute("docTitle", (String)"[" + String.valueOf(req.getParameter("table")) + "] - Hyper ADb");
		this.rq = req;

		PrintWriter out = res.getWriter();
		// caricamento delle azioni
		ACTION = req.getServletPath();
		PAGE = req.getPathInfo();
		if (PAGE != null) {
			if ("/info".equals(PAGE)) {
				res.setContentType("text/plain");
				// info(req, res);
			} else if ("/reload".equals(PAGE)) {
				res.setContentType("text/plain");
				databaseInit(out, LOG_WEB);
			} else if ("/list".equals(PAGE)) {
				res.setContentType("text/html");
				try {
					StringBuffer sContent = new StringBuffer();
					if (req.getParameter("table") == null) {
						sContent.append( listLog( getQuerySchema(DB_FLOW_STATS, getClass().getName()+":SERVICE_LIST_LOG") ) );
					} else {
						// 2DO: WebConfig.getQuerySchema = query convertibile solo con ridisegno della routine
						sContent.append( listLog("SELECT * FROM " + req.getParameter("table")) );
					}
					
					Object[] dataModel = new Object[]{sContent,
							INIT_BASE_XSL_ARCHIVE + ActionPages.XSL_BASE_PATH + getServletName() + "-" + suffixView + ".xsl",
							XmlUtils.isCacheResult(false)};
					writeLog(out, (String)XmlTransformer.getInstance().transform(dataModel));
				} catch ( SAXParseException e ) {
					e.printStackTrace();
					System.out.println("Errore globale di trasformazione xml");
				} catch ( Exception e ) {
					e.printStackTrace();
				}
			} else if ("/delete".equals(PAGE)) {
				res.setContentType("text/plain");
				try {
					if (req.getParameter("table") == null) {
						executeQuery( getQuerySchema(DB_FLOW_CONTENT, getClass().getName()+":SERVICE_DELETE_XSLRESULTS") );
						executeQuery("DELETE * FROM LOGS");
						executeQuery("DELETE * FROM ERRORS");
						writeLog(out, "Tutti i dati delle tabelle temporanee sono stati eliminati");
					} else {
						executeQuery("DELETE * FROM " + req.getParameter("table"));
						writeLog(out, "Tutti i dati della tabella temporanea ["+req.getParameter("table")+"] sono stati eliminati");
					}
					
				} catch ( Exception e ) {
					e.printStackTrace();
				}
			}
			
			/*
			 * req.setAttribute(ActionPages.PAGE_CONTENT_NAME,
			 * (String)XmlTransformer.getInstance() .transform(dataModel));
			 *  
			 */
		} else {
			res.sendError(500, "Opzione non configurata o non esistente.");
		}
	}

	public void destroy() {
		super.destroy();
		poolMgr.release();
	}

	public void init(ServletConfig cfg) throws ServletException {
		super.init(cfg);
		// caricamento istanza del poolmanager
		boolean buildDb = false;
		poolMgr = PoolManager.getInstance();
		if (this.getInitParameter( CTX_PARAM_DB_FLOW_LOG ) != null) {
			try {
				this.databaseLoader(null, LOG_CONSOLE);
			}
			catch ( Exception e ) {
				e.printStackTrace(System.err);
			}
		}
	}

	public final static void executeQuery(final String sql) {
		final String dbPool = DbLoader.poolFlowStats;
		System.out.println(":" + poolMgr.hashCode() + ":" + "executing error log query: [" + sql + "]");
		Connection conn = poolMgr.getConnection(dbPool);
		Statement st = null;
		try {
			st = conn.createStatement();
			st.executeUpdate(sql);
			st.close();
		}
		catch ( Exception ex ) {
			ex.printStackTrace();
		}
		finally {
			poolMgr.freeConnection(dbPool, conn);
		}
	}

	public final String listLog(final String sql) {
		String dbPool = DbLoader.poolFlowStats;
		/* patch temporanea */
		if (sql.indexOf("xml")!=-1) dbPool = DbLoader.poolFlowContent;
		StringBuffer sXmlBuffer = new StringBuffer();
		String tableName = null;
		System.out.println(":" + poolMgr.hashCode() + ":" + "retrieving error log list: [" + sql + "]");
		Connection conn = poolMgr.getConnection(dbPool);
		Statement st = null;
		try {
			st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
				ResultSetMetaData rsmd = rs.getMetaData();
				int totalColumns = rsmd.getColumnCount();
				StringBuffer sRowBuffer = new StringBuffer();
				for (int i = 1; i <= totalColumns; i++ ) {
					String columnName = rsmd.getColumnName(i);
					String columnTypeName = rsmd.getColumnTypeName(i);
					int columnType = rsmd.getColumnType(i);
					tableName = rsmd.getTableName(i);
					Object columnData = (Object)rs.getObject(columnName);
					if (columnData == null) columnData = "<empty />";
					if (columnType == java.sql.Types.LONGVARCHAR)
						// xmlmodels mostra i sorgenti xml, xmlresults va in errore
						// sRowBuffer.append(XmlUtils.tagHyperdbCData(columnName, String.valueOf(columnData))); // I want print the raw content into xsl
						sRowBuffer.append(XmlUtils.tag(columnName, String.valueOf(columnData))); // I'm sure of well-formness of xml code
					else
						sRowBuffer.append(XmlUtils.tagHyperdbCData(columnName, String.valueOf(columnData)));
				}
				
				// check validity
				// if (!XmlUtils.validateXmlDocument( new StringBuffer("<test>"+String.valueOf(sRowBuffer)+"</test>") ) )
				String message = XmlUtils.validateXmlDocumentMessage( new StringBuffer("<test>"+String.valueOf(sRowBuffer)+"</test>") );
				if ( null != message )
					// sXmlBuffer.append( "<error>"+ActionPages.PAGE_CONTENT_EMPTY+"</error>" );
					sXmlBuffer.append( XmlUtils.tagException( ActionPages.XML_DB_ROW, sRowBuffer.toString(), message) );
				else
					sXmlBuffer.append( XmlUtils.tag( ActionPages.XML_DB_ROW, sRowBuffer.toString()) );
				
				
			}
			rs.close();
			st.close();
		} catch ( Exception ex ) {
			ex.printStackTrace();
		}
		finally {
			poolMgr.freeConnection(dbPool, conn);
		}
		if ("".equals(tableName)) tableName = "notable";
		suffixView = tableName;

		return XmlUtils.tagPage(tableName, sXmlBuffer.toString(), this.rq);
	}

	private void databaseLoader(PrintWriter out, boolean isWeblog) throws IOException {
		
		/* Caricamento dei parametri di inizializzazione applicazione */
		final String INIT_PROP_REF = this.getInitParameter( CTX_PARAM_DB_FLOW_LOG );
		DbLoader.poolFlowStats = INIT_PROP_REF;
		
		INIT_BASE_XML_ARCHIVE = getInitParameter( CTX_PARAM_XML_ARCHIVE_PATH );
		INIT_BASE_XSL_ARCHIVE = getInitParameter( CTX_PARAM_XSL_ARCHIVE_PATH );

	}

	private void databaseInit(PrintWriter out, boolean isWeblog) throws IOException {
		
		// caricamento dei parametri di inizializzazione applicazione
		final String INIT_PROP_REF = this.getInitParameter( CTX_PARAM_DB_FLOW_LOG );
		DbLoader.poolFlowStats = INIT_PROP_REF;
		final String INIT_SCHEMA = getInitParameter( CTX_PARAM_DB_SCHEMA_LOG );
		final String INIT_POPULATION = getInitParameter( CTX_PARAM_DB_POPULATION_LOG );
		final int INIT_MAX_STATEMENTS = Integer.parseInt(getInitParameter( CTX_PARAM_DB_MAXSTAT_LOG ));
		final String INIT_STATEMENT_DELIMITER = getInitParameter( CTX_PARAM_DB_STATDELIM_LOG );
		
		INIT_BASE_XML_ARCHIVE = getInitParameter( CTX_PARAM_XML_ARCHIVE_PATH );
		INIT_BASE_XSL_ARCHIVE = getInitParameter( CTX_PARAM_XSL_ARCHIVE_PATH );
		
		String mex = "";
		Connection conn = poolMgr.getConnection(INIT_PROP_REF);
		// controllo l'esistenza dei dati con cui popolare il db
		String[] dataLoader = {INIT_SCHEMA, INIT_POPULATION};
		int loadCounter = 0;
		File f = new File(getClass().getResource(dataLoader[1]).getFile());
		if (f.exists()) loadCounter = 1;
		// recupero dello schema e dei dati con cui popolare il db temporaneo
		for (int e = 0; e <= loadCounter; e++ ) {
			mex = "Currently loading [" + dataLoader[e] + "]";
			if (isWeblog)
				writeLog(out, mex);
			else writeLog(mex);
			try {
				InputStream is = getClass().getResourceAsStream(dataLoader[e]);
				BufferedInputStream bis = new BufferedInputStream(is);
				BufferedReader br = new BufferedReader(new InputStreamReader(bis));
				StringBuffer line[] = new StringBuffer[INIT_MAX_STATEMENTS];
				int i = 0;
				while (br.ready()) {
					String str = br.readLine();
					if (str != null) {
						if (str.startsWith(INIT_STATEMENT_DELIMITER)) {
							line[++i] = new StringBuffer(is.available());
						}
						line[i].append(str + "\r\n");
					}
				}
				br.close();
				bis.close();
				is.close();
				Statement st = null;
				st = conn.createStatement();
				for (int j = 0; j < line.length; j++ ) {
					if (line[j] != null) {
						String sql = conn.nativeSQL(line[j].toString());
						st.executeUpdate(sql);
						mex = "STATEMENT #" + j + ":\r\n" + sql;
						if (isWeblog)
							writeLog(out, mex);
						else writeLog(mex);
					} else {
						mex = "EMPTY STATEMENT";
						if (isWeblog)
							writeLog(out, mex);
						else writeLog(mex);
					}
				}
				st.close();
				mex = "STATUS: Temporary database loaded!";
				if (isWeblog)
					writeLog(out, mex);
				else writeLog(mex);
			}
			catch ( Exception ex ) {
				ex.printStackTrace();
			}
			finally {
				poolMgr.freeConnection(INIT_PROP_REF, conn);
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.blogspot.fravalle.util.ADb#getSeed()
	 */
	public String getSeed() {
		// 2DO Auto-generated method stub
		return String.valueOf(getClass().hashCode());
	}

}