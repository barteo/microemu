/*
 * MicroEmulator
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.barteo.emulator;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.lcdui.Command;
import com.barteo.emulator.device.Device;

/**
 * SoftButton
 *      
 * @author <a href="mailto:barteo@it.pl">Bartek Teodorczyk</a>
 */
public class SoftButton extends Button
{

  public static int LEFT = 1;
  public static int RIGHT = 2;

  boolean menuActivate = false;
  Vector commandTypes = new Vector();

  Command command = null;

  Rectangle paintable;
  int alignment;


  public SoftButton(String name, Rectangle rectangle, String keyName, 
      Rectangle paintable, String alignmentName, Vector commands, boolean menuActivate)
  {
    super(name, rectangle, keyName);
      
    this.paintable = paintable;
    this.menuActivate = menuActivate;
    
    try {
      alignment = SoftButton.class.getField(alignmentName).getInt(null);
    } catch (Exception ex) {
      System.err.println(ex);
    }
    
    for (Enumeration e = commands.elements(); e.hasMoreElements(); ) {
      try {
        addCommandType(Command.class.getField((String) e.nextElement()).getInt(null));
      } catch (Exception ex) {
        System.err.println(ex);
      }
    }
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
        g.fillRect(paintable.x, paintable.y, paintable.width, paintable.height);
        if (command != null) {
            if (alignment == RIGHT) {
                xoffset = paintable.width - g.getFontMetrics().stringWidth(command.getLabel());
            }
            g.setColor(Device.foregroundColor);
            g.drawString(command.getLabel(), paintable.x + xoffset, paintable.y + paintable.height);
        }
    }


    /**
     *  Description of the Method
     */
    public void removeCommand() {
        command = null;
    }


    public boolean testCommandType(Command cmd) {
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
