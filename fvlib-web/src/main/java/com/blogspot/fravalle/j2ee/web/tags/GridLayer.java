/*
 * GridLayer.java - antares-web (antares-web.jar)
 * Copyright (C) 2005
 * Source file created on 30-set-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [GridLayer]
 * LOGIC = metodi di impostazione classe
 * INIT = metodi di configurazione
 * VIEW = metodi di visualizzazione
 * 
 * 2DO:
 * . Ordinamento multi colonna, manca l'assegnazione delle colonne legata alla VIEW
 * x Etichetta colonne
 * x Lunghezza globale massima delle stringhe
 * x Generazione dati incapsulati in frammento xml per trsformazione xsl
 * x Colonna predefinita di ordinamento
 * x Ordinamento multicolonna mancante del meccanismo di view
 * x Visualizzazione colonna tipo testo come blocco inizialmente nascosto 
 */

package com.blogspot.fravalle.j2ee.web.tags;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.blogspot.fravalle.util.UtilConstants;
import com.blogspot.fravalle.util.xml.XmlTransformer;
import com.blogspot.fravalle.util.xml.XmlUtils;

/**
 * @author antares
 */
public class GridLayer extends BodyTagSupport {

	public final static String WEBQUERY_ORDERKEY = "ok";
	public final static String WEBQUERY_ORDERVERSUS = "ov";
	public final static String WEBQUERY_ORDERMULTI = "om";
	public final static String WEBQUERY_RECORDPAGING = "rp";
	public final static String WEBQUERY_RECORDOFFSET = "ro";
	
	public final static int RECORD_PER_PAGE = 10;
	
	/**
	 * <code>bufferBookmark</code>: variabile locale di package, utilizzata per segnalare all'interno
	 * di uno stringbuffer la posizione della stringa da sostituire con il valore incapsulato
	 */
	protected final String _bufferBookmark = "[#@#]";
	/**
	 * <code>sLinkInterval</code>: variabile locale di package, intervallo stringa di segnalazione numerica
	 * colonne query utilizzate per comporre il valore del hyperlink
	 */
	protected String sLinkInterval;
	/**
	 * <code>iLinkColumn</code>: variabile locale di package, valore numerico utilizzato per indicare
	 * la colonna da utilizzare come link principale della griglia di visualizzazione
	 */
	protected int iLinkColumn;
	
	protected String[] asGridStyle = new String[2];
	protected String[] asAlternateRow = new String[2];
	
	private final int GRADIENT = 0, RED = 1, GREEN = 2, BLUE = 3;
	protected String[] asGradientCells = new String[4];
	
	protected String queryMultiColumnOrder;
	
	protected Connection conn;
	


	/**
	 * <code>query</code>: istruzioni di interrogazione database da eseguire durante
	 * la generazione della griglia di visualizzazione
	 */
	private String query;
	
	private String queryDefaultColumnOrder;
	
	/**
	 * <code>gridAltRows</code>: valori dei colori alternati da assegnare alle
	 * righe. Separati da "," nel caso di colori esadecimali il carattere "#"
	 * deve essere sostituito da "_"
	 */
	private String gridAltRows; 
	/**
	 * <code>linkSchema</code>: indicazioni dello schema del link principale della
	 * griglia di visualizzazione, il formato è composto da "colonna testo hyperlink"
	 * + "colonne valore hyperlink", es: "2-{[1][3]}" colonna 2 il cui hyperlink è
	 * composto dalla colonna 1 e 3
	 */
	private String linkSchema;
	/**
	 * <code>linkUrl</code>: particella del valore del link principale della griglia
	 * di visualizzazione, seguito sempre dal segnalatore di query "?"
	 */
	private String linkUrl;
	
	private String hiddenQueryCols;

	private String gridStyle;
	private String gridBackground;
	private String gridGradientCells;
	
	private String textColumnBoxed;
	protected boolean isTextBoxed = false;
	
	private String xslTransformation;
	protected boolean isXslTransform = false;
	
	private String maxCellTextLength;
	protected int MAX_CELL_TEXT_LEN = 150;

	public final String getXslTransformation() {
		return xslTransformation;
	}
	public final void setXslTransformation(String parXslTransformation) {
		xslTransformation = parXslTransformation;
		if (xslTransformation != null)
			isXslTransform = true;
	}
	public final boolean isXslTransform() {
		return isXslTransform;
	}
	public final String getTextColumnBoxed() {
		return textColumnBoxed;
	}

	public final void setTextColumnBoxed(String parTextBoxed) {
		textColumnBoxed = parTextBoxed;
		if (textColumnBoxed != null)
			isTextBoxed = Boolean.valueOf(textColumnBoxed).booleanValue();
	}
	public final boolean isTextBoxed() {
		return isTextBoxed;
	}	
	
	
	public final String getMaxCellTextLength() {
		return maxCellTextLength;
	}
	public final void setMaxCellTextLength(String parMaxCellTextLength) {
		maxCellTextLength = parMaxCellTextLength;
		MAX_CELL_TEXT_LEN = Integer.parseInt(maxCellTextLength);
	}
	public final String getLinkUrl() {
		return linkUrl;
	}
	public final void setLinkUrl(String parLinkUrl) {
		linkUrl = parLinkUrl;
	}
	public final String getQuery() {
		return query;
	}
	public final void setQuery(String parQuery) {
		query = parQuery;
	}
	public final String getQueryDefaultColumnOrder() {
		return queryDefaultColumnOrder;
	}
	public final void setQueryDefaultColumnOrder(String parQueryDefaultColumnOrder) {
		queryDefaultColumnOrder = parQueryDefaultColumnOrder;
	}
	public final String getHiddenQueryCols() {
		return hiddenQueryCols;
	}
	public final void setHiddenQueryCols(String parHiddenQueryCols) {
		hiddenQueryCols = parHiddenQueryCols;
	}

	
	public final String getGridGradientCells() {
		return gridGradientCells;
	}
	public final void setGridGradientCells(String parGridGradientCells) {
		gridGradientCells = parGridGradientCells;
		asGradientCells[0] =  gridGradientCells.substring(0, gridGradientCells.indexOf("{"));
		asGradientCells[1] = gridGradientCells.substring(gridGradientCells.indexOf("{")+1,gridGradientCells.indexOf(","));
		asGradientCells[2] = gridGradientCells.substring(gridGradientCells.indexOf(",")+1,gridGradientCells.lastIndexOf(","));
		asGradientCells[3] = gridGradientCells.substring(gridGradientCells.lastIndexOf(",")+1,gridGradientCells.indexOf("}"));
	}
	
	public final String getGridAltRows() {
		return gridAltRows;
	}
	public final void setGridAltRows(String parAltRows) {
		gridAltRows = parAltRows;
		asAlternateRow[0] = gridAltRows.substring(0,gridAltRows.indexOf(","));
		asAlternateRow[1] = gridAltRows.substring(gridAltRows.indexOf(",")+1,gridAltRows.length());
	}
	
	public final void setGridStyle(String parGridStyle) {
		gridStyle = parGridStyle;
		if (gridStyle.indexOf(",") != -1) { 
			asGridStyle[0] = gridStyle.substring(0,gridStyle.indexOf(","));
			asGridStyle[1] = gridStyle.substring(gridStyle.indexOf(",")+1,gridStyle.length());
		} else {
			asGridStyle[0] = gridStyle;
		}
	}
	public final void setGridBackground(String parGridBackground) {
		gridBackground = parGridBackground;
	}
	
	public final String getLinkSchema() {
		return linkSchema;
	}
	/**
	 * Metodo di impostazione del costrutto del link principale della griglia di visualizzazione
	 * @param parLinkSchema
	 * <code>Parametro recuperato dal tag per generare il costrutto del link principale
	 * della griglia di visualizzazione</code>
	 */
	public final void setLinkSchema(String parLinkSchema) {
		linkSchema = parLinkSchema;
		String st1 = linkSchema.substring(0,linkSchema.indexOf("-"));
		iLinkColumn = Integer.parseInt( st1);
		sLinkInterval = linkSchema.substring(linkSchema.indexOf("-")+1, linkSchema.length());
	}
	
	/* LOGIC: metodi specifici della classe */
	
	protected final String getQueryMultiColumnOrder() {
		return queryMultiColumnOrder;
	}
	protected final void setQueryMultiColumnOrder(String parQueryMultiColumnOrder) {
		queryMultiColumnOrder = parQueryMultiColumnOrder;
	}
	
	protected final Connection getConn() {
		return conn;
	}
	protected final void setConn(Connection parConn) {
		conn = parConn;
	}
	
	protected final String getGridStyle() {
		String s = "";
		
		if (gridStyle != null) {
			if (asGridStyle[0].startsWith(".")) {
				s = " class=\"" + asGridStyle[0].substring(1) + "\"";
			} else {
				s = " cellpadding=\"" + asGridStyle[0] + "\"" + " cellspacing=\"" + asGridStyle[1] + "\"";
			}
		}
		return s;
	}
	
	protected final String getGridBackground() {
		String s = "";
		
		if (gridBackground != null) {
			if (gridBackground.startsWith("_"))
				s = " bgColor=\"#" + gridBackground.substring(1) + "\"";
			else if (gridBackground.startsWith("."))
				s = " class=\"" + gridBackground.substring(1) + "\"";
			else
				s = " bgColor=\"" + gridBackground + "\"";
		}
		return s;
	}
	
	protected final String getAlternateRow(int row) {
		String s = "";
		double 	dCheckParity = new Double(row).doubleValue() / 2;
		int 	iCheckParity = row / 2;
		row = ( dCheckParity > iCheckParity ) ? 0 : 1 ;
		
		if (gridAltRows != null) {
			if (asAlternateRow[row].startsWith("_"))
				s = " bgColor=\"#" + asAlternateRow[row].substring(1) + "\"";
			else if (asAlternateRow[row].startsWith("."))
				s = " class=\"" + asAlternateRow[row].substring(1) + "\"";
			else
				s = " bgColor=\"" + asAlternateRow[row] + "\"";
		}
		return s;
	}

	protected final String getGradientCells(int cell, int totalCell, int row) {
		String s = "";
		double 	dCheckParity = new Double(row).doubleValue() / 2;
		int 	iCheckParity = row / 2;
		row = ( dCheckParity > iCheckParity ) ? 0 : 1 ;
		
		int gradient = 60;
		if (row == 1) gradient = 0;
		
		int rCol = 0, gCol = 0, bCol = 0;
		// asGradientCells[GRADIENT]
		if (gridGradientCells != null) {
			if (asGradientCells[RED].indexOf("+") != -1)
				rCol = 40 * cell - gradient;
			else if (asGradientCells[RED].indexOf("-") != -1)
				rCol = 255 / cell - gradient;
			else
				rCol = 255 - gradient;
			
			if (asGradientCells[GREEN].indexOf("+") != -1)
				gCol = 40 * cell - gradient;
			else if (asGradientCells[GREEN].indexOf("-") != -1)
				gCol = 255 / cell - gradient;
			else
				gCol = 255 - gradient;
			
			if (asGradientCells[BLUE].indexOf("+") != -1)
				bCol = 40 * cell - gradient;
			else if (asGradientCells[BLUE].indexOf("-") != -1)
				bCol = 255 / cell - gradient;
			else
				bCol = 255 - gradient;
			
			s = " style=\"background-color: rgb("+rCol+","+gCol+","+bCol+");\"";
		}
		return s;
	}
	
	
	protected boolean isHiddenColumn(int i) {
		if (hiddenQueryCols.indexOf("["+String.valueOf(i)+"]") == -1)
			return false;
		else
			return true;
	}
	protected boolean isHyperlinkColumn(int i) {
		if (sLinkInterval.indexOf("["+String.valueOf(i)+"]") == -1)
			return false;
		else
			return true;
	}
	protected boolean isHyperlinkText(int i) {
		return i == iLinkColumn;
	}

	protected String makeHyperlink(String linkText, String linkUrl, String linkQuery) {
		return "<a href=\""+linkUrl+linkQuery.substring(0,linkQuery.lastIndexOf(","))+"\">" + linkText + "</a>";
	}
	protected String makeTableRow(StringBuffer content, int row) {
		return "<tr" + this.getAlternateRow(row) + ">" + content.toString() + "</tr>";
	}
	protected String makeTableRow(String content, int row) {
		return "<tr" + this.getAlternateRow(row) + ">" + content + "</tr>";
	}
	protected String makeTableCell(String content, int idx, int totalCols, int row) {
		if (!this.isHiddenColumn(idx))
			return "<td" + this.getGradientCells(idx, totalCols, row) + ">" + content + "</td>";
		else
			return "";
	}
	protected String makeTableCell(String content, int totalCols, int row) {
		return "<td" + this.getGradientCells(1, 0, row) + ">" + content + "</td>";
	}
	protected String makeTableCellHead(String content, int idx) {
		if (!this.isHiddenColumn(idx))
			return "<th>" + content + "</th>";
		else
			return "";
	}
	protected String makeTable(StringBuffer tableContent) {
		return "<table" + this.getGridStyle() + this.getGridBackground() + "><tbody>" + tableContent.toString() + "</tbody></table>";
	}

	protected String buildQueryMultiOrderClause(String orderingColumnsPipe) {
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
	
	protected String buildQueryClause(boolean isOrdering, String par1, String par2) {
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
			queryClause = " limit "+recordPerPage+" offset " + recordOffset;
		}
		return queryClause;
	}
	
	protected String buildNavigationPanel(String sOk,String sOv,String sRp,String sRo) {
		String panel = "";
		/*
		int columnOrder = 1;
		if (sOk != null) {
			columnOrder = Integer.parseInt(sOk);
		}
		int columnVersus = 0;
		if (sOv != null) {
			columnVersus = Integer.parseInt(sOv);
		} */
		
		int recordPerPage = RECORD_PER_PAGE;
		if (sRp != null) {
			recordPerPage = Integer.parseInt(sRp);
		}
		int recordOffset = 0;
		if (sRo != null) {
			recordOffset = Integer.parseInt(sRo);
		}
		
		String sWebQuery = ((HttpServletRequest)pageContext.getRequest()).getQueryString();
		
		if ( sWebQuery != null  ) {
			if (sWebQuery.indexOf(WEBQUERY_RECORDOFFSET+"=") != -1 && sWebQuery.indexOf("&") != -1) {
				/* NOTA: la querystring contiene il parametro rowOffset e altri parametri */
				String rewriteQuery = "";
				for (Enumeration e = pageContext.getRequest().getParameterNames(); e.hasMoreElements();) {
					String parName = String.valueOf(e.nextElement());
					if (!parName.equals(WEBQUERY_RECORDOFFSET))
						rewriteQuery += parName + "=" + pageContext.getRequest().getParameter(parName) + "&";
				}
				if ( rewriteQuery.lastIndexOf("&") == (rewriteQuery.length()-1) )
					rewriteQuery = rewriteQuery.substring(0,rewriteQuery.lastIndexOf("&"));
				sWebQuery = "&" + rewriteQuery;
			} else {
				/* NOTA: la querystring contiene un solo parametro, se rowOffset viene rimosso */
				if (sWebQuery.indexOf(WEBQUERY_RECORDOFFSET+"=")!=-1) sWebQuery = "";
				else sWebQuery = "&" + sWebQuery;
			}
		} else {
			/* NOTA: la querystring non contiene alcun parametro */
			sWebQuery = "";
		}
		
		if (recordOffset>0)
			panel += "<a href=\"?"+WEBQUERY_RECORDOFFSET+"="+ (recordOffset - recordPerPage) + sWebQuery + "\">Indietro</a>";
		if (lastRowsCounted >= recordPerPage)
			panel += "<a href=\"?"+WEBQUERY_RECORDOFFSET+"="+(recordOffset+recordPerPage) + sWebQuery + "\">Avanti</a>";
		
		return panel;
	}

	
	private boolean isGroupingActive = true;
	private final String tagGrouping(String columnName, String tableName) throws SQLException { // filter
		String sqlGroup = "select distinct " + columnName + " from " + tableName + " where " + columnName + " is not null";
		StringBuffer sbXmlGroup = new StringBuffer(512);
		PreparedStatement ps = this.getConn().prepareStatement(sqlGroup, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		ResultSet rs = ps.executeQuery();
		while(rs.next()) {
			sbXmlGroup.append( XmlUtils.tagColumnData("value", String.valueOf(rs.getObject(1))) );
		}
		rs.close();
		ps.close();
		return XmlUtils.tagAtt("group", sbXmlGroup.toString(), "id", columnName);
	}
	
	
	
	private int lastRowsCounted = 0;
	
	// scrolling ottenuto tramite la generazione della query 
	private final StringBuffer getResultsPgsql() {
		
		boolean startColumnHeader = true;
		
		/* 1) Istanzio il flusso testuale al cui interno archivio le righe e le colonne dei record */
		StringBuffer sbXmlPage = new StringBuffer(1024);
		StringBuffer sbXmlPageContent = new StringBuffer(1024);
		StringBuffer sbXmlGroupingInfo = new StringBuffer(1024);
		StringBuffer sbTableContent = new StringBuffer(1024);
		StringBuffer sbTableHeader = new StringBuffer(1024);
		
		sbTableHeader.append("<thead>");
		
		/* 1a) Recupero i dati di ordinamento dalla Request */
		String sOk = pageContext.getRequest().getParameter( WEBQUERY_ORDERKEY );
		String sOv = pageContext.getRequest().getParameter( WEBQUERY_ORDERVERSUS );
		String sOm = pageContext.getRequest().getParameter( WEBQUERY_ORDERMULTI );
		if (sOm != null) {
			sOk = null;
			sOv = null;
			this.setQueryMultiColumnOrder(sOm);
		}
		String sRp = pageContext.getRequest().getParameter( WEBQUERY_RECORDPAGING );
		String sRo = pageContext.getRequest().getParameter( WEBQUERY_RECORDOFFSET );

		/* 1b) Recupero l'interrogazione dinamica caricata dal tag  */
		String lastQuery = this.getQuery() + ((sOk == null && sOv == null && this.getQueryMultiColumnOrder() != null) ? this.buildQueryMultiOrderClause(sOm) : this.buildQueryClause(true, sOk, sOv)) + buildQueryClause(false, sRp, sRo);
		
		/* 1c) Verifico il tipo di query; true = descrittiva, false = strutturale  */
		// boolean hasQueryOperatorAs = (lastQuery.indexOf(" as ") != -1);
		/* patch per consentire l'utilizzo delle function sulle colonne selezionate */
		boolean hasQueryOperatorAs = (lastQuery.indexOf(" as \"") != -1);
		
		
		try {
			int iRowCounter = 1;
			/* 2) Recupero la connessione db principale rilasciata dal context originario */
			this.setConn( (Connection)pageContext.getServletContext().getAttribute(UtilConstants.CTX_BEAN_DB_FLOW_CONTENT) );
			
			/* 3) Apro una dichiarazione interrogativa precompilata */
			/* 3b) In questa posizione creare il bivio per tutte le connessioni di content */
			String nativeSql = this.getConn().nativeSQL(lastQuery);
			PreparedStatement ps = this.getConn().prepareStatement(nativeSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
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

				/* 6) Preparo il ciclo su tutte le colonne della query */
				for (int i = 1; i <= totalColumns; i++) {
					String sqlColumnValue  = rs.getString(i); // String.valueOf( rs.getString(i) );
					String columnName = rsmd.getColumnName(i);
					String columnClassName = rsmd.getColumnClassName(i);
					// String columnN1 = rsmd.getSchemaName(i);
					// String columnTable = rsmd.getTableName(i);
					String columnNameQueryLabel = rsmd.getColumnLabel(i);
					int columnTypeInt = rsmd.getColumnType(i);
					int columnSize = rsmd.getColumnDisplaySize(i);
					String columnType = rsmd.getColumnTypeName(i);
					// int totalColumns = rsmd.getColumnCount();
					
					/* 6a) Flusso dati xml */
					if (this.isXslTransform()) {
						String sXml = "";
						if (!hasQueryOperatorAs)
							// sXml = XmlUtils.tagColumnData(columnName, sqlColumnValue);
							sXml = XmlUtils.tagColumnDataAtt(columnName, sqlColumnValue, false, new String[][]{new String[]{"id",String.valueOf(i)},new String[]{"className",String.valueOf(columnClassName)},new String[]{"type",String.valueOf(columnType)},new String[]{"size",String.valueOf(columnSize)}});
						else
							sXml = XmlUtils.tagColumnDataAtt(columnName, sqlColumnValue, true, new String[][]{new String[]{"id",String.valueOf(i)},new String[]{"className",String.valueOf(columnClassName)},new String[]{"type",String.valueOf(columnType)},new String[]{"size",String.valueOf(columnSize)}});
							// sXml = tagColumnData( "col", sqlColumnValue, ("sostituzione nome colonna originale [" + columnName + "] con numerazione progressiva colonna, nota: il driver jdbc di Postgres non supporta il recupero del nome originale della colonna nel caso di utilizzo di alias") );
						sbXmlRow.append( sXml + "\r\n" );
						
						// 
						String sGroup = "";
						if (columnType.equalsIgnoreCase("varchar") && isGroupingActive && iRowCounter == 1) {
							if (columnTable.indexOf(",") == -1) {
								sGroup = this.tagGrouping(columnName, columnTable);
								sbXmlGroupingInfo.append( sGroup );
							} /* else { sGroup = this.tagGrouping(columnName, "join-table"); } */
							sbXmlGroupingInfo.append( sGroup );
						}
					/* 6b) Flusso dati html */ 
					} else {
						if (sqlColumnValue == null) {
							/* int ERROR_CODE = 65;
							WebApplicationError.sendError(Constants.HTTP_ERROR_507, new Exception("Parametro nullo in colonna database"), (HttpServletRequest)pageContext.getRequest(), (HttpServletResponse)pageContext.getResponse(), getClass().getName(),
								pageContext.getServletConfig().getServletName()); */
							// throw new JspException("Eccezione su colonna nulla [" + rsmd.getColumnName(i) + "] di tipo {" + rsmd.getColumnTypeName(i) + "}" );
							sqlColumnValue = String.valueOf(sqlColumnValue);
							
						} else {
							
							if (sqlColumnValue.length() > MAX_CELL_TEXT_LEN)
								sqlColumnValue = sqlColumnValue.substring(0,MAX_CELL_TEXT_LEN) + "(...)";
							
							// parametrizzare scelta, visualizazione popup o scorrevole
							if (columnType.equals("text") && this.isTextBoxed()) {
								/* 2DO: Utilizzare riferimento a directory impostazioni db */
								String htmlDomId = "tmpId_"+iRowCounter+"_"+i;
								String htmlJsRoutine = "if ("+htmlDomId+".style.display=='block') " + htmlDomId+".style.display='none'; else " + htmlDomId+".style.display='block';";
								sbTableRowDetail.append( "<tr><td colspan=\""+totalColumns+"\" height=\"1\" style=\"height: 1px; padding: 0px;\" bgcolor=\"orange\"><a href=\"javascript:void(0)\" onclick=\""+htmlJsRoutine+"\"><img src=\"../res/gfx/img/plus_blue.gif\" height=\"11\" width=\"11\" border=\"0\" /></a><span style=\"font-size: 8pt;\">["+columnNameQueryLabel+"]</span></td></tr>" );
								
								sbTableRowDetail.append( "<tr><td colspan=\""+totalColumns+"\"><div id=\""+htmlDomId+"\" style=\"display: none;\">"
										+ sqlColumnValue
										+ "</div></td></tr>" );
								
							} else {
								if (this.isHyperlinkColumn(i)) {
									/* 6ba) Dopo aver controllato la validità genero la cella e la particella del valore hyperlink */
									sbTableRow.append( this.makeTableCell(sqlColumnValue,i,rsmd.getColumnCount(),iRowCounter) );
									hyperlinkValue += sqlColumnValue + UtilConstants.PAR_KEY_SEPARATOR;
								} else {
									if (this.isHyperlinkText(i)) {
										/* 6bb) Dopo aver segnalato con un bookmark la posizione del link testuale
										 * valorizzo la variabile del link testuale */
										sbTableRow.append(_bufferBookmark);
										hyperlinkText = sqlColumnValue;
									} else {
										sbTableRow.append( this.makeTableCell(sqlColumnValue,i,rsmd.getColumnCount(),iRowCounter) );
									}
								}
							}
							if (!columnType.equals("text")) {
								if (startColumnHeader) sbTableHeader.append( this.makeTableCellHead(rsmd.getColumnName(i),i) );
							}
						}
					}
				}
				
				if (this.isXslTransform()) {
					String sXml = XmlUtils.tagAtt("data", sbXmlRow.toString(), "row", String.valueOf(iRowCounter) );
					sbXmlPageContent.append( sXml );
				} else {
					/* 7) Preparo il link principale della griglia di visualizzazione */
					String mainLinkColumn = this.makeHyperlink( hyperlinkText, this.getLinkUrl(), hyperlinkValue );
					/* 7a) Identifico il bookmark del link principale e lo sostituisco con il link generato */
					final int cutStart = sbTableRow.toString().indexOf(_bufferBookmark), cutEnd = cutStart+_bufferBookmark.length();
					sbTableRow.replace(cutStart, cutEnd, this.makeTableCell(mainLinkColumn,rsmd.getColumnCount(),iRowCounter) );
					
					/* 8) Genero la riga della griglia */
					sbTableContent.append( this.makeTableRow( sbTableRow, iRowCounter ).toString() );
					sbTableContent.append( sbTableRowDetail.toString() );
				}
				iRowCounter++;
				startColumnHeader = false;
				lastRowsCounted = iRowCounter;
			}
			rs.close();
			ps.close();
		} catch ( SQLException e ) {
			e.printStackTrace();
		} catch ( Exception e ) {
			e.printStackTrace();
		}

		if (this.isXslTransform()) {
			String jdbcData = "<jdbc><querySource>"+lastQuery+"</querySource><pool>MyDbPool</pool></jdbc>\r\n";
			String sXml = XmlUtils.tagAtt("list", sbXmlPageContent.toString(), new String[][]{new String[]{"number","1"},new String[]{"orderKey",sOk},new String[]{"orderVersus",sOv},new String[]{"listBuffer",String.valueOf(RECORD_PER_PAGE)},new String[]{"listRows",String.valueOf(lastRowsCounted-1)},new String[]{"listOffset",sRo}});
			String groupingInfo = ""; // XmlUtils.tag("groupingInfo", sbXmlGroupingInfo.toString() );
			sbXmlPage.append( XmlUtils.tagPage("gridObject", jdbcData + groupingInfo + sXml, (HttpServletRequest)pageContext.getRequest()) );
			return sbXmlPage;
		} else {
			sbTableHeader.append("</thead>");
			sbTableContent.append(sbTableHeader.toString());
			sbTableContent.append("<pre>"+this.buildNavigationPanel(sOk,sOv,sRp,sRo)+"</pre>");
			sbTableContent.append("<!--"+lastQuery+"-->");
			return sbTableContent;
		}
	}
	
	public int doEndTag() throws JspException {

		StringBuffer tagContent = new StringBuffer(1024);
		
		try {
			if (this.isXslTransform()) {
				String xslResult = "";
				Object[] dataModel = new Object[]{ this.getResultsPgsql() , xslTransformation};
				xslResult = XmlTransformer.getInstance().transform(dataModel);
				tagContent.append( xslResult );
			} else {
				tagContent.append( this.makeTable( this.getResultsPgsql() ).toString() );
			}
				
			pageContext.getOut().write( tagContent.toString() );
			
		} catch ( IOException e ) {
			e.printStackTrace();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		// return super.doEndTag();
		return EVAL_PAGE;
	}
}
