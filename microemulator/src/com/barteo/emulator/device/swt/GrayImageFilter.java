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

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import com.barteo.emulator.app.ui.swt.ImageFilter;
import com.barteo.emulator.device.DeviceFactory;


public final class GrayImageFilter implements ImageFilter
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
    Color backgroundColor = 
        ((SwtDeviceDisplay) DeviceFactory.getDevice().getDeviceDisplay()).getBackgroundColor();    
    Color foregroundColor = 
        ((SwtDeviceDisplay) DeviceFactory.getDevice().getDeviceDisplay()).getForegroundColor();    
    Rr = (backgroundColor.getRed() - foregroundColor.getRed()) / 256d;
    Rg = (backgroundColor.getGreen() - foregroundColor.getGreen()) / 256d;
    Rb = (backgroundColor.getBlue() - foregroundColor.getBlue()) / 256d;
  }


  public RGB filterRGB (int x, int y, RGB rgb)
	{
    int a = 0;
    int Y = (int)(Yr * rgb.red + Yg * rgb.green + Yb * rgb.blue) % 256;
    if (Y > 255) {
      Y = 255;
    }
    Color foregroundColor = 
        ((SwtDeviceDisplay) DeviceFactory.getDevice().getDeviceDisplay()).getForegroundColor();
    
    return new RGB(    
    		(int) (Rr * Y) + foregroundColor.getRed(),
    		(int) (Rg * Y) + foregroundColor.getGreen(),
    		(int) (Rb * Y) + foregroundColor.getBlue());
  }

}
