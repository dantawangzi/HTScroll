/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.TasteAnalytics.Apollo.TreeMapView;

import com.TasteAnalytics.Apollo.TemporalView.TreeNode;
import java.util.List;
import javax.swing.JPanel;

/**
 *
 * @author Li
 */
public class TopicTreeMapPanel extends JPanel{
    
    //private MapDisplay view;
    private TreeModel model;
    private MapLayout algorithm;
    private Rect bounds;
    
    
    public TopicTreeMapPanel(List<TreeNode> tree)
    {
        
        model = new TreeModel();
        for (int i=0; i<tree.size(); i++)
        {
            model.addChild(tree.get(i));
            
        }
        
        
        
        
        
        this.algorithm=algorithm;
        bounds=new Rect(0,0,1,1);
        
        
        
       //   LayoutDifference measure=new LayoutDifference();
        Mappable[] leaves = model.getTreeItems();
     //   measure.recordLayout(leaves);
        model.layout(algorithm, bounds);
        setLayout(null);
    
        
        
        
    }
    
    
     
     void updateLayout()
    {
        if (model==null ) return;
        
    //    LayoutDifference measure=new LayoutDifference();
        Mappable[] leaves=model.getTreeItems();
       // measure.recordLayout(leaves);
        model.layout(algorithm, bounds);
       
        
       
    }
    
    
    
    
    
    
    
}
