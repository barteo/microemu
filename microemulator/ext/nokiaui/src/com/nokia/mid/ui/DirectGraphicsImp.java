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
 *
 *  Contributor(s):
 *    Bartek Teodorczyk <barteo@barteo.net>
 */

package com.nokia.mid.ui;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Graphics;
//import com.barteo.emulator.device.DeviceFactory;
import com.barteo.emulator.device.MutableImage;


class DirectGraphicsImp implements DirectGraphics {
    private Graphics graphics;
    private int alphaComponent;
    
    /**
     * @param g
     */
    public DirectGraphicsImp(Graphics g) {
        graphics = g;
    }
    
    /**
     * @param img
     * @param x
     * @param y
     * @param anchor
     * @param manipulation ignored, since manipulations are not supported at the moment
     */
    public void drawImage(Image img, int x, int y, int anchor, int manipulation) {
        if(img == null) {
            throw new NullPointerException();
        }
        if(anchor >= 64 || manipulation != 0) {
            throw new IllegalArgumentException();
        } else {
            graphics.drawImage(img, x + graphics.getTranslateX(), y + graphics.getTranslateY(), anchor);
            return;
        }
    }
    
    /**
     * @param argb
     */
    public void setARGBColor(int argb) {
        alphaComponent=(argb >> 24 & 0xff);
        graphics.setColor(argb & 0xffffff);
    }
    
    /**
     * @return
     */
    public int getAlphaComponent() {
        return alphaComponent;
    }
    
    /**
     * @return
     */
    public int getNativePixelFormat() {
        return TYPE_BYTE_1_GRAY;
    }
    
    /** Not supported
     * @param xPoints
     * @param xOffset
     * @param yPoints
     * @param yOffset
     * @param nPoints
     * @param argbColor
     */
    public void drawPolygon(int xPoints[], int xOffset, int yPoints[], int yOffset, int nPoints, int argbColor) {
    }
    
    /** Not supported
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x3
     * @param y3
     * @param argbColor
     */
    public void drawTriangle(int x1, int y1, int x2, int y2, int x3, int y3, int argbColor) {
    }
    
    /** Not supported
     * @param xPoints
     * @param xOffset
     * @param yPoints
     * @param yOffset
     * @param nPoints
     * @param argbColor
     */
    public void fillPolygon(int xPoints[], int xOffset, int yPoints[], int yOffset, int nPoints, int argbColor) {
    }
    
    /** Not supported
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x3
     * @param y3
     * @param argbColor
     */
    public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3, int argbColor) {
    }
    
    //manipulations are not supported!
    /**
     * @param pix
     * @param alpha
     * @param off
     * @param scanlen
     * @param x
     * @param y
     * @param width
     * @param height
     * @param manipulation ignored, since manipulations are not supported at the moment
     * @param format
     */
    public void drawPixels(byte[] pix, byte[] alpha, int off, int scanlen,
    int x, int y, int width, int height, int manipulation, int format) {
        if (pix == null) {
            throw new NullPointerException();
        }
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException();
        }
        
        Graphics g=graphics;
        int c;
        
        if (format == TYPE_BYTE_1_GRAY) {
            
            int b=7;
            
            for (int yj = 0; yj < height; yj++) {
                int line = off + yj * scanlen;
                int ypos = yj * width;
                for (int xj = 0; xj < width; xj++) {
                    c=doAlpha(pix, alpha, (line + xj) /8,b);
                    if((c >> 24 & 0xff)!=0)//alpha
                    {
                        if (g.getColor()!=c) g.setColor(c);
                        g.drawLine(xj+x,yj+y,xj+x,yj+y);
                    }
                    b--;
                    if(b<0)b=7;
                }
            }
        }
        else if (format == TYPE_BYTE_1_GRAY_VERTICAL) {
            int ods = off / scanlen;
            int oms = off % scanlen;
            int b=0;
            for (int yj = 0; yj < height; yj++) {
                int ypos = yj * width;
                int tmp = (ods + yj) /8 * scanlen+oms;
                for (int xj = 0; xj < width; xj++) {
                    c=doAlpha(pix, alpha, tmp + xj, b);
                    if (g.getColor()!=c) g.setColor(c);
                    if((c >> 24 & 0xff)!=0) //alpha
                        g.drawLine(xj+x,yj+y,xj+x,yj+y);
                }
                b++;
                if(b>7) b=0;
            }
        } else
            throw new IllegalArgumentException();
    }
    
    /** Only TYPE_USHORT_4444_ARGB format supported
     * @param pix
     * @param trans
     * @param off
     * @param scanlen
     * @param x
     * @param y
     * @param width
     * @param height
     * @param manipulation
     * @param format
     */
    public void drawPixels(short pix[], boolean trans, int off, int scanlen,
    int x, int y, int width, int height, int manipulation, int format) {
        if (format != TYPE_USHORT_4444_ARGB) {
            throw new IllegalArgumentException("Illegal format: " + format);
        }
        
        //Graphics g = image.getGraphics();
        Graphics g = graphics;
        
        for (int iy = 0; iy < height; iy++) {
            for (int ix = 0; ix < width; ix ++) {
                g.setColor(pix[off + ix + iy * scanlen]);
                g.drawLine(x + ix, y + iy,x + ix, y + iy);
            }
        }
        
        //graphics.drawImage(image, 0, 0, Graphics.TOP | Graphics.LEFT);
    }
    
    /** Not supported
     * @param pix
     * @param trans
     * @param off
     * @param scanlen
     * @param x
     * @param y
     * @param width
     * @param height
     * @param manipulation
     * @param format
     */
    public void drawPixels(int pix[], boolean trans, int off, int scanlen, int x, int y, int width,
    int height, int manipulation, int format) {
        throw new IllegalArgumentException();
    }
    
    /** Not supported
     * @param pix
     * @param alpha
     * @param offset
     * @param scanlen
     * @param x
     * @param y
     * @param width
     * @param height
     * @param format
     */
    public void getPixels(byte pix[], byte alpha[], int offset, int scanlen, int x, int y, int width,
    int height, int format) {
        throw new IllegalArgumentException();
    }
    
    /** Only TYPE_USHORT_4444_ARGB format supported
     * @param pix
     * @param offset
     * @param scanlen
     * @param x
     * @param y
     * @param width
     * @param height
     * @param format
     */
    public void getPixels(short pix[], int offset, int scanlen,
    int x, int y, int width, int height, int format) {
        if (format != TYPE_USHORT_4444_ARGB) {
            throw new IllegalArgumentException("Illegal format: " + format);
        }
        
        /*for (int iy = 0; iy < height; iy++) {
            for (int ix = 0; ix < width; ix++) {
                pix[offset + ix + iy * scanlen] = (short) ((MutableImage) image).getPixel(x + ix, y + iy);
            }
        }*/
    }
    
    /** Not supported
     * @param pix
     * @param offset
     * @param scanlen
     * @param x
     * @param y
     * @param width
     * @param height
     * @param format
     */
    public void getPixels(int pix[], int offset, int scanlen,
    int x, int y, int width, int height, int format) {
        throw new IllegalArgumentException();
    }
    
    
    private static int doAlpha(byte[] pix, byte[] alpha, int pos, int shift) {
        int p;
        int a;
        if (isBitSet(pix[pos],shift))
            p=0;
        else
            p=0x00FFFFFF;
        if (alpha == null || isBitSet(alpha[pos],shift))
            a=0xFF000000;
        else
            a=0;
        return p|a;
    }
    
    
    private static boolean isBitSet(byte b, int pos) {
        return ((b & (byte)(1 << pos)) != 0);
    }
    
}
