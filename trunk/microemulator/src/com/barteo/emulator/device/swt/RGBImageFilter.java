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

import com.barteo.emulator.app.ui.swt.ImageFilter;
import com.barteo.emulator.device.DeviceFactory;


public final class RGBImageFilter implements ImageFilter
{

  private double Rr, Rg, Rb;
  private Color backgroundColor;
  private Color foregroundColor;
  

  public RGBImageFilter()
	{
    backgroundColor = 
        ((SwtDeviceDisplay) DeviceFactory.getDevice().getDeviceDisplay()).getBackgroundColor();    
    foregroundColor = 
        ((SwtDeviceDisplay) DeviceFactory.getDevice().getDeviceDisplay()).getForegroundColor();    
    Rr = foregroundColor.getRed() - backgroundColor.getRed();
    Rg = foregroundColor.getGreen() - backgroundColor.getGreen();
    Rb = foregroundColor.getBlue() - backgroundColor.getBlue();
  }


  public int filterRGB (int x, int y, int r, int g, int b)
	{
    int a = 0;

    if (Rr > 0) {
      r = (int) (r * Rr) / 256 + backgroundColor.getRed();
    } else {
      r = (int) (r * -Rr) / 256 + foregroundColor.getRed();
    }
    if (Rr > 0) {
      g = (int) (g * Rg) / 256 + backgroundColor.getGreen();
    } else {
      g = (int) (g * -Rg) / 256 + foregroundColor.getGreen();
    }
    if (Rr > 0) {
      b = (int) (b * Rb) / 256 + backgroundColor.getBlue();
    } else {
      b = (int) (b * -Rb) / 256 + foregroundColor.getBlue();
    }

    return a | (r << 16) | (g << 8) | b;
  }

}
