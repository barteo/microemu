/*
 *  @(#)AlertPanel.java  10/10/2001
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


public class AlertPanel extends List implements ScreenPanel, CommandListener
{

  static String NAME = "Alert";

  Alert alerts[] = {
      new Alert("Alarm alert", 
          "This is alarm alert", null, AlertType.ALARM),
      new Alert("Confirmation alert", 
          "This is confirmation alert with 5 sec timeout", null, AlertType.CONFIRMATION),
      new Alert("Error alert", 
          "This is error alert", null, AlertType.ERROR),
      new Alert("Info alert", 
          "This is info alert with 5 sec timeout", null, AlertType.INFO),
      new Alert("Warning alert", 
          "This is warning alert", null, AlertType.WARNING)
  };  
  
  Command backCommand = new Command("Back", Command.BACK, 1);
  
  
  public AlertPanel()
  {
    super(NAME, List.IMPLICIT);
    
    for (int i = 0; i < alerts.length; i++) {
      if (i == 1 || i == 3) {
        alerts[i].setTimeout(5000);
      }
      append(alerts[i].getTitle(), null);
    }
    
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
      if (c == List.SELECT_COMMAND) {
        Display.getDisplay(SimpleDemo.getInstance()).setCurrent(alerts[getSelectedIndex()]);
      } else if (c == backCommand) {
        Display.getDisplay(SimpleDemo.getInstance()).setCurrent(SimpleDemo.getInstance().menuList);
      }
    } 
  }

}
