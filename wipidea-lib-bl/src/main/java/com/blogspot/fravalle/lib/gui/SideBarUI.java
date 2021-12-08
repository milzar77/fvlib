/*
 * SideBarUI.java - jdocman (jdocman.jar)
 * Copyright (C) 2003
 * Source file created on 14-nov-2003
 * Author: Francesco Valle <info@weev.it>
 * http://www.weev.it
 *
 */

package com.blogspot.fravalle.lib.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.border.SoftBevelBorder;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicButtonUI;

import com.blogspot.fravalle.lib.monitor.Monitor;
import com.blogspot.fravalle.util.FilteredProperties;
import com.blogspot.fravalle.util.SettingRes;
import com.blogspot.fravalle.util.UIRes;




// TODO : correggere l'indicazione dell'indice pannello
// in com.blogspot.fravalle.gui.SideBarUI, attualmente non � una sequenza
// progresssiva diretta ma una sequenza alternata (1,3,5,etc...)

public class SideBarUI extends JPanel {
	
	static Vector iconSet;
	static int iconCounter = 0;
	static {
		iconSet = new Vector();
		/*
		iconSet.add("/res/img/icons/icon14.png");
		iconSet.add("/res/img/icons/icon21.png");
		iconSet.add("/res/img/icons/icon11.png");
		iconSet.add("/res/img/icons/icon22.png");
		iconSet.add("/res/img/icons/icon02.png");
		iconSet.add("/res/img/icons/icon23.png");
		iconSet.add("/res/img/icons/icon01.png");
		iconSet.add("/res/img/icons/icon23.png");
		iconSet.add("/res/img/icons/icon12.png");
		iconSet.add("/res/img/icons/icon03.png");
		iconSet.add("/res/img/icons/icon19.png");
		*/
		iconSet.add("/res/img/icons/icon01.png");
		iconSet.add("/res/img/icons/icon02.png");
		iconSet.add("/res/img/icons/icon03.png");
		iconSet.add("/res/img/icons/icon04.png");
		iconSet.add("/res/img/icons/icon05.png");
		iconSet.add("/res/img/icons/icon06.png");
		iconSet.add("/res/img/icons/icon07.png");
		iconSet.add("/res/img/icons/icon08.png");
		iconSet.add("/res/img/icons/icon09.png");
		iconSet.add("/res/img/icons/icon10.png");
		iconSet.add("/res/img/icons/icon11.png");
		iconSet.add("/res/img/icons/icon12.png");
		iconSet.add("/res/img/icons/icon13.png");
		iconSet.add("/res/img/icons/icon14.png");
		iconSet.add("/res/img/icons/icon15.png");
		iconSet.add("/res/img/icons/icon16.png");
		iconSet.add("/res/img/icons/icon17.png");
		iconSet.add("/res/img/icons/icon18.png");
		iconSet.add("/res/img/icons/icon19.png");
		iconSet.add("/res/img/icons/icon20.png");
		iconSet.add("/res/img/icons/icon21.png");
		iconSet.add("/res/img/icons/icon22.png");
		iconSet.add("/res/img/icons/icon23.png");
	}
	
	private String[][] _sidebarLabels;
	
	private final String internalResource = "/res/gui/sidebar.properties";
	
	private int _sidebarWidth;
	private int _sidebarHeight;

	private int oldCompId = -1;
	private int newCompId = -1;
	private int lstCompId = -1;
	
	private ResourceBundle resPrefs;
	
	public int scrollJump = 0;
	
	private Color colScrollerPane = Color.LIGHT_GRAY;
	
	
	private boolean isOnlyIcon = false;
	
	private boolean _isVisibleScrollbars;
	private boolean _user_useSpecialGroups;
	private boolean _user_useJlf;
	private boolean _user_useLabels;
	private boolean _user_useColorParent;
	private boolean _user_isFilledButtons;
	
	
	public final static String BUTTON_ABSENT = null;
	public final static String BUTTON_DEFAULT = "";
	
	public void addItem(SidebarShortcut itemComp) {
		JComponent component = null;
		
		if (!isOnlyIcon)
			component = itemComp.getShortcut(this._user_isFilledButtons);
		else {
			component = itemComp.getShortcut(true);
			if ( "".equals( ((JButton)component).getText() ) )
				((JButton)component).setToolTipText( ((JButton)component).getToolTipText() );
			else
				((JButton)component).setToolTipText( ((JButton)component).getText() );
			((JButton)component).setText("");
			if ( ((JButton)component).getIcon() == null ) {
				//Image img1 = new javax.swing.ImageIcon( getClass().getResource( iconSet.elementAt(iconCounter++).toString() ) ).getImage();
				Object iconRes = iconSet.elementAt(iconCounter++);
				if (iconRes==null)
					iconRes = SettingRes.get("default.mdi.shortcut.icon");
				URL urlImage = getClass().getResource( iconRes.toString() );
				if (urlImage == null) {
					iconRes = SettingRes.get("default.mdi.shortcut.icon");
					urlImage = getClass().getResource( iconRes.toString() );
				}
				ImageIcon imgIcon1 = new javax.swing.ImageIcon( urlImage );
				Image img1 = imgIcon1.getImage();
				((JButton)component).setIcon( new javax.swing.ImageIcon( img1.getScaledInstance(24,24,Image.SCALE_AREA_AVERAGING) ) );
			}
			((JButton)component).setAlignmentX(JButton.CENTER_ALIGNMENT);
			((JButton)component).setOpaque(false);
			((JButton)component).setBorder( BorderFactory.createEmptyBorder(15,0,15,0));
			/*((JButton)component).setBorder( BorderFactory.createCompoundBorder(
					BorderFactory.createEmptyBorder(15,0,15,0)
					,
					((JButton)component).getBorder()
			) );*/
			
		}
		//component.setPreferredSize(new Dimension( _sidebarWidth - 20 , component.getSize().height ));
			
		if (itemComp.getGroup() != -1)
			addItem(component, itemComp.getGroup());
		else
			addItem(component, itemComp.getGroupLabel());
	}
	
	public void addItem(JComponent itemComp, String tabLabel) {
		boolean isGrouped = false;
		for ( int i = 0; i < groupPane.getComponents().length; i++ ) {
			if ( groupPane.getComponent(i) instanceof SidebarGroup ) {
				SidebarGroup group = (SidebarGroup)groupPane.getComponent(i);
				if (group.isThis(tabLabel)) {
					addItem(itemComp, i);
					isGrouped = true;
					break;
				}
			}
		}
		if (!isGrouped) {
			String message = "No group for:" + itemComp.getName() + " ["+tabLabel+"]";
			AWindow.showStatus(message, this);
		}
	}
	
	public void addItem(JComponent itemComp, int tabIdx) {
		
		scrollJump = itemComp.getMaximumSize().height;
		
		if (this._user_useColorParent) {
			Color refColor = (Color)((JPanel)
				    ((JScrollPane)groupPane.getComponent(tabIdx)).getViewport()
					.getComponent(0)).getBackground();
			Color cl = new Color( refColor.getRed() , refColor.getGreen()-25, refColor.getBlue()-25);
			itemComp.setBackground( cl );
		}
		try {
			
			((JPanel)
		    ((JScrollPane)groupPane.getComponent(tabIdx)).getViewport()
			.getComponent(0)).add(itemComp);
			
			((JScrollPane)groupPane.getComponent(tabIdx)).setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			if (this._isVisibleScrollbars)
				((JScrollPane)groupPane.getComponent(tabIdx)).setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			else
				((JScrollPane)groupPane.getComponent(tabIdx)).setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
	
			((JPanel)
			((JScrollPane)groupPane.getComponent(tabIdx)).getViewport()
			.getComponent(0)).updateUI();
			
			((JScrollPane)groupPane.getComponent(tabIdx)).getViewport().setAutoscrolls(false);
	
			((JScrollPane)groupPane.getComponent(tabIdx)).getViewport().updateUI();
	
			((JScrollPane)groupPane.getComponent(tabIdx)).updateUI();
		} catch (ArrayIndexOutOfBoundsException e) {
			Monitor.debug(e);
			Monitor.log(UIRes.getLabel("warning.sidebar.nogroup"));
		}

	}
	
	public void addObj(JToggleButton itemComp, int tabIdx) {

		((JPanel)groupPane.getComponent(tabIdx)).add(itemComp);
		((JPanel)groupPane.getComponent(tabIdx)).updateUI();

	}

	private void groupSelector(ActionEvent evt, int compId) {
		int totalElements = (((Component)evt.getSource()).getParent()).getComponentCount();
		newCompId = compId;
		if (oldCompId == -1) oldCompId = totalElements-1;
		else oldCompId = lstCompId;
		toggleBar(evt, compId, oldCompId);
	}
	
	private void toggleBar(ActionEvent evt, int idx, int oldIdx) {
	    
	    if ((lstCompId == -1) && (idx == ((_sidebarLabels.length*2)-1)) ) { //&& oldIdx == ((groupOKLabels.length*2)-1)) {
			((JToggleButton)(((Component)evt.getSource()).getParent()).getComponent(oldIdx-1)).setSelected(false);
			lstCompId = idx; 
	    } else {
			
	        lstCompId = idx;
			
			if ( ((JToggleButton)evt.getSource()).isSelected() ) {
				
				((JToggleButton)(((Component)evt.getSource()).getParent()).getComponent(oldIdx-1)).setSelected(false);
				
				// java.awt.GridBagConstraints gridBagConstraints1 = ((GridBagLayout)jpWest.getLayout()).getConstraints( (Component)evt.getSource() );
				GridBagConstraints gridBagConstraints1 = ((GridBagLayout)groupPane.getLayout()).getConstraints(
						(((Component)evt.getSource()).getParent()).getComponent(idx)
						);
				GridBagConstraints gridBagConstraintsOld = ((GridBagLayout)groupPane.getLayout()).getConstraints(
						(((Component)evt.getSource()).getParent()).getComponent(oldIdx)
						);
				
				
				gridBagConstraints1.fill = GridBagConstraints.BOTH;
				gridBagConstraints1.weighty = 1.0;
				gridBagConstraintsOld.fill = GridBagConstraints.BOTH;
				gridBagConstraintsOld.weighty = 0.0;
				
				
				((GridBagLayout)groupPane.getLayout()).setConstraints(
						(((Component)evt.getSource()).getParent()).getComponent(idx)
						, gridBagConstraints1 );
				((GridBagLayout)groupPane.getLayout()).setConstraints(
						(((Component)evt.getSource()).getParent()).getComponent(oldIdx)
						, gridBagConstraintsOld );
			} else {
			    
				((JToggleButton)(((Component)evt.getSource()).getParent()).getComponent(oldIdx-1)).setSelected(true);
			    
				GridBagConstraints gridBagConstraints1 = ((GridBagLayout)groupPane.getLayout()).getConstraints(
						(((Component)evt.getSource()).getParent()).getComponent(idx)
						);
				GridBagConstraints gridBagConstraintsOld = ((GridBagLayout)groupPane.getLayout()).getConstraints(
						(((Component)evt.getSource()).getParent()).getComponent(oldIdx)
						);
				
				
				gridBagConstraints1.fill = GridBagConstraints.BOTH;
				gridBagConstraints1.weighty = 0.0;
				gridBagConstraintsOld.fill = GridBagConstraints.BOTH;
				gridBagConstraintsOld.weighty = 1.0;
				
				
				((GridBagLayout)groupPane.getLayout()).setConstraints(
						(((Component)evt.getSource()).getParent()).getComponent(idx)
						, gridBagConstraints1 );
				((GridBagLayout)groupPane.getLayout()).setConstraints(
						(((Component)evt.getSource()).getParent()).getComponent(oldIdx)
						, gridBagConstraintsOld );
				
			}
	    }
	    
		groupPane.updateUI();
	}
	
	public SideBarUI(FilteredProperties props) {
		super(true);
		
		String[][] groupLabels = {};
		Object[] oLabels = props.keySet().toArray();
		
		Vector vt = new Vector();
		TreeMap treeMap = new TreeMap(props);
		Iterator it = treeMap.keySet().iterator();
		while(it.hasNext()) {
			String key = String.valueOf(it.next());
			String disabledKey = key + ".disabled";
			if (!key.endsWith(".icon") && !key.endsWith(".disabled") )
				if (props.getProperty(disabledKey)==null)
					vt.addElement(key);
		}
		/*
		for (Enumeration e = props.keys(); e.hasMoreElements();) {
			String key = String.valueOf(e.nextElement());
			String disabledKey = key + ".disabled";
			if (!key.endsWith(".icon") && !key.endsWith(".disabled") )
				if (props.getProperty(disabledKey)==null)
					vt.addElement(key);
		}
		*/
		groupLabels = new String[vt.size()][2];
		
		for (int i = 0; i < vt.size(); i++) {
			String key = String.valueOf(vt.elementAt(i));
			String groupLabel = props.getProperty(key);
			groupLabels[i][0] = groupLabel;
			String icon = props.getProperty(key+".icon");
			groupLabels[i][1] = icon;
		} 
		
		
		this._sidebarLabels = groupLabels;
		initComponents();
	}
	
	public SideBarUI(String[][] groupLabels) {
		this._sidebarLabels = groupLabels;
		initComponents();
	}

	public SideBarUI(String[][] groupLabels, int width, int height) {
		this._sidebarWidth = width;
		this._sidebarHeight = height;
		this._sidebarLabels = groupLabels;
		initComponents();
	}

	/* BEGIN F4J GUI CODE */
	
	/** Creates new form JNMan */
	public SideBarUI() {}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	private void initComponents() {
	    
	    try {
			
	        URL prefsFile = getClass().getResource( internalResource );

	        if (prefsFile != null) {
	            
            	resPrefs = ResourceBundle.getBundle("res.gui.sidebar");
            	
	            // this._isVisibleScrollbars = Boolean.valueOf( resPrefs.getString("is.visiblescrolls") ).booleanValue();
				this._user_useSpecialGroups = Boolean.valueOf( resPrefs.getString("use.specialgroups") ).booleanValue();
				
				if (_user_useSpecialGroups)
					_isVisibleScrollbars = false;
				else
					_isVisibleScrollbars = true;
				
				
				this._user_useJlf = Boolean.valueOf( resPrefs.getString("use.jlf") ).booleanValue();
				this._user_useLabels = Boolean.valueOf( resPrefs.getString("use.labels") ).booleanValue();
				
				this._sidebarWidth = Integer.parseInt( resPrefs.getString("width") );
				this._sidebarHeight = Integer.parseInt( resPrefs.getString("height") );
				if (_user_useSpecialGroups)
					_sidebarHeight = _sidebarHeight + 16;
				this._user_isFilledButtons = Boolean.valueOf( resPrefs.getString("is.filledbuttons") ).booleanValue();
				this._user_useColorParent = Boolean.valueOf( resPrefs.getString("use.colorparent") ).booleanValue();
				
				
				/* Configurazioni non accettabili */
				if (_sidebarWidth < 160 && _user_useSpecialGroups)
					_user_useLabels = false;
				
				if (!_user_isFilledButtons && _sidebarWidth < 160)
					isOnlyIcon=true;
				
				
				
				int skinReportStatus = 0;

				try {
					if (this._user_useJlf) {
						Object lnf = Class.forName(resPrefs.getString("ui.jlf.driver")).newInstance();
				
						java.io.File skinFile = new java.io.File( getClass().getResource( resPrefs.getString("ui.jlf.theme") ).getPath() );
						// daisy3.otm , anidaisy.otm , flat7.otm , slushy.otm
				
						if(skinFile.exists()) {
	
							java.lang.reflect.Method m = (java.lang.reflect.Method)lnf.getClass().getMethod("setOyoahaTheme", new Class[] { java.io.File.class });
							m.invoke( lnf, new Object[] {skinFile});
	
						}
				
						javax.swing.UIManager.setLookAndFeel((LookAndFeel)lnf);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					try { 
						Object lnf = Class.forName("com.oyoaha.swing.plaf.oyoaha.OyoahaLookAndFeel").newInstance();
						javax.swing.UIManager.setLookAndFeel((LookAndFeel)lnf);
					} catch (Exception never) {
						never.printStackTrace();
						skinReportStatus = 1;
					}
			
				} finally {
					if (skinReportStatus > 0) {
						try { javax.swing.UIManager.setLookAndFeel( "javax.swing.plaf.metal.MetalLookAndFeel" ); }
						// javax.swing.plaf.metal.MetalLookAndFeel
						catch (Exception never) { never.printStackTrace(); }
					}
				}
				
				
				
	        }	        
	    } catch (Exception e) {
	        e.printStackTrace();
			
	        this._isVisibleScrollbars = true;
			this._user_useSpecialGroups = false;
			this._user_useJlf = false;
			this._user_useLabels = true;
				
			this._sidebarWidth = 150;
			this._sidebarHeight = 20;

	    }
    
	    
   
	    
		groupPane = new javax.swing.JPanel();
		
		setLayout(new BorderLayout());
		
		groupPane.setLayout(new GridBagLayout());
		
		int total = _sidebarLabels.length;
		for (int i=0,e=0; i < total; i++) {
			JToggleButton btGroupHeader = new JToggleButton();
			JPanel containerShortcuts = new JPanel();
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			
			String groupPanelName = _sidebarLabels[i][0];
			
			btGroupHeader.setText( groupPanelName );
			

			int redColor = normalizeColor( i );
			Color colorParent = new Color( redColor , 250, 204);
			
			// colorParent = new Color( colorParent.getBlue(), colorParent.getRed(), colorParent.getGreen() );
			
			
			if (this._sidebarLabels[i][1] != null) {
			    /*
			    // FEATURE : implementare sistema di raccolta propriet�
			     * specifiche per generazione elementi pulsante a icona con
			     * testo descrittivo mostrato in tooltips, anche per elementi
			     * aggiunti dinamicamente
			     */ 
			    String imageBaseName = _sidebarLabels[i][1];
			    String suffix = "";
			    if ( imageBaseName == BUTTON_DEFAULT ) {
			        imageBaseName = "/res/img/default";
			    } else {
					// 1) "nome-x" per immagine singola
					// 2) "nome" & "nome-open" per rolloverdoppio
			        if (imageBaseName.endsWith("-x"))
			            suffix = "";
			        else
			            suffix = "-open";
			        
					btGroupHeader.setToolTipText( btGroupHeader.getText() );
			        
					if (!this._user_useLabels) btGroupHeader.setText( null );
			    }
			    
				if (_user_useSpecialGroups) {
					ImageIcon imageIcon = null;
					if (getClass().getResource( imageBaseName + resPrefs.getString("ui.icons.extension") ) != null)
						imageIcon = new ImageIcon( getClass().getResource( imageBaseName + resPrefs.getString("ui.icons.extension") ) );
					else
						imageIcon = new ImageIcon( getClass().getResource( resPrefs.getString("ui.icons.sidebar.default") ) );
					btGroupHeader.setIcon(imageIcon);
					btGroupHeader.setRolloverSelectedIcon( imageIcon );
					
					if (suffix.equals("-open")) {
						ImageIcon open_imageIcon = new ImageIcon( getClass().getResource( imageBaseName + suffix + resPrefs.getString("ui.icons.extension") ) );
						btGroupHeader.setRolloverIcon( open_imageIcon );
						btGroupHeader.setSelectedIcon( open_imageIcon );
					}
					
					if (imageIcon == null && !this._user_useLabels)
						btGroupHeader.setText( groupPanelName );
				} else {
					int fontSize = 11;
					if ( (_sidebarHeight/2) > 20 )
						fontSize = (_sidebarHeight/2)-6;
					btGroupHeader.setFont(new Font("System", Font.PLAIN, fontSize));
				}
				
				
				
				
				btGroupHeader.setBackground( Color.LIGHT_GRAY );
			}
			
			btGroupHeader.setPreferredSize( new Dimension( this._sidebarWidth, this._sidebarHeight ) );
			btGroupHeader.setMinimumSize( new Dimension( this._sidebarWidth, this._sidebarHeight ) );
			btGroupHeader.setMaximumSize( new Dimension( this._sidebarWidth, this._sidebarHeight ) );
			
			// jToggleButton1.setLayout(new FlowLayout(FlowLayout.LEFT));

			if (this._user_useSpecialGroups) {
				btGroupHeader.setLayout(new BorderLayout());
				btGroupHeader.add(new ScrollNavigatorPane( e, colorParent ), BorderLayout.EAST);
			}
			    
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = e++;
			gridBagConstraints1.fill = GridBagConstraints.VERTICAL;
			
			final int trasferId = e;
			btGroupHeader.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					groupSelector(evt, trasferId);
				}
			});			
			
			btGroupHeader.setHorizontalAlignment(SwingConstants.LEFT);
			if (_user_useColorParent)
				btGroupHeader.setBackground( colorParent );
			groupPane.add(btGroupHeader, gridBagConstraints1);
			
			if (!_user_isFilledButtons) {
				javax.swing.BoxLayout boxLayout = new BoxLayout(containerShortcuts, javax.swing.BoxLayout.Y_AXIS);
				containerShortcuts.setLayout( boxLayout );
			} else
				containerShortcuts.setLayout( new GridLayout(0,1) );

			if ( isOnlyIcon )
				containerShortcuts.setLayout( new BoxLayout(containerShortcuts, BoxLayout.Y_AXIS) );
			
			colScrollerPane = colorParent;
			
			if (_user_useColorParent)
				containerShortcuts.setBackground( colorParent );
			else
				containerShortcuts.setBackground( new Color(240,240,240) );
			
			// jPanel12.setMaximumSize(new Dimension(tabWidth, 0));
			// jPanel12.setMinimumSize(new Dimension(tabWidth, 0));
			// jPanel12.setPreferredSize(new Dimension(tabWidth, 0));
			gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = e++;
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			if (i==total-1)
				gridBagConstraints1.weighty = 1.0;
			
			SidebarGroup jscroll = new SidebarGroup(groupPanelName);
			
			jscroll.setViewportBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
			
			jscroll.setViewportView( containerShortcuts );
			jscroll.setMaximumSize(new Dimension(this._sidebarWidth, 0));
			jscroll.setMinimumSize(new Dimension(this._sidebarWidth, 0));
			jscroll.setPreferredSize(new Dimension(this._sidebarWidth, 0));
			jscroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			jscroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);;

			groupPane.add(jscroll, gridBagConstraints1);
			
			/*
			// FEATURE : implementare sfondo immagine per pannelli
			 * vedi: Message-ID: <37100023.11054947@news.interaccess.com>#1/1 
			 */
			
			
		}
		

		add(groupPane, BorderLayout.WEST);
		
		JButton sidebarOptions = new JButton();
		sidebarOptions.setBackground(Color.ORANGE);
		sidebarOptions.setToolTipText("Imposta le opzioni della ebar");
		sidebarOptions.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent parArg0) {
				File f = new File( getClass().getClassLoader().getResource(".").getFile() + "/res/gui/sidebar.properties" );
				JOptionPane optionPane = new JOptionPane();
				optionPane.add( new OptionPropertiesUI(f, "res.gui.sidebar", "", "") );
				JOptionPane.showOptionDialog(null, null, "Opzioni della ebar", JOptionPane.YES_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{new OptionPropertiesUI(f,"res.gui.sidebar", "", "")},null);
				/*JFrame jf = new JFrame("Opzioni sidebar");
				jf.getContentPane().add( new OptionPropertiesUI(f, "res.gui.sidebar", "", "") );
				jf.show();
				*/
			}
		});
		add(sidebarOptions, BorderLayout.SOUTH);

	}

	private void scrollUp(ActionEvent evt, int panelId) {

		

		Point scrollPosition = ((JScrollPane)
				((JComponent)evt.getSource()).getParent().getParent().getParent().getParent()
				.getComponent( panelId+1 )
		).getViewport().getViewPosition();
		


		if (scrollPosition.y >= (scrollJump/2)) {
				((JScrollPane)
						((JComponent)evt.getSource()).getParent().getParent().getParent().getParent()
						.getComponent( panelId+1 )
				).getViewport().setViewPosition( new Point(0 , scrollPosition.y - scrollJump));
				
		}
	}

	private void scrollDown(ActionEvent evt, int panelId) {

		int maxPosition = ((JPanel) 
((JScrollPane)				((JComponent)evt.getSource()).getParent().getParent().getParent().getParent()
				.getComponent( panelId+1 )
).getViewport().getComponent(0)
).getHeight();

		Point scrollPosition = ((JScrollPane)
				((JComponent)evt.getSource()).getParent().getParent().getParent().getParent()
				.getComponent( panelId+1 )
		).getViewport().getViewPosition();
		;

		Dimension scrollSize = ((JScrollPane)
				((JComponent)evt.getSource()).getParent().getParent().getParent().getParent()
				.getComponent( panelId+1 )
		).getViewport().getExtentSize();
		;

		if (scrollPosition.y < ((maxPosition-(scrollJump/2))-scrollSize.height) ) {
			// Dev.log( "if: " + (maxPosition-scrollJump) + " & " + scrollPosition.y);
			((JScrollPane)
					((JComponent)evt.getSource()).getParent().getParent().getParent().getParent()
					.getComponent( panelId+1 )
			).getViewport().setViewPosition( new Point(0 , scrollPosition.y + scrollJump));
			;
		}

	}



	public int normalizeColor(int i) {
		i = Integer.parseInt("2" + i + "4");
		if (i > 255)
			i -= 255;
		if (i > 255)
			return normalizeColor( i );
		else
			return i;
	}	


	// Variables declaration - do not modify
	private javax.swing.JPanel groupPane;
	// End of variables declaration

	class ScrollNavigatorPane extends JPanel {
		int panelId;
		public ScrollNavigatorPane(final int panelId, final Color colorParent) {
			this.panelId = panelId;
			setLayout(new GridLayout(0,1));
			
			/*
			JCheckBox jck = new JCheckBox();
			jck.setOpaque(false);
			jck.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					__isVisibleScrollbars = ((JCheckBox)evt.getSource()).isSelected();
				}
			});
			jck.setToolTipText("Abilita scrollbars laterale");
			// add(jck);
			*/
			
			JPanel jpButt = new JPanel(new GridLayout(0,2));
			
			BasicArrowButton arrowUp = new BasicArrowButton(1);
			arrowUp.setBackground( new Color( colorParent.getBlue(), colorParent.getRed(), colorParent.getGreen() ) );
			arrowUp.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					scrollUp(evt, panelId);
				}
			});		
			
			ImageIcon imageIcon = new ImageIcon( getClass().getResource( resPrefs.getString("ui.icons.sidebar.scrollup") ) );
			JButtonUI jb = new JButtonUI();
			jb.setIcon(imageIcon);
			jb.setMaximumSize(new Dimension(8,4));
			jb.setPreferredSize(new Dimension(8,4));
			jb.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					scrollUp(evt, panelId);
				}
			});			
			// jpButt.add(jb);
			jpButt.add(arrowUp);
			
			BasicArrowButton arrowDown = new BasicArrowButton(5);
			arrowDown.setBackground( new Color( colorParent.getBlue(), colorParent.getRed(), colorParent.getGreen() ) );
			arrowDown.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					scrollDown(evt, panelId);
				}
			});
			
			imageIcon = new ImageIcon( getClass().getResource( resPrefs.getString("ui.icons.sidebar.scrolldown") ) );
			jb = new JButtonUI();
			jb.setIcon(imageIcon);
			jb.setMaximumSize(new Dimension(8,4));
			jb.setPreferredSize(new Dimension(8,4));
			jb.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					scrollDown(evt, panelId);
				}
			});		
			// jpButt.add(jb);
			jpButt.add(arrowDown);
			
			jpButt.setLayout(new GridLayout(2,0));
			jpButt.setMaximumSize(new Dimension(32,16));
			jpButt.setPreferredSize(new Dimension(32,16));	

			add(jpButt);



			setMaximumSize(new Dimension(70,5));
			setOpaque(false);
		}
	}


	class SidebarGroup extends JScrollPane {
		private String groupLabel = "";
		public SidebarGroup(String label) {
			super();
			groupLabel = label;
		}
		
		public final boolean isThis(String labelTest){
			return groupLabel.equals(labelTest);
		}
		
		public final String getGroupLabel() {
			return groupLabel;
		}
		public final void setGroupLabel(String parGroupLabel) {
			groupLabel = parGroupLabel;
		}
	}
	
	protected class JButtonUI extends javax.swing.JButton {

		public JButtonUI() {
			super();
			setUI( new BasicButtonUI() );
		}
		
	}
	
}
