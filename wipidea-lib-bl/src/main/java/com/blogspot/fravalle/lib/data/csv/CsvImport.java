/*
 * CsvImport.java - libs (libs.jar)
 * Copyright (C) 2006
 * Source file created on 24-mag-2006
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [CsvImport]
 * 2DO:
 *
 */

package com.blogspot.fravalle.lib.data.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Logger;


/**
 * [][CsvImport]
 * 
 * <p><b><u>2DO</u>:</b>
 * 
 * 
 * @author Francesco Valle - (antares)
 */
public class CsvImport extends Csv {

	static final protected Logger logger = Logger.getLogger(CsvImport.class.getName());
	
	private File fileChannel;
	private boolean isHeadered = false;

	/**
	 * 
	 */
	public CsvImport() {
		super();
	}
	
	/**
	 * 
	 */
	public CsvImport(String url) {
		super();
		this.fileChannel = new File(url);
		logger.info("Starting data import from [" + url + "]");
	}
	
	/**
	 * @throws IOException
	 * 
	 */
	public CsvImport(StringBuffer content) throws IOException {
		super();
		this.fileChannel = File.createTempFile("data",".csv");
		FileWriter fw = new FileWriter(fileChannel);
		fw.write(content.toString());
		fw.flush();
		fw.close();
		fileChannel.deleteOnExit();
		logger.info("Starting data import from [" + fileChannel.getCanonicalPath() + "]");
		this.importData();
	}
	
	private void addColumn(Object reference, String value) {
		if (reference == null)
			reference = new Vector();
		
		if (reference instanceof Vector)
			((Vector)reference).add(value);
		else if (reference instanceof Hashtable)
			((Hashtable)reference).put(new Timestamp(System.currentTimeMillis()),value);
	}
	
	private Object parseColumns(String s) {
		Object rt = null;
		if (!isHeadered)
			rt = new Vector();
		else
			rt = new Hashtable();

		StringTokenizer st = new StringTokenizer(s, this.getDelim());
		while (st.hasMoreTokens())
			addColumn(rt, st.nextToken());

		logger.finest("Columns [" + rt.toString() + "]");
		
		return rt;
	}
	
	public void importData() throws IOException {
		logger.info("Start data import");
		BufferedReader br = new BufferedReader(new FileReader(fileChannel));
		String buffer;
		while ((buffer = br.readLine()) != null)
			add(parseColumns(buffer));
		br.close();
		logger.info("End data import");
	}
	
	public static void main(String[] args) throws IOException {
		CsvImport csv = new CsvImport("/media/VAR/downloads/Charts/statistics.csv");
		csv.importData();
	}
	
}
