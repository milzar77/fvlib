/*
 * HtmlAttributes.java - antares-web (antares-web.jar)
 * Copyright (C) 2005
 * Source file created on 7-giu-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [HtmlAttributes]
 * 2DO:
 *
 */

package com.blogspot.fravalle.util.html;

import java.util.Hashtable;

/**
 * @author antares
 */
public abstract class HtmlAttributes extends Hashtable implements HtmlTypes {

	private String[]	cssId = new String[2];
	private String[]	cssStyle = new String[2];
	private String[]	cssClass = new String[2];

}
