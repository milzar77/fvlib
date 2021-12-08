/*
 * CachedFile.java - jpovnet (jpovnet.jar)
 * Copyright (C) 2006
 * Source file created on 11-feb-2006
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [CachedFile]
 * 2DO:
 *
 */

package com.blogspot.fravalle.util.filesystem;

/**
 * @author francesco
 */
public class CachedFile {
	Object cache;
	long lastModified;
	
	/**
	 * 
	 */
	public CachedFile() {
		super();
		cache = null;
		lastModified = 0;
	}
	
	/**
	 * 
	 */
	public CachedFile(Object o, long last) {
		super();
		cache = o;
		lastModified = last;
	}
	
	public final Object getCache() {
		return cache;
	}
	public final long getLastModified() {
		return lastModified;
	}
	
	final public boolean isReady() {
		return cache != null;
	} 
	
}
