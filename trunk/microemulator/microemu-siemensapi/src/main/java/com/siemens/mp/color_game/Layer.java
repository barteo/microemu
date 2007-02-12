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
package com.siemens.mp.color_game;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public abstract class Layer {
    Image image;
    protected Layer(int width, int height) {
        this.width=width;
        this.height=height;
    }
    
    protected Layer(Image img) {
        this(img.getWidth(),img.getHeight());
        image=img;
    }
    
    protected  void copyAllLayerVariables(com.siemens.mp.color_game.Layer l) {
        l.width=width;
        l.height=height;
        l.x=x;
        l.y=y;
        l.image=image;
    }
    
    protected void setLayerImage(Image img) {
        image=img;
    }
    
    protected  javax.microedition.lcdui.Image getLayerImage() {
        return image;
    }
    
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    
    public void move(int dx, int dy) {
        x += dx;
        y += dy;
    }
    
    
    public final int getX() {
        return x;
    }
    
    
    public final int getY() {
        return y;
    }
    
    public final int getWidth() {
        return width;
    }
    
    
    public final int getHeight() {
        return height;
    }
    
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    
    public final boolean isVisible() {
        return visible;
    }
    
    public abstract void paint(Graphics g);
    
    void setWidthImpl(int w){
        width=w;
    }
    
    void setHeightImpl(int h){
        height=h;
    }
    
    int x; // = 0;
    int y; // = 0;
    int width; // = 0;
    int height; // = 0;
    boolean visible = true;
    
}