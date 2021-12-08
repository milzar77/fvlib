/*
 * IShortcutAction.java - japp (japp.jar)
 * Copyright (C) 2005
 * Source file created on 9-dic-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [IShortcutAction]
 * 2DO:
 *
 */

package com.blogspot.fravalle.lib.gui;

/**
 * @author antares
 */
public interface IShortcutAction {
	public final String SUFFIX_LABEL = ".label";
	public final String SUFFIX_GROUP = ".group";
	public final String SUFFIX_PARAMS = ".params";
	public final String SUFFIX_LAUNCHER = ".launcher";
	public final String SUFFIX_OPTIONS = ".options";
	public final String SUFFIX_ICON = ".icon";
	public final String caller = "launch";
	public void launch();
}
