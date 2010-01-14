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
package org.microemu.tests;

import javax.microedition.lcdui.Graphics;

public class ErrorHandlingCanvas extends BaseTestsCanvas {
	
	boolean makeErrorInPaint = false;
	
	int lastKeyCode = 0;
	
	public ErrorHandlingCanvas() {
		super("Canvas with Errors");
	}

	protected void paint(Graphics g) {
		int width = getWidth();
        int height = getHeight();

		g.setGrayScale(255);
		g.fillRect(0, 0, width, height);
		
		g.setColor(0);
		int line = 0;
		writeln(g, line++, "Make Error Canvas");
		if (fullScreenMode) {
			writeln(g, line++, "0 - Normal Mode");
		}
		writeln(g, line++, "1 - Error in keyPressed");
		writeln(g, line++, "2 - Error in pain");
		if (makeErrorInPaint) {
			makeErrorInPaint = false;
			writeln(g, line++, "Making error");
			throw new IllegalArgumentException("Emulator Should still work");
		}
		if (lastKeyCode != 0) {
			writeln(g, line++, "KeyCode: " + lastKeyCode);
		}
		
	}
	
	protected void keyPressed(int keyCode) {
		switch (keyCode) {
		case '0':
			if (fullScreenMode) {
				setFullScreenMode(false);
			}
			break;
		case '1':
			throw new IllegalArgumentException("Emulator Should still work");
		case '2':
			makeErrorInPaint = true;
			break;
		}
		lastKeyCode = keyCode;
		repaint();
	}
}