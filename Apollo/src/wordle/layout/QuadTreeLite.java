package wordle.layout;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;


class QuadTreeLite
{
	// The center of this tree
	private final double cx, cy;
	@SuppressWarnings("unused")
	private final double halfWidth;
	private final QuadTreeLite[] kids;
	private final QuadTreeLite parent;

	// construct lazily
	private List<WordleWrapperLite> objects = null;

	QuadTreeLite(final Rectangle2D bounds, final double minWidth)
	{
		this(null, bounds.getCenterX(), bounds.getCenterY(), bounds.getWidth() / 2,
				minWidth);
	}

	private QuadTreeLite(final QuadTreeLite parent, final double cx, final double cy,
			final double halfWidth, final double minWidth)
	{
		super();
		this.parent = parent;
		this.cx = cx;
		this.cy = cy;
		this.halfWidth = halfWidth;
		if (2 * halfWidth > minWidth)
		{
			final double newWidth = halfWidth / 2;
			kids = new QuadTreeLite[] {
					new QuadTreeLite(this, cx - newWidth, cy - newWidth, newWidth, minWidth),
					new QuadTreeLite(this, cx + newWidth, cy - newWidth, newWidth, minWidth),
					new QuadTreeLite(this, cx - newWidth, cy + newWidth, newWidth, minWidth),
					new QuadTreeLite(this, cx + newWidth, cy + newWidth, newWidth, minWidth) };
		}
		else
		{
			kids = null;
		}
	}

	private QuadTreeLite locate(final Rectangle2D thing)
	{
		if (kids == null)
			return this;

		final double left = thing.getMinX(), right = thing.getMaxX();
		if (left <= cx && right > cx) // straddles vertical
			return this;

		final double top = thing.getMinY(), bottom = thing.getMaxY();
		if (top <= cy && bottom > cy) // straddles horizontal
			return this;

		if (right <= cx && bottom <= cy)
			return kids[0].locate(thing);
		if (left > cx && bottom <= cy)
			return kids[1].locate(thing);
		if (right <= cx && top > cy)
			return kids[2].locate(thing);
		return kids[3].locate(thing);
	}

	WordleWrapperLite intersects(final WordleWrapperLite candidate)
	{
		final QuadTreeLite home = locate(candidate.getBounds());

		WordleWrapperLite word = home.intersectsBreadthFirst(candidate);
		if (word != null) {
			return word;
		}

		for (QuadTreeLite t = home; t != null; t = t.parent)
		{
			if (t.objects == null)
				continue;
			for (final WordleWrapperLite d : t.objects)
			{
				if (candidate.intersects(d))
					return d;
			}
		}
		return null;
	}

	private WordleWrapperLite intersectsBreadthFirst(final WordleWrapperLite candidate)
	{
		if (objects != null)
		{
			for (final WordleWrapperLite d : objects)
			{
				if (candidate.intersects(d))
					return d;
			}
		}
		if (kids != null)
		{
			for (final QuadTreeLite kid : kids) {
				WordleWrapperLite word = kid.intersectsBreadthFirst(candidate);
				if (word != null) {
					return word;
				}
			}
		}
		return null;
	}

	void add(final WordleWrapperLite d)
	{
		locate(d.getBounds()).insert(d);
	}

	private void insert(final WordleWrapperLite d)
	{
		if (objects == null)
			objects = new ArrayList<WordleWrapperLite>();
		objects.add(d);
	}
}
