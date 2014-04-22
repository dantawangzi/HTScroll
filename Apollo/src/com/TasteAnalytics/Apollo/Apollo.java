package com.TasteAnalytics.Apollo;
import com.TasteAnalytics.Apollo.GUI.MinimalismMainFrame;
import java.awt.Color;
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
        mmframe.start();
        
         UIManager.put("ProgressBar.background", Color.WHITE);
        UIManager.put("ProgressBar.foreground", Color.BLACK);
        UIManager.put("ProgressBar.selectionBackground", Color.YELLOW); UIManager.put("ProgressBar.selectionForeground", Color.RED);
        UIManager.put("ProgressBar.shadow", Color.GREEN);
        UIManager.put("ProgressBar.highlight", Color.BLUE);
    }

}
