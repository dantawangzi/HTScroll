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
    
    
    /*
    Treemap Settings
    */
    
    /// Number of Sentiments
    /// Default to 2 to show Negative and Positive. 
    /// If change to 3 will show Negative, Netural, and Positive
    public static int numOfSentiments = 2;
    
    /// Tree Node Panel Color
    public static Border treemapNodeBorder = BorderFactory.createLineBorder(Color.white, 3);
    
}
