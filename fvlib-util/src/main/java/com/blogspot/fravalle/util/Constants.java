/*
 * Constants.java - jappframework (jappframework.jar)
 * Copyright (C) 2005
 * Source file created on 11-dic-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [Constants]
 * 2DO:
 *
 */

package com.blogspot.fravalle.util;

/**
 * @author antares
 */
public abstract interface Constants {

	public final String _fileDatabasePath = "/res/db.properties"; // SettingRes.get("mdi.db.configuration.1");
	
	public final String _fileDatabaseForDirectConnection = "/res/db-direct.properties"; // SettingRes.get("mdi.db.configuration.2");
	
	
	public final String _logDefaultTag = "[LOG]"; // UIRes.getLabel("param.label.logging.default");
	public final String _logHistoryTag = "[LOG-HISTORY]"; //UIRes.getLabel("param.label.logging.history");
	public final String _logWarnTag = "[LOG-WARNING]"; // UIRes.getLabel("param.label.logging.warn");
	public final String _logEventTag = "[LOG-EVENTS]"; // UIRes.getLabel("param.label.logging.event");
	public final String _logInternalTag = "[LOG-INTERNAL]"; // UIRes.getLabel("param.label.logging.internal");

	/* ATTENZIONE: queste variabili vengono richiamate durante il caricamento della stessa classe */
	public final String _logDefaultDef = ""; // UIRes.getLabel("log.default");
	public final String _logHistoryDef = ""; // UIRes.getLabel("history.default");
	public final String _logWarnDef = ""; // UIRes.getLabel("warn.default");
	public final String _logEventDef = ""; // UIRes.getLabel("event.default");
	public final String _logInternalDef = ""; // UIRes.getLabel("internal.default");
	
	/**
	 * Riferimento a metodo fittizio della classe com.blogspot.fravalle.lib.bl.beans.Model
	 * @see com.blogspot.fravalle.lib.bl.beans.Model#get_runtimeKey()
	 */
	public final String CARD_RUNTIME_KEY = "_runtimeKey:Card key";
	
}
