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
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;

/**
 *
 * @author lyu8
 */
public class LabelText extends JLabel implements MouseListener {

    public int column, row;
    Rectangle2D rect;
    public int posX, posY;
    public String s;
    boolean isHighlighted;
    boolean isMagnified;
    Font font;
    int stringSize;
    Color rectColor;
    int index;
    TreeNode node;
    boolean isDisplayed;
    int occurance;
    public float probablity;
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

    public LabelText() {

        addMouseListener(this);

        isDisplayed = true;
    }

    public String getString() {

        return s;
    }

    List<Color> labelColor = new ArrayList<Color>();

    public void drawLabelRect(Graphics g) {
        int size = labelColor.size();
        for (int i = 0; i < labelColor.size(); i++) {
            g.setColor(labelColor.get(i));

            int w = (int) (rect.getWidth() / size);

            g.fillRect((int) rect.getX() + i * w, (int) rect.getY(), (int) w, (int) rect.getHeight());

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

 
  

    public Point2D getLocation2D() {
        return location;
    }

    public Rectangle2D getRect() {
        return rect;
    }

    @Override
    public void setFont(Font f) {
        font = f;
    }

    @Override
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

    @Override
    public void mouseClicked(MouseEvent e) {
      //  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mousePressed(MouseEvent e) {
      //  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseReleased(MouseEvent e) {
       // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseEntered(MouseEvent e) {

       

       // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseExited(MouseEvent e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
