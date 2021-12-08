/*
 * ActionPages.java - antares-web (antares-web.jar)
 * Copyright (C) 2005
 * Source file created on 18-apr-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [ActionPages]
 * 2DO:
 *
 */

package com.blogspot.fravalle.j2ee.web;

import javax.servlet.http.HttpServletRequest;

import com.blogspot.fravalle.util.UtilConstants;

/**
 * @author antares
 */
public abstract class ActionPages implements Constants, UtilConstants {

	public static boolean isValidAction(String page) {
		boolean isValid = false;
		
		// isValid  = (page != null && ("/"+INIT).equals(page)); // token esatto
		isValid  = (page != null && page.indexOf("/"+INIT)!=-1); // token esatto
		if (isValid) // && isTargetAccepted(page, INIT))
			return isValid;
		
		// isValid  = (page != null && ("/"+INFO).equals(page)); // token esatto
		isValid  = (page != null && page.indexOf("/"+INFO)!=-1);
		if (isValid) // && isTargetAccepted(page, INFO))
			return isValid;
		
		isValid  = (page != null && page.indexOf("/"+ROOT)!=-1);
		if (isValid && isExtensionAccepted(page)) //  && isTargetAccepted(page, ROOT)
			return isValid;
		
		isValid  = (page != null && ("/"+TEST).equals(page)); // token esatto
		if (isValid) // && isTargetAccepted(page, TEST))
			return isValid;

		isValid  = (page != null && ("/"+TEST_ERROR).equals(page)); // token esatto
		if (isValid) // && isTargetAccepted(page, TEST_ERROR) )
			return isValid;
		
		return isValid;
	}
	
	public static boolean isValidExtension(String uri) {
		boolean isValid = false;
		isValid  = (uri != null && uri.indexOf(EXT_LEGGI)!=-1);
		if (isValid)
			return isValid;
		isValid  = (uri != null && uri.indexOf(EXT_SCRIVI)!=-1);
		if (isValid)
			return isValid;
		
		return isValid;
	}
	
    static public final String getServerMapping(HttpServletRequest req) {
        return req.getScheme()+"://"+req.getServerName()+":8080"+req.getContextPath();
    }

    protected static boolean isTargetAccepted(String page, String target) throws NullPointerException {
    	boolean accept=false;
    		if ( page.lastIndexOf(target)== 
    				(page.length() - target.length())
    				) {accept=true;}
    	return accept;
    }
    
    protected static boolean isExtensionAccepted(String page) throws NullPointerException {
    	boolean ignore=true;
    	for (int i = 0; i < EXT_IGNORE_LIST.length; i++) {
    		// if ( page.lastIndexOf(EXT_IGNORE_LIST[i])!=-1 &&
    		if ( page.lastIndexOf(EXT_IGNORE_LIST[i])== 
    				(page.length() - EXT_IGNORE_LIST[i].length())
    				) {ignore=false; break;}
    	}
    	return ignore;
    }

}
