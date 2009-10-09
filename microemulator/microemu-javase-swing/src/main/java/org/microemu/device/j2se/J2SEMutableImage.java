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
 *  Contributor(s):
 *    Andres Navarro
 */

package org.microemu.device.j2se;

import java.awt.Graphics2D;
import java.awt.image.PixelGrabber;

import org.microemu.device.MutableImage;
import org.microemu.log.Logger;


public class J2SEMutableImage extends MutableImage
{
	private J2SEGraphicsSurface graphicsSurface;
	private PixelGrabber grabber = null;
	private int[] pixels;


	public J2SEMutableImage(int width, int height, boolean withAlpha, int fillColor)
	{
		graphicsSurface = new J2SEGraphicsSurface(width, height, withAlpha, fillColor);
	}


	public javax.microedition.lcdui.Graphics getGraphics()
	{
        Graphics2D g = graphicsSurface.getGraphics();
        g.setClip(0, 0, getWidth(), getHeight());
        J2SEDisplayGraphics displayGraphics = new J2SEDisplayGraphics(graphicsSurface);
		displayGraphics.setColor(0x00000000);
		displayGraphics.translate(-displayGraphics.getTranslateX(), -displayGraphics.getTranslateY());
		
		return displayGraphics;
	}


	public boolean isMutable()
	{
		return true;
	}


	public int getHeight()
	{
		return graphicsSurface.getImage().getHeight();
	}


	public java.awt.Image getImage()
	{
		return graphicsSurface.getImage();
	}


	public int getWidth()
	{
		return graphicsSurface.getImage().getWidth();
	}


	public int[] getData()
	{
		if (grabber == null) {
			pixels = new int[getWidth() * getHeight()];
			grabber = new PixelGrabber(graphicsSurface.getImage(), 0, 0, getWidth(), getHeight(), pixels, 0, getWidth());
		}

		try {
			grabber.grabPixels();
		} catch (InterruptedException e) {
			Logger.error(e);
		}

		return pixels;
	}

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
            (new PixelGrabber(graphicsSurface.getImage(), x, y, width, height, argb, offset, scanlength)).grabPixels();
        } catch (InterruptedException e) {
            Logger.error(e);
        }
    }

}
