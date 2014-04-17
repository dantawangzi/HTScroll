/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TasteAnalytics.Apollo.TreeMapView;

import com.TasteAnalytics.Apollo.GUI.ViewController;
import com.TasteAnalytics.Apollo.TemporalView.TreeNode;
import com.TasteAnalytics.Apollo.TopicRenderer.LabelText;
import com.TasteAnalytics.Apollo.Util.SystemPreferences;
import com.TasteAnalytics.Apollo.Wordle.LabelWordleLite;
import com.TasteAnalytics.Apollo.Wordle.WordleAlgorithmLite;
import com.TasteAnalytics.Apollo.Wordle.WordleLite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 *
 * @author lyu8
 */
public class TreeMapNodePanel extends JPanel {

    TreeNode node;
    ViewController parent;
    JLabel title = new JLabel();
    Rectangle myRect;
    int level;
    List<LabelText> labels;
    List<JLabel> mylabels = new ArrayList<JLabel>();

    boolean mouseOvered = false;
    
    private Graphics2D curg2d;

    public Rectangle getMyRect() {
        return myRect;
    }

    public void setMyRect(Rectangle myRect) {
        this.myRect = myRect;
    }

    class Slice {

        double value;
        Color color;

        public Slice(double value, Color color) {
            this.value = value;
            this.color = color;
        }
    }

    void drawPie(Graphics2D g, Rectangle area) {
        double totalPieSize = 0.0D;
        Slice[] slices = {new Slice(this.node.getSentiAgg().pos, new Color(160, 170, 105)),
            new Slice(-this.node.getSentiAgg().neg, new Color(174, 86, 80))};

        for (Slice slice : slices) {
            totalPieSize += slice.value;
        }

        double curValue = 0.0D;
        int startAngle = 0; // Not in use right now. Do we still need this? 
        for (int i = 0; i < SystemPreferences.numOfSentiments; i++) {
            startAngle = (int) (curValue * 360 / totalPieSize);
            int arcAngle = (int) (slices[i].value * 360 / totalPieSize);
            g.setColor(slices[i].color);
            g.fillArc(area.x, area.y, area.width, area.height,
                    startAngle, arcAngle);
            curValue += slices[i].value;
        }
    }
    
//    private boolean firsttime;
//    private boolean needDoLayout;
//    private BufferedImage bi;
//    private Rectangle area;
//    @Override
//    public void update(Graphics g) {
//        Graphics2D g2 = (Graphics2D) g;
//        //System.out.println("Rendering");
//        if (firsttime) {
//            area = new Rectangle(this.getWidth(), this.getHeight());
//            bi = (BufferedImage) createImage(this.getWidth(), this.getHeight());
//            curg2d = bi.createGraphics();
//            curg2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//            firsttime = false;
//        }
//
//        // Clears the rectangle that was previously drawn.
//        if (curg2d == null) return;
//        
//        curg2d.setColor(Color.WHITE);
//        //curg2d.setColor(Color.black);
//        curg2d.fillRect(0, 0, area.width, area.height);
//
//        if (needDoLayout) {
//
////            // System.out.println( this.name + "do le mei?");
////            clearPreviousValues();
////            // computerZeroslopeAreas();
////            computerZeroslopeAreasHierarchy(0);
////
////            //System.out.println("need do layout" + ++countnn);
////            needDoLayout = false;
//        }
//
//        curg2d.setColor(Color.BLACK);
//
//        g2.drawImage(bi, 0, 0, this);
//    }
//    

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
     
        
        int width = this.getWidth();
        int height = this.getHeight();
        int size = node.getArrayValue().size();

        Rectangle area = new Rectangle(0, 0, this.myRect.width / 4, this.myRect.width / 4);
        drawPie((Graphics2D) g, area);
        
//        update(g);
        

//            for (int i=0; i<size; i++)
//            {
//                g.setColor(Color.black);
//                
//                if (i==0)
//                {
//                    
//                    g.drawLine(0, height/2, width/size*i, height/2 - (int) (node.getArrayValue().get(i)*this.getHeight()));
//                }
//                else
//                g.drawLine(width/size*i-1, height/2 - (int) (node.getArrayValue().get(i-1)*this.getHeight()), width/size*i, height/2 - (int) (node.getArrayValue().get(i)*this.getHeight()));
//                
//            }
        //g.drawString("BLAH", 20, 20);
        //g.drawRect(200, 200, 200, 200);
    }

    public boolean isMouseOvered() {
        return mouseOvered;
    }

    public void setMouseOvered(boolean mouseOvered) {
        this.mouseOvered = mouseOvered;
    }

    public List<LabelText> getLabels() {
        return labels;
    }

    public void setLabels(List<LabelText> labels) {
        this.labels = labels;
    }

    public void updateLayout() {
        this.setBounds(this.myRect);
        Border bLine = BorderFactory.createLineBorder(Color.red, 2);
        if (mouseOvered) {
            this.setBorder(bLine);
            //this.setBackground(Color.red);
        } else {
            this.setBorder(null);

        }

        this.setBackground(node.getColor());

        //this.getRootPane().revalidate();
    }

    /// Public constructor
    public TreeMapNodePanel(ViewController v, TreeNode t, int l, Rectangle r) {
        parent = v;
        level = l;
        node = t;
        myRect = r;
        this.setBounds(myRect);
        this.setBackground(node.getColor());

        title.setText(t.getValue());

        if (!t.getChildren().isEmpty()) {
            title.setVisible(false);

            this.add(title);
        }

        // This is the main cause of not showing the Circle Word Cloud!!! 
        // Kind Derek!
        this.setLayout(null);
        
        

        TopicTreeMapPanelInteractions interactions = new TopicTreeMapPanelInteractions(this);
        addMouseListener(interactions);
        addMouseMotionListener(interactions);

    }

    public void DrawWordleCloud(Point2D p, List<LabelText> ls) {

        //ls.get(0).getFont()
        // No Need to create the list any more. We can make this a statics and reused list
        List<LabelWordleLite> list = new ArrayList<LabelWordleLite>();

//        int size = 0;
//
//        size = ;
        for (int i = 0; i < ls.size(); i++) {
            LabelText lt = ls.get(i);

            String text = lt.getString();
            if (text == null) {
                continue;
            }

            Font font = lt.getFont();

//            Font font = new Font("Helvetica-Condensed-Bold", Font.BOLD, 10);
            LabelWordleLite word = new LabelWordleLite(text, font, 0, lt);
            list.add(word);

        }

        WordleAlgorithmLite alg = new WordleAlgorithmLite(this.myRect);

//alg.displayParameters();
        alg.place(list);

        Rectangle2D bounds = this.myRect;// = findBoundary(list);

        // System.out.println(this.getBounds());
        // System.out.println(bounds);
        for (LabelWordleLite word : list) {

            setLabelVisualPos(word, p, bounds);
            
            
//            System.out.println(word.shape.getBounds().x);
        }
        
        this.labels = ls;

        // Doesn't seem this function use the SetLabel Pos at all!!
        for (int i = 0; i < ls.size(); i++) {
            JLabel jl = new JLabel();

            jl.setText(ls.get(i).getString());
            ls.get(i);

            Point2D p1 = ls.get(i).getLocation();
//            System.out.println();
            jl.setLocation((int) p1.getX(), (int) p1.getY());
//            jl.setPreferredSize(new Dimension(80,40));
            jl.setSize(jl.getPreferredSize());

            //multiTopicKeywordList.get(i).drawRect(g2d);
            jl.setFont(ls.get(i).getFont());
            jl.setBackground(Color.red);
            mylabels.add(jl);
            this.add(jl);
        }

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
            FontMetrics fm = this.getGraphics().getFontMetrics(font);
            Rectangle2D strBound = fm.getStringBounds(label.text, this.getGraphics());

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
//
//        float size = (float) (vi.getFont().getSize2D() /**
//                 * vi.getOccurance()
//                 */
//                );

//        Font font = vi.getFont().deriveFont(size);
//        FontMetrics fm = this.getGraphics().getFontMetrics(font);
//        Rectangle2D strBound = fm.getStringBounds(label.text, this.getGraphics());
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
//        double x = p.getX() + location.getX();// - glyphBound.getX() / 2;// - strBound.getWidth() / 2;
//        double y = p.getY() + location.getY();// - glyphBound.getY() / 2;// - strBound.getHeight() / 2;
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
        double x = p.getX() + location.getX();// - glyphBound.getX() / 2;
        double y = p.getY() + location.getY();//- glyphBound.getY() / 2 ;

        Point2D vilocation = new Point2D.Double(x, y);

//        System.out.println(x + " " + y);
        // TODO: Int Points may just limit the placement
        // Where do we use vi?
        vi.setLocation(new Point((int) vilocation.getX(), (int) vilocation.getY()));// + fm.getDescent() - strBound.getHeight() / 2));
        //System.out.println(p + " " +location);

        //System.out.println("xxx" + fm.getDescent());
        //Rectangle2D ss = new Rectangle2D.Double(vilocation.getX(), vilocation.getY(), glyphBound.getWidth(), glyphBound.getHeight());
        //vi.setRect(ss);
        // vi.setX(location.getX() + glyphBound.getX() + strBound.getWidth() / 2);
        // vi.setY(location.getY() + fm.getDescent() - strBound.getHeight() / 2);
    }

}
