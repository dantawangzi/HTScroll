package com.TasteAnalytics.Apollo;

import com.TasteAnalytics.HierarchicalTopics.gui.MinimalismMainFrame;

import javax.swing.*;


public class Apollo{


    /**
    * Main method which is called by the java interpreter. Basically displays the window and returns.
    *
    * @param args the command line arguments (currently none available)
    */
    public static void main (String args[]) {
       
        MinimalismMainFrame mmframe = new MinimalismMainFrame();
        mmframe.setVisible(true);
        mmframe.setExtendedState(mmframe.getExtendedState()|JFrame.MAXIMIZED_BOTH);
   
    }

}
