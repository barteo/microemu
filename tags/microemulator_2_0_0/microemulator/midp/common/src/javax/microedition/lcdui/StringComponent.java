/*
 * MicroEmulator 
 * Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
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

import org.microemu.device.DeviceFactory;

class StringComponent {
	private String text;

	private int breaks[] = new int[4];

	private boolean invertPaint = false;

	private int numOfBreaks;

	private int width;

	private int widthDecreaser;

	public StringComponent() {
		this(null);
	}

	public StringComponent(String text) {
		synchronized (this) {
			this.width = -1;
			this.widthDecreaser = 0;
			setText(text);
		}
	}

	public int getCharHeight() {
		return Font.getDefaultFont().getHeight();
	}

	public int getCharPositionX(int num) {
		synchronized (this) {
			if (numOfBreaks == -1) {
				updateBreaks();
			}
	
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
	}

	public int getCharPositionY(int num) {
		int y = 0;
		synchronized (this) {
			if (numOfBreaks == -1) {
				updateBreaks();
			}
	
			Font f = Font.getDefaultFont();
	
			for (int i = 0; i < numOfBreaks; i++) {
				if (num < breaks[i]) {
					break;
				}
				y += f.getHeight();
			}
		}

		return y;
	}

	public int getHeight() {
		int height;
		synchronized (this) {
			if (numOfBreaks == -1) {
				updateBreaks();
			}

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
		}

		return height;
	}

	public String getText() {
		return text;
	}

	public void invertPaint(boolean state) {
		synchronized (this) {
			invertPaint = state;
		}
	}

	public int paint(Graphics g) {
		if (text == null) {
			return 0;
		}

		int y;
		synchronized (this) {
			if (numOfBreaks == -1) {
				updateBreaks();
			}
	
			int i, prevIndex;
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
				g.drawSubstring(text, prevIndex, text.length() - prevIndex, 0, y, 0);
				y += f.getHeight();
			}
		}

		return y;
	}

	public void setText(String text) {
		synchronized (this) {
			this.text = text;
			this.numOfBreaks = -1;
		}
	}

	public void setWidthDecreaser(int widthDecreaser) {
		synchronized (this) {
			this.widthDecreaser = widthDecreaser;
			numOfBreaks = -1;
		}
	}

	private void insertBreak(int pos) {
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

	private void updateBreaks() {
		if (text == null) {
			return;
		}

		width = DeviceFactory.getDevice().getDeviceDisplay().getWidth()
				- widthDecreaser;

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
