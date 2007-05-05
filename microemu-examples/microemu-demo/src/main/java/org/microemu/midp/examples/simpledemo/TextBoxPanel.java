/**
 *  MicroEmulator
 *  Copyright (C) 2001-2007 Bartek Teodorczyk <barteo@barteo.net>
 *  Copyright (C) 2006-2007 Vlad Skarzhevskyy
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
package org.microemu.midp.examples.simpledemo;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;

public class TextBoxPanel extends BaseExamplesList {

	TextBox textBoxes[] = { new TextBox("Any character", null, 128, TextField.ANY),
			new TextBox("Email", null, 128, TextField.EMAILADDR),
			new TextBox("Numeric", null, 128, TextField.NUMERIC),
			new TextBox("Phone number", null, 128, TextField.PHONENUMBER),
			new TextBox("URL", null, 128, TextField.URL),
			new TextBox("Decimal", null, 128, TextField.DECIMAL),
			new TextBox("Password", null, 128, TextField.PASSWORD), };

	Command okCommand = new Command("Ok", Command.OK, 2);

	public TextBoxPanel() {
		super("TextBox", List.IMPLICIT);

		for (int i = 0; i < textBoxes.length; i++) {
			textBoxes[i].addCommand(BaseExamplesForm.backCommand);
			textBoxes[i].addCommand(okCommand);
			textBoxes[i].setCommandListener(this);
			append(textBoxes[i].getTitle(), null);
		}
	}

	public void commandAction(Command c, Displayable d) {
		if (d == this) {
			if (c == List.SELECT_COMMAND) {
				SimpleDemoMIDlet.setCurrentDisplayable(textBoxes[getSelectedIndex()]);
			}
		} else if ((c == BaseExamplesForm.backCommand) || (c == okCommand)) {
			for (int i = 0; i < textBoxes.length; i++) {
				if (d == textBoxes[i]) {
					SimpleDemoMIDlet.setCurrentDisplayable(this);
				}
			}
		}
		super.commandAction(c, d);
	}

}
