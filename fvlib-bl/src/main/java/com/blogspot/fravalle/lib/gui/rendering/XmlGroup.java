/*
 * XmlGroup.java - jappframework (jappframework.jar)
 * Copyright (C) 2006
 * Source file created on 13-gen-2006
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [XmlGroup]
 * 2DO:
 *
 */

package com.blogspot.fravalle.lib.gui.rendering;

import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * [][XmlGroup]
 * 
 * <p><b><u>2DO</u>:</b>
 * 
 * 
 * @author Francesco Valle - (antares)
 */
public class XmlGroup extends JPanel {

	public String myname;
	
	public XmlGroup() {
		super();
	}
	public XmlGroup(String s) {
		super();
		this.myname = s;
		setLayout(new GridBagLayout());
		setBorder(BorderFactory.createTitledBorder(this.myname.toUpperCase()));
		//setPreferredSize(new Dimension(200,30));
		//setMaximumSize(new Dimension(0,0));
		//setMinimumSize(new Dimension(50,20));
	}
	
	
	public String getMyname() {
		return this.myname;
	}
	public void setMyname(String parMyname) {
		this.myname = parMyname;
	}
}
