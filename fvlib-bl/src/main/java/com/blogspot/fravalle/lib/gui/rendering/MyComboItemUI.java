/*
 * MyComboItemUI.java - jappframework (jappframework.jar)
 * Copyright (C) 2006
 * Source file created on 9-gen-2006
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [MyComboItemUI]
 * 2DO:
 *
 */

package com.blogspot.fravalle.lib.gui.rendering;

import java.util.Vector;

/**
 * [][MyComboItemUI]
 * 
 * <p><b><u>2DO</u>:</b>
 * 
 * 
 * @author Francesco Valle - (antares)
 */
public class MyComboItemUI extends Object {
    private Vector v;
    
    public MyComboItemUI() {
		Vector tmpEmpty = new Vector();
		tmpEmpty.add("");
		tmpEmpty.add("");
    	this.v = tmpEmpty;
    }
    
	public MyComboItemUI(Vector v, int idx) {
	    this.v = v;
	}
	public final String getReal() {
		return ""+v.elementAt(1);
	}
	public final void changeReal(String s) {
		v.add(1, s);
	}
	
	public final String getItem() {
		return ""+v.elementAt(0);
	}
	public final String toString() {
	    return ""+v.elementAt(0);
	}
}
