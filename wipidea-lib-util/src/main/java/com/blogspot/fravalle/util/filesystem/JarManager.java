/*
 * JarManager.java - jpovnet (jpovnet.jar)
 * Copyright (C) 2006
 * Source file created on 5-feb-2006
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [JarManager]
 * 2DO:
 *
 */

package com.blogspot.fravalle.util.filesystem;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

/**
 * @author francesco
 */
public class JarManager {

	JarFile jarFile;
	
	Hashtable htResourcesLoaded = new Hashtable();
	
	public JarManager(Class fromClass, String name, String filter) {
		try {
			String path = "";
			if (fromClass.getResource("/")!=null)
				path = fromClass.getResource(".").getFile();
			File f = new File( path + name );
			// String theJar = System.getProperty("java.class.path");

			Logger.getAnonymousLogger().info(f.getAbsolutePath());
			
			jarFile = new JarFile(f);
			
			Enumeration e = jarFile.entries();
			while (e.hasMoreElements()) {
				JarEntry entry = (JarEntry)e.nextElement();
				if (filter.equals("")) {
					htResourcesLoaded.put(entry.getName(),entry);
				} else {
					if (entry.getName().endsWith(filter)) {
						htResourcesLoaded.put(entry.getName(),entry);
					} else if (entry.getName().endsWith(".MF"))
						System.out.println("Excluded: "+entry);
				}
				
			}
			
			Logger.getAnonymousLogger().info(
					"============= LIST ================="
					+ System.getProperty("line.separator")
					+ htResourcesLoaded
			);

			
		} catch ( IOException e1 ) {
			e1.printStackTrace();
		}
	}
	
	public JarManager(Class fromClass, String name) {
		this(fromClass, name, ".png");
	}
	
	public JarManager(File f) {
		super();
		try {
			jarFile = new JarFile(f);
			Enumeration e = jarFile.entries();
			while (e.hasMoreElements()) {
				JarEntry entry = (JarEntry)e.nextElement();
			}
		} catch ( IOException e1 ) {
			e1.printStackTrace();
		}
	}
	
	
	private void searchStream(String resource) {
		Logger.getAnonymousLogger().info( "================= FLUSSO ================="
				+ "Searching for: " + resource
		);

		InputStream is = getClass().getResourceAsStream(resource);
		if (is != null) {
			try {
				Logger.getAnonymousLogger().fine( "Stream size: " + is.available() );
				is.close();
			} catch ( IOException e ) {
				e.printStackTrace();
			}
		} else {
			Logger.getAnonymousLogger().info( "No stream!!!" );
		}
	}
	
	private String searchResource(String resource){
		URL url = getClass().getResource(resource);
		String path = null;
		if (url!=null)
			path = url.getFile();
		else
			path = "Not found!!!";
		
		if (url!=null) {
			if (path.indexOf(".jar!/") == -1)
				return path;
			else
				return "jar:"+path;
		} else
			return null;
	}
	
	final public StringBuffer getText(String entryPath) throws IOException {
		JarEntry entry = this.getEntry(entryPath);
		if (entry==null)
			throw new IOException("File ["+entryPath+"] doesn't exist!");
		return this.getText(entry);
	}
	
	final private StringBuffer getText(JarEntry entry) throws IOException {
		StringBuffer sb = new StringBuffer(512); 
		InputStream input = jarFile.getInputStream(entry);
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

	final public Enumeration getAllEntries() {
		return htResourcesLoaded.keys();
	}
	
	final public JarEntry getEntry(String entryPath) {
		if (entryPath.startsWith("/"))
			entryPath = entryPath.substring(1);
		return (JarEntry)htResourcesLoaded.get(entryPath);
	}
	
	final public ImageIcon getImageIcon(String entryPath) {
		JarEntry entry = this.getEntry(entryPath);
		return this.getImageIcon(entry);
	} 

	final private ImageIcon getImageIcon(JarEntry entry) {
		ImageIcon imageIcon = null;
		imageIcon = new ImageIcon( this.getResourceAsBytes(entry) );
		return imageIcon;
	} 
	
	final public byte[] getResourceAsBytes(JarEntry entry) {
		byte[] bytes = {};
		try {
			InputStream input = jarFile.getInputStream(entry);
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
	
	final public void writeResource(JarEntry entry) {
		try {
			InputStream input = jarFile.getInputStream(entry);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = input.read(buffer)) > 0) {
				baos.write(buffer, 0, len);
			}
			
			input.close();
			
			byte[] bytes = baos.toByteArray();
			Logger.getAnonymousLogger().info("Byte size: " + bytes.length);
			
			// String name = entry.getName().substring(entry.getName().indexOf("/"));
			FileOutputStream fos = new FileOutputStream("output.txt");
			fos.write(bytes);
			fos.flush();
			fos.close();
		}
		catch ( FileNotFoundException e ) {
			e.printStackTrace();
		} catch ( IOException e ) {
			e.printStackTrace();
		}
	} 
	
	final static public void createSlaveInstaller(File[] fList) {/*
		try {

			JarOutputStream jos = new JarOutputStream(new FileOutputStream("aslave.jar"));
			
			JarEntry jeReadme = new JarEntry("README.1st");
			jos.putNextEntry(jeReadme);
			jos.write("File generato in automatico da Ponf!".getBytes());
			
			for (int i = 0; i < fList.length; i++) {
				System.out.println("ARCHIVING:"+fList[i].getAbsolutePath());
				FileInputStream fis = new FileInputStream(fList[i].getAbsolutePath());
				// InputStream input = jarFile.getInputStream(entry);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = fis.read(buffer)) > 0) {
					baos.write(buffer, 0, len);
				}
				
				fis.close();
				byte[] bytes = baos.toByteArray();
				
				JarEntry je = null;
				if (fList[i].getName().endsWith(".properties"))
					je = new JarEntry(fList[i].getName());
					// je = new JarEntry("res/"+fList[i].getName());
				else
					je = new JarEntry(fList[i].getName());
				
				
				jos.putNextEntry(je);
				jos.write(bytes);
				
			}
			
			JarEntry jeManifest = new JarEntry("META-INF/MANIFEST.MF");
			jos.putNextEntry(jeManifest);
			jos.write(("Manifest-Version: 1.0"+System.getProperty("line.separator")).getBytes());
			jos.write(("Class-Path: . libs/lib.jar libs/ponf-slave.jar"+System.getProperty("line.separator")).getBytes());
			jos.write(("Main-Class: SlaveInstaller"+System.getProperty("line.separator")).getBytes());
			jos.write(("Sealed: false"+System.getProperty("line.separator")).getBytes());
			jos.write(("Created-By: Ponf!"+System.getProperty("line.separator")).getBytes());
			
			jos.flush();
			jos.close();
		} catch ( FileNotFoundException e ) {
			e.printStackTrace();
		} catch ( IOException e ) {
			e.printStackTrace();
		}
	*/}
	

}
