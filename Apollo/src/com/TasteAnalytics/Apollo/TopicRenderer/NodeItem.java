/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.TasteAnalytics.Apollo.TopicRenderer;

import com.TasteAnalytics.Apollo.TemporalView.TreeNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import treemap.MapLayout;
import treemap.MapModel;
import treemap.Mappable;
import treemap.PivotBySplitSize;
/**
 *
 * @author Li
 */
public class NodeItem extends LeafItem  implements MapModel{

    MapLayout algorithm = new PivotBySplitSize();
    Mappable[] items;
    boolean contentsVisible;
    boolean layoutValid;
    float darkness;
    List<Mappable> item = new ArrayList<Mappable>();
    
  TreeMapProcessingPanel parentTM ;
    
    
  public NodeItem(NodeItem parent,  int level, int order, TreeMapProcessingPanel parentTMap) {
    super(parent,  level, order, parentTMap);

    
  }
  
  
  public LeafItem setTree(TreeNode tree, HashMap<Integer, NodeItem> nodemap, HashMap<Integer, LeafItem> leafmap)
  {
      
      LeafItem li = null;
      if (tree.getChildren().isEmpty())          
      {
          
        //this.items = new Mappable[1];
        
        li = leafmap.get(tree.getIndex()) ;
        int parentIndex = tree.getParent().getIndex();
        li.setParent(nodemap.get(parentIndex));
          System.out.println(tree.getIndex());
          li.item.add(li);
        //this.items[0] = li;
      }
      else
      {
        //this.items = new Mappable[tree.getChildren().size()];
        //setupLeafNode(tree.get(0));
        
         for (int i=0; i<tree.getChildren().size(); i++)
         {            
             TreeNode tmpt = (TreeNode) tree.getChildren().get(i);
             if (tmpt.getChildren().isEmpty())
             {
                 li = setTree(tmpt,nodemap,leafmap) ;                                                  
             }
             else
             {
                 li = nodemap.get(tree.getIndex()) ;
//                if (tree.getParent() != null)
                {
                    int parentIndex = tmpt.getParent().getIndex();
                    li.setParent(nodemap.get(parentIndex));
                    li = setTree(tmpt,nodemap,leafmap) ;

                }
             }
              
             this.item.add(li);
              //this.items[i] = li;
             
         }
        
        
        
      }
      
      

      
      return li;
      
      
  }
  


//  void updateColors() {
//    super.updateColors();
//
//    for (int i = 0; i < items.length; i++) {
//      LeafItem fi = (LeafItem) items[i];
//      fi.updateColors();
//    }
//  }
//
  void checkLayout() {
    if (!layoutValid) {
      if (getItemCount() != 0) {
        algorithm.layout(this, bounds);
      }
      layoutValid = true;
    }
  }


//  boolean mousePressed() {
//    if (mouseInside()) {
//      if (contentsVisible) {
//        // Pass the mouse press to the child items
//        for (int i = 0; i < items.length; i++) {
//          FileItem fi = (FileItem) items[i];
//          if (fi.mousePressed()) {
//            return true;
//          }
//        }
//      } else {  // not opened
//        if (mouseButton == LEFT) {
//          if (parent == zoomItem) {
//            showContents();
//          } else {
//            parent.zoomIn();
//          }            
//        } else if (mouseButton == RIGHT) {
//          if (parent == zoomItem) {
//            parent.zoomOut();
//          } else {
//            parent.hideContents();
//          }
//        }
//        return true;
//      }
//    }
//    return false;
//  }
//
//
  // Zoom to the parent's boundary, zooming out from this item
  void zoomOut() {
    if (parent != null) {
      // Close contents of any opened children
      for (int i = 0; i < items.length; i++) {
        if (items[i] instanceof NodeItem) {
          ((NodeItem)items[i]).hideContents();
        }
      }
      parent.zoomIn();
    }
  }


  void zoomIn() {
    parentTM.zoomItem = this;
    parentTM.zoomBounds.target(x, y, w, h); ///width, h/height);
  }
//
//
  void showContents() {
    contentsVisible = true;
  }
//
//
  void hideContents() {
    // Prevent the user from closing the root level
    if (parent != null) {
      contentsVisible = false;
    }
  }
//
//  
  public void draw() {
    checkLayout();
    calcBox();
    
    if (contentsVisible) {
      for (int i = 0; i < items.length; i++) {
        items[i].draw();
      }
    } else {
      super.draw();
    }

//    if (contentsVisible) {
//      if (mouseInside()) {
//        if (parent == zoomItem) {
//          taggedItem = this;
//        }
//      }
//    }
//    if (mouseInside()) {
//      darkness *= 0.05;
//    } else {
//      darkness += (150 - darkness) * 0.05;
//    }
//    if (parent == zoomItem) {
//      colorMode(RGB, 255);
//      fill(0, darkness);
//      rect(boxLeft, boxTop, boxRight, boxBottom);
//    }
  }
//
//
//  void drawTitle() {
//    if (!contentsVisible) {
//      super.drawTitle();
//    }
//  }
//
//
//  void drawTag() {
//    float boxHeight = textAscent() + textPadding*2;
//
//    if (boxBottom - boxTop > boxHeight*2) {
//      // if the height of the box is at least twice the height of the tag,
//      // draw the tag inside the box itself
//      fill(0, 128);
//      rect(boxLeft, boxTop, boxRight, boxTop+boxHeight);
//      fill(255);
//      textAlign(LEFT, TOP);
//      text(name, boxLeft+textPadding, boxTop+textPadding);
//
//    } else if (boxTop > boxHeight) {
//      // if there's enough room to draw above, draw it there
//      fill(0, 128);
//      rect(boxLeft, boxTop-boxHeight, boxRight, boxTop);
//      fill(255);
//      text(name, boxLeft+textPadding, boxTop-textPadding);
//
//    } else if (boxBottom + boxHeight < height) {
//      // otherwise draw the tag below
//      fill(0, 128);
//      rect(boxLeft, boxBottom, boxRight, boxBottom+boxHeight);
//      fill(255);
//      textAlign(LEFT, TOP);
//      text(name, boxLeft+textPadding, boxBottom+textPadding);
//    }
//  }
//
//
  public Mappable[] getItems() {
    return items;
  }
//
//
  int getItemCount() {
    return items.length;
  }
 

    
}
