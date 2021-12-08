/*
 * DbInserter.java - antares-web (antares-web.jar)
 * Copyright (C) 2005
 * Source file created on 30-mag-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [DbInserter]
 * 2DO:
 *
 */

package com.blogspot.fravalle.util.db;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * @author antares
 */
public final class DbInserter extends ADb {

	public final static void insertItem(final String sql, final Object[] items) {
		final String dbPool = getDbFlow(DB_FLOW_CONTENT);
			// DbLoader.poolFlowContent;
		printConsoleOut(new Object[][]
				{
				{CONSOLE_LOG_GENERIC, String.valueOf(poolMgr.hashCode())},
				{CONSOLE_DEF_INSERT , sql }
				}
				);
		Connection conn = poolMgr.getConnection(dbPool);
		PreparedStatement pst = null;

		try {
			pst = conn.prepareStatement(sql);
			for (int i = 0; i < items.length; i++ ) {
				pst.setObject(i + 1, items[i]);
			}
			pst.execute();
			pst.close();
		}
		catch ( Exception ex ) {
			ex.printStackTrace();
		}
		finally {
			poolMgr.freeConnection(dbPool, conn);
		}
	}

	/* (non-Javadoc)
	 * @see com.blogspot.fravalle.util.ADb#getSeed()
	 */
	public String getSeed() {
		// 2DO Auto-generated method stub
		return String.valueOf(getClass().hashCode());
	}
	
}
