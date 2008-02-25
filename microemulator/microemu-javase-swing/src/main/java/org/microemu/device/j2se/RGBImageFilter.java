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


import org.microemu.device.DeviceFactory;
import org.microemu.device.impl.Color;



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
