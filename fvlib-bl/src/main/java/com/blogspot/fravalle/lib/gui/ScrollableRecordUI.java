/*
 * ScrollableRecordUI.java - jappslist (jappslist.jar)
 * Copyright (C) 2003
 * Source file created on 17-nov-2003
 * Author: Francesco Valle <info@weev.it>
 * http://www.weev.it
 *
 */

package com.blogspot.fravalle.lib.gui;

import java.beans.PropertyChangeSupport;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.blogspot.fravalle.lib.bl.beans.IModel;
import com.blogspot.fravalle.lib.bl.beans.IModelList;
import com.blogspot.fravalle.lib.bl.beans.ModelList;
import com.blogspot.fravalle.lib.bl.beans.TreeNodeBean;
import com.blogspot.fravalle.lib.bl.business.BeanManager;
import com.blogspot.fravalle.lib.bl.business.exceptions.FrameworkDatabaseException;
import com.blogspot.fravalle.lib.bl.business.exceptions.FrameworkFatalException;
import com.blogspot.fravalle.lib.data.sql.DataConnection;
import com.blogspot.fravalle.lib.data.sql.SearchConditions;
import com.blogspot.fravalle.lib.gui.mdi.AMDIApplet;
import com.blogspot.fravalle.lib.monitor.Monitor;
import com.blogspot.fravalle.util.Constants;
import com.blogspot.fravalle.util.UIRes;


/**
 * Questa classe definisce i metodi di base di tutti gli oggetti intefraccia collegati ad un bean type Model.
 * 
 * <p><b><u>TODO</u>:</b>
 * @author Francesco Valle - (antares)
 */
public class ScrollableRecordUI extends ABaseControllerUI {	
	
	// CFG: riferimento testuale al nome completo della classe che rappresenta i dati
	private String recordClassName;
	
	// CFG: sorgente dei dati
	private ScrollerSource scrollerSourceType;


	

	
	// CMP: 
	private ScrollerUI scrollerPanel;
		/* classe componente di scorrimento
		 */
	private JScrollPane navigationPanel;
	
	
	// protected Vector vtBeanRecords;
	protected IModelList beanList;
		/* mappa dei parametri del bean
		 * e elenco dei bean caricati
		 */
	
	public static java.util.Hashtable comboDefaultValues;
	
	/**
	 * Record counter predefinito
	 */
	private int recordCounter = -1;
	
	private String infoStatus; 
    
	protected DataConnection dbProp;
	
	
	private void buildNavigationPanel() {
		JTreeUI navigationTreePanel = new JTreeUI( record.getOrderByGroup() );
		navigationPanel.setViewportView(navigationTreePanel);
		navigationPanel.setPreferredSize(new java.awt.Dimension(170,0));
	}
	
	private void deselectNavigationPanel(){
		((JTreeUI)navigationPanel.getViewport().getView()).clearSelection();
	}
	
	/**
	 * Costruttore di GUI dotate di supporto per lo scorrimento dei record online (tramite  db connection) e offline (tramite xml caching)
	 * 
	 * @param recordClassName Riferimento al package del bean model
	 * @param isLabelMasked Attivazione etichette incorporate
	 * @param sourceType Proprietà della sorgente dati
	 */
	public ScrollableRecordUI(ScrollerInfo infoScroller) {
		this.isLabelMasked = infoScroller.isLabelMasked();
	    this.recordClassName = infoScroller.getBeanClassReference();
	    this.info = infoScroller;
	    
	    /* Inizializzo una nuova istanza del bean di riferimento */
		try {
		    this.record = (IModel)Class.forName( recordClassName ).newInstance();
		} catch (Exception ex) {
			Monitor.debug( "Impossibile recuperare il bean <" + recordClassName + ">");
			Monitor.debug( ex );
		}
		
		/* Inizializzo una mappa delle proprietà del bean */
		vtBeanMap = new Vector();
		
		/* Inizializzo il pannello di navigazione */
		scrollerSourceType = new ScrollerSource( info );
		scrollerPanel = new ScrollerUI();
		
		/* Imposto il messaggio di logging utente */
	    infoStatus = "<XML-GET>[" + this.recordClassName + "]";
	    Monitor.log( Constants._logHistoryDef + infoStatus );
	    updateInfoPanel( infoStatus );
	    
	    /* Ricevo una lista dei bean di riferimento caricati da una serializzazione xml dei bean */
	    beanList = (IModelList)BeanManager.loadXMLBean( info.getXmlReference() );
	    
		if (info.isXmlReference()) {
			/* CASO Bean XML */
		} else {
			/* CASO Bean SQL */
			scrollerPanel.onlineMode();
		}
		
		/* Inizializzazione della classe di tracciamento modifiche parametri */
		pcsMask = new PropertyChangeSupport(this);
		
		setLayout(new java.awt.BorderLayout());
		add(scrollerPanel, java.awt.BorderLayout.SOUTH);
		
		if (record != null) {
			JTreeUI navigationTreePanel = new JTreeUI( record.getOrderByGroup() );
			navigationPanel = new JScrollPane();
			navigationPanel.setViewportView(navigationTreePanel);
			navigationPanel.setPreferredSize(new java.awt.Dimension(170,0));
			if (this.isNavigationTree())
				add( navigationPanel , java.awt.BorderLayout.WEST);
		}
	}

	
	/**
	 * Metodo di aggiornamento pannello informazioni GUI ScrollerUI 
	 */
	private void updateInfoPanel(String textMex) {
		scrollerPanel.updateInfoPanel(textMex);
		AWindow.showStatus(textMex,this);
		// TODO : aggiungere supporto java.text... per frasi composte
	}

	/**
	 * Metodo privato per aggiornamento GUI 
	 */
	private void updateMask() {
	    /* Aggiornamento degli elementi collegati direttamente al bean
	     */
		
	    for (int i = 0; i < vtBeanMap.size(); i++) {
			String fldMap = vtBeanMap.elementAt( i ).toString();
			Object value = record.getFieldValue( fldMap );
			if (value == null) {
				Class c = record.getFieldType(fldMap);
				if (c == Boolean.class)
					value = new Boolean(false);
				else if (c == Integer.class)
					value = "0";
				else if (c == java.sql.Timestamp.class)
					value = new Timestamp(Calendar.getInstance().getTimeInMillis()); //  Timestamp.parse(Calendar.getInstance().getTime().toGMTString());
				else if (c == java.util.Date.class)
					value = new java.util.Date(Calendar.getInstance().getTimeInMillis());
			}
				
			pcsMask.firePropertyChange( fldMap, null, value);
	    }
	    
		infoStatus = "Posizione " + ( recordCounter + 1 ) + " di " + beanList.size();
		updateInfoPanel( infoStatus );

	}
	
	/**
	 * Metodo protetto per salvataggio in formato XML
	 * dell'elenco di bean caricati, protetta per garantire
	 * l'accesso delle sotto-classi 
	 */
	protected void save(java.awt.event.ActionEvent evt) {
	    infoStatus = "Elenco dati " + recordClassName + " archiviato in formato XML";
	    Monitor.log( Constants._logHistoryDef + infoStatus );
		// BeanManager.writeBean( beanList, SettingRes.get("mdi.working.xml.dir") + recordClassName + SettingRes.get("mdi.working.xml.bean.extension") );
		BeanManager.writeBean( beanList, info.getXmlReference() );
		// updateMask();
		clearCache(evt);
	}

	protected void clearCache(java.awt.event.ActionEvent evt) {
		
		try {
			if (!evt.getActionCommand().equals( UIRes.getLabel("scroller.buttons.save") ) ) {
				info.recacheData();
			}
		    infoStatus = "Elenco dati [" + recordClassName + "] ricaricato in cache";
		    Monitor.log( Constants._logHistoryDef + infoStatus );
			
		    beanList = (IModelList)BeanManager.loadXMLBean( info.getXmlReference() );
		    this.firstItem(evt);
		    buildNavigationPanel();
			this.updateUI();
		} catch (FrameworkFatalException e) {
			e.printStackTrace();
		} catch (FrameworkDatabaseException e) {
			e.printStackTrace();
		} finally {
			System.runFinalization();
			System.gc();
		}
	}

	
	/*
	protected void queryOffline(java.awt.event.ActionEvent evt) {
		
	    infoStatus = "Elenco dati " + recordClassName + " archiviato in formato XML";
	    Monitor.log( Constants._logHistoryDef + infoStatus );
		BeanManager.writeBean( beanList, info.getXmlReference() );
		
		// new Object[] {Monitor.getRunTimePath(Dev.isEclipseDevRuntime) + "res/data/" + recordClassName + "-bean.xml",new Boolean(true)}
		
		this.scrollerSourceType = new ScrollerSource( info );
		
		this.remove( scrollerPanel );
		this.scrollerPanel = new ScrollerUI();
		this.scrollerPanel.offlineMode();
		
		this.add(scrollerPanel, java.awt.BorderLayout.SOUTH);
		this.updateUI();

		beanList = (IModelList)BeanManager.loadXMLBean( info.getXmlReference() );
	}
	
	protected void queryOnline(java.awt.event.ActionEvent evt) {
		
		// new Object[] {Dev.getRunTimePath(Dev.isEclipseDevRuntime) + "res/data/" + recordClassName + "-bean.xml",new Boolean(true)}
		
	    // aggiornamento completo della cache sull'origine dati
		infoStatus = "Aggiornamento da dati cache XML ad origine QUERY " + recordClassName;
		Monitor.log( Constants._logHistoryDef + infoStatus );
		queryUpdateAll(evt);
	    
	    
		infoStatus = "Reset dei dati cache XML con origine QUERY " + recordClassName;
		Monitor.log( Constants._logHistoryDef + infoStatus );
		this.scrollerSourceType = new ScrollerSource(
		        "SELECT * FROM " + record.getSqltable() + ";",
		        false
		);
		
		this.remove( scrollerPanel );
		this.scrollerPanel = new ScrollerUI();
		this.scrollerPanel.onlineMode();
		this.add(scrollerPanel, java.awt.BorderLayout.SOUTH);
		this.updateUI();
		
		
		
		beanList = (IModelList)BeanManager.getInstance().loadSQLBean( info );
		// TODO : aggiungere metodo di confronto bean cache con bean query
		// attualmente tutti i record modificati o aggiunti nella cache vengono
		// inviati per l'inserimento/aggiornamento dei dati
		infoStatus = "Elenco dati " + recordClassName + " archiviato in cache formato XML";
		Monitor.log( Constants._logHistoryDef + infoStatus );
		
		
		((java.io.File)new java.io.File( SettingRes.get("mdi.working.xml.dir") + recordClassName + SettingRes.get("mdi.working.xml.bean.extension") )).delete();
		infoStatus = "Eliminazione dati " + recordClassName + " archiviati in cache XML";
		Monitor.log( Constants._logHistoryDef + infoStatus );
		// BeanManager.writeBean( vtBeanRecords, Dev.getRunTimePath(Dev.isEclipseDevRuntime) + "res/data/"+recordClassName+"-bean.xml" );
		
	}
	*/
	/**
	 * Metodo protetto per esecuzione SQL UPDATE su db
	 * i parametri fondamentali: 
	 * vtBeanRecords: 	elenco dei bean
	 * vtBeanMap:		elenco dei parametri del bean 
	 */
	protected void queryUpdateAll(java.awt.event.ActionEvent evt) {
		
	    infoStatus = "Aggiornamento/Inserimento dati completo dell'elenco " + recordClassName + " su database origine";
	    Monitor.log( Constants._logHistoryDef + infoStatus );

		try {
			/*
			SQLSelect dbs;

			dbs = new SQLSelect(dbProp.getParams(), true, false);
			
			dbs.preparedBeanUpdate(beanList, vtBeanMap, true);
			*/
			SearchConditions comboSearch = new SearchConditions(SearchConditions.UPDATE);
			comboSearch.setPoolName( info.searchConditions.getPoolName() );
			comboSearch.setUpdatableData(beanList);
			String str = (String)info.dataChannel(comboSearch);
			
		} catch (Exception ex) {
			Monitor.debug( ex );
		} finally {
			clearCache(evt);
		}

	}
	
	
	protected void queryUpdateCurrent(java.awt.event.ActionEvent evt) {

		infoStatus = "Aggiornamento/Inserimento dati singolo del bean " + recordClassName + " su database origine";
		Monitor.log( Constants._logHistoryDef + infoStatus );

		ModelList vtCurrentRecord = new ModelList();
		vtCurrentRecord.addElement( (IModel)record );
		
		try {
			
			SearchConditions comboSearch = new SearchConditions(SearchConditions.UPDATE);
			comboSearch.setPoolName( info.searchConditions.getPoolName() );
			comboSearch.setUpdatableData(vtCurrentRecord);
			String str = (String)info.dataChannel(comboSearch);
		} catch (Exception ex) {
			Monitor.debug( ex.getMessage() );
			Monitor.debug( ex );
		} finally {
			clearCache(evt);
		}
		
	}
	
	protected void scrollerGoto(int i) {
		recordCounter = i-1;
		updateMask();
		isChanged = false;
	}
	
	protected void gotoItem(TreeNodeBean node) {

		if (isChanged && recordCounter != -1) {
			beanList.setElementAt( (IModel)record, recordCounter);
		}
		
		int position = beanList.search(node.getKey());
		/* if (position == -1)
			position = beanList.search(node.getReferenced()); */
		// Oggetto privo di modifiche
		if (position!=-1) {
			record = (IModel)beanList.elementAt(position);
			scrollerGoto(position+1);
		}
	}
	
	/**
	 * Metodo protetto per caricamento record specifico
	 */
	protected void gotoItem(int idx) {
		record = (IModel)beanList.elementAt(idx);
		scrollerGoto(idx);
	}
	
	protected void gotoItem(java.awt.event.ActionEvent evt, int idx) {
		gotoItem(idx);
	}
	
	/**
	 * Metodo protetto per caricamento primo record
	 * dell'elenco di bean caricati 
	 */
	protected void firstItem(java.awt.event.ActionEvent evt) {
		this.deselectNavigationPanel();
	    if (beanList.size() > 0) {
	    	record = (IModel)beanList.firstElement();
			recordCounter = 1;
			previous(evt);
			scrollerPanel.jbFirst.setEnabled(false);
			scrollerPanel.jbNext.setEnabled(true);
			scrollerPanel.jbPrevious.setEnabled(false);
			scrollerPanel.jbLast.setEnabled(true);
			updateMask();
			isChanged = false;
	    }
	}
	
	/**
	 * Metodo protetto per caricamento ultimo record
	 * dell'elenco di bean caricati 
	 */
	protected void lastItem(java.awt.event.ActionEvent evt) {
		this.deselectNavigationPanel();
		if (beanList.size() >= 0) {
			record = (IModel)beanList.lastElement();
			recordCounter = beanList.size()-1;
			scrollerPanel.jbFirst.setEnabled(true);
			scrollerPanel.jbNext.setEnabled(false);
			scrollerPanel.jbPrevious.setEnabled(true);
			scrollerPanel.jbLast.setEnabled(false);
			updateMask();
			isChanged = false;
		}
	}
	
	/**
	 * Metodo protetto per eliminazione record corrente
	 * dell'elenco di bean caricati 
	 */
	protected void deleteItem(java.awt.event.ActionEvent evt) {
		
		Monitor.log( Constants._logEventDef + evt.getActionCommand() );
		
			if (evt.getActionCommand().equals( UIRes.getLabel("scroller.buttons.del") )) {
				beanList.removeElementAt( recordCounter );
				record = (IModel)beanList.lastElement();
				recordCounter = beanList.size()-1;
				String message = UIRes.getLabel("history.scroller.rem");
				this.updateInfoPanel(message);
				Monitor.log( message );
				buildNavigationPanel();
				updateMask();
			} else {
				beanList.removeElementAt(beanList.size()-1);
				record = (IModel)beanList.lastElement();
				recordCounter = beanList.size()-1;
				updateMask();
				scrollerPanel.jbFirst.setEnabled(true);
				scrollerPanel.jbNext.setEnabled(false);
				scrollerPanel.jbPrevious.setEnabled(true);
				scrollerPanel.jbLast.setEnabled(false);
				scrollerPanel.jbSave.setEnabled(true);
				
				scrollerPanel.jbQueryUpdateAll.setEnabled(true);
				// scrollerPanel.jbQueryUpdateCurrent.setEnabled(true);
				
				scrollerPanel.jbAdd.setText( UIRes.getLabel("scroller.buttons.add") );
				scrollerPanel.jbDelete.setText( UIRes.getLabel("scroller.buttons.del") );
				isChanged = false;
				String message = UIRes.getLabel("history.scroller.rem");
				this.updateInfoPanel(message);
				Monitor.log( message );
			}
	}
	
	/**
	 * Metodo protetto per aggiunta record
	 * all'elenco di bean caricati 
	 */
	protected void addItem(java.awt.event.ActionEvent evt) {
		Monitor.log( Constants._logEventDef + evt.getActionCommand() );
	    if (evt.getActionCommand().equals( UIRes.getLabel("scroller.buttons.add") )) {
		    if (!isChanged) {
			
		        scrollerPanel.jbFirst.setEnabled(false);
				scrollerPanel.jbNext.setEnabled(false);
				scrollerPanel.jbPrevious.setEnabled(false);
				scrollerPanel.jbLast.setEnabled(false);
				scrollerPanel.jbSave.setEnabled(false);
				
				scrollerPanel.jbQueryUpdateAll.setEnabled(false);
				// scrollerPanel.jbQueryUpdateCurrent.setEnabled(false);
				
				scrollerPanel.jbAdd.setText( UIRes.getLabel("scroller.buttons.confirm") );
				scrollerPanel.jbDelete.setText( UIRes.getLabel("scroller.buttons.abort") );
				
				try {
					beanList.addElement( (IModel)Class.forName( recordClassName ).newInstance() );
					String message = UIRes.getLabel("history.scroller.add");
					this.updateInfoPanel(message);
					Monitor.log( message );
				} catch (Exception ex) {
					Monitor.debug( ex.getMessage());
					Monitor.debug( ex );
				}
				
				
				
				record = (IModel)beanList.lastElement();
				recordCounter = beanList.size()-1;
				updateMask();
		    } else {
		    	if (beanList.size() == 0 || beanList.size() == 1) {
		    		Monitor.debug("Empty list");
		    		isChanged = false;
		    		addItem(evt);
		    	}
		    }
	    } else {
			
	        scrollerPanel.jbFirst.setEnabled(true);
			scrollerPanel.jbNext.setEnabled(false);
			scrollerPanel.jbPrevious.setEnabled(true);
			scrollerPanel.jbLast.setEnabled(false);
			scrollerPanel.jbSave.setEnabled(true);
			
			scrollerPanel.jbQueryUpdateAll.setEnabled(true);
			// scrollerPanel.jbQueryUpdateCurrent.setEnabled(true);
			
			
			
			
			// scorre l'elenco all'ultimo elemento
			recordCounter = beanList.size();
			
			previous(evt);
			
			scrollerPanel.jbAdd.setText( UIRes.getLabel("scroller.buttons.add") );
			
			scrollerPanel.jbDelete.setText( UIRes.getLabel("scroller.buttons.del") );
			buildNavigationPanel();
	    }
	}
	
	/**
	 * Metodo protetto per caricamento record precedente al corrente
	 * nell'elenco di bean caricati 
	 */
	protected void previous() {
		this.deselectNavigationPanel();
		if (beanList.size() > 0) {    
		if (recordCounter > 0) {
			recordCounter = recordCounter - 1;
			if ( recordCounter == 0) {
				scrollerPanel.jbFirst.setEnabled(false);
				scrollerPanel.jbNext.setEnabled(true);
				scrollerPanel.jbPrevious.setEnabled(false);
				scrollerPanel.jbLast.setEnabled(true);
			} else if (recordCounter < beanList.size()-1) {
				scrollerPanel.jbNext.setEnabled(true);
				scrollerPanel.jbLast.setEnabled(true);
			}
			
			record = (IModel)beanList.elementAt( recordCounter );
			updateMask();
			
			isChanged = false;
		} else if ( recordCounter == 0 ) {
			recordCounter = 0;
			scrollerPanel.jbFirst.setEnabled(false);
			scrollerPanel.jbNext.setEnabled(true);
			scrollerPanel.jbPrevious.setEnabled(false);
			scrollerPanel.jbLast.setEnabled(true);
		}
		}
	}
	protected void previous(java.awt.event.ActionEvent evt) {
		previous();
	}
	
	protected void manageScrollers(boolean status) {
		scrollerPanel.jbFirst.setEnabled(false);
		scrollerPanel.jbNext.setEnabled(status);
		scrollerPanel.jbPrevious.setEnabled(false);
		scrollerPanel.jbLast.setEnabled(status);
	}
	
	/**
	 * Metodo protetto per caricamento record successivo al corrente
	 * nell'elenco di bean caricati 
	 */
	protected void next() {
		this.deselectNavigationPanel();
		if (beanList.size() > 1) {
			if ( recordCounter < beanList.size()-1 ) {
				recordCounter = recordCounter + 1;
				if ( (recordCounter + 1) == beanList.size()) {
					scrollerPanel.jbFirst.setEnabled(true);
					scrollerPanel.jbNext.setEnabled(false);
					scrollerPanel.jbPrevious.setEnabled(true);
					scrollerPanel.jbLast.setEnabled(false);
				} else {
					scrollerPanel.jbFirst.setEnabled(true);
					scrollerPanel.jbPrevious.setEnabled(true);
				}
				
				
				
				if (isChanged) beanList.setElementAt( (IModel)record, recordCounter - 1 );
					
				record = (IModel)beanList.elementAt( recordCounter );
				updateMask();
	
				isChanged = false;
			}
		} else if ( recordCounter == (beanList.size()-1) ) {
			recordCounter = beanList.size()-1;
			scrollerPanel.jbFirst.setEnabled(true);
			scrollerPanel.jbNext.setEnabled(false);
			scrollerPanel.jbPrevious.setEnabled(true);
			scrollerPanel.jbLast.setEnabled(false);
		}
	


	}
	
	protected void next(java.awt.event.ActionEvent evt) {
		next();
	}
    
	final class ScrollerSource {
	    private String source;
		private boolean isXML;
		private boolean isSQL;
		
		public ScrollerSource(ScrollerInfo info) {
			this.source = info.getSource();
			this.isXML = info.isXmlReference();
			this.isSQL = info.isSqlReference(); 
		}
		
		public ScrollerSource(String source, boolean isXML) {
			this.source = source;
			this.isXML = isXML;
			if (!isXML) this.isSQL = true; 
		}

		public String getSource() {
			return this.source;
		}
		
		public boolean isXML() {
		    return isXML;
		}
		
		public boolean isSQL() {
			return isSQL;
		}
	}
	
	// Sotto-classe UIScroller 
	// ATTENZIONE : durante la navigazione offline
	// questa classe viene nuvoamente istanziata perdendo
	// la posizione iniziale nel container
	final class ScrollerUI extends javax.swing.JPanel {
		
		public void updateInfoPanel(String textMex) {
			jlRecordStatus.setText( textMex );
		}
		
		public void disableQueryButtons() {
			jPanel3.remove( jbQueryUpdateAll );
			// jPanel3.remove( jbQueryUpdateCurrent );
			jPanel3.updateUI();
		}
		
		public void onlineMode() {
			// jPanel3.remove( jbQueryOnline );
			jPanel3.updateUI();
		}
	    
		public void offlineMode() {
			this.disableQueryButtons();
			// jPanel3.remove( jbQueryOffline );
			jPanel3.updateUI();
		}
		
		/**
		 * Costruttore di pannello di scorrimento record 
		 */
		public ScrollerUI() {
			initComponents();
		}

		/** This method is called from within the constructor to
		 * initialize the form.
		 * WARNING: Do NOT modify this code. The content of this method is
		 * always regenerated by the Form Editor.
		 */
		private void initComponents() {
			jPanel1 = new javax.swing.JPanel();
			jPanel2 = new javax.swing.JPanel();
			jlRecordStatus = new JLabelUI();
			jPanel3 = new javax.swing.JPanel();
			jbFirst = new JButtonUI();
			jbPrevious = new JButtonUI();
			jbAdd = new JButtonUI();
			jbDelete = new JButtonUI();
			jbNext = new JButtonUI();
			jbLast = new JButtonUI();
			jbSave = new JButtonUI();
			jbClearCache = new JButtonUI();
			/* jbQueryOffline = new JButtonUI();
			jbQueryOnline = new JButtonUI(); */
			jbQueryUpdateAll = new JButtonUI();
			// jbQueryUpdateCurrent = new JButtonUI();
        
			setLayout(new java.awt.BorderLayout());
        
			jPanel1.setLayout(new java.awt.GridBagLayout());
			java.awt.GridBagConstraints gridBagConstraints1;
        
			jPanel2.setLayout(new java.awt.GridLayout(1, 0));
        
			jlRecordStatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			jlRecordStatus.setText(" ");
			// jlRecordStatus.setMaximumSize(new java.awt.Dimension(80,80));
			jlRecordStatus.setBorder(new javax.swing.border.TitledBorder( UIRes.getLabel("scroller.infopanel.borderTitle") ));
			jlRecordStatus.setOpaque(true);
			// jPanel2.setMaximumSize(new java.awt.Dimension(80,80));
			jPanel2.add(jlRecordStatus);
        
			gridBagConstraints1 = new java.awt.GridBagConstraints();
			 
			if (info.isSqlReference())
			    gridBagConstraints1.gridy=1;
			
			gridBagConstraints1.gridwidth = 5;
			gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints1.weightx = 1.0;
			jPanel1.add(jPanel2, gridBagConstraints1);
        
			jPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        
			jbFirst.setText( UIRes.getLabel("scroller.buttons.first") );
			jbFirst.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					actionScroller(evt);
				}
			});
        
			jPanel3.add(jbFirst);
        
			jbPrevious.setText( UIRes.getLabel("scroller.buttons.previous") );
			jbPrevious.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					actionScroller(evt);
				}
			});
        
			jPanel3.add(jbPrevious);
        
			if (!info.isScanner()) {
				jbAdd.setText( UIRes.getLabel("scroller.buttons.add") );
				jbAdd.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						actionScroller(evt);
					}
				});
	        
				jPanel3.add(jbAdd);
	        
				jbDelete.setText( UIRes.getLabel("scroller.buttons.del") );
				jbDelete.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						actionScroller(evt);
					}
				});
				
				jPanel3.add(jbDelete);
			}
			
        
			jbNext.setText( UIRes.getLabel("scroller.buttons.next") );
			jbNext.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					actionScroller(evt);
				}
			});
        
			jPanel3.add(jbNext);
        
			jbLast.setText( UIRes.getLabel("scroller.buttons.last") );
			jbLast.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					actionScroller(evt);
				}
			});
        
			jPanel3.add(jbLast);
        
			
			if (!info.isScanner()) {

					jbSave.setText( UIRes.getLabel("scroller.buttons.save") );
					jbSave.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent evt) {
							actionScroller(evt);
						}
					});
					jPanel3.add(jbSave);
			}
			
			if (info.isSqlReference() && !info.isScanner()) {
				jbClearCache.setText( "CACHE" );
				jbClearCache.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						actionScroller(evt);
					}
				});
	        
				jPanel3.add(jbClearCache);
				/*
				jbQueryOnline.setText( "ONLINE" );
				jbQueryOnline.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						actionScroller(evt);
					}
				});
	        
				jPanel3.add(jbQueryOnline);
				
				jbQueryOffline.setText( "OFFLINE" );
				jbQueryOffline.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						actionScroller(evt);
					}
				});
	        
				jPanel3.add(jbQueryOffline);
			    */
			    jbQueryUpdateAll.setText( "COMMIT ALL" );
				jbQueryUpdateAll.setToolTipText( "Aggiornamento record completo" );
				jbQueryUpdateAll.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						actionScroller(evt);
					}
				});
	        
				jPanel3.add(jbQueryUpdateAll);
				
				/*
				jbQueryUpdateCurrent.setText( "COMMIT" );
				jbQueryUpdateCurrent.setToolTipText( "Aggiornamento record singolo" );
				jbQueryUpdateCurrent.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						actionScroller(evt);
					}
				});
	        
				jPanel3.add(jbQueryUpdateCurrent);
				*/
			}
			
			gridBagConstraints1 = new java.awt.GridBagConstraints();
			jPanel1.add(jPanel3, gridBagConstraints1);
        
			add(jPanel1, java.awt.BorderLayout.SOUTH);
        
		}


		private void actionScroller(java.awt.event.ActionEvent evt) {
			if (evt.getActionCommand().equals( UIRes.getLabel("scroller.buttons.last") ))
			    lastItem(evt);
			else if (evt.getActionCommand().equals( UIRes.getLabel("scroller.buttons.first") ))
			    firstItem(evt);
			else if (evt.getActionCommand().equals( UIRes.getLabel("scroller.buttons.next") ))
			    next(evt);
			else if (evt.getActionCommand().equals( UIRes.getLabel("scroller.buttons.previous") ))
				previous(evt);
			else if (evt.getActionCommand().equals( UIRes.getLabel("scroller.buttons.del") ))
			    deleteItem(evt);
			else if (evt.getActionCommand().equals( UIRes.getLabel("scroller.buttons.abort") ))
				deleteItem(evt);
			else if (evt.getActionCommand().equals( UIRes.getLabel("scroller.buttons.add") ))
			    addItem(evt);
			else if (evt.getActionCommand().equals( UIRes.getLabel("scroller.buttons.confirm") ))
				addItem(evt);
			else if (evt.getActionCommand().equals( UIRes.getLabel("scroller.buttons.save") ))
			    save(evt);
			/* else if (evt.getActionCommand().equals( "ONLINE" ))
				queryOnline(evt); */
			else if (evt.getActionCommand().equals( "CACHE" ))
				clearCache(evt);
			/* else if (evt.getActionCommand().equals( "OFFLINE" ))
				queryOffline(evt); */
			else if (evt.getActionCommand().equals( "COMMIT ALL" ))
			    queryUpdateAll(evt);
			/* else if (evt.getActionCommand().equals( "COMMIT" ))
				queryUpdateCurrent(evt); */
			else
				Monitor.log(UIRes.getLabel("warning.scroller.noactionlinked"));
		}


		// Variables declaration - do not modify
		private javax.swing.JPanel jPanel1;
		private javax.swing.JPanel jPanel2;
		private JLabelUI jlRecordStatus;
		private javax.swing.JPanel jPanel3;
		private JButtonUI jbFirst;
		private JButtonUI jbPrevious;
		private JButtonUI jbAdd;
		private JButtonUI jbDelete;
		private JButtonUI jbNext;
		private JButtonUI jbLast;
		private JButtonUI jbSave;
		private JButtonUI jbClearCache;
		/* private JButtonUI jbQueryOffline;
		private JButtonUI jbQueryOnline; */
		private JButtonUI jbQueryUpdateAll;
		// private JButtonUI jbQueryUpdateCurrent;
		// End of variables declaration

	}


	
	/* CUSTOM BIND COMPONENTS */
	

	
	
	public class JTreeUI extends JTree {

		//Optionally play with line styles.  Possible values are
		//"Angled", "Horizontal", and "None" (the default).
		private boolean playWithLineStyle = true;
		private String lineStyle = "Angled";
		
		private String groupBy;

		private boolean isModelBased = false;
	
		private DefaultTreeModel model;

		public TreeNode createBaseTree() {

			DefaultMutableTreeNode root = new DefaultMutableTreeNode("Oggetti disponibili");

			DefaultMutableTreeNode nodeGroup = null;
			DefaultMutableTreeNode subNodeGroup = null;
			DefaultMutableTreeNode projectTree = null;
			DefaultMutableTreeNode nodeItem = null;

			try {
				Monitor.log( Constants._logDefaultDef + groupBy);

				IModelList vtResultValues = beanList;

				for (java.util.Enumeration e = vtResultValues.elements(); e.hasMoreElements();) {
				    Object userObj = e.nextElement();
					if (userObj instanceof IModel)
						nodeGroup = (TreeNodeBean)((IModel)userObj).getNode();
						// nodeGroup = new DefaultMutableTreeNode( (TreeNodeBean)((Model)userObj).getNode() );
					else
						nodeGroup = (TreeNodeBean)((IModel)userObj).getNode();
					
					// beanList.getAllSearchedResults(value)
					root.add( nodeGroup );
				}
				
				
			} catch (Exception ex) {
				Monitor.debug( ex );
			}


			return root;
		}
	
		public javax.swing.tree.TreeModel getTreeModel() {
			isModelBased = false;
			TreeNode root = createBaseTree();
			return new DefaultTreeModel(root); 
		}
	
		public void prepareTreeModel() {
			isModelBased = true;
			TreeNode root = createBaseTree();
			model = new DefaultTreeModel(root); 
		}

		
		
		
		
		
		// private JTree jTree;
		private DefaultTreeModel jTreeModel;
		private IModelList list;

		public TreeNode createBaseTree(boolean b) {
			DefaultMutableTreeNode root = new DefaultMutableTreeNode("Curricula inviati");

			for (int i = 0; i < list.size(); i++) {
				DefaultMutableTreeNode nodeItem = null;
				if (list.elementAt(i) instanceof ATreeNodableBean) {
					nodeItem = new DefaultMutableTreeNode(
					                   ((ATreeNodableBean)list.elementAt(i)).buildMainNode()
					           );
					DefaultMutableTreeNode[] subNodesItem = ((ATreeNodableBean)list.elementAt(i)).buildSubNodes();
					for (int e=0; e < subNodesItem.length; e++)
						nodeItem.add(subNodesItem[e]);

				} else {
					nodeItem = new DefaultMutableTreeNode(
							list.elementAt(i).toString()
					           );
				}

				root.add(nodeItem);
			}



			return root;
		}
		
		private JPanel objectUI;
		
		public JTreeUI(JPanel object) {	
			list = AMDIApplet.loadBeanCollection(
					"/tmp/working/" + "sample.bl.beans." + "CurriculumRecord-bean.xml"
			);
			objectUI = object;
			TreeNode root = createBaseTree(true);
			jTreeModel = new DefaultTreeModel(root);
			setModel(jTreeModel);

			this.setEditable(false);
			
			Object o = getParent();
			Object o1 = getTreeLock();
			
			// SELECTION LISTENER
			this.addTreeSelectionListener(new TreeSelectionListener() {
				                               public void valueChanged(TreeSelectionEvent e) {
					                               DefaultMutableTreeNode node = (DefaultMutableTreeNode)
					                                                        getLastSelectedPathComponent();
					                               Object o = getParent();
					                               Object o1 = getTreeLock();
					                               try {
						                               if (node == null)
							                               return;

						                               if (node.isLeaf()) {
							                               if (node instanceof ATreeNodableBean) {
							                               		Monitor.log("This node is a tree node bean");
								                               // objectUI.fillDetails( (IModel)node );

							                               	   // objectUI.enableTab(0,true);
							                               	   // objectUI.enableTab(1,true);
							                               	   // objectUI.enableTab(2,true);

							                               } else {
							                               		Monitor.log("This node is not a tree node bean");
							                               	// objectUI.enableTab(0,false);
							                               	// objectUI.enableTab(1,false);
							                               	// objectUI.enableTab(2,false);
							                               }
						                               } else {
						                               		Monitor.log("This node is not a LEAF");
						                               	// objectUI.enableTab(0,false);
						                               	// objectUI.enableTab(1,false);
						                               	// objectUI.enableTab(2,false);
						                               }
					                               } catch (Exception ex) {
						                               ex.printStackTrace();
					                               }
				                               }
			                               }
			                              );
		}
		
		public JTreeUI(String groupBy) {
			super();
			setUI( new BasicTreeUI() );
			this.initByGroup(groupBy);
		}

		public void initByGroup(String groupBy) {
			
			this.groupBy = groupBy;

			if(isModelBased) {
				prepareTreeModel();
				this.setModel( model );
			} else {
				this.setModel( getTreeModel() );
			}
			this.setEditable(false);

			this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

			if (playWithLineStyle) {
				this.putClientProperty("JTree.lineStyle", lineStyle);
				// this.put("Tree.collapsedIcon", IconDispenser.treePlus());
				// this.put("Tree.expandedIcon", IconDispenser.treeMinus());
			
			}


			// SELECTION LISTENER
			this.addTreeSelectionListener(new TreeSelectionListener() {
											  public void valueChanged(TreeSelectionEvent e) {
											  	
												  DefaultMutableTreeNode node = (DefaultMutableTreeNode)
																				getLastSelectedPathComponent();
												  try {
													  if (node == null)
													  	return;

													  if (node.isLeaf()) {
													  	// Object user = node.getUserObject();
													  	if (node instanceof TreeNodeBean) {
													  		gotoItem((TreeNodeBean)node);
													  	} else {
														  		// Monitor.log( Constants._logDefaultDef +"LEAF: " + e.getPath().toString() );
														  		TreeHeader nodeInfo = new TreeHeader(node.getUserObject());
														  		String hId = nodeInfo.getId();
														  		String hTitle = nodeInfo.getTitle();
														  		String hGroup = nodeInfo.getGroup();

														  		gotoItem(Integer.parseInt( nodeInfo.getIdx() ) );
													  	}
													  } else {
													  	Monitor.log( Constants._logDefaultDef + "FOLDER: " + e.getPath().toString() );
													  	// Object user = node.getUserObject();
													  	if (node instanceof TreeNodeBean) {
													  		gotoItem((TreeNodeBean)node);
													  	}
													  }
												  } catch (Exception ex){
												  	Monitor.debug( ex );
												  	Monitor.debug( ex.getMessage() );
												  }
											  }
										  });
		}
		
		public void addTreeNode(String nodeParentLabel, String nodeChildLabel ) {
		
			// TODO : implementare metodo di inserimento a pi� livelli
			DefaultMutableTreeNode newNode = new DefaultMutableTreeNode( nodeChildLabel );

			for (int TREE_ROW = 0; TREE_ROW < this.getRowCount(); TREE_ROW++ ) {

				TreePath path = this.getPathForRow( TREE_ROW );

				TreeNode selectedNode = (DefaultMutableTreeNode)path.getLastPathComponent();
			
				if(selectedNode.toString().equals( nodeParentLabel )) {

					int TOTAL_NODES;
					if(isModelBased)
						TOTAL_NODES = model.getChildCount( selectedNode );
					else
						TOTAL_NODES = ((DefaultTreeModel)this.getModel()).getChildCount( selectedNode );

				
					// new ProjectNode
					if(isModelBased)
						model.insertNodeInto(newNode, (DefaultMutableTreeNode)selectedNode, TOTAL_NODES);
					else 
						((DefaultTreeModel)this.getModel()).insertNodeInto(newNode, (DefaultMutableTreeNode)selectedNode, TOTAL_NODES);
				    

					break;
				}
			}

		
			TreeNode[] nodes = ((DefaultTreeModel)this.getModel()).getPathToRoot(newNode);
			TreePath newPath = new TreePath(nodes);
			this.scrollPathToVisible( newPath );
		

			return;
		}
	

		public void deleteCurrentSelectedNode() {

			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)this.getLastSelectedPathComponent();

			if (selectedNode == null) return;

			if (selectedNode.getParent() != null) {
				if(isModelBased)
					model.removeNodeFromParent(selectedNode);
				else
					Monitor.log(UIRes.getLabel("warning.notimplemented"));
			}

			return;

		}

		public void deleteNodes(String nodeParentLabel) {

			for (int TREE_ROW = 0; TREE_ROW < this.getRowCount(); TREE_ROW++ ) {

				javax.swing.tree.TreePath path = this.getPathForRow( TREE_ROW );

				TreeNode selectedNode = (DefaultMutableTreeNode)path.getLastPathComponent();

				if(selectedNode.toString().equals( nodeParentLabel )) {

					if (selectedNode == null) break;

					int TOTAL_NODES = model.getChildCount( selectedNode );

					if (selectedNode.getParent() != null) {

						for (int i = TOTAL_NODES-1; i >= 0 ; i--) {
							DefaultMutableTreeNode deleteNode = (DefaultMutableTreeNode)model.getChild( selectedNode, i );
							if(isModelBased)
								model.removeNodeFromParent( (DefaultMutableTreeNode)deleteNode );
							else
								Monitor.log(UIRes.getLabel("warning.notimplemented"));
						}

					}

					break;
				}
			}

			return;

		}

		public void addNode(String nodeParentLabel, String nodeChildLabel ) {

			DefaultMutableTreeNode newProjectNode = new DefaultMutableTreeNode(  nodeChildLabel  );

			for (int TREE_ROW = 0; TREE_ROW < this.getRowCount(); TREE_ROW++ ) {

				TreePath path = this.getPathForRow( TREE_ROW );

				TreeNode selectedNode = (DefaultMutableTreeNode)path.getLastPathComponent();

				if(selectedNode.toString().equals( nodeParentLabel )) {

					int TOTAL_NODES = model.getChildCount( selectedNode );

					// new ProjectNode
					if(isModelBased)
						model.insertNodeInto(newProjectNode, (DefaultMutableTreeNode)selectedNode, TOTAL_NODES);
					else
						Monitor.log(UIRes.getLabel("warning.notimplemented"));

					break;
				}
			}

			/*
			TreeNode[] nodes = model.getPathToRoot(newProjectNode);
			TreePath newPath = new TreePath(nodes);
			tree.scrollPathToVisible( newPath );
			*/

			return;

		}
		
		class TreeHeader {

				public String headerId;	
				public String headerTitle;
				public String headerGroup;
				public String headerIdx;

				public TreeHeader(Object model) {
					String[] array;
					if (model instanceof IModel) {
						this.headerId = "1";
					    this.headerTitle = "Titolo nodo";
						this.headerGroup = "Gruppo nodo";
						this.headerIdx = "Indice nodo";
					}else{
					    array = model.toString().split("@");
					    
						this.headerId = array[0];
					    this.headerTitle = array[1];
						this.headerGroup = array[2];
						this.headerIdx = array[3];
					}
				}

				public String getId() {
					return headerId;
				}
				public String getTitle() {
					return headerTitle;
				}
				public String getGroup() {
					return headerGroup;
				}
				public String getIdx() {
					return headerIdx;
				}
				
				public String toString() { 
					return headerTitle;
				}

		}
		

	}


	
}
