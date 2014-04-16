/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.TasteAnalytics.Apollo.TemporalView;

import com.TasteAnalytics.Apollo.GUI.ViewController;
import com.TasteAnalytics.Apollo.datahandler.CategoryBarElement;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lyu8
 */


public class TemporalBarChart {
    
    
    ViewController parent;
    
    TreeNode node;
    
    int numoftimeslots;
    List<Float> dataPoints = new ArrayList<Float>();
    
    private CategoryBarElement data;
    
    
    TemporalBarChart(ViewController vc, TreeNode ct)
    {
        parent = vc;
        node = ct;
        

    }
    
    int rectwidth;
    int rectheight;
    
    
    void draw(Graphics g, Rectangle2D area)
    {
        Graphics2D g2 = (Graphics2D) g;
        
        for (int i=0; i<node.getArrayValue().size(); i++)
        {
            double width = area.getWidth();
            double height = area.getHeight();
            
            double x = area.getX();
                    
            g2.setColor(node.getBaseColor());
            g2.draw(new Rectangle2D.Double(x + i*6, node.getArrayValue().get(i),
                               5, node.getArrayValue().get(i)));
            
        }
        
    }
    
    
    
}
