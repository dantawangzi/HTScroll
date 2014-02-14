/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TasteAnalytics.HierarchicalTopics.topicRenderer;

import java.awt.BorderLayout;
import java.awt.Frame;
//import javax.swing.JFrame;
import processing.core.PApplet;

import processing.opengl.*;
import codeanticode.glgraphics.*;

import de.fhpotsdam.unfolding.*;
import de.fhpotsdam.unfolding.geo.*;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.MarkerManager;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.utils.*;
import de.fhpotsdam.unfolding.providers.*;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import com.TasteAnalytics.HierarchicalTopics.gui.ViewController;

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
            mapDetail = new UnfoldingMap(this, new Microsoft.AerialProvider()/*, "detail", 10, 10, 585, 580*/);
//		mapDetail.zoomToLevel(4);
//		mapDetail.setZoomRange(4, 10);
            MapUtils.createDefaultEventDispatcher(this, mapDetail);

            // Static overview map
            //mapOverviewStatic = new UnfoldingMap(this, "overviewStatic", 605, 10, 185, 185);

            mapDetail.zoomToLevel(10);
            //mapDetail.zoom(40.0f);
            mapDetail.panTo(center);

            
            mapDetail.addMarkerManager(markerManager);
           
            for (int i = 0; i < Markers.size(); i++) {
//                    ScreenPosition tPos = Markers.get(i).getScreenPosition(mapDetail);
//                    strokeWeight(3);
//                    stroke(67, 211, 227, 100);
//                    noFill();
//                    ellipse(tPos.x, tPos.y, 36, 36);
                Markers.get(i).setColor(color(255, 0, 0, 100));
                

            }
           // markerManager.addMarkers(Markers);

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