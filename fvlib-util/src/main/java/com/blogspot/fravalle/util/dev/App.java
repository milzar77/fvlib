/*
 * ScrollableRecordUI.java - jappslist (jappslist.jar)
 * Copyright (C) 2003
 * Source file created on 17-nov-2003
 * Author: Francesco Valle <info@weev.it>
 * http://www.weev.it
 *
 */

package com.blogspot.fravalle.util.dev;

public final class App {
    
	public final static boolean append = true;
	
	public final static int DEBUG_MORTAL_ERROR = -2;
	public final static int DEBUG_ERROR = -1;
	public final static int DEBUG_NONE = 0;
	public final static int DEBUG_LIGHT = 1;
	public final static int DEBUG_MEDIUM = 2;
	public final static int DEBUG_HEAVY = 3;

	public static int DEBUG_DEFAULT = DEBUG_NONE;
	
	private final static java.io.File flog = new java.io.File( "./application.log" );
	public static java.io.PrintWriter pwo;
	
	public static boolean isEclipseDevRuntime = true;
	
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
	
	public static void debug(final int debugLevel, Exception e) {
		debug(debugLevel, (String)e.getMessage());
	}
	/**
	 * @param debugLevel Livello di notifica
	 * @param debugMex Messaggio di notifica
	 */
    public static void debug(final int debugLevel, String debugMex) {
        
        switch ( debugLevel ) {
			case DEBUG_MORTAL_ERROR :
				System.err.println( debugMex );
				try { writeLog( new java.util.Date() + " -- " + debugMex ); }
				catch (Exception ex) {ex.printStackTrace();}
				System.exit(1);
				break;
			case DEBUG_ERROR :
				System.err.println( debugMex );
				try { writeLog( new java.util.Date() + " -- " + debugMex ); }
				catch (Exception ex) {ex.printStackTrace();}
				break;
            case DEBUG_NONE :
                break;
			case DEBUG_LIGHT :
                System.out.println( debugMex );
				break;
			case DEBUG_MEDIUM :
			    System.out.println( new java.util.Date() + " -- " + debugMex );
				break;
			case DEBUG_HEAVY :
			    // file&console output
			    System.out.println( debugMex );
			    try { writeLog( new java.util.Date() + " -- " + debugMex ); }
			    catch (Exception ex) {ex.printStackTrace();}
				break;
            default :
                break;
        }
            
    }
    
    
}