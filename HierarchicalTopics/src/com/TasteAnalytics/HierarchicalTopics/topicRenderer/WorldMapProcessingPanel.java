/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TasteAnalytics.HierarchicalTopics.topicRenderer;

import codeanticode.glgraphics.*;
import com.TasteAnalytics.HierarchicalTopics.gui.ViewController;
import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;
import com.google.code.geocoder.model.LatLng;

import de.fhpotsdam.unfolding.*;
import de.fhpotsdam.unfolding.geo.*;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.MarkerManager;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.*;
import de.fhpotsdam.unfolding.utils.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JPanel;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.*;

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

    public WorldMapProcessingPanel(ViewController vc, List<HashMap> M, int w, int h) {
        //super("Embedded PApplet");
        super();
        parent = vc;

        setLayout(new BorderLayout());
        List<Point2D> L = new ArrayList<Point2D>();

        float max_x = -9999999.0f;  // lng
        float max_y = -9999999.0f;  // lat
        float min_x = 9999999.0f;
        float min_y = 9999999.0f;

        for (int i = 0; i < M.size(); i++) {

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

        Point2D center = new Point2D.Double((min_y + max_y) / 2, (min_x + max_x) / 2);
//        System.out.println( M.size());
//        System.out.println( L.size());
        //System.out.println(center.getX() + " " + center.getY());

        embed = new Embedded(L, center);

        
        embed.init();
        add(embed, BorderLayout.CENTER);

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
            //this.frame.setResizable(redraw);
            //  frame.setResizable(true);

             //new MapBox.MapBoxProvider());//
            
            mapDetail = new UnfoldingMap(this, new Google.GoogleMapProvider());//new Microsoft.AerialProvider()/*, "detail", 10, 10, 585, 580*/);
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
                Markers.get(i).setColor(color(44, 127, 184, 100));

                if (!counts.isEmpty()) {
                    float weight = (float) counts.get(i) / (float) countMax;

                    Markers.get(i).setStrokeColor(color(252, 141, 98, 100));
                    Markers.get(i).setStrokeWeight((int) (weight * 15));
                }

            }
            markerManager.addMarkers(Markers);

            oldWidth = width;
            oldHeight = height;

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
                markerManager.clearMarkers();
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
}
