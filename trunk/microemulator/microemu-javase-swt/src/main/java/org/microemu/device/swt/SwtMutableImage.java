/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
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

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.ImageData;
import org.microemu.app.ui.swt.SwtDeviceComponent;
import org.microemu.app.ui.swt.SwtGraphics;
import org.microemu.device.MutableImage;


public class SwtMutableImage extends MutableImage 
{
	public org.eclipse.swt.graphics.Image img;
	
	private org.eclipse.swt.graphics.GC gc;
	
	
	public SwtMutableImage(int width, int height) 
	{
		this.img = SwtDeviceComponent.createImage(width, height);
		this.gc = new GC(this.img);
		SwtDisplayGraphics displayGraphics = new SwtDisplayGraphics(new SwtGraphics(gc), this);
		displayGraphics.setColor(0x00ffffff);
		displayGraphics.fillRect(0, 0, width, height);
	}


	public javax.microedition.lcdui.Graphics getGraphics() 
	{
		SwtDisplayGraphics displayGraphics = new SwtDisplayGraphics(new SwtGraphics(gc), this);
		displayGraphics.setColor(0x00000000);
		displayGraphics.setClip(0, 0, getWidth(), getHeight());
		displayGraphics.translate(-displayGraphics.getTranslateX(), -displayGraphics.getTranslateY());

		return displayGraphics;
	}


	public boolean isMutable() 
	{
		return true;
	}


	public int getHeight() 
	{
		return img.getBounds().height;
	}


	public org.eclipse.swt.graphics.Image getImage() 
	{
		return img;
	}


	public int getWidth() 
	{
		return img.getBounds().width;
	}


	public int[] getData() 
	{
		byte[] tmp = img.getImageData().data;
		int[] result = new int[tmp.length];
		
		for (int i = 0; i < tmp.length; i++) {
			result[i] = tmp[i];
		}
		
		return result;
	}


	public void getRGB(int[] argb, int offset, int scanlength, int x, int y, int width, int height) 
	{
        if (width <= 0 || height <= 0) {
            return;
        }
        if (x < 0 || y < 0 || x + width > getWidth() || y + height > getHeight()) {
            throw new IllegalArgumentException("Specified area exceeds bounds of image");
        }
        if ((scanlength < 0 ? -scanlength : scanlength) < width) {
            throw new IllegalArgumentException("abs value of scanlength is less than width");
        }
        if (argb == null) { 
            throw new NullPointerException("null rgbData");
        }
        if (offset < 0 || offset + width > argb.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (scanlength < 0) { 
            if (offset + scanlength*(height-1) < 0) {
                throw new ArrayIndexOutOfBoundsException();
            }
        } else {
            if (offset + scanlength*(height-1) + width > argb.length) {
                throw new ArrayIndexOutOfBoundsException();
            }
        }
        
        ImageData imageData = img.getImageData();
        for (int i = 0; i < height; i++) {
        		imageData.getPixels(x, y + i, width, argb, offset + i * scanlength);
        }
	}

}
