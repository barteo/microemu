/*
 * @(#)Device.java  07/07/2001
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

import java.awt.Color;
import java.util.*;

import javax.microedition.lcdui.Command;


public class Device
{

	static Vector softButtons = new Vector();
	
	static Color backgroundColor = new Color(185, 182, 145);
	static Color foregroundColor = new Color(0, 0, 0);
	

	static {
		// create soft buttons
		SoftButton soft;
		
		soft = new SoftButton(1, 112, 38, 16, SoftButton.LEFT);
		soft.addCommandType(Command.BACK);
		soft.addCommandType(Command.EXIT);
		soft.addCommandType(Command.CANCEL);
		soft.addCommandType(Command.STOP);
		softButtons.addElement(soft);
		
		soft = new SoftButton(58, 112, 38, 16, SoftButton.RIGHT);
		soft.setMenuActivate(true);
		soft.addCommandType(Command.OK);
		soft.addCommandType(Command.SCREEN);
		soft.addCommandType(Command.ITEM);
		soft.addCommandType(Command.HELP);
		softButtons.addElement(soft);
	}


	Device()
	{
	}


	public static Color getBackgroundColor()
	{
		return backgroundColor;
	}


	public static Color getForegroundColor()
	{
		return foregroundColor;
	}


	public static Vector getSoftButtons()
	{
		return softButtons;
	}

}
