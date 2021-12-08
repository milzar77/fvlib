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

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicLabelUI;
import javax.swing.plaf.basic.BasicPanelUI;
import javax.swing.plaf.basic.BasicRadioButtonUI;
import javax.swing.plaf.basic.BasicTextAreaUI;
import javax.swing.plaf.basic.BasicTextFieldUI;

import com.blogspot.fravalle.lib.bl.beans.IModel;
import com.blogspot.fravalle.lib.bl.beans.IModelRecord;
import com.blogspot.fravalle.lib.data.sql.SearchConditions;
import com.blogspot.fravalle.lib.data.xml.SaxApplicationException;
import com.blogspot.fravalle.lib.data.xml.XmlReader;
import com.blogspot.fravalle.lib.gui.rendering.JXmlUI;
import com.blogspot.fravalle.lib.monitor.MainLogger;
import com.blogspot.fravalle.lib.monitor.Monitor;
import com.blogspot.fravalle.util.Constants;
import com.blogspot.fravalle.util.SettingRes;

/**
 * [][AControlsUI]
 * 
 * <p><b><u>2DO</u>:</b>
 * 
 * 
 * @author Francesco Valle - (antares)
 */
public abstract class AControlsUI extends JPanel {

	
	// DATA: modello di riferimento dei dati
	protected IModel record;
	
	// DATA-BIND: parametro di controllo per tracciamento stato di modifica del dato
	protected boolean isChanged = false;
	
	protected Vector vtBeanMap;
	
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
			if (labelSeed.indexOf(":") != -1) { 
				this.idRef = labelSeed.substring(0,labelSeed.indexOf(":"));
				this.idLabel = labelSeed.substring(labelSeed.indexOf(":")+1,labelSeed.length());
			} else {
				this.idRef = null;
				this.idLabel = labelSeed;
			}

			/* aggiunto riferimento componente alla mappa parametri del bean
			 */
			if (this.idRef != null)
				vtBeanMap.add((String) this.idRef);
			
			if (isLabelMasked)
				setBorder(new javax.swing.border.TitledBorder( idLabel ));
			
			
			/* aggiunto ascoltatore modifica contenuto componente
			 * in comunicazione con parametri bean
			 */
			if (this.idRef != null) {
				pcsMask.addPropertyChangeListener(new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent e) {
						if (e.getPropertyName().equals(idRef))
							setText( String.valueOf(e.getNewValue()) ); 
					}
				});
			}
			
		}
		
	}
	
	abstract protected class JBasicUI extends javax.swing.JTextField {}

	protected class JTextFieldUI extends JBasicUI {

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
			
			if (isLabelMasked)
				setBorder(new javax.swing.border.TitledBorder( idLabel ));
			// TODO : sostituire con eventi key (ENTER), tab (TAB)
			
			getDocument().addDocumentListener(new DocumentListener() {

			  public void insertUpdate(DocumentEvent e) {
			  	// oldRefValue
			  	if(record != null)
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
			
			/* 
			 * aggiunto ascoltatore modifica contenuto componente
			 * in comunicazione con parametri bean
			 */
			pcsMask.addPropertyChangeListener(new PropertyChangeListener() {
						public void propertyChange(PropertyChangeEvent e) {
							if (e.getPropertyName().equals(idRef)) {
								String parsedValue = String.valueOf( record.clearType(idRef, e.getNewValue()) );
								setText( String.valueOf(parsedValue) );
							}
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
			
			/*if (getText()!=null && (getText().startsWith("<") || getText().startsWith("&lt;") )) {
				XmlUI ui = new XmlUI();
				ui.setBind(idRef);
				JFrame jf = new JFrame();
				jf.getContentPane().add(ui);
				jf.show();
			}*/
			
			/* aggiunto riferimento componente alla mappa parametri del bean
			 */
			vtBeanMap.add((String) this.idRef);

			if (isLabelMasked) setBorder(new javax.swing.border.TitledBorder( idLabel ));
			
			
			
			getDocument().addDocumentListener(new DocumentListener() {

			  public void insertUpdate(DocumentEvent e) {
				/*if (getText()!=null && (getText().startsWith("<") || getText().startsWith("&lt;") )) {
					XmlUI ui = new XmlUI();
					ui.setBind(idRef);
					JFrame jf = new JFrame();
					jf.getContentPane().add(ui);
					jf.show();
				}*/
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
							if (e.getPropertyName().equals(idRef))
								setText( String.valueOf(e.getNewValue()) ); 
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
	
	
	protected class JTextMaskUI extends JBasicUI {
	    // TODO : reimplementare funzionalit� di maschera dato con "JFormattedTextField"

		private String idRef;
		private String idLabel;
		private String idDocumentation;
		private Class dataType;
		
		// new JButton("ODIERNA")

		public JTextMaskUI(final String labelSeed, Class parDataType) {
			this(labelSeed);
			this.dataType = parDataType;
		}
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
				      
				      if (!rawData.equals("") && !rawData.equals(" ")) {
				      	
				      	if (dataType == java.sql.Timestamp.class) {
				   	  		SimpleDateFormat sdf = new SimpleDateFormat(SettingRes.get("db.format.date.timestamp.patch"));
				   	  		Calendar c = Calendar.getInstance();
				   	  		c.setTime( sdf.parse(rawData) );
				   	  		// Timestamp timeValue = new Timestamp( c.getTimeInMillis() );
				   	   		String dataConvert = String.valueOf( c.getTimeInMillis() );
				   	   		record.firePropertyChange(idRef, null, dataConvert);
				   	   		isChanged = true;
				      	} else if (dataType == java.sql.Date.class) {
				   	  		SimpleDateFormat sdf = new SimpleDateFormat(SettingRes.get("db.format.date.normal"));
				   	  		Calendar c = Calendar.getInstance();
				   	  		c.setTime( sdf.parse(rawData) );
				   	  		
				   	  		sdf.applyPattern(SettingRes.get("db.format.date.simple"));
				   	   		String dataConvert = sdf.format(c.getTime());

				   	   		record.firePropertyChange(idRef, null, dataConvert);
				   	   		isChanged = true;
				      	} else if (dataType == java.util.Date.class) {
				   	  		SimpleDateFormat sdf = new SimpleDateFormat(SettingRes.get("db.format.date.normal"));
				   	  		Calendar c = Calendar.getInstance();
				   	  		c.setTime( sdf.parse(rawData) );
				      		record.firePropertyChange(idRef, new java.util.Date(), c.getTime());
				   	  		// record.firePropertyChange(idRef, null, sdf.format(c.getTime()));
				   	   		isChanged = true;
				      	}
				      	
			   	   		
				      } else {
				      	Monitor.log("TRACK LOG: " + rawData);
				      }

			      } catch (Exception ex) {
			      	Monitor.debug( ex );
			      }
			  }

			  public void removeUpdate(DocumentEvent e) {
				  /* record.firePropertyChange(idRef, null, getText());
				  isChanged = true; */
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
							if (e.getPropertyName().equals(idRef)) {
								if (dataType == java.sql.Timestamp.class) {
									Calendar c = Calendar.getInstance();
									String value = String.valueOf(e.getNewValue());
									if (value.equals("0"))
										value = String.valueOf(c.getTimeInMillis());
									Long date = new Long(value);
									SimpleDateFormat sdf = new SimpleDateFormat(SettingRes.get("db.format.date.timestamp.patch"));
						   	  		c.setTimeInMillis( date.longValue() );
									setText( sdf.format(c.getTime()) );
								} else if (dataType == java.sql.Date.class) {
									try {
										String date = String.valueOf(e.getNewValue());
										SimpleDateFormat sdf = new SimpleDateFormat(SettingRes.get("db.format.date.simple"));
							   	  		Calendar c = Calendar.getInstance();
							   	  		c.setTime( sdf.parse(date) );
							   	  		
								   	  	sdf.applyPattern(SettingRes.get("db.format.date.normal"));
							   	  		
										setText( sdf.format(c.getTime()) );
									} catch (ParseException ex) {
										ex.printStackTrace();
									}
								} else if (dataType == java.util.Date.class) {
									java.sql.Date date = (java.sql.Date)e.getNewValue();
									SimpleDateFormat sdf = new SimpleDateFormat(SettingRes.get("db.format.date.normal"));
						   	  		Calendar c = Calendar.getInstance();
						   	  		c.setTimeInMillis( date.getTime() );
									setText( sdf.format(c.getTime()) );
								} else {
									setText( String.valueOf(e.getNewValue()));
								}
							}
						}
			});
			
		}
		
	}
	
	
	private final String ACTIVE = "Attivato", INACTIVE = "Disattivato";
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
			setText(idValue);
			/* aggiunto riferimento componente alla mappa parametri del bean
			 */
			if (!vtBeanMap.contains( (String) this.idRef ))
			    vtBeanMap.add((String) this.idRef);

			addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent parE) { 
					if (isSelected()) {
						record.firePropertyChange(idRef, null, String.valueOf( getText().equals(ACTIVE) ));
						isChanged = true;
					}
				}
			});
			
			/* aggiunto ascoltatore modifica contenuto componente
			 * in comunicazione con parametri bean
			 */
			pcsMask.addPropertyChangeListener(new PropertyChangeListener() {
				
						public void propertyChange(PropertyChangeEvent e) {
							
							if (e.getPropertyName().equals(idRef)) {
								
								String strConvert = String.valueOf(e.getNewValue());

								if (strConvert.equals("false"))
									strConvert = INACTIVE;
								else if (strConvert.equals("true"))
									strConvert = ACTIVE;
								else
									strConvert = INACTIVE;
							    setSelected( idValue.equals( strConvert ) );
							}
						}
			});
			
		}
		
	}
	
	
	public class JItemGroupUI extends javax.swing.ButtonGroup {

		public JItemGroupUI(String ref, String defaultValue) {
			super();
			/* javax.swing.JRadioButton jRadioButton0 = new JRadioUI( ref, defaultValue );
			jRadioButton0.setText("invisibile");
			jRadioButton0.setVisible(false);
			add(jRadioButton0); */
		}
		
	}
	
	protected class JButtonUI extends javax.swing.JButton {

		public JButtonUI() {
			super();
			setUI( new BasicButtonUI() );
		}
		
	}


	protected class JActivationPanelUI extends javax.swing.JPanel {

		public JActivationPanelUI(String beanLabel) {
			super();
			setUI( new BasicPanelUI() );
        	JItemGroupUI group = new JItemGroupUI("group","pippo");
        	javax.swing.JRadioButton jr1 = new JRadioUI(beanLabel,ACTIVE);
        	javax.swing.JRadioButton jr2 = new JRadioUI(beanLabel,INACTIVE);
        	
        	group.add(jr1);
        	group.add(jr2);
        	
        	javax.swing.JPanel jp = new javax.swing.JPanel();
        	jp.add(jr1);
        	// jp.add(new javax.swing.JLabel(ACTIVE));
        	jp.add(jr2);
        	// jp.add(new javax.swing.JLabel(INACTIVE));
        	
        	// javax.swing.JCheckBox jcx = new javax.swing.JCheckBox("Attiva");
        	// jp.add(jcx);
        	add(jp);
		}
		
	}
	
	protected class XmlUI extends JXmlUI {
		String bindName;
		
		public XmlUI(){
			super();
		}
/*		public XmlUI(String bindNameRef, Object source){
			super(source);
			bindName = bindNameRef;
		} */
		public void setBind(String name) {
			Object source = record.getFieldValue(name);
			
			if (source != null && !source.toString().equals("null") && !source.toString().startsWith("<!")) {
				this.setXmlSource( record.getFieldValue(name), record, name);
				this.initView(pcsMask);
			} else {
				this.setXmlSource( empty, record, name);
				this.initView(pcsMask);
			}
		}
		
	}
	
	
	protected class TextContentUI extends javax.swing.JPanel {

		private String idRef;
		private String idLabel;
		private String idDocumentation;
		private JTextArea jtext;
		
		public void setText(String text) {
			jtext.setText(text);
		}
		
		public TextContentUI(final String labelSeed) {
			super();
			setUI( new BasicPanelUI() );
			setLayout(new BorderLayout());
			this.idRef = labelSeed.substring(0,labelSeed.indexOf(":"));
			this.idLabel = labelSeed.substring(labelSeed.indexOf(":")+1,labelSeed.length());

			
			jtext = new JTextArea();
			jtext.setUI( new BasicTextAreaUI() );
			if (isLabelMasked)
				jtext.setBorder(new javax.swing.border.TitledBorder( idLabel ));
			
			jtext.getDocument().addDocumentListener(new DocumentListener() {

			  public void insertUpdate(DocumentEvent e) {
				  record.firePropertyChange(idRef, null, jtext.getText());
				  isChanged = true;
			  }

			  public void removeUpdate(DocumentEvent e) {
				  record.firePropertyChange(idRef, null, jtext.getText());
				  isChanged = true;
			  }
			
			  public void changedUpdate(DocumentEvent e) {}
			});

			pcsMask.addPropertyChangeListener(new PropertyChangeListener() {
						public void propertyChange(PropertyChangeEvent e) {
							if (e.getPropertyName().equals(idRef))
								jtext.setText( String.valueOf(e.getNewValue()) ); 
						}
			});
		
			JScrollPane jscroll = new JScrollPane();
			jscroll.setViewportView(jtext);
			add(jscroll, BorderLayout.CENTER);
			
			JButton jb = new JButton();
			jb.setText("Genera XML");
			jb.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent parE) {
					 pcsMask.firePropertyChange(idRef, null, jtext.getText());
					 isChanged = true;
				}});
			add(jb, BorderLayout.SOUTH);
			
		}
		
	}
	
	
	protected class ContentUI extends JPanel {
		
		private String idRef;
		private String idLabel;
		
		private void setView(Object source, PropertyChangeSupport pcsMask) {
			boolean isXml = false;
			try {
				// test percorso xml
				// test contenuto xml
				// test
				XmlReader test = new XmlReader(source);
				isXml = test.getDocument() != null;
			} catch (SaxApplicationException e) {
				isXml = false;
			}
			MainLogger.getLog().fine("SOURCE:"+source);
			removeAll();
			if (isXml) {
				XmlUI ui = new XmlUI();
				ui.setBind(idRef);
				add(ui);
				updateUI();
			} else {
				TextContentUI ui = new TextContentUI(idRef+":"+idLabel);
				ui.setText( String.valueOf(source) );
				add(ui);
				updateUI();
			}
		}
		
		public ContentUI(final String labelSeed) {
			super();
			
			setLayout(new GridLayout(1,1));
			this.idRef = labelSeed.substring(0,labelSeed.indexOf(":"));
			this.idLabel = labelSeed.substring(labelSeed.indexOf(":")+1,labelSeed.length());
			
			vtBeanMap.add((String) this.idRef);

			if (isLabelMasked)
				setBorder(new javax.swing.border.TitledBorder( idLabel ));
			
			pcsMask.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent e) {
					if (e.getPropertyName().equals(idRef)) {
						setView( e.getNewValue(), pcsMask );
					}
				}
			});
			
		}
	}
	
	
}
