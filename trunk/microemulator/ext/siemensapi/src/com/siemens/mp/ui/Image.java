/*
 *  Siemens API for MicroEmulator
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
package com.siemens.mp.ui;

import javax.microedition.lcdui.Graphics;

/**
 *
 * @author  markus
 * @version
 */
public class Image {
    public Image(byte[] imageData) {
        System.out.println("public Image(byte[] imageData)");
    }
    
    public Image(byte[] bytes, int imageWidth, int imageHeight) {
        System.out.println("public Image(byte[] bytes, int imageWidth, int imageHeight)");
    }
    
    public Image(byte[] bytes, int imageWidth, int imageHeight, boolean transparent) {
        System.out.println("public Image(byte[] bytes, int imageWidth, int imageHeight, boolean transparent)");
    }
    
    public Image(Image image) {
        System.out.println("public Image(Image image)");
    }
    
    public Image(int imageWidth, int imageHeight) {
        System.out.println("public Image(int imageWidth, int imageHeight)");
    }
    
    public Image(String name, boolean doScale) {
        System.out.println("public Image(String name, boolean doScale)");
    }
    
    
    public static javax.microedition.lcdui.Image createImageFromBitmap(byte[] imageData, int imageWidth, int imageHeight) {
        
        return createImageFromBitmap(imageData,null,imageWidth,imageHeight);
    }
    
    //not in siemens real siemens api!!!
    public static javax.microedition.lcdui.Image createImageFromBitmap(byte[] imageData, byte[] alpha, int imageWidth, int imageHeight) {
        if(imageData==null) return null;
        
        javax.microedition.lcdui.Image ret=javax.microedition.lcdui.Image.createImage(imageWidth, imageHeight);
        
        if (imageWidth<8) imageWidth=8;
        
        Graphics g=ret.getGraphics();
        g.setColor(0);
        int c;
        
        for (int y=0;y<imageHeight;y++) {
            for(int x=0;x<imageWidth/8;x++) {
                for(int b=7;b>=0;b--) {
                    c=doAlpha(imageData, alpha, y*imageWidth/8+x,b);
                    g.setColor(c);
                    g.drawLine(x*8+7-b, y, x*8+7-b, y);
                    
                    
                }
            }
        }
        
        return ret;
    }
    public static javax.microedition.lcdui.Image createImageWithoutScaling(String name) {
        System.out.println("public static javax.microedition.lcdui.Image createImageWithoutScaling(String name)");
        return null;
    }
    
    public static javax.microedition.lcdui.Image createTransparentImageFromBitmap(byte[] imageData, int imageWidth, int imageHeight) {
        if(imageData==null) return null;
        
        javax.microedition.lcdui.Image ret=javax.microedition.lcdui.Image.createImage(imageWidth, imageHeight);
        
        if (imageWidth<4) imageWidth=4;
        
        Graphics g=ret.getGraphics();
        for (int y=0;y<imageHeight;y++) {
            for(int x=0;x<imageWidth/4;x++) {
                for(int b=7;b>=0;b-=2) {
                    if(isBitSet(imageData[y*imageWidth/4+x],b)) {
                        g.drawLine(x*4+3-b/2, y, x*4+3-b/2, y);
                    }
                }
                
            }
        }
        
        return ret;
    }
    
    public int getHeight() {
        System.out.println("public int getHeight()");
        return 0;
    }
    
    public static Image getNativeImage(javax.microedition.lcdui.Image img) {
        System.out.println("public static Image getNativeImage(javax.microedition.lcdui.Image img)");
        return null;
    }
    
    public int getWidth() {
        System.out.println("public int getWidth()");
        return 0;
    }
    
    private static boolean isBitSet( byte b, int pos ) {
        boolean retval = false;
        
        //if( (pos >= 0) && (pos < NUM_BITS) ) {
        retval = ((b & (byte)(1 << pos)) != 0);
        //}
        
        return retval;
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
}
