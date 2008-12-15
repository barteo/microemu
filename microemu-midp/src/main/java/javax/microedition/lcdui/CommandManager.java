/*
 *  MicroEmulator
 *  Copyright (C) 2005 Bartek Teodorczyk <barteo@barteo.net>
 *
 *  It is licensed under the following two licenses as alternatives:
 *    1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 *    2. Apache License (the "AL") Version 2.0
 *
 *  You may not use this file except in compliance with at least one of
 *  the above two licenses.
 *
 *  You may obtain a copy of the LGPL at
 *      http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 *  You may obtain a copy of the AL at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the LGPL or the AL for the specific language governing permissions and
 *  limitations.
 */

package javax.microedition.lcdui;

import java.util.Enumeration;
import java.util.Vector;


import org.microemu.MIDletBridge;
import org.microemu.device.DeviceFactory;
import org.microemu.device.impl.SoftButton;



class CommandManager 
{
	public static final Command CMD_MENU = new Command("Menu", Command.EXIT, 0);

	private static final Command CMD_BACK = new Command("Back", Command.BACK, 0);
	private static final Command CMD_SELECT = new Command("Select", Command.OK, 0);
	
	private static CommandManager instance = new CommandManager();
	
	private MenuList menuList = null;
	private Vector menuCommands;
	private Displayable previous;
	
	private CommandListener menuCommandListener = new CommandListener()
	{
		public void commandAction(Command c, Displayable d) 
		{
			if (menuList == null) {
				lateInit();
			}

		    menuList.setCommandOnExit((Command) menuCommands.elementAt(menuList.getSelectedIndex()), c);

			MIDletBridge.getMIDletAccess().getDisplayAccess().setCurrent(previous);
		}		
	};
		
	
	private CommandManager()
	{	
	}
	
	
	private void lateInit() {
		menuList = new MenuList("Menu", List.IMPLICIT);
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

		previous = MIDletBridge.getMIDletAccess().getDisplayAccess().getCurrent();
		MIDletBridge.getMIDletAccess().getDisplayAccess().setCurrent(menuList);
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
	
	private class MenuList extends List
	{
		private Command selection;
		private Command c;
		
		public MenuList(String string, int implicit) {
			super(string, implicit);
		}

		public void setCommandOnExit(Command selection, Command c) {
			this.selection = selection;
			this.c = c;
		}
		
		void hideNotify() {
			if ((c == CMD_SELECT) || c == List.SELECT_COMMAND) {
				MIDletBridge.getMIDletAccess().getDisplayAccess().commandAction(selection, previous);
			}
		}

	}
	
}
