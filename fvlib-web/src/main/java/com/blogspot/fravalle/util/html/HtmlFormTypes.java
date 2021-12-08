/*
 * HtmlFormTypes.java - antares-web (antares-web.jar)
 * Copyright (C) 2005
 * Source file created on 7-giu-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [HtmlFormTypes]
 * 2DO:
 *
 */

package com.blogspot.fravalle.util.html;

/**
 * @author antares
 */
public abstract interface HtmlFormTypes extends HtmlTypes {
	
	public static final int TYPE_FORM = 1;
	public static final int TYPE_INPUT = 2;
	public static final int TYPE_RADIO = 3;
	public static final int TYPE_CHECKBOX = 4;
	public static final int TYPE_SELECT = 5;
	public static final int TYPE_TEXTAREA = 6;
	public static final int TYPE_SELECT_DATE = 7;
	
	public static final String FORM = "form";
	public static final String INPUT = "input";
	public static final String RADIO = "input";
	public static final String CHECKBOX = "input";
	public static final String SELECT = "select";
	public static final String SELECT_DATE = "pseudogroup";
	public static final String TEXTAREA = "textarea";
	
}
