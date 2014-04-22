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
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JPanel;

/**
 *
 * @author Li
 */
public class TopicTreeMapPanel extends JPanel {

    //private MapDisplay view;
    public static ViewController parent;
//    private final TreeModel model;
    private MapLayout algorithm;
//    private Rect bounds;

    public int treemapPanelWidth = 0;
    public int treemapPanelHeight = 0;

    MapModel map;
    List<TreeNode> tree;

    HashMap<TreeNode, MapModel> NodeMap = new HashMap<TreeNode, MapModel>();
    HashMap<TreeNode, TreeMapNodePanel> nodePanels = new HashMap<TreeNode, TreeMapNodePanel>();

    public HashMap<TreeNode, TreeMapNodePanel> getNodePanel() {
        return nodePanels;
    }

    public TopicTreeMapPanel(ViewController vc, List<TreeNode> tr, int w, int h) throws IOException {

        this.setBackground(Colors.mainBackgroundColor);

        treemapPanelWidth = w;
        treemapPanelHeight = h;

        parent = vc; //Double Binding
        this.tree = tr;
        this.setSize(w, h);
        this.setPreferredSize(new Dimension(w, h));
        this.setLayout(null);

        tree.get(0).setMyRect(new Rectangle(0, 0, w, h));
//        model = new TreeModel();

        Mappable[] leaves = tree.get(0).getItems();
        map = new RandomMap(leaves);
        algorithm = new SquarifiedLayout();
        algorithm.layout(map, new Rect(0, 0, w, h));
        tree.get(0).calculateRect(new Rectangle(0, 0, w, h));

        List<LabelText> tmplist = new ArrayList<LabelText>();
        for (int i = 1; i < tree.size(); i++) {
            TreeMapNodePanel tmp = new TreeMapNodePanel(parent, tree.get(i), tree.get(i).getLevel(), tree.get(i).getMyRect(), parent.getTreemapMiniTemporal().get(tree.get(i)));
            nodePanels.put(tree.get(i), tmp);
            
            // TODO: HIGH We shoud optimize memory usable here. We don't need to make a copy of all labels!
            tmp.list = ViewController.allLabelInWordle.get(tree.get(i));

            tmplist.clear();

            for (int j = 0; j < parent.wordsToDisplayInWordle; j++) {
                tmplist.add(ViewController.allLabels.get(tree.get(i)).get(j));
            }

            tmp.setLabelsFromList(tmplist);

            //tmp.DrawWordleCloud(new Point(0, 0), tmplist);
//            PrefuseWordleLayout ppp = new PrefuseWordleLayout(parent, tree.get(i));
//            tmp.setLabelBounds(ppp.DrawWordleCloud(tmp.wordCloudPanel.getBounds()));
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

        if (clearFlag) {
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

                    //tmp.getLabels()
                    tmp.setLabelsFromList(tmplist);

//                    for (int i = 0; i < tmp.getLabels().size(); i++) {
//                        tmp.wordCloudPanel.add(tmp.getLabels().get(i));
//                    }
//                    PrefuseWordleLayout ppp = new PrefuseWordleLayout(parent, treenode);
//                    tmp.setLabelBounds(ppp.DrawWordleCloud(tmp.wordCloudPanel.getBounds()));
                    tmp.updateLayout();

                    //tmp.DrawWordleCloud(new Point(0, 0), nodePanels.get(treenode).getLabels());
                    //TreeMapNodePanel timeviewPanel = new TreeMapNodePanel(parent, tree.get(i),tree.get(i).getLevel(), tree.get(i).getMyRect());
                } else {

                    TreeMapNodePanel tmp = new TreeMapNodePanel(parent, treenode, treenode.getLevel(), treenode.getMyRect(), parent.getTreemapMiniTemporal().get(treenode));
                    nodePanels.put(treenode, tmp);
                    tmp.list = parent.allLabelInWordle.get(treenode);
                    tmp.setMyRect(treenode.getMyRect());

                    List<LabelText> tmplist = new ArrayList<LabelText>();

                    for (int j = 0; j < parent.wordsToDisplayInWordle; j++) {
                        tmplist.add(parent.allLabels.get(treenode).get(j));
                    }
                    tmp.setLabelsFromList(tmplist);

                    // TODO: Enable Wordle If Neccessary
//                    for (int i = 0; i < tmp.getLabels().size(); i++) {
//                        tmp.wordCloudPanel.add(tmp.getLabels().get(i));
//                    }
//                    //tmp.DrawWordleCloud(new PoitimeviewPanel0, 0), tmp.getLabels());
//                    PrefuseWordleLayout ppp = new PrefuseWordleLayout(parent, treenode);
//                    tmp.setLabelBounds(ppp.DrawWordleCloud(tmp.wordCloudPanel.getBounds()));
                    tmp.updateLayout();

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

        if (clearFlag) {
            for (TreeMapNodePanel tnp : nodePanels.values()) {
                this.add(tnp);
            }
        }

        this.revalidate();
        this.repaint();
        // TODO: Li, we need to rapint this! And this fix your revalidation issue
    }

}
