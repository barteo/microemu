/*
 *  @(#)SimpleDemo.java  10/10/2001
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

import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;


public class SimpleDemo extends MIDlet implements CommandListener
{
  
  static SimpleDemo instance;

  List menuList;
  ScreenPanel screenPanels[] = {
      new AlertPanel(),
      new CanvasPanel(),
      new DateFieldPanel(),
      new GaugePanel(),
      new ImageItemPanel(),
      new ListPanel(),
      new TextBoxPanel()
  };
  
  Command exitCommand = new Command("Exit", Command.EXIT, 1);

  
  public SimpleDemo()
  {
    instance = this;
    
    Ticker ticker = new Ticker("This is SimpleDemo ticker");
    
    menuList = new List("SimpleDemo", List.IMPLICIT);
      
    for (int i = 0; i < screenPanels.length; i++) {
      menuList.append(screenPanels[i].getName(), null);
      if (screenPanels[i] instanceof Screen) {
        ((Screen) screenPanels[i]).setTicker(ticker);
      }
    }
    menuList.addCommand(exitCommand);
    menuList.setCommandListener(this);
  }
    

  public void destroyApp(boolean unconditional) 
  {
  }


  public void pauseApp() 
  {
  }

  
  public void startApp() 
  {
    Display.getDisplay(this).setCurrent(menuList);
  }
  
 
  public static SimpleDemo getInstance()
  {
    return instance;
  }
  
  
  public void commandAction(Command c, Displayable d)
  {
    if (d == menuList) {
      if (c == List.SELECT_COMMAND) {
        Display.getDisplay(this).setCurrent((Displayable) screenPanels[menuList.getSelectedIndex()]);
      } else if (c == exitCommand) {
        destroyApp(true);
        notifyDestroyed();
      }
    }
  }

}
