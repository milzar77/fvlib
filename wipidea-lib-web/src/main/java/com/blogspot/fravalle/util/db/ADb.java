/*
 * ADb.java - antares-web (antares-web.jar)
 * Copyright (C) 2005
 * Source file created on 30-mag-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [ADb]
 * 2DO:
 *
 */

package com.blogspot.fravalle.util.db;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import com.blogspot.fravalle.j2ee.web.business.WebObjectException;
import com.blogspot.fravalle.lib.data.db.pooling.PoolManager;
import com.blogspot.fravalle.util.UtilConstants;
import com.blogspot.fravalle.util.xml.XmlUtils;

/**
 * @author antares
 */
public abstract class ADb extends HttpServlet implements UtilConstants {
	
	protected static PoolManager	poolMgr;
	/**
	 * <code>poolFlowConfig</code>: variabile di pool accessibile da tutte le sottoclassi ereditate, flusso di dati utilizzato per la configurazione dinamica della webapplication
	 */
	protected static String		poolFlowConfig;
	/**
	 * <code>poolFlowStats</code>: variabile di pool accessibile da tutte le sottoclassi ereditate, flusso di dati utilizzato da tutte le operazioni statistiche (log, error tracing)
	 */
	protected static String		poolFlowStats;
	/**
	 * <code>poolFlowContent</code>: variabile di pool accessibile da tutte le sottoclassi ereditate, flusso di dati utilizzato da tutte le operazioni di recupero contenuto
	 */
	protected static String		poolFlowContent;
	
	/**
	 * 
	 * Metodo da implementare in ogni classe ereditata per recuperare un seme
	 * generato direttamente dal figlio ereditante
	 * 
	 * @return String
	 */
	abstract public String getSeed();
	// abstract public void executeQuery2(final String p);
	// abstract public void info(HttpServletRequest req, HttpServletResponse res) throws IOException;
	
	protected static final void printConsoleOut(final String message) {
		System.out.println(message);
	}

	protected static final void printConsoleOut(final Object[][] messages) {
		for (int i = 0; i < messages.length; i++) {
			String[] ar = (String[])messages[i][0];
			System.out.print( ar[CONSOLE_LOG_LABEL_START] );
			System.out.print( messages[i][1] );
			System.out.print( ar[CONSOLE_LOG_LABEL_END] );
		}
		System.out.println();
	}
	
	protected void writeLog(PrintWriter out, String messageLog) throws IOException {
		out.println(messageLog);
	}

	protected void writeLog(String messageLog) {
		System.out.println(messageLog);
	}

	public static String getInsertSeed(){
		return String.valueOf( ADb.class.hashCode() ) + DB_SEED_SEPARATOR + new java.util.Date().getTime();
	}
	
	public static String getInsertSeed(final String seed) {
		return String.valueOf( seed ) + DB_SEED_SEPARATOR + new java.util.Date().getTime();
	}

	/* Database methods */
	
	public final static String MAIN_CONFIGURATION_ID = "0", MAIN_CONFIGURATION_AREA = MAIN_CONFIGURATION_ID;
	public final static String AREA_CONFIGURATION = "0";
	public final static String AREA_CONTENT = "1";
	public final static String AREA_STATS = "2";
	
	public final static int DB_FLOW_CONFIG = 0, DB_FLOW_CONTENT = 1, DB_FLOW_STATS = 2; 
	
	public final static String MAIN_CONTENT_ID = "1", MAIN_CONTENT_AREA = "1";
	
	public final static String MAIN_STATS_ID = "1", MAIN_STATS_AREA = "2";
	
	public void configDbFlow() {
		poolFlowConfig = getDbFlowPools(DB_FLOW_CONFIG); //"config";
		poolFlowContent = getDbFlowPools(DB_FLOW_CONTENT); //"content";
		poolFlowStats = getDbFlowPools(DB_FLOW_STATS);; //"stats";
		writeLog("[CONTEXT INIT: "+getServletName()+"] Db Flow Pool name configured during init {"+getClass().getName()+"} process");
	}
	
	public static final String getDbFlow(final int area) {
		switch (area) {
			case DB_FLOW_CONFIG:
				return poolFlowConfig;
				// break;
			case DB_FLOW_CONTENT:
				return poolFlowContent;
			case DB_FLOW_STATS:
				return poolFlowStats;
			default:
				return poolFlowConfig;
		}
		 
	}
	
	
	public static final String getDbFlowPools(final int area) {
		final String dbPool = poolFlowConfig;
		String value = "";
		Connection conn = poolMgr.getConnection(dbPool);
        try {
    		PreparedStatement pst = conn.prepareStatement("SELECT DISTINCT dbPool FROM configuration WHERE area = '"+String.valueOf(area)+"' AND dbPool IS NOT NULL");
    		ResultSet rs = pst.executeQuery();
    		ResultSetMetaData rsmd = rs.getMetaData();
    		while (rs.next()) {
    			value = rs.getString(1);
    		}
    		rs.close();
    		pst.close();
        } catch ( Exception e ) {
        	e.printStackTrace();
		} finally {
			poolMgr.freeConnection(dbPool, conn);
		}
		return value;
	}
	
	public static final String getConfigParam(String area, String queryClassname, String key, String columnAlias) {
		final String dbPool = poolFlowConfig;
		String value = "";
		Connection conn = poolMgr.getConnection(dbPool);
        try {
    		PreparedStatement pst = conn.prepareStatement("select "+key+" as "+columnAlias+" from configuration where area = '"+area+"' and queryClassName='"+queryClassname+"'");
    		ResultSet rs = pst.executeQuery();
    		ResultSetMetaData rsmd = rs.getMetaData();
    		while (rs.next()) {
    			value = rs.getString(1);
    		}
    		rs.close();
    		pst.close();
        } catch ( Exception e ) {
        	e.printStackTrace();
		} finally {
			poolMgr.freeConnection(dbPool, conn);
		}
		return value;
	}
	
	public static final String getConfigParam(String area, int id, String key, String columnAlias) {
		final String dbPool = poolFlowConfig;
		String value = "";
		Connection conn = poolMgr.getConnection(dbPool);
        try {
    		PreparedStatement pst = conn.prepareStatement("select "+key+" as "+columnAlias+" from configuration where area = '"+area+"' and id="+id);
    		ResultSet rs = pst.executeQuery();
    		ResultSetMetaData rsmd = rs.getMetaData();
    		while (rs.next()) {
    			value = rs.getString(1);
    		}
    		rs.close();
    		pst.close();
        } catch ( Exception e ) {
        	e.printStackTrace();
		} finally {
			poolMgr.freeConnection(dbPool, conn);
		}
		return value;
	}
	
	public static final String getConfigParam(int area, int id, String key, String columnAlias) {
		final String dbPool = poolFlowConfig;
		String value = "";
		Connection conn = poolMgr.getConnection(dbPool);
        try {
    		PreparedStatement pst = conn.prepareStatement("select "+key+" as "+columnAlias+" from configuration where area = "+area+" and id="+id);
    		ResultSet rs = pst.executeQuery();
    		ResultSetMetaData rsmd = rs.getMetaData();
    		while (rs.next()) {
    			value = rs.getString(1);
    		}
    		rs.close();
    		pst.close();
        } catch ( Exception e ) {
        	e.printStackTrace();
		} finally {
			poolMgr.freeConnection(dbPool, conn);
		}
		return value;
	}
	
	
	public static final String[][] getQueryPreparedParameters(int area, int id) {
		final String dbPool = poolFlowConfig;
		String[][] aStruct = null;
		//  = new String[][]{}
		Connection conn = poolMgr.getConnection(dbPool);
        try {
    		PreparedStatement pst = conn.prepareStatement("select queryParameterNames, queryParameters from configuration where area = "+area+" and id="+id);
    		ResultSet rs = pst.executeQuery();
    		ResultSetMetaData rsmd = rs.getMetaData();
    		String value = "";
    		int u = 0;
    		while (rs.next()) {
    			value = rs.getString(1);
    			u = rs.getInt(2);
    		}
    		rs.close();
    		pst.close();
    		
    		StringTokenizer st = new StringTokenizer(value, ",");
    		int counter = 0;
    		aStruct = new String[st.countTokens()][2];
    		
    		while (st.hasMoreTokens()) {
    			aStruct[counter++][0] = st.nextToken();
    		}
        } catch ( Exception e ) {
        	e.printStackTrace();
		} finally {
			poolMgr.freeConnection(dbPool, conn);
		}
		return aStruct;
	}
	
	public static final String[] getConfigQueryPrepared(int area, int id) {
		final String dbPool = poolFlowConfig;
		String[] aStruct = null;
		//  = new String[][]{}
		Connection conn = poolMgr.getConnection(dbPool);
        try {
    		PreparedStatement pst = conn.prepareStatement("select queryParameterNames, queryParameters from configuration where area = "+area+" and id="+id);
    		ResultSet rs = pst.executeQuery();
    		ResultSetMetaData rsmd = rs.getMetaData();
    		String value = "";
    		int u = 0;
    		while (rs.next()) {
    			value = rs.getString(1);
    			u = rs.getInt(2);
    		}
    		rs.close();
    		pst.close();
    		
    		StringTokenizer st = new StringTokenizer(value, ",");
    		int counter = 0;
    		aStruct = new String[st.countTokens()];
    		// u = st.countTokens();
    		
    		while (st.hasMoreTokens()) {
    			aStruct[counter++] = st.nextToken();
    		}
        } catch ( Exception e ) {
        	e.printStackTrace();
		} finally {
			poolMgr.freeConnection(dbPool, conn);
		}
		return aStruct;
	}
	
	
	public static final String getQuerySchema(int area, String id) {
		final String par1 = "queryModel", par2 = "SchemaQuery";
		return getConfigParam(String.valueOf(area), id, par1, par2);
	} 
	
	public static final String getQuerySchema(int area, int id) {
		final String par1 = "queryModel", par2 = "SchemaQuery";
		return getConfigParam(String.valueOf(area), id, par1, par2);
	} 
	
	
	/**
	 * @param area
	 * @param id
	 * @param req
	 * @return
	 */
	public static final String setConfigParam(int area, int id, HttpServletRequest req) {
		final String dbPool = poolFlowConfig;
		String value = "";
		Connection conn = poolMgr.getConnection(dbPool);
		String updateQuery = getQuerySchema(area, id);
		String[] aParams = getConfigQueryPrepared(area, id);
		for (int i = 0; i < aParams.length; i++) {
			value += " <b>(" + (i+1) +")</b> " + aParams[i] + "<br/>\r\n";
		}
        try {

    		PreparedStatement pst = conn.prepareStatement(updateQuery);
    		for (int i = 0; i < aParams.length; i++) {
    			// necessario per trasformazione in minuscolo dei dati del catalogo db
    			String parValue = req.getParameter( aParams[i].toLowerCase() );
    			/* if (parValue == null)
    				parValue = req.getParameter( aParams[i] ); */
    			
    			if (parValue != null) {
    				if (parValue.equalsIgnoreCase("null") || parValue.equals("") || parValue.equals("&nbsp;")) {
    					pst.setNull( (i+1) , java.sql.Types.SMALLINT);
    				} else {
    					boolean isNumber = true;
    					try {
    						Integer.parseInt(parValue);
    					} catch (Exception e) {
    						isNumber = false;
    					}
    					if (isNumber == true)
    						pst.setInt( (i+1) , (int)Integer.parseInt(parValue));
    					else
    						pst.setObject( (i+1) , parValue);
    				}
    			} else {
    				throw new WebObjectException("[WEBOBJECTEXCEPTION] Parametro della richiesta non allineato a query precompilata o inesistente: " + aParams[i]);
    				/* WebApplicationError wAppError = new WebApplicationError();
    				int errorCode = wAppError.setForwardError(Exception e, HttpServletRequest req, HttpServletResponse res,
    						String servletName, String servletMapping)
    						"[WEBOBJECTEXCEPTION] Parametro della richiesta non allineato a query precompilata o inesistente: " + aParams[i]);
    				*/
    			}
    		}
    		if (pst.execute()) {
    			ResultSet rs = pst.getResultSet();
	    		while (rs.next()) {
	    			value = rs.getString(1);
	    		}
	    		rs.close();
    		} else {
    			// int t = pst.executeUpdate();
    			int t = pst.getUpdateCount();
    			value += "<hr/>Total updated/inserted record count:" + t + "<hr/>";
    			value += "<p align=\"right\"><a href=\"" + req.getRequestURI() + "\">Ritorna</a></p><hr/>";
    		}

    		pst.close();
        } catch ( SQLException e ) {
        	e.printStackTrace();
		} catch ( WebObjectException e ) {
			e.printStackTrace();
		} finally {
			poolMgr.freeConnection(dbPool, conn);
		}
		return value;
	}
	
	
	/**
	 * 
	 * Metodo impiegato per recuperare un valore singolo (select), oppure per impostare più valori (update, insert) 
	 * 
	 * @param area
	 * Identificatore dell'area (chiave di gruppo)
	 * @param id
	 * Identificatore dell'elemento
	 * @param req
	 * Richiesta http contenente i parametri da utilizzare nel completamento della
	 * query precompilata
	 * @return
	 * Stringa del valore singolo selezionato, oppure testo indicante il risultato della modifica
	 */
	public static final String runQueryPrepared(int area, int id, HttpServletRequest req) {
		/*
		HttpServletRequest req = null;
		String[][] params = null;
		if (paramsContainer instanceof HttpServletRequest)
			req = (HttpServletRequest)paramsContainer;
		else
			params = (String[][])paramsContainer;
		 */
		String dbPool = poolFlowConfig;
		String value = "";
		Connection conn = poolMgr.getConnection(dbPool);
		String updateQuery = getQuerySchema(area, id);
		
		String[][] aParams = getQueryPreparedParameters(area, id);
		for (int i = 0; i < aParams.length; i++) {
			value += "(" + i +")" + aParams[i][0] + "<br/>\r\n";
		}
		value += "<hr/>";
		poolMgr.freeConnection(dbPool, conn);
        try {
        	
        	/* Blocco l'esecuzione della query se rappresenta un selezione multicolonna */
    		String checkQuery = updateQuery.toLowerCase();
    		if (checkQuery.indexOf("select ") != -1) {
    			
    			// as funcRt
    			if (checkQuery.indexOf(" from ") == -1) {
    				if ( checkQuery.indexOf(" as rtfunc;") == -1 )
    					throw new WebObjectException("[WEBOBJECTEXCEPTION] La query indicata non contiente l'indicatore di function o procedure, riportare la query con una colonna di alias \" as funcRt|Vd\": " + checkQuery);
    			} else {
	    			String selectedCols = checkQuery.substring("select ".length(), checkQuery.indexOf(" from "));
	    			if (selectedCols.indexOf("*") != -1 || selectedCols.indexOf(",") != -1)
	    				throw new WebObjectException("[WEBOBJECTEXCEPTION] La query di selezione indicata contiente più colonne di selezione, riportare la query con una sola colonna di selezione: " + checkQuery);
    			}
    		}
        	
        	/* cambio il flusso */
        	dbPool = poolFlowContent;
        	conn = poolMgr.getConnection(dbPool);
        	
    		PreparedStatement pst = conn.prepareStatement(updateQuery);
    		for (int i = 0; i < aParams.length; i++) {
    			// necessario per trasformazione in minuscolo dei dati del catalogo
    			String parValue = req.getParameter( aParams[i][0].toLowerCase() );
    			
    			if (parValue != null) {
    				if (parValue.equalsIgnoreCase("null") || parValue.equals("")) {
    					pst.setNull( (i+1) , java.sql.Types.SMALLINT);
	    			} else {
						boolean isNumber = true;
						try {
							Integer.parseInt(parValue);
						} catch (Exception e) {
							isNumber = false;
						}
						if (isNumber == true) {
							pst.setInt( (i+1) , (int)Integer.parseInt(parValue));
						} else {
							if (parValue.toLowerCase().startsWith("true") || parValue.toLowerCase().startsWith("false"))
								pst.setBoolean( (i+1) , Boolean.valueOf(parValue).booleanValue());
							else
								pst.setObject( (i+1) , parValue);
						}
					}
    			} else {
    				throw new WebObjectException("[WEBOBJECTEXCEPTION] Parametro della richiesta non allineato a query precompilata o inesistente: " + aParams[i]);
    			}
    		}
    		if (pst.execute()) {
    			ResultSet rs = pst.getResultSet();
    			int cnt = 0;
	    		while (rs.next()) {
	    				aParams[cnt][1] = String.valueOf(rs.getObject(cnt+1));
	    				value += "<br/>{row:["+cnt+"]="+aParams[cnt][1]+"}";
	    				cnt++;
	    		}
	    		rs.close();
    		} else {
    			// int t = pst.executeUpdate();
    			int t = pst.getUpdateCount();
    			value += "<hr/>Total updated/inserted record count:" + t + "<hr/>";
    			value += "<p align=\"right\"><a href=\"" + req.getRequestURI() + "\">Ritorna</a></p><hr/>";
    		}

    		pst.close();
        } catch ( SQLException e ) {
        	e.printStackTrace();
		} catch ( WebObjectException e ) {
			e.printStackTrace();
		} finally {
			poolMgr.freeConnection(dbPool, conn);
		}
		return value;
	}
	
	/**
	 * 
	 * Metodo impiegato per recuperare dati in formato xml (select), oppure per impostare più valori (update, insert)
	 * 
	 * @param area
	 * Identificatore dell'area (chiave di gruppo)
	 * @param sqlQuery
	 * Query precompilata da eseguire
	 * @param paramsContainer
	 * Array contenente i parametri da assegnare alla query
	 * @param outputType
	 * Tipo di dato ritornato, xml o plaintext (CSV row)
	 * @return
	 * Stringa rappresentante un documento xml o una riga csv 
	 */
	public static final String runQueryPlainPrepared(int area, String sqlQuery, String[][] paramsContainer, final int outputType) {
		final int PAR_NAME = 0, PAR_VALUE = 1;
		final int isXmlOutput = 0, isPlainOutput = 1;
		String value = null;
		String dbPool = poolFlowContent;
		Connection conn = poolMgr.getConnection(dbPool);


        try {
        	
    		PreparedStatement pst = conn.prepareStatement(sqlQuery);
    		for (int i = 0; i < paramsContainer.length; i++) {
    			// necessario per trasformazione in minuscolo dei dati del catalogo
    			String parValue = paramsContainer[i][PAR_VALUE];
    			
    			if (parValue != null) {
    				if (parValue.equalsIgnoreCase("null") || parValue.equals("")) {
    					pst.setNull( (i+1) , java.sql.Types.SMALLINT);
	    			} else {
						boolean isNumber = true;
						try {
							Integer.parseInt(parValue);
						} catch (Exception e) {
							isNumber = false;
						}
						if (isNumber == true) {
							pst.setInt( (i+1) , (int)Integer.parseInt(parValue));
						} else {
							if (parValue.toLowerCase().startsWith("true") || parValue.toLowerCase().startsWith("false"))
								pst.setBoolean( (i+1) , Boolean.valueOf(parValue).booleanValue());
							else
								pst.setObject( (i+1) , parValue);
						}
					}
    			} else {
    				throw new WebObjectException("[WEBOBJECTEXCEPTION] Parametro della richiesta non allineato a query precompilata o inesistente: " + paramsContainer[i][PAR_NAME]);
    			}
    		}
    		if (pst.execute()) {
    			ResultSet rs = pst.getResultSet();
    			ResultSetMetaData rsmd = rs.getMetaData();
	    		while (rs.next()) {
	    			if (outputType == isXmlOutput) {
		    			String xmlNode = "";
		    			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
		    				String colName = String.valueOf(rsmd.getColumnName(i));
		    				String colValue = String.valueOf(rs.getObject(i));
	    					xmlNode += XmlUtils.tagColumnData(colName, colValue);
		    			}
	    				value += XmlUtils.tag("meta", xmlNode);
	    			} else {
	    				// value += String.valueOf(rs.getObject(1)) + ";";
	    				value = (String)rs.getObject(1);
	    				// value = String.valueOf(rs.getObject(1));
	    			}
	    		}
	    		rs.close();
    		} else {
    			// int t = pst.executeUpdate();
    			int t = pst.getUpdateCount();
    			value += "<hr/>Total updated/inserted record count:" + t + "<hr/>";
    			// value += "<p align=\"right\"><a href=\"" + req.getRequestURI() + "\">Ritorna</a></p><hr/>";
    		}

    		pst.close();
        } catch ( SQLException e ) {
        	e.printStackTrace();
		} catch ( WebObjectException e ) {
			e.printStackTrace();
		} finally {
			poolMgr.freeConnection(dbPool, conn);
		}
		if (value != null)
			return value;
		else
			return null;
	}
	
	
	/**
	 * 
	 * Metodo impiegato per recuperare dati in formato xml (select), oppure per impostare più valori (update, insert)
	 * 
	 * @param area
	 * Identificatore dell'area (chiave di gruppo)
	 * @param sqlQuery
	 * Query precompilata da eseguire
	 * @param paramsContainer
	 * Array contenente i parametri da assegnare alla query
	 * @param outputType
	 * Tipo di dato ritornato, xml o plaintext (CSV row)
	 * @return
	 * Stringa rappresentante un documento xml o una riga csv 
	 */
	public static HashMap runQueryPlainPreparedRow(int area, String sqlQuery, String[][] paramsContainer, final int outputType) {
		final int PAR_NAME = 0, PAR_VALUE = 1;
		final int isXmlOutput = 0, isPlainOutput = 1;
		String dbPool = poolFlowContent;
		Connection conn = poolMgr.getConnection(dbPool);
		HashMap hm = null;

        try {
        	
    		PreparedStatement pst = conn.prepareStatement(sqlQuery);
    		for (int i = 0; i < paramsContainer.length; i++) {
    			// necessario per trasformazione in minuscolo dei dati del catalogo
    			String parValue = paramsContainer[i][PAR_VALUE];
    			
    			if (parValue != null) {
    				if (parValue.equalsIgnoreCase("null") || parValue.equals("")) {
    					pst.setNull( (i+1) , java.sql.Types.SMALLINT);
	    			} else {
						boolean isNumber = true;
						try {
							Integer.parseInt(parValue);
						} catch (Exception e) {
							isNumber = false;
						}
						if (isNumber == true) {
							pst.setInt( (i+1) , (int)Integer.parseInt(parValue));
						} else {
							if (parValue.toLowerCase().startsWith("true") || parValue.toLowerCase().startsWith("false"))
								pst.setBoolean( (i+1) , Boolean.valueOf(parValue).booleanValue());
							else
								pst.setObject( (i+1) , parValue);
						}
					}
    			} else {
    				throw new WebObjectException("[WEBOBJECTEXCEPTION] Parametro della richiesta non allineato a query precompilata o inesistente: " + paramsContainer[i][PAR_NAME]);
    			}
    		}
    		
    		hm = new HashMap();
    		
    		if (pst.execute()) {
    			ResultSet rs = pst.getResultSet();
    			ResultSetMetaData rsmd = rs.getMetaData();
	    		while (rs.next()) {
	    			if (outputType == isXmlOutput) {
		    			String xmlNode = "";
		    			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
		    				String colName = String.valueOf(rsmd.getColumnName(i));
		    				String colValue = String.valueOf(rs.getObject(i));
	    					xmlNode += XmlUtils.tagColumnData(colName, colValue);
		    			}
	    				String value = XmlUtils.tag("meta", xmlNode);
	    				hm.put( "xmlRow" , String.valueOf(value) );
	    			} else {
	    				// value += String.valueOf(rs.getObject(1)) + ";";
	    				// value = String.valueOf(rs.getObject(1));
		    			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
		    				String colName = String.valueOf(rsmd.getColumnName(i));
		    				String colValue = String.valueOf(rs.getObject(i));
	    					hm.put( colName.toLowerCase() , String.valueOf(colValue) );
		    			}
	    			}
	    		}
	    		rs.close();
    		} else {
    			// int t = pst.executeUpdate();
    			int t = pst.getUpdateCount();
    			String mex = "<hr/>Total updated/inserted record count:" + t + "<hr/>";
    			hm.put( "update/insert message" , String.valueOf(mex) );
    			// value += "<p align=\"right\"><a href=\"" + req.getRequestURI() + "\">Ritorna</a></p><hr/>";
    		}

    		pst.close();
        } catch ( SQLException e ) {
        	e.printStackTrace();
		} catch ( WebObjectException e ) {
			e.printStackTrace();
		} finally {
			poolMgr.freeConnection(dbPool, conn);
		}
		if (hm != null)
			return hm;
		else
			return null;
	}
	
}
