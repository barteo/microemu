/*
 * @(#)Display.java  07/07/2001
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

package javax.microedition.lcdui;

import javax.microedition.midlet.*;

import com.barteo.midp.lcdui.*;


public class Display
{

	static final int width = 96;
	static final int height = 100;

	Displayable current = null;
	Displayable nextScreen = null;

	static DisplayBridge dispBridge = new DisplayBridge();

	DisplayAccessor accessor = null;


	class DisplayAccessor implements DisplayAccess
	{

		Display display;


		DisplayAccessor(Display d)
		{
			display = d;
		}


		public void commandAction(Command cmd)
		{
			if (current == null) {
				return;
			}
			CommandListener listener = current.getCommandListener();
			if (listener == null) {
				return;
			}
			listener.commandAction(cmd, current);
		}


		public Display getDisplay()
		{
			return display;
		}


		public void keyPressed(int keyCode)
		{
			if (current != null) {
				current.keyPressed(keyCode);
			}
		}


		public void keyReleased(int keyCode)
		{
			if (current != null) {
				current.keyReleased(keyCode);
			}
		}


		public void paint(Graphics g)
		{
			if (current != null) {
				current.paint(g);
				g.translate(-g.getTranslateX(), -g.getTranslateY());
			}
		}


    public Displayable getCurrent()
		{
      return getDisplay().getCurrent();
    }


    public void setCurrent(Displayable d)
		{
      getDisplay().setCurrent(d);
    }

	}


	class AlertTimeout implements Runnable
	{

		int time;


		AlertTimeout(int time)
		{
			this.time = time;
		}


		public void run()
		{
			try {
				Thread.sleep(time);
			} catch (InterruptedException ex) {}
			setCurrent(nextScreen);
		}
	}


	Display()
	{
		accessor = new DisplayAccessor(this);
	}


  public void callSerially(Runnable r)
  {
    // Not implemented
  }


	public int numColors()
	{
		return 2;
	}


	public static Display getDisplay(MIDlet m)
	{
		return getDisplay();
	}


	public Displayable getCurrent()
	{
		return current;
	}


	public boolean isColor()
	{
		return false;
	}


	public void setCurrent(Displayable nextDisplayable)
	{
		if (current != null) {
			current.hideNotify(this);
		}

		if (nextDisplayable instanceof Alert)
		{
			setCurrent((Alert) nextDisplayable, current);
			return;
		}

		current = nextDisplayable;

		current.showNotify(this);
		dispBridge.setScrollUp(false);
		dispBridge.setScrollDown(false);
		dispBridge.updateCommands(current.getCommands());

		current.repaint();
	}


	public void setCurrent(Alert alert, Displayable nextDisplayable)
	{
		nextScreen = nextDisplayable;

		current = alert;

		current.showNotify(this);
		dispBridge.updateCommands(current.getCommands());
		current.repaint();

		if (alert.getTimeout() != Alert.FOREVER) {
			AlertTimeout at = new AlertTimeout(alert.getTimeout());
			Thread t = new Thread(at);
			t.start();
		}
	}


	void clearAlert()
	{
		setCurrent(nextScreen);
	}


	static Display getDisplay()
	{
		DisplayAccess a = DisplayBridge.getAccess();
		if (a == null) {
			Display d = new Display();
			a = d.accessor;
			DisplayBridge.setAccess(a);
		}

		return a.getDisplay();
	}


	static int getGameAction(int keyCode)
	{
		return DisplayBridge.getGameAction(keyCode);
	}


	static int getKeyCode(int gameAction)
	{
		return DisplayBridge.getKeyCode(gameAction);
	}


	boolean isShown(Displayable d)
	{
		if (current == null || current != d) {
			return false;
		} else {
			return true;
		}
	}


	void repaint(Displayable d)
	{
		if (current == d) {
			dispBridge.repaint();
		}
	}


	void updateCommands()
	{
		dispBridge.updateCommands(current.getCommands());
	}

}
