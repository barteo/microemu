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
 
package com.barteo.emulator;

import java.applet.Applet;
import java.awt.Component;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageFilter;
import java.awt.image.FilteredImageSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;

import com.sixlegs.image.png.PngImage;


public class Resource
{

	private static Resource resource = new Resource();
  
  static ClassLoader loader;
  
  
	public static Resource getInstance()
	{
		return resource;
	}
  
  
  public static void setClassLoader(ClassLoader a_loader)
  {
    loader = a_loader;
  }


  public Image getImage(String str)
			throws IOException
	{
		ImageFilter grayFilter;
    InputStream is;

    if (loader == null) {
      is = getClass().getResourceAsStream(str);
    } else {
      is = loader.getResourceAsStream(str);
    }
    if (is == null) {
      throw new IOException();
    }
    PngImage png = new PngImage(is);
    
//   	double[][] chrom = (double[][])png.getProperty("chromaticity xyz");
//		if (chrom == null) {
			grayFilter = new GrayImageFilter();
//		} else {
//			bwFilter = new BWImageFilter(chrom[1][1], chrom[2][1], chrom[3][1]);
//		}
		FilteredImageSource grayImageSource = new FilteredImageSource(png, grayFilter);

		return Toolkit.getDefaultToolkit().createImage(grayImageSource);
	}


  public Image getSystemImage(String str)
			throws IOException
	{
    InputStream is;

    if (loader == null) {
      is = getClass().getResourceAsStream(str);
    } else {
      is = loader.getResourceAsStream(str);
    }
    if (is == null) {
      throw new IOException();
    }
    PngImage png = new PngImage(is);
    
		return Toolkit.getDefaultToolkit().createImage(png);
	}


	public Image getImage(byte[] imageData, int imageOffset, int imageLength)
			throws IOException
	{
		ImageFilter grayFilter;

		ByteArrayInputStream is = new ByteArrayInputStream(imageData, imageOffset, imageLength);
		PngImage png = new PngImage(is);
   	double[][] chrom = (double[][])png.getProperty("chromaticity xyz");
		if (chrom == null) {
			grayFilter = new GrayImageFilter();
		} else {
			grayFilter = new GrayImageFilter(chrom[1][1], chrom[2][1], chrom[3][1]);
		}
		FilteredImageSource grayImageSource = new FilteredImageSource(png, grayFilter);

		return Toolkit.getDefaultToolkit().createImage(grayImageSource);
	}

}
