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


  public static int getKeyCode(int gameAction) 
  {
    return gameAction;
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
