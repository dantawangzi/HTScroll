/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DocumentViewer.java
 *
 * Created on Apr 28, 2010, 12:57:07 PM
 */
package com.TasteAnalytics.HierarchicalTopics.gui;

import au.com.bytecode.opencsv.CSVWriter;
import com.TasteAnalytics.HierarchicalTopics.datahandler.LDAHTTPClient;
import com.TasteAnalytics.HierarchicalTopics.file.CSVReader;
import com.TasteAnalytics.HierarchicalTopics.file.DataTable;
import com.TasteAnalytics.HierarchicalTopics.temporalView.renderer.TemporalViewPanel;
import com.TasteAnalytics.HierarchicalTopics.temporalView.renderer.TreeNode;
import com.TasteAnalytics.HierarchicalTopics.temporalView.renderer.ZoomedTemporalViewPanel;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mysql.jdbc.Connection;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.DefaultHighlighter;

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
    HashMap<Integer, String> selectedDocIDs;
    List<HashMap> selectedtweets = new ArrayList<HashMap>();
    SelectionListener listener;//for document selection
    public Map<String, Integer> awardNum2docIdx;//identified award number as common ground
    TreeMap<Integer, Integer> sortedResults;
    JPanel relatedDocsContentPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 10, 10));
    
    int _id_index = -1;
    int title_index = -1;
    int abstract_index = -1;
    int tokens_index = -1;
    
    
    HashSet<String> hideString = new HashSet<String>();
    
    
    public List<Integer> getSelectedDocuments() {
        return selectedDocuments;
    }

    /**
     * Creates new form DocumentViewer
     */
    public DocumentViewer() {
        
        ImageIcon logo_icon = new ImageIcon ( 
    		Toolkit.getDefaultToolkit().getImage(this.getClass().getClassLoader().
			getResource("resource/logo.png")));
            this.setIconImage(logo_icon.getImage());
            
            
           
        initComponents();
//        relatedDocsContentPanel.setPreferredSize(new Dimension(800, 800));
//        
//        jScrollPaneRelatedDocs.setBackground(Color.green);
//        
//         jScrollPaneRelatedDocs = new JScrollPane(relatedDocsContentPanel);
    }

    public DocumentViewer(ViewController viewController) {
        this();
        
        ImageIcon logo_icon = new ImageIcon ( 
    		Toolkit.getDefaultToolkit().getImage(this.getClass().getClassLoader().
			getResource("resource/logo.png")));
            this.setIconImage(logo_icon.getImage());
            
            
        this.parent = viewController;
    }

    /*DXW: Simplify the Initializer*/
    public DocumentViewer(final TemporalViewPanel p, final Point2D pt) throws IOException, UnknownHostException, ParseException {

        
        hideString.add("bb_box");
        hideString.add("pos");
        hideString.add("lat");
        hideString.add("geo_type");
        hideString.add("geo_class");
        hideString.add("loc");
        hideString.add("geo_name");
        hideString.add("sentiment");
        hideString.add("imp");
        hideString.add("neg");
        hideString.add("lon");
        hideString.add("cntry_code");
        hideString.add("e_list");
        hideString.add("_label");
        hideString.add("num_labels");
        hideString.add("cnty");
        hideString.add("_entities");
        hideString.add("_id");
        hideString.add("postcode");
        hideString.add("thresh");
        hideString.add("state");
        
        hideString.add("nom_id");
        hideString.add("osm_id");
        
        
        
        ImageIcon logo_icon = new ImageIcon ( 
    		Toolkit.getDefaultToolkit().getImage(this.getClass().getClassLoader().
			getResource("resource/logo.png")));
            this.setIconImage(logo_icon.getImage());
            
            relatedDocsContentPanel.setBackground(Color.white);
        initComponents();
        
//         relatedDocsContentPanel.setPreferredSize(new Dimension(800, 800));
//        jScrollPaneRelatedDocs = new JScrollPane(relatedDocsContentPanel);
        

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
        selectedDocIDs = new HashMap<Integer, String>();

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

        if (p instanceof ZoomedTemporalViewPanel) {
            System.out.println("omng");
            selectDocumentsToPresentZoomed((double) this.docSelectionThreshold.getValue() / 100.0f, ((ZoomedTemporalViewPanel) p).getDocumentInThisPanel());
        } else {
            selectDocumentsToPresent((double) this.docSelectionThreshold.getValue() / 100.0f);
        }

        //System.out.println("selectedDoc size " + selectedDocuments.size());
        // add elements to al, including duplicates
        Set<Integer> hs = new LinkedHashSet<Integer>();
        hs.addAll(selectedDocuments);
        // System.out.println("hs size " + hs.size());
        selectedDocuments.clear();

        selectedDocuments.addAll(hs);
        //System.out.println("selectedDoc size " + selectedDocuments.size());

        if (!parent.b_readFromDB) {
            columnNames = p.parent.getInternalDocs().get(0);
            tmpDocs = p.parent.getInternalDocs();
        }

        if (!p.parent.b_readFromDB) {
            this.updateDocViewContent(selectedDocuments);
        } else {
            this.updateDocViewContent(selectedDocuments, p.parent.host, p.parent.port, p.parent.database, p.parent.collection, p.parent.nameFields);
        }
        
//         DefaultTableModel dtm = 
//        (DefaultTableModel)jTable1.getModel();
        
        
        for (int i=0; i<model.getColumnCount(); i++)
        {
            String cname = model.getColumnName(i);
            if (hideString.contains(cname))
            {
                jTable1.removeColumn(jTable1.getColumn(cname));
            }
            
        }
        

////        geoHeatMapPanel.setLayout(null/*new CardLayout()*/);
////        geoHeatMapPanel.setPreferredSize(new Dimension(520, 264));
////        geoHeatMapPanel.setVisible(true);
//
//
//
////        JLabel Label = new JLabel();
////
////
////        Label.setBounds(0, 0, 520, 264);
////        // Label.setBackground(new Color(0, 0, 0, .5f));  
////        // Label.setOpaque(true);
////
////
////
////
////
//        
//        
//        
//        BufferedImage htimage = createHeatMap(1000, 500);
//        parent.getVCGF().setHeatmapImg(htimage);
//
//        
//        
//        
//        
//        //Label.setIcon(new ImageIcon( createHeatMap(520,264)));
//        //   geoHeatMapPanel.add(Label);
//
////           if (image != null) {
////            JLabel picLabel = new JLabel(new ImageIcon( createHeatMap(520,264)));
////
////           // picLabel.setOpaque(true);
////            picLabel.setBounds(0, 0, 520, 264);
////            picLabel.setBackground(new Color(0, 0, 0, .5f));
////           
////             geoHeatMapPanel.add(picLabel);
////            
////        }
//
//
//        //parent.getVCGF().setHeatmapImg(htimage);
//
//
//
//        //parent.updateGeoView(curTreeNode.getColor(), selectedDocuments);
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
    float LocalImageMap[];

    public float GenerateImgMap(int imgWidth, int imgHeight) {
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

                        if (calculate_distance(x, y, ax, ay) > (heatmapRadius - 3)) {
                            continue;
                        }

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

       // System.out.println("local maxxx");

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

    private void selectDocumentsToPresent(double threshold) throws IOException {
        // Computer How many documents should be shown on the screen.
        // This should be based on document percentage calculated in the follow function.
        // Threshold comes from the slider in the UI
        int docIdx = 0;
        //System.out.println("selectedTimeColumn " + selectedTimeColumn + " " + parentPanel.getData().idxOfDocumentPerSlot.get(selectedTimeColumn).size());
        int countTotalDoc = 0;
        if (parent.b_readFromDB) {

            LDAHTTPClient c = new LDAHTTPClient("https", parent.host, "2012");
            c.login(true,null,null);
//             MongoClient mongoClient = null;
//        try {
//                mongoClient = new MongoClient("10.18.203.211", 27017);
//            } catch (UnknownHostException ex) {
//                System.out.println("DB begin load cache error");
//            }
//
//            DB db = mongoClient.getDB("lda_results");
//            
//            DBCollection currentColl = db.getCollection(parent.collection);
//         DBCursor cursor = null;
//         DBObject dbo = null;

            for (int l = 0; l < curTreeNode.getTopicsContainedIdx().size(); l++) {
                int topicIndex = curTreeNode.getTopicsContainedIdx().get(l);
                for (Object r : (ArrayList) c.getTopicSlotDocs(
                        parent.collection, topicIndex, selectedTimeColumn, threshold, parent.database, parent.table, parent.nameFields, parent.id_type)) {
                   // System.out.println(r);

                    selectedtweets.add((HashMap) r);

                //selectedDocuments.add(docIdx);
                    //       selectedDocIDs.put(docIdx, docid);
                    countTotalDoc++;
                }

            }
            //c.close();

//            for (int k = 0; k < parentPanel.getData().idxOfDocumentPerSlot.get(selectedTimeColumn).size(); k++) {
//
//                for (int l = 0; l < curTreeNode.getTopicsContainedIdx().size(); l++) {
//                    docIdx = parentPanel.getData().idxOfDocumentPerSlot.get(selectedTimeColumn).get(k);
//                    int topicIndex = curTreeNode.getTopicsContainedIdx().get(l);
//
//
//
//                    BasicDBObject query = new BasicDBObject();
//
//                    query.put("type", "doc_to_topic");
//                    query.put("_id", docIdx);
//
//                    cursor = currentColl.find(query);
//                    dbo = (DBObject) cursor.next();
//                    String s = (String) dbo.get("weights");
//                    String[] tmp = s.split(",");
//                    String docid = (String) dbo.get("doc_id");
//                    if (Float.parseFloat(tmp[topicIndex]) > threshold) {
//                        selectedDocuments.add(docIdx);
//                        selectedDocIDs.put(docIdx, docid);
//                    }
//
//                    countTotalDoc++;
//                }
//            }
        } else {
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
        jPanel1.setBackground(c);
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
        searchKeywordWithinDoc = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        docSelectionThreshold = new javax.swing.JSlider();
        JExportButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        thresholdLabel = new javax.swing.JLabel();
        acrossCorpusWordSearchText = new javax.swing.JTextField();
        RTRatio = new javax.swing.JTextField();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPaneRelatedDocs = new javax.swing.JScrollPane(relatedDocsContentPanel);
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Document Viewer");
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

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

        searchKeywordWithinDoc.setText("Highlight Keywords with in Selected Document");
        searchKeywordWithinDoc.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                searchKeywordWithinDocFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                searchKeywordWithinDocFocusLost(evt);
            }
        });
        searchKeywordWithinDoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchKeywordWithinDocActionPerformed(evt);
            }
        });
        searchKeywordWithinDoc.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchKeywordWithinDocKeyReleased(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel1.setText("0 Highlighted");

        docSelectionThreshold.setValue(5);
        docSelectionThreshold.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                docSelectionThresholdStateChanged(evt);
            }
        });

        JExportButton.setText("Export Doc");
        JExportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JExportButtonActionPerformed(evt);
            }
        });

        jLabel3.setText("RT Ratio with Threshold:");

        jLabel2.setText("Select Docs above");

        thresholdLabel.setText("5%");

        acrossCorpusWordSearchText.setText("Filter Documents based on Keyword");
        acrossCorpusWordSearchText.setToolTipText("");
        acrossCorpusWordSearchText.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                acrossCorpusWordSearchTextFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                acrossCorpusWordSearchTextFocusLost(evt);
            }
        });
        acrossCorpusWordSearchText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                acrossCorpusWordSearchTextActionPerformed(evt);
            }
        });
        acrossCorpusWordSearchText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                acrossCorpusWordSearchTextKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(docSelectionThreshold, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(thresholdLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(44, 44, 44)
                        .addComponent(acrossCorpusWordSearchText, javax.swing.GroupLayout.PREFERRED_SIZE, 318, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(RTRatio, javax.swing.GroupLayout.PREFERRED_SIZE, 403, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(JExportButton, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(297, 297, 297))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(docSelectionThreshold, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(thresholdLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(acrossCorpusWordSearchText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(RTRatio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(JExportButton))
                .addContainerGap())
        );

        jSplitPane1.setDividerLocation(200);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jScrollPaneRelatedDocs.setMinimumSize(new java.awt.Dimension(100, 50));
        jSplitPane1.setRightComponent(jScrollPaneRelatedDocs);

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(30);
        jTextArea1.setMinimumSize(new java.awt.Dimension(100, 100));
        jScrollPane2.setViewportView(jTextArea1);

        jSplitPane1.setLeftComponent(jScrollPane2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSplitPane1)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(searchKeywordWithinDoc, javax.swing.GroupLayout.PREFERRED_SIZE, 520, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(64, 64, 64)
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(searchKeywordWithinDoc)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 448, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void searchKeywordWithinDocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchKeywordWithinDocActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchKeywordWithinDocActionPerformed

    private void searchKeywordWithinDocKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchKeywordWithinDocKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            System.out.println("Searching");
            wordSearch();
        }

    }//GEN-LAST:event_searchKeywordWithinDocKeyReleased

    private void searchKeywordWithinDocFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchKeywordWithinDocFocusGained
        searchKeywordWithinDoc.setText("");
    }//GEN-LAST:event_searchKeywordWithinDocFocusGained

    private void searchKeywordWithinDocFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchKeywordWithinDocFocusLost
        searchKeywordWithinDoc.setText("Highlight Keywords with in Selected Document");
    }//GEN-LAST:event_searchKeywordWithinDocFocusLost

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        // TODO add your handling code here:
        
        
        
        
        
    }//GEN-LAST:event_formComponentResized

    private void acrossCorpusWordSearchTextKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_acrossCorpusWordSearchTextKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            System.out.println("Searching");
            acrossCorpusWordSearch();
        }
    }//GEN-LAST:event_acrossCorpusWordSearchTextKeyReleased

    private void acrossCorpusWordSearchTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_acrossCorpusWordSearchTextActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_acrossCorpusWordSearchTextActionPerformed

    private void acrossCorpusWordSearchTextFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_acrossCorpusWordSearchTextFocusLost
        acrossCorpusWordSearchText.setText("Filter Documents based on Keyword");
    }//GEN-LAST:event_acrossCorpusWordSearchTextFocusLost

    private void acrossCorpusWordSearchTextFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_acrossCorpusWordSearchTextFocusGained
        acrossCorpusWordSearchText.setText("");
    }//GEN-LAST:event_acrossCorpusWordSearchTextFocusGained

    private void JExportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JExportButtonActionPerformed

        //export csv files to folder:
        int temprow = selectedDocuments.size();

        List<String[]> content = new ArrayList<String[]>();
        for (int i = 0; i < temprow; i++) {
            String[] tmpdata = new String[columnNames.length];

            int tempDocIdx = selectedDocuments.get(i);
            for (int j = 0; j < columnNames.length; j++) {
                tmpdata[j] = tmpDocs.get(tempDocIdx + 1)[j];
            }

            content.add(tmpdata);
        }

        au.com.bytecode.opencsv.CSVWriter csvW;
        try {
            //System.out.println(temprow);
            if (parent.csvfFolderPath==null)
            parent.csvfFolderPath = "";

            csvW = new au.com.bytecode.opencsv.CSVWriter(new FileWriter(parent.csvfFolderPath + "\\export.csv"));
            csvW.writeAll(content);
            csvW.close();

            JOptionPane.showMessageDialog(null, "Your document is here: " + parent.csvfFolderPath + "\\export.csv",
                "Export Documents Successful!", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException ex) {
            System.out.println("Export unsuccessful, check please!");
            Logger.getLogger(DocumentViewer.class.getName()).log(Level.SEVERE, null, ex);
        }

        // TODO add your handling code here:
    }//GEN-LAST:event_JExportButtonActionPerformed

    private void docSelectionThresholdStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_docSelectionThresholdStateChanged
        // TODO add your handling code here:

        if (!docSelectionThreshold.getValueIsAdjusting()) {

            double threshold = (double) docSelectionThreshold.getValue() / 100.0f;

            // Update the label
            this.thresholdLabel.setText(docSelectionThreshold.getValue() + "%");

            // Clear Existing Documents
            selectedDocuments.clear();
            selectedDocIDs.clear();
            selectedtweets.clear();

            if (parentPanel instanceof ZoomedTemporalViewPanel) {
                selectDocumentsToPresentZoomed(threshold, ((ZoomedTemporalViewPanel) parentPanel).getDocumentInThisPanel());
            } else {
                try {
                    selectDocumentsToPresent(threshold);
                } catch (IOException ex) {
                    Logger.getLogger(DocumentViewer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            if (!parent.b_readFromDB) {
                this.updateDocViewContent(selectedDocuments);
            } else {
                try {
                    this.updateDocViewContent(selectedDocuments, parent.host, parent.port, parent.database, parent.collection, parent.nameFields);

                    DefaultTableModel dtm =
                    (DefaultTableModel)jTable1.getModel();
                    for (int i=0; i<dtm.getColumnCount(); i++)
                    {
                        String cname = dtm.getColumnName(i);
                        if (hideString.contains(cname.toLowerCase()))
                        {
                            jTable1.removeColumn(jTable1.getColumn(cname));
                        }

                    }

                } catch (UnknownHostException ex) {
                    Logger.getLogger(DocumentViewer.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ParseException ex) {
                    Logger.getLogger(DocumentViewer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
    }//GEN-LAST:event_docSelectionThresholdStateChanged

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
    private javax.swing.JButton JExportButton;
    private javax.swing.JTextField RTRatio;
    private javax.swing.JTextField acrossCorpusWordSearchText;
    private javax.swing.JSlider docSelectionThreshold;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPaneRelatedDocs;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField searchKeywordWithinDoc;
    private javax.swing.JLabel thresholdLabel;
    // End of variables declaration//GEN-END:variables
    private List<String[]> tmpDocs;
    private DataTable dt;
    private DefaultTableModel model;
    private int[] paraSelectedRecords;

    static <K, V extends Comparable<? super V>>
            List<Entry<K, V>> entriesSortedByValues(Map<K, V> map) {

        List<Entry<K, V>> sortedEntries = new ArrayList<Entry<K, V>>(map.entrySet());

        Collections.sort(sortedEntries,
                new Comparator<Entry<K, V>>() {
                    @Override
                    public int compare(Entry<K, V> e1, Entry<K, V> e2) {
                        return e2.getValue().compareTo(e1.getValue());
                    }
                }
        );

        return sortedEntries;
    }

    final void updateDocViewContent(List<Integer> selectedDocIndexes) {

        // from csvf all Docs with first line :.................
        int temprow = selectedDocIndexes.size();
        Object[][] content = new Object[temprow][columnNames.length + 1];

        this.setVisible(true);

  //      System.out.println(tmpDocs.size() + " " + tmpDocs.get(0).length);
//        System.out.println(tmpDocs.get(0)[0] + tmpDocs.get(0)[1] + tmpDocs.get(0)[2] + tmpDocs.get(0)[3] + tmpDocs.get(0)[4]);
        if (false) {
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // for  all data sets  reddit only
            final HashMap<String, Color> randomColor = new HashMap<String, Color>();

            HashMap<String, List<Integer>> submissionMap = new HashMap<String, List<Integer>>();
            for (int i = 0; i < selectedDocIndexes.size(); i++) {
                int tempDocIdx = selectedDocIndexes.get(i);
                String submission = tmpDocs.get(tempDocIdx + 1)[8];

                if (submissionMap.containsKey(submission)) {
                    List<Integer> tmpi = submissionMap.get(submission);
                    tmpi.add(tempDocIdx);
                    submissionMap.put(submission, tmpi);
                } else {
                    List<Integer> tmpi = new ArrayList<Integer>();
                    tmpi.add(tempDocIdx);
                    submissionMap.put(submission, tmpi);

                }
            }

            String[] newColumnNames = {"weights", "_id", "name", "content", "time", "c_id", "scores", "isRoot", "submisson", "subreddit", 
             "summision_title", "submission_text", "subreddit_name",    "label", "pos", "neg"};
            content = new Object[temprow][newColumnNames.length ];

            //System.out.println(newColumnNames.length);
            int currentCount = 0;
            for (Map.Entry<String, List<Integer>> entry : submissionMap.entrySet()) {

                Color c;
                int r = (int) ((float) Math.random() * 255);
                int g = (int) ((float) Math.random() * 255);
                int b = (int) ((float) Math.random() * 255);
                c = new Color(r, g, b);

                String key = entry.getKey();
                List<Integer> docIdx = entry.getValue();

                randomColor.put(key, c);

                HashMap<Integer, Float> weightDoc = new HashMap<Integer, Float>();
                HashMap<Integer, Long> timeDoc = new HashMap<Integer, Long>();

                for (int i = 0; i < docIdx.size(); i++) {

                    int tempDocIdx = docIdx.get(i);

                    timeDoc.put(tempDocIdx, parentPanel.getData().getTime().get(tempDocIdx));
                }

                for (int i = 0; i < docIdx.size(); i++) {

                    float weight = 0.0f;
                    int tempDocIdx = docIdx.get(i);
                    for (int l = 0; l < curTreeNode.getTopicsContainedIdx().size(); l++) {

                        int topicIndex = curTreeNode.getTopicsContainedIdx().get(l);
                        weight += parentPanel.getData().values_Norm.get(tempDocIdx)[topicIndex];
                    }

                    weightDoc.put(tempDocIdx, weight);
                }

                List<Entry<Integer, Long>> sortedEntries = entriesSortedByValues(timeDoc);

                for (int i = 0; i < docIdx.size(); i++) {
                    int tempDocIdx = sortedEntries.get(i).getKey();

//                    for (int j=0; j<tmpDocs.get(tempDocIdx + 1).length; j++)
//                        System.out.print( tmpDocs.get(tempDocIdx + 1)[j] +  " ") ;
//                    
                    
                    content[i + currentCount][0] = String.valueOf(weightDoc.get(tempDocIdx));;
                    
                    
                    //content[i+currentCount][0] = String.valueOf( sortedEntries.get(i).getValue());
                   

                    
                    
                    for (int j = 0; j < (newColumnNames.length - 4); j++) {
                        content[i + currentCount][j + 1] = tmpDocs.get(tempDocIdx + 1)[j + 1];//both files have headers
                    }
                    
                     content[i + currentCount][13] = tmpDocs.get(tempDocIdx + 1)[39];
                    content[i + currentCount][14] = tmpDocs.get(tempDocIdx + 1)[40];
                    content[i + currentCount][15] = tmpDocs.get(tempDocIdx + 1)[41];

                }

                currentCount += docIdx.size();

            }
//        System.out.println(tmpDocs.size());
//         System.out.println(parentPanel.getData().values_Norm.size());

            newColumnNames[0] = "weights";

            if (dt == null) {
                model = new DefaultTableModel(content, newColumnNames);
                dt = new DataTable(content, newColumnNames);
                jTable1.setModel(model);
                listener = new SelectionListener(jTable1);
                jTable1.getSelectionModel().addListSelectionListener(listener);

//          final Stack lColor = new Stack();
//          lColor.push(Color.white);
//          lColor.push(Color.lightGray);
//           jTable1.setDefaultRenderer(Object.class, new DefaultTableCellRenderer()
//            {
//                
//                int flag = 0;
//                Color currentColor = Color.white;
//                Color replaceColor = Color.lightGray;
//                @Override
//                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
//                {
//                    final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//                   
//              
//                    
//                    if (row>0)
//                    {
//                            String v = (String) table.getValueAt(row, 8);
//                            String v1 = (String) table.getValueAt((row-1), 8);
//
//                            
//                            
//                         
//                           if (v1.equals(v))
//                           {
//                               flag = 0;
//                               // c.setBackground(currentColor);
//                           }
////                           
//
//                           else
//                           {
//                               flag = 1;
////                               Color tmp = new Color(currentColor.getRGB());
////                               currentColor = replaceColor;
////                               replaceColor = tmp;
//                               
//                              // c.setBackground(currentColor);
//                           }
//                       
//                    //c.setBackground(randomColor.get(v));
//                    }
//
//                    if (flag != 0)                    
//                    {
//                        if (currentColor.equals(Color.white))
//                            currentColor = Color.lightGray;
//                        else 
//                        if(currentColor.equals(Color.lightGray))
//                            currentColor = Color.white;
//                        
//                        c.setBackground(currentColor);
//                    }
//                    return c;
//                }
//            });
//            
            } else {
                dt.setEntireTable(content);
                model.setDataVector(content, newColumnNames);
                TableModelEvent event = new TableModelEvent(model);
                model.fireTableChanged(event);
            }

        } else {

        // for  all data sets
            ////////////////////// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
            HashMap<Integer, Float> weightDoc = new HashMap<Integer, Float>();
            for (int i = 0; i < selectedDocIndexes.size(); i++) {

                float weight = 0.0f;
                int tempDocIdx = selectedDocIndexes.get(i);
                for (int l = 0; l < curTreeNode.getTopicsContainedIdx().size(); l++) {

                    int topicIndex = curTreeNode.getTopicsContainedIdx().get(l);
                    weight += parentPanel.getData().values_Norm.get(tempDocIdx)[topicIndex];
                }

                weightDoc.put(tempDocIdx, weight);
            }

            List<Entry<Integer, Float>> sortedEntries = entriesSortedByValues(weightDoc);

            for (int i = 0; i < selectedDocIndexes.size(); i++) {
                int tempDocIdx = sortedEntries.get(i).getKey();

                content[i][0] = String.valueOf(sortedEntries.get(i).getValue());
                for (int j = 0; j < tmpDocs.get(tempDocIdx + 1).length; j++) {
                    content[i][j + 1] = tmpDocs.get(tempDocIdx + 1)[j];//both files have headers
                }

            }

            String[] newColumnNames = new String[columnNames.length + 1];
            newColumnNames[0] = "weights";
            for (int i = 1; i < newColumnNames.length; i++) {
                newColumnNames[i] = columnNames[i - 1];
            }

            if (dt == null) {
                model = new DefaultTableModel(content, newColumnNames);
                dt = new DataTable(content, newColumnNames);
                jTable1.setModel(model);
                listener = new SelectionListener(jTable1);
                jTable1.getSelectionModel().addListSelectionListener(listener);
            } else {
                dt.setEntireTable(content);
                model.setDataVector(content, newColumnNames);
                TableModelEvent event = new TableModelEvent(model);
                model.fireTableChanged(event);
            }

            int countRT = 0;
            for (int i = 0; i < content.length; i++) {
                String contentString = (String) content[i][parentPanel.parent.getContentIdx() + 1];
                //System.out.println(contentString.length());
                if (contentString.contains("RT ") || contentString.contains("rt ")) {
                    countRT++;
                    // System.out.println(contentString);
                }

            }

            float ratio = (float) countRT / selectedDocIndexes.size();

            RTRatio.setText(countRT + " in " + selectedDocIndexes.size() + " documents contain RT, ratio: " + Float.toString(ratio));
///////////////////////////////////////////////////////////////////////////////////////////////////////////////

        }
    }

    final void updateDocViewContent(List<Integer> selectedDocIndexes, String DBURL, int DBPORT, String DBName, String collectionName, String[] fields) throws UnknownHostException, ParseException {

        int temprow = selectedDocIndexes.size();

        int count = 0;
        int contentIdx = -1;
        Object[][] content;
        String[] columnFields;

        
        //for reddit  
        if (false)
        {
            HashMap<String, List<Integer>> submissionMap = new HashMap<String, List<Integer>>();
            for (int i = 0; i < selectedtweets.size(); i++) {
             
                String submission =(String) selectedtweets.get(i).get("submisson");

                if (submissionMap.containsKey(submission)) {
                    List<Integer> tmpi = submissionMap.get(submission);
                    tmpi.add(i);
                    submissionMap.put(submission, tmpi);
                } else {
                    List<Integer> tmpi = new ArrayList<Integer>();
                    tmpi.add(i);
                    submissionMap.put(submission, tmpi);

                }
            }
            
           
            
            String[] newNameFields = {"thresh", "name", "content", "time", "c_id", "scores", "is_root", "submisson", "subreddit", 
            "submission_title", "submission_text", "subreddit_name", "label", "pos", "neg"};
                       
            parent.nameFields = newNameFields;
            
            int keysize = newNameFields.length;

            int size = selectedtweets.size();
            content = new Object[size][keysize];

            columnFields = new String[keysize];;

            //contentIdx = 2;
        
            int currentCount = 0;
            for (Map.Entry<String, List<Integer>> entry : submissionMap.entrySet()) {
                String key = entry.getKey();
                List<Integer> docIdx = entry.getValue(); 
                HashMap<Integer, Float> weightDoc = new HashMap<Integer, Float>();
                HashMap<Integer, Long> timeDoc = new HashMap<Integer, Long>();
                
                for (int i = 0; i < docIdx.size(); i++) {

                    int tempDocIdx = docIdx.get(i);

                    SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                    
                    String s = (String) selectedtweets.get(tempDocIdx).get("time");
                    Date T = f.parse(s);
                    timeDoc.put(tempDocIdx, T.getTime());
                }
                
                List<Entry<Integer, Long>> sortedEntries = entriesSortedByValues(timeDoc);
         
                
                for (int i = 0; i < docIdx.size(); i++) {
                    int tempDocIdx = sortedEntries.get(i).getKey();

                        String[] s = new String[keysize];

                        for (int j = 0; j < newNameFields.length; j++) {
                            s[j] = String.valueOf(selectedtweets.get(tempDocIdx).get(newNameFields[j]));
                        }
                        content[i+currentCount] = s;
                    }
                    
              currentCount += docIdx.size();
                
               
                
            }
            
           
            
            
            for (int i = 0; i < keysize; i++) {
                columnFields[i] = newNameFields[i];
                if (parent.text_id.equals(newNameFields[i])) {
                    contentIdx = i;
                }
            }

//            for (int j = 0; j < size; j++) {
//
//                String[] s = new String[keysize];
//
//                for (int i = 0; i < newNameFields.length; i++) {
//                    s[i] = String.valueOf(selectedtweets.get(j).get(newNameFields[i]));
//                }
//                content[j] = s;
//            }
        }

        else // not for reddit
        {
        if (parent.nameFields == null || parent.nameFields.length == 0) {
            int keysize = selectedtweets.get(0).keySet().size();
            int size = selectedtweets.size();
            content = new Object[size][keysize];

            columnFields = new String[keysize];;

            int currentIdx = 0;
            //List<String> currentfields = new ArrayList<String>();

            for (Object entry : selectedtweets.get(0).entrySet()) {
                Map.Entry<Object, Object> entrykey = (Map.Entry<Object, Object>) entry;
                String key = String.valueOf(entrykey.getKey());
                //currentfields.add(key);
                columnFields[currentIdx] = key;
                if (parent.text_id.equals(key)) {
                    contentIdx = currentIdx;
                }
                 if ("title".equals(key)) {
                    title_index = currentIdx;
                }
                  if ("abstract".equals(key.toLowerCase())) {
                    abstract_index = currentIdx;
                }
                  
                  if (ViewController.ngram == 2)
                   if ("tokens_2".equals(key)) {
                    tokens_index = currentIdx;
                }
                   
                   if (ViewController.ngram == 1)
                      if ("tokens_1".equals(key)) {
                    tokens_index = currentIdx;
                }
                   
   
                    if ("_id".equals(key)) {
                    _id_index = currentIdx;
                }
                         
                
                currentIdx++;

            }
            
            
            

            for (int i = 0; i < selectedtweets.size(); i++) {
                String[] tmp = new String[columnFields.length];
                for (int j = 0; j < columnFields.length; j++) {

                    tmp[j] = String.valueOf(selectedtweets.get(i).get(columnFields[j]));

                }

                content[i] = tmp;

            }

        } else {

            String[] newNameFields = new String[parent.nameFields.length + 1];

            for (int i = 0; i < parent.nameFields.length; i++) {
                newNameFields[i + 1] = parent.nameFields[i];
            }

            newNameFields[0] = "thresh";

            int keysize = newNameFields.length;

            int size = selectedtweets.size();
            content = new Object[size][keysize];

            columnFields = new String[keysize];;

            //contentIdx = 2;
            for (int i = 0; i < keysize; i++) {
                columnFields[i] = newNameFields[i];
                if (parent.text_id.equals(newNameFields[i])) {
                    contentIdx = i;
                }
            }

            for (int j = 0; j < size; j++) {

                String[] s = new String[keysize];

                for (int i = 0; i < newNameFields.length; i++) {
                    s[i] = String.valueOf(selectedtweets.get(j).get(newNameFields[i]));

                }

                content[j] = s;

            }

        }
        }
        
        
        
        
        
        if (dt == null) {
            model = new DefaultTableModel(content, columnFields);
            dt = new DataTable(content, columnFields);
            jTable1.setModel(model);
            listener = new SelectionListener(jTable1);
            jTable1.getSelectionModel().addListSelectionListener(listener);
        } else {
            dt.setEntireTable(content);
            model.setDataVector(content, columnFields);
            TableModelEvent event = new TableModelEvent(model);
            model.fireTableChanged(event);
        }

        int countRT = 0;
        if (parent.b_readFromDB) {
//            for (int i = 0; i < content.length; i++) {
//                String contentString = (String) content[i][contentIdx];
//                //System.out.println(contentString.length());
////                if (contentString.contains("RT ") || contentString.contains("rt ")) {
////                    countRT++;
////                    // System.out.println(contentString);
////                }
//
//            }

        } else {

            for (int i = 0; i < content.length; i++) {
                String contentString = (String) content[i][parentPanel.parent.getContentIdx()];

               // System.out.println(contentString.length());
                if (contentString.contains("RT ") || contentString.contains("rt ")) {
                    countRT++;
                    // System.out.println(contentString);
                }

            }
        }
        
        
        

        if (parent.b_readFromDB) {
            float ratio = (float) countRT / (selectedtweets.size());
            RTRatio.setText(countRT + " in " + selectedtweets.size() + " documents contain RT, ratio: " + Float.toString(ratio));
        } else {
            float ratio = ((float) countRT / selectedDocIndexes.size());
            RTRatio.setText(countRT + " in " + selectedDocIndexes.size() + " documents contain RT, ratio: " + Float.toString(ratio));
        }

        
       
        
       
        
        
        this.setVisible(true);

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
            currentDoc = tmpDocs.get(i)[parent.getContentIdx()].toLowerCase();//Challenge 3

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
            
            if (e.getValueIsAdjusting())
            if (e.getSource() == tab.getSelectionModel() && tab.getRowSelectionAllowed()) {
                int selectedRow = jTable1.getSelectedRow();
                if (selectedRow != -1) {
                    jTextArea1.setText(null);
                    for (int i = 0; i < jTable1.getColumnCount(); i++) {
                        String tmp = jTable1.getValueAt(selectedRow, i).toString();
                        jTextArea1.append((String) jTable1.getValueAt(selectedRow, i));
                        jTextArea1.append("\n");
                    }
//                    System.out.println(selectedRow);
                    jTextArea1.setCaretPosition(0);
                    String[] tokens;
                    HashSet<String> t1 = new HashSet<String>()
                    ;
                    
                    relatedDocsContentPanel.removeAll();
                    
                    
                    if (tokens_index!=-1)
                    {
                        
                        if (ViewController.ngram == 2)
                        {
                            String tokens_2 =  jTable1.getModel().getValueAt(selectedRow, tokens_index).toString();
                            tokens_2 =  tokens_2.replaceAll("\\[", "");
                            tokens_2 =  tokens_2.replaceAll("\\]", "");
                            tokens_2 =  tokens_2.replaceAll("\"", "");
                            tokens = tokens_2.split(",");

                            for (String s: tokens)
                            {
                                t1.add(s);
                            }
                        }
                        else
                        {
                             String tokens_2 =  jTable1.getModel().getValueAt(selectedRow, tokens_index).toString();
                            tokens_2 =  tokens_2.replaceAll("\\[", "");
                            tokens_2 =  tokens_2.replaceAll("\\]", "");
                            tokens_2 =  tokens_2.replaceAll("\"", "");
                            tokens = tokens_2.split(",");

                            for (String s: tokens)
                            {
                                t1.add(s);
                            }
                            
                        }
                        
                    }
                    
                      String current_id = "";
                     if (_id_index!=-1)
                    {
                         current_id =  jTable1.getModel().getValueAt(selectedRow, _id_index).toString();
                    }
                     
//                     List<String> _ids = new ArrayList<String>();
//                     
//                
//                     
//                     //
//                     
//                     _ids.add("543c0befad1d0037b86434df");
//                     _ids.add("543c0befad1d0037b86434bb");
//                     _ids.add("543c0befad1d0037b86434b4");
//                     _ids.add("543c0befad1d0037b86434c6");
//                     
                     
                      LDAHTTPClient c = new LDAHTTPClient("https", parent.host, "2012");
                    try {
                        c.login(true,null,null);
                        
                        BasicDBList x = (BasicDBList)c.getRelatedTweetsEntity(parent.collection,curTreeNode.getIndex() ,current_id);
                                
                                
                                
//                        BasicDBList x = (BasicDBList)c.getTweetsEntity( parent.collection,_ids);
//                        
                         if(x!=null)
                         {
                             for (int i=0; i<x.size(); i++)
                             {
                                 BasicDBObject dbo = (BasicDBObject) x.get(i);
                                 
                                 String title = String.valueOf(dbo.get("Title"));
                                 String Abstract = String.valueOf(dbo.get("Abstract"));
                                 
                                 String tok = "";
                                 if (ViewController.ngram==2)
                                    tok = String.valueOf(dbo.get("tokens_2"));
                                 else
                                    tok = String.valueOf(dbo.get("tokens_1"));
                                 
                                 tok = tok.replaceAll("\"", "");
                                  tok =  tok.replaceAll("\\[", "");
                              tok =  tok.replaceAll("\\]", "");
                              tok =  tok.replaceAll("\"", "");
                                  HashSet<String> inter = new HashSet<String>(t1);
                                 HashSet<String> t2 = new HashSet<String>();
                                 
                                 String[] tokArray = tok.split(",");
                                 for (String s: tokArray)
                                    {
                                        t2.add(s);
                                    }
                                 String _id = String.valueOf(dbo.get("_id"));
                                
                    
                                 inter.retainAll(t2);
                                 
                                 JRelatedDocDisplayPanel jddp = new JRelatedDocDisplayPanel(t2,inter,title, Abstract, _id, parent.host, parent.collection, hideString);
                                 
                                 relatedDocsContentPanel.add(jddp);
                                 
                                 
                             }
                         }
                        
                        
                        relatedDocsContentPanel.revalidate();
                        relatedDocsContentPanel.repaint();
                        
                        
                        //TODO: make the call here 
                    } catch (IOException ex) {
                        Logger.getLogger(DocumentViewer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    
                    
                    
                }
//                if (e.getValueIsAdjusting()) {
//                    return;
//                }
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
