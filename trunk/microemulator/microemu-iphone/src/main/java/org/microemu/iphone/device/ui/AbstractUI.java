/**
 *  MicroEmulator
 *  Copyright (C) 2008 Markus Heberling <markus@heberling.net>
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
package org.microemu.iphone.device.ui;

import java.util.LinkedList;
import java.util.List;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

import joc.Message;
import joc.Scope;
import joc.Selector;
import obc.NSMutableArray;
import obc.NSObject;
import obc.UIBarButtonItem;
import obc.UIToolbar;

import org.microemu.device.ui.DisplayableUI;
import org.microemu.iphone.MicroEmulator;

public abstract class AbstractUI extends NSObject implements DisplayableUI {

	public static final int NAVIGATION_HEIGHT = 40;

	public static final int TOOLBAR_HEIGHT = 40;

	protected List<Command> commands = new LinkedList<Command>();

	protected CommandListener commandListener;

	protected UIToolbar toolbar;

	private Displayable displayable;

	protected MicroEmulator microEmulator;

	protected AbstractUI(MicroEmulator microEmulator, Displayable displayable) {
		super();
		this.microEmulator = microEmulator;
		this.displayable = displayable;
	}

	public void addCommand(Command cmd) {
		commands.add(cmd);
		microEmulator.postFromNewTread(new Runnable() {
			public void run() {
				updateToolbar();
			}
		});
	}

	protected void updateToolbar() {
		if (toolbar != null) {
			NSMutableArray items = new NSMutableArray().initWithCapacity$(commands.size());
			for (int i = 0; i < commands.size(); i++) {
				Command command = commands.get(i);
				System.out.println(command.getLabel());
				items.setObject$atIndex$(new UIBarButtonItem().initWithTitle$style$target$action$(command.getLabel(),
						0, new CommandCaller(command), new Selector("call")), i);
			}
			toolbar.setItems$(items);
		}
	}

	public void removeCommand(Command cmd) {
		commands.remove(cmd);
		microEmulator.postFromNewTread(new Runnable() {
			public void run() {
				updateToolbar();
			}
		});
	}

	public void setCommandListener(CommandListener l) {
		commandListener = l;
	}

	class CommandCaller extends NSObject {
		private Command command;

		private CommandCaller(Command command) {
			super();
			this.command = command;
		}

		@Message
		public void call() {
			commandListener.commandAction(command, displayable);
		}
	}
}
