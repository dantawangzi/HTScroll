/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TasteAnalytics.Apollo.TemporalView;

import java.awt.Point;
import java.awt.geom.Point2D;
import javax.swing.JFrame;

/**
 *
 * @author Li
 */
public class ZoomedTemporalFrame extends JFrame {
    
    public ZoomedTemporalFrame(Point location, int width, int height, ZoomedTemporalViewPanel ztvp) {
        this.setLocation((Point) location);
        this.setSize(width, height);
        //this.add(ztvp);
         //this.setUndecorated(true);
        this.setContentPane(ztvp);  
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
    }
}
