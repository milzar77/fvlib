/*
 * IModelList.java - FrameWork (FrameWork.jar)
 * Copyright (C) 2005
 * Source file created on 4-dic-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [IModelList]
 * TODO:
 *
 */

package com.blogspot.fravalle.lib.bl.beans;

import java.io.Serializable;
import java.util.Enumeration;

/**
 * @author antares
 */
public interface IModelList extends Serializable {
	public java.util.Vector getAllSearchedResults(String search);
	public int searchHeader(String search, int index, boolean isCaseSensitive);
	public boolean containsHeader(Object elem);
	public int indexOfHeader(Object elem, int index);
	public void addElement(Object record);
	public Object elementAt(int idx);
	public Object firstElement();
	public Object lastElement();
	public void removeElementAt(int idx);
	public void setElementAt( Object elem, int idx );
	public Enumeration elements();
	public int size();
	public int search(Object reference);
	public int search(String reference);
}
