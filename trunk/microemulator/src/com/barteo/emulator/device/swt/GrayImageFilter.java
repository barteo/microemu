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
 
package com.barteo.emulator.device.swt;

import java.awt.image.RGBImageFilter;

import org.eclipse.swt.graphics.Color;

import com.barteo.emulator.device.DeviceFactory;


public final class GrayImageFilter extends RGBImageFilter
{

  private double Yr, Yg, Yb;
  private double Rr, Rg, Rb;


  public GrayImageFilter ()
	{
    this(0.2126d, 0.7152d, 0.0722d);
  }


  public GrayImageFilter (double Yr, double Yg, double Yb)
	{
    this.Yr = Yr;
    this.Yg = Yg;
    this.Yb = Yb;
    canFilterIndexColorModel = true;
    Color backgroundColor = 
        ((SwtDeviceDisplay) DeviceFactory.getDevice().getDeviceDisplay()).getBackgroundColor();    
    Color foregroundColor = 
        ((SwtDeviceDisplay) DeviceFactory.getDevice().getDeviceDisplay()).getForegroundColor();    
    Rr = (backgroundColor.getRed() - foregroundColor.getRed()) / 256d;
    Rg = (backgroundColor.getGreen() - foregroundColor.getGreen()) / 256d;
    Rb = (backgroundColor.getBlue() - foregroundColor.getBlue()) / 256d;
  }


  public int filterRGB (int x, int y, int rgb)
	{
    int a = (rgb & 0xFF000000);
    int r = (rgb & 0x00FF0000) >>> 16;
    int g = (rgb & 0x0000FF00) >>> 8;
    int b = (rgb & 0x000000FF);
    int Y = (int)(Yr * r + Yg * g + Yb * b) % 256;
    if (Y > 255) {
      Y = 255;
    }
    Color foregroundColor = 
        ((SwtDeviceDisplay) DeviceFactory.getDevice().getDeviceDisplay()).getForegroundColor();    
    r = (int) (Rr * Y) + foregroundColor.getRed();
    g = (int) (Rg * Y) + foregroundColor.getGreen();
    b = (int) (Rb * Y) + foregroundColor.getBlue();

    return a | (r << 16) | (g << 8) | b;
  }

}
