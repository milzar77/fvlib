/*
 * Copyright (C) 2006
 * 
 */
/*
 * Project: jappframework (jappframework.jar) - Id: ILoggableObject.java
 * Source file created on 18-feb-2006
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [ILoggableObject]
 * 2DO:
 *
 */

package com.blogspot.fravalle.lib.monitor;

import java.util.logging.Logger;

/**
 * @author francesco
 */
abstract public interface ILoggableObject {
	public Logger getMainLogger();

	public Logger getLocalLogger();
}

