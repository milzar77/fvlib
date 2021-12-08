/*
 * ScrollableRecordUI.java - jappslist (jappslist.jar)
 * Copyright (C) 2003
 * Source file created on 17-nov-2003
 * Author: Francesco Valle <info@weev.it>
 * http://www.weev.it
 *
 */

package com.blogspot.fravalle.util.dev;

public final class Dev {
    
	public final static boolean append = true;
	
	public final static int DEBUG_MORTAL_ERROR = -2;
	public final static int DEBUG_ERROR = -1;
	public final static int DEBUG_NONE = 0;
	public final static int DEBUG_LIGHT = 1;
	public final static int DEBUG_MEDIUM = 2;
	public final static int DEBUG_HEAVY = 3;
	public final static int DEBUG_LOG = 4;

	public static int DEBUG_DEFAULT = DEBUG_NONE;
	
	private final static java.io.File flog = new java.io.File( "./application.log" );
	public static java.io.PrintWriter pwo;
	
	public static boolean isEclipseDevRuntime = true;
	public static boolean isError = true;
	
	private static String eclipseDevRuntimePath = ".//bin/";

	public static String getRunTimePath(boolean isEclipseDevRuntime) {
	    if (isEclipseDevRuntime)
	        return eclipseDevRuntimePath;
	    else
	        return ".//";
	}
	
	
	private static void writeLog(String log) throws Exception {
		pwo=new java.io.PrintWriter(new java.io.BufferedWriter(new java.io.FileWriter(flog, append)));
		pwo.print( log+"\r\n" );
		pwo.flush();
		pwo.close();
	}

	private static void consolePrinterErr(String text) {
		System.err.println( text );
	}

	private static void consolePrinterOut(String text) {
		System.out.println( text );
	}
	
/*	public static void log(MessageResource logMex) {
		debug(DEBUG_HEAVY, logMex);
	} */
	
	public static void log(String logMex) {
		isError = false;
		debug(DEBUG_LOG, logMex);
	}
	
	public static void debug(String debugMex) {
		isError = false;
		debug(DEBUG_HEAVY, debugMex);
	}
	
	public static void debug(final int debugLevel, String debugMex) {
		isError = false;
		debug(debugLevel, new Exception(debugMex));
	}

	public static void debug(Exception e) {
		isError = true;
		e.printStackTrace();
	}
	
	/**
	 * @param debugLevel Livello di notifica
	 * @param debugMex Messaggio di notifica
	 */
    public static void debug(final int debugLevel, Exception e) {
		java.util.Date debugDate = new java.util.Date();
		String dateHeader = debugDate + " -- ";
    	switch ( debugLevel ) {
			case DEBUG_MORTAL_ERROR :
				if (isError) e.printStackTrace();
				consolePrinterErr( dateHeader + e.getMessage() );
				try { writeLog( dateHeader + e.getMessage() ); }
				catch (Exception ex) {ex.printStackTrace();}
				System.exit(1);
				break;
			case DEBUG_ERROR :
				if (isError) e.printStackTrace();
				consolePrinterErr( dateHeader + e.getMessage() );
				try { writeLog( dateHeader + e.getMessage() ); }
				catch (Exception ex) {ex.printStackTrace();}
				break;
            case DEBUG_NONE :
                break;
			case DEBUG_LIGHT :
				consolePrinterOut( dateHeader + e.getMessage() );
				break;
			case DEBUG_MEDIUM :
				if (isError) e.printStackTrace();
				consolePrinterOut( dateHeader + e.getMessage() );
				break;
			case DEBUG_HEAVY :
				if (isError) e.printStackTrace();
				consolePrinterErr( dateHeader + e.getMessage() );
			    try { writeLog( dateHeader + e.getMessage() ); }
			    catch (Exception ex) {ex.printStackTrace();}
				break;
			case DEBUG_LOG :
				if (isError) e.printStackTrace();
				consolePrinterOut( dateHeader + e.getMessage() );
			    /* try { writeLog( new java.util.Date() + " -- " + debugMex ); }
			    catch (Exception ex) {ex.printStackTrace();} */
				break;
            default :
                break;
        }
            
    }
    
}