/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TasteAnalytics.Apollo.TopicRenderer;

//import org.gicentre.treemappa.*;     // For treemappa classes
//import org.gicentre.utils.colour.*;
//import org.gicentre.treemappa.*;
//import org.gicentre.utils.colour.*;
import processing.core.PApplet;
import processing.core.PFont;
import treemap.*;

/**
 *
 * @author Li
 */
public class TreeMapProcessingPanel {
    //PTreeMappa pTreeMappa; 

    int maxFontSize = 1000;
    int minFontSize = 1;

    PFont font;

    Treemap map;
    MapLayout layoutAlgorithm = new SquarifiedLayout();

    Embedded embed;

    public class Embedded extends PApplet {

        @Override
        public void setup() {

            size(800, 600);

            font = createFont("miso-bold.ttf", 10);

            map = new Treemap(null, 0, 0, width, height);

            smooth();
            noLoop();
// 
//  // Create an empty treemap.    
//  pTreeMappa = new PTreeMappa(this);
//  
//  // Load the data and build the treemap.
//  pTreeMappa.readData("life.csv"); 
        }

        @Override
        public void draw() {
            background(255);
            background(255);
            map.setLayout(layoutAlgorithm);
            map.updateLayout();
            map.draw();
            noLoop();


  // Get treemappa to draw itself.
            // pTreeMappa.draw();
        }

        
        
        @Override
            public void keyReleased() {

 
  // set layout algorithm
            if (key=='1') layoutAlgorithm = new SquarifiedLayout();
            if (key=='2') layoutAlgorithm = new PivotBySplitSize();
            if (key=='3') layoutAlgorithm = new SliceLayout();
            if (key=='4') layoutAlgorithm = new OrderedTreemap();
            if (key=='5') layoutAlgorithm = new StripTreemap();

            if (key=='1'||key=='2'||key=='3'||key=='4'||key=='5'||
              key=='s'||key=='S'||key=='p'||key=='P') loop();
          }
            
            
    }
    

//class WordItem extends SimpleMapItem {
//  String word;
//  int count;
//  int margin = 3;
// 
//  WordItem(String word) {
//    this.word = word;
//  }
// 
//  public void draw() {
//    // frames
//    // inheritance: x, y, w, h
//    strokeWeight(0.25);
//    fill(255);
//    rect(x, y, w, h);
// 
//    // maximize fontsize in frames
//    for (int i = minFontSize; i <= maxFontSize; i++) {
//      textFont(font,i);
//      if (w < textWidth(word) + margin || h < (textAscent()+textDescent()) + margin) {
//        textFont(font,i);
//        break;
//      }
//    }
// 
//    // text
//    fill(0);
//    textAlign(CENTER, CENTER);
//    text(word, x + w/2, y + h/2);
//  }
//}

}
