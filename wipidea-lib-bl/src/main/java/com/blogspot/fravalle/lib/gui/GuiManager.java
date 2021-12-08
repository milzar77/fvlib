/*
 * GuiManager.java - jappframework (jappframework.jar)
 * Copyright (C) 2006
 * Source file created on 9-gen-2006
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [GuiManager]
 * 2DO:
 *
 */

package com.blogspot.fravalle.lib.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.blogspot.fravalle.lib.bl.beans.IModel;
import com.blogspot.fravalle.lib.data.xml.SaxApplicationException;
import com.blogspot.fravalle.lib.data.xml.XmlReader;
import com.blogspot.fravalle.lib.gui.rendering.MyComboItemUI;
import com.blogspot.fravalle.lib.gui.rendering.XmlGroup;
import com.blogspot.fravalle.lib.monitor.MainLogger;
import com.blogspot.fravalle.lib.monitor.Monitor;
import com.blogspot.fravalle.util.UIRes;
import com.blogspot.fravalle.util.text.Text;

/**
 * [][GuiManager]
 * 
 * <p><b><u>2DO</u>:</b>
 * 
 * 
 * @author Francesco Valle - (antares)
 */
public class GuiManager {

	// private Document	document;
	static int			scan	= 1;
	private final static String START_LINE = "\n";
	
	private Object xmlBind;
	private String xmlBindref;
	
static private boolean autochange = false;
	
	private XmlReader xml;
	private Document root;
	private JPanel hookPane;
	boolean changeStatus = false;
	public boolean isChanged() {
		return changeStatus;
	}
	protected void isChanged(boolean status) {
		changeStatus = status;
	}
	
	/**
	 * @throws SaxApplicationException
	 * 
	 */
	public GuiManager(JPanel jp, Object doc) throws SaxApplicationException {
		super();
		xml = new XmlReader( doc );
		this.hookPane = jp;
		this.root = xml.getDocument();
	}
	
	public Document getXmlDocument() {
		return xml.getDocument();
	}
	
	public void build() {
		NodeList nl = root.getChildNodes();
		build(hookPane, nl);
	}

	public void build(Object binder, String bindRef) {
		xmlBind = binder;
		xmlBindref = bindRef;
		NodeList nl = root.getChildNodes();
		build(hookPane, nl);
	}
	
	public void buildXmlview(){
		NodeList nl = root.getChildNodes();
		xmlView(hookPane, nl);
	}
	
	public void buildTreeview(){
		NodeList nl = root.getChildNodes();
		Vector vt = treeView(new Vector(), nl);
		JTree tree = new JTree(vt);
		hookPane.add(tree);
	}
	
	public void bind() {
		if (xmlBind instanceof IModel)
			((IModel)xmlBind).firePropertyChange(xmlBindref, null, getXmlDocument().getDocumentElement().toString());
	}

	public void unbind(String source) {
		if (xmlBind instanceof IModel)
			((IModel)xmlBind).firePropertyChange(xmlBindref, null, source);
	}
	
	private void build(JPanel mainHookPanel, NodeList nl) {
		int c = scan++ ;
		for (int i = 0; i < nl.getLength(); i++ ) {
			Node n = nl.item(i);
			if (n.hasChildNodes()) {
				XmlGroup jpint = new XmlGroup(n.getNodeName());

				if (!isNotContentNode( n.getFirstChild().getNodeType()) ) {
					String label = n.getNodeName();
					if (n.hasAttributes() && n.getAttributes().getNamedItem("title") != null )
						label = n.getAttributes().getNamedItem("title").getNodeValue();
					//if (n.getChildNodes().getLength() != 1)
						// jpint.setBackground(Color.ORANGE);
					if (n.getChildNodes().item(0).hasChildNodes())
						jpint.setBorder(new javax.swing.border.TitledBorder( label ));
					// jpint.add(new JSeparator());
					jpint.setLayout(new BoxLayout(jpint, BoxLayout.PAGE_AXIS));
					build(jpint, n.getChildNodes());
				} else {
					build(jpint, n.getChildNodes());
				}
				hookPane.add(jpint);
			} else {
				if (n.getChildNodes().getLength() == 0)
					// if (n.getNodeValue() != null && !n.getNodeValue().equals(" "))
						out(n, hookPane);
					
			}

		}
	}
	
	class MyNode extends Vector {
		String name;
		// String Vector;
		public MyNode() {
			super();
		}
		
		public void setName(String parName) {
			this.name = parName;
		}
		
		public void addElement(Object o){
			if (!contains(o))
				super.addElement(o);
		}
		
		public String toString() {
			return name;
		}
	}
	
	private MyNode treeLoader(NodeList nl, MyNode mionodo) {
		for (int i = 0; i < nl.getLength(); i++ ) {
			Node n = nl.item(i);
			if (isElement(n.getNodeType())) {
				MyNode nod = new MyNode();
				nod.setName(n.getNodeName());
				if (n.hasChildNodes())
					mionodo.add( treeLoader(n.getChildNodes(), nod) );
				else {
					if (n.hasAttributes()) {
						MyNode attribs = new MyNode();
						attribs.setName( n.getNodeName() );
						for (int x = 0; x < n.getAttributes().getLength(); x++) {
							String value = n.getAttributes().item(x).getNodeName() + "=" + n.getAttributes().item(x).getNodeValue();
							attribs.add(value);
						}
						mionodo.add( attribs );
					} else {
						mionodo.add( n.getNodeName() );
					}
				}
			}
		}
		return mionodo;
	}
	
	private Vector treeView(Vector vtHook, NodeList nl) {
		for (int i = 0; i < nl.getLength(); i++ ) {
			Node n = nl.item(i);
			if ( n.getParentNode().getNodeName().startsWith("#document") ) {
				MyNode mynode = new MyNode();
				mynode.setName(n.getNodeName());
				vtHook.add( this.treeLoader(n.getChildNodes(), mynode) );
				break;
			}
		}
		return vtHook;
	}
	
	
	private int scanLevel = 0;
	
	private Color getColor(){
		Color color = new Color(200, 220, (scanLevel*15)+10);
		return color;
	}
	
	
	
	private void xmlView(JPanel mainHookPanel, NodeList nl) {

			for (int i = 0; i < nl.getLength(); i++ ) {
				Node n = nl.item(i);
				if ( this.isElement(n.getNodeType()) ) {
					// this.visualElement(n, hookPane);
					if (n.hasChildNodes()) {
						GridBagConstraints grid = new GridBagConstraints();
						grid.gridx=0;
						grid.gridy=i;
						grid.gridwidth=GridBagConstraints.REMAINDER;
						grid.fill=GridBagConstraints.BOTH;
						XmlGroup jpint = new XmlGroup(n.getNodeName());
						this.visualElement(n, jpint, grid);
	
						this.xmlView(jpint, n.getChildNodes());
					
						GridBagConstraints grid1 = new GridBagConstraints();
						grid1.fill=GridBagConstraints.BOTH;
						mainHookPanel.add(jpint, grid1);
					} else {
						
						GridBagConstraints grid = new GridBagConstraints();
						grid.gridx=0;
						grid.gridy=i;
						grid.gridwidth=GridBagConstraints.REMAINDER;
						grid.fill=GridBagConstraints.BOTH;
						XmlGroup jpint = new XmlGroup(n.getNodeName());
						
						this.visualElement(n, jpint, grid);
						
						GridBagConstraints grid1 = new GridBagConstraints();
						grid1.fill=GridBagConstraints.BOTH;
						/*grid1.gridx=0;
						grid1.gridy=0;*/
						mainHookPanel.add(jpint, grid1);
					}
					// System.out.println( "["+n.getNodeType()+"]"+n.getNodeName()+"="+n.getNodeValue() );
				}
			}

	}

	final String minus = " [-]";
	final String plus = " [+]";
	
	private void hider(JPanel hide, boolean isVisible) {

		if (hide.getComponentCount()>1) {
			for (int i = 0; i < hide.getComponentCount(); i++) {
				Component component = hide.getComponent(i);
				if (!(component instanceof JButton))
					component.setVisible(isVisible);
				else {
					if (isVisible)
						((JButton)component).setText( ((JButton)component).getToolTipText() + minus );
					else
						((JButton)component).setText( ((JButton)component).getToolTipText() + plus );
				}
					
			}
		}
	}
	
	private void visualElement(Node node, final JPanel hookPane, GridBagConstraints grid) {
		String textValue = node.getNodeValue();
		
		String nameTest = node.getParentNode().getNodeName();
		if (!nameTest.equals("#document"))
			scanLevel++;
		
		final JButton jtVisualElement = new JButton( node.getNodeName() + minus );
		jtVisualElement.setToolTipText( node.getNodeName() );
		jtVisualElement.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent parArg0) {
				if (parArg0.getActionCommand().indexOf(minus)!=-1)
					hider(hookPane, false);
				else
					hider(hookPane, true);
			}
		});
		
		if (textValue != null) {
			textValue = Text.trim(textValue);
			jtVisualElement.setText( textValue );
			jtVisualElement.setBackground(Color.ORANGE);
		} else {
			// jtVisualNode.setText( node.getNodeName() );
			jtVisualElement.setBackground(Color.LIGHT_GRAY);
		}
		// grid.fill=grid.BOTH;
		
		jtVisualElement.setBackground( getColor() );
		hookPane.add(jtVisualElement, grid);
		grid.gridy=grid.gridy+1;
		hookPane.add(this.visualNode(node), grid);

	}
	
	private JPanel visualNode(Node node) {
		// JPanel jpNode = new JPanel(new GridLayout(1,1));
		// String textValue = node.getNodeValue();
		JPanel jpNodeDetails = new JPanel();
		/*jpNode.add(j1);*/
		
		if (node.hasAttributes())
			jpNodeDetails.add( this.attribs(node) );
		
		JComponent jtVisualNode = null;
		// jtVisualNode.setColumns(9);
		if (node.hasChildNodes()) {
			/*if ( this.isContentNode(node.getNodeType()) ) {
				String textValue = node.getNodeValue();
				if (textValue != null) {
					*/
					//if (!textValue.equals("")) {
						String textValue = "";
						int textCounter = 0;
						for (int i = 0; i < node.getChildNodes().getLength(); i++){
							
							if ( !isElement(node.getChildNodes().item(i).getNodeType()) ) {
								String text = node.getChildNodes().item(i).getNodeValue();
								textValue += Text.trim(text);
								if (!textValue.equals("")) {
									textCounter++;
									textValue += "\r\n"+System.getProperty("line.separator");
								}
							}
						}
						
						/*
						String textValue = node.getFirstChild().getNodeValue();
						textValue = Text.trim(textValue);
						if (textValue.equals(""))
							jtVisualNode.setBackground(Color.ORANGE);
						*/
						/*else
							jtVisualNode.setBackground(Color.YELLOW);*/
						
						if (textCounter == 0) {
							jtVisualNode = new JTextField(textValue);
							if (textValue.equals(""))
								jtVisualNode.setBackground(Color.ORANGE);
							((JTextField)jtVisualNode).setColumns(9);
						} else {
							jtVisualNode = new JScrollPane();
							JTextArea textArea = new JTextArea(textValue);
							((JTextArea)textArea).setColumns(16);
							((JTextArea)textArea).setRows(8);
							((JTextArea)textArea).setWrapStyleWord(true);
							((JScrollPane)jtVisualNode).setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
							((JScrollPane)jtVisualNode).setViewportView(textArea);
						}

					/*//}
				}
			}*/
		} else {
			jtVisualNode = new JTextField("");
			((JTextField)jtVisualNode).setColumns(9);
			jtVisualNode.setBackground(Color.GREEN);
			
		}
		
		jpNodeDetails.add(jtVisualNode);
		
		return jpNodeDetails;

	}
	
	
	private boolean isElement(short key) {
		return ( key == Node.ELEMENT_NODE );
	}
	
	private boolean isNotContentNode(short key) {
		return ( key == Node.TEXT_NODE && key == Node.CDATA_SECTION_NODE );
	}
	
	private boolean isContentNode(short key) {
		return ( key == Node.TEXT_NODE || key == Node.CDATA_SECTION_NODE );
	}
	
	private void compareNodeType(short key) {
		switch ( key ) {
			case Node.ELEMENT_NODE:
				out("This is an element value type");
				break;
			case Node.ATTRIBUTE_NODE:
				out("This is an attribute value type");
				break;
			case Node.TEXT_NODE:
			default:
				out("This is a text node value type");
		}
	}

	private void processAction(Node node, final JTextField jtVisualNode, JPanel hookPane) {
		String label = node.getNodeName();
		if (isContentNode(node.getNodeType())) {
			label = node.getParentNode().getNodeName();
		}
		
		final String nodeLabel = label;
		jtVisualNode.setBorder(new javax.swing.border.TitledBorder( nodeLabel ));
		/* JLabel jl = new JLabel(label);
		jp.add(jl); */
		
		jtVisualNode.getDocument().addDocumentListener(new DocumentListener() {
			  public void insertUpdate(DocumentEvent e) {
				  String oldRefValue = jtVisualNode.getText();
				  javax.swing.border.TitledBorder t = (javax.swing.border.TitledBorder)jtVisualNode.getBorder();
				  xml.updateNodeValue(nodeLabel, oldRefValue);
				  bind();
			  }
			  public void removeUpdate(DocumentEvent e) {}				
			  public void changedUpdate(DocumentEvent e) {}
			});
		
		
		
		if (node.getParentNode() != null && node.getParentNode().hasAttributes()) {
			hookPane.add(jtVisualNode);
			JPanel j1 = attribs(node.getParentNode());
			hookPane.add(j1);
		} else if (node.hasAttributes()) {
			jtVisualNode.setBackground(Color.YELLOW);
			hookPane.add(jtVisualNode);
			JPanel j1 = attribs(node);
			hookPane.add(j1);
		}
		
		
	}
	
	private void out(Node node, JPanel hookPane) {

		if (!START_LINE.equals(node.getNodeValue())) {
			String textValue = node.getNodeValue();
			
			final JTextField jtVisualNode = new JTextField( textValue );
			if (textValue != null) {
				textValue = Text.trim(textValue);
				jtVisualNode.setText( textValue );
				if (textValue.equals(""))
					jtVisualNode.setBackground(Color.ORANGE);
				else
					jtVisualNode.setBackground(Color.PINK);
				jtVisualNode.setToolTipText("["+textValue+"]");
			} else
				jtVisualNode.setBackground(Color.BLUE);
				
			processAction(node, jtVisualNode, hookPane);
			
			// out( n.getNodeName() + "==" + n.getNodeValue());
		}
	    // compareNodeType(n.getNodeType());
	}
	
	private JPanel attribs(final Node n) {
		JPanel jp = new JPanel(new GridLayout(1,2));
		final JComboBox jcb = new JComboBox();
		final JTextField jtxt = new JTextField();
		jtxt.getDocument().addDocumentListener(new DocumentListener() {

			  public void insertUpdate(DocumentEvent e) {
			  	MainLogger.getLog().info("Active!" + jtxt.getText());
			  	if (!autochange) 
			  		((MyComboItemUI)jcb.getSelectedItem() ).changeReal(jtxt.getText());
			  	
				xml.updateNodeAttribute(n.getNodeName(),((MyComboItemUI)jcb.getSelectedItem() ).getItem(), ((MyComboItemUI)jcb.getSelectedItem() ).getReal());
				bind();
			  	autochange = false;
			  }

			  public void removeUpdate(DocumentEvent e) {
			  	MainLogger.getLog().info("ActiveRemove!" + jtxt.getText());
			  }
			
			  public void changedUpdate(DocumentEvent e) {
			  	MainLogger.getLog().info("ActiveUpdate!");
			  }
			});
		
		Vector vt = new Vector();
		vt.add(new MyComboItemUI());
		for (int i = 0; i < n.getAttributes().getLength(); i++) {
			Node node = n.getAttributes().item(i);
			Vector tmp = new Vector();
			tmp.add(node.getLocalName());
			tmp.add(node.getNodeValue());
			MyComboItemUI item = new MyComboItemUI(tmp, 0);
			vt.add(item);
		}
		jcb.setModel( new DefaultComboBoxModel(vt) );
		jcb.addItemListener(new ItemListener() {
		    public void itemStateChanged(ItemEvent e) {
		    	String v = ((MyComboItemUI)e.getItem()).getReal();
		    	autochange = true;
		    	jtxt.setText(v);
		    }
		});
		
		
		
		jp.add(jcb);
		jp.add(jtxt);

		return jp;
	}
	
	private static void out(String output) {
		Monitor.log(UIRes.getLabel("history.xmloutput")+output);
	}

	private String getTreeDepth(int count) {
		String s = "<";
		for (int i = 1; i <= count; i++ ) {
			s += "-";
		}
		return s;
	}


}


