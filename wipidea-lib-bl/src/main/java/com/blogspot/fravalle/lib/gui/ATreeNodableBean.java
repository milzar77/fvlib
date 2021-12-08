/*
 * TreeNodableBean.java - Java Document Manager
 * Copyright (C) 29 ottobre 2002 Francesco Valle <info@weev.it>
 * http://www.weev.it
 *
 */

package com.blogspot.fravalle.lib.gui;


public abstract class ATreeNodableBean extends javax.swing.tree.DefaultMutableTreeNode {
	
	public javax.swing.tree.DefaultMutableTreeNode buildMainNode() {return null;}
	public javax.swing.tree.DefaultMutableTreeNode[] buildSubNodes() {return null;}
	
}


