/*
 * IModel.java - FrameWork (FrameWork.jar)
 * Copyright (C) 2005
 * Source file created on 3-dic-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [IModel]
 * TODO:
 *
 */

package com.blogspot.fravalle.lib.bl.beans;

import java.io.Serializable;

import com.blogspot.fravalle.lib.bl.ModelKey;

/**
 * Questa interfaccia definisce i metodi di tutti gli oggetti istanziabili come bean type Model.
 * 
 * <p><b><u>TODO</u>:</b> Implementare le eccezioni rilanciate da ogni metodo
 * @author Francesco Valle - (antares)
 */
public interface IModel extends Serializable {
	
	/**
	 * Recupera la chiave di rappresentazione logica dei dati paginati nel bean 
	 * @return
	 */
	public long getModelKeyId();
	
	/**
	 * Imposta la chiave di rappresentazione logica dei dati paginati
	 * @param id
	 */
	public void setModelKeyId(long id);
	
	/**
	 * Recupera le chiavi dei dati paginati nel bean 
	 * @return
	 */
	public ModelKey getModelKey();
	
	public TreeNodeBean getNode();
	
	/**
	 * Metodo di lettura raggruppamento elementi nel jtree di navigazione dati
	 * @return
	 * Il nome dell'elemento di raggruppamento
	 */
	public String getOrderByGroup();
	
	/**
	 * Metodo di impostazione raggruppamento elementi nel jtree di navigazione dati
	 * @param order
	 * Il nome dell'elemento di raggruppamento
	 */
	public void setOrderByGroup(final String order);
	
	public Object getFieldValue(String fieldName);
	
	public Class getFieldType(String fieldName);
	
	public Object clearType(String fieldName, Object fieldValue);
	
	public void firePropertyChange(String str, String oldStr, String newStr);
	
	public void firePropertyChange(String str, java.util.Date oldStr, java.util.Date newStr);
	
}
