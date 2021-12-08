/*
 * MainMenu.java - Java Document Manager Copyright (C) 29 ottobre 2002 Francesco
 * Valle <info@weev.it> http://www.weev.it
 *  
 */

package com.blogspot.fravalle.lib.gui.menu;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JEditorPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import com.blogspot.fravalle.lib.gui.AWindow;
import com.blogspot.fravalle.lib.gui.OptionPropertiesUI;
import com.blogspot.fravalle.lib.gui.ShortcutAction;
import com.blogspot.fravalle.lib.gui.SidebarShortcut;
import com.blogspot.fravalle.lib.gui.mdi.MDIFrame;
import com.blogspot.fravalle.lib.monitor.MainLogger;
import com.blogspot.fravalle.util.FilteredProperties;
import com.blogspot.fravalle.util.SettingRes;
import com.blogspot.fravalle.util.UIRes;
import com.blogspot.fravalle.util.filesystem.FileSystemReader;
import com.blogspot.fravalle.util.filesystem.JarManager;
import com.blogspot.fravalle.util.filesystem.JarReader;


abstract public class AWindowMenu extends JMenuBar {
	public JMenu	jmPluginsInstalled;
	public int		JLF_MENU_ITEM	= 0;

	abstract public void initMenu();

	public AWindowMenu() {
		/*FlowLayout fl = new FlowLayout(FlowLayout.RIGHT);
		this.setLayout(fl);*/
		this.initMenu();
	}

	protected void prepareMenu(JMenu currentMenu, String strGroup) {
		Vector vt = UIRes.getLabelGroup(strGroup);
		for (int i = 0; i < vt.size(); i++ ) {
			Action action = new AbstractAction((String)vt.get(i)) {
				public void actionPerformed(ActionEvent e) {}
			};
			currentMenu.add(action);
		}
	}

	protected JMenu getPluginsMenu() {
		JMenu jmPlugins = new JMenu(UIRes.getLabel("window.menu.plugins"));
		Action scanForPlugins = new AbstractAction(UIRes.getLabel("window.menu.plugins.scan")) {
			public void actionPerformed(ActionEvent e) {
				/* PluginManager.scanPlugins(); */
				MainLogger.getLog().warning( UIRes.getLabel("warning.notimplemented") );
				MDIFrame.showStatus("PluginManager.scanPlugins() " + UIRes.getLabel("warning.notimplemented"), this);
			}
		};
		jmPluginsInstalled = new JMenu(UIRes.getLabel("window.menu.plugins.installed"));
		jmPlugins.add(scanForPlugins);
		jmPlugins.addSeparator();
		jmPlugins.add(jmPluginsInstalled);
		jmPlugins.addSeparator();
		final JMenu listApplets = new JMenu(UIRes.getLabel("window.menu.plugins.actives"));
		listApplets.addMenuListener(new MenuListener() {
			int	selectedMenu	= 0;

			public void menuSelected(MenuEvent parE) {
				final JInternalFrame[] frames = MDIFrame.getDesktop().getAllFrames();
				for (int i = 0; i < frames.length; i++ ) {
					selectedMenu = i;
					Action action = new AbstractAction(frames[i].getTitle()) {
						public void actionPerformed(ActionEvent e) {
							try {
								frames[selectedMenu].setLocation(0, 0);
								frames[selectedMenu].setSelected(true);
							}
							catch ( PropertyVetoException e1 ) {
								e1.printStackTrace();
							}
						}
					};
					listApplets.add(action);
				}
			}

			public void menuDeselected(MenuEvent parE) {
				listApplets.removeAll();
			}

			public void menuCanceled(MenuEvent parE) {}
		});
		/*
		 * final Action listApplets = new
		 * AbstractAction(UIRes.getLabel("window.menu.plugins.actives")) {
		 * public void actionPerformed(ActionEvent e) {
		 * AWindow.listRunningApplets(); MDIFrame.getDesktop().getAllFrames();
		 * listApplets. // } };
		 */
		jmPlugins.add(listApplets);
		Action closeAll = new AbstractAction(UIRes.getLabel("window.menu.options.close.applets")) {
			public void actionPerformed(ActionEvent e) {
				JInternalFrame[] frames = MDIFrame.getDesktop().getAllFrames();
				for (int i = 0; i < frames.length; i++ ) {
					frames[i].dispose();
				}
			}
		};
		jmPlugins.add(closeAll);
		return jmPlugins;
	}

	protected JMenu getOptionsMenu() {
		JMenu jmOptions = new JMenu(UIRes.getLabel("window.menu.options"));
		JMenu jmSkins = new JMenu(UIRes.getLabel("window.menu.options.jlf"));

		FilteredProperties filter = SettingRes.getPropertiesGroup("shortcut.default.jlf");
		for ( java.util.Enumeration e = filter.keys(); e.hasMoreElements(); ) {
			String keyName = (String)e.nextElement();
			
			if (keyName.indexOf(".param")!=-1) {
				if ( filter.get(keyName).toString().indexOf(".") != -1 ) {
					final String value = (String)filter.get(keyName);
					String key = keyName.substring(0,keyName.indexOf(".param")) + ".label";
					String label = (String)filter.get(key);
					Action changeSkin = new AbstractAction( label ) {
	                    public void actionPerformed(ActionEvent e) {
	                    	AWindow.lookAndFeel( value );
	                    }
	                };
	                jmSkins.add(changeSkin);
				}
			}
			/*
			if (jlfThemes != null) {
				
				for ( java.util.Enumeration enu = jlfThemes.keys(); enu.hasMoreElements(); ) {
					String paramValue = (String)enu.nextElement();
					Action changeTheme = new AbstractAction( jlfName+"-"+paramValue ) {
						                     public void actionPerformed(ActionEvent e) {
							                     AWindow.lookAndFeel( e.getActionCommand() );
						                     }
					                     };
					jmThemes.add(changeTheme);
				}


				jmSkins.add(jmThemes);

			} else {

				Action changeSkin = new AbstractAction( jlfName ) {
					                    public void actionPerformed(ActionEvent e) {
						                    JDMMain.applyJlf( e.getActionCommand() );
					                    }
				                    };
				jmSkins.add(changeSkin);

			}
			*/

		}
		

		JMenu jmOptionsDrawWindows = new JMenu(UIRes.getLabel("window.menu.options.winmove"));

		JRadioButtonMenuItem rd1 = new JRadioButtonMenuItem(UIRes.getLabel("window.menu.options.windrawborder"));
		JRadioButtonMenuItem rd2 = new JRadioButtonMenuItem(UIRes.getLabel("window.menu.options.windrawsolid"), true);

		Action jmOptionsDrawBorder = new AbstractAction(UIRes.getLabel("window.menu.options.windrawborder")) {
			                             public void actionPerformed(ActionEvent e) {
				                            /* JDMMain.mw.drawMDIChildBorder(true); */
			                             	MainLogger.getLog().warning(UIRes.getLabel("warning.notimplemented"));
			                             	MDIFrame.showStatus("MDIFrame.drawMDIChildBorder(true) " + UIRes.getLabel("warning.notimplemented"),this);
			                             }
		                             };
		Action jmOptionsDrawLive = new AbstractAction(UIRes.getLabel("window.menu.options.windrawsolid")) {
			                           public void actionPerformed(ActionEvent e) {
				                           	/* JDMMain.mw.drawMDIChildBorder(false); */
			                           		MainLogger.getLog().warning( UIRes.getLabel("warning.notimplemented") );
			                           		MDIFrame.showStatus("MDIFrame.drawMDIChildBorder(false) " + UIRes.getLabel("warning.notimplemented"),this);
			                           }
		                           };

		rd1.addActionListener(jmOptionsDrawBorder);
		rd2.addActionListener(jmOptionsDrawLive);

		ButtonGroup bgroup = new ButtonGroup();
		bgroup.add(rd1);
		bgroup.add(rd2);

		jmOptionsDrawWindows.add(rd1);
		jmOptionsDrawWindows.add(rd2);

		JMenu jmOptionsContainer = new JMenu(UIRes.getLabel("window.menu.options.container"));
		jmOptionsContainer.add(jmOptionsDrawWindows);

		
		jmOptions.add(jmSkins);
		jmOptions.addSeparator();
		jmOptions.add(jmOptionsContainer);
		
		if (MDIFrame.getWindowContainer()!=null) {
			ShortcutAction shortcut = new ShortcutAction(MDIFrame.getWindowContainer(),"updateLookAndFeel");
			shortcut.addParam( "com.oyoaha.swing.plaf.oyoaha.OyoahaLookAndFeel" );
			/*shortcut.setActionGroup( ShortcutAction.readShortcutGroup(shortcutKeyPrefix) );*/
			shortcut.setActionLabel( UIRes.getLabel("action.test") );
			jmOptions.add(new SidebarShortcut(shortcut).getShortcut());
		}
		
		jmOptions.addSeparator();

        Action windowAllOptions = new AbstractAction(UIRes.getLabel("window.menu.options.all")){
			public void actionPerformed(ActionEvent parArg0) {
				File f = new File( getClass().getClassLoader().getResource(".").getFile() + "/res/application.properties" );
				OptionPropertiesUI optionPane = new OptionPropertiesUI(f, "res.application", "", "");
				JOptionPane.showOptionDialog(null, null, UIRes.getLabel("window.menu.options.all"), JOptionPane.YES_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{optionPane},null);
			}
        };
		
        Action windowOptions = new AbstractAction(UIRes.getLabel("window.menu.options.global")){
			public void actionPerformed(ActionEvent parArg0) {
				File f = new File( getClass().getClassLoader().getResource(".").getFile() + "/res/application.properties" );
				OptionPropertiesUI optionPane = new OptionPropertiesUI(f, "res.application", "", "array,costante,db,hook,debug,internal,mdi,logging,shortcut,sidebar,ui");
				JOptionPane.showOptionDialog(null, null, UIRes.getLabel("window.menu.options.global"), JOptionPane.YES_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{optionPane},null);
			}
        };
        
        Action sidebarOptions = new AbstractAction(UIRes.getLabel("window.menu.options.ebar")){
			public void actionPerformed(ActionEvent parArg0) {
				File f = new File( getClass().getClassLoader().getResource(".").getFile() + "/res/gui/sidebar.properties" );
				OptionPropertiesUI optionPane = new OptionPropertiesUI(f, "res.gui.sidebar", "", "");
				JOptionPane.showOptionDialog(null, null, UIRes.getLabel("window.menu.options.ebar"), JOptionPane.YES_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{optionPane},null);
			}
        };
        jmOptions.add(sidebarOptions);
        jmOptions.add(windowOptions);
        jmOptions.add(windowAllOptions);
        
		return jmOptions;
	}

	protected JMenu getAboutMenu() {
		JMenu jmAbout = new JMenu(UIRes.getLabel("window.menu.about"));
		Action aboutAuthor = new AbstractAction(UIRes.getLabel("window.menu.about.author")) {
			                     public void actionPerformed(ActionEvent e) {
			                     	// MDIFrame.showStatus("@author",this);
			                     	JPanel infoPanel = new JPanel(new GridLayout(1,1,10,10));
			                     	infoPanel.add(new JLabel("<html><b>Francesco Valle</b></html>"));
			                     	infoPanel.add(new JLabel("<html><i>&lt;fv@weev.it&gt;</i></html>"));
			                     	Object[] items = new Object[]{
			                     			infoPanel
											};
			                     	JOptionPane.showOptionDialog(null, null, UIRes.getLabel("window.menu.about.author"), JOptionPane.YES_OPTION, JOptionPane.PLAIN_MESSAGE, null, items,null);
			                     }
		                     };
		Action aboutLicense = new AbstractAction(UIRes.getLabel("window.menu.about.license")) {
			                      public void actionPerformed(ActionEvent e) {
				                      /*MDIFrame.showStatus("@license",this);*/
			                      		JPanel license = new JPanel();
			                      		JScrollPane scroll = new JScrollPane();
			                      		try {
			                      			JEditorPane licenseContent = new JEditorPane();
				                      		licenseContent.setEditable(false);
				                      		licenseContent.setBackground(new Color(245,245,245));
				                      		licenseContent.setPreferredSize(new Dimension(500,450));
				                      		
				                      		licenseContent.setContentType("text/html; charset=ISO-8859-1");
				                      		
				                      		if (JarReader.isJarLoader()) {
				                				String theJar = SettingRes.get("ext.lib.path");
				                				JarManager manager = new JarManager(this.getClass(), theJar, ".html");
				                				licenseContent.setText( manager.getText("license/LICENSE.html").toString() );
				                      		} else {
				                      			String licensePath = new Object().getClass().getResource("/").getFile() + "license/LICENSE.html";
				                      			licenseContent.setText( FileSystemReader.getText(licensePath).toString() );
				                      			// licenseContent.setPage(new java.net.URL("file://"+licensePath));
				                      		}
				                      		
				                      		
				                      		scroll.setViewportView(licenseContent);
				                      		license.add( scroll );
										} catch ( Exception e1 ) {
											e1.printStackTrace();
										}
			                      		JOptionPane.showOptionDialog(AWindow.getWindowContainer(), null, UIRes.getLabel("window.menu.about.license"), JOptionPane.YES_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{license},null);
			                      		
			                      		
			                      }
		                      };
		Action  aboutProgram = new AbstractAction(UIRes.getLabel("window.menu.about.other")) {
			                      public void actionPerformed(ActionEvent e) {
			                      		MDIFrame.showStatus("@other",this);  
			                      }
		                      };

		jmAbout.add(aboutLicense);
		jmAbout.add(aboutAuthor);
		jmAbout.add(aboutProgram);
		return jmAbout;
	}
}