/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
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
 */
 
package org.microemu;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;

import org.microemu.device.ui.DisplayableUI;


public interface DisplayAccess
{
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
	
	void hideNotify();
	   
    void showNotify();
	
	// TODO try to change all calls to getCurrent to use getCurrentUI, then remove getCurrent
	Displayable getCurrent();

	DisplayableUI getCurrentUI();

	void setCurrent(Displayable d);
	
	void sizeChanged();
  
	void repaint();

	void clean();

}
