package org.microemu.android.device.ui;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.android.lcdui.Command;
import javax.microedition.android.lcdui.CommandListener;

import org.microemu.device.ui.DisplayableUI;

public abstract class AndroidDisplayableUI implements DisplayableUI {
	
	private List commands = new ArrayList();
	
	private CommandListener l = null;
	
	public List getCommands() {
		return commands;
	}
	
	public CommandListener getCommandListener() {
		return l;
	}
	
	// DisplayableUI

	public void addCommand(Command cmd) {
		commands.add(cmd);
	}

	public void removeCommand(Command cmd) {
		commands.remove(cmd);
	}

	public void setCommandListener(CommandListener l) {
		this.l = l;
	}

}
