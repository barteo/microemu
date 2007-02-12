/*
 *  MicroEmulator
 *  Copyright (C) 2001-2007 MicroEmulator Team.
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

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

public class AlertPanel extends BaseExamplesList {

	Alert alerts[] = {
			new Alert("Alarm alert", "This is alarm alert", null, AlertType.ALARM),
			new Alert("Confirmation alert", "This is confirmation alert with 5 sec timeout", null, AlertType.CONFIRMATION),
			new Alert("Error alert", "This is error alert", null, AlertType.ERROR),
			new Alert("Info alert", "This is info alert with 5 sec timeout", null, AlertType.INFO),
			new Alert("Warning alert", "This is warning alert", null, AlertType.WARNING) };

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
				SimpleDemoMIDlet.setCurrentDisplayable(alerts[getSelectedIndex()]);
			}
		}
		super.commandAction(c, d);
	}

}
