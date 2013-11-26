package wordle.layout;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.imageio.ImageIO;


public class WordleAlgorithmLite {

	public static enum LayoutShape {SQUARED, ROUND}
	public static enum InitialPosition{ALPHABETIC, CENTER_OUT, CENTER_LINE}

	private int moveResetTime = 1;
	private Rectangle2D bound = null;	
	private boolean autoAdjustingBound = true;
	private boolean cleanAfterDone = true;
	private InitialPosition initialPosition = InitialPosition.CENTER_OUT;
	private LayoutShape layoutShape = LayoutShape.ROUND;
	private boolean dumpLayout = false;
	private String dumpFileName = null;
	
	private PositionInitiatorLite getInitLayoutMethod(Dimension2D dim){
		if (initialPosition == InitialPosition.ALPHABETIC) {
			return new AlphabeticInitiator(dim);
		}
		else if (initialPosition == InitialPosition.CENTER_OUT) {
			return new CenterOutInitiator(dim);
		}
		else {
			return new CenterLineInitiator(dim);
		}
	}
	
	public void displayParameters(){
		System.out.println("Angle step(radian): "+SpiralWordleFitterLite.angleStep);
		System.out.println("Radius step(pixel): "+SpiralWordleFitterLite.radiusStep);
		System.out.println("Horizontal expansion(pixel): "+BinaryBoxTreeLite.hExpand);
		System.out.println("Vertical expansion(pixel): "+BinaryBoxTreeLite.vExpand);
		System.out.println("Horizontal min size(pixel): "+BinaryBoxTreeLite.hMin);
		System.out.println("Vertical min size(pixel): "+BinaryBoxTreeLite.vMin);
		System.out.println("Move reset time(integer): "+moveResetTime);
		System.out.println("Clean after done(bool): "+cleanAfterDone);
		System.out.println("Adjusting bound(bool): "+autoAdjustingBound);
		System.out.println("Initial position(enum): "+initialPosition);
		System.out.println("Layout shape(enum): "+layoutShape);
	}
	public void setSpiralAngleStep(double step){
		SpiralWordleFitterLite.angleStep = step;
	}
	public void setSpiralRadiusStep(double step){
		SpiralWordleFitterLite.radiusStep = step;
	}
	public void setDumpLayout(boolean flag, String fileName){
		dumpLayout = flag;
		dumpFileName = fileName;
	}
	public void setHorizontalExpansion(double hExpand) {
		BinaryBoxTreeLite.hExpand = hExpand;
	}
	public void setVerticalExpansion(double vExpand) {
		BinaryBoxTreeLite.vExpand = vExpand;
	}
	public void setHorizontalMinSize(double hMin) {
		BinaryBoxTreeLite.hMin = hMin;
	}
	public void setVerticalMinSize(double vMin) {
		BinaryBoxTreeLite.vMin = vMin;
	}
	public void setMoveResetTime(int time) {
		this.moveResetTime = time;
	}
	public void setInitialPosition(InitialPosition initPosition){
		initialPosition = initPosition;
	}
	public void setCleanAfterDone(boolean flag){
		this.cleanAfterDone = flag;
	}
	public void setAutoAdjustingBound(boolean flag){
		autoAdjustingBound = flag;
	}
	public WordleAlgorithmLite(double width, double height){
		this.bound = new Rectangle2D.Double(0, 0, width, height);
	}
	public WordleAlgorithmLite(Rectangle2D preferedBound) {
		this.bound = preferedBound;
	}
//	
//	public void calc(Collection<? extends WordleLite> wordles, PositionInitiatorLite pi){
//		List<WordleWrapperLite> list = new ArrayList<WordleWrapperLite>();
//		for(WordleLite wordle : wordles) {
//			list.add(new WordleWrapperLite(wordle));
//		}
//		WordleCollectionLite wrappers = new WordleCollectionLite(list);
//		
//		//get the actual layout bound
//		Dimension2D dim = new Dimension(bound.getWidth(), bound.getHeight());
//		if (autoAdjustingBound) {
//			dim = wrappers.getWorldSize(bound.getWidth()/bound.getHeight());
//		}
//		
//		wrappers.initLayout(dim, pi);
//
//		for(WordleWrapperLite word : wrappers.getDrawables()){
//			if (word.isLayedOut()) {
//				continue;
//			}
//			pi.initPosition(word);
//			SpiralWordleFitterLite mover = 
//				new SpiralWordleFitterLite(word, dim, layoutShape, moveResetTime);
//			boolean flag = false;
//			while (wrappers.intersects(word) != null){
//				flag = mover.move();
//			}
//			word.setLayedOut(flag);
//		}
//		if(cleanAfterDone) {
//			wrappers.cleanupLayout();
//		}
//	}	
	
	
	
//	public void place(Collection<? extends WordleLite> wordles){
//		Dimension2D rawDim = new Dimension(bound.getWidth(), bound.getHeight());
//		double frac = 1.5;
//		WHILE: while (true){
//			List<WordleWrapperLite> list = new ArrayList<WordleWrapperLite>();
//			for(WordleLite wordle : wordles) {
//				list.add(new WordleWrapperLite(wordle));
//			}
//			Dimension2D dim = new Dimension(
//					rawDim.getWidth()*frac, rawDim.getHeight()*frac);
//			System.out.println(dim.getWidth()+":"+dim.getHeight());
//			WordleCollectionLite wrappers = new WordleCollectionLite(list);
//			PositionInitiatorLite strategy = getInitLayoutMethod(dim);
//			for(WordleWrapperLite word : wrappers.getDrawables()){
//				word.setLayedOut(false);
//			}
//			
//			wrappers.initLayout(dim, strategy);
//			for(WordleWrapperLite word : wrappers.getDrawables()){
//				if (word.isLayedOut()) {
//					continue;
//				}
//				strategy.initPosition(word);
//				SpiralWordleFitterLite mover = 
//					new SpiralWordleFitterLite(word, dim, layoutShape, moveResetTime);
//				boolean flag = false;
////				System.out.println(word.bigBounds.x+", "+word.bigBounds.y+"<<");
////				System.out.println(word.bigBounds);
//				while (wrappers.intersects(word) != null){
//					flag = mover.move();
//				}
//				word.setLayedOut(flag);
//				if (!flag) {
//					if(cleanAfterDone) {
//						wrappers.cleanupLayout();
//					}
//					frac +=0.1;
//					continue WHILE;
//				}
//				else {
//					System.out.println(flag);
//				}
//			}
//			if (dumpLayout && dumpFileName != null) {
//				dump2Pdf(list, dumpFileName);
//			}
//			if(cleanAfterDone) {
//				wrappers.cleanupLayout();
//			}
//			break;
//		}
//	}
	
	public void place(Collection<? extends WordleLite> wordles){
		List<WordleWrapperLite> list = new ArrayList<WordleWrapperLite>();
		for(WordleLite wordle : wordles) {
			list.add(new WordleWrapperLite(wordle));
		}
		WordleCollectionLite wrappers = new WordleCollectionLite(list);
		//get the actual layout bound
		Dimension2D dim = new Dimension(bound.getWidth(), bound.getHeight());
               
		if (autoAdjustingBound) {
			dim = wrappers.getWorldSize(bound.getWidth()/bound.getHeight());
		}
		dim = new Dimension(dim.getWidth()*1.2, dim.getHeight()*1.2);
                
                 //System.out.println(dim.getHeight() + " " + dim.getWidth());
                 
                 
		PositionInitiatorLite strategy = getInitLayoutMethod(dim);
		
		wrappers.initLayout(dim, strategy);

		for(WordleWrapperLite word : wrappers.getDrawables()){
			if (word.isLayedOut()) {
				continue;
			}
			strategy.initPosition(word);
			SpiralWordleFitterLite mover = 
				new SpiralWordleFitterLite(word, dim, layoutShape, moveResetTime);
			boolean flag = false;
			//System.out.println(word.bigBounds.x+", "+word.bigBounds.y+"<<" + word.bigBounds.width + " " + word.bigBounds.height);
                        
//			System.out.println(word.bigBounds);
			Point2D earlistGood = null;
			while (true){
				mover.blindMovd();
				if (wrappers.intersects(word) == null) {
					if (mover.goodMove()) {
						earlistGood = null;
						break;
					}
					else if (earlistGood == null){
						earlistGood = mover.lastLoc();
					}
				}
				if (mover.radius > dim.getWidth() 
						&& mover.radius > dim.getHeight()) {
					break;
				}
			}
			if (earlistGood != null) {
				word.setLoc(earlistGood.getX(), earlistGood.getY());
			}
//			while (wrappers.intersects(word) != null){
//				flag = mover.move();
//			}
			word.setLayedOut(flag);
		}
		if (dumpLayout && dumpFileName != null) {
			dump2Pdf(list, dumpFileName);
		}
		if(cleanAfterDone) {
			wrappers.cleanupLayout();
		}
                
                Rectangle2D rect = null;
                for (WordleWrapperLite word : wrappers.getDrawables()) {
                    
                        rect = (Rectangle2D) word.getBounds().clone();			
                        //System.out.println(rect.getX() + " " + rect.getY() + " " + rect.getWidth() + " " + rect.getHeight());
		}
                
                
	}
	private void dump2Pdf(List<WordleWrapperLite> wrappers, String fileName){
		//dump the correct wordle result to the file for debug purpose
		Rectangle2D rect = null;
		for (WordleWrapperLite wordleWrapper : wrappers) {
			if (rect == null) {
				rect = (Rectangle2D) wordleWrapper.getBounds().clone();
			}
			else {
				rect.add(wordleWrapper.getBounds());
			}
		}
		int w = (int) rect.getWidth();
		int h = (int) rect.getHeight();
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g2 = bi.createGraphics();
		g2.translate(rect.getX(), rect.getY());
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, w, h);
		g2.setColor(Color.BLACK);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		for (final WordleWrapperLite dt : wrappers){
			dt.draw(g2, null, true);
		}
		try {
			ImageIO.write(bi, "PNG", new File(fileName));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.out.println("output wordle result: "+fileName);
		return;
//		try {
//			try {
//				Class.forName("com.itextpdf.text.Document");
//			} catch (ClassNotFoundException e) {
//				BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
//				Graphics2D g2 = bi.createGraphics();
//				g2.translate(rect.getX(), rect.getY());
//				g2.setColor(Color.WHITE);
//				g2.fillRect(0, 0, w, h);
//				g2.setColor(Color.BLACK);
//				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//						RenderingHints.VALUE_ANTIALIAS_ON);
//				g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
//						RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//				for (final WordleWrapper dt : wrappers){
//					dt.draw(g2, null, true);
//				}
//				try {
//					ImageIO.write(bi, "PNG", new File(fileName));
//				} catch (IOException e1) {
//					e1.printStackTrace();
//				}
//				return;
//			}
//			com.itextpdf.text.Document doc = 
//				new com.itextpdf.text.Document(new com.itextpdf.text.Rectangle(w, h));
//			com.itextpdf.text.pdf.PdfWriter writer = 
//				com.itextpdf.text.pdf.PdfWriter.getInstance(doc,new java.io.FileOutputStream(fileName));
//			doc.open();
//			com.itextpdf.text.pdf.PdfContentByte cb = writer.getDirectContent();
//			Graphics2D g2 = cb.createGraphics(w, h);
//			g2.translate(rect.getX(), rect.getY());
//			g2.setColor(Color.WHITE);
//			g2.fillRect(0, 0, w, h);
//
//			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//					RenderingHints.VALUE_ANTIALIAS_ON);
//			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
//					RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//
//			for (final WordleWrapper dt : wrappers){
//				dt.draw(g2, null, true);
//			}
//			g2.dispose();
//			g2.translate(0-rect.getX(), 0-rect.getY());
//			doc.close();
//			System.out.println("output wordle result: done");
//		} catch (java.io.FileNotFoundException e1) {
//			e1.printStackTrace();
//		} catch (com.itextpdf.text.DocumentException e1) {
//			e1.printStackTrace();
//		}
	}
}