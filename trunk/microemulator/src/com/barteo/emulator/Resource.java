/*
 * @(#)Resource.java  07/07/2001
 *
 * Copyright (c) 2001 Bartek Teodorczyk <barteo@it.pl>. All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package com.barteo.emulator;

import java.applet.Applet;
import java.awt.Component;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageFilter;
import java.awt.image.FilteredImageSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;

import com.sixlegs.image.png.PngImage;

public class Resource
{

	private static Resource resource = new Resource();


	public static Resource getInstance()
	{
		return resource;
	}


	public Image getImage(String str)
			throws IOException
	{
		ImageFilter bwFilter;

		PngImage png = new PngImage(getClass().getResourceAsStream(str));
//   	double[][] chrom = (double[][])png.getProperty("chromaticity xyz");
//		if (chrom == null) {
			bwFilter = new BWImageFilter();
//		} else {
//			bwFilter = new BWImageFilter(chrom[1][1], chrom[2][1], chrom[3][1]);
//		}
		FilteredImageSource bwImageSource = new FilteredImageSource(png, bwFilter);

		return Toolkit.getDefaultToolkit().createImage(bwImageSource);
	}


	public Image getImage(byte[] imageData, int imageOffset, int imageLength)
			throws IOException
	{
		ImageFilter bwFilter;

		ByteArrayInputStream is = new ByteArrayInputStream(imageData, imageOffset, imageLength);
		PngImage png = new PngImage(is);
   	double[][] chrom = (double[][])png.getProperty("chromaticity xyz");
		if (chrom == null) {
			bwFilter = new BWImageFilter();
		} else {
			bwFilter = new BWImageFilter(chrom[1][1], chrom[2][1], chrom[3][1]);
		}
		FilteredImageSource bwImageSource = new FilteredImageSource(png, bwFilter);

		return Toolkit.getDefaultToolkit().createImage(bwImageSource);
	}

}
