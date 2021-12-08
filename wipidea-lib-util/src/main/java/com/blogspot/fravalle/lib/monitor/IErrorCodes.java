/*
 * IErrorCodes.java - FrameWork (FrameWork.jar)
 * Copyright (C) 2005
 * Source file created on 3-dic-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [IErrorCodes]
 * TODO:
 *
 */

package com.blogspot.fravalle.lib.monitor;

/**
 * @author antares
 */
public interface IErrorCodes {
	final String prefix = "error.";
	
	final int CODE_ERROR_NOTFOUND = 0;
	final int CODE_NULLPOINTER = 1;
	
	final int CODE_PARAMETER_NOTFOUND = 10;
	
	final int PROPS_KEY_REFVAR_NOTFOUND = 20;
	final int PROPS_KEY_NOTFOUND = 30;
	final String KEY_NOTFOUND = "null";
	
	final int DATA_DB_NOTFOUND_FILECONF = 1000;
	final int DATA_DB_NOTFOUND_DRIVER = 1001;
	final int DATA_DB_NOTFOUND_URL = 1002;
	final int DATA_DB_NOTFOUND_LOGIN = 1003;
	
	final int DATA_DB_DRIVER_FAILURE = 1101;
	
	final int DATA_DB_CONNECTION_ERROR = 1201;
	
	final int FRAMEWORK_CAST_ERROR = 1301;
	
	final int CODE_WARNING_PATCH = 10001;
	final int CODE_MESSAGE_GENERIC = 10002;
	
}
