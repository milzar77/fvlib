/*
 * ProgressMonitorUI.java - jappframework (jappframework.jar)
 * Copyright (C) 2006
 * Source file created on 26-gen-2006
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [ProgressMonitorUI]
 * 2DO:
 *
 */

package com.blogspot.fravalle.lib.gui;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.AbstractCollection;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

/**
 * @author antares
 */
public class ProgressMonitorUI extends JProgressBar {
	
	private int localStart = 0;
	private int localEnd = 0;
	private int localUnit = 0;
	
	long startAt = 0;
	
	Thread reader;
	
	String lineEnd = System.getProperty("line.separator");
	boolean isTextArea; 
	boolean isTextBuffer;
	
	public ProgressMonitorUI() {
		super();
	}
	
	void setLocalUnit(int unit) {
		localUnit = unit;
	}
	void setLocal(int unit) {
		localUnit += unit;
		setValue( localUnit );
	}
	
	public void reset() {
		setLocal(0);
		setValue( localUnit );
	}
	
	void setLimit(int start, int end) {
		this.localStart = start;
		this.localEnd = end;
		setMinimum( localStart );
		setMaximum( localEnd );
	}

	public boolean running() {
		if (reader != null)
			return reader.isAlive();
		else
			return false;
	}
	public long proc() {
		return System.currentTimeMillis() - startAt;
	}
	
	public void monitor(Object monitorSource, final Object outputSource, final Hashtable structure) {
		startAt = System.currentTimeMillis();
		isTextArea = outputSource instanceof JTextArea; 
		isTextBuffer = outputSource instanceof StringBuffer;
		
		try {
			if (monitorSource instanceof File)
				this.fileMonitor((File)monitorSource, outputSource, structure);
			else if (monitorSource instanceof Vector)
				this.vectorMonitor((Vector)monitorSource, outputSource);
			else if (monitorSource instanceof Vector)
				this.listMonitor((AbstractCollection)monitorSource, outputSource);
		} catch ( FileNotFoundException e ) {
			e.printStackTrace();
		} catch ( IOException e ) {
			e.printStackTrace();
		} finally {
			reader.start();
		}
	}
	
	private void vectorMonitor(final Vector vector, final Object outputSource) throws FileNotFoundException, IOException {
		this.setLimit( 0, vector.size() );

		reader = new Thread("monitor loader") {
			
			public void run() {
				try {
					for (int i = 0; i < vector.size(); i++) {
						setLocal(i);
						updateSource(outputSource, vector.elementAt(i));
					}
					
					EventQueue.invokeLater(new Runnable() {
						public void run() {
							validate();
						}
					});
				}
				catch ( Exception exception ) {
					exception.printStackTrace();
				} finally {
					setValue(getMinimum());
				}
			}
		};
	}
	
	private void listMonitor(final AbstractCollection collection, final Object outputSource) throws FileNotFoundException, IOException {
		this.setLimit( 0, collection.size() );
		reader = new Thread("monitor loader") {
			
			public void run() {
				try {
					int counter = 0;
					while ( collection.iterator().hasNext() ) {
						counter++;
						Object o = collection.iterator().next();
						setLocal(counter);
						updateSource(outputSource, o);
					}

					EventQueue.invokeLater(new Runnable() {
						public void run() {
							validate();
						}
					});
				}
				catch ( Exception exception ) {
					exception.printStackTrace();
				} finally {
					setValue(getMaximum());
				}
			}
		};
	}
	
	private void fileMonitor(File f, final Object outputSource, final Hashtable structure) throws FileNotFoundException, IOException {
		final FileInputStream fins = new FileInputStream( f );
		this.setLimit( 0, fins.available() );

		final InputStreamReader inReader = new InputStreamReader(fins);
		final BufferedReader in = new BufferedReader(inReader);
		reader = new Thread("monitor loader") {
			
			public void run() {
				try {
					if (isTextArea)
						((JTextArea)outputSource).setText("");
					
					String line;
					while ((line = in.readLine()) != null) {
						setLocal(line.length());
						updateSource(outputSource, line);
						if (!line.equals(""))
							build(structure,line);
					}
					in.close();
					in.close();
					inReader.close();
					fins.close();
					
					EventQueue.invokeLater(new Runnable() {
						public void run() {
							validate();
						}
					});
				}
				catch ( IOException exception ) {
					exception.printStackTrace();
				} finally {
					setValue(getMinimum());
				}
			}
		};
	}
	
	synchronized private void updateSource(Object source, Object content) {
		
		if (isTextArea)
			((JTextArea)source).append( String.valueOf(content) + lineEnd );

		if (isTextBuffer)
			((StringBuffer)source).append( String.valueOf(content) + lineEnd );
		
	} 
	
	private void build(Hashtable structure, String str) {
		if (str.startsWith("#include")) {
			if ( structure.get("includes")==null )
				structure.put("includes",(Vector)new Vector());
			((Vector)structure.get("includes")).add(str);
		} else if (!str.equals("") && !str.startsWith("/*") && !str.startsWith("#")) {
			if ( structure.get("objects")==null )
				structure.put("objects",(Vector)new Vector());
			
			addObject(structure.get("objects"), str);

		}
	}
	
	private void addInclude(Vector vt) {
		// ((Vector)structure.get("includes")).add(str);
	}
	
	Vector flusso = new Vector();
	
	boolean isNewObject = false;
	int nestedCounter = 0;
	
	private void addObject(Object vt, String buffer) {
		if ( isObjectStart(buffer) && isObjectEnd(buffer) ) {
			PovrayObject pov = new PovrayObject(PovrayObject.SIMPLE,buffer);
			((Vector)vt).add(pov);
		} else {
			if ( !isObjectStart(buffer) && !isObjectEnd(buffer) ) {
				if (flusso != null)
					flusso.add(buffer);
			} else {
				
				if (isObjectStart(buffer)) {
					nestedCounter++;
					if (isNewObject) {
						flusso = new Vector();
						isNewObject = false;
						nestedCounter = 0;
					}
					flusso.add(buffer);
				} else if (isObjectEnd(buffer)) {
					/* implementare chiusura */
					flusso.add(buffer);
					if (nestedCounter==0) {
						PovrayObject pov = new PovrayObject(PovrayObject.SIMPLE,flusso);
						((Vector)vt).add(pov);
						isNewObject = true;
					} else if (nestedCounter==1) {
						PovrayObject pov = new PovrayObject(PovrayObject.COMPLEX,flusso);
						/* PATCH ANTI DUPLICATO, non ho compreso il motivo */ if ( !((Vector)vt).contains(pov) )
							((Vector)vt).add(pov);
						isNewObject = true;
					} else if (nestedCounter>1) {
						isNewObject = false;
					}
					
				}
				
			}
		}
	}
	
	boolean isObjectStart(String s){
		return s.indexOf("{") != -1;
	}

	boolean isObjectEnd(String s){
		return s.indexOf("}") != -1;
	}

}

class PovrayObject extends Vector {
	
	public static int SIMPLE = 0;
	public static int COMPLEX = 1; 
	
	String[] types = {"Oggetto semplice","Oggetto complesso"};
	String name;
	Vector source;
	
	PovrayObject(int type, String parSource){
		this.name = types[type];
		this.source = new Vector();
		this.source.add(parSource);
		add(name);
		add( source );
	}
	
	PovrayObject(int type, Vector parSource){
		this.name = types[type];
		this.source = parSource;
		add(name);
		add( source );
	}
	public String toString(){
		String parse = String.valueOf(source.elementAt(0));
		if (parse.indexOf("{")==-1)
			return parse;
		else
			return parse.substring(0,parse.indexOf("{"));
	}
} 
