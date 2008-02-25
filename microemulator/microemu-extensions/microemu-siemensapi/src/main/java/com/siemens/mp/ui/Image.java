/*
 *  Siemens API for MicroEmulator
 *  Copyright (C) 2003 Markus Heberling <markus@heberling.net>
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
    public static javax.microedition.lcdui.Image createImageWithoutScaling(String name) throws java.io.IOException{
        System.out.println("public static javax.microedition.lcdui.Image createImageWithoutScaling(String name)");
        return javax.microedition.lcdui.Image.createImage(name);
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
