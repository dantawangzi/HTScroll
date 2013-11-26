/**
 * 
 */
package org.mediavirus.parvis.gui.temporalView.renderer;

import java.awt.Color;
import java.awt.Graphics2D;


public class ColorBrewer {

	public final static float colors[][] = {
		{ 141f / 255f, 211f / 255f, 199f / 255f },
		{ 190f / 255f, 186f / 255f, 218f / 255f },
		{ 251f / 255f, 128f / 255f, 114f / 255f },
		{ 128f / 255f, 177f / 255f, 211f / 255f },
		{ 253f / 255f, 180f / 255f, 98f / 255f },
		{ 179f / 255f, 222f / 255f, 105f / 255f },
		{ 252f / 255f, 205f / 255f, 229f / 255f },
		{ 217f / 255f, 217f / 255f, 217f / 255f },
		{ 188f / 255f, 128f / 255f, 189f / 255f },
		{ 204f / 255f, 235f / 255f, 197f / 255f },
		{ 255f / 255f, 237f / 255f, 111f / 255f },
		{ 255f / 255f, 255f / 255f, 179f / 255f }};



public static Color getColor(int colorNum, boolean darker) {
	colorNum = colorNum % colors.length;
	if (darker)
		return Color.getHSBColor(colors[colorNum][0]*.75f, colors[colorNum][1]*.75f, colors[colorNum][2]*.75f);
	else
		return Color.getHSBColor(colors[colorNum][0], colors[colorNum][1], colors[colorNum][2]);
}
	
}
