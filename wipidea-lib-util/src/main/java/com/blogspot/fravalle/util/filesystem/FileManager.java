/*
 * FileManager.java - Weev Utility Library package (weev-lib.jar)
 * Copyright (C) 2 novembre 2002 Francesco Valle <info@weev.it>
 * http://www.weev.it
 *
 */

package com.blogspot.fravalle.util.filesystem;

public abstract class FileManager {
	
	public static java.util.Vector getClassesList(String dir, String[] prefixFilter) {
		java.util.Vector vt = new java.util.Vector();
		java.io.File pkg = new java.io.File(dir);
		
		if (pkg.exists()) {
		
			java.io.File[] files = pkg.listFiles();

			for(int i=0;i<files.length;i++) {
				String fpath = files[i].getName(); 
				if ( fpath.indexOf(".class") != -1 ) {
					if ( fpath.indexOf("$") == -1 ) {
						if (prefixFilter.length != 0) {
							if (fpath.startsWith( prefixFilter[0] ) || fpath.startsWith( prefixFilter[1] ))
								vt.addElement( fpath.substring(0, fpath.indexOf(".")) );
						} else {
							vt.addElement( fpath.substring(0, fpath.indexOf(".")) );
						}
					}
				}
			}
		} else {
			vt = new java.util.Vector(0);
		}
		return vt;
	}
	
	public static java.util.Vector getFileList(String dir, String filter) {
		java.util.Vector vt = new java.util.Vector();
		java.io.File dirFiles = new java.io.File(dir);
		java.io.File[] files = dirFiles.listFiles();

		for(int i=0;i<files.length;i++) {
			String fpath = files[i].getName(); 
			if ( fpath.indexOf( filter ) != -1 ) {
				vt.addElement( fpath.substring(0, fpath.indexOf(".")) );
			}
		}
		return vt;
	}
	
}
