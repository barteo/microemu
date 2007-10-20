/**
 *  MicroEmulator
 *  Copyright (C) 2006-2007 Bartek Teodorczyk <barteo@barteo.net>
 *  Copyright (C) 2006-2007 Vlad Skarzhevskyy
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
import java.util.HashMap;
import java.util.Map;

import org.microemu.device.impl.ButtonName;

/**
 * This class defines default computer keyboard key and char codes for buttons.
 * 
 * Use 'device.xml' to redefine codes for your device if required.
 * 
 * CharCodes do not depends on InputMode. This is computer keyboard codes when
 * it is impossible to map to VK keys.
 * 
 * @author vlads
 * 
 */
public class J2SEButtonDefaultKeyCodes {

	private static Map codes = new HashMap();

	private static class KeyInformation {

		int[] keyCodes;

		String charCodes = "";

	}

	public static int[] getKeyCodes(ButtonName name) {
		KeyInformation info = (KeyInformation) codes.get(name);
		if (info == null) {
			return new int[0];
		}
		return info.keyCodes;
	}

	public static String getCharCodes(ButtonName name) {
		KeyInformation info = (KeyInformation) codes.get(name);
		if (info == null) {
			return "";
		}
		return info.charCodes;
	}

	static {
		code(ButtonName.SOFT1, KeyEvent.VK_F1);
		code(ButtonName.SOFT2, KeyEvent.VK_F2);
		code(ButtonName.SELECT, KeyEvent.VK_ENTER);
		code(ButtonName.UP, KeyEvent.VK_UP, KeyEvent.VK_KP_UP);
		code(ButtonName.DOWN, KeyEvent.VK_DOWN, KeyEvent.VK_KP_DOWN);
		code(ButtonName.LEFT, KeyEvent.VK_LEFT, KeyEvent.VK_KP_LEFT);
		code(ButtonName.RIGHT, KeyEvent.VK_RIGHT, KeyEvent.VK_KP_RIGHT);

		// code(ButtonName.BACK, KeyEvent.VK_HOME);
		code(ButtonName.BACK_SPACE, KeyEvent.VK_BACK_SPACE);
		code(ButtonName.DELETE, KeyEvent.VK_CLEAR, KeyEvent.VK_DELETE);

		code(ButtonName.KEY_NUM0, KeyEvent.VK_0, KeyEvent.VK_NUMPAD0).charCodes = "0";
		code(ButtonName.KEY_NUM1, KeyEvent.VK_1, KeyEvent.VK_NUMPAD1).charCodes = "1";
		code(ButtonName.KEY_NUM2, KeyEvent.VK_2, KeyEvent.VK_NUMPAD2).charCodes = "2";
		code(ButtonName.KEY_NUM3, KeyEvent.VK_3, KeyEvent.VK_NUMPAD3).charCodes = "3";
		code(ButtonName.KEY_NUM4, KeyEvent.VK_4, KeyEvent.VK_NUMPAD4).charCodes = "4";
		code(ButtonName.KEY_NUM5, KeyEvent.VK_5, KeyEvent.VK_NUMPAD5).charCodes = "5";
		code(ButtonName.KEY_NUM6, KeyEvent.VK_6, KeyEvent.VK_NUMPAD6).charCodes = "6";
		code(ButtonName.KEY_NUM7, KeyEvent.VK_7, KeyEvent.VK_NUMPAD7).charCodes = "7";
		code(ButtonName.KEY_NUM8, KeyEvent.VK_8, KeyEvent.VK_NUMPAD8).charCodes = "8";
		code(ButtonName.KEY_NUM9, KeyEvent.VK_9, KeyEvent.VK_NUMPAD9).charCodes = "9";
		code(ButtonName.KEY_STAR, KeyEvent.VK_MULTIPLY, KeyEvent.VK_ASTERISK).charCodes = "*";
		code(ButtonName.KEY_POUND, KeyEvent.VK_MODECHANGE, KeyEvent.VK_SUBTRACT).charCodes = "#";
	}

	private static KeyInformation code(ButtonName name, int code) {
		KeyInformation info = new KeyInformation();
		info.keyCodes = new int[] { code };
		codes.put(name, info);
		return info;
	}

	private static KeyInformation code(ButtonName name, int code1, int code2) {
		KeyInformation info = new KeyInformation();
		info.keyCodes = new int[] { code1, code2 };
		codes.put(name, info);
		return info;
	}
}
