/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.TasteAnalytics.Apollo.TreeMapView;

import com.explodingpixels.macwidgets.HudWindow;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Li Yu, Derek Wang
 * @version 1.0
 * @company Taste Analytics, LLC
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
        
        
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void mouseReleased(MouseEvent e) {
      


//  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void mouseEntered(MouseEvent e) {
        
        
        if (currentPanel.node.getChildren().isEmpty())
            currentPanel.mouseOvered = true;
          
        
        currentPanel.updateLayout();
          
            createHudWindow(e, mainFloatingHUDWindow);

    }

    public void mouseExited(MouseEvent e) {
        currentPanel.mouseOvered = false;
        currentPanel.updateLayout();
        
        mainFloatingHUDWindow.getJDialog().setVisible(false);
        
       // If exit the treemap region. The main floating HUDWindow should dipose to clean the screen
//        mainFloatingHUDWindow.getJDialog().dispose();
        
    }

    public void mouseDragged(MouseEvent e) {
       

// throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void mouseMoved(MouseEvent e) {
      
    }
    
        /**
     * Keep only one the mainFloatingHUDWindow
     * This HUDWindow is used to show the Icons and Images.
     */
    private static final HudWindow mainFloatingHUDWindow = new HudWindow();
    
    
    
    
     private void createHudWindow(MouseEvent e, HudWindow _defaultHudWindow) {
         
         //TODO: This is running slow. Need update. 
            _defaultHudWindow.hideCloseButton();
            _defaultHudWindow.getContentPane().removeAll();
            _defaultHudWindow.getJDialog().setFocusable(false);
            _defaultHudWindow.getJDialog().setFocusableWindowState(false);
            
            _defaultHudWindow.getJDialog().setFocusable(false);
            _defaultHudWindow.getJDialog().setAlwaysOnTop(true);
            _defaultHudWindow.getJDialog().setVisible(true);
//            _defaultHudWindow.getJDialog().setSize(new Dimension(500, 500));
            
            _defaultHudWindow.getJDialog().setTitle("Breakdown View");
            
            _defaultHudWindow.getJDialog().setPreferredSize(new Dimension(500, 500));
            _defaultHudWindow.getJDialog().setLocation(new Point(e.getLocationOnScreen().x, e.getLocationOnScreen().y));
            _defaultHudWindow.getJDialog().pack();
           
            
    }
}
