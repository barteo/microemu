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
 
package javax.microedition.lcdui;

import java.io.IOException;
import java.io.InputStream;

import com.barteo.emulator.device.DeviceFactory;


public class Image
{


	public static Image createImage(int width, int height)
	{
		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException();
		}
	
		return DeviceFactory.getDevice().createImage(width, height);
	}
	
																
	public static Image createImage(String name)
  		throws IOException
	{
		return DeviceFactory.getDevice().createImage(name);
	}												 
												   

	public static Image createImage(Image source)
	{
		return DeviceFactory.getDevice().createImage(source);
	}
	

	public static Image createImage(byte[] imageData, int imageOffset, int imageLength)
	{
		return DeviceFactory.getDevice().createImage(imageData, imageOffset, imageLength);
	}
																

	public static Image	createImage(Image image, int x, int y, int width, int height, int transform)
	{
		throw new RuntimeException("TODO");
	}
	

	public static Image createImage(InputStream stream)
			throws IOException
	{
		throw new RuntimeException("TODO");
	}


	public static Image createRGBImage(int[] rgb, int width, int height, boolean processAlpha)
	{
		throw new RuntimeException("TODO");
	}	
	
		
	public Graphics getGraphics()
	{
		throw new IllegalStateException("Image is immutable");
	}


	public int getHeight()
	{
		return 0;
	}


	public int getWidth()
	{
		return 0;
	}
	

	public void getRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height)
	{
		throw new RuntimeException("TODO");
	}
	
		
	public boolean isMutable()
	{
		return false;
	}

}
