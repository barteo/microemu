/*
 * @(#)DisplayGraphics.java  07/07/2001
 *
 * Copyright (c) 2001 Bartek Teodorczyk <barteo@it.pl>. All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package com.barteo.midp.lcdui;

import java.awt.Color;
import java.awt.Graphics;

import javax.microedition.lcdui.*;

import com.barteo.emulator.Device;
import com.barteo.emulator.GrayImageFilter;


public class DisplayGraphics extends javax.microedition.lcdui.Graphics
{

	java.awt.Graphics g;


	protected DisplayGraphics(java.awt.Graphics a_g)
	{
		g = a_g;
	}


	public void clipRect(int x, int y, int width, int height)
	{
		g.clipRect(x, y, width, height);
	}


	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle)
	{
		g.drawArc(x, y, width, height, startAngle, arcAngle);
	}


  public void drawChar(char character, int x, int y, int anchor)
  {
    // Not implemented
  }


  public void drawChars(char[] data, int offset, int length, int x, int y, int anchor)
  {
    // Not implemented
  }


	public void drawImage(Image img, int x, int y, int anchor)
	{
		int newx = x;

		if (anchor == 0) {
			anchor = javax.microedition.lcdui.Graphics.TOP | javax.microedition.lcdui.Graphics.LEFT;
		}

		if ((anchor & javax.microedition.lcdui.Graphics.RIGHT) != 0) {
			newx -= img.getWidth();
		} else if ((anchor & javax.microedition.lcdui.Graphics.HCENTER) != 0) {
			newx -= img.getWidth() / 2;
		}

		g.drawImage(((ImageImpl) img).getImage(), newx, y, null);
	}


	public void drawLine(int x1, int y1, int x2, int y2)
	{
		g.drawLine(x1, y1, x2, y2);
	}


	public void drawRect(int x, int y, int width, int height)
	{
		drawLine(x, y, x + width, y);
		drawLine(x + width, y, x + width, y + height);
		drawLine(x + width, y + height, x, y + height);
		drawLine(x, y + height, x, y);
	}


	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight)
	{
		g.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
	}


	public void drawString(String str, int x, int y, int anchor)
	{
		int newx = x;
		int newy = y;

		if (anchor == 0) {
			anchor = javax.microedition.lcdui.Graphics.TOP | javax.microedition.lcdui.Graphics.LEFT;
		}

		if ((anchor & javax.microedition.lcdui.Graphics.TOP) != 0) {
			newy += g.getFontMetrics().getAscent();
		} else if ((anchor & javax.microedition.lcdui.Graphics.BOTTOM) != 0) {
			newy -= g.getFontMetrics().getDescent();
		}
		if ((anchor & javax.microedition.lcdui.Graphics.HCENTER) != 0) {
			newx -= g.getFontMetrics().stringWidth(str) / 2;
		} else if ((anchor & javax.microedition.lcdui.Graphics.RIGHT) != 0) {
			newx -= g.getFontMetrics().stringWidth(str);
		}
		g.drawString(str, newx, newy);
	}


	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle)
	{
		g.fillArc(x, y, width, height, startAngle, arcAngle);
	}


	public void fillRect(int x, int y, int width, int height)
	{
    g.fillRect(x, y, width, height);
	}


	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight)
	{
		g.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
	}


	public void setClip(int x, int y, int width, int height)
	{
		g.setClip(x, y, width, height);
	}


	public void setColor(int RGB)
	{
    GrayImageFilter filter = new GrayImageFilter();

    g.setColor(new Color(filter.filterRGB(0, 0, RGB)));
	}


	public void setGrayScale(int grey)
	{
		if (grey > 127) {
			g.setColor(Device.getBackgroundColor());
		} else {
			g.setColor(Device.getForegroundColor());
		}
	}


  public void translate(int x, int y)
  {
		super.translate(x, y);
    g.translate(x, y);
  }

}
