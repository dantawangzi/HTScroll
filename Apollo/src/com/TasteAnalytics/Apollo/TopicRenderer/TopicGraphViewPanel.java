/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TasteAnalytics.Apollo.TopicRenderer;

/**
 *
 * @author sasa
 */
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.BoxLayout;
import java.util.HashSet;
import java.util.Set;
import java.io.FileInputStream;
import java.util.Scanner;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import org.apache.commons.io.IOUtils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import javax.swing.JLabel;
import java.awt.Stroke;
import java.awt.BasicStroke;
import edu.uci.ics.jung.visualization.RenderContext;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import java.awt.geom.AffineTransform;
import java.awt.Paint;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.JCheckBox;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import com.TasteAnalytics.Apollo.GUI.ViewController;
import com.TasteAnalytics.Apollo.datahandler.CategoryBarElement;
import com.TasteAnalytics.Apollo.datahandler.LDAHTTPClient;
import com.TasteAnalytics.Apollo.GUI.MinimalismMainFrame;
import com.TasteAnalytics.Apollo.TemporalView.TreeNode;
import java.util.Collection;
import java.awt.Container;
import java.awt.GridLayout;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.algorithms.layout.RadialTreeLayout;
import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.TreeUtils;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import java.io.FileNotFoundException;
import org.apache.commons.collections15.Transformer;
import edu.uci.ics.jung.visualization.subLayout.TreeCollapser;
import edu.uci.ics.jung.visualization.VisualizationServer;
import java.util.List;
import java.util.Map;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.algorithms.layout.PolarPoint;
import edu.uci.ics.jung.visualization.decorators.EllipseVertexShapeTransformer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import java.awt.geom.Rectangle2D;
import edu.uci.ics.jung.visualization.decorators.GradientEdgePaintTransformer;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import org.apache.commons.collections15.Predicate;
import edu.uci.ics.jung.graph.util.Context;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.FontMetrics;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import com.TasteAnalytics.Apollo.TemporalView.TemporalViewPanel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.visualization.VisualizationViewer.GraphMouse;
import edu.uci.ics.jung.visualization.layout.ObservableCachingLayout;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.TasteAnalytics.Apollo.TemporalView.TemporalViewFrame;
import javax.swing.plaf.basic.BasicArrowButton;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;
import edu.uci.ics.jung.visualization.control.AnimatedPickingGraphMousePlugin;
import javax.swing.SwingUtilities;
import edu.uci.ics.jung.visualization.transform.HyperbolicTransformer;
import edu.uci.ics.jung.visualization.transform.LayoutLensSupport;
import edu.uci.ics.jung.visualization.transform.LensSupport;
import edu.uci.ics.jung.visualization.transform.shape.HyperbolicShapeTransformer;
import edu.uci.ics.jung.visualization.transform.shape.MagnifyShapeTransformer;
import edu.uci.ics.jung.visualization.transform.shape.ViewLensSupport;
import edu.uci.ics.jung.visualization.control.LensMagnificationGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ModalLensGraphMouse;
import edu.uci.ics.jung.visualization.transform.LensTransformer;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.JTextField;
import java.lang.Math;
//import com.explodingpixels.macwidgets;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.commons.lang.StringUtils;

public class TopicGraphViewPanel extends JPanel {

//private static HudWindow mainFloatingHUDWindow = new HudWindow();


    public static class customLabelTimecolumnKey {

        String field1;
        String field2;

        public customLabelTimecolumnKey(String t, String i) {
            field1 = t;
            field2 = i;

        }

        @Override
        public boolean equals(Object o) {
            if (o == null || !(o instanceof customLabelTimecolumnKey)) {
                return false;
            }

            customLabelTimecolumnKey other = (customLabelTimecolumnKey) o;
            return field1.equals(other.field1) && (other.field2 == null ? field2 == null : other.field2.equals(field2));
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 47 * hash + (this.field1 != null ? this.field1.hashCode() : 0);
            hash = 47 * hash + (this.field2 != null ? this.field2.hashCode() : 0);
            return hash;
        }

        @Override
        public String toString() {

            //String k = .toString();//Integer.parseInt(field2);
            return field1 + " " + field2;
        }

    }

    

    static public class MyLink {

        double capacity;
        double weight;
        int id;

        public MyLink(double weight, double capacity) {
            // this.id = edgeCount++;
            this.weight = weight;
            this.capacity = capacity;
        }

        @Override
        public String toString() {
            return "";
        }
    }
    private JLabel tooltipLabel;
    private JLabel magnifyLabel;
    private JLabel magnifyLabelLeft;
    private JLabel magnifyLabelRight;
    private JLabel magnifyLabelLeftLeft;
    private JLabel magnifyLabelRightRight;
    private int width, height;
    private BufferedImage bi;
    private Graphics2D curg2d;
    private CategoryBarElement data;
    private List<TreeNode> myTree;
    private TreeNode root;
    //private Graph<TreeNode,MyLink> gh;
    private DelegateForest<Object, MyLink> gh;
    private String folderPath;
    protected Map<Number, Number> edge_weight = new HashMap<Number, Number>();
    public Rectangle2D rectMouseSelect;
    public int newNodeIdx = 70;
    private List<JLabel> annotation;
    private List<Point2D> annotation_locations;
    private int annotation_count = 0;
    private myAnnotationMenu popupMenu;
    static int nodeStringStartIndex = 1;  // skip word "Group" or "Topic xx"
    int highlightOne = 0;
    boolean b_showText = true;

    public Graphics2D getCurg2d() {
        return curg2d;
    }

    public void setCurg2d(Graphics2D curg2d) {
        this.curg2d = curg2d;
    }

    public DelegateForest<Object, MyLink> getGh() {
        return gh;
    }

    public void setGh(DelegateForest<Object, MyLink> gh) {
        this.gh = gh;
    }
    private int node_width_interval = 70;
    private int node_height_interval = 40;
    /**
     * The ParallelDisplay component we are assigned to.
     */
    ViewController parent;
    private JLabel[][] labels;
    List<JPanel> vertexlabels;
    private JPanel mainPanel;
    //private Map<String, List<Dimension>> displayedWordMap;
    private boolean isCollapsed;
    private int labelsToDisplay;
    //private labelText labeltexts[][];
    //private boolean labelIsSelected[][];
    static public TemporalViewFrame tvf;
    static private int totalEdges;
    public Container content;
    HashMap<TreeNode, List<LabelText>> allLabels = new HashMap<TreeNode, List<LabelText>>();
    static private int MAXFONTSIZE = 30;
    static private int MINFONTSIZE = 6;//10
    private int labelFontSize = 10; //18
    static private int fontSizePerChar = 1;
    private int highlightFontSize = 26;
    static private int occuranceFontSizePara = 2;

    public HashMap<TreeNode, List<LabelText>> getAllLabels() {
        return allLabels;
    }

    public void setAllLabels(HashMap<TreeNode, List<LabelText>> allLabels) {
        this.allLabels = allLabels;
    }

    public List<TreeNode> getTree() {
        return this.myTree;

    }

    public CategoryBarElement getData() {
        return data;
    }

    private String HelveticaFont = "Helvetica-Condensed-Bold";

    private final static Font NORMALFONT = new Font("Sans-Serif", Font.PLAIN, 12);
    private final static Font TIMEDOMAINFONT = new Font("Sans-Serif", Font.BOLD, 12);
    private int highlightedColumn;
    private TreeNode highlightedColumnKey;
    private int highlightedRow;
    private boolean bMagnified = false;

    public TopicGraphViewPanel() {
        super();
    }

    public VisualizationViewer getVisualizationViewer() {
        if (vv != null) {
            return vv;
        }
        return null;
    }
    private boolean bMergeMode = true;
    Map<String, Integer> wordTermIndex;
    List<String[]> wordTermWeightsF;
    
    List<List<Float>> topkTermWeightMongo = new ArrayList<List<Float>>();

    public TopicGraphViewPanel(ViewController viewController, Map<String, Integer> termIndex, List<String[]> termWeight, List<List<Float>> TermWeightMongo) throws FileNotFoundException, IOException {
        this.parent = viewController;
        wordTermIndex = termIndex;
        wordTermWeightsF = termWeight;
        topkTermWeightMongo = TermWeightMongo;

        selectedNode = new TreeNode(null);

        tooltipLabel = new JLabel();
        magnifyLabel = new JLabel();
        magnifyLabelLeftLeft = new JLabel();
        magnifyLabelLeft = new JLabel();
        magnifyLabelRight = new JLabel();
        magnifyLabelRightRight = new JLabel();

        popupMenu = new myAnnotationMenu();
        annotation_locations = new ArrayList<Point2D>();
        annotation = new ArrayList<JLabel>();
    
       
        width = 1200;
        height = 1000;

        this.isCollapsed = false;

        //setPreferredSize(new Dimension(width, height));
     
        labelsToDisplay = 51;

        tvf = this.parent.getTemporalFrame();


        this.addComponentListener(new ComponentListener() {
            public void componentResized(ComponentEvent e) {

                ((TopicGraphViewPanel) e.getComponent()).UpdateTopicGraphicView(new Dimension(e.getComponent().getSize().width, e.getComponent().getSize().height));
            }

            public void componentMoved(ComponentEvent e) {
                //throw new UnsupportedOperationExceptibon("Not supported yet.");
            }

            public void componentShown(ComponentEvent e) {
                //throw new UnsupportedOperationException("Not supported yet.");
            }

            public void componentHidden(ComponentEvent e) {
                //throw new UnsupportedOperationException("Not supported yet.");
            }
        });
      

    }
    VisualizationViewer<Object, MyLink> vv;
    HorizontalOverlappingTreeLayout HtreeLayout;
    TreeCollapser collapser;
    VisualizationServer.Paintable rings;
    RadialTreeLayout<Object, MyLink> radialLayout;
    protected GradientEdgePaintTransformer<Object, MyLink> edgeDrawPaint;
    protected DirectionDisplayPredicate<Object, MyLink> show_arrow;
    LensSupport magnifyViewSupport;
    LensSupport hyperbolicLayoutSupport;
    LensSupport hyperbolicViewSupport;
    public TreeNode selectedNode;

    public String removeEmptyNode(String NodeString, String treeString) {

        Scanner sc = new Scanner(treeString);

        sc.useDelimiter("\n");
        String temp = sc.next();

        String nodes = sc.next();
        String edges = sc.next();

        if (!edges.contains("('" + NodeString + "'")) {
            nodes = nodes.replaceAll("'" + NodeString + "',", "");

            String searchS = ",'" + NodeString + "'),";

            int c1 = edges.indexOf(searchS);

            if (c1 != -1) {

                String s1 = edges.substring(0, c1);
                String s2 = edges.substring(c1 + searchS.length());

                int c2 = s1.lastIndexOf("(");
                String s3 = s1.substring(0, c2);

                edges = s3 + s2;

                //String nextNodeString = s1.substring(c2-1); //removeEmptyNode(nextNodeString, temp + "\n" + nodes + "\n" + edges);
            }

            return (temp + "\n" + nodes + "\n" + edges);
        } else {
            return treeString;
        }

    }
    List<Integer> leafSequence = new ArrayList<Integer>();

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public List<Integer> getLeafSequence() {
        return leafSequence;
    }

    public void setLeafSequence(List<Integer> leafSequence) {
        this.leafSequence = leafSequence;
    }

    public void processTree(String treeString) {

        treeString = treeString.replaceAll("\r", "");

        leafSequence.clear();

        Scanner sc = new Scanner(treeString);

        sc.useDelimiter("\n");
        String temp = sc.next();

        String nodes = sc.next();

        String[] tempNodes = nodes.split(",");

        TreeNode NodeArray[] = new TreeNode[1000];
        TreeNode LeafArray[] = new TreeNode[1000];

        for (String tempNode : tempNodes) {
            String a = tempNode.replaceAll("\\D", "");
            int index = Integer.parseInt(a);
            TreeNode t = new TreeNode(index, tempNode.replaceAll("[^\\p{L}\\p{N}]", ""));
            if (tempNode.replaceAll("[^\\p{L}\\p{N}]", "").charAt(0) == 'N') {
                String[] ary = new String[]{/*t.getValue()*/};
                t.setNodeTopics(ary);
                NodeArray[index] = t;
                myTree.add(t);
            } else if (tempNode.replaceAll("[^\\p{L}\\p{N}]", "").charAt(0) == 'L') {
                t.setNodeTopics(allTopics.get(index + parent.getGlobalReadIndex()));
                //      labels[1][2];
                LeafArray[index] = t;
                leafSequence.add(index);
                myTree.add(t);
            } else {
                int c = 0;
            }
        }

        String edges = sc.next();

        gh = new DelegateForest<Object, MyLink>(new DirectedOrderedSparseMultigraph<Object, MyLink>());

        //     Graph<Integer,Number> g =     	MixedRandomGraphGenerator.<Integer,Number>generateMixedRandomGraph(edge_weight);
        String[] tempEdges = edges.split("\\),");
        System.out.println(tempNodes.length + " nodes and " + tempEdges.length + " links in the tree");

        for (int i = 0; i < tempEdges.length - 1; i++) {
            String[] tempE = tempEdges[i].split(",");
            TreeNode tt1, tt2;

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

            gh.addVertex(tt1);
            gh.addVertex(tt2);
            //System.out.println(tt1.getValue() + " " + tt2.getValue());
            gh.addEdge(new MyLink(weight, 0), tt1, tt2);
            tt1.addChildNode(tt2);
        }

    }

    
    public void buildTreeWithTreeString(String everything)
    {
         myTree = new ArrayList<TreeNode>();
       
        //folderPath = path;
      
        everything = everything.replaceAll(" ", "");

        processTree(everything);

        try {
            int size = myTree.size();

            BufferedWriter out = new BufferedWriter(new FileWriter(folderPath + "newTree_Node" + size + ".txt"));
            out.write(everything);
            out.close();
        } catch (IOException e) {
            System.out.println("Exception ");

        }

        myTree.get(0).calculateNodeSize();

        //myTree.get(0).calculateNodeString();

        NodeStringProcessing();

        setNodeColor();
        
        
    }
    public void buildTree(String path) throws FileNotFoundException, IOException {

        myTree = new ArrayList<TreeNode>();
        String everything;
        folderPath = path;
        FileInputStream inputStream = new FileInputStream(folderPath + "tree.txt");

        try {
            everything = IOUtils.toString(inputStream);
        } finally {
            inputStream.close();
        }
        everything = everything.replaceAll(" ", "");

        processTree(everything);

        try {
            int size = myTree.size();

            BufferedWriter out = new BufferedWriter(new FileWriter(folderPath + "newTree_Node" + size + ".txt"));
            out.write(everything);
            out.close();
        } catch (IOException e) {
            System.out.println("Exception ");

        }

        myTree.get(0).calculateNodeSize();

        myTree.get(0).calculateNodeString();

        NodeStringProcessing();

        setNodeColor();

    }

    public void setNodeColor() {
        List<Float[]> colorSpecturm = parent.getTemporalFrame().getMainPanel().getCurrentColorMap();

        int size = colorSpecturm.size();

        myTree.get(0).setColor(Color.white);

        for (int i = 0; i < myTree.get(0).getChildren().size(); i++) {

            Color current = new Color(colorSpecturm.get((int) i % size)[1], colorSpecturm.get((int) i % size)[2], colorSpecturm.get((int) i % size)[3]);
            TreeNode t = (TreeNode) myTree.get(0).getChildren().get(i);
            t.setBaseColor(current);
            t.setColor(current);

        }

//        for (int i=0; i<myTree.size(); i++)
//        {
//            TreeNode t = myTree.get(i);
//            if (t.getLevel() == 1)  
//            {
//                t.setBaseColor(t.getParent().getBaseColor());
//                 t.setColor(t.getParent().getBaseColor()) ;         
//            }                        
//        }
        // System.out.println("set color begin");
        int level = 10;//myTree.get(0).getLevel();

        for (int l = 1; l < level; l++) {
            for (int i = 1; i < myTree.size(); i++) {
                TreeNode t = myTree.get(i);

                if (t.getLevel() > 1 && t.getLevel() == l) {
                    TreeNode colorNode = t.getParent(); //myTree.get(i);

//                while (colorNode.getLevel() > 1) {
//                    colorNode = colorNode.getParent();
//                }
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
                            if (tempColor == null) {
                                System.out.println("?? no tempColor");
                            }

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
        //System.out.println("set color success");

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

    public void NodeStringProcessing() {

        //int labelsToDisplay2 = 5;
        for (int i = 0; i < myTree.size(); i++) {
            if (!myTree.get(i).getChildren().isEmpty()) {
                TreeNode thisNode = myTree.get(i);

                int labelsToDisplay2 = labelsToDisplay / myTree.get(i).getNodeSize();
                String[] temp = new String[labelsToDisplay2 * myTree.get(i).getNodeSize()];

                int count = 0;
                for (int k = 0; k < (labelsToDisplay2); k++) {
                    for (int j = 0; j < thisNode.getTopicsContainedIdx().size(); j++) {
                        int index = thisNode.getTopicsContainedIdx().get(j);

                        String t[] = allTopics.get(index + parent.getGlobalReadIndex());
                        temp[count] = t[nodeStringStartIndex + k];
                        count++;
                    }
                }
                thisNode.setNodeTopics(temp);
            }

        }


    }

    public void MergeNodeTreeMultiple(List<String> childString, List<String> oldParentString, String newParentString) throws FileNotFoundException, IOException {
        int size1 = myTree.size();

        myTree.clear();
        String everything;
        FileInputStream inputStream = new FileInputStream(folderPath + "newTree_Node" + size1 + ".txt");
        try {
            everything = IOUtils.toString(inputStream);
        } finally {
            inputStream.close();
        }

        everything = everything.replaceAll(" ", "");

        // replace all strings in list
        for (int i = 0; i < childString.size(); i++) {
            String temp1 = oldParentString.get(i);
            String temp2 = childString.get(i);
            //String targetString = "\\(\'" + temp1 + "\',\'" + temp2 + "\'\\)";
            // String replaceString = "\\(\'" + newParentString + "\',\'" + temp2 + "\'\\)";

            String targetString = "('" + temp1 + "','" + temp2 + "')";
            String replaceString = "('" + newParentString + "','" + temp2 + "')";

            everything = everything.replaceAll(targetString, replaceString);

        }

        everything = everything.replaceAll("\\(\\(", "\\(");
        everything = everything.replaceAll("\\)\\)", "\\)");

        for (int i = 0; i < childString.size(); i++) {

            everything = removeEmptyNode(oldParentString.get(i), everything);
        }

        processTree(everything);

        try {
            int size = myTree.size();
            System.out.println(size);
            BufferedWriter out = new BufferedWriter(new FileWriter(folderPath + "newTree_Node" + size + ".txt"));
            out.write(everything);
            out.close();
        } catch (IOException e) {
            System.out.println("Exception ");

        }

        myTree.get(0).calculateNodeSize();

        myTree.get(0).calculateNodeString();

        //NodeStringProcessing();
        setNodeColor();

    }

    public void MergeNodeTree(String childString, String oldParentString, String newParentString) throws FileNotFoundException, IOException {

        int size1 = myTree.size();

        myTree.clear();
        String everything;
        FileInputStream inputStream = new FileInputStream(folderPath + "newTree_Node" + size1 + ".txt");
        try {
            everything = IOUtils.toString(inputStream);
        } finally {
            inputStream.close();
        }

        everything = everything.replaceAll(" ", "");

        String targetString = "\\(\'" + oldParentString + "\',\'" + childString + "\'\\)";
        String replaceString = "\\(\'" + newParentString + "\',\'" + childString + "\'\\)";

        everything = everything.replaceAll(targetString, replaceString);

//        if (everything2.equals(everything))
//            System.out.println("what why ");
        processTree(everything);

        try {
            int size = myTree.size();
            System.out.println(size);
            BufferedWriter out = new BufferedWriter(new FileWriter(folderPath + "newTree_Node" + size + ".txt"));
            out.write(everything);
            out.close();
        } catch (IOException e) {
            System.out.println("Exception ");

        }

        myTree.get(0).calculateNodeSize();

        myTree.get(0).calculateNodeString();

        //NodeStringProcessing();
        setNodeColor();
    }

    public void MergeLeafNodeTree(String mergeString, String mergeParentString, String mergedtoString, String mergedToParentString, int newNodeIdx) throws FileNotFoundException, IOException {

        int size1 = myTree.size();

        myTree.clear();
        String everything;
        FileInputStream inputStream = new FileInputStream(folderPath + "newTree_Node" + size1 + ".txt");
        try {
            everything = IOUtils.toString(inputStream);
        } finally {
            inputStream.close();
        }

        everything = everything.replaceAll(" ", "");

        String newNodeString = "Node" + Integer.toString(newNodeIdx);

        Scanner sc = new Scanner(everything);

        sc.useDelimiter("\n");
        String temp = sc.next();
        String nodes = sc.next();
        nodes = nodes + "\'" + newNodeString + "\',";

        String edges = sc.next();

        String removeOldString = "\\(\'" + mergeParentString + "\',\'" + mergeString + "\'\\),";
        edges = edges.replaceAll(removeOldString, "");

        String targetString = "\\(\'" + mergedToParentString + "\',\'" + mergedtoString + "\'\\)";
        String replaceString = "\\(\'" + mergedToParentString + "\',\'" + newNodeString + "\'\\)";

        edges = edges.replaceAll(targetString, replaceString);

        edges = edges.replaceAll("}", "");
        String AddEdge = "(\'" + newNodeString + "\',\'" + mergeString + "\'),";
        edges = edges + AddEdge;
        AddEdge = "(\'" + newNodeString + "\',\'" + mergedtoString + "\'),";
        edges = edges + AddEdge + "}";

        everything = temp + "\n" + nodes + "\n" + edges;

        processTree(everything);

        try {
            int size = myTree.size();
            System.out.println(size);
            BufferedWriter out = new BufferedWriter(new FileWriter(folderPath + "newTree_Node" + size + ".txt"));
            out.write(everything);
            out.close();
        } catch (IOException e) {
            System.out.println("Exception ");

        }

        myTree.get(0).calculateNodeSize();

        myTree.get(0).calculateNodeString();

        setNodeColor();

        //NodeStringProcessing();
    }

    public void buildNewTree(String deleteString, String parentString) throws FileNotFoundException, IOException {

        int size1 = myTree.size();

        myTree.clear();
        String everything;
        FileInputStream inputStream = new FileInputStream(folderPath + "newTree_Node" + size1 + ".txt");
        try {
            everything = IOUtils.toString(inputStream);
        } finally {
            inputStream.close();
        }

        everything = everything.replaceAll(" ", "");

        String deleteStringLong = deleteString + "\',";

        int c1 = everything.indexOf(deleteStringLong, 20);

        String s1 = everything.substring(0, c1 - 1);
        String s2 = everything.substring(c1 + 2 + deleteString.length());

        everything = s1 + s2;

        String s = "(\'" + parentString + "\',\'" + deleteString + "\'),";
        //"('Node34','Node34'),"
        //everything = everything.replaceAll(s, "");
        int c2 = everything.indexOf(s, 20);
        s1 = everything.substring(0, c2);
        s2 = everything.substring(c2 + s.length());
        everything = s1 + s2;
        everything = everything.replaceAll(deleteStringLong, parentString + "\',");

        processTree(everything);

        
//        if (!parent.b_readFromDB)
        try {
            int size = myTree.size();
            System.out.println(size);
            BufferedWriter out = new BufferedWriter(new FileWriter(folderPath + "newTree_Node" + size + ".txt"));
            out.write(everything);
            out.close();
        } catch (IOException e) {
            System.out.println("Exception ");

        }                
         if (parent.b_readFromDB)//else
        {
        LDAHTTPClient connection  = new LDAHTTPClient("http", parent.host, String.valueOf(parent.port));
        try {
            connection.login();
        } catch (IOException ex) {
            Logger.getLogger(MinimalismMainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        connection.updateTree(parent.collection, everything);
        
        connection.close();
        
        }
        myTree.get(0).calculateNodeSize();

        myTree.get(0).calculateNodeString();

        // NodeStringProcessing();
        setNodeColor();

    }
    
    
    int controlPanelHeight = 100;

    public void generateLayout() {

        totalEdges = myTree.size();

        // Layout<TreeNode,MyLink> layout = new CircleLayout(gh);
        //  TreeLayout treeLayout = new TreeLayout<TreeNode,MyLink>(gh);
        radialLayout = new RadialTreeLayout<Object, MyLink>(gh);
//         SpringLayout springLayout = new SpringLayout<TreeNode,MyLink>(gh);
        HtreeLayout = new HorizontalOverlappingTreeLayout<Object, MyLink>(gh, node_width_interval, node_height_interval);
        //  radialLayout.setSize(new Dimension(width,height));

        ewcs = new EdgeWeightStrokeFunction<MyLink>();

        vv = new VisualizationViewer<Object, MyLink>(HtreeLayout, new Dimension(width, height-controlPanelHeight));
        // vv.setSize(new Dimension(width, height));
        vv.setBackground(Color.white);

        collapser = new TreeCollapser();

        
        this.setLayout(new BorderLayout());
        
        
        JPanel contPanel = new JPanel();
        contPanel.setPreferredSize(this.getSize());
        contPanel.setLayout(new BorderLayout());
        
        
        
        content = this;//.getContentPane();
        //GraphZoomScrollPane zoompanel = new GraphZoomScrollPane(vv);

        //content.add(zoompanel);

        final DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse();
        vv.setGraphMouse(graphMouse);

        graphMouse.add(new MyGraphMousePlugin());

        hyperbolicViewSupport
                = new ViewLensSupport<Object, MyLink>(vv, new HyperbolicShapeTransformer(vv,
                                vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW)),
                        new ModalLensGraphMouse());

        hyperbolicLayoutSupport
                = new LayoutLensSupport<Object, MyLink>(vv, new HyperbolicTransformer(vv,
                                vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT)),
                        new ModalLensGraphMouse());

        magnifyViewSupport
                = new ViewLensSupport<Object, MyLink>(vv, new MagnifyShapeTransformer(vv,
                                vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW)),
                        new ModalLensGraphMouse(new LensMagnificationGraphMousePlugin(1.f, 6.f, .2f)));

        // hyperbolicViewSupport.getLensTransformer().setLensShape(new Rectangle2D.Float(500, 500, 100, 100));
        // magnifyViewSupport.getLensTransformer().setLensShape()
        hyperbolicLayoutSupport.getLensTransformer().setLensShape(hyperbolicViewSupport.getLensTransformer().getLensShape());

        magnifyViewSupport.getLensTransformer().setLensShape(hyperbolicLayoutSupport.getLensTransformer().getLensShape());
        //magnifyViewSupport.getLensTransformer().setLensShape(new Rectangle2D.Float(500, 500, 100, 100));

        graphMouse.addItemListener(magnifyViewSupport.getGraphMouse().getModeListener());

//            final JCheckBox magnifyView = new JRadioButton("Magnified View");
//        magnifyView.addItemListener(new ItemListener(){
//            public void itemStateChanged(ItemEvent e) {
//                magnifyViewSupport.activate(e.getStateChange() == ItemEvent.SELECTED);
//            }
//        });
        
        
    
        final PickedState<Object> pickedState = vv.getPickedVertexState();

        pickedState.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {

                Object subject = e.getItem();

//                 TemporalViewPanel mainPan = parent.getTemporalFrame().getMainPanel();
//                        mainPan.setFocusedCatgory(-99);
//                         mainPan.UpdateTemporalView(new Dimension(mainPanel.getWidth(), mainPanel.getHeight()),mainPan.getLocalNormalizingValue());
////                         
                if (pickedState.getPicked().size() == 1) {
                    if (subject instanceof TreeNode) {

                        TreeNode s = (TreeNode) subject;
                        selectedNode = s;
                        int in = s.getIndex();
                        String sla = s.getValue();
                        if (parent.getTemporalFrame() != null) {

                            int highlight_index = -1;
                            //

                            TemporalViewPanel mainPanel = parent.getTemporalFrame().getMainPanel();

                            for (int i = 0; i < mainPanel.currentNode.getChildren().size(); i++) {
                                TreeNode ttt = (TreeNode) mainPanel.currentNode.getChildren().get(i);
                                if (ttt.getIndex() == in && (ttt.getValue() == null ? sla == null : ttt.getValue().equals(sla))) {
                                    highlight_index = i;

                                }

                                if (highlight_index > -1) {
                                    //System.out.println("highlight" + highlight_index);
                                    mainPanel.setFocusedCatgory(highlight_index);
                                    mainPanel.repaint();
                                    // mainPanel.UpdateTemporalView(new Dimension(mainPanel.getWidth(), mainPanel.getHeight()),mainPanel.getLocalNormalizingValue());
                                }
                            }

                        }
                    } else if (subject instanceof DelegateTree) {
                        DelegateTree f = (DelegateTree) subject;
                        TreeNode t = (TreeNode) f.getRoot();

                        int in = t.getIndex();
                        String sla = t.getValue();
                        if (parent.getTemporalFrame() != null) {
                        }
                    } else {
                    }
                }

            }
        });

        JComboBox modeBox = graphMouse.getModeComboBox();
        modeBox.addItemListener(graphMouse.getModeListener());
        graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);

        final ScalingControl scaler = new CrossoverScalingControl();

        //scaler.scale(vv, 1, vv.getCenter());
        //  graphMouse.add(new ScalingGraphMousePlugin(new LayoutScalingControl(),1));
        //graphMouse.add(new ScalingGraphMousePlugin(new LayoutScalingControl(), -1));
        JButton left = new BasicArrowButton(BasicArrowButton.WEST);
        JButton right = new BasicArrowButton(BasicArrowButton.EAST);
        JButton up = new BasicArrowButton(BasicArrowButton.NORTH);
        JButton down = new BasicArrowButton(BasicArrowButton.SOUTH);
        left.setPreferredSize(new Dimension(40, 40));
        right.setPreferredSize(new Dimension(40, 40));
        up.setPreferredSize(new Dimension(40, 40));
        down.setPreferredSize(new Dimension(40, 40));

        left.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //MutableTransformer view = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW);
                MutableTransformer layout = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT);

                double scale = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).getScale();
                double deltaX = 20.0 / scale;
                double deltaY = 1;
                // Point2D delta = new Point2D.Double(deltaX, deltaY);

                layout.translate(deltaX, deltaY);

            }
        });

        right.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //MutableTransformer view = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW);
                MutableTransformer layout = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT);

                double scale = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).getScale();
                double deltaX = -20.0 / scale;
                double deltaY = 1;
                // Point2D delta = new Point2D.Double(deltaX, deltaY);

                layout.translate(deltaX, deltaY);

            }
        });

        up.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //MutableTransformer view = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW);
                MutableTransformer layout = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT);

                double scale = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).getScale();
                double deltaX = 1;
                double deltaY = 20.0 / scale;
                // Point2D delta = new Point2D.Double(deltaX, deltaY);

                layout.translate(deltaX, deltaY);

            }
        });

        down.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //MutableTransformer view = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW);
                MutableTransformer layout = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT);

                double scale = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).getScale();
                double deltaX = 1;
                double deltaY = -20.0 / scale;
                // Point2D delta = new Point2D.Double(deltaX, deltaY);

                layout.translate(deltaX, deltaY);

            }
        });

        JButton plus = new JButton("+");
        plus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 1.1f, vv.getCenter());

            }
        });
        JButton minus = new JButton("-");
        minus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 1 / 1.1f, vv.getCenter());

            }
        });

        JButton showEdge = new JButton("show Edge");
        showEdge.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ewcs.showHighlighted = !ewcs.showHighlighted;

                vv.repaint();
            }
        });

        Transformer<Object, Paint> vertexColor = new Transformer<Object, Paint>() {
            public Paint transform(Object i) {

                Color red = new Color(1, (float) 0, 0, (float) 1);
                Color yellow = new Color(1, (float) 1, 0, (float) 1);

                if (i instanceof TreeNode) {
                    TreeNode t = (TreeNode) i;

                    if (t.getDraggedTo() == true) {
                        return Color.cyan;
                    } else if (vv.getPickedVertexState().isPicked(t)) {
                        return red;
                    }

                    return t.getColor();

                } else if (i instanceof DelegateTree) {
                    DelegateTree t = (DelegateTree) i;

                    TreeNode rt = (TreeNode) t.getRoot();
                    if (rt.getDraggedTo() == true) {
                        return Color.cyan;
                    } else if (vv.getPickedVertexState().isPicked(rt)) {
                        return red;
                    }

                    return rt.getColor();

                }

                return red;
            }
        };

        edgeDrawPaint = new GradientEdgePaintTransformer<Object, MyLink>(Color.black, Color.lightGray, vv);
        show_arrow = new DirectionDisplayPredicate<Object, MyLink>(false, false);

//        displayedWordMap = new HashMap<String, List<Dimension>>();
//
//        Map<TreeNode, Point2D> lo = HtreeLayout.getLocations();
//        Set set = lo.entrySet();
//        Iterator i = set.iterator();
//
//        while (i.hasNext()) {
//            Map.Entry me = (Map.Entry) i.next();
//            TreeNode t = (TreeNode) me.getKey();
//            Point2D p = (Point2D) me.getValue();
//
//            if (t.getChildren().isEmpty() && t.getValue().contains("L")) {
//
//                int index = t.getIndex();
//                int px = (int) p.getX();
//                int py = (int) p.getY();
//
//                int countleng = 0;
//                for (int j = 1; j < labelsToDisplay; j++) {
//                    int leng = t.getNodeTopics()[j].length();
//                    Rectangle2D rect = new Rectangle2D.Float(px + 10 + countleng, py - 10, leng * fontSizePerChar, 20);
//                    countleng += (leng * fontSizePerChar + 2);
//                    // labeltexts[index][j - 1] = rect;
//
//                    if (!displayedWordMap.containsKey(t.getNodeTopics()[j])) {
//                        List<Dimension> templist = new ArrayList<Dimension>();
//                        Dimension tempD = new Dimension(index, j - 1);
//                        templist.add(tempD);
//                        displayedWordMap.put(t.getNodeTopics()[j], templist);
//
//
//                    } else {
//                        Dimension tempD = new Dimension(index, j - 1);
//                        displayedWordMap.get(t.getNodeTopics()[j]).add(tempD);
//                    }
//                }
//
//            }
//
//        }
        Transformer<Object, Shape> vertexSize = new Transformer<Object, Shape>() {
            public Shape transform(Object i) {
                Shape s = new Ellipse2D.Double(0, 0, 10, 10);

                if (i instanceof TreeNode) {
                    TreeNode t = (TreeNode) i;

                    if (!t.getChildren().isEmpty()) {

                        int size = t.getNodeSize() * 15;

                        if (size >= 60) {
                            size = (int) 80;
                        }
//                        else {
//                            size = 14;
//                        }

//                        if (size == 1)
//                                size = 4;
                        s = new Ellipse2D.Double(-size / 2, -size / 2, size, size);

                        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.S);
                    } else {

                        int size = 15;//t.getNodeSize();// 4 * 2;
                        s = new Ellipse2D.Double(-size / 2, -size / 2, size, size);

                        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.S);
                    }
                    return s;

                } else if (i instanceof DelegateTree) {
                    DelegateTree f = (DelegateTree) i;
                    TreeNode t = (TreeNode) f.getRoot();
                    int size = t.getNodeSize() * 15;

                    s = new Rectangle.Double(-size / 2, -size / 2, size, size);
                    return s;
                }

                return s;

            }
        };
//        
        Transformer<Object, String> VertexLabel = new Transformer<Object, String>() {
            public Paint transform(TreeNode i) {
//vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.S);
//                 if(!i.getChildren().isEmpty()) { 
//                   vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.E);
//                }
//                else {
//                 vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
//                }
//                 
//                 
                //Ellipse2D circle = new Ellipse2D.Double(-1,-1,2,2);//(-15, -15, 30, 30);

                // in this case, the vertex is twice as large
                //if(i.getIndex() == 2) return AffineTransform.getScaleInstance(1, 1).createTransformedShape(circle);
                // else return circle;
                return null;

            }

            public String transform(Object i) {

                if (i instanceof TreeNode) {
                    return ((TreeNode) i).getAnnonation();

                } else {
                    return "";

                }

                //            TreeNode t = (TreeNode) i;
                //            StringBuffer result = new StringBuffer();
//                int len = t.getNodeTopics().length;
//
//
//                for (int k = 1; k < len; k++) {
//                    result.append(t.getNodeTopics()[k]);
//                    result.append(" ");
//
//                }
//                String mynewstring = result.toString();
                //return mynewstring;
                // return 
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };

        //Position t = vv.getRenderer().getVertexLabelRenderer().getPosition();
        vv.getRenderContext().setVertexFontTransformer(new Transformer<Object, Font>() {
            @Override
            public Font transform(Object arg0) {
                Font font = new Font("Arial Unicode MS", Font.ITALIC, 20);
                return font;
            }
        });

        vv.getRenderContext().setVertexFillPaintTransformer(vertexColor);
        //vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv.getRenderContext().setVertexLabelTransformer(VertexLabel);//.setPosition(Renderer.VertexLabel.Position.CN//TR);
        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.S);

        //   vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.E);
        //  vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());      
        vv.getRenderContext().setVertexShapeTransformer(vertexSize);
        // vv.getRenderContext().setVertexShapeTransformer(new ClusterVertexShapeFunction());
        // vv.getRenderContext().setVertexFontTransformer(new VertexFontTransformer());
        vv.setVertexToolTipTransformer(new ToStringLabeller());

        vv.getRenderContext().setEdgeArrowPredicate(show_arrow);
        //vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.QuadCurve<Object, MyLink>());
        vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.CubicCurve<Object, MyLink>());
        //vv.getRenderContext().setArrowFillPaintTransformer(new ConstantTransformer(Color.lightGray));

        vv.getRenderContext().setEdgeStrokeTransformer(ewcs);
        vv.getRenderContext().setEdgeDrawPaintTransformer(edgeDrawPaint);

        JButton collapse = new JButton("Collapse");
        collapse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Collection picked = new HashSet(vv.getPickedVertexState().getPicked());
                if (picked.size() == 1) {

                    Object root1 = picked.iterator().next();
                    Forest inGraph = (Forest) HtreeLayout.getGraph();

                    try {
                        collapser.collapse(vv.getGraphLayout(), inGraph, root1);
                        buildLabelLocations();
                        isCollapsed = true;
                    } catch (InstantiationException e1) {
                    } catch (IllegalAccessException e1) {
                    }

                    vv.getPickedVertexState().clear();
                    vv.repaint();

                }
            }
        });

        JCheckBox merge = new JCheckBox("Merge");
        merge.setSelected(true);
        merge.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                bMergeMode = ((JCheckBox) e.getSource()).isSelected();
            }
        });

        JButton expand = new JButton("Expand");
        expand.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Collection picked = vv.getPickedVertexState().getPicked();
                for (Object v : picked) {
                    if (v instanceof Forest) {
                        Forest inGraph = (Forest) HtreeLayout.getGraph();
                        collapser.expand(inGraph, (Forest) v);
                        buildLabelLocations();
                    }

                    vv.getPickedVertexState().clear();

                    vv.repaint();
                }

                ObservableCachingLayout lll = (ObservableCachingLayout) vv.getGraphLayout();

                isCollapsed = false;

                for (int i = 0; i < lll.getGraph().getVertices().size(); i++) {
                    Object o = lll.getGraph().getVertices().toArray()[i];

                    if (o instanceof DelegateTree) {
                        isCollapsed = true;
                        break;
                    }

                }

            }
        });

        //  System.out.println("temporal frame " + tvf);
        JButton delete = new JButton("Delete");
        delete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Collection picked = new HashSet(vv.getPickedVertexState().getPicked());

                if (picked.size() == 1) {

                    Object root1 = picked.iterator().next();
                    //Forest inGraph = (Forest) HtreeLayout.getGraph();

                    TreeNode deleted_one = (TreeNode) root1;
                    try {
                        buildNewTree(deleted_one.getValue(), deleted_one.getParent().getValue());

                        vv.getGraphLayout().setGraph(gh);
                        HtreeLayout = new HorizontalOverlappingTreeLayout<Object, MyLink>(gh, node_width_interval, node_height_interval);
                        // vv.getPickedVertexState().clear();
                        buildLabelLocations();
                        vv.repaint();

                        //System.out.println(tvf);
                        //System.out.println("tree in graph is " + myTree.size());
                        if (tvf != null) {
                            tvf.updateData(myTree);

                        }

                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(TopicGraphViewPanel.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(TopicGraphViewPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
        });
        
        
       

        buildLabelLocations();

        System.out.println(" build labels done");
        
        
        vv.addPreRenderPaintable(new VisualizationViewer.Paintable() {
            public void paint(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                AffineTransform oldXform = g2d.getTransform();
                AffineTransform lat
                        = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).getTransform();
                AffineTransform vat
                        = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).getTransform();
                AffineTransform at = new AffineTransform();
                at.concatenate(g2d.getTransform());
                at.concatenate(vat);
                at.concatenate(lat);

                // g.drawImage(icon.getImage(), 0, 0,
                // 		icon.getIconWidth(),icon.getIconHeight(),vv);
                LensTransformer m = magnifyViewSupport.getLensTransformer();

                Shape lens = magnifyViewSupport.getLensTransformer().getLensShape();

                // at.concatenate(m.getTransform());
                g2d.setTransform(at);

//                for (int i=0; i<annotation_count; i++)
//                {
//                    
//                    Point2D p2d = new Point2D.Double();
//                    
//                     at.transform(annotation_locations.get(i), p2d);
//                    
//                    annotation.get(i).setBounds((int)p2d.getX(), (int)p2d.getX(), 100, 50);
//                    
//                }
                Collection picked = vv.getPickedVertexState().getPicked();

                // top selected node string
                if (!picked.isEmpty()) {

                    g.setColor(new Color(117.0f / 255.0f, 107f / 255.0f, 177 / 255.0f));
                    Font font = new Font(HelveticaFont, Font.BOLD, 40);
                    g.setFont(font);
                    Object o = picked.iterator().next();
                    TreeNode t1;
                    if (o instanceof TreeNode) {
                        t1 = (TreeNode) o;
                        if (t1.getChildren().isEmpty())
                        {
                        StringBuilder result = new StringBuilder();
                    int len = t1.getNodeTopics().length;

                    for (int k = 1; k < len; k++) {
                        result.append(t1.getNodeTopics()[k]);
                        result.append(" ");

                    }
                    String mynewstring = result.toString();

                    g.drawString(mynewstring, 20, 30);
                        
                        }
                    } else {
                        t1 = (TreeNode) ((DelegateTree) o).getRoot();
                    }

                    

                }

                ObservableCachingLayout lll = (ObservableCachingLayout) vv.getGraphLayout();

                for (int i = 0; i < lll.getGraph().getVertices().size(); i++) {
                    Object o = lll.getGraph().getVertices().toArray()[i];
                    if (o instanceof TreeNode) {

                        highlightOne = 0;
                        TreeNode t = (TreeNode) o;
                        if (t.equals(selectedNode)) {
                            highlightOne = 1;
                        }

                        Point2D loc = lll.transform(lll.getGraph().getVertices().toArray()[i]);

                        if (t.getChildren().isEmpty() && t.getValue().contains("L")) {

                            if (allLabels.get(t) != null) {
                                
                                
                                
                                for (int j = 0; j < allLabels.get(t).size(); j++) {

                                    LabelText temp = allLabels.get(t).get(j);

                                    if (highlightOne == 1 && j!= 0) {
                                        temp.drawRect(g);
                                    }

                                    if (temp.highlightFromLabelTopics == true && j==0) {
                                        temp.drawLabelRect(g);
                                    }
                                    
                                    if (temp.isDisplayed) {
                                        temp.drawString(g);
                                    }

                                }
                            }
                        }

                    } else if (o instanceof DelegateTree) {

                        TreeNode t = (TreeNode) ((DelegateTree) o).getRoot();

                        if (allLabels.get(t) != null) {
                            for (int j = 0; j < allLabels.get(t).size(); j++) {
                                LabelText temp = allLabels.get(t).get(j);

                                if (temp.isDisplayed) {
                                    temp.drawString(g);
                                }

                            }
                        }

                    }
                }

                g2d.setTransform(oldXform);
            }

            public boolean useTransform() {
                return false;
            }
        });

        vertexlabels = new ArrayList<JPanel>();
        setColorMap();

        JPanel fontSliderPanel = new JPanel(new GridLayout(1, 2));
        // fontSliderPanel.setSize(new Dimension(100,30));
        final JSlider fontSlider = new JSlider();
        final JLabel fontSizeLabel = new JLabel();
        fontSizeLabel.setText(String.valueOf(labelFontSize));

        fontSlider.setValue((int) (((float) labelFontSize - (float) MINFONTSIZE) / ((float) MAXFONTSIZE - (float) MINFONTSIZE) * 100));

        fontSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {

                int fontsize = fontSlider.getValue();

                labelFontSize = (int) (fontsize / 100.0f * (MAXFONTSIZE - MINFONTSIZE) + MINFONTSIZE);

                highlightFontSize = labelFontSize + 10;
                fontSizeLabel.setText(String.valueOf(labelFontSize));

//                node_height_interval = 50 + 10*(labelFontSize - 10);
//                node_width_interval = 150 + 10*(labelFontSize - 10);
//                
////                System.out.println(node_width_interval + " " + node_height_interval);
////                vv.getGraphLayout().setGraph(gh);
////                 HtreeLayout = new HorizontalOverlappingTreeLayout<Object, MyLink>(gh, node_width_interval, node_height_interval);
////               
                updateLabelLocations();
                //buildLabelLocations();
                vv.repaint();
            }
        });

        fontSliderPanel.add(fontSlider);
        fontSliderPanel.add(fontSizeLabel);

        JButton showText = new JButton("Display TEXT");
        showText.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                b_showText = !b_showText;
                updateLabelLocations();
                vv.repaint();

            }
        });

        JPanel naviGrid = new JPanel(new GridLayout(2, 2));
        naviGrid.setBorder(BorderFactory.createTitledBorder("Navigation"));

        naviGrid.add(left);
        naviGrid.add(right);
        naviGrid.add(up);
        naviGrid.add(down);

        JPanel scaleGrid = new JPanel(new GridLayout(1, 0));
        scaleGrid.setBorder(BorderFactory.createTitledBorder("Zoom"));
        JPanel controls = new JPanel();
        controls.setPreferredSize(new Dimension(width,controlPanelHeight));
        scaleGrid.add(plus);
        scaleGrid.add(minus);

        controls.add(showText);
        controls.add(naviGrid);
        controls.add(scaleGrid);
        controls.add(modeBox);
        controls.add(collapse);
        controls.add(expand);
        controls.add(delete);
        controls.add(showEdge);
        controls.add(merge);

        controls.add(fontSliderPanel);
        content.add(controls, BorderLayout.NORTH);

        final JTextField searchField = new JTextField("Enter Keyword");
        searchField.setSize(300, 24);
        controls.add(searchField);

        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                searchField.setText("");
                parent.b_searchHighlight = true;
            }

            public void focusLost(FocusEvent e) {
                searchField.setText("Enter Keyword");
                parent.b_searchHighlight = false;
                parent.getTemporalFrame().getMainPanel().repaint();
            }
        });

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String searchString = searchField.getText();

                    if (searchString.length() > 1) {

                        for (List<LabelText> value : allLabels.values()) {
                            for (int i = 0; i < value.size(); i++) {

                                value.get(i).isHighlighted = false;
                            }

                        }

                        searchString = searchString.replaceAll(" ", "");

                        for (List<LabelText> value : allLabels.values()) {
                            for (int i = 0; i < value.size(); i++) {

                                if (value.get(i).s != null) {
                                    if (value.get(i).s.equals(searchString)) {
                                        value.get(i).isHighlighted = true;

                                    }
                                }
                            }

                        }

                        List<Integer> highlightInt = new ArrayList<Integer>();
                        highlightInt.clear();

                        for (int j = parent.getGlobalReadIndex(); j < allTopics.size(); j++) {
                            String[] o = allTopics.get(j);

                            for (int i = 0; i < o.length; i++) {

                                if (o[i] != null) {
                                    if (o[i].equals(searchString)) {
                                        highlightInt.add(j - 1);
                                        break;

                                    }
                                }
                            }

                        }

//                        System.out.println(highlightInt.size());
//                        System.out.println(highlightInt);
//                        
//                        
                        parent.searchHighlight(highlightInt);

                    }

                    updateLabelLocations();
                    //buildLabelLocations();
                    vv.repaint();
                }
            }
        });
        content.add(vv,BorderLayout.SOUTH);
        
        //content.setLayout(null);
        content.add(magnifyLabel);
        content.add(magnifyLabelLeft);
        content.add(magnifyLabelRight);
        content.add(magnifyLabelLeftLeft);
        content.add(magnifyLabelRightRight);
        content.add(tooltipLabel);
        
        
        
        tooltipLabel.setBackground(Color.LIGHT_GRAY);
        tooltipLabel.setOpaque(true);

        magnifyLabel.setBackground(Color.white);
        magnifyLabel.setOpaque(true);

        magnifyLabelLeft.setBackground(Color.white);
        magnifyLabelLeft.setOpaque(true);

        magnifyLabelRight.setBackground(Color.white);
        magnifyLabelRight.setOpaque(true);

        magnifyLabelLeftLeft.setBackground(Color.white);
        magnifyLabelLeftLeft.setOpaque(true);

        magnifyLabelRightRight.setBackground(Color.white);
        magnifyLabelRightRight.setOpaque(true);

        

    }

    HashMap<TreeNode, List<LabelText>> highlightedMap = new HashMap<TreeNode, List<LabelText>>();

    public void highLightByYearIdxKwNode(TreeNode key, int selectedTimeColumnIdx, List<List<int[]>> tYK, HashMap<TreeNode, List<LabelText>> hm) {

        if (tYK == null) {
            System.out.println("topic Year Keywords is null");
            return;
        }

        TreeNode t = key;

        ObservableCachingLayout lll = (ObservableCachingLayout) vv.getGraphLayout();

        for (int ii = 0; ii < t.getTopicsContainedIdx().size(); ii++) {

            TreeNode t1 = null;
            String matchedNodeString = "LeafTopic" + t.getTopicsContainedIdx().get(ii);

            for (int i = 0; i < lll.getGraph().getVertices().size(); i++) {
                Object o = lll.getGraph().getVertices().toArray()[i];
                t1 = (TreeNode) o;
                if (t1.getValue().equals(matchedNodeString)) {
                    break;
                }
            }

            //System.out.println("t1 " + t1.getValue() + " inde " + matchedNodeString);
            if (allLabels.get(t1) != null) {

                if (!hm.containsKey(t1)) {
                    List<LabelText> aaa = new ArrayList<LabelText>();
                    hm.put(t1, aaa);
                }

                List<LabelText> ltl = allLabels.get(t1);

                int[] index = tYK.get(t.getTopicsContainedIdx().get(ii)).get(selectedTimeColumnIdx);

                for (int i = 0; i < index.length; i++) {

                    if ((index[i]) < (labelsToDisplay - 2)) {

                        //ltl.get(index[i] + 1).isHighlighted = true;
                        hm.get(t1).add(ltl.get(index[i] + 1));
                        //System.out.println("added label");
                    }

                }

            } else {
                System.out.println("where is my node?");
            }

        }

        //updateLabelLocations();
        //vv.repaint();
    }

    public List<LabelText> getCurrentNodeLabels(TreeNode t) {

        List<LabelText> r = new ArrayList<LabelText>();

//        ObservableCachingLayout lll = (ObservableCachingLayout) vv.getGraphLayout();
//        
//        for (int ii = 0; ii < t.getTopicsContainedIdx().size(); ii++) {
//
//            TreeNode t1 = null;
//            String matchedNodeString = "LeafTopic" + t.getTopicsContainedIdx().get(ii);
//
//            for (int i = 0; i < lll.getGraph().getVertices().size(); i++) {
//                Object o = lll.getGraph().getVertices().toArray()[i];
//                t1 = (TreeNode) o;
//                if (t1.getValue().equals(matchedNodeString)) {
//                    break;
//                }
//            }
//
//                //System.out.println("t1 " + t1.getValue() + " inde " + matchedNodeString);
//            if (allLabels.get(t1) != null) {
//
//                if (!hm.containsKey(t1)) {
//                    List<labelText> aaa = new ArrayList<labelText>();
//                    hm.put(t1, aaa);
//                }
//
//                List<labelText> ltl = allLabels.get(t1);
//               
//
//                for (int i = 0; i < index.length; i++) {
//
//                    if ((index[i]) < (labelsToDisplay - 2)) {
//
//                        ltl.get(index[i] + 1).isHighlighted = true;
//                        hm.get(t1).add(ltl.get(index[i] + 1));
//                        //System.out.println("added label");
//                    }
//
//                }
//
//            }
//
//        }
        return r;

    }

    public void highLightByYearIdxKwNode(TreeNode key, int selectedTimeColumnIdx, List<List<int[]>> tYK) {

        //node will be two situtations 1. collapsed 2. not collapsed, highlight all children
        for (List<LabelText> v : allLabels.values()) {
            for (int i = 0; i < v.size(); i++) {
                v.get(i).isHighlighted = false;
            }
        }
        TreeNode t = null;

        int situation = -1;

        ObservableCachingLayout lll = (ObservableCachingLayout) vv.getGraphLayout();

        for (int i = 0; i < lll.getGraph().getVertices().size(); i++) {
            Object o = lll.getGraph().getVertices().toArray()[i];

            if (o instanceof TreeNode) {
                t = (TreeNode) o;
//                if (key.getChildren().isEmpty())
//                if (key.getValue().equals(t.getValue())) {
//                    situation = 1;
//                    break;
//                }

                if (!key.getChildren().isEmpty()) {
                    if (key.getValue().equals(t.getValue())) {
                        situation = 2;
                        break;
                    }
                }
            } else {

                t = (TreeNode) ((DelegateTree) o).getRoot();
                if (key.getValue().equals(t.getValue())) {
                    situation = 1;
                    break;
                }
            }

        }

        if (situation == 1 && allLabels.get(t) != null) {
            List<LabelText> ltl = allLabels.get(t);

            for (int i = 0; i < t.getTopicsContainedIdx().size(); i++) {
                int leafIndex = key.getTopicsContainedIdx().get(i);

                for (int j = 0; j < tYK.get(leafIndex).get(selectedTimeColumnIdx).length; j++) {

                    int highlightIdx = tYK.get(leafIndex).get(selectedTimeColumnIdx)[j];

                    bHere:
                    for (int k = 0; k < ltl.size(); k++) {
                        if ((ltl.get(k).column == leafIndex)
                                && (ltl.get(k).row == (highlightIdx + 1))) {

                            ltl.get(k).isHighlighted = true;

                            break bHere;
                        }
                    }

                }
            }

        } else if (situation == 2) {

            for (int ii = 0; ii < t.getTopicsContainedIdx().size(); ii++) {

                TreeNode t1 = null;
                String matchedNodeString = "LeafTopic" + t.getTopicsContainedIdx().get(ii);

                for (int i = 0; i < lll.getGraph().getVertices().size(); i++) {
                    Object o = lll.getGraph().getVertices().toArray()[i];
                    t1 = (TreeNode) o;
                    if (t1.getValue().equals(matchedNodeString)) {
                        break;
                    }
                }

                //System.out.println("t1 " + t1.getValue() + " inde " + matchedNodeString);
                if (allLabels.get(t1) != null) {
                    List<LabelText> ltl = allLabels.get(t1);

                    int[] index = tYK.get(t.getTopicsContainedIdx().get(ii)).get(selectedTimeColumnIdx);

                    for (int i = 0; i < index.length; i++) {

                        if ((index[i]) < (labelsToDisplay - 2)) {

                            ltl.get(index[i] + 1).isHighlighted = true;
                        }

                    }
                }

            }

        }

        updateLabelLocations();
        vv.repaint();

    }

    public void highLightByYearIdxKw(TreeNode key, int[] index) {

        for (List<LabelText> v : allLabels.values()) {
            for (int i = 0; i < v.size(); i++) {
                v.get(i).isHighlighted = false;
            }
        }

        Map<TreeNode, Point2D> lo = HtreeLayout.getLocations();
        Set set = lo.entrySet();
        Iterator it = set.iterator();
        TreeNode t = null;
        while (it.hasNext()) {
            Map.Entry me = (Map.Entry) it.next();
            Object o = me.getKey();
            if (o instanceof TreeNode) {
                t = (TreeNode) me.getKey();
                if (key.getValue().equals(t.getValue())) {
                    break;
                }
            }

        }

//          ObservableCachingLayout lll = (ObservableCachingLayout) vv.getGraphLayout();
//          TreeNode thisNode = null;
//        for (int i = 0; i < lll.getGraph().getVertices().size(); i++) {
//            Object o = lll.getGraph().getVertices().toArray()[i];
//            
//            if (o instanceof TreeNode)
//            {
//                thisNode = (TreeNode) o;
//                if (key.getValue().equals(thisNode.getValue()))
//                    break;
//            }
//        }
        if (allLabels.get(t) != null) {
            List<LabelText> ltl = allLabels.get(t);

            for (int i = 0; i < index.length; i++) {

                //System.out.println(t + " " + (index[i] + 1));
                //System.out.println("labelsToDisplay " + labelsToDisplay + "size " + ltl.size() + " index " + (index[i]+1));
                if ((index[i]) < (labelsToDisplay - 2)) {
                    ltl.get(index[i] + 1).isHighlighted = true;
                }

            }
        }

        updateLabelLocations();
        vv.repaint();

    }

    public void setHighlightLabelsFromLabelTopics(HashMap<String, List<Integer>> highindex, HashMap<String, List<Float>> highweight, HashMap<String, Color> highIndexNumber) {
        //Color []colorPlate = {Color.CYAN, Color.pink, Color.MAGENTA, Color.gray};
        for (List<LabelText> lt : allLabels.values()) {
            for (LabelText l : lt) {
                l.setHighlightFromLabelTopics(false);
                l.labelColor.clear();
                //l.setRectColor(Color.yellow);
            }
        }

        Iterator it = highindex.keySet().iterator();
        int currI = 0;
        while (it.hasNext()) {
            String key = (String) it.next();
            List<Integer> index = highindex.get(key);
            List<Float> weight = highweight.get(key);
            Color highlightColor = highIndexNumber.get(key);

            int size2 = index.size();
            Color tempColor = highlightColor;

            Color convertColor = null;
            
            float max_weight = -1.0f;
            for (int j=0;j<weight.size(); j++)
                if (weight.get(j)>=max_weight)
                    max_weight = weight.get(j);
            
            
            for (int j = 0; j < index.size(); j++) {

                int nodeindex = index.get(j);
                ObservableCachingLayout lll = (ObservableCachingLayout) vv.getGraphLayout();
                TreeNode found = null;
                for (int i = 0; i < lll.getGraph().getVertices().size(); i++) {
                    Object o = lll.getGraph().getVertices().toArray()[i];
                    if (o instanceof TreeNode) {
                        TreeNode t = (TreeNode) lll.getGraph().getVertices().toArray()[i];
                        if (t.getChildren().isEmpty() && t.getValue().contains("L") && t.getIndex() == nodeindex) {
                            found = t;
                            break;
                        }
                    }
                }

                List<LabelText> l = allLabels.get(found);

                if (index.size() != 1) {
                    convertColor = new Color(highlightColor.getRGB());
                    float[] hsv = new float[3];
                    hsv = Color.RGBtoHSB(tempColor.getRed(), tempColor.getGreen(), tempColor.getBlue(), hsv);

                    float weightJ = weight.get(j);
                    System.out.println(weightJ + " " + hsv[0] + " " + hsv[1] + " " + hsv[2]);
                    hsv[1] = hsv[1] * weightJ/max_weight;

                    int tempColorInt = Color.HSBtoRGB(hsv[0], hsv[1], hsv[2]);
                    convertColor = new Color(tempColorInt);
                    //System.out.println(convertColor.getRed() + " " + convertColor.getGreen() + " " + convertColor.getBlue());

                }

                
                 if (index.size() == 1) {
                        l.get(0).labelColor.add(tempColor);
                    } else {
                        l.get(0).labelColor.add(convertColor);
                    }

                     l.get(0).setHighlightFromLabelTopics(true);
                    
                    
               
//                for (labelText tempLT : l) {
//
//                    if (index.size() == 1) {
//                        tempLT.setRectColor(tempColor);
//                    } else {
//                        tempLT.setRectColor(convertColor);
//                    }
//
//                    tempLT.setHighlightFromLabelTopics(true);
//                }

            }
            currI++;

        }

        vv.repaint();

    }

    
    
    
    void buildLabelLocations() {

        allLabels.clear();
        ObservableCachingLayout lll = (ObservableCachingLayout) vv.getGraphLayout();

        for (int i = 0; i < lll.getGraph().getVertices().size(); i++) {
            //System.out.println(i);
            Object o = lll.getGraph().getVertices().toArray()[i];

            List<LabelText> tempList = new ArrayList<LabelText>();
            Point2D loc = lll.transform(lll.getGraph().getVertices().toArray()[i]);

            if (o instanceof TreeNode) {

                TreeNode t = (TreeNode) lll.getGraph().getVertices().toArray()[i];

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

                            if (!parent.b_readFromDB)
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
                            
                            if (parent.b_readFromDB)
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

                } else // Nodes situation
                {
                    //int labelsToDisplay2 = 5;
                }

            } else if (o instanceof DelegateTree) {
                TreeNode t = (TreeNode) (((DelegateTree) o).getRoot());

                int labelsToDisplay2 = labelsToDisplay / t.getNodeSize();
                //String[] temp = new String[labelsToDisplay2 * myTree.get(i).getNodeSize()];

                for (int k = 0; k < (labelsToDisplay2); k++) {
                    for (int j = 0; j < t.getTopicsContainedIdx().size(); j++) {

                        int index1 = t.getTopicsContainedIdx().get(j);
                        String t1[] = allTopics.get(index1 + parent.getGlobalReadIndex());
                        LabelText tempLT = new LabelText();

                        int index = index1;

                        int px = (int) loc.getX();
                        int py = (int) loc.getY();

                        int leng = t1[nodeStringStartIndex + k].length();
                        
                        
                        
                        if (wordTermIndex.get(t1[nodeStringStartIndex + k].toLowerCase()) == null) {
                            tempLT.setOccurance(1);
                            tempLT.setProbablity(99999.0f);
                        } else {

                            //System.out.println(t1[nodeStringStartIndex + k].toLowerCase());
                            int tmpCol = -1;
                            
                            if (!parent.b_readFromDB)
                            {
                                tmpCol = wordTermIndex.get(t1[nodeStringStartIndex + k].toLowerCase());
                            }
                            
                            if (occurances.get(index + 1)[k + 1] == 0) {
                                tempLT.setOccurance(1);
                            } else {
                                if (occurances.get(index + 1)[k + 1] >= 20) {
                                    tempLT.setOccurance(20);
                                } else {
                                    tempLT.setOccurance(occurances.get(index + 1)[k + 1]);
                                }
                            }
                            

                            float weight = 0;
                            
                            
                             if (parent.b_readFromDB)
                                weight = topkTermWeightMongo.get(index).get(j-1);
                            else
                                weight = Float.parseFloat(wordTermWeightsF.get(index)[tmpCol]);
                             
                             
                            tempLT.setProbablity(weight);

                        }

                        Font font = new Font("Arial", Font.PLAIN, labelFontSize);
                        font = new Font("Font", Font.PLAIN, occuranceFontSizePara * tempLT.getOccurance()/*occurances.get(index + 1)[j]*/ + labelFontSize);
                        tempLT.setFont(font);

                        //tempLT.setColor(Color.BLUE);
                        tempLT.isHighlighted = false;

                        tempLT.column = index;
                        //  tempLT.setRect(new Rectangle2D.Float(px + 20 + countleng, py - 10, leng * fontSizePerChar, 20));

                        tempLT.row = nodeStringStartIndex + k;

                        tempLT.setString(t1[nodeStringStartIndex + k]);
                        //   tempLT.posX = px + 20 + countleng + 2;
                        //   tempLT.posY = py + 5;
//
                        //    countleng += (leng * fontSizePerChar + 2);

                        tempList.add(tempLT);
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

                int countleng = 0;
                for (int kkk = 0; kkk < tempList.size(); kkk++) {
                    // System.out.println(tempList.get(kkk).getProbablity());

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

                    tempLT.setRect(new Rectangle2D.Float(px + 20 + countleng, py - 10, leng * fontSizePerChar, 20));

                    tempLT.posX = px + 20 + countleng + 2;
                    tempLT.posY = py + 5;

                    countleng += (leng * fontSizePerChar + 2);

                }

                //int countleng = 0;
//                for (int k = 0; k < (labelsToDisplay2); k++) {
//                    for (int j = 0; j < t.getTopicsContainedIdx().size(); j++) {
//
//
//                        int index1 = t.getTopicsContainedIdx().get(j);
//
//                        String t1[] = allTopics.get(index1 + 1);
//
//                        int leng = t1[nodeStringStartIndex + k].length();
//
//                        labelText tempLT = new labelText();
//
//                        int index = index1;
//                        int px = (int) loc.getX();
//                        int py = (int) loc.getY();
//
//                        // temp[count] = t1[nodeStringStartIndex + k];                            
//
//
//
//                        if (wordTermIndex.get(t1[nodeStringStartIndex + k].toLowerCase()) == null) {
//                            tempLT.setOccurance(1);
//                            tempLT.setProbablity(0.0f);
//                        } else {
//
//                            //System.out.println(t1[nodeStringStartIndex + k].toLowerCase());
//                            int tmpCol = wordTermIndex.get(t1[nodeStringStartIndex + k].toLowerCase());
//
//                            if (occurances.get(index + 1)[k + 1] == 0) {
//                                tempLT.setOccurance(1);
//                            } else {
//                                tempLT.setOccurance(occurances.get(index + 1)[k + 1]);
//                            }
//
//
//                            tempLT.setProbablity(Float.parseFloat(wordTermWeightsF.get(index)[tmpCol]));
//
//                            // System.out.println(" " + tmpCol + " " + tempLT.getProbablity());
//
//
//                        }
//
//
//                        Font font = new Font("Arial", Font.PLAIN, labelFontSize);
//                        font = new Font("Arial", Font.PLAIN, labelFontSize);
//                        font = new Font("Font", Font.PLAIN, occuranceFontSizePara * occurances.get(index + 1)[j] + labelFontSize);
//                        tempLT.setFont(font);
//                        tempLT.setColor(Color.BLUE);
//                        tempLT.isHighlighted = false;
//
//
//                        JLabel tempLabel = new JLabel();
//                        tempLabel.setFont(font);
//                        FontMetrics fm = tempLabel.getFontMetrics(font);
//                        int widthOfString = fm.stringWidth(" " + t1[nodeStringStartIndex + k] + " ");
//                        leng = widthOfString;
//
//                        tempLT.column = index;
//                        tempLT.setRect(new Rectangle2D.Float(px + 20 + countleng, py - 10, leng * fontSizePerChar, 20));
//
//                        
//                        tempLT.row = nodeStringStartIndex + k;
//
//                        tempLT.setString(t1[nodeStringStartIndex + k]);
//                        tempLT.posX = px + 20 + countleng + 2;
//                        tempLT.posY = py + 5;
//
//                        countleng += (leng * fontSizePerChar + 2);
//
//                        tempList.add(tempLT);
//                    }
//                }
                allLabels.put(t, tempList);

            }
        }

    }

    public List<LabelText> getTopicLabels(TreeNode t) {

        List<LabelText> r = allLabels.get(t);

        return r;

    }

    public List<LabelText> getHighligtedLabels() {

        List<LabelText> r = new ArrayList<LabelText>();

        for (List<LabelText> value : allLabels.values()) {
            for (int i = 0; i < value.size(); i++) {
                if (value.get(i).isHighlighted == true) {
                    r.add(value.get(i));
                }
            }
        }

        return r;

    }

    void updateLabelLocations() {

        if (b_showText == true) {
            for (List<LabelText> value : allLabels.values()) {
                for (int i = 0; i < value.size(); i++) {
                    value.get(i).isDisplayed = true;
                }
            }

        } else {
            for (List<LabelText> value : allLabels.values()) {
                for (int i = 0; i < value.size(); i++) {
                    value.get(i).isDisplayed = false;
                }
            }
        }

        ObservableCachingLayout lll = (ObservableCachingLayout) vv.getGraphLayout();

        for (int i = 0; i < lll.getGraph().getVertices().size(); i++) {
            Object o = lll.getGraph().getVertices().toArray()[i];

            if (o instanceof TreeNode) {
                TreeNode t = (TreeNode) o;
                Point2D loc = lll.transform(o);

                if (t.getChildren().isEmpty() && t.getValue().contains("L")) {

                    int index = t.getIndex();
                    int px = (int) loc.getX();
                    int py = (int) loc.getY();

                    int countleng = 0;
                    if (allLabels.get(t) != null) {
                        for (int j = 0; j < allLabels.get(t).size(); j++) {

                            LabelText tempLT = allLabels.get(t).get(j);

                            int leng = tempLT.s.length();

                            Font font;

                            //tempLT.setOccurance(1);
                            if (tempLT.isHighlighted == true) {

                                font = new Font(HelveticaFont, Font.BOLD, occuranceFontSizePara * tempLT.getOccurance()/*occurances.get(index + 1)[j]*/ + highlightFontSize);
                                tempLT.setFont(font);
                                tempLT.setStringColor(Color.BLACK);

                            } else {
                                font = new Font(HelveticaFont, Font.PLAIN, occuranceFontSizePara * tempLT.getOccurance()/*occurances.get(index + 1)[j]*/ + labelFontSize);
                                tempLT.setFont(font);
                                tempLT.setStringColor(Color.BLUE);

                            }

                            JLabel tempLabel = new JLabel();
                            tempLabel.setFont(font);
                            FontMetrics fm = tempLabel.getFontMetrics(font);
                            int widthOfString = fm.stringWidth(" " + tempLT.s + " ");
                            leng = widthOfString;

                            
                            if (j==0)
                                leng = 40;//widthOfString + 10;
                            //tempLT.column = index;
                            tempLT.setRect(new Rectangle2D.Float(px + 20 + countleng, py - 10, leng * fontSizePerChar, 20));

                            //tempLT.row = j ;
                            //tempLT.setString(t.getNodeTopics()[j]);
                            tempLT.posX = px + 20 + countleng + 2;
                            tempLT.posY = py + 5;

                            countleng += (leng * fontSizePerChar + 2);

                        }

                    }

                }
            } else // Nodes situation
            {

                TreeNode t = (TreeNode) ((DelegateTree) o).getRoot();
                Point2D loc = lll.transform(o);
                int index = t.getIndex();
                int px = (int) loc.getX();
                int py = (int) loc.getY();

                if (allLabels.get(t) != null) {

                    int countleng = 0;
                    for (int j = 0; j < allLabels.get(t).size(); j++) {

                        LabelText tempLT = allLabels.get(t).get(j);

                        int leng = tempLT.s.length();

                        Font font;

                        tempLT.setOccurance(1);
                        if (tempLT.isHighlighted == true) {

                            font = new Font(HelveticaFont, Font.BOLD, occuranceFontSizePara * tempLT.getOccurance()/*occurances.get(index + 1)[j]*/ + highlightFontSize);
                            tempLT.setFont(font);
                            tempLT.setStringColor(Color.BLACK);

                        } else {
                            font = new Font(HelveticaFont, Font.PLAIN, occuranceFontSizePara * tempLT.getOccurance()/*occurances.get(index + 1)[j]*/ + labelFontSize);
                            tempLT.setFont(font);
                            tempLT.setStringColor(Color.BLUE);

                        }

                        JLabel tempLabel = new JLabel();
                        tempLabel.setFont(font);
                        FontMetrics fm = tempLabel.getFontMetrics(font);
                        int widthOfString = fm.stringWidth(" " + tempLT.s + " ");
                        leng = widthOfString;

                        //tempLT.column = index;
                        tempLT.setRect(new Rectangle2D.Float(px + 20 + countleng, py - 10, leng * fontSizePerChar, 20));

                        //tempLT.row = j ;
                        //tempLT.setString(t.getNodeTopics()[j]);
                        tempLT.posX = px + 20 + countleng + 2;
                        tempLT.posY = py + 5;

                        countleng += (leng * fontSizePerChar + 2);

                    }
                }
            }

        }

    }

    public List<LabelText> retriveLabelsOfNode(TreeNode t, int timecolumn, List<List<int[]>> tYK) {
        List<LabelText> r = new ArrayList<LabelText>();

        if (t.getChildren().isEmpty()) {

            List<LabelText> ltl = allLabels.get(t);
            //System.out.println(t);
            int[] index = tYK.get(t.getIndex()).get(timecolumn);

            for (int j = 0; j < index.length; j++) {

                if ((index[j]) < (labelsToDisplay - 2)) {
                    r.add(ltl.get(index[j] + 1));
                }
            }
        } else {

            //System.out.println("single here" );
            HashMap<TreeNode, List<LabelText>> highlightedOfthisNode = new HashMap<TreeNode, List<LabelText>>();
            // System.out.println("single here" );
            highLightByYearIdxKwNode(t, timecolumn, tYK, highlightedOfthisNode);

            //System.out.println("highlight over");
            r = putUpHighlightedKeywordList(highlightedOfthisNode);
            //System.out.println("putUp over");                                    
        }
        return r;

    }

    List<LabelText> putUpHighlightedKeywordList(HashMap<TreeNode, List<LabelText>> m) {
        List<LabelText> r = new ArrayList<LabelText>();

        int size = m.size();

        if (size != 0)
        {
        int maxkeywordnumber = 50;

        int iterationRuns = maxkeywordnumber / size;

        //System.out.println(size + " " + iterationRuns);
        if (iterationRuns < 1) {
            iterationRuns = 1;
        }

        if (iterationRuns > 5) {
            iterationRuns = 5;
        }

        for (int i = 0; i < iterationRuns; i++) {

            for (Object o : m.values()) {

                if (i < ((List<LabelText>) o).size()) {
                    LabelText k = ((List<LabelText>) o).get(i);

                    r.add(k);
                }
            }
        }

        return r;
        }
        else return null;
    }

//    public HashMap< customLabelTimecolumnKey, HashMap<TreeNode, List<labelText>>> buildLabelMap(TreeNode currentNode, List<List<int[]>> tYK) {
//
//        HashMap< customLabelTimecolumnKey, HashMap<TreeNode, List<labelText>>> hmp = new HashMap< customLabelTimecolumnKey, HashMap<TreeNode, List<labelText>>>();
//
//        int numberofyears = parent.getTemporalFrame().getData().getNumOfYears();
//
//        ObservableCachingLayout lll = (ObservableCachingLayout) vv.getGraphLayout();
//
//        for (int i = 0; i < numberofyears; i++) {
//            if (currentNode.getChildren().isEmpty()) {
//                TreeNode tk = currentNode;
//                customLabelTimecolumnKey key = new customLabelTimecolumnKey(tk, i);
//
//                HashMap<TreeNode, List<labelText>> tmpsubmap = new HashMap<TreeNode, List<labelText>>();
//            
//                List<labelText> tmpLabels = retriveLabelsOfNode(tk, i, tYK);
//            
//                tmpsubmap.put(tk, tmpLabels);
//
//                hmp.put(key, tmpsubmap);
//            } else {
//                TreeNode tk = (TreeNode) currentNode;
//                 customLabelTimecolumnKey key = null;
//                if (tk==null)
//                {
//                    tk = currentNode; //attachedpanel
//                    key = new customLabelTimecolumnKey(tk, i);                                        
//                }
//                
//                for (int k=0; k<tk.getChildren().size(); k++)
//                {
//                    
//                }
//                
//                                  
//                    
//                    HashMap<TreeNode, List<labelText>> tmpsubmap = new HashMap<TreeNode, List<labelText>>();
//                    
//                    for (int m = 0; m < tk.getTopicsContainedIdx().size(); m++) {
//
//                        int leafIndex = tk.getTopicsContainedIdx().get(m);
//
//                        TreeNode t1 = null;
//                        String matchedNodeString = "LeafTopic" + leafIndex;
//                        
//
//                        for (int ii = 0; ii < lll.getGraph().getVertices().size(); i++) {
//
//                            Object o = lll.getGraph().getVertices().toArray()[ii];
//
//                            t1 = (TreeNode) o;
//                            if (t1.getValue().equals(matchedNodeString)) {
//                                break;
//                            }
//                        }
//                        
//                        
//                        List<labelText> tmpLabels = retriveLabelsOfNode(t1, i, tYK);
//           
//                        tmpsubmap.put(t1, tmpLabels);
//
//                    }
//                    
//                     hmp.put(key, tmpsubmap);
//
//                
//            }
//
//        }
//
//        return hmp;
//        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
    public HashMap< customLabelTimecolumnKey, List<LabelText>> buildLabelMap(TreeNode cNode, List<List<int[]>> tYK) {

        HashMap< customLabelTimecolumnKey, List<LabelText>> hmp = new HashMap< customLabelTimecolumnKey, List<LabelText>>();
        ObservableCachingLayout lll = (ObservableCachingLayout) vv.getGraphLayout();

        System.out.println("building label maps in " + this.getName());

        int numberofyears = parent.getTemporalFrame().getData().getNumOfYears();
//        System.out.println("numberofyears" + numberofyears);
//        System.out.println(tYK.size())             ;
//        System.out.println(tYK.get(0).size());
//        System.out.println(tYK.get(0).get(0).length);
        TreeNode tk = cNode;
        System.out.println(tk.getValue() + "is building" + tk.getChildren().size() + " children");

        for (int i = 0; i < numberofyears; i++) {

            customLabelTimecolumnKey key = new customLabelTimecolumnKey(tk.getValue(), String.valueOf(i));

            List<LabelText> tmpLabels = retriveLabelsOfNode(tk, i, tYK);
            // System.out.println(tmpLabels.size());
            hmp.put(key, tmpLabels);
                //System.out.println(key.toString());

        }

         if (!tk.getChildren().isEmpty())
        for (int i = 0; i < numberofyears; i++) {

//            if (tk.getChildren().isEmpty()) {
//                //System.out.println("single leaf" + i);
//                customLabelTimecolumnKey key = new customLabelTimecolumnKey(tk.getValue(), i);
//
//                List<labelText> tmpLabels = retriveLabelsOfNode(tk, i, tYK);
//
//                hmp.put(key, tmpLabels);
//
//            } else 
            {
//                List<labelText> tmpLabels = retriveLabelsOfNode(tk, i, tYK);
//                    // System.out.println("single node" + i);
//                     
//                    customLabelTimecolumnKey key = new customLabelTimecolumnKey(tk, i);
//                    hmp.put(key, tmpLabels);

                for (int j = 0; j < tk.getChildren().size(); j++) {
                    TreeNode t1 = (TreeNode) tk.getChildren().get(j);
                    //TreeNode t1 = null;
//                    String matchedNodeString = "LeafTopic" + tk.getTopicsContainedIdx().get(j);
//
//                    for (int k = 0; k < lll.getGraph().getVertices().size(); k++) {
//                        Object o = lll.getGraph().getVertices().toArray()[k];
//                        t1 = (TreeNode) o;
//                        if (t1.getValue().equals(matchedNodeString)) {
//                            break;
//                        }
//                    }

                    //System.out.println("single node" + i);
                    List<LabelText> tmpLabels = retriveLabelsOfNode(t1, i, tYK);
                    //System.out.println("single node" + tmpLabels.size());

                    customLabelTimecolumnKey key = new customLabelTimecolumnKey(t1.getValue(), String.valueOf(i));
                    hmp.put(key, tmpLabels);

                }
            }

        }

        System.out.println("hmp size " + hmp.size());

        return hmp;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

//    void updateLabelLocations() {
//        
//        
//         ObservableCachingLayout lll = (ObservableCachingLayout) vv.getGraphLayout();
//
//        for (int i = 0; i < lll.getGraph().getVertices().size(); i++) {
//            Object o = lll.getGraph().getVertices().toArray()[i];
//            if (o instanceof TreeNode) {
//
//
//                TreeNode t = (TreeNode) lll.getGraph().getVertices().toArray()[i];
//                Point2D loc = lll.transform(lll.getGraph().getVertices().toArray()[i]);
//
//                if (t.getChildren().isEmpty() && t.getValue().contains("L")) {
//
//
//
//                    int index = t.getIndex();
//                    int px = (int) loc.getX();
//                    int py = (int) loc.getY();
//
//                    int countleng = 0;
//                    for (int j = 1; j < labelsToDisplay; j++) {
//                        int leng = t.getNodeTopics()[j].length();
//
//                        Font font = new Font("Arial", Font.PLAIN, labelFontSize);
//
////                        FontMetrics fm= magnifyLabel.getFontMetrics(font);
////                        int width=fm.stringWidth(" " + labeltexts[highlightedColumn][highlightedRow].s + " ");
//                        if (labelIsSelected[index][j - 1]) {
//                            font = new Font(Font.SERIF, Font.BOLD, highlightFontSize);
//                            font = new Font(Font.SERIF, Font.BOLD, occuranceFontSizePara * occurances.get(index + 1)[j] + highlightFontSize);
//                            labeltexts[index][j - 1].setFont(font);
//                            labeltexts[index][j - 1].setColor(Color.BLACK);
//                            labeltexts[index][j - 1].isHighlighted = true;
//
//                        } else {
//                            font = new Font("Arial", Font.PLAIN, labelFontSize);
//                            font = new Font("Font", Font.PLAIN, occuranceFontSizePara * occurances.get(index + 1)[j] + labelFontSize);
//                            labeltexts[index][j - 1].setFont(font);
//                            labeltexts[index][j - 1].setColor(Color.BLUE);
//                            labeltexts[index][j - 1].isHighlighted = false;
//
//                        }
//
//                        JLabel tempLabel = new JLabel();
//                        tempLabel.setFont(font);
//                        FontMetrics fm = tempLabel.getFontMetrics(font);
//                        int widthOfString = fm.stringWidth(" " + t.getNodeTopics()[j] + " ");
//                        leng = widthOfString;
//
//
//
//                        labeltexts[index][j - 1].column = index;
//                        labeltexts[index][j - 1].setRect(new Rectangle2D.Float(px + 20 + countleng, py - 10, leng * fontSizePerChar, 20));
//
//
//
//                        labeltexts[index][j - 1].column = index;
//                        labeltexts[index][j - 1].row = j - 1;
//
//
//
//                        labeltexts[index][j - 1].setString(t.getNodeTopics()[j]);
//                        labeltexts[index][j - 1].posX = px + 20 + countleng + 2;
//                        labeltexts[index][j - 1].posY = py + 5;
//
//                        countleng += (leng * fontSizePerChar + 2);
//                    }
//                    
//                    
//                    
////        ObservableCachingLayout lll = (ObservableCachingLayout) vv.getGraphLayout();
////
////        for (int i = 0; i < lll.getGraph().getVertices().size(); i++) {
////            Object o = lll.getGraph().getVertices().toArray()[i];
////            if (o instanceof TreeNode) {
////
////
////                TreeNode t = (TreeNode) lll.getGraph().getVertices().toArray()[i];
////                Point2D loc = lll.transform(lll.getGraph().getVertices().toArray()[i]);
////
////                if (t.getChildren().isEmpty() && t.getValue().contains("L")) {
////
////
////
////                    int index = t.getIndex();
////                    int px = (int) loc.getX();
////                    int py = (int) loc.getY();
////
////                    int countleng = 0;
////                    for (int j = 1; j < labelsToDisplay; j++) {
////                        int leng = t.getNodeTopics()[j].length();
////
////                        Font font = new Font("Arial", Font.PLAIN, labelFontSize);
////
//////                        FontMetrics fm= magnifyLabel.getFontMetrics(font);
//////                        int width=fm.stringWidth(" " + labeltexts[highlightedColumn][highlightedRow].s + " ");
////                        if (labelIsSelected[index][j - 1]) {
////                            font = new Font(Font.SERIF, Font.BOLD, highlightFontSize);
////                            font = new Font(Font.SERIF, Font.BOLD, occuranceFontSizePara * occurances.get(index + 1)[j] + highlightFontSize);
////                            labeltexts[index][j - 1].setFont(font);
////                            labeltexts[index][j - 1].setColor(Color.BLACK);
////                            labeltexts[index][j - 1].isHighlighted = true;
////
////                        } else {
////                            font = new Font("Arial", Font.PLAIN, labelFontSize);
////                            font = new Font("Font", Font.PLAIN, occuranceFontSizePara * occurances.get(index + 1)[j] + labelFontSize);
////                            labeltexts[index][j - 1].setFont(font);
////                            labeltexts[index][j - 1].setColor(Color.BLUE);
////                            labeltexts[index][j - 1].isHighlighted = false;
////
////                        }
////
////                        JLabel tempLabel = new JLabel();
////                        tempLabel.setFont(font);
////                        FontMetrics fm = tempLabel.getFontMetrics(font);
////                        int widthOfString = fm.stringWidth(" " + t.getNodeTopics()[j] + " ");
////                        leng = widthOfString;
////
////
////
////                        labeltexts[index][j - 1].column = index;
////                        labeltexts[index][j - 1].setRect(new Rectangle2D.Float(px + 20 + countleng, py - 10, leng * fontSizePerChar, 20));
////
////
////
////                        labeltexts[index][j - 1].column = index;
////                        labeltexts[index][j - 1].row = j - 1;
////
////
////
////                        labeltexts[index][j - 1].setString(t.getNodeTopics()[j]);
////                        labeltexts[index][j - 1].posX = px + 20 + countleng + 2;
////                        labeltexts[index][j - 1].posY = py + 5;
////
////                        countleng += (leng * fontSizePerChar + 2);
////                    }
//
//        
//        
//        
//        
//        /////
//        
//        
//        
//        
//
////                                 if (bMagnified && index == highlightedColumn)
////                                {
////                                    
////                                   
////                                    
////                                    for (int j = 1; j < labelsToDisplay; j++) 
////                                    {
////                                        
////                                        
////                                        
////                                        
////                                    }
////                                    
////                                    
////                                    countleng = 0;
////                                for (int j = 1; j < labelsToDisplay; j++) {
////                                     int tempSize = Math.abs((j-1)-highlightedRow);
////                                     
////                                     
////                                     
////                                    int leng = t.getNodeTopics()[j].length();
////                                    
////                                     labeltexts[index][j-1].column = index;
////                                    labeltexts[index][j-1].setRect(new Rectangle2D.Float(px+10+countleng, py - 10, leng * fontSizePerChar, 20));
////                                    
////                                    labeltexts[index][j-1].row = j-1;
////                                    
////                                                                         
////                                        Font font = new Font("Arial", Font.PLAIN, 12*);
////                                        labeltexts[index][j-1].setFont(font);
////                                        labeltexts[index][j-1].setColor(Color.BLUE);
////                                        
////                                        
////                                    
////
////                                    labeltexts[index][j-1].setString(t.getNodeTopics()[j]);
////                                    labeltexts[index][j-1].posX = px + 10 + countleng + 2;
////                                    labeltexts[index][j-1].posY =  py + 5;
////                                    
////                                    countleng += (leng * fontSizePerChar + 2);
////                                }
////                                }
//
//
//
//
//
//                }
//
//            }
//            else if (o instanceof DelegateTree)
//            {
//                
//                
//                
//                
//            }
//        }
//
//    }
    protected EdgeWeightStrokeFunction<MyLink> ewcs;// = new EdgeWeightStrokeFunction<MyLink>();

    private final static class EdgeWeightStrokeFunction<E> implements Transformer<E, Stroke> {

        public boolean showHighlighted = false;
        protected final Stroke basic = new BasicStroke(1);
        protected final Stroke heavy = new BasicStroke(2);
        protected final Stroke dotted = RenderContext.DOTTED;
        protected final Stroke dashed = RenderContext.DASHED;
        float dash[] = {10.0f};

//                    Transformer<MyLink, Stroke> edgeStroke = new Transformer<MyLink, Stroke>() {
//   }
//            public Stroke transform(MyLink i) {
//             
//                if(i.weight<= 20)
//                    return dotted;
//                else if (i.weight>20 && i.weight<=49)
//                    return dashed;
//                else
//                //    return basic;
//                return heavy;//new BasicStroke(1.0f, BasicStroke.CAP_BUTT,                   BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
//               // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//            }
//        };
        public Stroke transform(E i) {
            if (i instanceof MyLink) {
                if (showHighlighted) {
                    MyLink l = (MyLink) i;
                    if (l.weight <= totalEdges / 3) {
                        return heavy;
                    } else if (l.weight > totalEdges / 3 && l.weight <= totalEdges / 3 * 2) {
                        return new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);//dashed;
                    } else {
                        return dotted;
                    }
                } else {
                    return basic;
                }
            }

            return null;
        }
    }
    private List<Float[]> colorMap;

    public void setColorMap() {
        colorMap = new ArrayList<Float[]>();
        try {
            colorMap = this.parent.getNumericalColors();
        } catch (Exception ex) {
            System.out.println("TopicDisplay getColor failed");
        }
    }
    private List<String[]> allTopics;

    public void loadTopic(List<String[]> topics) throws IOException {

        allTopics = topics;
        extractFrequency();


    }

    public void UpdateTopicGraphicView(Dimension size) {
        width = size.width;
        height = size.height;
        

        repaintView();
    }

    public void repaintView() {

        bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        curg2d = bi.createGraphics();
        curg2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        update(curg2d);
        this.repaint();
    }
    private List<String[]> reorganizedTopics;
    private List<int[]> occurances;
    //private List<List<Dimension>> otherOccurances;

    private void extractFrequency() {
        //Re-organize topics based on the similarities
        reorganizedTopics = new ArrayList<String[]>();                 
         
        reorganizedTopics.add(allTopics.get(0));
        for (int i = parent.getGlobalReadIndex(); i < allTopics.size(); i++) {
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
            
                                 
            for (int i = parent.getGlobalReadIndex(); i < reorganizedTopics.size(); i++) {
                for (int j = parent.getGlobalReadIndex()+1; j < reorganizedTopics.get(i).length/*30*/; j++) {
                    //Compare every word with other words
                    keyPos = new Dimension(i, j);
                    for (int m = parent.getGlobalReadIndex(); m < reorganizedTopics.size(); m++) {
                        for (int n = parent.getGlobalReadIndex()+1; n < /*30*/reorganizedTopics.get(m).length; n++) {
                            if (m == i && n == j) {
                                //Skip the word itself
                            } else {
                                if (reorganizedTopics.get(i)[j].trim().equalsIgnoreCase(reorganizedTopics.get(m)[n].trim())) {
                                    count++;
//                                    tempPos = new Dimension(m, n);
//                                    if (count == 2) {//no dimension array has been created for the current word
//                                        tmpDim = new ArrayList<Dimension>();
//                                        tmpDim.add(keyPos);
//                                    }
//                                    tmpDim.add(tempPos);
                                }
                            }
                        }
                    }
//                    if (tmpDim != null) {
//                        otherOccurances.add(tmpDim);
//                    }
                    
                    occurances.get(i)[j] = count;
                    
//                    if (tmpDim != null) {
//                        for (int q = 0; q < tmpDim.size(); q++) {
//                            occurances.get(tmpDim.get(q).width)[tmpDim.get(q).height] = count;
//                        }
//                    }
//                    tmpDim = null;
                    count = 1;
                }
            }
        }
        
       
        
        
        
        
        
        System.out.println("Words that show up more than twice: " + occurances.size() + "in topic view");
    }
    private JPanel[] panels;

//    private void buildTopicLabels(List<Integer> sequence) {
//        if (sequence != null) {
//
//            int numOfTopics = 0, numOfWords = 0;
//
//            if (allTopics != null) {
//                numOfTopics = allTopics.size();//first line is the header
//                numOfWords = allTopics.get(0).length;
//                panels = new JPanel[numOfTopics];//first line is the header
//                labels = new JLabel[numOfTopics][numOfWords];
//            }
//
//            GridBagLayout gbag = new GridBagLayout();
//            GridBagConstraints gbc = new GridBagConstraints();
//            gbc.fill = GridBagConstraints.BOTH;
//            gbc.anchor = java.awt.GridBagConstraints.WEST;
//            gbc.weightx = 1.0;
//            gbc.weighty = 1.0;
//            gbc.gridwidth = GridBagConstraints.REMAINDER;
//            gbc.gridheight = 1;
//            gbc.insets = new Insets(0, 0, 0, 0);
//            //mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
//            //  mainPanel.setLayout(gbag);
//
//            int tmpi = 0;
//            panels[tmpi] = new JPanel();
//            //gbag.setConstraints(panels[i], gbc);
//            panels[tmpi].setLayout(new FlowLayout(FlowLayout.LEFT));
//            //panels[tmpi].setPreferredSize(new Dimension(428, 32));
//            panels[tmpi].setPreferredSize(new Dimension(856, 62));//for demo
//            for (int k = 1; k < numOfWords; k++) {
//                labels[0][k] = new JLabel(allTopics.get(0)[k]);
//                Font ff = new Font("Font", Font.PLAIN, 3 * occurances.get(0)[k] + 12);
//                //Font ff = new Font("Font", Font.PLAIN, 5 * occurances.get(0)[k] + 32);//for demo
//                labels[0][k].setFont(ff);
//                labels[0][k].setName(Integer.toString(tmpi) + "," + Integer.toString(k));
//                //     labels[0][k].addMouseListener(this);
//                panels[0].add(labels[0][k]);
//            }
//            //    mainPanel.add(panels[0], gbc);
//
//            //  setColorMap();
//            //   panelBckColors = new ArrayList<Color>();
//            for (int i = 1; i < numOfTopics; i++) {//first line is the header
//                //int tmpid = sequence.get(i-1)+1;
//
//                panels[i] = new JPanel();
//                panels[i].setLayout(new FlowLayout(FlowLayout.LEFT));
//                //panels[i].setPreferredSize(new Dimension(428, 32));
//                panels[tmpi].setPreferredSize(new Dimension(856, 62));//for demo
//                /**
//                 * sequence is 1 less in size than numOfTopics since numOfTopics
//                 * contains the header
//                 */
//                for (int j = 1; j < numOfWords; j++) {//Skip the first word every time
//                    labels[i][j] = new JLabel(reorganizedTopics.get(i)[j]);//Based on re-ordered topics
//                    //Font f = new Font("Font", Font.PLAIN, 2 * occurances.get(i)[j] + 12);
//                    Font f = new Font("Font", Font.PLAIN, 3 * occurances.get(i)[j] + 10);//For demo
//                    labels[i][j].setFont(f);
//                    labels[i][j].setName(Integer.toString(i) + "," + Integer.toString(j));
//                    //labels[i][j] = new JLabel(allTopics.get(sequence.get(i - 1) + 1)[j]);
//                    //Font f = new Font("Font", Font.PLAIN, 3 * occurances.get(sequence.get(i - 1) + 1)[j] + 12);
////                    labels[i][j].setFont(f);
////                    labels[i][j].setName(Integer.toString(sequence.get(i - 1) + 1) + "," + Integer.toString(j));
//                    //     labels[i][j].addMouseListener(this);                    
//                    panels[i].add(labels[i][j]);
//                }
//                try {
//                    //    Color tmpColor = Color.getHSBColor(colorMap.get(i-1)[1], (float) (colorMap.get(i - 1)[2]-0.4), colorMap.get(i-1)[3]);
//                    //Color tmpColor = new Color(colorMap.get(i-1)[1], colorMap.get(i - 1)[2], colorMap.get(i-1)[3], (float) 0.3);
//                    //     panels[i].setBackground(tmpColor);
//                    //    panelBckColors.add(tmpColor);
//                } catch (Exception e) {
//                    System.out.println("Can't assign colors to topic panels");
//                }
//
//            }
//
//        }
//
//    }

//    private JPanel buildLabel(TreeNode t) {
//
//        JPanel pan1 = new JPanel();
////       GridBagLayout gbag = new GridBagLayout();
////      GridBagConstraints gbc = new GridBagConstraints();
////        gbc.fill = GridBagConstraints.BOTH;
////        gbc.anchor = java.awt.GridBagConstraints.WEST;
////        gbc.weightx = 1.0;
////        gbc.weighty = 1.0;
////        gbc.gridwidth = GridBagConstraints.REMAINDER;
////        gbc.gridheight = 1;
////        gbc.insets = new Insets(0,0,0,0);
////        gbag.setConstraints(pan1, gbc);
//
//        pan1.setLayout(new FlowLayout(FlowLayout.LEFT));
//        pan1.setPreferredSize(new Dimension(100, 10));//for demo
//        for (int k = 1; k < 5; k++) {
//            String[] labelText = t.getNodeTopics();
//
//            JLabel tempLabel = new JLabel(labelText[k + 1]);
//
//            Font ff = new Font("Font", Font.PLAIN, 10);
//
//            tempLabel.setFont(ff);
//            tempLabel.setName(Integer.toString(k));
//
//            pan1.add(tempLabel);
//        }
//
//        return pan1;
//    }

//     public void paint(Graphics g) {
//			g.setColor(Color.lightGray);
//		
//			Graphics2D g2d = (Graphics2D)g;
//			Point2D center = HtreeLayout.getCenter();
//
//			Ellipse2D ellipse = new Ellipse2D.Double();
//                        
////                         g2d.drawLine( 0, height/3, (int) width, height/3);
////         g2d.drawLine( 0, height/3*2, (int) width, height/3*2);
////         g2d.drawLine(width/2, 0, width/2, height);
////         
//         
////			for(double d : depths) {
////				ellipse.setFrameFromDiagonal(center.getX()-d, center.getY()-d, 
////				Shape shape = vv.getRenderContext().
////						getMultiLayerTransformer().getTransformer(Layer.LAYOUT).transform(ellipse);
////				g2d.draw(shape);
////			}
//		}
    public boolean useTransform() {
        return true;
    }

    public final class HorizontalOverlappingTreeLayout<V, E> extends TreeLayout<V, E> {

        private int tempX, tempY;

        public Map<V, Point2D> getLocations() {
            return this.locations;

        }

        public HorizontalOverlappingTreeLayout(Forest<V, E> g, int distX, int distY) {
//        super(g);
//        this.distX = distX;
//        this.distY = distY;
//        buildTree();
            super(g, distX, distY);

//        if (g == null)
//            throw new IllegalArgumentException("Graph must be non-null");
//        if (distX < 1 || distY < 1)
//            throw new IllegalArgumentException("X and Y distances must each be positive");
//        
//        this.distX = distX;
//        this.distY = distY;
//        buildTree();
        }

//        public HorizontalOverlappingTreeLayout(Forest<V, E> g) {
//            super(g);
//
//        }
        //@Override
        @Override
        protected void buildTree() {
            // this.distX = 200;
//         this.distX = tempX;
//        this.distY = tempY;
            this.m_currentPoint = new Point(0, 20);
            Collection<V> roots = TreeUtils.getRoots(graph);

            if (roots.size() > 0 && graph != null) {
                calculateDimensionY(roots);
                for (V v : roots) {
                    calculateDimensionY(v);
                    m_currentPoint.y += this.basePositions.get(v) / 2 + this.distY;
                    buildTree(v, this.m_currentPoint.y);

                }

                
                //edit children locations
                if (!alreadyDone.isEmpty()) {

                    Iterator<V> it = alreadyDone.iterator();

                    while (it.hasNext()) {
                        V v = it.next();

                        double Y = this.locations.get(v).getY();
                        double X = this.locations.get(v).getX();

                        TreeNode t = (TreeNode) v;

                        // System.out.println(t.getValue() + "   " + t.getChildren().size());
                        if (graph.getSuccessors(v) != null) {
                            if (!graph.getSuccessors(v).isEmpty()) {

                                //System.out.println("here1");
                                boolean alterLocations = true;
                                for (V element : graph.getSuccessors(v)) {

                                    //System.out.println("here2");
                                    if (!graph.getSuccessors(element).isEmpty()) {
                                        alterLocations = false;
                                        break;
                                    }

                                }

                                // System.out.println("here3");
                                if (alterLocations == true) {

                                    //System.out.println("here4");
                                    int reducedsize = -10;//distY / 40;

                                    int sizeOfChild = graph.getSuccessors(v).size();

                                    int startLocation = (int) Y - this.basePositions.get(v) / 2;

                                    int distanceBetweenChildren = (this.basePositions.get(v) - reducedsize * 2) / sizeOfChild;

                                    int lastY = startLocation + reducedsize;

                                    for (V element : graph.getSuccessors(v)) {

                                        //System.out.println("here5");
                                        double tempx = this.locations.get(element).getX();
                                        double tempy = this.locations.get(element).getY();
                                        this.locations.get(element).setLocation(tempx, lastY);
                                        lastY += distanceBetweenChildren;

                                    }

                                }

                            }
                        }
                    }

                }

//                      if ( !alreadyDone.isEmpty()) {
//                   
//                          Iterator<V> it = alreadyDone.iterator();
//                          
//                          while(it.hasNext())
//                          {
//                           V v = it.next();
//                           
//                        double Y = this.locations.get(v).getY();
//                      int level = ((TreeNode)v).getLevel()*this.distX ;
//                      if (((TreeNode)v).getChildren().isEmpty())
//                        level = 4*this.distX ;
//                        
//                        
//                        this.locations.get(v).setLocation(level, Y);
//                          }
//                    
//                }
//                
            }
            // TODO: removed code here
        }

//         protected void calculateDimensionX(V v)
//        {
//            double Y = this.locations.get(v).getY();
//             if (v instanceof TreeNode)
//                    {
//                        int level = ((TreeNode)v).getLevel();
//                        levelx = level*this.distX + this.m_currentPoint.x;
//                        
//                    }
//            
//            
//        }
        // @Override
        @Override
        protected void buildTree(V v, int y) {
            if (!alreadyDone.contains(v)) {
                alreadyDone.add(v);

                // go one level further down
                this.m_currentPoint.x += this.distX;
                // this.m_currentPoint.x = lex;
                this.m_currentPoint.y = y;

                this.setCurrentPositionFor(v);

                int sizeYofCurrent = basePositions.get(v);

                int lastY = y - sizeYofCurrent / 2;

                int sizeYofChild;
                int startYofChild;

                for (V element : graph.getSuccessors(v)) {
                    sizeYofChild = this.basePositions.get(element);

                    startYofChild = lastY + sizeYofChild / 2;
                    buildTree(element, startYofChild);

                    lastY = lastY + sizeYofChild + distY;

                }
                this.m_currentPoint.x -= this.distX;
            }
        }

        private int calculateDimensionY(V v) {
            int size = 0;
            int childrenNum = graph.getSuccessors(v).size();

            if (childrenNum != 0) {
                for (V element : graph.getSuccessors(v)) {
                    size += calculateDimensionY(element) + distY;
                }
            }

            size = Math.max(0, size - distY);
            basePositions.put(v, size);
            //System.out.println( ((TreeNode) v).getValue() + " " + ((TreeNode) v).getChildren().size() + " size is " + size);
            return size;
        }

        private int calculateDimensionY(Collection<V> roots) {
            int size = 0;
            for (V v : roots) {
                int childrenNum = graph.getSuccessors(v).size();

                if (childrenNum != 0) {
                    for (V element : graph.getSuccessors(v)) {
                        size += calculateDimensionY(element) + distY;

                    }
                } else {

                    size += calculateDimensionY(v) + distY;

                }

                size = Math.max(0, size - distY);
                basePositions.put(v, size);
            }

            return size;
        }
    }

    class Rings implements VisualizationServer.Paintable {

        Collection<Double> depths;

        public Rings() {
            depths = getDepths();
        }

        private Collection<Double> getDepths() {
            Set<Double> depths = new HashSet<Double>();
            Map<Object, PolarPoint> polarLocations = radialLayout.getPolarLocations();
            for (Object v : gh.getVertices()) {
                PolarPoint pp = polarLocations.get(v);
                depths.add(pp.getRadius());
            }
            return depths;
        }

        public void paint(Graphics g) {
            g.setColor(Color.lightGray);

            Graphics2D g2d = (Graphics2D) g;
            Point2D center = radialLayout.getCenter();

            Ellipse2D ellipse = new Ellipse2D.Double();
            for (double d : depths) {
                ellipse.setFrameFromDiagonal(center.getX() - d, center.getY() - d,
                        center.getX() + d, center.getY() + d);
                Shape shape = vv.getRenderContext().
                        getMultiLayerTransformer().getTransformer(Layer.LAYOUT).transform(ellipse);
                g2d.draw(shape);
            }
        }

        public boolean useTransform() {
            return true;
        }
    }

    /**
     * a demo class that will create a vertex shape that is either a polygon or
     * star. The number of sides corresponds to the number of vertices that were
     * collapsed into the vertex represented by this shape.
     *
     * @author Tom Nelson
     *
     * @param <V>
     */
    class ClusterVertexShapeFunction<Object> extends EllipseVertexShapeTransformer<Object> {

        ClusterVertexShapeFunction() {
            // setSizeTransformer(new ClusterVertexSizeFunction<Object>(20));
        }

        @SuppressWarnings("unchecked")
        @Override
        public Shape transform(Object v) {
            if (v instanceof Graph) {
                //int size = ((Graph) v).getVertexCount();
//                if (size < 8) {
//                    int sides = Math.max(size, 3);
//
//                    return factory.getRegularPolygon(v, sides);
//                } else {
//                    return factory.getRegularStar(v, size);
//                }
            }
            return super.transform(v);
        }
    }

    /**
     * A demo class that will make vertices larger if they represent a collapsed
     * collection of original vertices
     *
     * @author Tom Nelson
     *
     * @param <V>
     */
    class ClusterVertexSizeFunction<Object> implements Transformer<Object, Shape> {

        int size;

        public ClusterVertexSizeFunction(Integer size) {
            this.size = size;
        }

        public Shape transform(Object v) {
            Shape s;
            if (v instanceof TreeNode) {
                TreeNode t = (TreeNode) v;
                if (!t.getChildren().isEmpty()) {

                    int size2 = t.getNodeSize() * 2;
                    s = new Ellipse2D.Double(-size2 / 2, -size2 / 2, size2, size2);

                    vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
                } else {

                    s = new Ellipse2D.Double(-1, -1, 2, 2);
                    vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.E);
                }

                return s;
            }
            return null;

        }
    }

    class myAnnotationMenu extends JPopupMenu implements ActionListener {

        private JTextField textField;
//        private JButton okButton;
        private Point2D p;

        public void setMouseLocation(Point p1) {
            p = p1;
        }

        public myAnnotationMenu() {

            textField = new JTextField("Enter annotation");

            textField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    textField.setText("");

                }

                public void focusLost(FocusEvent e) {
                    textField.setText("Enter annotation");
                }
            });

            textField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        GraphElementAccessor<Object, MyLink> pickSupport = vv.getPickSupport();

                        Object tempvertex = pickSupport.getVertex(vv.getModel().getGraphLayout(), p.getX(), p.getY());

                        if (tempvertex instanceof TreeNode) {
                            ((TreeNode) tempvertex).setAnnonation(textField.getText());
                        }

                        vv.repaint();
                        textField.setText("Enter annotation");

                        ((JTextField) e.getSource()).getParent().setVisible(false);
                    }
                }
            });

            this.add(textField, BorderLayout.WEST);

//            JMenuItem menuItem = new JMenuItem("OK");
//            this.addSeparator();
//            this.add(menuItem, BorderLayout.EAST);
//            menuItem.addActionListener(buttonListener);
        }
        ActionListener buttonListener = new ActionListener() {
            public void actionPerformed(ActionEvent event) {

                GraphElementAccessor<Object, MyLink> pickSupport = vv.getPickSupport();

                Object tempvertex = pickSupport.getVertex(vv.getModel().getGraphLayout(), p.getX(), p.getY());

                if (tempvertex instanceof TreeNode) {
                    ((TreeNode) tempvertex).setAnnonation(textField.getText());
                }

                textField.setText("enter annotation here");

//                annotation.get(annotation_count).setText(textField.getText());
//                annotation.get(annotation_count).setVisible(true);
//                annotation.get(annotation_count).setBounds((int)p.getX(),(int)p.getY(),100,50);
//                                
//                annotation_locations.get(annotation_count).setLocation(p);
//                
//                
//                annotation_count++;
            }
        };

        public void actionPerformed(ActionEvent e) {

            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    protected class MyGraphMousePlugin extends AnimatedPickingGraphMousePlugin/*MousePluginAbstractPopupGraphMousePluginTranslatingGraphMousePlugin*/ implements MouseListener {

        int start_x, start_y;
        boolean start_drag = false;
        int detectSize = 20;

        @Override
        public void mouseDragged(MouseEvent e) {

            start_drag = true;

            magnifyLabel.setVisible(false);
            magnifyLabelLeft.setVisible(false);
            magnifyLabelRight.setVisible(false);
            magnifyLabelLeftLeft.setVisible(false);
            magnifyLabelRightRight.setVisible(false);

            updateLabelLocations();

            if (bMergeMode && !isCollapsed) {
                final VisualizationViewer<Object, MyLink> vv2
                        = (VisualizationViewer<Object, MyLink>) e.getSource();

                ObservableCachingLayout lll = (ObservableCachingLayout) vv2.getGraphLayout();

                for (int i = 0; i < lll.getGraph().getVertices().size(); i++) {
                    Object o = lll.getGraph().getVertices().toArray()[i];

                    if (o instanceof TreeNode) {
                        ((TreeNode) o).setDraggedTo(false);

                    }

                }

                Collection picked = new HashSet(vv2.getPickedVertexState().getPicked());

                final Point2D p = e.getPoint();

                if (picked.size() == 1) {

                    Object root1 = picked.iterator().next();
                    //Forest inGraph = (Forest) HtreeLayout.getGraph();

                    GraphElementAccessor<Object, MyLink> pickSupport = vv2.getPickSupport();

                    Object tempvertex = new Object();

                    Point2D tempp = new Point2D.Float(e.getX(), e.getY());

                    Point2D pinv = vv2.getRenderContext().getMultiLayerTransformer().inverseTransform(tempp);;
                    //Object tempV = pickSupport.getVertex(vv.getModel().getGraphLayout(), pinv.getX(), pinv.getY());

                    //  pinv = vv.getRenderContext().getMultiLayerTransformer().transform(pinv);
                    double scale = vv2.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).getScale();
                    rectMouseSelect = new Rectangle2D.Float((float) (p.getX() - (float) (detectSize * scale / 2)), (float) (p.getY() - (float) (detectSize * scale / 2)),
                            (float) (detectSize * scale), (float) (detectSize * scale));

                    Collection<Object> ppp = pickSupport.getVertices(vv2.getModel().getGraphLayout(), rectMouseSelect);
                    if (ppp.size() > 1) {

                        for (Iterator ip = ppp.iterator(); ip.hasNext();) {
                            Object tempV = ip.next();
                            if (tempV instanceof TreeNode) {
                                if (!((TreeNode) tempV).equals((TreeNode) root1)) {
                                    tempvertex = tempV;
                                    System.out.println("tempvertex" + ((TreeNode) tempV).getValue());
                                    break;

                                }
                            }
                        }
                    } else {

                        Object tempV = pickSupport.getVertex(vv.getModel().getGraphLayout(), pinv.getX(), pinv.getY());
                        tempV = pickSupport.getVertex(vv.getModel().getGraphLayout(), p.getX(), p.getY());

                        if (tempV instanceof TreeNode) {
                            tempvertex = tempV;
                        }
                    }

                    if (tempvertex != null && tempvertex instanceof TreeNode) {

                        TreeNode deleted_one = (TreeNode) root1;

                        if ((tempvertex instanceof TreeNode) && !tempvertex.equals(deleted_one)) {

                            if (!((TreeNode) tempvertex).getChildren().isEmpty()) {
                                ((TreeNode) tempvertex).setDraggedTo(true);
                            } else {
                                /// merge leaf node test
                                ((TreeNode) tempvertex).setDraggedTo(true);

                            }

                            System.out.println("overlapped");

                        }
                    }

                } else if (picked.size() > 1) {

                    final PickedState<Object> pickedState = vv.getPickedVertexState();

                    double scale = vv2.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).getScale();
                    rectMouseSelect = new Rectangle2D.Float((float) (p.getX() - (float) (detectSize * scale / 2)), (float) (p.getY() - (float) (detectSize * scale / 2)),
                            (float) (detectSize * scale), (float) (detectSize * scale));
                    GraphElementAccessor<Object, MyLink> pickSupport = vv2.getPickSupport();

                    Collection<Object> ppp = pickSupport.getVertices(vv2.getModel().getGraphLayout(), rectMouseSelect);

                    List<TreeNode> pickedNodes = new ArrayList<TreeNode>();
                    List<TreeNode> draggedToNodes = new ArrayList<TreeNode>();

                    for (Iterator iterator = pickedState.getPicked().iterator(); iterator.hasNext();) {

                        pickedNodes.add((TreeNode) (Object) iterator.next());

                    }
                    for (Object iterable_element : ppp) {
                        draggedToNodes.add((TreeNode) iterable_element);
                    }

                    for (int j = 0; j < pickedState.getPicked().size(); j++) {
                    }

                    TreeNode tempvertex = new TreeNode(1);
                    boolean b_tempDrag = false;

                    for (int j = 0; j < draggedToNodes.size(); j++) {

                        if (!pickedNodes.contains(draggedToNodes.get(j))) {
                            b_tempDrag = true;
                            tempvertex = draggedToNodes.get(j);
                        }
                    }

                    if (b_tempDrag) {
                        if (!((TreeNode) tempvertex).getChildren().isEmpty()) {
                            ((TreeNode) tempvertex).setDraggedTo(true);
                        } else {
                            /// merge leaf node test
                            ((TreeNode) tempvertex).setDraggedTo(true);

                        }
                    }

                }
            }

            vv.repaint();

            if (SwingUtilities.isRightMouseButton(e) && b_showText) {
                final VisualizationViewer<Object, MyLink> vv1
                        = (VisualizationViewer<Object, MyLink>) e.getSource();

                Point2D p3 = vv1.getRenderContext().getMultiLayerTransformer().inverseTransform(e.getPoint());

                ObservableCachingLayout lll = (ObservableCachingLayout) vv.getGraphLayout();

                for (int i = 0; i < lll.getGraph().getVertices().size(); i++) {
                    Object o = lll.getGraph().getVertices().toArray()[i];

                    if (o instanceof TreeNode) {

                        TreeNode t = (TreeNode) o;
                        if (allLabels.get(t) != null) {
                            for (int j = 0; j < allLabels.get(t).size(); j++) {
                                Rectangle2D rect = allLabels.get(t).get(j).getRect();

                                if (inSide((int) p3.getX(), (int) p3.getY(), rect)) {

                                    // highlightedColumn = t.getIndex();
                                    highlightedColumnKey = t;
                                    highlightedRow = j;

                                    break;
                                }

                            }
                        }
                    } else // not a tree node
                    {
                        TreeNode t = (TreeNode) ((DelegateTree) o).getRoot();

                        if (allLabels.get(t) != null) {
                            for (int j = 0; j < allLabels.get(t).size(); j++) {
                                Rectangle2D rect = allLabels.get(t).get(j).getRect();

                                if (inSide((int) p3.getX(), (int) p3.getY(), rect)) {

                                    //highlightedColumn = t.getIndex();
                                    highlightedColumnKey = t;
                                    highlightedRow = j;

                                    break;
                                }
                            }
                        }
                    }

                }

                System.out.println("highlighted: " + highlightedColumnKey + " " + highlightedRow);

                Point2D pp = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(e.getPoint());

                double x = pp.getX();
                double y = pp.getY();

                int length = 0;  //labeltexts[highlightedColumn][highlightedRow].s.length();

                Point2D viewPoint = vv.getRenderContext().getMultiLayerTransformer().transform(pp);

                Font font = new Font("font", Font.BOLD, 40);
                magnifyLabel.setFont(font);

                FontMetrics fm = magnifyLabel.getFontMetrics(font);
                int width = fm.stringWidth(" " + allLabels.get(highlightedColumnKey).get(highlightedRow).s + " ");

                magnifyLabel.setBounds((int) viewPoint.getX() - width / 2, (int) viewPoint.getY() - 20, width, 40);
                magnifyLabel.setText(" " + allLabels.get(highlightedColumnKey).get(highlightedRow).s + " ");
                magnifyLabel.setHorizontalTextPosition(JLabel.CENTER);
                magnifyLabel.setVerticalTextPosition(JLabel.CENTER);
                //magnifyLabel.setAlignment(JLabel.CENTER);

                if (highlightedRow >= 1) {
                    font = new Font("font", Font.BOLD, 30);
                    magnifyLabelLeft.setFont(font);

                    fm = magnifyLabelLeft.getFontMetrics(font);
                    width = fm.stringWidth(" " + allLabels.get(highlightedColumnKey).get(highlightedRow - 1).s + " ");

                    magnifyLabelLeft.setBounds((int) magnifyLabel.getBounds().getX() - width, (int) viewPoint.getY() - 20, width, 40);
                    magnifyLabelLeft.setText(" " + allLabels.get(highlightedColumnKey).get(highlightedRow - 1).s + " ");
                    magnifyLabelLeft.setHorizontalTextPosition(JLabel.CENTER);
                    magnifyLabelLeft.setVerticalTextPosition(JLabel.CENTER);
                    //magnifyLabel.setAlignment(JLabel.CENTER);
                    magnifyLabelLeft.setVisible(true);
                }

                if (highlightedRow >= 2) {
                    font = new Font("font", Font.BOLD, 20);
                    magnifyLabelLeftLeft.setFont(font);

                    fm = magnifyLabelLeftLeft.getFontMetrics(font);
                    width = fm.stringWidth(" " + allLabels.get(highlightedColumnKey).get(highlightedRow - 2).s + " ");

                    magnifyLabelLeftLeft.setBounds((int) magnifyLabelLeft.getBounds().getX() - width, (int) viewPoint.getY() - 20, width, 40);
                    magnifyLabelLeftLeft.setText(" " + allLabels.get(highlightedColumnKey).get(highlightedRow - 2).s + " ");
                    magnifyLabelLeftLeft.setHorizontalTextPosition(JLabel.CENTER);
                    magnifyLabelLeftLeft.setVerticalTextPosition(JLabel.CENTER);

                    magnifyLabelLeftLeft.setVisible(true);
                }

                if (highlightedRow <= labelsToDisplay - 1) {
                    font = new Font("font", Font.BOLD, 30);
                    magnifyLabelRight.setFont(font);

                    fm = magnifyLabelRight.getFontMetrics(font);
                    width = fm.stringWidth(" " + allLabels.get(highlightedColumnKey).get(highlightedRow + 1).s + " ");

                    magnifyLabelRight.setBounds((int) magnifyLabel.getBounds().getX() + (int) magnifyLabel.getBounds().getWidth(), (int) viewPoint.getY() - 20, width, 40);
                    magnifyLabelRight.setText(" " + allLabels.get(highlightedColumnKey).get(highlightedRow + 1).s + " ");
                    magnifyLabelRight.setHorizontalTextPosition(JLabel.CENTER);
                    magnifyLabelRight.setVerticalTextPosition(JLabel.CENTER);
                    //magnifyLabel.setAlignment(JLabel.CENTER);
                    magnifyLabelRight.setVisible(true);
                }

                if (highlightedRow <= labelsToDisplay - 2) {
                    font = new Font("font", Font.BOLD, 20);
                    magnifyLabelRightRight.setFont(font);

                    fm = magnifyLabelRightRight.getFontMetrics(font);
                    width = fm.stringWidth(" " + allLabels.get(highlightedColumnKey).get(highlightedRow + 2).s + " ");

                    magnifyLabelRightRight.setBounds((int) magnifyLabelRight.getBounds().getX() + (int) magnifyLabelRight.getBounds().getWidth(), (int) viewPoint.getY() - 20, width, 40);
                    magnifyLabelRightRight.setText(" " + allLabels.get(highlightedColumnKey).get(highlightedRow + 2).s + " ");
                    magnifyLabelRightRight.setHorizontalTextPosition(JLabel.CENTER);
                    magnifyLabelRightRight.setVerticalTextPosition(JLabel.CENTER);

                    magnifyLabelRightRight.setVisible(true);
                }

                magnifyLabel.setVisible(true);

            }

            //updateLabelLocations();
            vv.repaint();

        }

        @Override
        public void mouseReleased(MouseEvent e) {

            bMagnified = false;

            magnifyLabel.setVisible(false);
            magnifyLabelLeft.setVisible(false);
            magnifyLabelRight.setVisible(false);
            magnifyLabelLeftLeft.setVisible(false);
            magnifyLabelRightRight.setVisible(false);

            if (bMergeMode && !isCollapsed) {

                final VisualizationViewer<Object, MyLink> vv1
                        = (VisualizationViewer<Object, MyLink>) e.getSource();

                Collection picked = new HashSet(vv1.getPickedVertexState().getPicked());

                final Point2D p = e.getPoint();

                if (SwingUtilities.isLeftMouseButton(e) && start_drag) {

                    if (picked.size() == 1) {

                        Object root1 = picked.iterator().next();
                        //Forest inGraph = (Forest) HtreeLayout.getGraph();
                        TreeNode deleted_one;

                        if (root1 instanceof TreeNode) {
                            deleted_one = (TreeNode) root1;
                        } else//if (root1 instanceof DelegateTree)
                        {
                            deleted_one = (TreeNode) ((DelegateTree) root1).getRoot();
                        }

                        GraphElementAccessor<Object, MyLink> pickSupport = vv1.getPickSupport();

                        Object tempvertex = new Object();

                        Point2D tempp = new Point2D.Float(e.getX(), e.getY());

                        Point2D pinv = vv1.getRenderContext().getMultiLayerTransformer().inverseTransform(tempp);;
                        //Object tempV = pickSupport.getVertex(vv.getModel().getGraphLayout(), pinv.getX(), pinv.getY());

                        double scale = vv1.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).getScale();
                        rectMouseSelect = new Rectangle2D.Float((float) (p.getX() - (float) (detectSize * scale / 2)), (float) (p.getY() - (float) (detectSize * scale / 2)), (float) (detectSize * scale), (float) (detectSize * scale));
                        // 
                        Collection<Object> ppp = pickSupport.getVertices(vv1.getModel().getGraphLayout(), rectMouseSelect);
                        if (ppp.size() <= 2) {

                            System.out.println(" ppp.size()>1" + ppp.size());
                            for (Iterator ip = ppp.iterator(); ip.hasNext();) {
                                Object tempV = ip.next();
                                if (tempV instanceof TreeNode) {
                                    if (!((TreeNode) tempV).equals((TreeNode) root1)) {
                                        tempvertex = tempV;
                                        System.out.println(" release tempvertex" + ((TreeNode) tempV).getValue());
                                        break;

                                    }
                                }
                            }
                        } else {
//                            tempvertex = pickSupport.getVertex(vv1.getModel().getGraphLayout(), pinv.getX(), pinv.getY());
                            tempvertex = pickSupport.getVertex(vv1.getModel().getGraphLayout(), p.getX(), p.getY());

                        }

                        final PickedState<Object> pickedState = vv1.getPickedVertexState();
                        if (pickedState.getPicked().size() <= 1 && ppp.size() > 1) {
                            if ((tempvertex instanceof TreeNode)) {
                                System.out.println(((TreeNode) tempvertex).getValue());
                                if (!((TreeNode) tempvertex).equals(deleted_one)) {

                                    if (!((TreeNode) tempvertex).getChildren().isEmpty()) {
                                        try {
                                            //System.out.println(tvf);
                                            MergeNodeTree(deleted_one.getValue(), deleted_one.getParent().getValue(), ((TreeNode) tempvertex).getValue());

                                            vv.getGraphLayout().setGraph(gh);
                                            HtreeLayout = new HorizontalOverlappingTreeLayout<Object, MyLink>(gh, node_width_interval, node_height_interval);
                                            //  vv.getPickedVertexState().clear();
                                            //System.out.println(tvf);
                                            buildLabelLocations();
                                            System.out.println("topic merged ");

                                            // TemporalViewFrame _tvf = ((TopicGraphViewPanel)(((VisualizationViewer) e.getComponent()).getParent().getParent()).getParent()).parent.getTemporalFrame();
                                            if (tvf != null) {
                                                tvf.updateData(myTree);
                                            }
                                        } catch (FileNotFoundException ex) {
                                            Logger.getLogger(TopicGraphViewPanel.class.getName()).log(Level.SEVERE, null, ex);
                                        } catch (IOException ex) {
                                            Logger.getLogger(TopicGraphViewPanel.class.getName()).log(Level.SEVERE, null, ex);
                                        }

                                    } else {
                                        try {
                                            //merge leaf node                                                
                                            MergeLeafNodeTree(deleted_one.getValue(), deleted_one.getParent().getValue(), ((TreeNode) tempvertex).getValue(),
                                                    ((TreeNode) tempvertex).getParent().getValue(), newNodeIdx);

                                            vv.getGraphLayout().setGraph(gh);
                                            HtreeLayout = new HorizontalOverlappingTreeLayout<Object, MyLink>(gh, node_width_interval, node_height_interval);
                                            //  vv.getPickedVertexState().clear();
                                            //System.out.println(tvf);
                                            System.out.println("leaf merged ");
                                            buildLabelLocations();

                                            if (tvf != null) {
                                                tvf.updateData(myTree);
                                            }

                                            newNodeIdx++;
                                        } catch (FileNotFoundException ex) {
                                            Logger.getLogger(TopicGraphViewPanel.class.getName()).log(Level.SEVERE, null, ex);
                                        } catch (IOException ex) {
                                            Logger.getLogger(TopicGraphViewPanel.class.getName()).log(Level.SEVERE, null, ex);
                                        }

                                    }

                                    vv.repaint();
                                }
                            } else {

                                System.out.println("vertex = null");
                            }
                        }

                    } else if (picked.size() > 1) {
                        try {
                            final PickedState<Object> pickedState = vv.getPickedVertexState();

                            double scale = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).getScale();
                            rectMouseSelect = new Rectangle2D.Float((float) (p.getX() - (float) (detectSize * scale / 2)), (float) (p.getY() - (float) (detectSize * scale / 2)),
                                    (float) (detectSize * scale), (float) (detectSize * scale));
                            GraphElementAccessor<Object, MyLink> pickSupport = vv.getPickSupport();

                            Collection<Object> ppp = pickSupport.getVertices(vv.getModel().getGraphLayout(), rectMouseSelect);

                            List<TreeNode> pickedNodes = new ArrayList<TreeNode>();
                            List<TreeNode> draggedToNodes = new ArrayList<TreeNode>();

                            for (Iterator iterator = pickedState.getPicked().iterator(); iterator.hasNext();) {

                                pickedNodes.add((TreeNode) (Object) iterator.next());

                            }
                            for (Object iterable_element : ppp) {
                                draggedToNodes.add((TreeNode) iterable_element);
                            }

                            for (int j = 0; j < pickedState.getPicked().size(); j++) {
                            }

                            TreeNode tempvertex = new TreeNode(1);
                            boolean b_tempDrag = false;

                            for (int j = 0; j < draggedToNodes.size(); j++) {

                                if (!pickedNodes.contains(draggedToNodes.get(j))) {
                                    b_tempDrag = true;
                                    tempvertex = draggedToNodes.get(j);
                                }
                            }

                            List<String> deleted = new ArrayList<String>();
                            List<String> parented = new ArrayList<String>();
                            Iterator pickIt = pickedState.getPicked().iterator();
                            while (pickIt.hasNext()) {
                                TreeNode temp = (TreeNode) pickIt.next();
                                deleted.add(temp.getValue());
                                parented.add(temp.getParent().getValue());
                            }

                            if (ppp.size() > 1) {
                                MergeNodeTreeMultiple(deleted, parented, ((TreeNode) tempvertex).getValue());

                                vv.getGraphLayout().setGraph(gh);
                                HtreeLayout = new HorizontalOverlappingTreeLayout<Object, MyLink>(gh, node_width_interval, node_height_interval);
                                //vv.getPickedVertexState().clear();
                                buildLabelLocations();
                                System.out.println("topic collapsed ");

                                if (tvf != null) {
                                    tvf.updateData(myTree);
                                }
                            }
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(TopicGraphViewPanel.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(TopicGraphViewPanel.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }

                }

            }

            vv.repaint();

            start_drag = false;
        }

        @Override
        public void mouseClicked(MouseEvent e) {

            highlightOne = 0;
            final VisualizationViewer<Object, MyLink> vv1
                    = (VisualizationViewer<Object, MyLink>) e.getSource();

            //          Point2D pinv = vv1.getRenderContext().getMultiLayerTransformer().inverseTransform(e.getPoint());;
            Collection picked = new HashSet(vv1.getPickedVertexState().getPicked());

            //TODO: double Click to add
            if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
                if (picked.size() == 1) {

                    
                        TemporalViewPanel tp = null;
                        try {
                            tp = new TemporalViewPanel(parent);
                        } catch (IOException ex) {
                            Logger.getLogger(TopicGraphViewPanel.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        if (!tvf.getTemporalPanelMap().containsKey(1)) {
                            List<TemporalViewPanel> tvpl = new ArrayList<TemporalViewPanel>();
                            tvf.getTemporalPanelMap().put(1, tvpl);

                        }

                        int index = tvf.getTemporalPanelMap().get(1).size();
                        tp.setName("1" + index);
                        tp.setPanelLabelId(index);
                        tp.setLevel(1);
                        tp.setData(tvf.getData());
                        tp.setTree(tvf.getTree());
                        tvf.getMainPanel().addChildPanel(tp);
                        tp.setFatherPanel(tvf.getMainPanel());
                        TreeNode tempt = (TreeNode) picked.iterator().next();
                        
                        for (TreeNode n : tvf.getTree()) {

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
                        tvf.getMainPanel().getDrawLabels().add(it);

                        Point2D pf = new Point2D.Float(0, 0);

                        tvf.getMainPanel().getDrawLabelsLocation().add(pf);
                        tp.setFatherPanel(tvf.getMainPanel());
                        tp.calculateLocalNormalizingValue(tp.getData(), tp.currentNode);
                        tp.buildLabelTimeMap();
                        
                        
                        tvf.getTemporalPanelMap().get(1).add(tp);
                        float normalizeValue = -1;
                        if (tvf.getTemporalPanelMap().get(1).size() > 0) {
                            for (TemporalViewPanel ttp : tvf.getTemporalPanelMap().get(1)) {
                                if (ttp.getLocalNormalizingValue() >= normalizeValue) {
                                    normalizeValue = ttp.getLocalNormalizingValue();
                                }
                            }
                            for (TemporalViewPanel ttp : tvf.getTemporalPanelMap().get(1)) {
                                ttp.setGlobalNormalizingValue(normalizeValue);
                            }
                        }

                            // tp.UpdateTemporalView(new Dimension(tf.getWidth()/(1+secondColumnExist+thirdColumnExist),tf.getHeight()/3), tp.getGlobalNormalizingValue());
                        for (TemporalViewPanel ttp : tvf.getTemporalPanelMap().get(1)) {
                            ttp.calculateRenderControlPointsOfEachHierarchy(tp.getData(), tp.currentNode, tp.getGlobalNormalizingValue());
                            ttp.computerZeroslopeAreasHierarchy(0);
                                //ttp.detectEvents();

                            //ttp.UpdateTemporalView(new Dimension(tvf.getContentPane().getWidth() / (1 + secondColumnExist + thirdColumnExist), tvf.getContentPane().getHeight() / 3), ttp.getGlobalNormalizingValue());
                        }

                            //tvf.getMainPanel().UpdateTemporalView(new Dimension(tvf.getContentPane().getWidth() / (1 + secondColumnExist + thirdColumnExist), tvf.getContentPane().getHeight() * 2 / 3), tvf.getMainPanel().getLocalNormalizingValue());
                        //tvf.getSubPanel().UpdateTemporalView(new Dimension(tvf.getContentPane().getWidth() / (1 + secondColumnExist + thirdColumnExist), tvf.getContentPane().getHeight() / 3), tvf.getSubPanel().getLocalNormalizingValue());
                        System.out.println("second column panel added");
                        tvf.setMigLayoutForScrollPane();

                    

                }

            }


            for (List<LabelText> v : allLabels.values()) {
                for (int i = 0; i < v.size(); i++) {
                    v.get(i).isHighlighted = false;
                }

            }

            updateLabelLocations();

            if (SwingUtilities.isRightMouseButton(e) && b_showText) {

                popupMenu.show(e.getComponent(), e.getX(), e.getY());
                popupMenu.setMouseLocation(e.getPoint());

                //annotation not done yet
                //  tempPanel.setVisible(true);
                Point2D pp = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(e.getPoint());

                double x = pp.getX();
                double y = pp.getY();

                Point2D viewPoint = vv.getRenderContext().getMultiLayerTransformer().transform(pp);

                vv.repaint();

              
            }

            //magnifyViewSupport.activate(true);
        }

        public boolean inSide(float x, float y, Rectangle2D rect) {
            
            if (x > rect.getX() && x < (rect.getX() + rect.getWidth()) && y > rect.getY() && y < (rect.getY() + rect.getHeight())) {
                return true;
            } else {
                return false;
            }

        }

        @Override
        public void mouseMoved(MouseEvent e) {

           // content.setLayout(null);
            tooltipLabel.setVisible(false);

            if (!SwingUtilities.isRightMouseButton(e) && !SwingUtilities.isLeftMouseButton(e) && b_showText) {
                //tempLabel.setVisible(false);

                for (List<LabelText> value : allLabels.values()) {
                    for (int i = 0; i < value.size(); i++) {
                        value.get(i).isHighlighted = false;
                    }
                }

                final VisualizationViewer<Object, MyLink> vv1 = (VisualizationViewer<Object, MyLink>) e.getSource();

                //if (!SwingUtilities.isLeftMouseButton(e) && !SwingUtilities.isRightMouseButton(e)) // if (pickSupport != null) 
                {

                    Point2D p3 = vv1.getRenderContext().getMultiLayerTransformer().inverseTransform(e.getPoint());

                    ObservableCachingLayout lll = (ObservableCachingLayout) vv.getGraphLayout();

                    here:
                    for (int i = 0; i < lll.getGraph().getVertices().size(); i++) {
                        Object o = lll.getGraph().getVertices().toArray()[i];

                        if (o instanceof TreeNode) {

                            TreeNode t = (TreeNode) o;
                            if (allLabels.get(t) != null) {
                                for (int j = 0; j < allLabels.get(t).size(); j++) {
                                    Rectangle2D rect = allLabels.get(t).get(j).getRect();
                                    if (inSide((int) p3.getX(), (int) p3.getY(), rect)) {

                                        allLabels.get(t).get(j).isHighlighted = true;

                                        TreeNode tempT = t;
                                        String matchString = allLabels.get(t).get(j).s;
                                        highlightOthers(matchString, tempT, j);

                                        tooltipLabel.setHorizontalTextPosition(JLabel.CENTER);
                                        tooltipLabel.setVerticalTextPosition(JLabel.CENTER);
                                        Font font = new Font(HelveticaFont, Font.PLAIN, labelFontSize);

                                        tooltipLabel.setText("<html> Frequency: " + allLabels.get(t).get(j).getOccurance() + "<br> Probablity: " + allLabels.get(t).get(j).getProbablity());

                                        FontMetrics fm = tooltipLabel.getFontMetrics(font);

                                        int widthOfString = Math.max(
                                                fm.stringWidth(" Frequency: " + allLabels.get(t).get(j).getOccurance()),
                                                fm.stringWidth(" Probablity: " + allLabels.get(t).get(j).getProbablity()));
                                        // int widthOfString = fm.stringWidth(" Frequency: " + allLabels.get(t).get(j).getOccurance() + "  Probablity: " + allLabels.get(t).get(j).getProbablity());

                                        tooltipLabel.setBounds((int) e.getPoint().getX(), (int) e.getPoint().getY(), widthOfString, 30);
                                        tooltipLabel.setVisible(true);

                                        break here;

                                    }
                                }
                            }

                        } else {
                            TreeNode t = (TreeNode) ((DelegateTree) o).getRoot();

                            if (allLabels.get(t) != null) {
                                for (int j = 0; j < allLabels.get(t).size(); j++) {
                                    Rectangle2D rect = allLabels.get(t).get(j).getRect();

                                    if (inSide((int) p3.getX(), (int) p3.getY(), rect)) {

                                        allLabels.get(t).get(j).isHighlighted = true;

                                        TreeNode tempT = t;
                                        String matchString = allLabels.get(t).get(j).s;
                                        highlightOthers(matchString, tempT, j);

                                        tooltipLabel.setHorizontalTextPosition(JLabel.CENTER);
                                        tooltipLabel.setVerticalTextPosition(JLabel.CENTER);
                                        Font font = new Font(HelveticaFont, Font.PLAIN, labelFontSize);

                                        tooltipLabel.setText("Frequency: " + allLabels.get(t).get(j).getOccurance() + " Probablity: " + allLabels.get(t).get(j).getProbablity());

                                        FontMetrics fm = tooltipLabel.getFontMetrics(font);
                                        int widthOfString = fm.stringWidth("Frequency: " + allLabels.get(t).get(j).getOccurance() + " Probablity: " + allLabels.get(t).get(j).getProbablity());
                                        tooltipLabel.setBounds((int) e.getPoint().getX(), (int) e.getPoint().getY(), widthOfString, 40);
                                        tooltipLabel.setVisible(true);

                                        break here;
                                    }
                                }
                            }

                        }

                    }


                }
                updateLabelLocations();
                //buildLabelLocations();

                //  vv.getPickedVertexState().clear();
                vv.repaint();

            }
            //content.setLayout(new BorderLayout());
        }

        public MyGraphMousePlugin() {
            // this(MouseEvent.BUTTON3);
            super();
            // start_drag = false;
        }

        public void highlightOthers(String tmpString, TreeNode key, int index) {

            ObservableCachingLayout lll = (ObservableCachingLayout) vv.getGraphLayout();

//
//                for (int i = 0; i < lll.getGraph().getVertices().size(); i++) {
//                    Object o = lll.getGraph().getVertices().toArray()[i];
//            
//                }
            for (Map.Entry pairs : allLabels.entrySet()) {

                List<LabelText> value = (List<LabelText>) pairs.getValue();

                for (int j = 0; j < value.size(); j++) {
                    if (tmpString.equals(value.get(j).s)) { //&& (j != index || !pairs.getKey().equals(key))

                        //if (lll.getGraph().getVertices().contains(pairs.getKey())) 
                        {
                            value.get(j).isHighlighted = true;
                            //System.out.println("label " + value.get(j).s + " is also selected " + tmpString);
                        }
                    }
                }
            }
        }

        protected void handlePopup(MouseEvent e) {
            // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void mousePressed(MouseEvent e) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void mouseExited(MouseEvent e) {
            // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    private final static class DirectionDisplayPredicate<V, E>
            implements Predicate<Context<Graph<V, E>, E>> //extends AbstractGraphPredicate<V,E>
    {

        protected boolean show_d;
        protected boolean show_u;

        public DirectionDisplayPredicate(boolean show_d, boolean show_u) {
            this.show_d = show_d;
            this.show_u = show_u;
        }

        public void showDirected(boolean b) {
            show_d = b;
        }

        public void showUndirected(boolean b) {
            show_u = b;
        }

        public boolean evaluate(Context<Graph<V, E>, E> context) {
            Graph<V, E> graph = context.graph;
            E e = context.element;
            if (graph.getEdgeType(e) == EdgeType.DIRECTED && show_d) {
                return true;
            }
            if (graph.getEdgeType(e) == EdgeType.UNDIRECTED && show_u) {
                return true;
            }
            return false;
        }
    }

    private final static class VertexFontTransformer<V>
            implements Transformer<V, Font> {

        protected boolean bold = true;
        Font f = new Font("Helvetica", Font.PLAIN, 18);
        Font b = new Font("Helvetica", Font.BOLD, 18);

        public void setBold(boolean bold) {
            this.bold = bold;
        }

        public Font transform(V v) {
            if (bold) {
                return b;
            } else {
                return f;
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
