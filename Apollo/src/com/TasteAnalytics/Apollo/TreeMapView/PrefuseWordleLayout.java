/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TasteAnalytics.Apollo.TreeMapView;

/**
 *
 * @author lyu8
 */
import com.TasteAnalytics.Apollo.GUI.ViewController;
import com.TasteAnalytics.Apollo.TemporalView.TreeNode;
import com.TasteAnalytics.Apollo.TopicRenderer.LabelText;
import com.TasteAnalytics.Apollo.TopicRenderer.PrefuseLabelTopicGraphPanel;
import com.TasteAnalytics.Apollo.Wordle.LabelWordleLite;
import com.TasteAnalytics.Apollo.Wordle.WordleAlgorithmLite;
import com.TasteAnalytics.Apollo.Wordle.WordleLite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.GroupAction;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.assignment.DataSizeAction;
import prefuse.action.assignment.ShapeAction;
import prefuse.action.filter.VisibilityFilter;
import prefuse.action.layout.AxisLabelLayout;
import prefuse.action.layout.AxisLayout;
import prefuse.action.layout.Layout;
import prefuse.controls.Control;
import prefuse.controls.ControlAdapter;
import prefuse.controls.DragControl;
import prefuse.controls.PanControl;
import prefuse.controls.ToolTipControl;
import prefuse.controls.ZoomControl;
import prefuse.data.Schema;
import prefuse.data.Table;
import prefuse.data.expression.AndPredicate;
import prefuse.data.io.CSVTableReader;
import prefuse.data.query.RangeQueryBinding;
import prefuse.data.query.SearchQueryBinding;
import prefuse.render.AbstractShapeRenderer;
import prefuse.render.AxisRenderer;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.render.Renderer;
import static prefuse.render.Renderer.DEFAULT_GRAPHICS;
import prefuse.render.RendererFactory;
import prefuse.render.ShapeRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.PrefuseLib;
import prefuse.util.UpdateListener;
import prefuse.util.ui.JFastLabel;
import prefuse.util.ui.JRangeSlider;
import prefuse.util.ui.JSearchPanel;
import prefuse.util.ui.UILib;
import prefuse.visual.VisualItem;
import prefuse.visual.VisualTable;
import prefuse.visual.expression.InGroupPredicate;
import prefuse.visual.expression.VisiblePredicate;

public class PrefuseWordleLayout extends Layout {

    
    
    ViewController parent; 
    public PrefuseWordleLayout(ViewController parent, TreeNode ct) {
       // super(new Visualization());
        this.parent = parent;
        Table t = new Table();

        List<LabelWordleLite> data = parent.allLabelInWordle.get(ct);
         List<LabelText> data2 = parent.allLabels.get(ct);

        t.addColumn("index", Integer.class);
        t.addColumn("label", String.class);
        t.addColumn("shape", Shape.class);
        t.addColumn("weight", String.class);

        for (int i = 0; i < data.size(); i++) {
            int row = t.addRow();
            t.set(row, "index", i);
            t.set(row, "label", data.get(i).text);
            t.set(row, "shape", data.get(i).shape);
            t.set(row, "weight", String.valueOf(data2.get(i).probablity));

        }

        
     //    ToolTipControl ttc = new ToolTipControl("label");
//        Control hoverc = new ControlAdapter() {
//
//            
//  
//            public void itemEntered(VisualItem item, MouseEvent evt) {
//                if (item.isInGroup("canUrban")) {
//                    //g_total.setText(item.getString("label"));
//                    item.setFillColor(item.getStrokeColor());
//                    item.setStrokeColor(ColorLib.rgb(255, 0, 0));
//                    item.getVisualization().repaint();
//                }
//            }
//
//            public void itemExited(VisualItem item, MouseEvent evt) {
//                if (item.isInGroup("canUrban")) {
//                    //g_total.setText(g_totalStr);
//                    item.setFillColor(item.getEndFillColor());
//                    item.setStrokeColor(item.getEndStrokeColor());
//                    item.getVisualization().repaint();
//                }
//            }
//        };
//        this.addControlListener(ttc);
//        this.addControlListener(hoverc);
//        
        
        //  this.setLayout(new BorderLayout());
        final Visualization vis = new Visualization();
        m_vis = vis;
        
        vis.add("canUrban", t);
        
        
        //VisualTable vt = vis.addTable("canUrban", t);
       
        
       
//        int[] palette = new int[]{
//            ColorLib.rgb(77, 175, 74),
//            ColorLib.rgb(55, 126, 184),
//            ColorLib.rgb(228, 26, 28),
//            ColorLib.rgb(152, 78, 163),
//            ColorLib.rgb(255, 127, 0)
//        };
//        
        
//        
//         DataColorAction color = new DataColorAction("canUrban", "index",
//                Constants.ORDINAL, VisualItem.STROKECOLOR, palette);
//        

         
//          ColorAction edges = new ColorAction("canUrban",
//                                       VisualItem.STROKECOLOR,
//                                       ColorLib.gray(200));
//
//          
//          
//          
//        ColorAction fill = new ColorAction("canUrban", VisualItem.FILLCOLOR, ColorLib.gray(100));
//       // ShapeAction shape = new ShapeAction("canUrban", Constants.SHAPE_RECTANGLE);
//        //DataSizeAction size = new DataSizeAction("canUrban", "index");
//
//        ActionList draw = new ActionList();
//
//        draw.add(edges);
//        draw.add(fill);
        //draw.add(shape);
       // draw.add(size);

        
//         DrawWordleCloud();
//         
//         
//         
//        draw.add(new RepaintAction());
//        vis.putAction("draw", draw);
//
//        vis.run("draw");
//        
//        //BorderFactory.createLineBorder(Color.yellow)
//        
//        d = new Display(vis);
//        
//        this.setBorder( BorderFactory.createLineBorder(Color.yellow));//BorderFactory.createEmptyBorder(10, 20, 10, 20));
//        this.setSize(1000, 1000);
//        this.setHighQuality(true);
//        
//        d.addControlListener(new DragControl());
//   // Pan with left-click drag on background
//         d.addControlListener(new PanControl()); 
//   // Zoom with right-click drag
//         d.addControlListener(new ZoomControl());

    }

    private Visualization m_vis;
//private static Display d;


    public final List<Rectangle2D> DrawWordleCloud(Rectangle2D wordBound/*List<LabelWordleLite> ls*/) {

        
         List<Rectangle2D> results = new ArrayList<Rectangle2D>();
         
         
         List<Float> wis = new ArrayList<Float>();
        List<LabelWordleLite> list = new ArrayList<LabelWordleLite>();
        for (Iterator<VisualItem> it = m_vis.items("canUrban"); it.hasNext();) {
            VisualItem vi = it.next();
            String text = vi.getString("label");
            if (text == null) {
                continue;
            }
            
          
           
            double weight = vi.getSize() * vi.getFont().getSize();
            
            String w = String.valueOf(vi.get("weight"));
            weight = Double.parseDouble(w)*parent.occuranceFontSizePara;
            
               Font font = new Font("Impact", Font.PLAIN,  (int) weight);
            
            vi.setFont(font);
    
//Font font = vi.getFont().deriveFont((float) weight);
            wis.add((float)weight);
            LabelWordleLite word = new LabelWordleLite(text, font, 0, vi);
            list.add(word);
        }
        
        
        //wordBound = getLayoutBounds();
        if (wordBound == null)
            wordBound = new Rectangle2D.Double(0,0,500,500);


        WordleAlgorithmLite alg = new WordleAlgorithmLite( new Rectangle2D.Double(0,0,wordBound.getWidth(),wordBound.getHeight()));

        alg.place(list);
        
        //for (int i=0; i<list.size(); i++)
        //System.out.println(list.get(i).getLocation().getX()  + " " +  list.get(i).getLocation().getY());

        int count =0;
        for (LabelWordleLite word : list) {

            Rectangle2D rd = setLabelVisualPos(word, wis.get(count++) );
                results.add(rd);
        }

        
//       
//        
//          for (Iterator<VisualItem> it = m_vis.items("canUrban"); it.hasNext();) {
//                                          
//               VisualItem vi = it.next();               
//               results.add(vi.getBounds());              
//              
//              
//          }
          
          
          return results;

    }

    private Rectangle2D setLabelVisualPos(WordleLite symbol, float fontSize) {
        LabelWordleLite label = (LabelWordleLite) symbol;
        Point2D location = label.getLocation();
        VisualItem vi = (VisualItem) label.data;
        Rectangle2D glyphBound = label.getShape().getBounds2D();//new Rectangle2D.Double(0,0,vi.getFont().getSize()*10,vi.getFont().getSize()*10); //label.getShape().getBounds2D();
//

        float size = fontSize;//(float) (vi.getFont().getSize2D() * vi.getSize());//fontSize;//
        Font font = vi.getFont().deriveFont(size);
        FontMetrics fm = DEFAULT_GRAPHICS.getFontMetrics(font);
        Rectangle2D strBound = fm.getStringBounds(label.text, DEFAULT_GRAPHICS);

        vi.setX(location.getX()+ glyphBound.getX() + strBound.getWidth() / 2);
        vi.setY(location.getY() + fm.getDescent() - strBound.getHeight() / 2);
        

        vi.setX(location.getX() + strBound.getWidth() / 2);// strBound.getWidth(), strBound.getHeight());
        vi.setY(location.getY() - strBound.getHeight() / 2);
//System.out.println(vi.getX() + " " + vi.getY());
        return (new Rectangle2D.Double(vi.getX(), vi.getY(),  strBound.getWidth(), strBound.getHeight()));
        

    }

    @Override
    public void run(double frac) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
