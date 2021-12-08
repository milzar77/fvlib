/*
 * CsvControlImport.java - libs (libs.jar)
 * Copyright (C) 2006
 * Source file created on 27-mag-2006
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [CsvControlImport]
 * 2DO:
 *
 */

package com.blogspot.fravalle.lib.data.csv;

import java.io.Serializable;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * [][CsvControlImport]
 * 
 * <p><b><u>2DO</u>:</b>
 * 
 * 
 * @author Francesco Valle - (antares)
 */
public final class CsvControlImport implements Serializable {

	private Vector cols2exclude;
	private Vector rows2exclude;
	private Vector rows2select;
	private Vector cols2filter;
	
	private boolean isSequenced = true;
	private boolean parseNumber = true;
	private boolean filterSensitive;
	private boolean regexpFilter;
	/*private boolean filterOn;
	public boolean isFilterOn() {
		return this.filterOn;
	}*/
	public final boolean isSequenced() {
		return this.isSequenced;
	}
	public final boolean isParseNumber() {
		return this.parseNumber;
	}
	
	public boolean isFilterSensitive() {
		return this.filterSensitive;
	}

	public boolean isRegexpFilter() {
		return this.regexpFilter;
	}
	/*public void addColHeaders(String colHeaders) {
	}*/
	
	public String getExcludedCols(){
		if (cols2exclude!=null && cols2exclude.size()>0){
			String s = cols2exclude.toString().substring(1,cols2exclude.toString().length()-1);
			return s.replaceAll(" ","");
		}else
			return null;
	}

	public void addExcludedCol(int i) {
		if (cols2exclude==null)
			cols2exclude=new Vector();
		cols2exclude.add(String.valueOf(i));
	}

	public void addExcludedCols(String cols) {
		if (cols!=null && !"".equals(cols)) {
			if (cols2exclude==null)
				cols2exclude=new Vector();
			StringTokenizer st = new StringTokenizer(this.parseIntervals(cols),",");
			while (st.hasMoreTokens()) {
				cols2exclude.add(String.valueOf(st.nextToken()));
			}
		}
	}
	
	public void addExcludedCols(int[] cols) {
		if (cols2exclude==null)
			cols2exclude=new Vector();
		for (int i = 0; i < cols.length; i++)
			cols2exclude.add(String.valueOf(cols[i]));
	}
	
	public String getExcludedRows(){
		if (rows2exclude!=null && rows2exclude.size()>0){
			String s = rows2exclude.toString().substring(1,rows2exclude.toString().length()-1);
			return s.replaceAll(" ","");
		}else
			return null;
	}

	public void addExcludedRow(int i) {
		if (rows2exclude==null)
			rows2exclude=new Vector();
		rows2exclude.add(String.valueOf(i));
	}
	
	public void addExcludedRows(String rows) {
		if (rows!=null && !"".equals(rows)) {
			if (rows2exclude==null)
				rows2exclude=new Vector();
			StringTokenizer st = new StringTokenizer(this.parseIntervals(rows),",");
			while (st.hasMoreTokens()) {
				rows2exclude.add(String.valueOf(st.nextToken()));
			}
		}
	}
	
	public void addExcludedRows(int[] rows) {
		if (rows2exclude==null)
			rows2exclude=new Vector();
		for (int i = 0; i < rows.length; i++)
			rows2exclude.add(String.valueOf(rows[i]));
	}

	public String getSelectedRows(){
		if (rows2select!=null && rows2select.size()>0){
			String s = rows2select.toString().substring(1,rows2select.toString().length()-1);
			return s.replaceAll(" ","");
		}else
			return null;
	}

	public void addSelectedRow(int i) {
		if (rows2select==null)
			rows2select=new Vector();
		rows2select.add(String.valueOf(i));
	}

	public void addSelectedRows(String rows) {
		if (rows!=null && !"".equals(rows)) {
			if (rows2select==null)
				rows2select=new Vector();
			StringTokenizer st = new StringTokenizer(this.parseIntervals(rows),",");
			while (st.hasMoreTokens()) {
				rows2select.add(String.valueOf(st.nextToken()));
			}
		}
	}
	
	public void addSelectedRows(int[] rows) {
		if (rows2select==null)
			rows2select=new Vector();
		for (int i = 0; i < rows.length; i++)
			rows2select.add(String.valueOf(rows[i]));
	}
/*
	public int[][] getPeaks() {
		return peaks;
	}
	public void setPeaks(int[][] par) {
		peaks = par;
	}
	*/
	int[][] peaks = new int[2][2];
	public void setPeaksThreshold(int[][] par) {
		peaks = par;
	}
	public void setPeaksThreshold(int[] parLo, int[] parHi) {
		peaks[0] = parLo;
		peaks[1] = parHi;
	}
	public int[][] getPeaksThreshold() {
		return peaks;
	}

	public Vector getFilteredCols(){
		if (cols2filter==null)
			cols2filter = new Vector();
		return cols2filter;
	}

	public void addFilteredCols(int colIndex, String filterName) {
		if (cols2filter==null)
			cols2filter=new Vector();
		CsvControlFilter filter = new CsvControlFilter(colIndex, false);
		filter.add(filterName);
		if (!cols2filter.contains(filter))
			cols2filter.add(filter);
	}

	public void addFilteredCols(final Vector filters, final boolean isRegexp) {
		cols2filter = filters;
		regexpFilter = isRegexp;
	}
	
	/**
	 * @param filters
	 * @deprecated
	 */
	public void addFilteredCols(String filters) {
		if (filters!=null && !"".equals(filters)) {
			if (cols2filter==null)
				cols2filter=new Vector();
			StringTokenizer st = new StringTokenizer(this.parseIntervals(filters),",");
			while (st.hasMoreTokens()) {
				CsvControlFilter filter = new CsvControlFilter(0,false);
				filter.add(String.valueOf(st.nextToken()));
				if (!cols2filter.contains(filter))
					cols2filter.add(filter);
			}
		}
	}
	
	/**
	 * 
	 */
	public CsvControlImport() {
		super();
		isSequenced = true;
		parseNumber = true;
	}
	
	/**
	 * 
	 */
	public CsvControlImport(boolean parSequenced, boolean parParseNumber, boolean fSensitive, boolean fRegexp) {
		super();
		isSequenced = parSequenced;
		parseNumber = parParseNumber;
		filterSensitive = fSensitive;
		regexpFilter = fRegexp;
	}

	final public String parseIntervals(String s1) {
		String s = s1;
		while ( s.indexOf("-") != -1){
			String sx = s.substring(0,s.indexOf("-"));
			sx = sx.substring(sx.lastIndexOf(",")+1);
			s = s.substring(s.indexOf("-")+1);
			String sy = "";
			if (s.indexOf(",")!=-1)
				sy = s.substring(0,s.indexOf(","));
			else
				sy = s;
			String out = this.extractIntervals(sx+"-"+sy);
			s1 = s1.replaceAll(sx+"-"+sy,out);
		}
		return s1;
	}
	
	final private String extractIntervals(String s) {
		int cut = s.indexOf("-");
		String sp1 = s.substring(0,cut);
		String sp2 = s.substring(cut+1);
		return this.get(sp1,sp2);
	}
	
	final private String get(String s1, String s2){
		StringBuffer sb = new StringBuffer(1024);
		int x = Integer.parseInt(s1);
		int y = Integer.parseInt(s2);
		sb.append(x+",");
		for (;x < y ;) {
			sb.append((++x) +",");
		}
		return sb.toString().substring(0,sb.toString().length()-1);
	}
	
	/*static final public void main(String[] args) {
		System.out.println(new CsvControlImport().parseIntervals("50-74,89-98"));
	}*/

}
