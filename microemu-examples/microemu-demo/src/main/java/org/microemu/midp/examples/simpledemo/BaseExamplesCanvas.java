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

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;

public abstract class BaseExamplesCanvas extends Canvas implements CommandListener {

	protected boolean fullScreenMode = false;
	
	protected static final Command fullScreenModeCommand = new Command("Full Screen", Command.ITEM, 5);
	
	public BaseExamplesCanvas(String title) {
		super();
		super.setTitle(title);
		
		addCommand(BaseExamplesForm.backCommand);
		addCommand(fullScreenModeCommand);
		setCommandListener(this);
	}

	public int writeln(Graphics g, int line, String s) {
		int y = (g.getFont().getHeight() + 1) * line;
		g.drawString(s, 0, y, Graphics.LEFT | Graphics.TOP);
		return y;
	}
	
	public void commandAction(Command c, Displayable d) {
		if (d == this) {
			if (c == BaseExamplesForm.backCommand) {
				SimpleDemoMIDlet.showMenu();
			} else if (c == fullScreenModeCommand) {
				setFullScreenMode(!fullScreenMode);
				repaint();
			}
		}
	}
	
	public void setFullScreenMode(boolean mode) {
		fullScreenMode = mode;
		super.setFullScreenMode(mode);
	}
}
