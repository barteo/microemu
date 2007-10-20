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
 *  
 *  @version $Id$ 
 */

package org.microemu.device.j2se;

import java.awt.event.KeyEvent;
import java.util.Hashtable;

import org.microemu.device.InputMethod;
import org.microemu.device.impl.Button;
import org.microemu.device.impl.ButtonDetaultDeviceKeyCodes;
import org.microemu.device.impl.ButtonName;
import org.microemu.device.impl.Shape;

public class J2SEButton implements Button {

	private String name;

	private ButtonName functionalName;

	private Shape shape;

	private int[] keyboardKeys;

	private String keyboardCharCodes;

	private int keyCode;

	private Hashtable inputToChars;

	private boolean modeChange;

	/**
	 * Create special functional buttons. e.g. ButtonName.DELETE and
	 * ButtonName.BACK_SPACE if not defined in 'device.xml'
	 * 
	 * @param name
	 */
	J2SEButton(ButtonName functionalName) {
		this(functionalName.getName(), null, Integer.MIN_VALUE, null, null);
	}

	/**
	 * @param name
	 * @param shape
	 * @param keyCode -
	 *            Integer.MIN_VALUE when unspecified
	 * @param keyName
	 * @param chars
	 */
	public J2SEButton(String name, Shape shape, int keyCode, String keyName, Hashtable inputToChars) {
		this.name = name;
		this.shape = shape;
		this.functionalName = ButtonName.getButtonName(name);

		// TODO make it attribute in device.xml
		modeChange = (functionalName == ButtonName.KEY_POUND);

		if (keyCode == Integer.MIN_VALUE) {
			this.keyCode = ButtonDetaultDeviceKeyCodes.getKeyCode(this.functionalName);
		} else {
			this.keyCode = keyCode;
		}

		if (keyName == null) {
			this.keyboardKeys = J2SEButtonDefaultKeyCodes.getKeyCodes(this.functionalName);
			// TODO make it attribute in device.xml
			this.keyboardCharCodes = J2SEButtonDefaultKeyCodes.getCharCodes(this.functionalName);
		} else {
			this.keyboardKeys = new int[] { parseKeyboardKey(keyName) };
		}

		this.inputToChars = inputToChars;
	}

	/**
	 * @deprecated
	 */
	public int getKeyboardKey() {
		if (keyboardKeys.length == 0) {
			return 0;
		}
		return keyboardKeys[0];
	}

	public int getKeyCode() {
		return keyCode;
	}

	public ButtonName getFunctionalName() {
		return functionalName;
	}

	public int[] getKeyboardKeyCodes() {
		return keyboardKeys;
	}

	/**
	 * CharCodes do not depends on InputMode. This is computer keyboard codes
	 * when it is impossible to map to VK keys.
	 */
	public char[] getKeyboardCharCodes() {
		if (keyboardCharCodes == null) {
			return new char[0];
		}
		return keyboardCharCodes.toCharArray();
	}

	public boolean isModeChange() {
		return modeChange;
	}

	public char[] getChars(int inputMode) {
		char[] result = null;
		switch (inputMode) {
		case InputMethod.INPUT_123:
			result = (char[]) inputToChars.get("123");
			break;
		case InputMethod.INPUT_ABC_LOWER:
			result = (char[]) inputToChars.get("abc");
			break;
		case InputMethod.INPUT_ABC_UPPER:
			result = (char[]) inputToChars.get("ABC");
			break;
		}
		if (result == null) {
			result = (char[]) inputToChars.get("common");
		}
		if (result == null) {
			result = new char[0];
		}

		return result;
	}

	public boolean isChar(char c, int inputMode) {
		if (inputToChars == null) {
			return false;
		}
		c = Character.toLowerCase(c);
		char[] chars = getChars(inputMode);
		if (chars != null) {
			for (int i = 0; i < chars.length; i++) {
				if (c == Character.toLowerCase(chars[i])) {
					return true;
				}
			}
		}

		return false;
	}

	public String getName() {
		return name;
	}

	public Shape getShape() {
		return shape;
	}

	private static int parseKeyboardKey(String keyName) {
		int key;
		try {
			key = KeyEvent.class.getField(keyName).getInt(null);
		} catch (Exception e) {
			try {
				key = Integer.parseInt(keyName);
			} catch (NumberFormatException e1) {
				key = -1;
			}
		}
		return key;
	}

}
