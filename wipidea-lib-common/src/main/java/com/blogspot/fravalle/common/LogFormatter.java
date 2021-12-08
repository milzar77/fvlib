/*
 * LogFormatter.java - ChartBuilder (ChartBuilder.jar)
 * Copyright (C) 2006
 * Source file created on 13-giu-2006
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [LogFormatter]
 * 2DO:
 *
 */

package com.blogspot.fravalle.common;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/**
 * [][LogFormatter]
 * 
 * <p>
 * <b><u>2DO </u>: </b>
 * 
 * 
 * @author Francesco Valle - (antares)
 */
final public class LogFormatter extends SimpleFormatter {

    static final public void main(String[] params) {
        System.out.println(" === Environment check already run! === ");
    }
    
    static {
        checkEnvironment();
    }


    /**
     * 
     */
    private static void checkEnvironment() {
        System.out.println(" === Environment check running... === ");
        Properties sysProps = System.getProperties();
        sysProps.list( System.out );
        LogChannel.buildDb();
    }
    
	Date dat = new Date();
	private final static String format = "{0,date} {0,time}";
	private MessageFormat formatter;
	private Object args[] = new Object[1];
	private String lineSeparator = (String) java.security.AccessController
			.doPrivileged(new sun.security.action.GetPropertyAction(
					"line.separator"));

	/**
	 * Format the given LogRecord disposing message text in a plain format with
	 * one line per row, furthermore if there is a a message text that goes over
	 * the 80 columns per row it breaks the message header and the message body
	 * in two lines.
	 * 
	 * @param record
	 *            the log record to be formatted.
	 * @return a formatted log record
	 */
	public synchronized String format(LogRecord record) {
		StringBuffer sb = new StringBuffer();
		// Minimize memory allocations here.
		dat.setTime(record.getMillis());
		args[0] = dat;
		StringBuffer text = new StringBuffer();
		if (formatter == null) {
			formatter = new MessageFormat(format);
		}
		formatter.format(args, text, null);

		sb.append("[");
		sb.append(record.getLevel().getLocalizedName());
		sb.append("]");
		sb.append(" ");

		sb.append(text);
		sb.append(" ");
		if (record.getSourceClassName() != null) {
			sb.append(record.getSourceClassName());
		} else {
			sb.append(record.getLoggerName());
		}
		if (record.getSourceMethodName() != null) {
			sb.append(" ");
			sb.append(record.getSourceMethodName());
		}

		sb.append(": ");

		String message = formatMessage(record);
		if ((message + sb.toString()).length() > 80)
			sb.append(lineSeparator);

		sb.append(message);
		sb.append(lineSeparator);
		if (record.getThrown() != null) {
			try {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				record.getThrown().printStackTrace(pw);
				pw.close();
				sb.append(sw.toString());
			} catch (Exception ex) {
			}
		}
		return sb.toString();
	}

}