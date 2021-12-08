/*
 * TreeNodeBean.java - jappframework (jappframework.jar)
 * Copyright (C) 2005
 * Source file created on 19-dic-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [TreeNodeBean]
 * 2DO:
 *
 */

package com.blogspot.fravalle.lib.bl.beans;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author antares
 */
public final class TreeNodeBean extends DefaultMutableTreeNode {

	public String headerId;	
	public String headerTitle;
	public String headerGroup;
	public String headerIdx;
	
	public Object referenced;
	
	

	public void setId(String par) {
		headerId = par;
	}
	public void setTitle(String par) {
		headerTitle = par;
	}
	public void setGroup(String par) {
		headerGroup = par;
	}
	public void setIdx(String par) {
		headerIdx = par;
	}
	public String getId() {
		return headerId;
	}
	public String getTitle() {
		return headerTitle;
	}
	public String getGroup() {
		return headerGroup;
	}
	public String getIdx() {
		return headerIdx;
	}
	
	public javax.swing.tree.DefaultMutableTreeNode buildMainNode() {return null;}
	public javax.swing.tree.DefaultMutableTreeNode[] buildSubNodes() {return null;}

	/**
	 * @param model
	 */
	public TreeNodeBean(Object model) {
		super(model);
		referenced = model;
	}
	
	public String getKey(){
		return referenced.toString();
	}
	
	public String toString() {
		return getTitle();
	}

	public Object getReferenced() {
		return referenced;
	}

}
