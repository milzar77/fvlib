/*
 * DirectoryScanner.java - Weev Utility Library package (weev-lib.jar)
 * Copyright (C) 2 novembre 2002 Francesco Valle <info@weev.it>
 * http://www.weev.it
 *
 */
 
/*
creare routine in grado di approfondire l'albero della directory padre
*/

package com.blogspot.fravalle.util.filesystem;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import com.blogspot.fravalle.lib.monitor.MainLogger;


public class DirectoryScanner {

    private static File f;

    static String dirPath           =   "";
    
    Hashtable directories    =   new Hashtable();
    Hashtable files          =   new Hashtable();
    
    static boolean isOutput =   false;
  

// CONSTRUCTORS

    public DirectoryScanner(String dirPath, boolean isOutput) {
        DirectoryScanner.dirPath        =   dirPath;
		DirectoryScanner.isOutput       =   isOutput;
    }

    public DirectoryScanner(String dirPath) {
		DirectoryScanner.dirPath        =   dirPath;
    }
    
    

// PUBLIC METHODS

    public void printList(Hashtable hs, String str, String nodeName, boolean printLabel) throws IOException {

	if (printLabel) MainLogger.getLog().fine(str+":");
	for (Enumeration e = hs.keys(); e.hasMoreElements();) {
		String keyName = (String) e.nextElement();
		MainLogger.getLog().fine( nodeName + "="+keyName+";path=" + hs.get(keyName) );
		
	}
    }

// RETURN METHODS

    public boolean testDirectory() throws IOException {
        f = new File(dirPath);
        if (f.exists()) return true;
        else return false;
 
    }

    public Hashtable getDirs() {
        return directories;
    }

    public Hashtable getFiles() {
        return files;
    }


// SYNCHRONIZED METHODS

    synchronized public void makeDirectoryList(String wildcard) throws IOException {
	directories.clear();
	files.clear();

	f = new File(dirPath);
            
        for (int i=0;i<f.list().length;i++) {
            File d = null;

            String objName = f.getAbsolutePath() +"/"+ f.list()[i];
           
            d = new File(objName);
            if (d.isDirectory()) {
            	directories.put(d.getName(), (String) d.getAbsolutePath());
            } else {
            	String fileName = d.getName();
            	if (fileName.indexOf(wildcard)!=-1) 
            		files.put(d.getName(), (String) d.getAbsolutePath());
            }
        }
    }
    
}

/*

// esempio utilizzo della classe DirectoryScanner

import com.blogspot.fravalle.lib.util.DirectoryScanner;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;
import java.util.StringTokenizer;


public class ScanDirectory {


    static DirectoryScanner[] allDl = new DirectoryScanner[100];
    static protected Hashtable hsMain = new Hashtable();
    
    static String srcPath;
    static String wildcard;


    public static void main (String arguments []) throws IOException {
        
        srcPath =   arguments[0];
        wildcard =   arguments[1];
        if (wildcard==null) wildcard=".";

	DirectoryScanner dl = new DirectoryScanner(srcPath);

	if (dl.testDirectory()) {

		dl.makeDirectoryList(wildcard);
		
		Hashtable hsDirs = (Hashtable) dl.getDirs();
		Hashtable hsFiles = (Hashtable) dl.getFiles();
	
		dl.printList(hsDirs, "Directories", "directory", true);

		dl.printList(hsFiles, "Files", "file", true);
		
		int counter = 0;
		for (Enumeration e = hsDirs.keys(); e.hasMoreElements();) {
			String keyName = (String) e.nextElement();
			if (counter<allDl.length) {
				allDl[counter] = new DirectoryScanner((String) hsDirs.get(keyName));
				allDl[counter].makeDirectoryList(wildcard);
	
				Hashtable hsSubDir = (Hashtable) allDl[counter].getDirs();
				Hashtable hsSubDirFiles = (Hashtable) allDl[counter].getFiles();
				allDl[counter].printList(hsSubDir, "Directories", "directory", false);
				allDl[counter].printList(hsSubDirFiles, "Files", "file", false);
				counter++;
			}
		}

		for (Enumeration e = hsDirs.keys(); e.hasMoreElements();) {
			String keyName = (String) e.nextElement();
			DirectoryScanner tdSub = new DirectoryScanner((String) hsDirs.get(keyName));
			tdSub.makeDirectoryList(wildcard);
			Hashtable hsSubDir = (Hashtable) tdSub.getDirs();
			Hashtable hsSubDirFiles = (Hashtable) tdSub.getFiles();

			hsMain.put(keyName, (Hashtable) hsSubDir);
		}
	
		for (Enumeration e = hsMain.keys(); e.hasMoreElements();) {
			String keyName = (String) e.nextElement();
		}
	} else {
	}
	
        System.exit(0);
        
    }
}

*/

