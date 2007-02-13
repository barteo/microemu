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