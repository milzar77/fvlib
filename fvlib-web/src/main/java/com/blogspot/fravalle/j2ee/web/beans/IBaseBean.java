/*
 * IBaseBean.java - jsptags (jsptags.jar)
 * Copyright (C) 2004
 * Source file created on 30-ott-2004
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [IBaseBean]
 * 2DO:
 * - rinominare i metodi con nomi più idonei
 * - sostituire i caratteri accentati nei commenti con i caratteri unicode o
 * le entità html
 *
 */

package com.blogspot.fravalle.j2ee.web.beans;
/**
 * 
 * IBaseBean è un'interfaccia che dichiara i metodi di base di tutte le classi
 * che ereditano l'abstract class BaseBean
 * 
 * @author antares
 * @version 0.1, 30/10/2004
 * @see com.blogspot.fravalle.j2ee.web.beans.BaseBean
 * 
 */

public interface IBaseBean {
	
	public final int BEAN_EMPTY = 0;
	public final int BEAN_LOADED = 1;
	public final int BEAN_UNLOADED = 2;
	
	public final String DIRECTION_FORWARD = ">";
	public final String DIRECTION_BACK = "<";
	
	/**
	 * Metodo di inserimento dati
	 * @param fieldValue New value to set for param field
	 * @param fieldName
	 */
	public void dataFiller();

	/**
	 * Metodo di recupero dati
	 * @param fieldValue New value to set for param field
	 * @param fieldName
	 */
	public void dataRetriever();
	
	/**
	 * Metodo di mascheramento dati
	 * @param fieldValue New value to set for param field
	 * @param fieldName
	 */
	public void dataMask();
	
}
