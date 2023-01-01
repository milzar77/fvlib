package com.blogspot.fravalle.lib.data.xml;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.Vector;

public class BookmarkReference {


    public final String sourceBookmarkFilePath;
    public final String sourceBookmarkFileName;

    public Boolean hasPrimaryReference;
    public final String source;

    public String key;
    public LinkedHashMap<String, String> values;

    public Integer totalDuplicatesInTree;

    public Integer totalDuplicatesInSameBranch;
    public Vector<String> categoryPath;

    public EBookmarkStatus ebs;

    public BookmarkReference(File sourceBookmarkFile, String s, Boolean hasPrimaryReference, Vector<String> currentCategoryPath, Boolean checkSanity) {
        this.sourceBookmarkFilePath = sourceBookmarkFile.getPath();
        this.sourceBookmarkFileName = sourceBookmarkFile.getName();
        this.hasPrimaryReference = hasPrimaryReference;
        this.source = s;
        this.categoryPath = currentCategoryPath;
        this.totalDuplicatesInTree = 0;
        this.totalDuplicatesInSameBranch = 0;
        this.ebs = EBookmarkStatus.ONLINE;
        this.readKey();
        this.readValues();

        if (checkSanity)
            this.checkSanity();
    }

    private void checkSanity() {
        if (this.totalDuplicatesInTree>0)
            return;
        URL url = null;
        try {
            url = new URL(this.key);

            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setConnectTimeout(5000);
            httpConn.setReadTimeout(2000);

            System.out.println("HTTP CONN URL: [" + url.toString() + "]");
            System.out.println("HTTP CONN STATUS: " + httpConn.getResponseCode());
            System.out.println("HTTP CONN MESSAGE: " + httpConn.getResponseMessage());
            //StringReader isr = new StringReader();
            InputStream is = (InputStream) httpConn.getContent();
            Scanner s = new Scanner(is).useDelimiter("\\A");
            String result = s.hasNext() ? s.next() : "";

            Boolean potential404 = (result.indexOf("\"404\"") != -1
                    || result.indexOf("'404'") != -1
                    || result.indexOf(">404<") != -1
                    || result.matches(".*\\b404\\b.*"));
            //System.out.println("HTTP CONN CONTENT: [" + result + "]");
            System.out.println("HTTP CONN POTENTIAL 404: [" + potential404
                    + "]");
            httpConn.disconnect();
            if (httpConn.getResponseCode() >= 200 && httpConn.getResponseCode() < 300) {
                this.ebs = EBookmarkStatus.ONLINE;
            } else {
                if (httpConn.getResponseCode() == 404) {
                    this.ebs = EBookmarkStatus.NOT_FOUND;
                } else {
                    if (potential404)
                        this.ebs = EBookmarkStatus.POTENTIAL_NOT_FOUND;
                    else
                        this.ebs = EBookmarkStatus.CONN_EXCEPTION;
                }
            }
        } catch (java.lang.ClassCastException e) {
            this.ebs = EBookmarkStatus.CONN_CLASSCASTEXCEPTION;
        } catch (java.io.FileNotFoundException e) {
            this.ebs = EBookmarkStatus.STRANGE_FILENOTFOUND_CONN_EXCEPTION;
        } catch (javax.net.ssl.SSLHandshakeException e) {
            this.ebs = EBookmarkStatus.SSLHANDSHAKEEXCEPTION;
        } catch (java.net.ConnectException e) {
            this.ebs = EBookmarkStatus.CONN_EXCEPTION;
        } catch (java.net.UnknownServiceException e) {
            this.ebs = EBookmarkStatus.UNKNOWNSERVICEEXCEPTION;
        } catch (java.net.UnknownHostException e) {
            this.ebs = EBookmarkStatus.NOT_MORE_REACHABLE;
        } catch (MalformedURLException e) {
            this.ebs = EBookmarkStatus.MALFORMEDURLEXCEPTION;
            //throw new RuntimeException(e);
        } catch (IOException e) {
            this.ebs = EBookmarkStatus.IOEXCEPTION;
            //throw new RuntimeException(e);
        }
    }

    public void readKey() {
        int idx = 0;
        String[] rowValues = source.split("(?i)(HREF)" );
        String rawValue = rowValues[1];
        this.key = readKeyValue(rawValue);
    }

    public void readValues() {
        String[] rowValues = source.split("\\=\\\"");
        LinkedHashMap<String, String> valueMap = new LinkedHashMap<>();
        String attLabel = "", attName = "", attValue = "";
        Integer idx = 0;

        for (String sValue : rowValues) {
            if ( sValue.toUpperCase().startsWith("<DT><H3") ) {
                //is it a category?
                //valueMap.put(readValue(sValue, 0), readValue(sValue, 1));
                //this.values = valueMap;
            } else {
                if (idx>0) {
                    attName = readValueForKey(rowValues, idx);
                    attValue = readValue(sValue);
                }
            }

            //if ( sValue.toUpperCase().lastIndexOf('"') != -1 || sValue.toUpperCase().lastIndexOf('\'') != -1) {
            if (!attName.equals("")) {
                valueMap.put(attName, attValue);
                attLabel = readLabel(sValue);
                valueMap.put("LABEL", attLabel);
            }

            //}
            idx++;

        }

        //completo il riempimento dell'elenco di attributi
        //this.values.putAll(valueMap);
        this.values = valueMap;

    }

    public String readKeyValue(String s) {
        if (s.indexOf('"')!=-1 /*||s.indexOf('\'')!=-1 */ ) {
            String s1 = s.substring(2);
            String v = s1.substring(0, s1.indexOf("\" ")==-1 ? s1.length() : s1.indexOf("\" "));
            return v;
        }
        return null;
    }

    public String readKeyValue(String[] s, int idx) {
        if (s[idx+1].indexOf('"')!=-1||s[idx+1].indexOf('\'')!=-1) {
            String v = s[idx+1].substring(1, s[idx+1].lastIndexOf('\"'));
            return v;
        }
        return null;
    }

    public String readValueForKey(String[] rowValues, Integer idx) {
        String s = rowValues[idx-1];
        String pattern =  "";
        if (s.lastIndexOf("\" ")!=-1) {
            pattern = "\" ";
        } else if (s.lastIndexOf("\"")!=-1) {
            pattern = "\"";
        }
        String v = s.substring(s.lastIndexOf(pattern)+pattern.length(), s.length());
        return v;
    }

    public String readLabel(String s) {
        if (s.indexOf('>')+1==s.lastIndexOf('<') ) {
            return "MY EMPTY LABEL";
        }
        if (s.indexOf('>')<s.lastIndexOf('<') && s.indexOf('>')!=-1 )
            return s.substring( s.indexOf('>')+1, s.lastIndexOf('<') );
        else
            return "";
    }

    public String readValue(String s) {
        if (s.indexOf('"')!=-1 /*||s.indexOf('\'')!=-1*/ ) {
            //String s1 = s.substring(2);
            if (s.indexOf("\" ")!=-1) {
                String v = s.substring(0, s.indexOf("\" "));
                return v;
            } else {
                String v = s.substring(0, s.indexOf("\""));
                return v;
            }

        }
        return null;
    }

}
