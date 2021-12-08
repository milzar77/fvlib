/*
 * IDataHook.java - jappframework (jappframework.jar)
 * Copyright (C) 2006
 * Source file created on 2-feb-2006
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [IDataHook]
 * 2DO:
 *
 */

package com.blogspot.fravalle.lib.bl.beans;

/**
 * @author francesco
 */
public interface IDataHook {
	public boolean addHook(Class parDataHook);
	public Object getFirstHook();
	public Object getLastHook();
	public Object getHook(String className);
}
