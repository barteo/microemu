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
 
package com.barteo.emulator.device.swt;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import com.barteo.emulator.app.ui.swt.ImageFilter;
import com.barteo.emulator.app.ui.swt.SwtDeviceComponent;
import com.barteo.emulator.device.DeviceFactory;


public final class RGBImageFilter implements ImageFilter
{

  private double Rr, Rg, Rb;
  private Color backgroundColor;
  private Color foregroundColor;
  

  public RGBImageFilter()
	{
    backgroundColor = SwtDeviceComponent.getColor(new RGB(
    		((SwtDeviceDisplay) DeviceFactory.getDevice().getDeviceDisplay()).getBackgroundColor().getRed(),
    		((SwtDeviceDisplay) DeviceFactory.getDevice().getDeviceDisplay()).getBackgroundColor().getGreen(),
    		((SwtDeviceDisplay) DeviceFactory.getDevice().getDeviceDisplay()).getBackgroundColor().getBlue()));
    foregroundColor = SwtDeviceComponent.getColor(new RGB(
    		((SwtDeviceDisplay) DeviceFactory.getDevice().getDeviceDisplay()).getForegroundColor().getRed(),
    		((SwtDeviceDisplay) DeviceFactory.getDevice().getDeviceDisplay()).getForegroundColor().getGreen(),
    		((SwtDeviceDisplay) DeviceFactory.getDevice().getDeviceDisplay()).getForegroundColor().getBlue()));
    Rr = foregroundColor.getRed() - backgroundColor.getRed();
    Rg = foregroundColor.getGreen() - backgroundColor.getGreen();
    Rb = foregroundColor.getBlue() - backgroundColor.getBlue();
  }


  public RGB filterRGB (int x, int y, RGB rgb)
	{
    int r, g, b;

    if (Rr > 0) {
      r = (int) (rgb.red * Rr) / 255 + backgroundColor.getRed();
    } else {
      r = (int) (rgb.red * -Rr) / 255 + foregroundColor.getRed();
    }
    if (Rr > 0) {
      g = (int) (rgb.green * Rg) / 255 + backgroundColor.getGreen();
    } else {
      g = (int) (rgb.green * -Rg) / 255 + foregroundColor.getGreen();
    }
    if (Rr > 0) {
      b = (int) (rgb.blue * Rb) / 255 + backgroundColor.getBlue();
    } else {
      b = (int) (rgb.blue * -Rb) / 255 + foregroundColor.getBlue();
    }

    return new RGB(r, g, b);
  }

}
