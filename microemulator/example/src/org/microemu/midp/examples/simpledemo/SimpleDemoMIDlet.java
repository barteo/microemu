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
					new KeyCanvasPanel(),
					new DateFieldPanel(),
					new GaugePanel(),
					new ImageItemPanel(),
					new ListPanel(),
					new TextFieldPanel(),
					new TextBoxPanel() };

			Ticker ticker = new Ticker("This is SimpleDemo ticker");

			menuList = new List("SimpleDemo", List.IMPLICIT);

			for (int i = 0; i < screenPanels.length; i++) {
				menuList.append(screenPanels[i].getTitle(), null);
				if (screenPanels[i] instanceof Screen) {
					((Screen) screenPanels[i]).setTicker(ticker);
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
