/**
 *  MicroEmulator
 *  Copyright (C) 2009 Bartek Teodorczyk <barteo@barteo.net>
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

package org.microemu.android.device.ui;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;

import org.microemu.android.MicroEmulatorActivity;
import org.microemu.device.ui.AlertUI;
import org.microemu.device.ui.CommandUI;

import android.app.AlertDialog;
import android.content.DialogInterface;

import java.util.Map;
import java.util.HashMap;

public class AndroidAlertUI extends AndroidDisplayableUI implements AlertUI {

	private AlertDialog alertDialog;

	private DialogInterface.OnClickListener onClickListener;

	private Map<Integer, CommandUI> buttons = new HashMap<Integer, CommandUI>();

	public AndroidAlertUI(final MicroEmulatorActivity activity, final Alert alert) {
		super(activity, alert, false);

		activity.post(new Runnable() {
			public void run() {
				alertDialog = new AlertDialog.Builder(activity).create();
				alertDialog.setTitle(alert.getTitle());
				onClickListener = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						activity.setDialog(null);
						if (getCommandListener() != null) {
							getCommandListener().commandAction(buttons.get(which).getCommand(), displayable);
						}
					}
				};
			}
		});
	}

	@Override
	public void showNotify() {
		activity.post(new Runnable() {
			public void run() {
				fixLookAndFeel();
				activity.setDialog(alertDialog);
			}
		});
	}

	@Override
	public void hideNotify() {
		activity.post(new Runnable() {
			public void run() {
				activity.setDialog(null);
			}
		});
	}

	private void fixLookAndFeel() {
		for (CommandUI cmd : getCommandsUI()) {
			buttonize(cmd);
		}
	}

	private void buttonize(CommandUI cmd) {
		int which = 0;
		Command command = cmd.getCommand();
		if (command == Alert.DISMISS_COMMAND) {
			which = DialogInterface.BUTTON_NEUTRAL;
		} else {
			switch (command.getCommandType()) {
				case Command.OK:
					which = DialogInterface.BUTTON_POSITIVE;
					break;
				case Command.CANCEL:
					which = DialogInterface.BUTTON_NEGATIVE;
					break;
			}
		}
		if (which == 0) {
			alertDialog.setButton(command.getLabel(), onClickListener);
		} else {
			alertDialog.setButton(which, command.getLabel(), onClickListener);
		}
		buttons.put(which, cmd);
	}

	//
	// AlertUI
	//		

	public void setString(final String str) {
		activity.post(new Runnable() {
			public void run() {
				alertDialog.setMessage(str);
			}
		});
	}

}
