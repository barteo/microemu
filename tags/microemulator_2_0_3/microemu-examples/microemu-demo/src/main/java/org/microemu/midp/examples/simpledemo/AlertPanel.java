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

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

public class AlertPanel extends BaseExamplesList {

	static final Command okCommand = new Command("Agree", Command.OK, 1);
	
	Alert alertCmd;
	
	Alert alerts[] = {
			new Alert("Alarm alert", "This is alarm alert", null, AlertType.ALARM),
			new Alert("Confirmation alert", "This is confirmation alert with 5 sec timeout", null, AlertType.CONFIRMATION),
			new Alert("Error alert", "This is error alert", null, AlertType.ERROR),
			new Alert("Info alert", "This is info alert with 5 sec timeout", null, AlertType.INFO),
			new Alert("Warning alert", "This is warning alert", null, AlertType.WARNING),
			alertCmd = new Alert("Command alert", "This is alert with command", null, AlertType.INFO)};

	public AlertPanel() {
		super("Alert", List.IMPLICIT);

		for (int i = 0; i < alerts.length; i++) {
			if (i == 1 || i == 3) {
				alerts[i].setTimeout(5000);
			}
			append(alerts[i].getTitle(), null);
		}
	}

	public void commandAction(Command c, Displayable d) {
		if (d == this) {
			if (c == List.SELECT_COMMAND) {
				if (alertCmd == alerts[getSelectedIndex()]) {
					alertCmd.addCommand(okCommand);
				}
				SimpleDemoMIDlet.setCurrentDisplayable(alerts[getSelectedIndex()]);
			}
		}
		super.commandAction(c, d);
	}

}
