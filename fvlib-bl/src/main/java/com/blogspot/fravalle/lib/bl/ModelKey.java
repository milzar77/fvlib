/*
 * ModelKey.java - FrameWork (FrameWork.jar)
 * Copyright (C) 2005
 * Source file created on 3-dic-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [ModelKey]
 * TODO:
 *
 */

package com.blogspot.fravalle.lib.bl;

import java.util.Vector;

/**
 * @author antares
 */
public class ModelKey {
	private String[] cols;
	private Vector vtKeys;
	private Vector vtValues;
	/**
	 * 
	 */
	public ModelKey() {
		super();
		vtValues = new Vector();
		vtKeys = new Vector();
	}
	
	public String[] getKeys() {
		return cols;
	}

	
	public void addKey(String key, String value) {
		vtKeys.add(key);
		vtValues.add(value);
	}
	
	public String getQueryKeys(Vector otherElements) {
		String _whereClause = " WHERE ";
		for (int i = 0; i < vtKeys.size(); i++) {
			_whereClause += String.valueOf(vtKeys.elementAt(i)) + "=? AND ";
			otherElements.add(vtValues.elementAt(i));
		}
		return _whereClause + " 1=1";
	}
	
}
