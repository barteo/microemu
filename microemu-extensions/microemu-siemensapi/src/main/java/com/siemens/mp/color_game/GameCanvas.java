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