/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TasteAnalytics.HierarchicalTopics.file;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import java.awt.Dimension;
import java.awt.geom.Point2D;

import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.lang3.math.NumberUtils;
//import org.apache.commons.lang.NumberUtils;
//import org.apache.commons.lang.math.NumberUtils ;
//import org.apache.commons.lang.math.NumberUtils;


import java.util.logging.Level;
import java.util.logging.Logger;
import com.TasteAnalytics.HierarchicalTopics.gui.MinimalismMainFrame;
import au.com.bytecode.opencsv.CSVReader;
import org.apache.commons.lang.StringUtils;

/**
 * A Simple file parser for reading CSV files and instantiate the
 * SimpleParallelSpaceModel interface.
 */
public class CSVFile extends SimpleParallelSpaceModel {

    /**
     * The url of the file.
     */
    URL url;
    BufferedReader br = null;
    private String filepath;
    private boolean hasNext = true;
    private CSVParser parser;
    private List<float[]> beforeNorm;
    private int skipLines;
    private boolean linesSkiped;
    private int tempNumDimensions;
    private int bytesRead = 0;
    private int filesize = 0;
    private Vector stringLabels = new Vector();
    private boolean isStringLabel[];
    private String name = "";
    public List<String[]> internalRecords;
    private List<Integer> docYear;
    private List<float[]> ori_norm;

    //for scatterplot view
 
  
    //For document viewer
    List<String[]> allElements;
    
    private List<String[]> allDocs;
    private List<String[]> allTopics;
    private List<Long> years;
    private List<String> ori_labels;//labels that correspond to the row numbers in the actual document
    private List<Float> topicSimilarities;
    //For time-sensitive keywords
    private Map<String, Integer> termIndex;
    private List<String[]> termWeights;
    private List<float[]> termWeights_norm;
    private int contentIdx;
    private List<Point2D> twitterGeoLocations;
    Point2D mediumLocation;
    Point2D minLocation;
    Point2D maxLocation;

    public List<HashMap<String, Integer>> content = new ArrayList<HashMap<String, Integer>>();
    
    
    public Point2D getMediumLocation() {
        return mediumLocation;
    }

    public Point2D getMinLocation() {
        return minLocation;
    }

    public Point2D getMaxLocation() {
        return maxLocation;
    }

    public List<Point2D> getTwitterGeoLocations() {
        return twitterGeoLocations;
    }
    private DateFormat format;

    public DateFormat getFormat() {
        return format;
    }

    public int getContentIdx() {
        return contentIdx;
    }



    public List<float[]> getTermWeights_norm() {
        return termWeights_norm;
    }

    public void setTermWeights_norm(List<float[]> termWeights_norm) {
        this.termWeights_norm = termWeights_norm;
    }
    private String folderPath;

    public String getFolderPath() {
        return folderPath;

    }

    /**
     * Creates a new CSVFile with the given url. The content is not read until
     * readContents() is called.
     *
     * @param url The url of the file to read.
     */
    public CSVFile(URL url) {
        this.url = url;
        name = url.getFile();
        name = name.substring(name.lastIndexOf('/') + 1);
        parser = new CSVParser(CSVParser.DEFAULT_SEPARATOR, CSVParser.DEFAULT_QUOTE_CHARACTER, CSVParser.DEFAULT_ESCAPE_CHARACTER);
    }

    public CSVFile(String path) {
        filepath = path;
        parser = new CSVParser(CSVParser.DEFAULT_SEPARATOR, CSVParser.DEFAULT_QUOTE_CHARACTER, CSVParser.DEFAULT_ESCAPE_CHARACTER);
    }

    /**
     * Returns the filename (without path).
     *
     * @return The filename.
     */
    public String getName() {
        return name;
    }

    /**
     * Reads the contents of the file and exposes them vis the
     * ParallelSpaceModel interface of the class. String values are stripped out
     * of the model and set as record labels.
     */
    public void readContents(boolean readall, boolean readfromdb, String DBURL, int DBPORT, String DBName, String collectionName, String fields) throws IOException, ParseException {

        /**
         * For actual content of the docs
         */
        String tmpURL = filepath.replaceAll("_usage", "");
        
       
        
       
         
         
        
        allDocs = new ArrayList<String[]>();//for actual documents
         com.TasteAnalytics.HierarchicalTopics.file.CSVReader csvReader = new com.TasteAnalytics.HierarchicalTopics.file.CSVReader(tmpURL);
        //au.com.bytecode.opencsv.CSVReader csvReader = new au.com.bytecode.opencsv.CSVReader(new FileReader(tmpURL));
        //if (readall)
        {
            allDocs = csvReader.readAll();//readAll();//first line is the header
            
            }
//         else {
//            allDocs = csvReader.read13();
//        }

        System.out.println("finished reading content, all record size =  " + allDocs.size());

        
        au.com.bytecode.opencsv.CSVReader csvReader1 = new au.com.bytecode.opencsv.CSVReader(new FileReader(filepath));
       
         //               org.mediavirus.parvis.file.CSVReader csvReader1 = new  org.mediavirus.parvis.file.CSVReader((filepath));

        allElements = new ArrayList<String[]>();
        allElements = csvReader1.readAll();
        System.out.println("allElements " + allElements.size());
        numDimensions = allElements.get(0).length;
        
        
        termWeights = new ArrayList<String[]>();
        termWeights_norm = new ArrayList<float[]>();

        int idx = tmpURL.lastIndexOf("\\");
        if (idx == -1) {
            idx = tmpURL.lastIndexOf("/");
        }

        String weightFilePath = tmpURL.substring(0, idx + 1);
        weightFilePath = weightFilePath + "topic-term-distributions.csv";

        if (!readfromdb) {
            com.TasteAnalytics.HierarchicalTopics.file.CSVReader csvReader2 = new com.TasteAnalytics.HierarchicalTopics.file.CSVReader(weightFilePath);
            //au.com.bytecode.opencsv.CSVReader csvReader2 = new au.com.bytecode.opencsv.CSVReader(new FileReader(weightFilePath));
            termWeights = csvReader2.readAll();

            System.out.println("finished reading topic-term-distributions");
        } else {

            MongoClient mongoClient = new MongoClient(DBURL, DBPORT);

            DB db = mongoClient.getDB(DBName);
            Set<String> colls = db.getCollectionNames();

            for (String s1 : colls) {
                System.out.println(s1);
            }

            DBCollection coll = db.getCollection(collectionName);
            System.out.println("database count : " + coll.getCount());
            DBCursor cursor = coll.find();

            while (cursor.hasNext()) {
                BasicDBObject dbo = (BasicDBObject) cursor.next();

                String ss = (String) dbo.get(fields);

                String xx[] = ss.split(",");
                termWeights.add(xx);

            }

            cursor.close();

            System.out.println("finished loading topic-term-distributions from db");

        }

        InputStream inputStream = new FileInputStream(filepath);
        InputStreamReader in = new InputStreamReader(inputStream);
        //InputStreamReader in = new InputStreamReader(conn.getInputStream());
        br = new BufferedReader(in);

        System.out.println("Start reading usage file..");

        internalRecords = new ArrayList<String[]>();       
       
        //readAll();

        br.close();

        System.out.println("finished reading usage file");

        idx = tmpURL.lastIndexOf("\\");
        if (idx == -1) {
            idx = tmpURL.lastIndexOf("/");
        }
        String termIndexPath = tmpURL.substring(0, idx + 1);
        folderPath = termIndexPath;
        termIndexPath = termIndexPath + "term-index.txt";

        InputStream inputStream2 = new FileInputStream(termIndexPath);
        DataInputStream inStream = new DataInputStream(inputStream2);
        InputStreamReader in2 = new InputStreamReader(inStream);
        br = new BufferedReader(in2);
        hasNext = true;
        readTermIndex();
        inStream.close();
        br.close();

        System.out.println("finished reading term-index file");

        String topicsPath = tmpURL.replaceAll(".csv", "-topk.csv");
        InputStream inputStream3 = new FileInputStream(topicsPath);
        InputStreamReader in3 = new InputStreamReader(inputStream3);
        br = new BufferedReader(in3);
        hasNext = true;
        readTopics();
        br.close();

        System.out.println("finished reading topk file");

        processRecords();

        System.out.println("finished processRecords");

//        if (readall) {
//            normalizeTermWeights();
//        }

        //if ()topicSequence
       

        String simipath = folderPath + "similarityMatrix.txt";
        File f2 = new File(simipath);

        normalizeTermWeights();
//        
        if ((!f2.exists())) {

             
            calculateTopicSimilarity();

            PrintWriter out = new PrintWriter(simipath);
            for (int j = 0; j < similarityMatrix.size(); j++) {
                for (int k = 0; k < similarityMatrix.get(j).size(); k++) {
                    out.printf("%f ", similarityMatrix.get(j).get(k));
                }
                out.printf("\n");
            }
            out.close();

            System.out.println("finished calculateTopicSimilarity, topic seq file output");
        } else {

            System.out.println("topicSequence exist, Loading...  topicSequence.txt");
           

            similarityMatrix = new ArrayList<List<Float>>();

            BufferedReader br1 = new BufferedReader(new FileReader(simipath));

            String line = br1.readLine();
            while (line != null) {
                List<Float> tmpls = new ArrayList<Float>();
                String[] tmps = line.split(" ");
                for (int k = 0; k < tmps.length; k++) {
                    tmpls.add(Float.parseFloat(tmps[k]));
                }
                similarityMatrix.add(tmpls);
                line = br1.readLine();
            }

            br1.close();

            System.out.println("finished loading similarity matrix");

        }

        
        
  
         BufferedReader br = new BufferedReader(new FileReader(folderPath + "content.ctt"));
        
        String line1 = br.readLine();
        while (line1!=null)
        {
            String[] tmp = line1.split(" ");
            HashMap<String,Integer> singleContent = new HashMap<String, Integer>();
            for (int i=0; i<tmp.length/2; i++)
            {
                String key = tmp[2*i];
                String count = tmp[2*i+1];
                
                singleContent.put(key, Integer.parseInt(count));
                                                                
            }
            content.add(singleContent);
            line1 = br.readLine();
        }
        br.close();
        
        System.out.println("content " + content.size());
        
        
        
        //System.out.println("Calculating topic dissimilarities of Cosine..");
        //calculateTopicSimilarityCosine();
        //System.out.println("Calculate individual document diversity");
        //calculateDocumentDiversity();

        

        

    }

    
    
     public void readContents(boolean readall) throws IOException, ParseException {

        /**
         * For actual content of the docs
         */
        String tmpURL = filepath.replaceAll("_usage", "");
                  
        
         au.com.bytecode.opencsv.CSVReader csvReader1 = new au.com.bytecode.opencsv.CSVReader(new FileReader(filepath));
       
      // org.mediavirus.parvis.file.CSVReader csvReader1 = new  org.mediavirus.parvis.file.CSVReader((filepath));

        allElements = new ArrayList<String[]>();
        //allElements = csvReader1.readAll();
        String[] xx = csvReader1.readNext();
        allElements.add(xx) ;
        System.out.println("allElements " + allElements.size());
        
        
        
        allDocs = new ArrayList<String[]>();//for actual documents
        com.TasteAnalytics.HierarchicalTopics.file.CSVReader csvReader = new com.TasteAnalytics.HierarchicalTopics.file.CSVReader(tmpURL);
        // au.com.bytecode.opencsv.CSVReader csvReader = new au.com.bytecode.opencsv.CSVReader(new FileReader(tmpURL));

          allDocs = csvReader.readAll();//readAll();//first line is the header


        System.out.println("finished reading content, all record size =  " + allDocs.size());

        
       
        numDimensions = allElements.get(0).length;
        
        
        termWeights = new ArrayList<String[]>();
        termWeights_norm = new ArrayList<float[]>();

        int idx = tmpURL.lastIndexOf("\\");
        if (idx == -1) {
            idx = tmpURL.lastIndexOf("/");
        }

        String weightFilePath = tmpURL.substring(0, idx + 1);
        weightFilePath = weightFilePath + "topic-term-distributions.csv";

        
            com.TasteAnalytics.HierarchicalTopics.file.CSVReader csvReader2 = new com.TasteAnalytics.HierarchicalTopics.file.CSVReader(weightFilePath);
            //termWeights = csvReader2.readAll();

            System.out.println("finished reading topic-term-distributions");
        

        InputStream inputStream = new FileInputStream(filepath);
        InputStreamReader in = new InputStreamReader(inputStream);
       
        br = new BufferedReader(in);

        System.out.println("Start reading usage file..");

        internalRecords = new ArrayList<String[]>();       

        br.close();

        System.out.println("finished reading usage file");

        idx = tmpURL.lastIndexOf("\\");
        if (idx == -1) {
            idx = tmpURL.lastIndexOf("/");
        }
        String termIndexPath = tmpURL.substring(0, idx + 1);
        folderPath = termIndexPath;
        termIndexPath = termIndexPath + "term-index.txt";

        InputStream inputStream2 = new FileInputStream(termIndexPath);
        DataInputStream inStream = new DataInputStream(inputStream2);
        InputStreamReader in2 = new InputStreamReader(inStream);
        br = new BufferedReader(in2);
        hasNext = true;
        readTermIndex();
        inStream.close();
        br.close();

        System.out.println("finished reading term-index file");

        String topicsPath = tmpURL.replaceAll(".csv", "-topk.csv");
        InputStream inputStream3 = new FileInputStream(topicsPath);
        InputStreamReader in3 = new InputStreamReader(inputStream3);
        br = new BufferedReader(in3);
        hasNext = true;
        readTopics();
        br.close();

        System.out.println("finished reading topk file");

        processRecords();

        System.out.println("finished processRecords");

//
//       
//
//        String simipath = folderPath + "similarityMatrix.txt";
//        File f2 = new File(simipath);
//
//        normalizeTermWeights();
////        
//        if ((!f2.exists())) {
//
//             
//            calculateTopicSimilarity();
//
//            PrintWriter out = new PrintWriter(simipath);
//            for (int j = 0; j < similarityMatrix.size(); j++) {
//                for (int k = 0; k < similarityMatrix.get(j).size(); k++) {
//                    out.printf("%f ", similarityMatrix.get(j).get(k));
//                }
//                out.printf("\n");
//            }
//            out.close();
//
//            System.out.println("finished calculateTopicSimilarity, topic seq file output");
//        } else {
//
//            System.out.println("topicSequence exist, Loading...  topicSequence.txt");
//           
//
//            similarityMatrix = new ArrayList<List<Float>>();
//
//            BufferedReader br1 = new BufferedReader(new FileReader(simipath));
//
//            String line = br1.readLine();
//            while (line != null) {
//                List<Float> tmpls = new ArrayList<Float>();
//                String[] tmps = line.split(" ");
//                for (int k = 0; k < tmps.length; k++) {
//                    tmpls.add(Float.parseFloat(tmps[k]));
//                }
//                similarityMatrix.add(tmpls);
//                line = br1.readLine();
//            }
//
//            br1.close();
//
//            System.out.println("finished loading similarity matrix");
//
//        }

   

        

        

    }

    /**
     * Reads the entire file into a List with each element being a String[] of
     * tokens.
     *
     * @return a List of String[], with each String[] representing a line of the
     * file.
     *
     * @throws IOException if bad things happen during the read
     */
    public void readAll() throws IOException {
        allElements = new ArrayList<String[]>();//usage
        
        
        int count = 0;
        while (hasNext) {
            System.out.println(count++);
            String[] nextLineAsTokens = readNext();
            if (nextLineAsTokens.length!= 20)
            {
                System.out.println(nextLineAsTokens.length);
            }
            if (nextLineAsTokens != null) {
                allElements.add(nextLineAsTokens);

            }
        }
        System.out.println("Topic proportions " + (allElements.size() - 1));
        System.out.println("Tweets " + (allDocs.size() - 1));
        //First row is header
        numDimensions = allElements.get(0).length;
        // this.initNumDimensions(numDimensions);
        //this.setAxisLabels(header);

    }

    private void readTermIndex() throws FileNotFoundException, IOException {
        termIndex = new HashMap<String, Integer>();
        int index = 0;
        String strLine;

        while ((strLine = br.readLine()) != null) {

            termIndex.put(strLine, index);
            index++;
        }

        System.out.println("Index is " + index);
    }

    private void readTopics() throws IOException {
        allTopics = new ArrayList<String[]>();
        while (hasNext) {
            String[] nextLineAsTokens = readNext();
            if (nextLineAsTokens != null) {
                allTopics.add(nextLineAsTokens);
            }
        }
        System.out.println("topics number: " + allTopics.size());
    }
    private static List<SimpleDateFormat> dateFormats = new ArrayList<SimpleDateFormat>() {
        {

//            add(new SimpleDateFormat("M/dd/yyyy"));
//            add(new SimpleDateFormat("dd.M.yyyy"));
            add(new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-S"));
            add(new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-S"));
             add(new SimpleDateFormat("yyyyMMddHHmmss"));

            add(new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss"));
            add(new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss"));
            add(new SimpleDateFormat("yyyy-MM-dd"));

            add(new SimpleDateFormat("MM-dd-yyyy"));

            add(new SimpleDateFormat("M/dd/yyyy hh:mm:ss a"));
            add(new SimpleDateFormat("M/dd/yyyy hh:mm:ss"));
            add(new SimpleDateFormat("M/dd/yyyy hh:mm a"));
            add(new SimpleDateFormat("M/dd/yyyy hh:mm"));
            add(new SimpleDateFormat("M/dd/yyyy"));
            add(new SimpleDateFormat("mm/dd/yyyy"));
            add(new SimpleDateFormat("mm/dd/yyyy hh:mm"));
            add(new SimpleDateFormat("M/dd/yyyy hh:mm:ss"));
            add(new SimpleDateFormat("M/dd/yyyy hh:mm"));
//            add(new SimpleDateFormat("dd.M.yyyy hh:mm:ss a"));
//            add(new SimpleDateFormat("dd.MMM.yyyy"));
            //add(new SimpleDateFormat("MM/dd/yyyy hh:mm"));
            add(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S"));
            add(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"));
            add(new SimpleDateFormat("yyyyMMdd"));
            add(new SimpleDateFormat("yyyy"));

        }
    };

    public DateFormat getTimeFormat(String sdf) {
        Date date = null;

        for (SimpleDateFormat format : dateFormats) {

            format.setLenient(false);
            try {
                date = format.parse(sdf);

                // int a = 0;
            } catch (ParseException ex) {
                // Logger.getLogger(CSVFile.class.getName()).log(Level.SEVERE, null, ex);
                // System.out.println("not matched!");
            }

            if (date != null) {
                //System.out.println("format matched " + format.toPattern());
                return format;

            }
        }

        return null;
    }

    public void processRecords() throws ParseException {
        String label = null;
        beforeNorm = new ArrayList<float[]>();
      

           
        docYear = new ArrayList<Integer>();
      
        years = new ArrayList<Long>();
        

    

        //allDocs.get(0).length;
        List<Integer> timeinfo;

        String[] dateTime = new String[1];
        String[] date = null, time = null;
        long tmpT = 0;
        String tmpFiller;
        Date dateT;
        timeinfo = new ArrayList<Integer>();
        String tmtime;

        int dateColumn = -1;

        for (int i = 0; i < allDocs.get(0).length; i++) {
            if (allDocs.get(0)[i].contains("DATE") || allDocs.get(0)[i].contains("date") || allDocs.get(0)[i].contains("StartDate") || allDocs.get(0)[i].contains("Year") || allDocs.get(0)[i].contains("time") || allDocs.get(0)[i].contains("Created_at")) {
                dateColumn = i;
                break;
            }

        }

        int contentColumn = -1;

        for (int i = 0; i < allDocs.get(0).length; i++) {
            if (allDocs.get(0)[i].equals("CONTENT") ||allDocs.get(0)[i].equals("Content") || allDocs.get(0)[i].equals("content")
                    || allDocs.get(0)[i].equals("Abstract") || allDocs.get(0)[i].equals("abstract") || allDocs.get(0)[i].equals("text")
                    || allDocs.get(0)[i].equals("Description") ||allDocs.get(0)[i].equals("new_text")
                    
                    
                    ) {
                contentColumn = i;
                break;
            }

        }

        contentIdx = contentColumn;

        // just for twitter locations
        twitterGeoLocations = new ArrayList<Point2D>();

        double max_locationX = -100000000, min_locationX = 100000;
        double max_locationY = -100000000, min_locationY = 100000;

        int locationIdx = -1;
        for (int i = 0; i < allDocs.get(0).length; i++) {
            if (allDocs.get(0)[i].equals("Location")) {
                locationIdx = i;
                break;
            }

        }

        if (locationIdx != -1) {

            for (int i = 1; i < allDocs.size(); i++) {
                String test = allDocs.get(i)[locationIdx];
                String[] x = test.split(" ");
                Point2D tempP = new Point2D.Double(Double.parseDouble(x[0]), Double.parseDouble(x[1]));
                twitterGeoLocations.add(tempP);

                if (Double.parseDouble(x[0]) >= max_locationX) {
                    max_locationX = Double.parseDouble(x[0]);
                }

                if (Double.parseDouble(x[0]) <= min_locationX) {
                    min_locationX = Double.parseDouble(x[0]);
                }

                if (Double.parseDouble(x[1]) >= max_locationY) {
                    max_locationY = Double.parseDouble(x[1]);
                }

                if (Double.parseDouble(x[1]) <= min_locationY) {
                    min_locationY = Double.parseDouble(x[1]);
                }

            }

        }

        maxLocation = new Point2D.Double(max_locationX, max_locationY);
        minLocation = new Point2D.Double(min_locationX, min_locationY);

        mediumLocation = new Point2D.Double((max_locationX + min_locationX) / 2, (max_locationY + min_locationY) / 2.0);

        System.out.println("map min" + minLocation);
        System.out.println("map max" + maxLocation);
        System.out.println("map center" + mediumLocation);
        

        if (dateColumn == -1) {
            System.out.println("date Column error, could not find date in csvfile");
        } else {
            System.out.println("date Column " + dateColumn);

        }

        if (contentColumn == -1) {
            System.out.println("no Conten column in CSF file or not reading content");
        }
        else {
            System.out.println("content Column " + contentColumn);

        }

//             tmtime = allDocs.get(3)[dateColumn];
//             // consumer  "2011-11-9 9:53:59"
//             //news 2 "2012-09-02"             
//             // occupy july "2011-08-19 20:19:31.0"
//             //protest "2011-11-9 7:44:5"
//             //tweets occpuy "2011-08-19 20:19:31.0"
//             //vast "5/14/2011 9:22"
//             // nsf "2003"
//             
//             
//             
//             tmtime = tmtime.replaceAll("\\/", "-");
//             tmtime = tmtime.replaceAll(":", "-");
//             tmtime = tmtime.replaceAll(" ", "-");
//             tmtime  = tmtime.replaceAll("\\.", "-");
//             tmtime ="2011-08-21-11-37-21-0";
//             format.setLenient(false);
        // format = getTimeFormat(tmtime);
        // format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//        if (tmtime.contains("/")) {
//            format = new SimpleDateFormat("M/dd/yyyy");
//            StaticVariables.TIME_PARSE_OPTION = 1;
//            StaticVariables.TIME_INTERVAL = 86400000L;
//                                            21600000L
//        }
        
        System.out.println("alldoc " + allDocs.size());
        System.out.println("all elements" + allElements.size());
         
        
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
       
        for (int idx = 1; idx < allDocs.size(); idx++) {
            //if (allElements.get(idx).length == numDimensions) 
            {
                //if (!allElements.get(idx)[0].isEmpty()) 
                

                    //System.out.println( allDocs.get(idx)[dateColumn]);
                    String tmptime = allDocs.get(idx)[dateColumn]; //column for year info

//                    if (tmptime.length()>4 &&  StringUtils.isNumeric(tmptime))
//                    
//                     tmpT = Long.parseLong(tmptime)*1000;
//
//                    
//////
//                    else
                    {
                        tmptime = tmptime.replaceAll("\\/", "-");
                        tmptime = tmptime.replaceAll(":", "-");
                        tmptime = tmptime.replaceAll(" ", "-");
                        tmptime = tmptime.replaceAll("\\.", "-");
                        format = getTimeFormat(tmptime);

                        try {

                            if (((SimpleDateFormat) format).toPattern() == "yyyy") {
                                tmptime = tmptime;//+"-1-1";
                                tmpT = Integer.parseInt(tmptime);
                            
                            } else {
                               
                                
                                dateT = (Date) format.parse(tmptime);
                                tmpT = dateT.getTime();
                            }

                        } catch (Exception e) {

                            System.out.println(((SimpleDateFormat) format).toPattern());
                            System.out.println("Datetime split failed at line " + idx + "With Time" + tmpT);

                        }
////                    
                    }
                internalRecords.add(allElements.get(idx));
                //System.out.println(idx);
                years.add(tmpT);//in milliseconds       

            }

        }
    
        
        System.out.println("internal Records" + internalRecords.size());

        //setParidx2DocIdx(parIdx2docIdx);
    }


    /**
     * Reads the next line from the buffer and converts to a string array.
     *
     * @return a string array with each comma-separated element as a separate
     * entry.
     *
     * @throws IOException if bad things happen during the read
     */
    public String[] readNext() throws IOException {

        String[] result = null;
        do {
            String nextLine = getNextLine();
            if (!hasNext) {
                return result; // should throw if still pending?
            }
            String[] r = parser.parseLineMulti(nextLine);
            if (r.length > 0) {
                if (result == null) {
                    result = r;
                } else {
                    String[] t = new String[result.length + r.length];
                    System.arraycopy(result, 0, t, 0, result.length);
                    System.arraycopy(r, 0, t, result.length, r.length);
                    result = t;
                }
            }
        } while (parser.isPending());
        return result;
    }

    /**
     * Reads the next line from the file.
     *
     * @return the next line from the file without trailing newline
     * @throws IOException if bad things happen during the read
     */
    private String getNextLine() throws IOException {
        if (!this.linesSkiped) {
            for (int i = 0; i < skipLines; i++) {
                br.readLine();
            }
            this.linesSkiped = true;
        }
        String nextLine = br.readLine();
        if (nextLine == null) {
            hasNext = false;
        }
        return hasNext ? nextLine : null;
    }

    /**
     * Method to add a ProgressListener to get notified of the loading progress.
     */
    /**
     * Dispatches a ProgressEvent to all listeners.
     *
     * @param e The ProgressEvent to send.
     */
    public List<String[]> getInternalRecord() {
        return internalRecords;
    }

    public List<String[]> getInternalDocs() {
        return allDocs;
    }

    public Map<String, Integer> getTermIndex() {
        return termIndex;
    }

    public List<String[]> getTermWeights() {
        return termWeights;
    }

    public List<String[]> getAllTopics() {
        return allTopics;
    }
    
    public void setAllTopics(List<String[]> alt) {
        allTopics = alt;
    }

    public List<List<Float>> getSimilarityMatrix() {
        return similarityMatrix;
    }

    public void setSimilarityMatrix(List<List<Float>> similarityMatrix) {
        this.similarityMatrix = similarityMatrix;
    }
    private List<List<Float>> similarityMatrix;//upper triangle of a similarity matrix

    public void normalizeTermWeights() {
        float[] tmpTermWeights;
        float[] rowSum = new float[termWeights.size()];
        float tmpsum = 0;

        for (int i = 0; i < termWeights.size(); i++) {
            tmpTermWeights = new float[termWeights.get(0).length];
            for (int j = 0; j < termWeights.get(0).length; j++) {
                tmpTermWeights[j] = Float.parseFloat(termWeights.get(i)[j]);
                tmpsum += tmpTermWeights[j];
            }
            termWeights_norm.add(tmpTermWeights);
            rowSum[i] = tmpsum;
            tmpsum = 0;
        }
        //Normalize termWeights
        for (int i = 0; i < termWeights.size(); i++) {
            for (int j = 0; j < termWeights.get(0).length; j++) {
                try {
                    if (rowSum[i] == 0) {
                        termWeights_norm.get(i)[j] = 0;
                    } else {
                        termWeights_norm.get(i)[j] = termWeights_norm.get(i)[j] / rowSum[i];
                    }
                } catch (Exception e) {
                    System.out.println("row " + i + "column " + j);
                }
            }
        }

    }

    public void calculateTopicSimilarity() {
        similarityMatrix = new ArrayList<List<Float>>();
        topicSimilarities = new ArrayList<Float>();
        Dimension dim = new Dimension();
        int firstAxis = 0;

        if (internalRecords != null) {
            float overallMin = Float.MAX_VALUE;

            int numOfTopics = internalRecords.get(0).length;
            float maxConcentration = 0;

            for (int i = 0; i < numOfTopics; i++) {
                List<Float> tmpSim = new ArrayList<Float>();
                float tempMaxConcentration = 0;
                int tempFirstAxis = 0;
                for (int j = 0; j < numOfTopics; j++) {
                    //if (j != i) {
                    float tmpDistance = 0;
                    //Whether calculating the similarity based on original value or normalized value
//                        for (int k = 0; k < ori_norm.size(); k++) {
//                            float a = (ori_norm.get(k)[j]);
//                            float b = (ori_norm.get(k)[i]);
//
//                            tmpDistance += (a - b) * (a - b);
//                            tempMaxConcentration += ori_norm.get(k)[j];
//                        }
                    for (int k = 0; k < termWeights_norm.get(0).length; k++) {
                        float a = termWeights_norm.get(i)[k];
                        float b = termWeights_norm.get(j)[k];

                        tmpDistance += Math.pow(Math.sqrt(a) - Math.sqrt(b), 2) * 0.5;
                    }

                    firstAxis = 0;
//                        tempFirstAxis = j;
//                        if (maxConcentration < tempMaxConcentration) {
//                            maxConcentration = tempMaxConcentration;
//                            firstAxis = tempFirstAxis;
//                        }
                    tmpSim.add(tmpDistance);
                    if (tmpDistance > 0 && tmpDistance < overallMin) {
                        overallMin = tmpDistance;
                        dim.width = i;
                        dim.height = j;
                    }
                    //}

                }
                similarityMatrix.add(tmpSim);
            }
            topicSimilarities.add(overallMin);
        }

       
       

        int i = 0;

        int tmpi = 1;
        while (i < numDimensions - 1) {
            int tmpIdx = 0;

            float min = Float.MAX_VALUE;
            for (int j = 0; j < similarityMatrix.get(tmpi).size(); j++) {

                if (min > similarityMatrix.get(tmpi).get(j)) {
                    
                        min = similarityMatrix.get(tmpi).get(j);
                        tmpIdx = j;
                    
                }
            }
            topicSimilarities.add(min);
            tmpi = tmpIdx;
           
            i++;
        }


    }


    public List<Long> getYears() {
        return years;
    }

    public List<Float> getTopicSimilarities() {
        return topicSimilarities;
    }

    private float calculateEntropy(float[] prob) {
        float entropy = 0;
        for (int i = 0; i < prob.length; i++) {
            if (prob[i] != 0) {
                entropy -= prob[i] * Math.log(prob[i]);
            }
        }
        return entropy;
    }
    float[][] topicSimilarityCosine;

    private void calculateTopicSimilarityCosine() throws FileNotFoundException, IOException {

        //s(i,j) = sigmaD ndi*ndj / (sqrt) sigmad ndi*ndi * (sqrt) sigmad nji*ndj
        int num_topics = allTopics.size() - 1;
        topicSimilarityCosine = new float[num_topics][num_topics];

        for (int i = 0; i < num_topics; i++) {
            for (int j = 0; j < num_topics; j++) {
                topicSimilarityCosine[i][j] = 0;
            }
        }

        String Path = folderPath + "topicSimilarityCosine.txt";

        File f1 = new File(Path);
        if (f1.exists()) {
            System.out.println("Loading calculateTopicSimilarityCosine");

            FileReader reader = new FileReader(Path);
            BufferedReader in = new BufferedReader(reader);
            for (int i = 0; i < num_topics; i++) {
                String string = in.readLine();
                String[] inputs = string.split("\\s");
                float[] tempFloat = new float[inputs.length];
                for (int j = 0; j < tempFloat.length; j++) {
                    topicSimilarityCosine[i][j] = Float.parseFloat(inputs[j]);
                }

            }
            in.close();
            reader.close();

        } else {
            PrintWriter out = new PrintWriter(Path);

            float minn = 9999999;
            float maxx = -9999999;

            float tempSum = 0;
            for (int i = 0; i < num_topics; i++) {
                for (int j = 0; j < num_topics; j++) {

                    if (i != j) {

                        float sum1 = 0;
                        float sum2 = 0;
                        float sum3 = 0;

                        for (int d = 1; d < allDocs.size(); d++) {
                            //allElements.get(d)
                            sum1 += Float.parseFloat(allElements.get(d)[i]) * Float.parseFloat(allElements.get(d)[j]);
                            sum2 += Float.parseFloat(allElements.get(d)[i]) * Float.parseFloat(allElements.get(d)[i]);
                            sum3 += Float.parseFloat(allElements.get(d)[j]) * Float.parseFloat(allElements.get(d)[j]);
                        }

                        topicSimilarityCosine[i][j] = (float) (sum1 / Math.sqrt(sum2) / Math.sqrt(sum3));

                    }

                    if (topicSimilarityCosine[i][j] != 0) {
                        topicSimilarityCosine[i][j] = 1 - topicSimilarityCosine[i][j];
                    }
                    //  topicSimilarityCosine[i][j] = 1/topicSimilarityCosine[i][j];

                    if (topicSimilarityCosine[i][j] >= maxx) {
                        maxx = topicSimilarityCosine[i][j];
                    }

                    if (topicSimilarityCosine[i][j] <= minn) {
                        minn = topicSimilarityCosine[i][j];
                    }

                    tempSum += topicSimilarityCosine[i][j];
                    // out.printf("%f ",topicSimilarityCosine[i][j]);

                }
                // out.printf("\n");

            }

            float mean = (tempSum) / num_topics / num_topics;

            System.out.println("maxx " + maxx + " minn " + minn + " mean " + mean);

            for (int i = 0; i < num_topics; i++) {
                for (int j = 0; j < num_topics; j++) {
                    //topicSimilarityCosine[i][j] = (topicSimilarityCosine[i][j])/mean;
                    out.printf("%f ", topicSimilarityCosine[i][j]);
                }
                out.printf("\n");
            }

            out.close();

            System.out.println("finished calculateTopicSimilarityCosine");

        }

    }
    float diversityOfDocument[];

    private void calculateDocumentDiversity() throws FileNotFoundException, IOException //div(d) = sig i sig j P(i|d)P(i|j) * dis(i,j);
    {
        int num_topics = allTopics.size() - 1;

        diversityOfDocument = new float[allDocs.size() - 1];
        String Path = folderPath + "diversityOfDocument.txt";

        File f1 = new File(Path);
        if (f1.exists()) {
            System.out.println("Loading diversityOfDocuments");

            FileReader reader = new FileReader(Path);
            BufferedReader in = new BufferedReader(reader);
            for (int i = 0; i < allDocs.size() - 1; i++) {
                String string = in.readLine();
                diversityOfDocument[i] = Float.parseFloat(string);
            }
            in.close();
            reader.close();

        } else {
            for (int i = 0; i < allDocs.size() - 1; i++) {
                diversityOfDocument[i] = 0;
            }

            float rowSum[] = new float[allDocs.size() - 1];

            for (int i = 1; i < allElements.size(); i++) {
                //float sum = 0;
                rowSum[i - 1] = 0;
                for (int j = 0; j < allElements.get(i).length; j++) {
                    rowSum[i - 1] += Float.parseFloat(allElements.get(i)[j]);
                }

                //System.out.println(sum);
            }

//            System.out.println("Max in usage is " + maxx);
            for (int d = 0; d < allDocs.size() - 1; d++) {
                if (rowSum[d] <= 100) {
                    diversityOfDocument[d] = -1;
                } else {
                    for (int i = 0; i < num_topics; i++) {
                        for (int j = 0; j < num_topics; j++) {

                            diversityOfDocument[d] += Float.parseFloat(allElements.get(d + 1)[i]) / rowSum[d] * Float.parseFloat(allElements.get(d + 1)[j]) / rowSum[d] * topicSimilarityCosine[i][j];
                        }
                    }
                }

            }

            PrintWriter out = new PrintWriter(Path);

            for (int i = 0; i < allDocs.size() - 1; i++) {
                out.printf("%f\n", diversityOfDocument[i]);
            }

            out.close();

        }

        for (int d = 0; d < allDocs.size(); d++) {
            String[] temp = allDocs.get(d);

            if (d == 0) {
                String[] xx = new String[temp.length + 1];
                for (int j = 0; j < temp.length; j++) {
                    xx[j] = temp[j];
                }
                xx[temp.length] = "diversity";

                allDocs.set(d, xx);
            } else {
                String[] xx = new String[temp.length + 1];
                for (int j = 0; j < temp.length; j++) {
                    xx[j] = temp[j];
                }
                xx[temp.length] = Float.toString(diversityOfDocument[d - 1]);

                allDocs.set(d, xx);

            }

        }

    }
}
