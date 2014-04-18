/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TasteAnalytics.Apollo.TreeMapView;

import com.TasteAnalytics.Apollo.GUI.ViewController;
import com.TasteAnalytics.Apollo.TemporalView.TemporalViewPanel;
import com.TasteAnalytics.Apollo.TemporalView.TreeNode;
import com.TasteAnalytics.Apollo.TopicRenderer.LabelText;
import com.TasteAnalytics.Apollo.Util.SystemPreferences;
import com.TasteAnalytics.Apollo.Wordle.LabelWordleLite;
import com.TasteAnalytics.Apollo.Wordle.WordleAlgorithmLite;
import com.TasteAnalytics.Apollo.Wordle.WordleLite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import net.coobird.thumbnailator.Thumbnails;

/**
 *
 * @author lyu8
 */
public class TreeMapNodePanel extends JPanel {
    
    
    class Slice {

        double value;
        Color color;

        public Slice(double value, Color color) {
            this.value = value;
            this.color = color;
        }
    }

   
    class sentimentPanel extends JPanel{
        
        int pos;
        int neg;
        int count;
        Rectangle myArea;
        sentimentPanel(int p, int n, int c, Rectangle area )
        {
            pos = p;
            neg = n;
            count = c;
            myArea = area;
                                    
            
        }
        
        
        void drawPie(Graphics2D g, Rectangle area) {
        double totalPieSize = 0.0D;
        Slice[] slices = {new Slice(pos, new Color(160, 170, 105)),
            new Slice(-neg, new Color(174, 86, 80))};

        for (Slice slice : slices) {
            totalPieSize += slice.value;
        }

        double curValue = 0.0D;
       
        for (int i = 0; i < SystemPreferences.numOfSentiments; i++) {
             int startAngle = (int) (curValue * 360 / totalPieSize);
            int arcAngle = (int) (slices[i].value * 360 / totalPieSize);
            g.setColor(slices[i].color);
            g.fillArc(area.x, area.y, area.width, area.height,
                    startAngle, arcAngle);
            curValue += slices[i].value;
        }
    }
         
         
          @Override
    
          public void paintComponent(Graphics g) {
            
        
                super.paintComponent(g);

                drawPie((Graphics2D) g, myArea);

            }
         
       
        
        }
    
    
    
    
    
    
    
    
    sentimentPanel senti;
    TreeNode node;
    ViewController parent;
    JLabel title = new JLabel();
    Rectangle myRect;
    int level;
    List<LabelText> labels;
    List<JLabel> mylabels = new ArrayList<JLabel>();

    boolean mouseOvered = false;
    
    private Graphics2D curg2d;
    JLabel imageLabel = new JLabel();
    public Rectangle getMyRect() {
        return myRect;
    }

    public void setMyRect(Rectangle myRect) {
        this.myRect = myRect;
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
     
        currg = g;
        int width = this.getWidth();
        int height = this.getHeight();
        int size = node.getArrayValue().size();

//        
//
//
//		Graphics2D g2d = (Graphics2D) g;
//                if (myBI!=null)
//		g2d.drawImage(myBI, null, 40,0);
	
        
        
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
       scrollPane.setBounds(sentiPieSize, 0, myRect.width-sentiPieSize, sentiPieSize);
        
       
        wordCloudPanel.setBounds(0,sentiPieSize,myRect.width *3/5, myRect.height*4/5-sentiPieSize);
        wordCloudPanel.setOpaque(false);
       
        
        wordRemoveWordPanel.setBounds(myRect.width *3/5,sentiPieSize, myRect.width *3/5,myRect.height*4/5-sentiPieSize);
        
        tmp.setBounds(0,  myRect.height*4/5, myRect.width, myRect.height/5);
        //tmp.setPreferredSize(new Dimension(myRect.width-40, 40));
        tmp.UpdateTemporalView(new Dimension( myRect.width, myRect.height/5), tmp.getGlobalNormalizingValue());
        
        
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
    
    JPanel wordCloudPanel = new JPanel();
    JPanel wordRemoveWordPanel = new JPanel();
    Graphics currg;
    
   TemporalViewPanel tmp;
   
   int sentiPieSize = 60;
   JPanel infoPanel = new JPanel();
      JScrollPane scrollPane ;
   
    public TreeMapNodePanel(ViewController v, TreeNode t, int l, Rectangle r, /*BufferedImage bi*/TemporalViewPanel tp) throws IOException {
        parent = v;
        level = l;
        node = t;
        myRect = r;
        tmp = tp;
        
        
        this.setBounds(myRect);
        this.setBackground(node.getColor());

        title.setText(t.getValue());

        if (!t.getChildren().isEmpty()) {
            title.setVisible(false);

            this.add(title);
        }
        
        
        senti = new sentimentPanel(node.getSentiAgg().pos,node.getSentiAgg().neg, node.getSentiAgg().count, new Rectangle(0,0,sentiPieSize,sentiPieSize));
        this.add(senti);
        senti.setBounds(new Rectangle(0,0,sentiPieSize,sentiPieSize));

        this.add(tmp);
//        tmp.setBounds(40, 0, myRect.width-40, 40);
//        tmp.setPreferredSize(new Dimension(myRect.width-40, 40));
//        tmp.UpdateTemporalView(new Dimension(myRect.width-40, 40), tmp.getGlobalNormalizingValue());
//        
//        myBI = bi;
//        
//        if (this.getWidth()-40>0)
//            myBI = Thumbnails.of(bi).size(this.getWidth()-40, 40).asBufferedImage();
       String msg = "";
       msg += "Average: " + node.avg_unNorm + "\n";
       msg += "STD: " + node.std_unNorm + "\n";
    
       
        JTextArea textArea = new JTextArea(msg);
    scrollPane = new JScrollPane(textArea);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
             
        this.add(scrollPane);
        this.add(wordCloudPanel);
        wordCloudPanel.setLayout(null);
//        wordCloudPanel.setBounds(0,40,this.getWidth(), this.getHeight()-40);
//        
        wordRemoveWordPanel.setBackground(Color.darkGray);
        this.add(wordRemoveWordPanel);
        
        
        
        updateLayout();
        
       // worldCloudPanel.setBackground(Color.red);
//        if (myBI instanceof BufferedImage) {
//    myBI = (BufferedImage) bi.getScaledInstance(200, 40, Image.SCALE_SMOOTH);
//}
        
        
//       imageLabel = new JLabel(new ImageIcon(bi));
//       imageLabel.setBounds(0, 40, 100, 40);
//        this.add(imageLabel);
        
       
        this.setLayout(null);
        

        TopicTreeMapPanelInteractions interactions = new TopicTreeMapPanelInteractions(this);
        addMouseListener(interactions);
        addMouseMotionListener(interactions);

    }

    
    
    
    BufferedImage myBI;

    public void setMyBI(BufferedImage myBI) throws IOException {
        this.myBI = myBI;
  
        if (myBI!=null && (this.getWidth()-40)>0)
        {
        this.myBI = Thumbnails.of(myBI)
                             .size(myRect.width-40, 40)
                             .asBufferedImage();
        }        

        
    }
    
    List<LabelWordleLite> list = new ArrayList<LabelWordleLite>();
    
    
    public void DrawWordleCloud(Point2D p, List<LabelText> ls) {

      

        
        WordleAlgorithmLite alg = new WordleAlgorithmLite( new Rectangle2D.Double(0,0,wordCloudPanel.getWidth(), wordCloudPanel.getHeight()));
//.wordCloudPanel.getBounds());

        alg.place(list);

      
//new Rectangle2D.Double(0,0,wordCloudPanel.getWidth(), wordCloudPanel.getHeight());//wordCloudPanel.getBounds();// 
        // System.out.println(this.getBounds());
        // System.out.println(bounds);
        
        for (LabelWordleLite word : list) {

            setLabelVisualPos(word);
            
        }
        

        

        for (int i = 0; i < labels.size(); i++) {
         
            Point2D p1 = list.get(i).getLocation();//labels.get(i).getLocation2D();
         //   labels.get(i).setLocation((int) p1.getX(), (int) p1.getY());
            
            labels.get(i).setBounds((int) p1.getX(), (int) p1.getY(), labels.get(i).getWidth(), labels.get(i).getHeight());            
            labels.get(i).setBackground(Color.red);    
            labels.get(i).setOpaque(true);
            labels.get(i).setFont(labels.get(i).getFont());
            labels.get(i).setText(labels.get(i).getString());
            
//            wordCloudPanel.add(labels.get(i));
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
            FontMetrics fm = null;
            Rectangle2D strBound = new Rectangle2D.Double(0,0,50,30);
            if (currg!=null)
            {
                fm = this.getGraphics().getFontMetrics(font);
                
               strBound = fm.getStringBounds(label.text, this.getGraphics());
            }
            
            float current_x = (float) (location.getX() - strBound.getWidth() / 2);
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

    private void setLabelVisualPos(WordleLite symbol) {
        LabelWordleLite label = (LabelWordleLite) symbol;
        Point2D location = label.getLocation();
        LabelText vi = (LabelText) label.data;
        //Rectangle2D glyphBound = new Rectangle2D.Double(0,0,vi.getFont().getSize()*10,vi.getFont().getSize()*10); //label.getShape().getBounds2D();
//
        float size = (float) (vi.getFont().getSize2D());
 
   
        Font font = vi.getFont().deriveFont(size);
        Rectangle2D strBound = new Rectangle2D.Double(0,0,0,0);
        FontMetrics fm = null;
        
        
        
        
        if (currg!=null)
        {
            fm = currg.getFontMetrics(font);
            strBound = fm.getStringBounds(label.text, currg);
        }
        
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
        double x = location.getX() - strBound.getWidth()/2;//+glyphBound.getX()p.getX() + location.getX() - glyphBound.getX() / 2 - strBound.getWidth() / 2;
        
        double y = 0;
        
  
        
        if (fm == null)
            y = location.getY() + strBound.getHeight()/2 ;//- glyphBound.getY() / 2p.getY() + location.getY() - glyphBound.getY() / 2  - strBound.getHeight() / 2;
        else
             y = location.getY()+ strBound.getHeight()/2;//-  glyphBound.getY() / 2p.getY() + location.getY() - glyphBound.getY() / 2  - strBound.getHeight() / 2;
        
        //fm.getDescent()
       
        
        Point2D vilocation = new Point2D.Double(x, y);

        
//        System.out.println(x + " " + y);
        // TODO: Int Points may just limit the placement
        // Where do we use vi?
        //vi.setLocation(new Point2D.Double( vilocation.getX(),  vilocation.getY()));// + fm.getDescent() - strBound.getHeight() / 2));
        vi.setBounds((int) vilocation.getX(), (int) vilocation.getY(), (int)strBound.getWidth(), (int)strBound.getHeight());
        vi.setBounds((int) vilocation.getX(), (int) vilocation.getY(), (int)strBound.getWidth(), (int)strBound.getHeight());
        //System.out.println(p + " " +location);

        //System.out.println("xxx" + fm.getDescent());
        //Rectangle2D ss = new Rectangle2D.Double(vilocation.getX(), vilocation.getY(), glyphBound.getWidth(), glyphBound.getHeight());
        //vi.setRect(ss);
        // vi.setX(location.getX() + glyphBound.getX() + strBound.getWidth() / 2);
        // vi.setY(location.getY() + fm.getDescent() - strBound.getHeight() / 2);
    }

}
