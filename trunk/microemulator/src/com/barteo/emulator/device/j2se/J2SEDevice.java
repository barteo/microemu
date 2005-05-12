/*
 *  MicroEmulator
 *  Copyright (C) 2002 Bartek Teodorczyk <barteo@it.pl>
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

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Image;

import com.barteo.emulator.device.Device;
import com.barteo.emulator.device.FontManager;
import com.sixlegs.image.png.PngImage;


public class J2SEDevice extends Device
{
	private FontManager fontManager = null;
  
	private Image normalImage;
	private Image overImage;
	private Image pressedImage;
  
  
	public Image createImage(int width, int height)
	{
		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException();
		}
	
		return new J2SEMutableImage(width, height);
	}
	
																
	public Image createImage(String name)
  		throws IOException
	{
		return getImage(name);
	}
  
  
	public Image createImage(javax.microedition.lcdui.Image source)
  {
    if (source.isMutable()) {
      return new J2SEImmutableImage((J2SEMutableImage) source);
    } else {
      return source;
    }
  }
  

  public Image createImage(byte[] imageData, int imageOffset, int imageLength)
	{
		ByteArrayInputStream is = new ByteArrayInputStream(imageData, imageOffset, imageLength);
		try {
			return getImage(is);
		} catch (IOException ex) {
			throw new IllegalArgumentException(ex.toString());
		}
	}
  
  
  public FontManager getFontManager()
  {
    if (fontManager == null) {
      fontManager = new J2SEFontManager();
    }
    
    return fontManager;
  }
  
  
  public int getGameAction(int keyCode) 
  {
		switch (keyCode) {
			case KeyEvent.VK_UP:
				return Canvas.UP;
        
			case KeyEvent.VK_DOWN:
				return Canvas.DOWN;
        
			case KeyEvent.VK_LEFT:
				return Canvas.LEFT;
      	
			case KeyEvent.VK_RIGHT:
				return Canvas.RIGHT;
      	
			case KeyEvent.VK_ENTER:
				return Canvas.FIRE;
      	
			case KeyEvent.VK_1:
			case KeyEvent.VK_A:
				return Canvas.GAME_A;
        
			case KeyEvent.VK_3:
			case KeyEvent.VK_B:
				return Canvas.GAME_B;
        
			case KeyEvent.VK_7:
			case KeyEvent.VK_C:
				return Canvas.GAME_C;
        
			case KeyEvent.VK_9:
			case KeyEvent.VK_D:
				return Canvas.GAME_D;
        
			default:
				return 0;
		}
  }


  public int getKeyCode(int gameAction) 
  {
		switch (gameAction) {
			case Canvas.UP:
				return KeyEvent.VK_UP;
        
			case Canvas.DOWN:
				return KeyEvent.VK_DOWN;
        
			case Canvas.LEFT:
				return KeyEvent.VK_LEFT;
        
			case Canvas.RIGHT:
				return KeyEvent.VK_RIGHT;
        
			case Canvas.FIRE:
				return KeyEvent.VK_ENTER;
        
			case Canvas.GAME_A:
				return KeyEvent.VK_1;
        
			case Canvas.GAME_B:
				return KeyEvent.VK_3;
        
			case Canvas.GAME_C:
				return KeyEvent.VK_7;
        
			case Canvas.GAME_D:
				return KeyEvent.VK_9;

			default:
				throw new IllegalArgumentException();
		}
  }


  public Image getNormalImage()
  {
    return normalImage;
  }

  
  public Image getOverImage()
  {
    return overImage;
  }

  
  public Image getPressedImage()
  {
    return pressedImage;
  }

  
  public boolean hasPointerMotionEvents()
  {
    return false;
  }
  
  
  public boolean hasPointerEvents()
  {
    return false;
  }
  
  
  public boolean hasRepeatEvents()
  {
    return false;
  }

  
  protected Image createSystemImage(String str)
			throws IOException
	{
    InputStream is;

    is = getClass().getResourceAsStream(str);
    if (is == null) {
      throw new IOException();
    }
    PngImage png = new PngImage(is);
    
		return new J2SEImmutableImage(Toolkit.getDefaultToolkit().createImage(png));
	}
  

	private Image getImage(String str)
			throws IOException
	{
		InputStream is = getEmulatorContext().getClassLoader().getResourceAsStream(str);

		if (is == null) {
				throw new IOException(str + " could not be found.");
		}

		return getImage(is);			
	}

  
  private Image getImage(InputStream is)
  		throws IOException
  {
		ImageFilter filter = null;
    PngImage png = new PngImage(is);
    
    try {
			png.getWidth();
		} catch (IOException ex) {
			throw new IOException("Error decoding PNG image: " + ex.toString());    	
		}
        
    if (getDeviceDisplay().isColor()) {
			filter = new RGBImageFilter();
    } else {
      if (getDeviceDisplay().numColors() == 2) {
        filter = new BWImageFilter();
      } else {
        filter = new GrayImageFilter();
      }
    }
    FilteredImageSource imageSource = new FilteredImageSource(png, filter);

		return new J2SEImmutableImage(Toolkit.getDefaultToolkit().createImage(imageSource));
  }


/* (non-Javadoc)
 * @see com.barteo.emulator.device.Device#setNormalImage(javax.microedition.lcdui.Image)
 */
protected void setNormalImage(Image image)
{
    normalImage = image;
}


/* (non-Javadoc)
 * @see com.barteo.emulator.device.Device#setOverImage(javax.microedition.lcdui.Image)
 */
protected void setOverImage(Image image)
{
    overImage = image;
}


/* (non-Javadoc)
 * @see com.barteo.emulator.device.Device#setPressedImage(javax.microedition.lcdui.Image)
 */
protected void setPressedImage(Image image)
{
    pressedImage = image;
}
  
}
