/*
 * OptionPropertiesUI.java - jappframework (jappframework.jar)
 * Copyright (C) 2006
 * Source file created on 1-feb-2006
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [OptionPropertiesUI]
 * 2DO:
 *
 */

package com.blogspot.fravalle.lib.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Properties;
import java.util.TreeMap;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.blogspot.fravalle.lib.monitor.MainLogger;
import com.blogspot.fravalle.util.AResource;
import com.blogspot.fravalle.util.SettingRes;
import com.blogspot.fravalle.util.UIRes;

/**
 * @author francesco
 */
public class OptionPropertiesUI extends JPanel {
	
	final String localSettingsFile = "/res/application.properties";
	final String localSettingsBundle = "res.application";
	
	
	String propPathFile;
	String bundleName;
	
	NumberFormat n = NumberFormat.getInstance();
	
	Properties options;
	private String excludedKeys;
	
	Color optionsColour;
	
	JPanel optionsPanel = new JPanel();
	JTabbedPane tabPanel;
	
	TreeMap tm;
	
	boolean isAppletPanelOptions = false;
	boolean isOptionParser = false;
	
	public OptionPropertiesUI() {
		super();
		tabPanel = new JTabbedPane();
		optionButton = new JButton();
		if (getClass().getClassLoader().getResource("/")!=null)
			propPathFile = getClass().getClassLoader().getResource("/").getFile() + localSettingsFile;
		else {
			int idx = localSettingsFile.indexOf("/");
			if (idx!=-1)
				propPathFile = localSettingsFile.substring(idx+1);
			else
				propPathFile = localSettingsFile;
		}
		
	}
	
	public OptionPropertiesUI(Class c) {
		this();

		bundleName = localSettingsBundle;
		optionsColour = Color.LIGHT_GRAY;
		
		String filterName = c.getName();
		if (filterName.lastIndexOf(".") != -1)
			filterName = filterName.substring( filterName.lastIndexOf(".")+1 );
		isAppletPanelOptions = true;
		isOptionParser = isAppletPanelOptions;
		excludedKeys = "";
		initPanel(filterName);
		optionButton.setBackground(optionsColour);
		optionButton.setFont(new Font("System", Font.PLAIN, 7));
		optionButton.setToolTipText("Mostra il pannello delle opzioni");
		optionsPanel.setBorder(BorderFactory.createTitledBorder("Opzioni MDI"));
	}
	
	public OptionPropertiesUI(String filterKey, String excludes) {
		this();
		
		
		bundleName = localSettingsBundle;
		optionsColour = Color.ORANGE;
		
		propPathFile = new File(bundleName.replace('.','/')+".properties").getAbsolutePath();
		
		isOptionParser = filterKey.indexOf(".") != -1;
		excludedKeys = excludes;
		initPanel(filterKey);
		optionButton.setBackground(optionsColour);
		optionButton.setFont(new Font("System", Font.PLAIN, 7));
		optionButton.setToolTipText("Salva le opzioni \"" + filterKey + "\"");
		optionsPanel.setBorder(BorderFactory.createTitledBorder("Opzioni"));
	}
	
	public OptionPropertiesUI(String filterKey) {
		this(filterKey, "");
		optionsPanel.setBorder(BorderFactory.createTitledBorder("Opzioni"));
	}

	public OptionPropertiesUI(File f, String parBbundleName, String filterKey, String excludes) {
		this();
		
		propPathFile = f.getAbsolutePath();
		
		bundleName = parBbundleName;
		optionsColour = Color.ORANGE;
		
		isOptionParser = filterKey.indexOf(".") != -1;
		excludedKeys = excludes;
		initPanel(filterKey);
		optionButton.setBackground(optionsColour);
		optionButton.setFont(new Font("System", Font.PLAIN, 7));
		optionButton.setToolTipText("Salva le opzioni \"" + filterKey + "\"");
	}
	

	
	public void initPanel(String filterKey) {	
		
		setLayout(new BorderLayout());
		// panel.setBackground(Color.LIGHT_GRAY);
		
		JScrollPane scroller = null;
		
		// optionsPanel.setLayout(new GridLayout());
		optionButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent parArg0) {
				/*if (!optionsPanel.isVisible()) {
					optionsPanel.setVisible(true);
					// scroller.setVisible(true);
					// optionsPanel.getParent().getParent().getComponent(1).setVisible(false);
				} else {
					// scroller.setVisible(false);
					optionsPanel.setVisible(false);
					// optionsPanel.getParent().getParent().getComponent(1).setVisible(true);
				}*/
				saveOptions();
			}
		});
		
		if (bundleName!=null){
			if (isAppletPanelOptions)
				add(optionButton, BorderLayout.WEST);
			else
				add(optionButton, BorderLayout.EAST);
		}
		// optionsPanel.setBackground(Color.LIGHT_GRAY);
		
		
		/* crea setaccio */
		
		TreeMap tmContainer = new TreeMap();
		tm = this.loadOptions(filterKey);
		Iterator it = tm.keySet().iterator();
		while (it.hasNext()) {
			String key = (String)it.next();
			if (key.indexOf(".")!=-1) {
				tmContainer.put(key.substring(0,key.indexOf(".")),"");
			} else {
				tmContainer.put(key,"");
			}
		}
		System.out.println(tmContainer);
		// System.exit(0);
		
		Iterator it2 = tmContainer.keySet().iterator();
		while (it2.hasNext()) {
			String key = (String)it2.next();
			optionsPanel = new JPanel();
			optionsPanel.add( this.loadPanels(key) );
			
			scroller = new JScrollPane();
			scroller.setViewportView(optionsPanel);
			scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			// new Dimension(350,400)
			scroller.setPreferredSize(new Dimension(350,400));
			tabPanel.addTab(key,scroller);
		}
		
		// optionsPanel.setPreferredSize(new Dimension(350,400));
		// optionsPanel.setVisible(false);
		
		add(tabPanel, BorderLayout.CENTER);
		
		setPreferredSize(new Dimension(630,400));
		
	}
	
	final void saveOptions() {

		Properties pr = AResource.getAllProps(bundleName, options);
		File fOut = new File( propPathFile );
		
		try {

			FileOutputStream fos = new FileOutputStream(fOut);
			Properties savedProps = new Properties();
			savedProps.putAll(pr);
			savedProps.store(fos, null);
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			
		}
		
		
	}
	
	final public Properties getOptions() {
		return options;
	}
	
	final TreeMap loadOptions(String filterKey) {
		JPanel settings = new JPanel();
		settings.setLayout(new BoxLayout(settings, BoxLayout.Y_AXIS));
		settings.setLayout(new GridBagLayout());
		
		if (localSettingsBundle.equals(bundleName))
			this.options = SettingRes.getPropertiesGroup(filterKey, isOptionParser, excludedKeys);
		else {
			if (bundleName==null) {
				try {
					options = new Properties();
					options.load(new FileInputStream(propPathFile));
				} catch ( FileNotFoundException e ) {
					e.printStackTrace();
				} catch ( IOException e ) {
					e.printStackTrace();
				}
			} else
				this.options = AResource.getAllProps(bundleName, options);
		}
		/*
		Hashtable tb = new Hashtable(options);
		TreeMap map = new TreeMap(tb);
		*/

		return new TreeMap(options);
	}
	
	final JPanel loadPanels(String filterKey) {
		JPanel settings = new JPanel();
		settings.setLayout(new BoxLayout(settings, BoxLayout.Y_AXIS));
		settings.setLayout(new GridBagLayout());
	
		int elementCounter = 0;
		Iterator it = tm.keySet().iterator();
		while (it.hasNext()) {
			final String key = (String)it.next();
		/*for (Enumeration e = options.keys(); e.hasMoreElements();) {
			final String key = (String)e.nextElement();*/
			if (key.startsWith(filterKey)) {
				
				GridBagConstraints gridConst = new GridBagConstraints();
				gridConst.gridx=0;
				gridConst.gridy=elementCounter;
				gridConst.weightx=0.2;
				// gridConst.gridwidth=1;
				gridConst.fill=GridBagConstraints.HORIZONTAL;
				gridConst.insets=new Insets(10,5,10,5);
				
				
				
				String value = options.getProperty(key);
				boolean isOptionFilesystem = false;
				
				File f = null;
				if (value.indexOf("\\") != -1 || value.indexOf("/") != -1) {
					f = new File(value);			
					isOptionFilesystem = f.exists();
					if (!f.exists()) {
						if (getClass().getClassLoader().getResource(".")!=null)
							f = new File( getClass().getClassLoader().getResource(".").getFile() + value);
						/*else {
							if (getClass().getResource(".")!=null)
								f = new File( getClass().getResource(".").getFile() + value);
						}*/
						if (value != null && getClass().getClassLoader().getResource(value) != null)
							f = new File( getClass().getClassLoader().getResource(value).getFile() );
	
						isOptionFilesystem = f.exists();
					}
				}
				final JTextField text = new JTextField(value);
				String label = key;
				if (label.indexOf(".")!=-1) {
					label = label.substring(label.indexOf("."));
					if (label.indexOf(".")!=-1)
						label = label.replace('.', ' ');
				}
				
				text.setColumns(25);
				this.formatItem(text, value);
	
				text.getDocument().addDocumentListener(new DocumentListener(){
	
					public void changedUpdate(DocumentEvent parArg0) {}
	
					public void insertUpdate(DocumentEvent parArg0) {
						String txt = text.getText();
						if (txt.indexOf("\\")!=-1)
							txt = txt.replace('\\','/');
						options.setProperty(key, txt);
					}
	
					public void removeUpdate(DocumentEvent parArg0) {
						String txt = text.getText();
						if (txt.indexOf("\\")!=-1)
							txt = txt.replace('\\','/');
						options.setProperty(key, text.getText());
					}
					
				});
				
				String newLabel = "";
				if (label.length() > 10)
					newLabel = label.substring(0,10) + "...";
				else
					newLabel = label;
				
				JLabel jLabel = new JLabel(" "+newLabel.toUpperCase());
				jLabel .setToolTipText(label);
				gridConst.gridx=0;
				gridConst.gridy=elementCounter;
				gridConst.fill=GridBagConstraints.BOTH;
				jLabel.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));
				settings.add(jLabel,gridConst);
				
				if (isOptionFilesystem) {
					JButton fileOpen = new JButton("...");
					fileOpen.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));
					final JFileChooser dialogFile = new JFileChooser();
					final File fTemp = f;
					fileOpen.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent parArg0) {
							dialogFile.setCurrentDirectory(fTemp.getParentFile());
							String label = "Select file";
							if (fTemp.isDirectory()) {
								dialogFile.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
								label = "Select directory";
							}
							int returnVal = dialogFile.showDialog(null, label);
							if (returnVal == JFileChooser.APPROVE_OPTION) {
								MainLogger.getLog().info( "Selected item:" + dialogFile.getSelectedFile() );
								
								if (fTemp.isDirectory())
									text.setText(dialogFile.getSelectedFile().getAbsolutePath()+System.getProperty("file.separator"));
								else
									text.setText(dialogFile.getSelectedFile().getAbsolutePath());
								
							} else {
								MainLogger.getLog().warning( UIRes.getLabel("messages.file.noselection") );
							}
						}
					});
					
					
					// text.setBorder(BorderFactory.createTitledBorder(label));
					gridConst.gridx=gridConst.gridx+1;
					gridConst.weightx=0.7;
					
					settings.add(text,gridConst);
					gridConst.gridx=gridConst.gridx+1;
					gridConst.weightx=0.1;
					gridConst.fill=GridBagConstraints.VERTICAL;
					settings.add(fileOpen,gridConst);
				} else {
					
					// text.setBorder(BorderFactory.createTitledBorder(label));
					gridConst.gridx=gridConst.gridx+1;
					gridConst.gridwidth=GridBagConstraints.REMAINDER;
					gridConst.weightx=0.8;
					gridConst.fill=GridBagConstraints.HORIZONTAL;
					settings.add(text,gridConst);
				}
				
				
				
				
				elementCounter++;
			}
		}

		return settings;
	}

	JButton optionButton;
	
	
	void formatItem(JTextField component, Object value) {
		try {
			Double d = new Double( String.valueOf(value) );
			n.setGroupingUsed(false);
			component.setText( n.format(d) );
			component.setHorizontalAlignment(JTextField.RIGHT);
			return;
		} catch (Exception e) {
			// e.printStackTrace();
		}

		if ( String.valueOf(value).equals("true") || String.valueOf(value).equals("false")) {
			component.setHorizontalAlignment(JTextField.CENTER);
			component.setBackground(Color.GRAY);
			component.setForeground(Color.WHITE);
			return;
		}

	};
	
}
