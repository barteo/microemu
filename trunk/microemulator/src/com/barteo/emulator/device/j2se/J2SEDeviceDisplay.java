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

import java.awt.Color;
import java.awt.Rectangle;

import com.barteo.emulator.EmulatorContext;
import com.barteo.emulator.device.DeviceDisplay;


public class J2SEDeviceDisplay implements DeviceDisplay
{
  EmulatorContext context;

  Rectangle displayRectangle;
  Rectangle displayPaintable;
    
  boolean isColor;
  int numColors;
  Color backgroundColor;
  Color foregroundColor;
  
  PositionedImage upImage;
  PositionedImage downImage;
  
  PositionedImage mode123Image;  
  PositionedImage modeAbcUpperImage;  
  PositionedImage modeAbcLowerImage;
  
  
  J2SEDeviceDisplay(EmulatorContext acontext)
  {
    context = acontext; 
  }
  
  
  public EmulatorContext getEmulatorContext()
  {
    return context;
  }

  
  public int getHeight()
  {
    return displayPaintable.height;
  }
  
  
  public int getWidth()
  {
    return displayPaintable.width;
  }
  
  
  public boolean isColor()
  {
    return isColor;
  }
  
  
  public int numColors()
  {
    return numColors;
  }
  
  
  public void repaint()
  {
    context.getDisplayComponent().repaint();
  }
  
  
  public void setScrollDown(boolean state) 
  {
    context.getDisplayComponent().setScrollDown(state);
  }


  public void setScrollUp(boolean state) 
  {
    context.getDisplayComponent().setScrollUp(state);
  }

  
  public Rectangle getDisplayPaintable()
  {
    return displayPaintable;
  }

  
  public Rectangle getDisplayRectangle()
  {
    return displayRectangle;
  }


  public Color getBackgroundColor()
  {
    return backgroundColor;
  }

  
  public Color getForegroundColor()
  {
    return foregroundColor;
  }
  
  
  public PositionedImage getUpImage()
  {
    return upImage;
  }

  
  public PositionedImage getDownImage()
  {
    return downImage;
  }

  
  public PositionedImage getMode123Image()
  {
    return mode123Image;
  }

  
  public PositionedImage getModeAbcUpperImage()
  {
    return modeAbcUpperImage;
  }

  
  public PositionedImage getModeAbcLowerImage()
  {
    return modeAbcLowerImage;
  }
  
}