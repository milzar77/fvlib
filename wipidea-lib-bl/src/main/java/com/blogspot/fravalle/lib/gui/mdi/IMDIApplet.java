/*
 * MDISpecial.java - jdocman (jdocman.jar)
 * Copyright (C) 2003
 * Source file created on 17-dic-2003
 * Author: Francesco Valle <info@weev.it>
 * http://www.weev.it
 *
 */

package com.blogspot.fravalle.lib.gui.mdi;

import com.blogspot.fravalle.util.UIRes;


/**
 * @author antares
 * 
 */
public interface IMDIApplet /*extends ILoggableObject*/ {
    
	public final String _logStart_applet = UIRes.getLabel("log.start.applet");
	public final String _logStop_applet = UIRes.getLabel("log.stop.applet");
	public final String _logEnd_applet = UIRes.getLabel("log.end.applet");
	// public final static String WINTITLE="MDI Scrollable data viewer";
    
    public void setMDITitle(String title);
	public String getMDITitle();

	public void setMDIIcon(String icon);
    public String getMDIIcon();
    
	public void showMDIStatus(String message);
	
	final static public int notRunning = -1;
	final static public int runAlways = 0;
    
}
