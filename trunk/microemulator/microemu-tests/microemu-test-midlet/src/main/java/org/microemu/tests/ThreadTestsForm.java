/**
 *  MicroEmulator
 *  Copyright (C) 2006-2007 Bartek Teodorczyk <barteo@barteo.net>
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
