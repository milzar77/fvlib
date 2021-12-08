/*
 * PageService.java - jsptags (jsptags.jar)
 * Copyright (C) 2004
 * Source file created on 16-ott-2004
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [PageService]
 * 2DO:
 *
 */

package com.blogspot.fravalle.j2ee.web.service;

import java.util.Vector;
// import com.blogspot.fravalle.lib.data.db.DbProperties;
// import com.blogspot.fravalle.lib.util.sql.SQLSelect;

/**
 * @author antares
 */
public class PageService {
	
	public static Vector readPage() {
		/*
		DbProperties dbProp = new DbProperties(new File("D://Apps//EclipseSDK//3.0M9//workspace//jsptags//WEB-INF//logs//pool-connections.log"), "mysql.");
		Vector vt = null;
		 try {
			
			SQLSelect dbs = new SQLSelect(dbProp, false, false);
			dbs.select("SELECT * FROM ARTICOLI WHERE idart=18", false, false, 3);
			vt = (Vector)dbs.getResults();
			
		 } catch (Exception e) {
		 	// throws new Exception("Errore nella connessione al database");
		 } finally {
		 	return vt;
		 }
		 */
		return null;
		
	}

	
	
}
