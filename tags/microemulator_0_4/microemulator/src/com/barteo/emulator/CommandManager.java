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
   * Updates the commands on the soft buttons.
   *
   * Requires that the command vector passed in is in priority order.
   *
   * A menu is created if there are more commands than the number of soft
   * buttons. The function of one of the soft buttons will be to display the
   * menu. The commands with the highest priorities are mapped to the soft
   * buttons, while the rest will appear in the menu.
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

    // Find all the soft buttons we have
    Vector devButtons = DeviceFactory.getDevice().getSoftButtons();
    Vector softButtons = new Vector(3);

    for (int i=0; i < devButtons.size(); i++) {
      SoftButton button = (SoftButton) devButtons.elementAt(i);
      if (button instanceof SoftButton) {
        softButtons.addElement(button);
        button.setCommand(null);
      }
    }

    if (commands == null) {
      return;
    }

    Vector cmds = commands;

    // Insert a MENU button if a menu is needed so that all the commands can be
    // displayed.
    int nSoftButtons = softButtons.size();
    if (commands.size() > nSoftButtons) {
      cmds = (Vector)commands.clone();
      cmds.insertElementAt(MENU_COMMAND, 0);
    }

    Vector unmappedCmds = new Vector();

    // First try to map commands to buttons which have the command's type as
    // one of their preferred types.
    for (int i = 0; i < nSoftButtons && i < cmds.size(); ++i) {
      Command cmd = (Command)cmds.elementAt(i);

      boolean commandAssignedToAButton = false;
      for (int j = 0; j < softButtons.size(); ++j) {
        SoftButton softButton = (SoftButton)softButtons.elementAt(j);
        if (softButton.preferredCommandType(cmd)) {
          // Note: there may be several buttons which cmd could be mapped to. We
          // take the first one, even though it may be "better" to take another.
          softButton.setCommand(cmd);
          softButtons.removeElementAt(j);
          commandAssignedToAButton = true;
          break;
        }
      }
      if (!commandAssignedToAButton) {
        unmappedCmds.addElement(cmd);
      }
    }

    // Now map all the unassigned commands to buttons.
    // Note: at this point softButtons contains only buttons which have no cmd.
    for (int i = 0; i < unmappedCmds.size(); ++i) {
      ((SoftButton)softButtons.elementAt(i)).setCommand((Command)
                                                    unmappedCmds.elementAt(i));
    }


    if (cmds.size() > nSoftButtons) {
      // Create menu for items that can't be mapped to buttons.

      while (commandList.size() > 0) {
        commandList.delete(0);
      }
      menuCommands.removeAllElements();

      for (int i = nSoftButtons; i < cmds.size(); ++i) {
        Command cmd = (Command)cmds.elementAt(i);
        commandList.append(cmd.getLabel(), null);
        menuCommands.addElement(cmd);
      }
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
      // Get the selection. This must be done before calling setCurrent which
      // recreates the commandList (recreating the commandList makes the
      // "selection" the "default selection").
      Command selection = (Command) menuCommands.elementAt(commandList.getSelectedIndex());

      MIDletBridge.getMIDletAccess().getDisplayAccess().setCurrent(previous);

      if ((cmd == SELECT_COMMAND) || cmd == List.SELECT_COMMAND) {
        MIDletBridge.getMIDletAccess().getDisplayAccess().commandAction(selection);
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
