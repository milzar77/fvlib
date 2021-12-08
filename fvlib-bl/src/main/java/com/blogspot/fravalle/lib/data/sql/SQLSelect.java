/*
 * RenderSQLSource.java - Weev Utility Library package (weev-lib.jar)
 * Copyright (C) 2 novembre 2002 Francesco Valle <info@weev.it>
 * http://www.weev.it
 *
 *
 // Esempio utilizzo della classe
 Properties p = new Properties();
 SQLSelect dbs;
 p.setProperty("jdbcDriver", (String)jdbcDriver);
 p.setProperty("jdbcUrl", (String)jdbcUrl);
 // pooling connection
 try {
	p.setProperty("dbPoolName", (String)poolName);
	p.setProperty("user", (String)"madsql");
	p.setProperty("password", (String)"orione");
	
	dbs = new SQLSelect(p, false, false);
	dbs.select(sqlSelect, true, true, 3);
	Vector vt = (Vector)dbs.getResults();
	
 } catch {

 }
 // direct connection
 try {
	 dbs = new SQLSelect(p, true, false);
	 dbs.select(sqlSelect, true, true, 3);
	 Vector vt = (Vector)dbs.getResults();
 } catch {

 }
 
 */
 
 
 
package com.blogspot.fravalle.lib.data.sql;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import com.blogspot.fravalle.lib.bl.beans.IModel;
import com.blogspot.fravalle.lib.bl.beans.IModelList;
import com.blogspot.fravalle.lib.bl.beans.IModelRecord;
import com.blogspot.fravalle.lib.data.db.pooling.PoolManager;
import com.blogspot.fravalle.lib.monitor.ErrorCodes;
import com.blogspot.fravalle.lib.monitor.MainLogger;
import com.blogspot.fravalle.lib.monitor.Monitor;
import com.blogspot.fravalle.util.Constants;
import com.blogspot.fravalle.util.UIRes;


public class SQLSelect {

    private int RESULT_MAX	= 5;
    private int CURSOR_POS	= 0;

    private static Connection conn;		// CONNESSIONE CONDIVISA DA TUTTE LE ISTANZE
    						// PER RINNOVARLA CHIUDERLA TRAMITE true
    
    private boolean closeConnection;
    private boolean usePool;

    private PoolManager poolMgr = PoolManager.getInstance();

// PUBLIC CONSTRUCTOR
    private Properties connectionProp;
    private boolean isDirectConn;
    private boolean pageResults;
    private java.util.Vector vt;
    private java.util.Vector vtPager;
    private java.util.Hashtable hsTableProperties;
	
    
    private IModelRecord record;
    
    public SQLSelect(Properties connectionProp, boolean isDirectConn, boolean pageResults) {
        this.connectionProp	=	connectionProp;
        this.isDirectConn	=	isDirectConn;
        this.pageResults	=	pageResults;
        init();
    }

    public Connection getConnection() {
    	return conn;
    }
    
    public void init() {
    	try {
	        poolMgr = PoolManager.getInstance();
	        if (isDirectConn)
	        	DbDirectConn();
	        else
	        	DbPoolConn();
	        if (pageResults)
	        	vtPager = new Vector();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
    }

// CONSOLE METHOD

    public static void main (String arguments []) throws IOException {
    	MainLogger.getLog().warning("\tClasse non eseguibile da console.");
        System.exit(0);
    }

// MAIN METHODS
  
    public void preparedBeanUpdate(IModelList vtBeanItems, Vector vtBeanMapItem, boolean closeConnection, int keySelect) {
        preparedBeanUpdate((Vector)vtBeanItems, vtBeanMapItem, closeConnection, keySelect, -1);
    }
    
    public void preparedBeanUpdate(IModelList vtBeanItems, Vector vtBeanMapItem, boolean closeConnection) {
        preparedBeanUpdate((Vector)vtBeanItems, vtBeanMapItem, closeConnection, 1, -1);
    }
    
   public void preparedBeanUpdate(Vector vtBeanItems, Vector vtBeanMapItem, boolean closeConnection, int keySelect) {
       preparedBeanUpdate(vtBeanItems, vtBeanMapItem, closeConnection, keySelect, -1);
   }
   
   public void preparedBeanUpdate(Vector vtBeanItems, Vector vtBeanMapItem, boolean closeConnection) {
       preparedBeanUpdate(vtBeanItems, vtBeanMapItem, closeConnection, 1, -1);
   }
    
   synchronized public void preparedBeanUpdate(Vector vtBeanItems, Vector vtBeanMapItem, boolean closeConnection, int keySelect, int start) {
	
	   boolean firstCheck = false;
	   String query = "";
	
	   try {
			
	       for (int id = 0; id < vtBeanItems.size(); id++) {
	           
	           IModel mybean = (IModel)vtBeanItems.elementAt(id);
	           
			   query = conn.nativeSQL( ((IModelRecord)mybean).beanPrepareSQL( vtBeanMapItem ) );
			   
			   Monitor.log( Constants._logEventDef + "Executing query: " + query);
			   /* int startIndex = 1;
			   if (vtBeanMapItem.elementAt(0).equals("nokey")) {
			       startIndex = 0;
			   } */
			       
			       
			   PreparedStatement pstmt = conn.prepareStatement(query);
			   // la prima posizione � l'elemento predefinita per la WHERE
			   for (int idvec = keySelect; idvec < vtBeanMapItem.size(); idvec++) {
			       Object vl = mybean.getFieldValue( ""+vtBeanMapItem.elementAt(idvec) );
			       if (keySelect != 0) pstmt.setString(idvec, ""+vl );
			       else pstmt.setString(idvec+1, ""+vl );
			   }
			   
			   pstmt.execute();
			   

		
			   pstmt.close();
			   
	       }
	       
		   if (!usePool) {
			   if (closeConnection) {
				   conn.close();
				   Monitor.log(UIRes.getLabel("event.db.connection.end"));
			   }
		   }
			
	   } catch(Exception ex) {
	   	Monitor.debug( UIRes.getLabel("warning.default") + "Error during column #" +keySelect+ " retrieving.\r\n" + ex.getMessage() + " & " + keySelect);
	   	Monitor.debug( ex );
	   } finally {
		   if (closeConnection) {
			   if (usePool) {
				   poolMgr.freeConnection( (String)connectionProp.getProperty("dbPoolName") , conn);
				   Monitor.log(UIRes.getLabel("event.db.connection.pool.release"));
			   }
		   }
			
		   
			
	   }
   }
    
    
   synchronized public void preparedUpdate(String query, Vector vtParallelValues, boolean closeConnection) {
	
	   boolean firstCheck = false;
	
	   try {
			
		   query = conn.nativeSQL(query);
	
		   PreparedStatement pstmt = conn.prepareStatement(query);
		   for (int idvec = 0; idvec < vtParallelValues.size(); idvec++)
		       pstmt.setString(idvec+1, ""+vtParallelValues.elementAt(idvec) );
		   
		   pstmt.execute();
	
		   pstmt.close();
			
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
			
		   
			
	   }
   }
   
   // plain result select
   double arrayTmp[][];
   String arrayLabelsTmp[];
   
   synchronized public java.util.Vector arrayDoubleDimSelect(String query, int[] columns, boolean closeConnection) {
    
	   boolean isMultiColumn = false; 
       
	   if (columns.length > 1) isMultiColumn = true;
       
	   int max = 1;
	   int count = 0;
	   int cols = 1;
	   if (isMultiColumn) max = 2;
	   double array[][] = new double[1000][max];
	   String arrayLabels[] = new String[1000];
	   array[0][0] = 0.0D;
	   arrayLabels[0] = "totale";
	   
	   java.util.Vector vt = new java.util.Vector();
	
	   boolean firstCheck = false;
	   
	
	   try {
			
		   query = conn.nativeSQL(query);
	
		   Statement stmt = conn.createStatement();
		   ResultSet rs = stmt.executeQuery( conn.nativeSQL(query) );
		int label = 0;
		   while(rs.next()) {
			   // TODO : implementare raccolta di tutte le colonne
			   if (isMultiColumn) {
					arrayLabels[count+1] = ""+rs.getObject( columns[0]-1 ) + " & " + rs.getObject( columns[1]-1 );
					array[count+1][0] = (new Double(""+rs.getObject( columns[0] ))).doubleValue();
					array[count+1][1] = (new Double(""+rs.getObject( columns[1] ))).doubleValue();
					count++;
					cols = 2;
			   } else {
			       
				   arrayLabels[count+1] = ""+rs.getObject(1);
				   array[count+1][0] = (new Double(""+rs.getObject(2))).doubleValue();
				   cols = 1;
				   count++;
			   }
			
		   }
		   
		   arrayTmp = new double[count+1][cols];
		   arrayLabelsTmp = new String[count+1];
		   
		   System.arraycopy( array, 0, arrayTmp, 0, count+1 );
		   
		   System.arraycopy( arrayLabels, 0, arrayLabelsTmp, 0, count+1 );
		   
		   vt.addElement( arrayTmp );
		   vt.addElement( arrayLabels );
		   
		   rs.close();
		   stmt.close();
			
		   if (!usePool) {
			   if (closeConnection) {
				   conn.close();
				   Monitor.log( UIRes.getLabel("event.db.connection.end") );
			   }
		   }
			
	   } catch(Exception ex) {
	   	Monitor.debug( ex.getMessage() );
	   	Monitor.debug( ex );
	   } finally {
		   if (closeConnection) {
			   if (usePool) {
				   poolMgr.freeConnection( (String)connectionProp.getProperty("dbPoolName") , conn);
				   Monitor.log( UIRes.getLabel("event.db.connection.pool.release") );
			   }
		   }
			
	   }
	   
	   return vt;
	   
   }
  
   // plain result select
   synchronized public void plainSelect(String query, boolean isMultiColumn, boolean closeConnection) {
    	
	   vt = new java.util.Vector();
	   
	
	   boolean firstCheck = false;
	   
	
	   try {
			
		   query = conn.nativeSQL(query);
	
		   Statement stmt = conn.createStatement();
		   ResultSet rs = stmt.executeQuery(query);
	
		   while(rs.next()) {
		       // TODO : implementare raccolta di tutte le colonne
		       if (isMultiColumn) {
					Vector v = new Vector();
					v.addElement( "" + rs.getObject(1) );
					v.addElement( "" + rs.getObject(2) );
					vt.addElement( (Vector) v );
		       } else {
		           vt.addElement( "" + rs.getObject(1) );
		       }
			
		   }

		
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
			
	   }
   }
   
    
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
				       record = (IModelRecord)Class.forName( beanModelUrl ).newInstance();
				       
				       record.beanFiller((Hashtable)recordSingolo);
				       vt.addElement( (IModelRecord)record );
						
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
    
    
    synchronized public void select(String query, boolean useOnlyVector, boolean closeConnection, int totalResults) {
    	
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
					
					if (useOnlyVector) {
						// implementare metodo che inserisca
						// solo nella prima posizione del
						// vettore i dati ripetuti del tipo e nome
						// colonne
						Object[] dataStructure = new Object[3];
						dataStructure[0] = columnData;
						dataStructure[1] = columnTypeName;
						dataStructure[2] = tableName + columnName;
						vtRecord.addElement((Object[]) dataStructure);
					} else {
						recordSingolo.put((tableName + columnName), (Object) columnData);
					}
					
					if (firstCheck) hsTableProperties.put((tableName + columnName), (String)columnTypeName);
				
				}
	
				if (cursore < getMaxResults()) {
					if (useOnlyVector) vt.addElement( (java.util.Vector) vtRecord );
					else vt.addElement( (Hashtable)recordSingolo );
					
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
				Monitor.log( UIRes.getLabel("event.db.connection.pool.release"));
			}
		}
		
		if (pageResults) vtPager.addElement((Vector) vt);
		
	}
    }


// RETURN METHODS

    final synchronized public Vector getResults() {
    	return vt;
    }

    final synchronized public Vector getPagedResults(int page) {
	return (Vector)vtPager.elementAt(page);
    }

    final synchronized public Hashtable getTableSchema() {
	return hsTableProperties;
    }

    final synchronized protected int getMaxResults() {
    	return RESULT_MAX;
    }

    final synchronized protected int getCursorPos() {
    	return CURSOR_POS;
    }

// CLASS METHODS

    final synchronized public void setMaxResults(int int_mx) {
    	RESULT_MAX = int_mx;
    }

    final synchronized public void setCursorPos(int int_cp) {
    	CURSOR_POS = int_cp;
    }

// DB CONNECTION METHODS
	// da utilizzare solo per la conn diretta
   final synchronized public void CloseDbConn() throws IOException {
	try {
		if (conn != null) {
			if (!conn.isClosed()) {
				conn.close();
				Monitor.log(UIRes.getLabel("event.db.connection.end"));
			} else {
				Monitor.log(UIRes.getLabel("event.db.connection.end.already"));
			}
		}
	} catch(Exception ex) {
		Monitor.debug( ex.getMessage() );
		Monitor.debug( ex );
	}
    }

    final synchronized public void DbPoolConn() throws IOException {
		try {
			usePool = true;
			closeConnection = false;
			conn = poolMgr.getConnection( (String)connectionProp.getProperty("dbPoolName") );
		} catch(Exception ex) {
			Monitor.debug( ex.getMessage() );
			Monitor.debug( ex );
		}
    }

    final synchronized public void DbDirectConn() {
    	try {
    		Properties authConn = new Properties();
			
    		if (connectionProp.getProperty("user") != null && connectionProp.getProperty("password") != null) {
    			authConn.setProperty("user", (String)connectionProp.getProperty("user"));
    			authConn.setProperty("password", (String)connectionProp.getProperty("password"));
    		}
        	
    		usePool = false;
    		closeConnection = true;
    		String jdbcDriver = (String)connectionProp.getProperty("jdbcDriver");
    		String jdbcUrl = (String)connectionProp.getProperty("jdbcUrl");
    		
	    	if (conn == null) {
				Class.forName( jdbcDriver );
				conn = DriverManager.getConnection( jdbcUrl, authConn);
				Monitor.log( UIRes.getLabel("event.db.connection.create") );
			} else {
				if (conn.isClosed()) {
					Class.forName( jdbcDriver );
					conn = DriverManager.getConnection( jdbcUrl, authConn );
					Monitor.log( UIRes.getLabel("event.db.connection.create.new") );
				} else {
					Monitor.log( UIRes.getLabel("event.db.connection.create.already") );
				}
			}
		} catch ( ClassNotFoundException e ) {
			Monitor.debug(ErrorCodes.DATA_DB_NOTFOUND_DRIVER, e);
		} catch ( SQLException e ) {
			Monitor.debug( ErrorCodes.DATA_DB_CONNECTION_ERROR, e);
		} catch ( NullPointerException e ) {
			Monitor.debug( ErrorCodes.CODE_NULLPOINTER, e);
		}
		

    }

    
    public void destroy() {
        poolMgr.release();
    }

}