/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@it.pl>
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
 *
 *  Contributor(s):
 *    3GLab
 */
 
package com.barteo.midp.lcdui;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import com.barteo.emulator.MIDletBridge;
import com.barteo.emulator.SoftButton;


public class DisplayBridge 
{

  static DisplayAccess da = null;
  static DisplayComponent dc = null;


  public static void setAccess(DisplayAccess a_da) 
  {
    da = a_da;
  }


  public static void setComponent(DisplayComponent a_dc) 
  {
    dc = a_dc;
  }


  public static void setCurrent(Displayable d) 
  {
    if (da != null) {
      da.setCurrent(d);
    }
  }


  public static DisplayAccess getAccess() 
  {
    return da;
  }


  public static DisplayComponent getComponent() 
  {
    return dc;
  }


  public static int getGameAction(int keyCode) 
  {
        switch (keyCode) {
        case KeyEvent.VK_UP:
            return Canvas.UP;
        case KeyEvent.VK_DOWN:
            return Canvas.DOWN;
        case KeyEvent.VK_LEFT:
      return Canvas.LEFT;
        case KeyEvent.VK_RIGHT:
      return Canvas.RIGHT;
        case KeyEvent.VK_ENTER:
      return Canvas.FIRE;
       case KeyEvent.VK_A:
            return Canvas.GAME_A;
        case KeyEvent.VK_B:
            return Canvas.GAME_B;
        case KeyEvent.VK_C:
            return Canvas.GAME_C;
        case KeyEvent.VK_D:
            return Canvas.GAME_D;

        case KeyEvent.VK_0:
        case KeyEvent.VK_1:
        case KeyEvent.VK_2:
        case KeyEvent.VK_3:
        case KeyEvent.VK_4:
        case KeyEvent.VK_5:
        case KeyEvent.VK_6:
        case KeyEvent.VK_7:
        case KeyEvent.VK_8:
        case KeyEvent.VK_9:
            int rval = Canvas.KEY_NUM0 + (keyCode-KeyEvent.VK_0);
            return rval;
        case KeyEvent.VK_MULTIPLY:
            return Canvas.KEY_STAR;
        case KeyEvent.VK_NUMBER_SIGN:
            return Canvas.KEY_POUND;
        default:
      return 0;
    }
  }


  public static int getKeyCode(int gameAction) 
  {
        switch (gameAction) {
        case Canvas.UP:
            return KeyEvent.VK_UP;
        case Canvas.DOWN:
            return KeyEvent.VK_DOWN;
        case Canvas.LEFT:
            return KeyEvent.VK_LEFT;
        case Canvas.RIGHT:
            return KeyEvent.VK_RIGHT;
        case Canvas.FIRE:
            return KeyEvent.VK_ENTER;
        case Canvas.GAME_A:
            return KeyEvent.VK_A;
        case Canvas.GAME_B:
            return KeyEvent.VK_B;
        case Canvas.GAME_C:
            return KeyEvent.VK_C;
        case Canvas.GAME_D:
            return KeyEvent.VK_D;

        case Canvas.KEY_NUM0:
        case Canvas.KEY_NUM1:
        case Canvas.KEY_NUM2:        
        case Canvas.KEY_NUM3:        
        case Canvas.KEY_NUM4:
        case Canvas.KEY_NUM5:
        case Canvas.KEY_NUM6:
        case Canvas.KEY_NUM7:        
        case Canvas.KEY_NUM8:        
        case Canvas.KEY_NUM9:
            int rval = KeyEvent.VK_0 + (gameAction-Canvas.KEY_NUM0);
            return rval;
        case Canvas.KEY_POUND:
            return KeyEvent.VK_NUMBER_SIGN;
        case Canvas.KEY_STAR:
            return KeyEvent.VK_MULTIPLY;
        default:
            return 0;
        }
  }


  public static Displayable getCurrent() 
  {
    if (da != null) {
      return da.getCurrent();
    }

    return null;
  }


  public static void commandAction(Command cmd) 
  {
    if (da != null) {
      da.commandAction(cmd);
    }
  }


  public static void keyPressed(int keyCode) 
  {
    if (da != null) {
      da.keyPressed(keyCode);
    }
  }


  public static void keyReleased(int keyCode) 
  {
    if (da != null) {
      da.keyReleased(keyCode);
    }
  }


  public static void paint(Graphics g) 
  {
    if (da != null) {
      DisplayGraphics dg = new DisplayGraphics(g);
      da.paint(dg);
    }
  }


  public static void updateCommands(Vector commands) 
  {
    CommandManager.getInstance().updateCommands(commands);
    
    /**
     * updateCommands has changed the softkey labels
     * tell the outside world it has happened.
     */
    MIDletBridge.notifySoftkeyLabelsChanged();
    repaint();
  }


  public static void repaint() 
  {
    if (dc != null) {
      dc.repaint();
    }
  }


  public static void setScrollDown(boolean state) 
  {
    if (dc != null) {
      dc.setScrollDown(state);
    }
  }


  public static void setScrollUp(boolean state) 
  {
    if (dc != null) {
      dc.setScrollUp(state);
    }
  }

}
