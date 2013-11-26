package wordle.layout;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.util.Comparator;


class WordleWrapperLite {

	Color getColor() {
		return Color.BLACK;
	}

	private WordleLite rawWordle = null;

	WordleLite getWordle() {
		return rawWordle;
	}

	WordleWrapperLite lastHit = null;
	double xOffset, yOffset;
	BinaryBoxTreeLite bbt;
	Rectangle2D.Double bigBounds = null;

	Shape getShape() {
		return rawWordle.getShape();
	}

	WordleWrapperLite(WordleLite rawGlyph) {
		this.rawWordle = rawGlyph;
		rawGlyph.setLayouted(false);
	}

	void ensureBigBounds() {
		if (bigBounds == null) {
			bigBounds = new Rectangle2D.Double();
			bigBounds.setFrame(getShape().getBounds2D());
			xOffset = 0 - bigBounds.x;
			yOffset = 0 - bigBounds.y;
		}
	}

	void bePrepared() {
		ensureBigBounds();
		bbt = new BinaryBoxTreeLite(this, getShape(), bigBounds);
	}

	void cleanup() {
		bbt = null;
	}

	WordleWrapperLite getLastHit() {
		return lastHit;
	}

	boolean intersects(final Rectangle2D r) {
		ensureBigBounds();
		return bigBounds.intersects(r);
	}

	boolean contains(final double x, final double y) {
		ensureBigBounds();
		return bigBounds.contains(x, y);
	}

	Point2D getLocation() {
		ensureBigBounds();
		return new Point2D.Double(bigBounds.x, bigBounds.y);
	}

	void setLoc(final double newX, final double newY) {
		ensureBigBounds();
		bigBounds.setFrame(newX, newY, bigBounds.width, bigBounds.height);
		rawWordle.setLocation(newX+xOffset, newY+yOffset);
	}

	void draw(Graphics2D g, ImageObserver o, boolean debug) {
		ensureBigBounds();
		final Shape glyphs = getShape();
		double x = bigBounds.x+xOffset;
		double y = bigBounds.y+yOffset;
		g.translate(x, y);
		try {
			g.setColor(getColor());
			g.fill(glyphs);
		} finally {
			g.translate(0-x, 0-y);
		}
		if (debug) {
			if (bbt == null) {
				System.err.println("bbt is cleaned, so cannot paint it");
			}
			else {
				bbt.drawSelf(g);
			}
		}
	}

	double getArea() {
		ensureBigBounds();
		return bigBounds.width * bigBounds.height;
	}

	double getWidth() {
		ensureBigBounds();
		return bigBounds.width;
	}

	double getHeight() {
		ensureBigBounds();
		return bigBounds.height;
	}

	boolean isLayedOut() {
		return rawWordle.isLayouted();
	}

	void setLayedOut(final boolean layedOut) {
		rawWordle.setLayouted(layedOut);
	}

	boolean intersects(final WordleWrapperLite s) {
		final boolean hit = bbt.intersects(s.bbt);
		if (hit)
			lastHit = s;
		return hit;
	}

	Rectangle2D getBounds() {
		ensureBigBounds();
		return bigBounds;
	}

	static final Comparator<WordleWrapperLite> AREA_ASCENDING = new Comparator<WordleWrapperLite>() {
		public int compare(WordleWrapperLite o1, WordleWrapperLite o2) {
			return Double.compare(o1.getArea(), o2.getArea());
		}
	};

	static final Comparator<WordleWrapperLite> AREA_DESCENDING = new Comparator<WordleWrapperLite>() {
		public int compare(WordleWrapperLite o1, WordleWrapperLite o2) {
			return Double.compare(o2.getArea(), o1.getArea());
		}
	};
}
