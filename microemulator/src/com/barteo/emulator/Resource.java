/*
 * MicroEmulator
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
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

/**
 * Resource
 *      
 * @author <a href="mailto:barteo@it.pl">Bartek Teodorczyk</a>
 */
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
		ImageFilter grayFilter;

    PngImage png = new PngImage(getClass().getResourceAsStream(str));
//   	double[][] chrom = (double[][])png.getProperty("chromaticity xyz");
//		if (chrom == null) {
			grayFilter = new GrayImageFilter();
//		} else {
//			bwFilter = new BWImageFilter(chrom[1][1], chrom[2][1], chrom[3][1]);
//		}
		FilteredImageSource grayImageSource = new FilteredImageSource(png, grayFilter);

		return Toolkit.getDefaultToolkit().createImage(grayImageSource);
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
