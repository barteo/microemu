/*
 * MicroEmulator
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.barteo.emulator;

import java.awt.image.RGBImageFilter;

import com.barteo.emulator.device.Device;

/**
 * BWImageFilter
 *      
 * @author <a href="mailto:barteo@it.pl">Bartek Teodorczyk</a>
 */
public final class BWImageFilter extends RGBImageFilter
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
    canFilterIndexColorModel = true;
  }


  public int filterRGB (int x, int y, int rgb)
	{
    int a = (rgb & 0xFF000000);
    int r = (rgb & 0x00FF0000) >>> 16;
    int g = (rgb & 0x0000FF00) >>> 8;
    int b = (rgb & 0x000000FF);
    int Y = (int)(Yr * r + Yg * g + Yb * b);
    if (Y > 127) {
	    return a | Device.backgroundColor.getRGB();
		} else {
	    return a | Device.foregroundColor.getRGB();
		}
  }

}

