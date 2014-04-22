/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TasteAnalytics.Apollo.TopicRenderer;

import au.com.bytecode.opencsv.CSVReader;
import com.TasteAnalytics.Apollo.GUI.ViewController;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import de.fhpotsdam.unfolding.*;
import de.fhpotsdam.unfolding.geo.*;
import de.fhpotsdam.unfolding.marker.AbstractMarker;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.MarkerManager;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.*;
import de.fhpotsdam.unfolding.utils.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;

/**
 *
 * @author Li
 */
public class WorldMapProcessingPanel extends JPanel {

    ViewController parent;
    List<Color> geoLocationColors = new ArrayList<Color>();
  

//    List<Point2D> allLocations;
    List<Integer> counts = new ArrayList<Integer>();

    int countMax = -1;
    Embedded embed;
    
    HashMap<String, tweetData> tweets = new HashMap<String, tweetData>();
    HashMap<String, List<tweetData>> tweetsPoster = new HashMap<String,  List<tweetData>>();
     
    
    HashMap<Point2D, List<String> > tweetsGroupedLocation = new HashMap<Point2D,  List<String>>();
    
    
    HashMap<Integer, Point2D> storeLocations;
 
    HashMap<String, Point2D> alltweetLocations = new HashMap<String,Point2D>();
 
 
    public WorldMapProcessingPanel(ViewController vc, List<Point2D> L, Point2D center) {
        //super("Embedded PApplet");
        super();

        parent = vc;

        setLayout(new BorderLayout());
        embed = new Embedded(L, center);

        //allLocations = L;
        add(embed, BorderLayout.CENTER);
        embed.init();

    }
    public  WorldMapProcessingPanel() {
        super();
    }

    public WorldMapProcessingPanel(ViewController vc, List<HashMap> M, int w, int h) throws IOException {
        //super("Embedded PApplet");
        super();
        parent = vc;

        setLayout(new BorderLayout());
        List<Point2D> L = new ArrayList<Point2D>();

        float max_x = -9999999.0f;  // lng
        float max_y = -9999999.0f;  // lat
        float min_x = 9999999.0f;
        float min_y = 9999999.0f;

        if (M.size()>1)
        for (int i = 0; i < M.size(); i++) {
            
            if (M.get(i).containsKey("latitude"))
            {
                float lat = Float.parseFloat((String) M.get(i).get("latitude"));
                float lng = Float.parseFloat((String) M.get(i).get("longitude"));

                L.add(new Point2D.Float(lat, lng));
    //
                if (lat >= max_y) {
                    max_y = lat;
                }
                if (lat <= min_y) {
                    min_y = lat;
                }

                if (lng >= max_x) {
                    max_x = lng;
                }
                if (lng <= min_x) {
                    min_x = lng;
                }

                if (M.get(i).containsKey("count")) {

                    counts.add((Integer) M.get(i).get("count"));

                    if (counts.get(i) >= countMax) {
                        countMax = counts.get(i);
                    }
                }
            }
//            else if (M.get(i).containsKey("geo"))
//            {
//                BasicDBList dbl = (BasicDBList)M.get(i).get("geo");
//                if (dbl.size() != 0)
//                {
//                    for (int j=0; j<dbl.size(); j++)
//                    System.out.println(((HashMap)dbl.get(i)).get("geo"));
//                }
//                
//                
//            }
        }
        
        
        //LoadAllData();

        Point2D center = new Point2D.Double((min_y + max_y) / 2, (min_x + max_x) / 2);
//        System.out.println( M.size());
//        System.out.println( L.size());
        //System.out.println(center.getX() + " " + center.getY());
      
         CSVReader storeLocationReader = null;
        try {
            storeLocationReader = new CSVReader(new FileReader("lowesStoreLatLong.csv"));
            storeLocationReader.readNext();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WorldMapProcessingPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        List<String[]> tmpData = storeLocationReader.readAll();
         
       storeLocations = new HashMap<Integer, Point2D>();
        
        for (int i=0; i<tmpData.size(); i++)
        {
            String[] s = tmpData.get(i);
            
            int storeindex = Integer.parseInt(s[0]);
            float lon = Float.parseFloat( s[2]);
            float lat = Float.parseFloat( s[3]);
            
            Point2D p = new Point2D.Float(lat, lon);
            
            storeLocations.put(storeindex, p);
            
        }
        
         for (tweetData td : tweets.values())
         {
             
             Point2D p = new Point2D.Float(td.lat, td.lon);
             alltweetLocations.put(td._id,p);
         }
        
        
        
        
        

        embed = new Embedded(L, center);

        
        embed.init();
        add(embed, BorderLayout.CENTER);

    }
    
    
    
    
    void LoadAllData()
    {
        MongoClient mongoClient = null;
        try {
                mongoClient = new MongoClient("10.18.203.211", 27017);
            } catch (UnknownHostException ex) {
                System.out.println("DB begin load cache error");
            }

        DB db = mongoClient.getDB("lowes");
            
        db.authenticate("li", "li_lowes_user".toCharArray());
        DBCollection currentColl = db.getCollection("lowes_tweets_job");
        DBCursor cursor = null;
        
        DBObject dbo = null;
        
        
        BasicDBObject query = new BasicDBObject("latitude", new BasicDBObject("$exists",true));
        BasicDBObject fields = new BasicDBObject();
//
        fields.put("latitude", true);
        fields.append("longitude", true);
        fields.append("pos", true);
        fields.append("neg", true);
        fields.append("text", true);
        fields.append("poster", true);
        fields.append("labels", true);
        int count = 0; 
            
        
        cursor = currentColl.find(query, fields);
        while(cursor.hasNext()) {
           DBObject o = cursor.next();
            count ++ ;
            
            float t1 = Float.parseFloat((String) o.get("latitude"));
            float t2 = Float.parseFloat((String) o.get("longitude"));
            int p = Integer.parseInt(String.valueOf((o.get("pos"))));
            int n = Integer.parseInt(String.valueOf((o.get("neg"))));
            String te = (String) o.get("text");
            String id = (String) o.get("_id");
            String poster = (String) o.get("poster");
             
            String label = (String) o.get("labels");
            tweetData td = new tweetData(t1,t2,id,te,p,n, label.toLowerCase());
            tweets.put(id, td);
            //tweetsPoster.put(poster, null);
            
            Point2D loc = new Point2D.Float(t1, t2);
            
            
            if (tweetsGroupedLocation.containsKey(loc))
            {
                List<String> ss = tweetsGroupedLocation.get(loc);
                ss.add(id);
                tweetsGroupedLocation.put(loc, ss);
            }
            else
            {
                List<String> ss = new ArrayList<String>();
                ss.add(id);
                tweetsGroupedLocation.put(loc, ss);
                
            }
            
            
          
            
            
            
            
            
           //System.out.println(o.toString());
        }
        
         
//            cursor = currentColl.find(query, fields);
//        
//             count = 0; 
//            
//            while (cursor.hasNext())
//            {
//                count ++ ;
//                      dbo = (DBObject) cursor.next();
//                   //   System.out.println(dbo.toString());
//                     
//                
//            }
//            
//            System.out.println();
            
            
            mongoClient.close();
        //DBObject query = new DBObject();
        
        
        
        
        
        
        
        
    }
    
    public void updateSelectedTweet(HashMap m)
    {
        float lat = Float.parseFloat((String)m.get("latitude"));
        float lng = Float.parseFloat((String)m.get("longitude"));
        
        embed.updateSelectedMarker(lat,lng);
    }
//

    public void UpdateGeoLocationsFromDoc(Color c, List<HashMap> M) {
        geoLocationColors.add(c);

        List<Point2D> L = new ArrayList<Point2D>();

        float max_x = -9999999.0f;  // lng
        float max_y = -9999999.0f;  // lat
        float min_x = 9999999.0f;
        float min_y = 9999999.0f;

        for (int i = 0; i < M.size(); i++) {

            if ( M.get(i).containsKey("latitude"))
                continue;
            
            float lat = Float.parseFloat((String) M.get(i).get("latitude"));
            float lng = Float.parseFloat((String) M.get(i).get("longitude"));

            L.add(new Point2D.Float(lat, lng));

            if (lat >= max_y) {
                max_y = lat;
            }
            if (lat <= min_y) {
                min_y = lat;
            }

            if (lng >= max_x) {
                max_x = lng;
            }
            if (lng <= min_x) {
                min_x = lng;
            }

        }

        List<SimplePointMarker> tmp = new ArrayList<SimplePointMarker>();
        for (int i = 0; i < L.size(); i++) {

            Location temp = new Location(L.get(i).getX(), L.get(i).getY());

            SimplePointMarker tempMarker = new SimplePointMarker(temp);
            
            tmp.add(tempMarker);

        }
        

        embed.updateMarkers(geoLocationColors, tmp);

    }

    public class Embedded extends PApplet {
        
        
        HashMap<Integer, ImageMarker> storeMarker = new HashMap<Integer, ImageMarker>() ;
        
        
        MyMarker selectedMark;

        UnfoldingMap mapDetail;
        // Small map showing the overview, i.e. the world
//        UnfoldingMap mapOverviewStatic;
        // Interactive finder box atop the overview map.
        //ViewportRect viewportRect;
        Location center;
        List<Marker> Markers;

        float oldWidth;
        float oldHeight;

        MarkerManager<Marker> markerManager = new MarkerManager<Marker>();

        private Embedded(List<Point2D> L, Point2D c) {

            //List<Location> Locations = new ArrayList<Location>();
            Markers = new ArrayList<Marker>();
            center = new Location(c.getX(), c.getY());
            for (int i = 0; i < L.size(); i++) {
                Location temp = new Location(L.get(i).getX(), L.get(i).getY());

                SimplePointMarker tempMarker = new SimplePointMarker(temp);
                Markers.add(tempMarker);

            }

        }

        @Override
        public void setup() {

            // original setup code here ...
            //size(800,800);
            size(1600, 1000);
            
                 PImage p = loadImage("logo_lowes_xxxsmall.png");
            
            for (Iterator<java.util.Map.Entry<Integer, Point2D>> it = storeLocations.entrySet().iterator(); it.hasNext();) {
                java.util.Map.Entry<Integer, Point2D> entry = it.next();
                
                int  key = entry.getKey();
                Point2D value = entry.getValue();
                
                ImageMarker im = new ImageMarker(new Location(value.getX(), value.getY()) ,p);
                storeMarker.put(key, im);
                markerManager.addMarker(im);
            }
            
//            int count = 0;
//            for (Iterator<java.util.Map.Entry<String, Point2D>> it = alltweetLocations.entrySet().iterator(); it.hasNext();) 
//            {
//                java.util.Map.Entry<String, Point2D> entry = it.next();
//                
//                String  key = entry.getKey();
//                Point2D value = entry.getValue();
//                SimplePointMarker ma = new SimplePointMarker(new Location(value.getX(),value.getY()));
//                
//                int pos = tweets.get(key).pos;
//                int neg = tweets.get(key).neg;
//                
//                if ((pos+neg) < 0 )
//                {
//                    //ma.setColor(color(224,12,18, 50));
////                if ( (pos+neg) < 0)    
//                    ma.setColor(color(44, 127, 184, 50));
////                else
////                    ma.setColor(color(80, 80, 80, 50));
//                
//                ma.setRadius(5.0f);
//                ma.setStrokeWeight(0);
//                //ma.setStrokeColor(color(44, 127, 184, 0));
//                markerManager.addMarker(ma);
//                count++;
//                }
////                if (count >=20099 )
////                    break;
//            }
            
            
            
            int countpos = 0;
            int countneg = 0;
            int countneut = 0;
            List<Float> negvalues = new ArrayList<Float>();
            List<Float> posvalues = new ArrayList<Float>();
            List<Point2D> locss = new ArrayList<Point2D>();
            List<Integer> tweetCounts = new ArrayList<Integer>();
            int min_neg = 10;
            Point2D min_negPoint = new Point2D.Float(0.0f,0.0f);
            int count = 0;      
            
            
            if (false)
            {
            for (Iterator<java.util.Map.Entry<Point2D, List<String>>> it = tweetsGroupedLocation.entrySet().iterator(); it.hasNext();) 
            {
                java.util.Map.Entry<Point2D, List<String>> entry = it.next();
                
                Point2D  key = entry.getKey();
                List<String> value = entry.getValue();
                
                SimplePointMarker ma = new SimplePointMarker(new Location(key.getX(),key.getY()));
                //ma.setRadius(value.size());
                int sumofpos = 0;
                int sumofneg = 0;
                
                //System.out.println(value.size());
                
                int count2 = 0;
                for (int i=0; i<value.size(); i++)
                {
                    String label = tweets.get(value.get(i)).label;
                    
                    if  ( label.contains("home") ||label.contains("depot") )
                    {    
                        count++;
                        count2++;
                        sumofpos += tweets.get(value.get(i)).pos;
                        sumofneg += tweets.get(value.get(i)).neg;
                        
                        if ((tweets.get(value.get(i)).pos + tweets.get(value.get(i)).neg)>0)
                            countpos++;
                        else if ((tweets.get(value.get(i)).pos + tweets.get(value.get(i)).neg)<0)
                            countneg++;
                        else
                            countneut ++;
           
                    }
                }
                
                negvalues.add((float)sumofneg/(float)count2);
                posvalues.add((float)sumofpos/(float)count2);
                locss.add(key);
                tweetCounts.add(count2);
                
                
        
//                int pos = tweets.get(key).pos;
//                int neg = tweets.get(key).neg;
//                
                if ((sumofpos+sumofneg) > 0 )
                {
                    
                    ma.setColor(color(153,195,25,100));
                    
                }
                else                
                    if ( (sumofpos+sumofneg) < 0)    
                    {
                      //    continue;
                      ma.setColor(color(234, 0, 27, 50));
                    }
                    
                else
                {
                    continue;
                  //  ma.setColor(color(0, 0, 0, 0));
                  //  ma.setStrokeWeight(0);
                }
               
//ma.setColor(color(224,12,18, 50)); 
                if (value.size()>20)
                {
                    ma.setRadius(25);
                }else
                    ma.setRadius(value.size());
                
                ma.setStrokeWeight(0);
                
                markerManager.addMarker(ma);
           
                //count++;

            
            }
            
            
            System.out.println(count);
            
            System.out.println(countpos + " " + countneg + " " + countneut);
            //System.out.print(min_negPoint);
            
            int sumOftweets = 0;
            for (int i=0; i<tweetCounts.size(); i++)
            {
                sumOftweets += tweetCounts.get(i);
            }
            
            float averageTweets = sumOftweets/tweetCounts.size();
            if (averageTweets <= sumOftweets*0.01)
                averageTweets =  (int)(sumOftweets*0.01);
           // averageTweets =200;
            
            int negidx=-1, negidx2=-1, negidx3=-1, negidx4=-1, negidx5=-1;;
            int posidx=-1, posidx2=-1, posidx3=-1, posidx4=-1, posidx5=-1;
            float posmax = -999, posmax2 = -999, posmax3 = -999, posmax4 = -999, posmax5 = -999;
            float negmin = 100, negmin2 = 100, negmin3 = 100, negmin4 = 100, negmin5 = 100;
            Point2D posmaxL = null, posmaxL2 = null, posmaxL3 = null,posmaxL4 = null, posmaxL5 = null;
            Point2D negminL = null, negminL2 = null, negminL3 = null, negminL4 = null, negminL5 = null;
            
            int pos1count = -1,pos2count = -1,pos3count = -1, pos4count = -1,pos5count = -1;
            int neg1count = -1,neg2count = -1,neg3count = -1,neg4count = -1,neg5count = -1;
            
            for (int i=0; i<posvalues.size();i++)
            {
                if (tweetCounts.get(i)>=averageTweets)
                if (posvalues.get(i)>=posmax )
                {
                    posmax = posvalues.get(i);
                    posmaxL = locss.get(i);
                    pos1count = tweetCounts.get(i);
                }
                
                
            }
            
            for (int i=0; i<posvalues.size();i++)
            {
                if (tweetCounts.get(i)>=averageTweets)
                if (posvalues.get(i)>=posmax2 && posvalues.get(i)!=posmax)
                {
                    posmax2 = posvalues.get(i);
                    posmaxL2 = locss.get(i);
                    pos2count = tweetCounts.get(i);
                }
                
            }
            
            for (int i=0; i<posvalues.size();i++)
            {
                if (tweetCounts.get(i)>=averageTweets)
                  if (posvalues.get(i)>=posmax3 && posvalues.get(i)!=posmax && posvalues.get(i)!=posmax2)
                {
                    posmax3 = posvalues.get(i);
                    posmaxL3 = locss.get(i);
                    pos3count = tweetCounts.get(i);
                }
                
            }
            
            for (int i=0; i<posvalues.size();i++)
            {
                if (tweetCounts.get(i)>=averageTweets)
                  if (posvalues.get(i)>=posmax4 && posvalues.get(i)!=posmax && posvalues.get(i)!=posmax2 && posvalues.get(i)!=posmax3)
                {
                    posmax4 = posvalues.get(i);
                    posmaxL4 = locss.get(i);
                    pos4count = tweetCounts.get(i);
                }
                
            }
            
            for (int i=0; i<posvalues.size();i++)
            {
                if (tweetCounts.get(i)>=averageTweets)
                  if (posvalues.get(i)>=posmax5 && posvalues.get(i)!=posmax && posvalues.get(i)!=posmax2 && posvalues.get(i)!=posmax3 && posvalues.get(i)!=posmax4)
                {
                    posmax5 = posvalues.get(i);
                    posmaxL5 = locss.get(i);
                    pos5count = tweetCounts.get(i);
                }
                
            }
            
            System.out.println(posmax + " " + posmax2 + " " +posmax3+ " " +posmax4+ " " +posmax5);
            System.out.println(posmaxL + " " + posmaxL2 + " " +posmaxL3 + " " +posmaxL4 + " " +posmaxL5);
            System.out.println(pos1count + " " + pos2count + " " +pos3count + " " +pos4count + " " +pos5count);
            
            
            
             for (int i=0; i<negvalues.size();i++)
            {
                if (tweetCounts.get(i)>=averageTweets)
                if (negvalues.get(i)<=negmin)
                {
                    negmin = negvalues.get(i);
                    negminL = locss.get(i);
                    neg1count = tweetCounts.get(i);
                }
                
                
            }
            
            for (int i=0; i<negvalues.size();i++)
            {
                if (tweetCounts.get(i)>=averageTweets)
                if (negvalues.get(i)<=negmin2 && negvalues.get(i)!=negmin)
                {
                    negmin2 = negvalues.get(i);
                    negminL2 = locss.get(i);
                    neg2count = tweetCounts.get(i);
                }
                
            }
            
            for (int i=0; i<negvalues.size();i++)
            {
                if (tweetCounts.get(i)>=averageTweets)
                if (negvalues.get(i)<=negmin3 && negvalues.get(i)!=negmin && negvalues.get(i)!=negmin2)
                {
                    negmin3 = negvalues.get(i);
                    negminL3 = locss.get(i);
                    neg3count = tweetCounts.get(i);
                }
                
            }
            
             for (int i=0; i<negvalues.size();i++)
            {
                if (tweetCounts.get(i)>=averageTweets)
                if (negvalues.get(i)<=negmin4 && negvalues.get(i)!=negmin && negvalues.get(i)!=negmin2 && negvalues.get(i)!=negmin3)
                {
                    negmin4 = negvalues.get(i);
                    negminL4 = locss.get(i);
                    neg4count = tweetCounts.get(i);
                }
                
            }
             
             for (int i=0; i<negvalues.size();i++)
            {
                if (tweetCounts.get(i)>=averageTweets)
                if (negvalues.get(i)<=negmin5 && negvalues.get(i)!=negmin && negvalues.get(i)!=negmin2 && negvalues.get(i)!=negmin3  && negvalues.get(i)!=negmin4)
                {
                    negmin5 = negvalues.get(i);
                    negminL5 = locss.get(i);
                    neg5count = tweetCounts.get(i);
                }
                
            }
             
            System.out.println(negmin + " " + negmin2 + " " +negmin3+ " " +negmin4+ " " +negmin5);
            System.out.println(negminL + " " +negminL2 + " " +negminL3+ " " +negminL4+ " " +negminL5);
            System.out.println(neg1count + " " + neg2count + " " +neg3count + " " + neg4count + " " +neg5count);
            
            
            //this.frame.setResizable(redraw);
            //  frame.setResizable(true);

             //new MapBox.MapBoxProvider());//
            
            }
            
            mapDetail = new UnfoldingMap(this, new StamenMapProvider());//new Microsoft.AerialProvider()/*, "detail", 10, 10, 585, 580*/);
            //new Microsoft.RoadProvider()
//		mapDetail.zoomToLevel(4);
            mapDetail.setZoomRange(1, 12);
            MapUtils.createDefaultEventDispatcher(this, mapDetail);

            // Static overview map
            //mapOverviewStatic = new UnfoldingMap(this, "overviewStatic", 605, 10, 185, 185);
            mapDetail.zoomToLevel(2);

            mapDetail.panTo(center);

            mapDetail.addMarkerManager(markerManager);
            
            
            for (int i = 0; i < Markers.size(); i++) {
//                    ScreenPosition tPos = Markers.get(i).getScreenPosition(mapDetail);
//                    strokeWeight(3);
//                    stroke(67, 211, 227, 100);
//                    noFill();
//                    ellipse(tPos.x, tPos.y, 36, 36);
                Markers.get(i).setColor(color(224,12,18,100));//(color(44, 127, 184, 100));

                if (!counts.isEmpty()) {
                    float weight = (float) counts.get(i) / (float) countMax;

                    Markers.get(i).setStrokeColor(color(252, 141, 98, 100));
                    Markers.get(i).setStrokeWeight((int) (weight * 15));
                }

            }
            markerManager.addMarkers(Markers);

            oldWidth = width;
            oldHeight = height;

       
//            storeMarker
//             markerManager.addMarkers(Markers);
            
            smooth();

            //noLoop();

            //viewportRect = new ViewportRect();
        }

        
        
        public void updateSelectedMarker(float lat, float lng)
                
        {
            if (selectedMark == null)
                
            {selectedMark = new MyMarker(new Location(lat,lng));
            mapDetail.addMarkers(selectedMark);}
            else
            {
               
                selectedMark.setLocation(new Location(lat,lng));
                
            }
            
            selectedMark.setColor(color(224,12,18,150));
            
            
            
        }
        @Override
        public void draw() {
            //background(0);

            background(0);
            mapDetail.draw();

            //mapDetail.draw();
            //mapOverviewStatic.draw();
            // Viewport is updated by the actual area of the detail map
//		ScreenPosition tl = mapOverviewStatic.getScreenPosition(mapDetail.getTopLeftBorder());
//		ScreenPosition br = mapOverviewStatic.getScreenPosition(mapDetail.getBottomRightBorder());
//		viewportRect.setDimension(tl, br);
//		viewportRect.draw();
        }

        public void updateMarkers(List<Color> lc, List<SimplePointMarker> llsm) {
            markerManager.clearMarkers();

            for (int j = 0; j < llsm.size(); j++) {

                //Color c = lc.get(j);
                // llsm.get(j).setColor(color(c.getRed(),c.getGreen(),c.getBlue(),c.getAlpha()));
                llsm.get(j).setColor(color(44, 127, 184, 100));
                markerManager.addMarker(llsm.get(j));

            }

        }

        public void updateDocMarkers(List<Color> lc, List<List<SimplePointMarker>> llsm) {
            markerManager.clearMarkers();

            for (int j = 0; j < llsm.size(); j++) {
                for (int i = 0; i < llsm.get(j).size(); i += 10) {

                //Color c = lc.get(j);
                    //llsm.get(j).get(i).setColor(color(c.getRed(),c.getGreen(),c.getBlue(),c.getAlpha()));
                    markerManager.addMarker(llsm.get(j).get(i));

                }
            }

        }

        @Override
        public void keyPressed() {

            if (key == 'c') {
                
                
                 Location topleft = mapDetail.getTopLeftBorder();
                 Location rightBottom = mapDetail.getBottomRightBorder();
                 
                 PGraphics pg = mapDetail.mapDisplay.getOuterPG();
		PImage thumbnail = pg.get();
                thumbnail.save("map.png");
                
                 System.out.println(topleft);
                 System.out.println(rightBottom);
                 
                 
               // markerManager.clearMarkers();
            }

        }

//        public void panViewportOnDetailMap() {
//            float x = viewportRect.x + viewportRect.w / 2;
//            float y = viewportRect.y + viewportRect.h / 2;
//            Location newLocation = mapOverviewStatic.mapDisplay.getLocation(x, y);
//            mapDetail.panTo(newLocation);
//        }
//
////        public void mousePressed() {
////            // do something based on mouse movement
////
////            // update the screen (run draw once)
////            redraw();
////        }
//        class ViewportRect {
//
//            float x;
//            float y;
//            float w;
//            float h;
//            boolean dragged = false;
//
//            public boolean isOver(float checkX, float checkY) {
//                return checkX > x && checkY > y && checkX < x + w && checkY < y + h;
//            }
//
//            public void setDimension(ScreenPosition tl, ScreenPosition br) {
//                this.x = tl.x;
//                this.y = tl.y;
//                this.w = br.x - tl.x;
//                this.h = br.y - tl.y;
//            }
//
//            public void draw() {
//                noFill();
//                stroke(251, 114, 0, 240);
//                rect(x, y, w, h);
//            }
//        }
        float oldX;
        float oldY;

        @Override
        public void mousePressed() {

            Marker hitMarker = mapDetail.getFirstHitMarker(mouseX, mouseY);
            if (hitMarker != null) {
                hitMarker.setSelected(true);

            } else {

                for (Marker marker : mapDetail.getMarkers()) {
                    marker.setSelected(false);
                }
            }

        }
//
//	public void mouseReleased() {
//		viewportRect.dragged = false;
//	}
//
//	public void mouseDragged() {
//		if (viewportRect.dragged) {
//			viewportRect.x = mouseX - oldX;
//			viewportRect.y = mouseY - oldY;
//
//			panViewportOnDetailMap();
//		}
//	}
    }
    
    
    public class MyMarker extends SimplePointMarker {
 
  public MyMarker(Location location) {
    super(location);
  }
 
  @Override
  public void draw(PGraphics pg, float x, float y) {
    pg.pushStyle();
    pg.noStroke();
    pg.fill(200, 20, 20, 150);
    pg.ellipse(x, y, 20, 20);
    pg.fill(255, 100);
    pg.ellipse(x, y, 10, 10);
    pg.popStyle();
  }
}
    
    
    public class ImageMarker extends AbstractMarker {

	PImage img;

	public ImageMarker(Location location, PImage img) {
		super(location);
		this.img = img;
	}

	@Override
	public void draw(PGraphics pg, float x, float y) {
		pg.pushStyle();
		pg.imageMode(PConstants.CORNER);
                
		// The image is drawn in object coordinates, i.e. the marker's origin (0,0) is at its geo-location.
		pg.image(img, x - img.width/2, y - img.height/2);
		pg.popStyle();
	}

	@Override
	protected boolean isInside(float checkX, float checkY, float x, float y) {
		return checkX > x && checkX < x + img.width && checkY > y && checkY < y + img.height;
	}

}
    
    
    public class tweetData
    {
        public float lat;
        public float lon;
        String _id;
        String text;
        int pos, neg;
        String label;
        String poster;
        
        tweetData(float lat, float lon, String id, String tt, int p, int n, String l)
        {
            this.lat = lat; 
            this.lon = lon;
            this._id = id;
            this.text = tt;
            this.pos = p;
            this.neg = n;
            this.label = l;
            
            
            
        }
        
        
    }
    
    
}
