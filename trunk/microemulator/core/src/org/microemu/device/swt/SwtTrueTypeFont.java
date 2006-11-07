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

package org.microemu.device.swt;

import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;

public class SwtTrueTypeFont implements SwtFont {

	private URL url;
	
	private String style;
	
	private int size;
	
	private boolean antialiasing;
	
	private boolean initialized;
	
	private Font font;

	public SwtTrueTypeFont(URL url, String style, int size, boolean antialiasing) {
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

	public Font getFont() {
		checkInitialized();

		return font;
	}

	private synchronized void checkInitialized() {
		if (!initialized) {
			int swtStyle = 0;
			if (style.indexOf("plain") != -1) {
				swtStyle |= SWT.NORMAL;
			}
			if (style.indexOf("bold") != -1) {
				swtStyle |= SWT.BOLD;
			}
			if (style.indexOf("italic") != -1) {
				swtStyle |= SWT.ITALIC;
			}
			if (style.indexOf("underlined") != -1) {
				// TODO underlined style not implemented
			}

//			font = SwtDeviceComponent.getFont(name, size, swtStyle, antialiasing);
			initialized = true;
			try {
				throw new RuntimeException("not implemented");
			} catch (RuntimeException ex) {
				ex.printStackTrace();
				throw ex;
			}
		}
	}

	public int charWidth(char ch) {
		try {
			throw new RuntimeException("not implemented");
		} catch (RuntimeException ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public int charsWidth(char[] ch, int offset, int length) {
		try {
			throw new RuntimeException("not implemented");
		} catch (RuntimeException ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public int getBaselinePosition() {
		try {
			throw new RuntimeException("not implemented");
		} catch (RuntimeException ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public int getHeight() {
		try {
			throw new RuntimeException("not implemented");
		} catch (RuntimeException ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public int stringWidth(String str) {
		try {
			throw new RuntimeException("not implemented");
		} catch (RuntimeException ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

}
