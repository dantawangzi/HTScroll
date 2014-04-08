/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.TasteAnalytics.Apollo.TopicRenderer;

import com.TasteAnalytics.Apollo.TemporalView.TreeNode;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author lyu8
 */
public class LabelText {

        int column, row;
        Rectangle2D rect;
        int posX, posY;
        String s;
        boolean isHighlighted;
        boolean isMagnified;
        Font font;
        int stringSize;
        Color rectColor;
        int index;
        TreeNode node;
        boolean isDisplayed;
        int occurance;
        float probablity;
        Point2D location;
        boolean highlightFromLabelTopics = false;

        Color stringColor;

        public void setHighlightFromLabelTopics(boolean b) {

            highlightFromLabelTopics = b;
        }

        public int getOccurance() {
            return occurance;
        }

        public void setOccurance(int occurance) {
            this.occurance = occurance;
        }

        public float getProbablity() {
            return probablity;
        }

        public void setProbablity(float probablity) {
            this.probablity = probablity;
        }

        LabelText() {
            isDisplayed = true;
        }

        public String getString() {

            return s;
        }

        List<Color> labelColor = new ArrayList<Color>();
        
        public void drawLabelRect(Graphics g)
        {
            int size = labelColor.size();
            for (int i = 0 ; i<labelColor.size(); i++)
            {
                g.setColor(labelColor.get(i));
                
                int w = (int) (rect.getWidth()/size);
                
                g.fillRect((int) rect.getX() + i*w, (int) rect.getY(), (int) w, (int) rect.getHeight());
                                                
            }
            
//            if (labelColor.isEmpty())
//                drawRect(g);
            
            
            
            
            
        }
        
        public void drawRect(Graphics g) {
            if (this.rectColor != null) {
                g.setColor(this.rectColor);
            } else {
                g.setColor(Color.LIGHT_GRAY);
            }

            g.fillRect((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
            // g.drawString(s, posX, posY);
        }

        public void drawString(Graphics g) {
            g.setColor(this.stringColor);
            g.setFont(font);

            g.drawString(s, posX, posY);
        }

        public void setRect(Rectangle2D r) {
            rect = r;
        }

        public void setLocation(Point2D r) {
            location = r;
        }

        public Point2D getLocation() {
            return location;
        }

        public Rectangle2D getRect() {
            return rect;
        }

        public void setFont(Font f) {
            font = f;
        }

        public Font getFont() {
            return this.font;
        }

        public void setRectColor(Color f) {
            rectColor = f;
        }

        public void setStringColor(Color f) {
            stringColor = f;
        }

        public void setString(String ss) {
            s = ss;
        }
        
        
        

    }
