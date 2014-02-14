package com.TasteAnalytics.HierarchicalTopics;

import com.TasteAnalytics.HierarchicalTopics.gui.MinimalismMainFrame;

import javax.swing.*;


public class HirarchicalTopicsMain{


    /**
    * Main method which is called by the java interpreter. Basically displays the window and returns.
    *
    * @param args the command line arguments (currently none available)
    */
    public static void main (String args[]) {
       //UIManager.put("org.mediavirus.parvis.gui.ParallelDisplayUI", "org.mediavirus.parvis.gui.BasicParallelDisplayUI");
        new MinimalismMainFrame().show();
    }

}
