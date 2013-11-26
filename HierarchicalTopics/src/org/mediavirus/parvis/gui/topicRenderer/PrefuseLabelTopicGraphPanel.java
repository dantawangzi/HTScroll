/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mediavirus.parvis.gui.topicRenderer;

import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.swing.JPopupMenu;
import org.mediavirus.parvis.gui.ViewController;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
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
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Schema;
import prefuse.data.tuple.TupleSet;
import prefuse.render.AbstractShapeRenderer;
import prefuse.render.DefaultRendererFactory;
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
    
    
    public PrefuseLabelTopicGraphPanel(String folderPath, ViewController vc, List<List<Float>> disMatrix) throws FileNotFoundException, IOException {
        
        super(new Visualization());

        this.parent = vc;

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
    
    
    private float calculateSum(float[][] M1, float[][]M2, int row, int col)
    {
        float sum = 0;
        
        for (int i=0; i<M1[row].length; i++)
        {            
            sum+= M1[row][i]*M2[i][col];
        }
        
        
        
        
        return sum;
    }

    private void setUpData(String folderPath,  List<List<Float>> disMatrix) throws FileNotFoundException, IOException {

        
        

        HashMap<Integer, String> labelDictMap = new HashMap<Integer, String>();


        String dictfile = folderPath + "labeldict";

        BufferedReader br = new BufferedReader(new FileReader(dictfile));

        int labelCount = 0;
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

        String file = folderPath + "label_topic_output.txt";

        br = new BufferedReader(new FileReader(file));
        String line = br.readLine();

        line = br.readLine();

        topicWeightPerLabel = new float[labelCount][];

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
                topicWeightPerLabel[i][index] = weight;
                TopicWeight tw = new TopicWeight(index, weight);
                topicWeightPerLabelMap.get(i).add(tw);
            }


        }
        
        System.out.println("topicWeightPerLabel calculated..");
        
        
        for (int i=0; i<topicWeightPerLabel.length; i++)
        {
            for (int j=0; j<topicWeightPerLabel[0].length; j++)
                 System.out.print(topicWeightPerLabel[i][j] + " ");
            
            
             System.out.println();
             
             
        }
        topicWeightPerLabelNew = topicWeightPerLabel;

        
        ///////////////////////////////////////////////////////////////////////////////////////////
        // between is topicweight * topic-topic distance
        float max = -999999;
        float min = 999999;
        for (int i=0; i<disMatrix.size();i++)
        {
            for(int j=0; j<disMatrix.get(i).size(); j++)
            {
                if (disMatrix.get(i).get(j)>=max)
                    max = disMatrix.get(i).get(j);
                if (disMatrix.get(i).get(j)<=min)
                    min = disMatrix.get(i).get(j);
            }
        }
        
        float[][] distanceMatrix = new float[disMatrix.size()][disMatrix.size()];
        
        for (int i=0; i<disMatrix.size();i++)
        {
            for(int j=0; j<disMatrix.get(i).size(); j++)
            {
//                if (disMatrix.get(i).get(j)==0)
//                   distanceMatrix[i][j] = 0;
//                else
                 distanceMatrix[i][j] = (1-disMatrix.get(i).get(j));//1-(disMatrix.get(i).get(j) - min)/(max - min);
                
            }
        }
        

        int w = labelCount;
        int h = topicWeightPerLabel[0].length;
        
        for (int i=0; i<w; i++)
        {
            for (int j=0; j<h; j++)
            {
                
                topicWeightPerLabelNew[i][j] = calculateSum(topicWeightPerLabel, distanceMatrix, i, j);
                

                
            }                        
        }



        br.close();
        
        ///////////////////////////////////////////////////////////////////////////////////////////

       // System.out.println(topicWeightPerLabel.length + " " + topicWeightPerLabel[0].length);
        
        
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
            }

        }



        for (int i = 0; i < labelCount; i++) {
            for (int j = 0; j < labelCount; j++) {

                System.out.print(hellingerDis[i][j] + " ");

            }
            System.out.print("\n");
        }



        graph = new Graph();

        int number_of_nodes = labelCount; //label numbers




        graph.addColumn("name", String.class);
        graph.addColumn("id", Integer.class);
        graph.addColumn("LabelText", String.class);
        for (int i = 0; i < number_of_nodes; i++) {

            Node n = graph.addNode();
            n.set("id", i);
            n.set("name", "Label" + Integer.toString(i));
            n.set("LabelText", labelDictMap.get(i + 1));
        }


        for (int i = 0; i < number_of_nodes; i++) {
            for (int j = number_of_nodes - 1; j > 1; j--) {
                if (j != i) {
                    graph.addEdge(i, j);

                }
            }
        }


    }

    private void setUpVisualization() {
        m_vis.add("graph", graph);

    }

    private void setUpRenderers() {
        FinalRenderer r = new FinalRenderer();//ShapeRenderer();


        DefaultRendererFactory drf = new DefaultRendererFactory(r);

        drf.add(new InGroupPredicate("nodedec"), new LabelRenderer("LabelText"));
        m_vis.setRendererFactory(drf);


        final Schema DECORATOR_SCHEMA = PrefuseLib.getVisualItemSchema();
        DECORATOR_SCHEMA.setDefault(VisualItem.INTERACTIVE, false);
        DECORATOR_SCHEMA.setDefault(VisualItem.TEXTCOLOR, ColorLib.rgb(0, 0, 0));
        DECORATOR_SCHEMA.setDefault(VisualItem.FONT, FontLib.getFont("Tahoma", 16));
        m_vis.addDecorators("nodedec", "graph.nodes", DECORATOR_SCHEMA);




    }

    private void setUpActions() {
        ColorAction fill = new ColorAction("graph.nodes", VisualItem.FILLCOLOR, ColorLib.rgb(0, 200, 0));
        ColorAction edges = new ColorAction("graph.edges", VisualItem.STROKECOLOR, ColorLib.gray(200));

        ActionList color = new ActionList();
        color.add(fill);
        color.add(edges);

        //ActionList layout = new ActionList();
        ActionList layout = new ActionList(Activity.INFINITY);


        //ForceDirectedLayout fdl = new ForceDirectedLayout("graph", false);

        DataMountainForceLayout fdl = new DataMountainForceLayout("graph", false, hellingerDis);
        fdl.setDataGroups("graph.nodes", "graph.edges");


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

        setHighQuality(true);
        addControlListener(new DragControl());
        addControlListener(new PanControl());
        addControlListener(new ZoomControl());
        addControlListener(new FinalControlListener());

    }
    int[] palette = {ColorLib.rgb(200, 0, 0), ColorLib.rgb(0, 0, 200)};

    public class FinalControlListener extends ControlAdapter implements Control {

        @Override
        public void itemClicked(VisualItem item, MouseEvent e) {
            if (item instanceof NodeItem) {
                String name = ((String) item.get("name"));
                int age = (Integer) item.get("id");
                List<TopicWeight> x = topicWeightPerLabelMap.get(age);
                JPopupMenu jpub = new JPopupMenu();
                jpub.add("name: " + name);                
                jpub.add("topicweight list: ");
                for (int i = 0; i < x.size() / 4; i++) {
                    jpub.add("Topic" + x.get(i).index + " Weight " + x.get(i).weight);

                }
                jpub.show(e.getComponent(), (int) item.getX(),
                        (int) item.getY());
            }
        }
    }

    public class FinalRenderer extends AbstractShapeRenderer {
//protected RectangularShape m_box = new Rectangle2D.Double();
        //protected Ellipse2D m_box = new Ellipse2D.Double();

        protected Rectangle2D m_box = new Rectangle2D.Double();

        @Override
        protected Shape getRawShape(VisualItem item) {
            m_box.setFrame(item.getX(), item.getY(), 100, 30);
            return m_box;
        }
    }

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

    public class DataMountainForceLayout extends ForceDirectedLayout {

        float[][] hellingerDistance;

        public DataMountainForceLayout(boolean enforceBounds) {
            super("data", enforceBounds, false);

            ForceSimulator fsim = new ForceSimulator();
            fsim.addForce(new NBodyForce(-0.4f, 25f, NBodyForce.DEFAULT_THETA));
            fsim.addForce(new SpringForce(1e-5f, 0f));
            fsim.addForce(new DragForce());
            setForceSimulator(fsim);

            m_nodeGroup = "data";
            m_edgeGroup = null;
        }

        public DataMountainForceLayout(String graph, boolean enforceBounds, float[][] edgeWeight) {
            super(graph, enforceBounds, false);

            ForceSimulator fsim = new ForceSimulator();
            fsim.addForce(new NBodyForce(-0.4f, 25f, NBodyForce.DEFAULT_THETA));
            fsim.addForce(new SpringForce(1e-5f, 0f));
            fsim.addForce(new DragForce());
            setForceSimulator(fsim);

            hellingerDistance = edgeWeight;
            m_nodeGroup = graph;
            //m_edgeGroup = null;



        }

        protected float getMassValue(VisualItem n) {
            return n.isHover() ? 5f : 1f;
        }

        @Override
        protected void initSimulator(ForceSimulator fsim) {
            // make sure we have force items to work with
            TupleSet t = (TupleSet) m_vis.getGroup(m_group);
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
                    
                    slen = hellingerDistance[idx1][idx2]* 1;
                    
                    fsim.addSpring(f1, f2, (coeff >= 0 ? coeff : -1.f), slen);

                    //fsim.addSpring(f1, f2, (coeff>=0?coeff:-1.f), (slen>=0?slen:-1.f));
                }
            }
        }
    } // end of inner class DataMountainForceLayout
}
