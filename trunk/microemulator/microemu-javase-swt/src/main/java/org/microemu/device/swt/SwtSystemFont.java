/*
 *  MicroEmulator
 *  Copyright (C) 2006 Bartek Teodorczyk <barteo@barteo.net>
 *
 *  It is licensed under the following two licenses as alternatives:
 *    1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 *    2. Apache License (the "AL") Version 2.0
 *
 *  You may not use this file except in compliance with at least one of
 *  the above two licenses.
 *
 *  You may obtain a copy of the LGPL at
 *      http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 *  You may obtain a copy of the AL at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the LGPL or the AL for the specific language governing permissions and
 *  limitations.
 */

package org.microemu.device.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.microemu.app.ui.swt.SwtDeviceComponent;

public class SwtSystemFont implements SwtFont {

	private String name;
	
	private String style;
	
	private int size;
	
	private boolean antialiasing;
	
	private boolean initialized;
	
	private Font font;

	public SwtSystemFont(String name, String style, int size, boolean antialiasing) {
		this.name = name;
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
			font = SwtDeviceComponent.getFont(name, size, swtStyle, antialiasing);
			initialized = true;
		}
	}

	public int charWidth(char ch) {
		return charsWidth(new char[] {ch}, 0, 1);
	}

	public int charsWidth(char[] ch, int offset, int length) {
		checkInitialized();

		return SwtDeviceComponent.stringWidth(font, new String(ch, offset, length));
	}

	public int getBaselinePosition() {
		checkInitialized();
		
		return SwtDeviceComponent.getFontMetrics(font).getAscent();
	}

	public int getHeight() {
		checkInitialized();
		
		return SwtDeviceComponent.getFontMetrics(font).getHeight();
	}

	public int stringWidth(String str) {
		checkInitialized();
		
		return SwtDeviceComponent.stringWidth(font, str);
	}

}
