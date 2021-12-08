/*
 * ExternalConnection.java - jappframework (jappframework.jar)
 * Copyright (C) 2005
 * Source file created on 18-dic-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [ExternalConnection]
 * 2DO:
 *
 */

package com.blogspot.fravalle.lib.bl.business;

import com.blogspot.fravalle.lib.bl.business.exceptions.FrameworkDatabaseException;
import com.blogspot.fravalle.lib.bl.business.exceptions.FrameworkFatalException;
import com.blogspot.fravalle.lib.data.sql.SearchConditions;
import com.blogspot.fravalle.lib.gui.AWindow;

/**
 * @author antares
 */
public abstract class ExternalConnection {

	protected AWindow getMDIContainer() {
		return AWindow.getWindowContainer();
	}

	public Object dataChannel(SearchConditions search) throws FrameworkFatalException, FrameworkDatabaseException {
		if (search.getPoolName() != null) {
			getMDIContainer().getDatabasePoolManager().setPool( search.getPoolName() );
			if (search.isUpdate())
				return getMDIContainer().getDatabasePoolManager().preparedBeanUpdate(search);
			else
				return getMDIContainer().getDatabasePoolManager().getData(search);
		} else
			return getMDIContainer().getDatabaseDirectManager().getData(search);
	}
	
	public Object dataChannel(SearchConditions search, String poolname) throws FrameworkFatalException, FrameworkDatabaseException {
		getMDIContainer().getDatabasePoolManager().setPool(poolname);
		return getMDIContainer().getDatabasePoolManager().getData(search);
	}

	
	/*
	   synchronized public void select(String query, String beanModelUrl, boolean closeConnection, int totalResults) {
    	
       vt = new java.util.Vector();
  
	
       boolean firstCheck = false;
	
	   try {
			
		   setMaxResults(totalResults);
			
		   query = conn.nativeSQL(query);
	
		   Statement stmt = conn.createStatement();
		   ResultSet rs = stmt.executeQuery(query);
	
				   int scroller_position = 0;
	                
				   int cursore = 0;
	
				   while(rs.next()) {
	
					   scroller_position++;
					   
				   if (scroller_position >= getCursorPos()) {
				   ResultSetMetaData rsmd = rs.getMetaData();
				   int totalColumns = rsmd.getColumnCount();
					
				   java.util.Vector vtRecord = new java.util.Vector();
				   Hashtable recordSingolo = new Hashtable();
					
				   if (firstCheck) hsTableProperties = new Hashtable();
					
				   for (int i=1; i<=totalColumns;i++) {
					   String columnName = rsmd.getColumnName(i);
					   String columnTypeName = rsmd.getColumnTypeName(i);
						
					   String tableName = rsmd.getTableName(i);
					   if (tableName!=null) {
						   if (tableName.equals("")) tableName = "";
						   else tableName += ".";
					   } else {
						   tableName = "";
					   }
						
					   Object columnData = (Object) rs.getObject(columnName);
					   if (columnData==null) columnData = new Object();
						
	
					   // recordSingolo.put((tableName + columnName), (Object) columnData);
					   recordSingolo.put("_"+columnName, (Object) columnData);
						
					   if (firstCheck) hsTableProperties.put((tableName + columnName), (String)columnTypeName);
					
				   }
				   
				   
		
				   if (cursore < getMaxResults()) {
					   // if (useOnlyVector) vt.addElement( (java.util.Vector) vtRecord );
					   // else vt.addElement( (Hashtable)recordSingolo );
				       record = (IRecord)Class.forName( beanModelUrl ).newInstance();
				       
				       record.beanFiller((Hashtable)recordSingolo);
				       vt.addElement( (IRecord)record );
						
				   } else {
					   break;
				   }
					
				   cursore++;
			   }
	
		   }
	
		   setCursorPos(scroller_position);
		
		   rs.close();
		   stmt.close();
			
		   if (!usePool) {
			   if (closeConnection) {
				   conn.close();
				   Monitor.log(UIRes.getLabel("event.db.connection.end"));
			   }
		   }
			
	   } catch(Exception ex) {
	   	Monitor.debug( ex.getMessage() );
	   	Monitor.debug( ex );
	   } finally {
		   if (closeConnection) {
			   if (usePool) {
				   poolMgr.freeConnection( (String)connectionProp.getProperty("dbPoolName") , conn);
				   Monitor.log(UIRes.getLabel("event.db.connection.pool.release"));
			   }
		   }
			
		   if (pageResults) vtPager.addElement((Vector) vt);
			
	   }
   }
	
	*/
	
}
