/*
 * BaseBean.java - jsptags (jsptags.jar)
 * Copyright (C) 2004
 * Source file created on 15-ott-2004
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [BaseBean]
 * 2DO:
 *
 */

package com.blogspot.fravalle.j2ee.web.beans;
/**
 * 
 * BaseBean � la classe che istanzia i servizi di base dei bean collegati
 * alla struttura dei dati utilizzate a livello applicativo 
 * 
 * @author antares
 * @version 0.1, 30/10/2004
 * @see com.blogspot.fravalle.web.mp3.beans.IBaseBean
 * 
 */

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;

import com.blogspot.fravalle.util.html.HtmlControls;
import com.blogspot.fravalle.util.html.HtmlFormAttributes;
import com.blogspot.fravalle.util.html.LabelIns;

/**
 * @author antares
 */
public abstract class BaseBean extends HtmlControls implements Serializable, IBaseBean {

	protected String sqlDatabase;
	protected String sqlTable;
	protected String[] sqlTableKeys;
	protected HtmlFormAttributes ha;
	
	public void dataFiller() {
		// db select based on schema
	}
	
	public void dataRetriever() {
		// db select based on schema
	}

	public void dataMask() {
		// data masking
	}

	/**
	 * Generazione della chiave
	 */
	public void keyBuilder() {
		// key builder
	}

	/**
	 * @return un numero intero che rappresenta l'elemento
	 */
	protected int dataSeed() {
		return 1;
	}


	private String beanSpecsCutPrefix(String prefix, String name) {
		if (name != null) {
			String s = name.substring(prefix.length(),name.length());
			return s.substring(0,1).toLowerCase() + s.substring(1,s.length());
		}
		return "FuckU";
	}
	
	private String beanSpecsCapitalized(String name) {
		if (name != null)
			return name.substring(0,1).toUpperCase() + name.substring(1,name.length());;
		return "FuckU";
	}
	
	private String beanSpecsDecapitalized(String name) {
		if (name != null)
			return name.substring(0,1).toLowerCase() + name.substring(1,name.length());;
		return "FuckU";
	}

	
	final int mxRows = 0, mxCols = 2, startCounter = 0, offsetX = 40, offsetY = 40;
	int row = 1, col = 0, counter = startCounter;
	
	
	private String gridAlign() {
		String s = "position: absolute; ";
		if (counter <= mxCols) {
			s += "left: " + (offsetX+300*counter) + "px; top: " + (offsetY+120*row) + "px;";
		} else {
			row++;
			col = 0;
			counter = startCounter;
			s += "left: " + (offsetX+300*counter) + "px; top: " + (offsetY+120*row) + "px;";
		}
		counter++;
		return s;
	}
	
	private String[] composeName(String propName, String propType){
		return  super.parseAtt( "name" , beanSpecsDecapitalized(propName) + "("+ propType +")" );
	}
	
	public String buildDate(int dateSize) {
		String s = "";
		for (int i = 1; i <= dateSize; i++) {
			s += "<option>"+i+"</option>";
		}
		return s; // this.ha.setContent("<select><option>"+((Date)o).getDate()+"</option></select><select><option>"+((Date)o).getMonth()+"</option></select><select><option>"+((Date)o).getYear()+"</option></select>");
	}
	
	public String buildControls() {
		int elementType = 0;
		if (ha == null) ha = new HtmlFormAttributes();
		StringBuffer sb = new StringBuffer();
		try {

			final String prefixBean_get = "get", prefixBean_set = "set", prefixBean_is = "is";
			
			Method[] m = (Method[])getClass().getMethods();
			for (int i = 0; i < m.length; i++ ) {
				elementType = 0;
				if ( (m[i].getReturnType() != int.class
						&& m[i].getReturnType() != Class.class)
						&& ( m[i].getName().startsWith("get") || m[i].getName().startsWith("is") ) ) {
					
					// clear last content
					ha = new HtmlFormAttributes();
					
					Object o = m[i].invoke( this, new Object[] {});
					System.out.println( m[i].getName() + " : " + o );
					
					final String propName = beanSpecsCutPrefix(prefixBean_get, m[i].getName());
					final String propType = m[i].getReturnType().getName();
					
					this.ha.setLabeled(true);
					this.ha.setLabel( beanSpecsDecapitalized(propName) );
					
					this.ha.setCoordinate( gridAlign() );

					if (o != null)
						this.ha.setAtt(super.parseAtt( beanSpecsDecapitalized(propName) , (String)o));
					
					this.ha.setAtt( composeName(propName, propType) );
					
					if (o != null)
						this.ha.setAtt(super.parseAtt( "value" , (String)o));
					
					if (m[i].getReturnType() == Date.class) {
						elementType = TYPE_SELECT_DATE;
						if (o != null) {
							Date d = (Date)o;
							if (d == null)
								d = new Date();

							this.ha.setContent(
									  "<select><option>"+(d.getDate())+"</option><option value=''/>"
									+ buildDate( 31 )
									+ "</select>"
									+ "<select><option>"+(d.getMonth())+"</option><option value=''/>"
									+ buildDate( 12 )
									+ "</select>"
									+ "<select><option>"+(d.getYear()+1900)+"</option><option value=''/>"
									+ "</select>"
									);
						} else {
							this.ha.setContent(
									  "<select>"
									+ buildDate( 31 )
									+ "</select>"
									+ "<select>"
									+ buildDate( 12 )
									+ "</select>"
									+ "<select><option>2005</option>"
									+ "</select>"
									);
						}
					} else {
						elementType = TYPE_INPUT;
					}
					
					sb.append( buildControl(elementType) );
					
				}
				
			}

		} catch (Exception ex) {
			logger.severe( ex.getMessage() );
		}
		
		HtmlFormAttributes form = new HtmlFormAttributes(false);
		form.setAction("#");
		// form.setTarget(super.parseAtt("_self","DEFAULT"));
		form.setTarget(super.parseAtt("popup","POPUP"));
		form.setContent( sb.toString() );
		String tagContent = super.printForm(form);
		
		return tagContent;
	}
	
	public String buildControl(LabelIns propName) {return buildControl(TAG_NOT_IMPLEMENTED);}
	public String buildControl(String propName, String propValue) {return buildControl(TAG_NOT_IMPLEMENTED);}
	
	public String buildControl(String propName) {
		int elementType = 0;
		propName = beanSpecsCapitalized(propName);
		if (ha == null) ha = new HtmlFormAttributes();

		try {
			
			Method m = (Method)getClass().getMethod("get" + propName, new Class[] {});
			Object o = m.invoke( this, new Object[] {});
			System.out.println( "o: " + o );
			this.ha.setLabeled(true);
			this.ha.setLabel( beanSpecsDecapitalized(propName) );
			
			this.ha.setAtt(super.parseAtt( beanSpecsDecapitalized(propName) , (String)o));
			this.ha.setAtt(super.parseAtt( "name" , beanSpecsDecapitalized(propName)));
			this.ha.setAtt(super.parseAtt( "value" , (String)o));
			// this.ha.setAtt(super.parseAtt( "label" , beanConformDecapitalized(propName)));

		} catch (Exception ex) {
			logger.severe(propName +": "+ ex.getMessage() );
		}

		elementType = TYPE_INPUT;

		return buildControl(elementType);
	}

	public String buildControl(Object o) {
		int elementType = 0;
		if (ha == null) ha = new HtmlFormAttributes();
		
		if (o instanceof String) {
			elementType = TYPE_INPUT;
		} else if (o instanceof Date) {
			elementType = TYPE_SELECT_DATE;
		}
		return buildControl(elementType);
	}
	
	protected String buildControl(int elementType) {
		String element = null;
		
		if (ha == null) ha = new HtmlFormAttributes();

		switch ( elementType ) {
			case TYPE_FORM:
				this.ha.setInline(false);
				this.ha.setHead(super.FORM);
				this.ha.setAtt(super.parseAtt("name","myform"));
				this.ha.setContent("<p>Il contenuto della form.</p>");
				break;
			case TYPE_INPUT:
				this.ha.setInline(true);
				this.ha.setHead(super.INPUT);
				this.ha.setAtt(super.parseAtt("type","text"));
				// this.ha.setAtt(super.parseAtt("name","myinput"));
				break;
			case TYPE_SELECT_DATE:
				this.ha.setInline(false);
				this.ha.setHead(super.SELECT_DATE);
				if (this.ha.getContent() == null) this.ha.setContent("<select><option>gg</option></select><select><option>mm</option></select><select><option>aaaa</option></select>");
				// this.ha.setAtt(super.parseAtt("type","text"));
				// this.ha.setAtt(super.parseAtt("name","myinput"));
				break;
			case TAG_NOT_IMPLEMENTED:
			default:
				this.ha.setInline(false);
				this.ha.setHead("p");
				this.ha.setAtt(super.parseAtt("align","center"));
				this.ha.setContent("Tag non definito o non implementato.");
				break;
		}
		
		element = super.printElement(ha);
		return element;
	}

}
