package com.blogspot.fravalle.lib.generator.primes;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;

public class PrimeListGenerator {

    private Vector<BigInteger> primeSpoolCache;

    private static String fileOutputPath;
    private String fileOutputName;

    private Boolean workingOn;
    private BigInteger limitedTo;


    private FileOutputStream fos;

    private Long timeStart = 0L;

    private Long timeEnd = 0L;

    private String scanId = "DEF";

    private static Integer caching = 1000;

    private static final int LESS_THAN = -1;

    private static final int EQUAL_TO = 0;
    private static final int GREAT_THAN = 1;

    private static final int PARALLEL_COMPUTATIONAL_UNIT = 1_000_000;

    private static final long CACHE_PRIMES_SPOOLER = 1000L;

    protected static DecimalFormat NUMBER_GROUP_FORMATTER = new DecimalFormat("#,###");
    private static Integer processIndex = 0;

    public PrimeListGenerator(final String pathTid, final String id) throws FileNotFoundException {
        this.scanId = id;
        this.primeSpoolCache = new Vector<BigInteger>();
        LocalDateTime ld = LocalDateTime.now();
        if (PrimeListGenerator.fileOutputPath == null)
            PrimeListGenerator.fileOutputPath = "/tmp/primes-generator/" + ld.getYear() + "_" + ld.getMonthValue() + "_" + ld.getDayOfMonth() + "-" + ld.getHour() + "_" + ld.getMinute() + "-t" + pathTid + "/";
        this.fileOutputName = "primes-list_" + this.scanId + ".txt";
        this.workingOn = true;
        //this.limitedTo = BigInteger.valueOf(Long.MAX_VALUE);//TODO:aumentare ben oltre il limite del tipo long
        this.limitedTo = new BigInteger("1000");
        File fOutput = new File(PrimeListGenerator.fileOutputPath + this.fileOutputName);
        if (fOutput.exists()) {
            fOutput.delete();
        } else {
            boolean dirCreated = new File(PrimeListGenerator.fileOutputPath).mkdirs();
        }
        this.fos = new FileOutputStream(fOutput, true);
    }

    public void scanRealNumbers(BigInteger from, BigInteger upTo, Integer cacheSize) throws IOException {
        this.timeStart = System.currentTimeMillis();
        System.out.printf("Started time @ %s\n", LocalDateTime.now());
        Long stimaMilionataInSecondi = 1L;
        if (upTo.divide(BigInteger.valueOf(1_000_000L)).multiply(BigInteger.valueOf(stimaMilionataInSecondi)).compareTo(BigInteger.valueOf(60L)) != GREAT_THAN) {
            if (ConsoleOptions.PRINT_FLOW_STATS)
                System.out.printf("Estimated total time is %s total seconds\n", upTo.divide(BigInteger.valueOf(1_000_000L)).multiply(BigInteger.valueOf(stimaMilionataInSecondi)));
        } else if (upTo.divide(BigInteger.valueOf(1_000_000L)).multiply(BigInteger.valueOf(stimaMilionataInSecondi)).divide(BigInteger.valueOf(60L)).compareTo(BigInteger.valueOf(60L)) != GREAT_THAN) {
            if (ConsoleOptions.PRINT_FLOW_STATS)
                System.out.printf("Estimated total time is %s total minutes\n", upTo.divide(BigInteger.valueOf(1_000_000L)).multiply(BigInteger.valueOf(stimaMilionataInSecondi)).divide(BigInteger.valueOf(60L)));
        } else {
            if (ConsoleOptions.PRINT_FLOW_STATS)
                System.out.printf("Estimated total time is %s total hours\n", upTo.divide(BigInteger.valueOf(1_000_000L)).multiply(BigInteger.valueOf(stimaMilionataInSecondi)).divide(BigInteger.valueOf(60L)).divide(BigInteger.valueOf(60L)));
        }

        //if (true) return;

        this.caching = cacheSize;
        BigInteger currentEvaluatedNumber = from;
        Integer currentCaching = caching;
        while (this.workingOn) {
            if (currentEvaluatedNumber.compareTo(upTo.add(BigInteger.ONE)) == GREAT_THAN) {
                this.workingOn = Boolean.FALSE;
            } else {
                if (currentCaching-- > 0) {
                    /*System.out.printf("CACHING #%s\n", currentCaching);
                    System.out.printf("Current number: %s\n", currentEvaluatedNumber);*/
                } else {
                    /*System.out.printf("CACHE CONTENT %s\n", this.primeSpoolCache);
                    System.out.printf("Current number reached: %s\n", currentEvaluatedNumber);*/
                    for (BigInteger bi : this.primeSpoolCache) {
                        if (bi.isProbablePrime(1))
                            fos.write((bi.toString() + "\n").getBytes());
                    }
                    //fos.write(this.primeSpoolCache.toString().getBytes());
                    if (ConsoleOptions.FLUSH_AT_EVERY_CACHING)
                        fos.flush();
                    this.primeSpoolCache.clear();
                    currentCaching = caching - 1;
                }
                this.primeSpoolCache.add(currentEvaluatedNumber);
            }
            currentEvaluatedNumber = currentEvaluatedNumber.add(BigInteger.ONE);
        }
        fos.flush();
        this.timeEnd = System.currentTimeMillis();
        System.out.printf("Ended time @ %s\n", LocalDateTime.now());
        if (ConsoleOptions.PRINT_FLOW_STATS) {
            System.out.printf("Total time for #%s in milliseconds: %s\n", this.scanId, (this.timeEnd - this.timeStart));
            System.out.printf("Total time for #%s in seconds: %s\n", this.scanId, (this.timeEnd - this.timeStart) / 1000);
            System.out.printf("Total time for #%s in minutes: %s\n", this.scanId, ((this.timeEnd - this.timeStart) / 1000) / 60);
        }
    }

    public void scanRealNumbersWithoutCache(Integer procIndex, BigInteger from, BigInteger upTo, BigInteger intervalPar) throws IOException {
        this.timeStart = System.currentTimeMillis();
        Long stimaMilionataInSecondi = 1L;
        System.out.printf("Started id#%s @ time %s\n", procIndex, LocalDateTime.now());
        if (upTo.divide(BigInteger.valueOf(1_000_000L)).multiply(BigInteger.valueOf(stimaMilionataInSecondi)).compareTo(BigInteger.valueOf(60L)) != GREAT_THAN) {
            if (ConsoleOptions.PRINT_FLOW_STATS)
                System.out.printf("Estimated total time is %s total seconds\n", upTo.divide(BigInteger.valueOf(1_000_000L)).multiply(BigInteger.valueOf(stimaMilionataInSecondi)));
        } else if (upTo.divide(BigInteger.valueOf(1_000_000L)).multiply(BigInteger.valueOf(stimaMilionataInSecondi)).divide(BigInteger.valueOf(60L)).compareTo(BigInteger.valueOf(60L)) != GREAT_THAN) {
            if (ConsoleOptions.PRINT_FLOW_STATS)
                System.out.printf("Estimated total time is %s total minutes\n", upTo.divide(BigInteger.valueOf(1_000_000L)).multiply(BigInteger.valueOf(stimaMilionataInSecondi)).divide(BigInteger.valueOf(60L)));
        } else if (upTo.divide(BigInteger.valueOf(1_000_000L)).multiply(BigInteger.valueOf(stimaMilionataInSecondi)).divide(BigInteger.valueOf(60L)).divide(BigInteger.valueOf(24L)).compareTo(BigInteger.valueOf(24L)) != GREAT_THAN) {
            if (ConsoleOptions.PRINT_FLOW_STATS)
                System.out.printf("Estimated total time is %s total hours\n", upTo.divide(BigInteger.valueOf(1_000_000L)).multiply(BigInteger.valueOf(stimaMilionataInSecondi)).divide(BigInteger.valueOf(60L)).divide(BigInteger.valueOf(60L)));
        } else if (upTo.divide(BigInteger.valueOf(1_000_000L)).multiply(BigInteger.valueOf(stimaMilionataInSecondi)).divide(BigInteger.valueOf(60L)).divide(BigInteger.valueOf(24L)).divide(BigInteger.valueOf(365L)).compareTo(BigInteger.valueOf(365L)) != GREAT_THAN) {
            if (ConsoleOptions.PRINT_FLOW_STATS) {
                System.out.printf("Estimated total time for id#%s is %s total hours\n", procIndex, upTo.divide(BigInteger.valueOf(1_000_000L)).multiply(BigInteger.valueOf(stimaMilionataInSecondi)).divide(BigInteger.valueOf(60L)).divide(BigInteger.valueOf(60L)));
                System.out.printf("Estimated total time for id#%s is %s total days\n", procIndex, upTo.divide(BigInteger.valueOf(1_000_000L)).multiply(BigInteger.valueOf(stimaMilionataInSecondi)).divide(BigInteger.valueOf(60L)).divide(BigInteger.valueOf(60L)).divide(BigInteger.valueOf(24L)));
            }
        } else {
            if (ConsoleOptions.PRINT_FLOW_STATS) {
                System.out.printf("Estimated total time for id#%s is %s total days\n", procIndex, upTo.divide(BigInteger.valueOf(1_000_000L)).multiply(BigInteger.valueOf(stimaMilionataInSecondi)).divide(BigInteger.valueOf(60L)).divide(BigInteger.valueOf(60L)).divide(BigInteger.valueOf(24L)));
                System.out.printf("Estimated total time for id#%s is %s total years\n", procIndex, upTo.divide(BigInteger.valueOf(1_000_000L)).multiply(BigInteger.valueOf(stimaMilionataInSecondi)).divide(BigInteger.valueOf(60L)).divide(BigInteger.valueOf(60L)).divide(BigInteger.valueOf(24L)).divide(BigInteger.valueOf(365L)));
            }
        }

        //if (true) return;

        this.prepareTaskProgress();

        /*Integer procIndex = 0;
        procIndex += 1;*/
        String key = procIndex + "-".concat(from.toString()).concat("-").concat(upTo.toString());
        GLOBAL_PROGRESS.put(key, new HashMap<String, BigInteger>());

        BigInteger currentEvaluatedNumber = from;

        odd_divisors = new BigInteger[ConsoleOptions.UP_TO_DIVISOR];
        //to add 2
        if (ConsoleOptions.DIVIDER_BY_ODD_ENABLED) {
            Vector<BigInteger> v = new Vector<BigInteger>();
            for (int idDivisor = 3; idDivisor < ConsoleOptions.UP_TO_DIVISOR; idDivisor += 2) {
                //System.out.println("ID: " + idDivisor);
                if (idDivisor == 3)
                    v.add(BigInteger.valueOf(2));
                v.add(BigInteger.valueOf(idDivisor));
            }
            odd_divisors = v.stream().toArray(size -> new BigInteger[size]);
            System.out.printf("IDs: %s\nWith array size: %s\n", v, odd_divisors.length);
        }
        while (this.workingOn) {
            if (currentEvaluatedNumber.compareTo(upTo.add(BigInteger.ONE)) == GREAT_THAN) {
                this.workingOn = Boolean.FALSE;
            } else {
                    /*System.out.printf("CACHE CONTENT %s\n", this.primeSpoolCache);
                    System.out.printf("Current number reached: %s\n", currentEvaluatedNumber);*/
                //for (BigInteger bi : this.primeSpoolCache) {
                //for (int reverseIdx = this.primeSpoolCache.size() - 1; reverseIdx > 0; reverseIdx--) {
                if ( this.candidateHasBasePrimeRequirements(currentEvaluatedNumber)  ) {
                    BigInteger bi = currentEvaluatedNumber;//this.primeSpoolCache.get(reverseIdx);
                    //System.out.printf("Number spooler size: %s\n", this.primeSpoolCache.size());
                    if (bi.isProbablePrime(1)) {
                        fos.write((bi.toString() + "\n").getBytes());
                        //System.out.printf("Estimated prime number [%s] found!\n", NUMBER_GROUP_FORMATTER.format(bi));
                        String s = null;
                        switch (ConsoleOptions.PRINT_FLOW) {
                            case ConsoleOptions.PRINT_FLOW_PRIMENUMBERS:
                                s = ConsoleOptions.FORMAT_WITH_GROUPING ? NUMBER_GROUP_FORMATTER.format(bi) : bi.toString();
                                //s = ConsoleOptions.FORMAT_WITH_LITERAL_SIZE ? this.formatWithLiteralSize(s) : s;
                                s = "[" + (ConsoleOptions.FORMAT_WITH_LITERAL_SIZE ? this.formatWithLiteralSize(bi, ConsoleOptions.FORMAT_WITH_GROUPING, ConsoleOptions.FORMAT_WITH_FUZZY_UNITS) : s) + "]";
                                break;
                            case ConsoleOptions.PRINT_FLOW_DOT:
                                s = ConsoleOptions.printFlowDictionary[ConsoleOptions.PRINT_FLOW_DOT];
                                break;
                            case ConsoleOptions.PRINT_FLOW_DOTQUAD:
                                s = ConsoleOptions.printFlowDictionary[ConsoleOptions.PRINT_FLOW_DOTQUAD];
                                break;
                        }
                        if (s != null) {
                            if (ConsoleOptions.PRINT_FLOW_ENABLED)
                                System.out.printf("%s", s);
                        } else if (ConsoleOptions.PRINT_FLOW_PROGRESS_ENABLED) {
                            this.updateTaskPrimeProgress(key, bi, from, upTo, intervalPar);//only detected primes
                        }
                    }
                    //this.primeSpoolCache.removeElementAt(reverseIdx);
                    //this.updateTaskPrimeProgress();//all numbers
                    //}
                }
                //fos.write(this.primeSpoolCache.toString().getBytes());
                if (ConsoleOptions.FLUSH_AT_EVERY_CACHING) {
                    fos.flush();
                    //System.out.printf("Caching terminated on %s!\n", NUMBER_GROUP_FORMATTER.format(currentEvaluatedNumber));
                }

                //if (this.primeSpoolCache.size() > CACHE_PRIMES_SPOOLER)
                //    this.primeSpoolCache.clear();

                //this.primeSpoolCache.add(currentEvaluatedNumber);
            }
            //currentEvaluatedNumber = currentEvaluatedNumber.add(BigInteger.ONE);
            //currentEvaluatedNumber = currentEvaluatedNumber.add(BigInteger.valueOf(2L));//skippa direttamente i pari/even
            currentEvaluatedNumber = currentEvaluatedNumber.add(
                    ConsoleOptions.EVEN_SKIPPER_ENABLED&&ConsoleOptions.EVEN_SKIPPER==ConsoleOptions.EVEN_SKIP_BY_ALGO ? BigInteger.valueOf(2L) :  BigInteger.ONE
            );
        }
        fos.flush();

        System.out.println();

        this.timeEnd = System.currentTimeMillis();
        System.out.printf("Ended time @ %s for caching terminated on %s!\n", LocalDateTime.now(), this.formatWithLiteralSize(currentEvaluatedNumber, ConsoleOptions.FORMAT_WITH_GROUPING, ConsoleOptions.FORMAT_WITH_FUZZY_UNITS));
        if (ConsoleOptions.PRINT_FLOW_STATS) {
            System.out.printf("Total time for #%s in milliseconds: %s\n", this.scanId, (this.timeEnd - this.timeStart));
            System.out.printf("Total time for #%s in seconds: %s\n", this.scanId, (this.timeEnd - this.timeStart) / 1000);
            System.out.printf("Total time for #%s in minutes: %s\n", this.scanId, ((this.timeEnd - this.timeStart) / 1000) / 60);
        }
    }

    private BigInteger[] BASE_ODD_DIVISORS = new BigInteger[]{BigInteger.valueOf(3L), BigInteger.valueOf(5L), BigInteger.valueOf(7L), BigInteger.valueOf(9L) };

    private BigInteger[] odd_divisors = null;//new BigInteger[]{BigInteger.valueOf(3L), BigInteger.valueOf(5L), BigInteger.valueOf(7L), BigInteger.valueOf(9L) };
    private BigInteger[] even_divisor = new BigInteger[]{BigInteger.valueOf(2L)};

    private boolean candidateHasBasePrimeRequirements(BigInteger currentBigi) {

        boolean rtCandidate = false;
        if (currentBigi.compareTo(BigInteger.valueOf(2L)) == EQUAL_TO)
            return true;

        if ( (ConsoleOptions.EVEN_SKIPPER_ENABLED&&ConsoleOptions.EVEN_SKIPPER==ConsoleOptions.EVEN_SKIP_BY_METH)
                || (ConsoleOptions.DIVIDER_BY_ODD_ENABLED&&ConsoleOptions.DIVIDER_BY_ODD==ConsoleOptions.DIVIDE_BY_ODD_METH)
        ) {

            BigInteger[] divisors = new BigInteger[]{BigInteger.ONE};

            if (ConsoleOptions.DIVIDER_BY_ODD_ENABLED) {
                divisors = odd_divisors;
            } else if (ConsoleOptions.EVEN_SKIPPER_ENABLED) {
                divisors = even_divisor;
            }

            for (BigInteger big : divisors) {
                BigInteger[] bds = currentBigi.divideAndRemainder(big);
                boolean hasNoRemainder = bds[1].compareTo(BigInteger.ZERO) == EQUAL_TO;
                boolean hasRemainder = bds[1].compareTo(BigInteger.ONE) == EQUAL_TO;
                //System.out.printf("%s è %s\n", currentBigi.toString(), isEven ? "pari" : "dispari");
                if (hasNoRemainder) {
                    rtCandidate = false;
                    break;
                } else {
                    rtCandidate = true;//return true for all odds;
                }
            }
        }

        return rtCandidate;
    }
    private boolean hasBasePrimeRequirementsByDecimal(BigInteger currentBigi) {

        BigDecimal bigd = new BigDecimal(currentBigi);

        BigDecimal[] bds = bigd.divideAndRemainder(BigDecimal.valueOf(2D));

        boolean isEven = bds[1].compareTo(BigDecimal.ZERO) == EQUAL_TO;
        boolean isOdd = bds[1].compareTo(BigDecimal.ONE) == EQUAL_TO;
        //System.out.printf("%s è %s\n", bigd.toBigInteger().toString(), isEven ? "pari" : "dispari");

        if (isEven)
            return false;

        return true;
    }

    private void prepareTaskProgress() {
        String bar = "";
        for (int i = 1; i <= 100; i++) {
            bar += ("-");
        }
        System.out.printf("\n" + bar + "\n");
    }

    //TODO: valutare uso della ImmutableMap di Guava (libreria Google)
    private static TreeMap<String, HashMap<String, BigInteger>> GLOBAL_PROGRESS = new TreeMap<String, HashMap<String, BigInteger>>();

    private void updateTaskPrimeProgress(String key, BigInteger currentPrimePar, BigInteger fromPar, BigInteger upToPar, BigInteger intervalPar) {

        HashMap<String, BigInteger> hm = GLOBAL_PROGRESS.containsKey(key) ? GLOBAL_PROGRESS.get(key) : new HashMap<String, BigInteger>();
        BigInteger progress = hm.containsKey("PROGRESS") ? hm.get("PROGRESS") : fromPar;
        BigInteger lastProgress = hm.containsKey("PROGRESS_LAST") ? hm.get("PROGRESS_LAST") : fromPar;
        BigInteger upTo = hm.containsKey("UPTO") ? hm.get("UPTO") : upToPar;
        BigInteger interval = hm.containsKey("HOWMUCH") ? hm.get("HOWMUCH") : intervalPar;

        // progresso : totale = x : 100 ; x = (100 * progresso) / totale;
        //BigInteger currentProgressPerc = (BigInteger.valueOf(100L).multiply( progresses.get(keyFilter).get("PROGRESS") )).divide( progresses.get(keyFilter).get("UPTO").subtract(progresses.get(keyFilter).get("HOWMUCH")) );
        BigInteger currentProgressPerc = null;
        if (interval.compareTo(upTo) == EQUAL_TO)
            currentProgressPerc = (BigInteger.valueOf(100L).multiply(progress)).divide(interval);
        else
            currentProgressPerc = (BigInteger.valueOf(100L).multiply(progress)).divide(upTo.subtract(interval));

        if (currentProgressPerc.compareTo(lastProgress) >= EQUAL_TO) {
            System.out.printf(".");
            //System.out.printf("\n"+this.progress+"|"+upTo+"|"+currentProgressPerc+"\n");
            lastProgress = lastProgress.add(BigInteger.ONE);
        }
        progress = progress.add(BigInteger.ONE);

        hm.put("PROGRESS_PERC", currentProgressPerc);
        hm.put("PROGRESS", progress);
        hm.put("PROGRESS_LAST", lastProgress);
        hm.put("CURRENT_PRIME", currentPrimePar);
        hm.put("FROM", fromPar);
        hm.put("UPTO", upTo);
        hm.put("HOWMUCH", interval);
        hm.put("LIMIT", this.limitedTo);

        GLOBAL_PROGRESS.put(key, hm);

        if (instance != null) {
            //System.out.println(key+"|"+progress.intValue());
            instance.updateProgressBar(progress, key);
        }

    }

    private static void parallelPrimeComputation(BigInteger from, BigInteger upTo, Integer totalProcesses) throws IOException {

        if (ConsoleOptions.PRINT_FLOW_PROGRESS_ENABLED) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    instance = new MyProgressWindow(GLOBAL_PROGRESS);
                    instance.setVisible(true);
                }
            });
        }


        //TODO: prevedere supporto tranches dinamiche e diverse tra loro in base a segmento numero reali, vedi curvatura numneri primi
        Integer trancheNumber = totalProcesses - 1;
        BigInteger currentRange = upTo.subtract(from.subtract(BigInteger.ONE));
        if (ConsoleOptions.PRINT_FLOW_STATS)
            System.out.println("Current Range is: " + currentRange);
        //Integer tranche = currentRange.divide(BigInteger.valueOf(trancheNumber)).intValue();
        BigInteger tranche = currentRange.divide(BigInteger.valueOf(trancheNumber));
        Vector<BigInteger> tranches = new Vector<BigInteger>(trancheNumber);
        Vector<BigInteger[]> ranges = new Vector<BigInteger[]>(trancheNumber);
        for (int i = 0; i < trancheNumber; i++) {
            tranches.add(i, tranche);
        }
        int index = 0;
        for (BigInteger bigInteger : tranches) {
            BigInteger startFrom = PrimeListGenerator.calculateTranche(bigInteger, from, index);
            if (ConsoleOptions.PRINT_FLOW_STATS)
                System.out.println("Starting from " + startFrom + " until " + (startFrom.add(bigInteger.subtract(BigInteger.ONE))));
            ranges.add(new BigInteger[]{startFrom, startFrom.add(bigInteger.subtract(BigInteger.ONE)), bigInteger});
            index++;
        }
        Thread[] threads = new Thread[tranches.size()];

        index = 0;
        //null;
        for (BigInteger[] bigRange : ranges) {

            int finalIndex = index;
            Runnable runner = new Runnable() {
                @Override
                public void run() {
                    try {
                        Integer procIndex = finalIndex;
                        procIndex += 1;
                        PrimeListGenerator primeListGenerator1 = new PrimeListGenerator(totalProcesses.toString(), "_From-" + bigRange[0] + "_To-" + bigRange[1]);
                        primeListGenerator1.scanRealNumbersWithoutCache(procIndex, bigRange[0], bigRange[1], bigRange[2]);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            };


            threads[index] = new Thread(runner);
            threads[index].start();
            index++;
        }

    }

    private static BigInteger calculateTranche(BigInteger trancheSize, BigInteger fromTranche, Integer trancheIteration) {
        BigInteger outputTranche = fromTranche;
                /*(trancheIteration==0)
                ?
                        fromTranche
                :
                        fromTranche.subtract(BigInteger.ONE)
                ;*/
        for (int i = 0; i < trancheIteration; i++) {
            if (trancheIteration == 0)
                outputTranche = outputTranche.add(trancheSize).subtract(BigInteger.ONE);
            else
                outputTranche = outputTranche.add(trancheSize);
        }
        return outputTranche;
    }

    private static String formatWithLiteralSize(String s) {
        BigInteger big = BigInteger.ONE;
        try {
            big = BigInteger.valueOf((Long) NUMBER_GROUP_FORMATTER.parse(s));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return formatWithLiteralSize(big, true, ConsoleOptions.FUZZY_UNIT_CUCUZZE);
    }

    private static String formatWithLiteralSize(BigInteger big, Boolean groupBy) {
        return formatWithLiteralSize(big, groupBy, ConsoleOptions.FUZZY_UNIT_CUCUZZE);
    }

    protected static String formatWithLiteralSize(BigInteger big, Boolean groupBy, Short easterEggFuzzy) {
        String rt = groupBy ? NUMBER_GROUP_FORMATTER.format(big) : big.toString();

        if (big.compareTo(BigInteger.valueOf(999_999L)) == GREAT_THAN && big.compareTo(BigInteger.valueOf(1_000_000_000L)) == LESS_THAN) {
            if (big.compareTo(BigInteger.valueOf(1_999_999L)) == GREAT_THAN)
                rt += " MILIONI";
            else
                rt += " MILIONE";
        } else if (big.compareTo(BigInteger.valueOf(999_999_999L)) == GREAT_THAN && big.compareTo(BigInteger.valueOf(1_000_000_000_000L)) == LESS_THAN) {
            if (big.compareTo(BigInteger.valueOf(1_999_999_999L)) == GREAT_THAN)
                rt += " MILIARDI";
            else
                rt += " MILIARDO";
        } else if (big.compareTo(BigInteger.valueOf(999_999_999_999L)) == GREAT_THAN && big.compareTo(BigInteger.valueOf(1_000_000_000_000_000L)) == LESS_THAN) {
            if (big.compareTo(BigInteger.valueOf(1_999_999_999_999L)) == GREAT_THAN)
                rt += " BILIONI";
            else
                rt += " BILIONE";
        } else if (big.compareTo(BigInteger.valueOf(999_999_999_999_999L)) == GREAT_THAN && big.compareTo(BigInteger.valueOf(1_000_000_000_000_000_000L)) == LESS_THAN) {
            if (big.compareTo(BigInteger.valueOf(1_999_999_999_999_999L)) == GREAT_THAN)
                rt += " FANTASTILIONE";
            else
                rt += " FANTASTILIONE";
        } else if (big.compareTo(BigInteger.valueOf(999_999_999_999_999_999L)) == GREAT_THAN && big.compareTo(new BigInteger("1000000000000000000000")) == LESS_THAN) {
            if (big.compareTo(BigInteger.valueOf(1_999_999_999_999_999_999L)) == GREAT_THAN)
                //9_223_372_036_854_775_800
                rt += " MEGAFANTASTILIARDI";
            else
                rt += " MEGAFANTASTILIARDO";
        } /*else {
            return big.toString();
        }*/
        return rt + (easterEggFuzzy > ConsoleOptions.FUZZY_UNIT_DEFAULT_NOT ? ConsoleOptions.fuzzyDictionary[easterEggFuzzy] : "");
    }


    public static MyProgressWindow instance;

    public static void main(String[] args) {
        PrimeListGenerator primeListGenerator1, primeListGenerator2, primeListGenerator3, primeListGenerator4, primeListGenerator5 = null;
        try {
            /*primeListGenerator0 = new PrimeListGenerator("0");
            primeListGenerator0.scanRealNumbers(false, BigInteger.valueOf(1000L), 100);*/

            /*
            primeListGenerator1 = new PrimeListGenerator("10", "UpTo"+1_000_000L);
            primeListGenerator1.scanRealNumbers(false, BigInteger.ZERO, BigInteger.valueOf(1_000_000L), 100_000);

            primeListGenerator2 = new PrimeListGenerator("10", "UpTo"+1_000_000L);
            primeListGenerator2.scanRealNumbers(false, BigInteger.ZERO, BigInteger.valueOf(1_000_000L), 100_000);
            */

            /*primeListGenerator2 = new PrimeListGenerator("UpTo"+Integer.MAX_VALUE);
            primeListGenerator2.scanRealNumbers(true, BigInteger.valueOf(Integer.MAX_VALUE), 100000);*/

            /*primeListGenerator3 = new PrimeListGenerator("UpTo"+Long.MAX_VALUE);
            primeListGenerator3.scanRealNumbers(true, BigInteger.ONE, BigInteger.valueOf(Long.MAX_VALUE), 100000);*/

            /*
            //BigInteger upTo1 = BigInteger.valueOf(200_000);//66666
            //BigInteger upTo1 = BigInteger.valueOf(20_000_000);//200000
            BigInteger upTo1 = BigInteger.valueOf(200_000_000);//2000000
            primeListGenerator4 = new PrimeListGenerator("UpTo"+upTo1);
            primeListGenerator4.scanRealNumbers(true, BigInteger.ONE, upTo1, 2_000_000);
            */


            //ConsoleOptions.PRINT_FLOW = ConsoleOptions.PRINT_FLOW_FAST;//fastest
            //ConsoleOptions.PRINT_FLOW = ConsoleOptions.PRINT_FLOW_PRIMENUMBERS;//more slow
            //ConsoleOptions.PRINT_FLOW = ConsoleOptions.PRINT_FLOW_DOT;
            //ConsoleOptions.PRINT_FLOW = ConsoleOptions.PRINT_FLOW_DOTQUAD;

            ConsoleOptions.PRINT_FLOW_PROGRESS = ConsoleOptions.PRINT_FLOW_PROGRESS_ONLYBAR;

            ConsoleOptions.FLUSH_AT_EVERY_CACHING = false;
            //ConsoleOptions.FLUSH_AT_EVERY_CACHING = true;

            //ConsoleOptions.FORMAT_WITH_GROUPING = false;
            ConsoleOptions.FORMAT_WITH_GROUPING = true;

            //ConsoleOptions.FORMAT_WITH_LITERAL_SIZE = false;
            ConsoleOptions.FORMAT_WITH_LITERAL_SIZE = true;

            //ConsoleOptions.FORMAT_WITH_FUZZY_UNITS = ConsoleOptions.FUZZY_UNIT_DEFAULT_NOT;
            ConsoleOptions.FORMAT_WITH_FUZZY_UNITS = ConsoleOptions.FUZZY_UNIT_CUCUZZE;


            //ConsoleOptions.PRINT_FLOW = ConsoleOptions.PRINT_FLOW_FAST;//fastest but inhibits the stats
            //ConsoleOptions.PRINT_FLOW = ConsoleOptions.PRINT_FLOW_NOT;
            //ConsoleOptions.PRINT_FLOW_PROGRESS = ConsoleOptions.PRINT_FLOW_PROGRESS_NOT;

            //ConsoleOptions.EVEN_SKIPPER=ConsoleOptions.EVEN_SKIP_BY_METH;
            ConsoleOptions.EVEN_SKIPPER=ConsoleOptions.EVEN_SKIP_BY_ALGO;

            ConsoleOptions.DIVIDER_BY_ODD = ConsoleOptions.DIVIDE_BY_ODD_METH;
            //ConsoleOptions.DIVIDER_BY_ODD = ConsoleOptions.DIVIDE_BY_ODD_ALGO;

            ConsoleOptions.UP_TO_DIVISOR = 20;

            ConsoleOptions.updateDefaultOptions();

            /*
            ConsoleOptions.PRINT_FLOW = ConsoleOptions.PRINT_FLOW_NOT;
            ConsoleOptions.PRINT_FLOW_PROGRESS = ConsoleOptions.PRINT_FLOW_PROGRESS_NOT;
            ConsoleOptions.updateDefaultOptions();
            BigInteger upTo = BigInteger.valueOf(Long.MAX_VALUE);
            PrimeListGenerator.parallelPrimeComputation(BigInteger.ONE, upTo, 1000);//circa 2900 anni
            PrimeListGenerator.parallelPrimeComputation(BigInteger.ONE, upTo, 1000);//circa 30 anni
            */

            //BigInteger upTo = BigInteger.valueOf(Integer.MAX_VALUE);//circa 30min
            BigInteger upTo = BigInteger.valueOf(200_000_000);
            //BigInteger upTo = BigInteger.valueOf(2_000_000);
            PrimeListGenerator.parallelPrimeComputation(BigInteger.ONE, upTo, 10);


        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //System.out.printf("Total numbers found: %s\n", primeListGenerator.primeSpoolCache.size());

    }

}

class MyProgressWindow extends JFrame {

    private TreeMap<String, HashMap<String, BigInteger>> progresses;

    public MyProgressWindow(TreeMap<String, HashMap<String, BigInteger>> progresses) {
        super("MyTitle for progress bar");
        this.progresses = progresses;
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //this.pack();
        this.setSize(800, 300);
        this.setAlwaysOnTop(true);
        //this.setVisible(true);

        this.getContentPane().setLayout(new FlowLayout());

        //Comparator comparator = new MyComparator();
        Map<String, HashMap<String, BigInteger>> desSortedMap = new TreeMap<String, HashMap<String, BigInteger>>(
                new Comparator<String>() {
                    @Override
                    public int compare(String s1, String s2) {
                        return s1.compareTo(s2);
                    }
                });
        desSortedMap.putAll(this.progresses);

        /*for (String key : desSortedMap.keySet()) {
            this.addMyComps(key);
        }*/
        for (Map.Entry<String, HashMap<String, BigInteger>> mapData : progresses.entrySet()) {
            System.out.println("DEBUG: " + mapData.getKey());
            this.addMyComps(mapData.getKey());
        }
    }

    private void addMyComps(String key) {

        BigInteger big = new BigInteger(key.substring(key.lastIndexOf('-') + 1));

        HashMap<String, BigInteger> hm = progresses.get(key);
        //BigInteger max = hm.get("LIMIT");
        JProgressBar jProgressBar = new JProgressBar(JProgressBar.VERTICAL, 0, 100);
        //JProgressBar jProgressBar = new JProgressBar(JProgressBar.VERTICAL,0, hm.get("UPTO").intValue());
        jProgressBar.setToolTipText(PrimeListGenerator.formatWithLiteralSize(
                big, true, Short.valueOf("0")
        ));
        jProgressBar.setStringPainted(true);
        jProgressBar.setPreferredSize(new Dimension(big.compareTo(BigInteger.valueOf(999_999_999)) > 0 ? 100 : 70, 250));
        //jProgressBar.setBorder(BorderFactory.createTitledBorder("Test"));
        //Border border = BorderFactory.createTitledBorder("Test");
        String borderTitle = "Up to " + PrimeListGenerator.NUMBER_GROUP_FORMATTER.format(big);
        Font font = new Font("Default", Font.PLAIN, 7);
        Border border = BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), borderTitle, TitledBorder.RIGHT, TitledBorder.BOTTOM, font);
        jProgressBar.setBorder(border);

        jProgressBar.setName(key);
        this.getContentPane().add(jProgressBar);
    }

    public void updateProgressBar(BigInteger prog, String keyFilter) {
        //for (String key : progresses.keySet()) {
        for (Component comp : this.getContentPane().getComponents()) {
            //if (comp.getName().equalsIgnoreCase(key)) {
            if (comp.getName().equalsIgnoreCase(keyFilter)) {
                // progresso : howMuch = x : 100 ; x = (100 * progresso) / howMuch;
                // upTo 1500 - progresso 700 = x 800 ==> upTo 1500 - howmuch 1000 = y 500 - 700 ==> +200 == 1500-200 = 1300
                // progresso ==> 200 : howmuch 1000 = x : 100 ==> x = (100 * progresso 200) / howmuch 1000
                BigInteger r1 = progresses.get(keyFilter).get("UPTO").subtract(progresses.get(keyFilter).get("HOWMUCH"));
                BigInteger relativeProgress = progresses.get(keyFilter).get("CURRENT_PRIME").subtract(r1).abs();
                BigInteger currentProgressPerc = (BigInteger.valueOf(100L).multiply(relativeProgress)).divide(progresses.get(keyFilter).get("HOWMUCH"));
                //BigInteger currentProgressPerc = (BigInteger.valueOf(100L).multiply( progresses.get(keyFilter).get("PROGRESS") )).divide( progresses.get(keyFilter).get("UPTO").subtract(progresses.get(keyFilter).get("PROGRESS")) );
                //System.out.println(currentProgressPerc.toString()+"% | "+relativeProgress.toString()+" || "+progresses.get(keyFilter).get("PROGRESS").toString()+" ||| "+progresses.get(keyFilter).get("UPTO").toString()+" |||| " + progresses.get(keyFilter).get("HOWMUCH").toString());
                ((JProgressBar) comp).setValue(currentProgressPerc.intValue());

                //((JProgressBar) comp).setValue(progresses.get(keyFilter).get("PROGRESS").intValue());
                        /*try {
                            Thread.sleep(1L);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }*/
            }
            //}
        }
        //}
    }
}

class ConsoleOptions {

    public static final Short PRINT_FLOW_NOT = Short.valueOf("0");

    public static final Short PRINT_FLOW_FAST = 1;
    public static final short PRINT_FLOW_PRIMENUMBERS = 2;//Short.valueOf("1");
    public static final short PRINT_FLOW_DOT = 3;//Short.valueOf("2");
    public static final short PRINT_FLOW_DOTQUAD = 4;//Short.valueOf("3");

    public static final String[] printFlowDictionary = {"", "PRINT_FLOW_FAST", "PRINT_FLOW_PRIMENUMBERS", ".", "[.]"};

    public static Short PRINT_FLOW = PRINT_FLOW_NOT;

    public static Short PRINT_FLOW_PROGRESS_NOT = 0;
    public static Short PRINT_FLOW_PROGRESS_PERCTXT = 1;
    public static Short PRINT_FLOW_PROGRESS_PERCBAR = 2;
    public static Short PRINT_FLOW_PROGRESS_ONLYBAR = 3;

    public static Short PRINT_FLOW_PROGRESS = PRINT_FLOW_PROGRESS_NOT;

    public static Boolean PRINT_FLOW_PROGRESS_ENABLED = PRINT_FLOW_PROGRESS > 0;

    public static Boolean PRINT_FLOW_ENABLED = PRINT_FLOW > 0;

    public static Boolean PRINT_FLOW_STATS = PRINT_FLOW == PRINT_FLOW_FAST || PRINT_FLOW >= PRINT_FLOW_PRIMENUMBERS;


    public static final short EVEN_SKIPPER_NOT = 0;
    public static final short EVEN_SKIP_BY_METH = 1;
    public static final short EVEN_SKIP_BY_ALGO = 2;
    public static short EVEN_SKIPPER = EVEN_SKIP_BY_ALGO;
    public static boolean EVEN_SKIPPER_ENABLED = EVEN_SKIPPER > EVEN_SKIPPER_NOT;


    public static final short DIVIDE_BY_ODD_NOT = 0;
    public static final short DIVIDE_BY_ODD_METH = 1;
    public static final short DIVIDE_BY_ODD_ALGO = 2;
    public static short DIVIDER_BY_ODD = DIVIDE_BY_ODD_METH;
    public static boolean DIVIDER_BY_ODD_ENABLED = DIVIDER_BY_ODD > DIVIDE_BY_ODD_NOT;

    public static Short UP_TO_DIVISOR = 10;

    public static void updateConsoleOptions() {

        PRINT_FLOW_PROGRESS_ENABLED = PRINT_FLOW_PROGRESS > 0;

        if (!PRINT_FLOW_PROGRESS_ENABLED)
            PRINT_FLOW_ENABLED = PRINT_FLOW > 0;
        else
            PRINT_FLOW_ENABLED = false;
        PRINT_FLOW_STATS = PRINT_FLOW != PRINT_FLOW_FAST || PRINT_FLOW == PRINT_FLOW_PRIMENUMBERS;

    }

    public static void updateAlgoritmoSetaccioOptions() {

        EVEN_SKIPPER_ENABLED = EVEN_SKIPPER > EVEN_SKIPPER_NOT;

        DIVIDER_BY_ODD_ENABLED = DIVIDER_BY_ODD > DIVIDE_BY_ODD_NOT;

    }

    public static void updateDefaultOptions() {
        updateConsoleOptions();
        updateAlgoritmoSetaccioOptions();
    }

    public static Boolean FORMAT_WITH_GROUPING = Boolean.FALSE;

    public static Boolean FORMAT_WITH_LITERAL_SIZE = Boolean.FALSE;

    public static final short FUZZY_UNIT_DEFAULT_NOT = Short.valueOf("0");

    public static final short FUZZY_UNIT_STRINGHE = Short.valueOf("1");
    public static final short FUZZY_UNIT_CUCUZZE = Short.valueOf("2");
    public static final short FUZZY_UNIT_COCOMERI = Short.valueOf("3");
    public static Short FORMAT_WITH_FUZZY_UNITS = FUZZY_UNIT_DEFAULT_NOT;

    public static final String[] fuzzyDictionary = {"", " DI STRINGHE", " DI CUCUZZE", " DI COCOMERI", " DI PINTE", " DI BOT", " DI BOND", " DI PEPITE", " DE SCHEI", " DI BARILI", " DI DOBLONI"};


    public static Boolean FLUSH_AT_EVERY_CACHING = Boolean.FALSE;

}
