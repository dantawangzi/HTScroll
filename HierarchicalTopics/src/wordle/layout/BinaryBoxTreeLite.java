package wordle.layout;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;


class BinaryBoxTreeLite {
	static LinkedList<BinaryBoxTreeLite> cache = new LinkedList();
	private static final BinaryBoxTreeLite[] ZERO_CHILD = new BinaryBoxTreeLite[0];

	private WordleWrapperLite wrapper;
	private Rectangle2D.Double bounds;
	private BinaryBoxTreeLite[] children = ZERO_CHILD;

	static double hExpand = -1;
	static double vExpand = -1;
	static double hMin = -1;
	static double vMin = -1;
	
	BinaryBoxTreeLite(WordleWrapperLite wrapper, Shape s, Rectangle2D b) {
		this.wrapper = wrapper;
		this.bounds = new Rectangle2D.Double(0, 0, b.getWidth(), b.getHeight());
//		final double hSpace = 1.8 * Math.log(b.getWidth()); //WEENY:original
//		final double vSpace = 1.2 + Math.log(b.getHeight());//WEENY:original
//		split(s, Math.max(5, b.getWidth() / 100));
		split(s,hMin>0 ? hMin : Math.max(10, b.getWidth()/100), 
				vMin>0 ? vMin : Math.max(5, b.getWidth()/100));
		expand(hExpand>0 ? hExpand : Math.min(5,(2+Math.log(b.getWidth()))/2),
			   vExpand>0 ? vExpand : Math.min(4,(2+Math.log(b.getHeight()))/2));
	}

	private BinaryBoxTreeLite(WordleWrapperLite sb, double x, double y, double w, double h) {
		this.wrapper = sb;
		this.bounds = new Rectangle2D.Double(x, y, w, h);
	}

	private void expand(final double hspace, final double vspace) {
		bounds.x -= hspace;
		bounds.y -= vspace;
		bounds.width += hspace * 2;
		bounds.height += vspace * 2;
		for (final BinaryBoxTreeLite k : children)
			k.expand(hspace, vspace);
	}

	private static BinaryBoxTreeLite instance(WordleWrapperLite wrapper, 
			double x, double y, double width, double height) {
		if (cache.isEmpty()) {
			return new BinaryBoxTreeLite(wrapper, x, y, width, height);
		}
		else{
			BinaryBoxTreeLite box = cache.removeFirst();
			box.wrapper = wrapper;
			box.bounds.setFrame(x, y, width, height);
			box.children = ZERO_CHILD;
			return box;
		}
	}
	private void split(final Shape s, final double xMin, final double yMin) {
		BinaryBoxTreeLite a = null, b = null;
		final double myX = bounds.x + wrapper.bigBounds.x;
		final double myY = bounds.y + wrapper.bigBounds.y;
		if (bounds.width >= bounds.height)
		{
			if (bounds.width <= xMin)
				return;
			if (s.intersects(myX, myY, bounds.width / 2, bounds.height))
				a = instance(wrapper, bounds.x, bounds.y,
						bounds.width / 2, bounds.height);
			if (s.intersects(myX + bounds.width / 2, myY, bounds.width / 2,
					bounds.height))
				b = instance(wrapper, bounds.x + bounds.width / 2,
						bounds.y, bounds.width / 2, bounds.height);
		}
		else
		{
			if (bounds.height <= yMin)
				return;
			if (s.intersects(myX, myY, bounds.width, bounds.height / 2))
				a = instance(wrapper, bounds.x, bounds.y, bounds.width,
						bounds.height / 2);
			if (s.intersects(myX, myY + bounds.height / 2, bounds.width,
					bounds.height / 2))
				b = instance(wrapper, bounds.x, bounds.y + bounds.height
						/ 2, bounds.width, bounds.height / 2);
		}
		if (a == null && b == null)
			return;
		if (a == null)
			children = new BinaryBoxTreeLite[] { b };
		else if (b == null)
			children = new BinaryBoxTreeLite[] { a };
		else
			children = new BinaryBoxTreeLite[] { a, b };
		if (a != null)
			a.split(s, xMin, yMin);
		if (b != null)
			b.split(s, xMin, yMin);

		// prune filled leaves
		if (a != null && a.children.length == 0 && 
			b != null && b.children.length == 0){
			cache.addLast(a);
			cache.addLast(b);
			children = ZERO_CHILD;
		}
	}
	@SuppressWarnings("unused")
	private void split(final Shape shape, final double minSize)
	{
		BinaryBoxTreeLite a = null, b = null;
		final double myX = bounds.x + wrapper.bigBounds.x;
		final double myY = bounds.y + wrapper.bigBounds.y;
		if (bounds.width >= bounds.height)
		{
			if (bounds.width <= minSize)
				return;
			if (shape.intersects(myX, myY, bounds.width / 2, bounds.height))
				a = instance(wrapper, bounds.x, bounds.y,
						bounds.width / 2, bounds.height);
			if (shape.intersects(myX + bounds.width / 2, myY, bounds.width / 2,
					bounds.height))
				b = instance(wrapper, bounds.x + bounds.width / 2,
						bounds.y, bounds.width / 2, bounds.height);
		}
		else
		{
			if (bounds.height <= minSize)
				return;
			if (shape.intersects(myX, myY, bounds.width, bounds.height / 2))
				a = instance(wrapper, bounds.x, bounds.y, bounds.width,
						bounds.height / 2);
			if (shape.intersects(myX, myY + bounds.height / 2, bounds.width,
					bounds.height / 2))
				b = instance(wrapper, bounds.x, bounds.y + bounds.height
						/ 2, bounds.width, bounds.height / 2);
		}
		if (a == null && b == null)
			return;
		if (a == null)
			children = new BinaryBoxTreeLite[] { b };
		else if (b == null)
			children = new BinaryBoxTreeLite[] { a };
		else
			children = new BinaryBoxTreeLite[] { a, b };
		if (a != null)
			a.split(shape, minSize);
		if (b != null)
			b.split(shape, minSize);

		// prune filled leaves
		if (a != null && a.children.length == 0 && 
			b != null && b.children.length == 0){
			cache.addLast(a);
			cache.addLast(b);
			children = ZERO_CHILD;
		}
	}

	boolean intersects(final BinaryBoxTreeLite b)
	{
		final double 
		x = b.bounds.x + b.wrapper.bigBounds.x, 
		y = b.bounds.y + b.wrapper.bigBounds.y, 
		w = b.bounds.width, 
		h = b.bounds.height;
		final double myX = bounds.x + wrapper.bigBounds.x;
		final double myY = bounds.y + wrapper.bigBounds.y;
		if (!(x + w > myX && y + h > myY && x < myX + bounds.width && y < myY
				+ bounds.height))
			return false;

		if (children.length == 0)
			return b.children.length == 0 ? true : b.intersects(this);

		for (final BinaryBoxTreeLite kid : children)
			if (kid.intersects(b))
				return true;

		return false;
	}

	private static final Stroke STROKE = new BasicStroke(.25f);
	private static final Color FILLCOLOR = new Color(1f, 0f, 0f, .1f);

	void drawSelf(final Graphics2D g)
	{
		if (children.length > 0) {
			for (BinaryBoxTreeLite k : children){
				k.drawSelf(g);
			}
			return;
		}
		final Stroke stroke = g.getStroke();
		g.setStroke(STROKE);
		g.setColor(Color.red);
		g.translate(wrapper.bigBounds.x, wrapper.bigBounds.y);
		g.draw(bounds);
		g.setColor(FILLCOLOR);
		g.fill(bounds);
		g.translate(0-wrapper.bigBounds.x, 0-wrapper.bigBounds.y);
//		g.setTransform(saved);
		g.setStroke(stroke);
	}
}
