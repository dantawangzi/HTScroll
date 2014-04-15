/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TasteAnalytics.Apollo.eventsview;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Area;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import com.TasteAnalytics.Apollo.GUI.ViewController;
import com.TasteAnalytics.Apollo.datahandler.CategoryBarElement;
import com.TasteAnalytics.Apollo.TemporalView.CategoryStream;
import com.TasteAnalytics.Apollo.TemporalView.ColorBrewer;
import java.awt.BasicStroke;
import com.TasteAnalytics.Apollo.TemporalView.TimeColumn;
import com.TasteAnalytics.Apollo.TemporalView.TreeNode;

/**
 *
 * @author Wenwen
 */
public class EventsViewPanel extends JPanel implements EventsViewListener, MouseListener {

    JFrame eventFrame;
    //EventsViewPanelRenderer newContentPane;
    /** The ParallelDisplay component we are assigned to. */
    ViewController parent;
    private int width, height;
    private boolean firsttime;
    private boolean needDoLayout;
    private BufferedImage bi;
    private Graphics2D curg2d;
    private Rectangle area;
    
    private TreeNode attechedNode;
    
    private List<float[]> topicStreams;
    private List<float[]> unormStreams;
    private int numOfTopics, numOfColumns;
    private int margin = 24;
    private CategoryBarElement cBE;
    private int[][] eventIndicator;
    private List<String[]> reOrganizedTopics;
    
    private List<CategoryStream> categorystreams;
    private List<CategoryStream> categorystreamsOutlines;

    public TreeNode getAttechedNode() {
        return attechedNode;
    }

    public void setAttechedNode(TreeNode attechedNode) {
        this.attechedNode = attechedNode;
    }

    
    public List<CategoryStream> getCategorystreamsOutlines() {
        return categorystreamsOutlines;
    }

    public void setCategorystreamsOutlines(List<CategoryStream> categorystreamsOutlines) {
        this.categorystreamsOutlines = categorystreamsOutlines;
    }
    
    private Color[] streamColors;
    //private float[][] detectionResult;
    private final static Font NORMALFONT = new Font("Sans-Serif", Font.PLAIN, 12);
    private final static Font TIMEDOMAINFONT = new Font("Sans-Serif", Font.BOLD, 12);
    
    private final static Font LABLEFONT = new Font("Sans-Serif", Font.PLAIN, 10);
    private Point[][] controlpointsForMainRibbon; // Control points for the themeriver
    private Point[][] controlPointsForOutline; // Control points to draw outline area

    public Point[][] getControlPointsForOutline() {
        return controlPointsForOutline;
    }

    public void setControlPointsForOutline(Point[][] controlPointsForOutline) {
        this.controlPointsForOutline = controlPointsForOutline;
    }

    public EventsViewPanel() {
        
        
        
       
        
        
    }

    /**
     * Provide the Control Points for the ThemeRiver Renderer
     * @return
     */
    public Point[][] getControlpoints() {
        return controlpointsForMainRibbon;
    }

    public List<CategoryStream> getCategoryAreas() {
        return categorystreams;
    }
    private List<TimeColumn> timecolumns;

    public List<TimeColumn> getTimecolumns() {
        return timecolumns;
    }
    private int focusedCatgoryId = -99;

    public int getFocusedCatgory() {
        return focusedCatgoryId;
    }

    public void setFocusedCatgory(int focusedCatgory) {
        this.focusedCatgoryId = focusedCatgory;
        this.repaint();

    }

    public int getFocusedColumn() {
        return focusedColumnId;
    }

    public void setFocusedColumn(int focusedColumn) {
        this.focusedColumnId = focusedColumn;
    }

    public List<float[]> getEventStreams() {
        return cBE.getCategoryBar();
    }

    public CategoryBarElement getData() {
        return cBE;
    }
    private int focusedColumnId = -99;
    private Color[] colors;

    public Color[] getColors() {
        return colors;
    }

    public void setColor(int index, Color c) {
        colors[index] = new Color(ColorBrewer.colors[index][0], ColorBrewer.colors[index][1], ColorBrewer.colors[index][2]);
    }

//    public void setTopicSimilarities(List<Float> sims){
//        topicSims = new ArrayList<Float>();
//        topicSims = sims;
//    }
    public EventsViewPanel(ViewController vc, int tempWidth, int tempHeight) {
        //Create and set up the event window
      
        parent = vc;

        width = tempWidth;
        height = tempHeight;

      //  width = this.getWidth();
      //  height = this.getHeight();

          timecolumns = new ArrayList<TimeColumn>();
        
        currentStreams = new ArrayList<CategoryStream>();
        
        
        
        EventsViewInteractions einteractions = new EventsViewInteractions(this);
        addMouseListener(einteractions);
        addMouseMotionListener(einteractions);

//        pack();
        //pack();
//        this.setVisible(true);

        this.addComponentListener(new ComponentListener() {

            public void componentResized(ComponentEvent e) {

                ((EventsViewPanel) e.getComponent()).UpdateEventView(new Dimension(e.getComponent().getSize().width, e.getComponent().getSize().height));
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
        
        categorystreams = new ArrayList<CategoryStream>();
        categorystreamsOutlines = new ArrayList<CategoryStream>();
        
        timecolumns = new ArrayList<TimeColumn>();


        firsttime = true;
        needDoLayout = true;
    }
    
    private ArrayList<float[][]> detectionResults = new ArrayList<float[][]>();
    

    
    
    
    
    
    private Point[][] currentPoint;
    
    public void calculateRenderControlPointsOfEachHierarchy(CategoryBarElement data, TreeNode t, float normalValue) {
        
        float maxValue = 1.0f;//data.getMaximum();
        // hierarchicalPoint.clear();
        try {
            
            int numofYears = data.getNumOfYears();// Number of bars
            //System.out.println(numofYears);

            
            
            int numberOfCategories = t.getChildren().size();
            
            if (numberOfCategories == 0) {
                numberOfCategories = 1;
            }
            
            timecolumns.clear();



            //   System.out.println("localmaxValue  " + localmaxValue);

//            int scale = 1;//numberOfCategories;
//            int wscale = 1;
            
            currentPoint = new Point[numofYears + 2][numberOfCategories + 1];
            int h = (height) /* - margin*/;
            
         

            double verticalratio = (double) h;//localmaxValue;//maxValue;
            double horizontalratio = (double) width / (double) numofYears;
            
            
            for (int icol = 0; icol < numberOfCategories; icol++) {
                currentPoint[0][icol] = new Point(0, (int) (height  * 0.5 ));
                currentPoint[numofYears + 1][icol] = new Point(width  , (int) (height * 0.5 ));
            }
            
            currentPoint[0][numberOfCategories] = new Point(0 , (int) (height  / 1 * 0.5 ));
            currentPoint[numofYears + 1][numberOfCategories] = new Point(width , (int) (height  * 0.5 ));



            //find max value
            
            
            
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
                
                sum /= normalValue;
              //  System.out.println(sum + " " +normalValue);
                
                
                int offset = (int) (/*margin * 1.25*/ + (maxValue - sum) * 0.5 * verticalratio);
                for (int j = 0; j < numberOfCategories; j++) {
                    
                    currentPoint[i + 1][j] = new Point((int) ((i + 0.5) * horizontalratio ), offset );
                    offset += (int) (columValues.get(j) / normalValue * verticalratio);
                    
                }
                
                currentPoint[i + 1][numberOfCategories] = new Point((int) ((i + 0.5) * horizontalratio ), offset );
                //TODO: Fix the year
                timecolumns.add(new TimeColumn(i, null, null, i * horizontalratio, 0 , horizontalratio, height ));
            }

            //hierarchicalPoint.add(tempPoint);

            
        } catch (Exception e) {
            System.out.println("Calculate hierarchical control point function failed!_themeriver");
        }
    }
    
    
    
    //private Point[][] controlpoints;
    private Point[][] levelpoints;
    //private List<TimeColumn> timecolumns;
    private int numofYears, topicNumber;
    private float[] spacing;

    private void calculateRenderControlPoints(CategoryBarElement data) {

        int maxValue = 1;//data.getMaximum();
        spacing = new float[numOfTopics];
        try {
            numofYears = numOfColumns;
            topicNumber = numOfTopics;

            timecolumns.clear();

            // Considering the starting and ending points in the river
            controlpointsForMainRibbon = new Point[numofYears + 2][topicNumber + 1];
            controlPointsForOutline = new Point[numofYears + 2][topicNumber + 1];
            levelpoints = new Point[numofYears + 2][topicNumber + 1];

            //Ratio: Pixel per unit
            int h = height - margin;

            double verticalratio = (double) h / maxValue;
            double horizontalratio = (double) width / (double) numofYears;

            // Inserting the starting and ending points.
            for (int icol = 0; icol < topicNumber; icol++) {
                /*For symetric separate rivers*/
                controlpointsForMainRibbon[0][icol] = new Point(0, (int) (height * 0.5));
                controlpointsForMainRibbon[numofYears + 1][icol] = new Point(width, (int) (height * 0.5));

                controlPointsForOutline[0][icol] = new Point(0, (int) (height * 0.5));
                controlPointsForOutline[numofYears + 1][icol] = new Point(width, (int) (height * 0.5));

                levelpoints[0][icol] = new Point(0, (int) (icol * 30));
                levelpoints[numofYears + 1][icol] = new Point(width, (int) (icol * 30));

                spacing[icol] = 0;
            }

            controlpointsForMainRibbon[0][topicNumber] = new Point(0, (int) (height * 0.5));
            controlpointsForMainRibbon[numofYears + 1][topicNumber] = new Point(width, (int) (height * 0.5));
            controlPointsForOutline[0][topicNumber] = new Point(0, (int) (height * 0.5));
            controlPointsForOutline[numofYears + 1][topicNumber] = new Point(width, (int) (height * 0.5));

            levelpoints[0][topicNumber] = new Point(0, (int) (30 * topicNumber));
            levelpoints[numofYears + 1][topicNumber] = new Point(width, (int) (30 * topicNumber));


            for (int i = 0; i < numofYears; i++) {
                List<Float> columValues = data.getColum(i);
                for (int k = 0; k < columValues.size(); k++) {
                    if (columValues.get(k) == Float.NaN) {
                        columValues.set(k, (float) 0);
                    }
                }

                //XW: Hack to show all the themriver results
                float offset = 0;//height - 20 ;

                for (int j = 0; j < topicNumber; j++) {
                    //offset = (int) ((columValues.get(j) * verticalratio));
                    offset = (columValues.get(j));
                    if (spacing[j] < offset) {
                        spacing[j] = offset;
                    }
                }

                controlpointsForMainRibbon[i + 1][topicNumber] = new Point((int) ((i + 0.5) * horizontalratio), 0);//offset);
                controlPointsForOutline[i + 1][topicNumber] = new Point((int) ((i + 0.5) * horizontalratio), 0);//offset);
                levelpoints[i + 1][topicNumber] = new Point((int) ((i + 0.5) * horizontalratio), 0);
                
                //TODO: Fix the year
                timecolumns.add(new TimeColumn(i, null, null, i * horizontalratio, 0, horizontalratio, height));
            }

            float tmpSum = 0;
            for (Float f : spacing) {
                tmpSum += f;
            }
//            for(int i=0; i<spacing.length; i++){
//                spacing[i] = spacing[i] / tmpSum;
//            }

            int tmpSpace = 0;
            for (int i = 0; i < numofYears; i++) {
                List<Float> columValues = data.getColum(i);
                for (int k = 0; k < columValues.size(); k++) {
                    if (columValues.get(k) == Float.NaN) {
                        columValues.set(k, (float) 0);
                    }
                }
                tmpSpace = 0;
                int offset = 0;
                for (int j = 0; j < topicNumber; j++) {
                    offset = (int) ((columValues.get(j) / tmpSum * verticalratio));
                    if (j == 0) {
                        tmpSpace += (int) (spacing[0] * 0.5 / tmpSum * verticalratio);
                        levelpoints[i + 1][0] = new Point((int) ((i + 0.5) * horizontalratio), tmpSpace);
                        controlpointsForMainRibbon[i + 1][0] = new Point((int) ((i + 0.5) * horizontalratio), tmpSpace + offset);
                        controlPointsForOutline[i + 1][0] = new Point((int) ((i + 0.5) * horizontalratio), tmpSpace + offset + 1);

                    } else {
                        tmpSpace += (int) ((spacing[j] * 0.5 + spacing[j - 1] * 0.5) / tmpSum * verticalratio);
                        controlpointsForMainRibbon[i + 1][j] = new Point((int) ((i + 0.5) * horizontalratio), offset + tmpSpace);
                        controlPointsForOutline[i + 1][j] = new Point((int) ((i + 0.5) * horizontalratio), offset + tmpSpace + 1);
                        levelpoints[i + 1][j] = new Point((int) ((i + 0.5) * horizontalratio), tmpSpace);

                    }

                    if (i == 0) {
                        controlpointsForMainRibbon[0][j].y = controlpointsForMainRibbon[0][j].y - (controlpointsForMainRibbon[1][j].y - levelpoints[1][j].y) / 2;
                        controlPointsForOutline[0][j].y = controlPointsForOutline[0][j].y - (controlPointsForOutline[1][j].y - levelpoints[1][j].y) / 2;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Calculate control point function failed!_event");
        }
    }

    public void UpdateEventView(Dimension size) {
        width = size.width;
        height = size.height;
        calculateRenderControlPoints(cBE);
        firsttime = false;
        needDoLayout = true;
        //EventsLabelPositioning.EventsLabelPositioningCal(cBE.topicYearKwIdx, eventIndicator, contours, levelpoints);
        repaintView();
    }

    private void clearPreviousValues() {
        categorystreams.clear();
    }

    public void repaintView() {
//        area = new Rectangle(width, height);
//        bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        area = new Rectangle(width, height);
        bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        curg2d = bi.createGraphics();
        curg2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        update(curg2d);
        //this.getContentPane().getComponents()[0].repaint();
        this.repaint();
    }

    @Override
    public void paint(Graphics g) {
        //super.paint(g);
        update(g);
    }

    @Override
    public void update(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        //System.out.println("Rendering");
        if (firsttime) {
            area = new Rectangle(width, height);
            bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);//(BufferedImage) createImage(width, height);
            curg2d = bi.createGraphics();
            curg2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            firsttime = false;
        }

        curg2d.setColor(Color.WHITE);
     
        curg2d.fillRect(0, 0, area.width, area.height);

        if (needDoLayout) {

            clearPreviousValues();
            
              computerZeroslopeAreasHierarchy(0);
            
            needDoLayout = false;
        }


        renderAreas(curg2d);

        
         curg2d.setColor(Color.black);
        if (!testEventPoints.isEmpty())
        {
            
            curg2d.setStroke(new BasicStroke(2));
            
            for (int i=0; i<contours.length;i++)
                for(int j=0; j<contours[0].length; j++){
                      if(contours[i][j][0] != null){
                         curg2d.draw(contours[i][j][0]);
                         curg2d.draw(contours[i][j][1]);    
                      }
                }
            
            
            
            
            curg2d.setStroke(new BasicStroke(1));
            
            
        }       
                
                
         curg2d.setColor(Color.BLACK);
         
         
        drawTimeLine(curg2d);

        // Draws the buffered image to the screen.
        g2.drawImage(bi, 0, 0, this);
    }
    private AlphaComposite highlightcomposite = AlphaComposite.getInstance(
            AlphaComposite.SRC_OVER, 0.2f);
    private AlphaComposite topicDehighlightcomposite = AlphaComposite.getInstance(
            AlphaComposite.SRC_OVER, 0.4f);
    private AlphaComposite labelcomposite = AlphaComposite.getInstance(
            AlphaComposite.SRC, 1.0f);
    private AlphaComposite backgroundcomposite = AlphaComposite.getInstance(
            AlphaComposite.SRC_OVER, 0.1f);

    
    private Color categoryColor;

    public Color getCategoryColor() {
        return categoryColor;
    }

    public void setCategoryColor(Color categoryColor) {
        this.categoryColor = categoryColor;
    }
    
    
    private void renderAreas(Graphics2D g2d) {


        
           for (int i = 0; i < currentStreams.size(); i++) {
                    
                    if (focusedCatgoryId != -99) {
                        if (focusedCatgoryId == i) {
                            g2d.setComposite(labelcomposite);
                        } else {
                            g2d.setComposite(topicDehighlightcomposite);
                        }
                    } else {
                        g2d.setComposite(labelcomposite);
                    }
                    
                    g2d.setColor(categoryColor);
                    g2d.fill(currentStreams.get(i).getRenderRegion());
                    
                }
           
           
//        if (categorystreams != null && categorystreams.size() != 0) {
//
//            //System.out.println("Comapre to Outline" + categorystreamsOutlines.size());
//
//            for (int i = 0; i < categorystreamsOutlines.size(); i++) {
//
//                g2d.setComposite(labelcomposite);
//                g2d.setColor(Color.BLACK);
//                
//                //g2d.fill(categorystreamsOutlines.get(i).getRenderRegion());
//                for(int j=0; j<contours[0].length; j++){
//                      if(contours[i][j][0] != null){
//                         g2d.draw(contours[i][j][0]);
//                         g2d.draw(contours[i][j][1]);
//                       }
//                }
//                             
//                
//            }
//
//
//            g2d.setComposite(labelcomposite);
//
//            //Set Background Color
//            g2d.setColor(Color.WHITE);
//
//            //Start from the left side of the panel
//            g2d.fillRect(0, controlpointsForMainRibbon[0][0].y, controlpointsForMainRibbon[0][0].x - 1,
//                    controlpointsForMainRibbon[0][controlpointsForMainRibbon[0].length - 1].y - controlpointsForMainRibbon[0][0].y);
//
//            //Finish to the right side of the panel
//            g2d.fillRect(controlpointsForMainRibbon[controlpointsForMainRibbon.length - 1][0].x + 1, controlpointsForMainRibbon[controlpointsForMainRibbon.length - 1][0].y,
//                    width - (controlpointsForMainRibbon[controlpointsForMainRibbon.length - 1][0].x + 1),
//                    controlpointsForMainRibbon[controlpointsForMainRibbon.length - 1][controlpointsForMainRibbon[0].length - 1].y - controlpointsForMainRibbon[controlpointsForMainRibbon.length - 1][0].y);
//            
//            
//            //Start from the left side of the panel
//            g2d.fillRect(0, controlPointsForOutline[0][0].y, controlPointsForOutline[0][0].x - 1,
//                    controlPointsForOutline[0][controlPointsForOutline[0].length - 1].y - controlPointsForOutline[0][0].y);
//
//            //Finish to the right side of the panel
//            g2d.fillRect(controlPointsForOutline[controlPointsForOutline.length - 1][0].x + 1, controlPointsForOutline[controlpointsForMainRibbon.length - 1][0].y,
//                    width - (controlPointsForOutline[controlPointsForOutline.length - 1][0].x + 1),
//                    controlPointsForOutline[controlPointsForOutline.length - 1][controlPointsForOutline[0].length - 1].y - controlPointsForOutline[controlPointsForOutline.length - 1][0].y);
//        }
    }

    private void drawTimeLine(Graphics2D g2d) {

        g2d.setColor(Color.LIGHT_GRAY);
//        g2d.fillRect(0, 0, width, margin / 2);
//        g2d.fillRect(0, height - (margin / 2), width, margin / 2);

        if (controlpointsForMainRibbon != null && controlpointsForMainRibbon.length != 1) {
            int number = numofYears;
            double ratio = (double) width / (double) number;
            g2d.setFont(LABLEFONT);
            FontMetrics metrics = this.curg2d.getFontMetrics();


            g2d.setColor(Color.BLACK);
            for (int i = 0; i < number; i++) {

                // Upper Boundary
                //g2d.drawLine((int) ((i + 1) * ratio), 0, (int) ((i + 1) * ratio), 40);

                // Lower Bondary
                g2d.drawLine((int) ((i + 1) * ratio), height, (int) ((i + 1) * ratio), height - 40);


//                String upperstr = "Something";//TimeUtils.returnDate(timecolumns.get(i).getFarTime(), data.getData().get(i).getDateFormatter());
//                Long tmpMili = data.getBeginningTime() + data.getTimeInterval()*i;
//                SimpleDateFormat sdf = new SimpleDateFormat("MM dd, yyyy HH:mm");
//                String lowerstr = sdf.format(new Date(tmpMili));//Integer.toString(data.getBeginningYear()+i);//TimeUtils.returnDate(timecolumns.get(i).getNearTime(), data.getData().get(i).getDateFormatter());
//
//                while ((metrics.stringWidth(upperstr) > (ratio - 10)) && (upperstr.length() > 3)) {
//                    upperstr = upperstr.substring(0, upperstr.length() - 3) + ".";
//                }
//
//                while ((metrics.stringWidth(lowerstr) > (ratio - 10)) && (lowerstr.length() > 3)) {
//                    lowerstr = lowerstr.substring(0, lowerstr.length() - 3) + ".";
//                }
//
//                g2d.drawString(upperstr, (int) ((i) * ratio + (ratio - metrics.stringWidth(upperstr)) / 2), 10);
//                g2d.drawString(lowerstr, (int) ((i) * ratio + (ratio - metrics.stringWidth(upperstr)) / 2), height - 10);
            }
            
            /**
             * Draw labels
             */
//            String tmpKW;
//            int tmpId = 0;
//            Point tmpP;
//            TextLayout layout;
//            FontRenderContext frc = g2d.getFontRenderContext();
//            try{
//            if(!EventsLabelPositioning.labels.isEmpty()){
//                for(int i=0; i<EventsLabelPositioning.labels.size(); i++){
//                for(int j=0; j<EventsLabelPositioning.labels.get(i).size(); j++){
//                    tmpId = EventsLabelPositioning.labels.get(i).get(j).kwIdx;
//                    tmpKW = reOrganizedTopics.get(i+1)[tmpId+2];  //First one is header
//                    layout = new TextLayout(tmpKW, LABLEFONT, frc);
//                    //layout.getBounds()
//                    tmpP = EventsLabelPositioning.labels.get(i).get(j).p;
//                    layout.draw(g2d, tmpP.x, tmpP.y);
//                }
//            }
//            }
//            }catch(Exception e){
//                System.out.println("Labels are empty");
//            }
            
        }
    }

    


    private List<CategoryStream> currentStreams;
    
     public void computerZeroslopeAreasHierarchy(int categoryIndex) {
        
        
        Point[][] tempPoint1 = currentPoint;// hierarchicalPoint.get(categoryIndex);

        if (tempPoint1 != null && tempPoint1.length != 1) {


            //  ArrayList<CategoryStream> tempCategoryStream = new ArrayList<CategoryStream>();

            //GeneralPaths which will convert to splines
            GeneralPath[] regions = new GeneralPath[tempPoint1[0].length];
            
            double ypos = (height/* - margin*/) / 2;
            double maxposx = tempPoint1[0][0].x;
            CubicCurve2D[] oldcurves = new CubicCurve2D[tempPoint1[0].length];
            
            for (int i = 0; i < tempPoint1[0].length; i++) {
                //Set the start of the spline
                oldcurves[i] = new CubicCurve2D.Double();
                oldcurves[i].setCurve(0, ypos, maxposx / 4, ypos, maxposx / 2, ypos, maxposx * 0.75,
                        tempPoint1[0][i].y);
                //Initializing the general path
                regions[i] = new GeneralPath();
            }
            
            
            for (int i = 0; i < tempPoint1.length - 1; i++) {
                for (int j = 0; j < tempPoint1[0].length; j++) {
                    
                    Point p1 = tempPoint1[i][j];
                    Point p4 = tempPoint1[i + 1][j];

                    //1. Inspection point: X-pos: half distance between P1 and p4
                    //                  Y-pos: the P1
                    Point2D.Double p2 = new Point2D.Double((double) (p1.x + (p4.x - p1.x) * 0.5), (double) p1.y);

                    //1. Inspection point: X-pos: half distance between P1 and p4
                    //                    Y-pos: the P1
                    Point2D.Double p3 = new Point2D.Double((double) (p1.x + (p4.x - p1.x) * 0.5), (double) p4.y);
                    
                    CubicCurve2D.Double currcurve = new CubicCurve2D.Double();
                    currcurve.setCurve(p1, p2, p3, p4);

                    //New Value for the spline
                    oldcurves[j] = currcurve;
                    regions[j].append(currcurve, true);
                }
            }
            
            
            for (int i = 0; i < tempPoint1[0].length - 1; i++) {
                GeneralPath currpath = new GeneralPath();
                currpath.append(regions[i], false);
                
                currpath.lineTo((float) width, (float) height);
                currpath.lineTo((float) 0, (float) height);
                currpath.closePath();
                
                GeneralPath currpath2 = new GeneralPath();
                currpath2.append(regions[i + 1], false);
                
                currpath2.lineTo((float) width, (float) 0);
                currpath2.lineTo((float) 0, (float) 0);
                currpath2.closePath();
                
                Area upper = new Area(currpath);
                upper.intersect(new Area(currpath2));

                currentStreams.add(new CategoryStream(upper));
            }

        }


    }
     
     
     
     
     
     
     

    public ArrayList<float[][]> getDetectionResults() {
        return detectionResults;
    }
    
     private float eventThreshold = (float) 2.0;

    public float getEventThreshold() {
        return eventThreshold;
    }

    public void setEventThreshold(float eventThreshold) {
        this.eventThreshold = eventThreshold;
    }
     
     
    
    public void detectEvents(float eThreshold, TreeNode t) {
        
        List<float[]> unormStreams = new ArrayList<float[]>();
        detectionResults.clear();
        
        if (!t.getChildren().isEmpty()) {
            for (int i = 0; i < t.getChildren().size(); i++) {
                List<Float> temp = ((TreeNode) t.getChildren().get(i)).getUnNormArrayValue();
                
                float[] tempf = new float[temp.size()];
                for (int j = 0; j < temp.size(); j++) {
                    tempf[j] = temp.get(j);
                }
                
                unormStreams.add(tempf);
                
            }
        } else {
            List<Float> temp = ((TreeNode) t).getUnNormArrayValue();
            
            float[] tempf = new float[temp.size()];
            for (int j = 0; j < temp.size(); j++) {
                tempf[j] = temp.get(j);
            }
            
            unormStreams.add(tempf);
        }


        //List<float[]>
        
        for (float[] fs : unormStreams) {
            detectionResults.add(Cusum.cusumProcess(fs, (float) eThreshold));
        }


         computeEventOutlineArea();

    }
    CubicCurve2D[][][] contours;

    List<Point2D> testEventPoints = new ArrayList<Point2D>();
    
    
      public void computeEventOutlineArea() {
        if (currentPoint != null && currentPoint.length != 1) {

            contours = new CubicCurve2D[currentPoint[0].length-1][currentPoint.length-1][2];//0-top curve; 1 - bottom curve
            
            
            float[][] flags = new float[currentPoint.length+1][currentPoint[0].length+1];
            
            //the first and last point in each ribbon are artificial
            for (int i = 0; i < detectionResults.size(); i++) {
                  for (int j = 0; j < detectionResults.get(i).length; j++) {
                    if (!(detectionResults.get(i)[j][1] > 0)) {
                        flags[j][i] = -1;
                    }
                }
            }
            
            testEventPoints.clear();
            
             for (int i = 0; i < currentPoint.length - 1; i++) {//number of time slots
                for (int j = 0; j < currentPoint[i].length - 1; j++) {

                        if(flags[i][j] != -1)
                        {
                            testEventPoints.add(currentPoint[i][j]);
                            
                             Point p1 = currentPoint[i][j];
                   
                             Point p4 = currentPoint[i + 1][j];


                        CubicCurve2D.Double currcurve = new CubicCurve2D.Double();
                        CubicCurve2D.Double bottomcurve = new CubicCurve2D.Double();
          
                        Point2D.Double p2 = new Point2D.Double((double) (p1.x + (p4.x - p1.x) * 0.5), (double) p1.y);
                        Point2D.Double p3 = new Point2D.Double((double) (p1.x + (p4.x - p1.x) * 0.5), (double) p4.y);
                    
                


                        currcurve.setCurve(p1, p2, p3, p4);
                        

           
                        contours[j][i][0] = currcurve;
                        
                        
                        
                        p1 = currentPoint[i][j+1];
                   
                             p4 = currentPoint[i + 1][j+1];


                        
                        bottomcurve = new CubicCurve2D.Double();
          
                        p2 = new Point2D.Double((double) (p1.x + (p4.x - p1.x) * 0.5), (double) p1.y);
                        p3 = new Point2D.Double((double) (p1.x + (p4.x - p1.x) * 0.5), (double) p4.y);
                        
                        bottomcurve.setCurve(p1, p2, p3, p4);
                        
                        contours[j][i][1] = bottomcurve;
                            
                        }
                            
                            
                        }
                
             }

        }
        
             //repaintView();
    }
     
     
     
    public void mouseClicked(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mousePressed(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseReleased(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseEntered(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseExited(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
