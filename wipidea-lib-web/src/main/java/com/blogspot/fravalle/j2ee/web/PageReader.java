/*
 * PageReader.java - jsptags (jsptags.jar)
 * Copyright (C) 2004
 * Source file created on 16-ott-2004
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [PageReader]
 * 2DO:
 *
 */

package com.blogspot.fravalle.j2ee.web;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.StringTokenizer;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.xml.sax.SAXParseException;

import com.blogspot.fravalle.j2ee.web.beans.PageBean;
import com.blogspot.fravalle.j2ee.web.business.WebApplicationError;
import com.blogspot.fravalle.j2ee.web.business.WebObjectException;
import com.blogspot.fravalle.lib.data.db.pooling.PoolManager;
import com.blogspot.fravalle.util.UtilConstants;
import com.blogspot.fravalle.util.db.ADb;
import com.blogspot.fravalle.util.db.DbLoader;
import com.blogspot.fravalle.util.db.DbLogger;
import com.blogspot.fravalle.util.xml.XmlImport;
import com.blogspot.fravalle.util.xml.XmlTransformer;
import com.blogspot.fravalle.util.xml.XmlUtils;


/**
 * @author antares
 */
public class PageReader extends BaseController {
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		this.INIT_BASE_XSL_ARCHIVE = config.getInitParameter( UtilConstants.CTX_PARAM_XSL_ARCHIVE_PATH );
		
		/* Inizializzazione del flusso db per i contenuti utilizzato da oggetti esterni quali taglibrary */
		poolMgr = PoolManager.getInstance();
		final String INIT_PROP_REF = this.getInitParameter( CTX_PARAM_DB_FLOW_CONTENT );
		PageReader.poolFlowContent = INIT_PROP_REF;
	}
	
	public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		/* Apertura connesione database per oggetti esterni appartenti a webcontext (jsp, taglib) */
		final String dbPool = PageReader.poolFlowContent; // poolFlowContent
		Connection conn = poolMgr.getConnection(dbPool);
        try {
    		// if (getServletContext().getAttribute(CTX_BEAN_DB_FLOW_CONTENT) == null) {
    			// conn = poolMgr.getConnection(dbPool);
    			getServletContext().setAttribute(CTX_BEAN_DB_FLOW_CONTENT, conn);
    		// }
        } catch ( Exception e ) {
        	e.printStackTrace();
		} finally {
			poolMgr.freeConnection(dbPool, conn);
		}
		
		/* Valorizzazione variabili di navigazione */
		PAGE = req.getPathInfo();
		URI = req.getRequestURI();
		REAL_CONTEXT = getServletContext().getRealPath("");
		/*
		 * switch ( ("/"+ActionPages.INFO).indexOf(PAGE) ) { case 1: break; case
		 * -1: break; default: break; }
		 */
		try {
			if (ActionPages.isValidAction(PAGE)) {
				if (ActionPages.isTargetAccepted(PAGE, "/" + ActionPages.TEST)) {
					FORWARD = "/web/test.jsp";
					ERROR_CODE = 0;
				} else if (ActionPages.isTargetAccepted(PAGE, "/" + ActionPages.TEST_ERROR)) {
					/* (PAGE.indexOf(ActionPages.TEST_ERROR) != -1) { */
					FORWARD = "/web/test.jsp";
					ERROR_CODE = 0;
					Class.forName("TestNomeClasseInesistente");
				} else {
					FORWARD = "/web" + PAGE + ".jsp";
					ERROR_CODE = 0;
					String p = "/" + ACTION;
					if (URI.lastIndexOf(p) != -1) { //sempre
						String fwPage = URI.substring(URI.lastIndexOf(p) + p.length(), URI.length());
						if (PAGE.indexOf(ActionPages.ROOT) != -1)
							fwPage = fwPage.substring(fwPage.lastIndexOf(ActionPages.ROOT) + ActionPages.ROOT.length(),
									fwPage.length());
						// subdirectory corrispondenti ai nomi dei todo
						if (new File(REAL_CONTEXT + "/web" + fwPage).exists()) {
							FORWARD = "/web" + fwPage;
							ERROR_CODE = 0;
							System.out.println("[DOMAIN]" + getClass().getPackage().getName());
							if (req.getParameter(UtilConstants.PAR_KEY) != null) {
								String[] key = new String[10];
								StringTokenizer st = new StringTokenizer(req.getParameter(UtilConstants.PAR_KEY), UtilConstants.PAR_KEY_SEPARATOR);
								int u = 0;
								while (st.hasMoreTokens()) {
									key[u] = (String)st.nextToken();
									u++;
								}
								
								String[] saPageStructure = new String[2];
								saPageStructure = this.buildDocument(key[0], key[1]); 
								
								if (saPageStructure[TOKEN_PAGEVIEW]==null) // se non è presente il rif al xsl utilizzo il nome della servlet mappata
									saPageStructure[TOKEN_PAGEVIEW] = getServletName();
								StringBuffer sXmlBuffer = new StringBuffer( XmlUtils.tagPage(saPageStructure[TOKEN_PAGEVIEW], saPageStructure[TOKEN_PAGECONTENT], req) );

								Object[] dataModel = new Object[]{sXmlBuffer,
										INIT_BASE_XSL_ARCHIVE + ActionPages.XSL_BASE_PATH + saPageStructure[TOKEN_PAGEVIEW] + ".xsl"};
								req.setAttribute(ActionPages.PAGE_CONTENT_NAME, (String)XmlTransformer.getInstance()
										.transform(dataModel));
							} else {
								if (req.getParameter(UtilConstants.PAR_XML_EXPORT_CONVERSION) != null) {
									String outputPath = req.getParameter(UtilConstants.PAR_XML_EXPORT_CONVERSION);
									try {
										XmlImport.xmlExportConversion(outputPath);
									} catch ( Exception e1 ) {
										e1.printStackTrace();
									}
									req.setAttribute(ActionPages.PAGE_CONTENT_NAME, (String)"<H2>Esportazione degli xml riuscita con successo.</H2"
											+ XmlImport.getLogEvents());
								} else if (req.getParameter(UtilConstants.PAR_DB_EXPORT) != null) {
									String keyValue = req.getParameter(UtilConstants.PAR_DB_EXPORT);
									try {
										XmlImport.dbExport();
									} catch ( Exception e1 ) {
										e1.printStackTrace();
									}
									req.setAttribute(ActionPages.PAGE_CONTENT_NAME, (String)"<H2>Esportazione dati db riuscita con successo.</H2"
											+ XmlImport.getLogRecords());
								}
								// page without querystring
								// FORWARD = "/web/appError.jsp";
								// ERROR_CODE = 20;
								// throw new WebObjectException(ActionPages.PAGE_CONTENT_EMPTY);
							}
						} else {
							FORWARD = "/web/errors/appError.jsp";
							ERROR_CODE = 25;
							req.setAttribute(ActionPages.BEAN_ERROR_LABEL, (String)"ERRORE DI CONFIGURAZIONE");
							req.setAttribute(ActionPages.BEAN_ERROR_DESC_SOLUTION,
									(String)"Subdirectory corrispondente al nome del [2DO] inesistente");
							//							WebApplicationError.setForwardError(e, req, res,
							// getClass().getName(), getServletName());
							// throw new WebObjectException(ActionPages.PAGE_CONTENT_EMPTY);
						}
					}
				}
				// da collegare a class.forname
				req.setAttribute(ActionPages.BEAN_PAGE_NAME, new PageBean(1));
			} else if (ActionPages.isValidExtension(URI)) {
				ERROR_CODE = 30;
				WebApplicationError.sendError(Constants.HTTP_ERROR_507, null, req, res, getClass().getName(),
						getServletName());
				return;
			} else {
				if (PAGE == null && ActionPages.isExtensionAccepted(PAGE)) {
					ERROR_CODE = 35;
					WebApplicationError.sendError(Constants.HTTP_ERROR_506, null, req, res, getClass().getName(),
							getServletName());
				} else {
					// esempio per scrivere stylesheet dinamicamente nel flusso
					// del link
					ERROR_CODE = 80;
					PrintWriter out = res.getWriter();
					res.setContentType("text/css");
					out
							.println("BODY	{ font-size: 20px; font-family: arial, verdana, arial, helvetica; font-weight: bold; color: indianred; }");
					out
							.println("A:visited	{ font-size: 10px; font-family: arial, verdana, arial, helvetica; font-weight: bold; color: navy; }");
					out
							.println("A:link		{ font-size: 20px; font-family: arial, verdana, arial, helvetica; font-weight: bold; color: gray; }");
					out
							.println("A:active	{ font-size: 15px; font-family: arial, verdana, arial, helvetica; font-weight: bold; color: coral; }");
					out
							.println("A:hover	{ font-size: 30px; font-family: arial, verdana, arial, helvetica; font-weight: bold; color: gold; }");
					out
							.println(".pageAuthor { font-size: 25px; font-family: verdana, arial, helvetica; font-weight: bold; color: gold; }");
				}
				return;
			}
		} catch ( ClassNotFoundException e ) {
			FORWARD = "/web/errors/error.jsp";
			ERROR_CODE = WebApplicationError.setForwardError(e, req, res, getClass().getName(), getServletName());
		} catch ( FileNotFoundException e ) {
			FORWARD = "/web/errors/error.jsp";
			ERROR_CODE = WebApplicationError.setForwardError(e, req, res, getClass().getName(), getServletName());
		} catch ( IOException e ) {
			FORWARD = "/web/errors/error.jsp";
			ERROR_CODE = WebApplicationError.setForwardError(e, req, res, getClass().getName(), getServletName());
		} catch ( NullPointerException e ) {
			FORWARD = "/web/errors/error.jsp";
			ERROR_CODE = WebApplicationError.setForwardError(e, req, res, getClass().getName(), getServletName());
		} catch ( WebObjectException e ) {
			FORWARD = "/web/errors/404.jsp";
			ERROR_CODE = WebApplicationError.setForwardError(e, req, res, getClass().getName(), getServletName());
		} catch ( SAXParseException e ) {
			FORWARD = "/web/errors/error.jsp";
			ERROR_CODE = WebApplicationError.setForwardError(e, req, res, getClass().getName(), getServletName());
		} catch ( Exception e ) {
			FORWARD = "/web/errors/error.jsp";
			ERROR_CODE = WebApplicationError.setForwardError(e, req, res, getClass().getName(), getServletName());
		} finally {
			// istruzioni raggiunte dal finally anche con return;
			String headers = "";
			for (Enumeration en = req.getHeaderNames(); en.hasMoreElements(); ) {
				String headerName = String.valueOf(en.nextElement());
				headers += headerName + ": " + req.getHeader(headerName) + "\r\n";
			}

			String sessionId = req.getRequestedSessionId();
			// aggiungere valid-page
			Object[] items = { DbLogger.getInsertSeed(), String.valueOf(PAGE), String.valueOf(FORWARD), String.valueOf(URI), String.valueOf(req.getQueryString()), String.valueOf(req.getContentLength()), req.getRemoteHost(), String.valueOf(sessionId), headers, String.valueOf(ERROR_CODE)};
			DbLogger.insertLog(
					getQuerySchema(DB_FLOW_STATS, getClass().getPackage().getName()+":SERVICE_INSERT_LOG"),
					items);
		}
		RequestDispatcher rd = this.getServletConfig().getServletContext().getRequestDispatcher(FORWARD);
		rd.forward(req, res);
	}
	
	public String getSeed() {
		return String.valueOf(getClass().hashCode());
	}
	
	
	private String[] buildDocument(final String id, final String areaKey) {
		
		String sPageContent = this.listItems(id, areaKey);
		
		final int isXmlOutput = 0, isPlainOutput = 1;
		String sqlXmlKey  = ADb.getQuerySchema(DB_FLOW_CONTENT, getClass().getPackage().getName()+":XMLDOCKEY");
		String[][] params = new String[1][2];
		params = new String[2][2];
		params[0][0] = "area";
		params[0][1] = String.valueOf(areaKey);
		params[1][0] = "id";
		params[1][1] = String.valueOf(id);
		String xmlKey = ADb.runQueryPlainPrepared(ADb.DB_FLOW_CONTENT, sqlXmlKey, params, isPlainOutput);
		
		String sPageView = this.listItemsView(id, areaKey, xmlKey);

		// TOKEN_PAGECONTENT, TOKEN_PAGEVIEW 
		return new String[]{sPageContent,sPageView};
	}
	
	private final String listItems(final String id, final String areaKey) {
		String INIT_BASE_XML_ARCHIVE = null;
//		INIT_BASE_XSL_ARCHIVE = getInitParameter( CTX_PARAM_XSL_ARCHIVE_PATH );

		final int isXmlOutput = 0, isPlainOutput = 1;
		
		final String dbPool = poolFlowContent;
		String sContent = null;
		String sMetaContent = null;
		boolean bResource = false;
		Connection conn = poolMgr.getConnection(dbPool);
		
		/* 2DO: Con metodo apposito in ADb ritornare bean apposito contentente i dati */
		
		String sqlXmlarchive = ADb.getQuerySchema(DB_FLOW_CONTENT, getClass().getPackage().getName()+":XMLARCHIVE");
		String[][] params = new String[1][2];
		params[0][0] = "area";
		params[0][1] = String.valueOf(areaKey);
		INIT_BASE_XML_ARCHIVE = ADb.runQueryPlainPrepared(ADb.DB_FLOW_CONTENT, sqlXmlarchive, params, isPlainOutput);
		/* if (INIT_BASE_XML_ARCHIVE==null)
			recupera da impostazioni globali
		*/
		String sqlXmldoc = ADb.getQuerySchema(DB_FLOW_CONTENT, getClass().getPackage().getName()+":XMLDOCNAME");
		params = new String[2][2];
		params[0][0] = "area";
		params[0][1] = String.valueOf(areaKey);
		params[1][0] = "id";
		params[1][1] = String.valueOf(id);
		String xmlDocname = ADb.runQueryPlainPrepared(ADb.DB_FLOW_CONTENT, sqlXmldoc, params, isPlainOutput);
		// ATTENZIONE: sembra che il driver passi un valore "null" in caso di assenza valore colonna, indagare meglio
		if (xmlDocname != null)
			bResource = true;

		String sqlXmldocmeta = ADb.getQuerySchema(DB_FLOW_CONTENT, getClass().getPackage().getName()+":XMLDOCMETA");
		params = new String[2][2];
		params[0][0] = "area";
		params[0][1] = String.valueOf(areaKey);
		params[1][0] = "id";
		params[1][1] = String.valueOf(id);
		sMetaContent = ADb.runQueryPlainPrepared(ADb.DB_FLOW_CONTENT, sqlXmldocmeta, params, isXmlOutput);
		
		try {
			if (bResource) {
				
				String fPath = xmlDocname;
				if (!fPath.startsWith("/")) {
					fPath = INIT_BASE_XML_ARCHIVE + fPath;
				}
				// recupero documento xml da filesystem
				// classe di bridge per conversione xml a standard catalogue/item
				File f = new File(fPath);
				if (f.exists()) {
					StringBuffer sXmlBuffer = new StringBuffer();
					FileInputStream fis = new FileInputStream(f);
					BufferedInputStream bis = new BufferedInputStream(fis);
					BufferedReader br = new BufferedReader(new InputStreamReader(bis));
					while (br.ready()) {
						String str = br.readLine();
						if (str != null) {
								sXmlBuffer.append(str + "\r\n");
						}
					}
					// in presenza di un contenuto xml multiplo eseguire recupero blocco tramite identificatore
	
					// recupero meta dati da db
					sXmlBuffer.append(sMetaContent + "\r\n");
	
					br.close();
					bis.close();
					fis.close();
					sContent = "<xml><catalogue><item>" + sXmlBuffer.toString() + "</item></catalogue></xml>" ;
				}
			} else {
				sContent = "<xml><catalogue><item>" + sMetaContent + "</item></catalogue></xml>" ;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			poolMgr.freeConnection(dbPool, conn);
		}

		return (sContent != null) ?sContent :ActionPages.PAGE_CONTENT_EMPTY;
	}
	
	
	private final String listItemsView(final String id, final String areaKey, final String xmlKey) {
		final String dbPool = ADb.poolFlowContent;
		String sName = null;
		String sView = null;

		/* 2DO: eseguire query precompilata con cui recuperare documento xml dei dati
		 * e generare metodo di lettura dati
		 */
		
		final int isXmlOutput = 0, isPlainOutput = 1;
		String sqlXmlKey  = ADb.getQuerySchema(DB_FLOW_CONTENT, getClass().getPackage().getName()+":XMLDOCMAP");
		String[][] params = new String[1][2];
		params = new String[3][2];
		params[0][0] = "area";
		params[0][1] = String.valueOf(areaKey);
		params[1][0] = "id";
		params[1][1] = String.valueOf(id);
		params[2][0] = "xmlkey";
		params[2][1] = String.valueOf(xmlKey);
		HashMap hm = ADb.runQueryPlainPreparedRow(ADb.DB_FLOW_CONTENT, sqlXmlKey, params, isPlainOutput);
		sName = (String)hm.get("servletname");
		sView = (String)hm.get("servletmap");
		
		/*
		Connection conn = poolMgr.getConnection(dbPool);
		Statement st = null;
		try {
			st = conn.createStatement();
			// 2DO: query convertibile solo con ridisegno della routine
			// WebConfig.getQuerySchema(WebConfig.MAIN_CONTENT_AREA, 3);
			// 
			ResultSet rs = st.executeQuery("SELECT " +
					" servletName, servletMap " +
					" FROM xmlmodels WHERE id = " + id
					+ " AND area = " + areaKey
					+ " AND xmlKey = '" + xmlKey + "'"
					);
			while (rs.next()) {
				 sName = rs.getString(1);
				 sView = rs.getString(2);
			}
			rs.close();
			st.close();

		}
		catch ( Exception ex ) {
			ex.printStackTrace();
		}
		finally {
			poolMgr.freeConnection(dbPool, conn);
		}
		*/
		return (sView != null) ?sView :sName;
	}	
	
}