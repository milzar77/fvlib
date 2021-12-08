/*
 * DefaultMenu.java - jappframework (jappframework.jar)
 * Copyright (C) 2006
 * Source file created on 16-feb-2006
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [DefaultMenu]
 * 2DO:
 *
 */

package com.blogspot.fravalle.lib.gui.menu;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;

import com.blogspot.fravalle.lib.gui.mdi.MDIFrame;
import com.blogspot.fravalle.util.UIRes;

/**
 * @author francesco
 */
public class DefaultMenu extends AWindowMenu {
	/**
	 * 
	 */
	public DefaultMenu() {
		super();
	}
	
	public void initMenu() {
		JMenu jmenu = new JMenu(UIRes.getLabel("window.title"));
		prepareMenu(jmenu,"window.menu");
		// this.add(jmenu);
		
		JMenu jmFile = new JMenu(UIRes.getLabel("window.menu.file"));

		JMenu jmOpen = new JMenu(UIRes.getLabel("window.menu.file.open"));

		Action closeFile = new AbstractAction(UIRes.getLabel("window.menu.file.close")) {
			                   public void actionPerformed(ActionEvent e) {
			                   		MDIFrame.showStatus("CLOSE!!!",this);
			                   }
		                   };

		Action exitWin = new AbstractAction(UIRes.getLabel("window.menu.file.exit")) {
			                 public void actionPerformed(ActionEvent e) {
			                 		MDIFrame.showStatus("EXIT!!!",this);
					                System.exit(0);
			                 }
		                 };

		jmFile.add(jmOpen);
		jmFile.add(closeFile);

		jmFile.addSeparator();
		jmFile.add(exitWin);

		this.add(jmFile);
		this.add(this.getPluginsMenu());
		this.add(this.getOptionsMenu());
		this.add(this.getAboutMenu());
	}
	
}
