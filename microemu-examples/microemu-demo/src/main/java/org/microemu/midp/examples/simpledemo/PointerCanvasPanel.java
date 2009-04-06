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
package org.microemu.midp.examples.simpledemo;

import javax.microedition.lcdui.Graphics;

public class PointerCanvasPanel extends BaseExamplesCanvas {

	private static final int CROSS_SIZE = 3;
	
	int ppX;
	
	int ppY;

	int poX;
	
	int poY;
	
	String lastPointerEvent = null;
	
	boolean pointerPressed = false;
	
	public PointerCanvasPanel() {
		super("PointerCanvas");
	}
	
	protected void paint(Graphics g) {
		int width = getWidth();
        int height = getHeight();

		g.setGrayScale(255);
		g.fillRect(0, 0, width, height);
		
		g.setColor(0);
		int line = 0;
		writeln(g, line++, "Pointer Canvas");
		if (fullScreenMode) {
			writeln(g, line++, "Back - Press any key");
		}
		
		if (!hasPointerEvents()) {
			writeln(g, line++, "Do not have PointerEvents");
		} else {
			if (!hasPointerMotionEvents()) {
				writeln(g, line++, "Do not have PointerMotionEvents");	
			}
			
			if (lastPointerEvent != null) {
				writeln(g, line++, ppX +"x"+ ppY + " " + lastPointerEvent);
				
				if (pointerPressed) {
					g.setColor(0xFFEE99);
					g.setStrokeStyle(Graphics.DOTTED);
				} else {
					g.setColor(0x5691F0);
				}
				g.drawRect(poX, poY, ppX - poX, ppY - poY);
				g.setStrokeStyle(Graphics.SOLID);
				
				g.setColor(0xBB5500);
				g.drawLine(ppX - CROSS_SIZE, ppY + CROSS_SIZE, ppX + CROSS_SIZE, ppY - CROSS_SIZE);
				g.drawLine(ppX - CROSS_SIZE, ppY - CROSS_SIZE, ppX + CROSS_SIZE, ppY + CROSS_SIZE);
			} else {
				writeln(g, line++, "Click anywhere and drag");	
			}
		}
	}

	public void pointerPressed(int x, int y) {
		lastPointerEvent = "Pressed " + Utils.when();
		this.ppX = x;
		this.ppY = y;
		this.poX = x;
		this.poY = y;
		pointerPressed = true;
		repaint();
	}

	public void pointerReleased(int x, int y) {
		lastPointerEvent = "Released " + Utils.when();
		this.ppX = x;
		this.ppY = y;
		pointerPressed = false;
		repaint();
	}

	public void pointerDragged(int x, int y) {
		lastPointerEvent = "Dragged " + Utils.when();
		this.ppX = x;
		this.ppY = y;
		repaint();
	}
	
	protected void keyPressed(int keyCode) {
		if (fullScreenMode) {
			setFullScreenMode(false);
			repaint();
		} else {
			SimpleDemoMIDlet.showMenu();
		}
	}
}
