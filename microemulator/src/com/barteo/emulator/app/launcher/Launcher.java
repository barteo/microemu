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
  
  MIDlet currentMIDlet = null;
  
  
  public Launcher()
  {
    menuList = new List("Launcher", List.IMPLICIT);

    menuList.setCommandListener(this);
  }


  public void addMIDletEntry(MIDletEntry entry)
  {
    midletEntries.addElement(entry);
  }
  
  
  public void removeMIDletEntries()
  {
    midletEntries.removeAllElements();
  }
  
  
  public MIDlet getCurrentMIDlet()
  {
    return currentMIDlet;
  }
  
  
  public void setCurrentMIDlet(MIDlet midlet)
  {
    currentMIDlet = midlet;
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
          currentMIDlet = ((MIDletEntry) midletEntries.elementAt(menuList.getSelectedIndex())).getMIDlet();
          try {
            MIDletBridge.getAccess(currentMIDlet).startApp();
      		} catch (MIDletStateChangeException ex) {
            System.err.println(ex);
      		}
        }
      }
    }
  }
  
}
