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

import com.barteo.midp.lcdui.ImmutableImage;
import com.barteo.midp.lcdui.MutableImage;


public class Image
{


	public static Image createImage(int width, int height)
	{
		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException();
		}
	
		return new MutableImage(width, height);
	}
	
																
	public static Image createImage(String name)
  		throws IOException
	{
		return new ImmutableImage(name);
	}												 
												   

	public static Image createImage(Image source)
	{
		return source;
	}
	

	public static Image createImage(byte[] imageData, int imageOffset, int imageLength)
	{
		return new ImmutableImage(imageData, imageOffset, imageLength);
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
	
	
	public boolean isMutable()
	{
		return false;
	}

}
