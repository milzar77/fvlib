/*
 * HtmlFormAttributes.java - antares-web (antares-web.jar)
 * Copyright (C) 2005
 * Source file created on 6-giu-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [HtmlFormAttributes]
 * 2DO:
 *
 */

package com.blogspot.fravalle.util.html;




/**
 * @author antares
 */
public class HtmlFormAttributes extends HtmlAttributes {
	private boolean		isInline;
	private boolean		isLabeled;
	
	private String[]	name = new String[2];
	private String[]	value = new String[2];
	private String[]	size = new String[2];
	private String		action = null;
	/**
	 * <code>Imposta il tipo di target, il secondo parametro specifica se il link viene caricato nella
	 * stessa finestra di navigazione o in una finestra di navigazione popup</code>
	 */
	private String[]	target = new String[2];

	private String		label = null;
	private String		coordinate = null;

	private String		head = null;
	private String		content = null;

	public HtmlFormAttributes() {
		this.isInline = false;
		this.isLabeled = false;
	}

	public HtmlFormAttributes(boolean isInlineTag) {
		this.isInline = isInlineTag;
		this.isLabeled = false;
	}
	public HtmlFormAttributes(boolean isInlineTag, boolean isLabeled) {
		this.isInline = isInlineTag;
		this.isLabeled = isLabeled;
	}

	public final boolean isInline() {
		return this.isInline;
	}

	public final void setInline(boolean parIsInline) {
		this.isInline = parIsInline;
	}
	
	public final String[] getAtt() {
		return this.name;
	}
	public final void setAtt(String[] parName) {
		if ( parName[ATT_NAME].equals("name") )
			this.name = parName;
		this.put( parName[ATT_NAME], parName[ATT_VALUE] );
	}
	
	public final String[] getName() {
		return this.name;
	}
	public final void setName(String[] parName) {
		this.name = parName;
		this.put( parName[ATT_NAME], parName[ATT_VALUE] );
	}
	public final String getAction() {
		return this.action;
	}
	public final void setAction(String parAction) {
		this.action = parAction;
	}
	public final String[] getTarget() {
		return this.target;
	}
	public final void setTarget(String[] parTarget) {
		this.target = parTarget;
	}
	public final String getContent() {
		return this.content;
	}
	public final void setContent(String parContent) {
		this.content = parContent;
	}
	
	public final String getHead() {
		return this.head;
	}
	public final void setHead(String parHead) {
		this.head = parHead;
	}
	public final boolean isLabeled() {
		return this.isLabeled;
	}
	public final void setLabeled(boolean parIsLabeled) {
		this.isLabeled = parIsLabeled;
	}
	public final String getLabel() {
		return this.label;
	}
	public final void setLabel(String parLabel) {
		this.label = parLabel;
	}
	public final String getCoordinate() {
		return this.coordinate;
	}
	public final void setCoordinate(String parCoordinate) {
		this.coordinate = parCoordinate;
	}
}