/*
 * Text.java - FrameWork (FrameWork.jar)
 * Copyright (C) 2005
 * Source file created on 3-dic-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [Text]
 * TODO:
 *
 */

package com.blogspot.fravalle.util.text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import com.blogspot.fravalle.lib.monitor.Monitor;

/**
 * @author antares
 */
public class Text {
	  
	public static String validaCaratteri(String s) {
		if (s==null)
			return null;
	    String acceptedChars =
	      " abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";  // allowed chars
	    String result = "";
	    for ( int i = 0; i < s.length(); i++ ) {
	        if ( acceptedChars.indexOf(s.charAt(i)) >= 0 )
	           result += s.charAt(i);
	        }
	    return result;
	}
	
	public static Configuration creaConfigurazione() {
		return creaConfigurazione( System.getProperty("java.io.tmpdir") , new Properties[]{new Properties()});
	}
	
	public static Configuration creaConfigurazione(String path) {
		return creaConfigurazione(path, new Properties[]{new Properties()});
	}
	
	public static Configuration creaConfigurazione(Properties props) {
		return creaConfigurazione( System.getProperty("java.io.tmpdir") , new Properties[]{props});
	}
	
	private static StringBuffer getPropertiesList(Configuration configuration) {
		StringBuffer strBuffer = new StringBuffer(512);
		for (Enumeration e = configuration.keys(); e.hasMoreElements();) {
			String name = String.valueOf(e.nextElement());
			strBuffer.append( name+"=" );
			strBuffer.append( configuration.getProperty(name) );
			strBuffer.append( System.getProperty("line.separator") );
		}
		return strBuffer;
	}
	private static void setConfiguration(Configuration configuration, Properties prop) {
		if (prop != null)
			configuration.putAll(prop);
	}
	
	public static Configuration creaConfigurazione(String path, Properties props) {
		return creaConfigurazione( path , new Properties[]{props});		
	}
	
	public static Configuration creaConfigurazione(String path, Properties[] props) {
		File fileTemp = null;
		StringBuffer strBuffer = new StringBuffer(512);
		
		Configuration configuration = new Configuration();
		
		try {
			
			File tempDir = new File(path);
			fileTemp = File.createTempFile("session-rendering_",".ini", tempDir );
			
			for (int i = 0; i < props.length; i++) {
				configuration = new Configuration();
				setConfiguration(configuration, props[i]);
				strBuffer.append( getPropertiesList(configuration) );
			}
			
			configuration.setFilesystemReference(fileTemp);
			

			FileWriter fw = new FileWriter(fileTemp);

			fw.write(String.valueOf( strBuffer ));
			fw.flush();
			fw.close();
			
		} catch ( IOException e ) {
			e.printStackTrace();
		} finally {
			if (fileTemp != null)
				fileTemp.deleteOnExit();
		}
		// System.out.println(strBuffer.toString());
		return configuration;
	}
	
	static public StringBuffer readSrc(String fileSource) {
		String str = "";
		StringBuffer buffer = new StringBuffer(512);
		String line = "";
	    try {
	    	BufferedReader br = new BufferedReader(new FileReader(new File(fileSource)));
			
	    	while (( line = br.readLine() ) != null ) {
    			if (line!=null)
    				buffer.append(line + System.getProperty("line.separator"));
    				// str += line + System.getProperty("line.separator");
	    	}
		} catch (Exception ex) {
			Monitor.debug( ex );
		}
		return buffer;
	}
	
	
	static final public String trim(String s) {
		if (s.startsWith(" ") || s.endsWith(" ")) {
			if (s.startsWith(" "))
				s = s.substring(" ".length());
			if (s.endsWith(" "))
				s = s.substring(0,s.lastIndexOf(" "));
			s = trim(s);
		}
		return s;
	}
	
	final static public boolean isRounded(double w) {
		return Math.round(w) - w == 0.0;
	}
	
	final static public String formatNumberSequence(int number, int maxSequence) {
		String s = String.valueOf(number);
		if (s.length() <= maxSequence) {
			if (s.length() == 1)
				return "0"+s;
			else if (s.length() == 2)
				return "00"+s;
			else if (s.length() == 3)
				return s;
			else
				return s;
		} else
			return s;
	}
	
}
