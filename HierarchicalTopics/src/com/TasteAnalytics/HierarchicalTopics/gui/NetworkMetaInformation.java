/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.TasteAnalytics.HierarchicalTopics.gui;

/**
 *
 * @author Lee
 */
public class NetworkMetaInformation {
    	public static String servername =  "192.168.0.17"; 
	
	//"caprica.uncc.edu";//"54.209.61.133";//"192.168.0.17";//
	
	// "10.18.202.126";//
	public static String serverport = "2012";
	public static String user = "";
	public static String password = "";

	public static String collection = "";

	public static String CookieString = "";
	public static String IncorrectCookieResponse = "these are not the droids you are looking for...";

		/**
	 * A class that host all the network meta information
	 */
	public NetworkMetaInformation() {
		
		// TODO Auto-generated constructor stub
	}
        
        
         private static final String OS = System.getProperty("os.name").toLowerCase();

    public static boolean isWindows() {
        System.out.println(OS.contains("win"));
        return (OS.contains("win"));
    }

    public static boolean isMac() {
        System.out.println(OS.contains("mac"));
        return (OS.contains("mac"));
    }

    public static boolean isUnix() {
        return (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"));
    }

    public static boolean isSolaris() {
        return (OS.contains("sunos"));
    }
	
}
