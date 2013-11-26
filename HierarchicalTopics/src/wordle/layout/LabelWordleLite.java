
package wordle.layout;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.Bidi;

import static java.awt.geom.AffineTransform.getRotateInstance;

public class LabelWordleLite extends WordleLite
{
	static BufferedImage TEMP_IMG = 
    	new BufferedImage(512, 512, BufferedImage.TYPE_3BYTE_BGR);
	static FontRenderContext FRC = new FontRenderContext(null, true, true);

	public final String text;
	public final Object data;
	
	public final Shape shape;
	
	private int bias = 0;
	public LabelWordleLite(String text, Font font, double angle) {
		this.text = text;
		this.data = text;
		this.shape = generate(font, text, angle);
		Graphics2D g = (Graphics2D) TEMP_IMG.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, TEMP_IMG.getWidth(), TEMP_IMG.getHeight());
        g.setFont(font);
        
        FontMetrics fm = g.getFontMetrics(font);
        Rectangle2D strBound = fm.getStringBounds(text, g);
        
        g.setColor(Color.BLACK);	        
        g.drawString(text, 0, fm.getAscent());
        
        for(bias = 0; bias<TEMP_IMG.getWidth(); bias++){
        	int h = 0;
        	for(h = 0; h<TEMP_IMG.getHeight()&&h<strBound.getHeight(); h++){
        		if (TEMP_IMG.getRGB(bias, h) != -1) {
        			return;
				}
        	}
        }
	}
	
	public LabelWordleLite(String text, Font font, double angle, Object data) {
		this.text = text;
		this.data = data;
		this.shape = generate(font, text, angle);
	}

	public Shape getShape() {
		return shape;
	}
	
	Shape generate(Font font, String text, double angle) {
		Shape result;
		final char[] chars = text.toCharArray();
		final int dir = Bidi.requiresBidi(chars, 0, text.length()) ? 
				Font.LAYOUT_RIGHT_TO_LEFT : Font.LAYOUT_LEFT_TO_RIGHT;
		int len = text.length();
		result = font.layoutGlyphVector(FRC, chars, 0, len, dir).getOutline();
		if (angle != 0.0) {
			result = getRotateInstance(angle).createTransformedShape(result);
		}
		return result;
	}
	
	@Override
	void setLocation(double x, double y) {
		super.setLocation(x-bias, y);
	}
}