/*
 * AWindow.java - jappframework (jappframework.jar)
 * Copyright (C) 2005
 * Source file created on 6-dic-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [AWindow]
 * 2DO:
 *
 */

package com.blogspot.fravalle.lib.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.blogspot.fravalle.common.ICommonArea;
import com.blogspot.fravalle.lib.data.sql.JdbcConnection;
import com.blogspot.fravalle.lib.data.sql.JdbcConnectionPool;
import com.blogspot.fravalle.lib.gui.menu.AWindowMenu;
import com.blogspot.fravalle.lib.monitor.MainLogger;
import com.blogspot.fravalle.util.Constants;
import com.blogspot.fravalle.util.FilteredProperties;
import com.blogspot.fravalle.util.SettingRes;

/**
 * @author antares
 */
abstract public class AWindow extends JFrame implements IWindow, ICommonArea {
	
	private static Logger localLogger = Logger.getLogger("mdi-container");
	
	static private StringBuffer textBuffer = new StringBuffer(32);
	static private StringBuffer xmlBuffer = new StringBuffer(32);
	static private Image imageBuffer;
	
	public static AWindow window;

	static Vector runningApplets = new Vector();
	
	public JdbcConnectionPool applicationDatabasePool;
	public JdbcConnection applicationDatabaseDirect;
	
	protected static AWindowMenu applicationMenu;
	protected static JComboBox applicationMonitor;
	protected static SideBarUI applicationSidebar;
	
	protected JPopupMenu jpopup;
	
	protected static String WINDOW_TITLE;
	protected static String WINDOW_JLF;	

	/* public BaseManager getDatabaseManager() {
		return applicationDatabasePool; 
	} */

	public JdbcConnectionPool getDatabasePoolManager() {
		if (applicationDatabasePool == null)
			applicationDatabasePool = new JdbcConnectionPool(Constants._fileDatabasePath);
		return applicationDatabasePool; 
	}

	public JdbcConnection getDatabaseDirectManager() {
		return applicationDatabaseDirect; 
	}
	
	public AWindow(String windowTitle, String strLookAndFeel) {
		super(windowTitle);
		/* Rimossa l'attivazione delle connessioni db durante la fase di avvio */
		// applicationDatabasePool = new JdbcConnectionPool(Constants._fileDatabasePath);
		// applicationDatabaseDirect = JdbcConnection.getInstance();
		
		WINDOW_TITLE = windowTitle;
		WINDOW_JLF = strLookAndFeel;
		try {
			UIManager.setLookAndFeel( strLookAndFeel );
		} catch (NullPointerException e) {
			getMainLogger().severe( e.getMessage() );
		} catch (ClassNotFoundException e) {
			getMainLogger().severe( e.getMessage() );
		} catch (InstantiationException e) {
			getMainLogger().severe( e.getMessage() );
		} catch (IllegalAccessException e) {
			getMainLogger().severe( e.getMessage() );
		} catch (UnsupportedLookAndFeelException e) {
			getMainLogger().severe( e.getMessage() );
		}
		this.defaultInit(this);
		this.init();
		this.initWindow();
		
		// this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setVisible(true);
	}
	
	protected abstract void init();
	
	protected abstract void initWindow();
	
	private static Vector vtHistoryLog = new Vector();
	
	protected void defaultInit(AWindow win) {
		win.getContentPane().setLayout(new BorderLayout());
		/* Pannello menu */
		buildApplicationMenu(win);
		
		/* Pannello laterale */
		buildApplicationSidebar(java.awt.BorderLayout.WEST, win);

		/* Pannello inferiore */
		buildApplicationMonitor(BorderLayout.SOUTH, win);
		
		win.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		win.addWindowListener(new WindowAdapter() {
			                  public void windowClosing(WindowEvent e) {
			                  	/*if (PoolManager.getInstance()!=null)
			                  		PoolManager.getInstance().release();*/
				                // getLog().info("Application closed");
			                  	System.exit(0);
			                  }
		                  }
		                 );
	}
	
	public static void showStatus(String message, Object caller) {
		if (applicationMonitor == null)
			return;
		String callerName = "";
		if (caller != null)
			callerName = "["+caller.getClass().getName()+"]";
		String logPrefix = Constants._logHistoryDef;
		
		// window.getLogger().history(logPrefix + message);
		
		vtHistoryLog.add(callerName + message);
		int intLastIndex = vtHistoryLog.size()-1;
		applicationMonitor.setModel( new DefaultComboBoxModel(vtHistoryLog) );
		applicationMonitor.setSelectedIndex(intLastIndex);
	}
	
	public static void showStatus(String message) {
		// applicationMonitor.setText(message);
		if (applicationMonitor == null)
			return;
		String logPrefix = Constants._logHistoryDef;
		getMainLogger().info( logPrefix + message);
		vtHistoryLog.add(logPrefix + message);
		int intLastIndex = vtHistoryLog.size()-1;
		applicationMonitor.setModel( new DefaultComboBoxModel(vtHistoryLog) );
		applicationMonitor.setSelectedIndex(intLastIndex);
	}
	
	protected void buildApplicationSidebar(String panelPosition, AWindow win) {
		FilteredProperties labels = SettingRes.getPropertiesGroup("sidebar");
		applicationSidebar = new SideBarUI(labels);
		addDefaultShortcuts();
		win.getContentPane().add(panelPosition, applicationSidebar);
	}
	
	protected void buildApplicationMonitor(String panelPosition, AWindow win) {
		// applicationMonitor = new JTextField();
		applicationMonitor = new JComboBox(new DefaultComboBoxModel(vtHistoryLog));
		applicationMonitor.setEditable(false);
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new GridLayout(1, 0));
		northPanel.setBorder(BorderFactory.createEmptyBorder(10, 4, 10, 4));
		northPanel.add(applicationMonitor);
		win.getContentPane().add(panelPosition, northPanel);
	}
	
	protected void buildApplicationMenu(AWindow win) {
		try {
			String menuClass = SettingRes.get("mdi.menu");
			if (menuClass.equals(SettingRes.KEY_NOTFOUND) )
				applicationMenu = (AWindowMenu)Class.forName("com.blogspot.fravalle.lib.gui.menu.DefaultMenu").newInstance();
			else
				applicationMenu = (AWindowMenu)Class.forName(menuClass).newInstance();
		} catch ( InstantiationException e ) {
			e.printStackTrace();
		} catch ( IllegalAccessException e ) {
			e.printStackTrace();
		} catch ( ClassNotFoundException e ) {
			e.printStackTrace();
		}
		win.setJMenuBar(applicationMenu);
	}
	
	public static void removeMenu(JMenu menu) {
		applicationMenu.remove( menu );
		applicationMenu.updateUI();
	}
	
	public static void addMenu(JMenu menu) {
		applicationMenu.add(menu);
		applicationMenu.updateUI();
	}
	
	public void manageSidebar(JComponent obj) {
		applicationSidebar.addItem(obj, 1);
	}

	/**
	 * @param props
	 * Le specifiche delle proprietà da includere nel file:
	 * <code>
	 * <p>shortcut.{identificatore univoco}.group=$variabile referenziata
	 * <p>shortcut.{identificatore univoco}.label=Etichetta shortcut
	 * <p>shortcut.{identificatore univoco}.ref=Parametri da passare al metodo da richiamare
	 * <p>shortcut.{identificatore univoco}.launcher=Nome del metodo da richiamare nell'azione
	 * </code>
	 */
	public void addConfiguredShortcuts(FilteredProperties props) {
		/* if (SettingRes.get("sidebar.comm.disabled")==null
				|| SettingRes.get("sidebar.prefs.disabled")==null
				|| SettingRes.get("sidebar.utils.disabled")==null
				|| SettingRes.get("sidebar.mylist.disabled")==null
				) return; */
		
		for (Enumeration e = props.keys(); e.hasMoreElements();){
			String shortcutKey = String.valueOf( e.nextElement() );
			if (ShortcutAction.isShortcutKey(shortcutKey)) {
				String shortcutKeyPrefix = ShortcutAction.readShortcutKeyPrefix(shortcutKey);
				String sLauncher = ShortcutAction.readShortcutLauncher(shortcutKeyPrefix);
				ShortcutAction shortcut = new ShortcutAction(this,sLauncher);
				shortcut.addParam( ShortcutAction.readShortcutParams(shortcutKeyPrefix) );
				shortcut.setActionGroup( ShortcutAction.readShortcutGroup(shortcutKeyPrefix) );
				shortcut.setActionLabel( ShortcutAction.readShortcutLabel(shortcutKeyPrefix) );
				String iconRes = ShortcutAction.readShortcutIcon(shortcutKeyPrefix);
				if (iconRes != null && !"null".equals(iconRes))
					shortcut.setActionIcon( ShortcutAction.readShortcutIcon(shortcutKeyPrefix) );
				if ( ShortcutAction.readShortcutOptions(shortcutKeyPrefix) != null
						&& (!ShortcutAction.readShortcutOptions(shortcutKeyPrefix).equals("null")
						&& !ShortcutAction.readShortcutOptions(shortcutKeyPrefix).equals("") )
					) {
					JCheckBox j1 = new JCheckBox("Offline mode");
					shortcut.setActionOptions(j1);
				}
				this.addShortcut( shortcut );
			}
		}
	}
	
	private void addDefaultShortcuts() {
		this.addConfiguredShortcuts(SettingRes.getPropertiesGroup("shortcut.default"));
	}
	
	public void addShortcut(ShortcutAction shortcut) {
		applicationSidebar.addItem(new SidebarShortcut(shortcut));
	}
	
	private void manageSidebar(SidebarShortcut shortcut) {
		applicationSidebar.addItem(shortcut);
	}
	
	final static public void lookAndFeel(String jlfDriver) {
		window.updateLookAndFeel(jlfDriver);
	} 
	
	public void updateLookAndFeel(String jlfDriver) {
		try {
			if ( jlfDriver.equals("default") ) {
				UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName());
			} else if ( jlfDriver.equals("skinlf") ) {
				/*SkinLookAndFeel.setSkin(
						SkinLookAndFeel.loadThemePack( "" )
					);
					
					SkinLookAndFeel.enable();*/
			} else {
				UIManager.setLookAndFeel( jlfDriver );
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			javax.swing.SwingUtilities.updateComponentTreeUI(this);
		}
	}
	
	class Jb1ActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				System.exit(0);
			} catch (Exception ex) {}
		}
	}
	
	class Jb2ActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				JButton Pippo = new JButton("PIPPO");
				Pippo.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						getMainLogger().info( "BE OR NOT TO BE!!!");
					}
				});
				// TODO: rimanere allineati ad elenco reale del container
			} catch (Exception ex) {}
		}
	}

	
	class Popitm1ActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				 // sidebar.addObj(new JButton("PROVA"));
			} catch (Exception ex) {}
		}
	}
	
	class PopupListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			if (e.isPopupTrigger()) {
				jpopup.show(e.getComponent(), e.getX(), e.getY());
			}
		}
		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger()) {
				jpopup.show(e.getComponent(), e.getX(), e.getY());
			}
		}

	}

	
	/* spostare il metodo nella classe adibita alla gestione di interfacce MDI */
	public static AWindow getWindowContainer() {
		return window; 
	}

	public void removeRunningApplet(String shortcut) {
		if (runningApplets.contains(shortcut))
			runningApplets.remove(shortcut);
	}
	public void addRunningApplet(String shortcut) {
		if (!runningApplets.contains(shortcut))
			runningApplets.add(shortcut);
	}
	
	public boolean isRunningApplet(String shortcut) {
		return runningApplets.contains(shortcut);
	}
	
	public static void listRunningApplets() {
		getMainLogger().info("RUNNING APPLETS: " + runningApplets.toString());
	}
	
	public Vector getRunningApplets() {
		return runningApplets;
	}

	public static void setXmlBuffer(Object buffer) {
		xmlBuffer = new StringBuffer(512);
		xmlBuffer.append(buffer.toString());
	}
	
	public static StringBuffer getXmlBuffer() {
		return xmlBuffer;
	}
	
	public static void setTextBuffer(Object buffer) {
		textBuffer = new StringBuffer(512);
		textBuffer.append(buffer.toString());
	}
	
	public static StringBuffer getTextBuffer() {
		return textBuffer;
	}
	
	public static void setImageBuffer(Object buffer) {
		/* if (imageBuffer==null)
			imageBuffer = new JPanel().createImage(32,32);
		else */
			imageBuffer = (Image)buffer;
	}
	
	public static Image getImageBuffer() {
		return imageBuffer;
	}

	
	public static final Logger getMainLogger() {
		return MainLogger.getLog();
	} 

	protected static final Logger getLocalLogger() {
		return localLogger;
	}
	
	abstract public void showPreview();
	
}

