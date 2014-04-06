/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.TasteAnalytics.Apollo.TopicRenderer;



import static javafx.scene.paint.Color.color;
import static javafx.scene.paint.Color.color;
import static javafx.scene.paint.Color.color;
import static javafx.scene.paint.Color.color;
import static javafx.scene.paint.Color.color;
import static javax.swing.Spring.width;
import static processing.core.PApplet.map;
import static processing.core.PConstants.HSB;
import static processing.core.PConstants.RGB;
import treemap.SimpleMapItem;

/**
 *
 * @author Li
 */




public class LeafItem extends SimpleMapItem{
    
    NodeItem parent;
    
    String name;
  int level;
  
  //color c;
  float hue;
  float brightness;
    
  float textPadding = 8;
    
  float boxLeft, boxTop;
  float boxRight, boxBottom;
    
    LeafItem(NodeItem parent, int level, int order) {
    this.parent = parent;
   
    this.order = order;
    this.level = level;
      
   

  }
//  void updateColors() {
//    if (parent != null) {
//      hue = map(order, 0, parent.getItemCount(), 0, 360);
//    }
//    brightness = modTimes.percentile(file.lastModified()) * 100;
//
//    colorMode(HSB, 360, 100, 100);
//    if (parent == zoomItem) {
//      c = color(hue, 80, 80);
//    } else if (parent != null) {
//      c = color(parent.hue, 80, brightness);
//    }
//    colorMode(RGB, 255);
//  }
//    
//    void calcBox() {
//    boxLeft = zoomBounds.spanX(x, 0, width);
//    boxRight = zoomBounds.spanX(x+w, 0, width);
//    boxTop = zoomBounds.spanY(y, 0, height);
//    boxBottom = zoomBounds.spanY(y+h, 0, height);
//  }
//
//
//  public void draw() {
//    calcBox();
//
//    fill(c);
//    rect(boxLeft, boxTop, boxRight, boxBottom);
//
//    if (textFits()) {
//      drawTitle();
//    } else if (mouseInside()) {
//      rolloverItem = this;
//    }
//   }
//    
//    
//  void drawTitle() {
//    fill(255, 200);
//    
//    float middleX = (boxLeft + boxRight) / 2;
//    float middleY = (boxTop + boxBottom) / 2;
//    if (middleX > 0 && middleX < width && middleY > 0 && middleY < height) {
//      if (boxLeft + textWidth(name) + textPadding*2 > width) {
//        textAlign(RIGHT);
//        text(name, width - textPadding, boxBottom - textPadding);
//      } else {
//        textAlign(LEFT);
//        text(name, boxLeft + textPadding, boxBottom - textPadding);
//      }
//    }
//  }
//
//
//  boolean textFits() {
//    float wide = textWidth(name) + textPadding*2;
//    float high = textAscent() + textDescent() + textPadding*2;
//    return (boxRight - boxLeft > wide) && (boxBottom - boxTop > high); 
//  }
//    
// 
//  boolean mouseInside() {
//    return (mouseX > boxLeft && mouseX < boxRight && 
//            mouseY > boxTop && mouseY < boxBottom);    
//  }
//
//
//  boolean mousePressed() {
//    if (mouseInside()) {
//      if (mouseButton == LEFT) {
//        parent.zoomIn();
//        return true;
//
//      } else if (mouseButton == RIGHT) {
//        if (parent == zoomItem) {
//          parent.zoomOut();
//        } else {
//          parent.hideContents();
//        }
//        return true;
//      }
//    }
//    return false;
//  }
    
}
