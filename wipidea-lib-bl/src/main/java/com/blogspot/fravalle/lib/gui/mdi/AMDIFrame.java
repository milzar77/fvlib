/*
 * AMDIWindow.java - jappframework (jappframework.jar)
 * Copyright (C) 2005
 * Source file created on 8-dic-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [AMDIWindow]
 * 2DO:
 *
 */

package com.blogspot.fravalle.lib.gui.mdi;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.BindException;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;

import com.blogspot.fravalle.lib.gui.AWindow;
import com.blogspot.fravalle.util.UIRes;
import com.blogspot.fravalle.util.gfx.Drawer;
import com.blogspot.fravalle.util.text.Text;

/**
 * @author antares
 */
abstract public class AMDIFrame extends AWindow {
	
	protected static MDIDesktop mdiDesktop;
	protected static AMDIApplet mdiApplet;
	
	/**
	 * @param parWindowTitle
	 * @param parWindowLookAndFeel
	 */
	public AMDIFrame(String parWindowTitle, String parWindowLookAndFeel) {
		super(parWindowTitle, parWindowLookAndFeel);
	}
	
	public void initWindow() {
		getMainLogger().info( UIRes.getLabel("warning.override.method") );
	}
	
	protected static void createMDIFrame(String pluginUrl) {
		try {
			Class loader = Class.forName(pluginUrl);
			if (getWindowContainer().isRunningApplet(pluginUrl) && AMDIApplet.runStatus != AMDIApplet.runAlways ) {
				// if (!neverMoreLaunch) {
				getMainLogger().info("Applet already running");
				// }
			} else {
				//mdiApplet = (MDIApplet)loader.newInstance();
				// Boolean isPalette = (Boolean)mdiApplet.getClientProperty("JInternalFrame.isPalette");
				// if (mdiApplet.runOnce <= 1) {
						mdiApplet = (AMDIApplet)loader.newInstance();
						mdiApplet.setVisible(true);
						mdiDesktop.add(mdiApplet);
						Boolean isPalette = (Boolean)mdiApplet.getClientProperty("JInternalFrame.isPalette");
						mdiApplet.setSelected(true);
						//if (!isPalette.booleanValue()) {
							getWindowContainer().addRunningApplet(pluginUrl);
						//}
				// } else {
					// mdiApplet.dispose();
					// MainLogger.getLog()MainLogger.getLog()getLog().info("Static applet already running");
				//}
					
			}
		} catch (java.beans.PropertyVetoException e) {
			getMainLogger().severe( e.getMessage() );
		} catch (ClassNotFoundException e) {
			getMainLogger().severe( e.getMessage() );
		} catch (InstantiationException e) {
			getMainLogger().severe( e.getMessage() );
		} catch (IllegalAccessException e) {
			getMainLogger().severe( e.getMessage() );
		} catch (Exception e) {
			if (e instanceof BindException)
				getMainLogger().warning("ERRORE DI AVVIO SERVER: " + e.getMessage());
			else{
				getMainLogger().severe( e.getMessage() );
				e.printStackTrace();
			}
		}
		
	}
	
	public static void manageMdi(String pluginUrl) {
			createMDIFrame(pluginUrl);
	}
	
	private Font defaultFont;
	private String customFontName; 
	
	public void applyStyle() {
		applyDefaultFormatting(this, 0);
	}
	
	public void applyStyle(String alternatore) {
		try {
			int altName = Integer.parseInt(alternatore);
			applyDefaultFormatting(this, altName);
		} catch ( NumberFormatException e ) {
			e.printStackTrace();
			applyDefaultFormatting(this, 0);
		}
	}
	
	void applyDefaultFormatting(Container source, int alternator) {
		for (int i = 0; i < source.getComponentCount(); i++) {
			Component component = source.getComponent(i);
			// this.defaultFormatting(component);
			
			if (alternator==0)
				this.defaultFormatting(component);
			else if (alternator==1)
				this.plainStyle(component);
			else if (alternator==2)
				this.myStyle(component);
			else if (alternator==3)
				this.myStyle2(component);
			else
				this.defaultFormatting(component);
			
			
			if (component instanceof Container) {
				applyDefaultFormatting((Container) component, alternator);
			}
		}
	}
	
	void defaultFormatting(Component component) {
		
		defaultFont = new Font("System", Font.PLAIN, 13);
		// customFontName = "Bitstream Vera Sans Mono";
		// customFontName = "Trebuchet MS";
		// customFontName = "Andover";
		// customFontName = "Android Nation";
		// customFontName = "Arioso";
		// customFontName = "Courier New";
		// customFontName = "Neuropol";
		// customFontName = "OCR A Extended";
		customFontName = "System";
		Font customFont = new Font(customFontName, Font.PLAIN, 13);

		if (component instanceof JComponent) {
			 Border borderTemp = ((JComponent)component).getBorder();
			 if (borderTemp instanceof TitledBorder) {
			 	
			 }
		}
			
		
		if (component instanceof JTextComponent || component instanceof JTable) {
			component.setFont(defaultFont);
			component.setBackground(new Color(251,251,251));
			component.setForeground(Color.DARK_GRAY);
			((JComponent)component).setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
		} else {
			component.setFont(customFont);
			if (component instanceof JComponent && !(component instanceof JScrollPane) && !(component instanceof JViewport) && !(component instanceof JPanel) && !(component instanceof JTabbedPane) ) {
				((JComponent)component).setBorder(new SoftBevelBorder(BevelBorder.RAISED));
				if (component instanceof JButton) {
					component.setBackground(Color.ORANGE);
					component.setForeground(Color.BLUE);
					((JComponent)component).setBorder(BorderFactory.createEtchedBorder());
				}
			} else {
				if (component instanceof JPanel || component instanceof JTabbedPane) {
					component.setBackground(new Color(240,240,240));
					// ((JComponent)component).setBorder(BorderFactory.createEmptyBorder());
					if (component instanceof JPanel) {	
						Border border = ((JComponent)component).getBorder();
						if ( !(border instanceof TitledBorder) )
							((JComponent)component).setBorder(null);
					} else {
						((JComponent)component).setBorder(null);
					}
				}
				if (component instanceof JViewport) {
					component.setBackground(new Color(0,40,40));
					// ((JComponent)component).setBorder(BorderFactory.createEmptyBorder());
					// ((JViewport)component).setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
				}
			}
		}
	}
	
	void myStyle(Component component) {
		
		plainStyle(component);
		
		defaultFont = new Font("Trebuchet MS", Font.PLAIN, 13);
		// customFontName = "Oloron Tryout";
		customFontName = "Bitstream Vera Sans Mono";

		Font customFont = new Font(customFontName, Font.PLAIN, 13);

		if (component instanceof JTextComponent || component instanceof JTable) {
			component.setFont(defaultFont);
			component.setBackground(new Color(251,251,251));
			component.setForeground(Color.DARK_GRAY);
			if (!(component instanceof JTextArea)) 
				((JComponent)component).setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
		} else {
			component.setFont(customFont);
			if (component instanceof JComponent && !(component instanceof JScrollPane) && !(component instanceof JViewport) && !(component instanceof JPanel) && !(component instanceof JTabbedPane) ) {
				((JComponent)component).setBorder(new SoftBevelBorder(BevelBorder.RAISED));
				/* if (component instanceof JButton) {
					component.setBackground(Color.ORANGE);
					component.setForeground(Color.BLUE);
					((JComponent)component).setBorder(BorderFactory.createEtchedBorder());
				} */
			} else {
				if (component instanceof JPanel || component instanceof JTabbedPane) {
					component.setBackground(new Color(240,240,240));
					// ((JComponent)component).setBorder(BorderFactory.createEmptyBorder());
					if (component instanceof JPanel) {	
						Border border = ((JComponent)component).getBorder();
						if ( !(border instanceof TitledBorder) )
							((JComponent)component).setBorder(null);
					} else {
						((JComponent)component).setBorder(null);
					}
				}
				if (component instanceof JScrollPane) {
					component.setBackground(new Color(240,240,240));
					((JComponent)component).setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
				}
			}
		}
	}
	
	void myStyle2(Component component) {
		
		defaultFont = new Font("Trebuchet MS", Font.PLAIN, 13);
		// customFontName = "Bitstream Vera Sans Mono";
		// customFontName = "Trebuchet MS";
		// customFontName = "Andover";
		// customFontName = "Android Nation";
		// customFontName = "Arioso";
		// customFontName = "Courier New";
		// customFontName = "Neuropol";
		// customFontName = "OCR A Extended";
		customFontName = "Oloron Tryout";
		Font customFont = new Font(customFontName, Font.PLAIN, 13);

		if (component instanceof JTextComponent || component instanceof JTable  || component instanceof JSpinner) {
			component.setFont(defaultFont);
			component.setForeground(Color.DARK_GRAY);
			component.setBackground(new Color(251,251,251));
			
			 Border borderTemp = ((JComponent)component).getBorder();
			 if (borderTemp instanceof TitledBorder) {
			 	((TitledBorder)borderTemp).setBorder( BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.ORANGE, 1), BorderFactory.createEmptyBorder(5,5,5,5)) );
			 	((JComponent)component).setBorder(BorderFactory.createCompoundBorder(borderTemp, new SoftBevelBorder(BevelBorder.LOWERED)));
			 } else {
			 	((JComponent)component).setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
			 }
			 
		} else {
			component.setFont(customFont);
			if (component instanceof JComponent && !(component instanceof JScrollPane) && !(component instanceof JViewport) && !(component instanceof JPanel) && !(component instanceof JTabbedPane) ) {
				((JComponent)component).setBorder(new SoftBevelBorder(BevelBorder.RAISED));
				if (component instanceof JButton) {
					component.setBackground(Color.ORANGE);
					component.setForeground(Color.BLUE);
					((JComponent)component).setBorder(BorderFactory.createEtchedBorder());
				}
			} else {
				if (component instanceof JPanel || component instanceof JTabbedPane) {
					component.setBackground(new Color(240,240,240));
					// ((JComponent)component).setBorder(BorderFactory.createEmptyBorder());
					if (component instanceof JPanel) {
						Border border = ((JComponent)component).getBorder();
						if ( !(border instanceof TitledBorder) )
							((JComponent)component).setBorder(null);
					} else {
						((JComponent)component).setBorder(null);
					}
				}
				if (component instanceof JViewport) {
					component.setBackground(new Color(0,40,40));
					// ((JComponent)component).setBorder(BorderFactory.createEmptyBorder());
					// ((JViewport)component).setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
				}
			}
		}
	}
	
	void plainStyle(Component component) {
		
		component.setFont(null);

		component.setBackground(null);
		component.setForeground(null);
		
		if ((component instanceof JComponent) && !(component instanceof JViewport)) {
			if (component instanceof JTextComponent) {
				((JComponent)component).setBorder( null );
			} else if (component instanceof JButton) {
				((JComponent)component).setBorder( BorderFactory.createEtchedBorder() );
				
			} else if (component instanceof JPanel) {	
				Border border = ((JComponent)component).getBorder();
				if ( !(border instanceof TitledBorder) )
					((JComponent)component).setBorder(null);
			} else {
				((JComponent)component).setBorder( null );
			}
		}

	}
	
	
	static Thread screenDraw;
	static Thread screenCatcher;
	static short screenCaptured = 0;
	static short lastShot = -1;
	static Color colorWaiter = new Color(0,150,50);
	static short colorCounter = 250;
	static short colorCounter2 = 0;
	static short pollerCounter = 0;
	final static public void screenCapture() {
		screenCapture("1000");
	}
	final static public void screenCapture(String timing) {
		if (new Long(timing).longValue()>Short.MAX_VALUE)
			timing = ""+Short.MAX_VALUE;
		
		final short time = new Short(timing).shortValue();
		final short timePoller = 100; 
		System.out.println(time);
		if (screenCatcher == null) {
			screenDraw = new Thread() {
				public void destroy() {
					screenDraw = null;
				}
				public void run() {
					Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
					int x = dim.width - 60, y = dim.height - 135, dimx = 50, dimy = 50;
					Graphics2D g2 = (Graphics2D)window.getGraphics();
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					do {
						try {
							Thread.sleep(timePoller);
							if (lastShot == -1 && lastShot != screenCaptured)
								lastShot = screenCaptured;
							
							pollerCounter += timePoller;
							
							int red = Drawer.getColor(time, pollerCounter), green = 210, blue = Drawer.getColorInverse(time, pollerCounter);

							g2.setColor(new Color(red,green,blue));
							g2.fillOval(x,y,dimx,dimy);
							g2.setColor(Color.WHITE);
							g2.drawString("#" + screenCaptured, x+16,y+28);
							
							if (screenCaptured > lastShot) {
								g2.setColor(Color.ORANGE);
								g2.fillOval(x,y,dimx,dimy);
								g2.setColor(Color.YELLOW);
								g2.drawString("#" + screenCaptured, x+16,y+28);
								lastShot = screenCaptured;
							}

						} catch ( InterruptedException e ) {
							e.printStackTrace();
						}
					}
					while (screenCatcher!=null);
					screenCaptured = 0;
					screenDraw = null;
				}
			};
			screenDraw.start();
			
			startScreenCapture(time);
		} else {
			screenCatcher.interrupt();
			window.showPreview();
		}
			
	}
	
	final static public void startScreenCapture(final short time) {
		// if (screenCatcher = null)
		screenCatcher = new Thread() {
			public void destroy() {
				screenCaptured = 0;
				screenDraw.interrupt();
				screenCatcher = null;
			}
			public void run() {
				do {
					try {
						Thread.sleep(time);
						pollerCounter = 0;
						String sequence = Text.formatNumberSequence(screenCaptured++, 3);
						Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
						BufferedImage screenShot = new Robot().createScreenCapture( new Rectangle(dim) );
						getMainLogger().setLevel(Level.INFO);
						getMainLogger().info("Writing screenshot to:" + System.getProperty("java.io.tmpdir"));
						File f = new File(System.getProperty("java.io.tmpdir")+"//"+"screenshot-" + sequence + ".png");
						javax.imageio.ImageIO.write(screenShot, "png", f);
					} catch ( HeadlessException e ) {
						e.printStackTrace();
					} catch ( AWTException e ) {
						e.printStackTrace();
					} catch ( FileNotFoundException e ) {
						e.printStackTrace();
					} catch ( IOException e ) {
						e.printStackTrace();
					} catch ( InterruptedException e ) {
						e.printStackTrace();
					}
				}
				while (screenCatcher!=null);
				screenCaptured = 0;
				screenCatcher = null;
			}
		};
		
		screenCatcher.start();
	}
	
	
	public void showPreview(){}
	
}
