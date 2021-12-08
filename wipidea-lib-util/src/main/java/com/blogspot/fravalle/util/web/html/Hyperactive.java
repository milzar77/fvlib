/*
 * Hyperactive.java - Weev Utility Library package (weev-lib.jar)
 * Copyright (C) 2 novembre 2002 Francesco Valle <info@weev.it>
 * http://www.weev.it
 *
 */

package com.blogspot.fravalle.util.web.html;

import javax.swing.*;
import javax.swing.text.html.*;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import java.net.*;
import java.io.*;


public class Hyperactive implements HyperlinkListener {

	private static String CSS_URL;
	
	public static HTMLEditorKit getCSSKit(String cssUrl) throws MalformedURLException, IOException  {
		
		CSS_URL = cssUrl;

		StyleSheet ss = getStyleSheet( new URL(cssUrl) );
		HTMLEditorKit editorKit = new HTMLEditorKit();
		if (cssUrl!=null && !cssUrl.equals("")) 
			editorKit.setStyleSheet(ss);
		
		return editorKit;

	}
	
	private static StyleSheet getStyleSheet(URL cssUrl)
	throws MalformedURLException, IOException  {

		StyleSheet ss = null;
		BufferedReader br = new BufferedReader( new InputStreamReader(cssUrl.openStream()) );
		ss = new StyleSheet();
		ss.loadRules(br, cssUrl);
		br.close();
		return ss;
	}


	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			JEditorPane pane = (JEditorPane) e.getSource();

			if (e instanceof HTMLFrameHyperlinkEvent) {

				HTMLFrameHyperlinkEvent  evt = (HTMLFrameHyperlinkEvent)e;

				try {
					StyleSheet ss = getStyleSheet( new URL(CSS_URL) );
					HTMLEditorKit editorKit = new HTMLEditorKit();
					editorKit.setStyleSheet(ss);
					HTMLDocument doc = (HTMLDocument) editorKit.createDefaultDocument();

					doc.processHTMLFrameHyperlinkEvent(evt);
				} catch (Exception ex) {}

			}
			else {
				try {
					StyleSheet ss = getStyleSheet( new URL(CSS_URL) );
					HTMLEditorKit editorKit = new HTMLEditorKit();
					editorKit.setStyleSheet(ss);

					pane.setEditorKit(editorKit);

					pane.setPage(e.getURL());

				} catch (Throwable t) {
					t.printStackTrace();
				}

			}
		} /* else {

		} */
	}
}
