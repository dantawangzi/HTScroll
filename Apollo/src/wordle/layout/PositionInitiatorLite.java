package wordle.layout;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public abstract class PositionInitiatorLite {
	private final Dimension2D size;

	public PositionInitiatorLite(double width, double height){
		this(new Dimension(width, height));
	}
	
	PositionInitiatorLite(final Dimension2D size) {
		this.size = size;
	}

	public void initialize(final WordleWrapperLite[] drawables) {
		Arrays.sort(drawables, WordleWrapperLite.AREA_DESCENDING);
	}

	double getWidth() {
		return size.getWidth();
	}

	double getHeight() {
		return size.getHeight();
	}

	double getCenterY() {
		return size.getHeight() / 2;
	}

	double getCenterX() {
		return size.getWidth() / 2;
	}

	double jitteryY(final WordleWrapperLite t) {
		return getDerriereDistribution(1.4)*1.25
		* Math.min(t.getHeight(),t.getWidth())*randomSign()
		+ getCenterY()-t.getHeight()/2;
	}
	
	protected static final Random RAND = new Random(12345L);
	
	static double getDerriereDistribution(final double centerDensity) {
		return .5 + (Math.pow(RAND.nextDouble(), centerDensity) * randomSign());
	}
	
	static double getSmooveDistribution() {
		double val = gen();
		while (Math.abs(val) > .5)
			val = gen();
		val += .5;
		return val;
	}
	
	static double gen() {
		return 0.6 * randomSign() * (Math.abs(Math.log(RAND.nextDouble())) - 2);
	}
	
	static double randomSign() {
		return RAND.nextDouble() < 0.5 ? -1 : 1;
	}
	
	public abstract Point2D initPosition(WordleLite dt);
	
	void initPosition(WordleWrapperLite dt){
		Point2D pnt = initPosition(dt.getWordle());
		dt.setLoc(pnt.getX(), pnt.getY());
	}
}


class CenterOutInitiator extends PositionInitiatorLite {
	public CenterOutInitiator(final Dimension2D size) {
		super(size);
	}

	public Point2D initPosition(WordleLite wl) {
		Point2D pnt = new Point2D.Double();
		Rectangle2D r = wl.getShape().getBounds2D();
		pnt.setLocation(
				(getWidth()-r.getWidth())/2, 
				(getHeight()-r.getHeight())/2);
		return pnt;
	}
}

class CenterLineInitiator extends PositionInitiatorLite {
	private final static Random RAND = new Random(12345L);
	public CenterLineInitiator(final Dimension2D size) {
		super(size);
	}

	@Override
	void initPosition(final WordleWrapperLite t) {
		t.setLoc(positionMap.get(t.getWordle()), jitteryY(t));
	}

	private final Map<WordleLite, Double> positionMap = new HashMap();

	@Override
	public void initialize(final WordleWrapperLite[] drawables) {
		positionMap.clear();
		for (int i = 0; i < drawables.length; i++){
			double x = RAND.nextDouble()*getWidth()-drawables[i].getWidth()/2;
			positionMap.put(drawables[i].getWordle(), x);
		}
		super.initialize(drawables);
	}

	public Point2D initPosition(WordleLite dt) {
		throw new UnsupportedOperationException();
	}
}

class AlphabeticInitiator extends PositionInitiatorLite implements Comparator<WordleWrapperLite> {
	private static final Method NORMALIZE;
	private static final Object NFKD;
	static {
		Method normalize = null;
		Object nfkd = null;
		try {
			final Class<?> n = Class.forName("java.text.Normalizer");
			Class<?> formEnum = null;
			for (final Class<?> c : n.getClasses()) {
				if (c.getName().equals("java.text.Normalizer$Form")) {
					formEnum = c;
					break;
				}
			}
			if (formEnum == null) {
				throw new RuntimeException("Couldn't find Form");
			}
			nfkd = formEnum.getField("NFKD").get(null);
			normalize = n.getMethod("normalize", CharSequence.class, formEnum);
		}
		catch (final Exception e) {
			System.err.println(e);
		}
		NORMALIZE = normalize;
		NFKD = nfkd;
	}

	private static final String NORMED ="AaEeIiOoUu" // grave
		+ "AaEeIiOoUuYy" // acute
		+ "AaEeIiOoUuYy" // circumflex
		+ "AaOo" // tilde
		+ "AaEeIiOoUuYy" // umlaut
		+ "Aa" // ring
		+ "Cc"; // cedilla

	private static final String ACCENTED = 
		    "\u00C0\u00E0\u00C8\u00E8\u00CC\u00EC\u00D2\u00F2\u00D9\u00F9" +
		    "\u00C1\u00E1\u00C9\u00E9\u00CD\u00ED\u00D3\u00F3\u00DA\u00FA" +
		    "\u00DD\u00FD\u00C2\u00E2\u00CA\u00EA\u00CE\u00EE\u00D4\u00F4" +
		    "\u00DB\u00FB\u0176\u0177\u00C3\u00E3\u00D5\u00F5\u00C4\u00E4" +
		    "\u00CB\u00EB\u00CF\u00EF\u00D6\u00F6\u00DC\u00FC\u0178\u00FF" +
		    "\u00C5\u00E5\u00C7\u00E7";

	public static String normalize(final String s) {
		if (NFKD != null && NORMALIZE != null) {
			try {
				return ((String) NORMALIZE.invoke(null, s, NFKD)).toLowerCase();
			}
			catch (final Exception e) {
				System.err.println(e);
			}
		}

		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			sb.append(normalize(s.charAt(i)));
		}
		return sb.toString().toLowerCase();
	}

	private static char normalize(final char c) {
		try {
			return NORMED.charAt(ACCENTED.indexOf(c));
		}
		catch (final StringIndexOutOfBoundsException expected) {
			return c;
		}
	}

	public AlphabeticInitiator(final Dimension2D size) {
		super(size);
	}

	private final Map<WordleLite, String> sortMap = new HashMap<WordleLite, String>();
	private final Map<WordleLite, Double> posMap = new HashMap<WordleLite, Double>();

	public void initPosition(final WordleWrapperLite t) {
		double y = totalCount>100 ? jitteryY(t) : getCenterY()-t.getHeight()/2;
		t.setLoc(posMap.get(t), y);
	}

	public int compare(final WordleWrapperLite o1, final WordleWrapperLite o2) {
		return sortMap.get(o1).compareTo(sortMap.get(o2));
	}

	private int totalCount = 0;

	@Override
	public void initialize(final WordleWrapperLite[] wrappers) {
		totalCount = wrappers.length;
		sortMap.clear();
		for (final WordleWrapperLite wrapper : wrappers) {
			LabelWordleLite wordle = (LabelWordleLite) wrapper.getWordle();
			sortMap.put(wordle, normalize(wordle.text));
		}
		Arrays.sort(wrappers, this);
		
		posMap.clear();
		final double sliceWidth = getWidth() / wrappers.length;
		for (int i = 0; i < wrappers.length; i++) {
			posMap.put(wrappers[i].getWordle(), i * sliceWidth);
		}
		super.initialize(wrappers);
	}

	public Point2D initPosition(WordleLite dt) {
		throw new UnsupportedOperationException();
	}
}


