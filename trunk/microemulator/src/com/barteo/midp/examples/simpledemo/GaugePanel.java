/*
 *  @(#)GaugePanel.java  10/10/2001
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


public class GaugePanel extends Form implements ScreenPanel, CommandListener
{
  
  static String NAME = "Gauge";
  
  Command backCommand = new Command("Back", Command.BACK, 1);
  
  Gauge noninteractive = new Gauge("Noninteractive", false, 25, 0);
  
  Runnable timerTask = new Runnable()
  {
    
    public void run()
    {
      while (true) {
        if (isShown()) {
          int value = noninteractive.getValue();
      
          if (noninteractive.getValue() >= 25) {
            noninteractive.setValue(0);
          } else {
            noninteractive.setValue(++value);
          }
        }
        try {
          Thread.sleep(500);
        } catch (InterruptedException ex) {
          break;
        }
      }
    }
  };
  
  
  public GaugePanel()
  {
    super(NAME);
    
    append(new Gauge("Interactive", true, 25, 0));
    append(noninteractive);
    
    Thread thread = new Thread(timerTask);
    thread.start();

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
  
}
