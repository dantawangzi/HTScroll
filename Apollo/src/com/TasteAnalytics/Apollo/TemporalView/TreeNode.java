/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TasteAnalytics.Apollo.TemporalView;

import com.TasteAnalytics.Apollo.TreeMapView.MapLayout;
import com.TasteAnalytics.Apollo.TreeMapView.MapModel;
import com.TasteAnalytics.Apollo.TreeMapView.Mappable;
import com.TasteAnalytics.Apollo.TreeMapView.RandomMap;
import com.TasteAnalytics.Apollo.TreeMapView.Rect;
import com.TasteAnalytics.Apollo.TreeMapView.SquarifiedLayout;
import com.TasteAnalytics.Apollo.TreeMapView.TreeModel;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.ArrayUtils;
import processing.core.PApplet;

/*
 * 
 * This software is subject to the terms of the Common Public License
 * Agreement, available at the following URL:
 *   http://www.opensource.org/licenses/cpl.html .
 * Copyright (C) 2003-2004 TONBELLER AG.
 * All Rights Reserved.
 * You must accept the terms of that agreement to use this software.
 * 
 *
 * 
 */

/**
 * Tree Node for the for a general tree of Objects
 */
public class TreeNode extends TreeModel{

  private TreeNode parent = null;
  private List children = null;
  private Object reference;
  private int index;
  private String label;
  private String annonation;
  private List<Float> value;
  private List<Float> unNormValue;
  
  private int nodeSize;
  private String[] topics;
  private List<Integer> topicsContainedIdx;
  private boolean draggedTo;
  private Color nodeColor;
  private Color baseColor;
  
  private float leafNodeWeight;
  
  //double size = 1;
  int order = 0;

//    public double getSize() {
//        return size;
//    }
//
//    public void setSize(double size) {
//        this.size = size;
//    }
  
  public int getOrder() {
        return order;
    }

  @Override
    public void setOrder(int order) {
        this.order = order;
    }
  
  
  private List<Float> valueSub;

    public List<Float> getValueSub() {
        return valueSub;
    }

    public void setValueSub(List<Float> valueSub) {
        this.valueSub = valueSub;
    }
  
  
  
  
    public float getLeafNodeWeight() {
        return leafNodeWeight;
    }

    public void setLeafNodeWeight(float leafNodeWeight) {
        this.leafNodeWeight = leafNodeWeight;
    }
  
  
  
  public void setBaseColor(Color c)
    {
         this.baseColor = c;
    }
  
  
  public Color getBaseColor()
    {
        return  this.baseColor;
    }
  
  
  public void setColor(Color c)
    {
        this.nodeColor = c;
    }
  
  
  public Color getColor()
    {
        return  this.nodeColor;
    }
  
  

   public void setDraggedTo(boolean m)
    {
         this.draggedTo = m;
    }
  
  
  public boolean getDraggedTo()
    {
        return  this.draggedTo;
    }
  
  
    public void setUnNormArrayValue(List<Float> mValue)
    {
        this.unNormValue = mValue;
    }
  
  
  public List<Float> getUnNormArrayValue()
    {
        return  this.unNormValue;
    }
  
  
  
  public void setArrayValue(List<Float> mValue)
    {
        this.value = mValue;
    }
  
  
  public List<Float> getArrayValue()
    {
        return  this.value;
    }
  
  public void setLabel(String mValue)
    {
        this.label = mValue;
    }
  
  
  public String[] getNodeTopics()
    {
        return  this.topics;
    }
  
  public void setNodeTopics(String[] mValue)
    {
        this.topics = mValue;
    }
  
  
  public String getValue()
    {
        return  this.label;
    }
  
    @Override
   public String toString()
    {
       
        // return (this.getValue());
         
        return this.annonation;
         
//        if (this.getChildren().isEmpty())
//        {
//            StringBuffer result = new StringBuffer();
//            int len = topics.length;
//            if (topics.length >=22)
//                len = 20;
//            else
//                len = topics.length;
//
//            for (int i = 1; i < (len); i++) {
//               result.append( topics[i] );
//               result.append( " " );
//
//            }
//            String mynewstring = result.toString();
//
//
//            return (mynewstring);
//        }
//        else
//            return (""/*this.getValue()*/);
    }
     
  public TreeNode() {
    this.parent = null;
    this.children = new ArrayList();
    this.label = "";
    this.value = new ArrayList<Float>();
    this.valueSub = new ArrayList<Float>();
    this.unNormValue = new ArrayList<Float>();    
    this.topics = null;
    this.nodeSize = 0;
    this.topicsContainedIdx = new ArrayList<Integer>();
    this.nodeColor = null; //new Color(255, 0, 0);
    this.baseColor = new Color(255, 0, 0);
    this.annonation = "";
    this.leafNodeWeight = 0;
  }
 
   
  public TreeNode(Object obj) {
    this.parent = null;
    this.reference = obj;
    this.children = new ArrayList();
    this.label = "";
    this.value = new ArrayList<Float>();
    this.valueSub = new ArrayList<Float>();
    this.unNormValue = new ArrayList<Float>();    
    this.topics = null;
    this.nodeSize = 0;
    this.topicsContainedIdx = new ArrayList<Integer>();
    this.nodeColor = null; //new Color(255, 0, 0);
    this.baseColor = new Color(255, 0, 0);
    this.annonation = "";
    this.leafNodeWeight = 0;
  }

   public TreeNode(int i) {
    this.parent = null;
    this.index = i;
    this.children = new ArrayList();
    this.label = "";
    this.value = new ArrayList<Float>();
    this.unNormValue = new ArrayList<Float>();
    this.topics = null;
    this.nodeSize = 0;
    this.topicsContainedIdx = new ArrayList<Integer>();
    this.nodeColor = null; //new Color(255, 0, 0);
    this.baseColor = new Color(255, 0, 0);
    this.annonation = "";
    this.leafNodeWeight = 0;
    this.valueSub = new ArrayList<Float>();
  }
   
     public TreeNode(int i, String l) {
    this.parent = null;
    this.index = i;
    this.children = new ArrayList();
    this.label = l;
    this.value = new ArrayList<Float>();
    this.unNormValue = new ArrayList<Float>();
    this.nodeSize = 0;
    this.topicsContainedIdx = new ArrayList<Integer>();
    this.nodeColor = null; //new Color(255, 0, 0);
    this.baseColor = new Color(255, 0, 0);
    this.annonation = "";
    this.leafNodeWeight = 0;
    this.valueSub = new ArrayList<Float>();
  }
  /**
   * remove node from tree
   */
  public void remove() {
    if (parent != null) {
      parent.removeChild(this);
    }
  }

  
public List<Integer> getTopicsContainedIdx()
  {
      return topicsContainedIdx;
  }
  /**
   * remove child node
   * @param child
   */
  private void removeChild(TreeNode child) {
    if (children.contains(child))
      children.remove(child);

  }

  /**
   * add child node
   * @param child node to be added
   */
  public void addChildNode(TreeNode child) {
    child.parent = this;
    if (!children.contains(child))
      children.add(child);
  }

  /**
   * deep copy (clone)
   * @return copy of TreeNode
   */
  public TreeNode deepCopy() {
    TreeNode newNode = new TreeNode(reference);
    for (Iterator iter = children.iterator(); iter.hasNext();) {
      TreeNode child = (TreeNode) iter.next();
      newNode.addChildNode(child.deepCopy());
    }
    return newNode;
  }

  /**
   * deep copy (clone) and prune 
   * @param depth - number of child levels to be copied
   * @return copy of TreeNode
   */
  public TreeNode deepCopyPrune(int depth) {
    if (depth < 0)
      throw new IllegalArgumentException("Depth is negative");
    TreeNode newNode = new TreeNode(reference);
    if (depth == 0)
      return newNode;
    for (Iterator iter = children.iterator(); iter.hasNext();) {
      TreeNode child = (TreeNode) iter.next();
      newNode.addChildNode(child.deepCopyPrune(depth - 1));
    }
    return newNode;
  }

  /**
   * @return level = distance from root
   */
  public int getLevel() {
    int level = 0;
    TreeNode p = parent;
    while (p != null) {
      ++level;
      p = p.parent;
    }
    return level;
  }

  /**
   * walk through subtree of this node
   * @param callbackHandler function called on iteration 
   */
  public int walkTree(TreeNodeCallback callbackHandler) {
    int code = 0;
    code = callbackHandler.handleTreeNode(this);
    if (code != TreeNodeCallback.CONTINUE)
      return code;
    ChildLoop: for (Iterator iter = children.iterator(); iter.hasNext();) {
      TreeNode child = (TreeNode) iter.next();
      code = child.walkTree(callbackHandler);
      if (code >= TreeNodeCallback.CONTINUE_PARENT)
        return code;
    }
    return code;
  }
  public int getNodeSize()
  {
      return this.nodeSize;
  }
  public int calculateNodeSize()
  {
      int n = 0;
      
      if (this.children.isEmpty())
      {
          n = 1; 
      }
      else
      {
          for (int i=0; i<this.children.size(); i++)
          {
              n += ((TreeNode)this.children.get(i)).calculateNodeSize();
              
          }
          
      }
      this.nodeSize = n;
      
      return n;
  }
  
  
  
   public Rectangle calculateRect(Rectangle bound)
  {
      Rectangle result = null;
      
      if (this.children.isEmpty())
      {
//          Rectangle r = new Rectangle( (int)this.getItems()[order].getBounds().x, 
//                      (int)this.getItems()[order].getBounds().y, 
//                      (int)this.getItems()[order].getBounds().w, 
//                      (int)this.getItems()[order].getBounds().h);
          result = bound;
          this.setMyRect(result);
      }
      else
      {
          
        Mappable[] leaves = this.getItems();
        
        MapModel map = new RandomMap(leaves);
       
	//Mappable[] items = map.getItems();
	    
	MapLayout algorithm = new SquarifiedLayout();
        
	algorithm.layout(map, new Rect(bound.x,bound.y,bound.width,bound.height));
                    
          for (int i=0; i<this.children.size(); i++)
          {
              Rectangle r = new Rectangle( (int)this.getItems()[i].getBounds().x, 
                      (int)this.getItems()[i].getBounds().y, 
                      (int)this.getItems()[i].getBounds().w, 
                      (int)this.getItems()[i].getBounds().h);
              
              result = ((TreeNode)this.children.get(i)).calculateRect(r);
              ((TreeNode)this.children.get(i)).setMyRect( r);
              
              
              
              
          }
          
      }      
      
      //this.myRect = result;
      return result;
  }
  
  double TreeMapTopicWeight = 0;

    public double getTreeMapTopicWeight() {
        return TreeMapTopicWeight;
    }

    public void setTreeMapTopicWeight(double TreeMapTopicWeight) {
        this.TreeMapTopicWeight = TreeMapTopicWeight;
    }
  
  double topicWeight = 0;

    public double getTopicWeight() {
        return topicWeight;
    }

    public void setTopicWeight(double topicWeight) {
        this.topicWeight = topicWeight;
    }
  
  float numberOfEvents = 0.5f;

    public float getNumberOfEvents() {
        return numberOfEvents;
    }

    public void setNumberOfEvents(float numberOfEvents) {
        this.numberOfEvents = numberOfEvents;
    }
  
  public float calculateTreeMapTopicWeight()
  {
      float result = 0;
      if (this.getChildren().isEmpty())
      {
          result = (float) Math.sqrt( this.numberOfEvents * this.topicWeight);
                    
      }
      else
      {
          for (int i=0 ;i<this.getChildren().size(); i++)
          {
              
              result += ((TreeNode)this.getChildren().get(i)).calculateTreeMapTopicWeight();
          }          
          
      }
      
      
      
      this.TreeMapTopicWeight = result;
      return result;
      
  }
  
  public float calculateNodeWeight()
  {
      float w = 0;
      
      if (this.children.isEmpty())
      {
          w = this.leafNodeWeight; 
      }
      else
      {
          for (int i=0; i<this.children.size(); i++)
          {
              w += ((TreeNode)this.children.get(i)).calculateNodeWeight();
              
          }
          
      }
      this.leafNodeWeight = w;
      
      return w;
  }
  
  
  
      public void calculateNodeContainedIdx()
  {
      
      
      if (!this.children.isEmpty())
      {
         
          for (int i=0; i<this.children.size(); i++)
          {
             ((TreeNode)this.children.get(i)).calculateNodeContainedIdx();
                                                          
               this.topicsContainedIdx.addAll(((TreeNode)this.children.get(i)).topicsContainedIdx);;
          }
          
         
          
      }
      else
      {
         this.topicsContainedIdx.add(this.getIndex());
          
      }
    
  }
      
      
  
    public String[] calculateNodeString()
  {
      
      
      if (!this.children.isEmpty() && this.topics.length<10)
      {
          String[] results = new String[10];
          for (int i=0; i<this.children.size(); i++)
          {
              String temp[] = ((TreeNode)this.children.get(i)).calculateNodeString();
                           
                
               results = (String[]) ArrayUtils.addAll(this.topics, temp);
               this.topics = results;               
               
               
               this.topicsContainedIdx.addAll(((TreeNode)this.children.get(i)).topicsContainedIdx);;
          }
          
          return results;
          
      }
      else
      {
          
          this.topicsContainedIdx.add(this.getIndex());
          return this.topics;
      }
    
  }
    
    
    
    
  @Override
     public Mappable[] getItems()
     {
         Mappable[] mp = new Mappable[this.getChildren().size()];
         
         for (int i=0; i<this.getChildren().size(); i++)
         {
            
             mp[i] = ((TreeNode)getChildren().get(i)).getMapItem();
             mp[i].setSize(((TreeNode)getChildren().get(i)).getTreeMapTopicWeight());
         }
         
         return mp;
         
     }
     
     Rectangle myRect;

    public Rectangle getMyRect() {
        return myRect;
    }

    public void setMyRect(Rectangle myRect) {
        this.myRect = myRect;
    }
     
     
     
     

  /**
   * walk through children subtrees of this node
   * @param callbackHandler function called on iteration 
   */
  public int walkChildren(TreeNodeCallback callbackHandler) {
    int code = 0;
    ChildLoop: for (Iterator iter = children.iterator(); iter.hasNext();) {
      TreeNode child = (TreeNode) iter.next();
      code = callbackHandler.handleTreeNode(child);
      if (code >= TreeNodeCallback.CONTINUE_PARENT)
        return code;
      if (code == TreeNodeCallback.CONTINUE) {
        code = child.walkChildren(callbackHandler);
        if (code > TreeNodeCallback.CONTINUE_PARENT)
          return code;
      }
    }
    return code;
  }

  public int getIndex()
  {
      return this.index;
  }
  
  public void setIndex(int i)
  {
      this.index = i;
  }
  /**
   * @return List of children
   */
  public List getChildren() {
    return children;
  }

  /**
   * @return parent node
   */
  public TreeNode getParent() {
    return parent;
  }

  /**
   * @return reference object
   */
  public Object getReference() {
    return reference;
  }
  
  public void setAnnonation(String s)
  {
      this.annonation = s;
      
  }
  
  public String getAnnonation()
  {
      
      return this.annonation;
  }


  /**
   * set reference object
   * @param object reference
   */
  public void setReference(Object object) {
    reference = object;
  }

} // TreeNode
/**
 * handle call back for position tree
 */
interface TreeNodeCallback {

  public static final int CONTINUE = 0;
  public static final int CONTINUE_SIBLING = 1;
  public static final int CONTINUE_PARENT = 2;
  public static final int BREAK = 3;

  /**
   * @param node the current node to handle
   * @return 0 continue tree walk
   *         1 break this node (continue sibling)
   *         2 break this level (continue parent level)
   *         3 break tree walk 
   */
  int handleTreeNode(TreeNode node);
  
  
  
  
} // TreeNodeCallback