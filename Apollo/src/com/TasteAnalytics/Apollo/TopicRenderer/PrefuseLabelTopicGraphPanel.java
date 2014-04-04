/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TasteAnalytics.Apollo.TopicRenderer;

import com.TasteAnalytics.Apollo.datahandler.LDAHTTPClient;

import java.awt.Color;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPopupMenu;
import com.TasteAnalytics.Apollo.GUI.ViewController;
import java.awt.BorderLayout;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.layout.Layout;
import prefuse.action.layout.RandomLayout;
import prefuse.action.layout.graph.ForceDirectedLayout;
import static prefuse.action.layout.graph.ForceDirectedLayout.FORCEITEM;
import prefuse.activity.Activity;
import prefuse.controls.Control;
import prefuse.controls.ControlAdapter;
import prefuse.controls.DragControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Schema;
import prefuse.data.Table;
import prefuse.data.tuple.TupleSet;
import prefuse.render.AbstractShapeRenderer;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.render.ShapeRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.PrefuseLib;
import prefuse.util.force.DragForce;
import prefuse.util.force.Force;
import prefuse.util.force.ForceItem;
import prefuse.util.force.ForceSimulator;
import prefuse.util.force.Spring;
import prefuse.util.force.NBodyForce;
import prefuse.util.force.SpringForce;
import prefuse.util.ui.JRangeSlider;
import prefuse.util.ui.JValueSlider;
import prefuse.visual.DecoratorItem;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;

/**
 *
 * @author Li
 */
public class PrefuseLabelTopicGraphPanel extends Display {

    Graph graph;
    //Visualization vis;
    private static final String LABEL = "label";

    public ViewController parent;

    private JValueSlider edgeThreSlider;
    private JLabel edgeNumberLabel;
    
    private JPanel itemPanel = new JPanel();
    
        
    float global_edge_factor = 3;
    private JValueSlider edgeWeightSlider;

    public PrefuseLabelTopicGraphPanel(String folderPath, ViewController vc, List<List<Float>> disMatrix) throws FileNotFoundException, IOException {

        super(new Visualization());

        this.parent = vc;
        this.setLayout(new BorderLayout());
        itemPanel.setSize(this.getWidth(), this.getHeight());
        itemPanel.setLayout(new BorderLayout());
        add(itemPanel,BorderLayout.PAGE_START);
        
        edgeNumberLabel = new JLabel();

        setUpData(folderPath, disMatrix);

        System.out.println("prefuse data setup done");

        setUpVisualization();
        setUpRenderers();
        setUpActions();
        setUpDisplay();

        m_vis.run("color");
        m_vis.run("layout");
        
        

    }

    float[][] topicWeightPerLabel;
    float[][] topicWeightPerLabelNew;

    float[][] hellingerDis;
    HashMap<Integer, List<TopicWeight>> topicWeightPerLabelMap = new HashMap<Integer, List<TopicWeight>>();

    class TopicWeight {

        int index;
        float weight;

        TopicWeight(int i, float j) {
            index = i;
            weight = j;

        }
    }

    private float calculateSum(float[][] M1, float[][] M2, int row, int col) {
        float sum = 0;

        for (int i = 0; i < M1[row].length; i++) {
            sum += M1[row][i] * M2[i][col];
        }

        return sum;
    }
    int labelCount = 0;
    HashMap<Integer, String> labelDictMap;
    String folder = "";

    float average_of_hell = 0;
    double sntd = 0;

    private void setUpData(String folderPath, List<List<Float>> disMatrix) throws FileNotFoundException, IOException {

        labelDictMap = new HashMap<Integer, String>();
        folder = folderPath;
      

        if (parent.b_readFromDB) {
            
            
            
            
            LDAHTTPClient c = new LDAHTTPClient("http", parent.host, "2012");
            c.login();
            
             String dictString = "";
             for (Object r : (ArrayList) c.getJobDocs(parent.collection,"labeldict"))
             {
                 
                  dictString = (String) ((HashMap) r).get("dict");
             }
             
             
             labelCount = 0; 
            
            String[]tmpDictString = dictString.split("\n");
            
             labelCount = tmpDictString.length ;
              System.out.println("label count is " + labelCount);
            for (int i=0; i<tmpDictString.length; i++)
            {
                
                
                 String labeltext = tmpDictString[i].split("\t")[0];
                 String labelint = tmpDictString[i].split("\t")[1];
                 if("NONE".equals(labeltext))
                         continue;
                 
                labelDictMap.put(Integer.parseInt(labelint), labeltext);
                
                
            }
            labelCount--;
              topicWeightPerLabel = new float[labelCount][];
            String labeltopicString = "";
             for (Object r : (ArrayList) c.getJobDocs(parent.collection,"label_topics"))
             {
                 
                  labeltopicString = (String) ((HashMap) r).get("dict");
             }
            
             String[] tmpLabelTopicString = labeltopicString.split("\n");
            
             for (int i=0; i<labelCount; i++)
             {
                 
                  String line = tmpLabelTopicString[2*i+1];
                String labeltopicview[] = line.split("\\|");

                topicWeightPerLabel[i] = new float[labeltopicview.length];

                List<TopicWeight> templst = new ArrayList<TopicWeight>();
                topicWeightPerLabelMap.put(i, templst);
                for (int j = 0; j < labeltopicview.length; j++) {
                    String temp1[] = labeltopicview[j].split(" ");
                    int index = Integer.parseInt(temp1[0].replaceAll("\\D+", ""));
                    float weight = Float.parseFloat(temp1[1]);

                    topicWeightPerLabel[i][index] = weight;

                    TopicWeight tw = new TopicWeight(index, weight);
                    topicWeightPerLabelMap.get(i).add(tw);
                }
                 
                 
             }
             
             
             
            

        } else {
            String dictfile = folder + "labeldict";

            BufferedReader br = new BufferedReader(new FileReader(dictfile));

            labelCount = 0;
            while (br.readLine() != null) {

                labelCount++;
            }

            labelCount -= 2;
            br.close();

            System.out.println("label count is " + labelCount);

            br = new BufferedReader(new FileReader(dictfile));

            String rline = br.readLine();

            while (rline != null) {
                String labeltext = rline.split("\t")[0];
                String labelint = rline.split("\t")[1];

                labelDictMap.put(Integer.parseInt(labelint), labeltext);
                rline = br.readLine();
            }

            br.close();

              topicWeightPerLabel = new float[labelCount][];
            String file = folder + "label_topic_output.txt";

            br = new BufferedReader(new FileReader(file));
            String line = br.readLine();

            line = br.readLine();
            for (int i = 0; i < labelCount; i++) {
                line = br.readLine();
                line = br.readLine();
                String labeltopicview[] = line.split("\\|");

                topicWeightPerLabel[i] = new float[labeltopicview.length];

                List<TopicWeight> templst = new ArrayList<TopicWeight>();
                topicWeightPerLabelMap.put(i, templst);
                for (int j = 0; j < labeltopicview.length; j++) {
                    String temp1[] = labeltopicview[j].split(" ");
                    int index = Integer.parseInt(temp1[0].replaceAll("\\D+", ""));
                    float weight = Float.parseFloat(temp1[1]);
//                if (i == 0) {
//                    weight = 0;
//                }
                    topicWeightPerLabel[i][index] = weight;

                    TopicWeight tw = new TopicWeight(index, weight);
                    topicWeightPerLabelMap.get(i).add(tw);
                }

            }
            System.out.println("topicWeightPerLabel calculated..");
            br.close();
        }

//        for (int i = 0; i < topicWeightPerLabel.length; i++) {
//            for (int j = 0; j < topicWeightPerLabel[0].length; j++) {
//                System.out.print(topicWeightPerLabel[i][j] + " ");
//            }
//
//            System.out.println();
//
//        }
        topicWeightPerLabelNew = topicWeightPerLabel;

        ///////////////////////////////////////////////////////////////////////////////////////////
        // between is topicweight * topic-topic distance
        float max = -999999;
        float min = 999999;
        for (int i = 0; i < disMatrix.size(); i++) {
            for (int j = 0; j < disMatrix.get(i).size(); j++) {
                if (disMatrix.get(i).get(j) >= max) {
                    max = disMatrix.get(i).get(j);
                }
                if (disMatrix.get(i).get(j) <= min) {
                    min = disMatrix.get(i).get(j);
                }
            }
        }

        float[][] distanceMatrix = new float[disMatrix.size()][disMatrix.size()];

        for (int i = 0; i < disMatrix.size(); i++) {
            for (int j = 0; j < disMatrix.get(i).size(); j++) {
//                if (disMatrix.get(i).get(j)==0)
//                   distanceMatrix[i][j] = 0;
//                else
                distanceMatrix[i][j] = (1 - disMatrix.get(i).get(j));//1-(disMatrix.get(i).get(j) - min)/(max - min);

            }
        }

        int w = labelCount;
        int h = topicWeightPerLabel[0].length;

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {

                topicWeightPerLabelNew[i][j] = calculateSum(topicWeightPerLabel, distanceMatrix, i, j);

            }
        }

        ///////////////////////////////////////////////////////////////////////////////////////////
        // System.out.println(topicWeightPerLabel.length + " " + topicWeightPerLabel[0].length);
        float maxHellValue = -1;
        float minHellValue = 9999999;
        hellingerDis = new float[labelCount][labelCount];

        for (int i = 0; i < labelCount; i++) {

            for (int j = 0; j < labelCount; j++) {

                float sum = 0;
                if (i != j) {

                    for (int k = 0; k < topicWeightPerLabel[0].length; k++) {
                        //float temp = (float) (Math.sqrt(topicWeightPerLabel[i][k]) - (Math.sqrt(topicWeightPerLabel[j][k])));
                        float temp = (float) (Math.sqrt(topicWeightPerLabelNew[i][k]) - (Math.sqrt(topicWeightPerLabelNew[j][k])));
                        temp = (float) Math.pow(temp, 2);
                        sum += temp;
                    }
                }
                hellingerDis[i][j] = (float) (1 / Math.sqrt(2) * Math.sqrt(sum));

                if (hellingerDis[i][j] >= maxHellValue) {
                    maxHellValue = hellingerDis[i][j];
                }

                if (hellingerDis[i][j] <= minHellValue) {
                    minHellValue = hellingerDis[i][j];
                }
            }

        }

        float sum_of_hell = 0;

//        for (int i = 0; i < labelCount; i++) {
//            for (int j = 0; j < labelCount; j++) {
//
//                System.out.print(hellingerDis[i][j] + " ");
//
//            }
//            System.out.print("\n");
//        }
        for (int i = 0; i < labelCount; i++) {

            for (int j = (i + 1); j < labelCount; j++) {
                sum_of_hell += hellingerDis[i][j];

            }

        }

        int edgesnumber = (labelCount - 1) + (labelCount - 2) * (labelCount - 1) / 2;
        average_of_hell = sum_of_hell / edgesnumber;
        float sn_square = 0;
        int count = 0;
        for (int i = 0; i < labelCount; i++) {

            for (int j = (i + 1); j < labelCount; j++) {

                if (i != j) {
                    sn_square += (hellingerDis[i][j] - average_of_hell) * (hellingerDis[i][j] - average_of_hell);
                    count++;
                }
            }

        }

        sntd = Math.sqrt(sn_square / count);

        graph = new Graph();
        graph.addColumn("name", String.class);
        graph.addColumn("id", Integer.class);
        graph.addColumn("LabelText", String.class);
        graph.addColumn("selected", Boolean.class);

        for (int i = 0; i < labelCount; i++) {

            Node n = graph.addNode();
            n.set("id", i);
            n.set("name", "Label" + Integer.toString(i));
            n.set("LabelText", labelDictMap.get(i));
            n.set("selected", false);

        }

        System.out.println("mean value " + average_of_hell + " std is " + sntd + " edgesnumber " + edgesnumber + " " + count);

        
        updateEdges((float) (average_of_hell + sntd));

        PrintOutJsonEdges();
        
        
        
        edgeThreSlider = new JValueSlider("EdgeWeight Slider", minHellValue, maxHellValue, (average_of_hell + sntd));
        edgeWeightSlider = new JValueSlider("Weight Slider", 1, 10, 5);
        
        
        edgeThreSlider.setVisible(true);
        edgeWeightSlider.setVisible(true);

        edgeThreSlider.setForeground(Color.gray);

        
        
        edgeWeightSlider.addChangeListener(
                new ChangeListener() {
                     public void stateChanged(ChangeEvent e) {
                    Number value = edgeWeightSlider.getValue();
                    global_edge_factor = value.intValue();
                      m_vis.removeAction("layout");
                         

                         ActionList layout = new ActionList(Activity.INFINITY);

                        DataMountainForceLayout fdl = new DataMountainForceLayout("graph.nodes", false, hellingerDis);
                        fdl.setDataGroups("graph.nodes", "graph.edges");
                        layout.add(fdl);
                        layout.add(new FinalDecoratorLayout("nodedec"));

                        layout.add(new RepaintAction());
                        m_vis.putAction("layout", layout);
                        
                        m_vis.run("color");
                        m_vis.run("layout");
                }
                });
        
        edgeThreSlider.addChangeListener(
                new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {

                        Number value = edgeThreSlider.getValue();
                        updateEdges(value.floatValue());
                        
                         m_vis.removeAction("layout");
                         

                         ActionList layout = new ActionList(Activity.INFINITY);

                        DataMountainForceLayout fdl = new DataMountainForceLayout("graph.nodes", false, hellingerDis);
                        fdl.setDataGroups("graph.nodes", "graph.edges");
                        layout.add(fdl);
                        layout.add(new FinalDecoratorLayout("nodedec"));

                        layout.add(new RepaintAction());
                        m_vis.putAction("layout", layout);
                        
                        m_vis.run("color");
                        m_vis.run("layout");
//                     setUpVisualization();
//                        setUpRenderers();
//                        setUpActions();
//                        setUpDisplay();
//                        m_vis.run("color");
//                        m_vis.run("layout");
                    }

                });

        edgeThreSlider.setSize(this.getSize().width/3, 150);
        itemPanel.add(edgeThreSlider, BorderLayout.LINE_START);
        
           edgeWeightSlider.setSize(this.getSize().width/3, 150);
        itemPanel.add(edgeWeightSlider, BorderLayout.CENTER);

        edgeNumberLabel.setSize(this.getSize().width/3, 150);

        itemPanel.add(edgeNumberLabel, BorderLayout.LINE_END);

    }

    void PrintOutJsonEdges() {

       // System.out.println("labelCount = " + labelCount);
        PrintWriter out = null;
        try {
            out = new PrintWriter(folder + "labels.json");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PrefuseLabelTopicGraphPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        out.println("{");
        out.println("\t\"nodes\":[");
        for (int i = 0; i < labelCount; i++) {

            if (i == labelCount - 1) {
                out.println("\t\t{\"name\":" + "\"" + labelDictMap.get(i + 1) + "\"," + "\"group\":" + i + "}");
            } else {
                out.println("\t\t{\"name\":" + "\"" + labelDictMap.get(i + 1) + "\"," + "\"group\":" + i + "},");
            }

        }

        out.println("\t],");
        out.println("\t\"links\":[");

        
        
       // System.out.println(graph.getEdgeCount());
        for (int k=0; k<graph.getEdgeCount(); k++)
        {
            
            Edge e =  graph.getEdge(k);
            
           // System.out.println(e);
            
            
            int i = (Integer) e.getSourceNode().get("id");
            int j = (Integer) e.getTargetNode().get("id");

            if (k== graph.getEdgeCount()-1)
                out.println("\t\t{\"source\":" + i + ",\"target\":" + j + ",\"value\":" + hellingerDis[i][j] + "}");
            else
                out.println("\t\t{\"source\":" + i + ",\"target\":" + j + ",\"value\":" + hellingerDis[i][j] + "},");
            
        }
//        TupleSet edges = graph.getEdges();
//        int edgesize = edges.getTupleCount();
//        Iterator s = edges.tuples();
//
//        while (s.hasNext()) {
//            Edge e = (Edge) s.next();
//            int i = (Integer) e.getSourceNode().get("id");
//            int j = (Integer) e.getTargetNode().get("id");
//
//            out.println("\t\t{\"source\":" + i + ",\"target\":" + j + ",\"value\":" + hellingerDis[i][j] + "},");
//
////                int value = (int) (hellingerDis[i][j]>40?hellingerDis[i][j]:0);
////                
////                if (i == (number_of_nodes-2) && (j==number_of_nodes-1))
////                {
////                    out.println("\t\t{\"source\":" + i + ",\"target\":" + j + ",\"value\":" + value + "}");                
////                }
////                else
////                    out.println("\t\t{\"source\":" + i + ",\"target\":" + j + ",\"value\":" + value + "},");                
//        }

        out.println("\t]");
        out.println("}");
        out.close();

    }

    void updateEdges(float currentV) {

        int number_of_nodes = labelCount; //label numbers

         //output to json
        for (int i = 0; i < graph.getEdgeCount(); i++) {
            graph.removeEdge(i);

        }

            //graph.clearEdges();
           //graph.getEdgeTable().clear();
        for (int i = 0; i < number_of_nodes; i++) {
            //for (int j = number_of_nodes - 1; j > 1; j--) 
            for (int j = (i + 1); j < number_of_nodes; j++) {
                //if (j != i) 
                {
                    if (hellingerDis[i][j] >= currentV) {
                        graph.addEdge(i, j);
                    }
                    //{"source":1,"target":0,"value":1},
                }
            }
        }

        //System.out.println(graph.getEdgeCount() + " edges in graph.");
        edgeNumberLabel.setText(graph.getEdgeCount() + " edges in graph.");

    }

    private void setUpVisualization() {
        //m_vis.removeGroup("graph");

        m_vis.add("graph", graph);

    }

    private void setUpRenderers() {
        FinalRenderer r = new FinalRenderer();//ShapeRenderer();

        int scale = 10;
        DefaultRendererFactory drf = new DefaultRendererFactory(r);

        drf.add(new InGroupPredicate("nodedec"), new LabelRenderer("LabelText"));
        drf.add(new InGroupPredicate("graph.edges"), new myEdgeRenderer(Constants.EDGE_TYPE_CURVE, Constants.EDGE_ARROW_FORWARD/*Constants.EDGE_ARROW_REVERSE*/));

        m_vis.setRendererFactory(drf);

        final Schema DECORATOR_SCHEMA = PrefuseLib.getVisualItemSchema();
        DECORATOR_SCHEMA.setDefault(VisualItem.INTERACTIVE, false);
        DECORATOR_SCHEMA.setDefault(VisualItem.TEXTCOLOR, ColorLib.rgb(0, 0, 0));
        DECORATOR_SCHEMA.setDefault(VisualItem.FONT, FontLib.getFont("Tahoma", 2 * scale));
        m_vis.addDecorators("nodedec", "graph.nodes", DECORATOR_SCHEMA);

    }

    private void setUpActions() {
        //ColorAction fill = new ColorAction("graph.nodes", VisualItem.FILLCOLOR, ColorLib.rgb(0, 200, 0));

        DataColorAction fill = new DataColorAction("graph.nodes", "selected",
                Constants.NOMINAL, VisualItem.FILLCOLOR, palette);

        ColorAction edges = new ColorAction("graph.edges", VisualItem.STROKECOLOR, ColorLib.gray(200));
        final ColorAction borderColor = new BorderColorAction("graph.nodes");

        ActionList colorOri = new ActionList();
        colorOri.add(fill);

        ActionList color = new ActionList(/*Activity.INFINITY*/);
        color.add(borderColor);
        color.add(fill);
        color.add(edges);

        //   ActionList layout = new ActionList(100000000L);
        ActionList layout = new ActionList(Activity.INFINITY);

//RandomLayout fdl = new RandomLayout();
        //ForceDirectedLayout fdl = new ForceDirectedLayout("graph", false);
        DataMountainForceLayout fdl = new DataMountainForceLayout("graph.nodes", false, hellingerDis);
        fdl.setDataGroups("graph.nodes", "graph.edges");

        //ForceSimulator a = fdl.getForceSimulator();
        // Force[] x = a.getForces();
//        NBodyForce nbf = (NBodyForce) x[0];
//        DragForce df = (DragForce) x[1];
//        SpringForce sf = (SpringForce) x[2];
        layout.add(fdl);
        layout.add(new FinalDecoratorLayout("nodedec"));

//        ActionList layout = new ActionList(Activity.INFINITY);
//        
//        layout.add(new RandomLayout("graph"));
        layout.add(new RepaintAction());

        //m_vis.setValue("graph.nodes", null, VisualItem.FIXED, true);
        m_vis.putAction("color", color);
        m_vis.putAction("layout", layout);
        
       

    }

    private void setUpDisplay() {

        //zoom(new Point2D.Double(0,0), 15);
//        double l = this.getScale();
//        System.out.println("scale: " + l);
        setSize(this.getWidth(), this.getHeight());
        setHighQuality(true);
        addControlListener(new DragControl());
        addControlListener(new PanControl());
        addControlListener(new ZoomControl());
        addControlListener(new FinalControlListener());

    }

    int[] palette = {ColorLib.rgb(224, 243, 219)};

    HashMap<String, List<Integer>> highlightedLabelsMap = new HashMap<String, List<Integer>>();
    HashMap<String, List<Float>> labelHighlightWeightMap = new HashMap<String, List<Float>>();
//    HashMap<String, Integer> labelHighlightIndexMap = new HashMap<String, Integer>();

    HashMap<String, Color> labelHighlightColorMap = new HashMap<String, Color>();

    public class FinalControlListener extends ControlAdapter implements Control {

        @Override
        public void itemClicked(VisualItem item, MouseEvent e) {
            if (item instanceof NodeItem) {

                Object xx = item.get("selected");

                if ((Boolean) xx) {
                    boolean currV = (Boolean) xx;
                    item.set("selected", !currV);
                    if (currV) {
                        item.setFillColor(palette[0]);
                    }
//                     else {
//                        item.setFillColor(palette[0]);
//                    }

                    item.getVisualization().repaint();
                    String name = ((String) item.get("name"));

                    parent.labelColor.push(labelHighlightColorMap.get(name));

                    labelHighlightWeightMap.remove(name);
                    highlightedLabelsMap.remove(name);
                    labelHighlightColorMap.remove(name);

                    parent.stateChangedFromLabelToTopic(highlightedLabelsMap, labelHighlightWeightMap, labelHighlightColorMap);
                    
                    
                    
                } else {
                    if (labelHighlightWeightMap.size() < 5) {

                        //int index = labelHighlightWeightMap.size();
                        Color currentColor = (Color) parent.labelColor.pop();
                        boolean currV = (Boolean) xx;
                        item.set("selected", !currV);

                        //System.out.println(item.get("selected"));
                        //                ni.setFillColor(palette[1]);                                        
                        if (currV) {
                            item.setFillColor(palette[0]);

                        } else {
                            Color cc = currentColor;

                            item.setFillColor(ColorLib.rgb(cc.getRed(), cc.getGreen(), cc.getBlue()));

                            //item.setFillColor(palette[0]);
                        }

                        item.getVisualization().repaint();

                        String name = ((String) item.get("name"));

//                if (labelHighlightWeightMap.containsKey(name))
//                {
//                    labelHighlightWeightMap.remove(name);
//                    highlightedLabelsMap.remove(name);
//                }
//                else
                        {

//                    if (labelHighlightWeightMap.size()<4)
//                    {
                            int age = (Integer) item.get("id");
                            List<TopicWeight> x = topicWeightPerLabelMap.get(age);
                            JPopupMenu jpub = new JPopupMenu();
                            jpub.add("name: " + name);

                            float SumOfTopic = 0;

                            for (int i = 0; i < x.size(); i++) {
                                SumOfTopic += x.get(i).weight;
                            }
                            jpub.add("topicweight list: " + SumOfTopic);
                            int count = 0;
                            List<Float> tmpLabelHighlightWeight = new ArrayList<Float>();
                            List<Integer> tmplabelHighlight = new ArrayList<Integer>();
                            float tmpSumTopic = 0;
                            for (int i = 0; i < x.size(); i++) {
                                if (x.get(i).weight <= 0.05 * SumOfTopic) {
                                    continue;
                                }

                                count++;
                                tmpLabelHighlightWeight.add(x.get(i).weight / SumOfTopic);
                                tmplabelHighlight.add(x.get(i).index);
                                jpub.add(x.get(i).index + " " + x.get(i).weight);
                                tmpSumTopic += x.get(i).weight;
                                if (tmpSumTopic >= 0.7 * SumOfTopic) {
                                    break;
                                }

                            }

                            labelHighlightColorMap.put(name, currentColor);
                            labelHighlightWeightMap.put(name, tmpLabelHighlightWeight);
                            highlightedLabelsMap.put(name, tmplabelHighlight);

//                            jpub.show(e.getComponent(), (int) item.getX(),
//                                    (int) item.getY());
                        }

                        parent.stateChangedFromLabelToTopic(highlightedLabelsMap, labelHighlightWeightMap, labelHighlightColorMap);
                    }

                    //}
                }
            }
        }
    }

    public class FinalRenderer extends AbstractShapeRenderer {
//protected RectangularShape m_box = new Rectangle2D.Double();
        //protected Ellipse2D m_box = new Ellipse2D.Double();

        protected RoundRectangle2D m_box = new RoundRectangle2D.Double();

        @Override
        protected Shape getRawShape(VisualItem item) {

            int length = ((String) item.get("LabelText")).length();
            m_box.setRoundRect(0, 0, 0, 0, 13 * length * .25, 30 * .5);
            m_box.setFrame(item.getX(), item.getY(), 13 * length, 30);

            return m_box;
        }

    }

//    
//         protected double getLineWidth(VisualItem item) {
//             if (item instanceof EdgeItem)
//                return 30;//item.getSize();
//             else
//                 return item.getSize();
//          }
    public class FinalDecoratorLayout extends Layout {

        public FinalDecoratorLayout(String group) {
            super(group);
        }

        public void run(double frac) {
            Iterator iter = m_vis.items(m_group);
            while (iter.hasNext()) {
                DecoratorItem decorator = (DecoratorItem) iter.next();
                VisualItem decoratedItem = decorator.getDecoratedItem();
                Rectangle2D bounds = decoratedItem.getBounds();
                double x = bounds.getCenterX();
                double y = bounds.getCenterY();
                setX(decorator, null, x);
                setY(decorator, null, y);
            }

        }
    }

    public static class BorderColorAction extends ColorAction {

        public BorderColorAction(String group) {
            super(group, VisualItem.STROKECOLOR);
        }

        public int getColor(VisualItem item) {
            NodeItem nitem = (NodeItem) item;
            if (nitem.isHover()) {
                return ColorLib.rgb(255, 12, 19);
            }

            int depth = nitem.getDepth();
            if (depth < 2) {
                return ColorLib.gray(100);
            } else if (depth < 4) {
                return ColorLib.gray(75);
            } else {
                return ColorLib.gray(50);
            }
        }
    }

    public class myEdgeRenderer extends EdgeRenderer {

        public myEdgeRenderer() {
            super();
        }

        private myEdgeRenderer(int EDGE_TYPE_CURVE) {
            super(EDGE_TYPE_CURVE);
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        private myEdgeRenderer(int edgeType, int arrowType) {
            super(edgeType, arrowType);
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        protected double getLineWidth(VisualItem item) {
            double width = 0;

            if (item instanceof EdgeItem) {
                width = 4;
            }

            return width;

        }

    }

    public class DataMountainForceLayout extends ForceDirectedLayout {

        float[][] hellingerDistance;

//        public DataMountainForceLayout(boolean enforceBounds) {
//            super("data", enforceBounds, false);
//
//            ForceSimulator fsim = new ForceSimulator();
//            //fsim.addForce(new NBodyForce(-0.4f, 25f, NBodyForce.DEFAULT_THETA));
//            fsim.addForce(new NBodyForce(-8.0f, 2500f, NBodyForce.DEFAULT_THETA));
//            fsim.addForce(new SpringForce(1e-5f, 0f));
//            fsim.addForce(new DragForce());
//            setForceSimulator(fsim);
//
//            m_nodeGroup = "data";
//            m_edgeGroup = null;
//        }
        public DataMountainForceLayout(String graph, boolean enforceBounds, float[][] edgeWeight) {
            super(graph, enforceBounds, false);

            ForceSimulator fsim = new ForceSimulator();
            //fsim.addForce(new NBodyForce(-0.4f, 25f, NBodyForce.DEFAULT_THETA));
            fsim.addForce(new NBodyForce(-0.4f, 30f, NBodyForce.DEFAULT_THETA));
            fsim.addForce(new SpringForce(1e-5f, 0f));
            fsim.addForce(new DragForce());
            setForceSimulator(fsim);

            hellingerDistance = edgeWeight;
            m_nodeGroup = graph;
            m_group = graph;
            //m_edgeGroup = null;

        }

        protected float getMassValue(VisualItem n) {
            return n.isHover() ? 5f : 1f;
        }

        @Override
        protected void initSimulator(ForceSimulator fsim) {
            // make sure we have force items to work with
            TupleSet t = (TupleSet) m_vis.getGroup(m_group);
            //System.out.println(t);
            t.addColumns(FORCEITEM_SCHEMA);

            Iterator iter = m_vis.visibleItems(m_nodeGroup);
            while (iter.hasNext()) {
                VisualItem item = (VisualItem) iter.next();
                // get force item
                ForceItem fitem = (ForceItem) item.get(FORCEITEM);
                if (fitem == null) {
                    fitem = new ForceItem();
                    item.set(FORCEITEM, fitem);
                }
                fitem.location[0] = (float) item.getEndX();
                fitem.location[1] = (float) item.getEndY();
                fitem.mass = getMassValue(item);
                fitem.mass = 3;//setMassValue(1);

                // get spring anchor
//                ForceItem aitem = (ForceItem)item.get(FORCEITEM);
//                if ( aitem == null ) {
//                    aitem = new ForceItem();
//                    item.set(FORCEITEM, aitem);
//                    aitem.location[0] = fitem.location[0];
//                    aitem.location[1] = fitem.location[1];
//                }
//                
                fsim.addItem(fitem);
//                fsim.addSpring(fitem, aitem, 0);
            }

            if (m_edgeGroup != null) {
                iter = m_vis.visibleItems(m_edgeGroup);
                while (iter.hasNext()) {
                    EdgeItem e = (EdgeItem) iter.next();
                    NodeItem n1 = e.getSourceItem();
                    ForceItem f1 = (ForceItem) n1.get(FORCEITEM);
                    NodeItem n2 = e.getTargetItem();
                    ForceItem f2 = (ForceItem) n2.get(FORCEITEM);
                    float coeff = getSpringCoefficient(e);
                    float slen = getSpringLength(e);

                    int idx1 = (Integer) n1.get("id");
                    int idx2 = (Integer) n2.get("id");

//                    fsim.addForce(null);
                    slen = hellingerDistance[idx1][idx2] * global_edge_factor;
                    //fsim.addSpring(f1, f2,  -1.f, slen);
                    fsim.addSpring(f1, f2, (coeff >= 0 ? coeff : -1.f), slen);

                    //fsim.addSpring(f1, f2, (coeff>=0?coeff:-1.f), (slen>=0?slen:-1.f));
                }
            }

        }
    } // end of inner class DataMountainForceLayout
}
