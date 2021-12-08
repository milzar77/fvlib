/*
 * MDIApplication.java - japp (japp.jar)
 * Copyright (C) 2005
 * Source file created on 8-dic-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [MDIApplication]
 * 2DO:
 *
 */

package com.blogspot.fravalle.lib.gui.mdi;


import com.blogspot.fravalle.lib.gui.AWindow;
import com.blogspot.fravalle.util.UIRes;



/**
 * @author antares
 */
public abstract class MDIApplication extends MDIFrame {
	
	/**
	 * 
	 */
	public static void main(String[] args) {
		AWindow windowAb = new MDIFrame();
	}
	
	public abstract void initMDI();
	
	public void initWindow() {
		getMainLogger().info( UIRes.getLabel("log.override.method") );
		initMDI();
	}

}
