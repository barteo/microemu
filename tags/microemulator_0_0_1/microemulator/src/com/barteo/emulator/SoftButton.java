/*
 * @(#)SoftButton.java  07/07/2001
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

package com.barteo.emulator;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.lcdui.Command;


public class SoftButton
{

	static int LEFT = 1;
	static int RIGHT = 2;

	boolean menuActivate = false;
	Vector commandTypes = new Vector();
	
	Command command = null;
	
	Rectangle bounds;
	int alignment;


	SoftButton(int x, int y, int width, int height, int alignment)
	{
		bounds = new Rectangle(x, y, width, height);
		setAlignment(alignment);
	}
	

	public Command getCommand()
	{
		return command;
	}


	public void paint(Graphics g)
	{
		int xoffset = 0;
		
		g.setColor(Device.getBackgroundColor());
		g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
		if (command != null)
		{
			if (alignment == RIGHT) {
				xoffset = bounds.width - g.getFontMetrics().stringWidth(command.getLabel());
			}
			g.setColor(Device.getForegroundColor());
			g.drawString(command.getLabel(), bounds.x + xoffset, bounds.y + bounds.height);
		}
	}


	public void removeCommand()
	{
		command = null;
	}


	public boolean setCommand(Command cmd)
	{
		boolean properType = false;
		
		if (!testCommandType(cmd)) {
			return false;
		}
	
		if (command == null) {
			command = cmd;
      return true;
		} else {
			if (cmd.getPriority() < command.getPriority()) {
				command = cmd;
        return true;
      } else {
        command = null;
        return false;
      }
		}
	}


	void addCommandType(int commandType)
	{
		commandTypes.addElement(new Integer(commandType));
	}
	

	public boolean getMenuActivate()
	{
		return menuActivate;
	}

  
	void setAlignment(int alignment)
	{
		this.alignment = alignment;
	}


  public void setMenuActivate(boolean state)
	{
		menuActivate = state;
	}
  
  
  public boolean testCommandType(Command cmd)
  {
		for (Enumeration ct = commandTypes.elements();  ct.hasMoreElements() ;) {
			if (cmd.getCommandType() == ((Integer) ct.nextElement()).intValue()) {
				return true;
			}
		}
    return false;
  }

}
