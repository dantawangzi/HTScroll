/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TasteAnalytics.Apollo.TreeMapView;

import com.TasteAnalytics.Apollo.GUI.ViewController;
import com.TasteAnalytics.Apollo.TemporalView.TreeNode;
import com.TasteAnalytics.Apollo.TopicRenderer.LabelText;
import com.TasteAnalytics.Apollo.Util.Colors;
import com.TasteAnalytics.Apollo.Util.SystemPreferences;
import com.TasteAnalytics.Apollo.Wordle.LabelWordleLite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 *
 * @author Li
 */
public class TopicTreeMapPanel extends JPanel {

    //private MapDisplay view;
    ViewController parent;
    private TreeModel model;
    private MapLayout algorithm;
    private Rect bounds;

    public int mywidth = 0;
    public int myheight = 0;

    MapModel map;
    List<TreeNode> tree;

    HashMap<TreeNode, MapModel> NodeMap = new HashMap<TreeNode, MapModel>();
    HashMap<TreeNode, TreeMapNodePanel> nodePanels = new HashMap<TreeNode, TreeMapNodePanel>();

    public HashMap<TreeNode, TreeMapNodePanel> getNodePanel() {
        return nodePanels;
    }

    public TopicTreeMapPanel(ViewController vc, List<TreeNode> tr, int w, int h) throws IOException {

//        TopicTreeMapPanelInteractions interactions = new TopicTreeMapPanelInteractions();
//        addMouseListener(interactions);
//        addMouseMotionListener(interactions);
        this.setBackground(Colors.mainBackgroundColor);

        mywidth = w;
        myheight = h;

        parent = vc;
        this.tree = tr;
        setSize(w, h);
        setPreferredSize(new Dimension(w, h));
        model = new TreeModel();

        //TODO: We need to reconsider the tree generation mechanism
//        TreeMapNodePanel root = new TreeMapNodePanel(vc, tree.get(0), tree.get(0).getLevel(), new Rectangle(0, 0, w, h));
//        for (int i=0; i<tree.size(); i++)
//        {
//            if (tree.get(i).getValue().contains("L"))
//            {
//                int index = tree.get(i).getIndex();
//                  tree.get(i).setNumberOfEvents(parent.topicEventsCount.get(index));
//                
//            }
//            
//        }
        // tree.get(0).calculateTreeMapTopicWeight();
       // updateTreeLayout(w, h);
        tree.get(0).setMyRect(new Rectangle(0, 0, w, h));
        Mappable[] leaves = tree.get(0).getItems();
        map = new RandomMap(leaves);
        algorithm = new SquarifiedLayout();
        algorithm.layout(map, new Rect(0, 0, w, h));
        tree.get(0).calculateRect(new Rectangle(0, 0, w, h));
        setLayout(null);

        for (int i = 1; i < tree.size(); i++) {

            TreeMapNodePanel tmp = new TreeMapNodePanel(parent, tree.get(i), tree.get(i).getLevel(), tree.get(i).getMyRect(), parent.getTreemapMiniTemporal().get(tree.get(i)));
            nodePanels.put(tree.get(i), tmp);
            tmp.list = parent.allLabelInWordle.get(tree.get(i));

            List<LabelText> tmplist = new ArrayList<LabelText>();

            for (int j = 0; j < parent.wordsToDisplayInWordle; j++) {
                tmplist.add(parent.allLabels.get(tree.get(i)).get(j));
            }
            tmp.setLabels(tmplist);
            tmp.DrawWordleCloud(new Point(0, 0), tmplist);

            if (tree.get(i).getChildren().isEmpty()) {
                tmp.setVisible(true);
            } else {
                tmp.setBorder(SystemPreferences.treemapNodeBorder);
                tmp.setVisible(false);
                tmp.setOpaque(false);

            }

            this.add(tmp);

        }

    }

    public List<TreeNode> getTree() {
        return tree;
    }

    public void setTree(List<TreeNode> tree) {
        this.tree = tree;
    }

    public void updateTreeLayout(int w, int h, boolean clearFlag) throws IOException {

        
        
        if (clearFlag)
        {
            this.removeAll();
        
        
            nodePanels.clear();
        }
        
        
        tree.get(0).setMyRect(new Rectangle(0, 0, w, h));
        Mappable[] leaves = tree.get(0).getItems();
        map = new RandomMap(leaves);
        algorithm = new SquarifiedLayout();

        algorithm.layout(map, new Rect(0, 0, w, h));

        tree.get(0).calculateRect(new Rectangle(0, 0, w, h));

        for (TreeNode treenode : tree) {

            if (treenode.getChildren().isEmpty()) {

                if (nodePanels.containsKey(treenode)) {

                    TreeMapNodePanel tmp = nodePanels.get(treenode);
                    //tmp.setMyBI(parent.getPanelImages().get(treenode));

                    tmp.setMyRect(treenode.getMyRect());
 List<LabelText> tmplist = new ArrayList<LabelText>();

                    for (int j = 0; j < parent.wordsToDisplayInWordle; j++) {
                        tmplist.add(parent.allLabels.get(treenode).get(j));
                    }
                    tmp.setLabels(tmplist);
                    
                    for (int i = 0; i < tmp.getLabels().size(); i++) {
                     tmp.wordCloudPanel.add(tmp.getLabels().get(i));
                    }
                    tmp.updateLayout();

                    tmp.DrawWordleCloud(new Point(0, 0), nodePanels.get(treenode).getLabels());

                    //TreeMapNodePanel tmp = new TreeMapNodePanel(parent, tree.get(i),tree.get(i).getLevel(), tree.get(i).getMyRect());
                } else {

                    TreeMapNodePanel tmp = new TreeMapNodePanel(parent, treenode, treenode.getLevel(), treenode.getMyRect(), parent.getTreemapMiniTemporal().get(treenode));
                    nodePanels.put(treenode, tmp);
                    tmp.list = parent.allLabelInWordle.get(treenode);
                    tmp.setMyRect(treenode.getMyRect());
                    

                    List<LabelText> tmplist = new ArrayList<LabelText>();

                    for (int j = 0; j < parent.wordsToDisplayInWordle; j++) {
                        tmplist.add(parent.allLabels.get(treenode).get(j));
                    }
                    tmp.setLabels(tmplist);
                    
                    for (int i = 0; i < tmp.getLabels().size(); i++) {
                     tmp.wordCloudPanel.add(tmp.getLabels().get(i));
                    }
                      
                    
                    tmp.updateLayout();
                      
                    tmp.DrawWordleCloud(new Point(0, 0), tmp.getLabels());

                    if (treenode.getChildren().isEmpty()) {
                        tmp.setVisible(true);
                    } else {
                        tmp.setBorder(SystemPreferences.treemapNodeBorder);
                        tmp.setVisible(false);
                        tmp.setOpaque(false);

                    }

                    this.add(tmp);

                }
            }

        }
        
        
        
        if (clearFlag)
        for (TreeMapNodePanel tnp : nodePanels.values()) {
            this.add(tnp);

        }

        this.revalidate();
    }

}
