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
 
package com.barteo.emulator.device.j2se;

import java.awt.Color;

import com.barteo.emulator.device.DeviceFactory;


public class RGBImageFilter extends java.awt.image.RGBImageFilter
{

  private double Rr, Rg, Rb;
  private Color backgroundColor;
  private Color foregroundColor;
  

  public RGBImageFilter()
	{
    canFilterIndexColorModel = true;
    backgroundColor = 
        ((J2SEDeviceDisplay) DeviceFactory.getDevice().getDeviceDisplay()).getBackgroundColor();    
    foregroundColor = 
        ((J2SEDeviceDisplay) DeviceFactory.getDevice().getDeviceDisplay()).getForegroundColor();    
    Rr = foregroundColor.getRed() - backgroundColor.getRed();
    Rg = foregroundColor.getGreen() - backgroundColor.getGreen();
    Rb = foregroundColor.getBlue() - backgroundColor.getBlue();
  }


  public int filterRGB (int x, int y, int rgb)
	{
    int a = (rgb & 0xFF000000);
    int r = (rgb & 0x00FF0000) >>> 16;
    int g = (rgb & 0x0000FF00) >>> 8;
    int b = (rgb & 0x000000FF);

    if (Rr > 0) {
      r = (int) (r * Rr) / 255 + backgroundColor.getRed();
    } else {
      r = (int) (r * -Rr) / 255 + foregroundColor.getRed();
    }
    if (Rr > 0) {
      g = (int) (g * Rg) / 255 + backgroundColor.getGreen();
    } else {
      g = (int) (g * -Rg) / 255 + foregroundColor.getGreen();
    }
    if (Rr > 0) {
      b = (int) (b * Rb) / 255 + backgroundColor.getBlue();
    } else {
      b = (int) (b * -Rb) / 255 + foregroundColor.getBlue();
    }

    return a | (r << 16) | (g << 8) | b;
  }

}
