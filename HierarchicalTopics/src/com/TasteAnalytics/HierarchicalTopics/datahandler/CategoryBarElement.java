 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TasteAnalytics.HierarchicalTopics.datahandler;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import java.io.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Integer;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.TasteAnalytics.HierarchicalTopics.file.CSVReader;
import com.TasteAnalytics.HierarchicalTopics.gui.MinimalismMainFrame;
import com.TasteAnalytics.HierarchicalTopics.topicRenderer.TopicGraphViewFrame;
import com.TasteAnalytics.HierarchicalTopics.datahandler.LDAHTTPClient;
/**
 *
 * @author wdou
 */
public class CategoryBarElement {

    List<float[]> values_UnNorm;
    public List<float[]> values_Norm;//After reorganizing based on topic sequences
    List<Integer> year;
    List<Long> time;
    /**
     * Each row is a category - topic Each colum is a bar - year *
     */
    List<float[]> categoryBar;
    List<float[]> unormalized_categoryBar;
    public Map<Integer, List<Integer>> idxOfDocumentPerSlot;
    private List<List<int[]>> topicTFs;
    private List<float[]> termWeightF;
    public List<List<int[]>> topicYearKwIdx;
    private SortedMap map;
    private Iterator iterator;
    List<float[]> categoryBarSub;

    public List<Long> getTime() {
        return time;
    }

    public CategoryBarElement(List<String[]> internalRecord, List<Long> years,
            List<String[]> allDocs, List<String[]> termWeights, List<float[]> termWeights_norm, Map<String, Integer> termIndex, List<String[]> allTopics, String csvPath,
            int contentIdx, DateFormat format, float incrementalDays, boolean b_readall, boolean b_recalculate, int NumOfTemporalBinsSub,List<HashMap<String, Integer>> content) throws IOException {
        try {
            initiateComponents(internalRecord, years, allDocs, termWeights, termWeights_norm, termIndex, allTopics, csvPath, contentIdx,
                    format, incrementalDays, b_readall, b_recalculate, NumOfTemporalBinsSub,content);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CategoryBarElement.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int getBeginningYear() {
        return beginningYear;
    }

    public void setBeginningYear(int beginningYear) {
        this.beginningYear = beginningYear;
    }

    public List<float[]> getCategoryBar() {
        return categoryBar;
    }

    public void setCategoryBar(List<float[]> categoryBar) {
        this.categoryBar = categoryBar;
    }

    public List<float[]> getUnormCategoryBar() {
        return unormalized_categoryBar;
    }

    public int getEndYear() {
        return endYear;
    }

    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }
    int beginningYear, endYear;
    Long beginningTime;
    Long hr2ms;

    public Long getHr2ms() {
        return hr2ms;
    }

    public Long getTimeInterval() {
        return hr2ms;
    }

    public Long getBeginningTime() {
        return beginningTime;
    }

    public CategoryBarElement(String dataBaseName, String host) throws IOException {
        //initiateComponentsFromMongo(dataBaseName);
        initiateComponentsFromWeb(dataBaseName, host);
        //initComponents(tmpURL);
    }
    private int _numberOfTopics = 0;

    public int getNumberOfTopics() {
        return _numberOfTopics;
    }

    public void setNumberOfTopics(int _numberOfTopics) {
        this._numberOfTopics = _numberOfTopics;
    }
    private int _numOfTemporalBins = 0;

    public int getNumOfYears() {
        return _numOfTemporalBins;
    }

    public void setNumOfYears(int _numOfYears) {
        this._numOfTemporalBins = _numOfYears;
    }

    public List<Float> getColum(int year) {

        if (!categoryBar.isEmpty()) {
            List<Float> tmpColum = new ArrayList<Float>();

            for (float[] fs : categoryBar) {
                //System.out.println(fs[year]);
                tmpColum.add(fs[year]);
            }
            return tmpColum;
        } else {
            return null;

        }
    }

    public List<float[]> getCategoryBarSub() {
        return categoryBarSub;
    }

    private int countMatches(String str, String sub) {
        if (str.isEmpty() || sub.isEmpty()) {
            return 0;
        }
        int count = 0;
        int idx = 0;
        while ((idx = str.indexOf(sub, idx)) != -1) {
            count++;
            idx += sub.length();
        }
        return count;
    }
    Long maxT = Long.MIN_VALUE;
    Long minT = Long.MAX_VALUE;

    double topicWeightThreshold = 0.25;
    
    
    private void initiateComponents(List<String[]> internalRecord, List<Long> years,
            List<String[]> allDocs, List<String[]> termWeights, List<float[]> termWeights_norm, Map<String, Integer> termIndex,
            List<String[]> allTopics, String csvPath, int contentIdx, DateFormat format, float incrementalDays, boolean b_readall, boolean b_recalculate, int NumOfTemporalBinsSub,List<HashMap<String, Integer>> content) throws FileNotFoundException, IOException {

        _numOfTemporalBinsSub = NumOfTemporalBinsSub;
        List<Float> sumOfDocuments = new ArrayList<Float>();
        float sumOfDocument = 0;
        values_UnNorm = new ArrayList<float[]>();
        values_Norm = new ArrayList<float[]>();
        year = new ArrayList<Integer>();
        categoryBar = new ArrayList<float[]>();
        unormalized_categoryBar = new ArrayList<float[]>();
        idxOfDocumentPerSlot = new HashMap<Integer, List<Integer>>();
        time = years;

        hr2ms = (long) (incrementalDays * 86400000);//86400000L;

        if (format != null) {
            System.out.println("time format is " + ((SimpleDateFormat) format).toPattern());
        }

        if (format != null && "yyyy".equals(((SimpleDateFormat) format).toPattern())) {
            hr2ms = 1L;
        }

        System.out.println("hr2ms incremental days: " + incrementalDays);

        maxT = Long.MIN_VALUE;
        minT = Long.MAX_VALUE;
        int countMax = 0;
        for (Long t : time) {
            countMax++;
            if (t > maxT) {
                maxT = t;
            }

            if (t < minT) {
                minT = t;
            }
        }

        System.out.println("min time " + minT + " " + "min time " + maxT + " " + countMax);

        try {
            /**
             * Reorganize internal record based on the topic sequences
             */
            //tmpValues = internalRecord;

            System.out.println("internal record in catebar " + internalRecord.size());
            _numberOfTopics = internalRecord.get(0).length;

            //_numOfHours = (int) Math.ceil((time.get(time.size() - 1) - time.get(0)) / hr2ms);
            _numOfTemporalBins = (int) Math.ceil((maxT - minT) / hr2ms);
            _numOfTemporalBins = (int) Math.floor((maxT - minT) / hr2ms) + 1;
            System.out.println("Number of Slot " + _numOfTemporalBins);
            
            /**
             * Convert String to float value*
             */
            for (int i = 0; i < internalRecord.size(); i++) {
                float[] totalTopicNumbers;
                totalTopicNumbers = new float[internalRecord.get(0).length];
                sumOfDocument = 0;
                for (int j = 0; j < totalTopicNumbers.length; j++) {
                    totalTopicNumbers[j] = Float.parseFloat(internalRecord.get(i)[j]);
                    sumOfDocument += totalTopicNumbers[j];
                }

                values_UnNorm.add(totalTopicNumbers);
                sumOfDocuments.add(sumOfDocument);
            }

        } catch (Exception ex) {
            Logger.getLogger(CategoryBarElement.class.getName()).log(Level.SEVERE, null, ex);
        }

        termWeightF = termWeights_norm;

        try {
            float[] tmpNorm;
            for (int i = 0; i < values_UnNorm.size(); i++) {
                tmpNorm = new float[values_UnNorm.get(0).length];
                for (int j = 0; j < values_UnNorm.get(0).length; j++) {
                    tmpNorm[j] = values_UnNorm.get(i)[j] / sumOfDocuments.get(i);
                    //tmpNorm[j] = values_UnNorm.get(i)[topicSequence.get(j)]/sums.get(i);
                }
                values_Norm.add(tmpNorm);
            }

        } catch (Exception ex) {
            System.out.println("Prob with normalizing in CategoryBar");
        }

        
        System.out.println("normalizing in CategoryBar done" + values_Norm.size() + " " + values_Norm.get(0).length);
        /**
         * First row contain the column names; Column7 (start from 0) is year
         * info *
         */

        beginningTime = minT; //time.get(0);

        if (!values_UnNorm.isEmpty()) 
       {
            int numberOfTopics = _numberOfTopics;//values_UnNorm.get(0).length;//number of topics
            int numRecords = values_UnNorm.size();
          
            System.out.println("numRecords " + numRecords);

            float[] individualTopicOfTotalTime;
            float[] unormCategory;
            Long tmpTime = beginningTime;

            List<Integer> documentPerSlotVector;

            for (int j = 0; j < _numOfTemporalBins; j++) {
                documentPerSlotVector = new ArrayList<Integer>();
                idxOfDocumentPerSlot.put(j, documentPerSlotVector);
            }

            int numKeywords = 50;//TODO: hardcode alert!!
            String curKeyword;
            int[] numOccur;
            List<int[]> topicKTF = null;

            topicTFs = new ArrayList<List<int[]>>();

            ///////////////////////////////////
            //testFileExistance(csvPath);
            if (testFileExistance(csvPath) && !b_recalculate) {
                //idxByHours   Map<Integer, List<Integer>>
                //topicTFs      List<List<int[]>>
                //categoryBar  List<float[]>
                //unormalized_categoryBar   List<float[]>

                System.out.println("cache files exist, Loading...");

                //read in files
                categoryBar.clear();
                unormalized_categoryBar.clear();
                idxOfDocumentPerSlot.clear();
                topicTFs.clear();

                try {
                    System.out.println("cache files exist, Loading...  categoryBar.txt");
                    String strFilePath = csvPath + "categoryBar.txt";

                    FileReader reader = new FileReader(strFilePath);
                    BufferedReader in = new BufferedReader(reader);
                    for (int i = 0; i < _numberOfTopics; i++) {

                        String string = in.readLine();
                        String[] inputs = string.split("\\s");
                        float[] tempFloat = new float[inputs.length];
                        for (int j = 0; j < tempFloat.length; j++) {
                            tempFloat[j] = Float.parseFloat(inputs[j]);
                        }

                        categoryBar.add(tempFloat);
                    }
                    in.close();
                    reader.close();
                    System.out.println("cache files exist, Loading...  unormalized_categoryBar.txt");

                    strFilePath = csvPath + "unormalized_categoryBar.txt";

                    reader = new FileReader(strFilePath);
                    in = new BufferedReader(reader);
                    for (int i = 0; i < _numberOfTopics; i++) {

                        String string = in.readLine();
                        String[] inputs = string.split("\\s");
                        float[] tempFloat = new float[inputs.length];
                        for (int j = 0; j < tempFloat.length; j++) {
                            tempFloat[j] = Float.parseFloat(inputs[j]);
                        }

                        unormalized_categoryBar.add(tempFloat);
                    }
                    in.close();
                    reader.close();

                    System.out.println("cache files exist, Loading...  topicTFs.txt");
                    strFilePath = csvPath + "topicTFs.txt";

                    reader = new FileReader(strFilePath);
                    in = new BufferedReader(reader);
                    for (int i = 0; i < _numberOfTopics; i++) {
                        List<int[]> tempList = new ArrayList<int[]>();

                        for (int k = 0; k < numKeywords; k++) {
                            String string = in.readLine();
                            String[] inputs = string.split("\\s");
                            int[] tempFloat = new int[inputs.length];
                            for (int j = 0; j < tempFloat.length; j++) {
                                tempFloat[j] = Integer.parseInt(inputs[j]);
                            }

                            tempList.add(tempFloat);
                        }

                        topicTFs.add(tempList);
                    }

                    in.close();
                    reader.close();
                    System.out.println("cache files exist, Loading...  idxByHours.txt");

                    strFilePath = csvPath + "idxByHours.txt";

                    reader = new FileReader(strFilePath);
                    in = new BufferedReader(reader);

                    for (int i = 0; i < _numOfTemporalBins; i++) {
                        List<Integer> tempList = new ArrayList<Integer>();

                        String string = in.readLine();
                        String[] inputs = string.split("\\s");
                        Integer ii = new Integer(inputs[0]);
                        for (int j = 1; j < inputs.length; j++) {
                            tempList.add(Integer.parseInt(inputs[j]));
                        }

                        idxOfDocumentPerSlot.put(ii, tempList);

                    }

                    in.close();
                    reader.close();

                    System.out.println("All cache files Loaded..");

                } catch (IOException ex) {
                    Logger.getLogger(CategoryBarElement.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else {

                System.out.println("Cache files missing! Re-Calculating Cache...");

                for (int i = 0; i < numberOfTopics; i++) {//number of topics
                    individualTopicOfTotalTime = new float[_numOfTemporalBins];

                    //Initialize the current array
                    for (int q = 0; q < _numOfTemporalBins; q++) {//number of time slots
                        individualTopicOfTotalTime[q] = 0;
                    }
                    for (int k = 0; k < numRecords; k++) {

                        if (values_UnNorm.get(k) != null) {
                            for (int j = 0; j < _numOfTemporalBins; j++) {
                                if (time.get(k) >= (tmpTime + j * hr2ms) && time.get(k) < (tmpTime + (j + 1) * hr2ms)) {

                                    individualTopicOfTotalTime[j] += values_UnNorm.get(k)[i];//i - ith topic
                                    //Do this only once, not for every topic  tf idf key word weight init
                                    if (i == 0) {
                                        idxOfDocumentPerSlot.get(j).add(k);
                                    }
                                }
                            }
                        }
                    }

                    topicKTF = new ArrayList<int[]>();
                    /*
                     * Count TF for every keyword for every year
                     */
                    for (int t = 0; t < numKeywords; t++) {

                        curKeyword = allTopics.get(i + 1)[t + 2].trim();

                        //curKeyword = curKeyword.replaceAll("_", " ");
                        numOccur = new int[_numOfTemporalBins];
                        for (int y = 0; y < _numOfTemporalBins; y++) {
                            int count = 0;
                            int tmpid = 0;
                            for (int l = 0; l < idxOfDocumentPerSlot.get(y).size(); l++) {
                                tmpid = idxOfDocumentPerSlot.get(y).get(l);
                                if (contentIdx != -1) {
                                    
//                                    if (values_Norm.get(tmpid)[i] > topicWeightThreshold)
//                                        count += countMatches(allDocs.get(tmpid + 1)[contentIdx].toLowerCase(), curKeyword);
                                    // for bi gram
                                     if (values_Norm.get(tmpid)[i] > topicWeightThreshold)
                                     {
                                         
                                         if (content.get(tmpid).containsKey(curKeyword))
                                         {
                                             
                                             count += content.get(tmpid).get(curKeyword);
                                         
                                         }
                                         //count += countMatches(allDocs.get(tmpid + 1)[contentIdx].toLowerCase(), curKeyword);
                                     }
                                    
                                    
                                    
                                }
                            }
                            numOccur[y] = count;
                        }
                        topicKTF.add(numOccur);//each array corresponds to a year
                    }
                    //}

                    categoryBar.add(individualTopicOfTotalTime);
                    topicTFs.add(topicKTF);

                    unormCategory = individualTopicOfTotalTime.clone();
                    unormalized_categoryBar.add(unormCategory);
                }

                {

                    System.out.println("output  unormalized_categoryBar.txt");
                    PrintWriter out = new PrintWriter(csvPath + "unormalized_categoryBar.txt");
                    for (int j = 0; j < unormalized_categoryBar.size(); j++) {
                        for (int i = 0; i < unormalized_categoryBar.get(j).length; i++) {
                            out.printf("%g ", unormalized_categoryBar.get(j)[i]);
                        }
                        out.printf("\n");
                    }
                    out.close();

                    System.out.println("Normalizing theme river..");
                    float[] tempSum = new float[_numOfTemporalBins];
                    float[] normTempSum = new float[_numOfTemporalBins];
                    float maxSum = 0;
                    for (int i = 0; i < _numOfTemporalBins; i++) {
                        tempSum[i] = 0;
                        for (int j = 0; j < numberOfTopics; j++) {
                            tempSum[i] += categoryBar.get(j)[i];
                        }
                        if (maxSum < tempSum[i]) {
                            maxSum = tempSum[i];
                        }
                    }
                    /**
                     * Normalize across columns*
                     */
                    for (int i = 0; i < _numOfTemporalBins; i++) {
                        normTempSum[i] = tempSum[i] / maxSum;
                    }
                    /**
                     * Normalize within columns*
                     */
                    for (int i = 0; i < _numOfTemporalBins; i++) {
                        for (int j = 0; j < numberOfTopics; j++) {
                            if (tempSum[i] != 0) {
                                categoryBar.get(j)[i] = categoryBar.get(j)[i] / tempSum[i] * normTempSum[i];
                            } else {
                                categoryBar.get(j)[i] = 0;//Don't know if this is right
                            }

                        }
                    }

                    System.out.println("Normalizing theme river finished.");
                    System.out.println("Output categoryBar.txt");
                    out = new PrintWriter(csvPath + "categoryBar.txt");
                    for (int j = 0; j < categoryBar.size(); j++) {
                        for (int i = 0; i < categoryBar.get(j).length; i++) {
                            out.printf("%g ", categoryBar.get(j)[i]);
                        }
                        out.printf("\n");
                    }
                    out.close();

                    System.out.println("output  c.txt");
                    out = new PrintWriter(csvPath + "topicTFs.txt");
                    for (int k = 0; k < topicTFs.size(); k++) {
                        for (int j = 0; j < topicTFs.get(k).size(); j++) {
                            for (int i = 0; i < topicTFs.get(k).get(j).length; i++) {
                                out.printf("%d ", topicTFs.get(k).get(j)[i]);
                            }
                            out.printf("\n");
                        }
                    }
                    out.close();

                    System.out.println("output idxByHours.txt");
                    out = new PrintWriter(csvPath + "idxByHours.txt");

                    for (Integer key : idxOfDocumentPerSlot.keySet()) {
                        //System.out.println("Key = " + key);
                        out.printf("%d ", key);
                        List<Integer> x = idxOfDocumentPerSlot.get(key);
                        for (int j = 0; j < x.size(); j++) {
                            out.printf("%d ", x.get(j));
                        }
                        out.printf("\n");

                    }

                    out.close();

                    System.out.println("Output done!");

                }

            }

            /////////////////////////////////////////////////////////////////////////////////
            //topicYearKwIdax load
            String filepathtyki = csvPath + "topicYearKwIdx.txt";
            File f1 = new File(filepathtyki);
            if (false/*f1.exists()*/) {
                topicYearKwIdx = new ArrayList<List<int[]>>();

                System.out.println("cache files exist, Loading topicYearKwIdx.txt... ");

                FileReader reader = new FileReader(filepathtyki);
                BufferedReader in = new BufferedReader(reader);
                for (int i = 0; i < _numberOfTopics; i++) {
                    List<int[]> tempList = new ArrayList<int[]>();

                    for (int k = 0; k < _numOfTemporalBins; k++) {
                        String string = in.readLine();
                        String[] inputs = string.split("\\s");
                        int[] tempFloat = new int[inputs.length];
                        for (int j = 0; j < tempFloat.length; j++) {
                            tempFloat[j] = Integer.parseInt(inputs[j]);
                        }

                        tempList.add(tempFloat);
                    }

                    topicYearKwIdx.add(tempList);
                }

                in.close();
                reader.close();

            } else {

                System.out.println("topicYearKwIdx calculating...");
                int[] tmp4;
                float[] tmpAllKeywords;
                float tmpWeight = 0, tmpWeightSum = 0, tmpWeightProduct = 1;
                int tmpCol = 0;
                List<int[]> topKeywordByYear;
                topicYearKwIdx = new ArrayList<List<int[]>>();

                if (b_readall) {
                    for (int i = 0; i < numberOfTopics; i++) {
                        /*
                         * For every topic, pick top 4 keyword for every time frame
                         */
                        
                        topKeywordByYear = new ArrayList<int[]>();//for each topic
                        try {
                            for (int y = 0; y < _numOfTemporalBins; y++) {

                                
                                tmp4 = new int[5];
                                tmpAllKeywords = new float[numKeywords];

                                for (int k = 0; k < numKeywords; k++) {

                                    curKeyword = allTopics.get((i) + 1)[k + 2].trim().toLowerCase();

                                    
                                    
                                    //System.out.println(curKeyword);
                                      tmpCol = termIndex.get(curKeyword);
                                   tmpWeight = (termWeightF.get(i)[tmpCol]);

                                   for (int n = 0; n < _numOfTemporalBins; n++) {
                                       tmpWeightSum += topicTFs.get(i).get(k)[n];
                                   }

                                   for (int m = 0; m < numberOfTopics; m++) {
                                       if (termWeightF.get(m)[tmpCol]!=0)
                                           tmpWeightProduct = tmpWeightProduct * termWeightF.get(m)[tmpCol];
                                   }

                                   tmpWeightProduct = (float) Math.pow(tmpWeightProduct, 1.0 / (float)numberOfTopics);//cast to float
                                   tmpWeightProduct = (float) (tmpWeight * Math.log(tmpWeight / tmpWeightProduct));
                                   
                                   if(tmpWeightSum > 0){
                                       tmpAllKeywords[k] = (float) ((1.0 * topicTFs.get(i).get(k)[y] / tmpWeightSum) ); //+ 0.1 * tmpWeightProduct);
                                   }
                                   else{
                                       tmpAllKeywords[k] = - Float.MAX_VALUE;
                                       //tmpAllKeywords[k] = (float) (0.1 * tmpWeightProduct);
                                   }
                     
                                   tmpWeightSum = 0;
                                   tmpWeightProduct = 1;
                       
                                }

  
                                List<compFloat> tmparray = new ArrayList<compFloat>();
                                for (int m = 0; m < tmpAllKeywords.length; m++) {

                                    if (!Float.isNaN(tmpAllKeywords[m])) {
                                
                                        compFloat cmf = new compFloat(m, tmpAllKeywords[m]);
                                        tmparray.add(cmf);
                                    }

                                }

                                FloatComparer c = new FloatComparer();
                                Collections.sort(tmparray, c);

                                int len = tmparray.size()>=tmp4.length?tmp4.length:tmparray.size();
                                
                                for (int m = 0; m < tmp4.length; m++) {
                                    tmp4[m] = -1;
                                }
                                for (int m = 0; m < len; m++) {

                                 
                                        tmp4[m] = tmparray.get(m).index;
                                 
                                }

                                topKeywordByYear.add(tmp4);
                            }
                        } catch (Exception e) {
                            System.out.println(e);
                            System.out.println("topicYearKwIdx error " + i + "th key word " );

                        }

                        topicYearKwIdx.add(topKeywordByYear);
                    }
                }

                System.out.println("topicYearKwIdx calculated..output cache files");

                PrintWriter ofp = new PrintWriter(csvPath + "topicYearKwIdx.txt");
                for (int i = 0; i < topicYearKwIdx.size(); i++) {
                    for (int j = 0; j < topicYearKwIdx.get(i).size(); j++) {
                        for (int k = 0; k < topicYearKwIdx.get(i).get(j).length; k++) {
                            ofp.print(topicYearKwIdx.get(i).get(j)[k] + " ");
                        }

                        ofp.print("\n");
                    }

                }

                ofp.close();

            }
 System.out.println("topicYearKwIdx done");
            /**
             * Normalize for the themeRiver - Normalize twice - first across
             * columns then within columns*
             */
            /**
             * year1 year2 year3 T1 T2 T3
             */
//            System.out.println("Normalizing theme river..");
//            float[] tempSum = new float[_numOfTemporalBins];
//            float[] normTempSum = new float[_numOfTemporalBins];
//            float maxSum = 0;
//            for (int i = 0; i < _numOfTemporalBins; i++) {
//                tempSum[i] = 0;
//                for (int j = 0; j < numberOfTopics; j++) {
//                    tempSum[i] += categoryBar.get(j)[i];
//                }
//                if (maxSum < tempSum[i]) {
//                    maxSum = tempSum[i];
//                }
//            }
//            /**
//             * Normalize across columns*
//             */
//            for (int i = 0; i < _numOfTemporalBins; i++) {
//                normTempSum[i] = tempSum[i] / maxSum;
//            }
//            /**
//             * Normalize within columns*
//             */
//            for (int i = 0; i < _numOfTemporalBins; i++) {
//                for (int j = 0; j < numberOfTopics; j++) {
//                    if (tempSum[i] != 0) {
//                        categoryBar.get(j)[i] = categoryBar.get(j)[i] / tempSum[i] * normTempSum[i];
//                    } else {
//                        categoryBar.get(j)[i] = 0;//Don't know if this is right
//                    }
//
//                }
//            }
//
//            System.out.println("Normalizing theme river finished.");
        }
    }

    private void initiateComponentsFromMongo(String MongoDBJobName) throws FileNotFoundException, IOException {

        MongoClient mongoClient = null;
        try {
            mongoClient = new MongoClient("10.18.203.211", 27017);
        } catch (UnknownHostException ex) {
            System.out.println("DB begin load cache error");
        }

        DB db = mongoClient.getDB("lda_results");
        DBCollection dbc = db.getCollection("job_index");

        BasicDBObject q1 = new BasicDBObject("_id", MongoDBJobName);
        DBCursor cursor = dbc.find(q1);
         float incrementalDays = 0;
         System.out.println(q1);
        while (cursor.hasNext())
        {
            
            
             BasicDBObject dbo1 = (BasicDBObject) cursor.next();
             System.out.println(dbo1);
             int _numberOfDocs = Integer.parseInt(dbo1.getString("num_docs"));
             minT = Long.parseLong(dbo1.getString("min_year"));
             maxT = Long.parseLong(dbo1.getString("max_year"));
             incrementalDays = Float.parseFloat(dbo1.getString("incremental_days"));
             String s = ((BasicDBObject) dbo1.get("mongo_input")).getString("date_format");
            _numberOfTopics = Integer.parseInt(((BasicDBObject) dbo1.get("meta")).getString("num_topics"));

        }
       
        
        
        DBCollection currentColl = db.getCollection(MongoDBJobName);

        categoryBar = new ArrayList<float[]>();
        unormalized_categoryBar = new ArrayList<float[]>();
        idxOfDocumentPerSlot = new HashMap<Integer, List<Integer>>();

        hr2ms = (long) (incrementalDays * 86400000);//86400000L;

        _numOfTemporalBins = (int) Math.ceil((maxT - minT) / hr2ms);
        _numOfTemporalBins = (int) Math.floor((maxT - minT) / hr2ms) + 1;

        beginningTime = minT; //time.get(0);
      
        int numKeywords = 30;//TODO: hardcode alert!!

        List<int[]> topicKTF = null;

        topicTFs = new ArrayList<List<int[]>>();

        System.out.println("Loading cache files from mongo ...");

        //read in files
        categoryBar.clear();
        unormalized_categoryBar.clear();
        idxOfDocumentPerSlot.clear();
        topicTFs.clear();
        
//        BasicDBObject andQuery = new BasicDBObject();
//	List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
//	obj.add(new BasicDBObject("number", 2));
//	obj.add(new BasicDBObject("name", "mkyong-2"));
//	andQuery.put("$and", obj);
// 
//	System.out.println(andQuery.toString());
// 
//	DBCursor cursor = collection.find(andQuery);
//	while (cursor.hasNext()) {
//		System.out.println(cursor.next());
//	}
        BasicDBObject dbo = new BasicDBObject("type", "cat_bar");
        DBCursor cursorfind = currentColl.find(dbo);
        HashMap<String, float[]> cateBarMongo = new HashMap<String, float[]>();
        while (cursorfind.hasNext())
        {
            BasicDBObject tmpDBO = (BasicDBObject) cursorfind.next();
            String key = tmpDBO.getString("_id");
            String weights = tmpDBO.getString("weights");
            String[] tmp = weights.split(",");
            float[] tmpvalue = new float[tmp.length];
            for (int i=0; i<tmp.length; i++)
                tmpvalue[i] = Float.parseFloat(tmp[i]);
            
            cateBarMongo.put(key, tmpvalue);                       
        }               
        
        for (int i=0; i<cateBarMongo.size(); i++)
        {
                String key = "tb" + (new Integer(i)).toString();
                categoryBar.add(cateBarMongo.get(key));                         
        }
        
        System.out.println("categoryBar loaded");
        
        
        BasicDBObject dboUn = new BasicDBObject("type", "unorm_cat_bar");
        cursorfind = currentColl.find(dboUn);
        HashMap<String, float[]> cateBarMongoUn = new HashMap<String, float[]>();
        while (cursorfind.hasNext())
        {
            BasicDBObject tmpDBO = (BasicDBObject) cursorfind.next();
            String key = tmpDBO.getString("_id");
            String weights = tmpDBO.getString("weights");
            String[] tmp = weights.split(",");
            float[] tmpvalue = new float[tmp.length];
            for (int i=0; i<tmp.length; i++)
                tmpvalue[i] = Float.parseFloat(tmp[i]);
            
            cateBarMongoUn.put(key, tmpvalue);                       
        }               
        
        for (int i=0; i<cateBarMongoUn.size(); i++)
        {
                String key = "utb" + (new Integer(i)).toString();
                unormalized_categoryBar.add(cateBarMongoUn.get(key));                         
        }
        
        System.out.println("unormalized_categoryBar loaded");
        
        BasicDBObject dboTFs = new BasicDBObject("type", "top_tf");
        HashMap<String, int[]> tfsMongo = new HashMap<String, int[]>();
        cursorfind = currentColl.find(dboTFs);
         while (cursorfind.hasNext())
        {
             BasicDBObject tmpDBO = (BasicDBObject) cursorfind.next();
             String key = tmpDBO.getString("_id");
             String weights = tmpDBO.getString("weights");
            String[] tmp = weights.split(",");
            int[] tmpvalue = new int[tmp.length];
            for (int i=0; i<tmp.length; i++)
                tmpvalue[i] = Integer.parseInt(tmp[i]);
            
            tfsMongo.put(key, tmpvalue); 
            //System.out.println(tmpDBO);
            
        }
                     
        
         
        for (int i=0; i<tfsMongo.size(); i++)
        {
            List<int[]> tmpL = new ArrayList<int[]>();
            for (int j=0; j<numKeywords; j++)
            {                                
                String key = "top" + (new Integer(i)).toString() + "term" + (new Integer(j)).toString();
                tmpL.add(tfsMongo.get(key));   
            }
            topicTFs.add(tmpL);
            
        }
        
         System.out.println("topicTFs  loaded");
        
        BasicDBObject dboidxbyh = new BasicDBObject("type", "idx_slot");
        HashMap<String, int[]> idxbyMongo = new HashMap<String, int[]>();
        cursorfind = currentColl.find(dboidxbyh);
         while (cursorfind.hasNext())
        {
             BasicDBObject tmpDBO = (BasicDBObject) cursorfind.next();
//             System.out.println(tmpDBO);
  
             String weights = tmpDBO.getString("weights");
            String[] tmp = weights.split(",");
            int[] tmpvalue = new int[tmp.length];
            String key = tmp[0];
            for (int i=0; i<(tmp.length-1); i++)
                tmpvalue[i] = Integer.parseInt(tmp[i+1]);
            
            
            idxbyMongo.put(key, tmpvalue); 
            //System.out.println(tmpDBO);
            
        }
                          
        
        for (Map.Entry<String, int[]> entry : idxbyMongo.entrySet()) 
        { 
            int Key = Integer.parseInt(entry.getKey());
            int[] tmp = entry.getValue();
            List<Integer> tmpl = new ArrayList<Integer>();
            for (int i=0; i<tmp.length; i++)
            {
                tmpl.add(tmp[i]);
            }
            idxOfDocumentPerSlot.put(Key, tmpl);
            
        }
        
         System.out.println("idxOfDocumentPerSlot loaded");
              
         
         BasicDBObject dbotyk = new BasicDBObject("type", "top_y_kw_idx");
        HashMap<String, int[]> tykbyMongo = new HashMap<String, int[]>();
        cursorfind = currentColl.find(dbotyk);
         while (cursorfind.hasNext())
        {
             BasicDBObject tmpDBO = (BasicDBObject) cursorfind.next();
            // System.out.println(tmpDBO);
                                  
             String key = tmpDBO.getString("_id");
             String weights = tmpDBO.getString("top_terms");
            String[] tmp = weights.split(",");
            int[] tmpvalue = new int[tmp.length];
            for (int i=0; i<tmp.length; i++)
                tmpvalue[i] = Integer.parseInt(tmp[i]);
            
            tykbyMongo.put(key, tmpvalue); 
            
            
            //System.out.println(tmpDBO);
            
        }
         
         
                 topicYearKwIdx = new ArrayList<List<int[]>>();

        for (int i = 0; i < _numberOfTopics; i++) {
            List<int[]> tempList = new ArrayList<int[]>();

            for (int k = 0; k < _numOfTemporalBins; k++) {
               
                String Key = "t" + Integer.toString(i) +"b" + Integer.toString(k);             
                
                tempList.add(tykbyMongo.get(Key));
            }

            topicYearKwIdx.add(tempList);
        }
         
          System.out.println("topicYearKwIdx loaded");
         

          System.out.println("loading cache files from mongo, done!");

    }
    
    
    private void initiateComponentsFromWeb(String JobName, String host) throws FileNotFoundException, IOException {

//        MongoClient mongoClient = null;
//        try {
//            mongoClient = new MongoClient("10.18.203.211", 27017);
//        } catch (UnknownHostException ex) {
//            System.out.println("DB begin load cache error");
//        }
//
//        DB db = mongoClient.getDB("lda_results");
//        DBCollection dbc = db.getCollection("job_index");
//
//        BasicDBObject q1 = new BasicDBObject("_id", MongoDBJobName);
//        DBCursor cursor = dbc.find(q1);
         float incrementalDays = 0;

         LDAHTTPClient c  = new LDAHTTPClient("http",host, "2012");
//         for (Object r : (ArrayList) c.getJobDocsMeta(JobName, JobName))
//			System.out.println(r);
         
         c.login(true, null, null);
         
         for (Object r : (ArrayList) c.getJob(JobName))
         {
             //System.out.println(r);
             HashMap hr = (HashMap) r;
             String snumbOfDocs = String.valueOf(hr.get("num_docs"));
//             if (snumbOfDocs.contains("."))
//             {
//                 
//             }  
//             int _numberOfDocs = 0;
             minT = (Long)(hr.get("min_year"));
             maxT = (Long)(hr.get("max_year"));
             Object to = (hr.get("incremental_days"));
             if (to instanceof Integer)
             {
                 int a = (Integer)to;
                 incrementalDays = (float) a;
             }  
             else if (to instanceof Double)
             {
                 double a = (Double) to;
                 incrementalDays = (float) a;
             }
                 
             else
             {
                incrementalDays = new Float((String) to);
             }
             //String s = ((BasicDBObject) hr.get("mongo_input")).get("date_format");
             _numberOfTopics = Integer.parseInt((String)(((HashMap)(hr.get("meta"))).get("num_topics")));
             
             
         }
         


        categoryBar = new ArrayList<float[]>();
        unormalized_categoryBar = new ArrayList<float[]>();
        idxOfDocumentPerSlot = new HashMap<Integer, List<Integer>>();

        hr2ms = (long) (incrementalDays * 86400000);//86400000L;

        _numOfTemporalBins = (int) Math.ceil((maxT - minT) / hr2ms);
        _numOfTemporalBins = (int) Math.floor((maxT - minT) / hr2ms) + 1;

        beginningTime = minT; //time.get(0);
      
        int numKeywords = 30;//TODO: hardcode alert!!

        List<int[]> topicKTF = null;

        topicTFs = new ArrayList<List<int[]>>();

        System.out.println("Loading cache files from mongo ...");

        //read in files
        categoryBar.clear();
        unormalized_categoryBar.clear();
        idxOfDocumentPerSlot.clear();
        topicTFs.clear();
         HashMap<String, float[]> cateBarMongo = new HashMap<String, float[]>();
          HashMap<String, float[]> cateBarMongoUn = new HashMap<String, float[]>();
           HashMap<String, int[]> idxbyMongo = new HashMap<String, int[]>();
          HashMap<String, int[]> tfsMongo = new HashMap<String, int[]>();
            HashMap<String, int[]> tykbyMongo = new HashMap<String, int[]>();
            
            
            
         for (Object r : (ArrayList) c.getJobDocs(JobName, "cat_bar"))
         {
             HashMap hr = (HashMap) r;
                    
             
                    String key = (String) hr.get("_id");
                    String weights = (String) hr.get("weights");
                    
                  String[] tmp = weights.split(",");
            float[] tmpvalue = new float[tmp.length];
             for (int i=0; i<tmp.length; i++)
                tmpvalue[i] = Float.parseFloat(tmp[i]);
             
            for (int i=0; i<tmp.length; i++)
                    cateBarMongo.put(key, tmpvalue);  
             
         }
                   
        
        for (int i=0; i<cateBarMongo.size(); i++)
        {
                String key = "tb" + (new Integer(i)).toString();
                categoryBar.add(cateBarMongo.get(key));                         
        }
        
        System.out.println("categoryBar loaded");
         
        for (Object r : (ArrayList) c.getJobDocs(JobName, "unorm_cat_bar"))
        {
             HashMap hr = (HashMap) r;
             String key = (String) hr.get("_id");
                    String weights = (String) hr.get("weights");
            String[] tmp = weights.split(",");
            float[] tmpvalue = new float[tmp.length];
            for (int i=0; i<tmp.length; i++)
                tmpvalue[i] = Float.parseFloat(tmp[i]);
            
            cateBarMongoUn.put(key, tmpvalue);             
        }
        
        for (int i=0; i<cateBarMongoUn.size(); i++)
        {
                String key = "utb" + (new Integer(i)).toString();
                unormalized_categoryBar.add(cateBarMongoUn.get(key));                         
        }
        
        System.out.println("unormalized_categoryBar loaded");
        
   
          for (Object r : (ArrayList) c.getJobDocs(JobName, "top_tf"))
        {
               HashMap hr = (HashMap) r;
             String key = (String) hr.get("_id");
                    String weights = (String) hr.get("weights");
            String[] tmp = weights.split(",");
            int[] tmpvalue = new int[tmp.length];
            for (int i=0; i<tmp.length; i++)
                tmpvalue[i] = Integer.parseInt(tmp[i]);
            
            tfsMongo.put(key, tmpvalue); 
           
            
        }
                     
        
         
        for (int i=0; i<tfsMongo.size(); i++)
        {
            List<int[]> tmpL = new ArrayList<int[]>();
            for (int j=0; j<numKeywords; j++)
            {                                
                String key = "top" + (new Integer(i)).toString() + "term" + (new Integer(j)).toString();
                tmpL.add(tfsMongo.get(key));   
            }
            topicTFs.add(tmpL);
            
        }
        
         System.out.println("topicTFs  loaded");
        
       
        for (Object r : (ArrayList) c.getJobDocs(JobName, "idx_slot"))
        {
               HashMap hr = (HashMap) r;
             
                    String weights = (String) hr.get("weights");
                     String[] tmp = weights.split(",");
            int[] tmpvalue = new int[tmp.length];
            String key = tmp[0];
            for (int i=0; i<(tmp.length-1); i++)
                tmpvalue[i] = Integer.parseInt(tmp[i+1]);
            
            
            idxbyMongo.put(key, tmpvalue); 
        }
       
       
  

                          
        
        for (Map.Entry<String, int[]> entry : idxbyMongo.entrySet()) 
        { 
            int Key = Integer.parseInt(entry.getKey());
            int[] tmp = entry.getValue();
            List<Integer> tmpl = new ArrayList<Integer>();
            for (int i=0; i<tmp.length; i++)
            {
                tmpl.add(tmp[i]);
            }
            idxOfDocumentPerSlot.put(Key, tmpl);
            
        }
        
         System.out.println("idxOfDocumentPerSlot loaded");
              
          for (Object r : (ArrayList) c.getJobDocs(JobName, "top_y_kw_idx"))
          {
               HashMap hr = (HashMap) r;
                    
             
                    String key = (String) hr.get("_id");
               String weights = (String) hr.get("top_terms");
                String[] tmp = weights.split(",");
                 int[] tmpvalue = new int[tmp.length];
            for (int i=0; i<tmp.length; i++)
                tmpvalue[i] = Integer.parseInt(tmp[i]);
            
            tykbyMongo.put(key, tmpvalue); 
          }
      
       
           
    
         
         
                 topicYearKwIdx = new ArrayList<List<int[]>>();

        for (int i = 0; i < _numberOfTopics; i++) {
            List<int[]> tempList = new ArrayList<int[]>();

            for (int k = 0; k < _numOfTemporalBins; k++) {
               
                String Key = "t" + Integer.toString(i) +"b" + Integer.toString(k);             
                
                tempList.add(tykbyMongo.get(Key));
            }

            topicYearKwIdx.add(tempList);
        }
         
        
       c.close();
          System.out.println("topicYearKwIdx loaded");
         

          System.out.println("loading cache files from mongo, done!");

    }

    public boolean testFileExistance(String path) {

        File f1 = new File(path + "categoryBar.txt");
        File f2 = new File(path + "unormalized_categoryBar.txt");
        File f3 = new File(path + "idxByHours.txt");
        File f4 = new File(path + "topicTFs.txt");

        return (f1.exists() && f2.exists() && f3.exists() && f4.exists());

    }

    int _numOfTemporalBinsSub = 10;

    public int getNumOfTemporalBinsSub() {
        return _numOfTemporalBinsSub;
    }

    public void setNumOfTemporalBins(int _numOfTemporalBins) {
        this._numOfTemporalBins = _numOfTemporalBins;
    }

    long sub_timeInterval;
    long subStartTime;

    public long getSub_timeInterval() {
        return sub_timeInterval;
    }

    public long getSubStartTime() {
        return subStartTime;
    }

    public void calculateSubCategoryBar(int timeslot) throws FileNotFoundException, IOException {

        categoryBarSub = new ArrayList<float[]>();
        categoryBarSub.clear();
        //maxT                
        //minT               

        subStartTime = minT + timeslot * hr2ms;

        long timeInterval = hr2ms / _numOfTemporalBinsSub;
        sub_timeInterval = timeInterval;

        int numberOfTopics = values_UnNorm.get(0).length;//number of topics
        int numRecords = values_UnNorm.size();
        float[] individualTopicOfTotalTime;

        Long tmpTime = subStartTime;
        int count = 0;

        for (int i = 0; i < numberOfTopics; i++) {
            //number of topics
            individualTopicOfTotalTime = new float[_numOfTemporalBinsSub];
            for (int q = 0; q < _numOfTemporalBinsSub; q++) {//number of time slots
                individualTopicOfTotalTime[q] = 0;
            }

            for (int j = 0; j < _numOfTemporalBinsSub; j++) {

                // List<Integer> templist =new  ArrayList<Integer>();
                count = 0;
                
                
                for (int k = 0; k < numRecords; k++) {
                    if (values_UnNorm.get(k) != null) {

                        if (time.get(k) >= (tmpTime + j * timeInterval) && time.get(k) < (tmpTime + (j + 1) * timeInterval)) {

                            individualTopicOfTotalTime[j] += values_UnNorm.get(k)[i];
                            count++;
                            //templist.add(k);
                        }
                    }
                }
                System.out.println(count + "!!");
            }

            categoryBarSub.add(individualTopicOfTotalTime);

        }

        System.out.println("slot: " + timeslot + " " + (count /= numberOfTopics) + "in this slot" + categoryBarSub.size() + " " + categoryBarSub.get(1).length);

    }

    class compFloat {

        compFloat(int x, float y) {
            index = x;
            value = y;
        }
        int index;
        float value;
    }

    public class FloatComparer implements Comparator<compFloat> {

        // I don't know why this isn't in Long...
//        private int compare(float a, float b) {
//            return a > b ? -1
//                    : a < b ? 1
//                    : 0;
//        }
//        public int compare(Float x, Float y) {
//            int startComparison = compare(x, y);
//            return startComparison != 0 ? startComparison
//                    : compare(x, y);
//
//            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        }
        public int compare(compFloat x, compFloat y) {
            int startComparison = compare(x.value, y.value);
            return startComparison != 0 ? startComparison
                    : compare(x.value, y.value);

            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public int compare(Float a, Float b) {
            return a > b ? -1
                    : a < b ? 1
                    : 0;
            // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
}
