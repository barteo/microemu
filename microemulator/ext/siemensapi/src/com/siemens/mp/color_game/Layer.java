/*
 * Layer.java
 *
 * Created on 2. Juli 2003, 12:45
 */

package com.siemens.mp.color_game;

import javax.microedition.midlet.*;

/**
 *
 * @author  markus
 * @version
 */
public abstract class Layer {
    protected Layer(javax.microedition.lcdui.Image image) {
    }
    
    protected Layer(int width, int height) {
    }
    
    protected  void copyAllLayerVariables(com.siemens.mp.color_game.Layer l) {
    }
    
    public int getHeight() {
        return 0;
    }
    
    protected  javax.microedition.lcdui.Image getLayerImage() {
        return null;
    }
    
    public int getWidth() {
        return 0;
    }
    
    public int getX() {
        return 0;
    }
    
    public int getY() {
        return 0;
    }
    
    public boolean isVisible() {
        return false;
    }
    
    public void move(int dx, int dy) {
    }
    
    public abstract  void paint(javax.microedition.lcdui.Graphics g);
    
    public void setHeight(int height) {
    }
    
    protected  void setLayerImage(javax.microedition.lcdui.Image image) {
    }
    
    public void setPosition(int x, int y) {
    }
    
    public void setVisible(boolean visible) {
    }
    
    public void setWidth(int width) {
    }
}
