/**
 *  MicroEmulator
 *  Copyright (C) 2008 Bartek Teodorczyk <barteo@barteo.net>
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
 *
 *  @version $Id$
 */

package org.microemu.android.device.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;

import org.microemu.device.ui.DisplayableUI;

public abstract class AndroidDisplayableUI implements DisplayableUI {
	
	private static Comparator<Command> commandsPriorityComparator = new Comparator<Command>() {

		public int compare(Command first, Command second) {
			if (first.getPriority() == second.getPriority()) {
				return 0;
			} else if (first.getPriority() < second.getPriority()) {
				return -1;
			} else {
				return 1;
			}
		}
		
	};
	
	private List<Command> commands = new ArrayList<Command>();
	
	private CommandListener commandListener = null;
	
	public List<Command> getCommands() {
		return commands;
	}
	
	public CommandListener getCommandListener() {
		return commandListener;
	}
	
	//
	// DisplayableUI
	//

	public void addCommand(Command cmd) {
		commands.add(cmd);
		// TODO decide whether this is the best way for keeping sorted commands
		Collections.sort(commands, commandsPriorityComparator);
	}

	public void removeCommand(Command cmd) {
		commands.remove(cmd);
	}

	public void setCommandListener(CommandListener l) {
		this.commandListener = l;
	}

}
