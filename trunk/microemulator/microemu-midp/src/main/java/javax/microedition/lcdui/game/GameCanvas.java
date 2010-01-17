/*
 *  MicroEmulator
 *  Copyright (C) 2005 Andres Navarro
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

package javax.microedition.lcdui.game;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

import org.microemu.GameCanvasKeyAccess;
import org.microemu.MIDletBridge;
import org.microemu.device.DeviceFactory;


/**
 *
 * @author Andres Navarro
 * @author radoshi
 */
public abstract class GameCanvas extends Canvas {
    // keystate constants
    public static final int UP_PRESSED = 1 << Canvas.UP;
    public static final int DOWN_PRESSED = 1 << Canvas.DOWN;
    public static final int LEFT_PRESSED = 1 << Canvas.LEFT;
    public static final int RIGHT_PRESSED = 1 << Canvas.RIGHT;
    public static final int FIRE_PRESSED = 1 << Canvas.FIRE;
    public static final int GAME_A_PRESSED = 1 << Canvas.GAME_A;
    public static final int GAME_B_PRESSED = 1 << Canvas.GAME_B;
    public static final int GAME_C_PRESSED = 1 << Canvas.GAME_C;
    public static final int GAME_D_PRESSED = 1 << Canvas.GAME_D;
    
    // true if this GameCanvas doesn't generate
    // key events for the game keys
    private boolean suppressKeyEvents;
    // the latched state of the keys
    // reseted on call to getKeyStates
    private int latchedKeyState;
    // the current keys state
    // this is copied to latchedKeyState
    // on call to getKeyState
    private int actualKeyState;
    
    private class KeyAccess implements GameCanvasKeyAccess {
        
        public boolean suppressedKeyEvents(GameCanvas canvas) {
            return canvas.suppressKeyEvents;
        }
        
        public void recordKeyPressed(GameCanvas canvas, int gameCode) {
            int bit = 1 << gameCode;
            synchronized(canvas) {
                latchedKeyState |= bit;
                actualKeyState |= bit;
            }
        }
        
        public void recordKeyReleased(GameCanvas canvas, int gameCode) {
            int bit = 1 << gameCode;
            synchronized(canvas) {
                actualKeyState &= ~bit;
            }
        }
        
        public void setActualKeyState(GameCanvas canvas, int keyState) {
            synchronized(canvas) {
                actualKeyState = keyState;
            }
        }
        
    }
    
    /** Creates a new instance of GameCanvas */
    protected GameCanvas(boolean suppressKeyEvents) 
    {
        MIDletBridge.registerGameCanvasKeyAccess(this, new KeyAccess());
        
        this.suppressKeyEvents = suppressKeyEvents;
    }
    
    protected Graphics getGraphics() {
        return DeviceFactory.getDevice().getDeviceDisplay().getGraphics(this);
    }
    
    public void paint(Graphics g) {
    }
    
    public void flushGraphics(int x, int y, int width, int height) {
        DeviceFactory.getDevice().getDeviceDisplay().flushGraphics(x, y, width, height);
    }

    public void flushGraphics() {
    	flushGraphics(0, 0, getWidth(), getHeight());
    }

    public int getKeyStates() {
    	synchronized(this) {
	        int ret = latchedKeyState;
	        latchedKeyState = actualKeyState;
	        return ret;
    	}
    }
    
    // TODO
    // should isDoubleBuffered from class Canvas return true in this class??
}

    
