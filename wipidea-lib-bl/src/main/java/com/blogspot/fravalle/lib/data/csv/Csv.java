/*
 * Csv.java - testing (testing.jar)
 * Copyright (C) 2005
 * Source file created on 14-giu-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [Csv]
 * TODO:
 *
 */

package com.blogspot.fravalle.lib.data.csv;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * @author antares
 */
abstract public class Csv extends Vector {

	private final int FIELD_TYPE_STR = 0,FIELD_TYPE_INT = 1;
	protected final String ROW_LINEBREAK = System.getProperty("line.separator");
	private final String FIELD_DELIMITER = ",";
	private final String[] FIELD_TYPE_DELIMITER = new String[]{"\"","'"};
	
	private String delim(String content, int type) {
		return FIELD_TYPE_DELIMITER[type] + content + FIELD_TYPE_DELIMITER[type];
	}

	protected String getDelim() {
		return FIELD_DELIMITER;
	}
	
	protected String readLineFromHashtable(Hashtable h) {
		StringBuffer sb = new StringBuffer(1024);
		for (Enumeration e = h.keys(); e.hasMoreElements();) {
			String k = (String)e.nextElement();
			String v = (String)h.get(k);
			String s = delim(v, FIELD_TYPE_STR) ;
			sb.append( s + FIELD_DELIMITER);
		}
		int cut = sb.lastIndexOf(FIELD_DELIMITER);
		sb.deleteCharAt(cut);
		return sb.toString() + ROW_LINEBREAK;
	}
	
	public void refill(Vector vt){
		this.removeAllElements();
		for (Enumeration e = vt.elements(); e.hasMoreElements();){
			this.add(e.nextElement());
		}
	}
	
}
