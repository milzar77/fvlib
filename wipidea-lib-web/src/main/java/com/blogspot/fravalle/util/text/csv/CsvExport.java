/*
 * CsvExport.java - testing (testing.jar)
 * Copyright (C) 2005
 * Source file created on 14-giu-2005
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [CsvExport]
 * TODO:
 *
 */

package com.blogspot.fravalle.util.text.csv;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

/**
 * @author antares
 */
public class CsvExport extends Csv {
	
	private Vector vtRows = new Vector();
	private Connection conn = null;
	
	// private int laSoglia = 10;
	private final static int CHANNEL_IN = 0;
	private final static int CHANNEL_DB = 1;
	private final static int CHANNEL_DOH = 2;
	

	public CsvExport(int CHANNEL) throws Exception {
		// if (CHANNEL == CHANNEL_DB)
		// this.laSoglia = soglia;
		
		try {
			connectionOpen();
			prendiRisultatiAndIncapsulaInAscteibolDentroVettore();
		}
		catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			connectionClose();
		}
	}
	
	public CsvExport() {
		Hashtable ht = new Hashtable();
		ht.put("k1", "pippo");
		ht.put("k2", "pluto");
		ht.put("k3", "topolino");
		add( ht );
		ht = new Hashtable();
		ht.put("y1", "paperino");
		ht.put("y2", "paperoga");
		ht.put("y3", "gastone");
		ht.put("y4", "paperone");
		ht.put("y5", "rockerduck");
		add( ht );
		ht = new Hashtable();
		ht.put("x1", "paperinik");
		ht.put("x1", "superpippo");
		add( ht );
		ht = new Hashtable();
		ht.put("z1", "ciccio");
		ht.put("z2", "nonna papera");
		add( ht );
	}
	
	private void prendiRisultatiAndIncapsulaInAscteibolDentroVettore() throws SQLException {
		PreparedStatement pstmt = conn.prepareStatement("select * from articoli where tema = 'economia'");
		ResultSet rs = pstmt.executeQuery();
		ResultSetMetaData rsmd = rs.getMetaData();
		while (rs.next()) {
			Hashtable ht = new Hashtable();
			for ( int i = 0; i < rsmd.getColumnCount(); i++) {
				int sqlCounter = i+1;
				String s = rsmd.getColumnTypeName(sqlCounter);
				ht.put( rsmd.getColumnName(sqlCounter), String.valueOf(rs.getObject(sqlCounter)) );
				
			}
			add( (Hashtable)ht );
		}
		System.out.println( "Dimensione del vettore: " + size() );
	}
	
	/**
	 * 
	 * Metodo di esportazione dati in formato csv
	 * 
	 * @param sFileName
	 */
	private void exportAll(String sFileName) throws FileNotFoundException, IOException {
		File f = new File( sFileName );
		try {
			FileOutputStream fos = new FileOutputStream(f);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			
			for (int i = 0; i < size(); i++ ) {
				Object obj = elementAt(i);
				String sOut = "";
				if (obj instanceof Hashtable)
					sOut = super.readLineFromHashtable((Hashtable)obj);
				bos.write( ( sOut ).getBytes() );
			}
			
			bos.flush();
			bos.close();
			fos.flush();
			fos.close();
		}
		catch ( Exception e ) {
			e.printStackTrace(System.err);
		} finally {
			// FileInputStream fis = new FileInputStream(f);
			// BufferedInputStream bis = new BufferedInputStream(fis); 
			
			BufferedReader br = new BufferedReader(new FileReader(f));
			String s = "";
			while( (s = br.readLine()) != null ) {
				// bis.read() != -1 ) {
				System.out.println( s );
			}
			
			br.close();
			// bis.close();
			// fis.close();
		}
		
	}
	
	/**
	 * 
	 * Metodo di esportazione dati paginati in formato csv
	 * 
	 * @param sFileName
	 */
	private void export(String sFileName, List pagedData) throws FileNotFoundException, IOException {
		File f = new File( sFileName );
		try {
			FileOutputStream fos = new FileOutputStream(f);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			
			for (int i = 0; i < pagedData.size(); i++ ) {
				Object obj = pagedData.get(i);
				String sOut = "";
				if (obj instanceof Hashtable)
					sOut = super.readLineFromHashtable((Hashtable)obj);
				bos.write( ( sOut ).getBytes() );
			}
			bos.flush();
			bos.close();
			fos.flush();
			fos.close();
			System.out.println( "Dimensione del vettore: " + pagedData.size() );
			
		}
		catch ( Exception e ) {
			e.printStackTrace(System.err);
		} finally {
			// FileInputStream fis = new FileInputStream(f);
			// BufferedInputStream bis = new BufferedInputStream(fis); 
			
			BufferedReader br = new BufferedReader(new FileReader(f));
			String s = "";
			while( (s = br.readLine()) != null ) {
				// bis.read() != -1 ) {
				System.out.println( s );
			}
			
			br.close();
			// bis.close();
			// fis.close();
		}
		
	}
	
	private void exportPages(int soglia) throws Exception {
		// allineare parametri di paginazione con parametri di scorrimento
		int resultCounter = 1;
		int pageCounter = 1;
		for (int i = 0; i < size(); i++) {
			int remaining = soglia-1;			
			if (resultCounter == soglia || ((size()-i) - remaining) < 0) {
				export(".//export//sFileName_"+pageCounter+".txt", subList((i+1)-soglia, i));
				System.out.println("Page " + pageCounter + " exported.");
				resultCounter = 1;
				pageCounter++;
			}
			resultCounter++;
		}
	}

	private void connectionOpen() throws SQLException, ClassNotFoundException {
		// conn = DriverManager.getConnection(URL, user, password);
		
		if (conn == null) {
			Class.forName("org.gjt.mm.mysql.Driver");
			conn = DriverManager.getConnection( "jdbc:mysql://db.orion.lan:3306/archivio?user=madsql&password=onemad" );
		}
		System.out.println("Apertura connessione a db");
	}
	
	private void connectionClose() throws SQLException {
		if (conn != null)
			conn.close();
		System.out.println("Chiusura connessione a db");
	}
	
	public final static void main(String[] args) throws IOException, Exception {
		
		CsvExport cvsExport = new CsvExport(CHANNEL_DB);
		// CsvExport cvsExport = new CsvExport();
		// cvsExport.exportAll( ".//export.csv" );
		cvsExport.exportPages( 6 );
		System.out.println( "Dimensione del vettore: " + cvsExport.size() );
		
	}
}
