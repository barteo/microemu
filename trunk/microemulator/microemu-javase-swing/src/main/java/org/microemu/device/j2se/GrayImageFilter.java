/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
 *
 *  It is licensed under the following two licenses as alternatives:
 *    1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 *    2. Apache License (the "AL") Version 2.0
 *
 *  You may not use this file except in compliance with at least one of
 *  the above two licenses.
 *
 *  You may obtain a copy of the LGPL at
 *      http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 *  You may obtain a copy of the AL at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the LGPL or the AL for the specific language governing permissions and
 *  limitations.
 */
 
package org.microemu.device.j2se;

import java.awt.image.RGBImageFilter;

import org.microemu.device.DeviceFactory;
import org.microemu.device.impl.Color;



public class GrayImageFilter extends RGBImageFilter
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
        ((J2SEDeviceDisplay) DeviceFactory.getDevice().getDeviceDisplay()).getBackgroundColor();    
    Color foregroundColor = 
        ((J2SEDeviceDisplay) DeviceFactory.getDevice().getDeviceDisplay()).getForegroundColor();    
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
        ((J2SEDeviceDisplay) DeviceFactory.getDevice().getDeviceDisplay()).getForegroundColor();    
    r = (int) (Rr * Y) + foregroundColor.getRed();
    g = (int) (Rg * Y) + foregroundColor.getGreen();
    b = (int) (Rb * Y) + foregroundColor.getBlue();

    return a | (r << 16) | (g << 8) | b;
  }

}
