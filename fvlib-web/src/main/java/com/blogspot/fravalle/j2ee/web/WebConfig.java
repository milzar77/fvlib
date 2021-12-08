/*
 * WebConfig.java - antares-web (antares-web.jar)
 * Copyright (C) 2005
 * Source file created on 1-ott-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [WebConfig]
 * 2DO:
 *
 */

package com.blogspot.fravalle.j2ee.web;

import java.io.IOException;
import java.sql.Connection;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.blogspot.fravalle.lib.data.db.pooling.PoolManager;

/**
 * @author antares
 */
public class WebConfig extends BaseController {
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		// this.INIT_BASE_XSL_ARCHIVE = config.getInitParameter( UtilConstants.CTX_PARAM_XSL_ARCHIVE_PATH );
		/* Inizializzazione del flusso db per i contenuti utilizzato da oggetti esterni quali taglibrary */
		poolMgr = PoolManager.getInstance();
		String INIT_PROP_REF = this.getInitParameter( CTX_PARAM_DB_FLOW_CONFIG );
		WebConfig.poolFlowConfig = INIT_PROP_REF;
		this.configDbFlow();
	}
	
	
	public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		/* Apertura connesione database per oggetti esterni appartenti a webcontext (jsp, taglib) */
		FORWARD = "/web/config/config.jsp";
		final String dbPool = WebConfig.poolFlowConfig;
		Connection conn = poolMgr.getConnection(dbPool);
        try {
    		// if (getServletContext().getAttribute(CTX_BEAN_DB_FLOW_CONTENT) == null) {
        	// pulizia della connesione predefinita dei contenuti
    			this.getServletContext().setAttribute(CTX_BEAN_DB_FLOW_CONTENT, conn);
    		// }
        } catch ( Exception e ) {
        	e.printStackTrace();
		} finally {
			poolMgr.freeConnection(dbPool, conn);
		}
		RequestDispatcher rd = this.getServletConfig().getServletContext().getRequestDispatcher(FORWARD);
		rd.forward(req, res);
	}
	
	/**
	 * Mantenuto per evitare conflitti nel caricamento automatico del servlet container
	 */
	public WebConfig() {
		super();
	}
	
	public String getSeed() {
		return String.valueOf(getClass().hashCode());
	}
}
