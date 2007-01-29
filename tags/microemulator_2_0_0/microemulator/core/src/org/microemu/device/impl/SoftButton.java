/*
 *  MicroEmulator
 *  Copyright (C) 2002 Bartek Teodorczyk <barteo@barteo.net>
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
 */

package org.microemu.device.impl;

import javax.microedition.lcdui.Command;


/**
 * A SoftButton can have an associated Command.
 */
public interface SoftButton 
{
	int TYPE_COMMAND = 1;
	int TYPE_ICON = 2;
	
	String getName();
	
	int getType();

  Command getCommand();
  
  void setCommand(Command cmd);
  
  boolean isVisible();
  
  void setVisible(boolean state);
  
  boolean isPressed();
  
  void setPressed(boolean state);
  
  Rectangle getPaintable();
  
  /**
   * Check if the command is of a type usually associated with this SoftButton.
   * E.g. "BACK" commands are normally placed only on a particular button.
   */
  boolean preferredCommandType(Command cmd);

}
