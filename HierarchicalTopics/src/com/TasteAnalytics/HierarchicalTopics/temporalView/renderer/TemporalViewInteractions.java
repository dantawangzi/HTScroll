/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TasteAnalytics.HierarchicalTopics.temporalView.renderer;

import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.picking.PickedState;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import com.TasteAnalytics.HierarchicalTopics.temporalView.renderer.TemporalViewPanel;
import java.awt.Dimension;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenu;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.AbstractButton;
import javax.swing.JMenuItem;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import com.TasteAnalytics.HierarchicalTopics.gui.DocumentViewer;
import com.TasteAnalytics.HierarchicalTopics.datahandler.CategoryBarElement;

/**
 * Mouse Behavior in ThemeRiver Interaction: Single Click will locate the time
 * range and highlight it. With a new HUDWindow shown. Double Click will zoom in
 * to certain time span.
 *
 * The best strategy would be to design the interface reaction so that it
 * doesn't matter if you get multiple events. The standard behavior of
 * interfaces such as file browsers, open file dialogs, list, etc. are all built
 * that way. The general metaphor is that a single click selects an object or
 * item and a double click does something with the selected item.
 *
 * In this way, you can process the two clicks independently of each other. IIRC
 * the Apple Human Interface Guidelines suggest this sort of model for
 * interfaces.
 *
 * @author xwang
 */
public class TemporalViewInteractions implements MouseListener, MouseMotionListener {

    private TemporalViewPanel attachedPanel;
    private CategoryStream focusedStream;
    private TimeColumn focusedColumn;
    private boolean isClicked;
    private int zoomlevel;
    private List<ZoomLevel> zoomManager;
    private int[] currentKw = null;
    private myPopup popUp;
    private boolean timecolumn;
    private boolean showingSingle;
    static int MAXTHIRDCOLUMNSIZE = 9;
    int globalMouseX;
    int globalMouseY;

    /**
     * Keep only one the mainFloatingHUDWindow This HUDWindow is used to show
     * the Icons and Images.
     */
    // private static HudWindow mainFloatingHUDWindow = new HudWindow();
    public TemporalViewInteractions(TemporalViewPanel panel) {
        attachedPanel = panel;
        isClicked = false;

        zoomlevel = 0;
        zoomManager = new ArrayList<ZoomLevel>();

        popUp = new myPopup();
    }

    @Override
    public void mouseClicked(MouseEvent me) {

        int mouseX = me.getX();
        int mouseY = me.getY();

        if (SwingUtilities.isRightMouseButton(me)) {

            mouseX = me.getX();
            mouseY = me.getY();

            popUp.setParentFrame(attachedPanel.parent.getTemporalFrame());

            // System.out.println("clccc" + popUp + " x " + me.getX() + " y " + me.getY());
            popUp.show(me.getComponent(), me.getX(), me.getY());

            popUp.setCurrentPanelString(me.getComponent().getName());

            if (popUp.heatImgList.size() != 0) {
                attachedPanel.parent.getVCGF().setHeatmapImg(popUp.heatImgList.get(5));

                attachedPanel.parent.getVCGF().invalidate();
            }

        }

        if (!timecolumn) {

            if (SwingUtilities.isLeftMouseButton(me)) {
                TreeNode selectedNode;
                clearPreviousFocuses();
                TemporalViewPanel targetPanel = attachedPanel;
                selectedNode = attachedPanel.currentNode;

                //click on main frame
                //adding second column of themerivers
                if ("Main".equals(attachedPanel.getName())) {
                    TemporalViewFrame tf = attachedPanel.parent.getTemporalFrame();
                    int selected_node_index = -1;
                    TemporalViewPanel p = tf.getMainPanel();
                    if (attachedPanel.getName().equals(p.getName())) {

                        for (int j = 0; j < attachedPanel.getCurrentAreas().size(); j++) {
                            CategoryStream categoryStream = attachedPanel.getCurrentAreas().get(j);
                            if (categoryStream.getRenderRegion().contains(mouseX, mouseY)) {

                                selected_node_index = j;
                                break;
                            }
                        }
                    }

                    if (selected_node_index != -1) {
                        TemporalViewPanel tp;
                        try {
                            
                            if (!tf.getTemporalPanelMap().containsKey(1))
                            {
                                List<TemporalViewPanel> tvpl = new ArrayList<TemporalViewPanel>();
                                tf.getTemporalPanelMap().put(1, tvpl);
                                
                            }

                            tp = new TemporalViewPanel(attachedPanel.parent);
                            int index = tf.getTemporalPanelMap().get(1).size();
                            tp.setName("attachedPanel.getLevel()+1" + " " + index);
                            tp.setPanelLabelId(index);
                            //tp.setPreferredSize(new Dimension(600, 300));
                            tp.setData(tf.getData());
                            tp.setTree(tf.getTree());
                            attachedPanel.addChildPanel(tp);
                            tp.setLevel(1);

                            if (attachedPanel.currentNode.getChildren().isEmpty()) {
                                tp.currentNode = attachedPanel.currentNode;
                            } else {
                                tp.currentNode = (TreeNode) attachedPanel.currentNode.getChildren().get(selected_node_index);
                            }

                            Integer it = new Integer(selected_node_index);
                            attachedPanel.getDrawLabels().add(it);

                            Point2D pf = new Point2D.Float((float) me.getPoint().getX() / attachedPanel.getWidth(),
                                    (float) me.getPoint().getY() / attachedPanel.getHeight());

                            attachedPanel.getDrawLabelsLocation().add(pf);
                            tp.setFatherPanel(attachedPanel);
                            tp.calculateLocalNormalizingValue(tp.getData(), tp.currentNode);
                            tp.buildLabelTimeMap();

                            tf.getTemporalPanelMap().get(1).add(tp);
                            float normalizeValue = -1;
                            if (tf.getTemporalPanelMap().get(1).size() > 0) {
                                for (TemporalViewPanel ttp : tf.getTemporalPanelMap().get(1)) {
                                    if (ttp.getLocalNormalizingValue() >= normalizeValue) {
                                        normalizeValue = ttp.getLocalNormalizingValue();
                                    }
                                }
                                for (TemporalViewPanel ttp : tf.getTemporalPanelMap().get(1)) {
                                    ttp.setGlobalNormalizingValue(normalizeValue);
                                }
                            }

                            for (TemporalViewPanel ttp : tf.getTemporalPanelMap().get(1)) {
                                ttp.calculateRenderControlPointsOfEachHierarchy(tp.getData(), tp.currentNode, tp.getGlobalNormalizingValue());
                                ttp.computerZeroslopeAreasHierarchy(0);
                                //ttp.detectEvents();

                                //         ttp.UpdateTemporalView(new Dimension(tf.getContentPane().getWidth() / (1 + secondColumnExist + thirdColumnExist), tf.getContentPane().getHeight() / 3), ttp.getGlobalNormalizingValue());
                            }

//                            tf.getMainPanel().UpdateTemporalView(new Dimension(tf.getContentPane().getWidth() / (1 + secondColumnExist + thirdColumnExist), tf.getContentPane().getHeight() * 2 / 3), tf.getMainPanel().getLocalNormalizingValue());
//                            tf.getSubPanel().UpdateTemporalView(new Dimension(tf.getContentPane().getWidth() / (1 + secondColumnExist + thirdColumnExist), tf.getContentPane().getHeight() / 3), tf.getSubPanel().getLocalNormalizingValue());
                            System.out.println("second column panel added");

                            tf.setMigLayoutForScrollPane();

                        } catch (IOException ex) {
                            Logger.getLogger(TemporalViewInteractions.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }

                }

                if (!"Main".equals(attachedPanel.getName()) && !"Sub".equals(attachedPanel.getName())) {

                    TemporalViewFrame tf = attachedPanel.parent.getTemporalFrame();


                    int selected_node_index = -1;

                    for (int j = 0; j < attachedPanel.getCurrentAreas().size(); j++) {
                        CategoryStream categoryStream = attachedPanel.getCurrentAreas().get(j);
                        if (categoryStream.getRenderRegion().contains(mouseX, mouseY)) {
                            selected_node_index = j;

                            break;
                        }
                    }

                    if (selected_node_index > -1) {
                        TemporalViewPanel tp;
                        try {

                            tp = new TemporalViewPanel(attachedPanel.parent);
                            int index = attachedPanel.getchildPanel().size();
                            tp.setName((attachedPanel.getLevel()+1) + " " + index);

                            //tp.setPreferredSize(new Dimension(600, 300));

                            tp.setData(tf.getData());

                            tp.setTree(tf.getTree());

                            if (!attachedPanel.currentNode.getChildren().isEmpty()) {
                                {
                                    tp.currentNode = (TreeNode) attachedPanel.currentNode.getChildren().get(selected_node_index);
                                }
                            } else {
                                {
                                    tp.currentNode = attachedPanel.currentNode;
                                }
                            }

                            Integer it = new Integer(selected_node_index);
                            attachedPanel.getDrawLabels().add(it);

                            Point2D pf = new Point2D.Float((float) me.getPoint().getX() / attachedPanel.getWidth(),
                                    (float) me.getPoint().getY() / attachedPanel.getHeight());

                            attachedPanel.getDrawLabelsLocation().add(pf);
                            tp.setFatherPanel(attachedPanel);
                            tp.calculateLocalNormalizingValue(tp.getData(), tp.currentNode);
                            tp.buildLabelTimeMap();
                            tp.setLevel(attachedPanel.getLevel() + 1);
                            
                            if (!tf.getTemporalPanelMap().containsKey(attachedPanel.getLevel()+1))
                            {
                                List<TemporalViewPanel> tvpl = new ArrayList<TemporalViewPanel>();
                                tf.getTemporalPanelMap().put(attachedPanel.getLevel()+1, tvpl);
                                
                            }
                            
                            
                            tf.getTemporalPanelMap().get((attachedPanel.getLevel()+1)).add(tp);
                            
                            attachedPanel.addChildPanel(tp);

                            float normalizeValue = -1;//attachedPanel.getchildPanel()
                            if (tf.getTemporalPanelMap().get(attachedPanel.getLevel()).size() > 0) {
                                for (TemporalViewPanel ttp : tf.getTemporalPanelMap().get(attachedPanel.getLevel())) {
                                    if (ttp.getLocalNormalizingValue() >= normalizeValue) {
                                        normalizeValue = ttp.getLocalNormalizingValue();
                                    }
                                }
                                for (TemporalViewPanel ttp : tf.getTemporalPanelMap().get(attachedPanel.getLevel())) {
                                    ttp.setGlobalNormalizingValue(normalizeValue);
                                }
                            }

                            for (TemporalViewPanel ttp : tf.getTemporalPanelMap().get(attachedPanel.getLevel())) {
                                ttp.calculateRenderControlPointsOfEachHierarchy(ttp.getData(), ttp.currentNode, ttp.getGlobalNormalizingValue());
                                ttp.computerZeroslopeAreasHierarchy(0);
                                ttp.detectEvents(ttp.getEventThreshold());
                                //ttp.UpdateTemporalView(new Dimension(tf.getContentPane().getWidth() / (1 + secondColumnExist + thirdColumnExist), tf.getContentPane().getHeight() / 9), ttp.getGlobalNormalizingValue());
                            }

                            tp.setPanelLabelId(index);

                            System.out.println((attachedPanel.getLevel()+1) + " column panel added, size " + tf.getTemporalPanelMap().get(attachedPanel.getLevel()+1).size());

                            tf.setMigLayoutForScrollPane();

//                                    tf.getMainPanel().UpdateTemporalView(new Dimension(tf.getContentPane().getWidth() / (1 + secondColumnExist + thirdColumnExist), tf.getContentPane().getHeight() * 2 / 3), tf.getMainPanel().getLocalNormalizingValue());
//                                    tf.getSubPanel().UpdateTemporalView(new Dimension(tf.getContentPane().getWidth() / (1 + secondColumnExist + thirdColumnExist), tf.getContentPane().getHeight() / 3), tf.getSubPanel().getLocalNormalizingValue());
//
//                                    for (TemporalViewPanel xd : tf.getSecondColumn()) {
//                                        xd.UpdateTemporalView(new Dimension(tf.getContentPane().getWidth() / (1 + secondColumnExist + thirdColumnExist), tf.getContentPane().getHeight() / 3), xd.getGlobalNormalizingValue());
//                                    }
                        } catch (IOException ex) {
                            Logger.getLogger(TemporalViewInteractions.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }

                }

                if (me.getClickCount() == 2) {
                    targetPanel = attachedPanel.parent.getTemporalFrame().getSubPanel();
                    TreeNode tempN = targetPanel.currentNode.getParent();
                    if (tempN != null) {
                        targetPanel.currentNode = tempN;
                    } else {
                        targetPanel.currentNode = targetPanel.getTree().get(0);
                    }

                    selectedNode = targetPanel.currentNode;

//                    targetPanel.calculateLocalNormalizingValue(attachedPanel.getData(), targetPanel.currentNode);
//
//                    targetPanel.calculateRenderControlPointsOfEachHierarchy(attachedPanel.getData(), targetPanel.currentNode, targetPanel.getLocalNormalizingValue());
//                    targetPanel.computerZeroslopeAreasHierarchy(0);
//                     targetPanel.detectEvents(targetPanel.getEventThreshold());
//                    targetPanel.UpdateTemporalView(new Dimension(targetPanel.getWidth(), targetPanel.getHeight()), targetPanel.getLocalNormalizingValue());
                } else {

                    targetPanel = attachedPanel.parent.getTemporalFrame().getSubPanel();
                    if (attachedPanel.getName() == null ? targetPanel.getName() == null : attachedPanel.getName().equals(targetPanel.getName())) {
                        for (int j = 0; j < attachedPanel.getCurrentAreas().size(); j++) {

                            CategoryStream categoryStream = attachedPanel.getCurrentAreas().get(j);

                            //System.out.println("mouse x and y " + mouseX + " " + mouseY);
                            if (categoryStream.getRenderRegion().contains(mouseX, mouseY)) {

                                //System.out.println(attachedPanel.getName());
                                //targetPanel = attachedPanel.parent.getTemporalFrame().getSubPanel(); 
                                if (!attachedPanel.currentNode.getChildren().isEmpty()) {
                                    {
                                        targetPanel.currentNode = (TreeNode) attachedPanel.currentNode.getChildren().get(j);
                                    }
                                } else {
                                    {
                                        targetPanel.currentNode = (TreeNode) attachedPanel.currentNode;
                                    }
                                }

                                selectedNode = targetPanel.currentNode;

//                                targetPanel.calculateLocalNormalizingValue(attachedPanel.getData(), targetPanel.currentNode);
//
//
//                                targetPanel.calculateRenderControlPointsOfEachHierarchy(attachedPanel.getData(), targetPanel.currentNode, targetPanel.getLocalNormalizingValue());
//                                targetPanel.computerZeroslopeAreasHierarchy(0);
//                                 targetPanel.detectEvents(targetPanel.getEventThreshold());
//                                targetPanel.UpdateTemporalView(new Dimension(targetPanel.getWidth(), targetPanel.getHeight()), targetPanel.getLocalNormalizingValue());
                                break;
                            }
                        }
                    } else {
                        for (int j = 0; j < attachedPanel.getCurrentAreas().size(); j++) {

                            CategoryStream categoryStream = attachedPanel.getCurrentAreas().get(j);

                            //System.out.println("mouse x and y " + mouseX + " " + mouseY);
                            if (categoryStream.getRenderRegion().contains(mouseX, mouseY)) {

                                // System.out.println(attachedPanel.getName());
                                //targetPanel = attachedPanel.parent.getTemporalFrame().getSubPanel(); 
                                if (!attachedPanel.currentNode.getChildren().isEmpty()) {
                                    {
                                        targetPanel.currentNode = (TreeNode) attachedPanel.currentNode.getChildren().get(j);
                                    }
                                } else {
                                    {
                                        targetPanel.currentNode = (TreeNode) attachedPanel.currentNode;
                                    }
                                }

                                selectedNode = targetPanel.currentNode;
//                                targetPanel.calculateLocalNormalizingValue(targetPanel.getData(), targetPanel.currentNode);
//                                targetPanel.calculateRenderControlPointsOfEachHierarchy(attachedPanel.getData(), targetPanel.currentNode, targetPanel.getLocalNormalizingValue());
//                                targetPanel.computerZeroslopeAreasHierarchy(0);
//                                targetPanel.detectEvents(targetPanel.getEventThreshold());
//                                targetPanel.UpdateTemporalView(new Dimension(targetPanel.getWidth(), targetPanel.getHeight()), targetPanel.getLocalNormalizingValue());

                                //                            
                                //                            TemporalViewPanel targetPanel = parent.getTemporalFrame().getSubPanel();
                                break;
                            }
                        }

                    }
                }

                // System.out.println("this is "+ selectedNode.getValue() + " selected");
                attachedPanel.parent.stateChanged(selectedNode);

//                targetPanel.currentNode = selectedNode;
//                targetPanel.calculateLocalNormalizingValue(targetPanel.getData(), targetPanel.currentNode);
//                targetPanel.calculateRenderControlPointsOfEachHierarchy(attachedPanel.getData(), targetPanel.currentNode, targetPanel.getLocalNormalizingValue());
//                targetPanel.computerZeroslopeAreasHierarchy(0);
//                targetPanel.UpdateTemporalView(new Dimension(targetPanel.getWidth(), targetPanel.getHeight()), targetPanel.getLocalNormalizingValue());
            }
        } else { //timecolumn mode

            if (SwingUtilities.isLeftMouseButton(me)) {

                switch (me.getClickCount()) {
                    case 2: {

                        int currentColumn = -1;
                        for (int i = 0; i < attachedPanel.getTimecolumns().size(); i++) {
                            TimeColumn timeColumn = attachedPanel.getTimecolumns().get(i);
                            if (timeColumn.getRenderRegion().contains(mouseX, mouseY)) {

                                currentColumn = i;
                                break;
                            }
                        }

                        if (currentColumn > -1) {
                            try {
                                System.out.println("currentColumn" + currentColumn);
                                CategoryBarElement d = ((TemporalViewFrame) attachedPanel.parent.getTemporalFrame()).getData();
                                d.calculateSubCategoryBar(currentColumn);

                                ZoomedTemporalViewPanel ztvp = new ZoomedTemporalViewPanel(attachedPanel.parent);
                                ztvp.parent = attachedPanel.parent;
                                ztvp.setCurrentAreas(new ArrayList<CategoryStream>());
                                ztvp.setTree(((TemporalViewFrame) attachedPanel.parent.getTemporalFrame()).getTree());
                                ztvp.setName("test");
                                ztvp.setData(d);
                                TreeNode tmp = attachedPanel.currentNode;
                                ztvp.currentNode = tmp;

                                ztvp.ClearZoomedNodeValue(tmp);
                                ztvp.BuildZoomedNodeValue(d, tmp, 0);

                                ztvp.setDocIdx(d.idxOfDocumentPerSlot.get(currentColumn));

                                HashMap<Integer, List<Integer>> lmap = new HashMap<Integer, List<Integer>>();
                                for (int j = 0; j < d.getNumOfTemporalBinsSub(); j++) {
                                    List<Integer> tmp1 = new ArrayList<Integer>();
                                    lmap.put(j, tmp1);
                                }

                                for (int i = 0; i < d.idxOfDocumentPerSlot.get(currentColumn).size(); i++) {
                                    long ld = d.getTime().get(d.idxOfDocumentPerSlot.get(currentColumn).get(i));

                                    int idx = (int) ((ld - d.getSubStartTime()) / d.getSub_timeInterval());
                                    lmap.get(idx).add(d.idxOfDocumentPerSlot.get(currentColumn).get(i));

                                }

                                ztvp.setDocumentInThisPanel(lmap);

                                ztvp.calculateLocalNormalizingValueSub(d, tmp);

                                //ztvp.getTimecolumns()
                                ztvp.setZoomed(true);
                                ztvp.setsize(1000, 500);

                                ztvp.calculateRenderControlPointsOfEachHierarchySub(d, tmp, ztvp.getLocalNormalizingValueSub());
                                //ztvp.getCurrentAreas().clear();

                                ztvp.computerZeroslopeAreasHierarchy(0);

                                ztvp.setTimecolumns(ztvp.getSubtimecolumns());

                                ztvp.UpdateTemporalView(new Dimension(1000, 500), 1);
                                System.out.println(ztvp.getLocalNormalizingValueSub() + " zoomed");

                                Point d2 = new Point(me.getXOnScreen(), me.getYOnScreen());

                                ZoomedTemporalFrame ztf = new ZoomedTemporalFrame(d2, 1000 + 20, 500 + 40, ztvp); //TODO: border size?
                                ztf.setVisible(true);

                            } catch (FileNotFoundException ex) {
                                Logger.getLogger(TemporalViewInteractions.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (IOException ex) {
                                Logger.getLogger(TemporalViewInteractions.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                    }

                    break;

                    case 1: {

                        int hour = 0;
                        int currentYear = 0;
                        for (int i = 0; i < attachedPanel.getTimecolumns().size(); i++) {
                            TimeColumn timeColumn = attachedPanel.getTimecolumns().get(i);
                            if (timeColumn.getRenderRegion().contains(mouseX, mouseY)) {
                                focusedColumn = timeColumn;
                                focusedColumn.setIsFocused(true);
                                attachedPanel.setFocusedColumn(i);//Hightlight year

                                currentYear = i;

                                //attachedPanel.parent.fireYearSelected(attachedPanel.getData().idxByHours.get(i));
                                List<Integer> selectedByYearTopic = new ArrayList<Integer>();

                                boolean belongsToOneStream = false;
                                for (int j = 0; j < attachedPanel.getCurrentAreas().size(); j++) {
                                    if (attachedPanel.getCurrentAreas().get(j).getRenderRegion().contains(mouseX, mouseY)) {

                                        attachedPanel.setTimeColumnStream(j);

                                        belongsToOneStream = true;

                                        if (attachedPanel.getFocusedSelectionList().contains(new Point2D.Double(i, j))) {
                                            attachedPanel.getFocusedSelectionList().remove(new Point2D.Double(i, j));

                                            break;
                                        }

                                        attachedPanel.getFocusedSelectionList().add(new Point2D.Double(i, j));
                                        try {
                                        //                                TreeNode t1;
                                            //                                if (!attachedPanel.currentNode.getChildren().isEmpty()) {
                                            //                                    t1 = (TreeNode) attachedPanel.currentNode.getChildren().get(j);
                                            //                                } else {
                                            //                                    t1 = (TreeNode) attachedPanel.currentNode;
                                            //                                }
                                            //                                //here
                                            //                                size = attachedPanel.getData().idxOfDocumentPerSlot.get(currentYear).size();
                                            //                                idx = 0;
                                            //                                for (int k = 0; k < size; k++) {
                                            //                                    idx = attachedPanel.getData().idxOfDocumentPerSlot.get(currentYear).get(k);
                                            //                                    //currentT = attachedPanel.getData().getTopicSequences().get(j);
                                            //
                                            //                                    for (int l = 0; l < t1.getTopicsContainedIdx().size(); l++) {
                                            //                                        int topicIndex = t1.getTopicsContainedIdx().get(l);
                                            //                                        if (attachedPanel.getData().values_Norm.get(idx)[topicIndex/*j*/] > 0.25) {
                                            //                                            selectedByYearTopic.add(idx);
                                            //                                        }
                                            //                                    }
                                            //                                }

                                        // if (!selectedByYearTopic.isEmpty()) --ww-old
                                            //                                }
                                            //DocumentViewer tempDV = new DocumentViewer(selectedByYearTopic, attachedPanel, t1.getColor(), new Point2D.Double(i, j));
                                            DocumentViewer dv = new DocumentViewer(attachedPanel, new Point2D.Double(i, j));
                                        } catch (IOException ex) {
                                            Logger.getLogger(TemporalViewInteractions.class.getName()).log(Level.SEVERE, null, ex);
                                        }

                                        selectedByYearTopic.clear();
                                        break;
                                    }
                                }

                                if (belongsToOneStream == false) {
                                    for (int j = 0; j < attachedPanel.getCurrentAreas().size(); j++) {

                                        attachedPanel.setTimeColumnStream(j);

                                        if (attachedPanel.getFocusedSelectionList().contains(new Point2D.Double(i, j))) {
                                            attachedPanel.getFocusedSelectionList().remove(new Point2D.Double(i, j));
                                        }

                                        attachedPanel.getFocusedSelectionList().add(new Point2D.Double(i, j));

//                                TreeNode t1 = (TreeNode) attachedPanel.currentNode;
//
//                                size = attachedPanel.getData().idxOfDocumentPerSlot.get(currentYear).size();
//                                idx = 0;
//                                for (int k = 0; k < size; k++) {
//                                    idx = attachedPanel.getData().idxOfDocumentPerSlot.get(currentYear).get(k);
//                                    //currentT = attachedPanel.getData().getTopicSequences().get(j);
//
//                                    for (int l = 0; l < t1.getTopicsContainedIdx().size(); l++) {
//                                        int topicIndex = t1.getTopicsContainedIdx().get(l);
//                                        if (attachedPanel.getData().values_Norm.get(idx)[topicIndex/*j*/] > 0.25) {
//                                            selectedByYearTopic.add(idx);
//                                        }
//                                    }
//                                }
//                                attachedPanel.parent.fireYearTopicSelected(selectedByYearTopic);
                                    }
                                    try {
                                        //                            DocumentViewer tempDV = new DocumentViewer(selectedByYearTopic, attachedPanel, null, new Point2D.Double(i, -99));
                                        DocumentViewer dv = new DocumentViewer(attachedPanel, new Point2D.Double(i, -99));
                                    } catch (IOException ex) {
                                        Logger.getLogger(TemporalViewInteractions.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    selectedByYearTopic.clear();

                                }

                                //isClicked = true;
                                attachedPanel.repaintView();

                                break;
                            }
                        }
                        //  System.out.println(attachedPanel.getFocusedColumn() + " focus is this");
                        attachedPanel.repaintView();

                    }
                    break;

                }

                if (me.getClickCount() == 9) {
                    int currentColumn = -1;
                    for (int i = 0; i < attachedPanel.getTimecolumns().size(); i++) {
                        TimeColumn timeColumn = attachedPanel.getTimecolumns().get(i);
                        if (timeColumn.getRenderRegion().contains(mouseX, mouseY)) {

                            currentColumn = i;
                            break;
                        }
                    }

                    if (currentColumn > -1) {
                        try {
                            System.out.println("currentColumn" + currentColumn);
                            CategoryBarElement d = ((TemporalViewFrame) attachedPanel.parent.getTemporalFrame()).getData();
                            d.calculateSubCategoryBar(currentColumn);

                            ZoomedTemporalViewPanel ztvp = new ZoomedTemporalViewPanel(attachedPanel.parent);
                            ztvp.parent = attachedPanel.parent;
                            ztvp.setCurrentAreas(new ArrayList<CategoryStream>());
                            ztvp.setTree(((TemporalViewFrame) attachedPanel.parent.getTemporalFrame()).getTree());
                            ztvp.setName("test");
                            ztvp.setData(d);
                            TreeNode tmp = attachedPanel.currentNode;
                            ztvp.currentNode = tmp;

                            ztvp.ClearZoomedNodeValue(tmp);
                            ztvp.BuildZoomedNodeValue(d, tmp, 0);

                            ztvp.setDocIdx(d.idxOfDocumentPerSlot.get(currentColumn));

                            HashMap<Integer, List<Integer>> lmap = new HashMap<Integer, List<Integer>>();
                            for (int j = 0; j < d.getNumOfTemporalBinsSub(); j++) {
                                List<Integer> tmp1 = new ArrayList<Integer>();
                                lmap.put(j, tmp1);
                            }

                            for (int i = 0; i < d.idxOfDocumentPerSlot.get(currentColumn).size(); i++) {
                                long ld = d.getTime().get(d.idxOfDocumentPerSlot.get(currentColumn).get(i));

                                int idx = (int) ((ld - d.getSubStartTime()) / d.getSub_timeInterval());
                                lmap.get(idx).add(d.idxOfDocumentPerSlot.get(currentColumn).get(i));

                            }

                            ztvp.setDocumentInThisPanel(lmap);

                            ztvp.calculateLocalNormalizingValueSub(d, tmp);

                            //ztvp.getTimecolumns()
                            ztvp.setZoomed(true);
                            ztvp.setsize(1000, 500);

                            ztvp.calculateRenderControlPointsOfEachHierarchySub(d, tmp, ztvp.getLocalNormalizingValueSub());
                            //ztvp.getCurrentAreas().clear();

                            ztvp.computerZeroslopeAreasHierarchy(0);

                            ztvp.setTimecolumns(ztvp.getSubtimecolumns());

                            ztvp.UpdateTemporalView(new Dimension(1000, 500), 1);
                            System.out.println(ztvp.getLocalNormalizingValueSub() + " zoomed");

                            Point d2 = new Point(me.getXOnScreen(), me.getYOnScreen());

                            ZoomedTemporalFrame ztf = new ZoomedTemporalFrame(d2, 1000 + 20, 500 + 40, ztvp); //TODO: border size?
                            ztf.setVisible(true);

                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(TemporalViewInteractions.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(TemporalViewInteractions.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                } else if (me.getClickCount() == 8) {

                    int hour = 0;
                    int currentYear = 0;
                    for (int i = 0; i < attachedPanel.getTimecolumns().size(); i++) {
                        TimeColumn timeColumn = attachedPanel.getTimecolumns().get(i);
                        if (timeColumn.getRenderRegion().contains(mouseX, mouseY)) {
                            focusedColumn = timeColumn;
                            focusedColumn.setIsFocused(true);
                            attachedPanel.setFocusedColumn(i);//Hightlight year

                            currentYear = i;

                            //attachedPanel.parent.fireYearSelected(attachedPanel.getData().idxByHours.get(i));
                            List<Integer> selectedByYearTopic = new ArrayList<Integer>();

                            boolean belongsToOneStream = false;
                            for (int j = 0; j < attachedPanel.getCurrentAreas().size(); j++) {
                                if (attachedPanel.getCurrentAreas().get(j).getRenderRegion().contains(mouseX, mouseY)) {

                                    attachedPanel.setTimeColumnStream(j);

                                    belongsToOneStream = true;

                                    if (attachedPanel.getFocusedSelectionList().contains(new Point2D.Double(i, j))) {
                                        attachedPanel.getFocusedSelectionList().remove(new Point2D.Double(i, j));

                                        break;
                                    }

                                    attachedPanel.getFocusedSelectionList().add(new Point2D.Double(i, j));
                                    try {
                                        //                                TreeNode t1;
                                        //                                if (!attachedPanel.currentNode.getChildren().isEmpty()) {
                                        //                                    t1 = (TreeNode) attachedPanel.currentNode.getChildren().get(j);
                                        //                                } else {
                                        //                                    t1 = (TreeNode) attachedPanel.currentNode;
                                        //                                }
                                        //                                //here
                                        //                                size = attachedPanel.getData().idxOfDocumentPerSlot.get(currentYear).size();
                                        //                                idx = 0;
                                        //                                for (int k = 0; k < size; k++) {
                                        //                                    idx = attachedPanel.getData().idxOfDocumentPerSlot.get(currentYear).get(k);
                                        //                                    //currentT = attachedPanel.getData().getTopicSequences().get(j);
                                        //
                                        //                                    for (int l = 0; l < t1.getTopicsContainedIdx().size(); l++) {
                                        //                                        int topicIndex = t1.getTopicsContainedIdx().get(l);
                                        //                                        if (attachedPanel.getData().values_Norm.get(idx)[topicIndex/*j*/] > 0.25) {
                                        //                                            selectedByYearTopic.add(idx);
                                        //                                        }
                                        //                                    }
                                        //                                }

                                        // if (!selectedByYearTopic.isEmpty()) --ww-old
                                        //                                }
                                        //DocumentViewer tempDV = new DocumentViewer(selectedByYearTopic, attachedPanel, t1.getColor(), new Point2D.Double(i, j));
                                        DocumentViewer dv = new DocumentViewer(attachedPanel, new Point2D.Double(i, j));
                                    } catch (IOException ex) {
                                        Logger.getLogger(TemporalViewInteractions.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                    selectedByYearTopic.clear();
                                    break;
                                }
                            }

                            if (belongsToOneStream == false) {
                                for (int j = 0; j < attachedPanel.getCurrentAreas().size(); j++) {

                                    attachedPanel.setTimeColumnStream(j);

                                    if (attachedPanel.getFocusedSelectionList().contains(new Point2D.Double(i, j))) {
                                        attachedPanel.getFocusedSelectionList().remove(new Point2D.Double(i, j));
                                    }

                                    attachedPanel.getFocusedSelectionList().add(new Point2D.Double(i, j));

//                                TreeNode t1 = (TreeNode) attachedPanel.currentNode;
//
//                                size = attachedPanel.getData().idxOfDocumentPerSlot.get(currentYear).size();
//                                idx = 0;
//                                for (int k = 0; k < size; k++) {
//                                    idx = attachedPanel.getData().idxOfDocumentPerSlot.get(currentYear).get(k);
//                                    //currentT = attachedPanel.getData().getTopicSequences().get(j);
//
//                                    for (int l = 0; l < t1.getTopicsContainedIdx().size(); l++) {
//                                        int topicIndex = t1.getTopicsContainedIdx().get(l);
//                                        if (attachedPanel.getData().values_Norm.get(idx)[topicIndex/*j*/] > 0.25) {
//                                            selectedByYearTopic.add(idx);
//                                        }
//                                    }
//                                }
//                                attachedPanel.parent.fireYearTopicSelected(selectedByYearTopic);
                                }
                                try {
                                    //                            DocumentViewer tempDV = new DocumentViewer(selectedByYearTopic, attachedPanel, null, new Point2D.Double(i, -99));
                                    DocumentViewer dv = new DocumentViewer(attachedPanel, new Point2D.Double(i, -99));
                                } catch (IOException ex) {
                                    Logger.getLogger(TemporalViewInteractions.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                selectedByYearTopic.clear();

                            }

                            //isClicked = true;
                            attachedPanel.repaintView();

                            break;
                        }
                    }
                    //  System.out.println(attachedPanel.getFocusedColumn() + " focus is this");
                    attachedPanel.repaintView();

                }
            }

        }

        attachedPanel.parent.getTemporalFrame().invalidate();

    }

    @Override
    public void mousePressed(MouseEvent me) {
    }

    @Override
    public void mouseReleased(MouseEvent me) {
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
        // If exit the themeriver region. The main floating HUDWindow should dipose to clean the screen
        //mainFloatingHUDWindow.getJDialog().dispose();
    }

    @Override
    public void mouseDragged(MouseEvent me) {
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        int mouseX = me.getX();
        int mouseY = me.getY();

        attachedPanel.currentMouseLocation.setLocation(mouseX, mouseY);

        globalMouseX = me.getX();
        globalMouseY = me.getY();

        if (!isClicked) {
            clearPreviousFocuses();

            for (int i = 0; i < attachedPanel.getTimecolumns().size(); i++) {
                TimeColumn timeColumn = attachedPanel.getTimecolumns().get(i);
                if (timeColumn.getRenderRegion().contains(mouseX, mouseY)) {
                    attachedPanel.setDisplayLengendTimeColumn(i);

                    if (attachedPanel.getLastDisplayLengendTimeColumn() != i) {
                        attachedPanel.setLastDisplayLengendTimeColumn(i);

                        //cal ratio
                    }

                    attachedPanel.setNeedDoLayout(false);
                    attachedPanel.repaintView();
                    break;
                }
            }

//               attachedPanel.repaintView();
            TemporalViewPanel p = attachedPanel.parent.getTemporalFrame().getMainPanel();

            if (attachedPanel.getName().equals(p.getName())) {

                boolean movedToOneStream = false;
                int selected_node_index = -1;
                for (int j = 0; j < attachedPanel.getCurrentAreas().size(); j++) {
                    CategoryStream categoryStream = attachedPanel.getCurrentAreas().get(j);
                    if (categoryStream.getRenderRegion().contains(mouseX, mouseY)) {
                        focusedStream = categoryStream;
                        focusedStream.setIsHighlight(true);

                        attachedPanel.setFocusedCatgory(j);

                        selected_node_index = j;
                        movedToOneStream = true;

                        break;
                    }
                }

                if (!movedToOneStream) {
                    attachedPanel.setFocusedCatgory(-99);
                }

                TreeNode tempt = attachedPanel.currentNode;

//                TemporalViewPanel targetPanel = attachedPanel.parent.getTemporalFrame().getSubPanel();
//                 attachedPanel.parent.getTemporalFrame().getSubPanel().currentNode = (TreeNode)tempt.getChildren().get(selected_node_index);
//                     targetPanel.currentNode = (TreeNode)tempt.getChildren().get(selected_node_index);
//                          targetPanel.calculateLocalNormalizingValue(targetPanel.getData(), targetPanel.currentNode);
//                          //targetPanel.setbShowEvents(false);
//                          targetPanel.getDetectionResults().clear();
//                         targetPanel.detectEvents(targetPanel.getEventThreshold());
//                                 // popUp.cbEventMenuItem.setSelected(false);
//                          
//                     targetPanel.UpdateTemporalView(new Dimension(targetPanel.getWidth(), targetPanel.getHeight()), targetPanel.getLocalNormalizingValue());
                //   System.out.println(attachedPanel.parent.getTemporalFrame().getSubPanel().currentNode.getValue());
                if (!tempt.getChildren().isEmpty() && selected_node_index != -1) {
                    TreeNode selectedNode = (TreeNode) tempt.getChildren().get(selected_node_index);

                    attachedPanel.parent.stateChanged(selectedNode);

//                    VisualizationViewer vv = attachedPanel.parent.getTopicGraphViewPanel().getVisualizationViewer();
//                    final PickedState<TreeNode> pickedState = vv.getPickedVertexState();
//                    //  int graphSize = vv.getGraphLayout().getGraph().getVertexCount();
//                    // vv.getLayout().getGraph() ;
//                    Collection<TreeNode> vertices = vv.getGraphLayout().getGraph().getVertices();
//
//                    pickedState.clear();
//                    //System.out.println(selectedNode.toString());
//                    int in = selectedNode.getIndex();
//                    String sla = selectedNode.getValue();
//                    //  System.out.println("selectedNode = " + in + " " + sla); 
//                    for (Iterator<TreeNode> it = vertices.iterator(); it.hasNext();) {
//                        TreeNode t = it.next();
//                        if (in == t.getIndex() && (sla == null ? t.getValue() == null : sla.equals(t.getValue()))) {
//                            pickedState.pick(t, true);
//                            // System.out.println(t.toString() + "is matched" );
//                            break;
//                        }
//                    }
//                    attachedPanel.parent.getTopicGraphViewPanel().getVisualizationViewer().setPickedVertexState(pickedState);
                }

                attachedPanel.setNeedDoLayout(false);
                attachedPanel.repaintView();

            }

            if (timecolumn/*attachedPanel.getPanelTimeColumnMode()*/) {

                int selected_node_index = -1;

                TreeNode mouseOveredNode = null;

                for (int j = 0; j < attachedPanel.getCurrentAreas().size(); j++) {
                    CategoryStream categoryStream = attachedPanel.getCurrentAreas().get(j);
                    if (categoryStream.getRenderRegion().contains(mouseX, mouseY)) {
                        selected_node_index = j;

                        break;
                    }
                }

                if (selected_node_index != -1) {
                    if (!attachedPanel.currentNode.getChildren().isEmpty()) {

                        mouseOveredNode = (TreeNode) attachedPanel.currentNode.getChildren().get(selected_node_index);

                    } else {

                        mouseOveredNode = attachedPanel.currentNode;

                    }
                }

                for (int i = 0; i < attachedPanel.getTimecolumns().size(); i++) {
                    TimeColumn timeColumn = attachedPanel.getTimecolumns().get(i);
                    if (timeColumn.getRenderRegion().contains(mouseX, mouseY)) {
                        focusedColumn = timeColumn;
                        focusedColumn.setIsFocused(true);
                        attachedPanel.setFocusedColumn(i);

                        
                         /**
     * Wenwen: comment here if not want word cloud
     */
                        attachedPanel.parent.stateChangedSecond(mouseOveredNode, i, attachedPanel, showingSingle);

                        //attachedPanel.setTimeColumnStream(j);
                    }
                }

                attachedPanel.setTimeColumnStream(-99);
                attachedPanel.setNeedDoLayout(false);
                attachedPanel.repaintView();
            }

        }

    }

    private void clearPreviousFocuses() {
        if (focusedStream != null) {
            focusedStream = null;
        }

        if (focusedColumn != null) {
            focusedColumn = null;
        }

        attachedPanel.setDisplayLengendTimeColumn(-99);

        attachedPanel.setFocusedCatgory(-99);
        attachedPanel.setFocusedColumn(-99);
    }

    class ZoomLevel {

        int zoomlevel;
        Long nearTime;
        Long farTime;

        public Long getFarTime() {
            return farTime;
        }

        public void setFarTime(Long farTime) {
            this.farTime = farTime;
        }

        public Long getNearTime() {
            return nearTime;
        }

        public void setNearTime(Long nearTime) {
            this.nearTime = nearTime;
        }

        public int getZoomlevel() {
            return zoomlevel;
        }

        public void setZoomlevel(int zoomlevel) {
            this.zoomlevel = zoomlevel;
        }

        public ZoomLevel(int zoomlevel, Long nearTime, Long farTime) {
            this.zoomlevel = zoomlevel;
            this.nearTime = nearTime;
            this.farTime = farTime;
        }
    }

    class myPopup extends JPopupMenu implements ActionListener {
        //JPopupMenu removePanel;

        TemporalViewFrame parentFrame;
        int targetRegion = 0;
        String currentPanelString;
        JSlider eventParaSlider;
        JLabel thresholdLabel;
        JCheckBoxMenuItem cbEventMenuItem;
        float threshold = 0;
        int currentdraw = 0;
        List<BufferedImage> heatImgList = new ArrayList<BufferedImage>();

        ;
        myPopup() {
            //super();

            //  removePanel = new JPopupMenu();
            JCheckBoxMenuItem cbMenuItem = new JCheckBoxMenuItem("Time Column Mode");
            cbMenuItem.addActionListener(aListener);
            this.add(cbMenuItem);
            this.addSeparator();
            JMenuItem menuItem = new JMenuItem("Remove");
            menuItem.addActionListener(bListener);
            this.add(menuItem);
            this.addSeparator();

            cbEventMenuItem = new JCheckBoxMenuItem("Show Event Mode");
            cbEventMenuItem.addActionListener(eListener);
            this.add(cbEventMenuItem);
            this.addSeparator();

            JPanel subPanel = new JPanel(new BorderLayout());
            subPanel.setComponentOrientation(
                    ComponentOrientation.LEFT_TO_RIGHT);
            this.add(subPanel);
            eventParaSlider = new JSlider();

            subPanel.add(eventParaSlider, BorderLayout.LINE_START);

            eventParaSlider.setValue(40);
            thresholdLabel = new JLabel();

            subPanel.add(thresholdLabel, BorderLayout.LINE_END);
            thresholdLabel.setText(String.valueOf(2.0));
            eventParaSlider.addChangeListener(thresholdListener);

            this.addSeparator();

            JMenuItem menuItemEvent = new JMenuItem("Detect Events");

            menuItemEvent.addActionListener(eventListener);
            this.add(menuItemEvent);

            this.addSeparator();

            JMenuItem menuPlayItemEvent = new JMenuItem("Play");

            menuPlayItemEvent.addActionListener(playEventListener);
            this.add(menuPlayItemEvent);

            this.addSeparator();

            JMenuItem singleItemEvent = new JCheckBoxMenuItem("SingleTopiceWords");
            singleItemEvent.addActionListener(keywordcheckboxListener);
            this.add(singleItemEvent);

        }

        public void setCurrentPanelString(String s) {
            this.currentPanelString = s;

        }

        public void setParentFrame(TemporalViewFrame pt) {
            this.parentFrame = pt;

        }
        ChangeListener thresholdListener = new ChangeListener() {
            public void stateChanged(ChangeEvent e) {

                eventParaSliderStateChanged(e);

            }
        };

        private void eventParaSliderStateChanged(ChangeEvent e) {

            threshold = eventParaSlider.getValue() / 20.0f;
            // Update the label
            this.thresholdLabel.setText(String.valueOf(threshold));

        }
        ActionListener playEventListener = new ActionListener() {
            public void actionPerformed(ActionEvent event) {

                heatImgList.clear();
                currentdraw = 0;
                int selected_node_index = -1;
                for (int j = 0; j < attachedPanel.getCurrentAreas().size(); j++) {
                    CategoryStream categoryStream = attachedPanel.getCurrentAreas().get(j);
                    if (categoryStream.getRenderRegion().contains(globalMouseX, globalMouseY)) {
                        focusedStream = categoryStream;
                        focusedStream.setIsHighlight(true);

                        attachedPanel.setFocusedCatgory(j);
                        selected_node_index = j;

                        break;
                    }
                }

                TreeNode selecedNode = attachedPanel.currentNode;

                if (selected_node_index != -1) {
                    if (!attachedPanel.currentNode.getChildren().isEmpty()) {

                        selecedNode = (TreeNode) attachedPanel.currentNode.getChildren().get(selected_node_index);

                    } else {

                        selecedNode = attachedPanel.currentNode;

                    }
                }

                try {

                    if (!(attachedPanel instanceof ZoomedTemporalViewPanel)) {

                        List<Float> maxes = new ArrayList<Float>();
                        List<DocumentViewer> dvs = new ArrayList<DocumentViewer>();
                        float lmaxvalue = -9;
                        for (int i = 0; i < attachedPanel.getTimecolumns().size(); i++) {

                            DocumentViewer dv = new DocumentViewer(attachedPanel, selecedNode, i);

                            float tempvalue = dv.GenerateImgMap(500, 250);
                            maxes.add(tempvalue);
                            dvs.add(dv);

                            if (tempvalue >= lmaxvalue) {
                                lmaxvalue = tempvalue;
                            }
                            //System.out.println(dv.getSelectedDocuments().size() + " image " + i + " calculated");
                            //String ts = Integer.toString(i) + ".png";
                            //File outputfile = new File(ts);
                            //ImageIO.write(heatImgList.get(i), "png", outputfile);
                        }

                        for (int i = 0; i < maxes.size(); i++) {
                            heatImgList.add((BufferedImage) dvs.get(i).createHeatMap(500, 250, lmaxvalue));
                        }

//                        heatImgList.add((BufferedImage) (dv.createHeatMap(500, 250)));
                    } else {
                        ZoomedTemporalViewPanel zp = (ZoomedTemporalViewPanel) attachedPanel.getSubtimecolumns();

                        List<Float> maxes = new ArrayList<Float>();
                        List<DocumentViewer> dvs = new ArrayList<DocumentViewer>();
                        float lmaxvalue = -9;
                        for (int i = 0; i < zp.getSubtimecolumns().size(); i++) {

                            DocumentViewer dv = new DocumentViewer(zp, selecedNode, i);

                            float tempvalue = dv.GenerateImgMap(500, 250);
                            maxes.add(tempvalue);
                            dvs.add(dv);

                            if (tempvalue >= lmaxvalue) {
                                lmaxvalue = tempvalue;
                            }

                        }

                        for (int i = 0; i < maxes.size(); i++) {
                            heatImgList.add((BufferedImage) dvs.get(i).createHeatMap(500, 250, lmaxvalue));
                        }

                    }
                } catch (IOException ex) {
                    Logger.getLogger(TemporalViewInteractions.class.getName()).log(Level.SEVERE, null, ex);
                }

                // attachedPanel.parent.getVCGF().getImgPreviewPanel();
                attachedPanel.parent.getVCGF().setHeatmapImgPack(heatImgList);

//                for (int i=0; i<heatImgList.size();i++)
//                {
//                    
//                      // Code to be executed
//                           
//                        
//                       try {
//                          Thread.sleep(2000);
//                        
//                           attachedPanel.parent.getVCGF().setHeatmapImg(heatImgList.get(i));
//                           
//                            attachedPanel.parent.getVCGF().invalidate();
//                           
//                           System.out.println("image " + i + " displayed");
//                       } catch (InterruptedException ex) {
//                           Logger.getLogger(TemporalViewInteractions.class.getName()).log(Level.SEVERE, null, ex);
//                       }
//                    
//                
//                
//               }
            }
        };
        ActionListener eventListener = new ActionListener() {
            public void actionPerformed(ActionEvent event) {

                //attachedPanel.setbShowEvents(!attachedPanel.isbShowEvents());
                if (attachedPanel.getEventThreshold() != threshold) {

                    attachedPanel.detectEvents(threshold);

                    attachedPanel.setEventThreshold(threshold);

                } else {

                    if (attachedPanel.getName() == "Sub") {
                        attachedPanel.detectEvents(attachedPanel.getEventThreshold());
                    } else if (attachedPanel.getDetectionResults() == null) {

                        attachedPanel.detectEvents(attachedPanel.getEventThreshold());
                        System.out.println("no former events, new events detected");
                    } else if (attachedPanel.getDetectionResults().size() == 0) {
                        attachedPanel.detectEvents(attachedPanel.getEventThreshold());
                        System.out.println("no former events, new events detected2");
                    }

                }

                attachedPanel.computeEventOutlineArea();

            }
        };
        ActionListener eListener = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                AbstractButton aButton = (AbstractButton) event.getSource();
                boolean selected = aButton.getModel().isSelected();

                attachedPanel.setbShowEvents(selected);

                if (attachedPanel.getName() == "Sub") {
                    attachedPanel.detectEvents(attachedPanel.getEventThreshold());
                } else if (attachedPanel.getDetectionResults() == null) {

                    attachedPanel.detectEvents(attachedPanel.getEventThreshold());
                    System.out.println("no former events, new events detected");
                } else if (attachedPanel.getDetectionResults().size() == 0) {
                    attachedPanel.detectEvents(attachedPanel.getEventThreshold());
                    System.out.println("no former events, new events detected2");
                }

                attachedPanel.computeEventOutlineArea();

            }
        };
        ActionListener aListener = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                AbstractButton aButton = (AbstractButton) event.getSource();
                boolean selected = aButton.getModel().isSelected();

                timecolumn = selected;
                parentFrame.setTimeColumnMode(selected);
                attachedPanel.setPanelTimeColumnMode(selected);

                if (attachedPanel.multiTopicKeywordList != null) {
                    attachedPanel.multiTopicKeywordList.clear();
                }

                if (attachedPanel.singleTopicKeywordList != null) {
                    attachedPanel.singleTopicKeywordList.clear();
                }

            }
        };
        
        
        
        // remove panel interactions
        void RemovePanel(TemporalViewPanel currentP)
        {
            
            
                if (currentP.getchildPanel().isEmpty())
                {
            
                   int indexremove = -1;
                   for (int j = 0; j < currentP.getFatherPanel().getchildPanel().size(); j++) {
                            
                                currentP.getFatherPanel().getchildPanel().remove(currentP);
                                indexremove = j;
                                System.out.print("TemporalFrame " + currentP.getName() +" removed");
                                break;
                            
                        }
                
                   
                  //remove all children
                              
                   
                    parentFrame.getTemporalPanelMap().get(currentP.getLevel()).remove(currentP);
                    currentP.getFatherPanel().getDrawLabels().remove(indexremove);
                    currentP.getFatherPanel().getDrawLabelsLocation().remove(indexremove);
                    System.out.println("removed" + currentP.getLevel() + " column " + indexremove);   
                
                }
                else
                {
                    
                    for (int i=0; i< currentP.getchildPanel().size(); i++)
                    {
                        RemovePanel(currentP.getchildPanel().get(i));
                        i--;
                    }
                    
                    int indexremove = -1;
                    for (int j = 0; j < currentP.getFatherPanel().getchildPanel().size(); j++) {

                                 currentP.getFatherPanel().getchildPanel().remove(currentP);
                                 indexremove = j;
                                 System.out.print("TemporalFrame " + currentP.getName() +" removed");
                                 break;

                         }


                   //remove all children


                 parentFrame.getTemporalPanelMap().get(currentP.getLevel()).remove(currentP);
                 currentP.getFatherPanel().getDrawLabels().remove(indexremove);
                 currentP.getFatherPanel().getDrawLabelsLocation().remove(indexremove);
                 System.out.println("removed" + currentP.getLevel() + " column " + indexremove);     
                    
                    
                }
        }
        
        
        
        ActionListener bListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JMenuItem source = (JMenuItem) (e.getSource());
                //System.out.print(source);
                
                TemporalViewPanel currentP = attachedPanel;
                                       
                RemovePanel(currentP);
                
                         
            
                
                
                
            
//                for (TemporalViewPanel tvp : parentFrame.getSecondColumn()) {
//                    if (tvp.getName().equals(currentPanelString)) {
//
//                        if (!parentFrame.getThirdColumn().isEmpty()) {
//                            // int size = ;
//                            for (int k = 0; k < parentFrame.getThirdColumn().size(); k++) {
//                                TemporalViewPanel tvpson = parentFrame.getThirdColumn().get(k);
//                                if (tvpson.getFatherPanel().equals(tvp)) {
//                                    parentFrame.getThirdColumn().remove(tvpson);
//                                    tvpson.getFatherPanel().removeChildPanel(tvpson);
//                                    System.out.println("removed 3rd column ");
//                                    k--;
//                                    //tvpson.getFatherPanel().getDrawLabels().remove(Integer.parseInt(currentPanelString.replaceAll("\\D", ""))); 
//                                }
//
//                            }
//
//                        }
//
//                        int indexremove = 0;
//
//                        for (int j = 0; j < tvp.getFatherPanel().getchildPanel().size(); j++) {
//                            if (tvp.getFatherPanel().getchildPanel().get(j).equals(tvp)) {
//                                tvp.getFatherPanel().getchildPanel().remove(tvp);
//                                indexremove = j;
//                                break;
//                            }
//                        }
//                        if (tvp.getFatherPanel().getDrawLabels().size() > 0) {
//                            tvp.getFatherPanel().getDrawLabels().remove(indexremove);
//                            tvp.getFatherPanel().getDrawLabelsLocation().remove(indexremove);
//                        }
//
//                        parentFrame.getSecondColumn().remove(tvp);
//                        parentFrame.getMainPanel().removeChildPanel(tvp);
//
//                        break;
//                    }
//
//                }
//
//                for (TemporalViewPanel tvp : parentFrame.getThirdColumn()) {
//
//                    int indexremove = 0;
//                    if (tvp.getName().equals(currentPanelString)) {
//                        parentFrame.getThirdColumn().remove(tvp);
//
//                        for (int j = 0; j < tvp.getFatherPanel().getchildPanel().size(); j++) {
//                            if (tvp.getFatherPanel().getchildPanel().get(j).equals(tvp)) {
//                                tvp.getFatherPanel().getchildPanel().remove(tvp);
//                                indexremove = j;
//                                break;
//                            }
//                        }
//
//                        tvp.getFatherPanel().getDrawLabels().remove(indexremove);
//                        tvp.getFatherPanel().getDrawLabelsLocation().remove(indexremove);
//                        //tvp.getFatherPanel().getDrawLabels().remove(Integer.parseInt(currentPanelString.replaceAll("\\D", "")));
//                        break;
//                    }
//
//                }

                parentFrame.setMigLayoutForScrollPane();

                parentFrame.updateAllPanels();

                //parentFrame.revalidate();
                //parent.setGridBagLayout();
                // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };

        ActionListener keywordcheckboxListener = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                AbstractButton aButton = (AbstractButton) event.getSource();
                boolean selected = aButton.getModel().isSelected();

                showingSingle = selected;

            }
        };

        public void actionPerformed(ActionEvent e) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
//        public void actionPerformed(ActionEvent e) {
//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        }
}
