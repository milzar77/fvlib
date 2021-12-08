/*
 * MDIWindow.java - Java Document Manager
 * Copyright (C) 29 ottobre 2002 Francesco Valle <info@weev.it>
 * http://www.weev.it
 *
 */ 

package com.blogspot.fravalle.lib.gui.mdi;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import com.blogspot.fravalle.lib.bl.beans.DataHook;
import com.blogspot.fravalle.lib.bl.beans.IDataHook;
import com.blogspot.fravalle.lib.bl.beans.ModelList;
import com.blogspot.fravalle.lib.bl.business.exceptions.FrameworkDatabaseException;
import com.blogspot.fravalle.lib.bl.business.exceptions.FrameworkFatalException;
import com.blogspot.fravalle.lib.data.sql.SearchConditions;
import com.blogspot.fravalle.lib.gui.AWindow;
import com.blogspot.fravalle.lib.gui.OptionPropertiesUI;
import com.blogspot.fravalle.lib.gui.WindowHook;
import com.blogspot.fravalle.lib.monitor.MainLogger;


abstract public class AMDIApplet extends JInternalFrame implements IMDIApplet {

	/**
	 * 
	 */
	abstract protected void initComponents();
	
	private static Logger localLogger = Logger.getLogger("mdi-applet");
	
	static public int runOnce = 1;
	static public int runStatus = notRunning;
	
	
    private String mdiTitle;
	private String mdiIcon;
	protected JMenu MDIMENU;
	
	protected WindowHook hook;
	
	
	protected JPanel mainPane;
	protected JPanel optionPane = new JPanel(new BorderLayout());
	
	Dimension storeSize = null;
	
	static private IDataHook dataHook;
	
	CardLayout cardLayout;
	final String MAIN_PANEL = "main";
	final String GLOBAL_OPTIONS_PANEL = "options";
	final String APPLET_OPTIONS_PANEL = "appletOptions";
	final String LOCAL_OPTIONS_PANEL = "localOptions";
	
	protected void addUi(JComponent ui, String uiName) {
		mainPane.add(ui, uiName);
	}
	
	protected void addMainUi(JComponent ui) {
		mainPane.add(ui, MAIN_PANEL);
	}
	
	protected void addLocalOptionPane(OptionPropertiesUI panel) {
		addUi(panel, LOCAL_OPTIONS_PANEL);
	}
	
	public AMDIApplet() throws FrameworkFatalException {
		super();
	}
	
	public AMDIApplet(String winTitle) {
		super(winTitle, 
			  true,
			  true,
			  true,
			  true);
		this.mdiTitle = winTitle;
		this.showMDIStatus(_logStart_applet);
		mainPane = new JPanel();
		cardLayout = new CardLayout();
		mainPane.setLayout(cardLayout);
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(java.awt.BorderLayout.CENTER, mainPane);
	}
	
	public AMDIApplet(String winTitle, int status) {
		this(winTitle);
		AMDIApplet.runStatus=status;
		runOnce++;
	}
	
	/**
	 * 
	 * Questo constructor consente di agganciare all'applet degli oggetti aggiornabili e
	 * raggiungibili da classi esterne tramite il metodo statico getDataHook().getHook("class name")
	 * 
	 * @param winTitle
	 * @param status
	 * @param parDataHook
	 * Array di oggetti aggiornabili agganciati all'applet
	 */
	public AMDIApplet(String winTitle, int status, Class[] parDataHook) {
		this(winTitle, status);

		if (parDataHook!=null) {
			dataHook = new DataHook();
			for (int i = 0; i < parDataHook.length; i++)
				dataHook.addHook(parDataHook[i]);
		}
		this.initMDIApplet(null);
		
		this.initComponents();
		
		addDefaultOptionPane();
	}

	public AMDIApplet(String winTitle, String winIcon, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable)
			throws FrameworkFatalException {
		super(winTitle, 
			  resizable,
			  closable,
			  maximizable,
			  iconifiable);
		this.showMDIStatus(_logStart_applet);
		this.mdiTitle = winTitle;
		
		this.mdiIcon = winIcon;
		this.MDIMENU = null;
		
		
		this.initMDIApplet(winIcon);
		
		
		// FEATURE : on close remove menu item
		setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
		
		addInternalFrameListener(
				new InternalFrameAdapter() {
					public void internalFrameClosing(InternalFrameEvent ife) {
						removePluginMenu(ife);
					}
				}
				);
		
	}
	
	public AMDIApplet(String winTitle, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable)
			throws FrameworkFatalException {
		super(winTitle, 
		      resizable,
		      closable,
		      maximizable,
		      iconifiable);
		
		this.showMDIStatus(_logStart_applet);
		
		this.mdiTitle = winTitle;
		this.mdiIcon = null;
		this.MDIMENU = null;
		
		this.initMDIApplet(null);
		
		
		// FEATURE : on close remove menu item
		setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
		
		addInternalFrameListener(
		        new InternalFrameAdapter() {
					public void internalFrameClosing(InternalFrameEvent ife) {
						removePluginMenu(ife);
					}
				}
		        );
		
		/* addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
							  public void windowClosing(java.awt.event.WindowEvent e) {
								  System.exit(0);
							  }
						  }
						 );
		*/
		
	}
	
	protected void setHook(Object o){
		hook = new WindowHook(o);
	}
	
	/* controllare eventuali conflitti tra classi ereditate */
	static protected IDataHook getDataHook() {
		return dataHook;
	}
	
	private void initMDIApplet(String winIcon) {
		if (winIcon != null) {
			ImageIcon imageIcon = new ImageIcon(getClass().getResource(winIcon));
			ImageIcon scaledImageIcon = new ImageIcon(imageIcon.getImage().getScaledInstance(16, 16,
					java.awt.Image.SCALE_AREA_AVERAGING));
			setFrameIcon(scaledImageIcon);
		}
		this.setHook(this);
	}
	
	protected static AWindow getMDIContainer() {
		return AWindow.getWindowContainer();
	}
	
	public Object readData(SearchConditions search) throws FrameworkFatalException, FrameworkDatabaseException {
		if (search.getPoolName() != null) {
			getMDIContainer().getDatabasePoolManager().setPool( search.getPoolName() );
			return getMDIContainer().getDatabasePoolManager().getData(search);
		} else
			return getMDIContainer().getDatabaseDirectManager().getData(search);
	}
	
	public Object readData(SearchConditions search, String poolname) throws FrameworkFatalException, FrameworkDatabaseException {
		getMDIContainer().getDatabasePoolManager().setPool(poolname);
		return getMDIContainer().getDatabasePoolManager().getData(search);
	}
	
   
    /* // esempio funzionante di mix del l&f
    public void updateUI() {
	    setUI(new com.sun.java.swing.plaf.motif.MotifInternalFrameUI(this) );
    }
    public void setUI(InternalFrameUI ui) {

        if (ui instanceof MetalInternalFrameUI) {
            super.setUI(ui);
        } else {
            super.setUI(new MetalInternalFrameUI(this));
        }
    }
    */
	
	void packApplet(){
		this.pack();
	};
	
	protected void addDefaultOptionPane() {
		addUi(new OptionPropertiesUI(this.getClass()), APPLET_OPTIONS_PANEL);

		final JToggleButton buttonLocalOptions = new JToggleButton();
		final JToggleButton buttonAppletOptions = new JToggleButton();
		
		buttonLocalOptions.setBackground(Color.ORANGE);
		buttonLocalOptions.setFont(new Font("System", Font.PLAIN, 7));
		buttonLocalOptions.setToolTipText("Mostra le opzioni locali");
		buttonLocalOptions.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent parArg0) {
				
				if (buttonAppletOptions.isSelected())
					buttonAppletOptions.setSelected(false);
				if (buttonLocalOptions.isSelected()) {
					if (storeSize==null)
						storeSize = getSize();
					setPreferredSize(new Dimension(540,380));
					cardLayout.show(mainPane, LOCAL_OPTIONS_PANEL);
				} else {
					setPreferredSize(storeSize);
					cardLayout.show(mainPane, MAIN_PANEL);
				}
				packApplet();
				
			}
		});
		
		
		buttonAppletOptions.setBackground(Color.LIGHT_GRAY);
		buttonAppletOptions.setFont(new Font("System", Font.PLAIN, 7));
		buttonAppletOptions.setToolTipText("Mostra le opzioni");
		buttonAppletOptions.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent parArg0) {
				if (buttonLocalOptions.isSelected())
					buttonLocalOptions.setSelected(false);
				
				if (buttonAppletOptions.isSelected()) {
					if (storeSize==null)
						storeSize = getSize();
					setPreferredSize(new Dimension(540,380));
					cardLayout.show(mainPane, APPLET_OPTIONS_PANEL);
				} else {
					setPreferredSize(storeSize);
					cardLayout.show(mainPane, MAIN_PANEL);
				}
				packApplet();
			}
		});
		
		JPanel temp = new JPanel(new BorderLayout());
		temp.add(buttonAppletOptions, BorderLayout.WEST);
		temp.add(buttonLocalOptions, BorderLayout.EAST);
		
		getContentPane().add(temp, BorderLayout.SOUTH);
	}
	
	public void showStatus(String message) {
		MDIFrame.showStatus(message,this);
	}
	
    public String getMDIIcon() {
		return mdiIcon;
    }

    public String getMDITitle() {
        return mdiTitle;
    }

    public void setMDIIcon(String icon) {
		this.mdiIcon = icon;
    }

    public void setMDITitle(String title) {
		this.mdiTitle = title;
    }

    public void showMDIStatus(String message) {
    	this.showStatus(message);
    }
    
    public void addPluginMenu(javax.swing.Action menuAction) {
		if (this.MDIMENU != null)
			MDIFrame.removeMenu( this.MDIMENU );
		else
			this.MDIMENU = new javax.swing.JMenu( getMDITitle() );
		
	    
		MDIMENU.add(menuAction);
		MDIFrame.addMenu( MDIMENU );
    }
    
	public void addPluginMenuSub(javax.swing.JMenu msub) {
	    if (this.MDIMENU != null)
	    	MDIFrame.removeMenu( this.MDIMENU );
	    else
			this.MDIMENU = new javax.swing.JMenu( getMDITitle() );
	    
		// this.MDIMENU = menu;
		MDIMENU.add( msub );
		MDIFrame.addMenu( MDIMENU );
	}
    

	private void removePluginMenu(InternalFrameEvent ife) {
		this.removePluginMenu();
	}
	
	public void removePluginMenu() {
	    try {
	    	MDIFrame.removeMenu( this.MDIMENU );
	    } catch (Exception e) {
	    	AWindow.getMainLogger().severe(_logStop_applet);
	        this.showMDIStatus(_logStop_applet);
	    }
	}
	
	public static ModelList loadBeanCollection(String xmlBean) {
		
		ModelList vt = new ModelList();

		try {
			java.beans.XMLDecoder d = new java.beans.XMLDecoder(
			                                  new java.io.BufferedInputStream(
			                                  		new java.io.FileInputStream(xmlBean)
			                                  )
			                          );
			vt = (ModelList)d.readObject();
			
			d.close();

		} catch (Exception e) {
			AWindow.getMainLogger().info(_logStop_applet);
		}
		return vt;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JInternalFrame#dispose()
	 */
	public void dispose() {
		super.dispose();
		this.showMDIStatus(_logEnd_applet + "[" + this.getClass().getName() + "]");
		getMDIContainer().removeRunningApplet(this.getClass().getName());
	}
	

	public static void main(String[] args, AMDIApplet applet) throws FrameworkFatalException, FrameworkDatabaseException, IOException, PropertyVetoException {
		if (args == null || args.length == 0 ) {
			Thread reader = new Thread() {
				public void run() {
					getMainLogger().warning( "MDIAPPLET not configured for console." );
				}
			};
		} else if ( "client".equals(args[0]) ) {
			
			final AMDIApplet slaveClient = applet;
			slaveClient.setMaximizable(false);
			slaveClient.setIconifiable(false);
			slaveClient.setClosable(false);
			slaveClient.pack();
			slaveClient.setVisible(true);
			slaveClient.show();
			
			MainLogger.getLog().info("MDIAPPLET INTO JFRAME: Sessione avviata");
			
			final JFrame win = new JFrame();
			win.getContentPane().add(slaveClient);
			// win.setUndecorated(true);
			win.pack();
			win.show();
			
			win.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			win.addWindowListener(new WindowAdapter() {
	            public void windowClosing(WindowEvent e) {
	            	getMainLogger().info("MDIAPPLET INTO JFRAME: Sessione terminata");
	            	System.exit(0);
	            }
	        }
	       );
		} else {
			getMainLogger().info("MDIAPPLET INTO JFRAME: Use argument \"client\" to access the visual version.");
		}
	}


	public static final Logger getMainLogger() {
		return MainLogger.getLog();
	}

	protected static final Logger getLocalLogger() {
		return localLogger;
	}
	
}
