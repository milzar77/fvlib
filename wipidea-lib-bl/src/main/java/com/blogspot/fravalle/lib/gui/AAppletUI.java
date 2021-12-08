/*
 * AControlsUI.java - jappframework (jappframework.jar)
 * Copyright (C) 2005
 * Source file created on 20-dic-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [AControlsUI]
 * 2DO:
 *
 */

package com.blogspot.fravalle.lib.gui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Vector;
import javax.swing.JApplet;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicLabelUI;
import javax.swing.plaf.basic.BasicRadioButtonUI;
import javax.swing.plaf.basic.BasicTextAreaUI;
import javax.swing.plaf.basic.BasicTextFieldUI;

import com.blogspot.fravalle.lib.bl.beans.IModel;
import com.blogspot.fravalle.lib.bl.beans.IModelRecord;
import com.blogspot.fravalle.lib.data.sql.SearchConditions;
import com.blogspot.fravalle.lib.monitor.Monitor;
import com.blogspot.fravalle.util.Constants;

/**
 * [][AControlsUI]
 * 
 * <p><b><u>2DO</u>:</b>
 * 
 * 
 * @author Francesco Valle - (antares)
 */
abstract public class AAppletUI extends JApplet {

	public AAppletUI(){
		pcsMask = new PropertyChangeSupport(this);
	}
	
	// DATA: modello di riferimento dei dati
	protected IModel record;
	
	// DATA-BIND: parametro di controllo per tracciamento stato di modifica del dato
	protected boolean isChanged = false;
	
	protected Vector vtBeanMap = new Vector();
	
	// VIEW: etichetta del singolo dato
	protected boolean isLabelMasked=false;
	
	// DATA-BIND: tracciamento della valorizzazione dati
	protected PropertyChangeSupport pcsMask;
	
	protected ScrollerInfo info;
	
	protected class JLabelUI extends javax.swing.JLabel {

		private String idRef;
		private String idLabel;
		// TODO : id per documentazione elemento associato a componente
		private String idDocumentation;
		
		public JLabelUI() {
			super();
			setUI( new BasicLabelUI() );
		}
		public JLabelUI(final String labelSeed) {
			super();
			setUI( new BasicLabelUI() );
			if (labelSeed.indexOf(":")!=-1) {
			this.idRef = labelSeed.substring(0,labelSeed.indexOf(":"));
			this.idLabel = labelSeed.substring(labelSeed.indexOf(":")+1,labelSeed.length());
			} else {
				this.idRef = labelSeed;
				this.idLabel = labelSeed;
			}
			/* aggiunto riferimento componente alla mappa parametri del bean
			 */
			vtBeanMap.add((String) this.idRef);
			
			if (isLabelMasked) setBorder(new javax.swing.border.TitledBorder( idLabel ));
			
			
			/* aggiunto ascoltatore modifica contenuto componente
			 * in comunicazione con parametri bean
			 */
			pcsMask.addPropertyChangeListener(new PropertyChangeListener() {
						public void propertyChange(PropertyChangeEvent e) {
							if (e.getPropertyName().equals(idRef)) setText( String.valueOf(e.getNewValue())); 
						}
			});
			
		}
		
	}
	

	protected class JTextFieldUI extends javax.swing.JTextField {

	    private String idRef;
	    private String oldRefValue;
		private String idLabel;
		private String idDocumentation;
		
		public JTextFieldUI(final String labelSeed) {
			super();
			setUI( new BasicTextFieldUI() );
			this.idRef = labelSeed.substring(0,labelSeed.indexOf(":"));
			this.idLabel = labelSeed.substring(labelSeed.indexOf(":")+1,labelSeed.length());
			this.oldRefValue = null;

			/* aggiunto riferimento componente alla mappa parametri del bean */
			vtBeanMap.add((String) this.idRef);
			
			if (isLabelMasked) setBorder(new javax.swing.border.TitledBorder( idLabel ));
			
			// TODO : sostituire con eventi key (ENTER), tab (TAB)
			getDocument().addDocumentListener(new DocumentListener() {

			  public void insertUpdate(DocumentEvent e) {
			  	// oldRefValue
				  record.firePropertyChange(idRef, null, getText());
				  oldRefValue = getText();
				  isChanged = true;
			  }

			  public void removeUpdate(DocumentEvent e) {
				  record.firePropertyChange(idRef, null, getText());
				  isChanged = true;
			  }
			
			  public void changedUpdate(DocumentEvent e) {
				  // firePropertyChange(id, null, getText());
			  }
			});
			
			/* aggiunto ascoltatore modifica contenuto componente
			 * in comunicazione con parametri bean
			 */
			pcsMask.addPropertyChangeListener(new PropertyChangeListener() {
						public void propertyChange(PropertyChangeEvent e) {
							if (e.getPropertyName().equals(idRef)) setText(String.valueOf(e.getNewValue())); 
						}
			});
			
		}
		
	}

	protected class JTextAreaUI extends javax.swing.JTextArea {

		private String idRef;
		private String idLabel;
		private String idDocumentation;
		
		public JTextAreaUI(final String labelSeed) {
			super();
			setUI( new BasicTextAreaUI() );
			this.idRef = labelSeed.substring(0,labelSeed.indexOf(":"));
			this.idLabel = labelSeed.substring(labelSeed.indexOf(":")+1,labelSeed.length());
			
			/* aggiunto riferimento componente alla mappa parametri del bean
			 */
			vtBeanMap.add((String) this.idRef);

			if (isLabelMasked) setBorder(new javax.swing.border.TitledBorder( idLabel ));
			
			
			
			getDocument().addDocumentListener(new DocumentListener() {

			  public void insertUpdate(DocumentEvent e) {
				  record.firePropertyChange(idRef, null, getText());
				  isChanged = true;
			  }

			  public void removeUpdate(DocumentEvent e) {
				  record.firePropertyChange(idRef, null, getText());
				  isChanged = true;
			  }
			
			  public void changedUpdate(DocumentEvent e) {
				  // firePropertyChange(id, null, getText());
			  }
			});
			
			/* aggiunto ascoltatore modifica contenuto componente
			 * in comunicazione con parametri bean
			 */
			pcsMask.addPropertyChangeListener(new PropertyChangeListener() {
						public void propertyChange(PropertyChangeEvent e) {
							if (e.getPropertyName().equals(idRef)) setText( String.valueOf(e.getNewValue()) ); 
						}
			});
			
		}
		
	}


	protected class JComboUI extends javax.swing.JComboBox {

		private String idRef;
		private String idLabel;
		private boolean groupBy;
		// TODO : id per documentazione elemento associato a componente
		private String idDocumentation;
		private java.util.Hashtable hsReferenceMaskedValues;
		
		String joinTable, joinKey, joinMask;
		
		// public JComboUI(final String labelSeed) {
		public JComboUI(String labelSeed) {
			super();
			setUI( new BasicComboBoxUI() );
			super.setEditable(false);
			
			boolean isJoinQuery = false; 
			
			this.groupBy = labelSeed.startsWith("+");
			
			if (!this.groupBy)
			    this.groupBy = labelSeed.startsWith("{");
			
			if ( this.groupBy && ScrollableRecordUI.comboDefaultValues == null) {
				if (labelSeed.startsWith("+")) 
				    labelSeed = labelSeed.substring( labelSeed.indexOf("+")+1, labelSeed.length());
				else if (labelSeed.startsWith("{")) { 
					String[] array = (labelSeed.substring(labelSeed.indexOf( "{" )+1 , labelSeed.indexOf( "}" ))).split(",");
				    joinTable = array[0];
					joinKey = array[1];
					joinMask = array[2];
					labelSeed = labelSeed.substring( labelSeed.indexOf("}")+1, labelSeed.length());
					isJoinQuery = true;
				}
				
				this.idRef = labelSeed.substring(0,labelSeed.indexOf(":"));
				this.idLabel = labelSeed.substring(labelSeed.indexOf(":")+1,labelSeed.length());
				
				// com.blogspot.fravalle.gui.ScrollableRecordUI.comboDefaultValues= new java.util.Hashtable();
				
				try {
				    // TODO : impostare parametri per jdbc
				    String sqlColumn = this.idRef.substring(1);
			
					String sqlSelect = ""; 
					if (!isJoinQuery)
					    sqlSelect = "SELECT "+sqlColumn+" FROM " + ((IModelRecord)record).getSqltable() + " GROUP BY "+sqlColumn;
					else
					    sqlSelect = "SELECT " + joinTable + "." + joinMask + ", " + ((IModelRecord)record).getSqltable() + "." + sqlColumn + " FROM " + ((IModelRecord)record).getSqltable()
					    + " INNER JOIN " + joinTable + " ON " + ((IModelRecord)record).getSqltable() + "." + sqlColumn + " = " + joinTable + "." + joinKey
					    + " GROUP BY " + joinTable + "." + joinMask;
					
		    
					SearchConditions comboSearch = new SearchConditions();
					comboSearch.setPoolName( info.searchConditions.getPoolName() );
					comboSearch.setQuery( sqlSelect );

					Vector vtComboValues = (Vector)info.dataChannel(comboSearch);

					if (!isJoinQuery)
					    setModel( new javax.swing.DefaultComboBoxModel(
									(Vector)vtComboValues
									));
					else {
					    int i = 0;
					    for (java.util.Enumeration e = vtComboValues.elements(); e.hasMoreElements();) {
					        Vector v = (Vector)e.nextElement();
					        addItem( new ComboItemUI(v, i) );
							i++;
					    }
					}
					    
					
				} catch (Exception ex) {
					Monitor.debug( ex );
					Monitor.debug( ex.getMessage() );
				}
				
				

				
				// TODO : esecuzione metodo di raggruppamento valori
			} else {
				this.idRef = labelSeed.substring(0,labelSeed.indexOf(":"));
				this.idLabel = labelSeed.substring(labelSeed.indexOf(":")+1,labelSeed.length());
				
				if (ScrollableRecordUI.comboDefaultValues != null)
					setModel( new javax.swing.DefaultComboBoxModel(
								(Vector)ScrollableRecordUI.comboDefaultValues.get(this.idRef)
								));
			}


			/* aggiunto riferimento componente alla mappa parametri del bean
			 */
			vtBeanMap.add((String) this.idRef);
			
			if (isLabelMasked) setBorder(new javax.swing.border.TitledBorder( this.idLabel ));
			
			addItemListener(new ItemListener() {
			    public void itemStateChanged(ItemEvent e) {
			        
			        if (e.getItem() instanceof ComboItemUI) {
			            String v = ((ComboItemUI)e.getItem()).getReal();
						record.firePropertyChange(idRef, null, v);
			        } else {
						record.firePropertyChange(idRef, null, (String)e.getItem());
			        }
					isChanged = true;
			    }
			});
			
			// TODO : condividere classe raggiungibile da package esterni
			// e replicabile per bean xml
			
			/* aggiunto ascoltatore modifica contenuto componente
			 * in comunicazione con parametri bean
			 */
			pcsMask.addPropertyChangeListener(new PropertyChangeListener() {
						public void propertyChange(PropertyChangeEvent e) {
							if (e.getPropertyName().equals(idRef)) {
							    // TODO : incapsulare vettore per generazione lista combo
							    String selectedValue = (String)e.getNewValue();
							    if (hsReferenceMaskedValues == null)
							        setSelectedItem( selectedValue );
							    else
							        setSelectedIndex( Integer.parseInt( hsReferenceMaskedValues.get(selectedValue).toString() ) ); 
							}
						}
			});
			
		}
		
		class ComboItemUI extends Object {
		    private Vector v;
			public ComboItemUI(Vector v, int idx) {
			    this.v = v;
			    if (hsReferenceMaskedValues == null) hsReferenceMaskedValues = new java.util.Hashtable();
				hsReferenceMaskedValues.put(getReal(),(String)""+idx);
			    Monitor.log( Constants._logDefaultDef + v);
			}
			public final String getReal() {
				return ""+v.elementAt(1);
			}
			public final String getItem() {
				return ""+v.elementAt(0);
			}
			public final String toString() {
			    return ""+v.elementAt(0);
			}
		}
		
	}
	
	
	protected class JTextMaskUI extends javax.swing.JTextField {
	    // TODO : Implementare funzionalit� di maschera dato

		private String idRef;
		private String idLabel;
		private String idDocumentation;
		
		// new JButton("ODIERNA")
		
		public JTextMaskUI(final String labelSeed) {
			super();
			setUI( new BasicTextFieldUI() );
			this.idRef = labelSeed.substring(0,labelSeed.indexOf(":"));
			this.idLabel = labelSeed.substring(labelSeed.indexOf(":")+1,labelSeed.length());

			/* aggiunto riferimento componente alla mappa parametri del bean
			 */
			vtBeanMap.add((String) this.idRef);
			
			if (isLabelMasked)
				setBorder(new javax.swing.border.TitledBorder( idLabel ));
			
			// TODO : sostituire con eventi key (ENTER), tab (TAB)
			getDocument().addDocumentListener(new DocumentListener() {

			  public void insertUpdate(DocumentEvent e) {
			      try {
			          // FEATURE : implementare mascheramento data e formattazione automatica
				      String rawData = getText();
				      String formatData = "";
				      
				      if (!rawData.equals("") || rawData.equals(" ")) {
						java.text.SimpleDateFormat df1 = new java.text.SimpleDateFormat("dd/MM/yyyy");
						java.util.Date dtLocal = df1.parse( rawData );
						formatData = df1.format( dtLocal );
						setText(formatData);
						Monitor.log( Constants._logDefaultDef +"insert");
				      }
				      
				      
					  record.firePropertyChange(idRef, null, formatData);
					  isChanged = true;
			      } catch (Exception ex) {
			      	Monitor.debug( ex );
			      	Monitor.debug( ex.getMessage() );
			      }
			  }

			  public void removeUpdate(DocumentEvent e) {
				  record.firePropertyChange(idRef, null, getText());
				  isChanged = true;
			  }
			
			  public void changedUpdate(DocumentEvent e) {
				  // firePropertyChange(id, null, getText());
			  }
			});
			
			/* aggiunto ascoltatore modifica contenuto componente
			 * in comunicazione con parametri bean
			 */
			pcsMask.addPropertyChangeListener(new PropertyChangeListener() {
						public void propertyChange(PropertyChangeEvent e) {
							if (e.getPropertyName().equals(idRef)) setText((String)e.getNewValue()); 
						}
			});
			
		}
		
	}
	
	
	protected class JRadioUI extends javax.swing.JRadioButton {
		// TODO : Implementare funzionalit� di maschera dato

		public String idRef;
		private String idLabel;
		private String idDocumentation;
		public String idValue;
		
		public JRadioUI(final String idRef, final String idValue) {
			super();
			setUI( new BasicRadioButtonUI() );
			this.idRef = idRef;
			this.idValue = idValue;

			/* aggiunto riferimento componente alla mappa parametri del bean
			 */
			if (!vtBeanMap.contains( (String) this.idRef ))
			    vtBeanMap.add((String) this.idRef);
			
			/* aggiunto ascoltatore modifica contenuto componente
			 * in comunicazione con parametri bean
			 */
			pcsMask.addPropertyChangeListener(new PropertyChangeListener() {
						public void propertyChange(PropertyChangeEvent e) {
							
							if (e.getPropertyName().equals(idRef)) {
							    setSelected( e.getNewValue().equals( idValue ) );
							}
						}
			});
			
		}
		
	}
	
	
	public class JItemGroupUI extends javax.swing.ButtonGroup {

		public JItemGroupUI(String ref, String defaultValue) {
			super();
			javax.swing.JRadioButton jRadioButton0 = new JRadioUI( ref, defaultValue );
			jRadioButton0.setText("invisibile");
			jRadioButton0.setVisible(false);
			add(jRadioButton0);
		}
		
	}
	
	protected class JButtonUI extends javax.swing.JButton {

		public JButtonUI() {
			super();
			setUI( new BasicButtonUI() );
		}
		
	}

	
}
