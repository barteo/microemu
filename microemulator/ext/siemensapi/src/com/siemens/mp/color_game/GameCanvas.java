/*
 * GameCanvas.java
 *
 * Created on 2. Juli 2003, 12:24
 */

package com.siemens.mp.color_game;

import javax.microedition.lcdui.*;

/**
 *
 * @author  markus
 * @version
 */
public class GameCanvas extends Canvas{
    private Graphics g;
    private Image img;
    protected GameCanvas(boolean suppressKeyEvents)
    {
        super();
        img=Image.createImage(getWidth(),getHeight());
        g=img.getGraphics();
    }
    
    public void flushGraphics()
    {
        repaint();
    }
    
    public  void flushGraphics(int x, int y, int width, int height)
    {
        repaint(x,y, width, height);
    }
    
    protected  javax.microedition.lcdui.Graphics getGraphics()
    {
        return g;
    }
    
    public int getKeyStates()
    {
        return 0;
    }
    
    protected void keyPressed(int keyCode)
    {
    }
    
    protected  void keyRepeated(int keyCode)
    {
    }
    
    public  void paint(javax.microedition.lcdui.Graphics g)
    {
        g.drawImage(img,0,0,0);
    }
}
