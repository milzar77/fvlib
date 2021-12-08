/*
 * CsvAdapter.java - libs (libs.jar)
 * Copyright (C) 2006
 * Source file created on 25-mag-2006
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [CsvAdapter]
 * 2DO:
 *
 */

package com.blogspot.fravalle.lib.data.csv;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Logger;


/**
 * [][CsvAdapter]
 * 
 * <p>
 * <b><u>2DO </u>: </b>
 * 
 * 
 * @author Francesco Valle - (antares)
 */
final public class CsvAdapter {

	static final protected Logger logger = Logger.getLogger(CsvAdapter.class.getName());
	
	final static private int PLOW = 0, PHIGH = 1;
	
	private int counter = 0;
	static private int lineCounter = 0;

	static private char inputDelimiter = ',';
	static private char outputDelimiter = ',';

	static private int[] notANumber = new int[1000];

	final static private String cleanContent(String s, int counter, boolean parseNumber) {
		/*
		 * if ( (s.indexOf("%") + s.indexOf("/") + s.indexOf(".") ) > 0 ) s =
		 * "\"" + s + "\"";
		 */
		/*
		 * if (s.indexOf(".")!=-1) s = s.replaceAll("\\.",",");
		 */
		if (s.indexOf("%") != -1)
			s = s.replaceAll("%", "");
		if (parseNumber) {
			try {
				Float fObj = new Float(s);
			} catch (NumberFormatException e) {
				notANumber[counter] = counter;
			}
			if (notANumber[counter] == counter)
				return "";
		}
		return s + outputDelimiter;
	}

	final static private String prepare(int i) {
		return ","+String.valueOf(i)+",";
	}
	final static private String prepare(String s) {
		return ","+String.valueOf(s)+",";
	}
	
	static final String REM = "_rem_";
	
	final static private String parseColumns(String s, String excludeCol, boolean isSequence, boolean parseNumber, int[][] peaks) {
		int counter = 0;
		String str = "";
		String compareExclude = prepare(excludeCol);
		StringTokenizer st = new StringTokenizer(s, String
				.valueOf(inputDelimiter));
		
		while (st.hasMoreElements()) {
			counter++;
			String compareCounter = prepare(""+counter);
			String token = st.nextToken();
			if (compareExclude.replace(',',' ').indexOf(compareCounter.replace(',',' ')) == -1){
				String rem = "";
				try{
					if ((peaks[PLOW][0] + peaks[PLOW][1]) > 0) {
							if (new Double(token).doubleValue()<peaks[PLOW][1]){
								token = "0";
								rem = REM;
							}
					}
					if (peaks[PHIGH][0] + peaks[PHIGH][1] > 0) {
						if (new Double(token).doubleValue()>peaks[PHIGH][1]){
							token = "0";
							rem = REM;
						}
					}
				}catch (NumberFormatException e) {
					//token = "0,";
				}
				
				str += rem + cleanContent(token, counter, parseNumber);
			}
		}
		
		if ("".equals(str) || str == null)
			return "";
		
		String rt = str.substring(0, str.length() - 1);
		
		
		if (isSequence)
			return String.valueOf(lineCounter++) + String.valueOf(outputDelimiter)
				+ rt + "\r\n";
		else
			return rt + "\r\n";
	}
	
	static private Reader getData(String s) throws FileNotFoundException {
		File tFile = new File(s);
		if (tFile.exists() && tFile.isFile())
			return new FileReader(s);
		else
			return new StringReader(s);
	}

	static public Csv mergeCsv(Csv[] csv, int col) throws IOException {	
		
		StringBuffer sb = new StringBuffer(1024);
		CsvImport merge = new CsvImport();
		try {
			if (csv[0]==null)
				return csv[0]; //new CsvImport();
			for (int row = 0; row < csv[0].size(); row++) {
				java.util.Vector newRow = new Vector();
				newRow.add(((Vector)csv[0].elementAt(row)).elementAt(0));
				for (int e = 0; e < csv.length; e++) {
					java.util.Vector current = (java.util.Vector)csv[e].elementAt(row);
					newRow.add(current.elementAt(col));
				}
				merge.add(newRow);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return merge;
	}
	
	static public Csv getCsv(String content, CsvControlImport config) throws IOException {
		StringBuffer sb = CsvAdapter.rewriteCsv2Buffer(content, config);
		CsvImport csv = new CsvImport(sb);	
		return csv;
	}
	
	static public Csv getCsv(String content) throws IOException {
		StringBuffer sb = CsvAdapter.rewriteCsv2Buffer(content, new CsvControlImport());
		CsvImport csv = new CsvImport(sb);	        
		return csv;
	}
	/*
	static public StringBuffer rewriteCsv2Buffer(String s) {
		return rewriteCsv2Buffer(s, "");
	}
	static public StringBuffer rewriteCsv2Buffer(String s, String excludeCol) {
		return rewriteCsv2Buffer(s, excludeCol, "");
	}
	static public StringBuffer rewriteCsv2Buffer(String s, String excludeCol, String excludeRow) {
		return rewriteCsv2Buffer(s, excludeCol, excludeRow, null, true, true);
	}
	static public StringBuffer rewriteCsv2Buffer(String s, String excludeCol, String excludeRow, String selectRow, boolean isSequence, boolean parseNumber, CsvControlImport config) {
	*/
	
	static public StringBuffer rewriteCsv2Buffer(String s, CsvControlImport config) {
		String excludeCol = config.getExcludedCols(); // format: "4,5,6,7,8,9,10";
		logger.fine("DEBUG EXCLUDE COL: "+excludeCol);
		String excludeRow = config.getExcludedRows();
		logger.fine("DEBUG EXCLUDE ROW: "+excludeRow);
		String selectRow = config.getSelectedRows();
		logger.fine("DEBUG SELECT ROW: "+selectRow);
		boolean isSequence = config.isSequenced();
		logger.fine("DEBUG SEQUENCE: "+isSequence);
		boolean parseNumber = config.isParseNumber();
		logger.fine("DEBUG PARSE: "+parseNumber);
		
		StringBuffer sb = new StringBuffer(1024);
		
		try {
			BufferedReader br = new BufferedReader( getData(s) );
			
			String buffer;
			int rowCounter = 0;
			
			while ((buffer = br.readLine()) != null) {
				
				rowCounter++;
				
				boolean isFiltered = isFiltered(buffer, config);
				/*if (config.isFilterOn()) {
					if (isFiltered)
						continue;
				} else {*/
				//System.out.println("DIM:"+config.getFilteredCols() + "---" + isFiltered);
					if (!isFiltered && config.getFilteredCols().size()>0)
						continue;
				//}

				String srow = parseColumns(buffer, excludeCol, isSequence, parseNumber, config.getPeaksThreshold());
				
				if (srow.indexOf(REM)==-1){
					if (selectRow == null && prepare(excludeRow).indexOf(prepare(rowCounter)) == -1)
						sb.append( srow );
					else if (prepare(selectRow).indexOf(prepare(rowCounter)) != -1)
						sb.append( srow );
				}
				
			}

			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb;
	}
	
	static final private boolean isFiltered(String buffer, CsvControlImport config) {
		/* da implementare controllo colonna */
		//String[] cols = buffer.split(",");
		if (!config.isFilterSensitive())
			buffer = buffer.toLowerCase();
		boolean isFiltered = false;
		Vector vt = config.getFilteredCols();
		for (Enumeration e = vt.elements(); e.hasMoreElements();){
			CsvControlFilter filters = (CsvControlFilter)e.nextElement();
			for (Enumeration e1 = filters.elements(); e1.hasMoreElements();){
				String s = String.valueOf(e1.nextElement());
				if (!config.isFilterSensitive())
					s = s.toLowerCase();
				if (config.isRegexpFilter()) {
					if ( buffer.matches(s) ) {
						isFiltered = buffer.matches(s);
						break;
					}
				} else {
					if ( buffer.indexOf(s)!=-1 ) {
						isFiltered = buffer.indexOf(s)!=-1;
						break;
					}
				}
			}
		}
		return isFiltered;
	}
	
	static public Reader rewriteCsv(String s) {
		int last = 117; // getLastRow(content);
		boolean isSequence = true;
		String excludeCol = "1,2,4,5,6,7,8,9,10";
		int[] excludeRow = {1,last};
		BufferedReader brCleaned = null;
		
		try {
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			BufferedReader br = new BufferedReader( getData(s) );
			String buffer;
			int rowCounter = 0;
			while ((buffer = br.readLine()) != null) {
				rowCounter++;
				if (String.valueOf(excludeRow+",").indexOf(String.valueOf(rowCounter+",")) == -1)
					baos.write( parseColumns(buffer, excludeCol, isSequence, true, new int[2][2]).getBytes() );
			}
			br.close();
			
			brCleaned = new BufferedReader(new StringReader(
					"ID,value\r\n"
							+ baos.toString()));
			
			logger.fine(baos.toString());

			baos.flush();
			baos.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return brCleaned;

	}

}