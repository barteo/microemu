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
package com.siemens.mp.game;

import javax.microedition.lcdui.*;
/**
 *
 * @author  markus
 * @version
 */
public class Sprite extends GraphicObject{
    Image pixels[];
    Image mask[];
    int x;
    int y;
    int frame;
    int collx,colly,collw,collh;
    
    public Sprite(byte[] pixels, int pixel_offset, int width, int height, byte[] mask, int mask_offset, int numFrames) {
        this(
        com.siemens.mp.ui.Image.createImageFromBitmap(pixels,mask,width,height*numFrames),
        com.siemens.mp.ui.Image.createImageFromBitmap(mask,width,height*numFrames),
        numFrames
        );
    }
    
    public Sprite(ExtendedImage pixels, ExtendedImage mask, int numFrames) {
        this(pixels.getImage(),mask.getImage(),numFrames);
    }
    
    public Sprite(Image pixels, Image mask, int numFrames) {
        this.pixels=new Image[numFrames];
        
        for (int i=0;i<numFrames;i++) {
            Image img=Image.createImage(pixels.getWidth(), pixels.getHeight()/numFrames);
            
            img.getGraphics().drawImage(pixels, 0, -i*pixels.getHeight()/numFrames,0);
            this.pixels[i]=img;
         }
        
        if(mask!=null) {
            this.mask=new Image[numFrames];
            for (int i=0;i<numFrames;i++) {
                Image img=Image.createImage(mask.getWidth(), mask.getHeight()/numFrames);
                
                img.getGraphics().drawImage(mask, 0, -i*mask.getHeight()/numFrames,0);
                this.mask[i]=img;
            }
        }
        //this.pixels=pixels;
        //this.mask=mask;
        collx=0;
        colly=0;
        collw=this.pixels[0].getWidth();
        collh=this.pixels[0].getHeight();
    }
    
    public int getFrame() {
        //System.out.println("public int getFrame()");
        return frame;
    }
    
    public int getXPosition() {
        //System.out.println("public int getXPosition()");
        return x;
    }
    
    public int getYPosition() {
        //System.out.println("public int getYPosition()");
        return y;
    }
    
    public boolean isCollidingWith(Sprite other) {
        //System.out.println("public boolean isCollidingWith(Sprite other)");
        return false;
    }
    
    public boolean isCollidingWithPos(int xpos, int ypos) {
        //System.out.println("public boolean isCollidingWithPos(int xpos, int ypos)");
        return  (xpos>=x+collx)&&(xpos<x+collw)&&
                (ypos>=y+colly)&&(ypos<y+collh);
    }
    
    public void setCollisionRectangle(int x, int y, int width, int height) {
        //System.out.println("public void setCollisionRectangle(int x, int y, int width, int height)");
    collx=x;
    colly=y;
    collw=width;
    collh=height;
    }
    
    public void setFrame(int framenumber) {
        //System.out.println("public void setFrame(int framenumber)");
        frame=framenumber;
    }
    
    public void setPosition(int x, int y) {
        //System.out.println("public void setPosition(int x, int y)");
        this.x=x;
        this.y=y;
    }
    
    protected void paint(Graphics g) {
        //System.out.println(frame);
        g.drawImage(pixels[frame], x,y,0);
        //for(int i=0;i<pixels.length;i++) g.drawImage(pixels[i].getImage(), 20,y*pixels[i].getImage().getHeight(),0);
        //g.drawImage(mask.getImage(), x,y,0);
    }
}
