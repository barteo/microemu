/*
 *  @(#)DisplayBridge.java  07/07/2001
 *
 *  Copyright (c) 2001 Bartek Teodorczyk <barteo@it.pl>. All Rights Reserved.
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package com.barteo.midp.lcdui;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import com.barteo.emulator.SoftButton;

/**
 *  Description of the Class
 *
 *@author     barteo
 *@created    3 wrzesieñ 2001
 */
public class DisplayBridge {

    static DisplayAccess da = null;
    static DisplayComponent dc = null;


    /**
     *  Sets the access attribute of the DisplayBridge class
     *
     *@param  a_da  The new access value
     */
    public static void setAccess(DisplayAccess a_da) {
        da = a_da;
    }


    /**
     *  Sets the component attribute of the DisplayBridge class
     *
     *@param  a_dc  The new component value
     */
    public static void setComponent(DisplayComponent a_dc) {
        dc = a_dc;
    }


    /**
     *  Sets the current attribute of the DisplayBridge class
     *
     *@param  d  The new current value
     */
    public static void setCurrent(Displayable d) {
        if (da != null) {
            da.setCurrent(d);
        }
    }


    /**
     *  Gets the access attribute of the DisplayBridge class
     *
     *@return    The access value
     */
    public static DisplayAccess getAccess() {
        return da;
    }


    /**
     *  Gets the component attribute of the DisplayBridge class
     *
     *@return    The component value
     */
    public static DisplayComponent getComponent() {
        return dc;
    }


    /**
     *  Gets the gameAction attribute of the DisplayBridge class
     *
     *@param  keyCode  Description of Parameter
     *@return          The gameAction value
     */
    public static int getGameAction(int keyCode) {
        if (keyCode == KeyEvent.VK_LEFT) {
            return Canvas.LEFT;
        } else if (keyCode == KeyEvent.VK_RIGHT) {
            return Canvas.RIGHT;
        } else if (keyCode == KeyEvent.VK_UP) {
            return Canvas.UP;
        } else if (keyCode == KeyEvent.VK_DOWN) {
            return Canvas.DOWN;
        } else if (keyCode == KeyEvent.VK_ENTER) {
            return Canvas.FIRE;
        } else {
            return 0;
        }
    }


    /**
     *  Gets the keyCode attribute of the DisplayBridge class
     *
     *@param  gameAction  Description of Parameter
     *@return             The keyCode value
     */
    public static int getKeyCode(int gameAction) {
        return gameAction;
    }


    /**
     *  Gets the current attribute of the DisplayBridge class
     *
     *@return    The current value
     */
    public static Displayable getCurrent() {
        if (da != null) {
            return da.getCurrent();
        }
        return null;
    }


    /**
     *  Description of the Method
     *
     *@param  cmd  Description of Parameter
     */
    public static void commandAction(Command cmd) {
        if (da != null) {
            da.commandAction(cmd);
        }
    }


    /**
     *  Description of the Method
     *
     *@param  keyCode  Description of Parameter
     */
    public static void keyPressed(int keyCode) {
        if (da != null) {
            da.keyPressed(keyCode);
        }
    }


    /**
     *  Description of the Method
     *
     *@param  keyCode  Description of Parameter
     */
    public static void keyReleased(int keyCode) {
        if (da != null) {
            da.keyReleased(keyCode);
        }
    }


    /**
     *  Description of the Method
     *
     *@param  g  Description of Parameter
     */
    public static void paint(Graphics g) {
        if (da != null) {
            DisplayGraphics dg = new DisplayGraphics(g);
            da.paint(dg);
        }
    }


    /**
     *  Description of the Method
     *
     *@param  commands  Description of Parameter
     */
    public static void updateCommands(Vector commands) {
        CommandManager.getInstance().updateCommands(commands);
        repaint();
    }


    /**
     *  Description of the Method
     */
    public static void repaint() {
        if (dc != null) {
            dc.repaint();
        }
    }


    /**
     *  Sets the scrollDown attribute of the DisplayBridge object
     *
     *@param  state  The new scrollDown value
     */
    public void setScrollDown(boolean state) {
        if (dc != null) {
            dc.setScrollDown(state);
        }
    }


    /**
     *  Sets the scrollUp attribute of the DisplayBridge object
     *
     *@param  state  The new scrollUp value
     */
    public void setScrollUp(boolean state) {
        if (dc != null) {
            dc.setScrollUp(state);
        }
    }

}
