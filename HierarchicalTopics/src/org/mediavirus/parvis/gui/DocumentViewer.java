/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DocumentViewer.java
 *
 * Created on Apr 28, 2010, 12:57:07 PM
 */
package org.mediavirus.parvis.gui;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultHighlighter;
import org.mediavirus.parvis.file.CSVReader;
import org.mediavirus.parvis.model.DataTable;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.mediavirus.parvis.gui.temporalView.renderer.TemporalViewPanel;
import org.mediavirus.parvis.gui.temporalView.renderer.TreeNode;


import com.mysql.jdbc.Connection;
import java.net.UnknownHostException;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mediavirus.parvis.gui.temporalView.renderer.ZoomedTemporalViewPanel;

/**
 *
 * @author wdou1
 */
public class DocumentViewer extends JFrame {

    /**
     * The ParallelDisplay component we are assigned to.
     */
    ViewController parent;
    TemporalViewPanel parentPanel;
    TreeNode curTreeNode;
    int selectedTimeColumn;
    List<Integer> selectedDocuments;
    SelectionListener listener;//for document selection
    public Map<String, Integer> awardNum2docIdx;//identified award number as common ground
    TreeMap<Integer, Integer> sortedResults;

    public List<Integer> getSelectedDocuments() {
        return selectedDocuments;
    }

    /**
     * Creates new form DocumentViewer
     */
    public DocumentViewer() {
        initComponents();
    }

    public DocumentViewer(ViewController viewController) {
        this();
        this.parent = viewController;
    }

    /*DXW: Simplify the Initializer*/
    public DocumentViewer(final TemporalViewPanel p, final Point2D pt) throws IOException {

        initComponents();

        //Set this frame just to close itself
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Set Frame behavior
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                p.removeDocumentViewerat(pt);
            }
        });

        parentPanel = p;
        this.parent = p.parent;


        
        
        
        
        
        
        
        //BufferedImage image = ImageIO.read(new File(parent.csvfFolderPath + "Vastopolis_MapSmall.png"));


        selectedDocuments = new ArrayList<Integer>();

        // Point X = Time Column, Y = Selected Topic Value
        selectedTimeColumn = (int) pt.getX();

        // Determine whether is selecting a whole column or not. Equals to -99 indicate the selection of a full column
        if (pt.getY() != -99) {

            // Determine which TreeNode in the Hierarchical Topic View is currently selected
            if (!p.currentNode.getChildren().isEmpty()) {
                curTreeNode = (TreeNode) p.currentNode.getChildren().get((int) pt.getY());
            } else {
                curTreeNode = (TreeNode) p.currentNode;
            }

            // Set the Frame background color to the single column selected
            this.getContentPane().setBackground(curTreeNode.getColor());

        } else {

            curTreeNode = (TreeNode) p.currentNode;

            // Color for selecting a column would be close to white
        }

        
        if (p instanceof ZoomedTemporalViewPanel)
        {
            System.out.println("omng");
            selectDocumentsToPresentZoomed((double) this.docSelectionThreshold.getValue() / 100.0f, ((ZoomedTemporalViewPanel)p).getDocumentInThisPanel());
        }
        else                
            selectDocumentsToPresent((double) this.docSelectionThreshold.getValue() / 100.0f);

        //System.out.println("selectedDoc size " + selectedDocuments.size());
        // add elements to al, including duplicates
        Set<Integer> hs = new LinkedHashSet<Integer>();
        hs.addAll(selectedDocuments);
        // System.out.println("hs size " + hs.size());
        selectedDocuments.clear();

        selectedDocuments.addAll(hs);
        //System.out.println("selectedDoc size " + selectedDocuments.size());

        columnNames = p.parent.getInternalDocs().get(0);
        tmpDocs = p.parent.getInternalDocs();

        
        if (!p.parent.b_readFromDB)
            this.updateDocViewContent(selectedDocuments);
        else
            this.updateDocViewContent(selectedDocuments, p.parent.host, p.parent.port, p.parent.database, p.parent.collection, p.parent.nameFields);
        
        


//        geoHeatMapPanel.setLayout(null/*new CardLayout()*/);
//        geoHeatMapPanel.setPreferredSize(new Dimension(520, 264));
//        geoHeatMapPanel.setVisible(true);



//        JLabel Label = new JLabel();
//
//
//        Label.setBounds(0, 0, 520, 264);
//        // Label.setBackground(new Color(0, 0, 0, .5f));  
//        // Label.setOpaque(true);
//
//
//
//
//
        BufferedImage htimage = createHeatMap(1000, 500);
        parent.getVCGF().setHeatmapImg(htimage);

        //Label.setIcon(new ImageIcon( createHeatMap(520,264)));
        //   geoHeatMapPanel.add(Label);

//           if (image != null) {
//            JLabel picLabel = new JLabel(new ImageIcon( createHeatMap(520,264)));
//
//           // picLabel.setOpaque(true);
//            picLabel.setBounds(0, 0, 520, 264);
//            picLabel.setBackground(new Color(0, 0, 0, .5f));
//           
//             geoHeatMapPanel.add(picLabel);
//            
//        }


        //parent.getVCGF().setHeatmapImg(htimage);



        //parent.updateGeoView(curTreeNode.getColor(), selectedDocuments);


    }

    public DocumentViewer(final TemporalViewPanel p, TreeNode t, int timecolumn_id) throws IOException {

        initComponents();

        //Set this frame just to close itself
        //setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // Set Frame behavior
        parentPanel = p;
        this.parent = p.parent;

        selectedDocuments = new ArrayList<Integer>();

        // Point X = Time Column, Y = Selected Topic Value
        selectedTimeColumn = (int) timecolumn_id;

        curTreeNode = t;

        selectDocumentsToPresent((double) this.docSelectionThreshold.getValue() / 100.0f);


        Set<Integer> hs = new LinkedHashSet<Integer>();
        hs.addAll(selectedDocuments);
        // System.out.println("hs size " + hs.size());
        selectedDocuments.clear();
        selectedDocuments.addAll(hs);


    }
    
    float localMaxValue = -9;
    
     float LocalImageMap[] ;
    public float GenerateImgMap(int imgWidth, int imgHeight)
    {
        if (parent.geoLocations.isEmpty()) {
            return 99999;
        }
        
         float boundary_left = (float) parent.twitterPointMax.getY();   //93.62f;
        float boundary_right = (float) parent.twitterPointMin.getY(); //93.12f;
        float boundary_top = (float) parent.twitterPointMax.getX();//42.35f;
        float boundary_bottom = (float) parent.twitterPointMin.getX();// 42.10f;

//        int imgWidth = 520;
//        int imgHeight = 264;

        //BufferedImage img = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);

        List<Point2D> tempTwitterLocations = new ArrayList<Point2D>();
        for (int i = 0; i < selectedDocuments.size(); i++) {
            int index = selectedDocuments.get(i);
            tempTwitterLocations.add(parent.geoLocations.get(index));

        }

        //WritableRaster raster = img.getRaster();
        LocalImageMap = new float[imgWidth * imgHeight];
        for (int i = 0; i < imgWidth * imgHeight; i++) {
            LocalImageMap[i] = 0;
        }

        int heatmapRadius = 7;

        for (int i = 0; i < tempTwitterLocations.size(); i++) {
            double ty = tempTwitterLocations.get(i).getX();
            double tx = tempTwitterLocations.get(i).getY();
            double x = (double) (imgWidth - (tx - boundary_right) / Math.abs(boundary_right - boundary_left) * imgWidth);
            double y = (double) (imgHeight - ((ty - boundary_bottom)) / (boundary_top - boundary_bottom) * imgHeight);

//           System.out.println("ty = " + ty + " tx = " + tx);
//           System.out.println("y = " + y + " x = " + x);

        
                    

            for (int ay = (int) (Math.ceil(y) - heatmapRadius); ay < Math.floor(y) + heatmapRadius; ay++) {
                for (int ax = (int) (Math.ceil(x) - heatmapRadius); ax < Math.floor(x) + heatmapRadius; ax++) {
                    if (ay >= 0 && ay < imgHeight && ax >= 0 && ax < imgWidth) {
                        
                        if (calculate_distance(x, y, ax, ay)>(heatmapRadius-3))
                            continue;
                        
                        double weight = calculate_gaussian_weight(x, y, ax, ay, 1.1);

                        int index = ay * imgWidth + ax;


                        LocalImageMap[index] += weight;
//			count[index]++;
                    }
                }
            }
        }

        float maxxx = -9;
        for (int i = 0; i < imgWidth * imgHeight; i++) {
            if (LocalImageMap[i] >= maxxx) {
                maxxx = LocalImageMap[i];
            }

        }
     
        localMaxValue = maxxx;
        return maxxx;
    }
    
    
    public BufferedImage createHeatMap(int imgWidth, int imgHeight, float maxValue) {

        if (parent.geoLocations.isEmpty()) {
            return null;
        }

        BufferedImage img = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);


        WritableRaster raster = img.getRaster();




        for (int y = 0; y < imgHeight; y++) {
            for (int x = 0; x < imgWidth; x++) {
                int index = y * imgWidth + x;
                //System.out.println(map[index]);



                if (LocalImageMap[index] != 0) {

                    if (LocalImageMap[index] >= maxValue / 10) {
                        LocalImageMap[index] = 255;
                    } else {
                        LocalImageMap[index] = (float) (LocalImageMap[index] / (maxValue / 10) * 255);
                    }
                    int r = 0, g = 0, b = 0;
                    int alpha = (int) LocalImageMap[index];
                    int tmp;


                    if (alpha <= 255 && alpha >= 235) {
                        tmp = 255 - alpha;
                        r = 255 - tmp;
                        g = tmp * 12;
                    } else if (alpha <= 234 && alpha >= 200) {
                        tmp = 234 - alpha;
                        r = 255 - (tmp * 8);
                        g = 255;
                    } else if (alpha <= 199 && alpha >= 150) {
                        tmp = 199 - alpha;
                        g = 255;
                        b = tmp * 5;
                    } else if (alpha <= 149 && alpha >= 100) {
                        tmp = 149 - alpha;
                        g = 255 - (tmp * 5);
                        b = 255;
                    } else {
                        b = 255;
                    }


                    //raster.setSample(x, y, 3, 0.5);
                    raster.setSample(x, y, 0, r);
                    raster.setSample(x, y, 1, g);
                    raster.setSample(x, y, 2, b);


                }



            }
        }





        return img;
    }
    
    

    public BufferedImage createHeatMap(int imgWidth, int imgHeight) {

        if (parent.geoLocations.isEmpty()) {
            return null;
        }


        float boundary_left = (float) parent.twitterPointMax.getY();   //93.62f;
        float boundary_right = (float) parent.twitterPointMin.getY(); //93.12f;
        float boundary_top = (float) parent.twitterPointMax.getX();//42.35f;
        float boundary_bottom = (float) parent.twitterPointMin.getX();// 42.10f;

//        int imgWidth = 520;
//        int imgHeight = 264;

        BufferedImage img = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);

        List<Point2D> tempTwitterLocations = new ArrayList<Point2D>();
        for (int i = 0; i < selectedDocuments.size(); i++) {
            int index = selectedDocuments.get(i);
            tempTwitterLocations.add(parent.geoLocations.get(index));

        }

        WritableRaster raster = img.getRaster();
        float map[] = new float[imgWidth * imgHeight];
        for (int i = 0; i < imgWidth * imgHeight; i++) {
            map[i] = 0;
        }

        int heatmapRadius = 10;

        for (int i = 0; i < tempTwitterLocations.size(); i++) {
            double ty = tempTwitterLocations.get(i).getX();
            double tx = tempTwitterLocations.get(i).getY();
            double x = (double) (imgWidth - (tx - boundary_right) / Math.abs(boundary_right - boundary_left) * imgWidth);
            double y = (double) (imgHeight - ((ty - boundary_bottom)) / (boundary_top - boundary_bottom) * imgHeight);

//           System.out.println("ty = " + ty + " tx = " + tx);
//           System.out.println("y = " + y + " x = " + x);

            
                
                
                
            for (int ay = (int) (Math.ceil(y) - heatmapRadius); ay < Math.floor(y) + heatmapRadius; ay++) {
                for (int ax = (int) (Math.ceil(x) - heatmapRadius); ax < Math.floor(x) + heatmapRadius; ax++) {
                    if (ay >= 0 && ay < imgHeight && ax >= 0 && ax < imgWidth) {
                        
                        
                                                
                        double weight = calculate_gaussian_weight(x, y, ax, ay, 1.1);

                        int index = ay * imgWidth + ax;


                        map[index] += weight;
//			count[index]++;
                    }
                }
            }
        }

        float maxxx = -9;
        for (int i = 0; i < imgWidth * imgHeight; i++) {
            if (map[i] >= maxxx) {
                maxxx = map[i];
            }



        }


        System.out.println("local maxxx");


        for (int y = 0; y < imgHeight; y++) {
            for (int x = 0; x < imgWidth; x++) {
                int index = y * imgWidth + x;
                //System.out.println(map[index]);



                if (map[index] != 0) {

                    if (map[index] >= maxxx / 10) {
                        map[index] = 255;
                    } else {
                        map[index] = (float) (map[index] / (maxxx / 10) * 255);
                    }
                    int r = 0, g = 0, b = 0;
                    int alpha = (int) map[index];
                    int tmp;


                    if (alpha <= 255 && alpha >= 235) {
                        tmp = 255 - alpha;
                        r = 255 - tmp;
                        g = tmp * 12;
                    } else if (alpha <= 234 && alpha >= 200) {
                        tmp = 234 - alpha;
                        r = 255 - (tmp * 8);
                        g = 255;
                    } else if (alpha <= 199 && alpha >= 150) {
                        tmp = 199 - alpha;
                        g = 255;
                        b = tmp * 5;
                    } else if (alpha <= 149 && alpha >= 100) {
                        tmp = 149 - alpha;
                        g = 255 - (tmp * 5);
                        b = 255;
                    } else {
                        b = 255;
                    }


                    //raster.setSample(x, y, 3, 0.5);
                    raster.setSample(x, y, 0, r);
                    raster.setSample(x, y, 1, g);
                    raster.setSample(x, y, 2, b);





//                       raster.setSample(x, y, 0, 255*map[index]*40);
//                        raster.setSample(x, y, 1, 0);
//                        raster.setSample(x, y, 2, 0);
                }



            }
        }


//        
//         raster.setSample(x, y, 0, 255);
//                    raster.setSample(x, y, 1, 0);
//                    raster.setSample(x, y, 2, 0);

        return img;
    }
    
     double calculate_distance(double sx, double sy, int dx, int dy) {
        
        float dist = (float) ((sx - dx) * (sx - dx) + (sy - dy) * (sy - dy));

        dist = (float) Math.sqrt(dist);
        return dist;
    }
     

    double calculate_gaussian_weight(double sx, double sy, int dx, int dy, double sigma) {
        double result;
        float dist = (float) ((sx - dx) * (sx - dx) + (sy - dy) * (sy - dy));

        double temp = Math.sqrt(2 * Math.PI) * sigma;
        double exp_number = -dist / (2 * sigma * sigma);
        result = 1 / (Math.pow(temp, 3)) * Math.exp(exp_number);

        return (result);
    }

    private void selectDocumentsToPresent(double threshold) {
        // Computer How many documents should be shown on the screen.
        // This should be based on document percentage calculated in the follow function.
        // Threshold comes from the slider in the UI
        int docIdx = 0;
        //System.out.println("selectedTimeColumn " + selectedTimeColumn + " " + parentPanel.getData().idxOfDocumentPerSlot.get(selectedTimeColumn).size());
        int countTotalDoc = 0;
        for (int k = 0; k < parentPanel.getData().idxOfDocumentPerSlot.get(selectedTimeColumn).size(); k++) {

            for (int l = 0; l < curTreeNode.getTopicsContainedIdx().size(); l++) {
                docIdx = parentPanel.getData().idxOfDocumentPerSlot.get(selectedTimeColumn).get(k);
                int topicIndex = curTreeNode.getTopicsContainedIdx().get(l);
                if (parentPanel.getData().values_Norm.get(docIdx)[topicIndex] > threshold) {
                    selectedDocuments.add(docIdx);
                }
                countTotalDoc++;
            }
        }







    }
    
    
       private void selectDocumentsToPresentZoomed(double threshold, HashMap<Integer, List<Integer>> documentInThisPanel) {
        // Computer How many documents should be shown on the screen.
        // This should be based on document percentage calculated in the follow function.
        // Threshold comes from the slider in the UI
        int docIdx = 0;
        //System.out.println("selectedTimeColumn " + selectedTimeColumn + " " + parentPanel.getData().idxOfDocumentPerSlot.get(selectedTimeColumn).size());
        int countTotalDoc = 0;
        
        
        for (int k = 0; k < documentInThisPanel.get(selectedTimeColumn).size(); k++) {

            for (int l = 0; l < curTreeNode.getTopicsContainedIdx().size(); l++) {
                docIdx = documentInThisPanel.get(selectedTimeColumn).get(k);
                int topicIndex = curTreeNode.getTopicsContainedIdx().get(l);
                if (parentPanel.getData().values_Norm.get(docIdx)[topicIndex] > threshold) {
                    selectedDocuments.add(docIdx);
                }
                countTotalDoc++;
            }
        }







    }

    @Deprecated
    public DocumentViewer(List<Integer> l, final TemporalViewPanel p, Color c, final Point2D pt) {
        initComponents();
        this.getContentPane().setBackground(c);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        int[] tmp = new int[l.size()];
        for (int i = 0; i < l.size(); i++) {
            tmp[i] = l.get(i);
        }

        columnNames = p.parent.getInternalDocs().get(0);
        tmpDocs = p.parent.getInternalDocs();
        this.updateDocContent(tmp);


        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                p.removeDocumentViewerat(pt);
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        searchKeywordWithinDoc = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        acrossCorpusWordSearchText = new javax.swing.JTextField();
        docSelectionThreshold = new javax.swing.JSlider();
        jLabel2 = new javax.swing.JLabel();
        thresholdLabel = new javax.swing.JLabel();
        RTRatio = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Document Viewer");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jScrollPane2.setViewportView(jTextArea1);

        searchKeywordWithinDoc.setText("Highlight Keywords with in Selected Document");
        searchKeywordWithinDoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchKeywordWithinDocActionPerformed(evt);
            }
        });
        searchKeywordWithinDoc.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                searchKeywordWithinDocFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                searchKeywordWithinDocFocusLost(evt);
            }
        });
        searchKeywordWithinDoc.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchKeywordWithinDocKeyReleased(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel1.setText("0 Highlighted");

        acrossCorpusWordSearchText.setText("Filter Documents based on Keyword");
        acrossCorpusWordSearchText.setToolTipText("");
        acrossCorpusWordSearchText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                acrossCorpusWordSearchTextActionPerformed(evt);
            }
        });
        acrossCorpusWordSearchText.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                acrossCorpusWordSearchTextFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                acrossCorpusWordSearchTextFocusLost(evt);
            }
        });
        acrossCorpusWordSearchText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                acrossCorpusWordSearchTextKeyReleased(evt);
            }
        });

        docSelectionThreshold.setValue(25);
        docSelectionThreshold.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                docSelectionThresholdStateChanged(evt);
            }
        });

        jLabel2.setText("Select Docs above");

        thresholdLabel.setText("25%");

        jLabel3.setText("RT Ratio with Threshold:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 785, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(RTRatio, javax.swing.GroupLayout.PREFERRED_SIZE, 504, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(docSelectionThreshold, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(thresholdLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(44, 44, 44)
                                .addComponent(acrossCorpusWordSearchText, javax.swing.GroupLayout.PREFERRED_SIZE, 318, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(searchKeywordWithinDoc, javax.swing.GroupLayout.PREFERRED_SIZE, 520, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel1)
                        .addGap(128, 128, 128)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(docSelectionThreshold, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(thresholdLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(acrossCorpusWordSearchText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(RTRatio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 368, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchKeywordWithinDoc, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE))
                .addGap(0, 27, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void searchKeywordWithinDocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchKeywordWithinDocActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchKeywordWithinDocActionPerformed

    private void acrossCorpusWordSearchTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_acrossCorpusWordSearchTextActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_acrossCorpusWordSearchTextActionPerformed

    private void docSelectionThresholdStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_docSelectionThresholdStateChanged
        // TODO add your handling code here:

        if (!docSelectionThreshold.getValueIsAdjusting()) {

            double threshold = (double) docSelectionThreshold.getValue() / 100.0f;

            // Update the label
            this.thresholdLabel.setText(docSelectionThreshold.getValue() + "%");

            // Clear Existing Documents
            selectedDocuments.clear();

            if (parentPanel instanceof ZoomedTemporalViewPanel)
                selectDocumentsToPresentZoomed(threshold, ((ZoomedTemporalViewPanel)parentPanel).getDocumentInThisPanel());
            else
                 selectDocumentsToPresent(threshold);
            

            
        if (!parent.b_readFromDB)
            this.updateDocViewContent(selectedDocuments);
        else
            {
                try {
                    this.updateDocViewContent(selectedDocuments,parent.host, parent.port,parent.database, parent.collection, parent.nameFields);
                } catch (UnknownHostException ex) {
                    Logger.getLogger(DocumentViewer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        
                


        }

    }//GEN-LAST:event_docSelectionThresholdStateChanged

    private void searchKeywordWithinDocKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchKeywordWithinDocKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            System.out.println("Searching");
            wordSearch();
        }

    }//GEN-LAST:event_searchKeywordWithinDocKeyReleased

    private void acrossCorpusWordSearchTextKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_acrossCorpusWordSearchTextKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            System.out.println("Searching");
            acrossCorpusWordSearch();
        }
    }//GEN-LAST:event_acrossCorpusWordSearchTextKeyReleased

    private void acrossCorpusWordSearchTextFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_acrossCorpusWordSearchTextFocusGained
        acrossCorpusWordSearchText.setText("");
    }//GEN-LAST:event_acrossCorpusWordSearchTextFocusGained

    private void searchKeywordWithinDocFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchKeywordWithinDocFocusGained
        searchKeywordWithinDoc.setText("");
    }//GEN-LAST:event_searchKeywordWithinDocFocusGained

    private void acrossCorpusWordSearchTextFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_acrossCorpusWordSearchTextFocusLost
        acrossCorpusWordSearchText.setText("Filter Documents based on Keyword");
    }//GEN-LAST:event_acrossCorpusWordSearchTextFocusLost

    private void searchKeywordWithinDocFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchKeywordWithinDocFocusLost
        searchKeywordWithinDoc.setText("Highlight Keywords with in Selected Document");
    }//GEN-LAST:event_searchKeywordWithinDocFocusLost

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DocumentViewer().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField RTRatio;
    private javax.swing.JTextField acrossCorpusWordSearchText;
    private javax.swing.JSlider docSelectionThreshold;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField searchKeywordWithinDoc;
    private javax.swing.JLabel thresholdLabel;
    // End of variables declaration//GEN-END:variables
    private List<String[]> tmpDocs;
    private DataTable dt;
    private DefaultTableModel model;
    private int[] paraSelectedRecords;

    final void updateDocViewContent(List<Integer> selectedDocIndexes) {

        // from csvf all Docs with first line :.................
        int temprow = selectedDocIndexes.size();
        Object[][] content = new Object[temprow][columnNames.length];

        this.setVisible(true);
        for (int i = 0; i < selectedDocIndexes.size(); i++) {
            int tempDocIdx = selectedDocIndexes.get(i);
            content[i] = tmpDocs.get(tempDocIdx + 1);//both files have headers
        }

        if (dt == null) {
            model = new DefaultTableModel(content, columnNames);
            dt = new DataTable(content, columnNames);
            jTable1.setModel(model);
            listener = new SelectionListener(jTable1);
            jTable1.getSelectionModel().addListSelectionListener(listener);
        } else {
            dt.setEntireTable(content);
            model.setDataVector(content, columnNames);
            TableModelEvent event = new TableModelEvent(model);
            model.fireTableChanged(event);
        }


        int countRT = 0;
        for (int i = 0; i < content.length; i++) {
            String contentString = (String) content[i][parentPanel.parent.getContentIdx()];
            if (contentString.contains("RT ") || contentString.contains("rt ")) {
                countRT++;
                // System.out.println(contentString);
            }

        }

        float ratio = (float) countRT / selectedDocIndexes.size();


        RTRatio.setText(countRT + " in " + selectedDocIndexes.size() + " documents contain RT, ratio: " + Float.toString(ratio));



    }

    final void updateDocViewContent(List<Integer> selectedDocIndexes, String DBURL, int DBPORT, String DBName, String collectionName, String[] fields) throws UnknownHostException {


        int temprow = selectedDocIndexes.size();
        Object[][] content = new Object[temprow][fields.length];



        MongoClient mongoClient = new MongoClient(DBURL, DBPORT);
        DB db = mongoClient.getDB(DBName);
        Set<String> colls = db.getCollectionNames();

        DBCollection coll = db.getCollection(collectionName);
        System.out.println("database count : " + coll.getCount());

        List<Integer> abc = selectedDocIndexes;
        for (Object o : abc) {
            o = (Integer) o + 1;
        }                
        
        BasicDBObject query = new BasicDBObject("_id", new BasicDBObject("$in", abc));
        DBCursor cursor = coll.find(query);

        int count = 0;
        try {
            while (cursor.hasNext()) {

                BasicDBObject dbo = (BasicDBObject) cursor.next();

                String[] s = new String[fields.length];
                for (int i = 0; i < fields.length; i++) {
                    s[i] = (String) dbo.get(fields[i]);
                                      //  System.out.println(fields[i] + " " + s[i]);                  
                }
                content[count] = s;
                        
                count++;

            }
        } finally {
            cursor.close();
        }


        mongoClient.close();


        // from csvf all Docs with first line :.................


        this.setVisible(true);
//        for (int i = 0; i < selectedDocIndexes.size(); i++) {
//            int tempDocIdx = selectedDocIndexes.get(i);
//            content[i] = tmpDocs.get(tempDocIdx + 1);//both files have headers
//        }

        if (dt == null) {
            model = new DefaultTableModel(content, fields);
            dt = new DataTable(content, fields);
            jTable1.setModel(model);
            listener = new SelectionListener(jTable1);
            jTable1.getSelectionModel().addListSelectionListener(listener);
        } else {
            dt.setEntireTable(content);
            model.setDataVector(content, fields);
            TableModelEvent event = new TableModelEvent(model);
            model.fireTableChanged(event);
        }


        int countRT = 0;
        for (int i = 0; i < content.length; i++) {
            String contentString = (String) content[i][parentPanel.parent.getContentIdx()];
            if (contentString.contains("RT ") || contentString.contains("rt ")) {
                countRT++;
                // System.out.println(contentString);
            }

        }

        float ratio = (float) countRT / selectedDocIndexes.size();


        RTRatio.setText(countRT + " in " + selectedDocIndexes.size() + " documents contain RT, ratio: " + Float.toString(ratio));



    }

    @Deprecated
    final void updateDocContent(int[] selectedRecords) {


        Map<Integer, Integer> parIdx2docIdx = parent.getParIdx2docIdx();
        //int numRec = Integer.parseInt(selectedRecords);
        paraSelectedRecords = selectedRecords;
        int temprow = selectedRecords.length;
        int[] docIdx = new int[temprow];
        for (int i = 0; i < docIdx.length; i++) {

            try {
                // in csv file  parIdx2docIdx
                //parid starts from 0 and recorder id starts from 1 => parid = rid-1
                docIdx[i] = parIdx2docIdx.get(selectedRecords[i]);
            } catch (Exception e) {
                System.out.println("selected-par" + paraSelectedRecords[i] + "docID" + parIdx2docIdx.get(selectedRecords[i]));
            }


        }


        Object[][] content = new Object[temprow][columnNames.length];
        this.setVisible(true);
        for (int i = 0; i < docIdx.length; i++) {
            int tempDocIdx = docIdx[i];
            content[i] = tmpDocs.get(tempDocIdx);//both files have headers
        }

        if (dt == null) {
            model = new DefaultTableModel(content, columnNames);
            dt = new DataTable(content, columnNames);
            jTable1.setModel(model);
            listener = new SelectionListener(jTable1);
            jTable1.getSelectionModel().addListSelectionListener(listener);
        } else {
            dt.setEntireTable(content);
            model.setDataVector(content, columnNames);
            TableModelEvent event = new TableModelEvent(model);
            model.fireTableChanged(event);
        }
//        for (String str : tmpDocs.get(numRec)) {
//            System.out.println(str);
//        }
    }

    private void wordSearch() {
        try {
            jTextArea1.getHighlighter().removeAllHighlights();
            String doc = jTextArea1.getText();
            Pattern pattern = Pattern.compile(searchKeywordWithinDoc.getText());
            Matcher matcher = pattern.matcher(doc);

            int matchCount = 0;
            while (matcher.find()) {
                matchCount++;
                for (int i = 0; i < matcher.groupCount() + 1; i++) {
                    try {
                        jTextArea1.getHighlighter().addHighlight(matcher.start(i), matcher.end(i), DefaultHighlighter.DefaultPainter);
                    } catch (Exception ex) {
                        System.out.println(ex);
                    }
                }
            }
            jLabel1.setText(Integer.toString(matchCount) + " Highlighted");

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void acrossCorpusWordSearch() {
        //Pattern keyword = Pattern.compile(jTextField2.getText());
        Map<Integer, Integer> searchResults = new HashMap<Integer, Integer>();
        ValueComparator vc = new ValueComparator(searchResults);
        sortedResults = new TreeMap(vc);
        List<Integer> searched;
        String keyword = acrossCorpusWordSearchText.getText().toLowerCase().trim();
        keyword = " " + keyword;

        String currentDoc;
        int matchCount = 0;
        int lastIndex = 0;
        for (int i = 0; i < tmpDocs.size(); i++) {
            matchCount = 0;
            lastIndex = 0;

            //currentDoc  = tmpDocs.get(i)[2].toLowerCase();//Challenge 1
            currentDoc = tmpDocs.get(i)[1].toLowerCase();//Challenge 3

            matchCount = currentDoc.split(keyword).length - 1;

//            while (lastIndex != -1) {
//                lastIndex = currentDoc.indexOf(keyword, lastIndex);
//                if (lastIndex != -1) {
//                    matchCount++;
//                }
//            }
            if (matchCount > 0) {
                searchResults.put(i, matchCount);
            }
        }

//        for(int i=0; i<tmpDocs.size(); i++){
//            String[] tmpDoc = tmpDocs.get(i);
//            Matcher m = keyword.matcher(tmpDoc[3]);
//            int matchCount = 0;
//            while (m.find()) {
//                matchCount++;
//            }
//            if(matchCount>0){
//                searchResults.put(i, matchCount);
//            }
//        }

        if (searchResults.size() > 0) {
            searched = new ArrayList<Integer>();
            for (int key : searchResults.keySet()) {
                searched.add(key);
            }
//            sortedResults.putAll(searchResults);
//            searched = new ArrayList<Integer>();
//            for (int key : sortedResults.keySet()) {
//                searched.add(key);
//            }
            updateContent_domestic(searched);
        }
    }

    private void updateContent_domestic(List<Integer> searched) {
        paraSelectedRecords = null;
        paraSelectedRecords = new int[searched.size()];

        Object[][] content = new Object[searched.size()][columnNames.length];
        this.setVisible(true);
        List<Integer> pass2PC = new ArrayList<Integer>();
        for (int i = 0; i < searched.size(); i++) {
            content[i] = tmpDocs.get(searched.get(i));//both files have headers
            pass2PC.add(searched.get(i) - 1);
            paraSelectedRecords[i] = searched.get(i) - 1;
        }

        if (dt == null) {
            model = new DefaultTableModel(content, columnNames);
            dt = new DataTable(content, columnNames);
            jTable1.setModel(model);
            listener = new SelectionListener(jTable1);
            jTable1.getSelectionModel().addListSelectionListener(listener);
        } else {
            dt.setEntireTable(content);
            model.setDataVector(content, columnNames);
            TableModelEvent event = new TableModelEvent(model);
            model.fireTableChanged(event);
        }



    }

    private class SelectionListener implements ListSelectionListener {

        JTable tab;

        SelectionListener(JTable tab) {
            this.tab = tab;
        }

        public void valueChanged(ListSelectionEvent e) {
            if (e.getSource() == tab.getSelectionModel() && tab.getRowSelectionAllowed()) {
                int selectedRow = jTable1.getSelectedRow();
                if (selectedRow != -1) {
                    jTextArea1.setText(null);
                    for (int i = 0; i < jTable1.getColumnCount(); i++) {
                        String tmp = jTable1.getValueAt(selectedRow, i).toString();
                        jTextArea1.append((String) jTable1.getValueAt(selectedRow, i));
                        jTextArea1.append("\n");
                    }
                    System.out.println(selectedRow);

                    // parent.getScatterPlotChart().setViewingDoc(paraSelectedRecords[selectedRow]);
                }
                if (e.getValueIsAdjusting()) {
                    return;
                }
            }
        }
    }
    private String[] columnNames;

//    void loadDocs(String tmpURL) throws MalformedURLException, IOException {
//        tmpURL = tmpURL.replaceAll("_usage", "");
//        CSVReader csvReader = new CSVReader(tmpURL);
//        //tmpDocs = csvReader.readAll();//first line is the header //TODO
//        tmpDocs = null;
//
//        this.parent.setInternalDocs(tmpDocs);
//        columnNames = tmpDocs.get(0);
//
//        awardNum2docIdx = new HashMap<String, Integer>();
//        for (int i = 1; i < tmpDocs.size(); i++) {
//            String tmpAwardNum = tmpDocs.get(i)[0];
//            awardNum2docIdx.put(tmpAwardNum, i);
//        }
//    }

    void loadDocs(List<String[]> docs) {
        tmpDocs = docs;
        columnNames = tmpDocs.get(0);
    }
}
