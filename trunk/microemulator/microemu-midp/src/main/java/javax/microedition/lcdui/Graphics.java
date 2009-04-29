/*
 * MicroEmulator
 * Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
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
 *
 * Contributor(s):
 *   3GLab
 *   Andres Navarro
 */

package javax.microedition.lcdui;

public class Graphics {
	
	public static final int SOLID = 0;

	public static final int DOTTED = 1;

	public static final int LEFT = 4;

	public static final int RIGHT = 8;

	public static final int TOP = 16;

	public static final int BASELINE = 64;

	public static final int BOTTOM = 32;

	public static final int HCENTER = 1;

	public static final int VCENTER = 2;

	private int translateX = 0;

	private int translateY = 0;

	public void clipRect(int x, int y, int width, int height) {
		implementationError();
	}

	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        implementationError();
    }

	public void drawChar(char character, int x, int y, int anchor) {
		char[] carr = new char[1];
		carr[0] = character;
		drawString(new String(carr), x, y, anchor);
	}

	public void drawChars(char[] data, int offset, int length, int x, int y, int anchor) {
        drawString(new String(data, offset, length), x, y, anchor);
    }

	public void drawImage(Image img, int x, int y, int anchor) {
		implementationError();
	}

	public void drawLine(int x1, int y1, int x2, int y2) {
		implementationError();
	}

	public void drawRect(int x, int y, int width, int height) {
		implementationError();
	}

	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        implementationError();
    }

	public void drawString(String str, int x, int y, int anchor) {
		implementationError();
	}

	public void drawSubstring(String str, int offset, int len, int x, int y, int anchor) {
        drawString(str.substring(offset, offset + len), x, y, anchor);
    }

    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        implementationError();
    }

	public void fillRect(int x, int y, int width, int height) {
		implementationError();
	}

	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        implementationError();
    }

	public int getBlueComponent() {
		return getColor() & 255;
	}

	public int getClipHeight() {
		implementationError();

		return -1;
	}

	public int getClipWidth() {
		implementationError();

		return -1;
	}

	public int getClipX() {
		implementationError();

		return -1;
	}

	public int getClipY() {
		implementationError();

		return -1;
	}

	public int getColor() {
		implementationError();

		return -1;
	}

	public Font getFont() {
		implementationError();

		return null;
	}

	public int getGrayScale() {
		return (getRedComponent() + getGreenComponent() + getBlueComponent()) / 3;
	}

	public int getGreenComponent() {
		return (getColor() >> 8) & 255;
	}

	public int getRedComponent() {
		return (getColor() >> 16) & 255;
	}

	public int getStrokeStyle() {
		implementationError();

		return -1;
	}

	public int getTranslateX() {
		return translateX;
	}

	public int getTranslateY() {
		return translateY;
	}

	public void setClip(int x, int y, int width, int height) {
		implementationError();
	}

	public void setColor(int RGB) {
		implementationError();
	}

	public void setColor(int red, int green, int blue) {
		int rgb = blue; // 0XRRGGBB
		rgb += green << 8;
		rgb += red << 16;
		setColor(rgb);
	}

	public void setFont(Font font) {
		implementationError();
	}

	public void setGrayScale(int grey) {
		if (grey < 0 || grey > 255) {
			throw new IllegalArgumentException();
		}
		setColor(grey, grey, grey);
	}

	public void setStrokeStyle(int style) {
		implementationError();
	}		

	public void translate(int x, int y) {
		translateX += x;
		translateY += y;
	}
	
	// MIDP2

	public void drawRegion(Image src, int x_src, int y_src, int width,
			int height, int transform, int x_dst, int y_dst, int anchor) {
		implementationError();
	}

	public void drawRGB(int[] rgbData, int offset, int scanlength, int x,
			int y, int width, int height, boolean processAlpha) {
		implementationError();
	}

	public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3) {
		implementationError();
	}

	public void copyArea(int x_src, int y_src, int width, int height,
			int x_dest, int y_dest, int anchor) {
		implementationError();
	}

	public int getDisplayColor(int color) {
		implementationError();

		return -1;
	}

	private void implementationError() {
		try {
			throw new RuntimeException("Must be implemented in DisplayGraphics");
		} catch (RuntimeException ex) {
			ex.printStackTrace();
		}
	}

}
