/*
 * @(#)DisplayBridge.java  07/07/2001
 *
 * Copyright (c) 2001 Bartek Teodorczyk <barteo@it.pl>. All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package com.barteo.midp.lcdui;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;

import com.barteo.emulator.Device;
import com.barteo.emulator.SoftButton;


public class DisplayBridge
{

	static DisplayAccess da = null;
	static DisplayComponent dc = null;


	public static DisplayAccess getAccess()
	{
		return da;
	}


	public static void setAccess(DisplayAccess a_da)
	{
		da = a_da;
	}


	public static DisplayComponent getComponent()
	{
		return dc;
	}


	public static void setComponent(DisplayComponent a_dc)
	{
		dc = a_dc;
	}


	public static int getGameAction(int keyCode)
	{
		if (keyCode == KeyEvent.VK_LEFT) {
			return Canvas.LEFT;
		} else if (keyCode == KeyEvent.VK_RIGHT) {
			return Canvas.RIGHT;
		} else if (keyCode == KeyEvent.VK_UP) {
			return Canvas.UP;
		} else if (keyCode == KeyEvent.VK_DOWN) {
			return Canvas.DOWN;
		} else if (keyCode == KeyEvent.VK_ENTER) {
			return Canvas.FIRE;
		} else
			return 0;
	}


	public static int getKeyCode(int gameAction)
	{
    return gameAction;
	}


	public static void commandAction(Command cmd)
	{
		if (da != null) {
			da.commandAction(cmd);
		}
	}


	public static void keyPressed(int keyCode)
	{
		if (da != null) {
			da.keyPressed(keyCode);
		}
	}


	public static void paint(Graphics g)
	{
		if (da != null) {
			DisplayGraphics dg = new DisplayGraphics(g);
			da.paint(dg);
		}
	}


	public static Displayable getCurrent()
  {
		if (da != null) {
			return da.getCurrent();
		}
    return null;
  }


	public static void setCurrent(Displayable d)
  {
		if (da != null) {
			da.setCurrent(d);
		}
  }


	public void setScrollDown(boolean state)
	{
		if (dc != null) {
			dc.setScrollDown(state);
		}
	}


	public void setScrollUp(boolean state)
	{
		if (dc != null) {
			dc.setScrollUp(state);
		}
	}


  public static void updateCommands(Vector commands)
	{
    CommandManager.getInstance().updateCommands(commands);
		repaint();
	}


	public static void repaint()
	{
		if (dc != null) {
			dc.repaint();
		}
	}

}
