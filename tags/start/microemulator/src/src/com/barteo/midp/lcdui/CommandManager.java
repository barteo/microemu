/*
 * @(#)CommandManager.java  07/07/2001
 *
 * Copyright (c) 2001 Bartek Teodorczyk <barteo@it.pl>. All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package com.barteo.midp.lcdui;

import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import com.barteo.emulator.Device;
import com.barteo.emulator.SoftButton;


public class CommandManager
{

  static CommandManager instance = new CommandManager();
  
  static final Command MENU_COMMAND = new Command("Menu", Command.SCREEN, 0);
  static final Command BACK_COMMAND = new Command("Back", Command.BACK, 0);
  static final List commandList = new List("Menu", Choice.IMPLICIT);
  static CommandListener commandManagerListener;
  static Displayable previous;
  
  static Vector menuCommands = new Vector();

  static class CommandManagerListener implements CommandListener
  {
    
    public void commandAction(Command cmd, Displayable d)
    {
      DisplayBridge.setCurrent(previous);
			if (cmd == List.SELECT_COMMAND) {
				DisplayBridge.commandAction((Command) menuCommands.elementAt(commandList.getSelectedIndex()));
			}
    }
    
  }
  
  static {
    commandManagerListener = new CommandManagerListener();
    commandList.addCommand(BACK_COMMAND);
    commandList.setCommandListener(commandManagerListener);
  }
  
  public static CommandManager getInstance()
  {
    return instance;
  }


  void commandAction(Command cmd)
  {
    if (cmd == MENU_COMMAND) {
      previous = DisplayBridge.getCurrent();
      DisplayBridge.setCurrent(commandList);
    } else {
      DisplayBridge.commandAction(cmd);
    }
  }
  
  
  void updateCommands(Vector commands)
  {
    Command cmd;
    SoftButton sb;
    boolean menu_needed = false;
    
    for (Enumeration s = Device.getSoftButtons().elements();  s.hasMoreElements() ;) {
      ((SoftButton) s.nextElement()).removeCommand();
  	}

		if (commands == null) {
			return;
		}

    // Check Menu Screen is needed & sort
		menuCommands.removeAllElements();
  	for (Enumeration e = commands.elements() ; e.hasMoreElements() ;) {
      cmd = (Command) e.nextElement();
		  for (Enumeration s = Device.getSoftButtons().elements();  s.hasMoreElements() ;) {
        sb = (SoftButton) s.nextElement();
        if (!sb.setCommand(cmd)) {
          if (sb.testCommandType(cmd)) {
            menu_needed = true;
          }
        }
			}
			boolean inserted = false;
			for (int i = 0; i < menuCommands.size(); i++) {
				if (cmd.getPriority() < ((Command) menuCommands.elementAt(i)).getPriority()) {
					menuCommands.insertElementAt(cmd, i);
					inserted = true;
					break;
				}
			}
			if (!inserted) {
				menuCommands.addElement(cmd);
			}
    }
    
    if (menu_needed) {
      while (commandList.size() > 0) {
        commandList.delete(0);
      }
		  for (Enumeration s = Device.getSoftButtons().elements(); s.hasMoreElements() ;) {
        sb = (SoftButton) s.nextElement();
        if (sb.getMenuActivate()) {
          sb.removeCommand();
          sb.setCommand(MENU_COMMAND);
        }
			}
    }
    
  	for (int i = 0; i < menuCommands.size() ; i++) {
      boolean alreadyInserted = false;
      cmd = (Command) menuCommands.elementAt(i);
		  for (Enumeration s = Device.getSoftButtons().elements(); s.hasMoreElements() ;) {
        if (((SoftButton) s.nextElement()).getCommand() == cmd) {
          alreadyInserted = true;
        }
			}
      if (!alreadyInserted) {
        commandList.append(cmd.getLabel(), null);
      } else {
				menuCommands.removeElementAt(i);
				i--;
			}
    }    
  }
  
}
