package com.blogspot.fravalle.lib.data.db.pooling;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import com.blogspot.fravalle.util.Constants;

// import com.blogspot.fravalle.lib.data.db.pooling.LogWriter;

public class PoolManager {

   static private PoolManager instance;
   static private int clients;

   private LogWriter logWriter;
   private PrintWriter pw;

   private Vector drivers = new Vector();
   private Hashtable pools = new Hashtable();

   private PoolManager()
   {
      init();
   }


   static synchronized public PoolManager getInstance()
   {
      if (instance == null)
      {
         instance = new PoolManager();
      }
      clients++;
      return instance;
   }

   private void init()
   {
      // Log to System.err until we have read the logfile property
      pw = new PrintWriter(System.err, true);
      logWriter = new LogWriter("PoolManager", LogWriter.INFO, pw);
      
      
      // properties file as resource 
      // stored in the base directory
      // of application
      InputStream is = getClass().getResourceAsStream( Constants._fileDatabasePath );
      Properties dbProps = new Properties();
      try
      {
         dbProps.load(is);
         // dbProps.list(System.out);
      }
      catch (Exception e)
      {
         logWriter.log("Can't read the properties file. " +
            "Make sure data.properties is in the CLASSPATH",
            LogWriter.ERROR);
         return;
      }
      String logFile = dbProps.getProperty("logfile");
      if (logFile != null)
      {
         try
         {
            pw = new PrintWriter(new FileWriter(logFile, true), true);
            logWriter.setPrintWriter(pw);
         }
         catch (IOException e)
         {
            logWriter.log("Can't open the log file: " + logFile +
               ". Using System.err instead", LogWriter.ERROR);
         }
      }
      loadDrivers(dbProps);
      createPools(dbProps);
   }

   private void loadDrivers(Properties props)
   {
      String driverClasses = props.getProperty("drivers");
      StringTokenizer st = new StringTokenizer(driverClasses);
      while (st.hasMoreElements())
      {
         String driverClassName = st.nextToken().trim();
         try
         {
            Driver driver = (Driver)
               Class.forName(driverClassName).newInstance();
            DriverManager.registerDriver(driver);
            drivers.addElement(driver);
            logWriter.log("Registered JDBC driver " + driverClassName,
               LogWriter.INFO);
         }
         catch (Exception e)
         {
            logWriter.log(e, "Can't register JDBC driver: " +
               driverClassName, LogWriter.ERROR);
         }
      }
   }

   private void createPools(Properties props)
   {
      Enumeration propNames = props.propertyNames();
      while (propNames.hasMoreElements())
      {   
         String name = (String) propNames.nextElement();
         if (name.endsWith(".url"))
         {   
            String poolName = name.substring(0, name.lastIndexOf("."));
            String url = props.getProperty(poolName + ".url");
            if (url == null)
            {   
               logWriter.log("No URL specified for " + poolName,
                  LogWriter.ERROR);
               continue;
            }
   
            String user = props.getProperty(poolName + ".user");
            String password = props.getProperty(poolName + ".password");
   
            String maxConns = props.getProperty(poolName + 
               ".maxconns", "0");
            int max;
            try
            {
               max = Integer.valueOf(maxConns).intValue();
            }
            catch (NumberFormatException e)
            {
               logWriter.log("Invalid maxconns value " + maxConns + 
                               " for " + poolName, LogWriter.ERROR);
               max = 0;
            }
   
            String initConns = props.getProperty(poolName + 
                              ".initconns", "0");
            int init;
            try
            {
               init = Integer.valueOf(initConns).intValue();
            }
            catch (NumberFormatException e)
            {
               logWriter.log("Invalid initconns value " + initConns + 
                             " for " + poolName, LogWriter.ERROR);
               init = 0;
            }
   
            String loginTimeOut = props.getProperty(poolName + 
               ".logintimeout", "5");
            int timeOut;
            try
            {
               timeOut = Integer.valueOf(loginTimeOut).intValue();
            }
            catch (NumberFormatException e)
            {
               logWriter.log("Invalid logintimeout value " + loginTimeOut + 
                            " for " + poolName, LogWriter.ERROR);
               timeOut = 5;
            }
   
            String logLevelProp = props.getProperty(poolName + 
                           ".loglevel", String.valueOf(LogWriter.ERROR));
            int logLevel = LogWriter.INFO;
            if (logLevelProp.equalsIgnoreCase("none"))
            {
               logLevel = LogWriter.NONE;
            }
            else if (logLevelProp.equalsIgnoreCase("error"))
            {
               logLevel = LogWriter.ERROR;
            }
            else if (logLevelProp.equalsIgnoreCase("debug"))
            {
               logLevel = LogWriter.DEBUG;
            }

            ConnectionPool pool =
                           new ConnectionPool(poolName, url, user, password,
                                            max, init, timeOut, pw, logLevel);
            pools.put(poolName, pool);
         }
      }
   }
   
   
   public Connection getConnection(String name)
   {
      Connection conn = null;
      ConnectionPool pool = (ConnectionPool) pools.get(name);
      if (pool != null)
      {
         try {
            conn = pool.getConnection();
         } catch (SQLException e) {
            logWriter.log(e, "Exception getting connection from " +
               name, LogWriter.ERROR);
         }
      } else {
      	if (name.indexOf("-") != -1) {
			String poolName = name.substring(0,name.indexOf("-"));
			String poolUrl = name.substring(name.indexOf("-")+1,name.indexOf(","));
			ConnectionPool pool1 =
                           new ConnectionPool(poolName, poolUrl, "madsql", "onemad",
                                            0, 0, 5, pw, LogWriter.INFO);
			try {
				conn = pool1.getConnection();
			}
			catch ( SQLException e ) {
				e.printStackTrace();
			}
		}
      }

      return conn;
   }

   public void freeConnection(String name, Connection con)
   {
      ConnectionPool pool = (ConnectionPool) pools.get(name);
      if (pool != null)
      {
         pool.freeConnection(con);
      }
   }

   public synchronized void release()
   {
      // Wait until called by the last client
      if (--clients != 0)
      {
         return;
      }
   
      Enumeration allPools = pools.elements();
      while (allPools.hasMoreElements())
      {
         ConnectionPool pool = (ConnectionPool) allPools.nextElement();
         pool.release();
      }

      Enumeration allDrivers = drivers.elements();
      while (allDrivers.hasMoreElements())
      {
         Driver driver = (Driver) allDrivers.nextElement();
         try
         {
            DriverManager.deregisterDriver(driver);
            logWriter.log("Deregistered JDBC driver " + 
                          driver.getClass().getName(), LogWriter.INFO);
         }
         catch (SQLException e)
         {
            logWriter.log(e, "Couldn't deregister JDBC driver: " + 
                             driver.getClass().getName(), LogWriter.ERROR);
         }
      }
   }
}