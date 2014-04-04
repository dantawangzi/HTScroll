/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TasteAnalytics.Apollo.TopicRenderer;



import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.Action;
import prefuse.action.ActionList;
import prefuse.action.ItemAction;
import prefuse.action.RepaintAction;
import prefuse.action.animate.ColorAnimator;
import prefuse.action.animate.LocationAnimator;
import prefuse.action.animate.QualityControlAnimator;
import prefuse.action.animate.VisibilityAnimator;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.FontAction;
import prefuse.action.filter.FisheyeTreeFilter;
import prefuse.action.layout.CollapsedSubtreeLayout;
import prefuse.action.layout.graph.NodeLinkTreeLayout;
import prefuse.activity.SlowInSlowOutPacer;
import prefuse.controls.ControlAdapter;
import prefuse.controls.FocusControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Tree;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
import prefuse.data.io.TreeMLReader;
import prefuse.data.search.PrefixSearchTupleSet;
import prefuse.data.tuple.TupleSet;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.ShapeRenderer;
//import prefuse.render.TextItemRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.ui.JFastLabel;
import prefuse.util.ui.JSearchPanel;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;
import prefuse.visual.sort.TreeDepthItemSorter;
import com.TasteAnalytics.Apollo.TemporalView.TreeNode;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.assignment.DataSizeAction;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.controls.DragControl;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.demos.TreeMap.NodeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.render.Renderer;
import prefuse.render.ShapeRenderer;
//import edu.berkeley.guir.prefuse.render.TextImageItemRenderer;
//import edu.berkeley.guir.prefuse.render.ImageFactory;
import prefuse.util.PrefuseLib;
import prefuse.visual.NodeItem;


/**
 *
 * @author Li
 */
public class EventsPanelPrefuse extends Display {
        private static final String SIZE = "size";

    private static final String LABEL = "label";
    
    Graph graph;
    
    
    //TextImageItemRenderer imageRenderer;
    
    
//     private TextItemRenderer m_nodeRenderer;
    private EdgeRenderer m_edgeRenderer;
    
    private int m_orientation = Constants.ORIENT_LEFT_RIGHT;
    ShapeRenderer m_nodeRenderer;
    
    private List<TreeNode> myTree;

    public List<TreeNode> getMyTree() {
        return myTree;
    }

    public void setMyTree(List<TreeNode> myTree) {
        this.myTree = myTree;
    }
    
    
    
    public EventsPanelPrefuse(Graph gh, List<TreeNode> tree, HashMap hm){
    
         super(new Visualization());
   
         myTree = tree;
         
        graph = gh;
     
        gh.getNodeTable().addColumn("size", float.class);

        for (int i=0; i<gh.getNodeCount(); i++)
        {
            Node n = gh.getNode(i);
            String s1 = n.getString("label");
            n.setFloat(SIZE, 1.0f);
            Object o = hm.get(s1);
            Float c;
            
            if (o!=null)
            {
                c = (Float) o;
                
                n.setFloat(SIZE, (float)c*3);
                
            }
            
                        
        }
        
         gh.getNodeTable().addColumn("Color", Color.class);
        
//         
        
        m_vis.addGraph("graph", graph);
        m_vis.setInteractive("graph.edges", null, false);
        m_vis.setValue("graph.nodes", null, VisualItem.SHAPE, new Integer(Constants.SHAPE_RECTANGLE));
        

//       imageRenderer = new TextImageItemRenderer();
//        imageRenderer.setMaxImageDimensions(150,150);
//        imageRenderer.setImageSize(0.2);
//        imageRenderer.setHorizontalPadding(2);    
//        imageRenderer.setImageFactory(new ImageFactory());
//               
        
       m_nodeRenderer = new ShapeRenderer(10);
      // m_nodeRenderer.setBounds(null);
        
        //NodeRenderer noderRender = new NodeRenderer();
        
        EdgeRenderer edgeR = new EdgeRenderer(prefuse.Constants.EDGE_TYPE_CURVE, prefuse.Constants.EDGE_ARROW_NONE);

        
        DefaultRendererFactory drf = new DefaultRendererFactory();
        drf.setDefaultRenderer(m_nodeRenderer);
        drf.setDefaultEdgeRenderer(edgeR);
        m_vis.setRendererFactory(drf);

        int[] palette = new int[] {
            ColorLib.rgb(255,180,180), ColorLib.rgb(190,190,255)
        };
        ColorAction nStroke = new ColorAction("graph.nodes", VisualItem.STROKECOLOR);
        
//        int[] fillArray = new int[gh.getNodeCount()];
//        for (int i=0; i<gh.getNodeCount(); i++)
//        {
//            
//            int r = myTree.get(i).getColor().getRed();
//            int g = myTree.get(i).getColor().getGreen();
//            int b = myTree.get(i).getColor().getBlue();
//            
//            
//            fillArray[i] = ColorLib.rgb(r, g, b);
//            
//            
//            
//        }
                        
//        DataColorAction colornode = new DataColorAction("graph.nodes", "Color",
//    Constants.NOMINAL, VisualItem.FILLCOLOR, fillArray);
        
        
        
        nStroke.setDefaultColor(ColorLib.gray(100));

//        DataColorAction nFill = new DataColorAction("graph.nodes", "flag",
//            Constants.NOMINAL, VisualItem.FILLCOLOR, palette);
        ColorAction edges = new ColorAction("graph.edges",
            VisualItem.STROKECOLOR, ColorLib.gray(200));
//        ColorAction arrow = new ColorAction("graph.edges",
//            VisualItem.FILLCOLOR, ColorLib.gray(200));
        ActionList color = new ActionList();
        color.add(nStroke);
        //color.add(nFill);
        color.add(edges);
        //color.add(arrow);
       // color.add(colornode);
        
        
        
        DataSizeAction nodeDataSizeAction = new DataSizeAction("graph.nodes", SIZE);
        color.add(nodeDataSizeAction);
        m_vis.putAction("draw", color);
        
        
              NodeLinkTreeLayout treeLayout = new NodeLinkTreeLayout("graph",
                m_orientation, 100, 10, 20);
        treeLayout.setLayoutAnchor(new Point2D.Double(25,300));
        
        

        ActionList layout = new ActionList(Activity.INFINITY);
        layout.add(new ForceDirectedLayout("graph"));
        layout.add(new RepaintAction());

        
        
        
        drf.add(new InGroupPredicate("nodedec"), new LabelRenderer("id"));
        
        
        
        
        
        
        
  
        m_vis.putAction("treeLayout", treeLayout);
        
        
        
        m_vis.putAction("color", color);
       // m_vis.putAction("layout", layout);

        
        
//        //setSize(720, 500); // set display size
//        pan(360, 250);
        setHighQuality(true);
        addControlListener(new DragControl());
        addControlListener(new PanControl());
        addControlListener(new ZoomControl());
        
        
//         Node focus = gh.getNode(0);
//        PrefuseLib.setX(focus, null, 400);
//        PrefuseLib.setY(focus, null, 250);
      //  focusGroup.setTuple(focus);
        
        

        m_vis.run("color");
        m_vis.run("treeLayout");
        
        
        
        
        
        
}
}
