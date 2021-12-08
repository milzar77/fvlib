/*
 * Drawer.java - jpovnet (jpovnet.jar)
 * Copyright (C) 2006
 * Source file created on 11-feb-2006
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [Drawer]
 * 2DO:
 *
 */

package com.blogspot.fravalle.util.gfx;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * @author francesco
 */
abstract public class Drawer {
	
	int pointCounter = 0;
	
	final static public void painter(Object o, int pointCounter, boolean useParent) {
		if (o instanceof JComponent) {
			if (useParent)
				painter( (Graphics2D) ((JComponent)o).getParent().getGraphics(), pointCounter, ((JComponent)o).getHeight() );
			else
				painter( (Graphics2D) ((JComponent)o).getGraphics(), pointCounter, ((JComponent)o).getHeight() );
		} else if (o instanceof Graphics2D)
			painter( (Graphics2D) o, pointCounter, 200 );
		else if (o instanceof Graphics)
			painter( (Graphics2D) o, pointCounter, 200 );
	}
	
	final static public void painter(JComponent c) {
		painter( (Graphics2D)c.getGraphics() , 0, 200);
	}
	
	final static public void painter(Graphics g) {
		painter((Graphics2D)g, 0, 200);
	}
	
	static int colorCounter = 1;
	static int colorCounterMax = 254;
	
	static int pointCounterMax = Integer.MAX_VALUE;
	
	final static public void painter(Graphics2D gg, int pointCounter, int limit) {
		if (pointCounterMax==0)
			pointCounterMax = pointCounter+1-limit;
		
		Color color = Color.ORANGE;
		if (colorCounter > 0 && colorCounter < 255)
			color = new Color(255,colorCounter++,155);
		else {
			if (colorCounterMax > 0) {
				color = new Color(255,colorCounterMax--,155);
			} else {
				colorCounter = 1;
				colorCounterMax = 254;
				color = new Color(255,colorCounter,155);
			}
		}
		/*
		if (pointCounter >1 && pointCounter < limit)
			color = Color.YELLOW;
		else {
			if (pointCounterMax > 0)
				color = Color.BLACK;
		}
		*/
			
		gg.setColor(color);
		
		gg.setBackground(Color.YELLOW);
		
		//gg.drawString("Rendering in progress...",  10, 10);
		gg.drawLine(
				(pointCounter > 0 && pointCounter <= limit) ? ++pointCounter :
					--pointCounter 
				,
				0,
				0,
				(pointCounter > 0 && pointCounter <= limit) ? ++pointCounter :
					--pointCounter
		);
		
	} 
	
	
	static public final int WIDTH = 0;
	static public final int HEIGHT = 1;

	static public int[] mantainVgaRatio(String dim, int dimSide) {
		int intDim = new Integer(dim).intValue();
		return mantainVgaRatio(intDim, dimSide);
	}
	
	static public int[] mantainVgaRatio(int dim, int dimSide) {
		int[] dims = new int[2];
		double ratio = 640.0/480.0; 
		if (dimSide == 0) {
			double newDim = dim / ratio;
			dims[0] = dim;
			dims[1] = new Double(newDim).intValue();
		} else if (dimSide == 1) {
			double newDim = dim * ratio;
			dims[0] = new Double(newDim).intValue();
			dims[1] = dim;
		}
		return dims;
	}
	
	static public int[] mantainRatio(int w, int h, int newDim, int dimSide) {
		int[] dims = new int[2];
		double ratio = new Double(w).doubleValue()/new Double(h).doubleValue(); 
		if (dimSide == 0) {
			double nDim = newDim / ratio;
			dims[0] = newDim;
			dims[1] = new Double(nDim).intValue();
		} else if (dimSide == 1) {
			double nDim = newDim * ratio;
			dims[0] = new Double(nDim).intValue();
			dims[1] = newDim;
		} else {
			dims[0] = w;
			dims[1] = h;
		}
		return dims;
	}

	final static private void _drawInfo(Graphics2D g2, Object[] info, JComponent observer, boolean useAntialias) {
		if (useAntialias)
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setBackground(Color.WHITE);
		g2.setColor(Color.WHITE);	
		
		for (int i = 0; i < info.length; i++) {
			if (info[i] instanceof Image) {
				Rectangle2D rect = new Rectangle2D.Float();
				rect.setRect(0,0,((Image)info[i]).getWidth(observer),((Image)info[i]).getHeight(observer));
				// g2.setClip(rect);
				g2.drawImage((Image)info[i], 0,0, observer);
			} else if (info[i] instanceof String)
				g2.drawString(info[i].toString(),5,i*15);			
		}
	}
	
	static final private void drawInfo(Graphics2D g2, Object[] info, JComponent observer, boolean useAntialias) {
		_drawInfo(g2, info, observer, useAntialias);
	}
	
	static final public void drawInfo(Graphics2D g2, Object[] info, boolean useAntialias) {
		_drawInfo(g2, info, new JPanel(), useAntialias);
	}
	
	static final public void drawInfo(Graphics g, Object[] info, boolean useAntialias) {
		_drawInfo((Graphics2D)g, info, new JPanel(), useAntialias);
	}

	static final public void drawInfo(Graphics g, Object[] info, JComponent observer, boolean useAntialias) {
		_drawInfo((Graphics2D)g, info, new JPanel(), useAntialias);
	}

	/*
	static final public void drawInfo(ImageIcon icon, Object[] info, JComponent observer, boolean useAntialias) {
		BufferedImage bImage = new BufferedImage(icon.getIconWidth(),icon.getIconHeight(),BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = (Graphics2D)bImage.getGraphics();
		g2.drawImage(icon.getImage(), 0,0, observer);
		_drawInfo(g2, info, observer, useAntialias);
		icon.setImage(bImage);
	}
	
	static final public void drawInfoOnImage(BufferedImage bImage, Image img, Object[] info, JComponent observer, boolean useAntialias) {
		Graphics2D g2 = (Graphics2D)bImage.getGraphics();
		g2.drawImage(img, 0,0, observer);
		_drawInfo(g2, info, observer, useAntialias);
	}
	*/
	
	
	final static public int getColor(short totTime, short time) {
		int i = (255*time) / totTime; 
		if (i>255)
			i = 254;
		return i;
	}
	
	final static public int getColorInverse(short totTime, short time) {
		int i = (255*time) / totTime;
		i = 255 - i;
		if (i<0)
			i = 0;
		return i;
	}
	
}
