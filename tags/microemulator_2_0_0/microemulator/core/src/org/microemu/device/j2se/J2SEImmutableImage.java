/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
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
 *  
 *  Contributor(s):
 *    Andres Navarro
 */

package org.microemu.device.j2se;

import java.awt.Toolkit;

public class J2SEImmutableImage extends javax.microedition.lcdui.Image {
	private java.awt.Image img;
	private int width;
	private int height;

	public J2SEImmutableImage(java.awt.Image image) {
		img = image;
		width = image.getWidth(null);
		height = image.getHeight(null);
	}

	public J2SEImmutableImage(J2SEMutableImage image) {
		img = Toolkit.getDefaultToolkit().createImage(
				image.getImage().getSource());
	}

	public int getHeight() {
		return height;
	}

	public java.awt.Image getImage() {
		return img;
	}

	public int getWidth() {
		return width;
	}

	public void getRGB(int[] argb, int offset, int scanlength, int x, int y,
			int width, int height) {

		if (width <= 0 || height <= 0)
			return;
		if (x < 0 || y < 0 || x + width > getWidth()
				|| y + height > getHeight())
			throw new IllegalArgumentException(
					"Specified area exceeds bounds of image");
		if ((scanlength < 0 ? -scanlength : scanlength) < width)
			throw new IllegalArgumentException(
					"abs value of scanlength is less than width");
		if (argb == null)
			throw new NullPointerException("null rgbData");
		if (offset < 0 || offset + width > argb.length)
			throw new ArrayIndexOutOfBoundsException();
		if (scanlength < 0) {
			if (offset + scanlength * (height - 1) < 0)
				throw new ArrayIndexOutOfBoundsException();
		} else {
			if (offset + scanlength * (height - 1) + width > argb.length)
				throw new ArrayIndexOutOfBoundsException();
		}

		try {
			(new java.awt.image.PixelGrabber(img, x, y, width, height, argb,
					offset, scanlength)).grabPixels();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
