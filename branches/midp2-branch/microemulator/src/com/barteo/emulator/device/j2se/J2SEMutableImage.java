/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@it.pl>
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
 
package com.barteo.emulator.device.j2se;

import java.awt.Frame;
import java.awt.Graphics2D;

import com.barteo.emulator.device.MutableImage;


public class J2SEMutableImage extends MutableImage 
{

  java.awt.Image img;
  Frame f = new Frame();
  DisplayGraphics displayGraphics = null;


  public J2SEMutableImage(int width, int height) 
  {
    f.addNotify();
    img = f.createImage(width, height);
  }


  public javax.microedition.lcdui.Graphics getGraphics() 
  {
    if (displayGraphics == null) {
      displayGraphics = new DisplayGraphics((Graphics2D) img.getGraphics());
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
	
	
	public int getPixel(int x, int y)
	{
		// TODO poprawic zwracanie koloru
		return 0;
	}
	
}
