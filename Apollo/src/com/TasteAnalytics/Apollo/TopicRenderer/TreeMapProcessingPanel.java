/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TasteAnalytics.Apollo.TopicRenderer;

//import org.gicentre.treemappa.*;     // For treemappa classes
//import org.gicentre.utils.colour.*;
//import org.gicentre.treemappa.*;
//import org.gicentre.utils.colour.*;
import com.TasteAnalytics.Apollo.TemporalView.TreeNode;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.List;
import javax.swing.JFrame;
import processing.core.PApplet;
import processing.core.PFont;
import treemap.*;

/**
 *
 * @author Li
 */
public class TreeMapProcessingPanel extends JFrame {//JPanel{
    //PTreeMappa pTreeMappa; 

    int maxFontSize = 1000;
    int minFontSize = 1;

    PFont font;
    //Font font;
    Treemap map;
    Treemap map2;
    MapLayout layoutAlgorithm = new SquarifiedLayout();

    Embedded embed;
    BoundsIntegrator zoomBounds;

    NodeItem rootItem;
    LeafItem rolloverItem;
    NodeItem taggedItem;
    NodeItem zoomItem;

    public TreeMapProcessingPanel(List<TreeNode> tree) {
        font = new PFont();//"Sans-Serif", Font.PLAIN, 12);;
        ;
        this.setPreferredSize(new Dimension(1000, 1000));
        this.setSize(new Dimension(1000, 1000));
        embed = new Embedded(tree, this);

        embed.init();

        add(embed, BorderLayout.CENTER);

    }

    public class Embedded extends PApplet {

        List<TreeNode> myTree;
        TreeMapProcessingPanel parentTreeMap;

        Embedded(List<TreeNode> tree, TreeMapProcessingPanel tmpp) {
            myTree = tree;
            parentTreeMap = tmpp;

        }

        @Override
        public void setup() {

            size(1024, 768);
            width = 1024;
            height = 768;
//
////            WordMap mapData = new WordMap(0);
////            
////            WordMap mapData2 = new WordMap(1);
////            String[] words = {"abc", "bcd", "qqq", "ddd", "eee"};
////            
////            mapData2.addWord(new WordItem(2),2);
////            mapData2.addWord(new WordItem(2),2);
////            mapData2.addWord(new WordItem(2),2);
////            mapData2.finishAdd();
////                        
////            
////            mapData.addWord(new WordItem(1),1);
////            
//////            for (int i = 0; i < 1; i++) {
//////                // translate all to UPPERCASE
//////                String word = words[i].toLowerCase();
//////                mapData.addWord(new WordItem(0),0);
//////            }
////
////            
////            mapData.addWord(mapData2,0);
////            
////            
////            mapData.finishAdd();
//
//            // create treemap with mapData
//            map = new Treemap(mapData, 0, 0, 500, 500);
//        //map2 = new Treemap(mapData2, 0, 0, 100, 100);
////            zoomBounds = new BoundsIntegrator(0, 0, width, height);
////  
////             cursor(CROSS);
////            rectMode(CORNERS);
////            
////
////  
////            font = createFont("SansSerif", 13);
////            
////                     // map = new Treemap(null, 0, 0, width, height);
////                      
////            selectRoot();        
////                      
////                     
////            //smooth();
////           // noLoop();
        }

        void selectRoot() {

  //SwingUtilities.invokeLater(new Runnable() {
            // public void run() {
            setRoot(myTree);

    //}
            //});
        }

        void setRoot(List<TreeNode> tree) {

            NodeItem tm = new NodeItem(null, 0, 0, parentTreeMap);

            HashMap<Integer, NodeItem> nodemap = new HashMap<Integer, NodeItem>();
            HashMap<Integer, LeafItem> leafmap = new HashMap<Integer, LeafItem>();

            for (int i = 0; i < tree.size(); i++) {
                LeafItem li = null;
                if (tree.get(i).getChildren().isEmpty()) {
                    int index = tree.get(i).getIndex();
                    li = new LeafItem(null, tree.get(i).getLevel(), i, parentTreeMap);
                    leafmap.put(index, li);
                } else {
                    int index = tree.get(i).getIndex();
                    li = new NodeItem(null, tree.get(i).getLevel(), i, parentTreeMap);
                    nodemap.put(index, (NodeItem) li);
                }

            }

       //     tm.setTree(tree.get(0), nodemap, leafmap);
            tm.setBounds(0, 0, width, height);
            tm.contentsVisible = true;

   //         rootItem = tm;
       // rootItem.zoomIn();
            // rootItem.updateColors();

        }

        @Override
        public void draw() {
//            background(255);
//            background(255);
//            map.setLayout(layoutAlgorithm);
//            map.updateLayout();
//            map.draw();
//            noLoop();

            background(255);
            map.setLayout(layoutAlgorithm);
            map.updateLayout();
            map.draw();

     
//  
//  
//            background(0);
////            textFont(font);
////
//            frameRate(30);
//            zoomBounds.update();
////            
////            rolloverItem = null;
////            taggedItem = null;
//
//            if (rootItem != null) {
//              rootItem.draw();
//            }
//            fill(color(100,100,101,200));
//            rect(0, 0,70,100  );
//            //rect(rootItem.boxLeft,rootItem.boxTop, rootItem.boxRight,rootItem.boxBottom  );
////            if (rolloverItem != null) {
////              rolloverItem.drawTitle();
////            }
////            if (taggedItem != null) {
////              taggedItem.drawTag();
////            }
//          
//
//  // Get treemappa to draw itself.
//            // pTreeMappa.draw();
//        
        }

        @Override
        public void mousePressed() {
//            if (zoomItem != null) {
//              zoomItem.mousePressed();
//            }
        }

        @Override
        public void keyReleased() {

            // set layout algorithm
            if (key == '1') {
                layoutAlgorithm = new SquarifiedLayout();
            }
            if (key == '2') {
                layoutAlgorithm = new PivotBySplitSize();
            }
            if (key == '3') {
                layoutAlgorithm = new SliceLayout();
            }
            if (key == '4') {
                layoutAlgorithm = new OrderedTreemap();
            }
            if (key == '5') {
                layoutAlgorithm = new StripTreemap();
            }

            if (key == '1' || key == '2' || key == '3' || key == '4' || key == '5'
                    || key == 's' || key == 'S' || key == 'p' || key == 'P') {
                loop();
            }
        }
        
        
        
        

    }


}
