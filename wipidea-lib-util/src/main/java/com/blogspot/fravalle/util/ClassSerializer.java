/*
 * ClassSerializer.java - jweevlib (jweevlib.jar)
 * Copyright (C) 2003
 * Source file created on 15-nov-2003
 * Author: Francesco Valle <info@weev.it>
 * http://www.weev.it
 *
 */

package com.blogspot.fravalle.util;


/**
 * @author antares
 * 
 */
public final class ClassSerializer {

	static java.io.File f;
	static java.io.FileOutputStream outStream;
	static java.io.ObjectOutputStream outObjStream;
	
	static java.io.FileInputStream inStream;
	static java.io.ObjectInputStream inObjStream;
	
	public static void objWriter(Object obj, String fileName) {
		try {
		   //	".//class-file.ser"

		   f = new java.io.File( fileName );
		   outStream = new java.io.FileOutputStream( f );
		   outObjStream = new java.io.ObjectOutputStream(outStream);
		   outObjStream.writeObject( obj );
		   outObjStream.flush();
		   outObjStream.close();
		} catch(Exception e) {
		   e.printStackTrace();
		}
			
	}
	
	public static Object objReader(java.io.InputStream resFile) {
		Object obj = new Object();
		try {
			inObjStream = new java.io.ObjectInputStream( resFile );
			obj = inObjStream.readObject();
			inObjStream.close();
		} catch(Exception e) {
		   e.printStackTrace();
		}
			
		return obj;
	}
	
	public static Object objReader(java.net.URL resFile) {
		Object obj = new Object();
		try {
			f = new java.io.File( resFile.getFile() );
			inStream = new java.io.FileInputStream( f );
			inObjStream = new java.io.ObjectInputStream(inStream);
			obj = inObjStream.readObject();
			inObjStream.close();
		} catch(Exception e) {
		   e.printStackTrace();
		}
			
		return obj;
	}
}
