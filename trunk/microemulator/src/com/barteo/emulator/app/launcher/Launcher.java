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
package com.barteo.emulator.app.launcher;

import java.util.Vector;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;

import com.barteo.emulator.MIDletBridge;
import com.barteo.emulator.MIDletEntry;


public class Launcher extends MIDlet implements CommandListener
{

  static String nomidlets = "[no midlets]";
  
  List menuList;
  
  Vector midletEntries = new Vector();
  
  
  public Launcher()
  {
    menuList = new List("Launcher", List.IMPLICIT);

    menuList.append("Test1", null);
    menuList.append("Test2", null);

    menuList.setCommandListener(this);
  }


  public void addMIDletEntry(MIDletEntry entry)
  {
    midletEntries.addElement(entry);
  }
  
  
  public void destroyApp(boolean unconditional) 
  {
  }


  public void pauseApp() 
  {
  }

  
  public void startApp() 
  {
    for (int i = menuList.size() - 1; i >= 0; i--) {
      menuList.delete(i);
    }
    if (midletEntries.size() == 0) {
      menuList.append(nomidlets, null);
    } else {
      for (int i = 0; i < midletEntries.size(); i++) {
        menuList.append(((MIDletEntry) midletEntries.elementAt(i)).getName(), null);
      }
    }
    
    Display.getDisplay(this).setCurrent(menuList);
  }

  
  public void commandAction(Command c, Displayable d)
  {
    if (d == menuList) {
      if (c == List.SELECT_COMMAND) {
        if (!menuList.getString(menuList.getSelectedIndex()).equals(nomidlets)) {
          MIDlet midlet = ((MIDletEntry) midletEntries.elementAt(menuList.getSelectedIndex())).getMIDlet();
          try {
            MIDletBridge.getAccess(midlet).startApp();
      		} catch (MIDletStateChangeException ex) {
            System.err.println(ex);
      		}
        }
      }
    }
  }
  
}
