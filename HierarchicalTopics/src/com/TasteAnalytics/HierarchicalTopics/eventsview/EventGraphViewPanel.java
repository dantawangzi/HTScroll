/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TasteAnalytics.HierarchicalTopics.eventsview;

//
//import prefuse.Constants;
//import prefuse.Display;
//import prefuse.Visualization;
//import prefuse.action.Action;
//import prefuse.action.ActionList;
//import prefuse.action.ItemAction;
//import prefuse.action.RepaintAction;
//import prefuse.action.animate.ColorAnimator;
//import prefuse.action.animate.LocationAnimator;
//import prefuse.action.animate.QualityControlAnimator;
//import prefuse.action.animate.VisibilityAnimator;
//import prefuse.action.assignment.ColorAction;
//import prefuse.action.assignment.FontAction;
//import prefuse.action.filter.FisheyeTreeFilter;
//import prefuse.action.layout.CollapsedSubtreeLayout;
//import prefuse.action.layout.graph.NodeLinkTreeLayout;
//import prefuse.activity.SlowInSlowOutPacer;
//import prefuse.controls.ControlAdapter;
//import prefuse.controls.FocusControl;
//import prefuse.controls.PanControl;
//import prefuse.controls.ZoomControl;
//import prefuse.controls.ZoomToFitControl;
//import prefuse.data.Tree;
//import prefuse.data.Tuple;
//import prefuse.data.event.TupleSetListener;
//import prefuse.data.io.TreeMLReader;
//import prefuse.data.search.PrefixSearchTupleSet;
//import prefuse.data.tuple.TupleSet;
//import prefuse.render.DefaultRendererFactory;
//import prefuse.render.EdgeRenderer;
//import prefuse.render.ShapeRenderer;
////import prefuse.render.TextItemRenderer;
//import prefuse.util.ColorLib;
//import prefuse.util.FontLib;
//import prefuse.util.ui.JFastLabel;
//import prefuse.util.ui.JSearchPanel;
//import prefuse.visual.VisualItem;
//import prefuse.visual.expression.InGroupPredicate;
//import prefuse.visual.sort.TreeDepthItemSorter;
//
//
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.graph.util.TreeUtils;
import edu.uci.ics.jung.samples.LensVertexImageShaperDemo;
import edu.uci.ics.jung.samples.VertexImageShaperDemo;
import edu.uci.ics.jung.samples.VertexImageShaperDemo.DemoVertexIconShapeTransformer;
import edu.uci.ics.jung.samples.VertexImageShaperDemo.DemoVertexIconTransformer;
import edu.uci.ics.jung.samples.VertexImageShaperDemo.PickWithIconListener;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.LayeredIcon;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.DefaultVertexIconTransformer;
import edu.uci.ics.jung.visualization.decorators.EllipseVertexShapeTransformer;
import edu.uci.ics.jung.visualization.decorators.VertexIconShapeTransformer;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.renderers.BasicVertexRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;
import edu.uci.ics.jung.visualization.transform.MutableTransformerDecorator;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.apache.commons.collections15.Transformer;
import com.TasteAnalytics.HierarchicalTopics.temporalView.renderer.TreeNode;
import com.TasteAnalytics.HierarchicalTopics.topicRenderer.TopicGraphViewFrame;
import com.TasteAnalytics.HierarchicalTopics.topicRenderer.TopicGraphViewFrame.HorizontalOverlappingTreeLayout;
//import javax.swing.KeyStroke;
//import javax.swing.SwingConstants;
//import org.mediavirus.parvis.gui.temporalView.renderer.TreeNode;
//import prefuse.action.assignment.DataColorAction;
//import prefuse.action.assignment.DataSizeAction;
//import prefuse.action.layout.graph.ForceDirectedLayout;
//import prefuse.activity.Activity;
//import prefuse.controls.DragControl;
//import prefuse.data.Graph;
//import prefuse.data.Node;
//import prefuse.demos.TreeMap.NodeRenderer;
//import prefuse.render.LabelRenderer;
//import prefuse.render.Renderer;
//import prefuse.render.ShapeRenderer;
//
//
//import edu.berkeley.guir.prefuse.render.TextImageItemRenderer;
//import edu.berkeley.guir.prefuse.render.ImageFactory;
//import prefuse.util.PrefuseLib;
//import prefuse.visual.NodeItem;
//
//
//
//

/**
 *
 * @author Li
 */
public class EventGraphViewPanel extends JPanel {

    VisualizationViewer<Object, TopicGraphViewFrame.MyLink> vv;
    HorizontalOverlappingTreeLayout HtreeLayout;
    private int node_width_interval = 150;
    private int node_height_interval = 40;
    private DelegateForest<Object, TopicGraphViewFrame.MyLink> gh;

    public VisualizationViewer<Object, TopicGraphViewFrame.MyLink> getVv() {
        return vv;
    }

    public EventGraphViewPanel(DelegateForest<Object, TopicGraphViewFrame.MyLink> gg, List<TreeNode> tree, HashMap hm) {

        gh = gg;

        this.setPreferredSize(new Dimension(1000, 1000));

        HtreeLayout = new HorizontalOverlappingTreeLayout<Object, TopicGraphViewFrame.MyLink>(gh, node_width_interval, node_height_interval);

        vv = new VisualizationViewer<Object, TopicGraphViewFrame.MyLink>(HtreeLayout, new Dimension(this.getWidth(), this.getHeight()));
        
        //vv.setBackground(Color.white);

        //GraphZoomScrollPane zoompanel = new GraphZoomScrollPane(vv);

        //this.add(zoompanel);

        //final DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse();
        //vv.setGraphMouse(graphMouse);
       
        
        //graphMouse.setMode(ModalGraphMouse.Mode.PICKING);

        //final VertexIconShapeTransformer<Object> vertexIconShapeTransformer =
          //      new VertexIconShapeTransformer<Object>(new EllipseVertexShapeTransformer<Object>());

        //final DefaultVertexIconTransformer<Object> vertexIconTransformer = new DefaultVertexIconTransformer<Object>();


        //Map<Object, Icon> iconMap = new HashMap<Object, Icon>();
        
        
//        for (int i = 0; i < tree.size(); i++) {
//           
//            
//            try {
//                
//                String key = tree.get(i).getValue();
//                
//                Icon icon = (Icon)hm.get(key);
//                //LayeredIcon c = new LayeredIcon(new ImageIcon(Canvas.class.getResource("c:\\test.png")).getImage());               
//                 // Icon icon = c;
//                  
//                  
//                iconMap.put(tree.get(i), icon);
//                
//                
//            } catch (Exception ex) {
//                System.err.println("i dont hava an icon");
//            }
//        }
        
         

        //vv.getRenderer().setVertexRenderer(new BasicVertexRenderer());
         


        //vertexIconShapeTransformer.setIconMap(iconMap);
        //vertexIconTransformer.setIconMap(iconMap);

        //vv.getRenderContext().setVertexShapeTransformer(vertexIconShapeTransformer);
        //vv.getRenderContext().setVertexIconTransformer(vertexIconTransformer);

        
//        
//        PickedState<Object> ps = vv.getPickedVertexState();
//        ps.addItemListener(new PickWithIconListener<Object>(vertexIconTransformer));

        
        
        
    //vertexIconTransformer.setFillImages(true);

//        Transformer<Object, String> VertexLabel = new Transformer<Object, String>() {
//            public String transform(Object i) {
//
//                    if (i instanceof EventsViewPanel) {
//                    return String.valueOf(((EventsViewPanel) i).getAttechedNode().getLeafNodeWeight());
//
//                } else {
//                    return "000";
//                }
//                    
//                //                if (i instanceof TreeNode) {
////                    return String.valueOf(((TreeNode) i).getLeafNodeWeight());
////
////                } else {
////                    return "000";
////                }
//
//            }
//        };


//        Transformer<Object, Shape> vertexSize = new Transformer<Object, Shape>() {
//            public Shape transform(Object i) {
//                Shape s = new Ellipse2D.Double(0, 0, 10, 10);
//
//                if (i instanceof TreeNode) {
//                    TreeNode t = (TreeNode) i;
//
//                    if (!t.getChildren().isEmpty()) {
//
////                        int size = (int)t.getLeafNodeWeight() * 20;
////                        s = new Ellipse2D.Double(-size / 2, -size / 2, size, size);                                                
//                        s = new Ellipse2D.Double(-15, -15, 30, 30);
//
//                    } else {
//
//                        int size = (int) t.getLeafNodeWeight() * 40;
//
//                        s = new Rectangle2D.Double(0, -size / 2, 1000, size);
//                        //s = new Rectangle2D.Double(0, 0, size, size);
//                        // s = new Ellipse2D.Double(-size / 2, -size / 2, size, size);
//                    }
//                    return s;
//                }
//                return s;
//            }
//        };

        
       // vv.getRenderer().setVertexRenderer(new MyRenderer());
        
       // vv.getRenderContext().setVertexShapeTransformer(new ShapeTransformer(new Dimension(100,10)));

       //vv.getRenderContext().setVertexShapeTransformer(vertexSize);
       // vv.getRenderContext().setVertexLabelTransformer(VertexLabel);
      //  vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.S);




       // this.add(vv);







    }
    
//    static class MyRenderer extends JPanel implements Renderer.Vertex<Object, TopicGraphViewFrame.MyLink>
//    {
//        static final long serialVersionUID = 420000L;
//
//        @Override
//        public void paintVertex(RenderContext<Object, TopicGraphViewFrame.MyLink> rc,
//                                Layout<Object, TopicGraphViewFrame.MyLink> layout, Object vertex)
//        {
//           // System.out.println("MY RENDER");
//            try
//                
//            {   
//                
//                if (((EventsViewPanel)vertex).getAttechedNode().getValue().contains("Leaf"))
//                {
//                GraphicsDecorator graphicsContext = rc.getGraphicsContext();
//                Point2D center = layout.transform(vertex);
//                Dimension size = ((EventsViewPanel) vertex).getPreferredSize();
//                size = new Dimension(1000,400);
//                center.setLocation(500,200);
//                graphicsContext.draw((EventsViewPanel)vertex, rc.getRendererPane(), (int)center.getX(), (int)center.getY(), size.width, size.height, true);
//                //graphicsContext.draw((EventsViewPanel)vertex, rc.getRendererPane(), (int)center.getX(), (int)center.getY(), size.width, size.height, true);
//                }
//                else
//                {
//                    GraphicsDecorator graphicsContext = rc.getGraphicsContext();
//                     Point2D center = layout.transform(vertex);
//                      Dimension size = ((EventsViewPanel) vertex).getPreferredSize();
//                    // graphicsContext.draw(null);                    
//                    
//                }
//                
//                    
//            }
//            catch (Exception e)
//            {
//                System.err.println("Failed to render images!\n");
//                System.err.println("Caught Exception: " + e.getMessage());
//            }
//        }
//
////        public void paintVertex(RenderContext<Object, TopicGraphViewFrame.MyLink> rc, Layout<Object, TopicGraphViewFrame.MyLink> layout, Object v) {
////            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
////        }
//    }

    static class ShapeTransformer implements Transformer<Object, Shape>
    {
        Dimension d = null;

        public ShapeTransformer(Dimension d_in)
        {
            d = d_in;
        }

        public Shape transform(Object i)
        {
            Rectangle form = new Rectangle(d);
            Ellipse2D for1 = new Ellipse2D.Float(10,10,10,10);
            if (i instanceof EventsViewPanel)
            {
                float value = ((EventsViewPanel) i).getAttechedNode().getLeafNodeWeight();
                if (value>0)
                    return form;
                else
                    return for1;
                
            }
            //JPanel panel = (JPanel) i;
            //System.out.println("Shape transform");
            //Rectangle form = new Rectangle(d);
            //panel.setEnabled(true);
            return form;
        }

        
    }
    
    
    public final class HorizontalOverlappingTreeLayout<V, E> extends TreeLayout<V, E> {

        
        int verticalDis = 40;
        public Map<V, Point2D> getLocations() {
            return this.locations;

        }

        public HorizontalOverlappingTreeLayout(Forest<V, E> g, int distX, int distY) {

            super(g, distX, distY);


        }

        //@Override
        @Override
        protected void buildTree() {

            this.m_currentPoint = new Point(0, 20);
            Collection<V> roots = TreeUtils.getRoots(graph);


            if (roots.size() > 0 && graph != null) {
                calculateDimensionY(roots);
                for (V v : roots) {
                    //calculateDimensionY(v);
                    m_currentPoint.y += this.basePositions.get(v) / 2 + this.distY;
                    buildTree(v, this.m_currentPoint.y);

                }
                


//                if (false/*!alreadyDone.isEmpty()*/) {
//
//                    Iterator<V> it = alreadyDone.iterator();
//
//                    while (it.hasNext()) {
//                        V v = it.next();
//
//                        double Y = this.locations.get(v).getY();
//                        double X = this.locations.get(v).getX();
//
//                        TreeNode t = (TreeNode) v;
//
//                        // System.out.println(t.getValue() + "   " + t.getChildren().size());
//                        if (graph.getSuccessors(v) != null) {
//                            if (!graph.getSuccessors(v).isEmpty()) {
//
//                                //System.out.println("here1");
//                                boolean alterLocations = true;
//                                for (V element : graph.getSuccessors(v)) {
//
//                                    //System.out.println("here2");
//                                    if (!graph.getSuccessors(element).isEmpty()) {
//                                        alterLocations = false;
//                                        break;
//                                    }
//                                }
//
//                                // System.out.println("here3");
//                                if (alterLocations == true) {
//
//                                    //System.out.println("here4");
//                                    int reducedsize = distY / 10;
//
//                                    int sizeOfChild = graph.getSuccessors(v).size();
//
//                                    int startLocation = (int) Y - this.basePositions.get(v) / 2;
//
//                                    int distanceBetweenChildren = (this.basePositions.get(v) - reducedsize * 2) / sizeOfChild;
//
//                                    int lastY = startLocation + reducedsize;
//
//                                    for (V element : graph.getSuccessors(v)) {
//
//                                        //System.out.println("here5");
//                                        double tempx = this.locations.get(element).getX();
//                                        double tempy = this.locations.get(element).getY();
//                                        this.locations.get(element).setLocation(tempx, lastY);
//                                        lastY += distanceBetweenChildren;
//
//                                    }
//
//                                }
//                            }
//                        }
//                    }
//
//                }






            }
            // TODO: removed code here
        }

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
                    sizeYofChild = this.basePositions.get(element) + distY;

                    startYofChild = lastY + sizeYofChild / 2 ;

                    buildTree(element, startYofChild);

                    if (((TreeNode) element).getChildren().isEmpty()) {
                        lastY = lastY + sizeYofChild + distY * (int) (((TreeNode) element).getLeafNodeWeight());
                    } else {
                        lastY = lastY + sizeYofChild + distY;
                    }
                }
                this.m_currentPoint.x -= this.distX;
            }
        }

        private int calculateDimensionY(V v) {
            int size = 0;
            int childrenNum = graph.getSuccessors(v).size();

            if (childrenNum != 0) {
                for (V element : graph.getSuccessors(v)) {
                    size += calculateDimensionY(element) + (int) (distY * ((TreeNode) element).getLeafNodeWeight());
                }
            }

            size = Math.max(0, size - distY);
            basePositions.put(v, size);
            
            return size;
        }

        private int calculateDimensionY(Collection<V> roots) {
            int size = 0;
            for (V v : roots) {
                int childrenNum = graph.getSuccessors(v).size();
                int ttt = 0;
                if (childrenNum != 0) {
                    for (V element : graph.getSuccessors(v)) {
                        size += calculateDimensionY(element) + (int) (distY * ((TreeNode) element).getLeafNodeWeight());
                        //ttt = (int) (distY * ((TreeNode) element).getLeafNodeWeight());
                    }
                } 
                else {

                    size += calculateDimensionY(v) + (int) (distY * ((TreeNode) v).getLeafNodeWeight());
                    //ttt = (int) (distY * ((TreeNode) v).getLeafNodeWeight());
                }


                
                size = Math.max(0, size - ttt/*distY*/ );
                basePositions.put(v, size);
            }

            return size;
        }
    }
}
//prefuse stuff
//public class EventGraphViewPanel extends Display {
//    
//    
//    private static final String SIZE = "size";
//
//    private static final String LABEL = "label";
//    
//    Graph graph;
//    
//    
//    //TextImageItemRenderer imageRenderer;
//    
//    
////     private TextItemRenderer m_nodeRenderer;
//    private EdgeRenderer m_edgeRenderer;
//    
//    private int m_orientation = Constants.ORIENT_LEFT_RIGHT;
//    ShapeRenderer m_nodeRenderer;
//    
//    private List<TreeNode> myTree;
//
//    public List<TreeNode> getMyTree() {
//        return myTree;
//    }
//
//    public void setMyTree(List<TreeNode> myTree) {
//        this.myTree = myTree;
//    }
//    
//    
//    
//    public EventGraphViewPanel(Graph gh, List<TreeNode> tree){
//    
//         super(new Visualization());
//   
//         myTree = tree;
//         
//        graph = gh;
//     
//        gh.getNodeTable().addColumn("size", int.class);
//
//        for (int i=0; i<gh.getNodeCount(); i++)
//        {
//            Node n = gh.getNode(i);
//            n.setInt(SIZE, i);
//                        
//        }
//        
//         gh.getNodeTable().addColumn("Color", Color.class);
//        
//        
//        
//        m_vis.addGraph("graph", graph);
//        m_vis.setInteractive("graph.edges", null, false);
//        m_vis.setValue("graph.nodes", null, VisualItem.SHAPE, new Integer(Constants.SHAPE_RECTANGLE));
//        
//
////       imageRenderer = new TextImageItemRenderer();
////        imageRenderer.setMaxImageDimensions(150,150);
////        imageRenderer.setImageSize(0.2);
////        imageRenderer.setHorizontalPadding(2); 
////     
////        
////        imageRenderer.setImageFactory(new ImageFactory());
////        
//        
//        
//       m_nodeRenderer = new ShapeRenderer(10);
//      // m_nodeRenderer.setBounds(null);
//        
//        //NodeRenderer noderRender = new NodeRenderer();
//        
//        EdgeRenderer edgeR = new EdgeRenderer(prefuse.Constants.EDGE_TYPE_CURVE, prefuse.Constants.EDGE_ARROW_NONE);
//
//        
//        DefaultRendererFactory drf = new DefaultRendererFactory();
//        drf.setDefaultRenderer(m_nodeRenderer);
//        drf.setDefaultEdgeRenderer(edgeR);
//        m_vis.setRendererFactory(drf);
//
//        int[] palette = new int[] {
//            ColorLib.rgb(255,180,180), ColorLib.rgb(190,190,255)
//        };
//        ColorAction nStroke = new ColorAction("graph.nodes", VisualItem.STROKECOLOR);
//        
////        int[] fillArray = new int[gh.getNodeCount()];
////        for (int i=0; i<gh.getNodeCount(); i++)
////        {
////            
////            int r = myTree.get(i).getColor().getRed();
////            int g = myTree.get(i).getColor().getGreen();
////            int b = myTree.get(i).getColor().getBlue();
////            
////            
////            fillArray[i] = ColorLib.rgb(r, g, b);
////            
////            
////            
////        }
//        
//        
//        
////        DataColorAction colornode = new DataColorAction("graph.nodes", "Color",
////    Constants.NOMINAL, VisualItem.FILLCOLOR, fillArray);
//        
//        
//        
//        nStroke.setDefaultColor(ColorLib.gray(100));
//
////        DataColorAction nFill = new DataColorAction("graph.nodes", "flag",
////            Constants.NOMINAL, VisualItem.FILLCOLOR, palette);
//        ColorAction edges = new ColorAction("graph.edges",
//            VisualItem.STROKECOLOR, ColorLib.gray(200));
////        ColorAction arrow = new ColorAction("graph.edges",
////            VisualItem.FILLCOLOR, ColorLib.gray(200));
//        ActionList color = new ActionList();
//        color.add(nStroke);
//        //color.add(nFill);
//        color.add(edges);
//        //color.add(arrow);
//       // color.add(colornode);
//        
//        
//        
//        DataSizeAction nodeDataSizeAction = new DataSizeAction("graph.nodes", SIZE);
//        color.add(nodeDataSizeAction);
//        m_vis.putAction("draw", color);
//        
//        
//              NodeLinkTreeLayout treeLayout = new NodeLinkTreeLayout("graph",
//                m_orientation, 100, 10, 20);
//        treeLayout.setLayoutAnchor(new Point2D.Double(25,300));
//        
//        
//
//        ActionList layout = new ActionList(Activity.INFINITY);
//        //layout.add(new ForceDirectedLayout("graph"));
//        layout.add(new RepaintAction());
//
//        
//        
//        
//        drf.add(new InGroupPredicate("nodedec"), new LabelRenderer("id"));
//        
//        
//        
//        
//        
//        
//        
//  
//        m_vis.putAction("treeLayout", treeLayout);
//        
//        
//        
//        m_vis.putAction("color", color);
//       // m_vis.putAction("layout", layout);
//
//        
//        
////        //setSize(720, 500); // set display size
////        pan(360, 250);
//        setHighQuality(true);
//        addControlListener(new DragControl());
//        addControlListener(new PanControl());
//        addControlListener(new ZoomControl());
//        
//        
////         Node focus = gh.getNode(0);
////        PrefuseLib.setX(focus, null, 400);
////        PrefuseLib.setY(focus, null, 250);
//      //  focusGroup.setTuple(focus);
//        
//        
//
//        m_vis.run("color");
//        m_vis.run("treeLayout");
//        
//        
//        
//        
//        
//        
//}
//}
