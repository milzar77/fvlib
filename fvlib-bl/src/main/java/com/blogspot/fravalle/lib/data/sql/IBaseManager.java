/*
 * IBaseManager.java - jappframework (jappframework.jar)
 * Copyright (C) 2005
 * Source file created on 5-dic-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [IBaseManager]
 * 2DO:
 *
 */

package com.blogspot.fravalle.lib.data.sql;

import com.blogspot.fravalle.lib.bl.business.exceptions.FrameworkDatabaseException;
import com.blogspot.fravalle.lib.bl.business.exceptions.FrameworkFatalException;

/**
 * @author antares
 */
public interface IBaseManager {
	public final String PROP_POOLNAME = "dbPoolName";
	public final int DB_SERVER_UNDEFINED = -1;
	public final int DB_SERVER_OTHER = 0;
	public final int DB_SERVER_MYSQL = 1;
	public final int DB_SERVER_POSTGRESQL = 2;
	
	public final String PARSE_MYSQL = "mysql";
	public final String PARSE_POSTGRESQL = "postgresql";
	
	public final String SHOW_DB = "show_database:";
	public final String SHOW_DB_POSTGRESQL = "SELECT datname FROM pg_database WHERE datacl IS NULL";
	public final String SHOW_DB_MYSQL = "SHOW DATABASES";

	public final String SHOW_TABLES = "show_tables:";
	public final String SHOW_TABLES_POSTGRESQL = "SELECT relname FROM pg_class WHERE relkind = 'r' AND relowner = 100 AND relacl IS NULL";
	public final String SHOW_TABLES_MYSQL = "SHOW TABLES FROM ";
	
	public Object getData(SearchConditions search) throws FrameworkFatalException, FrameworkDatabaseException;
	
	public String getServerType(int serverType);
	
	public int getDbServer();
	
	public void clearConnection();
	
	public Object getAvailableServers(Object o);
	
}
