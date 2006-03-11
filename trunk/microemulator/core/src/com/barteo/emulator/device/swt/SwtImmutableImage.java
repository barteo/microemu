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
 */
 
package com.barteo.emulator.device.swt;


public class SwtImmutableImage extends javax.microedition.lcdui.Image
{
	org.eclipse.swt.graphics.Image img;

  
	public SwtImmutableImage(org.eclipse.swt.graphics.Image image)
	{
		img = image;
	}
  
  
  public SwtImmutableImage(SwtMutableImage image)
  {
	// TODO poprawic tworzenie image	
//    img = Toolkit.getDefaultToolkit().createImage(image.getImage().getSource());
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
	
	
	public void getRGB(int[] argb, int offset, int scanlenght, int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		super.getRGB(argb, offset, scanlenght, x, y, width, height);
	}
  
}
