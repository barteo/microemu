/*
 *  @(#)CanvasPanel.java  10/10/2001
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

package com.barteo.midp.examples.simpledemo;

import java.util.*;

import javax.microedition.lcdui.*;


public class CanvasPanel extends Canvas implements ScreenPanel, CommandListener
{
  
  static String NAME = "Canvas";
  
  Command backCommand = new Command("Back", Command.BACK, 1);
  
  int posMove = 0;
  static int POSNUMBER = 20;

  TimerTask timerTask = new TimerTask()
  {
    
    public void run()
    {
      if (isShown()) {
        if (posMove >= POSNUMBER) {
          posMove = 0;
        } else {
          posMove++;
        }
        repaint();
      }
    }
  };

  
  public CanvasPanel()
  {
    Timer timer = new Timer();
    timer.schedule(timerTask, 0, 100);

    addCommand(backCommand);
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
      }
    }
  }
  
  
  public void paint(Graphics g)
  {
    g.setGrayScale(255);
    g.fillRect(0, 0, getWidth(), getHeight());

    g.setGrayScale(0);
    g.drawRect(2, 2, getWidth() - 5, getHeight() - 5);
    
    int pos = posMove;
    while (pos < getWidth() - 5) {
      g.drawLine(3 + pos, 3, 3 + pos, getHeight() - 4);
      pos += POSNUMBER;
    }
    pos = posMove;
    while (pos < getHeight() - 5) {
      g.drawLine(3, 3 + pos, getWidth() - 4, 3 + pos);
      pos += POSNUMBER;
    }
  }

}
