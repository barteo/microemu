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

package org.microemu.device.impl.ui;

import java.util.Vector;

import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

import org.microemu.DisplayAccess;
import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;
import org.microemu.device.ui.CommandUI;
import org.microemu.device.ui.DisplayableUI;

public class DisplayableImplUI implements DisplayableUI {
	
	protected Displayable displayable;
	
	private Vector commands = new Vector();
	
	protected DisplayableImplUI(Displayable displayable) {
		this.displayable = displayable;
	}

	public void addCommandUI(CommandUI cmd) {
		// Check that its not the same command
		for (int i = 0; i < commands.size(); i++) {
			if (cmd == (CommandUI) commands.elementAt(i)) {
				// Its the same just return
				return;
			}
		}

		// Now insert it in order
		boolean inserted = false;
		for (int i = 0; i < commands.size(); i++) {
			if (cmd.getCommand().getPriority() < ((CommandUI) commands.elementAt(i)).getCommand().getPriority()) {
				commands.insertElementAt(cmd, i);
				inserted = true;
				break;
			}
		}
		if (inserted == false) {
			// Not inserted just place it at the end
			commands.addElement(cmd);
		}		

		if (displayable.isShown()) {
			updateCommands();
		}
	}

	public void removeCommandUI(CommandUI cmd) {
		commands.removeElement(cmd);
		
		if (displayable.isShown()) {
			updateCommands();
		}
	}

	public void setCommandListener(CommandListener l) {
		// TODO Auto-generated method stub

	}
	
	public CommandListener getCommandListener() {
		// TODO Auto-generated method stub

		return null;
	}

	public void hideNotify() {
		// TODO Auto-generated method stub

	}

	public void showNotify() {
		updateCommands();
	}

	public void invalidate() {
		// TODO implement invalidate
	}
	
	public Vector getCommandsUI()
	{
		return commands;
	}

	private void updateCommands() {
		CommandManager.getInstance().updateCommands(getCommandsUI());
		MIDletAccess ma = MIDletBridge.getMIDletAccess();
		if (ma == null) {
			return;
		}
		DisplayAccess da = ma.getDisplayAccess();
		if (da == null) {
			return;
		}
		da.repaint();
	}

}
