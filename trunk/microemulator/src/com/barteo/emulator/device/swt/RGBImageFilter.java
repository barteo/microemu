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


  public RGB filterRGB (int x, int y, RGB rgb)
	{
    int r, g, b;

    if (Rr > 0) {
      r = (int) (rgb.red * Rr) / 256 + backgroundColor.getRed();
    } else {
      r = (int) (rgb.red * -Rr) / 256 + foregroundColor.getRed();
    }
    if (Rr > 0) {
      g = (int) (rgb.green * Rg) / 256 + backgroundColor.getGreen();
    } else {
      g = (int) (rgb.green * -Rg) / 256 + foregroundColor.getGreen();
    }
    if (Rr > 0) {
      b = (int) (rgb.blue * Rb) / 256 + backgroundColor.getBlue();
    } else {
      b = (int) (rgb.blue * -Rb) / 256 + foregroundColor.getBlue();
    }

    return new RGB(r, g, b);
  }

}
