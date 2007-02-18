/*
 *  MicroEmulator
 *  Copyright (C) 2006 Bartek Teodorczyk <barteo@barteo.net>
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

package org.microemu.device.j2se;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import org.microemu.log.Logger;

public class J2SETrueTypeFont implements J2SEFont {

	private final static Graphics2D graphics = (Graphics2D) new BufferedImage(1, 1,
			BufferedImage.TYPE_INT_ARGB).getGraphics();

	private URL url;

	private String style;

	private int size;

	private boolean antialiasing;

	private boolean initialized;

	private FontMetrics fontMetrics;

	public J2SETrueTypeFont(URL url, String style, int size, boolean antialiasing) {
		this.url = url;
		this.style = style.toLowerCase();
		this.size = size;
		this.antialiasing = antialiasing;

		this.initialized = false;
	}

	public void setAntialiasing(boolean antialiasing) {
		if (this.antialiasing != antialiasing) {
			this.antialiasing = antialiasing;
			initialized = false;
		}
	}

	public int charWidth(char ch) {
		checkInitialized();

		return fontMetrics.charWidth(ch);
	}

	public int charsWidth(char[] ch, int offset, int length) {
		checkInitialized();

		return fontMetrics.charsWidth(ch, offset, length);
	}

	public int getBaselinePosition() {
		checkInitialized();

		return fontMetrics.getAscent();
	}

	public int getHeight() {
		checkInitialized();

		return fontMetrics.getHeight();
	}

	public int stringWidth(String str) {
		checkInitialized();

		return fontMetrics.stringWidth(str);
	}

	public Font getFont() {
		checkInitialized();

		return fontMetrics.getFont();
	}

	private synchronized void checkInitialized() {
		if (!initialized) {
			int awtStyle = 0;
			if (style.indexOf("plain") != -1) {
				awtStyle |= Font.PLAIN;
			}
			if (style.indexOf("bold") != -1) {
				awtStyle |= Font.BOLD;
			}
			if (style.indexOf("italic") != -1) {
				awtStyle |= Font.ITALIC;
			}
			if (style.indexOf("underlined") != -1) {
				// TODO underlined style not implemented
			}
			if (antialiasing) {
				graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			} else {
				graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			}

			try {
				Font baseFont = Font.createFont(Font.TRUETYPE_FONT, url.openStream());
				fontMetrics = graphics.getFontMetrics(baseFont.deriveFont(awtStyle, size));
				initialized = true;
			} catch (FontFormatException ex) {
				Logger.error(ex);
			} catch (IOException ex) {
				Logger.error(ex);
			}
		}
	}

}
