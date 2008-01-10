package org.microemu.android.device.ui;

import javax.microedition.android.lcdui.Command;
import javax.microedition.android.lcdui.CommandListener;

import org.microemu.device.ui.DisplayableUI;

public abstract class AndroidDisplayableUI implements DisplayableUI {

	public void addCommand(Command cmd) {
		// TODO Auto-generated method stub
System.out.println("AndroidDisplayableUI::addCommand " + cmd.getLabel());
	}

	public void removeCommand(Command cmd) {
		// TODO Auto-generated method stub
System.out.println("AndroidDisplayableUI::removeCommand " + cmd.getLabel());
	}

	public void setCommandListener(CommandListener l) {
		// TODO Auto-generated method stub
System.out.println("AndroidDisplayableUI::setCommandListener");
	}

}
