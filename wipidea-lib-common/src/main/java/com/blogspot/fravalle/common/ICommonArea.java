/**
 * $RCSfile: ICommonArea.java,v $
 *
 * Copyright (C) 2007. All Rights Reserved.
 *
 * CommonArea.java - WEEV-Library
 * Source file created on 23/lug/07, 02:45:12
 *
 * Author	: Francesco Valle <fv@weev.it>
 * WebInfo	: http://www.weev.it
 * Edited by	: francesco
 * 
 * [CommonArea]
 * TODO:
 * - cvs import
 *
 * @author Weev
 * @version $Revision: 1.1 $,	$Date: 2007/09/09 13:02:48 $
 */
package com.blogspot.fravalle.common;

/**
 * @author francesco
 *
 */
public interface ICommonArea {
    public String PREFS_KEY_WINDOW_TITLE = "title";
    public String PREFS_KEY_WINDOW_START_MAXIMIZED = "maximized";
    
    
    public short PREFS_GROUP_COMMON = 1;
    public short PREFS_GROUP_COMMON_WINDOW = 11;
    public short PREFS_GROUP_COMMON_WINDOW_EVENT = 111;
    public short PREFS_GROUP_COMMON_WINDOW_EVENT_INIT = 1111;
    public short PREFS_GROUP_COMMON_WINDOW_EVENT_START = 1112;
    public short PREFS_GROUP_COMMON_WINDOW_EVENT_END = 1113;
    
    public short PREFS_GROUP_COMMON_LOGGING = 12;
    public short PREFS_GROUP_COMMON_LOGGING_DB = 121;
    
}
