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
 
package com.barteo.emulator.device.swt;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Image;

import org.eclipse.swt.SWT;

import com.barteo.emulator.app.ui.swt.ImageFilter;
import com.barteo.emulator.app.ui.swt.SwtDeviceComponent;
import com.barteo.emulator.device.Device;
import com.barteo.emulator.device.FontManager;


public class SwtDevice extends Device
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
	
		return new SwtMutableImage(width, height);
	}
	
																
	public Image createImage(String name)
  		throws IOException
	{
		return getImage(name);
	}
  
  
	public Image createImage(javax.microedition.lcdui.Image source)
  {
    if (source.isMutable()) {
      return new SwtImmutableImage((SwtMutableImage) source);
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
      fontManager = new SwtFontManager();
    }
    
    return fontManager;
  }
  
  
  public int getGameAction(int keyCode) 
  {
		// TODO poprawic KeyEvent	
		switch (keyCode) {
			case SWT.ARROW_UP:
				return Canvas.UP;
        
			case SWT.ARROW_DOWN:
				return Canvas.DOWN;
        
			case SWT.ARROW_LEFT:
				return Canvas.LEFT;
      	
			case SWT.ARROW_RIGHT:
				return Canvas.RIGHT;
      	
			case SWT.CR:
				return Canvas.FIRE;
      	
/*			case KeyEvent.VK_1:
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
				return Canvas.GAME_D;*/
        
			default:
				return 0;
		}
  }


  public int getKeyCode(int gameAction) 
  {
		// TODO poprawic KeyEvent	
		switch (gameAction) {
			case Canvas.UP:
				return SWT.ARROW_UP;
        
			case Canvas.DOWN:
				return SWT.ARROW_DOWN;
        
			case Canvas.LEFT:
				return SWT.ARROW_LEFT;
        
			case Canvas.RIGHT:
				return SWT.ARROW_RIGHT;
        
			case Canvas.FIRE:
				return SWT.CR;
        
/*			case Canvas.GAME_A:
				return KeyEvent.VK_1;
        
			case Canvas.GAME_B:
				return KeyEvent.VK_3;
        
			case Canvas.GAME_C:
				return KeyEvent.VK_7;
        
			case Canvas.GAME_D:
				return KeyEvent.VK_9;*/

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
    
		return new SwtImmutableImage(SwtDeviceComponent.createImage(is));
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
    if (getDeviceDisplay().isColor()) {
			filter = new RGBImageFilter();
    } else {
      if (getDeviceDisplay().numColors() == 2) {
        filter = new BWImageFilter();
      } else {
        filter = new GrayImageFilter();
      }
    }

		return new SwtImmutableImage(SwtDeviceComponent.createImage(is, filter));
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
    // TODO Auto-generated method stub
    
}
  
}
