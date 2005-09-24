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

import javax.microedition.lcdui.*;

public abstract class GameCanvas extends Canvas {
    public static final int UP_PRESSED = 1 << Canvas.UP;
    public static final int DOWN_PRESSED = 1 << Canvas.DOWN;
    public static final int LEFT_PRESSED = 1 << Canvas.LEFT;
    public static final int RIGHT_PRESSED = 1 << Canvas.RIGHT;
    public static final int FIRE_PRESSED = 1 << Canvas.FIRE;
    public static final int GAME_A_PRESSED = 1 << Canvas.GAME_A;
    public static final int GAME_B_PRESSED = 1 << Canvas.GAME_B;
    public static final int GAME_C_PRESSED = 1 << Canvas.GAME_C;
    public static final int GAME_D_PRESSED = 1 << Canvas.GAME_D;
    private Image offscreen_buffer;
    
    private int keyMask;
    
    
    protected GameCanvas(boolean suppressKeyEvents) {
        offscreen_buffer =
        Image.createImage(getWidth(), getHeight());
    }
    
    
    
    protected Graphics getGraphics() {
        return offscreen_buffer.getGraphics();
    }
    
    
    public int getKeyStates() {
        int ret=keyMask;
        keyMask=0;
        return ret;
    }
    
    
    public void paint(Graphics g) {
        g.drawImage(offscreen_buffer, 0, 0, Graphics.TOP|Graphics.LEFT);
    }
    
    public void flushGraphics(int x, int y, int width, int height) {
        
        repaint(x,y,width,height);
    }
    

    public void flushGraphics() {
        repaint();
    }
    

    protected void keyPressed(int keyCode) {
        //super.keyPressed(keyCode);
        keyMask=keyMask|(1<<getGameAction(keyCode));
    }
    
    /**
     * Called when a key is released.
     */
    protected void keyReleased(int keyCode) {
        //super.keyReleased(keyCode);
    }
    
    /**
     * Called when a key is repeated (held down).
     */
    protected void keyRepeated(int keyCode) {
        //super.keyRepeated(keyCode);
    }
    
    protected void hideNotify() {
        keyMask=0;
    }
}