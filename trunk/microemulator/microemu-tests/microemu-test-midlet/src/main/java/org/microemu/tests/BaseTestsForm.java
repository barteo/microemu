package org.microemu.tests;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

public class BaseTestsForm extends Form implements CommandListener, DisplayableUnderTests {

	public BaseTestsForm(String title) {
		super(title);

		addCommand(DisplayableUnderTests.backCommand);
		setCommandListener(this);
	}

	public void commandAction(Command c, Displayable d) {
		if (d == this) {
			if (c == backCommand) {
				Manager.midletInstance.showMainPage();
			}
		}
	}
}
