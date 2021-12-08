/**
 * $RCSfile: LogChannel.java,v $
 *
 * Copyright (C) 2007. All Rights Reserved.
 *
 * LogChannel.java - WEEV-Library
 * Source file created on 23/lug/07, 21:05:23
 *
 * Author	: Francesco Valle <fv@weev.it>
 * WebInfo	: http://www.weev.it
 * Edited by	: francesco
 * 
 * [LogChannel]
 * TODO:
 * - cvs import
 *
 * @author Weev
 * @version $Revision: 1.1 $,	$Date: 2007/09/09 13:02:48 $
 */
package com.blogspot.fravalle.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import prefs.IPrefsKeys;
import prefs.PrefsUtil;

/**
 * @author francesco
 * TODO:
 * - sistema di trasferimento messaggi a db basato su code di spool
 *      + level
 *      + timestamp
 *      + message
 *      + class
 *      + method
 *      + user
 *      
 *      [sysProps]:
 *      + java.runtime.name
 *      + sun.boot.library.path
 *      + java.specification.version
 *      + java.version
 *      + java.vm.version
 *      + java.vm.specification.version
 *      + java.runtime.version
 *      + java.vm.vendor
 *      + java.vendor.url
 *      + java.vm.name
 *      + file.encoding.pkg
 *      + java.util.logging.config.file
 *      + user.country
 *      + user.language
 *      + sun.java.launcher
 *      + sun.os.patch.level
 *      + sun.arch.data.model
 *      + java.vm.specification.name
 *      + user.dir
 *      + java.awt.graphicsenv
 *      + awt.toolkit
 *      + java.vm.info
 *      + os.arch
 *      + os.name
 *      + os.version
 *      + sun.jnu.encoding
 *      + file.encoding
 *      + java.class.version
 *      + user.timezone
 *      + java.awt.printerjob
 *      + java.io.tmpdir
 *      + java.vendor.url.bug
 *      + sun.cpu.endian=little
 *      + sun.io.unicode.encoding=UnicodeLittle
 *      + sun.desktop=windows
 */
public final class LogChannel implements IPrefsKeys {

    static private final String[] groupLabel = {"JavaInfo","VmInfo"};

    static private final String defaultJdbcDriver = "";//"org.apache.derby.jdbc.EmbeddedDriver";
    static private final String defaultJdbcUrl = "";//"jdbc:derby:/tmp/derby/log;create=true";
    
    static private final String jdbcDriver = PrefsUtil.getCommonLoggingDbPrefs().get( PK_JDBC_DRIVERS , defaultJdbcDriver );
    static private final String jdbcUrl = PrefsUtil.getCommonLoggingDbPrefs().get( PK_JDBC_URL , defaultJdbcUrl );
    static private final String dbPopulator = PrefsUtil.getCommonLoggingDbPrefs().get( PK_DB_POPULATION , "res/populator.sql" );
    static private final String dbStatementSeparator = PrefsUtil.getCommonLoggingDbPrefs().get( PK_DB_STMT_SEP , "\n" );

    private static void clearDb(Connection conn) {
        try {
            boolean clearingPassed = false;
            Statement stm = conn.createStatement();
            
            clearingPassed = stm.execute( "delete from DEVFLOW" );
            if (clearingPassed)
                clearingPassed = stm.execute( "delete from PROPSET" );
            if (clearingPassed)
                clearingPassed = stm.execute( "delete from MAPSET" );
            
            stm.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    private static void populateDb(Connection conn) {
        clearDb(conn);
        StringBuffer sqlPopulation = new StringBuffer();
        if (dbPopulator.endsWith( ".properties" )) {
            ResourceBundle rb = ResourceBundle.getBundle("res.populator");
            sqlPopulation.append( rb.getString( PK_DB_POPULATION ) );
        }
        
        
        StringTokenizer st = new StringTokenizer(sqlPopulation.toString(), dbStatementSeparator);
        boolean populatorPassed = false;
        try {
            Statement stm = conn.createStatement();
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                populatorPassed = stm.execute( token );
            }
            stm.close();
        } catch (SQLSyntaxErrorException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
    }
    
    private static void initDb() {
        Connection conn;
        System.out.println("===>"+jdbcDriver);
        
        try {
            Class.forName( jdbcDriver );
            
            conn = DriverManager.getConnection( jdbcUrl );
            System.out.println(conn.isClosed());
            populateDb(conn);
            conn.close();
            System.out.println(conn.isClosed());
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * 
     */
    public static void buildDb() {
        
        initDb();

    }

}
