/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
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

package org.microemu.device.swt;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;

import org.eclipse.swt.graphics.RGB;
import org.microemu.app.ui.swt.SwtGraphics;
import org.microemu.device.Device;
import org.microemu.device.DeviceFactory;
import org.microemu.device.impl.Rectangle;
import org.microemu.device.impl.Shape;
import org.microemu.device.impl.SoftButton;

public class SwtSoftButton extends SwtButton implements SoftButton {

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

	/**
	 * @param name
	 * @param rectangle
	 * @param keyCode - Integer.MIN_VALUE when unspecified
	 * @param keyName
	 * @param paintable
	 * @param alignmentName
	 * @param commands
	 * @param font
	 */
	public SwtSoftButton(String name, Shape shape, int keyCode, String keyName,
			Rectangle paintable, String alignmentName, Vector commands, Font font) {
		super(name, shape, keyCode, keyName, new Hashtable());

		this.type = TYPE_COMMAND;

		this.paintable = paintable;
		this.visible = true;
		this.pressed = false;
		this.font = font;

		if (alignmentName != null) {
			try {
				alignment = SwtSoftButton.class.getField(alignmentName).getInt(null);
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

	public SwtSoftButton(String name, Rectangle paintable, Image normalImage, Image pressedImage) {
		super(name, null, Integer.MIN_VALUE, null, null);
		
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

	public Rectangle getPaintable() {
		return paintable;
	}

	public void paint(SwtGraphics g) {
		if (!visible  || paintable == null) {
			return;
		}

		org.eclipse.swt.graphics.Rectangle clip = g.getClipping();
		
		g.setClipping(paintable.x, paintable.y, paintable.width, paintable.height);
		if (type == TYPE_COMMAND) {
			int xoffset = 0;
			Device device = DeviceFactory.getDevice();
			SwtDeviceDisplay deviceDisplay = (SwtDeviceDisplay) device.getDeviceDisplay();
			if (pressed) {
				g.setForeground(g.getColor(new RGB(deviceDisplay
						.getForegroundColor().getRed(), deviceDisplay
						.getForegroundColor().getGreen(), deviceDisplay
						.getForegroundColor().getBlue())));
			} else {
				g.setBackground(g.getColor(new RGB(deviceDisplay
						.getBackgroundColor().getRed(), deviceDisplay
						.getBackgroundColor().getGreen(), deviceDisplay
						.getBackgroundColor().getBlue())));
			}
			g.fillRectangle(paintable.x, paintable.y, paintable.width,
					paintable.height);
			synchronized (this) {
				if (command != null) {
					if (font != null) {
						SwtFontManager fontManager = (SwtFontManager) device.getFontManager();
						SwtFont buttonFont = (SwtFont) fontManager.getFont(font);
						g.setFont(buttonFont.getFont());
					}
					if (alignment == RIGHT) {
						xoffset = paintable.width
								- g.stringWidth(command.getLabel());
					}
					if (pressed) {
						g.setBackground(g.getColor(new RGB(deviceDisplay
								.getBackgroundColor().getRed(), deviceDisplay
								.getBackgroundColor().getGreen(), deviceDisplay
								.getBackgroundColor().getBlue())));
					} else {
						g.setForeground(g.getColor(new RGB(deviceDisplay
								.getForegroundColor().getRed(), deviceDisplay
								.getForegroundColor().getGreen(), deviceDisplay
								.getForegroundColor().getBlue())));
					}
					g.drawString(command.getLabel(), paintable.x + xoffset,
							paintable.y
									+ (paintable.height - g.getFontMetrics()
											.getHeight()), true);
				}
			}
		} else if (type == TYPE_ICON) {
			if (pressed) {
				g.drawImage(((SwtImmutableImage) pressedImage).getImage(), paintable.x, paintable.y);
			} else {
				g.drawImage(((SwtImmutableImage) normalImage).getImage(), paintable.x, paintable.y);
			}
		}
		
		g.setClipping(clip);
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
