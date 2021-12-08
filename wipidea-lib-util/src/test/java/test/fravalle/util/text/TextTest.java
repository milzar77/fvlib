/*
 * TextTest.java - libs (libs.jar)
 * Copyright (C) 2006
 * Source file created on 19-mag-2006
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [TextTest]
 * 2DO:
 *
 */

package test.fravalle.util.text;

import java.util.Enumeration;

import junit.framework.TestCase;
import junit.framework.TestResult;

/**
 * [][TextTest]
 * 
 * <p><b><u>2DO</u>:</b>
 * 
 * 
 * @author Francesco Valle - (antares)
 */
public class TextTest extends TestCase {

	public TextTest() {
		// super();
		super("testValidaCaratteriNull");
	}
	
	public static void main(String[] args) {
		// junit.swingui.TestRunner.run(TextTest.class);
		junit.textui.ResultPrinter rp = new junit.textui.ResultPrinter(System.out);
		junit.textui.TestRunner tr = new junit.textui.TestRunner(rp);
		TextTest test = new TextTest();
		tr.startTest(test);
		// tr.doRun(test);
		TestResult report = tr.run(test);
		tr.endTest(test);
		System.out.println("=====");
		for (Enumeration e = report.failures(); e.hasMoreElements();) {
			System.out.println(e.nextElement());
		}
	}

	public final void testValidaCaratteri() {
		assertNotNull( com.blogspot.fravalle.util.text.Text.validaCaratteri("Test, 1: prova"));
	}
	
	public final void testValidaCaratteriNull() {
		assertNull( com.blogspot.fravalle.util.text.Text.validaCaratteri(null));
	}

	/*
	 * Class under test for Configuration creaConfigurazione()
	 */
	public final void testCreaConfigurazione() {
		//2DO Implement creaConfigurazione().
	}

	public final void testTrim() {
		//2DO Implement trim().
	}

	public final void testIsRounded() {
		//2DO Implement isRounded().
	}

}
