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

package org.microemu.android.device.ui;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

import org.microemu.android.MicroEmulatorActivity;
import org.microemu.device.ui.CommandUI;
import org.microemu.device.ui.DisplayableUI;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class AndroidDisplayableUI implements DisplayableUI {
	
	protected MicroEmulatorActivity activity;
	
	protected Displayable displayable;
	
	protected View view;
	
	protected TextView titleView;
	
	private static Comparator<CommandUI> commandsPriorityComparator = new Comparator<CommandUI>() {

		public int compare(CommandUI first, CommandUI second) {
			if (first.getCommand().getPriority() == second.getCommand().getPriority()) {
				return 0;
			} else if (first.getCommand().getPriority() < second.getCommand().getPriority()) {
				return -1;
			} else {
				return 1;
			}
		}
		
	};
	
	private Vector<AndroidCommandUI> commands = new Vector<AndroidCommandUI>();
	
	private CommandListener commandListener = null;
	
	protected AndroidDisplayableUI(MicroEmulatorActivity activity, Displayable displayable, boolean initView) {
		this.activity = activity;
		this.displayable = displayable;
		
		if (initView) {
			view = new LinearLayout(activity);
			((LinearLayout) view).setOrientation(LinearLayout.VERTICAL);
			view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
			
			titleView = new TextView(activity);
			titleView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			titleView.setTextAppearance(titleView.getContext(), android.R.style.TextAppearance_DialogWindowTitle);
			((LinearLayout) view).addView(titleView);
		}
	}
	
	public Vector<AndroidCommandUI> getCommandsUI() {
		return commands;
	}
	
	public CommandListener getCommandListener() {
		return commandListener;
	}
	
	//
	// DisplayableUI
	//

	public void addCommandUI(CommandUI cmd) {
		commands.add((AndroidCommandUI) cmd);
		// TODO decide whether this is the best way for keeping sorted commands
		Collections.sort(commands, commandsPriorityComparator);
	}

	public void removeCommandUI(CommandUI cmd) {
		commands.remove(cmd);
	}

	public void setCommandListener(CommandListener l) {
		this.commandListener = l;
	}

	public void invalidate() {
		activity.post(new Runnable() {
			public void run() {
				if (titleView != null) {
					titleView.setText(displayable.getTitle());
				}
			}
		});
	}

	public void showNotify() {
		activity.post(new Runnable() {
			public void run() {
				activity.setContentView(view);
				view.requestFocus();
			}
		});
	}

	public void hideNotify() {
	}

	public Vector<AndroidCommandUI> getCommands() {
		return commands;
	}
}
