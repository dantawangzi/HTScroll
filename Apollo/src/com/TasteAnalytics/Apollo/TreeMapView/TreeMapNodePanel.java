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
import com.TasteAnalytics.Apollo.Wordle.WordleLite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import net.coobird.thumbnailator.Thumbnails;

/**
 *
 * @author lyu8
 */
public final class TreeMapNodePanel extends JPanel {

    class Slice {

        double value;
        Color color;

        public Slice(double value, Color color) {
            this.value = value;
            this.color = color;
        }
    }

    class SentimenBar extends JPanel {

        int positiveValue;
        int negativevalue;
        int count;
        Rectangle barArea;

        public SentimenBar(int positive, int negative, int count, Rectangle drawableArea) {

            this.positiveValue = positive;
            this.negativevalue = negative;
            this.count = count;
            this.barArea = drawableArea;
            this.setBounds(drawableArea);
        }

        private void drawSentimentBar(Graphics2D g, Rectangle2D area) {

            float totalValue = this.positiveValue - this.negativevalue;

            float flagPercentage = (float) this.positiveValue / totalValue;

            g.setColor(SystemPreferences.positiveColor);
            g.fillRect(0, 0, (int) (area.getWidth() * flagPercentage), (int) area.getHeight());

            g.setColor(SystemPreferences.negativeColor);
            g.fillRect((int) (area.getWidth() * flagPercentage), 0, (int) (area.getWidth() * (1 - flagPercentage)), (int) area.getHeight());
        }

        // TODO: Add String and Interaction
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            this.drawSentimentBar((Graphics2D) g, barArea);
        }

    }

    class sentimentPanel extends JPanel {

        int pos;
        int neg;
        int count;
        Rectangle myArea;

        sentimentPanel(int p, int n, int c, Rectangle area) {
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

    private SentimenBar sentiBar;
    sentimentPanel senti;
    TreeNode node;
    ViewController parent;
    JLabel title = new JLabel();
    Rectangle myRect;
    int level;
    //List<LabelText> labels;

    HashMap<Integer, LabelText> labels = new HashMap<Integer, LabelText>();

    HashMap<Integer, LabelText> removed_labels = new HashMap<Integer, LabelText>();

    public HashMap<Integer, LabelText> getRemoved_labels() {
        return removed_labels;
    }

    public void setRemoved_labels(HashMap<Integer, LabelText> removed_labels) {
        this.removed_labels = removed_labels;
    }

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
//    @Override
//    public void paintComponent(Graphics g) {
//        super.paintComponent(g);
//     
//         currg = g;
//        int width = this.getWidth();
//        int height = this.getHeight();
//        int size = node.getArrayValue().size();
//
////        
////
////
////		Graphics2D g2d = (Graphics2D) g;
////                if (myBI!=null)
////		g2d.drawImage(myBI, null, 40,0);
//	
//        
//        
////        update(g);
//        
//
////            for (int i=0; i<size; i++)
////            {
////                g.setColor(Color.black);
////                
////                if (i==0)
////                {
////                    
////                    g.drawLine(0, height/2, width/size*i, height/2 - (int) (node.getArrayValue().get(i)*this.getHeight()));
////                }
////                else
////                g.drawLine(width/size*i-1, height/2 - (int) (node.getArrayValue().get(i-1)*this.getHeight()), width/size*i, height/2 - (int) (node.getArrayValue().get(i)*this.getHeight()));
////                
////            }
//
//    }
    public boolean isMouseOvered() {

        return mouseOvered;
    }

    public void setMouseOvered(boolean mouseOvered) {
        this.mouseOvered = mouseOvered;
    }

    public HashMap<Integer, LabelText> getLabels() {
        return labels;
    }

    public void setLabelsFromList(List<LabelText> r) {
        for (int i = 0; i < r.size(); i++) {

            r.get(i).setParentTreeNodePanel(this);
            this.labels.put(r.get(i).getIndex(), r.get(i));
        }

    }

    // Have two layout scheme. One is for vertical friendly and one is for horizontal friendly
    public void updateLayout() {

        this.setBounds(this.myRect);

        if (myRect.width >= myRect.height) {
            // SentiBar goes from left to right. Always show

            // Topic List occupy only left side of the view
            scrollPane.setBounds(SystemPreferences.treemapBorderWidth, 24, (int) topic_list_panel_width, myRect.height - 24);

            // Time View occupies the bottom of the view
            timeviewPanel.setBounds((int) topic_list_panel_width + SystemPreferences.treemapBorderWidth, myRect.height * 4 / 5, myRect.width, myRect.height / 5);

            // Map Image is set on the right between Sentibar and teh Time View
        } else {
            // Layout Vertically

            /* SentiBar goes from left to right. Always show */
            /*Topic List occupy across the panel but shorter*/
            scrollPane.setBounds(SystemPreferences.treemapBorderWidth, 24, this.myRect.width, 2 * myRect.height / 5);

            // Map Image is set on the right between Sentibar and teh Time View

            /*Topic List occupy across the panel but shorter*/
            timeviewPanel.setBounds(SystemPreferences.treemapBorderWidth, myRect.height * 4 / 5, myRect.width, myRect.height / 5);

        }

        if (SystemPreferences.isNormalizationNecessary) {
            timeviewPanel.UpdateTemporalView(new Dimension(myRect.width, myRect.height / 5), timeviewPanel.getGlobalNormalizingValue());
        }else{
            timeviewPanel.UpdateTemporalView(new Dimension(myRect.width, myRect.height / 5), timeviewPanel.getLocalNormalizingValue());
        }
        

//        wordCloudPanel.setBounds(0, sentiPieSize, myRect.width, myRect.height * 3 / 5 - sentiPieSize);
//        wordCloudPanel.setOpaque(false);
//        wordRemoveWordPanel.setBounds(0, myRect.height * 3 / 5, myRect.width, myRect.height * 1 / 5);
//        wordRemoveWordPanel.setLayout(new FlowLayout());
        //tmp.setPreferredSize(new Dimension(myRect.width-40, 40));
//       
        Border bLine = BorderFactory.createMatteBorder(
                                    5, 5, 5, 5, node.getColor());
        
        this.setBorder(bLine);

//        if (mouseOvered) {
//            //this.setBorder(bLine);
//            this.setBackground(Color.red);
//        } else {
//            this.setBorder(null);
//
//        }
        
        this.setBackground(new Color(39, 39, 39));

        //this.getRootPane().revalidate();
    }

    /// Public constructor
    public JPanel wordCloudPanel = new JPanel();
    public JPanel wordRemoveWordPanel = new JPanel();
    Graphics currg;

    private final TemporalViewPanel timeviewPanel;
    private final float topic_list_panel_width;
    int sentiPieSize = 40;
    JPanel infoPanel = new JPanel();
    JScrollPane scrollPane;

    public TreeMapNodePanel(ViewController v, TreeNode t, int l, Rectangle r, /*BufferedImage bi*/ TemporalViewPanel tp) throws IOException {
        parent = v;
        level = l;
        node = t;
        myRect = r;
        timeviewPanel = tp;

        this.setBounds(myRect);
        this.setBackground(node.getColor());

        // Determine the minimum width for Topic List Panel
        if (myRect.width / 4 < SystemPreferences.topicListPanelMinWidth) {
            topic_list_panel_width = SystemPreferences.topicListPanelMinWidth;
        } else {
            topic_list_panel_width = myRect.width / 4;
        }

        title.setText(t.getValue());

        if (!t.getChildren().isEmpty()) {
            title.setVisible(false);
            this.add(title);
        }

        //TODO: Maybe making this a switch so people can change between sentiment bar or pie
//        senti = new sentimentPanel(node.getSentiAgg().pos, node.getSentiAgg().neg, node.getSentiAgg().count, new Rectangle(0, 0, sentiPieSize, sentiPieSize));
//        this.add(senti);
//        senti.setBounds(new Rectangle(0, 0, sentiPieSize, sentiPieSize));
        sentiBar = new SentimenBar(node.getSentiAgg().pos, node.getSentiAgg().neg, node.getSentiAgg().count, new Rectangle(SystemPreferences.treemapBorderWidth, 0, this.getWidth(), SystemPreferences.sentimentBarHeight));
        this.add(sentiBar);

        this.add(timeviewPanel);
//        timeviewPanel.setBounds(40, 0, myRect.width-40, 40);
//        timeviewPanel.setPreferredSize(new Dimension(myRect.width-40, 40));
//        timeviewPanel.UpdateTemporalView(new Dimension(myRect.width-40, 40), timeviewPanel.getGlobalNormalizingValue());
//        
//        myBI = bi;
//        
//        if (this.getWidth()-40>0)
//            myBI = Thumbnails.of(bi).size(this.getWidth()-40, 40).asBufferedImage();
//        String msg = "";
//        msg += "Average: " + node.avg_unNorm + "\n";
//        msg += "STD: " + node.std_unNorm + "\n";
//
//        JTextArea textArea = new JTextArea(msg);
//        scrollPane = new JScrollPane(textArea);
//        textArea.setLineWrap(true);
//        textArea.setWrapStyleWord(true);

        /*Set up the Topic Term List Display*/
        CheckListItem[] inputs = new CheckListItem[node.getNodeTopics().length - 2];

        for (int i = 0; i < node.getNodeTopics().length - 2 ; i++) {
            inputs[i] = new CheckListItem(node.getNodeTopics()[i+2]);
            inputs[i].setSelected(true);
        }
      
        JList topicList = new JList(inputs);

        // Use a CheckListRenderer (see below) 
        // to renderer topicList cells
        topicList.setCellRenderer(new CheckListRenderer());
        topicList.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        // Add a mouse listener to handle changing selection
        topicList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                JList list = (JList) event.getSource();

                // Get index of item clicked
                int index = list.locationToIndex(event.getPoint());
                CheckListItem item = (CheckListItem) list.getModel().getElementAt(index);

                // Toggle selected state
                item.setSelected(!item.isSelected());

                // Repaint cell
                list.repaint(list.getCellBounds(index, index));
            }
        });
        scrollPane = new JScrollPane(topicList);

        this.add(scrollPane);
//        this.add(wordCloudPanel);
//        wordCloudPanel.setLayout(null);
//
//        wordRemoveWordPanel.setBackground(Color.darkGray);
//        wordRemoveWordPanel.setLayout(null);
//        this.add(wordRemoveWordPanel);

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

        if (myBI != null && (this.getWidth() - 40) > 0) {
            this.myBI = Thumbnails.of(myBI)
                    .size(myRect.width - 40, 40)
                    .asBufferedImage();
        }

    }

    List<LabelWordleLite> list = new ArrayList<LabelWordleLite>();

    public void DrawWordleCloud(Point2D p, List<LabelText> ls) {

//        
//        WordleAlgorithmLite alg = new WordleAlgorithmLite( new Rectangle2D.Double(0,0,wordCloudPanel.getWidth(), wordCloudPanel.getHeight()));
////.wordCloudPanel.getBounds());
//
//        alg.place(topicList);
//
//      
//
//        
//        for (LabelWordleLite word : topicList) {
//
//          //  setLabelVisualPos(word);
//            
//        }
        for (int i = 0; i < list.size(); i++) {

            Point2D p1 = list.get(i).getLocation();

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
            Rectangle2D strBound = new Rectangle2D.Double(0, 0, 50, 30);
            if (currg != null) {
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
        Rectangle2D strBound = new Rectangle2D.Double(0, 0, 0, 0);
        FontMetrics fm = null;

        if (currg != null) {
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
        double x = location.getX() - strBound.getWidth() / 2;//+glyphBound.getX()p.getX() + location.getX() - glyphBound.getX() / 2 - strBound.getWidth() / 2;

        double y = 0;

        if (fm == null) {
            y = location.getY() + strBound.getHeight() / 2;//- glyphBound.getY() / 2p.getY() + location.getY() - glyphBound.getY() / 2  - strBound.getHeight() / 2;
        } else {
            y = location.getY() + strBound.getHeight() / 2;//-  glyphBound.getY() / 2p.getY() + location.getY() - glyphBound.getY() / 2  - strBound.getHeight() / 2;
        }
        //fm.getDescent()

        Point2D vilocation = new Point2D.Double(x, y);

//        System.out.println(x + " " + y);
        // TODO: Int Points may just limit the placement
        // Where do we use vi?
        //vi.setLocation(new Point2D.Double( vilocation.getX(),  vilocation.getY()));// + fm.getDescent() - strBound.getHeight() / 2));
        vi.setBounds((int) vilocation.getX(), (int) vilocation.getY(), (int) strBound.getWidth(), (int) strBound.getHeight());
        vi.setBounds((int) vilocation.getX(), (int) vilocation.getY(), (int) strBound.getWidth(), (int) strBound.getHeight());
        //System.out.println(p + " " +location);

        //System.out.println("xxx" + fm.getDescent());
        //Rectangle2D ss = new Rectangle2D.Double(vilocation.getX(), vilocation.getY(), glyphBound.getWidth(), glyphBound.getHeight());
        //vi.setRect(ss);
        // vi.setX(location.getX() + glyphBound.getX() + strBound.getWidth() / 2);
        // vi.setY(location.getY() + fm.getDescent() - strBound.getHeight() / 2);
    }

    public void setLabelBounds(List<Rectangle2D> bound) {

        for (int i = 0; i < bound.size(); i++) {

            labels.get(i).setBounds((int) (bound.get(i).getX() - wordCloudPanel.getWidth() / 4),
                    (int) (bound.get(i).getY() - wordCloudPanel.getHeight() / 4),
                    (int) bound.get(i).getWidth(), (int) bound.get(i).getHeight());

            // labels.get(i).setBackground(Color.red);    
            //labels.get(i).setOpaque(true);
            labels.get(i).setFont(labels.get(i).getFont());
            labels.get(i).setText(labels.get(i).getString());

//            wordCloudPanel.add(labels.get(i));
        }

    }

    class MyCellRenderer extends JLabel implements ListCellRenderer {

        public MyCellRenderer() {
            setOpaque(true);
        }

        @SuppressWarnings("empty-statement")
        public Component getListCellRendererComponent(JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {

            setText(value.toString());

            Color background;
            Color foreground;

            // check if this cell represents the current DnD drop location
            JList.DropLocation dropLocation = list.getDropLocation();
            if (dropLocation != null
                    && !dropLocation.isInsert()
                    && dropLocation.getIndex() == index) {

                background = Color.BLUE;
                foreground = Color.WHITE;

                // check if this cell is selected
            } else if (isSelected) {
                background = Color.RED;
                foreground = Color.WHITE;

                // unselected, and not the DnD drop location
            } else {
                background = Color.WHITE;
                foreground = Color.BLACK;
            };

            setBackground(background);
            setForeground(foreground);

            return this;
        }
    }

    // Represents items in the topicList that can be selected
    class CheckListItem {

        private String label;
        private boolean isSelected = false;

        public CheckListItem(String label) {
            this.label = label;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean isSelected) {
            this.isSelected = isSelected;
        }

        public String toString() {
            return label;
        }
    }

// Handles rendering cells in the topicList using a check box
    class CheckListRenderer extends JRadioButton
            implements ListCellRenderer {

        public Component getListCellRendererComponent(
                JList list, Object value, int index,
                boolean isSelected, boolean hasFocus) {
            setEnabled(list.isEnabled());
            setSelected(((CheckListItem) value).isSelected());
            setFont(list.getFont());
            setBackground(list.getBackground());
            setForeground(list.getForeground());
            setText(value.toString());
            return this;
        }
    }
}
