/*
 * DatabaseManager.java - jappframework (jappframework.jar)
 * Copyright (C) 2005
 * Source file created on 5-dic-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [DatabaseManager]
 * 2DO:
 *
 */

package com.blogspot.fravalle.lib.data.sql;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.Vector;

import com.blogspot.fravalle.lib.bl.beans.IModelList;
import com.blogspot.fravalle.lib.bl.beans.IModelRecord;
import com.blogspot.fravalle.lib.bl.business.exceptions.FrameworkDatabaseException;
import com.blogspot.fravalle.lib.bl.business.exceptions.FrameworkFatalException;
import com.blogspot.fravalle.lib.data.xml.XmlUtils;
import com.blogspot.fravalle.lib.monitor.ErrorCodes;
import com.blogspot.fravalle.lib.monitor.IErrorCodes;
import com.blogspot.fravalle.lib.monitor.MainLogger;
import com.blogspot.fravalle.util.Constants;
import com.blogspot.fravalle.util.SettingRes;

/**
 * @author antares
 */
public abstract class DatabaseManager extends ABaseManager {

	private String xslTransformation;
	protected boolean isXslTransform = false;
	private String queryDefaultColumnOrder;
	private String queryMultiColumnOrder;
	private int lastRowsCounted = 0;
	private boolean isGroupingActive = true;
	/**
	 * <code>bufferBookmark</code>: variabile locale di package, utilizzata per segnalare all'interno
	 * di uno stringbuffer la posizione della stringa da sostituire con il valore incapsulato
	 */
	protected final String _bufferBookmark = "[#@#]";

	protected DatabaseManager() {}
	
	protected DatabaseManager(String configUrl) {
		getLogger().info( "Loading database settings from:" + configUrl + " status=" + new File(configUrl).exists());
		init(configUrl);
	}
	
	protected void init(String configUrl) {
		getLogger().info( Constants._logEventDef + "[DatabaseManager INIT]" );
		configuration = new DataConnection(configUrl);
	}
	
	private final String tagGrouping(String columnName, String tableName) throws SQLException { // filter
		String sqlGroup = "select distinct " + columnName + " from " + tableName + " where " + columnName + " is not null";
		StringBuffer sbXmlGroup = new StringBuffer(512);
		PreparedStatement ps = this.getConnection().prepareStatement(sqlGroup, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		ResultSet rs = ps.executeQuery();
		while(rs.next()) {
			sbXmlGroup.append( XmlUtils.tagColumnData("value", String.valueOf(rs.getObject(1))) );
		}
		rs.close();
		ps.close();
		return XmlUtils.tagAtt("group", sbXmlGroup.toString(), "id", columnName);
	}
	
	private final String getXslTransformation() {
		return xslTransformation;
	}
	private final void setXslTransformation(String parXslTransformation) {
		xslTransformation = parXslTransformation;
		if (xslTransformation != null)
			isXslTransform = true;
	}
	private final boolean isXslTransform() {
		return isXslTransform;
	}
	
	private final String getQueryDefaultColumnOrder() {
		return queryDefaultColumnOrder;
	}
	
	private String buildQueryMultiOrderClause(String orderingColumnsPipe) {
		/* Ordinamento multicolonna */
		String queryClause = "";
		String orderingColumns[][];
		StringTokenizer st = new StringTokenizer(orderingColumnsPipe, ",");
		orderingColumns = new String[st.countTokens()][2];
		int e = 0;
		while (st.hasMoreTokens()) {
			String s = st.nextToken();
			orderingColumns[e][0] = s.substring(0,s.indexOf("-")) ;
			orderingColumns[e][1] = s.substring(s.indexOf("-")+1) ;
			e++;
		}
		
		for (int i = 0; i < orderingColumns.length; i++) {
			queryClause += orderingColumns[i][0] + " " + orderingColumns[i][1] + ",";
		}
		queryClause = queryClause.substring(0,queryClause.lastIndexOf(","));
		queryClause = " order by " + queryClause;
		
		return queryClause;
	}
	
	private String buildQueryClause(boolean isOrdering, String par1, String par2) {
		String queryClause = "";
		if (isOrdering) {
			int columnOrder = 1;
			if (this.getQueryDefaultColumnOrder() != null)
				columnOrder = Integer.parseInt(getQueryDefaultColumnOrder());
			if (par1 != null) {
				columnOrder = Integer.parseInt(par1);
			}
			int columnVersus = 0;
			if (par2 != null) {
				columnVersus = Integer.parseInt(par2);
			}
			queryClause = " order by " + columnOrder + ((columnVersus == 0) ? " asc" : " desc");
		} else {
			int recordPerPage = 10;
			if (par1 != null) {
				recordPerPage = Integer.parseInt(par1);
			}
			int recordOffset = 0;
			if (par2 != null) {
				recordOffset = Integer.parseInt(par2);
			} 
			
			switch (_dbServer) {
				case DB_SERVER_MYSQL :
					queryClause = " limit " + recordOffset + "," + recordPerPage;
					break;
				case DB_SERVER_POSTGRESQL :
					queryClause = " limit "+recordPerPage+" offset " + recordOffset;
					break;
				default :
					break;
			}
			
		}
		return queryClause;
	}
	
	private final String getQueryMultiColumnOrder() {
		return queryMultiColumnOrder;
	}
	private final void setQueryMultiColumnOrder(String parQueryMultiColumnOrder) {
		queryMultiColumnOrder = parQueryMultiColumnOrder;
	}
	
	protected final Vector getVectorData(SearchConditions conditions) throws NullPointerException, FrameworkFatalException, FrameworkDatabaseException {
		Vector vtRecords = new Vector();
		if (conditions.getQuery() == null)
			throw new NullPointerException(ErrorCodes.errorLabel(ErrorCodes.CODE_PARAMETER_NOTFOUND) );
		
		boolean startColumnHeader = true;
		this.lastRowsCounted = 0;
		
		/* 1a) Recupero i dati di ordinamento */
		String sOk = conditions.getOrderKey();
		String sOv = conditions.getOrderVersus();
		String sOm = conditions.getOrderMultiKey();
		if (sOm != null) {
			sOk = null;
			sOv = null;
			this.setQueryMultiColumnOrder(sOm);
		}
		String sRp = conditions.getRecordPageCurrent();
		String sRo = conditions.getRecordPageOffset();
		if (conditions.getRecordPerPage() == 0) {
			sRp = "1000";
			conditions.setRecordPageCurrent(sRp);
			sRo = "0";
			conditions.setRecordPageOffset(sRo);
		}
		
		/* 1b) Recupero l'interrogazione dinamica caricata dal tag  */
		String lastQuery = conditions.getQuery();
		
		if (!conditions.getQuery().startsWith(SHOW_DB) && !conditions.getQuery().startsWith(SHOW_TABLES) && !conditions.getQuery().startsWith("EXPLAIN"))
			lastQuery += ((sOk == null && sOv == null && this.getQueryMultiColumnOrder() != null) ? this.buildQueryMultiOrderClause(sOm) : this.buildQueryClause(true, sOk, sOv)) + buildQueryClause(false, sRp, sRo);
		if (conditions.getOrderKey() == null
				&& conditions.getOrderVersus() == null
				&& conditions.getRecordPageCurrent() == null
				&& conditions.getRecordPageOffset() == null)
			lastQuery = conditions.getQuery();
		
		/* 1c) Verifico il tipo di query; true = descrittiva, false = strutturale  */
		// boolean hasQueryOperatorAs = (lastQuery.indexOf(" as ") != -1);
		/* patch per consentire l'utilizzo delle function sulle colonne selezionate */
		boolean hasQueryOperatorAs = (lastQuery.indexOf(" as \"") != -1);
		
		
		try {
			int iRowCounter = 1;
			/* 2) Recupero la connessione db principale rilasciata dal context originario */
			
			/* 3) Apro una dichiarazione interrogativa precompilata */
			/* 3b) In questa posizione creare il bivio per tutte le connessioni di content */
			if (getConnection()==null)
				throw new FrameworkDatabaseException("[FATAL-ERROR]Connessione non disponibile!!!");
			
			if (conditions.getQuery().startsWith(SHOW_DB)) {
				MainLogger.getLog().info("Query interna:" + _dbServer);
				if (this.getServerType(_dbServer).equals(PARSE_MYSQL))
					lastQuery = SHOW_DB_MYSQL;
				else if (getServerType(_dbServer).equals(PARSE_POSTGRESQL))
					lastQuery = SHOW_DB_POSTGRESQL;
				
				MainLogger.getLog().info("Query interna:" + lastQuery);
			}
			if (conditions.getQuery().startsWith(SHOW_TABLES)) {
				if (this.getServerType(_dbServer).equals(PARSE_MYSQL))
					lastQuery = SHOW_TABLES_MYSQL + conditions.getQuery().substring(SHOW_TABLES.indexOf(":")+1);
				else if (this.getServerType(_dbServer).equals(PARSE_POSTGRESQL))
					lastQuery = SHOW_TABLES_POSTGRESQL;
				
				MainLogger.getLog().info("Query interna:[" + _dbServer + "]" + lastQuery + "\n" + SHOW_TABLES);
			}
			
			Object[] messages = new Object[]{lastQuery};
			getLogger().info( ErrorCodes.error(ErrorCodes.CODE_MESSAGE_GENERIC) + messages);
			
			String nativeSql = getConnection().nativeSQL(lastQuery);
			PreparedStatement ps = getConnection().prepareStatement(nativeSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			/* 4) Eseguo l'interrogazione ottenendo l'elenco risultante */
			ResultSet rs = ps.executeQuery();
			
			/* 4a) Recupero parziale dei metadati del ResultSet */
			ResultSetMetaData rsmd = rs.getMetaData();
			String columnSchema = rsmd.getSchemaName(1);
			String columnTable = rsmd.getTableName(1);
			/* Recupero del nome tabella se assente dai metadati del ResultSet */
			if (columnTable.equals("")) {
				String tmpQuery = nativeSql.toLowerCase();
				
				if ( tmpQuery.lastIndexOf(" where ") != -1 ){
					columnTable = tmpQuery.substring( tmpQuery.indexOf(" from ")+" from ".length() , tmpQuery.lastIndexOf(" where ") );
				}else{
					if ( tmpQuery.lastIndexOf(" order by ") != -1 )
						columnTable = tmpQuery.substring( tmpQuery.indexOf(" from ")+" from ".length() , tmpQuery.lastIndexOf(" order by ") );
					else
						columnTable = tmpQuery.substring( tmpQuery.indexOf(" from ")+" from ".length() );
					/* manca il "group by" */
				}
				
				columnTable = columnTable.trim();
			}
			int totalColumns = rsmd.getColumnCount();
			
			/* 4b) Ciclo dei dati ritornati dall'interrogazione */
			while (rs.next()) {
				/* 5) Istanzio il flusso testuale al cui interno archivio la riga del record */
				StringBuffer sbTableRow = new StringBuffer(1024);
				StringBuffer sbTableRowDetail = new StringBuffer(1024);
				StringBuffer sbXmlRow = new StringBuffer(1024);
				/* 5a) Valorizzo la variabile al cui interno archivio il testo del hyperlink */
				String hyperlinkText = "";
				/* 5b) Valorizzo la variabile al cui interno archivio il valore del hyperlink */
				String hyperlinkValue = "";
				
				if (totalColumns == 1) {
					vtRecords.add( String.valueOf(rs.getObject(1)) );
				}else{
					Vector vtCurrentRow = new Vector();
					/* 6) Preparo il ciclo su tutte le colonne della query */
					for (int i = 1; i <= totalColumns; i++) {
						Object sqlColumnValue  = rs.getObject(i); // String.valueOf( rs.getString(i) );
						String columnName = rsmd.getColumnName(i);
						String columnClassName = rsmd.getColumnClassName(i);
						// String columnN1 = rsmd.getSchemaName(i);
						// String columnTable = rsmd.getTableName(i);
						String columnNameQueryLabel = rsmd.getColumnLabel(i);
						int columnTypeInt = rsmd.getColumnType(i);
						int columnSize = rsmd.getColumnDisplaySize(i);
						String columnType = rsmd.getColumnTypeName(i);
						// int totalColumns = rsmd.getColumnCount();	
						vtCurrentRow.add((String)sqlColumnValue);
					}
					vtRecords.add(vtCurrentRow);
				}
				iRowCounter++;
				startColumnHeader = false;
				lastRowsCounted = iRowCounter;
			}
			rs.close();
			ps.close();
		} catch ( SQLException e ) {
			e.printStackTrace();
		} catch (FrameworkDatabaseException e) {
			throw e;
		} /*catch ( Exception e ) {
			e.printStackTrace();
		} */ finally {
			// free connection
		}
		return vtRecords;
	}
	
	/**
	 * Metodo di rappresentazione dati in formato xml con supporto della paginazione
	 * @param conditions
	 * oggetto rappresentante i riferimenti della ricerca
	 * @return
	 * la rappresentazione xml dei dati selezionati 
	 * @throws FrameworkFatalException
	 */
	protected final StringBuffer getStructuredData(SearchConditions conditions) throws NullPointerException, FrameworkFatalException, FrameworkDatabaseException {
		
		if (conditions.getQuery() == null || conditions.getXslTransformer() == null)
			throw new NullPointerException(ErrorCodes.errorLabel(ErrorCodes.CODE_PARAMETER_NOTFOUND) );
		
		this.setXslTransformation(conditions.getXslTransformer());
		
		boolean startColumnHeader = true;
		this.lastRowsCounted = 0;
		
		/* 1) Istanzio il flusso testuale al cui interno archivio le righe e le colonne dei record */
		StringBuffer sbXmlPage = new StringBuffer(1024);
		StringBuffer sbXmlPageContent = new StringBuffer(1024);
		
		StringBuffer sbTableContent = new StringBuffer(1024);
		StringBuffer sbTableHeader = new StringBuffer(1024);
		
		/* 1a) Recupero i dati di ordinamento */
		String sOk = conditions.getOrderKey();
		String sOv = conditions.getOrderVersus();
		String sOm = conditions.getOrderMultiKey();
		if (sOm != null) {
			sOk = null;
			sOv = null;
			this.setQueryMultiColumnOrder(sOm);
		}
		String sRp = conditions.getRecordPageCurrent();
		String sRo = conditions.getRecordPageOffset();

		if (sRp == null & sRo == null) {
			if (conditions.getRecordPerPage() == 0) {
				sRp = "1000";
				conditions.setRecordPageCurrent(sRp);
				sRo = "0";
				conditions.setRecordPageOffset(sRo);
			} else {
				sRp = String.valueOf(conditions.getRecordPerPage());
				conditions.setRecordPageCurrent(sRp);
				sRo = "0";
				conditions.setRecordPageOffset(sRo);
			}
		}
		

		/* 1b) Recupero l'interrogazione dinamica caricata dal tag  */
		
		String lastQuery = conditions.getQuery();
		if (!conditions.getQuery().startsWith("SHOW") && !conditions.getQuery().startsWith("DESCRIBE") && !conditions.getQuery().startsWith("EXPLAIN") && !conditions.getQuery().startsWith("SELECT datname") )
			lastQuery += ((sOk == null && sOv == null && this.getQueryMultiColumnOrder() != null) ? this.buildQueryMultiOrderClause(sOm) : this.buildQueryClause(true, sOk, sOv)) + buildQueryClause(false, sRp, sRo);
		if (conditions.getOrderKey() == null
				&& conditions.getOrderVersus() == null
				&& conditions.getRecordPageCurrent() == null
				&& conditions.getRecordPageOffset() == null)
			lastQuery = conditions.getQuery();
		
		
		/* 1c) Verifico il tipo di query; true = descrittiva, false = strutturale  */
		// boolean hasQueryOperatorAs = (lastQuery.indexOf(" as ") != -1);
		/* patch per consentire l'utilizzo delle function sulle colonne selezionate */
		boolean hasQueryOperatorAs = (lastQuery.indexOf(" as \"") != -1);
		
		
		try {
			int iRowCounter = 1;
			/* 2) Recupero la connessione db principale rilasciata dal context originario */
			
			/* 3) Apro una dichiarazione interrogativa precompilata */
			/* 3b) In questa posizione creare il bivio per tutte le connessioni di content */
			if (getConnection()==null)
				throw new FrameworkDatabaseException("[FATAL-ERROR]Connessione non disponibile!!!");
			
			Object[] messages = new Object[]{lastQuery};
			getLogger().info ( ErrorCodes.error(ErrorCodes.CODE_MESSAGE_GENERIC) + messages);
			
			String nativeSql = getConnection().nativeSQL(lastQuery);
			
			PreparedStatement ps = getConnection().prepareStatement(nativeSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			/* 4) Eseguo l'interrogazione ottenendo l'elenco risultante */
			ResultSet rs = ps.executeQuery();
			
			/* 4a) Recupero parziale dei metadati del ResultSet */
			ResultSetMetaData rsmd = rs.getMetaData();
			String columnSchema = rsmd.getSchemaName(1);
			String columnTable = rsmd.getTableName(1);
			/* Recupero del nome tabella se assente dai metadati del ResultSet */
			if (columnTable.equals("")) {
				String tmpQuery = nativeSql.toLowerCase();
				
				if ( tmpQuery.lastIndexOf(" where ") != -1 ){
					columnTable = tmpQuery.substring( tmpQuery.indexOf(" from ")+" from ".length() , tmpQuery.lastIndexOf(" where ") );
				}else{
					if ( tmpQuery.lastIndexOf(" order by ") != -1 )
						columnTable = tmpQuery.substring( tmpQuery.indexOf(" from ")+" from ".length() , tmpQuery.lastIndexOf(" order by ") );
					else
						columnTable = tmpQuery.substring( tmpQuery.indexOf(" from ")+" from ".length() );
					/* manca il "group by" */
				}
				
				columnTable = columnTable.trim();
			}
			
			/* 4b) Ciclo dei dati restituiti dall'interrogazione */
			while (rs.next()) {
				/* 5) Istanzio il flusso testuale al cui interno archivio la riga del record */
				StringBuffer sbTableRow = new StringBuffer(1024);
				StringBuffer sbTableRowDetail = new StringBuffer(1024);
				
				/* 5a) Valorizzo la variabile al cui interno archivio il testo del hyperlink */
				String hyperlinkText = "";
				/* 5b) Valorizzo la variabile al cui interno archivio il valore del hyperlink */
				String hyperlinkValue = "";

				/* 6) Preparo il ciclo su tutte le colonne della query */
				sbXmlPageContent.append( this.mapping(rs, rsmd, columnTable, iRowCounter, hasQueryOperatorAs) );
				
				iRowCounter++;
				startColumnHeader = false;
				lastRowsCounted = iRowCounter;
			}
			
			if (lastRowsCounted == 0) {
				getLogger().info("Empty resultset");
				rs.next();
				sbXmlPageContent.append( this.mapping(rs, rsmd, columnTable, iRowCounter, hasQueryOperatorAs) );
			}
				
			rs.close();
			ps.close();
		} catch ( SQLException e ) {
			e.printStackTrace();
		} catch (FrameworkDatabaseException e) {
			throw e;
		} catch ( FrameworkFatalException e ) {
			throw e;
		} /*catch ( Exception e ) {
			e.printStackTrace();
		} */ catch (ParseException e) {
			throw new FrameworkFatalException(e);
		} finally {
			// free connection
		}
		
		
		
		String jdbcData = "<jdbc><server type=\""+ this.getServerType(_dbServer) +"\" /><querySource>"+lastQuery+"</querySource><pool>"+conditions.getPoolName()+"</pool></jdbc>\r\n";
		// SHOW DATABASES
		String sXml = XmlUtils.tagAtt("list", sbXmlPageContent.toString(), new String[][]{new String[]{"number","1"},new String[]{"orderKey",sOk},new String[]{"orderVersus",sOv},new String[]{"listBuffer",String.valueOf(conditions.getRecordPerPage())},new String[]{"listRows",String.valueOf(lastRowsCounted-1)},new String[]{"listOffset",sRo}});
		String groupingInfo = ""; // XmlUtils.tag("groupingInfo", sbXmlGroupingInfo.toString() );
		sbXmlPage.append( XmlUtils.tagPage("gridObject", jdbcData + groupingInfo + sXml) );
		
		return sbXmlPage;
	}
	
	private StringBuffer mapping(ResultSet rsLocal,ResultSetMetaData rsmd, String tableName, int iRow, boolean hasQueryOperatorAs) throws SQLException, ParseException, FrameworkFatalException {
		StringBuffer xmlBuffer = new StringBuffer(1024);
		StringBuffer sbXmlRow = new StringBuffer(1024);
		StringBuffer sbXmlGroupingInfo = new StringBuffer(1024);
		
		int totalColumns = rsmd.getColumnCount();
		
		for (int i = 1; i <= totalColumns; i++) {
			String columnClassName = rsmd.getColumnClassName(i);
			String sqlColumnValue = null;
			try {
				if (rsLocal != null && rsLocal.next())
					sqlColumnValue = rsLocal.getString(i);
				/*else {
					sqlColumnValue = String.valueOf( Class.forName(rsmd.getColumnClassName(i)).newInstance() );
				}*/
			}
			catch ( SQLException e ) {
				e.printStackTrace();
			}/*
			catch ( InstantiationException e ) {
				e.printStackTrace();
			}
			catch ( IllegalAccessException e ) {
				e.printStackTrace();
			}
			catch ( ClassNotFoundException e ) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}*/
			String columnName = rsmd.getColumnName(i);
			// String columnN1 = rsmd.getSchemaName(i);
			// String columnTable = rsmd.getTableName(i);
			String columnNameQueryLabel = rsmd.getColumnLabel(i);
			int columnTypeInt = rsmd.getColumnType(i);
			int columnSize = rsmd.getColumnDisplaySize(i);
			String columnType = rsmd.getColumnTypeName(i);
			
			String columnConvert = this.convertiTipo(columnType, _dbServer);
			
			// int totalColumns = rsmd.getColumnCount();
			
			/* 6a) Flusso dati xml */
			if (this.isXslTransform()) {
				String sXml = "";
				String[] xmlSrc = {};
				if (sqlColumnValue != null && sqlColumnValue.startsWith("<")) {
					xmlSrc = new String[]{"isSrc","xml"};
					sqlColumnValue = sqlColumnValue.replace(':','_');
				}
				if (sqlColumnValue == null)
					sqlColumnValue = String.valueOf(sqlColumnValue);
				
				if ( columnClassName.equals("java.sql.Timestamp")) {
		   	  		SimpleDateFormat sdf = new SimpleDateFormat(SettingRes.get("db.format.date.timestamp"));
		   	  		Calendar c = Calendar.getInstance();
		   	  		if (sqlColumnValue != null && !"null".equals(sqlColumnValue.toLowerCase()))
		   	  			c.setTime( sdf.parse(sqlColumnValue) );
		   	  		// sqlColumnValue = String.valueOf( c.getTimeInMillis() );
		   	  		// Timestamp timeValue = new Timestamp( c.getTimeInMillis() );
		   	  		sqlColumnValue =  String.valueOf( c.getTimeInMillis() );
				} else if ( columnClassName.equals("java.lang.Integer")) {
					if (sqlColumnValue == null || "null".equals(sqlColumnValue.toLowerCase()))
						sqlColumnValue =  "0";
				}
				String columnNameForBeanSpecs = columnName.substring(0,1).toUpperCase() + columnName.substring(1);
				
				if (!hasQueryOperatorAs)
					sXml = XmlUtils.tagColumnDataAtt(columnName, sqlColumnValue, false, new String[][]{xmlSrc, new String[]{"id",String.valueOf(i)},new String[]{"className",String.valueOf(columnClassName)},new String[]{"type",String.valueOf(columnConvert)},new String[]{"legacyType",String.valueOf(columnType)},new String[]{"size",String.valueOf(columnSize)},new String[]{"beanNameSpecs",String.valueOf(columnNameForBeanSpecs)}});
				else
					sXml = XmlUtils.tagColumnDataAtt(columnName, sqlColumnValue, true, new String[][]{xmlSrc, new String[]{"id",String.valueOf(i)},new String[]{"className",String.valueOf(columnClassName)},new String[]{"type",String.valueOf(columnConvert)},new String[]{"legacyType",String.valueOf(columnType)},new String[]{"size",String.valueOf(columnSize)}});
				sbXmlRow.append( sXml + "\r\n" );
				
				// 
				String sGroup = "";
				if (columnType.equalsIgnoreCase("varchar") && isGroupingActive && iRow == 1) {
					if (tableName.indexOf(",") == -1) {
						sGroup = this.tagGrouping(columnName, tableName);
						sbXmlGroupingInfo.append( sGroup );
					} /* else { sGroup = this.tagGrouping(columnName, "join-table"); } */
					sbXmlGroupingInfo.append( sGroup );
				}
			} else {
				/* 6b) Flusso dati html */
				throw new FrameworkFatalException("Conversione non implementata");
			}
		}
		
		if (this.isXslTransform()) {
			String sXml = XmlUtils.tagAtt("data", sbXmlRow.toString(), "row", String.valueOf(iRow) );
			xmlBuffer.append( sXml );
		} else {
			throw new FrameworkFatalException("Operazione non raggiungibile da blocco in blocco precedente");
		}
		return xmlBuffer;
	}
	
	
	public String preparedBeanUpdate(SearchConditions search) {
		if (search.getUpdatableData() == null) {
			Vector vtBeanItems = search.getUpdatable()[0];
			Vector vtBeanMapItem = search.getUpdatable()[1];
			// preparedBeanUpdate(vtBeanItems);
		} else {
			preparedBeanUpdate(search.getUpdatableData());
		}
		return new String("eseguito");
	}
	
	synchronized public void preparedBeanUpdate(IModelList vtBeanItems) {
		
		   boolean firstCheck = false;
		   String query = "";

		   try {
				
		       for (int id = 0; id < vtBeanItems.size(); id++) {
		           
		           IModelRecord mybean = (IModelRecord)vtBeanItems.elementAt(id);
		           
		           Vector vt = mybean.updateSqlBean();
		           
		           if (mybean.hasChanges()) {
		           	
			           String updateQuery = String.valueOf( vt.elementAt(0) );		           
					   query = getConnection().nativeSQL( updateQuery );
					   
					   
					   getLogger().info( Constants._logEventDef + "Executing query: " + query);
					   
					   PreparedStatement pstmt = getConnection().prepareStatement(query);
					   Vector vtTmp = (Vector)vt.elementAt(1);
					   for (int idvec = 0; idvec < vtTmp.size(); idvec++) {
	
					   	  String parValue = String.valueOf(vtTmp.elementAt(idvec));
					   	  
					   	  boolean isNull = false;
					   	  if (parValue.equals("null") || parValue.equals("") || parValue.equals("&nbsp;")) {
							pstmt.setNull( (idvec+1) , java.sql.Types.SMALLINT);
							isNull = true;
					   	  }
					   	  
					   	  boolean isNumber = true;
					   	  try {
					   	   		int intValue = Integer.parseInt(parValue);
					   	   		pstmt.setInt( (idvec+1) , intValue);
					   	  } catch (NumberFormatException e) {
					   	   		isNumber = false;
					   	  }
					   	  
					   	  boolean isDate = true;
					   	  try {
					   	  		SimpleDateFormat sdf = new SimpleDateFormat(SettingRes.get("db.format.date.timestamp"));
					   	  		Calendar c = Calendar.getInstance();
					   	  		c.setTime( sdf.parse(parValue) );
					   	   		Timestamp timeValue = new Timestamp( c.getTimeInMillis() );
					   	   		pstmt.setTimestamp( (idvec+1) , timeValue);
					   	   		getLogger().info( "Date parsed: " + parValue);
					   	  } catch (Exception e) {
					   	   		isDate = false;
					   	  }
					   	  
					   	  
					   	  if (!isNull && !isNumber && !isDate) {
							
								if (parValue.equals( SettingRes.get("db.format.boolean.false") )
										|| parValue.equals( SettingRes.get("db.format.boolean.true") )
										|| parValue.equals( "true" )
										|| parValue.equals( "false" )
									) {
									if (parValue.equals( SettingRes.get("db.format.boolean.false") ))
										parValue = "false";
									if (parValue.equals( SettingRes.get("db.format.boolean.true") ))
										parValue = "true";
									pstmt.setBoolean( (idvec+1) , Boolean.valueOf(parValue).booleanValue());
								} else
									pstmt.setObject( (idvec+1) , parValue);
							
					   	  }
					   	  
					   }
					   
					   int total = pstmt.executeUpdate();
					   
					   pstmt.close();
					   MainLogger.getLog().info("Update eseguito su "+ total +" elementi!!!");
		           }
		       }

		   } catch(ClassCastException ex) {
		   		getLogger().severe( ErrorCodes.error(IErrorCodes.FRAMEWORK_CAST_ERROR) + ex.getMessage() );
		   } catch(NullPointerException ex) {
		   		getLogger().severe( ex.getMessage() );
		   } catch (SQLException ex) {
		   	getLogger().severe( ex.getMessage() );
		   } finally {}
	   }
	
}
