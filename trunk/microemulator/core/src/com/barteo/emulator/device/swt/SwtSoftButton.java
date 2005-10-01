/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
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
 
package com.barteo.emulator.device.swt;

import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.lcdui.Command;

import org.eclipse.swt.graphics.RGB;

import com.barteo.emulator.app.ui.swt.SwtGraphics;
import com.barteo.emulator.device.DeviceFactory;
import com.barteo.emulator.device.impl.Rectangle;
import com.barteo.emulator.device.impl.SoftButton;


public class SwtSoftButton extends SwtButton implements SoftButton
{

  public static int LEFT = 1;
  public static int RIGHT = 2;

  Vector commandTypes = new Vector();

  Command command = null;

  Rectangle paintable;
  int alignment;


  public SwtSoftButton(String name, Rectangle rectangle, String keyName, Rectangle paintable, String alignmentName, Vector commands)
  {
    super(name, rectangle, keyName, null);
      
    this.paintable = paintable;
    
    try {
      alignment = SwtSoftButton.class.getField(alignmentName).getInt(null);
    } catch (Exception ex) {
      System.err.println(ex);
    }
    
    for (Enumeration e = commands.elements(); e.hasMoreElements(); ) {
      String tmp = (String) e.nextElement();
      try {
        addCommandType(Command.class.getField(tmp).getInt(null));
      } catch (Exception ex) {
        System.err.println("a3" + ex);
      }
    }
  }


    /**
     *  Sets the command attribute of the SoftButton object
     *
     *@param  cmd  The new command value
     */
    public void setCommand(Command cmd) {
        command = cmd;
    }


    /**
     *  Gets the command attribute of the SoftButton object
     *
     *@return    The command value
     */
    public Command getCommand() {
        return command;
    }


    public void paint(SwtGraphics g) 
    {
        int xoffset = 0;

        SwtDeviceDisplay deviceDisplay =
            (SwtDeviceDisplay) DeviceFactory.getDevice().getDeviceDisplay();
		g.setBackground(g.getColor(new RGB(
				deviceDisplay.getBackgroundColor().getRed(), 
				deviceDisplay.getBackgroundColor().getGreen(), 
				deviceDisplay.getBackgroundColor().getBlue())));
        g.fillRectangle(paintable.x, paintable.y, paintable.width, paintable.height);
        if (command != null) {
            if (alignment == RIGHT) {
                xoffset = paintable.width - g.stringWidth(command.getLabel());
            }
    		g.setForeground(g.getColor(new RGB(
    				deviceDisplay.getForegroundColor().getRed(), 
					deviceDisplay.getForegroundColor().getGreen(), 
					deviceDisplay.getForegroundColor().getBlue())));
            g.drawString(command.getLabel(), 
            		paintable.x + xoffset, paintable.y + (paintable.height - g.getFontMetrics().getHeight()), true);
        }
    }


    public boolean preferredCommandType(Command cmd) {
        for (Enumeration ct = commandTypes.elements(); ct.hasMoreElements(); ) {
            if (cmd.getCommandType() == ((Integer) ct.nextElement()).intValue()) {
                return true;
            }
        }
        return false;
    }


    public void addCommandType(int commandType) {
        commandTypes.addElement(new Integer(commandType));
    }

}
