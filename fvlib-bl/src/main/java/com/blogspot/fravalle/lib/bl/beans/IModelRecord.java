/*
 * Record.java - jweevlib (jweevlib.jar)
 * Copyright (C) 2005
 * Source file created on 4-dic-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [Record]
 * TODO:
 *
 */

package com.blogspot.fravalle.lib.bl.beans;

import java.io.Serializable;
import java.util.Vector;

/**
 * Questa interfaccia astratta definisce i metodi di tutti gli oggetti istanziabili come bean type QueryModel.
 * 
 * <p><b><u>TODO</u>:</b> Implementare le eccezioni rilanciate da ogni metodo
 * @author Francesco Valle - (antares)
 */
abstract public interface IModelRecord extends Serializable {

	public void beanFiller(java.util.Hashtable paramMap);
	public String beanPrepareSQL(java.util.Vector vtBeanMap);
	public Vector updateSqlBean();
	
	public boolean hasChanges();
	
	/**
	 * Metodo di lettura tabella predefinita in un database per l'oggetto
	 * @return
	 * Il nome della tabella predefinita dell'oggetto
	 */
	public String getSqltable();

	/**
	 * Metodo di impostazione tabella predefinita in un database per l'oggetto
	 * @param par
	 * Il nome della tabella
	 */
	public void setSqltable(final String par);

}
