/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TasteAnalytics.Apollo.TemporalView;

import com.TasteAnalytics.Apollo.datahandler.CategoryBarElement;
import com.TasteAnalytics.Apollo.GUI.ViewController;
import com.TasteAnalytics.Apollo.TopicRenderer.WorldMapProcessingPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.LineBorder;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author xwang
 */
public class TemporalViewFrame extends JPanel implements TemporalViewListener, MouseListener, KeyListener {

    /**
     * The ParallelDisplay component we are assigned to.
     */
    private int myFrameWidth = 0;
    private int myFrameHeight = 0;
    ViewController parent;
    private TemporalViewPanel mainPanel;
    //private WorldMapProcessingPanel worldPanel;
    //private TemporalViewPanel subPanel;
    private CategoryBarElement data;

    private List<Float> topicSims;
    private List<Float[]> colorMap;
    //private List<TemporalViewPanel> secondColumn;
    //private List<TemporalViewPanel> thirdColumn;
    private List<TreeNode> myTree;

    HashMap<Integer, List<TemporalViewPanel>> temporalPanelMap = new HashMap<Integer, List<TemporalViewPanel>>();
    HashMap<Integer, JPanel> layoutPanelMap = new HashMap<Integer, JPanel>();

    public HashMap<Integer, List<TemporalViewPanel>> getTemporalPanelMap() {
        return temporalPanelMap;
    }

    public HashMap<Integer, JPanel> getLayoutPanelMap() {
        return layoutPanelMap;
    }

    //JPanel leftPanel;
    // JPanel rightPanel;
    // JPanel midPanel;
    public List<TreeNode> getTree() {
        return myTree;
    }

    public CategoryBarElement getData() {
        return data;

    }

    public TemporalViewPanel getMainPanel() {
        return mainPanel;
    }

    public void setMainPanel(TemporalViewPanel mainPanel) {
        this.mainPanel = mainPanel;
    }

//    public TemporalViewPanel getSubPanel() {
//        return subPanel;
//    }
//    public WorldMapProcessingPanel getWorldPanel() {
//        return worldPanel;
//    }
//
//    public void setWorldPanel(WorldMapProcessingPanel worldPanel) {
//        this.worldPanel = worldPanel;
//    }

    private boolean b_timeColumnMode;

    public boolean getTimeColumnMode() {
        return b_timeColumnMode;
    }

    public void setTimeColumnMode(boolean b) {
        b_timeColumnMode = b;
    }

    public void setMigLayoutForScrollPane() {

        List<Integer> removeKeys = new ArrayList<Integer>();

        for (Map.Entry<Integer, List<TemporalViewPanel>> entry : temporalPanelMap.entrySet()) {
            int Key = entry.getKey();

            List<TemporalViewPanel> value = (List<TemporalViewPanel>) entry.getValue();
            if (value.isEmpty()) {
                removeKeys.add(Key);

            }
        }

        for (int i = 0; i < removeKeys.size(); i++) {
            int Key = removeKeys.get(i);
            temporalPanelMap.remove(Key);
            System.out.println("key " + Key + " removed, Map size " + temporalPanelMap.size());

        }

        setAllPanelSize();

        //System.out.println("all panel size set");
        testPanel.removeAll();
        int framewidth = testPanel.getWidth();

        int frameheight = testPanel.getHeight();
        int dummyHeight = frameheight/3;
        
        

        int num_of_extra_columns = temporalPanelMap.size();
        int sizec1 = num_of_extra_columns > 0 ? 1 : 0;
        layoutPanelMap.clear();

        int secondaryPanelWith = (num_of_extra_columns + 1);

        MigLayout layout;

        String columnCons = "[]";
        for (int i = 0; i < temporalPanelMap.size(); i++) {
            columnCons += "0[]";

        }

        layout = new MigLayout("insets 0", columnCons, "[]0[]0[]");

        //layout = new GridLayout();
        //System.out.println("constraint " + columnCons);
        int x1 = (int) (framewidth / (1 + sizec1));
        int y1 = frameheight / 3;

        testPanel.setLayout(layout);

        layoutPanelMap.put(0, new JPanel());
        layoutPanelMap.get(0).setSize(new Dimension(x1, frameheight));
        layoutPanelMap.get(0).setPreferredSize(new Dimension(x1, frameheight));

        GridBagLayout gridBag = new GridBagLayout();
        layoutPanelMap.get(0).setLayout(gridBag);

        GridBagConstraints cons;

        cons = new GridBagConstraints();
        cons.gridx = 0;
        cons.gridy = 0;
        cons.gridwidth = 1;
        cons.gridheight = 2;

        cons.weighty = 1;//0.66;
        cons.weightx = 1.0f;

        cons.fill = GridBagConstraints.BOTH;

        cons.anchor = GridBagConstraints.PAGE_START;
        gridBag.setConstraints(mainPanel, cons);
        mainPanel.setBorder(new LineBorder(Color.black, 1));
        layoutPanelMap.get(0).add(mainPanel);

        cons = new GridBagConstraints();
        cons.gridx = 0;
        cons.gridy = 2;
        cons.weighty = 0.33;

        cons.gridheight = 1;
        cons.gridwidth = 1;
        cons.weightx = 1.0f;

        cons.fill = GridBagConstraints.BOTH;
        cons.anchor = GridBagConstraints.PAGE_END;

//        worldPanel.setBorder(new LineBorder(Color.black, 1));
//        gridBag.setConstraints(worldPanel, cons);
//        layoutPanelMap.get(0).add(worldPanel);
//        
        //layoutPanelMap.get(0).setPreferredSize(new Dimension(mainPanel.getMyPanelWidth(), frameheight));
        testPanel.add(layoutPanelMap.get(0), "gap 0 0");

        int secondColumnSize = 3;
        int numOfdummies = 0;
        if (num_of_extra_columns >= 1) {

            layoutPanelMap.put(1, new JPanel());

            if (temporalPanelMap.get(1).size() <= 3) {
                secondColumnSize = 3;
            } else {
                secondColumnSize = temporalPanelMap.get(1).size();
            }

            GridLayout borderlayout = new GridLayout(secondColumnSize, 1);
            layoutPanelMap.get(1).setLayout(borderlayout);

            if (secondColumnSize <= 3) {
                int widthHere = 0;
                y1 = frameheight / 3;
                for (int i = 0; i < temporalPanelMap.get(1).size(); i++) {

                    temporalPanelMap.get(1).get(i).setBorder(new LineBorder(Color.black, 1));
                    temporalPanelMap.get(1).get(i).setPanelLabelId(i);
                    temporalPanelMap.get(1).get(i).setName("second" + i);

                    temporalPanelMap.get(1).get(i).setBorder(new LineBorder(Color.black, 1));
                    widthHere = temporalPanelMap.get(1).get(i).getMyPanelWidth();
                    //temporalPanelMap.get(1).get(i).setMyPanelSize(x1, y1);
                    layoutPanelMap.get(1).add(temporalPanelMap.get(1).get(i));

                }
               // System.out.println("widthHere" + widthHere);

                numOfdummies = 3 - temporalPanelMap.get(1).size();
                for (int j = 0; j < numOfdummies; j++) {
                    JPanel dummyPanel = new JPanel();
                    dummyPanel.setSize(new Dimension(widthHere, dummyHeight));
                    dummyPanel.setPreferredSize(new Dimension(widthHere, dummyHeight));
                    //dummyPanel.setBorder(new LineBorder(Color.black, 10));
                    dummyPanel.setBackground(Color.gray);
                    layoutPanelMap.get(1).add(dummyPanel);

                }

            } else {
                //y1 = frameheight/secondColumnSize;

                for (int i = 0; i < secondColumnSize; i++) {

                    temporalPanelMap.get(1).get(i).setBorder(new LineBorder(Color.black, 1));
                    temporalPanelMap.get(1).get(i).setPanelLabelId(i);
                    temporalPanelMap.get(1).get(i).setName("1 " + i);
                    //temporalPanelMap.get(1).get(i).setMyPanelSize(x1, y1);
                    layoutPanelMap.get(1).add(temporalPanelMap.get(1).get(i));

                }

            }

            layoutPanelMap.get(1).setSize(temporalPanelMap.get(1).get(0).getMyPanelWidth(), frameheight);
            layoutPanelMap.get(1).setPreferredSize(new Dimension(temporalPanelMap.get(1).get(0).getMyPanelWidth(), frameheight));

            testPanel.add(layoutPanelMap.get(1)/*, "gap 0 0"*/);
        }

        HashMap<Integer, Integer> lastColumnDummyNum = new HashMap<Integer, Integer>();

        lastColumnDummyNum.put(1, numOfdummies);

        for (int L = 2; L < (temporalPanelMap.size() + 1); L++) {
            Component[] jp = layoutPanelMap.get(L - 1).getComponents();

            layoutPanelMap.put(L, new JPanel());

            BoxLayout borderlayout = new BoxLayout(layoutPanelMap.get(L), BoxLayout.Y_AXIS);
            layoutPanelMap.get(L).setLayout(borderlayout);
            layoutPanelMap.get(L).setPreferredSize(new Dimension(temporalPanelMap.get(L - 1).get(0).getMyPanelWidth(), frameheight));
            layoutPanelMap.get(L).setSize(temporalPanelMap.get(L - 1).get(0).getMyPanelWidth(), frameheight);

            for (int m = 0; m < jp.length; m++) {

                int count = 0;
                System.out.println(jp[m].getClass());
                Component comp = jp[m];

                if (jp[m] instanceof JPanel && !(jp[m] instanceof TemporalViewPanel)) {
                    JPanel dummyPanel = new JPanel();

                    dummyPanel.setPreferredSize(new Dimension(temporalPanelMap.get(L - 1).get(0).getMyPanelWidth(), temporalPanelMap.get(L - 1).get(0).getMyPanelHeight()));
                    //dummyPanel.setBorder(new LineBorder(Color.black, 10));
                    dummyPanel.setBackground(Color.gray);
                    layoutPanelMap.get(L).add(dummyPanel);

                } else if (jp[m] instanceof TemporalViewPanel) {
                    TemporalViewPanel tjp = (TemporalViewPanel) jp[m];

                    if (tjp.getchildPanel().isEmpty()) {
                        JPanel dummyPanel = new JPanel();
                        dummyPanel.setPreferredSize(new Dimension(tjp.getMyPanelWidth(), tjp.getMyPanelHeight()));
                        //dummyPanel.setBorder(new LineBorder(Color.black, 10));
                        dummyPanel.setBackground(Color.gray);
                        layoutPanelMap.get(L).add(dummyPanel);

                    } else {

                        for (int j = 0; j < tjp.getchildPanel().size(); j++) {

                            TemporalViewPanel tempPanelChild = tjp.getchildPanel().get(j);
                            tempPanelChild.setBorder(new LineBorder(Color.black, 1));
                            tempPanelChild.setName(tempPanelChild.getLevel() + " " + count);
                            tempPanelChild.setPanelLabelId(j);
                            //widthHere = tempPanelChild.getMyPanelWidth();
                            //y1 = tempPanel.getFatherPanel().getMyPanelHeight()/tempPanel.getFatherPanel().getchildPanel().size();
                            //tempPanel.setPreferredSize(new Dimension(x1, y1));
                            layoutPanelMap.get(L).add(tempPanelChild);

                            count++;

                        }

                    }

                } else {
                    System.out.println("non-panel component detected, impossible!");
                }

            }

            testPanel.add(layoutPanelMap.get(L), "gap 0 0");

        }

//        for (int L = 2; L < (temporalPanelMap.size() + 1); L++) {
//            //find child panel and create dummy panel
//            layoutPanelMap.put(L, new JPanel());
//            ///MigLayout layout3 = new MigLayout("fill, gap 0");
//             
//             
//                int numOfEmptyParents = 0;
//                int tmpCount = 0;
//                int numOFChildrenSum = 0;
//            for (TemporalViewPanel p : temporalPanelMap.get(L-1))
//            {
//                if (p.getchildPanel().isEmpty())
//                    numOfEmptyParents++;
//                else
//                    numOFChildrenSum+=p.getchildPanel().size();
//            }
//        
//            int ColumnSize = 0;
//              if (temporalPanelMap.get(L).size()<=3)
//                ColumnSize = 3;
//            else
//                ColumnSize = temporalPanelMap.get(1).size();
//        
//            int sizeofthenextcolumn = 0;
//             
//            int numOfdummiesFather = lastColumnDummyNum.get(L-1);
//            sizeofthenextcolumn =  /*temporalPanelMap.get(L).size()*/ numOFChildrenSum + numOfdummiesFather + numOfEmptyParents  /*temporalPanelMap.get(L-1).size()*/;
//            
//             
//            
//            System.out.println("num " + L + " column has " + sizeofthenextcolumn + " panels, empty parents " + numOfEmptyParents);
//            BoxLayout borderlayout = new BoxLayout(layoutPanelMap.get(L),BoxLayout.Y_AXIS);//(sizeofthenextcolumn, 1);
//            
//            
//            
//            layoutPanelMap.get(L).setLayout(borderlayout);
//            layoutPanelMap.get(L).setPreferredSize(new Dimension(temporalPanelMap.get(L).get(0).getMyPanelWidth(), frameheight));
//            layoutPanelMap.get(L).setSize(temporalPanelMap.get(L).get(0).getMyPanelWidth(), frameheight);
//            
//            int count = 0;
//              int widthHere = 0;
//          
//                for (int m = 0; m < temporalPanelMap.get(L - 1).size(); m++) {
//
//                    TemporalViewPanel tempPanel = temporalPanelMap.get(L - 1).get(m);
//                    if (temporalPanelMap.get(L - 1).get(m).getchildPanel().isEmpty())                    
//                    {
//                        JPanel dummyPanel = new JPanel();                    
//                        dummyPanel.setPreferredSize(new Dimension(tempPanel.getMyPanelWidth(), dummyHeight));
//                        dummyPanel.setBorder(new LineBorder(Color.black, 10));
//                        dummyPanel.setBackground(Color.green);
//                        layoutPanelMap.get(L).add(dummyPanel);
//                        tmpCount++;
//                    }
//                    else
//                    {
//                        for (int j = 0; j < tempPanel.getchildPanel().size(); j++) {
//
//                            TemporalViewPanel tempPanelChild = tempPanel.getchildPanel().get(j);
//                            tempPanelChild.setBorder(new LineBorder(Color.black, 1));
//                            tempPanelChild.setName(tempPanelChild.getLevel() + " " + count);
//                            tempPanelChild.setPanelLabelId(j);
//                            widthHere = tempPanelChild.getMyPanelWidth();
//                            //y1 = tempPanel.getFatherPanel().getMyPanelHeight()/tempPanel.getFatherPanel().getchildPanel().size();
//                            //tempPanel.setPreferredSize(new Dimension(x1, y1));
//                            layoutPanelMap.get(L).add(tempPanelChild);
//
//                            count++;
//
//                        }
//                    }
//
//                }
//                
//                
//                if (ColumnSize<=3)
//                {
//                    int numOfdummies2 = 3 - temporalPanelMap.get(1).size();
//                    for (int j = 0; j < numOfdummies2; j++) {
//                        JPanel dummyPanel = new JPanel();
//                        dummyPanel.setSize(new Dimension(widthHere, dummyHeight));
//                        dummyPanel.setPreferredSize(new Dimension(widthHere, dummyHeight));
//                        dummyPanel.setBorder(new LineBorder(Color.black, 10));
//                        dummyPanel.setBackground(Color.green);
//                        layoutPanelMap.get(L).add(dummyPanel);
//
//                    }
//                
//                
//                    tmpCount += numOfdummies2;
//                    
//                    
//                    
//                }
//            
//    
//
//            lastColumnDummyNum.put(L, tmpCount);            
//            
//            testPanel.add(layoutPanelMap.get(L), "gap 0 0");
//
//        }
        updateAllPanels();

//        for (Integer i : temporalPanelMap.keySet())
//        {
//            
//            //System.out.println( i + " " + temporalPanelMap.get(i).size() );
//            if (i>=2)
//            {
//                for (int j=0; j<temporalPanelMap.get(i).size();j++)
//                //System.out.println(temporalPanelMap.get(i).get(j).getMyPanelHeight());
//                
//     
//            }
//        }
//          
//        
//        for (Integer i : lastColumnDummyNum.values())
//             System.out.println(" " +  i);
//        
        repaint();
        this.getRootPane().revalidate();
        //revalidate();

    }

//    public void setGridBagLayout() {
//        this.getContentPane().removeAll();
//        int secondColumnExist = secondColumn.isEmpty() ? 0 : 1;
//        int thirdColumnExist = thirdColumn.isEmpty() ? 0 : 1;
//
//        GridBagLayout gridBag = new GridBagLayout();
//        this.getContentPane().setLayout(gridBag);
//
//        GridBagConstraints cons;
//
//        cons = new GridBagConstraints();
//        cons.gridx = 0;
//        cons.gridy = 0;
//        cons.gridwidth = 1;
//        cons.gridheight = 2;
//
//        cons.weighty = 0.66;
//        cons.weightx = 1.0f / (1 + secondColumnExist + thirdColumnExist);
//
//        // System.out.println( cons.weightx);
//        cons.fill = GridBagConstraints.BOTH;
////          cons.gridwidth  = 900;
////        cons.gridheight = 1000;
//        // cons.fill = GridBagConstraints.BOTH;
//
//        //this.getContentPane().setBorder(outline);
//        cons.anchor = GridBagConstraints.PAGE_START;
//        // mainPanel.setMinimumSize(new Dimension(300,300));
//        gridBag.setConstraints(mainPanel, cons);
//        mainPanel.setBorder(new LineBorder(Color.black, 1));
//        this.getContentPane().add(mainPanel);
//
//        cons = new GridBagConstraints();
//        cons.gridx = 0;
//        cons.gridy = 2;
//        cons.weighty = 0.33;
//        //cons.weightx = 0;
////         cons.gridwidth  = 900;
//        cons.gridheight = 1;
//        cons.gridwidth = 1;
//        cons.weightx = 1.0f / (1 + secondColumnExist + thirdColumnExist);
//
//        //System.out.println( cons.weightx);
//        // cons.gridheight = 1;
//        cons.fill = GridBagConstraints.BOTH;
//        cons.anchor = GridBagConstraints.PAGE_END;
//        //  subPanel.setMinimumSize(new Dimension(300,300));
//        subPanel.setBorder(new LineBorder(Color.black, 1));
//        gridBag.setConstraints(subPanel, cons);
//        this.getContentPane().add(subPanel);
//
//        for (int i = 0; i < secondColumn.size(); i++) {
//            cons = new GridBagConstraints();
//            cons.gridx = 1;
//            cons.gridy = i;
//            //cons.weightx = 0;
////         cons.gridwidth  = 900;
////        cons.gridheight = 1000;
//            //  cons.anchor = GridBagConstraints.CENTER;
//            cons.gridwidth = 1;
//            //cons.gridheight = 1;
//            //cons.fill = GridBagConstraints.BOTH;
//            cons.weightx = 1.0f / (1 + secondColumnExist + thirdColumnExist);
//
//            cons.weighty = 0.33;//secondColumn.size();
//            cons.fill = GridBagConstraints.BOTH;
//            // secondColumn.get(i).setMinimumSize(new Dimension(300,300));
//            gridBag.setConstraints(secondColumn.get(i), cons);
//            secondColumn.get(i).setBorder(new LineBorder(Color.black, 1));
//            secondColumn.get(i).setPanelLabelId(i);
//            secondColumn.get(i).setName("second" + i);
//
//            this.getContentPane().add(secondColumn.get(i));
//
//        }
//
//        for (int i = 0; i < thirdColumn.size(); i++) {
//            cons = new GridBagConstraints();
//            cons.gridx = 2;
//            cons.gridy = i;
//            //cons.weightx = 0;
//            cons.gridwidth = 1;
//            // cons.gridheight = 1;
//            cons.weightx = 1.0f / (1 + secondColumnExist + thirdColumnExist);
//            cons.weighty = 0.33;//this.getHeight()/ thirdColumn.size();
////         cons.gridwidth  = 900;
////        cons.gridheight = 1000;
//            cons.fill = GridBagConstraints.BOTH;
//            gridBag.setConstraints(thirdColumn.get(i), cons);
//            thirdColumn.get(i).setBorder(new LineBorder(Color.black, 1));
//            thirdColumn.get(i).setName("third" + i);
//            thirdColumn.get(i).setPanelLabelId(i);
//            this.getContentPane().add(thirdColumn.get(i));
//
//        }
//
//        repaint();
//        revalidate();
//
//    }
    public void setAllPanelSize() {

        int tempWidth = myFrameWidth;
        int tempHeight = myFrameHeight;

        int sizeFactor = -1;
        int mainSizeFactor = 0;

        if (temporalPanelMap.isEmpty()) {
            sizeFactor = 0;
            mainSizeFactor = 0;
        } else if (temporalPanelMap.size() == 1) {
            sizeFactor = 1;
            mainSizeFactor = 1;
        } else {
            sizeFactor = 3;
            mainSizeFactor = 1;

        }

        testPanel.setPreferredSize(
                new Dimension(
                        (temporalPanelMap.size()) * tempWidth / (sizeFactor + 1) + tempWidth / (1 + mainSizeFactor),
                        tempHeight));

       // System.out.println("in update testpan " + testPanel.getWidth() + " " + testPanel.getHeight());
        mainPanel.setMyPanelSize(tempWidth / (1 + mainSizeFactor), tempHeight * 3 / 3);
        //  worldPanel.resize(tempWidth / (1 + mainSizeFactor),tempHeight / 3);
        //worldPanel.setPreferredSize(new Dimension(tempWidth / (1 + mainSizeFactor), tempHeight / 3));
        //subPanel.setMyPanelSize(tempWidth / (1 + mainSizeFactor), tempHeight / 3);

        int maxSecondaryPanelHeight = testPanel.getHeight() / 3;

        int secondWidth = (int) (myFrameWidth / (1 + sizeFactor));
       // System.out.println(temporalPanelMap.size() + " secondWidth " + secondWidth + " ");

        if (temporalPanelMap.size() > 0) {
            for (TemporalViewPanel p : temporalPanelMap.get(1)) {
                int sizeofpanelshere = temporalPanelMap.get(1).size();
                int tmpHeight = (int) (myFrameHeight / sizeofpanelshere);
                tmpHeight = tmpHeight >= maxSecondaryPanelHeight ? maxSecondaryPanelHeight : tmpHeight;

                p.setMyPanelSize(secondWidth, tmpHeight);

            }

            for (int i = 2; i < temporalPanelMap.size() + 1; i++) {

                for (TemporalViewPanel p : temporalPanelMap.get(i)) {

                    int sizes = p.getFatherPanel().getchildPanel().size();
                    p.setMyPanelSize(secondWidth, (int) (p.getFatherPanel().getMyPanelHeight() / sizes));
                    // System.out.println("sizes " + sizes + " " +(int) (p.getFatherPanel().getMyPanelHeight() / sizes));

                }
            }

        }

    }

    public void updateAllPanels() {

        //setMigLayoutForScrollPane();
        //mainPanel.setPreferredSize(new Dimension(mainPanel.getMyPanelWidth(), mainPanel.getMyPanelHeight()));
        mainPanel.UpdateTemporalView(new Dimension(mainPanel.getMyPanelWidth(), mainPanel.getMyPanelHeight() * 3 / 3), mainPanel.getLocalNormalizingValue());
        //subPanel.UpdateTemporalView(new Dimension(subPanel.getMyPanelWidth(), subPanel.getMyPanelHeight()), subPanel.getLocalNormalizingValue());
        // worldPanel.setPreferredSize(new Dimension(mainPanel.getMyPanelWidth(), mainPanel.getMyPanelHeight()/2));

        for (List<TemporalViewPanel> ltp : temporalPanelMap.values()) {
            float tempMaxNormalValue = -1;

            for (TemporalViewPanel p : ltp) {
                if (p.getLocalNormalizingValue() >= tempMaxNormalValue) {
                    tempMaxNormalValue = p.getLocalNormalizingValue();
                }
            }

            for (TemporalViewPanel p : ltp) {

                p.setGlobalNormalizingValue(tempMaxNormalValue);
                p.UpdateTemporalView(new Dimension(p.getMyPanelWidth(), p.getMyPanelHeight()), tempMaxNormalValue);

            }

            testPanel.invalidate();
            //worldPanel.invalidate();
//        tempMaxNormalValue = -1;
//        for (TemporalViewPanel p : thirdColumn) {
//            if (p.getLocalNormalizingValue() >= tempMaxNormalValue) {
//                tempMaxNormalValue = p.getLocalNormalizingValue();
//            }
//        }
//        
//        
//        for (TemporalViewPanel p : getTemporalPanelMap().get(n)) {
//            p.setPreferredSize(new Dimension(x1, (int) (tempHeight / 9)));
//            p.UpdateTemporalView(new Dimension(x1, (int) (tempHeight / 9)), tempMaxNormalValue);
//
//        }
        }

    }

    JPanel testPanel = new JPanel();
    //JScrollPane scrollPane;
    JPanel menuPanel = new JPanel();

    public TemporalViewFrame(ViewController vc, int WW, int HH) throws IOException {
        super();
        setPreferredSize(new Dimension(WW, HH));

//        for (int i=0; i<2; i++)
//        {
//            List<TemporalViewPanel> tmptvp = new ArrayList<TemporalViewPanel>();
//            temporalPanelMap.put(i, tmptvp);
//            JPanel tmpjp = new JPanel();
//            layoutPanelMap.put(i,tmpjp);
//        }
//        
        this.setLayout(new BorderLayout());

       // menuPanel.setSize(WW, HH/5);
       //  this.getContentPane().add(menuPanel,BorderLayout.PAGE_START);
        //scrollPane = new JScrollPane(testPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        //this.add(scrollPane, BorderLayout.CENTER);
        //scrollPane.setViewportView(testPanel);
        this.add(testPanel);
        

        myFrameWidth = 1000;//this.getContentPane().getWidth();//1000;//
        myFrameHeight = 1000;//this.getContentPane().getHeight() - this.getContentPane().getHeight()/5;//1000;//
        //System.out.println("frame size " + myFrameWidth + " " + myFrameHeight);

        testPanel.setPreferredSize(new Dimension(myFrameWidth, myFrameHeight));

        mainPanel = new TemporalViewPanel(vc);


        //subPanel = new TemporalViewPanel(vc);
        mainPanel.setName("Main");
        //subPanel.setName("Sub");

        mainPanel.setLevel(0);
        //subPanel.setLevel(0);

        //this.setSize(new Dimension(1800, 900));

        mainPanel.setMyPanelSize( myFrameWidth, myFrameHeight / 3 * 3);
        //worldPanel.setPreferredSize(new Dimension(myFrameWidth, myFrameHeight / 3 ));

        //subPanel.setMyPanelSize( myFrameWidth, myFrameHeight / 3);

        layoutPanelMap.put(0, new JPanel());
        //List<TemporalViewPanel> ini = new ArrayList<TemporalViewPanel>();
//        ini.add(subPanel)ini.add(subPanel);
//        temporalPanelMap.put(0, );

//        secondColumn = new ArrayList<TemporalViewPanel>();
//        thirdColumn = new ArrayList<TemporalViewPanel>();
//        //setGridBagLayout();
        //setMigLayout();
        //testPanel.add(new JPanel());
        //setMigLayoutForScrollPane();
        this.addComponentListener(new ComponentListener() {
            public void componentResized(ComponentEvent e) {

                //scrollPane.setPreferredSize(new Dimension(e.getComponent().getSize().width, e.getComponent().getSize().height));
                ((TemporalViewFrame) e.getComponent()).setSize(new Dimension(e.getComponent().getSize().width, e.getComponent().getSize().height));

                myFrameWidth = ((TemporalViewFrame) e.getComponent()).getWidth();//e.getComponent().getSize().width;
                myFrameHeight = ((TemporalViewFrame) e.getComponent()).getHeight();//e.getComponent().getSize().height;

//                int sizeFactor = -1;
//                int mainSizeFactor = 0;
//                if (getTemporalPanelMap().size() == 0) {
//                    sizeFactor = 0;
//                    mainSizeFactor = 0;
//                } else if (getTemporalPanelMap().size() == 1) {
//                    sizeFactor = 0;
//                    mainSizeFactor = 1;
//                } else if (getTemporalPanelMap().size() == 2) {
//                    sizeFactor = 3;
//                    mainSizeFactor = 1;
//
//                } else {
//                    sizeFactor = 3;
//                    mainSizeFactor = 1;
//                }
//
//                testPanel.setPreferredSize(
//                        new Dimension(
//                                (sizeFactor) * myFrameWidth / 2  + myFrameWidth / (1 + mainSizeFactor),
//                                myFrameHeight));
//
//                System.out.println("testpan " + (myFrameWidth / 2 / (1 + sizeFactor) + myFrameWidth / (1 + mainSizeFactor)) + " " + myFrameHeight);
//                
//                int x1 = (int) (myFrameWidth / (1 + mainSizeFactor));
//                int y1 = (int) (myFrameHeight * 2 / 3);
//
//                mainPanel.setMyPanelSize(x1, y1);
//                subPanel.setMyPanelSize(x1, y1/2);
//
//                mainPanel.UpdateTemporalView(new Dimension(x1, y1), mainPanel.getLocalNormalizingValue());
//                subPanel.UpdateTemporalView(new Dimension(x1, (int) (myFrameHeight / 3)), subPanel.getLocalNormalizingValue());
                setMigLayoutForScrollPane();//setMigLayout();

                //update all
//                int maxSecondaryPanelHeight = testPanel.getHeight() / 3;
//                int maxSecondaryPanelWidth = testPanel.getWidth() / 2;
//
//                int minSecondaryPanelHeight = testPanel.getHeight() / 20;
//                int minSecondaryPanelWidth = testPanel.getHeight() / 4;
//
//                if (getTemporalPanelMap().size() > 0) {
//                    float tempMaxNormalValue = -1;
//
//                    for (TemporalViewPanel p : getTemporalPanelMap().get(1)) {
//                        if (p.getLocalNormalizingValue() >= tempMaxNormalValue) {
//                            tempMaxNormalValue = p.getLocalNormalizingValue();
//                        }
//                    }
//
//                    x1 = (int) (myFrameWidth / 2 / (1 + sizeFactor));
//
//                    for (TemporalViewPanel p : getTemporalPanelMap().get(1)) {
//
//                        int sizeofpanelshere = getTemporalPanelMap().get(1).size();
//                        int tmpHeight = myFrameHeight / sizeofpanelshere;
//                        tmpHeight = tmpHeight >= maxSecondaryPanelHeight ? maxSecondaryPanelHeight : tmpHeight;
//
//                        p.setPreferredSize(new Dimension(x1, (int) tmpHeight));
//                        p.setGlobalNormalizingValue(tempMaxNormalValue);
//                        p.UpdateTemporalView(new Dimension(x1, (int) tmpHeight), tempMaxNormalValue);
//
//                    }
//
//                    for (int i = 2; i < getTemporalPanelMap().size() + 1; i++) {
//
//                        tempMaxNormalValue = -1;
//                        for (TemporalViewPanel p : getTemporalPanelMap().get(i)) {
//                            if (p.getLocalNormalizingValue() >= tempMaxNormalValue) {
//                                tempMaxNormalValue = p.getLocalNormalizingValue();
//                            }
//                        }
//
//                        for (TemporalViewPanel p : getTemporalPanelMap().get(i)) {
//
//                            int sizes = p.getFatherPanel().getchildPanel().size();
//                            p.setPreferredSize(new Dimension(x1, (int) (p.getFatherPanel().getMyPanelHeight() / sizes)));
//                            p.UpdateTemporalView(new Dimension(x1, (int) (p.getFatherPanel().getMyPanelHeight() / sizes)), tempMaxNormalValue);
//
//                        }
//                    }
//                }
                ((TemporalViewFrame) e.getComponent()).invalidate();

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
        //pack();

    }


    
    
    public void loadCacheData(String databaseName, String TreeString, String host) throws IOException
    {

        data = new CategoryBarElement(databaseName, host);
        myTree = new ArrayList<TreeNode>();
        buildTreeWithString(TreeString);

        BuildNodeValue(data, myTree.get(0));
        BuildUnNormNodeValue(data, myTree.get(0));

        myTree.get(0).calculateNodeContainedIdx();
        //  myTree.get(0).calculateNodeString();
        System.out.println("building trees and values in temporal frame finished...");
        setNodeColor();
        getMainPanel().setData(data);

        getMainPanel().currentNode = myTree.get(0);
        //calculateRenderControlPoints(data);

        getMainPanel().setTree(myTree);

        getMainPanel().calculateLocalNormalizingValue(data, getMainPanel().currentNode);
        getMainPanel().calculateRenderControlPointsOfEachHierarchy(data, getMainPanel().currentNode, getMainPanel().getLocalNormalizingValue());
        getMainPanel().computerZeroslopeAreasHierarchy(0);
        getMainPanel().detectEvents(getMainPanel().getEventThreshold());

//        getSubPanel().setData(data);
//
//        getSubPanel().currentNode = myTree.get(1);
//        //calculateRenderControlPoints(data);
//
//        getSubPanel().setTree(myTree);
//        getSubPanel().calculateLocalNormalizingValue(data, getSubPanel().currentNode);
//        getSubPanel().calculateRenderControlPointsOfEachHierarchy(data, getSubPanel().currentNode, getSubPanel().getLocalNormalizingValue());
//        getSubPanel().computerZeroslopeAreasHierarchy(0);
//        getSubPanel().detectEvents(getMainPanel().getEventThreshold());
        getMainPanel().UpdateTemporalView(new Dimension(getMainPanel().getMyPanelWidth(), getMainPanel().getMyPanelHeight()), getMainPanel().getLocalNormalizingValue());

        //getSubPanel().UpdateTemporalView(new Dimension(getSubPanel().getMyPanelWidth(), getSubPanel().getMyPanelHeight()), getSubPanel().getLocalNormalizingValue());
        System.out.println("initial calculating of main and subpanel done");

    }

    public void loadData(String path, List<String[]> internalRecord, List<Long> years,
            List<String[]> allDocs, List<String[]> termWeights, List<float[]> termWeights_norm, Map<String, Integer> termIndex,
            List<String[]> allTopics, String csvpath, int contentIdx, DateFormat f, float incrementalDays, boolean b_readall, boolean b_recalculate, int NumOfTemporalBinsSub, List<HashMap<String, Integer>> content) throws FileNotFoundException, IOException {

        data = new CategoryBarElement(internalRecord, years, allDocs, termWeights, termWeights_norm, termIndex, allTopics, csvpath, contentIdx, f,
                incrementalDays, b_readall, b_recalculate, NumOfTemporalBinsSub, content);

        myTree = new ArrayList<TreeNode>();

        System.out.println("building trees and values in temporal frame");

        buildTree(path);

        System.out.println("build tree finished");
        BuildNodeValue(data, myTree.get(0));
        BuildUnNormNodeValue(data, myTree.get(0));

        myTree.get(0).calculateNodeContainedIdx();
        //  myTree.get(0).calculateNodeString();

        System.out.println("building trees and values in temporal frame finished...");
        setNodeColor();
        getMainPanel().setData(data);

        getMainPanel().currentNode = myTree.get(0);
        //calculateRenderControlPoints(data);

        getMainPanel().setTree(myTree);

        getMainPanel().calculateLocalNormalizingValue(data, getMainPanel().currentNode);
        getMainPanel().calculateRenderControlPointsOfEachHierarchy(data, getMainPanel().currentNode, getMainPanel().getLocalNormalizingValue());
        getMainPanel().computerZeroslopeAreasHierarchy(0);
        getMainPanel().detectEvents(getMainPanel().getEventThreshold());

//        getSubPanel().setData(data);
//
//        getSubPanel().currentNode = myTree.get(1);
//        //calculateRenderControlPoints(data);
//
//        getSubPanel().setTree(myTree);
//        getSubPanel().calculateLocalNormalizingValue(data, getSubPanel().currentNode);
//        getSubPanel().calculateRenderControlPointsOfEachHierarchy(data, getSubPanel().currentNode, getSubPanel().getLocalNormalizingValue());
//        getSubPanel().computerZeroslopeAreasHierarchy(0);
//        getSubPanel().detectEvents(getMainPanel().getEventThreshold());
        getMainPanel().UpdateTemporalView(new Dimension(getMainPanel().getMyPanelWidth(), getMainPanel().getMyPanelHeight()), getMainPanel().getLocalNormalizingValue());

        //getSubPanel().UpdateTemporalView(new Dimension(getSubPanel().getMyPanelWidth(), getSubPanel().getMyPanelHeight()), getSubPanel().getLocalNormalizingValue());
        System.out.println("initial calculating of main and subpanel done");

    }

    public void updateData(List<TreeNode> tree) {

        //myTree.clear();
        myTree = tree;

        // System.out.println("tree is" + tree.size());
        // System.out.println(myTree.size());
        BuildNodeValue(data, myTree.get(0));
        BuildUnNormNodeValue(data, myTree.get(0));
        myTree.get(0).calculateNodeContainedIdx();
        // myTree.get(0).calculateNodeString();

        getMainPanel().currentNode = myTree.get(0);
        getMainPanel().setTree(myTree);

        getMainPanel().calculateLocalNormalizingValue(data, getMainPanel().currentNode);
        getMainPanel().calculateRenderControlPointsOfEachHierarchy(data, getMainPanel().currentNode, getMainPanel().getLocalNormalizingValue());

        getMainPanel().computerZeroslopeAreasHierarchy(0);

//        getSubPanel().currentNode = myTree.get(0);
//
//        getSubPanel().setTree(myTree);
//        getSubPanel().calculateLocalNormalizingValue(data, getSubPanel().currentNode);
//        getSubPanel().calculateRenderControlPointsOfEachHierarchy(data, getSubPanel().currentNode, getSubPanel().getLocalNormalizingValue());
//
//        getSubPanel().computerZeroslopeAreasHierarchy(0);
//
//        getMainPanel().getDrawLabels().clear();
//        getSubPanel().getDrawLabels().clear();
//        getMainPanel().getDrawLabelsLocation().clear();
//        getSubPanel().getDrawLabelsLocation().clear();
        getMainPanel().UpdateTemporalView(new Dimension(getMainPanel().getMyPanelWidth(), getMainPanel().getMyPanelHeight()), getMainPanel().getLocalNormalizingValue());
        //getSubPanel().UpdateTemporalView(new Dimension(getSubPanel().getMyPanelWidth(), getSubPanel().getMyPanelHeight()), getSubPanel().getLocalNormalizingValue());

        setMigLayoutForScrollPane();
        //setGridBagLayout();

    }

    private float[] BuildUnNormNodeValue(CategoryBarElement data, TreeNode t) {
        int numofYears = data.getNumOfYears();
        List<Float> result = new ArrayList<Float>();
        float[] tempresult = new float[numofYears];
        float[] tempsum = new float[numofYears];

        if (t.getUnNormArrayValue().size() == 0) {
            if (t.getChildren().size() == 0) {

                if (t.getValue().contains("L")) {
                    int index = t.getIndex();
                    tempresult = data.getUnormCategoryBar().get(index);

                    for (int i = 0; i < numofYears; i++) {
                        result.add(tempresult[i]);
                    }

                    t.setUnNormArrayValue(result);
                } else {
                    for (int i = 0; i < numofYears; i++) {
                        result.add(0f);
                    }

                    t.setUnNormArrayValue(result);

                }
                return tempresult;

            } else {

                for (int i = 0; i < t.getChildren().size(); i++) {
                    for (int j = 0; j < numofYears; j++) {
                        tempresult = BuildUnNormNodeValue(data, (TreeNode) (t.getChildren().get(i)));
                        tempsum[j] += tempresult[j];
                    }

                }

                for (int i = 0; i < numofYears; i++) {
                    result.add(tempsum[i]);
                }

                t.setUnNormArrayValue(result);
                return tempsum;
            }
        }

        result = t.getArrayValue();
        for (int i = 0; i < numofYears; i++) {
            tempresult[i] = result.get(i);
        }

        return tempresult;

    }

    private float[] BuildNodeValue(CategoryBarElement data, TreeNode t) {
        int numofYears = data.getNumOfYears();
        List<Float> result = new ArrayList<Float>();
        float[] tempresult = new float[numofYears];
        float[] tempsum = new float[numofYears];

        if (t.getArrayValue().isEmpty()) {
            if (t.getChildren().isEmpty()) {

                if (t.getValue().contains("L")) {
                    int index = t.getIndex();
                    tempresult = data.getCategoryBar().get(index);

                    for (int i = 0; i < numofYears; i++) {
                        result.add(tempresult[i]);
                    }

                    t.setArrayValue(result);
                } else {
                    for (int i = 0; i < numofYears; i++) {
                        result.add(0f);
                    }

                    t.setArrayValue(result);

                }
                return tempresult;

            } else {

                for (int i = 0; i < t.getChildren().size(); i++) {
                    for (int j = 0; j < numofYears; j++) {
                        tempresult = BuildNodeValue(data, (TreeNode) (t.getChildren().get(i)));
                        tempsum[j] += tempresult[j];
                    }

                }

                for (int i = 0; i < numofYears; i++) {
                    result.add(tempsum[i]);
                }

                t.setArrayValue(result);
                return tempsum;
            }
        }

        result = t.getArrayValue();
        for (int i = 0; i < numofYears; i++) {
            tempresult[i] = result.get(i);
        }

        return tempresult;

    }

    public void buildTree(String path) throws FileNotFoundException, IOException {

        String everything;
        FileInputStream inputStream = new FileInputStream(path + "tree.txt");
        try {
            everything = IOUtils.toString(inputStream);
        } finally {
            inputStream.close();
        }

        everything = everything.replaceAll(" ", "");
        everything = everything.replaceAll("\r", "");

        Scanner sc = new Scanner(everything);

        sc.useDelimiter("\n");
        String temp = sc.next();

        String nodes = sc.next();

        String[] tempNodes = nodes.split(",");

        System.out.println(tempNodes.length + " nodes in tree");
        TreeNode NodeArray[] = new TreeNode[1000];
        TreeNode LeafArray[] = new TreeNode[1000];

        for (int i = 0; i < tempNodes.length; i++) {

            String a = tempNodes[i].replaceAll("\\D", "");

            int index = Integer.parseInt(a);

            TreeNode t = new TreeNode(index, tempNodes[i].replaceAll("[^\\p{L}\\p{N}]", ""));

            if (tempNodes[i].replaceAll("[^\\p{L}\\p{N}]", "").charAt(0) == 'N') {

                NodeArray[index] = t;
                myTree.add(t);
            } else if (tempNodes[i].replaceAll("[^\\p{L}\\p{N}]", "").charAt(0) == 'L') {
                LeafArray[index] = t;
                myTree.add(t);
            } else {
                int c = 0;
            }
        }

        String edges = sc.next();

        String[] tempEdges = edges.split("\\),");

        System.out.println((tempEdges.length - 1) + " links in tree");

        for (int i = 0; i < tempEdges.length - 1; i++) {
            String[] tempE = tempEdges[i].split(",");
            TreeNode tt1, tt2;

            int index1 = Integer.parseInt(tempE[0].replaceAll("\\D", ""));
            int index2 = Integer.parseInt(tempE[1].replaceAll("\\D", ""));

            String tempE1 = tempE[0].replaceAll("[^\\p{L}\\p{N}]", "");
            if (tempE1.charAt(0) == 'N') {

                tt1 = NodeArray[index1];
            } else if (tempE1.charAt(0) == 'L') {
                tt1 = LeafArray[index1];
            } else {
                int c = 0;
                tt1 = null;
            }

            String tempE2 = tempE[1].replaceAll("[^\\p{L}\\p{N}]", "");
            if (tempE2.charAt(0) == 'N') {
                tt2 = NodeArray[index2];
            } else if (tempE2.charAt(0) == 'L') {
                tt2 = LeafArray[index2];
            } else {
                int c = 0;
                tt2 = null;
            }

            tt1.addChildNode(tt2);
        }
    }

    public void buildTreeWithString(String everything) {

        everything = everything.replaceAll(" ", "");
        everything = everything.replaceAll("\r", "");

        Scanner sc = new Scanner(everything);

        sc.useDelimiter("\n");
        String temp = sc.next();

        String nodes = sc.next();

        String[] tempNodes = nodes.split(",");

        System.out.println(tempNodes.length + " nodes in tree");
        TreeNode NodeArray[] = new TreeNode[1000];
        TreeNode LeafArray[] = new TreeNode[1000];

        for (int i = 0; i < tempNodes.length; i++) {

            String a = tempNodes[i].replaceAll("\\D", "");

            int index = Integer.parseInt(a);

            TreeNode t = new TreeNode(index, tempNodes[i].replaceAll("[^\\p{L}\\p{N}]", ""));

            if (tempNodes[i].replaceAll("[^\\p{L}\\p{N}]", "").charAt(0) == 'N') {

                NodeArray[index] = t;
                myTree.add(t);
            } else if (tempNodes[i].replaceAll("[^\\p{L}\\p{N}]", "").charAt(0) == 'L') {
                LeafArray[index] = t;
                myTree.add(t);
            } else {
                int c = 0;
            }
        }

        String edges = sc.next();

        String[] tempEdges = edges.split("\\),");

        System.out.println((tempEdges.length - 1) + " links in tree");

        for (int i = 0; i < tempEdges.length - 1; i++) {
            String[] tempE = tempEdges[i].split(",");
            TreeNode tt1, tt2;

            int index1 = Integer.parseInt(tempE[0].replaceAll("\\D", ""));
            int index2 = Integer.parseInt(tempE[1].replaceAll("\\D", ""));

            String tempE1 = tempE[0].replaceAll("[^\\p{L}\\p{N}]", "");
            if (tempE1.charAt(0) == 'N') {

                tt1 = NodeArray[index1];
            } else if (tempE1.charAt(0) == 'L') {
                tt1 = LeafArray[index1];
            } else {
                int c = 0;
                tt1 = null;
            }

            String tempE2 = tempE[1].replaceAll("[^\\p{L}\\p{N}]", "");
            if (tempE2.charAt(0) == 'N') {
                tt2 = NodeArray[index2];
            } else if (tempE2.charAt(0) == 'L') {
                tt2 = LeafArray[index2];
            } else {
                int c = 0;
                tt2 = null;
            }

            tt1.addChildNode(tt2);
        }
    }

    public void setNodeColor() {
        List<Float[]> colorSpecturm = getMainPanel().getCurrentColorMap();

        int size = colorSpecturm.size();

        myTree.get(0).setColor(Color.white);

        for (int i = 0; i < myTree.get(0).getChildren().size(); i++) {

            Color current = new Color(colorSpecturm.get((int) i % size)[1], colorSpecturm.get((int) i % size)[2], colorSpecturm.get((int) i % size)[3]);
            TreeNode t = (TreeNode) myTree.get(0).getChildren().get(i);
            t.setBaseColor(current);
            t.setColor(current);

        }

//        for (int i=0; i<myTree.size(); i++)
//        {
//            TreeNode t = myTree.get(i);
//            if (t.getLevel() == 1)  
//            {
//                t.setBaseColor(t.getParent().getBaseColor());
//                 t.setColor(t.getParent().getBaseColor()) ;         
//            }                        
//        }
        // System.out.println("set color begin");
        for (int i = 1; i < myTree.size(); i++) {
            TreeNode t = myTree.get(i);

            if (t.getLevel() > 1) {
                TreeNode colorNode = t.getParent(); //myTree.get(i);

//                while (colorNode.getLevel() > 1) {
//                    colorNode = colorNode.getParent();
//                }
                if (t.getParent().getChildren().isEmpty()) {
                    t.setColor(colorNode.getColor());
                } else {
                    if (t.getParent().getChildren().size() == 1) {

                        t.setColor(colorNode.getColor());
                        //t.setBaseColor(t.getParent().getBaseColor()) ;

                    } else {

                        // t.setBaseColor(t.getParent().getBaseColor()) ;
                        int size2 = t.getParent().getChildren().size();
                        Color tempColor = t.getParent().getColor();//colorNode.getBaseColor();
                        float[] hsv = new float[3];
                        Color.RGBtoHSB(tempColor.getRed(), tempColor.getGreen(), tempColor.getBlue(), hsv);
                        List<Color> c = getHueColors(size2, hsv[0]);
                        for (int j = 0; j < t.getParent().getChildren().size(); j++) {
                            if (t.equals(((TreeNode) t.getParent().getChildren().get(j)))) {
                                t.setColor(c.get(j));
                            }
                        }

                    }
                }
            }
        }
        //System.out.println("set color success");

    }

    private List<Color> getHueColors(int size, float H) {
        List<Color> tempcolorMap = new ArrayList<Color>();
        try {

            float S = (float) 0.5;
            float V = (float) 0.2;
            float varH = (float) 0.2;
            for (int i = 0; i < size; i++) {
                float tempS = (float) S / ((float) size) * (float) i + (float) 0.2;
                float tempV = (float) V / size * i + (float) 0.7;
                float tempH = H - (float) varH / 2 + (float) varH / size * i;
                Color r = hsv2rgb(tempH, tempS, tempV);

                tempcolorMap.add(r);
            }

            if (size == 0) {
                Color r = hsv2rgb(H, S, V);
                tempcolorMap.add(r);

            }

        } catch (Exception e) {
            System.out.println(" hue colorMap generation failed!");
        }
        return tempcolorMap;
    }

    private Color hsv2rgb(float h, float s, float v) {
        h = (h % 1 + 1) % 1; // wrap hue

        int i = (int) Math.floor((float) (h * 6));
        float f = h * 6 - i;
        float p = v * (1 - s);
        float q = v * (1 - s * f);
        float t = v * (1 - s * (1 - f));

        switch (i) {
            case 0:
                return new Color(v, t, p);
            case 1:
                return new Color(q, v, p);
            case 2:
                return new Color(p, v, t);
            case 3:
                return new Color(p, q, v);
            case 4:
                return new Color(t, p, v);
            case 5:
                return new Color(v, p, q);
        }

        return null;
    }

//     public class MyChangeAction implements ChangeListener{
//        public void stateChanged(ChangeEvent ce){
//        int value = slider.getValue();
//        float alpha = (float)value/(float)50 + 1;
//        String str = Float.toString(alpha);
//        if(str.length()>3){
//            str = str.substring(0, 3);
//        }
//        label.setText(str);
//        
//        int w = mainPanel.getWidth();
//        int h = mainPanel.getHeight();
//        mainPanel.recalculateEvents(alpha, w, h);
//        }
    //   }
    public void setFocusedCatgory(int focusedCatgory) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void mouseClicked(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void mousePressed(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void mouseReleased(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void mouseEntered(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void mouseExited(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void keyTyped(KeyEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void keyPressed(KeyEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void keyReleased(KeyEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
