/*
 * ErrorCodes.java - FrameWork (FrameWork.jar)
 * Copyright (C) 2005
 * Source file created on 3-dic-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [ErrorCodes]
 * TODO:
 *
 */

package com.blogspot.fravalle.lib.monitor;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author antares
 */
public abstract class ErrorCodes implements IErrorCodes {

	private static ResourceBundle rb = ResourceBundle.getBundle("res.errors", Locale.getDefault());
	public static String errorLabel(int id) {
		return error(Long.parseLong( String.valueOf(id) ));
	}
	public static String error(long id) {
		String s = null;
		try {
			s = rb.getString(prefix + String.valueOf(id));
		} catch (MissingResourceException mre) {
			Monitor.debug( CODE_ERROR_NOTFOUND, mre );
			s = "Key [" + id + "] not found. Add it to your /res/errors.properties";
		}
		return s;
	}
}
