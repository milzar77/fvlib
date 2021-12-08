/*
 * SidebarShortcut.java - jappframework (jappframework.jar)
 * Copyright (C) 2005
 * Source file created on 8-dic-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [SidebarShortcut]
 * 2DO:
 *
 */

package com.blogspot.fravalle.lib.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.blogspot.fravalle.util.SettingRes;

/**
 * @author antares
 */
public class SidebarShortcut {

	private int group = -1;
	private String groupLabel = "";
	private String label = "Etichetta";
	private String icon = SettingRes.get("default.mdi.shortcut.icon");
	private Object myAction;
	private JComponent optionPanel;
	/**
	 * 
	 */
	public SidebarShortcut(ShortcutAction shortcut) {
		super();
		this.label = shortcut.getActionLabel();
		this.groupLabel = shortcut.getActionGroup();
		this.icon = shortcut.getActionIcon();
		this.myAction = shortcut;
		if (shortcut.getActionOptions()!=null)
			optionPanel = (JComponent)shortcut.getActionOptions();
	}
	public SidebarShortcut(String parLabel, String parGroup, Object parMyAction) {
		super();
		this.label = parLabel;
		this.groupLabel = parGroup;
		this.myAction = parMyAction;
	}
	public SidebarShortcut(String parLabel, int parGroup, Object parMyAction) {
		super();
		this.label = parLabel;
		this.group = parGroup;
		this.myAction = parMyAction;
	}
	public SidebarShortcut(String parGroupLabel, String parLabel,
			Object parMyAction, JComponent parOptionPanel) {
		super();
		groupLabel = parGroupLabel;
		label = parLabel;
		myAction = parMyAction;
		optionPanel = parOptionPanel;
	}
	final int buttonSize = 35, buttonPadding = 25;
	
	public JComponent getShortcut() {
		return getShortcut(true);
	}
	
	public JComponent getShortcut(boolean isSpaced) {
		// final JToggleButton shortcut = new JToggleButton(label);
		boolean useIcon = label.startsWith(":") && label.endsWith(":");
		JButton shortcut = new JButton();
		
		if (!useIcon)
			shortcut.setText(label);
		else {
			shortcut.setText("");
			shortcut.setToolTipText(label);
		}
		shortcut.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				ThreadLauncher launcher = new ThreadLauncher(1);
				try {
					// if (shortcut.getModel().isSelected()) {
						// getLog().info("APPLET LAUNCHED");
						launcher.start();
					// }
				} catch ( RuntimeException e ) {
					e.printStackTrace();
				} finally {
					launcher.interrupt();
				}

			}
		});
		if (isOptionPanel()) {
			JPanel jp = new JPanel();
			JCheckBox jcb = new JCheckBox();
			jcb.setToolTipText("Show Shortcut Options");
			
			jp.add(jcb);
			jp.add(shortcut);
			JPanel jp1 = new JPanel();
			jp1.add(getOptionPanel());
			jp1.setVisible(true);
			jp.add(jp1);
			return jp;
		}
		
		if (useIcon) {
			javax.swing.ImageIcon imageIcon = new javax.swing.ImageIcon( getClass().getResource( this.icon ) );
			javax.swing.ImageIcon scaledImageIcon = new javax.swing.ImageIcon(
				imageIcon.getImage().getScaledInstance( 32,32, java.awt.Image.SCALE_AREA_AVERAGING ) 
			);
			shortcut.setBorder(BorderFactory.createEmptyBorder());
			shortcut.setIcon( scaledImageIcon );
			shortcut.setOpaque(false);
			shortcut.setMaximumSize(new Dimension(buttonSize+10,buttonSize+10));
			shortcut.setPreferredSize(new Dimension(buttonSize+10,buttonSize+10));
		}
		
			
		//shortcut.setToolTipText( label );
		
		
		// shortcut.setIconTextGap( 0 );
		/*shortcut.setVerticalAlignment(JButton.CENTER);
		shortcut.setHorizontalAlignment(JButton.CENTER);
		shortcut.setAlignmentX(JButton.CENTER_ALIGNMENT);
		shortcut.setAlignmentY(JButton.CENTER_ALIGNMENT);*/

		JPanel jp = new JPanel(new GridBagLayout());
		GridBagConstraints grid = new GridBagConstraints();
		jp.setMaximumSize(new Dimension(buttonSize*5,buttonSize*3));
		jp.setPreferredSize(new Dimension(buttonSize*5,buttonSize*3));
		if (useIcon)
			jp.setMaximumSize(new Dimension(buttonSize+buttonPadding*2,buttonSize+buttonPadding*2));

		jp.add(shortcut, grid);
		jp.setOpaque(false);
		jp.setBorder(BorderFactory.createEmptyBorder(buttonPadding,buttonPadding,buttonPadding,buttonPadding));
		jp.setName(label);
		
		/*if (isSpaced) {
			// if (useIcon)
				this.setBorderSpacing(jp);
		}*/
			
		
		if (isSpaced)
			return shortcut;
		else
			return jp;
			
	}
	
	
	private void setBorderSpacing(JComponent component) {
		// component.setOpaque(false);
		// component.setBackground()
		// component.setBorder( BorderFactory.createCompoundBorder( BorderFactory.createEmptyBorder(40,0,40,0), component.getBorder() ) );
	}

	public final int getGroup() {
		return group;
	}
	public final void setGroup(int parGroup) {
		group = parGroup;
	}
	public final String getGroupLabel() {
		return groupLabel;
	}
	public final void setGroupLabel(String parGroupLabel) {
		groupLabel = parGroupLabel;
	}
	public final JComponent getOptionPanel() {
		return optionPanel;
	}
	public final boolean isOptionPanel() {
		return optionPanel != null;
	}
	public final void setOptionPanel(JComponent parOptionPanel) {
		optionPanel = parOptionPanel;
	}
	
	
	protected class ThreadLauncher extends Thread {
		long l;
		public ThreadLauncher(long par){
			this.l = par;
		}
		public void run() {
			try {
				Method m = null;
				if (myAction instanceof ShortcutAction)
					m = (Method)myAction.getClass().getMethod(IShortcutAction.caller,new Class[]{});
				else
					m = (Method)myAction.getClass().getMethod("shortcut",new Class[]{});
				m.invoke( myAction, new Object[] {});

					
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			/* } catch (FrameworkFatalException e) {
				e.printStackTrace();
			} catch (FrameworkDatabaseException e) {
				e.printStackTrace(); */
			}
		}
	}
	
}
