/**
 *  MicroEmulator
 *  Copyright (C) 2008 Bartek Teodorczyk <barteo@barteo.net>
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
 *
 *  @version $Id$
 */

package org.microemu.android.device.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.microedition.android.lcdui.Command;
import javax.microedition.android.lcdui.CommandListener;

import org.microemu.device.ui.DisplayableUI;

public abstract class AndroidDisplayableUI implements DisplayableUI {
	
	private static Comparator commandsPriorityComparator = new Comparator() {

		public int compare(Object object1, Object object2) {
			if (((Command) object1).getPriority() == ((Command) object2).getPriority()) {
				return 0;
			} else if (((Command) object1).getPriority() < ((Command) object2).getPriority()) {
				return -1;
			} else {
				return 1;
			}
		}
		
	};
	
	private String title;
	
	private List commands = new ArrayList();
	
	private CommandListener l = null;
	
	public List getCommands() {
		return commands;
	}
	
	public CommandListener getCommandListener() {
		return l;
	}
	
	//
	// DisplayableUI
	//

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void addCommand(Command cmd) {
System.out.println("AndroidDisplayableUI::addCommand(..) " + cmd.getLabel());		
		commands.add(cmd);
		// TODO decide whether this is the best way for keeping sorted commands
		Collections.sort(commands, commandsPriorityComparator);
	}

	public void removeCommand(Command cmd) {
		commands.remove(cmd);
	}

	public void setCommandListener(CommandListener l) {
		this.l = l;
	}

}
