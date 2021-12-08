/*
 * TestModel.java - jappframework (jappframework.jar)
 * Copyright (C) 2005
 * Source file created on 12-dic-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [TestModel]
 * 2DO:
 *
 */

package com.blogspot.fravalle.lib.bl.beans;

import com.blogspot.fravalle.lib.bl.ModelKey;
import com.blogspot.fravalle.lib.bl.beans.AModelBase;
import com.blogspot.fravalle.lib.bl.beans.TreeNodeBean;
import com.blogspot.fravalle.lib.monitor.MainLogger;

/**
 * @author antares
 */
public class TestModel extends AModelBase {

	private final static String deco = "\t\t: \n\t\t=\t";
	
	public String stringa;
	public int intero;
	
	TestModel(){
		super();
	}
	
	public final static void main(String[] args) {
		TestModel test = new TestModel();
		Object newValue = "new";

		String oldValue = (String)test.getFieldValue("stringa");

		output(oldValue);
		
		test.updateField(newValue, "stringa");
		
		oldValue = (String)test.getFieldValue("stringa");
		
		output(oldValue);
		
		long l = Integer.MAX_VALUE;
		double d = 2.0;
		float f = 1;
		short s = 1;
		int i = 1;
		
		test.updateField(String.valueOf(d), "intero");
		
		
		double intValue = ((Integer)test.getFieldValue("intero")).intValue();
		test.out( "Test:" + intValue);
				
		
		
		test.out( "Test:" + test.getPrimitive("intero").adouble() );
		
	}

	final public static void output(String s){
		MainLogger.getLog().info( s);
	}
	final private void out(String s){
		MainLogger.getLog().info( "Casting" + deco + s);
	}
	
	/* (non-Javadoc)
	 * @see com.blogspot.fravalle.lib.bl.beans.IModel#getModelKeyId()
	 */
	public long getModelKeyId() {
		// 2DO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.blogspot.fravalle.lib.bl.beans.IModel#setModelKeyId(long)
	 */
	public void setModelKeyId(long parId) {
		// 2DO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.blogspot.fravalle.lib.bl.beans.IModel#getModelKey()
	 */
	public ModelKey getModelKey() {
		// 2DO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.blogspot.fravalle.lib.bl.beans.IModel#setModelKey(com.blogspot.fravalle.lib.bl.ModelKey)
	 */
	public void setModelKey(ModelKey parKey) {
		// 2DO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.blogspot.fravalle.lib.bl.beans.IModel#getNode()
	 */
	public TreeNodeBean getNode() {
		// 2DO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.blogspot.fravalle.lib.bl.beans.AModel#updateField(java.lang.String, java.lang.String)
	 */
	protected void updateField(String parFieldValue, String parFieldName) {
		MainLogger.getLog().warning("Override NON corretto!!!");
		super.updateField((Object)parFieldValue, parFieldName);
	}

}
