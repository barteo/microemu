/*
 *  Siemens API for MicroEmulator
 *  Copyright (C) 2003 Markus Heberling <markus@heberling.net>
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
package com.siemens.mp.game;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import com.barteo.emulator.device.DeviceDisplay;
import com.barteo.emulator.device.DeviceFactory;

/**
 *
 * @author  markus
 * @version
 */
public class ExtendedImage extends com.siemens.mp.misc.NativeMem {
    Image image;
    
    public ExtendedImage(Image image) {
        this.image=image;
    }
    
    public void blitToScreen(int x, int y) {
/*		DeviceDisplay dd = DeviceFactory.getDevice().getDeviceDisplay();
    	Image image = dd.getDisplayImage();
    	
    	draw ExtendedImage to image 
    	
    	dd.repaint();*/
        System.out.println(" public void blitToScreen(int x, int y)");
    }
    
    public void clear(byte color) {
        Graphics g=image.getGraphics();
        g.setColor(color);
        g.fillRect(0,0,image.getWidth(),image.getHeight());
    }
    
    
    public Image getImage() {
        return image;
    }
    
    
    public int getPixel(int x, int y) {
        System.out.println("int getPixel(int x, int y)");
        return 1;
    }
    
    
    public void getPixelBytes(byte[] pixels, int x, int y, int width, int height) {
        System.out.println("void getPixelBytes(byte[] pixels, int x, int y, int width, int height)");
    }
    
    
    public void setPixel(int x, int y, byte color) {
        System.out.println("void setPixel(int x, int y, byte color)");
    }
    
    
    public void setPixels(byte[] pixels, int x, int y, int width, int height) {
        Image img=com.siemens.mp.ui.Image.createImageFromBitmap(pixels,width,height);
        image.getGraphics().drawImage(img, x,y, 0);
    }
}
