package com.TasteAnalytics.Apollo.Wordle;

import java.awt.Shape;
import java.awt.geom.Point2D;

public abstract class WordleLite {
	abstract public Shape getShape();
	
	private Point2D location = new Point2D.Double();
	private boolean isLayouted = false;
	public Point2D getLocation(){
		return location;
	}
	void setLocation(double x, double y){
		location.setLocation(x, y);
	}
	public boolean isLayouted(){
		return isLayouted;
	}
	void setLayouted(boolean flag){
		isLayouted = flag;
	}
	void cleanup(){
	}
	
}