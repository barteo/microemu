/*
 *  Nokia API for MicroEmulator
 *  Copyright (C) 2003 Markus Heberling <markus@heberling.net>
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
 
package com.nokia.mid.ui;
 
import javax.microedition.lcdui.Image;


public interface DirectGraphics 
{
	public static final int FLIP_HORIZONTAL = 8192;
	public static final int FLIP_VERTICAL = 16384;
	public static final int ROTATE_90 = 90;
	public static final int ROTATE_180 = 180;
	public static final int ROTATE_270 = 270;
	public static final int TYPE_BYTE_1_GRAY = 1;
	public static final int TYPE_BYTE_1_GRAY_VERTICAL = -1;
	public static final int TYPE_BYTE_2_GRAY = 2;
	public static final int TYPE_BYTE_4_GRAY = 4;
	public static final int TYPE_BYTE_8_GRAY = 8;
	public static final int TYPE_BYTE_332_RGB = 332;
	public static final int TYPE_USHORT_4444_ARGB = 4444;
	public static final int TYPE_USHORT_444_RGB = 444;
	public static final int TYPE_USHORT_555_RGB = 555;
	public static final int TYPE_USHORT_1555_ARGB = 1555;
	public static final int TYPE_USHORT_565_RGB = 565;
	public static final int TYPE_INT_888_RGB = 888;
	public static final int TYPE_INT_8888_ARGB = 8888;


	public void drawImage(Image image, int x, int y, int anchor, int manipulation);
    
	public void drawPixels(byte pixels[], byte transparencyMask[], int offset, int scanlength, int x, int y, int width,
			int height, int manipulation, int format);
    
	public void drawPixels(int pixels[], boolean transparency, int offset, int scanlength, int x, int y, int width,
			int height, int manipulation, int format);
    
	public void drawPixels(short pixels[], boolean transparency, int offset, int scanlength, int x, int y, int width,
			int height, int manipulation, int format);
    
	public void drawPolygon(int xPoints[], int xOffset, int yPoints[], int yOffset, int nPoints, int argbColor);
    
	public void drawTriangle(int x1, int y1, int x2, int y2, int x3, int y3, int argbColor);
    
	public void fillPolygon(int xPoints[], int xOffset, int yPoints[], int yOffset, int nPoints, int argbColor);
    
	public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3, int argbColor);
    
	public int getAlphaComponent();
    
	public int getNativePixelFormat();
    
	public void getPixels(byte pixels[], byte transparencyMask[], int offset, int scanlength, int x, int y, int width,
			int height, int format);
    
	public void getPixels(int pixels[], int offset, int scanlength, int x, int y, int width,
			int height, int format);
    
	public void getPixels(short pixels[], int offset, int scanlength, int x, int y, int width,
			int height, int format);
    
	public void setARGBColor(int i);
        
}