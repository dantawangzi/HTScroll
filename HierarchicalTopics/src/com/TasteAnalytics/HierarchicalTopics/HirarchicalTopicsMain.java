package com.TasteAnalytics.HierarchicalTopics;

import com.TasteAnalytics.HierarchicalTopics.gui.LoginFrame;
import com.TasteAnalytics.HierarchicalTopics.gui.MinimalismMainFrame;
import com.TasteAnalytics.HierarchicalTopics.gui.NetworkMetaInformation;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.*;

public class HirarchicalTopicsMain {

    /**
     * Main method which is called by the java interpreter. Basically displays
     * the window and returns.
     *
     * @param args the command line arguments (currently none available)
     */
   

    public static void main(String args[]) {
        //UIManager.put("org.mediavirus.parvis.gui.ParallelDisplayUI", "org.mediavirus.parvis.gui.BasicParallelDisplayUI");
        MinimalismMainFrame mmf;
        
        mmf = new MinimalismMainFrame();
        mmf.setVisible(false);

        boolean connectionSuccess = false;

        File loginKeyFile = new File(
                "./...key");

        if (loginKeyFile.exists()) {

            try {
                BufferedReader br = new BufferedReader(new FileReader(
                        loginKeyFile));

                NetworkMetaInformation.CookieUserString = br.readLine();
						NetworkMetaInformation.CookieKeyString = br.readLine();
                br.close();

            } catch (IOException e1) {
                e1.printStackTrace();
            }
            
//            System.out.println(mmf);
//            System.out.println(mmf.viewController);

            connectionSuccess = mmf.viewController.InitializeNetworkConnection(true, null, null);

            if (connectionSuccess) {
                System.out.println("cookie used success");
                mmf.setVisible(true);

                //ViewController.showMainDashboardFrame();
            } else {
                System.out.println("cookie file failed, try login..");
                File cookiefile = new File(
                        "./...key"
                );
                cookiefile.delete();
                System.out.println("Delete cookiefile");

                LoginFrame lf = new LoginFrame(mmf.viewController);

            }
        } else {

            LoginFrame lf = new LoginFrame(mmf.viewController);

        }

        //new MinimalismMainFrame().show();
    }

}
