/*
 * HtmlControls.java - antares-web (antares-web.jar)
 * Copyright (C) 2005
 * Source file created on 6-giu-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [HtmlControls]
 * 2DO:
 *
 */

package com.blogspot.fravalle.util.html;

import java.util.Enumeration;
import java.util.logging.Logger;


/**
 * @author antares
 */
public abstract class HtmlControls implements HtmlFormTypes {
	
	protected static Logger logger = Logger.getLogger("HtmlControls");
	
	private static boolean isUppercase = false;
	
	private static final String BLANK = " ";
	private static final String TAG_OPEN = "<";
	private static final String TAG_CLOSE_BODY = "</";
	private static final String TAG_CLOSE = ">";
	private static final String TAG_CLOSE_INLINE = "/>";

	protected final static String[] parseAtt(String name, String value) {
		return new String[]{name, value};
	}
	
	private static String insertTagContent(String content) {
		
		return null;
	}
	
	private final static String tagElement(String tagName, HtmlFormAttributes ha) {
		StringBuffer sb = new StringBuffer();
		if(isUppercase)
			tagName = tagName.toUpperCase();
			
		sb.append(TAG_OPEN);
		sb.append(tagName);
		
		if (ha != null) {
			for (Enumeration e = ha.keys(); e.hasMoreElements();) {
				String attName = (String)e.nextElement();
				sb.append(BLANK);
				sb.append(attName);
				sb.append("=");
				sb.append("\"");
				sb.append( ha.get(attName) );
				sb.append("\"");
			}
		}
		if (ha.isInline()) {
			sb.append(BLANK);
			sb.append(TAG_CLOSE_INLINE);
		} else {
			sb.append(TAG_CLOSE);
			sb.append( ha.getContent() );
			sb.append(TAG_CLOSE_BODY);
			sb.append(tagName);
			sb.append(TAG_CLOSE);
		}
		if (ha.isLabeled()){
			if (ha.getCoordinate() == null)
				return "<label id=\""+ha.getLabel()+"\">" + ha.getLabel() + ": " + sb.toString() + "</label>";
			return "<label style=\""+ha.getCoordinate()+"\">" + ha.getLabel() + ": " + sb.toString() + "</label>";
		}
			
		return sb.toString();
	}

	public final static String printElement(HtmlFormAttributes ha) {
		String tag = tagElement(ha.getHead(), ha); 
		System.out.println( "[TAG]: " + tag );
		return tag;
	}
	
	public final static String printForm(HtmlFormAttributes ha) {
		String jsPopup = "";
		if (ha.getAction() == null)
			ha.setAtt( HtmlControls.parseAtt( "action" , (String)"#") );
		else
			ha.setAtt( HtmlControls.parseAtt( "action" , (String)ha.getAction()) );
		
		if (ha.getTarget() != null){
			if ( "".equals(ha.getTarget()[ATT_VALUE]) || "DEFAULT".equals(ha.getTarget()[ATT_VALUE]) ) {
				ha.setAtt( HtmlControls.parseAtt( "target" , (String)ha.getTarget()[ATT_NAME]) );
			} else if ( "POPUP".equals(ha.getTarget()[ATT_VALUE]) ) {
				ha.setAtt( HtmlControls.parseAtt( "target" , (String)ha.getTarget()[ATT_NAME]) );
				
				String jsCommand = "return popper()";
				ha.setAtt( HtmlControls.parseAtt( "onsubmit" , jsCommand) );
				
				// destrutturare per inserimento in db weblan
				// collegare routine di recupero dati da db
				jsPopup = "<script language='javascript'>"
						+ "var "+ha.getTarget()[ATT_NAME]+"=null;"
						+ "function popper(){ "+ha.getTarget()[ATT_NAME]+" = open('about:blank','"+ha.getTarget()[ATT_NAME]+"','left=0,top=0,width=350,height=350,toolbar=1,resizable=0',true);"+ha.getTarget()[ATT_NAME]+".focus();return true;}"
						+ "function popper(par){ "+ha.getTarget()[ATT_NAME]+" = open('about:blank','"+ha.getTarget()[ATT_NAME]+"','left=0,top=0,width=350,height=350,toolbar=1,resizable=0',true);"+ha.getTarget()[ATT_NAME]+".focus();document.forms[0].submit();setTimeout('closer()',1500);}"
						+ "function closer(){ alert('Operazione completata, chiusura automatica.');"+ha.getTarget()[ATT_NAME]+".close(); self.location.reload()}"
						+ "</script><input type=button name=popper value=send onclick=popper(true) />";
			}
		}
		
		String tag = tagElement(FORM, ha); 
		System.out.println( "[TAG]: " + tag );
		return tag + jsPopup;
	}

	public final static String printInputText(HtmlFormAttributes ha) {
		String tag = tagElement(INPUT, ha); 
		System.out.println( "[TAG]: " + tag );
		return tag;
	}

	public static String printTextarea(HtmlFormAttributes ha) {
		return null;
	}

	public static String printSelect(HtmlFormAttributes ha) {
		return null;
	}

	public static String printRadio(HtmlFormAttributes ha) {
		return null;
	}

	public static String printRadios(HtmlFormAttributes ha) {
		return null;
	}

	public static String printCheckbox(HtmlFormAttributes ha) {
		return null;
	}

	public static String printCheckboxes(HtmlFormAttributes ha) {
		return null;
	}
}

class Test implements HtmlFormTypes {
	public final static void main(String[] args) throws Exception {
		boolean isInline = true;
		String tagContent = null;
		HtmlFormAttributes form = new HtmlFormAttributes(isInline);
		form.setAtt(HtmlControls.parseAtt("name","pluto"));
		form.setAtt(HtmlControls.parseAtt("action","http://"));
		form.setContent("Contenuto del tag");
		tagContent = HtmlControls.printForm(form);
		
		HtmlFormAttributes input = new HtmlFormAttributes(isInline);
		input.setContent("Contenuto del tag");
		input.setAtt(HtmlControls.parseAtt("type","text"));
		tagContent = HtmlControls.printInputText(input);

		HtmlFormAttributes element = new HtmlFormAttributes(false);
		element.setHead(RADIO);
		element.setAtt(HtmlControls.parseAtt("type","radio"));
		element.setContent("Contenuto del tag");
		tagContent = HtmlControls.printElement(element);
		
		System.exit(0);
	}
}

