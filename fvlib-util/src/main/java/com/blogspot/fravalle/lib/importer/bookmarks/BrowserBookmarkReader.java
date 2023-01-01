package com.blogspot.fravalle.lib.importer.bookmarks;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Vector;
import java.util.logging.Logger;

public class BrowserBookmarkReader {

    static final protected Logger logger = Logger.getLogger(BrowserBookmarkReader.class.getName());

    static final private Vector<String> safetyFilter;

    static {
        safetyFilter = new Vector<String>();
        //THIS IS A MALICIOUS SITE NOTIFIED BY FIREFOX AND SILENTLY SUPPRESSED BY BLOGSPOT
        safetyFilter.add("http://arts.cultural-china.com/");
    }

    static private LinkedHashMap<String, BookmarkReference> BOOKMARKS_CACHE = new LinkedHashMap<String, BookmarkReference>();

    static private EBookmarkOutputFormat ebof;

    static private Boolean checkSanity;

    static private Integer sliceIntoPieces = 1500000;

    static Vector<String> spooler = new Vector<String>();

    private LinkedHashMap<String, BookmarkReference> BOOKMARKS_SESSION = new LinkedHashMap<String, BookmarkReference>();

    private File fileChannel;

    public Boolean useAsPrimaryReference;

    private Vector<String> lines = new Vector<String>();

    private StringBuffer sbBookmark;


    public BrowserBookmarkReader(String url, Boolean useAsPrimaryReference) {
        super();
        this.useAsPrimaryReference = useAsPrimaryReference;
        File tFile = new File(url);
        if (tFile.exists() && !tFile.isDirectory()) {
            this.fileChannel = tFile;
            logger.info("Starting data import from file [" + this.fileChannel.getName() + "]");
        }
    }

    private String parseRecord(String s, LinkedHashMap<String, BookmarkReference> allBks) {

        //logger.finest("Columns [" + rt.key + "]");

        return null;
    }

    public static void writeSpooler() throws IOException {
        Integer aboutTotalAmountOfBytes = 0;

        ///////==>FROM HERE

        //File fOut = new File("./bookmarks-fv.html");
        //FileOutputStream fos = new FileOutputStream(fOut, false);

        String attachedScript ="" +
                "<style type=\"text/css\">.hiddenCat {display: none;}</style>" +
                "<script>" +
                "function showBookmarks(id) { alert(id); document.getElementById('p_'+id).style.display='block'; }" +
                "function showBookmarksByClassName(id) { var domExpander = document.getElementById(id); if (domExpander.innerText=='[+]') domExpander.innerText = '[-]'; else domExpander.innerText = '[+]'; " +
                "var doms = document.getElementsByClassName('p_'+id); /*alert(doms.length);*/ " +
                "for ( dom of doms) { /*alert(dom.style.display);*/ dom.style.display= dom.style.display=='none'||dom.style.display=='' ? 'block' : 'none' ; }" +
                "}" +
                "</script>";

        if (ebof.equals(EBookmarkOutputFormat.WEBPAGE)) {
            //fos.write("<html><head>".getBytes());
            spooler.add("<html><head>");
        }
//        if (ebof.equals(EBookmarkOutputFormat.WEBPAGE) || ebof.equals(EBookmarkOutputFormat.WEBFRAGMENT))
//            fos.write(attachedScript.getBytes());
        if (ebof.equals(EBookmarkOutputFormat.WEBPAGE)) {
            //fos.write("</head><body>".getBytes());
            spooler.add("</head><body>");
        }
        if (ebof.equals(EBookmarkOutputFormat.WEBPAGE) || ebof.equals(EBookmarkOutputFormat.WEBFRAGMENT)) {
            //fos.flush();
        }

        //fos.write("<DIV id=\"bkContainer\">".getBytes());
        spooler.add("<DIV id=\"bkContainer\">");

        String lastCat = "";

        for (String k : BOOKMARKS_CACHE.keySet()) {
            logger.info("IMPORTED KEY ID:\t" + k);
            logger.finer("IMPORTED KEY PATH:\t" + BOOKMARKS_CACHE.get(k).categoryPath);
            logger.finest("IMPORTED KEY VALUE:\t" + BOOKMARKS_CACHE.get(k).values);

            String catId = BOOKMARKS_CACHE.get(k).categoryPath.hashCode()+"";
            String catName = BOOKMARKS_CACHE.get(k).categoryPath.toString();
            String bkStatus = BOOKMARKS_CACHE.get(k).ebs.toString();

            String bookmarkOutput = "";

            if (BOOKMARKS_CACHE.get(k).key.startsWith("http")) {
                String[] pieceOfUrl1 = BOOKMARKS_CACHE.get(k).key.split("://");
                String bookmarkSkeleton = "<DIV class=\"hiddenCat p_id%s\" alt=\"%s\"><A TARGET=\"_new\" onclick=\"inMemoryOfNavigator('%s','%s')\" >%s (%s)</A></DIV>\n";
                bookmarkOutput = String.format(bookmarkSkeleton, catId, catName, pieceOfUrl1[0], pieceOfUrl1[1], BOOKMARKS_CACHE.get(k).values.get("LABEL"), bkStatus);
            }


            String catOutputSkeleton = "<DIV><SPAN id=\"id%s\" onclick=\"showBookmarksByClassName(this.id)\">[+]</SPAN> <SPAN onclick=\"showBookmarksByClassName('id%s')\">%s</SPAN>" +
                    "<SPAN onclick=\"showUnavailable('id%s')\" alt='Show/Hide unavailable bookmarked sites'>-o-</SPAN>" +
                    "</DIV>";
            String catOutput = String.format(catOutputSkeleton, catId, catId, catName, catId);

            if (!lastCat.equals(catName)) {
                //fos.write(catOutput.getBytes());
                spooler.add(catOutput);
                lastCat = catName;
                aboutTotalAmountOfBytes += catOutput.length();
            }

            if (BOOKMARKS_CACHE.get(k).key.startsWith("http")) {
                //fos.write(bookmarkOutput.getBytes());
                spooler.add(bookmarkOutput);
                aboutTotalAmountOfBytes += bookmarkOutput.length();
            }
            //fos.flush();

        }

        //fos.write("</DIV>".getBytes());
        spooler.add("</DIV>");

        if (ebof.equals(EBookmarkOutputFormat.WEBPAGE) || ebof.equals(EBookmarkOutputFormat.WEBFRAGMENT)) {
            //fos.write(attachedScript.getBytes());
            spooler.add(attachedScript);
            aboutTotalAmountOfBytes += attachedScript.length();
        }

        if (ebof.equals(EBookmarkOutputFormat.WEBPAGE)) {
            //fos.write("</body></html>".getBytes());
            spooler.add("</body></html>");
        }

        /*if (ebof.equals(EBookmarkOutputFormat.WEBPAGE))
            fos.flush();*/

        //fos.close();

        logger.info("ABOUT TOTAL AMOUNT OF BYTES="+aboutTotalAmountOfBytes);


        ///////==>UNTIL HERE

        aboutTotalAmountOfBytes = 0;
        Integer totalAmountOfBytes = 0;
        Integer slicerCounter = 1;

        //File fOut = new File("./bookmarks-fv-test.html");
        File fOut = new File("./bookmarks-fv-test_"+slicerCounter+".html");
        FileOutputStream fos = new FileOutputStream(fOut, false);

        for (String ln : spooler) {
            aboutTotalAmountOfBytes += ln.length();
            totalAmountOfBytes += ln.length();
            fos.write(ln.getBytes());
            fos.flush();
            if (aboutTotalAmountOfBytes > sliceIntoPieces) {


                logger.info("REACHED! SIZE LIMIT ["+aboutTotalAmountOfBytes+"] ON: " + ln);

                fos.flush();
                fos.close();

                fos = null;

                slicerCounter += 1;

                aboutTotalAmountOfBytes = 0;

                fOut = new File("./bookmarks-fv-test_"+slicerCounter+".html");
                fos = new FileOutputStream(fOut, false);
            }

        }

        fos.close();

        logger.info("==> TOTAL AMOUNT OF BYTES IN SPOOLING="+totalAmountOfBytes);

    }

    public void importData() throws IOException {
        logger.info("Start data import");

        Vector<String> currentCategoryPath = new Vector<String>();

        BufferedReader br = new BufferedReader(new FileReader(fileChannel));
        String buffer;

        while ((buffer = br.readLine()) != null) {
            lines.add(buffer);
        }

        br.close();

        logger.info( String.format("Total lines to import: %s", lines.size()));

        Integer idx = 0;

        for (String line : lines) {

            if ( line.toUpperCase().trim().startsWith("<DT><A") && line.toUpperCase().trim().endsWith("</A>") ) {
                //eseguo il parsing della linea creando il bookmark reference
                Vector<String> myCategoryPath = new Vector<String>();
                myCategoryPath.addAll(currentCategoryPath);
                BookmarkReference bk = new BookmarkReference(fileChannel, line, this.useAsPrimaryReference, myCategoryPath, checkSanity);
                //aggiungo il bookmark reference all'elenco univoco di bookmark reference
                if (safetyFilter.toString().contains( bk.key ) ) {
                    //skip for safety reason
                    continue;
                }
                if (!BOOKMARKS_CACHE.containsKey(bk.key)) {
                    if (this.useAsPrimaryReference) {
                        BOOKMARKS_CACHE.put(bk.key, bk);
                    } else {
                        if (!bk.hasPrimaryReference)
                            BOOKMARKS_CACHE.put(bk.key, bk);
                    }
                } else {
                    String currentCat = bk.categoryPath.toString();
                    bk = BOOKMARKS_CACHE.get(bk.key);
                    bk.totalDuplicatesInTree += 1;
                    if (bk.categoryPath.toString().equalsIgnoreCase( currentCat ))
                        bk.totalDuplicatesInSameBranch += 1;
                    logger.finest("Total Duplicates in Tree: " + bk.totalDuplicatesInTree);
                    logger.finest("Total Duplicates in Same Branch: " + bk.totalDuplicatesInSameBranch);
                }
                /*if (idx>30) break;*/

            } else if ( line.toUpperCase().trim().startsWith("<DL><P>") ) {
                if (lines.elementAt(idx > 2 ? idx - 1 : idx).toUpperCase().trim().startsWith("<H1>")) {
                    idx++;
                    continue;
                }
                String categoryName = idx > 2
                        ? lines.elementAt(idx - 1).length()==0||lines.elementAt(idx - 1).lastIndexOf("\">")==-1 ? "MY CAT&DOG" : lines.elementAt(idx - 1).substring(lines.elementAt(idx - 1).lastIndexOf("\">") + 2, lines.elementAt(idx - 1).indexOf("</"))
                        : "EMPTY CAT";

                currentCategoryPath.add(categoryName);
                logger.fine( String.format("CATEGORY PATH: %s\n", currentCategoryPath) );
            } else if ( line.toUpperCase().trim().startsWith("<H1>") && line.toUpperCase().trim().endsWith("</H1>") ) {
                logger.finer("THIS IS THE ROOT CATEGORY CONTAINER");
                //isRootCategory = true;
            } else if ( line.toUpperCase().trim().startsWith("</DL><P>") ) {
                if (currentCategoryPath.size()>1) {
                    logger.finer("THIS IS AN END OF CATEGORY CONTAINER");
                    currentCategoryPath.remove(currentCategoryPath.size() - 1);
                }
            }
            idx++;

        }




    }


    public static void main(String[] args) throws IOException {

        //"/home/goldenplume/Documenti/BookmarksBackup/FV-FREE"
        if (args.length==4) {

            checkSanity = Boolean.parseBoolean(args[2]);
            sliceIntoPieces = Integer.parseInt(args[3]);

            /*System.out.println("STATUS: " + checkSanity);
            if (true)
                System.exit(0);*/

            if ( args[1].equals("web-page") ) {
                ebof = EBookmarkOutputFormat.WEBPAGE;
            } else if (args[1].equals("web-fragment")) {
                ebof = EBookmarkOutputFormat.WEBFRAGMENT;
            } else if ( args[1].equals("fragment") ) {
                ebof = EBookmarkOutputFormat.FRAGMENT;
            } else {
                ebof = EBookmarkOutputFormat.WEBPAGE;
            }

            LinkedList<BrowserBookmarkReader> linkedList = new LinkedList<BrowserBookmarkReader>();
            File tFile = new File(args[0]);
            File[] files = tFile.listFiles();
            File currentOverridesAll = null;
            if (files.length > 0) {
                File fLast = null;
                for (File f : files) {

                    try {
                        String[] ar = f.getName().split(" - ");
                        if (ar.length>1)
                            continue;
                        String firstLetterAsNumberUsedBuGDriveCaching = ar[ar.length-1].substring(0, 1);
                        logger.finest("First letter of ["+ar[ar.length-1]+"]: " + firstLetterAsNumberUsedBuGDriveCaching);
                        Short.parseShort(firstLetterAsNumberUsedBuGDriveCaching); continue;
                    } catch(Exception ex) {
                        logger.finest("The file seems to not be a GDrive caching file. Reason:\n" + ex.getMessage());
                    }

                    if (fLast==null)
                        currentOverridesAll = f;
                    else {
                        if ( currentOverridesAll.lastModified() < f.lastModified() )
                            currentOverridesAll = f;
                    }
                    logger.info("Starting data import from [" + f.getName() + "] Currently overriden by: " + currentOverridesAll.getName());
                    BrowserBookmarkReader bbr = new BrowserBookmarkReader(
                            f.getAbsolutePath(),
                            false
                    );
                    linkedList.add(bbr);
                    //bbr.importData();
                    fLast = f;
                }
            }

            for (BrowserBookmarkReader b : linkedList) {
                if (b.fileChannel.getAbsolutePath().equals(currentOverridesAll.getAbsolutePath())) {
                    b.useAsPrimaryReference = true;
                }
                logger.finest("Bookmark source used to override all others: "+ currentOverridesAll.getName() +"; Current source: [" + b.fileChannel.getName() + "]");
                b.importData();
            }

            BrowserBookmarkReader.writeSpooler();

        }

        /*
        BrowserBookmarkReader bbr1 = new BrowserBookmarkReader(
                //"/home/goldenplume/Documenti/BookmarksBackup/FV-FREE"
                "/home/goldenplume/Documenti/BookmarksBackup/FV-FREE/ESPERIA_EDGE-favorites_26_12_22.html",
                true
        );
        bbr1.importData();

        BrowserBookmarkReader bbr2 = new BrowserBookmarkReader(
                //"/home/goldenplume/Documenti/BookmarksBackup/FV-FREE"
                "/home/goldenplume/Documenti/BookmarksBackup/FV-FREE/ESPERIA_EDGE-favorites_26_12_22.html",
                false
        );
        bbr2.importData();

        BrowserBookmarkReader bbr3 = new BrowserBookmarkReader(
                //"/home/goldenplume/Documenti/BookmarksBackup/FV-FREE"
                "/home/goldenplume/Documenti/BookmarksBackup/FV-FREE/GoogleBookmarks.html",
                false
        );
        bbr3.importData();
         */
    }

}

