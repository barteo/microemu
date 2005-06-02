/*
 * MicroEmulator 
 * Copyright (C) 2001 Bartek Teodorczyk <barteo@it.pl>
 * 
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Contributor(s): 
 *   3GLab
 */

package javax.microedition.lcdui;

public class Graphics 
{
	public static final int SOLID = 0;
	public static final int DOTTED = 1;

	public static final int LEFT = 4;
	public static final int RIGHT = 8;
	public static final int TOP = 16;
	public static final int BASELINE = 64;
	public static final int BOTTOM = 32;
	public static final int HCENTER = 1;
	public static final int VCENTER = 2;

	int strokeStyle = SOLID;

	int translateX = 0;
	int translateY = 0;

	
	public void clipRect(int x, int y, int width, int height) 
	{
		// Implemented in DisplayGraphics
	}

	
	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) 
	{
		// Implemented in DisplayGraphics
	}

	
	public void drawChar(char character, int x, int y, int anchor) 
	{
		char[] carr = new char[1];
		carr[0] = character;

		drawString(new String(carr), x, y, anchor);
	}

	
	public void drawChars(char[] data, int offset, int length, int x, int y, int anchor) 
	{
		drawString(new String(data, offset, length), x, y, anchor);
	}

	
	public void drawImage(Image img, int x, int y, int anchor) 
	{
		// Implemented in DisplayGraphics
	}

	
	public void drawLine(int x1, int y1, int x2, int y2) 
	{
		// Implemented in DisplayGraphics
	}

	
	public void drawRect(int x, int y, int width, int height) 
	{
		// Implemented in DisplayGraphics
	}

	
	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		// Implemented in DisplayGraphics
	}

	
	public void drawString(String str, int x, int y, int anchor) 
	{
		// Implemented in DisplayGraphics
	}

	
	public void drawSubstring(String str, int offset, int len, int x, int y, int anchor) 
	{
		drawString(str.substring(offset, offset + len), x, y, anchor);
	}

	
	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) 
	{
		// Implemented in DisplayGraphics
	}

	
	public void fillRect(int x, int y, int width, int height) 
	{
		// Implemented in DisplayGraphics
	}

	
	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) 
	{
		// Implemented in DisplayGraphics
	}

	
	public int getBlueComponent() 
	{
		return getColor() & 255;
	}

	
	public int getClipHeight() 
	{
		// Implemented in DisplayGraphics
		throw new IllegalStateException();
	}

	
	public int getClipWidth() 
	{
		// Implemented in DisplayGraphics
		throw new IllegalStateException();
	}

	
	public int getClipX() 
	{
		// Implemented in DisplayGraphics
		throw new IllegalStateException();
	}

	
	public int getClipY() 
	{
		// Implemented in DisplayGraphics
		throw new IllegalStateException();
	}

	
	public int getColor() 
	{
		// Implemented in DisplayGraphics
		throw new IllegalStateException();
	}

	
	public Font getFont() 
	{
		// Implemented in DisplayGraphics
		throw new IllegalStateException();
	}

	
	public int getGrayScale() 
	{
		return (getRedComponent() + getGreenComponent() + getBlueComponent()) / 3;
	}

	
	public int getGreenComponent() 
	{
		return (getColor() >> 8) & 255;
	}

	
	public int getRedComponent() 
	{
		return (getColor() >> 16) & 255;
	}

	
	public int getStrokeStyle() 
	{
		return strokeStyle;
	}

	
	public int getTranslateX() 
	{
		return translateX;
	}

	
	public int getTranslateY() 
	{
		return translateY;
	}

	
	public void setClip(int x, int y, int width, int height) 
	{
		// Implemented in DisplayGraphics
	}

	
	public void setColor(int RGB) 
	{
		// Implemented in DisplayGraphics
	}

	
	public void setColor(int red, int green, int blue) 
	{
		int rgb = blue; //0XRRGGBB
		rgb += green << 8;
		rgb += red << 16;
		setColor(rgb);
	}

	
	public void setFont(Font font) 
	{
		// Implemented in DisplayGraphics
	}

	
	public void setGrayScale(int grey) 
	{
		setColor(grey, grey, grey);
	}

	
	public void setStrokeStyle(int style) 
	{
		if (style != SOLID && style != DOTTED) {
			throw new IllegalArgumentException();
		}
		strokeStyle = style;
	}

	
	public void translate(int x, int y) 
	{
		translateX += x;
		translateY += y;
	}

}
