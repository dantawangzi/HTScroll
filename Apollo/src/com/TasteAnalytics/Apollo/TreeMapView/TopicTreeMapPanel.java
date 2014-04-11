/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.TasteAnalytics.Apollo.TreeMapView;

import com.TasteAnalytics.Apollo.GUI.ViewController;
import com.TasteAnalytics.Apollo.TemporalView.TreeNode;
import com.TasteAnalytics.Apollo.TopicRenderer.LabelText;
import com.TasteAnalytics.Apollo.TopicRenderer.TopicGraphViewPanel;
import com.TasteAnalytics.Apollo.Util.Colors;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
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
public class TopicTreeMapPanel extends JPanel{
    
    //private MapDisplay view;
    ViewController parent;
    private TreeModel model;
    private MapLayout algorithm;
    private Rect bounds;
    
    MapModel map;
    List<TreeNode> tree;
    
    HashMap<TreeNode,MapModel> NodeMap = new HashMap<TreeNode,MapModel>();
    HashMap<TreeNode,TreeMapNodePanel> nodePanel = new HashMap<TreeNode,TreeMapNodePanel>();

    public HashMap<TreeNode, TreeMapNodePanel> getNodePanel() {
        return nodePanel;
    }
         
    
    int wordsToDisplay = 50;
    
    
    public TopicTreeMapPanel(ViewController vc, List<TreeNode> tr, int w, int h)
    {
        
//        TopicTreeMapPanelInteractions interactions = new TopicTreeMapPanelInteractions();
//        addMouseListener(interactions);
//        addMouseMotionListener(interactions);
        
        this.setBackground(Colors.mainBackgroundColor);
   
        parent = vc;
        this.tree = tr;
        setSize(w,h);
        setPreferredSize(new Dimension(w,h));
        model = new TreeModel();
  
        
        TreeMapNodePanel root = new TreeMapNodePanel(vc, tree.get(0),tree.get(0).getLevel(), new Rectangle(0, 0, w, h));
        
        for (int i=0; i<tree.size(); i++)
        {
            if (tree.get(i).getValue().contains("L"))
            {
                int index = tree.get(i).getIndex();
                  tree.get(i).setNumberOfEvents(parent.topicEventsCount.get(index));
                
            }
            
        }
        
      
        
        tree.get(0).calculateTreeMapTopicWeight();
        
//        for (int i=0; i<tree.size(); i++)
//        {
//            System.out.println(tree.get(i).getValue() + " " + tree.get(i).getTreeMapTopicWeight());
//        // tree.get(i).size = tree.get(i).getTreeMapTopicWeight();
//        }
        
        
        updateTreeLayout(w,h);
        
        
//        tree.get(0).setMyRect(new Rectangle(0, 0, w, h));
//        Mappable[] leaves = tree.get(0).getItems();
//        map = new RandomMap(leaves);       	    
//	algorithm = new SquarifiedLayout();
//        
//	algorithm.layout(map, new Rect(0, 0, w, h));
//        
//        tree.get(0).calculateRect(new Rectangle(0, 0, w, h));
        setLayout(null);
           
           
        for (int i=1; i<tree.size();i++)
        {
         //Rect r = new Rect(tree.get(i).getMyRect().x,tree.get(i).getMyRect().y, tree.get(i).getMyRect().width, tree.get(i).getMyRect().height);   
         
           TreeMapNodePanel tmp = new TreeMapNodePanel(parent, tree.get(i),tree.get(i).getLevel(), tree.get(i).getMyRect());
           
           nodePanel.put(tree.get(i), tmp);
            if (tree.get(i).getChildren().isEmpty())
            {
                tmp.setVisible(true);
               // System.out.println(tree.get(i).getMyRect());
            }
            else
            {
                
                Border orangeLine = BorderFactory.createLineBorder(Color.white, 3);

                tmp.setBorder(orangeLine);
                tmp.setVisible(false);
                tmp.setOpaque(false);
                
                
                
            }
            this.add(tmp);
                
        }
       
        
        HashMap<TreeNode, List<LabelText>> allLabels = parent.topicGraphicPanel.getAllLabels();
        
        
        for (Map.Entry<TreeNode, List<LabelText>> entry : allLabels.entrySet()) {
                        
            
            TreeNode key = entry.getKey();
            
            List<LabelText> value = entry.getValue();

            if ( nodePanel.containsKey(key) )
            {
                List<LabelText> tmplist = new ArrayList<LabelText>();
                
                for (int i=0; i<wordsToDisplay; i++)
                {
                   tmplist.add(value.get(i));
                }
                                
                nodePanel.get(key).setLabels(tmplist);                  
                nodePanel.get(key).DrawWordleCloud(new Point(0,0), tmplist);
                
            }
            else
                System.out.println("does not contain this node " + key.toString());
                    
                    
                    
        }
        
        for (int i=0; i<tree.size(); i++)           
        {
            
            if (tree.get(i).getChildren().isEmpty())
            {
                for (int j=0; j<tree.size(); j++)
                {
                    TreeNode matchNode = parent.getTemporalFrame().getTree().get(j);
                    
                    if (matchNode.getValue().equals(tree.get(i).getValue()) )
                    {
                        tree.get(i).setArrayValue(matchNode.getArrayValue());                        
                        break;
                    }
                }
                                
                
            }
            
            
            
     
            
            
            
    
        
        
        
    
        }
    }
    
    
    
     public void updateTreeLayout(int w, int h)
    {
        
        tree.get(0).setMyRect(new Rectangle(0, 0, w, h));
        Mappable[] leaves = tree.get(0).getItems();
        map = new RandomMap(leaves);       	    
	algorithm = new SquarifiedLayout();
        
	algorithm.layout(map, new Rect(0, 0, w, h));
        
        tree.get(0).calculateRect(new Rectangle(0, 0, w, h));
        
        
        for (int i=0; i<tree.size(); i++)
        {
            if (nodePanel.containsKey(tree.get(i)))
            {
                TreeMapNodePanel tmp = nodePanel.get(tree.get(i));
                tmp.setMyRect(tree.get(i).getMyRect());
                tmp.updateLayout();
                //TreeMapNodePanel tmp = new TreeMapNodePanel(parent, tree.get(i),tree.get(i).getLevel(), tree.get(i).getMyRect());
                
                
            }
            
        }
        
        
        
        
        
        //if (model==null ) return;
        
    //    LayoutDifference measure=new LayoutDifference();
        //Mappable[] leaves=model.getTreeItems();
       // measure.recordLayout(leaves);
        //model.layout(algorithm, bounds);
       
//        for (int i=0; i<model.getItems().length; i++)
//        {
//            Mappable l = model.getItems()[i];
//            
//            TreeMapNodePanel tmpp = new TreeMapNodePanel(tree.get(leafOrder.get(i)));
//            
//            Rect rect = l.getBounds();
//            Border orangeLine = BorderFactory.createLineBorder(Color.orange);
//            //        mButtonPanel.setBorder(orangeLine);
//            tmpp.setBounds( (int)rect.x,  (int)rect.y,  (int)rect.w,  (int)rect.h);
//            tmpp.setBorder(orangeLine);
//           //tmpp.setBackground(Color.red);
//           
//            this.add(tmpp);
//            
//            
//        }
       
    }
    
     
     
     
    
    
    
    
    
    
}
