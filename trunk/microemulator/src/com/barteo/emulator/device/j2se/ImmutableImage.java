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
 
package com.barteo.emulator.device.j2se;

import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;

import com.sixlegs.image.png.PngImage;


public class ImmutableImage extends javax.microedition.lcdui.Image
{
	java.awt.Image img;

  
	public ImmutableImage(String name)
      throws IOException
	{
  	img = getImage(name);
	}


	public ImmutableImage(byte[] imageData, int imageOffset, int imageLength)
	{
		ImageFilter grayFilter;

		ByteArrayInputStream is = new ByteArrayInputStream(imageData, imageOffset, imageLength);
		PngImage png = new PngImage(is);
   	double[][] chrom = null;
    try {
      chrom = (double[][])png.getProperty("chromaticity xyz");
    } catch (IOException ex) {
      System.err.println(ex);
    }
		if (chrom == null) {
			grayFilter = new GrayImageFilter();
		} else {
			grayFilter = new GrayImageFilter(chrom[1][1], chrom[2][1], chrom[3][1]);
		}
		FilteredImageSource grayImageSource = new FilteredImageSource(png, grayFilter);

		img = Toolkit.getDefaultToolkit().createImage(grayImageSource);
	}

  
	public int getHeight()
	{
		return img.getHeight(null);
	}


	public java.awt.Image getImage()
	{
		return img;
	}


	public int getWidth()
	{
		return img.getWidth(null);
	}

  
  static java.awt.Image getImage(String str)
			throws IOException
	{
		ImageFilter grayFilter;
    InputStream is;

    is = ImmutableImage.class.getResourceAsStream(str);
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
  
}
