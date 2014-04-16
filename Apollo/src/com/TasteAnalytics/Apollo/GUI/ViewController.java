/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TasteAnalytics.Apollo.GUI;

import com.TasteAnalytics.Apollo.TemporalView.TemporalViewFrame;
import com.TasteAnalytics.Apollo.TemporalView.TemporalViewListener;
import com.TasteAnalytics.Apollo.TemporalView.TemporalViewPanel;
import com.TasteAnalytics.Apollo.TemporalView.TreeNode;
import com.TasteAnalytics.Apollo.TopicRenderer.LabelText;
import com.TasteAnalytics.Apollo.TopicRenderer.TopicGraphViewPanel;
import com.TasteAnalytics.Apollo.TopicRenderer.TopicGraphViewPanel.labelTextComparer;
import com.TasteAnalytics.Apollo.TopicRenderer.VastGeoFrame;
import com.TasteAnalytics.Apollo.TopicRenderer.WorldMapProcessingPanel;
import com.TasteAnalytics.Apollo.TreeMapView.TopicTreeMapPanel;
import com.TasteAnalytics.Apollo.TreeMapView.TreeMapNodePanel;
import com.TasteAnalytics.Apollo.datahandler.CategoryBarElement;
import com.TasteAnalytics.Apollo.eventsview.EventViewFrame;
import com.TasteAnalytics.Apollo.eventsview.EventsViewListener;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.picking.PickedState;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;
import javax.swing.JLabel;
import org.apache.commons.lang.StringUtils;
import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Table;

/**
 * To coordinate between views
 *
 * @author wdou
 */
public class ViewController {

    public TemporalViewListener tvl;
    public EventsViewListener evl;
    public DocumentViewer docViewer;
    int contentIdx;
    public TemporalViewFrame tf;
    public TopicGraphViewPanel topicGraphicPanel;
    private List<String[]> InternalUsageRecord;
    private List<String[]> InternalDocs;
    private List<Float> topicSimilarities;

    public boolean topicChanged = false;
    private DateFormat format;
    public String csvfFolderPath;
    public VastGeoFrame VCGF;
    public WorldMapProcessingPanel worldMapProcessingFrame;
    public List<Point2D> geoLocations;

    
    public List<Float> topicWeights = new ArrayList<Float>();
    public List<Float> topicEventsCount = new ArrayList<Float>();
    public HashMap<Integer, TreeNode.SentimentModel> sen = new HashMap<Integer, TreeNode.SentimentModel> ();
    
    public List<TreeNode> myTree;
    public CategoryBarElement data;
    public HashMap<Integer, TreeNode> leaves = new HashMap<Integer,TreeNode>();
     
     
     
     public void loadCacheData(String databaseName, String TreeString, String host) throws IOException
    {

        data = new CategoryBarElement(databaseName, host);
        myTree = new ArrayList<TreeNode>();
        buildTreeWithString(TreeString);

        BuildNodeValue(data, myTree.get(0));
        BuildUnNormNodeValue(data, myTree.get(0));

        myTree.get(0).calculateNodeContainedIdx();
    
      
        setNodeColor();
      
        
        

    } 
     
     private List<Float[]> colorMap;
      public List<Float[]> getCurrentColorMap() {
        colorMap = new ArrayList<Float[]>();
        try {
            colorMap = this.getNewHueColors();//this.parent.getHSVColors(currentStreams.size());//;//getNumericalColors();
        } catch (Exception e) {
            System.out.println("colorMap generation failed!");
        }
        return colorMap;
    }
      
     
     public void setNodeColor() {
        List<Float[]> colorSpecturm = getCurrentColorMap();

        int size = colorSpecturm.size();

        myTree.get(0).setColor(Color.white);

        for (int i = 0; i < myTree.get(0).getChildren().size(); i++) {

            Color current = new Color(colorSpecturm.get((int) i % size)[1], colorSpecturm.get((int) i % size)[2], colorSpecturm.get((int) i % size)[3]);
            TreeNode t = (TreeNode) myTree.get(0).getChildren().get(i);
            t.setBaseColor(current);
            t.setColor(current);

        }

        for (int i = 1; i < myTree.size(); i++) {
            TreeNode t = myTree.get(i);

            if (t.getLevel() > 1) {
                TreeNode colorNode = t.getParent(); //myTree.get(i);

                if (t.getParent().getChildren().isEmpty()) {
                    t.setColor(colorNode.getColor());
                } else {
                    if (t.getParent().getChildren().size() == 1) {

                        t.setColor(colorNode.getColor());
                        //t.setBaseColor(t.getParent().getBaseColor()) ;

                    } else {

                        // t.setBaseColor(t.getParent().getBaseColor()) ;
                        int size2 = t.getParent().getChildren().size();
                        Color tempColor = t.getParent().getColor();//colorNode.getBaseColor();
                        float[] hsv = new float[3];
                        Color.RGBtoHSB(tempColor.getRed(), tempColor.getGreen(), tempColor.getBlue(), hsv);
                        List<Color> c = getHueColors(size2, hsv[0]);
                        for (int j = 0; j < t.getParent().getChildren().size(); j++) {
                            if (t.equals(((TreeNode) t.getParent().getChildren().get(j)))) {
                                t.setColor(c.get(j));
                            }
                        }

                    }
                }
            }
        }
       

    }
     
     
         private float[] BuildUnNormNodeValue(CategoryBarElement data, TreeNode t) {
        int numofYears = data.getNumOfYears();
        List<Float> result = new ArrayList<Float>();
        float[] tempresult = new float[numofYears];
        float[] tempsum = new float[numofYears];

        if (t.getUnNormArrayValue().size() == 0) {
            if (t.getChildren().size() == 0) {

                if (t.getValue().contains("L")) {
                    int index = t.getIndex();
                    tempresult = data.getUnormCategoryBar().get(index);

                    for (int i = 0; i < numofYears; i++) {
                        result.add(tempresult[i]);
                    }

                    t.setUnNormArrayValue(result);
                } else {
                    for (int i = 0; i < numofYears; i++) {
                        result.add(0f);
                    }

                    t.setUnNormArrayValue(result);

                }
                return tempresult;

            } else {

                for (int i = 0; i < t.getChildren().size(); i++) {
                    for (int j = 0; j < numofYears; j++) {
                        tempresult = BuildUnNormNodeValue(data, (TreeNode) (t.getChildren().get(i)));
                        tempsum[j] += tempresult[j];
                    }

                }

                for (int i = 0; i < numofYears; i++) {
                    result.add(tempsum[i]);
                }

                t.setUnNormArrayValue(result);
                return tempsum;
            }
        }

        result = t.getArrayValue();
        for (int i = 0; i < numofYears; i++) {
            tempresult[i] = result.get(i);
        }

        return tempresult;

    }
    
     
     private float[] BuildNodeValue(CategoryBarElement data, TreeNode t) {
        int numofYears = data.getNumOfYears();
        List<Float> result = new ArrayList<Float>();
        float[] tempresult = new float[numofYears];
        float[] tempsum = new float[numofYears];

        if (t.getArrayValue().isEmpty()) {
            if (t.getChildren().isEmpty()) {

                if (t.getValue().contains("L")) {
                    int index = t.getIndex();
                    tempresult = data.getCategoryBar().get(index);

                    for (int i = 0; i < numofYears; i++) {
                        result.add(tempresult[i]);
                    }

                    t.setArrayValue(result);
                } else {
                    for (int i = 0; i < numofYears; i++) {
                        result.add(0f);
                    }

                    t.setArrayValue(result);

                }
                return tempresult;

            } else {

                for (int i = 0; i < t.getChildren().size(); i++) {
                    for (int j = 0; j < numofYears; j++) {
                        tempresult = BuildNodeValue(data, (TreeNode) (t.getChildren().get(i)));
                        tempsum[j] += tempresult[j];
                    }

                }

                for (int i = 0; i < numofYears; i++) {
                    result.add(tempsum[i]);
                }

                t.setArrayValue(result);
                return tempsum;
            }
        }

        result = t.getArrayValue();
        for (int i = 0; i < numofYears; i++) {
            tempresult[i] = result.get(i);
        }

        return tempresult;

    }
        
      public void buildTreeWithString(String everything) {

        everything = everything.replaceAll(" ", "");
        everything = everything.replaceAll("\r", "");

        Scanner sc = new Scanner(everything);

        sc.useDelimiter("\n");
        String temp = sc.next();

        String nodes = sc.next();

        String[] tempNodes = nodes.split(",");

        System.out.println(tempNodes.length + " nodes in tree");
        TreeNode NodeArray[] = new TreeNode[1000];
        TreeNode LeafArray[] = new TreeNode[1000];

        for (int i = 0; i < tempNodes.length; i++) {

            String a = tempNodes[i].replaceAll("\\D", "");

            int index = Integer.parseInt(a);

            TreeNode t = new TreeNode(index, tempNodes[i].replaceAll("[^\\p{L}\\p{N}]", ""));

            if (tempNodes[i].replaceAll("[^\\p{L}\\p{N}]", "").charAt(0) == 'N') {
                String[] ary = new String[]{/*t.getValue()*/};
                t.setNodeTopics(ary);
                NodeArray[index] = t;
                myTree.add(t);
            } else if (tempNodes[i].replaceAll("[^\\p{L}\\p{N}]", "").charAt(0) == 'L') {
                t.setNodeTopics(allTopics.get(index + getGlobalReadIndex()));
                LeafArray[index] = t;
                t.setTopicWeight(topicWeights.get(index));                                
                myTree.add(t);
                leaves.put(index, t);
            } else {
                int c = 0;
            }
        }

        String edges = sc.next();

        String[] tempEdges = edges.split("\\),");

        System.out.println((tempEdges.length - 1) + " links in tree");

        for (int i = 0; i < tempEdges.length - 1; i++) {
            String[] tempE = tempEdges[i].split(",");
            TreeNode tt1, tt2;

            int index1 = Integer.parseInt(tempE[0].replaceAll("\\D", ""));
            int index2 = Integer.parseInt(tempE[1].replaceAll("\\D", ""));

            String tempE1 = tempE[0].replaceAll("[^\\p{L}\\p{N}]", "");
            if (tempE1.charAt(0) == 'N') {

                tt1 = NodeArray[index1];
            } else if (tempE1.charAt(0) == 'L') {
                tt1 = LeafArray[index1];
            } else {
                int c = 0;
                tt1 = null;
            }

            String tempE2 = tempE[1].replaceAll("[^\\p{L}\\p{N}]", "");
            if (tempE2.charAt(0) == 'N') {
                tt2 = NodeArray[index2];
            } else if (tempE2.charAt(0) == 'L') {
                tt2 = LeafArray[index2];
            } else {
                int c = 0;
                tt2 = null;
            }

            tt1.addChildNode(tt2);
        }
    }
        
     
    public VastGeoFrame getVCGF() {
        return VCGF;
    }

    public void setVCGF(VastGeoFrame VCGF) {
        this.VCGF = VCGF;
    }
    public Point2D twitterPointMax;
    public Point2D twitterPointMin;

    public int getContentIdx() {
        return contentIdx;
    }

    public void setContentIdx(int contentIdx) {
        this.contentIdx = contentIdx;
    }

    public DateFormat getFormat() {
        return format;
    }

    public void setFormat(DateFormat format) {
        this.format = format;
    }
    private List<Integer> leafNodeSequence;

    public List<Integer> getLeafNodeSequence() {
        return leafNodeSequence;
    }

    public void setLeafNodeSequence(List<Integer> leafNodeSequence) {
        this.leafNodeSequence = leafNodeSequence;
    }
    public static Map<Integer, Integer> parIdx2docIdx;

    public static Map<Integer, Integer> getParIdx2docIdx() {
        return parIdx2docIdx;
    }

    public void setParidx2DocIdx(Map m) {
        parIdx2docIdx = new HashMap(m);
    }
    public EventViewFrame eventViewFrame;
    private TreeNode currentNode;

    // public List<Color> labelColor = new ArrayList<Color>(); 
    public Stack labelColor = new Stack();

    public ViewController() {
        leafNodeSequence = new ArrayList<Integer>();

//        labelColor.push(new Color(141,211,199));                
//        labelColor.push(new Color(255,255,179));
//        labelColor.push(new Color(190,186,218));
//        labelColor.push(new Color(251,128,114));
        labelColor.push(new Color(228, 26, 28));
        labelColor.push(new Color(55, 126, 184));
        labelColor.push(new Color(77, 175, 74));
        labelColor.push(new Color(152, 78, 163));

        labelColor.push(new Color(255, 127, 0));
    }

    /**
     * No header
     *
     * @param records
     */
    public void setUsageRecord(List<String[]> records) {
        this.InternalUsageRecord = records;
    }

    public List<String[]> getUsageRecord() {
        return this.InternalUsageRecord;
    }

    /**
     * The first row is the header.
     *
     * @param docs
     */
    public void setInternalDocs(List<String[]> docs) {
        this.InternalDocs = docs;
    }

    public List<String[]> getInternalDocs() {
        return this.InternalDocs;
    }

    public void addDocumentViewer(DocumentViewer d) {
        docViewer = d;
    }

    public void addTemporalFrame(TemporalViewFrame f) {
        tf = f;
    }

    public TemporalViewFrame getTemporalFrame() {
        if (tf != null) {

            return tf;
        }
        return null;
    }

    TopicTreeMapPanel treemappanel;

    public TopicTreeMapPanel getTmp() {
        return treemappanel;
    }

    public void setTmp(TopicTreeMapPanel tmp) {
        this.treemappanel = tmp;
    }

    public TopicGraphViewPanel getTopicGraphViewPanel() {

        if (topicGraphicPanel != null) {
            return topicGraphicPanel;
        }
        return null;
    }

    public DocumentViewer getDocumentViewer() {
        if (docViewer != null) {
            return docViewer;
        }
        return null;
    }

    public EventViewFrame getEventsViewFrame() {
        if (eventViewFrame != null) {
            return this.eventViewFrame;
        }
        return null;
    }

    public void addTopicGraphViewPanel(TopicGraphViewPanel t) {
        topicGraphicPanel = t;
    }

//    
    public void addEventFrame(EventViewFrame ev) {
        eventViewFrame = ev;
    }

    public void addWorldMapProcessingFrame(WorldMapProcessingPanel wf) {
        worldMapProcessingFrame = wf;
    }

    public WorldMapProcessingPanel getWorldMapProcessingFrame() {
        if (worldMapProcessingFrame != null) {
            return this.worldMapProcessingFrame;
        }
        return null;
    }

    List<LabelText> highlightedTextLabels;

    int previousTimeColumn = -1;
    TreeNode lastNode = new TreeNode();
    boolean isShowingSingleTopic = false;

    HashMap<TreeNode, List<LabelText>> nodeKeywordHighlightMap = new HashMap<TreeNode, List<LabelText>>();

    public void stateChangedSecond(TreeNode ct, int selectedTimeColumn, TemporalViewPanel ap, boolean isSingle) {

        currentNode = ct;

        List<List<int[]>> tYK = getTemporalFrame().getData().topicYearKwIdx;
        highlightedTextLabels = null;//.clear();
        //nodeKeywordHighlightMap = null;
        //nodeKeywordHighlightMap.clear();

//              if (currentNode == null)          
//                currentNode = ap.currentNode;
        if (ct == null) {
            if (ap.multiTopicKeywordList != null) {
                ap.multiTopicKeywordList.clear();
            }

        }

        if (lastNode != null && currentNode != null) {
            if (selectedTimeColumn != previousTimeColumn || (!lastNode.equals(currentNode))) {

                TopicGraphViewPanel.customLabelTimecolumnKey key;
            //if (currentNode == null)          
                //currentNode = ap.currentNode;

                TreeNode ttt = currentNode;//findMatchingNodeInTopicGraph(currentNode);
            //key = new customLabelTimecolumnKey( ttt, selectedTimeColumn);

                key = new TopicGraphViewPanel.customLabelTimecolumnKey(currentNode.getValue(), String.valueOf(selectedTimeColumn));

 
                highlightedTextLabels = ap.getLabelTimeMap().get(key);

                if (highlightedTextLabels == null) {

                    System.out.println(key);
                    System.out.println("no wordle cloud map ");

                } else {
                    Point tmp_p = new Point(ap.getWidth() / 2, ap.getHeight() / 2);

                    ap.DrawWordleCloud(tmp_p/*ap.currentMouseLocation*/, highlightedTextLabels);
                }
            }
        }

        previousTimeColumn = selectedTimeColumn;
        lastNode = currentNode;

        // getTopicGraphViewPanel().updateLabelLocations();
        if (ct != null && ct.getChildren().isEmpty()) {
            //System.out.println("ctct");
            TreeNode ttt = findMatchingNodeInTopicGraph(ct);
            //ap.drawTopicWords(ttt);
            ap.showingNode = ttt;
        } else {
            ap.showingNode = null;
        }

        if (tYK.isEmpty()) {
            // System.out.println("current Node is null");

            // 
        } else {

            if (currentNode == null) {
                //System.out.println("No node selected, showing all");
                currentNode = ap.currentNode;

                if (currentNode.getChildren().isEmpty()) {

                    int[] x = tYK.get(currentNode.getIndex()).get(selectedTimeColumn);
                    getTopicGraphViewPanel().highLightByYearIdxKw(currentNode, x);

                } else {
                    getTopicGraphViewPanel().highLightByYearIdxKwNode(currentNode, selectedTimeColumn, tYK);

                    //getTopicGraphViewPanel().highLightByYearIdxKwNode(currentNode, selectedTimeColumn, tYK, nodeKeywordHighlightMap);
                }

                //getTopicGraphViewPanel().highLightByYearIdxKwNode(currentNode, selectedTimeColumn, tYK);
            } else if (currentNode.getChildren().isEmpty()) {

                int[] x = tYK.get(currentNode.getIndex()).get(selectedTimeColumn);
                getTopicGraphViewPanel().highLightByYearIdxKw(currentNode, x);

            } else {
                getTopicGraphViewPanel().highLightByYearIdxKwNode(currentNode, selectedTimeColumn, tYK);

                //getTopicGraphViewPanel().highLightByYearIdxKwNode(currentNode, selectedTimeColumn, tYK, nodeKeywordHighlightMap);
            }
        }

   //     System.out.println(nodeKeywordHighlightMap.size());
//        isShowingSingleTopic = isSingle;
//
//        if (isSingle) {
//            if (currentNode.getChildren().isEmpty()) {
//                highlightedTextLabels = getTopicGraphViewPanel().getTopicLabels(findMatchingNodeInTopicGraph(currentNode));
//
//            } else {
//                highlightedTextLabels = getTopicGraphViewPanel().getCurrentNodeLabels(findMatchingNodeInTopicGraph(currentNode));//putUpHighlightedKeywordList(nodeKeywordHighlightMap);
//
//            }
//
//        } else 
//        {
//            if (currentNode.getChildren().isEmpty()) {
//                highlightedTextLabels = getTopicGraphViewPanel().getHighligtedLabels();
//
//            } else {
//                highlightedTextLabels = putUpHighlightedKeywordList(nodeKeywordHighlightMap);
//            }
//        }
        //highlightedTextLabels = getTopicGraphViewPanel().getHighligtedLabels();
        //System.out.println(highlightedTextLabels.size());
    }

    List<LabelText> putUpHighlightedKeywordList(HashMap<TreeNode, List<LabelText>> m) {
        List<LabelText> r = new ArrayList<LabelText>();

        int size = m.size();

        int maxkeywordnumber = 20;

        int iterationRuns = maxkeywordnumber / size;

        if (iterationRuns < 1) {
            iterationRuns = 1;
        }

        for (int i = 0; i < iterationRuns; i++) {
            for (Object o : m.values()) {
                r.add((LabelText) ((List<LabelText>) o).get(i));
            }
        }

        return r;
    }

    void updateGeoView(Color c, List<Integer> l) {
        if (getWorldMapProcessingFrame() != null) {
            //getWorldMapProcessingFrame().UpdateGeoLocations(c, l);
        }

    }

    public int getGlobalReadIndex() {
        return globalReadIndex;
    }

    public void setGlobalReadIndex(int globalReadIndex) {
        this.globalReadIndex = globalReadIndex;
    }

    int globalReadIndex = 0;
    float intervalDays = 7;
    boolean b_recaluateValue = false;
    int zoomSubBins = 5;
    boolean b_readAll = true;
    public boolean b_readFromDB = false;
    public String host = "";
    String user = "";
    String password = "";
    String table = "";
    //String host = "152.15.99.7";
    public int port = 2012;
    String database = "patents";
    public String collection = "patent";
    String collection2 = "unsorted_terms";
    String[] nameFields;
    String nameField2;
    String text_id;
    String id_type = "";

    boolean tagLDA = false;

    public void readHeaderFile(String headerpath) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(headerpath));

        String line = br.readLine();
        String[] split = line.split(":");
        intervalDays = Float.parseFloat(split[1]);

        line = br.readLine();
        split = line.split(":");
        b_recaluateValue = (Integer.parseInt(split[1]) == 1) ? true : false;

        line = br.readLine();
        split = line.split(":");
        zoomSubBins = Integer.parseInt(split[1]);

        line = br.readLine();
        split = line.split(":");
        b_readAll = (Integer.parseInt(split[1]) == 1) ? true : false;

        line = br.readLine();
        split = line.split(":");
        b_readFromDB = (Integer.parseInt(split[1]) == 1) ? true : false;

        if (b_readFromDB) {

            line = br.readLine();
            split = line.split(":");
            host = split[1];

            line = br.readLine();
            split = line.split(":");
            port = Integer.parseInt(split[1]);

            line = br.readLine();
            split = line.split(":");
            database = split[1];

            line = br.readLine();
            split = line.split(":");
            collection = split[1];

            line = br.readLine();
            split = line.split(":");
            nameFields = split[1].split(",");

            line = br.readLine();
            split = line.split(":");
            collection2 = split[1];

            line = br.readLine();
            split = line.split(":");
            nameField2 = split[1];
//              line= br.readLine();
//              split = line.split(":"); 
//              host = split[1];
//              
//              line= br.readLine();
//              split = line.split(":"); 
//              user = split[1];
//              
//              line= br.readLine();
//              split = line.split(":"); 
//              password = split[1];
//              
//              line= br.readLine();
//              split = line.split(":"); 
//              table = split[1];

        } else {
        }

    }

    public TreeNode findMatchingNodeInTopicGraph(TreeNode findt) {

        VisualizationViewer vv = getTopicGraphViewPanel().getVisualizationViewer();
        Collection<TreeNode> vertices = vv.getGraphLayout().getGraph().getVertices();

        int in = findt.getIndex();
        String sla = findt.getValue();
        //   System.out.println("selectedNode = " + in + " " + sla); 
        for (Iterator<TreeNode> it = vertices.iterator(); it.hasNext();) {

            Object k = it.next();
            if (k instanceof TreeNode) {
                TreeNode t = (TreeNode) k;
                if (in == t.getIndex() && (sla == null ? t.getValue() == null : sla.equals(t.getValue()))) {

                    return t;
                }
            }
        }
        return null;

    }

    public void stateChangedFromLabelToTopic(HashMap<String, List<Integer>> highindex, HashMap<String, List<Float>> highWeight, HashMap<String, Color> highIndexNumber) {
        getTopicGraphViewPanel().setHighlightLabelsFromLabelTopics(highindex, highWeight, highIndexNumber);

    }

    public void addThemeRiver(TreeNode ct) throws IOException {

        TemporalViewPanel tp = null;

        tp = new TemporalViewPanel(this);

        if (!this.getTemporalFrame().getTemporalPanelMap().containsKey(1)) {
            List<TemporalViewPanel> tvpl = new ArrayList<TemporalViewPanel>();
            this.getTemporalFrame().getTemporalPanelMap().put(1, tvpl);

        }

        int index = this.getTemporalFrame().getTemporalPanelMap().get(1).size();
        tp.setName("1" + index);
        tp.setPanelLabelId(index);
        tp.setLevel(1);
        tp.setData(this.getTemporalFrame().getData());
        tp.setTree(this.getTemporalFrame().getTree());
        this.getTemporalFrame().getMainPanel().addChildPanel(tp);
        tp.setFatherPanel(this.getTemporalFrame().getMainPanel());
        TreeNode tempt = ct;

        for (TreeNode n : this.getTemporalFrame().getTree()) {

            if ((n.getIndex() == tempt.getIndex()) && n.getValue().equals(tempt.getValue())) //if ((n.getIndex() == tempt.getIndex()) && n.getChildren().isEmpty())
            {
                tp.currentNode = n;
                break;
            }
        }

        if (tp.currentNode == null) {
            System.out.println("No node found matches");
        }

        Integer it = tempt.getIndex();
        it = index;
        this.getTemporalFrame().getMainPanel().getDrawLabels().add(it);

        Point2D pf = new Point2D.Float(0, 0);

        this.getTemporalFrame().getMainPanel().getDrawLabelsLocation().add(pf);
        tp.setFatherPanel(this.getTemporalFrame().getMainPanel());
        tp.calculateLocalNormalizingValue(tp.getData(), tp.currentNode);
        tp.buildLabelTimeMap();

        this.getTemporalFrame().getTemporalPanelMap().get(1).add(tp);
        float normalizeValue = -1;
        if (this.getTemporalFrame().getTemporalPanelMap().get(1).size() > 0) {
            for (TemporalViewPanel ttp : this.getTemporalFrame().getTemporalPanelMap().get(1)) {
                if (ttp.getLocalNormalizingValue() >= normalizeValue) {
                    normalizeValue = ttp.getLocalNormalizingValue();
                }
            }
            for (TemporalViewPanel ttp : this.getTemporalFrame().getTemporalPanelMap().get(1)) {
                ttp.setGlobalNormalizingValue(normalizeValue);
            }
        }

        // tp.UpdateTemporalView(new Dimension(tf.getWidth()/(1+secondColumnExist+thirdColumnExist),tf.getHeight()/3), tp.getGlobalNormalizingValue());
        for (TemporalViewPanel ttp : this.getTemporalFrame().getTemporalPanelMap().get(1)) {
            ttp.calculateRenderControlPointsOfEachHierarchy(tp.getData(), tp.currentNode, tp.getGlobalNormalizingValue());
            ttp.computerZeroslopeAreasHierarchy(0);
                                //ttp.detectEvents();

            //ttp.UpdateTemporalView(new Dimension(tvf.getContentPane().getWidth() / (1 + secondColumnExist + thirdColumnExist), tvf.getContentPane().getHeight() / 3), ttp.getGlobalNormalizingValue());
        }

                            //tvf.getMainPanel().UpdateTemporalView(new Dimension(tvf.getContentPane().getWidth() / (1 + secondColumnExist + thirdColumnExist), tvf.getContentPane().getHeight() * 2 / 3), tvf.getMainPanel().getLocalNormalizingValue());
        //tvf.getSubPanel().UpdateTemporalView(new Dimension(tvf.getContentPane().getWidth() / (1 + secondColumnExist + thirdColumnExist), tvf.getContentPane().getHeight() / 3), tvf.getSubPanel().getLocalNormalizingValue());
        System.out.println("second column panel added");
        this.getTemporalFrame().setMigLayoutForScrollPane();

    }

    public void stateChangedNew(TreeNode ct) {

        for ( TreeMapNodePanel p : treemappanel.getNodePanel().values())
        {
            p.setMouseOvered(false);  
            p.updateLayout();
            
        }
        
        TreeNode a = findMatchingNodeInTopicGraph(ct);
        
        
        if (a.getChildren().isEmpty())
        {
            treemappanel.getNodePanel().get(a).setMouseOvered(true);
            treemappanel.getNodePanel().get(a).updateLayout();
        }
        else
        {
            for (int i=0; i<a.getChildren().size(); i++)
            {
                TreeNode t = (TreeNode) a.getChildren().get(i);
                treemappanel.getNodePanel().get(t).setMouseOvered(true);
                treemappanel.getNodePanel().get(t).updateLayout();
            }
            
        }
        
       
         //TopicTreeMap
    }

    public void stateChanged(TreeNode ct) {
        currentNode = ct;
 // topic graph gone
        
        
//        VisualizationViewer vv = getTopicGraphViewPanel().getVisualizationViewer();
//        final PickedState<TreeNode> pickedState = vv.getPickedVertexState();
//        Collection<TreeNode> vertices = vv.getGraphLayout().getGraph().getVertices();
//
//        pickedState.clear();
//        //System.out.println(selectedNode.toString());
//        int in = currentNode.getIndex();
//        String sla = currentNode.getValue();
//        //   System.out.println("selectedNode = " + in + " " + sla); 
//        for (Iterator<TreeNode> it = vertices.iterator(); it.hasNext();) {
//
//            Object k = it.next();
//            if (k instanceof TreeNode) {
//                TreeNode t = (TreeNode) k;
//                if (in == t.getIndex() && (sla == null ? t.getValue() == null : sla.equals(t.getValue()))) {
//                    pickedState.pick(t, true);
//                    // System.out.println(t.getValue() + "is matched" );
//                    break;
//                }
//            }
//        }
//
//        TreeNode tx = currentNode.getParent();
//        while (tx != null) {
//            int in1 = tx.getIndex();
//            String sla1 = tx.getValue();
//
//            innerCircle:
//            for (Iterator<TreeNode> it1 = vertices.iterator(); it1.hasNext();) {
//
//                TreeNode t1 = it1.next();
//                if (in1 == t1.getIndex() && (sla1.equals(t1.getValue()))) {
//                    pickedState.pick(t1, true);
//                    //  System.out.println(t1.getValue() + "is matched" );
//                    break innerCircle;
//                }
//            }
//
//            tx = tx.getParent();
//        }
//
//        getTopicGraphViewPanel().getVisualizationViewer().setPickedVertexState(pickedState);
//
//        vv.repaint();

        //change event view frame
    }

    public boolean b_searchHighlight = false;
    public List<Integer> searchHighlightStreams = new ArrayList<Integer>();

    public void searchHighlight(List<Integer> highlightIndex) {

        searchHighlightStreams.clear();

        TemporalViewPanel mPanel = getTemporalFrame().getMainPanel();

        TreeNode root = getTemporalFrame().getTree().get(0);

        for (int j = 0; j < highlightIndex.size(); j++) {
            for (int i = 0; i < root.getChildren().size(); i++) {

                TreeNode t = (TreeNode) root.getChildren().get(i);

                if (t.getTopicsContainedIdx().contains(highlightIndex.get(j))) {
                    //System.out.println(highlightIndex.get(j) + " " + t.getIndex() + " " + i);
                    searchHighlightStreams.add(i);
                    break;
                }

            }
        }

        mPanel.repaintView();

    }

    public void fireYearSelected(List<Integer> get) {
        int[] tmp = new int[get.size()];
        for (int i = 0; i < get.size(); i++) {
            tmp[i] = get.get(i);
        }
        //spChart.updateView(tmp);
        docViewer.updateDocContent(tmp);
//        this.getParallelDisplay().setOriginator("TR");
//        this.getParallelDisplay().setSelectedDocfromTR(get);
        //this.getParallelDisplay().repaint();
    }

    /**
     * When selecting a year in the Temporal View
     *
     * @param s
     */
    public void fireYearTopicSelected(List<Integer> s) {
        int[] tmp = new int[s.size()];
        for (int i = 0; i < s.size(); i++) {
            tmp[i] = s.get(i);
        }
        // spChart.updateView(tmp);
        docViewer.updateDocContent(tmp);
        //  this.getParallelDisplay().setOriginator("TR");
        //this.getParallelDisplay().setSelectedDocfromTR(s);
        //    this.getParallelDisplay().repaint();
    }

    public void fireTopicIdle() {
        if (tvl != null) {
            tvl.setFocusedCatgory(-99);
            // evl.setFocusedCatgory(-99);
        }
    }

    public void addTemporalViewListener(TemporalViewListener l) {
        tvl = l;
    }

//    public void addEventsViewListener(EventsViewListener ev){
//        evl = ev;
//    }
    public void removeTemporalViewListener() {
        tvl = null;
    }

    public void setTopicSimilarities(List<Float> sim) {
        topicSimilarities = new ArrayList<Float>();
        topicSimilarities = sim;
    }

    
        private Color hsv2rgb(float h, float s, float v) {
        h = (h % 1 + 1) % 1; // wrap hue

        int i = (int) Math.floor((float) (h * 6));
        float f = h * 6 - i;
        float p = v * (1 - s);
        float q = v * (1 - s * f);
        float t = v * (1 - s * (1 - f));

        switch (i) {
            case 0:
                return new Color(v, t, p);
            case 1:
                return new Color(q, v, p);
            case 2:
                return new Color(p, v, t);
            case 3:
                return new Color(p, q, v);
            case 4:
                return new Color(t, p, v);
            case 5:
                return new Color(v, p, q);
        }

        return null;
    }
        
    
        private List<Color> getHueColors(int size, float H) {
        List<Color> tempcolorMap = new ArrayList<Color>();
        try {

            float S = (float) 0.5;
            float V = (float) 0.2;
            float varH = (float) 0.2;
            for (int i = 0; i < size; i++) {
                float tempS = (float) S / ((float) size) * (float) i + (float) 0.2;
                float tempV = (float) V / size * i + (float) 0.7;
                float tempH = H - (float) varH / 2 + (float) varH / size * i;
                Color r = hsv2rgb(tempH, tempS, tempV);

                tempcolorMap.add(r);
            }

            if (size == 0) {
                Color r = hsv2rgb(H, S, V);
                tempcolorMap.add(r);

            }

        } catch (Exception e) {
            System.out.println(" hue colorMap generation failed!");
        }
        return tempcolorMap;
    }
        
        
    public List<Float[]> getHSVColors(int n) {
        List<Float[]> colorMap = new ArrayList<Float[]>();
        for (int j = 0; j < n; j++) {
            float Alpha = 1;
            float H = 0;
            H = (float) ((float) j / (float) n * 0.8);
            if (j > 0.9) {
                H = (float) ((float) j / (float) n * 0.8);// - (float)0.1;
            } else {
                H = (float) ((float) j / (float) n * 0.8);
            }
            float S = (float) 0.5;
            //For RGB to HSV color
            //float S = (float) (((float) 1.0 - norm_tSims.get(j))*0.8);
            float V = (float) 1.0;

            Float[] tmpColor = new Float[4];
            tmpColor[0] = Alpha;
            tmpColor[1] = H;
            tmpColor[2] = S;
            tmpColor[3] = V;

            colorMap.add(tmpColor);
        }
        return colorMap;
    }
    private List<Float[]> hueColors;

    public List<Float[]> getHueColors() {
        return hueColors;
    }

    public static Color[] labelColors
            = {
                new Color(141, 211, 199),
                new Color(255, 255, 179),
                new Color(190, 186, 218),
                new Color(251, 128, 114)

            };
    
    
     public static Color[] brewerColor
            = {
                new Color(141,211,199),
                new Color(190,186,218),
                new Color(251,128,114),
              
                new Color(204,235,197),
                   new Color(252,205,229),
//                    new Color(217,217,217),
                                 new Color(204,235,197),
                new Color(128,177,211),//lowes
    
  
                new Color(253,180,98),//hdepot
                new Color(255,237,111)
            };

    public void setNewHueColors() {
        hueColors = new ArrayList<Float[]>();

        float[] hsv = new float[3];
        float Alpha = 1;
//        float R1 = 141;
//        float G1 = 211;
//        float B1 = 199;
        // Color.RGBtoHSB(R1,G1,B1,hsv);

//        Float[] tmpColor = new Float[4];
//        tmpColor[0] = Alpha;
//        tmpColor[1] = R1 / (float) 255.0;
//        tmpColor[2] = G1 / (float) 255.0;
//        tmpColor[3] = B1 / (float) 255.0;
//        hueColors.add(tmpColor);

        for (int i=0; i<brewerColor.length; i++)
        {
        Float[] tmpColor1 = new Float[4];
        float R = (float) brewerColor[i].getRed();
        float G = (float) brewerColor[i].getGreen();
        float B = (float) brewerColor[i].getBlue();
        tmpColor1[0] = Alpha;
        tmpColor1[1] = R / (float) 255.0;
        tmpColor1[2] = G / (float) 255.0;
        tmpColor1[3] = B / (float) 255.0;
        hueColors.add(tmpColor1);
            
        }
        
//        Float[] tmpColor1 = new Float[4];
//        float R = (float) 255.0;
//        float G = (float) 159.0;
//        float B = (float) 0;
////        tmpColor1[0] = Alpha;
////        tmpColor1[1] = R / (float) 255.0;
////        tmpColor1[2] = G / (float) 255.0;
////        tmpColor1[3] = B / (float) 255.0;
////        hueColors.add(tmpColor1);
//
//        Float[] tmpColor2 = new Float[4];
//        R = (float) 190.0;
//        G = (float) 186.0;
//        B = (float) 218.0;
//        tmpColor2[0] = Alpha;
//        tmpColor2[1] = R / (float) 255.0;
//        tmpColor2[2] = G / (float) 255.0;
//        tmpColor2[3] = B / (float) 255.0;
//        hueColors.add(tmpColor2);
//
//        Float[] tmpColor3 = new Float[4];
//        R = (float) 251.0;
//        G = (float) 128.0;
//        B = (float) 114.0;
//        tmpColor3[0] = Alpha;
//        tmpColor3[1] = R / (float) 255.0;
//        tmpColor3[2] = G / (float) 255.0;
//        tmpColor3[3] = B / (float) 255.0;
//        hueColors.add(tmpColor3);
//
//        Float[] tmpColor4 = new Float[4];
//        R = (float) 128.0;
//        G = (float) 177.0;
//        B = (float) 211.0;
//        tmpColor4[0] = Alpha;
//        tmpColor4[1] = R / (float) 255.0;
//        tmpColor4[2] = G / (float) 255.0;
//        tmpColor4[3] = B / (float) 255.0;
//        hueColors.add(tmpColor4);
//
//        Float[] tmpColor5 = new Float[4];
//        R = (float) 253.0;
//        G = (float) 180.0;
//        B = (float) 98.0;
//        tmpColor5[0] = Alpha;
//        tmpColor5[1] = R / (float) 255.0;
//        tmpColor5[2] = G / (float) 255.0;
//        tmpColor5[3] = B / (float) 255.0;
//        hueColors.add(tmpColor5);
//
//        Float[] tmpColor6 = new Float[4];
//        R = (float) 179.0;
//        G = (float) 222.0;
//        B = (float) 105.0;
//        tmpColor6[0] = Alpha;
//        tmpColor6[1] = R / (float) 255.0;
//        tmpColor6[2] = G / (float) 255.0;
//        tmpColor6[3] = B / (float) 255.0;
//        hueColors.add(tmpColor6);
//
//        Float[] tmpColor7 = new Float[4];
//        R = (float) 252.0;
//        G = (float) 205.0;
//        B = (float) 229.0;
//        tmpColor7[0] = Alpha;
//        tmpColor7[1] = R / (float) 255.0;
//        tmpColor7[2] = G / (float) 255.0;
//        tmpColor7[3] = B / (float) 255.0;
//        hueColors.add(tmpColor7);
//
//        Float[] tmpColor8 = new Float[4];
//        R = (float) 217.0;
//        G = (float) 217.0;
//        B = (float) 217.0;
//        tmpColor8[0] = Alpha;
//        tmpColor8[1] = R / (float) 255.0;
//        tmpColor8[2] = G / (float) 255.0;
//        tmpColor8[3] = B / (float) 255.0;
//        hueColors.add(tmpColor8);

    }

    public List<Float[]> getNewHueColors() {

        if (this.hueColors != null) {
            return this.hueColors;
        } else {
            return null;
        }
    }

    public List<Float[]> getNumericalColors() {

        List<Float> norm_tSims = new ArrayList<Float>();
        float sum = 0;
        if (topicSimilarities != null) {
            for (int i = 0; i < topicSimilarities.size(); i++) {
                sum += topicSimilarities.get(i);
            }
            norm_tSims.add((float) 0.0);
            float tmpAccu = topicSimilarities.get(0);
            for (int i = 0; i < topicSimilarities.size() - 1; i++) {
                float tmpPos = tmpAccu / sum;
                norm_tSims.add(tmpPos);
                tmpAccu += topicSimilarities.get(i + 1);
            }
            norm_tSims.add((float) 1.0);

            List<Float[]> colorMap = new ArrayList<Float[]>();

            for (int j = 0; j < norm_tSims.size(); j++) {
                float Alpha = 1;
                float H = 0;
                H = (float) (norm_tSims.get(j) * 0.8);
                if (norm_tSims.get(j) > 0.9) {
                    H = (float) (norm_tSims.get(j) * 0.8);// - (float)0.1;
                } else {
                    H = (float) (norm_tSims.get(j) * 0.8);
                }
                float S = (float) 0.5;
                //For RGB to HSV color
                //float S = (float) (((float) 1.0 - norm_tSims.get(j))*0.8);
                float V = (float) 1.0;

                Float[] tmpColor = new Float[4];
                tmpColor[0] = Alpha;
                tmpColor[1] = H;
                tmpColor[2] = S;
                tmpColor[3] = V;

                colorMap.add(tmpColor);
            }
            return colorMap;
        } else {
            System.out.println("TopicSim is null");
            return null;
        }
    }

    public Graph makePrefuseGraph(String orgTree) {

        // Create tables for node and edge data, and configure their columns.
        Table nodeData = new Table();
        Table edgeData = new Table(0, 1);
        nodeData.addColumn("label", String.class);
        edgeData.addColumn(Graph.DEFAULT_SOURCE_KEY, int.class);
        edgeData.addColumn(Graph.DEFAULT_TARGET_KEY, int.class);
        edgeData.addColumn("weight", int.class);
        // Need more data in your nodes or edges?  Just add more
        // columns.

        // Create Graph backed by those tables.  Note that I'm
        // creating a directed graph here also.
        Graph g = new Graph(nodeData, edgeData, true);

        Scanner sc = new Scanner(orgTree);

        sc.useDelimiter("\n");
        String temp = sc.next();

        String nodes = sc.next();

        String[] tempNodes = nodes.split(",");

        Node NodeArray[] = new Node[100];
        Node LeafArray[] = new Node[100];

        for (int i = 0; i < tempNodes.length; i++) {

            String a = tempNodes[i].replaceAll("\\D", "");

            int index = Integer.parseInt(a);

            if (tempNodes[i].replaceAll("[^\\p{L}\\p{N}]", "").charAt(0) == 'N') {

                Node n = g.addNode();
                n.setString("label", tempNodes[i].replaceAll("[^\\p{L}\\p{N}]", ""));
                NodeArray[index] = n;

            } else if (tempNodes[i].replaceAll("[^\\p{L}\\p{N}]", "").charAt(0) == 'L') {
                Node n = g.addNode();
                n.setString("label", tempNodes[i].replaceAll("[^\\p{L}\\p{N}]", ""));
                LeafArray[index] = n;

            } else {
                int c = 0;
            }
        }

        String edges = sc.next();

        String[] tempEdges = edges.split("\\),");
        for (int i = 0; i < tempEdges.length - 1; i++) {
            String[] tempE = tempEdges[i].split(",");
            Node tt1, tt2;

            int weight = 100;

            int index1 = Integer.parseInt(tempE[0].replaceAll("\\D", ""));
            int index2 = Integer.parseInt(tempE[1].replaceAll("\\D", ""));

            String tempE1 = tempE[0].replaceAll("[^\\p{L}\\p{N}]", "");
            if (tempE1.charAt(0) == 'N') {
                tt1 = NodeArray[index1];
                weight = index1;
            } else if (tempE1.charAt(0) == 'L') {
                tt1 = LeafArray[index1];
            } else {
                int c = 0;
                tt1 = null;
            }

            String tempE2 = tempE[1].replaceAll("[^\\p{L}\\p{N}]", "");
            if (tempE2.charAt(0) == 'N') {
                tt2 = NodeArray[index2];
            } else if (tempE2.charAt(0) == 'L') {
                tt2 = LeafArray[index2];
            } else {
                int c = 0;
                tt2 = null;
            }

            Edge e1 = g.addEdge(tt1, tt2);

            e1.setInt("weight", weight);

        }

        return g;
    }
    
    
    public HashMap<TreeNode, List<LabelText>> allLabels = new HashMap<TreeNode, List<LabelText>>();
      private int labelsToDisplay = 51;
        public List<String[]> allTopics;
        
        private String HelveticaFont = "Helvetica-Condensed-Bold";
        
        static private int occuranceFontSizePara = 2;
        private int labelFontSize = 10; //18
        
        static private int fontSizePerChar = 1;
        
    void buildLabelLocations(Map<String, Integer> wordTermIndex, List<String[]> wordTermWeightsF, List<List<Float>> topkTermWeightMongo) {

        allLabels.clear();
      

        for (int i = 0; i < myTree.size(); i++) {
            //System.out.println(i);
            TreeNode o = myTree.get(i);

            List<LabelText> tempList = new ArrayList<LabelText>();
            Point2D loc = new Point2D.Float(0,0);

            if (o.getChildren().isEmpty()) {

                TreeNode t = o;

                //System.out.print(allLabels.size());
                if (t.getChildren().isEmpty() && t.getValue().contains("L")) {

                    int index = t.getIndex();

                    int countleng = 0; float w = 0;
                    for (int j = 1; j < labelsToDisplay; j++) {

                       
                        try{
                        LabelText tempLT = new LabelText();

                        int leng = t.getNodeTopics()[j].length();
                        Font font = null;//new Font("Arial", Font.PLAIN, labelFontSize);

                        if (j == 1) {
                            tempLT.setOccurance(1);
                            tempLT.setProbablity(99999.0f);
                        } else {

                            String tempxx = t.getNodeTopics()[j];

                            int tmpCol = -1;

                            if (!b_readFromDB)
                            {
                                if (!StringUtils.isNumeric(tempxx)) {
                                    tmpCol = wordTermIndex.get(t.getNodeTopics()[j].toLowerCase());
                                } else {
                                    tmpCol = wordTermIndex.get(t.getNodeTopics()[j]);
                                }
                            }
                            
                            if (tmpCol == 989)
                            {
                                int a = 0;
                                
                            }
                            if (occurances.get(index + 1)[j] == 0) {
                                tempLT.setOccurance(1);
                            } else {
                                if (occurances.get(index + 1)[j] >= 20) {
                                    tempLT.setOccurance(20);
                                } else {
                                    tempLT.setOccurance(occurances.get(index + 1)[j]);
                                }
                            }
                            
                            float weight = 0; 
                            
                            if (b_readFromDB)
                                weight = topkTermWeightMongo.get(index).get(j-1);
                            else
                                weight = Float.parseFloat(wordTermWeightsF.get(index)[tmpCol]);

                            w = weight;
                            tempLT.setProbablity(weight);
                        }
                        
                        

                        font = new Font(HelveticaFont, Font.PLAIN, occuranceFontSizePara * tempLT.getOccurance()/*occurances.get(index + 1)[j]*/ + labelFontSize);
                        //Font HelveticaFont = new Font("Helvetica", Font.BOLD, 12);

                        tempLT.setFont(font);
                        //tempLT.setColor(Color.BLUE);

                        tempLT.column = index;

                        tempLT.row = j - 1;

                        tempLT.setString(t.getNodeTopics()[j]);

                        tempList.add(tempLT);
                        }
                        catch(Exception e){
                                System.out.println("word " + t.getNodeTopics()[j] + " in dict line " + wordTermIndex.get(t.getNodeTopics()[j])
                                + " weight is " + w);
                                }
                    }

                    labelTextComparer c = new labelTextComparer();
                    
                    
                    Collections.sort(tempList, c);
                    float maxP = -99999;
                    for (int j = 0; j < tempList.size(); j++) {

                        if (tempList.get(j).getProbablity() >= maxP && tempList.get(j).getProbablity() <= 59999) {
                            maxP = tempList.get(j).getProbablity();

                        }
                    }

                    for (int kkk = 0; kkk < tempList.size(); kkk++) {

                        int px = (int) loc.getX();
                        int py = (int) loc.getY();

                        LabelText tempLT = tempList.get(kkk);
                        if (tempLT.getProbablity() == 99999.0f) {
                            tempLT.setProbablity(0);
                        } else {

                            tempLT.setProbablity(tempLT.getProbablity() / maxP);
                        }

                        Font font = tempLT.getFont();

                        JLabel tempLabel = new JLabel();
                        tempLabel.setFont(font);
                        FontMetrics fm = tempLabel.getFontMetrics(font);
                        int widthOfString = fm.stringWidth(" " + tempLT.s + " ");
                        int leng = widthOfString;

                        if (kkk==0)
                            leng = 40;//widthOfString + 10;
                        
                        tempLT.setRect(new Rectangle2D.Float(px + 20 + countleng, py - 10, leng * fontSizePerChar, 20));

                        tempLT.posX = px + 20 + countleng + 2;
                        tempLT.posY = py + 5;

                        countleng += (leng * fontSizePerChar + 2);

                    }

                    allLabels.put(t, tempList);

                } 
//                else // Nodes situation
//                {
//                    //int labelsToDisplay2 = 5;
//                }
//            } else if (o instanceof DelegateTree) {
//                TreeNode t = (TreeNode) (((DelegateTree) o).getRoot());
//
//                int labelsToDisplay2 = labelsToDisplay / t.getNodeSize();
//                //String[] temp = new String[labelsToDisplay2 * myTree.get(i).getNodeSize()];
//
//                for (int k = 0; k < (labelsToDisplay2); k++) {
//                    for (int j = 0; j < t.getTopicsContainedIdx().size(); j++) {
//
//                        int index1 = t.getTopicsContainedIdx().get(j);
//                        String t1[] = allTopics.get(index1 + parent.getGlobalReadIndex());
//                        LabelText tempLT = new LabelText();
//
//                        int index = index1;
//
//                        int px = (int) loc.getX();
//                        int py = (int) loc.getY();
//
//                        int leng = t1[nodeStringStartIndex + k].length();
//                        
//                        
//                        
//                        if (wordTermIndex.get(t1[nodeStringStartIndex + k].toLowerCase()) == null) {
//                            tempLT.setOccurance(1);
//                            tempLT.setProbablity(99999.0f);
//                        } else {
//
//                            //System.out.println(t1[nodeStringStartIndex + k].toLowerCase());
//                            int tmpCol = -1;
//                            
//                            if (!parent.b_readFromDB)
//                            {
//                                tmpCol = wordTermIndex.get(t1[nodeStringStartIndex + k].toLowerCase());
//                            }
//                            
//                            if (occurances.get(index + 1)[k + 1] == 0) {
//                                tempLT.setOccurance(1);
//                            } else {
//                                if (occurances.get(index + 1)[k + 1] >= 20) {
//                                    tempLT.setOccurance(20);
//                                } else {
//                                    tempLT.setOccurance(occurances.get(index + 1)[k + 1]);
//                                }
//                            }
//                            
//
//                            float weight = 0;
//                            
//                            
//                             if (parent.b_readFromDB)
//                                weight = topkTermWeightMongo.get(index).get(j-1);
//                            else
//                                weight = Float.parseFloat(wordTermWeightsF.get(index)[tmpCol]);
//                             
//                             
//                            tempLT.setProbablity(weight);
//
//                        }
//
//                        Font font = new Font("Arial", Font.PLAIN, labelFontSize);
//                        font = new Font("Font", Font.PLAIN, occuranceFontSizePara * tempLT.getOccurance()/*occurances.get(index + 1)[j]*/ + labelFontSize);
//                        tempLT.setFont(font);
//
//                        //tempLT.setColor(Color.BLUE);
//                        tempLT.isHighlighted = false;
//
//                        tempLT.column = index;
//                        //  tempLT.setRect(new Rectangle2D.Float(px + 20 + countleng, py - 10, leng * fontSizePerChar, 20));
//
//                        tempLT.row = nodeStringStartIndex + k;
//
//                        tempLT.setString(t1[nodeStringStartIndex + k]);
//                        //   tempLT.posX = px + 20 + countleng + 2;
//                        //   tempLT.posY = py + 5;
////
//                        //    countleng += (leng * fontSizePerChar + 2);
//
//                        tempList.add(tempLT);
//                    }
//
//                }
//
//                labelTextComparer c = new labelTextComparer();
//                Collections.sort(tempList, c);
//
//                float maxP = -99999;
//                for (int j = 0; j < tempList.size(); j++) {
//
//                    if (tempList.get(j).getProbablity() >= maxP && tempList.get(j).getProbablity() <= 59999) {
//                        maxP = tempList.get(j).getProbablity();
//
//                    }
//                }
//
//                int countleng = 0;
//                for (int kkk = 0; kkk < tempList.size(); kkk++) {
//                    // System.out.println(tempList.get(kkk).getProbablity());
//
//                    int px = (int) loc.getX();
//                    int py = (int) loc.getY();
//
//                    LabelText tempLT = tempList.get(kkk);
//                    if (tempLT.getProbablity() == 99999.0f) {
//                        tempLT.setProbablity(0);
//                    } else {
//
//                        tempLT.setProbablity(tempLT.getProbablity() / maxP);
//                    }
//
//                    Font font = tempLT.getFont();
//
//                    JLabel tempLabel = new JLabel();
//                    tempLabel.setFont(font);
//                    FontMetrics fm = tempLabel.getFontMetrics(font);
//                    int widthOfString = fm.stringWidth(" " + tempLT.s + " ");
//                    int leng = widthOfString;
//
//                    tempLT.setRect(new Rectangle2D.Float(px + 20 + countleng, py - 10, leng * fontSizePerChar, 20));
//
//                    tempLT.posX = px + 20 + countleng + 2;
//                    tempLT.posY = py + 5;
//
//                    countleng += (leng * fontSizePerChar + 2);
//
//                }
//
//                allLabels.put(t, tempList);
//
//            }
        }

    }
    }
    
      
    
    private List<String[]> reorganizedTopics;
      
      
    private List<int[]> occurances;
    
    
        public void extractFrequency() {
        //Re-organize topics based on the similarities
        reorganizedTopics = new ArrayList<String[]>();                 
         
        reorganizedTopics.add(allTopics.get(0));
        for (int i = getGlobalReadIndex(); i < allTopics.size(); i++) {
            int t = (i - 1) + 1;
            reorganizedTopics.add(allTopics.get(t));
        }

        
        
        if (reorganizedTopics != null) {
            occurances = new ArrayList<int[]>(reorganizedTopics.size());
            for (int i = 0; i < reorganizedTopics.size(); i++) {
                int[] temp = new int[reorganizedTopics.get(0).length];
                occurances.add(temp);
            }
            //otherOccurances = new ArrayList<List<Dimension>>();
            int count = 1;
            Dimension keyPos, tempPos;
            List<Dimension> tmpDim = null;
            
                                 
            for (int i = getGlobalReadIndex(); i < reorganizedTopics.size(); i++) {
                for (int j = getGlobalReadIndex()+1; j < reorganizedTopics.get(i).length/*30*/; j++) {
                    //Compare every word with other words
                    keyPos = new Dimension(i, j);
                    for (int m = getGlobalReadIndex(); m < reorganizedTopics.size(); m++) {
                        for (int n = getGlobalReadIndex()+1; n < /*30*/reorganizedTopics.get(m).length; n++) {
                            if (m == i && n == j) {
                                //Skip the word itself
                            } else {
                                if (reorganizedTopics.get(i)[j].trim().equalsIgnoreCase(reorganizedTopics.get(m)[n].trim())) {
                                    count++;
//                                  
                                }
                            }
                        }
                    }

                    occurances.get(i)[j] = count;
                    
                    count = 1;
                }
            }
        }
        
    }
        
        
                        public class labelTextComparer implements Comparator<LabelText> {
        //@Override
//  public int compare(labelText x, labelText y) {
//    // TODO: Handle null x or y values
//    int startComparison = compare(x.probablity, y.probablity);
//    return startComparison != 0 ? startComparison
//                                : compare(x.probablity, y.probablity);
//  }

        // I don't know why this isn't in Long...
        private int compare(float a, float b) {
            return a > b ? -1
                    : a < b ? 1
                    : 0;
        }

        public int compare(LabelText x, LabelText y) {
            int startComparison = compare(x.probablity, y.probablity);
            return startComparison != 0 ? startComparison
                    : compare(x.probablity, y.probablity);

            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

     
    }
}
