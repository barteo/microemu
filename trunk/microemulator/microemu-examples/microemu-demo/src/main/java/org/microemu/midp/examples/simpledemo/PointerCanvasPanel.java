/**
 *  MicroEmulator
 *  Copyright (C) 2006-2007 Bartek Teodorczyk <barteo@barteo.net>
 *  Copyright (C) 2006-2007 Vlad Skarzhevskyy
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
