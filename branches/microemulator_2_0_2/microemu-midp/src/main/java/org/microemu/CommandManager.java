/*
 *  MicroEmulator
 *  Copyright (C) 2005 Bartek Teodorczyk <barteo@barteo.net>
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

package org.microemu;

import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import org.microemu.device.DeviceFactory;
import org.microemu.device.impl.SoftButton;



public class CommandManager 
{
	public static final Command CMD_SCREEN_UP = new Command("", Command.SCREEN, 0);
	public static final Command CMD_SCREEN_DOWN = new Command("", Command.SCREEN, 0);
	
	private static final Command CMD_MENU = new Command("Menu", Command.EXIT, 0);
	private static final Command CMD_BACK = new Command("Back", Command.BACK, 0);
	private static final Command CMD_SELECT = new Command("Select", Command.OK, 0);
	
	private static CommandManager instance = new CommandManager();
	
	private List menuList = null;
	private Vector menuCommands;
	private Displayable previous;
	
	private CommandListener menuCommandListener = new CommandListener()
	{
		public void commandAction(Command c, Displayable d) 
		{
			if (menuList == null) {
				lateInit();
			}

		    Command selection = (Command) menuCommands.elementAt(menuList.getSelectedIndex());

			MIDletBridge.getMIDletAccess().getDisplayAccess().setCurrent(previous);

			if ((c == CMD_SELECT) || c == List.SELECT_COMMAND) {
				MIDletBridge.getMIDletAccess().getDisplayAccess().commandAction(selection, previous);
			}
		}		
	};
		
	
	private CommandManager()
	{	
	}
	
	
	private void lateInit() {
		menuList = new List("Menu", List.IMPLICIT);
		menuList.addCommand(CMD_BACK);
		menuList.addCommand(CMD_SELECT);
		menuList.setCommandListener(menuCommandListener);
	}
	
	
	public static CommandManager getInstance()
	{
		return instance;
	}
	
	
	public void commandAction(Command command)
	{
		if (menuList == null) {
			lateInit();
		}

		if (command == CMD_MENU) {			
			previous = MIDletBridge.getMIDletAccess().getDisplayAccess().getCurrent();
			MIDletBridge.getMIDletAccess().getDisplayAccess().setCurrent(menuList);
		} else {
			MIDletBridge.getMIDletAccess().getDisplayAccess().commandAction(command, MIDletBridge.getMIDletAccess().getDisplayAccess().getCurrent());
		}
	}
	
	
	public void updateCommands(Vector commands)
	{
		if (menuList == null) {
			lateInit();
		}
		
		Vector buttons = DeviceFactory.getDevice().getSoftButtons();
		int numOfButtons = 0;
		Enumeration en = buttons.elements();
		while (en.hasMoreElements()) {
			SoftButton button = (SoftButton) en.nextElement();
			if (button.getType() == SoftButton.TYPE_COMMAND) {
				button.setCommand(null);
				numOfButtons++;
			}
		}
		
		if (commands == null) {			
			return;
		}
		
		Vector commandsTable = new Vector();
		for (int i = 0; i < commands.size(); i++) {
			commandsTable.addElement(null);
		}
		
		// Sort commands using priority
		en = commands.elements();
		while (en.hasMoreElements()) {
			Command commandToSort = (Command) en.nextElement();
			
			for (int i = 0; i < commandsTable.size(); i++) {
				if (commandsTable.elementAt(i) == null) {
					commandsTable.setElementAt(commandToSort, i);
					break;
				}
				if (commandToSort.getPriority() < ((Command) commandsTable.elementAt(i)).getPriority()) {
					for (int j = commandsTable.size() - 1; j > i; j--) {
						if (commandsTable.elementAt(j - 1) != null) {
							commandsTable.setElementAt(commandsTable.elementAt(j - 1), j);
						}
					}
				}
			}			
		}
		
		if (commandsTable.size() <= numOfButtons) {
			fillPossibleCommands(buttons, commandsTable);			
			return;
		}
		
		// Menu is needed
		commandsTable.insertElementAt(CMD_MENU, 0);
		fillPossibleCommands(buttons, commandsTable);
		while (menuList.size() > 0) {
	        menuList.delete(0);
	    }
		for (int i = 0; i < commandsTable.size(); i++) {
			menuCommands = commandsTable;
			menuList.append(((Command) commandsTable.elementAt(i)).getLabel(), null);
		}
	}
	
	
	private void fillPossibleCommands(Vector buttons, Vector commandsTable)
	{
		for (int i = 0; i < commandsTable.size(); i++) {
			Enumeration en = buttons.elements();
			while (en.hasMoreElements()) {
				SoftButton button = (SoftButton) en.nextElement();
				if (button.getType() == SoftButton.TYPE_COMMAND
						&& button.getCommand() == null 
						&& button.preferredCommandType((Command) commandsTable.elementAt(i))) {
					button.setCommand((Command) commandsTable.elementAt(i));
					commandsTable.removeElementAt(i);
					i--;
					break;
				}
			}
		}
		for (int i = 0; i < commandsTable.size(); i++) {
			Enumeration en = buttons.elements();
			while (en.hasMoreElements()) {
				SoftButton button = (SoftButton) en.nextElement();
				if (button.getType() == SoftButton.TYPE_COMMAND
						&& button.getCommand() == null) {
					button.setCommand((Command) commandsTable.elementAt(i));
					commandsTable.removeElementAt(i);
					i--;
					break;
				}
			}
		}
		
		// if there are unassigned softbuttons with paintable != null
		//   then fill these with commands from softbuttons with paintable == null
		Enumeration hiddenEn = buttons.elements();
		while (hiddenEn.hasMoreElements()) {
			SoftButton hiddenButton = (SoftButton) hiddenEn.nextElement();
			if (hiddenButton.getType() == SoftButton.TYPE_COMMAND
					&& hiddenButton.getPaintable() == null
					&& hiddenButton.getCommand() != null) {				
				Enumeration en = buttons.elements();
				while (en.hasMoreElements()) {
					SoftButton button = (SoftButton) en.nextElement();
					if (button.getType() == SoftButton.TYPE_COMMAND
							&& button.getPaintable() != null
							&& button.getCommand() == null) {
						button.setCommand(hiddenButton.getCommand());
						break;
					}
				}
			}
		}
	}
	
}
