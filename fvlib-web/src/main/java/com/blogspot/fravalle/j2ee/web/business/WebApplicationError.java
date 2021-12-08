/*
 * WebApplicationError.java - antares-web (antares-web.jar)
 * Copyright (C) 2005
 * Source file created on 19-apr-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [WebApplicationError]
 * 2DO:
 *
 */

package com.blogspot.fravalle.j2ee.web.business;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.xml.sax.SAXParseException;

import com.blogspot.fravalle.j2ee.web.ActionPages;
import com.blogspot.fravalle.util.db.ADb;
import com.blogspot.fravalle.util.db.DbLogger;
import com.blogspot.fravalle.util.xml.XmlTransformer;
import com.blogspot.fravalle.util.xml.XmlUtils;


/**
 * @author antares
 */
public class WebApplicationError extends Exception {
	private final static String	baseModelPath	= "./"; //"/home/antares/public_html/web/"; 

	public WebApplicationError() {
		super("Default web application error.");
	}

	public static int setForwardError(Exception e, HttpServletRequest req, HttpServletResponse res,
			String servletName, String servletMapping) {
		int errorCode = 0;
		int errorCodeCounter = 0;
		// super();
		String sTypeName = "", sLabel = "", sDesc = "", errorDesc = "Variabile locale nel metodo non valorizzata.";
		if (e instanceof ClassNotFoundException) {
			errorCode = ++errorCodeCounter;
			sLabel = "Classe non presente nel classpath";
			sDesc = "Controllare la presenza della classe nel classpath.";
			errorDesc = e.getMessage();
			sTypeName = e.getClass().getName();
		} else if (e instanceof IOException) {
			errorCode = ++errorCodeCounter;
			sLabel = "Errore I/O";
			sDesc = "Controllare permessi scrittura o esistenza percorso directory.";
			errorDesc = e.getMessage();
			sTypeName = e.getClass().getName();
		} else if (e instanceof NullPointerException) {
			errorCode = ++errorCodeCounter;
			sLabel = "Null pointer exception";
			sDesc = "Controllare in stacktrace il metodo che manipola una variabile impostata a null. (Stupid! it's a null pointer)";
			errorDesc = e.getMessage();
			sTypeName = e.getClass().getName();
		} else if (e instanceof FileNotFoundException) {
			errorCode = ++errorCodeCounter;
			sLabel = "File Not Found";
			sDesc = "Controllare l'esistenza del file indicato nel percorso utilizzato.";
			errorDesc = e.getMessage();
			sTypeName = e.getClass().getName();
		} else if (e instanceof FileNotFoundException) {
			errorCode = ++errorCodeCounter;
			sLabel = "File Not Found";
			sDesc = "Controllare l'esistenza del file indicato nel percorso utilizzato.";
			errorDesc = e.getMessage();
			sTypeName = e.getClass().getName();
		} else if (e instanceof SQLException) {
			errorCode = ++errorCodeCounter;
			sLabel = "SQL error";
			sDesc = "Controllare la query.";
			errorDesc = e.getMessage();
			sTypeName = e.getClass().getName();
		} else if (e instanceof SAXParseException) {
			errorCode = ++errorCodeCounter;
			sLabel = "SAX error";
			sDesc = "Controllare il sorgente xml.";
			
			// normalize xml errors
			errorDesc = XmlUtils.replace(e.getMessage());
			// errorDesc = e.getMessage(); // i tag segnalati rompono
			sTypeName = e.getClass().getName();
			/*
		} else if (e instanceof NoSuchMethodException) {
			errorCode = ++errorCodeCounter;
			sLabel = "Method error";
			sDesc = "Controllare JDK, possibile metodo non implentato a causa di JDK non allineato.";
			errorDesc = e.getMessage(); */
		} else {
			errorCode = ++errorCodeCounter;
			sLabel = "Eccezione generica";
			sDesc = "Eccezione non tracciata, verificare codice.";
			errorDesc = e.getMessage();
			sTypeName = e.getClass().getName();
		}
		req.setAttribute(ActionPages.BEAN_ERROR_LABEL, (String)sLabel);
		req.setAttribute(ActionPages.BEAN_ERROR_DESC_SOLUTION, (String)sDesc);
		// connessione a db per recuperare possibile soluzione in base a class
		// exception
		req.setAttribute(ActionPages.BEAN_ERROR_OBJ, (Exception)e);
		req.setAttribute(ActionPages.BEAN_ERROR_SERVLET_SRC, (String)servletName);
		req.setAttribute(ActionPages.BEAN_ERROR_SERVLET_MAP, (String)"[" + servletMapping + "]");
		try {
			StringBuffer sXmlBuffer = new StringBuffer();
/*			sXmlBuffer.append(XmlUtils.tagError( "[CODE]:", String.valueOf(errorCode) ));
			sXmlBuffer.append(XmlUtils.tagError( "[LABEL]:", String.valueOf(sLabel) ));
			sXmlBuffer.append(XmlUtils.tagError( "[DESC]:", String.valueOf(errorDesc) ));
			sXmlBuffer.append(XmlUtils.tagError( "[SOLUTION]:", String.valueOf(sDesc) )); */
			sXmlBuffer.append(XmlUtils.tag( "TYPENAME", String.valueOf(sTypeName) ));
			sXmlBuffer.append(XmlUtils.tag( "CODE", String.valueOf(errorCode) ));
			sXmlBuffer.append(XmlUtils.tag( "LABEL", String.valueOf(sLabel) ));
			sXmlBuffer.append(XmlUtils.tag( "DESCRIPTION", String.valueOf(errorDesc) ));
			// sXmlBuffer.append(XmlUtils.tagHyperdbCData( "DESC", String.valueOf(errorDesc) ));
			
			sXmlBuffer.append(XmlUtils.tag( "SOLUTION", String.valueOf(sDesc) ));
			sXmlBuffer.append(XmlUtils.tag( "SERVLET-NAME", String.valueOf(servletName) ));
			sXmlBuffer.append(XmlUtils.tag( "SERVLET-MAPPING", String.valueOf(servletMapping) ));
			

			// !!! attenzione il primo oggetto deve essere stringbuffer altrimenti viene interpretato come percorso di file
			// rendere obbligatorio l'utilizzo di un metodo che generi lo skeleton di base
			req.setAttribute("docTitle", (String)"Web Application Error");
			Object[] dataModel = new Object[]{ new StringBuffer(XmlUtils.tagErrorListPage( String.valueOf(sXmlBuffer), req )), baseModelPath + ActionPages.XSL_BASE_PATH + servletMapping+"-error.xsl",
					XmlUtils.isCacheResult(false)};
			req.setAttribute(ActionPages.PAGE_CONTENT_NAME, (String)XmlTransformer.getInstance().transform(dataModel));
		} catch ( Exception ex ) {
			ex.printStackTrace();
		}

		try {
			Object[] items = { DbLogger.getInsertSeed(), sLabel, sDesc, servletName, servletMapping};
			DbLogger.insertLog(
					ADb.getQuerySchema(ADb.DB_FLOW_STATS, "com.blogspot.fravalle.j2ee.web.business.WebApplicationError"+":SETFORWARDERROR_INSERT_LOG"),
					items);
		}
		catch ( Exception ex ) {
			ex.printStackTrace();
		}
		return errorCode;
	}

	public static void sendError(Object[] httpErrorCode, Exception e, HttpServletRequest req, HttpServletResponse res,
			String servletName, String servletMapping) throws IOException {
		// super();
		req.setAttribute(ActionPages.BEAN_ERROR_LABEL, (String)httpErrorCode[ActionPages.HTTP_ERROR_TITLE].toString());
		// es: "Classe non presente nel classpath"
		req.setAttribute(ActionPages.BEAN_ERROR_DESC_SOLUTION, (String)httpErrorCode[ActionPages.HTTP_ERROR_DESC]
				.toString());
		// connessione a db per recuperare possibile soluzione in base a class
		// exception
		req.setAttribute(ActionPages.BEAN_ERROR_OBJ, (Exception)e);
		req.setAttribute(ActionPages.BEAN_ERROR_SERVLET_SRC, (String)servletName);
		req.setAttribute(ActionPages.BEAN_ERROR_SERVLET_MAP, (String)"[" + servletMapping + "]");
		res.sendError(((Integer)httpErrorCode[ActionPages.HTTP_ERROR_CODE]).intValue(),
				httpErrorCode[ActionPages.HTTP_ERROR_TITLE].toString());
		return;
	}
}