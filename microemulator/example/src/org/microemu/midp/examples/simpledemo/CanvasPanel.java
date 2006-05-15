/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
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
 
package org.microemu.midp.examples.simpledemo;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;


public class CanvasPanel extends Canvas implements ScreenPanel, CommandListener
{ 
  private static final String NAME = "Canvas";
  private static final int POSNUMBER = 20;
  
  private static final Command backCommand = new Command("Back", Command.BACK, 1);
  private static final Command neCommand = new Command("NE Move", Command.ITEM, 1);
  private static final Command nwCommand = new Command("NW Move", Command.ITEM, 2);
  private static final Command seCommand = new Command("SE Move", Command.ITEM, 3);
  private static final Command swCommand = new Command("SW Move", Command.ITEM, 4);

	boolean cancel = false;
	
  private int moveX = 1, moveY = 1;
  private int posX = 0, posY = 0;

  private Runnable timerTask = new Runnable()
  {
    
    public void run()
    {
      while (!cancel) {
        if (isShown()) {
          synchronized (this) {
            if (moveX > 0) {
              if (posX >= POSNUMBER) {
                posX = 0;
              }
            } else {
              if (posX < 0) {
                posX = POSNUMBER;
              }
            }
            if (moveY > 0) {
              if (posY >= POSNUMBER) {
                posY = 0;
              }
            } else {
              if (posY < 0) {
                posY = POSNUMBER;
              }
            }
            posX += moveX;
            posY += moveY;
          }
          repaint();
        }
        try {
          Thread.sleep(100);
        } catch (InterruptedException ex) {
          break;
        }
      }
    }
  };

  
  public CanvasPanel()
  {
    Thread thread = new Thread(timerTask, "CanvasPanel");
    thread.start();

    addCommand(backCommand);
    addCommand(neCommand);
    addCommand(nwCommand);
    addCommand(seCommand);
    addCommand(swCommand);
    setCommandListener(this);
  }
  

  public String getName()
  {
    return NAME;
  }


  public void commandAction(Command c, Displayable d)
  {
    if (d == this) {
      if (c == backCommand) {
        Display.getDisplay(SimpleDemo.getInstance()).setCurrent(SimpleDemo.getInstance().menuList);
        return;
      }
      synchronized (this) {
        if (c == nwCommand) {
          moveX = -1;
          moveY = -1;
        } else if (c == neCommand) {
          moveX = 1;
          moveY = -1;
        } else if (c == swCommand) {
          moveX = -1;
          moveY = 1;
        } else if (c == seCommand) {
          moveX = 1;
          moveY = 1;
        }
      }
    }
  }
  
  
  public void paint(Graphics g)
  {
    g.setGrayScale(255);
    g.fillRect(0, 0, getWidth(), getHeight());

    g.setGrayScale(0);
    g.drawRect(2, 2, getWidth() - 5, getHeight() - 5);
    
    int pos = posX;
    while (pos < getWidth() - 5) {
      g.drawLine(3 + pos, 3, 3 + pos, getHeight() - 4);
      pos += POSNUMBER;
    }
    pos = posY;
    while (pos < getHeight() - 5) {
      g.drawLine(3, 3 + pos, getWidth() - 4, 3 + pos);
      pos += POSNUMBER;
    }
  }

}
