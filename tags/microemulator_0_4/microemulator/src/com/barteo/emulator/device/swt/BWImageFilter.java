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

import org.eclipse.swt.graphics.RGB;

import com.barteo.emulator.app.ui.swt.ImageFilter;
import com.barteo.emulator.device.DeviceFactory;


public final class BWImageFilter implements ImageFilter
{

  private double Yr, Yg, Yb;


  public BWImageFilter ()
	{
    this(0.2126d, 0.7152d, 0.0722d);
  }


  public BWImageFilter (double Yr, double Yg, double Yb)
	{
    this.Yr = Yr;
    this.Yg = Yg;
    this.Yb = Yb;
  }


  public RGB filterRGB (int x, int y, RGB rgb)
	{
    int Y = (int)(Yr * rgb.red + Yg * rgb.green + Yb * rgb.blue);
    if (Y > 127) {
    	return ((SwtDeviceDisplay) DeviceFactory.getDevice().getDeviceDisplay()).getBackgroundColor().getRGB();
		} else {
	    return ((SwtDeviceDisplay) DeviceFactory.getDevice().getDeviceDisplay()).getForegroundColor().getRGB();
		}
  }

}

