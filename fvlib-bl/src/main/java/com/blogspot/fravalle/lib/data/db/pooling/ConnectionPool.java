package com.blogspot.fravalle.lib.data.db.pooling;

import java.sql.*;
import java.util.*;

import java.io.*;


public class ConnectionPool {

   private String name;
   private String URL;
   private String user;
   private String password;
   private int maxConns;
   private int timeOut;
   private LogWriter logWriter;
   
   private int checkedOut;
   private Vector freeConnections = new Vector();

   public ConnectionPool(String name, String URL, String user, 
      String password, int maxConns, int initConns, int timeOut,
      PrintWriter pw, int logLevel)
      {
   
      this.name = name;
      this.URL = URL;
      this.user = user;
      this.password = password;
      this.maxConns = maxConns;
      this.timeOut = timeOut > 0 ? timeOut : 5;

      logWriter = new LogWriter(name, logLevel, pw);
      initPool(initConns);

      logWriter.log("New pool created", LogWriter.INFO);
      String lf = System.getProperty("line.separator");
      logWriter.log(lf +
                    " url=" + URL + lf +
                    " user=" + user + lf +
                    " password=" + password + lf +
                    " initconns=" + initConns + lf +
                    " maxconns=" + maxConns + lf +
                    " logintimeout=" + this.timeOut, LogWriter.DEBUG);
      logWriter.log(getStats(), LogWriter.DEBUG);
   }

   private void initPool(int initConns)
   {
      for (int i = 0; i < initConns; i++)
      {
         try
         {
            Connection pc = newConnection();
            freeConnections.addElement(pc);
         }
         catch (SQLException e)
         { }
      }
   }

   public Connection getConnection() throws SQLException
   {
      logWriter.log("Request for connection received", LogWriter.DEBUG);
      try
      {
         return getConnection(timeOut * 1000);
      }
       catch (SQLException e)
      {
         logWriter.log(e, "Exception getting connection", 
                       LogWriter.ERROR);
         throw e;
      }
   }

   private synchronized Connection getConnection(long timeout) 
                      throws SQLException
   {

      // Get a pooled Connection from the cache or a new one.
      // Wait if all are checked out and the max limit has
      // been reached.
      long startTime = System.currentTimeMillis();
      long remaining = timeout;
      Connection conn = null;
      while ((conn = getPooledConnection()) == null)
      {
         try
         {
            logWriter.log("Waiting for connection. Timeout=" + remaining,
                          LogWriter.DEBUG);
            wait(remaining);
         }
         catch (InterruptedException e)
         { }
         remaining = timeout - (System.currentTimeMillis() - startTime);
         if (remaining <= 0)
         {
            // Timeout has expired
            logWriter.log("Time-out while waiting for connection", 
                          LogWriter.DEBUG);
            throw new SQLException("getConnection() timed-out");
         }
      }

      // Check if the Connection is still OK
      if (!isConnectionOK(conn))
      {
         // It was bad. Try again with the remaining timeout
         logWriter.log("Removed selected bad connection from pool", 
                       LogWriter.ERROR);
         return getConnection(remaining);
      }
      checkedOut++;
      logWriter.log("Delivered connection from pool", LogWriter.INFO);
      logWriter.log(getStats(), LogWriter.DEBUG);
      return conn;
   }

   private boolean isConnectionOK(Connection conn)
   {
      Statement testStmt = null;
      try
      {
         if (!conn.isClosed())
         {
            // Try to createStatement to see if it's really alive
            testStmt = conn.createStatement();
            testStmt.close();
         }
         else
         {
            return false;
         }
      }
      catch (SQLException e)
      {
         if (testStmt != null)
         {
            try
            {
               testStmt.close();
            }
            catch (SQLException se)
            { }
         }
         logWriter.log(e, "Pooled Connection was not okay", 
                           LogWriter.ERROR);
         return false;
      }
      return true;
   }

   private Connection getPooledConnection() throws SQLException
   {
      Connection conn = null;
      if (freeConnections.size() > 0)
      {
         // Pick the first Connection in the Vector
         // to get round-robin usage
         conn = (Connection) freeConnections.firstElement();
         freeConnections.removeElementAt(0);
      }
      else if (maxConns == 0 || checkedOut < maxConns)
      {
         conn = newConnection();
      }
      return conn;
   }

   private Connection newConnection() throws SQLException
   {
      Connection conn = null;
      if (user == null) {
         conn = DriverManager.getConnection(URL);
      }
      else {
         conn = DriverManager.getConnection(URL, user, password);
      }
      logWriter.log("Opened a new connection", LogWriter.INFO);
      return conn;
   }

   public synchronized void freeConnection(Connection conn)
   {
      // Put the connection at the end of the Vector
      freeConnections.addElement(conn);
      checkedOut--;
      notifyAll();
      logWriter.log("Returned connection to pool", LogWriter.INFO);
      logWriter.log(getStats(), LogWriter.DEBUG);
   }

   public synchronized void release()
   {
      Enumeration allConnections = freeConnections.elements();
      while (allConnections.hasMoreElements())
      {
         Connection con = (Connection) allConnections.nextElement();
         try
         {
            con.close();
            logWriter.log("Closed connection", LogWriter.INFO);
         }
         catch (SQLException e)
         {
            logWriter.log(e, "Couldn't close connection", LogWriter.ERROR);
         }
      }
      freeConnections.removeAllElements();
   }

   private String getStats() {
      return "Total connections: " + 
         (freeConnections.size() + checkedOut) +
         " Available: " + freeConnections.size() +
         " Checked-out: " + checkedOut;
   }
}