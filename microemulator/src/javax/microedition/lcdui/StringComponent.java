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
 */

package javax.microedition.lcdui;

import com.barteo.emulator.device.DeviceFactory;

class StringComponent 
{
	String text;
	int breaks[] = new int[4];
	boolean invertPaint = false;
	int numOfBreaks;
	int width;

	
	StringComponent() 
	{
		this(null);
	}

	
	StringComponent(String text) 
	{
		this.text = text;
		width = DeviceFactory.getDevice().getDeviceDisplay().getWidth();
		updateBreaks();
	}

	
	String getText() 
	{
		return text;
	}

	
	void setText(String text) 
	{
		this.text = text;
		updateBreaks();
	}

	
	int getCharPositionX(int num) 
	{
		int i, prevIndex = 0;
		Font f = Font.getDefaultFont();

		for (i = 0; i < numOfBreaks; i++) {
			if (num < breaks[i]) {
				break;
			}
			prevIndex = breaks[i];
		}
		return f.substringWidth(text, prevIndex, num - prevIndex);
	}

	
	int getCharPositionY(int num) 
	{
		int y = 0;
		Font f = Font.getDefaultFont();

		for (int i = 0; i < numOfBreaks; i++) {
			if (num < breaks[i]) {
				break;
			}
			y += f.getHeight();
		}

		return y;
	}

	
	int getCharHeight() 
	{
		return Font.getDefaultFont().getHeight();
	}

	
	int getHeight() 
	{
		int height;
		Font f = Font.getDefaultFont();

		if (text == null) {
			return 0;
		}

		if (numOfBreaks == 0) {
			return f.getHeight();
		}

		height = numOfBreaks * f.getHeight();

		if (breaks[numOfBreaks - 1] == text.length() - 1
			&& text.charAt(text.length() - 1) == '\n') {
		} else {
			height += f.getHeight();
		}

		return height;
	}

	
	void insertBreak(int pos) 
	{
		int i;

		for (i = 0; i < numOfBreaks; i++) {
			if (pos < breaks[i]) {
				break;
			}
		}
		if (numOfBreaks + 1 == breaks.length) {
			int newbreaks[] = new int[breaks.length + 4];
			System.arraycopy(breaks, 0, newbreaks, 0, numOfBreaks);
			breaks = newbreaks;
		}
		System.arraycopy(breaks, i, breaks, i + 1, numOfBreaks - i);
		breaks[i] = pos;
		numOfBreaks++;
	}

	
	void invertPaint(boolean state) 
	{
		invertPaint = state;
	}

	
	int length() 
	{
		return text.length();
	}

	
	int paint(Graphics g) 
	{
		if (text == null) {
			return 0;
		}

		int i, prevIndex, y;
		Font f = Font.getDefaultFont();

		for (i = prevIndex = y = 0; i < numOfBreaks; i++) {
			if (invertPaint) {
				g.setGrayScale(0);
			} else {
				g.setGrayScale(255);
			}
			g.fillRect(0, y, width, f.getHeight());
			if (invertPaint) {
				g.setGrayScale(255);
			} else {
				g.setGrayScale(0);
			}
			g.drawSubstring(text, prevIndex, breaks[i] - prevIndex, 0, y, 0);
			prevIndex = breaks[i];
			y += f.getHeight();
		}
		if (prevIndex != text.length()) {
			if (invertPaint) {
				g.setGrayScale(0);
			} else {
				g.setGrayScale(255);
			}
			g.fillRect(0, y, width, f.getHeight());
			if (invertPaint) {
				g.setGrayScale(255);
			} else {
				g.setGrayScale(0);
			}
			g.drawSubstring(
				text,
				prevIndex,
				text.length() - prevIndex,
				0,
				y,
				0);
			y += f.getHeight();
		}

		return y;
	}

	
	void setWidth(int width) 
	{
		this.width = width;
		updateBreaks();
	}

	
	void updateBreaks() 
	{
		if (text == null) {
			return;
		}

		int prevIndex = 0;
		int canBreak = 0;
		numOfBreaks = 0;
		Font f = Font.getDefaultFont();

		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == ' ') {
				canBreak = i + 1;
			}
			if (text.charAt(i) == '\n') {
				insertBreak(i);
				canBreak = 0;
				prevIndex = i + 1;
				continue;
			}
			if (f.substringWidth(text, prevIndex, i - prevIndex + 1) > width) {
				if (canBreak != 0) {
					insertBreak(canBreak);
					i = canBreak;
					prevIndex = i;
				} else {
					insertBreak(i);
					prevIndex = i + 1;
				}
				canBreak = 0;
			}
		}
	}

}
