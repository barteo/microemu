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

import java.util.Random;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

public class CanvasPanel extends BaseExamplesCanvas implements HasRunnable {

	private static final int POSNUMBER = 20;

	private static final Command neCommand = new Command("3 NE Move", Command.ITEM, 1);

	private static final Command nwCommand = new Command("1 NW Move", Command.ITEM, 2);

	private static final Command seCommand = new Command("9 SE Move", Command.ITEM, 3);

	private static final Command swCommand = new Command("7 SW Move", Command.ITEM, 4);

	private boolean cancel = false;

	private boolean moving = true;

	private int moveX = 2, moveY = 2;

	private int posX = 0, posY = 0;

	private int ballMoveX = 2, ballMoveY = -3;
	
	private int ballPosX = 15, ballPosY = 15;
	
	private int ballColor = 0x5691F0;

	private Random ballRandom = new Random();

	private Runnable timerTask = new Runnable() {

		public void run() {
			while (!cancel) {
				if (moving && isShown()) {
					synchronized (this) {
						if (moveX > 0) {
							if (posX >= POSNUMBER) {
								posX = 0;
							}
						} else {
							if (posX < 0) {
								posX = POSNUMBER;
							}
						}
						if (moveY > 0) {
							if (posY >= POSNUMBER) {
								posY = 0;
							}
						} else {
							if (posY < 0) {
								posY = POSNUMBER;
							}
						}
						posX += moveX;
						posY += moveY;
					}
					repaint();
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException ex) {
					break;
				}
			}
		}
	};

	public CanvasPanel() {
		super("Canvas");

		addCommand(neCommand);
		addCommand(nwCommand);
		addCommand(seCommand);
		addCommand(swCommand);
	}

	public void startRunnable() {
		cancel = false;
		Thread thread = new Thread(timerTask, "CanvasPanelThread");
		thread.start();
	}


	public void stopRunnable() {
		cancel = true;
	}

	public void commandAction(Command c, Displayable d) {
		if (d == this) {
			synchronized (this) {
				if (c == nwCommand) {
					moveX = -1;
					moveY = -1;
					moving = true;
				} else if (c == neCommand) {
					moveX = 1;
					moveY = -1;
					moving = true;
				} else if (c == swCommand) {
					moveX = -1;
					moveY = 1;
					moving = true;
				} else if (c == seCommand) {
					moveX = 1;
					moveY = 1;
					moving = true;
				} 
			}
		}
		super.commandAction(c, d);
	}

	protected void keyPressed(int keyCode) {
		if (keyCode == '1' /*nwCommand*/) {
			moveX = -1;
			moveY = -1;
			moving = true;
		} else if (keyCode == '2') {
			moveX = 0;
			moveY = -1;
			moving = true;
		} else if (keyCode == '3' /*neCommand*/) {
			moveX = 1;
			moveY = -1;
			moving = true;
		} else if (keyCode == '4') {
			moveX = -1;
			moveY = 0;
			moving = true;
		} else if (keyCode == '6') {
			moveX = 1;
			moveY = 0;
			moving = true;
		} else if (keyCode == '7' /*swCommand*/) {
			moveX = -1;
			moveY = 1;
			moving = true;
		} else if (keyCode == '8') {
			moveX = 0;
			moveY = 1;
			moving = true;
		} else if (keyCode == '9' /*seCommand*/) {
			moveX = 1;
			moveY = 1;
			moving = true;
		} else if (keyCode == '5' /*fullScreenModeCommand*/) {
			super.setFullScreenMode(!fullScreenMode);
			repaint();
		} else if (keyCode == KEY_POUND) {
			moving = !moving;
		} else if (keyCode == '0' /*backCommand*/) {
			SimpleDemoMIDlet.showMenu();
		} else if (fullScreenMode) {
			setFullScreenMode(false);
		}
	}

	public void paint(Graphics g) {
		int width = getWidth();
        int height = getHeight();

		g.setGrayScale(255);
		g.fillRect(0, 0, width, height);

		g.setColor(0x5691F0);
		g.drawRect(0, 0, width - 1, height - 1);

		g.setGrayScale(0);
		g.drawRect(2, 2, width - 5, height - 5);

		int pos = posX;
		while (pos < width - 5) {
			g.drawLine(3 + pos, 3, 3 + pos, height - 4);
			pos += POSNUMBER;
		}
		pos = posY;
		while (pos < height - 5) {
			g.drawLine(3, 3 + pos, width - 4, 3 + pos);
			pos += POSNUMBER;
		}

		// Paint canvas info in the middle
		String text = width + " x " + height;

		Font f = g.getFont();
		int w = f.stringWidth(text) + 4;
		int h = 2 * f.getHeight() + 4;

		int arcWidth = w;
		int arcHeight = h;
		g.setColor(0xFFCC11);
		g.drawRoundRect((width - w)/2, (height - h)/2, w, h, arcWidth, arcHeight);
		g.setColor(0xFFEE99);
		g.fillRoundRect((width - w)/2, (height - h)/2, w, h, arcWidth, arcHeight);

		g.setColor(0xBB5500);
		g.drawString(text, width/2, (height - f.getHeight())/2, Graphics.HCENTER | Graphics.TOP);

		// Pint Ball
		g.setColor(ballColor);
		g.fillRoundRect(ballPosX - 4, ballPosY - 4, 8, 8, 8, 8);

		ballPosX += ballMoveX;
		ballPosY += ballMoveY;

		boolean changeColor = false;
		if ((ballPosX < 4) || (ballPosX > width - 4)) {
			ballMoveX = -ballMoveX;
			changeColor = true;
		}
		if ((ballPosY < 4) || (ballPosY > height - 4)) {
			ballMoveY = -ballMoveY;
			changeColor = true;
		}
		if (changeColor) {
			ballColor = ballRandom.nextInt(0xFF) + (ballRandom.nextInt(0xFF) << 8) + (ballRandom.nextInt(0xFF) << 16);
		}
	}


}
