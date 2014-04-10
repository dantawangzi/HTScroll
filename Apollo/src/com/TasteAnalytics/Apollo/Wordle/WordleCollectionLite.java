package com.TasteAnalytics.Apollo.Wordle;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Iterator;


class WordleCollectionLite {
	
	private final WordleWrapperLite[] wrappers;
	WordleCollectionLite(final WordleWrapperLite[] wrappers) {
		this.wrappers = wrappers.clone();
	}
	WordleCollectionLite(final Collection<WordleWrapperLite> wrappers){
		this.wrappers = new WordleWrapperLite[wrappers.size()];
		int idx = 0;
		for(Iterator<WordleWrapperLite> it = wrappers.iterator(); it.hasNext();){
			this.wrappers[idx++] = it.next();
		}
	}
	
	private QuadTreeLite spatialIndex = null;

	void initLayout(final Dimension2D dim, PositionInitiatorLite pi){
		Rectangle2D rect = new Rectangle2D.Double();
		rect.setFrameFromDiagonal(0, 0, dim.getWidth(), dim.getHeight());
		spatialIndex = new QuadTreeLite(rect, 200);
		for (final WordleWrapperLite s : wrappers){
			s.bePrepared();
		}
		pi.initialize(wrappers);
	}
	void cleanupLayout(){
		for (final WordleWrapperLite s : wrappers){
			s.cleanup();
		}
		BinaryBoxTreeLite.cache.clear();
	}
	WordleWrapperLite intersects(final WordleWrapperLite candidate){
		final WordleWrapperLite lastHit = candidate.getLastHit();
		if (lastHit != null) {
			if (candidate.intersects(lastHit))
				return lastHit;
		}
		WordleWrapperLite symbol = spatialIndex.intersects(candidate);
		if (symbol != null) {
			return symbol;
		}
		setLayOut(candidate);
		return null;
	}
	private void setLayOut(final WordleWrapperLite candidate){
		spatialIndex.add(candidate);
		increaseBounds(candidate);
	}
	Rectangle2D bounds = null;
	private void increaseBounds(final WordleWrapperLite candidate){
		if (bounds == null) {
			bounds = new Rectangle2D.Double();
			bounds.setFrame(candidate.getBounds());
		}
		else {
			bounds.add(candidate.getBounds());
		}
	}
	WordleWrapperLite[] getDrawables(){
		return wrappers;
	}
	
	Dimension2D getWorldSize(final double aspectRatio){
		double totalArea = 0;
		double maxWidth = 0;
		double maxHeight = 0;
		for (final WordleWrapperLite dt : wrappers) {
			totalArea += dt.getArea();
			maxWidth = Math.max(maxWidth, dt.getWidth());
			maxHeight = Math.max(maxHeight, dt.getHeight());
		}
		final double width = Math.max(maxWidth, Math.sqrt(aspectRatio * totalArea));
		final double height = Math.max(maxHeight, Math.sqrt(totalArea / aspectRatio));
		return new Dimension(width * 1.2, height * 1.2);
	}
}
class Dimension extends Dimension2D {
	final double width, height;

	Dimension(final double width, final double height) {
		super();
		this.width = width;
		this.height = height;
	}
	public double getWidth() {
		return width;
	}
	public double getHeight() {
		return height;
	}
	public void setSize(final double width, final double height) {
		throw new UnsupportedOperationException("Size is immutable.");
	}
}