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

package org.microemu.device.j2se;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Shape;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;

import org.microemu.device.Device;
import org.microemu.device.DeviceFactory;
import org.microemu.device.impl.Rectangle;
import org.microemu.device.impl.SoftButton;

public class J2SESoftButton extends J2SEButton implements SoftButton {

	public static int LEFT = 1;
	public static int RIGHT = 2;
	
	private int type;
	
	private Image normalImage;
	private Image pressedImage;

	private Vector commandTypes = new Vector();

	private Command command = null;

	private Rectangle paintable;

	private int alignment;
	
	private boolean visible;

	private boolean pressed;
	
	private Font font;

	public J2SESoftButton(String name, Rectangle rectangle, String keyName,
			Rectangle paintable, String alignmentName, Vector commands, Font font) {
		super(name, rectangle, keyName, null);
		
		this.type = TYPE_COMMAND;

		this.paintable = paintable;
		this.visible = true;
		this.pressed = false;
		this.font = font;

		if (alignmentName != null) {
			try {
				alignment = J2SESoftButton.class.getField(alignmentName).getInt(null);
			} catch (Exception ex) {
				System.err.println(ex);
			}
		}

		for (Enumeration e = commands.elements(); e.hasMoreElements();) {
			String tmp = (String) e.nextElement();
			try {
				addCommandType(Command.class.getField(tmp).getInt(null));
			} catch (Exception ex) {
				System.err.println("a3" + ex);
			}
		}
	}
	
	public J2SESoftButton(String name, Rectangle paintable, Image normalImage, Image pressedImage) {
		super(name, null, null, null);
		
		this.type = TYPE_ICON;
		
		this.paintable = paintable;
		this.normalImage = normalImage;
		this.pressedImage = pressedImage;
		
		this.visible = true;
		this.pressed = false;
	}
	
	public int getType() {
		return type;
	}

	/**
	 * Sets the command attribute of the SoftButton object
	 * 
	 * @param cmd
	 *            The new command value
	 */
	public void setCommand(Command cmd) {
		synchronized (this) {
			command = cmd;
		}
	}

	/**
	 * Gets the command attribute of the SoftButton object
	 * 
	 * @return The command value
	 */
	public Command getCommand() {
		return command;
	}

	public Rectangle getPaintable() {
		return paintable;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean state) {
		visible = state;
	}

	public boolean isPressed() {
		return pressed;
	}

	public void setPressed(boolean state) {
		pressed = state;
	}

	public void paint(Graphics g) {
		if (!visible || paintable == null) {
			return;
		}
		
		Shape clip = g.getClip();
		
		g.setClip(paintable.x, paintable.y, paintable.width, paintable.height);
		if (type == TYPE_COMMAND) {
			int xoffset = 0;		
			Device device = DeviceFactory.getDevice();
			J2SEDeviceDisplay deviceDisplay = (J2SEDeviceDisplay) device.getDeviceDisplay();		
			if (pressed) {
				g.setColor(deviceDisplay.foregroundColor);
			} else {
				g.setColor(deviceDisplay.backgroundColor);
			}
			g.fillRect(paintable.x, paintable.y, paintable.width, paintable.height);
			synchronized (this) {
				if (command != null) {
					if (font != null) {
						J2SEFontManager fontManager = (J2SEFontManager) device.getFontManager();
						J2SEFont buttonFont = (J2SEFont) fontManager.getFont(font);
						g.setFont(buttonFont.getFont());
					}
					FontMetrics metrics = g.getFontMetrics();
					if (alignment == RIGHT) {
						xoffset = paintable.width
								- metrics.stringWidth(command.getLabel()) - 1;
					}
					if (pressed) {
						g.setColor(deviceDisplay.backgroundColor);
					} else {
						g.setColor(deviceDisplay.foregroundColor);
					}
					g.drawString(command.getLabel(), paintable.x + xoffset,
							paintable.y + paintable.height - metrics.getDescent());
				}
			}
		} else if (type == TYPE_ICON) {
			if (pressed) {
				g.drawImage(((J2SEImmutableImage) pressedImage).getImage(), paintable.x, paintable.y, null);
			} else {
				g.drawImage(((J2SEImmutableImage) normalImage).getImage(), paintable.x, paintable.y, null);
			}
		}
		
		g.setClip(clip);
	}

	public boolean preferredCommandType(Command cmd) {
		for (Enumeration ct = commandTypes.elements(); ct.hasMoreElements();) {
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
