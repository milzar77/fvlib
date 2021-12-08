/*
 * ScrollableRecordUI.java - jappslist (jappslist.jar)
 * Copyright (C) 2003
 * Source file created on 17-nov-2003
 * Author: Francesco Valle <info@weev.it>
 * http://www.weev.it
 *
 */

package com.blogspot.fravalle.lib.monitor;


/**
 * Classe di monitoraggio applicativo per debug ed informazioni storiche
 * @author antares
 */
public final class Monitor implements IMonitor {

	/**
	 * Parametro di controllo creazione file di logging, true = non creare file, false = creare file
	 * TODO: rendere il parametro configurabile con dati caricati a db
	 */
	public final static boolean append = true;

	/**
	 * Parametro di impostazione livello predefinito di debugging
	 * TODO: rendere il parametro configurabile con dati caricati a db
	 */
	public static int debugDefault = DEBUG_NONE;
	
	/**
	 * Parametro di impostazione posizione file di logging 
	 * TODO: rendere il parametro configurabile con dati caricati a db
	 */
	public final static String LOG_FILE = "./application.log";
	
	
	private final static java.io.File flog = new java.io.File( LOG_FILE );
	
	private static java.io.PrintWriter pwo;
	
	/**
	 * Parametro di controllo percorso di esecuzione applicazione, true = ambiente IDE
	 */
	public static boolean isEclipseDevRuntime = true;
	
	/**
	 * Parametro di impostazione percorso di esecuzione interno ad ambiente IDE
	 */
	private static String eclipseDevRuntimePath = ".//bin/";
	
	public static boolean isError = true;

	public static boolean isDebuggingSession = true;

	/**
	 * Metodo di recupero percorso interno ad ambiente IDE
	 * @param isEclipseDevRuntime
	 * parametro di controllo percorso interno ad ambiente IDE
	 * @return
	 * percorso di esecuzione interno all'ambiente IDE
	 */
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
		if(isDebuggingSession)
			System.err.println( text );
	}

	private static void consolePrinterOut(String text) {
		System.out.println( text );
	}

	/*
	public static void log(MessageResource logMex) {
		debug(DEBUG_HEAVY, logMex);
	}
	*/
	/**
	 * Metodo di scrittura log testuale
	 * @param logMex
	 * messaggio di notifica
	 */
	public static void log(String logMex) {
		isError = false;
		debug(OUTPUT_LOG, logMex);
	}

	/**
	 * Metodo di scrittura log basato su codice errore gestito dalla classe ErrorCodes tramite il file
	 * di proprietà /res/errors.properties
	 * @param errorCode
	 * codice di errore
	 * @see ErrorCodes
	 */
	public static void log(long errorCode) {
		isError = false;
		debug(OUTPUT_LOG, ErrorCodes.error(errorCode));
	}
	
	/**
	 * Metodo di scrittura log basato su codice errore gestito dalla classe ErrorCodes tramite il file
	 * di proprietà /res/errors.properties
	 * @param errorCode
	 * codice di errore
	 * @param objects
	 * oggetti da tracciare
	 * @see ErrorCodes
	 */
	public static void log(long errorCode, Object[] objects) {
		isError = false;
		String message = ErrorCodes.error(errorCode);
		for (int i =0; i < objects.length; i++) {
			message += "\r\n";
			if (objects[i]!=null)
				message += objects[i].getClass().getName() + " : " + String.valueOf(objects[i]);
			else
				message += "\t" + String.valueOf(objects[i]);
		}
		debug(OUTPUT_LOG, message);
	}
	
	/**
	 * Metodo di scrittura debug basato su codice errore gestito dalla classe ErrorCodes tramite il file
	 * di proprietà /res/errors.properties, livello di notifica alto
	 * @param errorCode
	 * codice di errore
	 * @param e
	 * eccezione tracciata
	 * @see ErrorCodes
	 */
	public static void debug(long errorCode, Exception e) {
		isError = false;
		debug(DEBUG_HEAVY, ErrorCodes.error(errorCode));
	}
	
	/**
	 * Metodo di scrittura debug testuale, livello di notifica alto
	 * @param debugMex
	 * messaggio di notifica
	 */
	public static void debug(String debugMex) {
		isError = false;
		debug(DEBUG_HEAVY, debugMex);
	}
	
	/**
	 * Metodo di scrittura debug testuale con livello di notifica impostabile
	 * @param debugLevel
	 * livello di notifica
	 * @param debugMex
	 * messaggio di notifica
	 */
	public static void debug(final int debugLevel, String debugMex) {
		isError = false;
		debug(debugLevel, new Exception(debugMex));
	}

	/**
	 * Metodo di tracciamento eccezione
	 * @param e
	 * eccezione tracciata
	 */
	public static void debug(Exception e) {
		isError = true;
		e.printStackTrace();
	}
	
	/**
	 * Metodo di tracciamento eccezione con livello di notifica impostabile
	 * @param debugLevel
	 * livello di notifica
	 * @param debugMex
	 * eccezione tracciata
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
			case OUTPUT_LOG :
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