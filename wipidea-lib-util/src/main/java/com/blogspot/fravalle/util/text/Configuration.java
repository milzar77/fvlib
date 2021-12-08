/*
 * Configuration.java - jpovnet (jpovnet.jar)
 * Copyright (C) 2006
 * Source file created on 24-gen-2006
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [Configuration]
 * 2DO:
 *
 */

package com.blogspot.fravalle.util.text;

import java.io.File;
import java.util.Properties;

/**
 * @author antares
 */
public class Configuration extends Properties {
	
	private File filesystemReference;
	
	public final String getFilesystemPath() {
		return getFilesystemReference().getAbsolutePath();
	}
	
	public final File getFilesystemReference() {
		return filesystemReference;
	}
	public final void setFilesystemReference(File parFilesystemReference) {
		filesystemReference = parFilesystemReference;
	}
}
