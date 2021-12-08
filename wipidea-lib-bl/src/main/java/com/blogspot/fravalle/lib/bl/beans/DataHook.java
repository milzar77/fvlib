/*
 * DataHook.java - jappframework (jappframework.jar)
 * Copyright (C) 2006
 * Source file created on 2-feb-2006
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [DataHook]
 * 2DO:
 *
 */

package com.blogspot.fravalle.lib.bl.beans;

import java.util.Enumeration;
import java.util.Vector;

/**
 * @author francesco
 */
public class DataHook extends ADataHook {
	
	private Vector hookContainer;
	private Object dataHook;
	
	public DataHook() {
		hookContainer = new Vector();
	}
	
	public boolean addHook(Class parDataHook) {
		boolean added = false;
		try {
			if (parDataHook != null)
				dataHook = parDataHook.newInstance();
			added = hookContainer.add(dataHook);
		} catch ( InstantiationException e ) {
			e.printStackTrace();
		} catch ( IllegalAccessException e ) {
			e.printStackTrace();
		}
		return added;
	}
	
	public Object getFirstHook() {
		return hookContainer.firstElement();
	}

	public Object getLastHook() {
		return hookContainer.lastElement();
	}

	public Object getHook(String className) {
		Object rtHook = null;
		for (Enumeration e = hookContainer.elements(); e.hasMoreElements();) {
			rtHook = e.nextElement();
			if (rtHook.getClass().getName().indexOf(className)!=-1)
				break;
			else
				rtHook = null;
		}
		return rtHook;
	}
	
}
