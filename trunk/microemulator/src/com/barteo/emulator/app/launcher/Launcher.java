/*
 * MicroEmulator
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.barteo.emulator.app.launcher;

import java.util.Vector;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;

import com.barteo.emulator.MIDletBridge;
import com.barteo.emulator.MIDletEntry;

/**
 * Launcher
 *      
 * @author <a href="mailto:barteo@it.pl">Bartek Teodorczyk</a>
 */
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
