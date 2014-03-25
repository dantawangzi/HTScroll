


/*
 */
package com.TasteAnalytics.HierarchicalTopics.gui;

import com.TasteAnalytics.HierarchicalTopics.datahandler.LDAHTTPClient;
import com.TasteAnalytics.HierarchicalTopics.eventsview.EventViewFrame;
import com.TasteAnalytics.HierarchicalTopics.file.CSVFile;
import com.TasteAnalytics.HierarchicalTopics.temporalView.renderer.TemporalViewFrame;
import com.TasteAnalytics.HierarchicalTopics.topicRenderer.TopicGraphViewFrame;
import com.TasteAnalytics.HierarchicalTopics.topicRenderer.VastGeoFrame;
import com.TasteAnalytics.HierarchicalTopics.topicRenderer.WorldMapProcessingPanel;
import com.explodingpixels.macwidgets.HudWidgetFactory;
import com.explodingpixels.macwidgets.HudWindow;
import com.explodingpixels.macwidgets.MacButtonFactory;
import com.explodingpixels.macwidgets.MacUtils;
import com.explodingpixels.macwidgets.SourceList;
import com.explodingpixels.macwidgets.SourceListCategory;
import com.explodingpixels.macwidgets.SourceListDarkColorScheme;
import com.explodingpixels.macwidgets.SourceListItem;
import com.explodingpixels.macwidgets.SourceListModel;
import com.google.gson.Gson;



import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mysql.jdbc.Connection;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.io.*;
import java.math.BigDecimal;
import java.net.*;
import java.security.InvalidKeyException;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.JTable;
import javax.swing.border.Border;
import org.apache.commons.io.IOUtils;
import prefuse.data.Graph;
import prefuse.data.io.*;

import prefuse.data.io.AbstractGraphWriter;
import prefuse.data.io.GraphWriter;
import prefuse.data.io.TreeMLWriter;

/**
 *
 * @authors Derek Xiaoyu Wang, Wenwen Dou, Li Yu
 */
public class MinimalismMainFrame extends javax.swing.JFrame implements Runnable{

    //private ViewController viewcontroller;
    //Defined by wdou
    private com.TasteAnalytics.HierarchicalTopics.gui.ViewController viewController;

    DocumentViewer documentViewer = null;
    TemporalViewFrame temporalFrame = null;
    TopicGraphViewFrame topicFrame = null;
    VastGeoFrame vcGeoFrame = null;
    EventViewFrame eventViewFrame = null;

    ConsoleFrame consoleFrame = null;
    
    
     JSplitPane mainSplit;
    
    JSplitPane leftSplit, rightSplit;
        
    JScrollPane leftTopScrollPane, leftBottomScrollPane;
    JScrollPane rightTopScrollPane, rightBottomScrollPane;
    
    
    
    public MinimalismMainFrame() {
        
        
        
        //this.setSize(null);
        
        initComponents();
       
        
       // MacUtils.makeWindowLeopardStyle(this.getRootPane());

//        UnifiedToolBar toolBar =  UnifiedToolBar();
//        JButton button = new JButton("My Button");
//        button.putClientProperty("JButton.buttonType", "textured");
//
//// Create a new mac button based on the JButton.
//        AbstractButton macButton = MacButtonFactory.makeUnifiedToolBarButton(button);
//        // Add the button to the left side of the toolbar.
//        toolBar.addComponentToLeft(button);
//
//        // This is so that the window can be dragged from anywhere on the toolbar.
//        // This is optional, but will make your Java application feel more like an OSX app.
//        macMainMenu.installWindowDraggerOnWindow(MainFrame);
//
//        // Add the toolbar to the frame.
//        this.add(toolBar.getComponent(), BorderLayout.NORTH);
        
        
         JPanel testPanel = new JPanel();
        testPanel.setBackground(Color.red);
        testPanel.setPreferredSize(new Dimension(1500,1000));
        
        JPanel testPanel2 = new JPanel();
        testPanel2.setBackground(Color.black);
        testPanel2.setPreferredSize(new Dimension(1500,1000));
        
        JPanel testPanel3 = new JPanel();
        testPanel3.setBackground(Color.green);
        testPanel3.setPreferredSize(new Dimension(1500,1000));
        
        JPanel testPanel4 = new JPanel();
        testPanel4.setBackground(Color.yellow);
        testPanel4.setPreferredSize(new Dimension(1500,1000));
        
        
        Border orangeLine = BorderFactory.createLineBorder(Color.orange);
        mButtonPanel.setBorder(orangeLine);
        
            rightTopScrollPane = new JScrollPane(testPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, 
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        rightTopScrollPane.setViewportView(testPanel);
        
        rightBottomScrollPane = new JScrollPane(testPanel4, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);        
        rightBottomScrollPane.setViewportView(testPanel4);
        
           
        leftTopScrollPane = new JScrollPane(testPanel2, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, 
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        leftTopScrollPane.setViewportView(testPanel2);
        
        leftBottomScrollPane = new JScrollPane(testPanel3, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);        
        leftBottomScrollPane.setViewportView(testPanel3);
        
        
        
        leftSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                           leftTopScrollPane, leftBottomScrollPane);
        leftSplit.setOneTouchExpandable(true);
        leftSplit.setDividerLocation(150);
        
        
        rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                           rightTopScrollPane, rightBottomScrollPane);
        rightSplit.setOneTouchExpandable(true);
        rightSplit.setDividerLocation(150);
        
        
        
        mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                           leftSplit, rightSplit);
        mainSplit.setOneTouchExpandable(true);
        mainSplit.setDividerLocation(150);
       mainSplit.setContinuousLayout(true);
        
        
        //Provide minimum sizes for the two components in the split pane
        Dimension minimumSize = new Dimension(100, 50);
        leftTopScrollPane.setMinimumSize(minimumSize);
        leftBottomScrollPane.setMinimumSize(minimumSize);
        
        rightTopScrollPane.setMinimumSize(minimumSize);
        rightBottomScrollPane.setMinimumSize(minimumSize);
        
        Border blackline = BorderFactory.createLineBorder(Color.black);
        mViewPanel.setBorder(blackline);
        mViewPanel.add(mainSplit);
        
        
        Thread thread = new Thread(this);  
        thread.start();  
    
        System.out.println("This is currently running on the main thread, " +  
        "the id is: " + Thread.currentThread().getId());  
        
        

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        menuEditGroup = new javax.swing.ButtonGroup();
        buttonEditGroup = new javax.swing.ButtonGroup();
        mButtonPanel = new javax.swing.JPanel();
        jButtonLoadData = new javax.swing.JButton();
        jConnectMongoButton = new javax.swing.JButton();
        mViewPanel = new javax.swing.JPanel();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        openMenu = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jCheckBoxTemporalFrame = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxTopicGraph = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxGeoFrame = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxLabelTopicFrame = new javax.swing.JCheckBoxMenuItem();
        helpMenu = new javax.swing.JMenu();
        helpItem = new javax.swing.JMenuItem();
        jCheckBoxConsoleMenu = new javax.swing.JCheckBoxMenuItem();
        aboutItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("HirarchicalTopics");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        jButtonLoadData.setFont(new java.awt.Font("Myriad Pro", 0, 18)); // NOI18N
        jButtonLoadData.setText("Load Data");
        jButtonLoadData.setActionCommand("Load Data...");
        jButtonLoadData.setMargin(new java.awt.Insets(0, 5, 0, 0));
        jButtonLoadData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openItemActionPerformed(evt);
            }
        });
        mButtonPanel.add(jButtonLoadData);

        jConnectMongoButton.setFont(new java.awt.Font("Myriad Pro", 0, 18)); // NOI18N
        jConnectMongoButton.setText("Connect Mongo");
        jConnectMongoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jConnectMongoButtonActionPerformed(evt);
            }
        });
        mButtonPanel.add(jConnectMongoButton);

        getContentPane().add(mButtonPanel, java.awt.BorderLayout.PAGE_START);

        mViewPanel.setLayout(new java.awt.BorderLayout());
        getContentPane().add(mViewPanel, java.awt.BorderLayout.CENTER);

        menuBar.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N

        fileMenu.setText("File");

        openMenu.setText("Open File...");
        openMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openItemActionPerformed(evt);
            }
        });
        fileMenu.add(openMenu);

        menuBar.add(fileMenu);

        jMenu1.setText("Panels");

        jCheckBoxTemporalFrame.setSelected(true);
        jCheckBoxTemporalFrame.setText("Themeriver Frame");
        jCheckBoxTemporalFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxTemporalFrameActionPerformed(evt);
            }
        });
        jMenu1.add(jCheckBoxTemporalFrame);

        jCheckBoxTopicGraph.setSelected(true);
        jCheckBoxTopicGraph.setText("HierarchicalTopics Frame");
        jCheckBoxTopicGraph.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxTopicGraphActionPerformed(evt);
            }
        });
        jMenu1.add(jCheckBoxTopicGraph);

        jCheckBoxGeoFrame.setSelected(true);
        jCheckBoxGeoFrame.setText("GeoSpatial Frame");
        jCheckBoxGeoFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxGeoFrameActionPerformed(evt);
            }
        });
        jMenu1.add(jCheckBoxGeoFrame);

        jCheckBoxLabelTopicFrame.setSelected(true);
        jCheckBoxLabelTopicFrame.setText("Label/Topic Graph Frame");
        jCheckBoxLabelTopicFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxLabelTopicFrameActionPerformed(evt);
            }
        });
        jMenu1.add(jCheckBoxLabelTopicFrame);

        menuBar.add(jMenu1);

        helpMenu.setText("Help");

        helpItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        helpItem.setText("Help");
        helpMenu.add(helpItem);

        jCheckBoxConsoleMenu.setSelected(true);
        jCheckBoxConsoleMenu.setText("Console");
        jCheckBoxConsoleMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxConsoleMenuActionPerformed(evt);
            }
        });
        helpMenu.add(jCheckBoxConsoleMenu);

        aboutItem.setText("About...");
        helpMenu.add(aboutItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    File currentPath = null;
    static public Map<Integer, Integer> parIdx2docIdx;

    private void openItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openItemActionPerformed

//               System.out.println("MySQL Connect Example.");
//        Connection conn = null;
//        String url = "jdbc:mysql://152.15.99.7/";
//        String dbName = "lda_results";
//        String driver = "com.mysql.jdbc.Driver";
//        String userName = "lee";
//        String password = "uncc_lee";
//        try {
//            Class.forName(driver).newInstance();
//            conn = (Connection) DriverManager.getConnection(url + dbName, userName, password);
//            System.out.println("Connected to the database");
//            
//            
//            String selectSQL = "select description, id from patents limit 1000";
//            
//            
//            
//            PreparedStatement preparedStatement = conn.prepareStatement(selectSQL);
//            //preparedStatement.setInt(1, 1001);
//            ResultSet rs = preparedStatement.executeQuery(selectSQL);
//
//
//            while (rs.next()) {
//
//                String userid = rs.getString("description");
//                String usernamea = rs.getString("id");
//
//                System.out.println("longitude : " + userid);
//                System.out.println("latitude : " + usernamea);
//
//            }
//
//
//            conn.close();
//            System.out.println("Disconnected from database");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        
        
        
        
        
       

        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return (f.isDirectory() || f.getName().endsWith(".csv"));
            }

            public String getDescription() {
                return "CSV Files";
            }
        });

        if (currentPath == null) {
            chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        } else {
            chooser.setCurrentDirectory(currentPath);
        }

        int option = chooser.showOpenDialog(this);

        if (option == JFileChooser.APPROVE_OPTION) {
            if (chooser.getSelectedFile() != null) {
                currentPath = chooser.getSelectedFile().getParentFile();
                String tmpURL = chooser.getSelectedFile().getAbsolutePath();

                String urltext = "file:///" + chooser.getSelectedFile().getAbsolutePath();
                urltext = urltext.replace('\\', '/');

                try {

                    if (tmpURL.endsWith("csv")) {

                        int idx = tmpURL.lastIndexOf("\\");
                        if (idx == -1) {
                            idx = tmpURL.lastIndexOf("/");
                        }
                        String folderPath = tmpURL.substring(0, idx + 1);

                        String headerPath = folderPath + "header.txt";

                        viewController.readHeaderFile(headerPath);

                        CSVFile csvf = new CSVFile(tmpURL);
                        
                        
                        if (viewController.b_readAll)
                        {

                            csvf.readContents(viewController.b_readAll, viewController.b_readFromDB,
                                    viewController.host, viewController.port, viewController.database, viewController.collection2, viewController.nameField2
                            );

                            viewController.setUsageRecord(csvf.getInternalRecord());

                            viewController.setInternalDocs(csvf.getInternalDocs());

                            viewController.setTopicSimilarities(csvf.getTopicSimilarities());

                            //viewController.getTopicDisplay().loadTopic(csvf.getAllTopics());

                            viewController.getDocumentViewer().loadDocs(csvf.getInternalDocs());

                            viewController.setFormat(csvf.getFormat());

                            viewController.setContentIdx(csvf.getContentIdx());

                            
                            
                            if (!viewController.b_readAll) {
                                viewController.setContentIdx(0);
                            }

                            viewController.setGlobalReadIndex(1);
                        }
                        else
                        {
                            csvf.readContents(viewController.b_readAll);
                             //viewController.getTopicDisplay().loadTopic(csvf.getAllTopics());
                             //SimpleDateFormat f = new SimpleDateFormat("YYYY-hh-dd");
                              viewController.setFormat(csvf.getFormat());
                            
                        }
                        setTitle("HirarchicalTopics" + csvf.getName());
                        viewController.setNewHueColors();

                        Toolkit toolkit = Toolkit.getDefaultToolkit();

                        Dimension scrnsize = toolkit.getScreenSize();

                        String csvfilepath = csvf.getFolderPath();
                        viewController.csvfFolderPath = csvfilepath;
                        temporalFrame = new TemporalViewFrame(viewController, scrnsize.width / 2, scrnsize.height);
                        viewController.addTemporalFrame(temporalFrame);

                        temporalFrame.loadData(csvf.getFolderPath(), csvf.getInternalRecord(), csvf.getYears(),
                                csvf.getInternalDocs(), csvf.getTermWeights(), csvf.getTermWeights_norm(), csvf.getTermIndex(), csvf.getAllTopics(),
                                csvfilepath, csvf.getContentIdx(), csvf.getFormat(), viewController.intervalDays, viewController.b_readAll, viewController.b_recaluateValue, viewController.zoomSubBins
                        ,csvf.content);

                        temporalFrame.setVisible(true);
                        temporalFrame.setSize(scrnsize.width / 2, scrnsize.height);
                        temporalFrame.setLocation(0, 0);

                        topicFrame = new TopicGraphViewFrame(viewController, csvf.getTermIndex(), csvf.getTermWeights(), null);
                        viewController.addTopicGraphViewPanel(topicFrame);
                        viewController.getTopicGraphViewPanel().loadTopic(csvf.getAllTopics());
                        System.out.println("topic frame load topics done.");

                        viewController.getTopicGraphViewPanel().buildTree(csvf.getFolderPath());

                        System.out.println("topic frame build tree done..");

                        topicFrame.setSize(scrnsize.width / 2, scrnsize.height);
                        topicFrame.setLocation(scrnsize.width / 2, 0);

                        viewController.getTopicGraphViewPanel().generateLayout();
                        topicFrame.setVisible(true);

                        System.out.println("Topics Graph done!");
        
        
                        initializeViews(csvf);
                        /**
                         * Initialize temporal view*
                         */

                    }
                } catch (Exception e) {
                    System.out.println(e.toString() + e.getMessage());
                }

            }
        }
    }//GEN-LAST:event_openItemActionPerformed

    /**
     * Exit the Application
     */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        System.exit(0);
    }//GEN-LAST:event_exitForm

    private void jCheckBoxTemporalFrameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxTemporalFrameActionPerformed

        boolean currentState = jCheckBoxTemporalFrame.getState();
        temporalFrame.setVisible(currentState);        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBoxTemporalFrameActionPerformed

    private void jCheckBoxTopicGraphActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxTopicGraphActionPerformed
        // TODO add your handling code here:
        boolean currentState = jCheckBoxTopicGraph.getState();
        topicFrame.setVisible(currentState);
    }//GEN-LAST:event_jCheckBoxTopicGraphActionPerformed

    private void jCheckBoxGeoFrameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxGeoFrameActionPerformed
        // TODO add your handling code here:
        boolean currentState = jCheckBoxGeoFrame.getState();
        vcGeoFrame.setVisible(currentState);
    }//GEN-LAST:event_jCheckBoxGeoFrameActionPerformed

    private void jCheckBoxLabelTopicFrameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxLabelTopicFrameActionPerformed
        // TODO add your handling code here:
        boolean currentState = jCheckBoxLabelTopicFrame.getState();
        eventViewFrame.setVisible(currentState);
    }//GEN-LAST:event_jCheckBoxLabelTopicFrameActionPerformed

    private void jConnectMongoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jConnectMongoButtonActionPerformed

        viewController.host = "caprica.uncc.edu";//10.18.202.126"; //"54.209.61.133";
        viewController.b_readFromDB = true;
        viewController.setGlobalReadIndex(0);
        
        JOptionPane p = new JOptionPane();

        String[] columnName = {"Data"};
        List<String> jobNames = new ArrayList<String>();
        
        
        //"54.209.61.133"
        LDAHTTPClient connection  = new LDAHTTPClient("http", viewController.host, String.valueOf(viewController.port));
        try {
            connection.login();
        } catch (IOException ex) {
            Logger.getLogger(MinimalismMainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            for (Object r : (ArrayList) connection.getJobs())
            {
                //String s = (String)((HashMap)r).get("field");
                 jobNames.add((String) ((HashMap)r).get("_id"));             ;
                // System.out.println(((HashMap)r).get("_id"));
            }
            
        } catch (IOException ex) {
            Logger.getLogger(MinimalismMainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        

        Object[][] data = new Object[jobNames.size()][];
        for (int i = 0; i < jobNames.size(); i++) {
            Object[] tmp = new Object[1];
            tmp[0] = (Object) jobNames.get(i);
            data[i] = tmp;

        }
        JTable table = new JTable(data, columnName);

        
//        HudWindow hud = new HudWindow("Select Dataset");
//        hud.getJDialog().setSize(400, 650);
//        hud.getJDialog().setLocationRelativeTo(null);
//        hud.getJDialog().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        hud.getJDialog().setVisible(true);
//        hud.hideCloseButton();
//        
       
       
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        
        
//        JButton button = HudWidgetFactory.createHudButton("Button");
//        hud.getContentPane().setLayout(new FlowLayout());
//        hud.getContentPane().add(button);
//        
////        JPanel jp = new JPanel();
////        jp.add(scrollPane);
////        jp.setPreferredSize(new Dimension(300,400));
//        hud.getContentPane().add(scrollPane);
//
//        
//        
//        SourceListModel model = new SourceListModel();
//        SourceListCategory category = new SourceListCategory("Category");
//        model.addCategory(category);
//        model.addItemToCategory(new SourceListItem("Item"), category);
//        SourceList sourceList = new SourceList(model);     
//        
//        
//        sourceList.setColorScheme(new SourceListDarkColorScheme());
//        
//        //hud.getContentPane().add(sourceList.getComponent());
        
        
        JOptionPane.showMessageDialog(null, scrollPane,
                "select", JOptionPane.YES_NO_CANCEL_OPTION);

        String job = jobNames.get(table.getSelectedRow());
        viewController.collection = job;

        

        CSVFile csvf = new CSVFile("");
        try {
            
            
       // viewController.setUsageRecord(csvf.getInternalRecord());

        //viewController.setInternalDocs(csvf.getInternalDocs());

       // viewController.setTopicSimilarities(csvf.getTopicSimilarities());

        List<String[]> topics  = new ArrayList<String[]>() ;
        
        
         HashMap<String, String[]> topicsByMongo = new HashMap<String, String[]>();
        
        for (Object r : (ArrayList) connection.getJobDocs(job,"topic"))
        {
            
            
            String Key =(String)   ((HashMap)r).get("_id");
            String terms = (String) ((HashMap)r).get("terms");
                                    
            String[] tmps = terms.split(",");
            String[] tmpdest = new String[tmps.length+2];
            tmpdest[0] = "Group";
            tmpdest[1] = Key;
            for (int i=0; i<tmps.length; i++)
            {
                tmpdest[2+i] = tmps[i];
                
            }
            topicsByMongo.put(Key, tmpdest); 
        }
         for (int i=0; i<topicsByMongo.size(); i++)
        {
            String key = "t" + Integer.toString(i);
            topics.add(topicsByMongo.get(key));
        }

       System.out.append("topk loaded");

        
            
           for (Object r : (ArrayList) connection.getJob(job))
           {
               HashMap hr = (HashMap) r;
               List<String> ls = ((List)hr.get("field"));
               if (ls != null)
               {
                   viewController.nameFields = new String[ls.size()];
                   for (int i=0; i<ls.size(); i++)
                        viewController.nameFields[i] = ls.get(i);
               }
               else
                   viewController.nameFields = null;
                   
               
//               String field = String.valueOf(hr.get("field"));
//               field = field.replaceAll("\\[","");
//               field = field.replaceAll("\\]","");
//                       field = field.replaceAll("\"","");
//                        field = field.replaceAll(" ","");
//               viewController.nameFields = field.split(",");
               viewController.text_id = ((String)(((HashMap)(hr.get("mongo_input"))).get("text_index")));
               viewController.database = ((String)(((HashMap)(hr.get("mongo_input"))).get("db")));
               viewController.table = ((String)(((HashMap)(hr.get("mongo_input"))).get("table")));
               viewController.id_type = ((String)(((HashMap)(hr.get("mongo_input"))).get("_id_type")));
           }
            
     
        
        
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
       viewController.setFormat(format);

        String TreeString = "";
        
        
        for (Object r : (ArrayList) connection.getJobDocs(job, "flat"))
        {
            //System.out.println(r);
            TreeString = (String) ((HashMap) r).get("tree");
        }
        
        
        
        List<HashMap> maplocations = new ArrayList<HashMap>();
        
        for (Object r : (ArrayList) connection.getGroupbyDocs("name", viewController.database,viewController.table,"latitude","longitude"))
        {
            maplocations.add((HashMap) r);
        }
        
        
                        
//        q1 = new BasicDBObject("type", "flat");
//        cursorfind = currentColl.find(q1);
//        BasicDBObject dbo1 = (BasicDBObject) cursorfind.next();
//        TreeString = dbo1.getString("tree");
        
        viewController.setNewHueColors();

        Toolkit toolkit = Toolkit.getDefaultToolkit();

        Dimension scrnsize = toolkit.getScreenSize();

        String csvfilepath = csvf.getFolderPath();
        viewController.csvfFolderPath = csvfilepath;
        
            temporalFrame = new TemporalViewFrame(viewController, scrnsize.width / 2, scrnsize.height);

            viewController.addTemporalFrame(temporalFrame);

            temporalFrame.loadCacheData(job, TreeString, viewController.host);
            temporalFrame.createWorldMap(maplocations); 
         
            temporalFrame.setVisible(true);
            temporalFrame.setSize(scrnsize.width / 2, scrnsize.height);
            temporalFrame.setLocation(0, 0);

            
            HashMap<String, Float> termWeightMongo = new HashMap<String, Float>();
            List<List<Float>> topkTermWeightMongo = new ArrayList<List<Float>>();
            
             HashMap<String, String> topicSimMongo = new HashMap<String, String>();
            List<List<Float>> topicSim = new ArrayList<List<Float>>();
            
            
            
            if (viewController.b_readFromDB)
            {
                
                for (Object r : (ArrayList) connection.getJobDocs(job, "topic_terms"))
                {
                    HashMap hr = (HashMap) r;
                    
             
                    String key = (String) hr.get("_id");
                    double weights = (Double) hr.get("weight");
                    
                  // float tmpvalue = Float.parseFloat(weights);
                    termWeightMongo.put(key, (float)weights);  
                    
                }
                
                for (int i=0; i<topicsByMongo.size(); i++)
                {
                    List<Float> tmpL = new ArrayList<Float>();
                    for (int j=0; j<50; j++) // hard code
                    {                                
                        String key = "dist_top" + (new Integer(i)).toString() + "term" + (new Integer(j)).toString();
                        tmpL.add(termWeightMongo.get(key));   
                    }
                    topkTermWeightMongo.add(tmpL);

                }
                
                
                for (Object r : (ArrayList) connection.getJobDocs(job, "top_sim"))
                {
                    HashMap hr = (HashMap) r;
                    
             
                    String key = (String) hr.get("_id");
                    String weights = (String) hr.get("weights");
                    
                  // float tmpvalue = Float.parseFloat(weights);
                    topicSimMongo.put(key, weights);  
                    
                }
                
                for (int i=0; i<topicSimMongo.size(); i++)
                {
                    List<Float> tmpL = new ArrayList<Float>();                                                                        
                        
                    String key = "topsim" + (new Integer(i)).toString();
                    
                    String weights = topicSimMongo.get(key);
                    String[]tmps = weights.split(",");
                    for (int j=0;j<tmps.length; j++)                        
                        tmpL.add(Float.parseFloat(tmps[j]));   
                    
                    topicSim.add(tmpL);

                }
                
                csvf.setSimilarityMatrix(topicSim);
//                
                
//                dbo = new BasicDBObject("type", "topic_terms");
//                cursorfind = currentColl.find(dbo);
//               
//                while (cursorfind.hasNext())
//                {
//                    BasicDBObject tmpDBO = (BasicDBObject) cursorfind.next();
//                    String key = tmpDBO.getString("_id");
//                    String weights = tmpDBO.getString("weight");
//                    
//                    float tmpvalue = Float.parseFloat(weights);
//                    
//                    termWeightMongo.put(key, tmpvalue);                       
//                }               
//                
//                for (int i=0; i<topicsByMongo.size(); i++)
//                {
//                    List<Float> tmpL = new ArrayList<Float>();
//                    for (int j=0; j<50; j++) // hard code
//                    {                                
//                        String key = "dist_top" + (new Integer(i)).toString() + "term" + (new Integer(j)).toString();
//                        tmpL.add(termWeightMongo.get(key));   
//                    }
//                    topkTermWeightMongo.add(tmpL);
//
//                }
                
                
            }
            
            
            
            
        topicFrame = new TopicGraphViewFrame(viewController, csvf.getTermIndex(), csvf.getTermWeights(), topkTermWeightMongo);
        viewController.addTopicGraphViewPanel(topicFrame);
        viewController.getTopicGraphViewPanel().loadTopic(topics);
        System.out.println("topic frame load topics done.");

        viewController.getTopicGraphViewPanel().buildTreeWithTreeString(TreeString);

        System.out.println("topic frame build tree done..");

        topicFrame.setSize(scrnsize.width / 2, scrnsize.height);
        topicFrame.setLocation(scrnsize.width / 2, 0);

        viewController.getTopicGraphViewPanel().generateLayout();
        topicFrame.setVisible(true);

        System.out.println("Topics Graph done!");
        
            initializeViews(csvf);
            
            
        } catch (IOException ex) {
            Logger.getLogger(MinimalismMainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            connection.close();
            
// TODO add your handling code here:
        } catch (IOException ex) {
            Logger.getLogger(MinimalismMainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jConnectMongoButtonActionPerformed

    private void jCheckBoxConsoleMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxConsoleMenuActionPerformed
    
        boolean currentState = jCheckBoxConsoleMenu.getState();
        consoleFrame.setVisible(currentState);

// TODO add your handling code here:
    }//GEN-LAST:event_jCheckBoxConsoleMenuActionPerformed

    void initializeViews(CSVFile csvf) throws IOException {

        Toolkit toolkit = Toolkit.getDefaultToolkit();

        Dimension scrnsize = toolkit.getScreenSize();

        String csvfilepath = csvf.getFolderPath();

       

        

        temporalFrame.getMainPanel().buildLabelTimeMap();
        //temporalFrame.getSubPanel().buildLabelTimeMap();

        viewController.setLeafNodeSequence(topicFrame.getLeafSequence());

        int s = viewController.getTopicGraphViewPanel().getTree().size();

        FileInputStream inputStream = new FileInputStream(csvfilepath + "newTree_Node" + s + ".txt");

        String treeString = IOUtils.toString(inputStream);

//
//                        MongoClient mongoClient = new MongoClient("152.15.99.7", 27017);
//                        DB db = mongoClient.getDB("patents");
//                        Set<String> colls = db.getCollectionNames();
//
//                        for (String s1 : colls) {
//                            System.out.println(s1);
//                        }
//
//                        DBCollection coll = db.getCollection("patent");
//                        System.out.println("database count : " + coll.getCount());
////                        
//                      
////                       DBCursor cursor = coll.find()
////                                BasicDBObject query = new BasicDBObject("_id", 71);
//                        
//                        List<Integer> abc = new ArrayList<Integer>();
//                        
//                        abc.add(1);
//                        
//                        abc.add(13);
//                        
//                        abc.add(1124);
//                        
//                        BasicDBObject query = new BasicDBObject("_id", new BasicDBObject("$in",abc));
//                                DBCursor cursor = coll.find(query);
//                      
//                                BasicDBObject dbo = (BasicDBObject)cursor.next();
//                                 System.out.println( dbo.get("_id"));
//                                 System.out.println(dbo.get("doc_id:"));
//                                 System.out.println( dbo.get("description"));
//                                //BasicDBList l = (BasicDBList)((BasicDBObject) cursor.next()).get("_id");
//                                //System.out.println(l);
////                       
//                                
//                                             
//                                                               
//                                                               
//                                
//                        
//                        
//                        try {
//                            while (cursor.hasNext()) {
//                                BasicDBList l = (BasicDBList)((BasicDBObject) cursor.next()).get("geo_extracted");
//                                //System.out.println(l);
//                                if (l == null)
//                                    continue;
//                                
//                                if (((String)(l.get(0))).length() == 0)
//                                    continue;
//                               
//                                if ( ((String)(l.get(1))).length() == 0)
//                                    continue;
//                                
//                                
//                                if (((String)(l.get(0))) == null)
//                                    continue;
//                               
//                                if ( ((String)(l.get(1))) == null)
//                                    continue;
//                                
//                                
//                                Point2D tempP = new Point2D.Double(Double.parseDouble((String)l.get(0)), Double.parseDouble((String)l.get(1)));
//                                geoLocations.add(tempP);
//                               // System.out.println((String)(l.get(0)) + "  " + (String)(l.get(1)));
//                               // System.out.println(tempP);
//                                
//                                if (geoLocations.size()>=10)
//                                    break;
//                              
//                            }
//                        } finally {
//                            cursor.close();
//                        }
//                        
        //System.out.println(geoLocations.size());
        //MongoClient.close();
        //DBCursor cursor = coll.find();
//                        try {
//                           while(cursor.hasNext()) {
//                               System.out.println(cursor.next());
//                           }
//                        } finally {
//                           cursor.close();
//                        }
//                        
//                        BasicDBObject allQuery = new BasicDBObject();
//                        BasicDBObject fields = new BasicDBObject();
//                        fields.put("geo_extracted", 1);
//        
//        
//                        
//                        DBCursor cursor = coll.find(allQuery, fields);
//
//                       
//
//
////                        System.out.println("MySQL Connect Example.");
////                        Connection conn = null;
////                        String url = "jdbc:mysql://152.15.106.31/";
////                        String dbName = "streamingApr15";
////                        String driver = "com.mysql.jdbc.Driver";
////                        String userName = "data";
////                        String password = "uncc_ldav";
////    
////    
////    
////    try {
////      Class.forName(driver).newInstance();
////      conn = (Connection) DriverManager.getConnection(url+dbName,userName,password);
////       System.out.println("Connected to the database");
////      String selectSQL = "select longitude, latitude from location_entity limit 100";
////        PreparedStatement preparedStatement = conn.prepareStatement(selectSQL);
////        //preparedStatement.setInt(1, 1001);
////        ResultSet rs = preparedStatement.executeQuery(selectSQL );
////
////
////      while (rs.next()) {
//// 
////				String userid = rs.getString("longitude");
////				String username = rs.getString("latitude");
//// 
////				System.out.println("longitude : " + userid);
////				System.out.println("latitude : " + username);
//// 
////			}
////      
////     
////      conn.close();
////      System.out.println("Disconnected from database");
////    } catch (Exception e) {
////      e.printStackTrace();
////    }
        Graph pgh = null;//(viewController.makePrefuseGraph(treeString));
//

        System.out.println("get geo locations done");
        viewController.geoLocations = csvf.getTwitterGeoLocations();

        
        
//        WorldMapProcessingPanel worldMapFrame = new WorldMapProcessingPanel(viewController,csvf.getTwitterGeoLocations(), csvf.getMediumLocation());
//        viewController.addWorldMapProcessingFrame(worldMapFrame);
//        worldMapFrame.setSize(1000, 1000);
//        worldMapFrame.setVisible(true);
////
//  
        
        
//        viewController.twitterPointMax = csvf.getMaxLocation();
 //       viewController.twitterPointMin = csvf.getMinLocation();

//        vcGeoFrame = new VastGeoFrame(viewController, csvf.getFolderPath(), csvf.getTwitterGeoLocations());
//
//        viewController.setVCGF(vcGeoFrame);
//
//        if (viewController.geoLocations!=null)
//            vcGeoFrame.setVisible(true);
//        else
//            vcGeoFrame.setVisible(false);
////
//////
//        System.out.println("GeoFrame done");

        eventViewFrame = new EventViewFrame(viewController, temporalFrame.getTree(), temporalFrame.getData(), viewController.getLeafNodeSequence(),
                viewController.getTopicGraphViewPanel().getGh(), pgh, treeString, csvf.getFolderPath(), csvf.getSimilarityMatrix());
//                       
        eventViewFrame.setVisible(true);

        System.out.println("label graph done");

        
        consoleFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                jCheckBoxConsoleMenu.setState(false);
            }
        });
        
        
//        vcGeoFrame.addWindowListener(new java.awt.event.WindowAdapter() {
//            @Override
//            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
//                jCheckBoxGeoFrame.setState(false);
//            }
//        });

        eventViewFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                jCheckBoxLabelTopicFrame.setState(false);
            }
        });

        topicFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                jCheckBoxTopicGraph.setState(false);
            }
        });

        temporalFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                jCheckBoxTemporalFrame.setState(false);
            }
        });

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        UIManager.put("com.TasteAnalytics.HierarchicalTopics.temporalView", "com.TasteAnalytics.HierarchicalTopics.temporalView");
        new MinimalismMainFrame().show();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutItem;
    private javax.swing.ButtonGroup buttonEditGroup;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuItem helpItem;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JButton jButtonLoadData;
    private javax.swing.JCheckBoxMenuItem jCheckBoxConsoleMenu;
    private javax.swing.JCheckBoxMenuItem jCheckBoxGeoFrame;
    private javax.swing.JCheckBoxMenuItem jCheckBoxLabelTopicFrame;
    private javax.swing.JCheckBoxMenuItem jCheckBoxTemporalFrame;
    private javax.swing.JCheckBoxMenuItem jCheckBoxTopicGraph;
    private javax.swing.JButton jConnectMongoButton;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JPanel mButtonPanel;
    private javax.swing.JPanel mViewPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.ButtonGroup menuEditGroup;
    private javax.swing.JMenuItem openMenu;
    // End of variables declaration//GEN-END:variables

    public void  run() {
       viewController = new ViewController();
        
        jCheckBoxConsoleMenu.setState(false);
        consoleFrame = new ConsoleFrame();
        consoleFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //// TODO: DXW---Need to comment this out!April 03, 2013
//        Topics topicDisplay = new Topics(viewController);
//
//        viewController.addTopicDisplay(topicDisplay);

        documentViewer = new DocumentViewer(viewController);
        documentViewer.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        viewController.addDocumentViewer(documentViewer);

        // Get the default toolkit
        Toolkit toolkit = Toolkit.getDefaultToolkit();

        // Get the current screen size
        Dimension scrnsize = toolkit.getScreenSize();

        // Set Initial Location
        this.setLocation(scrnsize.width / 2, scrnsize.height / 3);

        // Set Inital Size
        this.setSize(300, 200);
        
  //      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
