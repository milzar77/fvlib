/*
 * FileSystemReader.java - jpovnet (jpovnet.jar)
 * Copyright (C) 2006
 * Source file created on 7-feb-2006
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [FileSystemReader]
 * 2DO:
 *
 */

package com.blogspot.fravalle.util.filesystem;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.swing.ImageIcon;

/**
 * @author francesco
 */
abstract public class FileSystemReader {

	static final public byte[] getResourceAsBytes(String path) {
		byte[] bytes = {};
		try {
			InputStream input;
			if (new File(path).exists())
				input = new FileInputStream(path);
			else
				input = new Object().getClass().getResourceAsStream(path);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = input.read(buffer)) > 0) {
				baos.write(buffer, 0, len);
			}
			input.close();
			bytes = baos.toByteArray();
		} catch ( IOException e ) {
			System.out.println("I/O ERROR: " + e.getMessage());
		}
			
		return bytes;
	}
	
	static final public ImageIcon getImageIcon(String path) {
		ImageIcon imageIcon = null;
		try {
			if (!(new File(path).exists()))
				path = new Object().getClass().getResource("/").getPath() + path;
				// byte[] bts = getResourceAsBytes(path);
			imageIcon = new ImageIcon( getResourceAsBytes(path) );
		} catch (Exception eof) {
			System.out.println("ERROR, STREAM NOT READY: " + eof.getMessage());
		}
		return imageIcon;
	} 

	static final public CachedFile getCached(String path, long seed) {
		if (!(new File(path).exists())) {
			path = new Object().getClass().getResource("/").getPath() + path;
		}
		
		File testCache = new File(path);
		if (!testCache.exists()) {
			//System.out.println("============ CAHE DOESN'T EXIST!!!");
			return new CachedFile();
		}
			
		long last = testCache.lastModified();
		//System.out.println("============ LAST MODIFIED: " + last);
		
		if (last == seed)
			;
		else {
			//System.out.println("============ NOT CAHED!!!");
			return new CachedFile( getImageIcon(path) , last );
		}
		
		//System.out.println("============ CAHED!!!");
		return new CachedFile();
	} 
	
	final static public StringBuffer getText(String path) throws IOException {
		StringBuffer sb = new StringBuffer(512); 
		InputStream input = new FileInputStream(path);
		InputStreamReader inputReader = new InputStreamReader(input);
		BufferedReader reader = new BufferedReader(inputReader);
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		reader.close();
		input.close();
		return sb;
	}
	
}

