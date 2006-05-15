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
	private static final Command CMD_MENU = new Command("Menu", Command.EXIT, 0);
	private static final Command CMD_BACK = new Command("Back", Command.BACK, 0);
	private static final Command CMD_SELECT = new Command("Select", Command.OK, 0);
	
	private static CommandManager instance = new CommandManager();
	
	private static List menuList = new List("Menu", List.IMPLICIT);
	private static Vector menuCommands;
	private static Displayable previous;
	
	private static CommandListener menuCommandListener = new CommandListener()
	{
		public void commandAction(Command c, Displayable d) 
		{
		    Command selection = (Command) menuCommands.elementAt(menuList.getSelectedIndex());

			MIDletBridge.getMIDletAccess().getDisplayAccess().setCurrent(previous);

			if ((c == CMD_SELECT) || c == List.SELECT_COMMAND) {
				MIDletBridge.getMIDletAccess().getDisplayAccess().commandAction(selection);
			}
		}		
	};
	
	static {
		menuList.addCommand(CMD_BACK);
		menuList.addCommand(CMD_SELECT);
		menuList.setCommandListener(menuCommandListener);
	}
	
	
	private CommandManager()
	{	
	}
	
	
	public static CommandManager getInstance()
	{
		return instance;
	}
	
	
	public void commandAction(Command command)
	{
		if (command == CMD_MENU) {
			previous = MIDletBridge.getMIDletAccess().getDisplayAccess().getCurrent();
			MIDletBridge.getMIDletAccess().getDisplayAccess().setCurrent(menuList);
		} else {
			MIDletBridge.getMIDletAccess().getDisplayAccess().commandAction(command);
		}
	}
	
	
	public void updateCommands(Vector commands)
	{
		Vector buttons = DeviceFactory.getDevice().getSoftButtons();
		Enumeration en = buttons.elements();
		while (en.hasMoreElements()) {
			((SoftButton) en.nextElement()).setCommand(null);
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
		
		if (commandsTable.size() <= buttons.size()) {
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
				if (button.getCommand() == null 
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
				if (button.getCommand() == null) {
					button.setCommand((Command) commandsTable.elementAt(i));
					commandsTable.removeElementAt(i);
					i--;
					break;
				}
			}
		}
	}
	
}
