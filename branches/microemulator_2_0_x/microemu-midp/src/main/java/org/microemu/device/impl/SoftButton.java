/*
 *  MicroEmulator
 *  Copyright (C) 2002 Bartek Teodorczyk <barteo@barteo.net>
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
