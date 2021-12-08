/*
 * JarReader.java - jpovnet (jpovnet.jar)
 * Copyright (C) 2006
 * Source file created on 5-feb-2006
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [JarReader]
 * 2DO:
 *
 */

package com.blogspot.fravalle.util.filesystem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.jar.JarFile;
import javax.swing.ImageIcon;

/**
 * @author francesco
 */
public class JarReader {
	
	static boolean isJarLoader = JarReader.isJarLoader();
	
	static private final JarReader jarLoader = new JarReader();
	
	JarFile jarFile;
	
	/**
	 * 
	 */
	private JarReader() {
		super();
	}
	
	final static public boolean isJarLoader(){
		String resource = "/" + new JarReader().getClass().getName().replace('.','/') + ".class";
		URL url = new JarReader().getClass().getResource(resource);
		String path = "";
		if (url!=null)
			path = url.getFile();
		else
			path = "Not found!!!";
		
		/*
		 * Otherwise sample:
		 	java.class.path=ponf.jar
		 */
		
		return url!=null && path.indexOf(".jar!/") != -1;
	}
	
	static final public byte[] getResourceAsBytes(String path) {
		byte[] bytes = {};
		try {
			InputStream input = jarLoader.getClass().getResourceAsStream(path);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = input.read(buffer)) > 0) {
				baos.write(buffer, 0, len);
			}
			input.close();
			bytes = baos.toByteArray();
			
		} catch ( IOException e ) {
			e.printStackTrace();
		}
		return bytes;
	}
	
	static final public ImageIcon getImageIcon(String path) {
		ImageIcon imageIcon = null;
		if (isJarLoader) {
			imageIcon = new ImageIcon( getResourceAsBytes(path) );
		} else {
			String basePath = new Object().getClass().getResource("/").getPath();
			System.out.println("OUT: " + basePath);
			imageIcon = new ImageIcon( getResourceAsBytes(path) );
			// MainLogger.getLog().info("NOT YET IMPLEMENTED!!!");
		}
		return imageIcon;
	} 

}
