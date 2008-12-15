/**
 *  MicroEmulator
 *  Copyright (C) 2001-2007 Bartek Teodorczyk <barteo@barteo.net>
 *  Copyright (C) 2006-2007 Vlad Skarzhevskyy
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
