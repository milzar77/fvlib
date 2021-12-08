/*
 * WindowHook.java - jappframework (jappframework.jar)
 * Copyright (C) 2005
 * Source file created on 18-dic-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [WindowHook]
 * 2DO:
 *
 */

package com.blogspot.fravalle.lib.gui;



/**
 * @author antares
 */
public class WindowHook {

	/**
	 * 
	 */
	public WindowHook() {
		super();
		initHook();
	}
	public WindowHook(Class c) {
		super();
		initHook();
	}
	public WindowHook(Object instance) {
		super();
		initHook(instance);
	}

	private void initHook() {}
	
	private void initHook(Object instance) {
		writeHook();
	}
	
	private void writeHook() {
		/* Writing hook somewhere */
		// getLog().info("Writing hook somewhere");
	}
	
}
