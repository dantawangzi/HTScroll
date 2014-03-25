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
import java.awt.Frame;
import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import processing.core.PApplet;
import processing.opengl.*;

/**
 *
 * @author Li
 */
public class WorldMapProcessingFrame extends Frame {

    ViewController parent;
    List<Color> geoLocationColors = new ArrayList<Color>();
    List<List<SimplePointMarker>> geoLocations;

    List<Point2D> allLocations;
    Embedded embed;
    public WorldMapProcessingFrame(ViewController vc, List<Point2D> L, Point2D center) {
        super("Embedded PApplet");
        
        
        
        
        
        this.geoLocations = new ArrayList<List<SimplePointMarker>>();

        parent = vc;

        setLayout(new BorderLayout());
        embed = new Embedded(L, center);
        
        allLocations = L;
        add(embed, BorderLayout.CENTER);
        embed.init();



    }
    
    
        public WorldMapProcessingFrame(ViewController vc, List<HashMap> M) {
        super("Embedded PApplet");
        this.geoLocations = new ArrayList<List<SimplePointMarker>>();

        parent = vc;

        setLayout(new BorderLayout());
         List<Point2D> L = new ArrayList<Point2D>();
         
         
         float max_x = -9999999.0f;  // lng
         float max_y = -9999999.0f;  // lat
         float min_x = 9999999.0f;
         float min_y = 9999999.0f;
         
         
        for (int i=0; i<M.size(); i++)
        {
        
            float lat = Float.parseFloat((String)M.get(i).get("latitude"));
            float lng = Float.parseFloat((String)M.get(i).get("longitude"));
            
            
             L.add(new Point2D.Float(lat,lng));
//
                if (lat>=max_y)
                    max_y = lat;
                if (lat<=min_y)
                    min_y = lat;

                if (lng>=max_x)
                    max_x = lng;
                if (lng<=min_x)
                    min_x = lng;
            
//            String country = (String) M.get(i).get("In what Country do you currently live?");
//            String state = (String) M.get(i).get("In what State or Province do you currently live?");
//            String city = (String) M.get(i).get("In what City do you currently live?");
//            String address = city + ", " + state + ","+ country;
//            
//            final Geocoder geocoder = new Geocoder();
//            GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(address).setLanguage("en").getGeocoderRequest();
//            GeocodeResponse geocoderResponse = geocoder.geocode(geocoderRequest);
//            List<GeocoderResult> gcr = geocoderResponse.getResults();
//
//            System.out.println(gcr);
//            
//            if (gcr!=null && !gcr.isEmpty())
//            {
//                LatLng loc = gcr.get(0).getGeometry().getLocation();
//
//
//                BigDecimal lat = loc.getLat();
//                BigDecimal lng = loc.getLng();
//
//                L.add(new Point2D.Float( lat.floatValue(),lng.floatValue()));
//
//                if (lat.floatValue()>=max_y)
//                    max_y = lat.floatValue();
//                if (lat.floatValue()<=min_y)
//                    min_y = lat.floatValue();
//
//                if (lng.floatValue()>=max_x)
//                    max_x = lng.floatValue();
//                if (lng.floatValue()<=min_x)
//                    min_x = lng.floatValue();
//            
//            }
        
        
        }
        
        
        
        
        
        
        Point2D center = new Point2D.Double((min_y+max_y)/2, (min_x+max_x)/2);
//        System.out.println( M.size());
//        System.out.println( L.size());
        System.out.println( center.getX() + " " + center.getY());
        
        embed = new Embedded(L, center);
        
        allLocations = L;
        add(embed, BorderLayout.CENTER);
        embed.init();



    }
//

    public void UpdateGeoLocations(Color c, List<Integer> l) {
        geoLocationColors.add(c);

        List<SimplePointMarker> tmp = new ArrayList<SimplePointMarker>();
        for (int i = 0; i < l.size(); i++) {
            
            int index = l.get(i);                       
                    
            Location temp = new Location(allLocations.get(index).getX(), -allLocations.get(index).getY());

            SimplePointMarker tempMarker = new SimplePointMarker(temp);
            tmp.add(tempMarker);

        }

        geoLocations.add(tmp);
        
        
        embed.updateDocMarkers(geoLocationColors, geoLocations);

    }

    public class Embedded extends PApplet {

        UnfoldingMap mapDetail;
        // Small map showing the overview, i.e. the world
        UnfoldingMap mapOverviewStatic;
        // Interactive finder box atop the overview map.
        ViewportRect viewportRect;
        Location center;
        List<Marker> Markers;
        
        
 
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
        

        public void setup() {
            // original setup code here ...
            size(1000, 1000);
            mapDetail = new UnfoldingMap(this, new Microsoft.RoadProvider() );//new Microsoft.AerialProvider()/*, "detail", 10, 10, 585, 580*/);
            
//		mapDetail.zoomToLevel(4);
//		mapDetail.setZoomRange(4, 10);
            MapUtils.createDefaultEventDispatcher(this, mapDetail);

            // Static overview map
            //mapOverviewStatic = new UnfoldingMap(this, "overviewStatic", 605, 10, 185, 185);

            mapDetail.zoomToLevel(2);
            //mapDetail.zoom(40.0f);
            mapDetail.panTo(center);

            
            mapDetail.addMarkerManager(markerManager);
           
            for (int i = 0; i < Markers.size(); i++) {
//                    ScreenPosition tPos = Markers.get(i).getScreenPosition(mapDetail);
//                    strokeWeight(3);
//                    stroke(67, 211, 227, 100);
//                    noFill();
//                    ellipse(tPos.x, tPos.y, 36, 36);
                Markers.get(i).setColor(color(12, 24, 255, 100));
                

            }
            markerManager.addMarkers(Markers);

            //viewportRect = new ViewportRect();
             
            

        }

        public void draw() {
            //background(0);

            mapDetail.draw();
            //mapOverviewStatic.draw();

            // Viewport is updated by the actual area of the detail map
//		ScreenPosition tl = mapOverviewStatic.getScreenPosition(mapDetail.getTopLeftBorder());
//		ScreenPosition br = mapOverviewStatic.getScreenPosition(mapDetail.getBottomRightBorder());
//		viewportRect.setDimension(tl, br);
//		viewportRect.draw();
        }
        
        
        
        
        public void updateDocMarkers(List<Color> lc,List<List<SimplePointMarker>> llsm)
        {
           markerManager.clearMarkers();
           
           for (int j=0;j<llsm.size(); j++)
           {
            for (int i = 0; i < llsm.get(j).size(); i+=10) {
                
                Color c = lc.get(j);
                llsm.get(j).get(i).setColor(color(c.getRed(),c.getGreen(),c.getBlue(),c.getAlpha()));
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
        
        

        public void panViewportOnDetailMap() {
            float x = viewportRect.x + viewportRect.w / 2;
            float y = viewportRect.y + viewportRect.h / 2;
            Location newLocation = mapOverviewStatic.mapDisplay.getLocation(x, y);
            mapDetail.panTo(newLocation);
        }

//        public void mousePressed() {
//            // do something based on mouse movement
//
//            // update the screen (run draw once)
//            redraw();
//        }
        class ViewportRect {

            float x;
            float y;
            float w;
            float h;
            boolean dragged = false;

            public boolean isOver(float checkX, float checkY) {
                return checkX > x && checkY > y && checkX < x + w && checkY < y + h;
            }

            public void setDimension(ScreenPosition tl, ScreenPosition br) {
                this.x = tl.x;
                this.y = tl.y;
                this.w = br.x - tl.x;
                this.h = br.y - tl.y;
            }

            public void draw() {
                noFill();
                stroke(251, 114, 0, 240);
                rect(x, y, w, h);
            }
        }
        float oldX;
        float oldY;
//	public void mousePressed() {
//		if (viewportRect.isOver(mouseX, mouseY)) {
//			viewportRect.dragged = true;
//			oldX = mouseX - viewportRect.x;
//			oldY = mouseY - viewportRect.y;
//		}
//	}
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
}