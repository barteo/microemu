/*
 *  @(#)SoftButton.java  07/07/2001
 *
 *  Copyright (c) 2001 Bartek Teodorczyk <barteo@it.pl>. All Rights Reserved.
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package com.barteo.emulator;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.lcdui.Command;
import com.barteo.emulator.device.Device;

/**
 *  Description of the Class
 *
 *@author     barteo
 *@created    3 wrzesieñ 2001
 */
public class SoftButton {

    public static int LEFT = 1;
    public static int RIGHT = 2;

    boolean menuActivate = false;
    Vector commandTypes = new Vector();

    Command command = null;

    Rectangle bounds;
    int alignment;


    /**
     *  Constructor for the SoftButton object
     *
     *@param  x          Description of Parameter
     *@param  y          Description of Parameter
     *@param  width      Description of Parameter
     *@param  height     Description of Parameter
     *@param  alignment  Description of Parameter
     */
    public SoftButton(int x, int y, int width, int height, int alignment) {
        bounds = new Rectangle(x, y, width, height);
        setAlignment(alignment);
    }


    /**
     *  Sets the command attribute of the SoftButton object
     *
     *@param  cmd  The new command value
     *@return      Description of the Returned Value
     */
    public boolean setCommand(Command cmd) {
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


    /**
     *  Sets the menuActivate attribute of the SoftButton object
     *
     *@param  state  The new menuActivate value
     */
    public void setMenuActivate(boolean state) {
        menuActivate = state;
    }


    /**
     *  Gets the command attribute of the SoftButton object
     *
     *@return    The command value
     */
    public Command getCommand() {
        return command;
    }


    /**
     *  Gets the menuActivate attribute of the SoftButton object
     *
     *@return    The menuActivate value
     */
    public boolean getMenuActivate() {
        return menuActivate;
    }


    /**
     *  Description of the Method
     *
     *@param  g  Description of Parameter
     */
    public void paint(Graphics g) {
        int xoffset = 0;

        g.setColor(Device.backgroundColor);
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        if (command != null) {
            if (alignment == RIGHT) {
                xoffset = bounds.width - g.getFontMetrics().stringWidth(command.getLabel());
            }
            g.setColor(Device.foregroundColor);
            g.drawString(command.getLabel(), bounds.x + xoffset, bounds.y + bounds.height);
        }
    }


    /**
     *  Description of the Method
     */
    public void removeCommand() {
        command = null;
    }


    /**
     *  A unit test for JUnit
     *
     *@param  cmd  Description of Parameter
     *@return      Description of the Returned Value
     */
    public boolean testCommandType(Command cmd) {
        for (Enumeration ct = commandTypes.elements(); ct.hasMoreElements(); ) {
            if (cmd.getCommandType() == ((Integer) ct.nextElement()).intValue()) {
                return true;
            }
        }
        return false;
    }


    /**
     *  Sets the alignment attribute of the SoftButton object
     *
     *@param  alignment  The new alignment value
     */
    void setAlignment(int alignment) {
        this.alignment = alignment;
    }


    /**
     *  Adds a feature to the CommandType attribute of the SoftButton object
     *
     *@param  commandType  The feature to be added to the CommandType attribute
     */
    public void addCommandType(int commandType) {
        commandTypes.addElement(new Integer(commandType));
    }

}
