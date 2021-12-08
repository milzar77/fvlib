/*
 * JXmlUI.java - jappframework (jappframework.jar)
 * Copyright (C) 2006
 * Source file created on 9-gen-2006
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [JXmlUI]
 * 2DO:
 *
 */

package com.blogspot.fravalle.lib.gui.rendering;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.w3c.dom.Document;

import com.blogspot.fravalle.lib.data.xml.XmlConstants;
import com.blogspot.fravalle.lib.gui.AWindow;
import com.blogspot.fravalle.lib.gui.GuiManager;
import com.blogspot.fravalle.lib.monitor.MainLogger;

/**
 * [][JXmlUI]
 * 
 * <p><b><u>2DO</u>:</b>
 * 
 * 
 * @author Francesco Valle - (antares)
 */
public class JXmlUI extends javax.swing.JPanel {
	/**
	 * It can be StringBuffer or String
	 */
	Object xmlSource;
	Object xmlBind;
	String xmlBindref;
	PropertyChangeSupport pcsMaskBind;
	GuiManager gui;
	
	final protected String empty = XmlConstants.emptyDocument;

	public Document getXmlDocument() {
		return gui.getXmlDocument();
	}
	
	public JXmlUI(){
		// this.setXmlSource( new String("/tmp/working/cache.xml") );
		// new StringBuffer("<xml title=\"Documento xml\"><p title=\"Plutoniano\"><a title=\"AGroup\">AAA</a><b title=\"BGroup\">Pluto</b></p></xml>");
		// "/home/antares/Documents/var/wings/archmap.svg";
		// this.initView();
	}
	public JXmlUI(Object source){
		this.setXmlSource(source);
		this.initView();
	}

	public void setXmlSource(Object source, Object bind, String bindRef) {
		this.xmlSource = source;
		this.xmlBind = bind;
		this.xmlBindref = bindRef;
	}
	
	public void setXmlSource(final Object parSource) {
		if (parSource instanceof String) {
			this.xmlSource = parSource;
		} else if (parSource instanceof StringBuffer) {
			this.xmlSource = parSource.toString();
		} else if (parSource instanceof File || parSource instanceof InputStream) {
					StringBuffer buffer = new StringBuffer(512);
					try {
						InputStream fins = null;
						if (parSource instanceof InputStream)
							fins = (InputStream)parSource;
						else {
							if ( ((File)parSource).exists() )
								fins = new FileInputStream( (File)parSource );
							else {
								MainLogger.getLog().info("READING: " + ((File)parSource).getPath().replace('\\','/') + " from " + parSource.getClass().getResource("/"));
								fins = parSource.getClass().getResourceAsStream( ((File)parSource).getPath().replace('\\','/') );
							}
						}
						
						if (fins != null) {
							final InputStreamReader inReader = new InputStreamReader(fins);
							final BufferedReader in = new BufferedReader(inReader);
							
							String line;
							while ((line = in.readLine()) != null) {
								buffer.append(line);
							}
							in.close();
							in.close();
							inReader.close();
							fins.close();
						}
					}
					catch ( IOException exception ) {
						exception.printStackTrace();
						setXmlSource(XmlConstants.emptyDocument);
					} finally {
						setXmlSource(buffer);
					}

		} else
			this.xmlSource = parSource;
		
		AWindow.setXmlBuffer(getXmlSource());
	}
	public Object getXmlSource() {
		return this.xmlSource;
	}
	
	protected void initView(PropertyChangeSupport pcsMask) {
		pcsMaskBind = pcsMask;
		initView();
	}
	
	public void resetView(File f) {
		this.removeAll();
		this.updateUI();
		this.setXmlSource(f);
		this.initView();
	}
	
	public void initView() {
		pcsMaskBind = new PropertyChangeSupport(this);
			boolean parseError = false;
			
			final JPanel xmlMap = new JPanel(new GridLayout(0,1)); // empty here
			if (!this.getXmlSource().equals(empty)) {
				try {
					gui = new GuiManager(xmlMap, this.getXmlSource());
					gui.build(xmlBind, xmlBindref);
				} catch (Exception e) {
					parseError = true;
					e.printStackTrace();
				}
			}
			
			final JPanel xmlView = new JPanel();
			if (!this.getXmlSource().equals(empty)) {
				try {
					gui = new GuiManager(xmlView, this.getXmlSource());
					gui.buildXmlview();
				} catch (Exception e) {
					parseError = true;
					e.printStackTrace();
				}
			}
			
			final JPanel xmlTree = new JPanel(new GridLayout(0,1));
			if (!this.getXmlSource().equals(empty)) {
				try {
					gui = new GuiManager(xmlTree, this.getXmlSource());
					gui.buildTreeview();
				} catch (Exception e) {
					parseError = true;
					e.printStackTrace();
				}
			}
			
			
			final JTabbedPane jtab = new JTabbedPane(JTabbedPane.BOTTOM);
			
			final JTextArea jtxt = new JTextArea();
			jtxt.setWrapStyleWord(true);
			
			if (!this.getXmlSource().equals(empty)) {
				if (parseError)
					jtxt.setText( String.valueOf(this.getXmlSource()) );
				else
					jtxt.setText( gui.getXmlDocument().getDocumentElement().toString() );
				
			} else
				jtxt.setText( empty );
			
			jtxt.getDocument().addDocumentListener(new DocumentListener(){

				public void changedUpdate(DocumentEvent parE) {
					// jtxt.setText(gui.getXmlDocument().getDocumentElement().toString());
				}

				public void insertUpdate(DocumentEvent parE) {
					
				}

				public void removeUpdate(DocumentEvent parE) {
					// jtxt.setText(gui.getXmlDocument().getDocumentElement().toString());
				}
			});
			
			JScrollPane jscroll = new JScrollPane();
			jscroll.setViewportView(jtxt);
			JPanel txt = new JPanel(new BorderLayout());
			txt.add(jscroll, BorderLayout.CENTER);
			
			JButton jb = new JButton();
			jb.setText("Rigenera UI");
			jb.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent parE) {
					xmlMap.removeAll();
					pcsMaskBind.firePropertyChange(xmlBindref,null,jtxt.getText());
					// gui.unbind(jtxt.getText());
					/*try {
						gui = new GuiManager(jpan, jtxt.getText());
						gui.build(xmlBind, xmlBindref);
						jtab.setSelectedIndex(0);
					} catch (SaxApplicationException e) {
						e.printStackTrace();
					}
					*/
				}});

			txt.add(jb, BorderLayout.SOUTH);
			
			JScrollPane jscrollXmlmap = new JScrollPane();
			jscrollXmlmap.setViewportView(xmlMap);

			JScrollPane jscrollXmlview = new JScrollPane();
			jscrollXmlview.setViewportView(xmlView);
			
			JScrollPane jscrollTreeview = new JScrollPane();
			jscrollTreeview.setViewportView(xmlTree);
			
			jtab.addTab("Mappa xml",jscrollXmlview);
			jtab.addTab("Dati xml",jscrollXmlmap);
			jtab.addTab("Rappresentazione xml",jscrollTreeview);
			jtab.addTab("Contenuto xml",txt);
			if (this.getXmlSource().equals(empty) || parseError)
				jtab.setSelectedIndex(1);
			
			this.setLayout(new GridLayout(1,1));
			this.add(jtab);

	}
	
	protected boolean isChanged(){
		return gui.isChanged();
	}
	
}
