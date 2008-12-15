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
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.Screen;
import javax.microedition.lcdui.Ticker;
import javax.microedition.midlet.MIDlet;

public class SimpleDemoMIDlet extends MIDlet implements CommandListener {

	static SimpleDemoMIDlet instance;

	static final Command exitCommand = new Command("Exit", Command.EXIT, 1);

	List menuList = null;

	Displayable screenPanels[];

	public SimpleDemoMIDlet() {
		instance = this;
	}

	public void destroyApp(boolean unconditional) {
		if (screenPanels != null) {
			for (int i = 0; i < screenPanels.length; i++) {
				if (screenPanels[i] instanceof HasRunnable) {
					((HasRunnable) screenPanels[i]).stopRunnable();
				}
			}
		}
	}

	public void pauseApp() {
	}

	public void startApp() {
		if (menuList == null) {
			screenPanels = new Displayable[] {
					new AlertPanel(),
					new CanvasPanel(),
					new GameCanvasPanel(),
					new KeyCanvasPanel(),
					new PointerCanvasPanel(),
					new DateFieldPanel(),
					new GaugePanel(),
					new ImageItemPanel(),
					new ListPanel(),
					new TextFieldPanel(),
					new TextBoxPanel(),
					new HTTPPanel()};

			Ticker ticker = new Ticker("This is SimpleDemo ticker");

			menuList = new List("SimpleDemo", List.IMPLICIT);

			for (int i = 0; i < screenPanels.length; i++) {
				menuList.append(screenPanels[i].getTitle(), null);
				if ((screenPanels[i] instanceof Screen) && (i < 4)) {
					((Screen)screenPanels[i]).setTicker(ticker);
				}
			}
			menuList.addCommand(exitCommand);
			menuList.setCommandListener(this);
		}

		showMenu();
	}

	public static SimpleDemoMIDlet getInstance() {
		return instance;
	}

	public static void showMenu() {
		setCurrentDisplayable(instance.menuList);
	}
	
	public static void setCurrentDisplayable(Displayable nextDisplayable) {
		Display display = Display.getDisplay(instance);
		Displayable current = display.getCurrent();
		if (current instanceof HasRunnable) {
			((HasRunnable) current).stopRunnable();
		}
		if (nextDisplayable instanceof HasRunnable) {
			((HasRunnable) nextDisplayable).startRunnable();
		}
		display.setCurrent(nextDisplayable);
	}
	
	public void commandAction(Command c, Displayable d) {
		if (d == menuList) {
			if (c == List.SELECT_COMMAND) {
				setCurrentDisplayable(screenPanels[menuList.getSelectedIndex()]);
			} else if (c == exitCommand) {
				destroyApp(true);
				notifyDestroyed();
			}
		}
	}

}
