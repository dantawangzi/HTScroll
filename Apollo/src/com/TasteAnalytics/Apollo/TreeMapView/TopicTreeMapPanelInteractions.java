/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.TasteAnalytics.Apollo.TreeMapView;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lyu8
 */
public class TopicTreeMapPanelInteractions implements MouseListener, MouseMotionListener{
    
    
    TreeMapNodePanel currentPanel;
    TopicTreeMapPanelInteractions(TreeMapNodePanel p)
    {
        currentPanel = p;
        
        
    }

    
    
    public void mouseClicked(MouseEvent e) {
        
        
        try {
            currentPanel.parent.addThemeRiver(currentPanel.node);
            
            
            
            
            //build new theme river in temporal frame
            
            
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        } catch (IOException ex) {
            Logger.getLogger(TopicTreeMapPanelInteractions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void mousePressed(MouseEvent e) {
        
        
        
        
        //
        
        
        
        
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void mouseReleased(MouseEvent e) {
      


//  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void mouseEntered(MouseEvent e) {
        
        
        if (currentPanel.node.getChildren().isEmpty())
            currentPanel.mouseOvered = true;
          
        
        currentPanel.updateLayout();
          
          
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void mouseExited(MouseEvent e) {
        currentPanel.mouseOvered = false;
        currentPanel.updateLayout();
        
        
    }

    public void mouseDragged(MouseEvent e) {
       

// throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void mouseMoved(MouseEvent e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
