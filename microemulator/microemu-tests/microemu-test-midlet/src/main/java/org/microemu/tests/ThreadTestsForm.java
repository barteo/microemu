/**
 *  MicroEmulator
 *  Copyright (C) 2006-2007 Bartek Teodorczyk <barteo@barteo.net>
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
package org.microemu.tests;

import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;

/**
 * @author vlads
 * 
 */
public class ThreadTestsForm extends BaseTestsForm {

	static final Command startThreadCommand = new Command("start Thread", Command.ITEM, 1);

	static final Command stopThreadCommand = new Command("stop Thread", Command.ITEM, 2);

	static final Command startTimerCommand = new Command("start Timer", Command.ITEM, 3);

	static final Command stopTimerCommand = new Command("stop Timer", Command.ITEM, 4);

	private static boolean testTimeronInit = true;

	private Thread thread;

	public ThreadTestsForm() {
		super("ThreadTests");
		addCommand(startThreadCommand);
		addCommand(stopThreadCommand);
		// addCommand(startTimerCommand);
		// addCommand(stopTimerCommand);
	}

	public static void onMIDletInit() {
		if (testTimeronInit) {
			Timer runAwayTimer = new Timer();
			runAwayTimer.scheduleAtFixedRate(new TimerTask() {
				public void run() {
					System.out.println("runAwayTimer");

				}
			}, 100, 2000);
		}
	}

	public void commandAction(Command c, Displayable d) {
		if (c == startThreadCommand) {
			thread = new Thread() {
				public void run() {
					while (true) {
						try {
							sleep(1000);
							System.out.println("runAwayThread");
						} catch (InterruptedException e) {
							return;
						}
					}
				}
			};
			thread.start();
		} else if (c == stopThreadCommand) {
			if (thread != null) {
				thread.interrupt();
				thread = null;
			}
		} else {
			super.commandAction(c, d);
		}
	}
}
