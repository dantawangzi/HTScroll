/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.TasteAnalytics.Apollo.Util;

import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.border.Border;

/**
 *
 * @author derekwang
 */
public class SystemPreferences {
    
    /*   System Settings  */
    public static boolean isNormalizationNecessary = true;
    
    /*   Treemap Settings  */
    
    /// Number of Sentiments
    /// Default to 2 to show Negative and Positive. 
    /// If change to 3 will show Negative, Netural, and Positive
    public static int numOfSentiments = 2;
    
    public static int sentimentBarHeight = 12; // 12 pixel for the height
   
    
    public static Color positiveColor = new Color(160, 170, 105);
    public static Color negativeColor = new Color(174, 86, 80);
    
    /// Tree Node Panel Color
    public static Border treemapNodeBorder = BorderFactory.createLineBorder(Color.white, 3);
    
    public static int topicListPanelMinWidth = 120;
    public static int treemapBorderWidth = 4;
    
    public static Color mainColor = new Color(0,52,128, 128);
    
    
    /*Timeline Selection Panel*/
    public static boolean timecolumn = true;
    
}
