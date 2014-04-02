/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TasteAnalytics.HierarchicalTopics.eventsview;

import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DirectedOrderedSparseMultigraph;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.layout.ObservableCachingLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import com.TasteAnalytics.HierarchicalTopics.gui.ViewController;
import com.TasteAnalytics.HierarchicalTopics.datahandler.CategoryBarElement;
import com.TasteAnalytics.HierarchicalTopics.temporalView.renderer.TreeNode;
import net.miginfocom.swing.MigLayout;
import com.TasteAnalytics.HierarchicalTopics.topicRenderer.TopicGraphViewPanel;
import prefuse.data.Graph;

/**
 *
 * @author xwang
 */
import com.TasteAnalytics.HierarchicalTopics.topicRenderer.EventsPanelPrefuse;
import com.TasteAnalytics.HierarchicalTopics.topicRenderer.PrefuseLabelTopicGraphPanel;
import com.TasteAnalytics.HierarchicalTopics.topicRenderer.TopicGraphViewPanel.MyLink;

public class EventViewFrame extends JFrame {
    
    public ViewController parent;
    
    
    private List<TreeNode> myTree;
    
    public List<TreeNode> getMyTree() {
        return myTree;
    }
    
    public void setMyTree(List<TreeNode> myTree) {
        this.myTree = myTree;
    }
    HashMap eventIconMap = new HashMap();
    private List<EventsViewPanel> eventsPanelList;
    private EventGraphViewPanel graphPanel;
    private EventsPanelPrefuse prefuseEventPanel;
    private PrefuseLabelTopicGraphPanel labelTopicGraphPanel;

    public PrefuseLabelTopicGraphPanel getLabelTopicGraphPanel() {
        return labelTopicGraphPanel;
    }
    
    
    
    
    
    List<TreeNode> edgeList = new ArrayList<TreeNode>();
    HashMap locationMap = new HashMap();
    boolean bEdgeVertexRetrived = false;
    
    public List<EventsViewPanel> getEventsPanelList() {
        return eventsPanelList;
    }
    
    public void setEventsPanelList(List<EventsViewPanel> eventsPanelList) {
        this.eventsPanelList = eventsPanelList;
    }
    
    public final float calculateLocalNormalizingValue(CategoryBarElement data, TreeNode t) {
        
        
        
        int numofYears = data.getNumOfYears();// Number of bars

        
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

        //localNormalizingValue = localmaxValue;

        return localmaxValue;
        
        
    }
    public TreeNode[] leaftNodes;
    //public TreeNode[] nodeNodes;
    static int MAXLEAFNODESCOUNT = 100;
    private List<JLabel> NodeLabels = new ArrayList<JLabel>();
    private List<TreeNode> nodeNodes = new ArrayList<TreeNode>();
    
    public EventViewFrame(ViewController vc, List<TreeNode> thisTree, CategoryBarElement data, List<Integer> seq, DelegateForest<Object, TopicGraphViewPanel.MyLink> g, Graph pgh, String everything, String folderPath, List<List<Float>> disMatrix) throws FileNotFoundException, IOException {
        super("Tag Relationship");
        
        parent = vc;
        
        this.setPreferredSize(new Dimension(1000, 1000));
        
        this.setSize(new Dimension(1000, 1000));
        int panelWidth = 1000;
        int panelHeight = 1000;
        JPanel testPanel = new JPanel()
        {   
            @Override
            public void paintComponent(Graphics g) {
                if (bEdgeVertexRetrived)
                {
                    DrawEdges(g, edgeList, locationMap);
                    
                }
                
            }
            
            public void DrawEdges(Graphics g, List<TreeNode> el, HashMap m)
            {
                
                g.drawLine(20, 20, 250, 250);
                for (int i=0; i<el.size()/2; i++)
                {
                    Point2D p1 = (Point2D)m.get(el.get(2*i).getValue());
                    Point2D p2 = (Point2D)m.get(el.get(2*i+1).getValue());
                                        
                    g.drawLine((int)p1.getX(), (int)p1.getY(), (int)p2.getX(), (int)p2.getY());
                    
                }
                
            }
        };
        
        testPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));
        
        
        
        leaftNodes = new TreeNode[MAXLEAFNODESCOUNT];
        // nodeNodes = new TreeNode[MAXLEAFNODESCOUNT];

        
        int count = 0;
        int nodeCount = 0;
        for (TreeNode t : thisTree) {
            if (t.getValue().contains("Leaf")) {
                String a = t.getValue().replaceAll("\\D", "");
                int index = Integer.parseInt(a);
                if (index >= 0 && index < MAXLEAFNODESCOUNT) {
                    leaftNodes[index] = t;
                    count++;
                    
                }
                
            } else {
                String a = t.getValue().replaceAll("\\D", "");
                int index = Integer.parseInt(a);
                if (index >= 0 && index < MAXLEAFNODESCOUNT) {
                    nodeNodes.add(t);
                    //nodeNodes[index] = t;
                    nodeCount++;
                    
                }
                
                
                
            }
            
        }
        
        
        float min_weights = 99.0f;
        float max_weights = -99.0f;
        float[] weights = new float[count];
        float tempsum = 0.0f;
        for (int i = 0; i < count; i++) {
            
            weights[i] = calculateLocalNormalizingValue(data, leaftNodes[i]);
            tempsum += weights[i];
            
            if (weights[i] >= max_weights) {
                max_weights = weights[i];
            }
            
            if (weights[i] <= min_weights) {
                min_weights = weights[i];
            }
        }

        //min_weights /= tempsum;

        HashMap hm = new HashMap();
        HashMap hm2 = new HashMap();
        
        
        
        for (int i = 0; i < thisTree.size(); i++) {
            
            hm.put(thisTree.get(i).getValue(), thisTree.get(i));
            
        }
        
        for (int i = 0; i < count; i++) {
            float value = weights[i] / min_weights;//(weights[i]-min_weights)/(max_weights-min_weights);

            String key = "LeafTopic" + i;
            Object o = hm.get(key);
            ((TreeNode) o).setLeafNodeWeight(value);
            hm2.put(key, value);
            
        }
        
        
        
        
        thisTree.get(0).calculateNodeWeight();
        
        myTree = thisTree;



        /////////////////////////////////////////////////////////////////////////////////////////////////////


        //prefuse
        
        this.getContentPane().setLayout(new BorderLayout());
       labelTopicGraphPanel = new PrefuseLabelTopicGraphPanel(folderPath, vc, disMatrix);
       this.getContentPane().add(labelTopicGraphPanel);
        

        //prefuseEventPanel = new EventsPanelPrefuse(pgh, myTree, hm2);
         //this.getContentPane().add(prefuseEventPanel);


        /////////////////////////////////////////////////////////////////////////////////////


        // jung stuff
        
        
//         graphPanel = new EventGraphViewPanel(g, thisTree, eventIconMap);
//         
//         
/////edges
//        
//        
//        
//        Collection<MyLink> e = g.getEdges();
//        
//        Iterator iterator = e.iterator();
//        while (iterator.hasNext()) {
//            MyLink link = (MyLink) iterator.next();
//            Collection<Object> n = g.getIncidentVertices(link);
//            
//            for (Object iterable_element : n) {
//                edgeList.add((TreeNode) iterable_element);
//            }
//                                    
//        }
//        
//        
//        
//        
//        
//        
//        
//        
//       ///nodes
//        
//        
//        
//        
//
//        //double scale = graphPanel.getVv().getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).getScale();
//        ObservableCachingLayout lll = (ObservableCachingLayout) graphPanel.getVv().getGraphLayout();
//        
//        double max_locationY = -9999;
//        
//        for (int i = 0; i < lll.getGraph().getVertices().size(); i++) {
//            Object o = lll.getGraph().getVertices().toArray()[i];
//            if (o instanceof TreeNode) {
//                
//                TreeNode t = (TreeNode) lll.getGraph().getVertices().toArray()[i];
//                Point2D loc = lll.transform(lll.getGraph().getVertices().toArray()[i]);
//                
//                if (loc.getY() >= max_locationY) {
//                    max_locationY = loc.getY();
//                }
//                
//                locationMap.put(t.getValue(), loc);
//                
//            }
//        }
//        
//        bEdgeVertexRetrived = true;
//        
//        
//        System.out.println(max_locationY);
//        
//        eventsPanelList = new ArrayList<EventsViewPanel>();
//        
//        
//        
//        for (Iterator it = g.getVertices().iterator(); it.hasNext();) {
//            Object o = it.next();
//            String s = ((TreeNode) o).getValue();
//
//            //if (s.contains("Leaf"))
//            {
//                Object o1 = hm.get(s);
//                ((TreeNode) o).setLeafNodeWeight(((TreeNode) o1).getLeafNodeWeight());
//                
//            }
//            
//            
//        }
//
////
//        
//        
//        
//        this.getContentPane().setLayout(new BorderLayout());
//        JScrollPane scrollPane = new JScrollPane(testPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
//        this.getContentPane().add(scrollPane, BorderLayout.CENTER);
//        
//        scrollPane.setViewportView(testPanel);
//        
//        
//        
//        testPanel.setPreferredSize(new Dimension(panelWidth, (int) (max_locationY + 500)));
//        testPanel.setLayout(null);
//        testPanel.setBackground(Color.green);
//        
//        for (int i = 0; i < count; i++) {
//
//            //load, calculate, detect, draw
//            int tempHeight = (int) ((weights[i] / tempsum) * panelHeight);
//            EventsViewPanel tempPanel = new EventsViewPanel(vc, panelWidth, tempHeight);
//
//            // System.out.println( "weights " + weights[i] +" height "+ tempHeight);
//            //tempPanel.setPreferredSize(new Dimension(panelWidth,tempHeight));
//
//            //tempPanel.setPreferredSize(new Dimension(400, 400));
//            Point2D p = (Point2D) locationMap.get(leaftNodes[i].getValue());
//            
//            tempPanel.calculateRenderControlPointsOfEachHierarchy(data, leaftNodes[i], weights[i]);
//            tempPanel.setCategoryColor(leaftNodes[i].getColor());
//            tempPanel.detectEvents(2.0f, leaftNodes[i]);
//            tempPanel.setAttechedNode(leaftNodes[i]);
//            tempPanel.setBounds((int) p.getX(), (int) p.getY(), panelWidth, tempHeight);
//            eventsPanelList.add(tempPanel);
//            
//            
//        }
//        
//        for (int i = 0; i < nodeCount; i++) {
//            JPanel tempLabel = new JPanel();
//            
//            tempLabel.setBackground(Color.red);
//            
//            Point2D p = (Point2D) locationMap.get(nodeNodes.get(i).getValue());
//            System.out.println(p);
//            tempLabel.setBounds((int) p.getX(), (int) p.getY(), 30, 30);
//            
//            testPanel.add(tempLabel);
//            
//        }
//        
//        
//        for (int i = 0; i < eventsPanelList.size(); i++) {
//            //eventsPanelList.get(i).setBounds(100, 100+i*150, panelWidth , tempHeight*10);
//            testPanel.add(eventsPanelList.get(i));
//            
//        }
////
//
//
//        //this.getContentPane().setLayout(new BorderLayout());
//
//
//        //this.getContentPane().add(graphPanel.getVv(),BorderLayout.CENTER);
////
////

//        this.setVisible(true);
        this.addComponentListener(new ComponentListener() {
            public void componentResized(ComponentEvent e) {
                
                ((EventViewFrame) e.getComponent()).setSize(new Dimension(e.getComponent().getSize().width, e.getComponent().getSize().height));

//                .UpdateEventView(new Dimension(e.getComponent().getSize().width, e.getComponent().getSize().height));
//                System.out.println("panel redraw " + e.getComponent().getSize().width + "; " + e.getComponent().getSize().height);
//                ((EventViewFrame) e.getComponent()).invalidate();
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

        // pack();
    }
    
    public void processTreeString(String treeString, DelegateForest gh) {
        
        
        Scanner sc = new Scanner(treeString);
        
        sc.useDelimiter("\n");
        String temp = sc.next();
        
        
        String nodes = sc.next();
        
        String[] tempNodes = nodes.split(",");
        
        TreeNode NodeArray[] = new TreeNode[100];
        TreeNode LeafArray[] = new TreeNode[100];
        
        for (int i = 0; i < tempNodes.length; i++) {
            
            String a = tempNodes[i].replaceAll("\\D", "");
            
            int index = Integer.parseInt(a);
            
            
            TreeNode t = new TreeNode(index, tempNodes[i].replaceAll("[^\\p{L}\\p{N}]", ""));
            
            if (tempNodes[i].replaceAll("[^\\p{L}\\p{N}]", "").charAt(0) == 'N') {
                
                String[] ary = new String[]{/*t.getValue()*/};
                t.setNodeTopics(ary);
                NodeArray[index] = t;
                //myTree.add(t);
            } else if (tempNodes[i].replaceAll("[^\\p{L}\\p{N}]", "").charAt(0) == 'L') {
                //      labels[1][2];
                LeafArray[index] = t;

                //myTree.add(t);
            } else {
                int c = 0;
            }
        }
        
        
        
        String edges = sc.next();

//        gh = new DelegateForest<Object, TopicGraphViewPanel.MyLink>(new DirectedOrderedSparseMultigraph<Object, TopicGraphViewPanel.MyLink>());

        //     Graph<Integer,Number> g =     	MixedRandomGraphGenerator.<Integer,Number>generateMixedRandomGraph(edge_weight);

        
        String[] tempEdges = edges.split("\\),");
        for (int i = 0; i < tempEdges.length - 1; i++) {
            String[] tempE = tempEdges[i].split(",");
            TreeNode tt1, tt2;
            EventsViewPanel nt1, nt2;
            
            
            int weight = 100;
            
            int index1 = Integer.parseInt(tempE[0].replaceAll("\\D", ""));
            int index2 = Integer.parseInt(tempE[1].replaceAll("\\D", ""));
            
            String tempE1 = tempE[0].replaceAll("[^\\p{L}\\p{N}]", "");
            if (tempE1.charAt(0) == 'N') {
                tt1 = NodeArray[index1];
                weight = index1;
                nt1 = new EventsViewPanel();
                nt1.setPreferredSize(new Dimension(50, 50));
                nt1.setBackground(Color.red);
                nt1.setAttechedNode(tt1);
            } else if (tempE1.charAt(0) == 'L') {
                tt1 = LeafArray[index1];
                nt1 = eventsPanelList.get(index1);
//                nt1.setPreferredSize(new Dimension(50,50));
//                nt1.setBackground(Color.red);
//                nt1.setAttechedNode(tt1);
            } else {
                
                tt1 = null;
                nt1 = null;
            }
            
            String tempE2 = tempE[1].replaceAll("[^\\p{L}\\p{N}]", "");
            if (tempE2.charAt(0) == 'N') {
                tt2 = NodeArray[index2];
                nt2 = new EventsViewPanel();
                nt2.setPreferredSize(new Dimension(50, 50));
                nt2.setBackground(Color.red);
                nt2.setAttechedNode(tt2);
            } else if (tempE2.charAt(0) == 'L') {
                tt2 = LeafArray[index2];
                nt2 = eventsPanelList.get(index2);
//                nt2 = new EventsViewPanel();
//                nt2.setPreferredSize(new Dimension(50,50));
//                nt2.setBackground(Color.red);
//                nt2.setAttechedNode(tt2);
            } else {
                int c = 0;
                tt2 = null;
                nt2 = null;
            }

            //gh.addVertex(tt1);
            //gh.addVertex(tt2);
            gh.addVertex(nt1);
            gh.addVertex(nt2);
            //System.out.println(tt1.getValue() + " " + tt2.getValue());

            gh.addEdge(new MyLink(weight, 0), nt1, nt2);
            tt1.addChildNode(tt2);
        }
        
        
        
    }
    
    public BufferedImage createImage2(JPanel panel) {
        
        int w = panel.getWidth();
        int h = panel.getHeight();
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bi.createGraphics();
        panel.paint(g);
        return bi;
    }
}
