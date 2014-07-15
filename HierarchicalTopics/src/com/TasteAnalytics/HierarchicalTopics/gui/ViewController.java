/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TasteAnalytics.HierarchicalTopics.gui;

import com.TasteAnalytics.HierarchicalTopics.datahandler.LDAHTTPClient;
import com.TasteAnalytics.HierarchicalTopics.eventsview.EventViewFrame;
import com.TasteAnalytics.HierarchicalTopics.eventsview.EventsViewListener;
import com.TasteAnalytics.HierarchicalTopics.eventsview.EventsViewPanel;
import com.TasteAnalytics.HierarchicalTopics.temporalView.renderer.TemporalViewFrame;
import com.TasteAnalytics.HierarchicalTopics.temporalView.renderer.TemporalViewListener;
import com.TasteAnalytics.HierarchicalTopics.temporalView.renderer.TemporalViewPanel;
import com.TasteAnalytics.HierarchicalTopics.temporalView.renderer.TreeNode;
import com.TasteAnalytics.HierarchicalTopics.topicRenderer.TopicGraphViewFrame;
import com.TasteAnalytics.HierarchicalTopics.topicRenderer.TopicGraphViewFrame.customLabelTimecolumnKey;
import com.TasteAnalytics.HierarchicalTopics.topicRenderer.VastGeoFrame;
import com.TasteAnalytics.HierarchicalTopics.topicRenderer.WorldMapProcessingFrame;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;
import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Table;
import wordle.layout.LabelWordleLite;
import wordle.layout.WordleAlgorithmLite;
import wordle.layout.WordleLite;

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
    public TopicGraphViewFrame topicGraphicPanel;
    private List<String[]> InternalUsageRecord;
    private List<String[]> InternalDocs;
    private List<Float> topicSimilarities;
  
    public boolean topicChanged = false;
    private DateFormat format;
    public String csvfFolderPath;
    public VastGeoFrame VCGF;
    public WorldMapProcessingFrame worldMapProcessingFrame;
    public List<Point2D> geoLocations;
    
    MinimalismMainFrame mf;
    public void setParentFrame(MinimalismMainFrame m){
        mf = m;        
    };
    
    public MinimalismMainFrame getParentFrame(){
        
        return mf;
    };
    
     public String CookieString = "";

     private LDAHTTPClient connection = null;

	public LDAHTTPClient getConnection() {
		return connection;
	}

	public boolean InitializeNetworkConnection(boolean initByCookie,
			String username, String password) {

		connection = new LDAHTTPClient("http",
				this.host, "2012");
		try {
			return connection.login(initByCookie, username, password);
		} catch (IOException ex) {
			return false;

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
        
          labelColor.push(new Color(228,26,28));                
        labelColor.push(new Color(55,126,184));
        labelColor.push(new Color(77,175,74));
        labelColor.push(new Color(152,78,163));
        
        labelColor.push(new Color(255,127,0));
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

    
    
    
    public TopicGraphViewFrame getTopicGraphViewPanel() {

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

    public void addTopicGraphViewPanel(TopicGraphViewFrame t) {
        topicGraphicPanel = t;
    }

//    
    public void addEventFrame(EventViewFrame ev) {
        eventViewFrame = ev;
    }

    public void addWorldMapProcessingFrame(WorldMapProcessingFrame wf) {
        worldMapProcessingFrame = wf;
    }

    public WorldMapProcessingFrame getWorldMapProcessingFrame() {
        if (worldMapProcessingFrame != null) {
            return this.worldMapProcessingFrame;
        }
        return null;
    }

    List<TopicGraphViewFrame.labelText> highlightedTextLabels;

    int previousTimeColumn = -1;
    TreeNode lastNode = new TreeNode();
    boolean isShowingSingleTopic = false;
   

    HashMap<TreeNode, List<TopicGraphViewFrame.labelText>> nodeKeywordHighlightMap = new HashMap<TreeNode, List<TopicGraphViewFrame.labelText>>();

    public void stateChangedSecond(TreeNode ct, int selectedTimeColumn, TemporalViewPanel ap, boolean isSingle) {
        
        
        
        
        
        
        
        
        
        
        
        currentNode = ct;

        List<List<int[]>> tYK = getTemporalFrame().getData().topicYearKwIdx;
        highlightedTextLabels = null;//.clear();
        //nodeKeywordHighlightMap = null;
        //nodeKeywordHighlightMap.clear();
        
        
//              if (currentNode == null)          
//                currentNode = ap.currentNode;
              
        
        if (ct == null)
        {
              if (ap.multiTopicKeywordList!=null)
                ap.multiTopicKeywordList.clear();
            
        }
              
        if (lastNode!=null && currentNode!=null)    
        if (selectedTimeColumn != previousTimeColumn || (!lastNode.equals(currentNode))) {
            
          
            
            
            TopicGraphViewFrame.customLabelTimecolumnKey key;
            //if (currentNode == null)          
                //currentNode = ap.currentNode;
            
            TreeNode ttt = findMatchingNodeInTopicGraph(currentNode);
            //key = new customLabelTimecolumnKey( ttt, selectedTimeColumn);
            
            
            key = new TopicGraphViewFrame.customLabelTimecolumnKey(currentNode.getValue(), String.valueOf(selectedTimeColumn));
            
            //System.out.println( ap.getLabelTimeMap().size());
            
           // System.out.println(key.toString());
            
            //System.out.println("you key? " + ap.getLabelTimeMap().containsKey(key));
         
            
                       highlightedTextLabels = ap.getLabelTimeMap().get(key);
            
            if (highlightedTextLabels==null)
            {
                
//                Iterator it = ap.getLabelTimeMap().keySet().iterator();
//            while(it.hasNext())
//            {
//                TopicGraphViewFrame.customLabelTimecolumnKey k = (TopicGraphViewFrame.customLabelTimecolumnKey) it.next();
//                System.out.println(k);
//            }
                
                System.out.println(key);
                System.out.println("no wordle cloud map ");
                
            }
           else
            {
                Point tmp_p = new Point(ap.getWidth()/2, ap.getHeight()/2);
                
                ap.DrawWordleCloud(tmp_p/*ap.currentMouseLocation*/, highlightedTextLabels);
            }
        }

        previousTimeColumn = selectedTimeColumn;
        lastNode = currentNode;

        // getTopicGraphViewPanel().updateLabelLocations();
        
        
        if (ct != null && ct.getChildren().isEmpty())
        {
            //System.out.println("ctct");
            TreeNode ttt = findMatchingNodeInTopicGraph(ct);
            //ap.drawTopicWords(ttt);
            ap.showingNode = ttt;
        }
        else
        {
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

    List<TopicGraphViewFrame.labelText> putUpHighlightedKeywordList(HashMap<TreeNode, List<TopicGraphViewFrame.labelText>> m) {
        List<TopicGraphViewFrame.labelText> r = new ArrayList<TopicGraphViewFrame.labelText>();

        int size = m.size();

        int maxkeywordnumber = 20;

        int iterationRuns = maxkeywordnumber / size;

        if (iterationRuns < 1) {
            iterationRuns = 1;
        }

        for (int i = 0; i < iterationRuns; i++) {
            for (Object o : m.values()) {
                r.add((TopicGraphViewFrame.labelText) ((List<TopicGraphViewFrame.labelText>) o).get(i));
            }
        }

        return r;
    }

    void updateGeoView(Color c, List<Integer> l) {
        if (getWorldMapProcessingFrame() != null) {
            getWorldMapProcessingFrame().UpdateGeoLocations(c, l);
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
    public String host = "caprica.uncc.edu";//"54.209.61.133";
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
    String id_type="";
    
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

    public void stateChangedFromLabelToTopic(HashMap<String, List<Integer>> highindex,HashMap<String, List<Float>> highWeight, HashMap<String, Color> highIndexNumber)
    {
        getTopicGraphViewPanel().setHighlightLabelsFromLabelTopics(highindex, highWeight, highIndexNumber);
        
        
    }
    
    
    
    
    public void stateChanged(TreeNode ct) {
        currentNode = ct;

        // change temporal view frame
        TemporalViewPanel targetPanel = getTemporalFrame().getSubPanel();
        getTemporalFrame().getSubPanel().currentNode = currentNode;
        targetPanel.currentNode = currentNode;
        targetPanel.calculateLocalNormalizingValue(targetPanel.getData(), targetPanel.currentNode);
        //targetPanel.setbShowEvents(false);
        targetPanel.getDetectionResults().clear();
        targetPanel.detectEvents(targetPanel.getEventThreshold());
        
        targetPanel.UpdateTemporalView(new Dimension(targetPanel.getMyPanelWidth(), targetPanel.getMyPanelHeight()), targetPanel.getLocalNormalizingValue());

        //                     targetPanel.calculateLocalNormalizingValue(attachedPanel.getData(), targetPanel.currentNode);
//
//                    targetPanel.calculateRenderControlPointsOfEachHierarchy(attachedPanel.getData(), targetPanel.currentNode, targetPanel.getLocalNormalizingValue());
//                    targetPanel.computerZeroslopeAreasHierarchy(0);
//                     targetPanel.detectEvents(targetPanel.getEventThreshold());
//                    targetPanel.UpdateTemporalView(new Dimension(targetPanel.getWidth(), targetPanel.getHeight()), targetPanel.getLocalNormalizingValue());
        // change topic graph view
        VisualizationViewer vv = getTopicGraphViewPanel().getVisualizationViewer();
        final PickedState<TreeNode> pickedState = vv.getPickedVertexState();
        //  int graphSize = vv.getGraphLayout().getGraph().getVertexCount();
        // vv.getLayout().getGraph() ;
        Collection<TreeNode> vertices = vv.getGraphLayout().getGraph().getVertices();

        pickedState.clear();
        //System.out.println(selectedNode.toString());
        int in = currentNode.getIndex();
        String sla = currentNode.getValue();
        //   System.out.println("selectedNode = " + in + " " + sla); 
        for (Iterator<TreeNode> it = vertices.iterator(); it.hasNext();) {

            Object k = it.next();
            if (k instanceof TreeNode) {
                TreeNode t = (TreeNode) k;
                if (in == t.getIndex() && (sla == null ? t.getValue() == null : sla.equals(t.getValue()))) {
                    pickedState.pick(t, true);
                    // System.out.println(t.getValue() + "is matched" );
                    break;
                }
            }
        }

        TreeNode tx = currentNode.getParent();
        while (tx != null) {
            int in1 = tx.getIndex();
            String sla1 = tx.getValue();

            innerCircle:
            for (Iterator<TreeNode> it1 = vertices.iterator(); it1.hasNext();) {

                TreeNode t1 = it1.next();
                if (in1 == t1.getIndex() && (sla1.equals(t1.getValue()))) {
                    pickedState.pick(t1, true);
                    //  System.out.println(t1.getValue() + "is matched" );
                    break innerCircle;
                }
            }

            tx = tx.getParent();
        }

        getTopicGraphViewPanel().getVisualizationViewer().setPickedVertexState(pickedState);

        vv.repaint();

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
     * @param get
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

//    public void sendToSPChart(int[] selected){
//        spChart.updateView(selected);
//    }
    /**
     * Setter for current selected documents in the parallel coordinate view.
     *
     * @param seletedRecord seleted ids in parallel coordinate.
     */
    public void setDocumentViewData(int[] selectedRecord) {
        docViewer.updateDocContent(selectedRecord);
    }

    public void setTopicSimilarities(List<Float> sim) {
        topicSimilarities = new ArrayList<Float>();
        topicSimilarities = sim;
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
    
    
   
    
    public static Color[] labelColors = 
    {
        new Color(141,211,199),
        new Color(255,255,179),
        new Color(190,186,218),
        new Color(251,128,114)
        
        
        
    };
    
    
   
    public void setNewHueColors() {
        hueColors = new ArrayList<Float[]>();

        float[] hsv = new float[3];
        float Alpha = 1;
        float R1 = 141;
        float G1 = 211;
        float B1 = 199;
        // Color.RGBtoHSB(R1,G1,B1,hsv);

        Float[] tmpColor = new Float[4];
        tmpColor[0] = Alpha;
        tmpColor[1] = R1 / (float) 255.0;
        tmpColor[2] = G1 / (float) 255.0;
        tmpColor[3] = B1 / (float) 255.0;
        hueColors.add(tmpColor);

        Float[] tmpColor1 = new Float[4];
        float R = (float) 255.0;
        float G = (float) 255.0;
        float B = (float) 179.0;
        tmpColor1[0] = Alpha;
        tmpColor1[1] = R / (float) 255.0;
        tmpColor1[2] = G / (float) 255.0;
        tmpColor1[3] = B / (float) 255.0;
        hueColors.add(tmpColor1);

        Float[] tmpColor2 = new Float[4];
        R = (float) 190.0;
        G = (float) 186.0;
        B = (float) 218.0;
        tmpColor2[0] = Alpha;
        tmpColor2[1] = R / (float) 255.0;
        tmpColor2[2] = G / (float) 255.0;
        tmpColor2[3] = B / (float) 255.0;
        hueColors.add(tmpColor2);

        Float[] tmpColor3 = new Float[4];
        R = (float) 251.0;
        G = (float) 128.0;
        B = (float) 114.0;
        tmpColor3[0] = Alpha;
        tmpColor3[1] = R / (float) 255.0;
        tmpColor3[2] = G / (float) 255.0;
        tmpColor3[3] = B / (float) 255.0;
        hueColors.add(tmpColor3);

        Float[] tmpColor4 = new Float[4];
        R = (float) 128.0;
        G = (float) 177.0;
        B = (float) 211.0;
        tmpColor4[0] = Alpha;
        tmpColor4[1] = R / (float) 255.0;
        tmpColor4[2] = G / (float) 255.0;
        tmpColor4[3] = B / (float) 255.0;
        hueColors.add(tmpColor4);

        Float[] tmpColor5 = new Float[4];
        R = (float) 253.0;
        G = (float) 180.0;
        B = (float) 98.0;
        tmpColor5[0] = Alpha;
        tmpColor5[1] = R / (float) 255.0;
        tmpColor5[2] = G / (float) 255.0;
        tmpColor5[3] = B / (float) 255.0;
        hueColors.add(tmpColor5);

        Float[] tmpColor6 = new Float[4];
        R = (float) 179.0;
        G = (float) 222.0;
        B = (float) 105.0;
        tmpColor6[0] = Alpha;
        tmpColor6[1] = R / (float) 255.0;
        tmpColor6[2] = G / (float) 255.0;
        tmpColor6[3] = B / (float) 255.0;
        hueColors.add(tmpColor6);

        Float[] tmpColor7 = new Float[4];
        R = (float) 252.0;
        G = (float) 205.0;
        B = (float) 229.0;
        tmpColor7[0] = Alpha;
        tmpColor7[1] = R / (float) 255.0;
        tmpColor7[2] = G / (float) 255.0;
        tmpColor7[3] = B / (float) 255.0;
        hueColors.add(tmpColor7);

        Float[] tmpColor8 = new Float[4];
        R = (float) 217.0;
        G = (float) 217.0;
        B = (float) 217.0;
        tmpColor8[0] = Alpha;
        tmpColor8[1] = R / (float) 255.0;
        tmpColor8[2] = G / (float) 255.0;
        tmpColor8[3] = B / (float) 255.0;
        hueColors.add(tmpColor8);

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
}
