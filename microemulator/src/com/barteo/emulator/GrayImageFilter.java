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
 * GrayImageFilter
 *      
 * @author <a href="mailto:barteo@it.pl">Bartek Teodorczyk</a>
 */
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
    Rr = (Device.backgroundColor.getRed() - Device.foregroundColor.getRed()) / 256d;
    Rg = (Device.backgroundColor.getGreen() - Device.foregroundColor.getGreen()) / 256d;
    Rb = (Device.backgroundColor.getBlue() - Device.foregroundColor.getBlue()) / 256d;
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
    r = (int) (Rr * Y) + Device.foregroundColor.getRed();
    g = (int) (Rg * Y) + Device.foregroundColor.getGreen();
    b = (int) (Rb * Y) + Device.foregroundColor.getBlue();

    return a | (r << 16) | (g << 8) | b;
  }

}
