/*
 * @(#)Displayable.java  07/07/2001
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

import java.util.Enumeration;
import java.util.Vector;


public abstract class Displayable
{

	Display currentDisplay = null;

	Vector commands = new Vector();
	CommandListener listener = null;


	public void addCommand(Command cmd)
	{
		for (Enumeration e = commands.elements() ; e.hasMoreElements() ;) {
			if (cmd == (Command) e.nextElement()) {
				return;
			}
		}

		commands.addElement(cmd);

		if (isShown()) {
			currentDisplay.updateCommands();
		}
	}


	public void removeCommand(Command cmd)
	{
		commands.removeElement(cmd);

		if (isShown()) {
			currentDisplay.updateCommands();
		}
	}


	public boolean isShown()
	{
		if (currentDisplay == null) {
			return false;
		}
		return currentDisplay.isShown(this);
	}


	public void setCommandListener(CommandListener l)
	{
		listener = l;
	}


	CommandListener getCommandListener()
	{
		return listener;
	}


	Vector getCommands()
	{
		return commands;
	}


	void hideNotify()
	{
	}


	void hideNotify(Display d)
	{
		currentDisplay = null;
		hideNotify();
	}


	void keyPressed(int keyCode)
	{
	}


	void keyReleased(int keyCode)
	{
	}


	abstract void paint(Graphics g);


	void repaint()
	{
		if (currentDisplay != null) {
			currentDisplay.repaint(this);
		}
	}


	void showNotify()
	{
	}


	void showNotify(Display d)
	{
		currentDisplay = d;
		showNotify();
	}

}
