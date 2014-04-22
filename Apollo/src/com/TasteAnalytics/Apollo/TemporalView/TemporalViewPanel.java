/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TasteAnalytics.Apollo.TemporalView;

import com.TasteAnalytics.Apollo.eventsview.Cusum;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Area;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.DateFormat;
import java.util.ArrayList;

import java.util.List;
import javax.swing.JPanel;
import com.TasteAnalytics.Apollo.GUI.ViewController;
import com.TasteAnalytics.Apollo.TopicRenderer.LabelText;
import com.TasteAnalytics.Apollo.datahandler.CategoryBarElement;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;
import org.apache.commons.io.IOUtils;
import java.util.HashMap;

import java.util.concurrent.TimeUnit;
import com.TasteAnalytics.Apollo.TopicRenderer.TopicGraphViewPanel;
import com.TasteAnalytics.Apollo.Wordle.LabelWordleLite;
import com.TasteAnalytics.Apollo.Wordle.WordleAlgorithmLite;
import com.TasteAnalytics.Apollo.Wordle.WordleLite;

/**
 *
 * @author sasa
 */
public class TemporalViewPanel extends JPanel implements TemporalViewListener, MouseListener, KeyListener {

    protected int width, height;
    private int myPanelWidth, myPanelHeight;
    private boolean firsttime;
    private boolean needDoLayout;
    private BufferedImage bi;
    private Graphics2D curg2d;
    private Rectangle area;
    protected int margin = 0;//24
    private CategoryBarElement data;
    private List<Integer> tSequence;
    private List<Float> topicSims;
    private List<Float[]> colorMap;
    private List<TreeNode> myTree;
    private TreeNode root;
    public TreeNode currentNode;
    //Li added
    private Point[][] testPoint;
    protected Point[][] currentPoint;
    private List<Point[][]> hierarchicalPoint;
    private String name;
    /**
     * The ParallelDisplay component we are assigned to.
     */
    public ViewController parent;
    private int drawPanelLabelId;
    private List<Integer> drawLabels;
    private List<Point2D> drawLabelsLocations;
    private List<Point2D> focusedSelectionList;
    private DateFormat timeIntervalFormat;
    private int level = -1;

    public void setMyPanelSize(int w, int h) {
        myPanelWidth = w;
        myPanelHeight = h;
        setPreferredSize(new Dimension(w, h));

    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getMyPanelWidth() {
        return myPanelWidth;
    }

    public void setMyPanelWidth(int myPanelWidth) {
        this.myPanelWidth = myPanelWidth;
    }

    public int getMyPanelHeight() {
        return myPanelHeight;
    }

    public void setMyPanelHeight(int myPanelHeight) {
        this.myPanelHeight = myPanelHeight;
    }

    private boolean zoomed = false;

    public boolean isZoomed() {
        return zoomed;
    }

    public void setZoomed(boolean zoomed) {
        this.zoomed = zoomed;
    }

    public void setsize(int w, int h) {
        this.width = w;
        this.height = h;
    }

    public List<Point2D> getFocusedSelectionList() {
        return focusedSelectionList;
    }

    public void setFocusedSelectionList(List<Point2D> focusedSelectionList) {
        this.focusedSelectionList = focusedSelectionList;
    }

    public void removeDocumentViewerat(Point2D pt) {

        if (pt.getY() == -99) {

            for (int k = 0; k < focusedSelectionList.size(); k++) {

                if (focusedSelectionList.get(k).getX() == pt.getX()) {
                    focusedSelectionList.remove(focusedSelectionList.get(k));
                    k--;

                }

            }

            for (Point2D t : focusedSelectionList) {

                if (t.getX() == pt.getX()) {
                    focusedSelectionList.remove(t);

                }
            }

        }

        if (this.focusedSelectionList.contains(pt)) {
            this.focusedSelectionList.remove(pt);

        }

        repaintView();

    }

//    
//    private List<Integer> focusedTimeColumnList;
//
//    public List<Integer> getFocusedTimeColumnList() {
//        return focusedTimeColumnList;
//    }
//
//    public void setFocusedTimeColumnList(List<Integer> focusedTimeColumnList) {
//        this.focusedTimeColumnList = focusedTimeColumnList;
//    }
//    
//    
//    private List<Integer> focusedStreamList;
//
//    public List<Integer> getFocusedStreamList() {
//        return focusedStreamList;
//    }
//
//    public void setFocusedStreamList(List<Integer> focusedStreamList) {
//        this.focusedStreamList = focusedStreamList;
//    }
    public void setNeedDoLayout(boolean b) {

        needDoLayout = b;
    }
    private boolean b_timeColumn;

    public boolean getPanelTimeColumnMode() {

        return b_timeColumn;

    }

    public void setPanelTimeColumnMode(boolean d) {

        b_timeColumn = d;

    }

    public List<Point2D> getDrawLabelsLocation() {

        return drawLabelsLocations;

    }

    public List<Integer> getDrawLabels() {

        return drawLabels;

    }

    public void setPanelLabelId(int d) {

        drawPanelLabelId = d;

    }
    private TemporalViewPanel fatherPanel;

    public TemporalViewPanel getFatherPanel() {
        return fatherPanel;
    }

    public void setFatherPanel(TemporalViewPanel p) {
        this.fatherPanel = p;
    }
    private List<TemporalViewPanel> childPanel;

    public List<TemporalViewPanel> getchildPanel() {
        return childPanel;
    }

    public void setFatherPanel(List<TemporalViewPanel> p) {
        this.childPanel = p;
    }

    public void addChildPanel(TemporalViewPanel p) {
        this.childPanel.add(p);
    }

    public void removeChildPanel(TemporalViewPanel p) {
        this.childPanel.remove(p);
    }

    private boolean bShowEvents = false;

    public boolean isbShowEvents() {
        return bShowEvents;
    }

    public void setbShowEvents(boolean bShowEvents) {
        this.bShowEvents = bShowEvents;
    }

    public void setData(CategoryBarElement d) {

        data = d;

    }

    public void setTree(List<TreeNode> t) {

        if (myTree != null) {

            myTree = t;
        }
    }

    public CategoryBarElement getData() {
        return data;
    }
    private final static Font NORMALFONT = new Font("Sans-Serif", Font.PLAIN, 12);
    private final static Font TIMEDOMAINFONT = new Font("Sans-Serif", Font.BOLD, 12);

    /**
     * Provide the Control Points for the ThemeRiver Renderer
     *
     * @return
     */
    public Point[][] getControlpoints() {
        return controlpoints;
    }
    private Point[][] controlpoints; // Control points for the themeriver
    private List<CategoryStream> categorystreams;
    private List<CategoryStream> currentStreams;
    private List<ArrayList<CategoryStream>> hierarchicalstreams;

    public List<CategoryStream> getCategoryAreas() {
        return categorystreams;
    }

    public List<CategoryStream> getCurrentAreas() {
        return currentStreams;
    }

    public void setCurrentAreas(List<CategoryStream> a) {
        currentStreams = a;
    }

    protected List<TimeColumn> timecolumns;

    public void setTimecolumns(List<TimeColumn> timecolumns) {
        this.timecolumns = timecolumns;
    }

    public List<TimeColumn> getTimecolumns() {
        return timecolumns;
    }
    private int focusedCatgoryId = -99;
    private int timeColumnStream = -99;

    public void setTimeColumnStream(int j) {
        timeColumnStream = j;

    }

    public int getFocusedCatgory() {
        return focusedCatgoryId;
    }

    //int previousFocusedCategory = -99;
    public void setFocusedCatgory(int focusedCatgory) {
        this.focusedCatgoryId = focusedCatgory;
        //System.out.println("Set Focused Area: " + this.focusedCatgoryId);
        // this.repaint();

    }

    public void setName(String n) {
        this.name = n;

    }

    public String getName() {
        return this.name;

    }

    public int getFocusedColumn() {
        return focusedColumnId;
    }

    public void setFocusedColumn(int focusedColumn) {
        this.focusedColumnId = focusedColumn;
    }
    int displayLengendTimeColumn = -1;
    int lastDisplayLengendTimeColumn = -1;

    public void setDisplayLengendTimeColumn(int focusedColumn) {
        this.displayLengendTimeColumn = focusedColumn;
    }

    public int getLastDisplayLengendTimeColumn() {
        return this.lastDisplayLengendTimeColumn;
    }

    public void setLastDisplayLengendTimeColumn(int focusedColumn) {
        this.lastDisplayLengendTimeColumn = focusedColumn;
    }

    public List<float[]> getEventStreams() {
        return data.getCategoryBar();
    }
    private int focusedColumnId = -99;
    private Color[] colors;

    public List<TreeNode> getTree() {
        return myTree;
    }

    public Color[] getColors() {
        return colors;
    }

    public void setColor(int index, Color c) {
        colors[index] = new Color(ColorBrewer.colors[index][0], ColorBrewer.colors[index][1], ColorBrewer.colors[index][2]);
    }
    private Color[] currentColors;

    public Color[] getCurrentColors() {
        return currentColors;
    }

    public void setCurrentColor(int index, Color c) {
        currentColors[index] = new Color(ColorBrewer.colors[index][0], ColorBrewer.colors[index][1], ColorBrewer.colors[index][2]);
    }

    public void setTopicSimilarities(List<Float> sims) {
        topicSims = new ArrayList<Float>();
        topicSims = sims;
    }

//    public void loadData(String tmpURL) throws MalformedURLException, IOException {
//        data = new CategoryBarElement(tmpURL);
//        calculateRenderControlPoints(data);
//        setVisible(true);
//
//    }

//    public void loadData(List<String[]> internalRecord, List<Integer> topicSequence, List<Long> years,
//            List<String[]> allDocs, List<String[]> termWeights, List<float[]> termWeights_norm, Map<String, Integer> termIndex, List<String[]> allTopics, String path, int contentIdx, DateFormat format) throws IOException {
//        data = new CategoryBarElement(internalRecord, topicSequence, years, allDocs, termWeights, termWeights_norm,termIndex, allTopics, path, contentIdx, format);
//
//
//        BuildNodeValue(data, treeNodes.get(0));
//
//        currentNodetreeNodesree.get(0);
//        // calculateRenderControlPoints(data);
//
//        timeIntervalFormat = format;
//        setVisible(true);
//        tSequence = new ArrayList<Integer>();
//        tSequence = topicSequence;
//    }
    public List<Integer> getTSequence() {
        return tSequence;
    }

    public TemporalViewPanel() {
        //  super("ThemeRiver");
    }
    //public TemporalViewPanel(ParallelDisplay display) {

    public TemporalViewPanel(ViewController viewController) throws IOException {
        this.parent = viewController;
        viewController.addTemporalViewListener(this);
        this.setPanelTimeColumnMode(true);
        // mainPanel = new javax.swing.JPanel();
        // this.setTitle("ThemeRiver");
        //  setPreferredSize(new Dimension(1200, 900));
        //setSize(600, 500);
        eventThreshold = (float) 3.0;
        childPanel = new ArrayList<TemporalViewPanel>();

        focusedSelectionList = new ArrayList<Point2D>();
        
        

        //focusedTimeColumnList = new ArrayList<Integer>();
        //focusedStreamList = new ArrayList<Integer>();
        width = 900;
        height = 1000;

        
        
        drawPanelLabelId = -1;
        drawLabels = new ArrayList<Integer>();
        drawLabelsLocations = new ArrayList<Point2D>();
        // subPanel = new javax.swing.JPanel();

        // this.getContentPane().setLayout(new BorderLayout());
        //this.add(mainPanel, BorderLayout.WEST);
        //this.add(subPanel, BorderLayout.EAST);
        //  this.getContentPane().add(mainPanel);
        // this.getContentPane().add(subPanel);
        TemporalViewInteractions interactions = new TemporalViewInteractions(this);
        addMouseListener(interactions);
        addMouseMotionListener(interactions);

        categorystreams = new ArrayList<CategoryStream>();
        timecolumns = new ArrayList<TimeColumn>();

        currentStreams = new ArrayList<CategoryStream>();

        firsttime = true;
        needDoLayout = true;

        hierarchicalstreams = new ArrayList<ArrayList<CategoryStream>>();

        hierarchicalPoint = new ArrayList<Point[][]>();

        //currentPoint = new Point[][];
        this.addKeyListener(this);  // This class has its own key listeners.
        this.setFocusable(true);

        myTree = new ArrayList<TreeNode>();
        // buildTree();

        this.addComponentListener(new ComponentListener() {
            public void componentResized(ComponentEvent e) {

                ((TemporalViewPanel) e.getComponent()).invalidate();//UpdateTemporalView(new Dimension(e.getComponent().getSize().width, e.getComponent().getSize().height));

                //System.out.println(e.toString() + " w "+ e.getComponent().getSize().width + " h "+ e.getComponent().getSize().height);
            }

            public void componentMoved(ComponentEvent e) {
                //throw new UnsupportedOperationException("Not supported yet.");
            }

            public void componentShown(ComponentEvent e) {
                //throw new UnsupportedOperationException("Not supported yet.");
            }

            public void componentHidden(ComponentEvent e) {
                //throw new UnsupportedOperationException("Not supported yet.");
            }
        });

    }

    public void buildTree() throws FileNotFoundException, IOException {

        String everything;
        FileInputStream inputStream = new FileInputStream("./data/tree.txt");
        try {
            everything = IOUtils.toString(inputStream);
        } finally {
            inputStream.close();
        }

        Scanner sc = new Scanner(everything);

        sc.useDelimiter("\n");
        String temp = sc.next();

        String nodes = sc.next();

        String[] tempNodes = nodes.split(",");

        TreeNode NodeArray[] = new TreeNode[100];
        TreeNode LeafArray[] = new TreeNode[100];

        for (int i = 0; i < tempNodes.length - 1; i++) {

            String a = tempNodes[i].replaceAll("\\D", "");

            int index = Integer.parseInt(a);

            TreeNode t = new TreeNode(index, tempNodes[i].replaceAll("[^\\p{L}\\p{N}]", ""));

            if (tempNodes[i].replaceAll("[^\\p{L}\\p{N}]", "").charAt(0) == 'N') {
                NodeArray[index] = t;
                myTree.add(t);
            } else if (tempNodes[i].replaceAll("[^\\p{L}\\p{N}]", "").charAt(0) == 'L') {
                LeafArray[index] = t;
                myTree.add(t);
            } else {
                int c = 0;
            }
        }

        String edges = sc.next();

        String[] tempEdges = edges.split("\\),");
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

    public void UpdateTemporalView(Dimension size, float normalValue) {
        width = size.width;
        height = size.height;

        margin = 0;//(int) height * 24 / 1800;

        this.setPreferredSize(size);

        // System.out.println("ReCalculated" + countnn);
        if (zoomed) {
            calculateRenderControlPointsOfEachHierarchySub(data, currentNode, localNormalizingValueSub);
        } else {
            calculateRenderControlPointsOfEachHierarchy(data, currentNode, normalValue);
        }

        firsttime = false;
        needDoLayout = true;

        if (!zoomed) {
            computeEventOutlineArea();
        }

        repaintView();
    }

    private void clearPreviousValues() {
        categorystreams.clear();
        hierarchicalstreams.clear();
        currentStreams.clear();
    }

    public void repaintView() {
        
        if (width!=0 && height!=0){
        area = new Rectangle(width, height);
        bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        curg2d = bi.createGraphics();
        curg2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        update(curg2d);
        this.repaint();
        }
    }

    private void calculateRenderControlPoints(CategoryBarElement data) {

        float maxValue = 1.0f;//data.getMaximum();
        try {

            int numofYears = data.getNumOfYears();// Number of bars
            System.out.println(numofYears);

            int topicNumber = data.getNumberOfTopics(); // Number of Topics

            timecolumns.clear();

//        int topicGroups = 3;
//        testPoint = new Point[numofYears+2][topicGroups+1];
//         int h = height/3 - margin ;
//
//         int offset_h = height/3;
//         int offset_w = 0;//width/3;
//         
//        double verticalratio = (double) h / maxValue;
//        double horizontalratio = (double) width/2 / (double) numofYears;
//        
//        
//          for (int icol = 0; icol < topicGroups; icol++) {
//            testPoint[0][icol] = new Point(0, (int) (height/3 * 0.5 + offset_h));
//            testPoint[numofYears + 1][icol] = new Point(width/2 + offset_w, (int) (height/3 * 0.5 + offset_h));
//        }
//
//        testPoint[0][topicGroups] = new Point(0 + offset_w, (int) (height/3 * 0.5 + offset_h));
//        testPoint[numofYears + 1][topicGroups] = new Point(width/2 + offset_w, (int) (height/3 * 0.5 + offset_h));
//
//            for (int i = 0; i < numofYears; i++) {
//                List<Float> columValues = data.getColum(i);
//
//                // Get individual size. Use this to compare with the maxValue
//                double sum = 0;
//                for (Float f : columValues) {
//                    sum += f;
//                }
//                //XW: Hack to show all the themriver results
//                int offset = (int) (margin * 1.25 + (maxValue - sum) * 0.5 * verticalratio);
//                for (int j = 0; j < topicGroups; j++) {
//
//                    if (j == 0) {
//                        testPoint[i + 1][j] = new Point((int) ((i + 0.5) * horizontalratio  + offset_w), offset  + offset_h);
//                        float tempSum = 0;
//                        tempSum = columValues.get(0) + columValues.get(1) + columValues.get(2);
//                        offset += (int) (tempSum * verticalratio);
//                    }
//                    else if (j == 1) {
//                         testPoint[i + 1][j] = new Point((int) ((i + 0.5) * horizontalratio + offset_w), offset + offset_h);
//                        float tempSum = 0;
//                        tempSum = columValues.get(3) + columValues.get(4) + columValues.get(5);
//                        offset += (int) (tempSum * verticalratio);
//                    }
//                    else if (j == 2) {
//                         testPoint[i + 1][j] = new Point((int) ((i + 0.5) * horizontalratio + offset_w), offset + offset_h);
//                        float tempSum = 0;
//                        tempSum = columValues.get(6) + columValues.get(7) + columValues.get(8) + columValues.get(9);
//                        offset += (int) (tempSum * verticalratio);
//                    }
//
//                    //testPoint[i + 1][j] = new Point((int) ((i + 0.5) * horizontalratio), offset);
//
//
//                }
//
//                testPoint[i + 1][topicGroups] = new Point((int) ((i + 0.5) * horizontalratio + offset_w), offset + offset_h);
//                //TODO: Fix the year
//                timecolumns.add(new TimeColumn(i, null, null, i * horizontalratio, 0 + offset_w, horizontalratio, height/3 - offset_h));
//            }
            // Considering the starting and ending points in the river
            controlpoints = new Point[numofYears + 2][topicNumber + 1];

            //Ratio: Pixel per unit
            int h = height - margin;

            double verticalratio = (double) h / maxValue;
            double horizontalratio = (double) width / (double) numofYears;
            // Inserting the starting and ending points.
            for (int icol = 0; icol < topicNumber; icol++) {
                controlpoints[0][icol] = new Point(0, (int) (height * 0.5));
                controlpoints[numofYears + 1][icol] = new Point(width, (int) (height * 0.5));
            }

            controlpoints[0][topicNumber] = new Point(0, (int) (height * 0.5));
            controlpoints[numofYears + 1][topicNumber] = new Point(width, (int) (height * 0.5));

            for (int i = 0; i < numofYears; i++) {
                List<Float> columValues = data.getColum(i);

                // Get individual size. Use this to compare with the maxValue
                double sum = 0;
                for (Float f : columValues) {
                    sum += f;
                }
                //XW: Hack to show all the themriver results
                int offset = (int) (margin * 1.25 + (maxValue - sum) * 0.5 * verticalratio);
                for (int j = 0; j < topicNumber; j++) {
                    controlpoints[i + 1][j] = new Point((int) ((i + 0.5) * horizontalratio), offset);

                    offset += (int) (columValues.get(j) * verticalratio);
                }

                controlpoints[i + 1][topicNumber] = new Point((int) ((i + 0.5) * horizontalratio), offset);
                // // TODO: Fix the year
                timecolumns.add(new TimeColumn(i, null, null, i * horizontalratio, 0, horizontalratio, height));
            }
        } catch (Exception e) {
            System.out.println("Calculate control point function failed!_themeriver");
        }
    }

    private float[] BuildNodeValue(CategoryBarElement data, TreeNode t) {
        int numofYears = data.getNumOfYears();
        List<Float> result = new ArrayList<Float>();
        float[] tempresult = new float[numofYears];
        float[] tempsum = new float[numofYears];

        if (t.getArrayValue().isEmpty()) {
            if (t.getChildren().isEmpty()) {
                int index = t.getIndex();
                tempresult = data.getCategoryBar().get(index);

                for (int i = 0; i < numofYears; i++) {
                    result.add(tempresult[i]);
                }

                t.setArrayValue(result);

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

    public void ClearZoomedNodeValue(TreeNode t) {
        t.getValueSub().clear();
        if (t.getChildren().isEmpty()) {
            t.getValueSub().clear();
            System.out.println(t.getIndex() + "subvalue cleared");
        } else {

            for (int i = 0; i < t.getChildren().size(); i++) {
                ClearZoomedNodeValue((TreeNode) t.getChildren().get(i));

            }

        }

    }

    public float[] BuildZoomedNodeValue(CategoryBarElement data, TreeNode t, int timeslot) {
        int numofYears = data.getNumOfTemporalBinsSub();//data.getNumOfYears();
        List<Float> result = new ArrayList<Float>();
        float[] tempresult = new float[numofYears];
        float[] tempsum = new float[numofYears];

        if (t.getValueSub().isEmpty()) {
            if (t.getChildren().isEmpty()) {
                int index = t.getIndex();
                tempresult = data.getCategoryBarSub().get(index);

                for (int i = 0; i < data.getNumOfTemporalBinsSub(); i++) {
                    result.add(tempresult[i]);
                }

                t.setValueSub(result);

                System.out.println(t.getIndex() + "subvalue built");
                return tempresult;

            } else {

                for (int i = 0; i < t.getChildren().size(); i++) {
                    for (int j = 0; j < data.getNumOfTemporalBinsSub(); j++) {
                        tempresult = BuildZoomedNodeValue(data, (TreeNode) (t.getChildren().get(i)), 0);
                        tempsum[j] += tempresult[j];
                    }

                }

                for (int i = 0; i < data.getNumOfTemporalBinsSub(); i++) {
                    result.add(tempsum[i]);
                }

                t.setValueSub(result);
                return tempsum;
            }
        }

        result = t.getValueSub();
        for (int i = 0; i < data.getNumOfTemporalBinsSub(); i++) {
            tempresult[i] = result.get(i);
        }

        return tempresult;

    }

    private float localNormalizingValue;

    public float getLocalNormalizingValue() {
        return this.localNormalizingValue;
    }

    public void setLocalNormalizingValue(float f) {
        this.localNormalizingValue = f;
    }
    private float globalNormalizingValue;

    public float getGlobalNormalizingValue() {
        return this.globalNormalizingValue;
    }

    public void setGlobalNormalizingValue(float f) {
        this.globalNormalizingValue = f;
    }

    public float calculateLocalNormalizingValue(CategoryBarElement data, TreeNode t) {

        localNormalizingValue = 1;
        int numofYears = data.getNumOfYears();// Number of bars
        //System.out.println(numofYears);

        int numberOfCategories = t.getChildren().size();

        if (numberOfCategories == 0) {
            numberOfCategories = 1;
        }

        float localmaxValue = -0.1f;

        for (int i = 0; i < numofYears; i++) {

            List<Float> columValues = new ArrayList<Float>();

            if (numberOfCategories == 1) {
                TreeNode tempn = t;
                columValues.add(tempn.getArrayValue().get(i));

            } else {
                for (int j = 0; j < numberOfCategories; j++) {
                    TreeNode tempn = (TreeNode) t.getChildren().get(j);
                    columValues.add(tempn.getArrayValue().get(i));
                }
            }
            // Get individual size. Use this to compare with the maxValue
            double sum = 0;

            for (Float f : columValues) {
                sum += f;
            }
            if (sum >= localmaxValue) {
                localmaxValue = (float) sum;
            }

        }

        localNormalizingValue = localmaxValue;

        return localNormalizingValue;

    }

    private float localNormalizingValueSub;

    public float getLocalNormalizingValueSub() {
        return localNormalizingValueSub;
    }

    public float calculateLocalNormalizingValueSub(CategoryBarElement data, TreeNode t) {

        localNormalizingValueSub = 1;

        int numofYears = data.getNumOfTemporalBinsSub();

        int numberOfCategories = t.getChildren().size();

        if (numberOfCategories == 0) {
            numberOfCategories = 1;
        }

        float localmaxValue = -0.1f;

        for (int i = 0; i < numofYears; i++) {

            List<Float> columValues = new ArrayList<Float>();

            if (numberOfCategories == 1) {
                TreeNode tempn = t;
                columValues.add(tempn.getValueSub().get(i));

            } else {
                for (int j = 0; j < numberOfCategories; j++) {
                    TreeNode tempn = (TreeNode) t.getChildren().get(j);
                    columValues.add(tempn.getValueSub().get(i));
                }
            }
            // Get individual size. Use this to compare with the maxValue
            double sum = 0;

            for (Float f : columValues) {
                sum += f;
            }
            if (sum >= localmaxValue) {
                localmaxValue = (float) sum;
            }

        }

        localNormalizingValueSub = localmaxValue;

        return localNormalizingValueSub;

    }

    public void calculateRenderControlPointsOfEachHierarchy(CategoryBarElement data, TreeNode t, float normalValue) {

        float maxValue = 1.0f;
        
        try {

            int numofYears = data.getNumOfYears();// Number of bars            
            int numberOfCategories = t.getChildren().size();

            if (numberOfCategories == 0) {
                numberOfCategories = 1;
            }

            timecolumns.clear();
            
            int scale = 1;//numberOfCategories;
            int wscale = 1;
            
            
            float mover = 1;  //theme rive 0.5, stacked graph 1
            
            
            currentPoint = new Point[numofYears + 2][numberOfCategories + 1];
            int h = (height) / scale - margin;
            
            double verticalratio = (double) h / scale / 1;//localmaxValue;//maxValue;
            double horizontalratio = (double) width / wscale / (double) numofYears;

            for (int icol = 0; icol < numberOfCategories; icol++) {
                currentPoint[0][icol] = new Point(0, (int) (height / scale / 1 *mover ));
                currentPoint[numofYears + 1][icol] = new Point(width / wscale , (int) (height / scale / 1 * mover ));
            }

            currentPoint[0][numberOfCategories] = new Point(0 , (int) (height / scale / 1 * mover));
            currentPoint[numofYears + 1][numberOfCategories] = new Point(width / wscale , (int) (height / scale / 1 *mover ));

            //find max value
            for (int i = 0; i < numofYears; i++) {

                List<Float> columValues = new ArrayList<Float>();

                if (numberOfCategories == 1) {
                    TreeNode tempn = t;
                    columValues.add(tempn.getArrayValue().get(i));

                } else {
                    for (int j = 0; j < numberOfCategories; j++) {
                        TreeNode tempn = (TreeNode) t.getChildren().get(j);
                        columValues.add(tempn.getArrayValue().get(i));
                    }
                }
                // Get individual size. Use this to compare with the maxValue
                double sum = 0;

                for (Float f : columValues) {
                    sum += f;
                }

                sum /= normalValue;

                int offset = (int) (margin * 1.25 + (maxValue - sum) * mover * verticalratio);
                for (int j = 0; j < numberOfCategories; j++) {

                    currentPoint[i + 1][j] = new Point((int) ((i + mover) * horizontalratio ), offset );
                    offset += (int) (columValues.get(j) / normalValue * verticalratio);

                }

                currentPoint[i + 1][numberOfCategories] = new Point((int) ((i + mover) * horizontalratio ), offset );
                //TODO: Fix the year
                timecolumns.add(new TimeColumn(i, null, null, i * horizontalratio, 0 , horizontalratio, height / scale ));
            }

            
        } catch (Exception e) {
            System.out.println("Calculate hierarchical control point function failed!_themeriver");
        }
    }

    public void calculateRenderControlPointsOfEachHierarchySub(CategoryBarElement data, TreeNode t, float normalValue) {

        subtimecolumns.clear();

        float maxValue = 1.0f;//data.getMaximum();
        // hierarchicalPoint.clear();
        try {

            int numofYears = data.getNumOfTemporalBinsSub();//data.getNumOfYears();// Number of bars
            //System.out.println(numofYears);

            int numberOfCategories = t.getChildren().size();

            if (numberOfCategories == 0) {
                numberOfCategories = 1;
            }

            // List<float[]> values = data.getCategoryBarSub();
            currentPoint = new Point[numofYears + 2][numberOfCategories + 1];

            double verticalratio = (double) this.height / 1;//localmaxValue;//maxValue;
            double horizontalratio = (double) width / (double) numofYears;

            for (int icol = 0; icol < numberOfCategories; icol++) {
                currentPoint[0][icol] = new Point(0, (int) (this.height / 1 * 0.5));
                currentPoint[numofYears + 1][icol] = new Point(width, (int) (this.height * 0.5));
            }

            currentPoint[0][numberOfCategories] = new Point(0, (int) (this.height / 1 * 0.5));
            currentPoint[numofYears + 1][numberOfCategories] = new Point(width, (int) (this.height / 1 * 0.5));

            for (int i = 0; i < numofYears; i++) {

                List<Float> columValues = new ArrayList<Float>();

                if (numberOfCategories == 1) {
                    TreeNode tempn = t;
                    columValues.add(tempn.getValueSub().get(i));

                } else {
                    for (int j = 0; j < numberOfCategories; j++) {
                        TreeNode tempn = (TreeNode) t.getChildren().get(j);
                        columValues.add(tempn.getValueSub().get(i));
                    }
                }
                // Get individual size. Use this to compare with the maxValue
                double sum = 0;

                for (Float f : columValues) {
                    sum += f;
                }

                sum /= normalValue;

                int offset = (int) (margin * 1.25 + (maxValue - sum) * 0.5 * verticalratio);
                for (int j = 0; j < numberOfCategories; j++) {

                    currentPoint[i + 1][j] = new Point((int) ((i + 0.5) * horizontalratio), offset);
                    offset += (int) (columValues.get(j) / normalValue * verticalratio);

                }

                currentPoint[i + 1][numberOfCategories] = new Point((int) ((i + 0.5) * horizontalratio), offset);
                //TODO: Fix the year
                subtimecolumns.add(new TimeColumn(i, null, null, i * horizontalratio, 0, horizontalratio, height));
                //here 

            }

            for (int icol = 0; icol < numberOfCategories + 1; icol++) {
                currentPoint[0][icol] = new Point(0, currentPoint[1][icol].y);
                currentPoint[numofYears + 1][icol] = new Point(width, currentPoint[numofYears][icol].y);
            }

        } catch (Exception e) {
            System.out.println("Calculate sub hierarchical control point function failed!_themeriver");
        }
    }

    List<TimeColumn> subtimecolumns = new ArrayList<TimeColumn>();

    public List<TimeColumn> getSubtimecolumns() {
        return subtimecolumns;
    }

    //@Override
    @Override
    public void paint(Graphics g) {
        //super.paint(g);
        update(g);
    }
    int countnn = 0;

    @Override
    public void update(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        //System.out.println("Rendering");
        if (firsttime) {
            area = new Rectangle(width, height);
            bi = (BufferedImage) createImage(width, height);
            curg2d = bi.createGraphics();
            curg2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            firsttime = false;
        }

        // Clears the rectangle that was previously drawn.
        if (curg2d == null) return;
        
        curg2d.setColor(Color.WHITE);
        //curg2d.setColor(Color.black);
        curg2d.fillRect(0, 0, area.width, area.height);

        if (needDoLayout) {

            // System.out.println( this.name + "do le mei?");
            clearPreviousValues();
            // computerZeroslopeAreas();
            computerZeroslopeAreasHierarchy(0);

            //System.out.println("need do layout" + ++countnn);
            needDoLayout = false;
        }

        renderAreas(curg2d);
        
       
        drawTimeLine(curg2d);

        curg2d.setColor(Color.BLACK);

//         curg2d.setColor(Color.red);
//        for (int i=0; i<testEventPoints.size(); i++)        
//            curg2d.fillRect((int)testEventPoints.get(i).getX(), (int)testEventPoints.get(i).getY(), 5, 5);


//        curg2d.setColor(Color.BLACK);

        
        if (!testEventPoints.isEmpty() && bShowEvents) {

            curg2d.setStroke(new BasicStroke(2));

            for (int i = 0; i < contours.length; i++) {
                for (int j = 0; j < contours[0].length; j++) {
                    if (contours[i][j][0] != null) {
                        curg2d.draw(contours[i][j][0]);
                        curg2d.draw(contours[i][j][1]);

                        //  curg2d.drawLine((int)contours[i][j][0].getX1(), (int)contours[i][j][0].getY1(), (int)contours[i][j][1].getX1(), (int)contours[i][j][1].getY1());
                        // curg2d.drawLine((int)contours[i][j][0].getX2(), (int)contours[i][j][0].getY2(), (int)contours[i][j][1].getX2(), (int)contours[i][j][1].getY2());
                    }
                }
            }

            curg2d.setStroke(new BasicStroke(1));

        }

        curg2d.setColor(Color.BLACK);

        if (drawPanelLabelId != -1) {
            Font font = new Font("Arial", Font.ITALIC, 30);

            curg2d.setFont(font);
           // curg2d.drawString(Integer.toString(drawPanelLabelId), 0, 30);
            
            int size = currentNode.getTopicsContainedIdx().size();
            String currentTopics = "";
            
            
            for (int i=0; i<size;i++)
            {
                currentTopics += "Topic" + Integer.toString(currentNode.getTopicsContainedIdx().get(i)) + " ";
                
            }
            
            font = new Font("Arial", Font.ITALIC, 20);

            curg2d.setFont(font);
            
          //  curg2d.drawString(currentTopics, 0, 50);
            
        
        }
        
        
        // display topic words
//        if (showingNode!=null)
//            {
//                
//               
//                   String nodetopics = "";    
//          
// 
//                for (int i = 1; i < 10; i++) {
//                    nodetopics += showingNode.getNodeTopics()[i] + " ";
//                }
//                
//                Font font = new Font("Arial", Font.ITALIC, 15);
//                    
//                    curg2d.setFont(font);
//                    //curg2d.setColor(showingNode.getColor());
//                   curg2d.drawString(nodetopics, 0, this.getHeight()-this.getHeight()/10);
//            }           
        
//        curg2d.drawLine(0, 0, (int) width, 0);
//        curg2d.drawLine(0, 0, 0, height);
//        curg2d.drawLine(0, height, width, height);
//        curg2d.drawLine(width, 0, width, height);

//         curg2d.drawLine(width/2, 0, width/2, height);
//         
//         
//         curg2d.drawLine(width/2, height/3 + height/9, width, height/3 + height/9);
//         curg2d.drawLine(width/2, height/3 + height/9*2, width, height/3 + height/9*2);
//      
        // Draws the buffered image to the screen.
        g2.drawImage(bi, 0, 0, this);
    }

    public void computeLinearAreas() {

        if (controlpoints != null && controlpoints.length != 1) {

            GeneralPath[] regions = new GeneralPath[controlpoints[0].length];

            for (int i = 0; i < controlpoints[0].length; i++) {
                regions[i] = new GeneralPath();
            }

            for (int i = 0; i < controlpoints.length - 1; i++) {
                for (int j = 0; j < controlpoints[0].length; j++) {
                    curg2d.setColor(Color.getHSBColor(ColorBrewer.colors[i][0], ColorBrewer.colors[i][1], ColorBrewer.colors[i][2]));
                    Point p1 = controlpoints[i][j];
                    Point p2 = controlpoints[i + 1][j];
                    Line2D.Double currline = new Line2D.Double(p1, p2);
                    regions[j].append(currline, true);
                }
            }

            //categoryStreams = new CategoryStream[controlpoints[0].length - 1];
            for (int i = 0; i < controlpoints[0].length - 1; i++) {
                GeneralPath currpath = new GeneralPath();
                currpath.append(regions[i], false);

                currpath.lineTo((float) width, (float) height);
                currpath.lineTo((float) 0, (float) height);
                currpath.closePath();

                GeneralPath currpath2 = new GeneralPath();
                currpath2.append(regions[i + 1], false);

                currpath2.lineTo((float) width, (float) 0);
                currpath2.lineTo((float) 0, (float) 0);
                currpath2.closePath();

                // Remain for the Area of Magic Lens
//                Area lens = new Area(new Rectangle(lenscenterx - lenswidth / 2, MARGIN / 2, lenswidth, height - MARGIN));
                Area upper = new Area(currpath);
                upper.intersect(new Area(currpath2));
//                upper.subtract(lens);
                categorystreams.add(new CategoryStream(upper));
            }

            assignAreaColors();
        }
    }

    private void computerZeroslopeAreas() {

//        
//         if (testPoint != null && testPoint.length != 1) {
//
//            //GeneralPaths which will convert to splines
//            GeneralPath[] regions = new GeneralPath[testPoint[0].length];
//
//            double ypos = (height - margin) / 2;
//            double maxposx = testPoint[0][0].x;
//            CubicCurve2D[] oldcurves = new CubicCurve2D[testPoint[0].length];
//
//            for (int i = 0; i < testPoint[0].length; i++) {
//                //Set the start of the spline
//                oldcurves[i] = new CubicCurve2D.Double();
//                oldcurves[i].setCurve(0, ypos, maxposx / 4, ypos, maxposx / 2, ypos, maxposx * 0.75,
//                        testPoint[0][i].y);
//                //Initializing the general path
//                regions[i] = new GeneralPath();
//            }
//
//
//            for (int i = 0; i < testPoint.length - 1; i++) {
//                for (int j = 0; j < testPoint[0].length; j++) {
//
//                    Point p1 = testPoint[i][j];
//                    Point p4 = testPoint[i + 1][j];
//
//                    //1. Inspection point: X-pos: half distance between P1 and p4
//                    //                  Y-pos: the P1
//                    Point2D.Double p2 = new Point2D.Double((double) (p1.x + (p4.x - p1.x) * 0.5), (double) p1.y);
//
//                    //1. Inspection point: X-pos: half distance between P1 and p4
//                    //                    Y-pos: the P1
//                    Point2D.Double p3 = new Point2D.Double((double) (p1.x + (p4.x - p1.x) * 0.5), (double) p4.y);
//
//                    CubicCurve2D.Double currcurve = new CubicCurve2D.Double();
//                    currcurve.setCurve(p1, p2, p3, p4);
//
//                    //New Value for the spline
//                    oldcurves[j] = currcurve;
//                    regions[j].append(currcurve, true);
//                }
//            }
//
//
//            for (int i = 0; i < testPoint[0].length - 1; i++) {
//                GeneralPath currpath = new GeneralPath();
//                currpath.append(regions[i], false);
//
//                currpath.lineTo((float) width, (float) height);
//                currpath.lineTo((float) 0, (float) height);
//                currpath.closePath();
//
//                GeneralPath currpath2 = new GeneralPath();
//                currpath2.append(regions[i + 1], false);
//
//                currpath2.lineTo((float) width, (float) 0);
//                currpath2.lineTo((float) 0, (float) 0);
//                currpath2.closePath();
//
//                Area upper = new Area(currpath);
//                upper.intersect(new Area(currpath2));
//
//                // Locate individual category ribbon location for interaction.
//                categorystreams.add(new CategoryStream(upper));
//            }
//            //assignAreaColors();
//            assignAreaContinuousColors(topicSims);
//        }
        if (controlpoints != null && controlpoints.length != 1) {

            //GeneralPaths which will convert to splines
            GeneralPath[] regions = new GeneralPath[controlpoints[0].length];

            double ypos = (height - margin) / 2;
            double maxposx = controlpoints[0][0].x;
            CubicCurve2D[] oldcurves = new CubicCurve2D[controlpoints[0].length];

            for (int i = 0; i < controlpoints[0].length; i++) {
                //Set the start of the spline
                oldcurves[i] = new CubicCurve2D.Double();
                oldcurves[i].setCurve(0, ypos, maxposx / 4, ypos, maxposx / 2, ypos, maxposx * 0.75,
                        controlpoints[0][i].y);
                //Initializing the general path
                regions[i] = new GeneralPath();
            }

            for (int i = 0; i < controlpoints.length - 1; i++) {
                for (int j = 0; j < controlpoints[0].length; j++) {

                    Point p1 = controlpoints[i][j];
                    Point p4 = controlpoints[i + 1][j];

                    //1. Inspection point: X-pos: half distance between P1 and p4
                    //                  Y-pos: the P1
                    Point2D.Double p2 = new Point2D.Double((double) (p1.x + (p4.x - p1.x) * 0.5), (double) p1.y);

                    //1. Inspection point: X-pos: half distance between P1 and p4
                    //                    Y-pos: the P1
                    Point2D.Double p3 = new Point2D.Double((double) (p1.x + (p4.x - p1.x) * 0.5), (double) p4.y);

                    CubicCurve2D.Double currcurve = new CubicCurve2D.Double();
                    currcurve.setCurve(p1, p2, p3, p4);

                    //New Value for the spline
                    oldcurves[j] = currcurve;
                    regions[j].append(currcurve, true);
                }
            }

            for (int i = 0; i < controlpoints[0].length - 1; i++) {
                GeneralPath currpath = new GeneralPath();
                currpath.append(regions[i], false);

                currpath.lineTo((float) width, (float) height);
                currpath.lineTo((float) 0, (float) height);
                currpath.closePath();

                GeneralPath currpath2 = new GeneralPath();
                currpath2.append(regions[i + 1], false);

                currpath2.lineTo((float) width, (float) 0);
                currpath2.lineTo((float) 0, (float) 0);
                currpath2.closePath();

                Area upper = new Area(currpath);
                upper.intersect(new Area(currpath2));

                // Locate individual category ribbon location for interaction.
                categorystreams.add(new CategoryStream(upper));
            }
            //assignAreaColors();
            assignAreaContinuousColors(topicSims);
        }
    }

    public void computerZeroslopeAreasHierarchy(int categoryIndex) {

        Point[][] tempPoint1 = currentPoint;// hierarchicalPoint.get(categoryIndex);

        if (tempPoint1 != null && tempPoint1.length != 1) {

            //  ArrayList<CategoryStream> tempCategoryStream = new ArrayList<CategoryStream>();
            //GeneralPaths which will convert to splines
            GeneralPath[] regions = new GeneralPath[tempPoint1[0].length];

            double ypos = (height - margin) / 2;
            double maxposx = tempPoint1[0][0].x;
            CubicCurve2D[] oldcurves = new CubicCurve2D[tempPoint1[0].length];

            for (int i = 0; i < tempPoint1[0].length; i++) {
                //Set the start of the spline
                oldcurves[i] = new CubicCurve2D.Double();
                oldcurves[i].setCurve(0, ypos, maxposx / 4, ypos, maxposx / 2, ypos, maxposx * 0.75,
                        tempPoint1[0][i].y);
                //Initializing the general path
                regions[i] = new GeneralPath();
            }

            for (int i = 0; i < tempPoint1.length - 1; i++) {
                for (int j = 0; j < tempPoint1[0].length; j++) {

                    Point p1 = tempPoint1[i][j];
                    Point p4 = tempPoint1[i + 1][j];

                    //1. Inspection point: X-pos: half distance between P1 and p4
                    //                  Y-pos: the P1
                    Point2D.Double p2 = new Point2D.Double((double) (p1.x + (p4.x - p1.x) * 0.5), (double) p1.y);

                    //1. Inspection point: X-pos: half distance between P1 and p4
                    //                    Y-pos: the P1
                    Point2D.Double p3 = new Point2D.Double((double) (p1.x + (p4.x - p1.x) * 0.5), (double) p4.y);

                    CubicCurve2D.Double currcurve = new CubicCurve2D.Double();
                    currcurve.setCurve(p1, p2, p3, p4);

                    //New Value for the spline
                    oldcurves[j] = currcurve;
                    regions[j].append(currcurve, true);
                }
            }

            for (int i = 0; i < tempPoint1[0].length - 1; i++) {
                GeneralPath currpath = new GeneralPath();
                currpath.append(regions[i], false);

                currpath.lineTo((float) width, (float) height);
                currpath.lineTo((float) 0, (float) height);
                currpath.closePath();

                GeneralPath currpath2 = new GeneralPath();
                currpath2.append(regions[i + 1], false);

                currpath2.lineTo((float) width, (float) 0);
                currpath2.lineTo((float) 0, (float) 0);
                currpath2.closePath();

                Area upper = new Area(currpath);
                upper.intersect(new Area(currpath2));

                // Locate individual category ribbon location for interaction.
                // tempCategoryStream.add(new CategoryStream(upper));
                currentStreams.add(new CategoryStream(upper));
            }
            //assignAreaColors();
            // hierarchicalstreams.add(tempCategoryStream);
            //currentStreams.add(tempCategoryStream);

            assignAreaCurrentContinuousColors();

        }

//        if (controlpoints != null && controlpoints.length != 1) {
//
//            //GeneralPaths which will convert to splines
//            GeneralPath[] regions = new GeneralPath[controlpoints[0].length];
//
//            double ypos = (height - margin) / 2;
//            double maxposx = controlpoints[0][0].x;
//            CubicCurve2D[] oldcurves = new CubicCurve2D[controlpoints[0].length];
//
//            for (int i = 0; i < controlpoints[0].length; i++) {
//                //Set the start of the spline
//                oldcurves[i] = new CubicCurve2D.Double();
//                oldcurves[i].setCurve(0, ypos, maxposx / 4, ypos, maxposx / 2, ypos, maxposx * 0.75,
//                        controlpoints[0][i].y);
//                //Initializing the general path
//                regions[i] = new GeneralPath();
//            }
//
//
//            for (int i = 0; i < controlpoints.length - 1; i++) {
//                for (int j = 0; j < controlpoints[0].length; j++) {
//
//                    Point p1 = controlpoints[i][j];
//                    Point p4 = controlpoints[i + 1][j];
//
//                    //1. Inspection point: X-pos: half distance between P1 and p4
//                    //                  Y-pos: the P1
//                    Point2D.Double p2 = new Point2D.Double((double) (p1.x + (p4.x - p1.x) * 0.5), (double) p1.y);
//
//                    //1. Inspection point: X-pos: half distance between P1 and p4
//                    //                    Y-pos: the P1
//                    Point2D.Double p3 = new Point2D.Double((double) (p1.x + (p4.x - p1.x) * 0.5), (double) p4.y);
//
//                    CubicCurve2D.Double currcurve = new CubicCurve2D.Double();
//                    currcurve.setCurve(p1, p2, p3, p4);
//
//                    //New Value for the spline
//                    oldcurves[j] = currcurve;
//                    regions[j].append(currcurve, true);
//                }
//            }
//
//
//            for (int i = 0; i < controlpoints[0].length - 1; i++) {
//                GeneralPath currpath = new GeneralPath();
//                currpath.append(regions[i], false);
//
//                currpath.lineTo((float) width, (float) height);
//                currpath.lineTo((float) 0, (float) height);
//                currpath.closePath();
//
//                GeneralPath currpath2 = new GeneralPath();
//                currpath2.append(regions[i + 1], false);
//
//                currpath2.lineTo((float) width, (float) 0);
//                currpath2.lineTo((float) 0, (float) 0);
//                currpath2.closePath();
//
//                Area upper = new Area(currpath);
//                upper.intersect(new Area(currpath2));
//
//                // Locate individual category ribbon location for interaction.
//                categorystreams.add(new CategoryStream(upper));
//            }
//            //assignAreaColors();
//            assignAreaContinuousColors(topicSims);
//        }
    }
    private AlphaComposite highlightcomposite = AlphaComposite.getInstance(
            AlphaComposite.SRC_OVER, 0.4f);
    private AlphaComposite topicDehighlightcomposite = AlphaComposite.getInstance(
            AlphaComposite.SRC_OVER, 0.4f); //0.4
    private AlphaComposite labelcomposite = AlphaComposite.getInstance(
            AlphaComposite.SRC, 1.0f);
    private AlphaComposite backgroundcomposite = AlphaComposite.getInstance(
            AlphaComposite.SRC, 0.1f);
    private Color backgroundColor;

    private void renderAreas(Graphics2D g2d) {

        //if (getCategoryAreas() != null && !getCategoryAreas().isEmpty()) {
        if (!currentStreams.isEmpty()) {

            //int current_j = -1;
            if (!this.name.equals("Main")) {
                Color tempColor = new Color(0, 0, 0);
                for (int i = 0; i < currentStreams.size(); i++) {

                    if (currentNode.getChildren().isEmpty()) {
                        tempColor = currentNode.getColor();
                    } else {
                        if (i >= currentNode.getChildren().size()) {
                            continue;
//                            System.out.println("Something not right here" + this.name + "streamsize"+currentStreams.size() + "chldsize"+ "node" + currentNode.getValue()+ currentNode.getChildren().size());
//                            for (int j=0; j<currentNode.getChildren().size(); j++)
//                            {
//                                 System.out.println(((TreeNode)currentNode.getChildren().get(j)).getValue());
//                            }

//                            System.out.println( currentNode.getValue());
//                             tempColor = ((TreeNode) currentNode.getChildren().get(i-1)).getColor();
//                            for ()
////                            System.out.println("Something not right here" + this.name + "streamsize"+currentStreams.size() + "chldsize"+ "node" + currentNode.getValue()+ currentNode.getChildren().size());
                        } else {
                            tempColor = ((TreeNode) currentNode.getChildren().get(i)).getColor();
                        }
                    }
                    g2d.setColor(tempColor);
                    g2d.fill(currentStreams.get(i).getRenderRegion());
                }

                if (currentNode.getParent() == null) {
                    for (int j = 0; j < currentStreams.size(); j++) {

                        if (focusedCatgoryId != -99) {
                            if (focusedCatgoryId == j) {
                                g2d.setComposite(labelcomposite);
                            } else {
                                g2d.setComposite(topicDehighlightcomposite);
                            }
                        } else {
                            g2d.setComposite(labelcomposite);
                        }

                        g2d.setColor(currentColors[j]);
                        g2d.fill(currentStreams.get(j).getRenderRegion());
                    }
                }

                // 
            } else {

                if (!parent.b_searchHighlight) {
                    for (int i = 0; i < currentStreams.size(); i++) {

                        if (focusedCatgoryId != -99) {
                            if (focusedCatgoryId == i) {
                                g2d.setComposite(labelcomposite);
                            } else {
                                g2d.setComposite(topicDehighlightcomposite);
                            }
                        } else {
                            g2d.setComposite(labelcomposite);
                        }

                        g2d.setColor(currentColors[i]);
                        g2d.fill(currentStreams.get(i).getRenderRegion());

                    }
                } else {

                    for (int i = 0; i < currentStreams.size(); i++) {

                        if (parent.searchHighlightStreams.contains(i)) {

                            g2d.setComposite(labelcomposite);

                        } else {
                            g2d.setComposite(topicDehighlightcomposite);
                        }

                        g2d.setColor(currentColors[i]);
                        g2d.fill(currentStreams.get(i).getRenderRegion());

                    }

                }

            }

            g2d.setComposite(labelcomposite);

            for (int i = 0; i < drawLabels.size(); i++) {
               // int index = drawLabels.get(i);
                //Rectangle2D rect = currentStreams.get(index).getRenderRegion().getBounds2D();

                //PathIterator path = currentStreams.get(index).getRenderRegion().getPathIterator(null);
                //currentStreams.get(index).getRenderRegion().
//                float temp_sum = 0;
//                int count = 0;
//                float[] coords = new float[10];
//                while (!path.isDone()) {
//                    path.currentSegment(coords);
//                    path.next();
//                    for (int j = 0; j < 5; j++) {
//                        if (coords[2 * j] != 0 || coords[2 * j + 1] != 0) {
//                            temp_sum += coords[2 * j + 1];
//                            count++;
//                        }
//
//                    }
//                }
                // temp_sum = temp_sum / count;
                Font font = new Font("Arial", Font.PLAIN, 20);
                g2d.setColor(Color.black);
                g2d.setFont(font);
                //g2d.drawString(Integer.toString(i), (int)rect.getCenterX(), (int)temp_sum);

                g2d.drawString(Integer.toString(i), (int) (drawLabelsLocations.get(i).getX() * width) - 10, (int) (drawLabelsLocations.get(i).getY() * height) + 10);

            }

            
            
            //draw topic number
//            if (currentNode.getNodeTopics() != null) {
//                for (int i = 0; i < currentNode.getNodeTopics().length; i++) {
//                    if (currentNode.getValue().contains("Leaf")) {
//                        Font font = new Font("Arial", Font.PLAIN, 25);
//                        g2d.setFont(font);
//                        g2d.setColor(currentNode.getColor());
//                      g2d.drawString(currentNode.toString(), 10, 40);
//
//                    }
//                                        
//                }
//            }
            
            

//            
//            for (int j=0; j<hierarchicalstreams.size(); j++) {
//                for (int i = 0; i < hierarchicalstreams.get(j).size(); i++) {
//
//                   
//                  g2d.setColor(colors[i]);
//                  g2d.fill( hierarchicalstreams.get(j).get(i).getRenderRegion());
//              }
//            }
            for (int i = 0; i < getTimecolumns().size(); i++) {

                if (focusedColumnId == i) {
                    g2d.setColor(Color.YELLOW);
                    g2d.setComposite(highlightcomposite);
                    {
                        g2d.fill(getTimecolumns().get(i).getRenderRegion());

                    }

                }
            }

            //for (int i = 0; i < getTimecolumns().size(); i++) {
            for (int j = 0; j < focusedSelectionList.size(); j++) {

                g2d.setColor(Color.YELLOW);
                g2d.setComposite(highlightcomposite);
                // g2d.fill(getTimecolumns().get(i).getRenderRegion());

                int lx = (int) focusedSelectionList.get(j).getX();
                int ly = (int) focusedSelectionList.get(j).getY();

                //if (focusedStreamList.get(j)<currentStreams.size() && focusedStreamList.get(j)>-1)
                {
                    Area a1 = new Area(getTimecolumns().get(lx).getRenderRegion()); // The value is set elsewhere in the code    

                    Area a2 = currentStreams.get(ly).getRenderRegion();

                    a1.intersect(a2);

                    g2d.fill(a1);

                    //System.out.println(timeColumnStream + " is focused");
                }

            }
            // }

//             g2d.setComposite(highlightcomposite);
//             g2d.setColor(Color.black);
//              g2d.fillRect(0,0,width,height);
            // g2d.setBackground(Color.black);
            // g2d.setComposite(labelcomposite);
            //Set Background Color
//   g2d.setColor(Color.WHITE);
//            if (!"main".equals(this.getName()))
//            {
//                //g2d.setComposite(backgroundcomposite);
//                g2d.setBackground(Color.black);
//                g2d.fillRect(0,0,width,height);
//                 g2d.setComposite(labelcomposite);
//            }
            //    g2d.fillRect(0, testPoint[0][0].y, testPoint[0][0].x - 1,
            //    testPoint[0][testPoint[0].length - 1].y - testPoint[0][0].y);
            //Finish to the right side of the panel
            // g2d.fillRect(testPoint[testPoint.length - 1][0].x + 1, testPoint[testPoint.length - 1][0].y,
            //        width - (testPoint[testPoint.length - 1][0].x + 1),
            //        testPoint[testPoint.length - 1][testPoint[0].length - 1].y - testPoint[testPoint.length - 1][0].y);
            //Start from the left side of the panel
//            g2d.fillRect(0, controlpoints[0][0].y, controlpoints[0][0].x - 1,
//                    controlpoints[0][controlpoints[0].length - 1].y - controlpoints[0][0].y);
//
//            //Finish to the right side of the panel
//            g2d.fillRect(controlpoints[controlpoints.length - 1][0].x + 1, controlpoints[controlpoints.length - 1][0].y,
//                    width - (controlpoints[controlpoints.length - 1][0].x + 1),
//                    controlpoints[controlpoints.length - 1][controlpoints[0].length - 1].y - controlpoints[controlpoints.length - 1][0].y);
        }
    }

    
    public TreeNode showingNode = null;
    
    public void drawTopicWords(TreeNode n)
    {
         // System.out.print("here" + n.getNodeTopics());
//        bi = (BufferedImage) createImage(width, height);
//            curg2d = bi.createGraphics();
   

                   
        
 
        
        
    }
    
    
    private void assignAreaColors() {
        colors = new Color[getCategoryAreas().size()];
        for (int i = 0; i < getCategoryAreas().size(); i++) {
            int index = i % 12;
            colors[i] = new Color(ColorBrewer.colors[index][0], ColorBrewer.colors[index][1], ColorBrewer.colors[index][2]);
            //colors[i] = new Color(255,0,0);
        }
    }

    private void assignAreaCurrentContinuousColors() {
        int n;
        if (currentStreams.isEmpty()) {
            n = 10;
        } else {
            n = currentStreams.size();
        }//hierarchicalstreams.get(0).size();

        currentColors = new Color[n];
        List<Float[]> colorSpecturm = getCurrentColorMap();
        int size = colorSpecturm.size();
        for (int i = 0; i < currentColors.length; i++) {
            // int c = i%size;
            currentColors[i] = new Color(colorSpecturm.get((int) i % size)[1], colorSpecturm.get((int) i % size)[2], colorSpecturm.get((int) i % size)[3]);//Color.getHSBColor(colorSpecturm.get(i%size)[1], colorSpecturm.get(i%size)[2], colorSpecturm.get(i%size)[3]);
//            try{
//                colors[i] = new Color(colorSpecturm.get(i)[1], colorSpecturm.get(i)[2], colorSpecturm.get(i)[3]);
//            }catch(Exception ex){
//                System.out.println(ex);
//            }
        }
    }

    private void assignAreaContinuousColors(List<Float> tSim) {
        colors = new Color[getCategoryAreas().size()];

        List<Float[]> colorSpecturm = getColorMap();//getNumericalColors(topicSims);
        for (int i = 0; i < colors.length; i++) {
            colors[i] = Color.getHSBColor(colorSpecturm.get(i)[1], colorSpecturm.get(i)[2], colorSpecturm.get(i)[3]);
//            try{
//                colors[i] = new Color(colorSpecturm.get(i)[1], colorSpecturm.get(i)[2], colorSpecturm.get(i)[3]);
//            }catch(Exception ex){
//                System.out.println(ex);
//            }
        }
    }

    public Color[] getStreamColors() {
        return colors;
    }

    private List<Float[]> getColorMap() {
        colorMap = new ArrayList<Float[]>();
        try {
            colorMap = this.parent.getHSVColors(getCategoryAreas().size());//;//getNumericalColors();
        } catch (Exception e) {
            System.out.println("colorMap generation failed!");
        }
        return colorMap;
    }

    public List<Float[]> getCurrentColorMap() {
        colorMap = new ArrayList<Float[]>();
        try {
            colorMap = this.parent.getNewHueColors();//this.parent.getHSVColors(currentStreams.size());//;//getNumericalColors();
        } catch (Exception e) {
            System.out.println("colorMap generation failed!");
        }
        return colorMap;
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

    protected void drawTimeLine(Graphics2D g2d) {

         g2d.setComposite(labelcomposite);
        g2d.setColor(Color.LIGHT_GRAY);
        // g2d.fillRect(0, 0, width, margin / 2);
        g2d.fillRect(0, height - (margin / 2), width, margin / 2);

        SimpleDateFormat f = (SimpleDateFormat) parent.getFormat();
        String intervalString = " ";

        //if (f!=null)
        if (f.toPattern() == "yyyy") {
            intervalString = parent.data.getHr2ms().toString() + "Year";

        } else {
            long millis = 0;

            if (parent.data.getHr2ms() == null) {
                millis = 0;
            } else {
                millis = parent.data.getHr2ms();
            }

            long days = TimeUnit.MILLISECONDS.toDays(millis);
           
            if (days<1)
            {
                long hours = TimeUnit.MILLISECONDS.toHours(millis);
                if (hours<1)
                {
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
                    intervalString = minutes + "minutes(s)";
                    
                }    
                else
                 intervalString = hours + "hour(s)";
                
                
            }    
                
            else
                intervalString = days + "day(s)";
        }
        

        {
            int number = parent.data.getNumOfYears();
            // System.out.println("How many times" + number);
            double ratio = (double) width / (double) number;
            g2d.setFont(NORMALFONT);
            FontMetrics metrics = this.curg2d.getFontMetrics();

            g2d.setColor(Color.BLACK);
            for (int i = 0; i < 0/*number*/; i++) {

                // Upper Boundary
                g2d.drawLine((int) ((i + 1) * ratio), 0, (int) ((i + 1) * ratio), height / 20);

                // Lower Bondary
                g2d.drawLine((int) ((i + 1) * ratio), height, (int) ((i + 1) * ratio), height - height / 15);

                // String upperstr = "Something";//TimeUtils.returnDate(timecolumns.get(i).getFarTime(), data.getData().get(i).getDateFormatter());
                Long tmpMili = data.getBeginningTime() + data.getTimeInterval() * i;

                String lowerstr = "";

                if (f != null) {
                    if (f.toPattern() == "yyyy") {
                        lowerstr = tmpMili.toString();

                    } else {
                        SimpleDateFormat sdf = new SimpleDateFormat(f.toPattern());
                        //SimpleDateFormat sdf = new SimpleDateFormat("MM dd yyyy");
                        lowerstr = sdf.format(new Date(tmpMili));//Integer.toString(data.getBeginningYear()+i);//TimeUtils.returnDate(timecolumns.get(i).getNearTime(), data.getData().get(i).getDateFormatter());
                    }
                }


//               
                if (i == displayLengendTimeColumn) 
                {

                    //g2d.drawString(lowerstr, (int) ((i) * ratio), height - height / 20);


                }

                if (i == displayLengendTimeColumn && this.name.contains("Main")) // g2d.drawString(upperstr, (int) ((i) * ratio + (ratio - metrics.stringWidth(upperstr)) / 2), 10);
                {

                    //g2d.drawString(lowerstr, (int) ((i) * ratio), height - height / 20);
                    int docNumberInSlot = data.idxOfDocumentPerSlot.get(i).size();
                    String sDocNumberInSlot = "Total Number of Tweets in this time slot: " + docNumberInSlot;
                   // g2d.drawString(sDocNumberInSlot, 10, height / 20 + 10);

                }
            }

//            g2d.setFont(TIMEDOMAINFONT);
//            metrics = big.getFontMetrics();
            // Draw Chart Legend
//            String timedomain = "Temporal Activities";//datrep.getTimeDomain();
//            int stringwidth = metrics.stringWidth(timedomain);
//            int xpos = (getWidth() - stringwidth) / 2;
//
//            g2d.setColor(Color.WHITE);
//            g2d.fillRect(xpos - 5, height - 25, stringwidth + 10, 20);
//
//            g2d.setColor(Color.BLACK);
//            g2d.drawString(timedomain, xpos, height - 10);
        }
        g2d.setColor(Color.BLACK);
      //  g2d.drawString(intervalString, 10, 0 + height / 20);

        int size = 0;

        if (multiTopicKeywordList != null) {

            size = multiTopicKeywordList.size();

            for (int i = 0; i < size; i++) {
                String s = multiTopicKeywordList.get(i).getString();
                Point2D p = multiTopicKeywordList.get(i).getLocation();

                g2d.setColor(Color.CYAN);
                //multiTopicKeywordList.get(i).drawRect(g2d);

                g2d.setFont(multiTopicKeywordList.get(i).getFont());
                g2d.setColor(Color.DARK_GRAY);
                g2d.drawString(s, (float) p.getX(), (float) p.getY());

            }

        }

    }

    void calculateReTweetRatio() {
        int sum = 0;
        for (int i = 0; i < parent.data.idxOfDocumentPerSlot.size(); i++) {

            for (int j = 0; j < parent.data.idxOfDocumentPerSlot.get(i).size(); j++) {
                int tempDocIdx = parent.data.idxOfDocumentPerSlot.get(i).get(j);
                String[] p = parent.getInternalDocs().get(tempDocIdx + 1);//both files have headers
            }

        }

        //get Document and get topic and calculate
    }
    private ArrayList<float[][]> detectionResults = new ArrayList<float[][]>();

    public ArrayList<float[][]> getDetectionResults() {
        return detectionResults;
    }
    private float eventThreshold = (float) 3.0;

    public float getEventThreshold() {
        return eventThreshold;
    }

    public void setEventThreshold(float eventThreshold) {
        this.eventThreshold = eventThreshold;
    }

    public float detectEvents(float eThreshold) {

        List<float[]> unormStreams = new ArrayList<float[]>();
        detectionResults.clear();

        if (!currentNode.getChildren().isEmpty()) {
            for (int i = 0; i < currentNode.getChildren().size(); i++) {
                List<Float> temp = ((TreeNode) currentNode.getChildren().get(i)).getUnNormArrayValue();

                float[] tempf = new float[temp.size()];
                for (int j = 0; j < temp.size(); j++) {
                    tempf[j] = temp.get(j);
                }

                unormStreams.add(tempf);

            }
        } else {
            List<Float> temp = ((TreeNode) currentNode).getUnNormArrayValue();

            float[] tempf = new float[temp.size()];
            for (int j = 0; j < temp.size(); j++) {
                tempf[j] = temp.get(j);
            }

            unormStreams.add(tempf);
        }

        //List<float[]>
        for (float[] fs : unormStreams) {
            detectionResults.add(Cusum.cusumProcess(fs, (float) eThreshold));
        }


        int count = 0;
        for (float[][] detectionResult : detectionResults) {
            for (float[] detectionResult1 : detectionResult) {
                if ((detectionResult1[1] > 0)) {
                            
                    if (detectionResult1[1] == 1) {
                        count++;
                       
                    }
                }
            }
        }
        
        
        return (float) (count==0?0.5:count);
    }
    
    
    CubicCurve2D[][][] contours;
    List<Point2D> testEventPoints = new ArrayList<Point2D>();

    public void computeEventOutlineArea() {
        if (currentPoint != null && currentPoint.length != 1) {

            contours = new CubicCurve2D[currentPoint[0].length - 1][currentPoint.length - 1][2];//0-top curve; 1 - bottom curve

            float[][] flags = new float[currentPoint.length + 1][currentPoint[0].length + 1];

            //the first and last point in each ribbon are artificial
            for (int i = 0; i < detectionResults.size(); i++) {
                for (int j = 0; j < detectionResults.get(i).length; j++) {
                    if (!(detectionResults.get(i)[j][1] > 0)) {
                        flags[j][i] = -1;
                    }
                    else
                    {
//                        int xx = 0;
//                        System.out.println(detectionResults.get(i)[j][0] + " "  + detectionResults.get(i)[j][1]);
                        
                        
                    }
                }
            }

            testEventPoints.clear();
int count = 0;
            for (int i = 0; i < currentPoint.length - 1; i++) {//number of time slots
                for (int j = 0; j < currentPoint[i].length - 1; j++) {
                        
                    if (flags[i][j] != -1) {
                        count++;
                        testEventPoints.add(currentPoint[i][j]);

                        Point p1 = currentPoint[i][j];

                        Point p4 = currentPoint[i + 1][j];

                        CubicCurve2D.Double currcurve = new CubicCurve2D.Double();
                        CubicCurve2D.Double bottomcurve = new CubicCurve2D.Double();

                        Point2D.Double p2 = new Point2D.Double((double) (p1.x + (p4.x - p1.x) * 0.5), (double) p1.y);
                        Point2D.Double p3 = new Point2D.Double((double) (p1.x + (p4.x - p1.x) * 0.5), (double) p4.y);

                        currcurve.setCurve(p1, p2, p3, p4);

                        contours[j][i][0] = currcurve;

                        p1 = currentPoint[i][j + 1];

                        p4 = currentPoint[i + 1][j + 1];

                        bottomcurve = new CubicCurve2D.Double();

                        p2 = new Point2D.Double((double) (p1.x + (p4.x - p1.x) * 0.5), (double) p1.y);
                        p3 = new Point2D.Double((double) (p1.x + (p4.x - p1.x) * 0.5), (double) p4.y);

                        bottomcurve.setCurve(p1, p2, p3, p4);

                        contours[j][i][1] = bottomcurve;

                    }

                }

            }

            
//            for (int i = 0; i < currentPoint[0].length; i++) {
//                //Set the start of the spline
//                oldcurves[i] = new CubicCurve2D.Double();
//                oldcurves[i].setCurve(0, ypos, maxposx / 4, ypos, maxposx / 2, ypos, maxposx * 0.75,
//                        currentPoint[0][i].y);
////                oldcurves[i].setCurve(0, ypos, maxposx / 4, ypos, maxposx / 2, ypos, maxposx * 0.75,
////                        levelpoints[0][i].y);
//                //Initializing the general path
//                regions[i] = new GeneralPath();
//                regionBottoms[i] = new GeneralPath();
//            }
//
//
//            //System.out.println(controlpoints.length + "Compare to " + detectionResults.size());
//
//            for (int i = 0; i < currentPoint.length - 1; i++) {//number of time slots
//
//                // Loop through all the topic number here: XW
//                for (int j = 0; j < currentPoint[0].length - 1; j++) {
//                    try{
//                        if(flags[i][j] == -1)
//                    {
//                         Point p1 = currentPoint[i][j];                        
//                        Point p4 = currentPoint[i + 1][j];
//
//
//                        CubicCurve2D.Double currcurve = new CubicCurve2D.Double();
//                        CubicCurve2D.Double bottomcurve = new CubicCurve2D.Double();
//
//                        Point p5 = levelpoints[i][j];
//                        Point p8 = levelpoints[i + 1][j];
//                        
//                        currcurve.setCurve(p1, p4, p5, p8);
//                        bottomcurve.setCurve(p1, p4, p5, p8);
//                        
//                        oldcurves[j] = currcurve;
//                        regions[j].append(currcurve, true);
//                        regionBottoms[j].append(bottomcurve, true);
//                        
//                        contours[j][i][0] = null;
//                        contours[j][i][1] = null;
//                        
//                        
//                    } else {
//                        Point p1 = currentPoint[i][j];
//                        //Point p4 = levelpoints[i][j];
//                        Point p4 = currentPoint[i + 1][j];
//
//
//                        CubicCurve2D.Double currcurve = new CubicCurve2D.Double();
//                        CubicCurve2D.Double bottomcurve = new CubicCurve2D.Double();
//
//                        Point p5 = levelpoints[i][j];
//                        Point p8 = levelpoints[i + 1][j];
////                 
//                        Point2D.Double pS = new Point2D.Double((double) p1.x, (double) (p1.y - (p1.y - levelpoints[i][j].y) * 0.5));
//                        Point2D.Double pE = new Point2D.Double((double) p4.x, (double) (p4.y - (p4.y - levelpoints[i + 1][j].y) * 0.5));
////              
//                        //1. Inspection point: X-pos: half distance between P1 and p4
//                        //                  Y-pos: the P1
//                        Point2D.Double p2 = new Point2D.Double((double) (pS.x + (pE.x - pS.x) * 0.5), (double) pS.y);
//
//                        //1. Inspection point: X-pos: half distance between P1 and p4
//                        //                    Y-pos: the P1
//                        Point2D.Double p3 = new Point2D.Double((double) (pS.x + (pE.x - pS.x) * 0.5), (double) pE.y);
//
//
//
//                        Point2D.Double pStart = new Point2D.Double((double) p5.x, (double) (2 * p5.y - pS.y));
//                        Point2D.Double pEnd = new Point2D.Double((double) p8.x, (double) (2 * p8.y - pE.y));
//
//
//                        Point2D.Double p6 = new Point2D.Double((double) (pStart.x + (pEnd.x - pStart.x) * 0.5), (double) pStart.y);
//                        Point2D.Double p7 = new Point2D.Double((double) (pStart.x + (pEnd.x - pStart.x) * 0.5), (double) pEnd.y);
//
//
//
//                        currcurve.setCurve(pS, p2, p3, pE);
//                        bottomcurve.setCurve(pStart, p6, p7, pEnd);
//
//                        //New Value for the spline
//                        oldcurves[j] = currcurve;
//                        regions[j].append(currcurve, true);
//                        //regionBottoms[j].append(line, true);
//                        regionBottoms[j].append(bottomcurve, true);
//                        
//                        contours[j][i][0] = currcurve;
//                        contours[j][i][1] = bottomcurve;
//                    }
//                    }catch(Exception ex){
//                        contours[j][i][0] = null;
//                        contours[j][i][1] = null;
//                    }
//
//                    
//                }
//            }
//
////            categorystreamsOutlines.clear();
////            for (int i = 0; i < controlPointsForOutline[0].length - 1; i++) {//for each ribbon
////                GeneralPath currpath = new GeneralPath();
////                currpath.append(regionBottoms[i], false);
////                
////
////                currpath.lineTo((float) width, (float) height);
////                currpath.lineTo((float) 0, (float) height);
////                currpath.closePath();
////
////                GeneralPath currpath2 = new GeneralPath();
////                //currpath2.append(regions[i + 1], false);
////                currpath2.append(regions[i], false);
////
////                currpath2.lineTo((float) width, (float) 0);
////                currpath2.lineTo((float) 0, (float) 0);
////                currpath2.closePath();
////
////                Area upper = new Area(currpath);
////                upper.intersect(new Area(currpath2));
////
////                // Locate individual category ribbon location for interaction.
////                categorystreamsOutlines.add(new CategoryStream(upper));
////            }
//
//
        }

        repaintView();
    }

    public HashMap<TopicGraphViewPanel.customLabelTimecolumnKey, List<LabelText>> getLabelTimeMap() {
        return labelTimeMap;
    }

    HashMap< TopicGraphViewPanel.customLabelTimecolumnKey, List<LabelText>> labelTimeMap
            = new HashMap< TopicGraphViewPanel.customLabelTimecolumnKey, List<LabelText>>();

   
    public void buildLabelTimeMap() {

        if (parent.data.topicYearKwIdx != null) {
            System.out.println(this.currentNode.getValue());
         //   labelTimeMap = parent.getTopicGraphViewPanel().buildLabelMap((this.currentNode), parent.data.topicYearKwIdx);
            //labelTimeMap = parent.getTopicGraphViewPanel().buildLabelMap(parent.findMatchingNodeInTopicGraph(this.currentNode), parent.data.topicYearKwIdx);
            
                    }
        else
            System.out.println("topicYearKwIdx is null, no labels built..");

    }
    
    


    public Point2D currentMouseLocation = new Point2D.Double();

    

                 
                 
    public List<LabelText> multiTopicKeywordList;

    List<LabelText> singleTopicKeywordList;

    
    
    
    
    
    public void DrawWordleCloud(Point2D p, List<LabelText> ls) {

        List<LabelWordleLite> list = new ArrayList<LabelWordleLite>();

        int size = 0;
//        if (ls.size() >= 20) {
//                size = 20;
//            } 
//        else
        size = ls.size();

        for (int i = 0; i < size; i++) {
            LabelText lt = ls.get(i);

            String text = lt.getString();
            if (text == null) {
                continue;
            }

            Font font = lt.getFont();
            
           //      Font font = new Font("Helvetica-Condensed-Bold", Font.BOLD, 10);
            LabelWordleLite word = new LabelWordleLite(text, font, 0, lt);
            list.add(word);

        }

        WordleAlgorithmLite alg = new WordleAlgorithmLite(new Rectangle2D.Double(0, 0, 1200,600));//this.getWidth(), this.getHeight()));
        //alg.displayParameters();
        alg.place(list);

        Rectangle2D bounds = findBoundary(list);

       // System.out.println(this.getBounds());
       // System.out.println(bounds);

        for (LabelWordleLite word : list) {

            setLabelVisualPos(word, p, bounds);
            //System.out.println(((TopicGraphViewPanel.labelText)word.data).getRect());
        }

        multiTopicKeywordList = ls;

    }

    private Rectangle2D findBoundary(List<LabelWordleLite> symbol) {
        float min_x, max_x, min_y, max_y;
        min_x = 99999;
        min_y = 99999;
        max_x = -99999;
        max_y = -99999;

        for (WordleLite word : symbol) {

            LabelWordleLite label = (LabelWordleLite) word;
            Point2D location = label.getLocation();
            Rectangle2D glyphBound = label.getShape().getBounds2D();
            //Rectangle2D strBound = fm.getStringBounds(label.text, this.curg2d);
            LabelText vi = (LabelText) label.data;

            float size = (float) (vi.getFont().getSize2D() /**
                     * vi.getOccurance()
                     */
                    );
            Font font = vi.getFont().deriveFont(size);
            FontMetrics fm = this.curg2d.getFontMetrics(font);
            Rectangle2D strBound = fm.getStringBounds(label.text, this.curg2d);

            float current_x = (float) (location.getX() - glyphBound.getX() / 2 - strBound.getWidth() / 2);
            if (current_x >= max_x) {
                max_x = current_x;
            }
            if (current_x <= min_x) {
                min_x = current_x;
            }

            current_x = (float) (location.getX() + glyphBound.getX() + strBound.getWidth());
            if (current_x <= min_x) {
                min_x = current_x;
            }
            if (current_x >= max_x) {
                max_x = current_x;
            }

            float current_y = (float) (location.getY() - glyphBound.getY() / 2 - strBound.getHeight() / 2);
            if (current_y >= max_y) {
                max_y = current_y;
            }
            if (current_y <= min_y) {
                min_y = current_y;
            }

            current_y = (float) (location.getY() + glyphBound.getY() + strBound.getHeight());
            if (current_y <= min_y) {
                min_y = current_y;
            }

            if (current_y >= max_y) {
                max_y = current_y;
            }
        }

        Rectangle2D r = new Rectangle2D.Float(min_x, min_y, max_x, max_y);

        return r;
    }

    private void setLabelVisualPos(WordleLite symbol, Point2D p, Rectangle2D bounds) {
        LabelWordleLite label = (LabelWordleLite) symbol;
        Point2D location = label.getLocation();
        LabelText vi = (LabelText) label.data;
        Rectangle2D glyphBound = label.getShape().getBounds2D();

        float size = (float) (vi.getFont().getSize2D() /**
                 * vi.getOccurance()
                 */
                );
        Font font = vi.getFont().deriveFont(size);
        FontMetrics fm = this.curg2d.getFontMetrics(font);
        Rectangle2D strBound = fm.getStringBounds(label.text, this.curg2d);

         //System.out.println(vi.getString() + "size " + size);
        // System.out.println("strBound " + strBound);
        // System.out.println("glyphBound " + glyphBound);
        /*
         * location is the glyph's location, i.e.:
         * x = the minimum x coordinate of the glyph
         * y = the baseline of the text string
         */
        //vi.setLocation(new Point2D.Double(location.getX(), location.getY()));
        //  vi.setLocation(new Point2D.Double(location.getX() + glyphBound.getX() + strBound.getWidth() / 2, location.getY() + fm.getDescent() - strBound.getHeight() / 2));
        double x = p.getX() + location.getX() - glyphBound.getX() / 2 - strBound.getWidth() / 2;
        double y = p.getY() + location.getY() - glyphBound.getY() / 2 - strBound.getHeight() / 2;

//        if (x < 0) {
//            x = x - bounds.getX();
//        }
//        else if (x >= this.getWidth()) {
//            x = x - ( x + bounds.getWidth()-this.getWidth());
//        }
//
//        if (y < 0) {
//            y = y - bounds.getY();
//        }
//        else if (y >= this.getHeight()) {
//            y = y - (y - bounds.getHeight());
//        }
        Point2D vilocation = new Point2D.Double(x, y);

        
        vi.setLocation(new Point((int)vilocation.getX(), (int)vilocation.getY()));// + fm.getDescent() - strBound.getHeight() / 2));
        //System.out.println(p + " " +location);
        
        //System.out.println("xxx" + fm.getDescent());
        //Rectangle2D ss = new Rectangle2D.Double(vilocation.getX(), vilocation.getY(), glyphBound.getWidth(), glyphBound.getHeight());
        //vi.setRect(ss);
        // vi.setX(location.getX() + glyphBound.getX() + strBound.getWidth() / 2);
        // vi.setY(location.getY() + fm.getDescent() - strBound.getHeight() / 2);
    }

    public void mouseClicked(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mousePressed(MouseEvent e) {

        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseReleased(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseEntered(MouseEvent e) {

        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseExited(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void keyPressed(KeyEvent e) {

        int d = focusedCatgoryId;
        char c = e.getKeyChar();

        if (c == '\b') {
        }

    }

    public void keyReleased(KeyEvent e) {
        int d = focusedCatgoryId;
        char c = e.getKeyChar();
        if (c == '\b') {
        }

        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void keyTyped(KeyEvent e) {
        int d = focusedCatgoryId;
        char c = e.getKeyChar();
        if (c == '\b') {
        }
        //throw new UnsupportedOperationException("Not supported yet.");
    }
}
