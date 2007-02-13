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
package org.microemu.tests;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;

public abstract class BaseTestsCanvas extends Canvas implements CommandListener, DisplayableUnderTests {

	protected boolean fullScreenMode = false;
	
	protected static final Command fullScreenModeCommand = new Command("Full Screen", Command.ITEM, 5);
	
	public BaseTestsCanvas(String title) {
		super();
		super.setTitle(title);
		
		addCommand(DisplayableUnderTests.backCommand);
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
			if (c == DisplayableUnderTests.backCommand) {
				Manager.midletInstance.showMainPage();
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
