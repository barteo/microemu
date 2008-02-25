/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
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
 */
 
package org.microemu;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;

import org.microemu.device.ui.DisplayableUI;


public interface DisplayAccess
{
	Command CMD_SCREEN_UP = new Command("", Command.SCREEN, 0);

	Command CMD_SCREEN_DOWN = new Command("", Command.SCREEN, 0);	

	void commandAction(Command c, Displayable d);

	Display getDisplay();

	void keyPressed(int keyCode);

	void keyRepeated(int keyCode);

	void keyReleased(int keyCode);

	void pointerPressed(int x, int y);

	void pointerReleased(int x, int y);

	void pointerDragged(int x, int y);

	void paint(Graphics g);
	
	boolean isFullScreenMode();
	
	void serviceRepaints();
  
	// TODO try to change all calls to getCurrent to use getCurrentUI, then remove getCurrent
	Displayable getCurrent();

	DisplayableUI getCurrentUI();

	void setCurrent(Displayable d);
	
	void sizeChanged(int width, int height);
  
	void updateCommands();

	void clean();

}
