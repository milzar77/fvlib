/*
 * BaseController.java - jweevlib (jweevlib.jar)
 * Copyright (C) 2005
 * Source file created on 23-apr-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [BaseController]
 * 2DO: creare nuova libreria per package basati su servlet.jar
 *
 */

package com.blogspot.fravalle.j2ee.web;

import java.sql.Connection;
import javax.servlet.http.HttpServletRequest;

import com.blogspot.fravalle.j2ee.web.beans.PageBean;
import com.blogspot.fravalle.util.UtilConstants;
import com.blogspot.fravalle.util.db.ADb;

/**
 * @author antares
 */
public abstract class BaseController extends ADb implements UtilConstants, Constants {

	protected String	ACTION			= "doc";
	protected String	PAGE			= "";
	protected String	URI				= "";
	protected String	REAL_CONTEXT	= "";
	protected String	FORWARD			= "";
	
	protected final static int TOKEN_PAGECONTENT = 0, TOKEN_PAGEVIEW = 1;

	/**
	 * <code>ERROR_CODE</code>: 
	 * Valore intero indicante lo stato di errore successivamente archiviato nella tabella ERRORS
	 * 2DO: implementare in interfaccia dedicata gli error code applicativi e caricarli in db 
	 */
	protected int		ERROR_CODE		= 0;
	
	/**
	 * <code>INIT_BASE_XSL_ARCHIVE</code>: 
	 * Directory base dei fogli di stile xsl
	 */
	protected String	INIT_BASE_XSL_ARCHIVE	= "/";
	
	protected Connection conn;
	
	protected String refPoolName;
	
	public java.lang.String getServletInfo() {
		return super.getServletInfo();
	}
	
	public void destroy() {
		super.destroy();
		poolMgr.release();
	}
	
	public String getSeed() {
		return String.valueOf(getClass().hashCode());
	}
	
	
	public void setForward(String sUrl) {
		this.FORWARD = sUrl;
	}
	
	public String getForward() {
		return this.FORWARD;
	}
	
	public void setNavInfo(HttpServletRequest req) {
		/* Valorizzazione variabili di navigazione */
		this.PAGE = req.getPathInfo();
		this.URI = req.getRequestURI();
		this.REAL_CONTEXT = getServletContext().getRealPath("");
	}
	
	public void initNavConnection(String poolRef) {
		/* Apertura connesione database per oggetti esterni appartenti a webcontext (jsp, taglib) */
		final String dbPool = poolRef;
		refPoolName = dbPool;
		conn = poolMgr.getConnection(dbPool);
        try {
    		// if (getServletContext().getAttribute(CTX_BEAN_DB_FLOW_CONTENT) == null) {
    			// conn = poolMgr.getConnection(dbPool);
    			this.getServletContext().setAttribute(CTX_BEAN_DB_FLOW_CONTENT, conn);
    			// this.getServletContext().setAttribute(CTX_BEAN_DB_FLOW, conn);
    		// }
        } catch ( Exception e ) {
        	e.printStackTrace();
		}/* finally {
			// poolMgr.freeConnection(dbPool, conn);
		}*/
	}

	public void releaseNavConnection(String poolRef) {
		/* if (poolRef.equalsIgnoreCase(refPoolName)) { */
			poolMgr.freeConnection(poolRef, conn);
		/* } else { // dividere il canale di configurazione da quello di contenuto
			poolMgr.freeConnection(poolRef, conn);
			poolMgr.freeConnection(refPoolName, conn);
		} */
	}
	
	public void releaseNavConnection() {
		poolMgr.freeConnection(refPoolName, conn);
	}
	
	public void setPageContent(String sContent) {
		this.getServletContext().setAttribute(ActionPages.PAGE_CONTENT_NAME, sContent);
		this.getServletContext().setAttribute(ActionPages.BEAN_PAGE_NAME, new PageBean(1));
	}
	
	public void setPageContent(HttpServletRequest req, String sContent) {
		req.setAttribute(ActionPages.PAGE_CONTENT_NAME, sContent);
		req.setAttribute(ActionPages.BEAN_PAGE_NAME, new PageBean(1));
	}
	
	// public abstract void init(ServletConfig config) throws ServletException;
	// rivedere metodo
	
}
