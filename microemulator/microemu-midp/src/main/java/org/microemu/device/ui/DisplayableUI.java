package org.microemu.device.ui;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;

public interface DisplayableUI {
	
	void addCommand(Command cmd);
	
	void removeCommand(Command cmd);
	
	void setCommandListener(CommandListener l);

	void hideNotify();

	void showNotify();

}
