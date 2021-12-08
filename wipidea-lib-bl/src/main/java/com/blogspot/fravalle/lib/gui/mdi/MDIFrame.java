/*
 * WindowDesktop.java - jappframework (jappframework.jar)
 * Copyright (C) 2005
 * Source file created on 6-dic-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [WindowDesktop]
 * 2DO:
 *
 */

package com.blogspot.fravalle.lib.gui.mdi;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.blogspot.fravalle.util.SettingRes;
import com.blogspot.fravalle.util.UIRes;

import prefs.PrefsUtil;

/**
 * @author antares
 */
public class MDIFrame extends AMDIFrame {
	
	private JButton jb1;
	private JButton jb2;


	// private static boolean neverMoreLaunch = false;
	// JInternalFrame

	public static MDIDesktop getDesktop(){
		return mdiDesktop;
	}
	

	// private static MDIFrame instance = new MDIFrame();
	public MDIFrame() {
            super( PrefsUtil.getCommonWindowPrefs().get( PREFS_KEY_WINDOW_TITLE , UIRes.getLabel("window.title") ), SettingRes.get("ui.jlf.default") );
	}
	// public static MDIFrame getInstance(){return instance;};
	
	protected void init() {

		/* Pannello MDI */
		JPanel centralPanel = new JPanel();
		centralPanel.setLayout(new BorderLayout());
		centralPanel.setBorder(BorderFactory.createEmptyBorder(3, 4, 3, 4));
		mdiDesktop = new MDIDesktop();
		centralPanel.add(java.awt.BorderLayout.CENTER, mdiDesktop);
		
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		// buttonsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		
		
		/* Contestualizzazione dei pannelli */
		getContentPane().add(java.awt.BorderLayout.CENTER, centralPanel);


		int inset = 0; // 50;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int wSizeW = screenSize.width; // - inset*2;
		int wSizeH = screenSize.height; // -inset*2;
		setBounds(inset, inset,
		        wSizeW,
		        wSizeH
		        );

		setIconImage(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/res/img/winicon.gif")));

	}

	
}
