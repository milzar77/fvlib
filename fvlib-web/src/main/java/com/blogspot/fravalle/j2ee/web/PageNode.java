/*
 * PageNode.java - antares-web (antares-web.jar)
 * Copyright (C) 2005
 * Source file created on 8-ott-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [PageNode]
 * 
 * Classe adibita alla gestione di tutti i nodi del flow di contenuto
 * 
 * 2DO:
 *
 */

package com.blogspot.fravalle.j2ee.web;

import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.blogspot.fravalle.j2ee.web.business.WebApplicationError;
import com.blogspot.fravalle.lib.data.db.pooling.PoolManager;
import com.blogspot.fravalle.util.db.DbLogger;

/**
 * @author antares
 */
public class PageNode extends BaseController {
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		poolMgr = PoolManager.getInstance();
	}
	
	public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		// super.service(req, res);
		/* 1) Apertura connesione database per oggetti esterni appartenti a webcontext (jsp, taglib) */
		this.initNavConnection(poolFlowContent);
		
		/* 2) Imposto ed eseguo le operazioni specifiche della servlet */
        try {
        	this.setForward("/web/nodeContent.jsp");
    		this.setNavInfo(req);
    		this.setPageContent(req, "Il contenuto della pagina");
    		
    		// this.getServletContext().setAttribute(CTX_BEAN_DB_FLOW_CONTENT, conn);
    		// req.setAttribute(CTX_BEAN_DB_FLOW_CONTENT, conn);
    		
        } /*catch ( ClassNotFoundException e ) {
			FORWARD = "/web/errors/error.jsp";
			ERROR_CODE = WebApplicationError.setForwardError(e, req, res, getClass().getName(), getServletName());
		} catch ( FileNotFoundException e ) {
			FORWARD = "/web/errors/error.jsp";
			ERROR_CODE = WebApplicationError.setForwardError(e, req, res, getClass().getName(), getServletName());
		} catch ( IOException e ) {
			FORWARD = "/web/errors/error.jsp";
			ERROR_CODE = WebApplicationError.setForwardError(e, req, res, getClass().getName(), getServletName());
		} */ catch ( NullPointerException e ) {
			this.setForward("/web/errors/error.jsp");
			ERROR_CODE = WebApplicationError.setForwardError(e, req, res, getClass().getName(), getServletName());
		} /* catch ( WebObjectException e ) {
			FORWARD = "/web/errors/404.jsp";
			ERROR_CODE = WebApplicationError.setForwardError(e, req, res, getClass().getName(), getServletName());
		} catch ( SAXParseException e ) {
			FORWARD = "/web/errors/error.jsp";
			ERROR_CODE = WebApplicationError.setForwardError(e, req, res, getClass().getName(), getServletName());
		} */ catch ( Exception e ) {
			this.setForward("/web/errors/error.jsp");
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
			/* Traccio l'accesso recuperando la query di inserimento tramite
			 * il riferimento "package <domain>:label" */
			DbLogger.insertLog(
					getQuerySchema(DB_FLOW_STATS, getClass().getPackage().getName()+":SERVICE_INSERT_LOG"),
					items);
			// com.blogspot.fravalle.j2ee.web.PageNode
		}
		
		/* 3) Inoltro il forward della servlet */
		RequestDispatcher rd = this.getServletConfig().getServletContext().getRequestDispatcher(this.getForward());
		rd.forward(req, res);
		
		/* 4) Rilascio la connessione del database pool */
		this.releaseNavConnection(poolFlowContent);
	}
}
