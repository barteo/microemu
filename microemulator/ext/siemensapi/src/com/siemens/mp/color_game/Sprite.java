/*
 * Sprite.java
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
public class Sprite extends Layer{
    public Sprite(javax.microedition.lcdui.Image image)
    {
        super(image);
    }
    
    public Sprite(javax.microedition.lcdui.Image image, int frameWidth, int frameHeight)
    {
        super(image);
    }
    
    public Sprite(com.siemens.mp.color_game.Sprite s)
    {
        super(s.getLayerImage());
    }
    
    public boolean collidesWith(javax.microedition.lcdui.Image image, int x, int y, boolean pixelLevel)
    {
        return false;
    }
    
    public boolean collidesWith(com.siemens.mp.color_game.Sprite s, boolean pixelLevel)
    {
        return false;
    }
    
    public int getFrame()
    {
        return 0;
    }
    
    public int getFrameSequenceLength()
    {
        return 0;
    }
    
    public int getRawFrameCount()
    {
        return 0;
    }
    
    public void nextFrame()
    {
    }
    
    public void paint(javax.microedition.lcdui.Graphics g)
    {
    }
    
    public void prevFrame()
    {
    }
    
    public void setCollisionRectangle(int x, int y, int width, int height)
    {
    }
    
    public void setFrame(int sequenceIndex)
    {
    }
    
    public void setFrameSequence(int[] sequence)
    {
    }
    
    public void setImage(javax.microedition.lcdui.Image image, int frameWidth, int frameHeight)
    {
    }
}
