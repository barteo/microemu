/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@it.pl>
 *  Copyright (C) 2002 3G Lab http://www.3glab.com
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
 
package com.barteo.emulator;

import java.util.Vector;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import com.barteo.emulator.device.DeviceFactory;
import com.barteo.emulator.device.SoftButton;


public class CommandManager 
{
  static CommandManager instance = new CommandManager();

  final static Command MENU_COMMAND = new Command("Menu", Command.SCREEN, 0);
  final static Command BACK_COMMAND = new Command("Back", Command.BACK, 0);
  final static Command SELECT_COMMAND = new Command("Select", Command.OK, 0);
  final static List commandList = new List("Menu", Choice.IMPLICIT);
  static CommandListener commandManagerListener;
  static Displayable previous;

  static Vector menuCommands = new Vector();


  public static CommandManager getInstance() 
  {
    return instance;
  }


  public void commandAction(Command cmd) 
  {
    if (cmd == MENU_COMMAND) {
      previous = MIDletBridge.getMIDletAccess().getDisplayAccess().getCurrent();
      MIDletBridge.getMIDletAccess().getDisplayAccess().setCurrent(commandList);
    } else {
      MIDletBridge.getMIDletAccess().getDisplayAccess().commandAction(cmd);
    }
  }

  /**
   * Private method used to determine if we have our
   * special menu command. In which case the SoftKeys
   * have a special setup.
   *
   * @param commands the command vector to check against
   * @param nSoftButtons the number of soft buttons we need at least 2
   */
  private boolean isMenuCommands(Vector commands, int nSoftButtons) 
  {
    if ((nSoftButtons > 1) && 
        (commands.size() == 2) &&
        ((Command)commands.elementAt(0) == BACK_COMMAND) &&
        ((Command)commands.elementAt(1) == SELECT_COMMAND)) {
      return true;
    }
    
    return false;
  }

  /**
   * Updates the commands on the soft buttons.
   * Requires that the command vector passed in
   * is in priority order.
   */
  public void updateCommands(Vector commands) 
  {
    // Verify that the list is ordered
    // Really an assert condition leave till all working
    if (commands != null) {
      for (int i=0; i<commands.size()-1; i++) {
        Command cmda = (Command)commands.elementAt(i);
        Command cmdb = (Command)commands.elementAt((i+1));
        if (cmda.getPriority() > cmdb.getPriority()) {
          System.err.println("Assert: CommandManager.updateCommands commands out of order");
        }
      }
    }

    // Count how many soft buttons we have and remove the
    // old commands while we are there
    Vector devButtons = DeviceFactory.getDevice().getSoftButtons();
    Vector sbArray = new Vector(3);

    for (int i=0; i<devButtons.size(); i++) {
      SoftButton button = (SoftButton) devButtons.elementAt(i);
      if (button instanceof SoftButton) {
        sbArray.addElement(button);
        button.removeCommand();
        button.setMenuActivate(false);
      }
    }

    if (commands == null) {
      return;
    }
          
    int nSoftButtons = sbArray.size();
    if (isMenuCommands(commands, nSoftButtons) == true) {
      /* Special case if we have our BACK, SELECT commands */

      ((SoftButton)sbArray.elementAt(0)).setCommand(BACK_COMMAND);
      ((SoftButton)sbArray.elementAt(nSoftButtons-1)).setCommand(SELECT_COMMAND);
    } else if (nSoftButtons >= commands.size()) {
      /* No menus or special menus need to be created */

      for (int i=0; i<commands.size(); i++) {
        for (int j = 0; j < sbArray.size(); j++) {
          if (((SoftButton)sbArray.elementAt(j)).setCommand((Command)commands.elementAt(i))) {
            break;
          }
        }
      }
    } else {
      /* Menu needed */

      Vector cmdsToAdd = (Vector) commands.clone();
           
      // Fill out the most important ones we can
      for (int i=0; i < (nSoftButtons - 1); i++) {
        for (int j = 0; j < cmdsToAdd.size(); j++) {
          Command tmpC = (Command) cmdsToAdd.elementAt(j);
          if (((SoftButton) sbArray.elementAt(i)).setCommand(tmpC)) {
            cmdsToAdd.remove(tmpC);
            break;
          }
        }
      }

      // Now we need a menu for the rest of the items
      // Clear the commandList
      while (commandList.size() > 0) {
        commandList.delete(0);
      }

      menuCommands.removeAllElements();
      for (int i = 0; i < cmdsToAdd.size(); i++) {
        Command tmpC = (Command) cmdsToAdd.elementAt(i);
        menuCommands.addElement(tmpC);
        commandList.append(tmpC.getLabel(), null);
      }

      // Now set it up to be a menu button, and select button
      SoftButton menuSB = (SoftButton)sbArray.elementAt(nSoftButtons-1);
      menuSB.setMenuActivate(true);
      menuSB.removeCommand();
      menuSB.setCommand(MENU_COMMAND);
    }
  }


  static class CommandManagerListener implements CommandListener 
  {
    /**
     *  Description of the Method
     *
     *@param  cmd  Description of Parameter
     *@param  d    Description of Parameter
     */
    public void commandAction(Command cmd, Displayable d) 
    {
      MIDletBridge.getMIDletAccess().getDisplayAccess().setCurrent(previous);
      if ((cmd == SELECT_COMMAND) || cmd == List.SELECT_COMMAND) {
        MIDletBridge.getMIDletAccess().getDisplayAccess().commandAction(
            (Command) menuCommands.elementAt(commandList.getSelectedIndex()));
      }
    }
  }


  static {
    commandManagerListener = new CommandManagerListener();
    commandList.addCommand(BACK_COMMAND);
    commandList.addCommand(SELECT_COMMAND);
    commandList.setCommandListener(commandManagerListener);
  }

}
