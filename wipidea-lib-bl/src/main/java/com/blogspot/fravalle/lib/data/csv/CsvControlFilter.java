/*
 * CsvControlFilter.java - libs (libs.jar)
 * Copyright (C) 2006
 * Source file created on 11-giu-2006
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [CsvControlFilter]
 * 2DO:
 *
 */

package com.blogspot.fravalle.lib.data.csv;

import java.util.Vector;

/**
 * [][CsvControlFilter]
 * 
 * <p><b><u>2DO</u>:</b>
 * 
 * 
 * @author Francesco Valle - (antares)
 */
public class CsvControlFilter extends Vector {
	int column;
	boolean regexpFilter;
	//public boolean caseSensitive;
	public CsvControlFilter(final int colIndex, final boolean isRegexp) {
		super();
		this.column = colIndex;
		regexpFilter = isRegexp;
	}
	final public int getColumn(){
		return column;
	}
	public boolean isRegexpFilter() {
		return this.regexpFilter;
	}
}
