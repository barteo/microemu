/*
 *  MicroEmulator
 *  Copyright (C) 2002-2003 Bartek Teodorczyk <barteo@it.pl>
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.barteo.emulator.app.ui.swt;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;


public class SwtGraphics 
{
	private Display display;
	private GC gc;
	private int transX = 0;
	private int transY = 0;
	private HashMap colors;
	

	public SwtGraphics(Display display) 
	{
		this.display = display;
		this.gc = new GC(display);
	}


	public SwtGraphics(GC gc)
	{
		this.gc = gc;
	}


	public void dispose()
	{
		gc.dispose();
		
		if (colors != null) {
			for (Iterator it = colors.values().iterator(); it.hasNext(); ) {
				((Color) it.next()).dispose();
			}
		}
	}


	public void drawImage(Image image, int srcX, int srcY, int srcWidth, int srcHeight,
			int destX, int destY, int destWidth, int destHeight) 
	{
		gc.drawImage(image, srcX, srcY, srcWidth, srcHeight, destX + transX, destY + transY, destWidth, destHeight);
	}


	public void drawImage(Image image, int x, int y) 
	{
		gc.drawImage(image, x + transX, y + transY);
	}
	
	
	public void translate(int x, int y)
	{
		transX += x;
		transY += y;
	}
	
	
	public Color getColor(RGB rgb)
	{
		if (colors == null) {
			colors = new HashMap();
		}
		
		Color result = (Color) colors.get(rgb);
		if (result == null) {
			result = new Color(display, rgb);
			colors.put(rgb, result);
		}
		
		return result;
	}


	public FontMetrics getFontMetrics() 
	{
		return gc.getFontMetrics();
	}


	public void setFont(Font font) 
	{
		gc.setFont(font);
	}


	public Color getBackground() 
	{
		return gc.getBackground();
	}


	public Color getForeground() 
	{
		return gc.getForeground();
	}


	public void setBackground(Color color) 
	{
		gc.setBackground(color);
	}


	public void setForeground(Color color) 
	{
		gc.setForeground(color);
	}


	public Rectangle getClipping() 
	{
		return gc.getClipping();
	}


	public void setClipping(int x, int y, int width, int height) 
	{
		gc.setClipping(x + transX, y + transY, width, height);
	}


	public void drawArc(int x, int y, int width, int height, int startAngle, int endAngle) 
	{
		gc.drawArc(x + transX, y + transY, width, height, startAngle, endAngle);
	}


	public void drawLine(int x1, int y1, int x2, int y2) 
	{
		gc.drawLine(x1 + transX, y1 + transY, x2 + transX, y2 + transY);
	}


	public void drawRoundRectangle(int x, int y, int width, int height, int arcWidth, int arcHeight) 
	{
		gc.drawRoundRectangle(x + transX, y + transY, width, height, arcWidth, arcHeight);
	}


	public void drawString(String string, int x, int y, boolean isTransparent) 
	{
		gc.drawString(string, x + transX, y + transY, isTransparent);
	}


	public void fillArc(int x, int y, int width, int height, int startAngle, int endAngle) 
	{
		gc.fillArc(x + transX, y + transY, width, height, startAngle, endAngle);
	}


	public void fillRectangle(int x, int y, int width, int height) 
	{
		gc.fillRectangle(x + transX, y + transY, width, height);
	}


	public void fillRoundRectangle(int x, int y, int width, int height, int arcWidth, int arcHeight) 
	{
		gc.fillRoundRectangle(x + transX, y + transY, width, height, arcWidth, arcHeight);
	}


	public int stringWidth(String string) 
	{
		return gc.stringExtent(string).x;
	}


	public Font getFont() 
	{
		return gc.getFont();
	}


	public void setClipping(Rectangle rect) 
	{
		Rectangle tmp = new Rectangle(rect.x + transX, rect.y + transY, rect.width, rect.height);
		gc.setClipping(tmp);
	}

}
