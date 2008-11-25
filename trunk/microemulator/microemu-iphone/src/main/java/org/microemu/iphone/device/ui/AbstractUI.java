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
		updateToolbar();
	}

	protected void updateToolbar() {
		if (toolbar != null) {
			new Thread(new Runnable() {
				public void run() {
					Scope scope=new Scope();
					microEmulator.post(new Runnable() {
						public void run() {
							Scope scope=new Scope();
							NSMutableArray items = new NSMutableArray().initWithCapacity$(commands.size());
							for (int i = 0; i < commands.size(); i++) {
								Command command = commands.get(i);
								System.out.println(command.getLabel());
								items.setObject$atIndex$(new UIBarButtonItem().initWithTitle$style$target$action$(
										command.getLabel(), 0, new CommandCaller(command), new Selector("call")), i);
							}
							toolbar.setItems$(items);
							scope.close();
						}
					});
					scope.close();
				}
			}).start();
		}
	}

	public void removeCommand(Command cmd) {
		commands.remove(cmd);
		updateToolbar();
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
