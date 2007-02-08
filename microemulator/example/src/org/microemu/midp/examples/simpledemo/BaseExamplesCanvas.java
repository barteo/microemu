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

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

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
