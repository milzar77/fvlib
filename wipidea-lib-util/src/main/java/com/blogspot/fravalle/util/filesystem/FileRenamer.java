/*
 * FileRenamer.java - Weev Utility Library package (weev-lib.jar)
 * Copyright (C) 2 novembre 2002 Francesco Valle <info@weev.it>
 * http://www.weev.it
 *
 */

package com.blogspot.fravalle.util.filesystem;

import java.io.IOException;

public class FileRenamer {

    static String[] arrayCounter;
    static String[] customMessages      = new String[3];
    static String totalZero             =   "";
    static int capacity                 =   0;
    static boolean isMoreThanTwoDigit   = false;
    static boolean isOutput             = true;

    int howManyZero  =   0;
    
    /*
    
    Provare anche con questi 2 metodi:
    
    1	000	000	+
    		  1	=
    ---------------------
    1	000	001
    
    substring(1,String.length) = 000001
    
    Oppure con una divisione ed un successivo spostamento della virgola
    	
    
    */
    
    String prefix = "";
    
    public void init() {
        customMessages[0] = "\r\n\tChe cavolo vuoi combinare?";
        customMessages[1] = "\r\n\tStai esagerando.";
        customMessages[2] = "\r\n\tOra vattene!";
        
    }
    
    
    
    public FileRenamer(int capacity, String prefix, boolean isOutput) {
        FileRenamer.capacity   =   capacity;
        FileRenamer.isOutput   =   isOutput;
        init();
    }
    

    static public void makeCounter(int howManyZero, boolean isSimpleDigit) throws IOException {
        
        String[] tmp    = new String[capacity];
        
        arrayCounter    = tmp;
        
        totalZero       = makeZero(howManyZero);
        
        for (int i = 0; i < capacity; i++) {
            
            if (!isSimpleDigit) {
                
                int refDigit = new Integer(i).toString().length();
                
                if ( refDigit == 1 ) arrayCounter[i] = totalZero + i;
                
                if ( refDigit == 2 ) arrayCounter[i] = totalZero.substring(0, (totalZero.length()-1) ) + i;
                
                if ( refDigit == 3 ) arrayCounter[i] = totalZero.substring(0, (totalZero.length()-2) ) + i;
                
                if ( refDigit == 4 ) arrayCounter[i] = totalZero.substring(0, (totalZero.length()-3) ) + i;

/*
                    if (i<=9) {
                        arrayCounter[i] = ((isMoreThanTwoDigit) ? totalZero.concat("0") : totalZero) + i;
                        // arrayCounter[i] = totalZero + i;
                    } else {
                        if (i<100) arrayCounter[i] = ((isMoreThanTwoDigit) ? totalZero : "") + i;
                        else arrayCounter[i] = ((isMoreThanTwoDigit) ? totalZero.substring(0,(totalZero.length()-1)) : "") + i;
                    }
*/
                System.out.println(arrayCounter[i] + " : Array");
            } else {
                System.out.println(i);
            }
        
        }
        
        System.out.println("\r\nLunghezza totale dell'array: " + arrayCounter.length);
        
    }
    
    static String makeZero(int idx) {

        int numberOfDigit = new Integer(capacity).toString().length();
        
        int i=0;
        
        System.out.println("str: "+numberOfDigit);
        
        String str = "";
        
        for (i = 0; i < idx; i++) str += "0";
        
        if (numberOfDigit > idx) isMoreThanTwoDigit = true;
        
        if (numberOfDigit > i) {
            if (isMoreThanTwoDigit) {
                if (isOutput) {
                    System.out.println(customMessages[0]);
                    System.out.println(
                    "\r\n\tDunque, gli zeri che vuoi aggiungere"
                    + " come prefisso sono in tutto"
                    + "\r\n\t" + (idx-1) + " cifre; le cifre del totale assegnato ("
                    + capacity + ") sono in tutto "
                    + numberOfDigit + " cifre."
                    + "\r\n\t" + "Percio', il discorso fila solo se"
                    + " imposti il primo valore boolean a"
                    + "\r\n\t" + "false."
                    + "\r\n" + customMessages[2]);
                    System.exit(1);
                }
            }
        }

        if (numberOfDigit >= 5) {
            if (capacity > 10000) {
                    System.out.println(customMessages[1] + " Quindi ti sego prima. Sorry...");
                    System.exit(1);
            }
        }
   
        
        return str;
        
    }
 
    static public String[] getArray() {
        if (arrayCounter.length>0){
            return arrayCounter;
        } else {
            arrayCounter[0] = "0";
            System.out.println("E' necessario l'oggetto principale");
            System.exit(1);
            return arrayCounter;
        }
    }
    
}

/*

// esempio utilizzo della classe FileRenamer

import com.blogspot.fravalle.lib.util.FileRenamer;

import java.io.IOException;

// java Prova true  false   9050  3   "Prova"
        // Esegue un elenco 00001 - 09050
// java Prova false false   9050  2   "Prova"
        // Esegue un elenco 0001 -  9050

public class Prova {

    static boolean isOutput = false;
    static boolean isSimpleDigit = false;

    public static void main (String arguments []) throws IOException {
        
        isOutput        =   new Boolean(arguments[0]).booleanValue();
        isSimpleDigit   =   new Boolean(arguments[1]).booleanValue();
        
        for (int i = 0; i < arguments.length; i++) {
            System.out.println(arguments[i]);
        }
        
        if (isSimpleDigit) {
            System.out.println("\r\n\tDunque, vatteli a scrivere a mano 'sti numeri!!!");
            System.exit(0);
        }
        
        int capacity = Integer.parseInt(arguments[2]);
        int numberOfDigit = Integer.parseInt(arguments[3]);

        if (capacity>100 && numberOfDigit == 0) {
            System.out.println("\r\n\tNon so cosa vuoi combinare..."
            +"\r\n\tMa fa lo stesso perche' ti butto fuori comunque!!!"
            +"\r\n\t"
            +"\r\n\t[Prova a impostare il totale di posizioni ad un"
            +"\r\n\tnumero minore o uguale a 100. Io comunque non ti"
            +"\r\n\tho detto niente...]"
            );
            System.exit(0);
        }
        
        FileRenamer fr = new FileRenamer(capacity,"rzr", isOutput);

        fr.makeCounter((numberOfDigit+1), isSimpleDigit);

        System.out.println("\r\n" + fr.getArray()[0]);
       
        System.exit(0);
        
    }
}

*/
