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
 *
 *  Contributor(s):
 *    Andres Navarro
 */

package org.microemu.device.j2se;

import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;

import org.microemu.device.MutableImage;
import org.microemu.log.Logger;


public class J2SEMutableImage extends MutableImage
{
	private java.awt.Image img;
	private J2SEDisplayGraphics displayGraphics = null;
	private PixelGrabber grabber = null;
	private int[] pixels;


	public J2SEMutableImage(int width, int height)
	{
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}


	public javax.microedition.lcdui.Graphics getGraphics()
	{
		if (displayGraphics == null) {
                        // Andres Navarro
                        displayGraphics = new J2SEDisplayGraphics((java.awt.Graphics2D)img.getGraphics(), this);
                        // Andres Navarro
			displayGraphics.setGrayScale(255);
			displayGraphics.fillRect(0, 0, getWidth(), getHeight());
			displayGraphics.setGrayScale(0);
		}
		return displayGraphics;
	}


	public boolean isMutable()
	{
		return true;
	}


	public int getHeight()
	{
		return img.getHeight(null);
	}


	public java.awt.Image getImage()
	{
		return img;
	}


	public int getWidth()
	{
		return img.getWidth(null);
	}


	public int[] getData()
	{
		if (grabber == null) {
			pixels = new int[getWidth() * getHeight()];
			grabber = new PixelGrabber(img, 0, 0, getWidth(), getHeight(), pixels, 0, getWidth());
		}

		try {
			grabber.grabPixels();
		} catch (InterruptedException e) {
			Logger.error(e);
		}

		return pixels;
	}

        // Andres Navarro
        public void getRGB(int []argb, int offset, int scanlength,
                int x, int y, int width, int height) {

            if (width <= 0 || height <= 0)
                return;
            if (x < 0 || y < 0 || x + width > getWidth() || y + height > getHeight())
                throw new IllegalArgumentException("Specified area exceeds bounds of image");
            if ((scanlength < 0? -scanlength:scanlength) < width)
                throw new IllegalArgumentException("abs value of scanlength is less than width");
            if (argb == null)
                throw new NullPointerException("null rgbData");
            if (offset < 0 || offset + width > argb.length)
                throw new ArrayIndexOutOfBoundsException();
            if (scanlength < 0) {
                if (offset + scanlength*(height-1) < 0)
                    throw new ArrayIndexOutOfBoundsException();
            } else {
                if (offset + scanlength*(height-1) + width > argb.length)
                    throw new ArrayIndexOutOfBoundsException();
            }

            try {
                (new PixelGrabber(img, x, y, width, height, argb, offset, scanlength)).grabPixels();
            } catch (InterruptedException e) {
                Logger.error(e);
            }
        }
        // Andres Navarro

}
