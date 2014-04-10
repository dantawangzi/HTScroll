package com.TasteAnalytics.Apollo.Wordle;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.Random;
import com.TasteAnalytics.Apollo.Wordle.WordleAlgorithmLite.LayoutShape;


class SpiralWordleFitterLite {
	private final WordleWrapperLite wrapper;

	public double radius;
	private double theta;

	private final double startX, startY;
	private final double startTheta;
	private final Dimension2D size;
	private final LayoutShape overallShape;

	private final int moveResetTime;
	
	private final static Random rand = new Random(12345L);
	
	 static double angleStep = 0.02;
	 static double radiusStep = 0.3;
	SpiralWordleFitterLite(WordleWrapperLite wrapper, Dimension2D size, 
			LayoutShape layoutShape, int moveResetTime){
		this.wrapper = wrapper;
		this.size = size;
		this.startX = wrapper.bigBounds.x;
		this.startY = wrapper.bigBounds.y;
		this.overallShape = layoutShape;
		this.moveResetTime = moveResetTime;
		radius = 1.0;
		startTheta = rand.nextDouble() * 2 * Math.PI;
		theta = startTheta;
		yRadius = wrapper.getWordle().getShape().getBounds().getHeight()/2;
		yRadius = Math.max(2, yRadius);
		xRadius = wrapper.getWordle().getShape().getBounds().getWidth()/6;
		xRadius = Math.max(2, xRadius);
		xRadius = size.getWidth();
		yRadius = size.getHeight();
		
		resetCnt = 0;
		rectRadius = Math.sqrt(yRadius*yRadius + xRadius*xRadius);
	}

	double centerX = 0;
	double centerY = 1;
	double rectRadius = 0;
	double yRadius = 2;
	double xRadius = 2;
	private int resetCnt = 0;
	private double lastNewX = Double.MIN_VALUE;
	private double lastNewY = Double.MIN_VALUE;
	boolean move2(){
		double w = size.getWidth();
		double h = size.getHeight();
		do
		{
			lastNewX = radius/rectRadius*xRadius * Math.cos(theta);
			lastNewY = radius/rectRadius*yRadius * Math.sin(theta);
			wrapper.setLoc(startX+lastNewX, startY+lastNewY);
//			wrapper.setLoc(startX + radius * Math.cos(theta), startY + radius
//					* Math.sin(theta));
			theta += angleStep;
			radius += radiusStep;
			if (radius > w && radius > h || overallShape == LayoutShape.ROUND)
			{	
//				System.out.println(overallShape == LayoutShape.ROUND);
				resetCnt++;
//				System.out.println(moveResetTime+"<<");
				if (resetCnt > moveResetTime) {
					return false;
				}
				
				theta = rand.nextDouble() * 2 * Math.PI;
				radius = resetCnt*4;
				wrapper.setLoc(startX + radius * Math.cos(theta), startY + radius
						* Math.sin(theta));
//				System.out.println(resetCnt);
				w*=1.2; 
				h*=1.2;
				/*
				 * test
				 */
//				System.out.println("theta = "+theta+ " ,  radius = "+ radius);
//				return;
			}
			/*
			 * avoid jump out of the bound.
			 */
		} while (wrapper.bigBounds.x < 0 || wrapper.bigBounds.y < 0
				|| wrapper.bigBounds.x + wrapper.getWidth() > w
				|| wrapper.bigBounds.y + wrapper.getHeight() > h);
		return true;
	}
	Point2D lastLoc(){
		return new Point2D.Double(
				startX + lastNewX, startY + lastNewY);
	}
	boolean blindMovd(){
		double w = size.getWidth();
		double h = size.getHeight();
		
		{
			lastNewX = radius * Math.cos(theta);
			lastNewY = radius * Math.sin(theta);
		}
		
		{
			lastNewX = radius/rectRadius*xRadius * Math.cos(theta);
			lastNewY = radius/rectRadius*yRadius * Math.sin(theta);
		}
		
		//		System.out.println(startX+","+startY);
		wrapper.setLoc(startX + lastNewX, startY + lastNewY);
		theta += angleStep;
		radius += radiusStep;
		//			System.out.println(radius);
		/*
		 * test
		 */
		//			System.out.println("x = "+ dt.bigBounds.x + ", y = "+ dt.bigBounds.y+ ", w = "+dt.bigBounds.width + ", h = "+ dt.bigBounds.height);
		if (radius > w && radius > h || overallShape == LayoutShape.ROUND)
		{	
			//				System.out.println(overallShape == LayoutShape.ROUND);
			resetCnt++;
			//				System.out.println(moveResetTime+"<<");
			if (resetCnt > moveResetTime+10) {
				return false;
			}

			theta = rand.nextDouble() * 2 * Math.PI;
			radius = resetCnt*4;
			wrapper.setLoc(startX + radius * Math.cos(theta), startY + radius
					* Math.sin(theta));
		}
		/*
		 * avoid jump out of the bound.
		 */
		return true;
	}
	boolean goodMove(){
		double w = size.getWidth();
		double h = size.getHeight();
		boolean bad =  
			wrapper.bigBounds.x < 0 || 
			wrapper.bigBounds.y < 0 || 
			wrapper.bigBounds.x + wrapper.getWidth() > w || 
			wrapper.bigBounds.y + wrapper.getHeight() > h;
		return !bad;
	}
	boolean move(){
		double w = size.getWidth();
		double h = size.getHeight();
//		System.out.println(startX+","+startY);
		do
		{
			wrapper.setLoc(startX + radius * Math.cos(theta), startY + radius
					* Math.sin(theta));
			theta += angleStep;
			radius += radiusStep;
//			System.out.println(radius);
			/*
			 * test
			 */
//			System.out.println("x = "+ dt.bigBounds.x + ", y = "+ dt.bigBounds.y+ ", w = "+dt.bigBounds.width + ", h = "+ dt.bigBounds.height);
			if (radius > w && radius > h || overallShape == LayoutShape.ROUND)
			{	
//				System.out.println(overallShape == LayoutShape.ROUND);
				resetCnt++;
//				System.out.println(moveResetTime+"<<");
				if (resetCnt > moveResetTime+10) {
					return false;
				}
				
				theta = rand.nextDouble() * 2 * Math.PI;
				radius = resetCnt*4;
				wrapper.setLoc(startX + radius * Math.cos(theta), startY + radius
						* Math.sin(theta));
//				System.out.println(resetCnt);
				w*=1.2; 
				h*=1.2;
				/*
				 * test
				 */
//				System.out.println("theta = "+theta+ " ,  radius = "+ radius);
//				return;
			}
			/*
			 * avoid jump out of the bound.
			 */
		} while (wrapper.bigBounds.x < 0 || wrapper.bigBounds.y < 0
				|| wrapper.bigBounds.x + wrapper.getWidth() > w
				|| wrapper.bigBounds.y + wrapper.getHeight() > h);
		return true;
	}
}