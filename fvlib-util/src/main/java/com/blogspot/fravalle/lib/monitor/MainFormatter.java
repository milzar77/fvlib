/*
 * MainFormatter.java - jappframework (jappframework.jar)
 * Copyright (C) 2006
 * Source file created on 9-feb-2006
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [MainFormatter]
 * 2DO:
 *
 */

package com.blogspot.fravalle.lib.monitor;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;


/**
 * @author francesco
 */
public class MainFormatter extends Formatter {
	Date						dat				= new Date();
	private final static String	format			= "{0,date} {0,time}";
	private MessageFormat		formatter;
	private Object				args[]			= new Object[1];
	// Line separator string. This is the value of the line.separator
	// property at the moment that the SimpleFormatter was created.
	private String				lineSeparator	= System.getProperty("line.separator"); //FIX:(String)java.security.AccessController
														//.doPrivileged(new sun.security.action.GetPropertyAction(
														//		"line.separator"));

	/**
	 * Format the given LogRecord.
	 * 
	 * @param record
	 *            the log record to be formatted.
	 * @return a formatted log record
	 */
	public synchronized String format(LogRecord record) {
		StringBuffer sb = new StringBuffer();
		// Minimize memory allocations here.
	
		if (record.getLoggerName()!=null)
			sb.append("["+record.getLoggerName()+"]");
		else
			sb.append("{anonymous}");
		sb.append(" ");
		if (record.getSourceClassName() != null) {
			sb.append(""+record.getSourceClassName()+"");
		} else {
			sb.append(record.getLoggerName());
		}
		if (record.getSourceMethodName() != null) {
			sb.append(" ");
			sb.append("["+record.getSourceMethodName()+"]");
		}

		sb.append(" @ ");
		dat.setTime(record.getMillis());
		args[0] = dat;
		StringBuffer text = new StringBuffer();
		if (formatter == null) {
			formatter = new MessageFormat(format);
		}
		formatter.format(args, text, null);
		sb.append(text);
		
		sb.append(lineSeparator);
		String message = formatMessage(record);
		sb.append(record.getLevel().getLocalizedName());
		sb.append(": ");
		sb.append(message);
		sb.append(lineSeparator);
		if (record.getThrown() != null) {
			try {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				record.getThrown().printStackTrace(pw);
				pw.close();
				sb.append(sw.toString());
			}
			catch ( Exception ex ) {}
		}
		return sb.toString();
	}
}